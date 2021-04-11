//
//  File:           Brain.java
//  Author:     Krzysztof Langner
//  Date:           1997/04/28
//
//    Modified by:  Paul Marlow

//    Modified by:      Edgar Acosta
//    Date:             March 4, 2008

import jason.asSyntax.ASSyntax;
import jason.asSyntax.Literal;

import java.util.*;
import java.lang.*;



class Brain extends Thread implements SensorInput {
    //---------------------------------------------------------------------------
    // This constructor:
    // - stores connection to krislet
    // - starts thread for this object

    HashMap<String, List<String>> relevantObjects = new HashMap<String, List<String>>(){{
        put("ball", new ArrayList<String>(){{
            add("");
        }});
        put("flag", new ArrayList<String>(){{
            add(" c "); add(" rt"); add(" rb"); add(" lt"); add(" lb"); add(" ct"); add(" cb");
            add("glt"); add("glb"); add("grt"); add("grb");
            add("plt"); add("plc"); add("plb"); add("prt"); add("prc"); add("prb");
        }});
        put("goal", new ArrayList<String>(){{
            add("l"); add("r");
        }});
        put("line", new ArrayList<String>(){{
            add("t"); add("b");; add("l");; add("r");
        }});
    }};
    List<String> playModes = new LinkedList<String>(){{
        add("before_kick_off"); add("play_on");
        add("goal_l"); add("goal_r");
        add("goal_kick_l"); add("goal_kick_r");
        add("kick_in_l"); add("kick_in_r");
        add("kick_off_l"); add("kick_off_r");
    }};
    HashMap<String, Literal> playModeToPercept = new HashMap<String, Literal>();


    public Brain(KrisletWorld world, SendCommand krislet,
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

        playModes.forEach(m -> {
            playModeToPercept.put(m, ASSyntax.createLiteral(m));
        });
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
        while (!m_timeOver) {
            //clear percepts
            world.clearPercepts(m_name);
            world.clearPercepts();

            // add play mode if we care about it
            m_playMode = m_playMode.replaceAll("_([0-9]+)", "");
            Literal playModeL = playModeToPercept.get(m_playMode);
            if(playModeL != null){
                world.addPercept(playModeL);
            }

            relevantObjects.forEach((objName, objValues) -> {
                objValues.forEach(v -> {
                    ObjectInfo obj = m_memory.getObject(objName, v);
                    if(obj != null) {
                        world.addPercept(m_name, ASSyntax.createLiteral("distance", ASSyntax.createString(objName), ASSyntax.createString(v), ASSyntax.createNumber(obj.m_distance)));
                        world.addPercept(m_name, ASSyntax.createLiteral("direction", ASSyntax.createString(objName), ASSyntax.createString(v), ASSyntax.createNumber(obj.m_direction)));
                    }
                });
            });

            try {
                Thread.sleep(2*SoccerParams.simulator_step);
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
    private KrisletWorld world;
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