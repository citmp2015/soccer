package de.tub.vspj.soccer.models;

import de.tub.vspj.soccer.models.fieldobjects.Ball;
import de.tub.vspj.soccer.models.fieldobjects.Player;
import de.tub.vspj.soccer.models.fieldobjects.Referee;
import de.tub.vspj.soccer.models.sensors.ISensor;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Approximated version of the playing field.
 * Note: The actual field is not a perfect rectangle.
 */
public class Field implements java.io.Serializable
{
    public static final int WIDTH = 52483;
    public static final int LENGTH = 67930;

    //according to Wikipedia for Grundig Stadion in Nürnberg
    public static final double WIDTH_METERS = 52.5;
    public static final double LENGTH_METERS = 68.0;

    public static final Point CENTER = new Point(WIDTH/2, 0);
    public static final Point2D.Double CENTER_METERS = new Point2D.Double(WIDTH_METERS/2.0, LENGTH_METERS/2.0);

    public Field(Team teamA, Team teamB)
    {
        _teamA = teamA;
        _teamB = teamB;

        _referees = new ArrayList<Referee>();
        _balls = new ArrayList<Ball>();
        _sensorsHash = new HashMap<Integer, ISensor>();

        indexTeamSensors(teamA);
        indexTeamSensors(teamB);
    }

    public Team teamA()
    {
        return _teamA;
    }

    public Team teamB()
    {
        return _teamB;
    }

    public void addReferee(Referee referee)
    {
        _referees.add(referee);

        indexSensors((Iterable<ISensor>)referee.sensors());
    }

    public Iterable<Referee> referees()
    {
        return _referees;
    }

    public void addBall(Ball ball)
    {
        _balls.add(ball);

        indexSensors((Iterable<ISensor>)ball.sensors());
    }

    public Iterable<Ball> balls()
    {
        return _balls;
    }

    /**
     * Returns the sensor object for the passed sensor ID.
     * Because all of the sensors on the field are indexed
     * this function locates the sensor in constant time.
     */
    public ISensor sensor(int sensorId)
    {
        return _sensorsHash.get(sensorId);
    }

    /**
     * Maps the sensor coordinate system to a meters based coordinate system.
     * The origin (0m,0m) is at the upper left corner (0,LENGTH/2).
     */
    public static Point2D.Double mapToMeters(int x, int y)
    {
        y = (y-(LENGTH/2))*-1; //normalize first

        return new Point2D.Double(
                WIDTH_METERS*((double)x/(double)WIDTH),
                LENGTH_METERS*((double)y/(double)LENGTH)
        );
    }

    public static double distanceBetweenPoints(int x1, int y1, int x2, int y2)
    {
        Point2D.Double a = mapToMeters(x1, y1);
        Point2D.Double b = mapToMeters(x2, y2);

        double x = b.x-a.x;
        double y = b.y-a.y;

        return Math.sqrt(x*x+y*y);
    }

    protected void indexSensors(Iterable<ISensor> sensors)
    {
        for(ISensor sensor : sensors)
            _sensorsHash.put(sensor.id(), sensor);
    }

    private void indexTeamSensors(Team team)
    {
        for(Player player : team.players())
            indexSensors((Iterable<ISensor>)player.sensors());
    }

    protected Team _teamA, _teamB;
    protected ArrayList<Referee> _referees;
    protected ArrayList<Ball> _balls;
    protected HashMap<Integer, ISensor> _sensorsHash;
}
