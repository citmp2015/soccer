package de.tub.vspj.soccer;

import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.io.CsvReader;
import org.apache.flink.api.java.tuple.Tuple2;

public class Playground
{
	public static void main(String[] args) throws Exception
	{
		if(args.length < 2)
		{
			System.err.println("Usage: Playground <module> <inputfile>");
			return;
		}

		final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

		CsvReader reader = env.readCsvFile(args[1]);

		switch(args[0])
		{
		case "AveragePlayerSpeed": AveragePlayerSpeed(reader); break;
		}
	}

	protected static void AveragePlayerSpeed(CsvReader file) throws Exception
	{
		file.includeFields(0x21); //field 0 and 5

		DataSet<Tuple2<Integer, Integer>> data = file.types(Integer.class, Integer.class);

		DataSet<Tuple2<Integer, Integer>> avg =  data
			.groupBy(0)
			.reduceGroup((values, collector) -> {
				int sensorId = -1;
				double sum = 0;
				int count = 0;

				for(Tuple2<Integer, Integer> value : values)
				{
					sensorId = value.f0;
					//sum += value.f1*0.000001*3.6; //km/h
					sum += value.f1*0.000001*3.6*10.0; //km/h * 10
					++count;
				}

				collector.collect(new Tuple2<Integer, Integer>(sensorId, (int)((sum/(double)count)+0.5)));
			});

		avg.print();
	}
}
