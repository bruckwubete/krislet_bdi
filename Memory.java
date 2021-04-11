import java.util.Vector;

//
//  File:           Memory.java
//  Author:     Krzysztof Langner
//  Date:           1997/04/28
//
class Memory 
{
    //---------------------------------------------------------------------------
    // This constructor:
    // - initializes all variables
    public Memory()
    {
    }


    //---------------------------------------------------------------------------
    // This function puts see information into our memory
    public void store(VisualInfo info)
    {
    m_info = info;
    }
    public void storeSpeedDirection(Float d)
    {
        speedDirection = d;
    }
    public float getSpeedDirection()
    {
        while(speedDirection == null)
        {
            // We can get information faster then 75 miliseconds
            try
            {
                Thread.sleep(SIMULATOR_STEP);
            }
            catch(Exception e)
            {
            }
        }
        return speedDirection;
    }


    //---------------------------------------------------------------------------
    // This function looks for specified object
    public ObjectInfo getObject(String name) 
    {
    if( m_info == null )
        waitForNewInfo();

    for(int c = 0 ; c < m_info.m_objects.size() ; c ++)
        {
        ObjectInfo object = (ObjectInfo)m_info.m_objects.elementAt(c);
        if(object.m_type.compareTo(name) == 0)
            return object;
        }                                                

    return null;
    }

    public ObjectInfo getObject(String objType, String name)
    {
        if( m_info == null )
            waitForNewInfo();

        switch (objType) {
            case "line":
                return  getLine(name);
            case "flag":
                return  getFlag(name);
            case "ball":
                return getBall(name);
            case "goal":
                return getGoal(name);
        }

        return null;
    }

    // This function looks for specified line
    public LineInfo getLine(String kind)
    {
        if( m_info == null )
            waitForNewInfo();

        Vector<LineInfo> m_line_list = m_info.getLineList();

        for(int c = 0 ; c < m_line_list.size() ; c ++)
        {
            if(kind.equals("" + m_line_list.elementAt(c).m_kind))
                return m_line_list.elementAt(c);
        }

        return null;
    }

    // This function looks for specified line
    public BallInfo getBall(String kind)
    {
        if( m_info == null )
            waitForNewInfo();

        Vector<BallInfo> m_ball_list = m_info.getBallList();

        for(int c = 0 ; c < m_ball_list.size() ; c ++)
        {
            if(kind.equals("" + m_ball_list.elementAt(c).m_type))
                return m_ball_list.elementAt(c);
        }

        return null;
    }

    // This function looks for specified line
    public GoalInfo getGoal(String kind)
    {
        if( m_info == null )
            waitForNewInfo();

        Vector<GoalInfo> m_goal_list = m_info.getGoalList();

        for(int c = 0 ; c < m_goal_list.size() ; c ++)
        {
            if(kind.equals("" + m_goal_list.elementAt(c).getSide()))
                return m_goal_list.elementAt(c);
        }

        return null;
    }

    // This function looks for specified line
    public FlagInfo getFlag(String kind)
    {
        if( m_info == null )
            waitForNewInfo();

        Vector<FlagInfo> m_flag_list = m_info.getFlagList();

        for(int c = 0 ; c < m_flag_list.size() ; c ++)
        {
            FlagInfo e = m_flag_list.elementAt(c);
            String eKind = "" + e.m_type + e.m_pos1 + e.m_pos2 + e.m_num;
            if(eKind.equals(kind + "0"))
                return m_flag_list.elementAt(c);
        }

        return null;
    }


    //---------------------------------------------------------------------------
    // This function waits for new visual information
    public void waitForNewInfo() 
    {
    // first remove old info
    m_info = null;
    // now wait until we get new copy
    while(m_info == null)
        {
        // We can get information faster then 75 miliseconds
        try
            {
            Thread.sleep(SIMULATOR_STEP);
            }
        catch(Exception e)
            {
            }
        }
    }


    //===========================================================================
    // Private members
    volatile private VisualInfo m_info; // place where all information is stored
    volatile private Float speedDirection;
    final static int SIMULATOR_STEP = 100;
}

