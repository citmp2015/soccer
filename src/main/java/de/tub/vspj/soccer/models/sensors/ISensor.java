package de.tub.vspj.soccer.models.sensors;

import de.tub.vspj.soccer.models.fieldobjects.IFieldObject;

public interface ISensor
{
    public enum SensorType
    {
        Ball,
        Leg,
        Arm
    }

    public int id();
    public SensorType sensorType();
    public int frequency();
    public IFieldObject owner();
}
