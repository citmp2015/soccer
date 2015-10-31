package de.tub.vspj.soccer.models.fieldobjects;

import de.tub.vspj.soccer.models.sensors.ISensor;
import java.util.ArrayList;

public class Referee implements IFieldObject, java.io.Serializable
{
    public Referee()
    {
        _sensors = new ArrayList<ISensor>();
    }

    @Override
    public ObjectType objectType()
    {
        return ObjectType.Referee;
    }

    @Override
    public void addSensor(ISensor sensor)
    {
        _sensors.add(sensor);
    }

    @Override
    public Iterable<? extends ISensor> sensors()
    {
        return _sensors;
    }

    protected ArrayList<ISensor> _sensors;
}
