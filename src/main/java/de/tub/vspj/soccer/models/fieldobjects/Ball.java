package de.tub.vspj.soccer.models.fieldobjects;

import de.tub.vspj.soccer.models.sensors.BallSensor;
import de.tub.vspj.soccer.models.sensors.ISensor;
import java.util.ArrayList;

public class Ball implements IFieldObject, java.io.Serializable
{
    public Ball()
    {
    }

    @Override
    public ObjectType objectType()
    {
        return ObjectType.Ball;
    }

    public void setSensor(BallSensor sensor)
    {
        _sensor = sensor;
    }

    @Override
    public void addSensor(ISensor sensor)
    {
        if(sensor instanceof BallSensor)
            setSensor((BallSensor)sensor);
        else
            throw new Error("Ball can only have sensors of type BallSensor");
    }

    @Override
    public Iterable<? extends ISensor> sensors()
    {
        ArrayList<BallSensor> list = new ArrayList<BallSensor>();
        if(_sensor != null)
            list.add(_sensor);

        return list;
    }

    protected BallSensor _sensor;
}
