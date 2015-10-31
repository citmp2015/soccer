package de.tub.vspj.soccer.models.sensors;

import de.tub.vspj.soccer.models.fieldobjects.IFieldObject;

public class LegSensor implements ISensor, java.io.Serializable
{
    public enum LegAttachment
    {
        LeftLeg,
        RightLeg
    }

    public LegSensor(int id, IFieldObject object, LegAttachment attachment)
    {
        _id = id;
        _object = object;
        _attachment = attachment;

        _object.addSensor(this);
    }

    @Override
    public int id()
    {
        return _id;
    }

    public LegAttachment attachment()
    {
        return _attachment;
    }

    @Override
    public SensorType sensorType()
    {
        return SensorType.Leg;
    }

    @Override
    public int frequency()
    {
        return 200;
    }

    @Override
    public IFieldObject owner()
    {
        return _object;
    }

    protected int _id;
    protected IFieldObject _object;
    protected LegAttachment _attachment;
}
