package de.tub.vspj.soccer.models.sensors;

import de.tub.vspj.soccer.models.fieldobjects.IFieldObject;

public class ArmSensor implements ISensor, java.io.Serializable
{
    public enum ArmAttachment
    {
        LeftArm,
        RightArm
    }

    public ArmSensor(int id, IFieldObject object, ArmAttachment attachment)
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

    public ArmAttachment attachment()
    {
        return _attachment;
    }

    @Override
    public SensorType sensorType()
    {
        return SensorType.Arm;
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
    protected ArmAttachment _attachment;
}
