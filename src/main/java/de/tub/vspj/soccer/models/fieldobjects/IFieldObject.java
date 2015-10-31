package de.tub.vspj.soccer.models.fieldobjects;

import de.tub.vspj.soccer.models.sensors.ISensor;

public interface IFieldObject
{
    public enum ObjectType
    {
        Ball,
        Player,
        Referee
    }

    public ObjectType objectType();

    public void addSensor(ISensor sensor);
    public Iterable<? extends ISensor> sensors();
}
