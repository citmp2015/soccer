package de.tub.vspj.soccer.jobs;

import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.io.CsvReader;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tub.vspj.soccer.ModelGenerator;
import de.tub.vspj.soccer.models.Field;
import de.tub.vspj.soccer.models.Game;
import de.tub.vspj.soccer.models.fieldobjects.Goalkeeper;
import de.tub.vspj.soccer.models.fieldobjects.IFieldObject;
import de.tub.vspj.soccer.models.fieldobjects.Player;
import de.tub.vspj.soccer.models.sensors.ISensor;
import de.tub.vspj.soccer.models.sensors.LegSensor;

public class CreateHeatMapData
{

    public static Logger LOG = LoggerFactory.getLogger(CreateHeatMapData.class);

    public static void run(String[] args) throws Exception
    {
        if(args.length < 2)
        {
            System.err.println("Please specify input data file and an output path!");
            return;
        }

        LOG.info("Starting job: {}", CreateHeatMapData.class.getSimpleName());

        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        final Game game = ModelGenerator.load();
        final Field field = game.field();

        CsvReader file = env.readCsvFile(args[0]);
        file.includeFields(0xf); //fields 0 through 3

        DataSet<OutputField> results = file.tupleType(SensorData.class)
                .filter((value) -> {
                    ISensor sensor = field.sensor(value.sid());

                    // TODO Heatmap data for Ball, Referee, Player and Team

                    // To track the position, it should be enough, to get the data from only one sensors per actor
                    if (sensor.sensorType() == ISensor.SensorType.Leg && ((LegSensor) sensor).attachment() == LegSensor.LegAttachment.RightLeg)
                        return true;

                    return false;
                })
                .groupBy(0)
                .reduceGroup((Iterable<SensorData> values, Collector<OutputField> collector) -> {
                    long startTime = 0;
                    long endTime = 0;
                    int counter = 0;

                    SensorData first = null;

                    OutputField outputField = new OutputField();

                    for(SensorData value : values)
                    {
                        if(first == null) //iterators in Java suck
                        {
                            first = value;
                            startTime = value.t();
                            endTime = startTime;
                            outputField.owner = field.sensor(first.sid()).owner();
                        }

                        //since the data has a way too high (time) resolution, we're only
                        //gonna look every ~1s where a player is on the pitch
//                        if((value.t()-endTime) < 1000000000L)
//                            continue;

                        //ignore when players walk on/off the pitch
                        if((value.t() >= game.startHalfTime() && value.t() <= game.endHalfTime())
                                || value.t() < game.startFirstHalf() || value.t() > game.endSecondHalf())
                            continue;

                        outputField.addPoint(value.x(), value.y());

                        counter++;

                        endTime = value.t();
                    }

                    if(field.sensor(first.sid()).owner().objectType() == IFieldObject.ObjectType.Player)
                    {
                        LOG.info("Added {} values for player: {}", counter, ((Player) field.sensor(first.sid()).owner()).name());
                    }
                    else if(field.sensor(first.sid()).owner().objectType() == IFieldObject.ObjectType.Referee)
                    {
                        LOG.info("Added {} values for player: {}", counter, "Referee");
                    }

                    collector.collect(outputField);
                });

        // Override existing files
        results.writeAsText(args[1], FileSystem.WriteMode.OVERWRITE);
        results.print();
    }

    /**
     * Helper class for AvaregePlayerSpeed to operate on the data.
     */
    public static class SensorData extends Tuple4<Byte, Long, Integer, Integer>
    {
        public SensorData()
        {
            super();
        }

        public SensorData(byte sid, long t, int x, int y)
        {
            super();

            f0 = sid;
            f1 = t;
            f2 = x;
            f3 = y;
        }

        public byte sid() {return f0;}
        public long t() {return f1;}
        public int x() {return f2;}
        public int y() {return f3;}
    }

    /**
     * Helper class to represent the output field
     */
    public static class OutputField
    {
        // Possible resolution values for the field based on the project description
        // 8 X 13 --> 104 cells
        // 16 X 25 --> 400 cells
        // 32 X 50 --> 1600 cells
        // 64 X 100 --> 6400 cells
        public static int fieldXResolution = 8;
        public static int fieldYResolution = 13;

        // Used in the addPoint method. Used to compute the values only once
        private static int sectorsX = Field.WIDTH / fieldXResolution;
        private static int sectorsY = Field.LENGTH / fieldYResolution;

        public IFieldObject owner;
        public int[][] field = new int[fieldXResolution][fieldYResolution];

        @Override
        public String toString()
        {
            StringBuffer str = new StringBuffer();

            str.append("Field for player: ");
            if(owner.objectType() == IFieldObject.ObjectType.Player)
            {
                Player player = (Player) owner;
                str.append('[');
                str.append(player.team().name());
                str.append("] ");
                str.append(player.name());

                if(player instanceof Goalkeeper)
                    str.append(" (GK)");
            }
            else if(owner.objectType() == IFieldObject.ObjectType.Referee)
            {
                str.append("[Referee]");
            }

            for(int i = 0; i < fieldYResolution; i++){
                str.append("\n[");
                for(int j = 0; j < fieldXResolution; j++){
                    str.append(field[j][i]);
                    str.append(", ");
                }
                str.append("]");
            }
            str.append("\n");

            return str.toString();
        }

        // Suggested values for approximated field by project description
        // (0,33965), (0,-33960),(52483,33965),(52483,-33960)
        public void addPoint(int x, int y){

            LOG.info("Got points ({},{}). Width: {}, Height: {}", x, y, sectorsX, sectorsY);

            // Add size of half field to y coordinate to avoid negative values
            y += 33965;

            // If measurement is out of field, do not take into account
            if (x < 0 || x > 52483 || y < 0 || y > 67925)
                return;

            // No floor function needed, because it is implicit in java integer division
            int xField = x / sectorsX;
            int yField = y / sectorsY;

            LOG.info("Putting ({},{}) into field.", xField, yField);

            field[xField][yField] += 1;
        }
    }
}

