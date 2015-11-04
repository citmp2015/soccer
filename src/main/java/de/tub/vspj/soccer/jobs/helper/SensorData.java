package de.tub.vspj.soccer.jobs.helper;

import de.tub.vspj.soccer.ModelGenerator;
import de.tub.vspj.soccer.models.Game;
import de.tub.vspj.soccer.models.Field;
import org.apache.flink.api.java.tuple.Tuple4;

/**
 * Helper class for to operate on the data.
 */
public class SensorData extends Tuple4<Byte, Long, Integer, Integer>
{
    Game game = ModelGenerator.load();

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
    public int y() {
            return t() > game.endHalfTime() ? Field.LENGTH - f3 : f3;
    }
}
