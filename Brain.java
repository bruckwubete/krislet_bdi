//
//  File:           Brain.java
//  Author:     Krzysztof Langner
//  Date:           1997/04/28
//
//    Modified by:  Paul Marlow

//    Modified by:      Edgar Acosta
//    Date:             March 4, 2008

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.Math;
import java.util.*;
import java.util.regex.*;

class Brain extends Thread implements SensorInput {
    //---------------------------------------------------------------------------
    // This constructor:
    // - stores connection to krislet
    // - starts thread for this object
    public Brain(VCWorld world, SendCommand krislet,
                 String team,
                 char side,
                 int number,
                 String playMode) {
        this.world = world;
        m_timeOver = false;
        m_krislet = krislet;
        m_memory = new Memory();
        //m_team = team;
        m_side = side;
        // m_number = number;
        m_playMode = playMode;
        start();
    }


    //---------------------------------------------------------------------------
    // This is main brain function used to make decision
    // In each cycle we decide which command to issue based on
    // current situation. the rules are:
    //
    //  1. If you don't know where is ball then turn right and wait for new info
    //
    //  2. If ball is too far to kick it then
    //      2.1. If we are directed towards the ball then go to the ball
    //      2.2. else turn to the ball
    //
    //  3. If we dont know where is opponent goal then turn wait 
    //              and wait for new info
    //
    //  4. Kick ball
    //
    //  To ensure that we don't send commands to often after each cycle
    //  we waits one simulator steps. (This of course should be done better)

    // ***************  Improvements ******************
    // Allways know where the goal is.
    // Move to a place on my side on a kick_off
    // ************************************************

    public void run() {
        ObjectInfo object;
        ObjectInfo ball;
        ObjectInfo goal;

        // first put it somewhere on my side
        if (Pattern.matches("^before_kick_off.*", m_playMode))
            m_krislet.move(-Math.random() * 52.5, 34 - Math.random() * 68.0);

        float turnAngle = 10;
        float objectDistance = 10;

        while (!m_timeOver) {
            boolean ballVisible = false;
            boolean ballInRange = false;
            boolean goalVisible = false;
            System.out.println("BRAIN SENSING");

            //ball visible?
            ball = m_memory.getObject("ball");

            //goal visible?
            if (m_side == 'l')
                goal = m_memory.getObject("goal r");
            else
                goal = m_memory.getObject("goal l");

            if (ball != null && ball.m_direction < 5.0) {
                if (ball.m_distance < 1.0) {
                    if (goal != null) {
                        world.addPercept(VCWorld.ball_in_view_close_goal_not_in_view);
                    } else {
                        world.addPercept(VCWorld.ball_in_view_close_goal_in_view);
                    }
                } else {
                    world.addPercept(VCWorld.ball_in_view_far);
                }

            } else {
                world.addPercept(VCWorld.ball_not_in_view);
            }

            try {
                Thread.sleep(2 * SoccerParams.simulator_step);
            } catch (Exception e) {
            }
        }
        m_krislet.bye();
    }


    //===========================================================================
    // Here are suporting functions for implement logic


    //===========================================================================
    // Implementation of SensorInput Interface

    //---------------------------------------------------------------------------
    // This function sends see information
    public void see(VisualInfo info) {
        m_memory.store(info);
    }


    //---------------------------------------------------------------------------
    // This function receives hear information from player
    public void hear(int time, int direction, String message) {
    }

    //---------------------------------------------------------------------------
    // This function receives hear information from referee
    public void hear(int time, String message) {
        if (message.compareTo("time_over") == 0)
            m_timeOver = true;

    }


    //===========================================================================
    // Private members
    private SendCommand m_krislet;          // robot which is controled by this brain
    private Memory m_memory;               // place where all information is stored
    private char m_side;
    volatile private boolean m_timeOver;
    private String m_playMode;
    private VCWorld world;

    //helper
    private boolean stringToBool(String string) throws Exception {
        if (string.equalsIgnoreCase("true")) {
            return true;
        } else if (string.equalsIgnoreCase("false")) {
            return false;
        } else {
            throw new Exception("Cannot map " + string + " to a boolean value");
        }
    }

}
