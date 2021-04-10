import jason.asSyntax.ASSyntax;
import jason.asSyntax.Literal;
import jason.asSyntax.NumberTermImpl;
import jason.asSyntax.Structure;
import jason.environment.Environment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

/**
 * Simple Vacuum cleaning environment
 *
 * @author Jomi
 *
 */
public class VCWorld extends Environment {
    private Object modelLock = new Object();

    /** general delegations */
    private Logger   logger = Logger.getLogger("env."+VCWorld.class.getName());


    /** constant terms used for perception */
    public static final Literal before_kick_off = ASSyntax.createLiteral("before_kick_off");
    public static final Literal goal_l = ASSyntax.createLiteral("goal_l");
    public static final Literal goal_r = ASSyntax.createLiteral("goal_r");
    public static final Literal play_on = ASSyntax.createLiteral("play_on");
    public static final Literal kick_off_l = ASSyntax.createLiteral("kick_off_l");
    public static final Literal kick_off_r = ASSyntax.createLiteral("kick_off_r");

    private HashMap<String, KrisletContext> players = new HashMap<String, KrisletContext>();

    
    
    public VCWorld() {}
    
    private void joinTeam(String ag, String team, int player_num) {
    	clearPercepts();
        players.put(ag, new KrisletContext(ag,this,team));
        new Thread(players.get(ag)).start();
//        try {
//            Thread.sleep(200);
//        } catch (Exception e) {}
    }

    @Override
    public boolean executeAction(String ag, Structure action) {
        logger.info(ag + " EXECUTING:  "+action);

        synchronized (modelLock) {
            // Change the world model based on action
        	int player_num = Integer.parseInt(action.getTerms().get(0).toString());//((NumberTermImpl) (action.getTerms().get(1))).solve();
        	//System.out.println(player_num);
            switch (action.getFunctor()) {
                case "turn_to_ball":
                	ObjectInfo ball = this.players.get(ag).player.getBall(); //TODO: try to do this in BDI
                	if(ball != null) 
                		this.players.get(ag).player.turn(ball.getDirection());
                	else
                		this.players.get(ag).player.turn(40);
                    break;
                case "dash":
                	this.players.get(ag).player.dash(300);;
                    break;
                case "turn_to_goal":
                	this.players.get(ag).player.turn(30);
                    break;
                case "kick_start":
                	this.players.get(ag).player.kick(40, 40);
                    break;
                case "dribble":
                	this.players.get(ag).player.kick(10, 0);
                case "kick_to_goal":
                	ObjectInfo goal = this.players.get(ag).player.getGoal();
                	if(goal != null) 
                		this.players.get(ag).player.kick(100,goal.getDirection());
                	else
                		this.players.get(ag).player.kick(100, 0);
                    break;
                case "move":
                    waitForPlay(ag);
                    //System.out.println("IN ACTION MOVE WITH PARAMS: first param is: " + ((NumberTermImpl) (action.getTerms().get(0))).solve());
                	this.players.get(ag).player.move(((NumberTermImpl) (action.getTerms().get(1))).solve(), ((NumberTermImpl) (action.getTerms().get(2))).solve());
                    break;
                case "move_too":
                	//calculate player pos

                	//turn to point

                	//dash

                	//calc player new pos

                	//if there world.addpercept(there)

                	break;
                case "join_team":
                	this.joinTeam(ag, action.getTerms().get(1).toString(),player_num);///////////////////////////////////////////////////////////////////////////////
                	break;
                case "print":
                	System.out.println("AAAAAAAAAAAAAAA" +action.getTerms());
                	break;
                default:
                    logger.info("The action " + action + " is not implemented!");
                    return false;
            }
        }

        try {
            Thread.sleep(2 * SoccerParams.simulator_step);
        } catch (Exception e) {
        }

        clearPercepts(); // resets perceptions. Percepts are set by brain
        return true;
    }

    private void waitForPlay(String ag) {
        while (this.players.get(ag).player == null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void stop() {
        super.stop();
    }
}

