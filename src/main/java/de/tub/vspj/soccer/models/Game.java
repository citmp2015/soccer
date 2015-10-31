package de.tub.vspj.soccer.models;

public class Game implements java.io.Serializable
{
    public Game()
    {
    }

    public void setField(Field field)
    {
        _field = field;
    }

    public Field field()
    {
        return _field;
    }

    public void setTimeFrames(long startFirstHalf, long endFirstHalf, long startSecondHalf, long endSecondHalf)
    {
        _startFirstHalf = startFirstHalf;
        _endFirstHalf = endFirstHalf;
        _startSecondHalf = startSecondHalf;
        _endSecondHalf = endSecondHalf;
    }

    public long startFirstHalf()
    {
        return _startFirstHalf;
    }

    public long endFirstHalf()
    {
        return _endFirstHalf;
    }

    public long startSecondHalf()
    {
        return _startSecondHalf;
    }

    public long endSecondHalf()
    {
        return _endSecondHalf;
    }

    public long startHalfTime()
    {
        return _endFirstHalf+1L;
    }

    public long endHalfTime()
    {
        return _startSecondHalf-1L;
    }

    protected Field _field;
    protected long _startFirstHalf, _endFirstHalf;
    protected long _startSecondHalf, _endSecondHalf;
}
