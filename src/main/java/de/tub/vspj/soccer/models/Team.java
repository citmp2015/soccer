package de.tub.vspj.soccer.models;

import de.tub.vspj.soccer.models.fieldobjects.Player;
import java.util.ArrayList;

public class Team implements java.io.Serializable
{
    public Team(String name)
    {
        _name = name;

        _players = new ArrayList<Player>();
    }

    public String name()
    {
        return _name;
    }

    public void addPlayer(Player player)
    {
        _players.add(player);
    }

    public Iterable<Player> players()
    {
        return _players;
    }

    protected String _name;
    protected ArrayList<Player> _players;
}
