package de.tu_berlin.cit.vs15.soccer;

/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.util.Collector;
import org.apache.log4j.Logger;

/**
 * Skeleton for a Flink Job.
 *
 * For a full example of a Flink Job, see the WordCountJob.java file in the same
 * package/directory or have a look at the website.
 *
 * You can also generate a .jar file that you can submit on your Flink cluster.
 * Just type mvn clean package in the projects root directory. You will find the
 * jar in target/flink-quickstart-0.1-SNAPSHOT-Sample.jar
 *
 */
public class Job {

	public static String FILE_PATH = "C:\\Users\\Malcolm-X\\Downloads\\full-game2";//"C:\\Users\\Malcolm-X\\Downloads\\full-game2"; //"/full-game"
	public static int GRID_GRANULARITY_DISTANCE_X = 1000;
	public static int GRID_GRANULARITY_DISTANCE_Y = 1000;
	public static int GRID_GRANULARITY_FIELDS_X = 10;
	public static int GRID_GRANULARITY_FIELDS_Y = 10;
	public static boolean USE_DISTANCE = true;
	public static int SENSOR_ID = 13;//left leg of Nick Gertje
	public static String OUTPUT_FILE = "C:\\Users\\Malcolm-X\\Downloads\\out.csv";
	// TODO: check integrity of heatmap by calculating missing sensor
	// information
	private static Logger log;


	public static void main(String[] args) throws Exception {
		//FILE_PATH = args[0];
		//GRID_GRANULARITY_FIELDS_X = Integer.getInteger(args[1]);
		//GRID_GRANULARITY_FIELDS_Y = Integer.getInteger(args[2]);
		//SENSOR_ID = Integer.getInteger(args[3]);

		// set up the execution environment

		final ExecutionEnvironment env = ExecutionEnvironment
				.getExecutionEnvironment();
		System.out.println("Starting new Job: Heatmap");


		DataSet<Tuple3<Integer, Integer, Integer>> mappedData = env
				.readCsvFile(FILE_PATH).ignoreInvalidLines()
				.includeFields("1011000000000")
				.types(Integer.class, Integer.class, Integer.class);
		
		HeatmapMapper flatMapFunction = new HeatmapMapper();
		flatMapFunction.init(Constants.HEATMAP_X_MAX, Constants.HEATMAP_Y_MAX, 10, 5);
		
		DataSet<Tuple2<Tuple2<Integer, Integer>, Integer>> newMappedData = mappedData
				.flatMap(flatMapFunction).groupBy(0).sum(1);
        newMappedData.print();
		//newMappedData.writeAsText(OUTPUT_FILE);
        //newMappedData.write();
		/**
		 * Here, you can start creating your execution plan for Flink.
		 *
		 * Start with getting some data from the environment, like
		 * env.readTextFile(textPath);
		 *
		 * then, transform the resulting DataSet<String> using operations like
		 * .filter() .flatMap() .join() .coGroup() and many more. Have a look at
		 * the programming guide for the Java API:
		 *
		 * http://flink.apache.org/docs/latest/programming_guide.html
		 *
		 * and the examples
		 *
		 * http://flink.apache.org/docs/latest/examples.html
		 *
		 */

		// execute program
		//env.execute("Flink Java API Skeleton");
	}

	public static final class HeatmapToStringMapper implements FlatMapFunction<Tuple2<Tuple2<Integer, Integer>, Integer>, String>{

		private String name;
		private int sensorId;
		public void init(String name, int sensorId){
			this.name = name;
			this.sensorId = sensorId;
		}

		@Override
		public void flatMap(Tuple2<Tuple2<Integer, Integer>, Integer> tuple2IntegerTuple2, Collector<String> collector) throws Exception {

		}
	}

	public static final class HeatmapMapper
			implements
			FlatMapFunction<Tuple3<Integer, Integer, Integer>, Tuple2<Tuple2<Integer, Integer>, Integer>> {

		/**
		 * 
		 */
		private static final long serialVersionUID = -287513613975676230L;
		private int maxX, maxY;
		private int fieldsX, fieldsY;
		double granularityX, granularityY;

//		public HeatmapMapper(int maxX, int maxY, int fieldsX, int fieldsY) {
//			this.maxX = maxX;
//			this.maxY = maxY;
//			this.granularityX = ((double) (maxX * 2)) / (double) fieldsX;
//			this.granularityY = ((double) (maxY * 2)) / (double) fieldsY;
//			this.fieldsX = fieldsX;
//			this.fieldsY = fieldsY;
//		}
		
		public void init(int maxX, int maxY, int fieldsX, int fieldsY){
			this.maxX = maxX;
			this.maxY = maxY;
			this.granularityX = ((double) (maxX * 2)) / (double) fieldsX;
			this.granularityY = ((double) (maxY * 2)) / (double) fieldsY;
			this.fieldsX = fieldsX;
			this.fieldsY = fieldsY;
		}

		@Override
		public void flatMap(Tuple3<Integer, Integer, Integer> value,
				Collector<Tuple2<Tuple2<Integer, Integer>, Integer>> out)
				throws Exception {
			if (!value.f0.equals(SENSOR_ID)){
				return;
			}
			Tuple2<Integer, Integer> key = new Tuple2<Integer, Integer>(
					this.getXIndexForPosition(value.f1),
					this.getYIndexForPosition(value.f2));

			Tuple2<Tuple2<Integer, Integer>, Integer> mapResult = new Tuple2<Tuple2<Integer, Integer>, Integer>(
					key, 1);
			out.collect(mapResult);
		}


		
		public int getXIndexForPosition(int x) {
			return (int) ((maxX + x) / granularityX);
		}

		public int getYIndexForPosition(int y) {
			return (int) ((y + maxY) / granularityY);
		}
//		private void setDefault() {
//			this.maxX = maxX;
//			this.maxY = maxY;
//			this.granularityX = ((double) (maxX * 2)) / (double) fieldsX;
//			this.granularityY = ((double) (maxY * 2)) / (double) fieldsY;
//			this.fieldsX = fieldsX;
//			this.fieldsY = fieldsY;
//		}
//		private void writeObject(java.io.ObjectOutputStream out)
//				throws IOException {
//			// Order: maxX, maxY,fieldsX, fieldsY, granularityX, granularityY
//			out.writeInt(maxX);
//			out.writeInt(maxY);
//			out.writeInt(fieldsX);
//			out.writeInt(fieldsY);
//			out.writeDouble(granularityX);
//			out.writeDouble(granularityY);
//
//		}
//		private void readObject(java.io.ObjectInputStream in)
//				throws IOException, ClassNotFoundException {
//			this.maxX = in.readInt();
//			this.maxY = in.readInt();
//			this.fieldsX = in.readInt();
//			this.fieldsY = in.readInt();
//			this.granularityX = in.readDouble();
//			this.granularityY = in.readDouble();
//		}
//		private void readObjectNoData() throws ObjectStreamException {
//			throw new ObjectStreamException() {
//				/**
//				 * 
//				 */
//				private static final long serialVersionUID = -2957626867345995321L;
//				private static final String message = "The HeatmapMapFunction requires a non-empty grid in order to work. please use the correct constructor and make sure to call the write object function (serializable interface) before sending this class to another JVM!";
//				//TODO: add real exception class later
//			};
//		}

	}


}
