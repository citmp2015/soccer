package de.tub.vspj.soccer.models.sensors;

import de.tub.vspj.soccer.models.fieldobjects.Ball;
import de.tub.vspj.soccer.models.fieldobjects.IFieldObject;

public class BallSensor implements ISensor, java.io.Serializable
{
    public BallSensor(int id, Ball ball)
    {
        _id = id;
        _ball = ball;

        _ball.addSensor(this);
    }

    @Override
    public int id()
    {
        return _id;
    }

    @Override
    public SensorType sensorType()
    {
        return SensorType.Ball;
    }

    @Override
    public int frequency()
    {
        return 2000;
    }

    @Override
    public IFieldObject owner()
    {
        return _ball;
    }

    protected int _id;
    protected Ball _ball;
}
