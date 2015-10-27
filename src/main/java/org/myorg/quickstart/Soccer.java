package org.myorg.quickstart;

import org.apache.flink.api.common.functions.GroupCombineFunction;
import org.apache.flink.api.common.functions.GroupReduceFunction;
import org.apache.flink.api.common.operators.Order;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.SortedGrouping;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.util.Collector;

public class Soccer {
	public static void main(String[] args) throws Exception {

		// set up the execution environment
		final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
		
		System.out.println("Starting Soccer job");
		
		// We might add .includeFields("10010") to only take the first and the fourth field
//		DataSet<Tuple13<Integer, String, Integer, Integer, Integer, Integer, Integer, Integer, Integer, 
//						Integer, Integer, Integer, Integer>> input = env.readCsvFile("/home/adrian/Documents/Verteilte Systeme/Soccer/snippet.csv")
//						.ignoreInvalidLines()
//						.types(	Integer.class, String.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, 
//								Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class);

		//Clean input, because of duplicates
		
		DataSet<Tuple3<Integer, String, Integer>> input = env.readCsvFile("/home/adrian/Documents/Verteilte Systeme/Soccer/snippet.csv")
			.ignoreInvalidLines()
			.includeFields("1110000000000")
			.types(	Integer.class, String.class, Integer.class);
		
		// collector type must be declared
		DataSet<Tuple2<Integer, Integer>> output = input.groupBy(0).sortGroup(1, Order.ASCENDING).reduceGroup(new GroupReduceFunction<Tuple3<Integer,String,Integer>, Tuple2<Integer, Integer>>() {

			private static final long serialVersionUID = 1L;

			@Override
			public void reduce(Iterable<Tuple3<Integer, String, Integer>> values, Collector<Tuple2<Integer, Integer>> out)
					throws Exception {
				
					int sum = 0, key = -1, previous = 0;
					boolean skipFirst = true;
									
					for(Tuple3<Integer, String, Integer> tuple : values){
						
						if(skipFirst){
							key = tuple.f0;
							skipFirst = false;
							previous = tuple.f2;
							continue;
						}
						
						sum += Math.abs(tuple.f2 - previous);
						previous = tuple.f2;
					}
					
					out.collect(new Tuple2<Integer,Integer>(key, sum));
				
			}
		});
		
		output.print();
				
//		input.writeAsText("/home/adrian/Documents/Verteilte Systeme/Soccer/output.txt");
	}
}
