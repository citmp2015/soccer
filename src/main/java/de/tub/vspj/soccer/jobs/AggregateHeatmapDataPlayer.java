package de.tub.vspj.soccer.jobs;

import org.apache.flink.api.common.operators.Order;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.io.CsvReader;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tub.vspj.soccer.ModelGenerator;
import de.tub.vspj.soccer.jobs.helper.HeatmapData;
import de.tub.vspj.soccer.jobs.helper.SensorData;
import de.tub.vspj.soccer.models.Field;
import de.tub.vspj.soccer.models.Game;
import de.tub.vspj.soccer.models.fieldobjects.IFieldObject;
import de.tub.vspj.soccer.models.fieldobjects.Player;
import de.tub.vspj.soccer.models.sensors.ISensor;
import de.tub.vspj.soccer.models.sensors.LegSensor;

public class AggregateHeatmapDataPlayer {

    public static Logger LOG = LoggerFactory.getLogger(AggregateHeatmapDataPlayer.class);

    public static void run(String[] args) throws Exception {

        if (args.length < 2) {
            System.err.println("Please specify input data file and an output path!");
            return;
        }

        LOG.info("Starting job: {}", AggregateHeatmapDataPlayer.class.getSimpleName());

        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        final Game game = ModelGenerator.load();
        final Field field = game.field();

        CsvReader file = env.readCsvFile(args[0]);
        file.includeFields(0xf); //fields 0 through 3

        DataSet<HeatmapData> results = file.tupleType(SensorData.class)
                .filter((value) -> {
                    ISensor sensor = field.sensor(value.sid());

                    // To track the position, it should be enough, to get the data from only one sensors per actor
                    if (sensor.sensorType() == ISensor.SensorType.Leg && ((LegSensor) sensor).attachment() == LegSensor.LegAttachment.RightLeg)
                        return true;

                    return false;
                })
                .groupBy(0)
                .sortGroup(1, Order.ASCENDING)
                .reduceGroup((Iterable<SensorData> values, Collector<HeatmapData> collector) -> {
                    long startTime = 0;
                    long endTime = 0;
                    int counter = 0;

                    SensorData first = null;

                    HeatmapData outputField = new HeatmapData();

                    for (SensorData value : values) {
                        if (first == null) //iterators in Java suck
                        {
                            first = value;
                            startTime = value.t();
                            endTime = startTime;
                            outputField.owner = field.sensor(first.sid()).owner();
                        }

                        //since the data has a way too high (time) resolution, we're only
                        //gonna look every ~1s where a player is on the pitch
                        if ((value.t() - endTime) < 1000000000L)
                            continue;

                        //ignore when players walk on/off the pitch
                        if ((value.t() >= game.startHalfTime() && value.t() <= game.endHalfTime())
                                || value.t() < game.startFirstHalf() || value.t() > game.endSecondHalf())
                            continue;

                        outputField.addPoint(value.x(), value.y());

                        counter++;

                        endTime = value.t();
                    }

                    if (field.sensor(first.sid()).owner().objectType() == IFieldObject.ObjectType.Player) {
                        LOG.info("Added {} values for player: {}", counter, ((Player) field.sensor(first.sid()).owner()).name());
                    } else if (field.sensor(first.sid()).owner().objectType() == IFieldObject.ObjectType.Referee) {
                        LOG.info("Added {} values for player: {}", counter, "Referee");
                    }

                    collector.collect(outputField);
                });

        // Override existing files
        results.writeAsText(args[1], FileSystem.WriteMode.OVERWRITE);
        results.print();
    }
}
