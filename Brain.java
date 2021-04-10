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
import java.lang.*;



class Brain extends Thread implements SensorInput {
    //---------------------------------------------------------------------------
    // This constructor:
    // - stores connection to krislet
    // - starts thread for this object
    public Brain(VCWorld world, SendCommand krislet,
                 String team,
                 char side,
                 int number,
                 String playMode, String ag) {
        this.world = world;
        m_timeOver = false;
        m_krislet = krislet;
        m_memory = new Memory();
        m_team = team;
        m_side = side;
        m_number = number;
        m_playMode = playMode;
        m_name = ag;

        System.out.println("Name: "+m_name);
        start();
    }

    ObjectInfo ball;
    ObjectInfo goal;

    FlagInfo rtFlag;
    double x, y;
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

        float turnAngle = 10;
        float objectDistance = 10;

        while (!m_timeOver) {

            world.clearPercepts(m_name);



            //System.out.println(world.);
            //System.out.println("PLAY MODE IS IN: " + m_playMode);
            if (Pattern.matches("^before_kick_off.*", m_playMode)) {
                world.clearPercepts();
                world.addPercept(VCWorld.before_kick_off);
            }

            if (Pattern.matches("^goal_l.*", m_playMode)) {
                world.clearPercepts();
                world.addPercept(VCWorld.goal_l);
            }

            if (Pattern.matches("^goal_r.*", m_playMode)) {
                world.clearPercepts();
                world.addPercept(VCWorld.goal_r);
            }

            if (Pattern.matches("^play_on.*", m_playMode)) {
                world.clearPercepts();
                world.addPercept(VCWorld.play_on);
            }

            if (Pattern.matches("^kick_off_l.*", m_playMode)) {
                world.clearPercepts();
                world.addPercept(VCWorld.kick_off_l);
            }

            if (Pattern.matches("^kick_off_r.*", m_playMode)) {
                world.clearPercepts();
                world.addPercept(VCWorld.kick_off_r);
            }

            boolean ballVisible = false;
            boolean ballInRange = false;
            boolean goalVisible = false;
            //System.out.println("BRAIN SENSING");

            //ball visible?
            ball = m_memory.getObject("ball");
            //goal visible?
            if (m_side == 'l')
                goal = m_memory.getObject("goal r");
            else
                goal = m_memory.getObject("goal l");

            if (ball != null && ball.m_direction < 2.0) {
                if (ball.m_distance < 1.0) {
                    //world.addPercept(VCWorld.ball_in_view_close);
                    //world.removePercept(VCWorld.ball_in_view_far);
                    //world.removePercept(VCWorld.ball_not_in_view);
                    world.addPercept(m_name,VCWorld.ball_in_view_close);
                    world.removePercept(m_name,VCWorld.ball_in_view_far);
                    world.removePercept(m_name,VCWorld.ball_not_in_view);
                } else {
                    world.addPercept(m_name,VCWorld.ball_in_view_far);
                    world.removePercept(m_name,VCWorld.ball_in_view_close);
                    world.removePercept(m_name,VCWorld.ball_not_in_view);
                    //world.addPercept(VCWorld.ball_in_view_far);
                    //world.removePercept(VCWorld.ball_in_view_close);
                    //world.removePercept(VCWorld.ball_not_in_view);
                }

            } else {
                world.addPercept(m_name,VCWorld.ball_not_in_view);
                world.removePercept(m_name,VCWorld.ball_in_view_close);
                world.removePercept(m_name,VCWorld.ball_in_view_far);
                //world.addPercept(VCWorld.ball_not_in_view);
                //world.removePercept(VCWorld.ball_in_view_close);
                //world.removePercept(VCWorld.ball_in_view_far);
            }

            if(goal != null) {
                if (goal.getDistance() < 25.0) {
                    world.addPercept(m_name,VCWorld.net_close);
                    world.removePercept(m_name,VCWorld.net_far);
                    //world.addPercept(VCWorld.net_close);
                    //world.removePercept(VCWorld.net_far);
                } else {
                    world.addPercept(m_name,VCWorld.net_far);
                    world.removePercept(m_name,VCWorld.net_close);
                    //world.addPercept(VCWorld.net_far);
                    //world.removePercept(VCWorld.net_close);
                }
            }else {
                world.addPercept(m_name,VCWorld.cant_see_net);
                world.removePercept(m_name,VCWorld.net_far);
                world.removePercept(m_name,VCWorld.net_close);
                //world.addPercept(VCWorld.cant_see_net);
                //world.removePercept(VCWorld.net_far);
                //world.removePercept(VCWorld.net_close);
            }

            //System.out.println("Belief of " + m_name + " is: " + world.getPercepts(m_name));

            rtFlag = m_memory.getFlag(" rt");
            float speedDirection = m_memory.getSpeedDirection();
            if(rtFlag != null) {
                float flgDirection = Math.abs(Math.abs(rtFlag.getDirection()) - Math.abs(speedDirection));
                float flgDistance = rtFlag.getDistance();
                System.out.println("REAL flgDirection " + flgDirection);
                if (flgDirection > 90) flgDirection = 180 - flgDirection;
                System.out.println("flgDirection: " + flgDirection + " flgDistance: " + flgDistance);

                //System.out.println("FOUND RT FLAG AT " + rtFlag.getDistance() + rtFlag.getDirection());
                x = 105 -  (Math.cos(Math.toRadians(flgDirection)) * flgDistance);
                y = Math.sin(Math.toRadians(flgDirection)) * flgDistance;

            }
            System.out.printf("coordinate: (%s, %s)%n", x, y);


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
    public void setSpeedDirection(Float d) {
        m_memory.storeSpeedDirection(d);
    }

    public ObjectInfo getBall() {
        return ball;
    }
    public ObjectInfo getGoal() {
        return goal;
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
        System.out.println("HEARD MESSAGE " + message);
        m_playMode = message;

    }


    //===========================================================================
    // Private members
    private SendCommand m_krislet;          // robot which is controled by this brain
    private Memory m_memory;               // place where all information is stored
    private char m_side;
    volatile private boolean m_timeOver;
    private String m_playMode;
    private VCWorld world;
    private String m_name;
    private String m_team;
    private int m_number;

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