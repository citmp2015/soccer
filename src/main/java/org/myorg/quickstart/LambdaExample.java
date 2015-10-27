package org.myorg.quickstart;

import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.util.Collector;

public class LambdaExample {
	public static void main(String[] args) throws Exception {

		// set up the execution environment
		final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

		DataSet<Integer> input = env.fromElements(1, 2, 3);

		// collector type must be declared
		input.flatMap((Integer number, Collector<String> out) -> {
			for (int i = 0; i < number; i++) {
				out.collect("a");
			}
		}).print();
		// returns "a", "a", "aa", "a", "aa" , "aaa" !!! WRONG
	}
}
