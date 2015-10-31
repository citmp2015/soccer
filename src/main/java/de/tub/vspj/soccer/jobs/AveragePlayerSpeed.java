package de.tub.vspj.soccer.jobs;

import de.tub.vspj.soccer.ModelGenerator;
import de.tub.vspj.soccer.models.Field;
import de.tub.vspj.soccer.models.Game;
import de.tub.vspj.soccer.models.fieldobjects.Goalkeeper;
import de.tub.vspj.soccer.models.fieldobjects.IFieldObject;
import de.tub.vspj.soccer.models.fieldobjects.Player;
import de.tub.vspj.soccer.models.fieldobjects.Referee;
import de.tub.vspj.soccer.models.sensors.ISensor;
import de.tub.vspj.soccer.models.sensors.LegSensor;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.GroupReduceFunction;
import org.apache.flink.api.common.operators.Order;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.io.CsvReader;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.util.Collector;

import java.util.Iterator;

public class AveragePlayerSpeed
{
	public static void run(String[] args) throws Exception
	{
		if(args.length < 1)
		{
			System.err.println("Please specify input data file!");
			return;
		}

		final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
		final Game game = ModelGenerator.load();
		final Field field = game.field();

		CsvReader file = env.readCsvFile(args[0]);
		file.includeFields(0xf); //fields 0 through 3

		DataSet<ResultData> results = file.tupleType(SensorData.class)
			.filter((value) -> {
				ISensor sensor = field.sensor(value.sid());

				//we're only interested in the sensor for one leg of each player
				//so skip balls, the left leg of each player and referee, and
				//the arms of the goalkeepers
				if(sensor.sensorType() == ISensor.SensorType.Leg && ((LegSensor)sensor).attachment() == LegSensor.LegAttachment.RightLeg)
					return true;
				
				//TODO: instead of only looking at one leg, we should use the average of both legs

				return false;
			})
			.groupBy(0)
			.sortGroup(1, Order.ASCENDING)
			.reduceGroup((Iterable<SensorData> values, Collector<ResultData> collector) -> {
				long startTime = 0;
				long endTime = 0;
				int prevX = 0;
				int prevY = 0;
				
				SensorData first = null;
	
				double totalDistance = 0.0;
				double maxSpeed = 0.0;
	
				for(SensorData value : values)
				{
					if(first == null) //iterators in Java suck
					{
						first = value;
						startTime = value.t();
						endTime = startTime;
						prevX = value.x();
						prevY = value.y();
					}
					
					//since the data has a way too high (time) resolution, we're only
					//gonna look every ~1s where a player is on the pitch
					if((value.t()-endTime) < 1000000000000L)
						continue;
	
					//ignore when players walk on/off the pitch
					if(value.t() >= game.startHalfTime() && value.t() <= game.endHalfTime())
						continue;
	
					double distance = field.distanceBetweenPoints(prevX, prevY, value.x(), value.y());
					double speed = (distance/1000.0)*(3600000000000000.0/((double)(value.t()-endTime)));
					
					totalDistance += distance;
					if(speed > maxSpeed)
						maxSpeed = speed;
	
					prevX = value.x();
					prevY = value.y();
					endTime = value.t();
				}
	
				ResultData result = new ResultData();
				result.owner = field.sensor(first.sid()).owner();
				result.totalDistance = totalDistance;
				result.maxSpeed = maxSpeed;
				result.timePlayed = (endTime-startTime);
	
				//account for half time gap
				if(startTime < game.startHalfTime() && endTime > game.endHalfTime())
					result.timePlayed -= (game.endHalfTime()-game.startHalfTime());
	
				if(result.timePlayed > 0L) //avoid div by zero
				{
					//first scale down the picoseconds to something more sane
					//otherwise 64bit double might overflow
					double factor = 3600000000.0/((double)(result.timePlayed/1000000L));
					result.averageSpeed = (totalDistance/1000.0)*factor;
				}
				
				collector.collect(result);
			});

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
	 * Helper class to present results.
	 */
	public static class ResultData
	{
		public IFieldObject owner;
		public double totalDistance;
		public long timePlayed;
		public double averageSpeed;
        public double maxSpeed;

		@Override
		public String toString()
		{
			StringBuffer str = new StringBuffer();

			if(owner.objectType() == IFieldObject.ObjectType.Player)
			{
				Player player = (Player)owner;
				str.append('[');
				str.append(player.team().name());
				str.append("] ");
				str.append(player.name());

				if(player instanceof Goalkeeper)
					str.append(" (GK)");
			}
			else if(owner.objectType() == IFieldObject.ObjectType.Referee)
			{
				Referee ref = (Referee)owner;
				str.append("[Referee]");
			}
			else
				return null;

			str.append(":   ");

			/*str.append("Distance(");
			str.append(Math.round(totalDistance*10.0)/10.0);
			str.append("m)   ");*/
			
			str.append("Distance(");
			str.append(Math.round((totalDistance/1000.0)*10.0)/10.0); //m -> km -> round with 1 decimal place
			str.append("km)   ");
			
			/*str.append("Played(");
			str.append(Math.round(timePlayed/1000000000000L));
			str.append("sec)   ");*/
			
			str.append("Played(");
			str.append(Math.round(timePlayed/60000000000000L));
			str.append("min)   ");

			str.append("AvgSpeed(");
			str.append(Math.round(averageSpeed*10.0)/10.0); //round with 1 decimal place
			str.append("km/h)   ");
			
			str.append("MaxSpeed(");
			str.append(Math.round(maxSpeed*10.0)/10.0); //round with 1 decimal place
			str.append("km/h)");

			return str.toString();
		}
	}
}
