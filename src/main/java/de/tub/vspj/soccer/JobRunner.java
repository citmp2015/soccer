package de.tub.vspj.soccer;

import de.tub.vspj.soccer.jobs.AggregatedPlayerStats;
import de.tub.vspj.soccer.jobs.CreateHeatMapData;

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
            case "CreateHeatMapData":
                CreateHeatMapData.run(moduleArgs);
                break;
        }
    }
}
