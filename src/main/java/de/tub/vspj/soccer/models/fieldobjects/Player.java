package de.tub.vspj.soccer.models.fieldobjects;

import de.tub.vspj.soccer.models.Team;
import de.tub.vspj.soccer.models.sensors.ISensor;
import java.util.ArrayList;

public class Player implements IFieldObject, java.io.Serializable
{
    public Player(String name, Team team)
    {
        _name = name;
        _team = team;

        _team.addPlayer(this);

        _sensors = new ArrayList<ISensor>();
    }

    @Override
    public ObjectType objectType()
    {
        return ObjectType.Player;
    }

    public String name()
    {
        return _name;
    }

    public Team team()
    {
        return _team;
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

    protected String _name;
    protected Team _team;
    protected ArrayList<ISensor> _sensors;
}
