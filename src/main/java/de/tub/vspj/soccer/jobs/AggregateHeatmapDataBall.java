package de.tub.vspj.soccer.jobs;

import org.apache.flink.api.common.operators.Order;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.io.CsvReader;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tub.vspj.soccer.ModelGenerator;
import de.tub.vspj.soccer.jobs.helper.HeatmapData;
import de.tub.vspj.soccer.jobs.helper.SensorData;
import de.tub.vspj.soccer.models.Field;
import de.tub.vspj.soccer.models.Game;
import de.tub.vspj.soccer.models.sensors.ISensor;

public class AggregateHeatmapDataBall {

    public static Logger LOG = LoggerFactory.getLogger(AggregateHeatmapDataBall.class);

    public static void run(String[] args) throws Exception
    {
        if(args.length < 2)
        {
            System.err.println("Please specify input data file and an output path!");
            return;
        }

        LOG.info("Starting job: {}", AggregateHeatmapDataBall.class.getSimpleName());

        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        final Game game = ModelGenerator.load();
        final Field field = game.field();

        CsvReader file = env.readCsvFile(args[0]);
        file.includeFields(0xf); //fields 0 through 3

        DataSet<HeatmapData> results = file.tupleType(SensorData.class)
                .filter((value) -> {
                    ISensor sensor = field.sensor(value.sid());

                    // Select all sensors attached to the ball
                    int id = sensor.id();
                    if (id == 4 || id == 8 || id == 10 || id == 12)
                        return true;

                    return false;
                })
                .<Tuple3<Long, Integer, Integer>>project(1, 2, 3) // Retain type information -> See flink description
                .sortPartition(0, Order.ASCENDING)
                .reduceGroup((Iterable<Tuple3<Long, Integer, Integer>> values, Collector<HeatmapData> collector) -> {
                    long startTime = 0;
                    long endTime = 0;
                    int counter = 0;

                    Tuple3 first = null;

                    HeatmapData outputField = new HeatmapData();

                    for (Tuple3 value : values) {

                        long time = (long) value.f0;

                        if (first == null) //iterators in Java suck
                        {
                            first = value;
                            startTime = time;
                            endTime = startTime;
                            outputField.owner = field.sensor(4).owner(); // Set manually
                        }

                        //since the data has a way too high (time) resolution, we're only
                        //gonna look every ~1s where a player is on the pitch
                        if ((time - endTime) < 1000000000L)
                            continue;

                        // ignore ball during none game times
                        // Consider time when transmission was broken -> See description
                        if ((time >= game.startHalfTime() && time <= game.endHalfTime())
                                || time < game.startFirstHalf()
                                || (time < game.startHalfTime() && time > 12398000000000000L)
                                || time > game.endSecondHalf())
                            continue;

                        outputField.addPoint((int) value.f1, (int) value.f2);

                        counter++;

                        endTime = time;
                    }

                    LOG.info("Added {} measurements to map", counter);

                    collector.collect(outputField);
                });

        // Override existing files
        results.writeAsText(args[1], FileSystem.WriteMode.OVERWRITE);
        results.print();
    }
}
