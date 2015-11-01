package de.tub.vspj.soccer;

import de.tub.vspj.soccer.jobs.AggregateHeatmapDataBall;
import de.tub.vspj.soccer.jobs.AggregateHeatmapDataReferee;
import de.tub.vspj.soccer.jobs.AggregatedPlayerStats;
import de.tub.vspj.soccer.jobs.AggregateHeatmapDataPlayer;

public class JobRunner {
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Usage: bin/flink run ../target/soccer.jar <module> [inputfile]");
            return;
        }

        String[] moduleArgs = new String[args.length - 1];
        System.arraycopy(args, 1, moduleArgs, 0, args.length - 1);

        switch (args[0]) {
            case "AggregatedPlayerStats":
                AggregatedPlayerStats.run(moduleArgs);
                break;
            case "HeatmapPlayer":
                AggregateHeatmapDataPlayer.run(moduleArgs);
                break;
            case "HeatmapBall":
                AggregateHeatmapDataBall.run(moduleArgs);
                break;
            case "HeatmapReferee":
                AggregateHeatmapDataReferee.run(moduleArgs);
                break;
        }
    }
}
