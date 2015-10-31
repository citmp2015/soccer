package de.tub.vspj.soccer;

import de.tub.vspj.soccer.models.*;
import de.tub.vspj.soccer.models.fieldobjects.*;
import de.tub.vspj.soccer.models.sensors.*;

public class ModelGenerator
{
    public static Game load()
    {
        Game game = new Game();
        game.setTimeFrames(
                10753295594424116L, 12557295594424116L,
                13086639146403495L, 14879639146403495L
        );

        Team teamA = new Team("Team A");
        Team teamB = new Team("Team B");

        createGoalkeeper("Nick Gertje", 13, 14, 97, 98, teamA);
        createPlayer("Dennis Dotterweich", 47, 16, teamA);
        createPlayer("Niklas Waelzlein", 49, 88, teamA);
        createPlayer("Wili Sommer", 19, 52, teamA);
        createPlayer("Philipp Harlass", 53, 54, teamA);
        createPlayer("Roman Hartleb", 23, 24, teamA);
        createPlayer("Erik Engelhardt", 57, 58, teamA);
        createPlayer("Sandro Schneider", 59, 28, teamA);

        createGoalkeeper("Leon Krapf", 61, 62, 99, 100, teamB);
        createPlayer("Kevin Baer", 63, 64, teamB);
        createPlayer("Luca Ziegler", 65, 66, teamB);
        createPlayer("Ben Mueller", 67, 68, teamB);
        createPlayer("Vale Reitstetter", 69, 38, teamB);
        createPlayer("Christopher Lee", 71, 40, teamB);
        createPlayer("Leon Heinze", 73, 74, teamB);
        createPlayer("Leo Langhans", 75, 44, teamB);

        Field field = new Field(teamA, teamB);
        field.addReferee(createReferee(105, 106));
        field.addBall(createBall(4));
        field.addBall(createBall(8));
        field.addBall(createBall(10));
        field.addBall(createBall(12));

        game.setField(field);

        return game;
    }

    protected static Player createPlayer(String name, int leftLegSensorId, int rightLegSensorId, Team team)
    {
        Player player = new Player(name, team);

        new LegSensor(leftLegSensorId, player, LegSensor.LegAttachment.LeftLeg);
        new LegSensor(rightLegSensorId, player, LegSensor.LegAttachment.RightLeg);

        return player;
    }

    protected static Goalkeeper createGoalkeeper(String name, int leftLegSensorId, int rightLegSensorId, int leftArmSensorId, int rightArmSensorId, Team team)
    {
        Goalkeeper player = new Goalkeeper(name, team);

        new LegSensor(leftLegSensorId, player, LegSensor.LegAttachment.LeftLeg);
        new LegSensor(rightLegSensorId, player, LegSensor.LegAttachment.RightLeg);

        new ArmSensor(leftArmSensorId, player, ArmSensor.ArmAttachment.LeftArm);
        new ArmSensor(rightArmSensorId, player, ArmSensor.ArmAttachment.RightArm);

        return player;
    }

    protected static Referee createReferee(int leftLegSensorId, int rightLegSensorId)
    {
        Referee ref = new Referee();

        new LegSensor(leftLegSensorId, ref, LegSensor.LegAttachment.LeftLeg);
        new LegSensor(rightLegSensorId, ref, LegSensor.LegAttachment.RightLeg);

        return ref;
    }

    protected static Ball createBall(int sensorId)
    {
        Ball ball = new Ball();
        new BallSensor(sensorId, ball);

        return ball;
    }
}
