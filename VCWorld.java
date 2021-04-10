import jason.asSyntax.ASSyntax;
import jason.asSyntax.Literal;
import jason.asSyntax.NumberTermImpl;
import jason.asSyntax.Structure;
import jason.environment.Environment;

import java.util.ArrayList;
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
    public static final Literal ball_not_in_view = ASSyntax.createLiteral("ball_not_in_view");
    public static final Literal ball_in_view_far = ASSyntax.createLiteral("ball_in_view_far");
    public static final Literal ball_in_view_close = ASSyntax.createLiteral("ball_in_view_close");
    
    //net literals
    public static final Literal net_close = ASSyntax.createLiteral("net_close");
    public static final Literal net_far = ASSyntax.createLiteral("net_far");
    public static final Literal cant_see_net = ASSyntax.createLiteral("cant_see_net");
    
    public static final Literal ball_in_my_side = ASSyntax.createLiteral("ball_in_my_side");

    //private KrisletContext playerContext;
    private List<KrisletContext> players = new ArrayList<KrisletContext>();
    
    
    public VCWorld() {
        //clearPercepts();
        //playerContext = new KrisletContext(this,"right");
        //new Thread(playerContext).start();
        //try {
        //    Thread.sleep(200);
        //} catch (Exception e) {}
    	for(int i = 0; i<10; i++) {
        	players.add(null);
        }
    }
    
    private void joinTeam(String ag, String team, int player_num) {
    	clearPercepts();
        players.add(player_num,new KrisletContext(ag,this,team));
        new Thread(players.get(player_num)).start();
        try {
            Thread.sleep(200);
        } catch (Exception e) {}
    }
    
    @Override
    public boolean executeAction(String ag, Structure action) {
        logger.info(ag + " EXECUTING:  "+action);
        
        synchronized (modelLock) {
            // Change the world model based on action
        	int player_num = Integer.valueOf(action.getTerms().get(0).toString());//((NumberTermImpl) (action.getTerms().get(1))).solve();
        	System.out.println(player_num);
            switch (action.getFunctor()) {
                case "turn_to_ball":
                	ObjectInfo ball = this.players.get(player_num).player.getBall();
                	if(ball != null) 
                		this.players.get(player_num).player.turn(ball.getDirection());
                	else
                		this.players.get(player_num).player.turn(40);
                    break;
                case "dash_to_ball":
                	this.players.get(player_num).player.dash(100);;
                    break;
                case "turn_to_goal":
                	this.players.get(player_num).player.turn(30);
                    break;
                case "kick_start":
                	this.players.get(player_num).player.kick(40,40);
                    break;
                case "dribble":
                	this.players.get(player_num).player.kick(10, 0);
                case "kick_to_goal":
                	ObjectInfo goal = this.players.get(player_num).player.getGoal();
                	if(goal != null) 
                		this.players.get(player_num).player.kick(100,goal.getDirection());
                	else
                		this.players.get(player_num).player.kick(100, 0);
                    break;
                case "move":
                    //System.out.println("IN ACTION MOVE WITH PARAMS: first param is: " + ((NumberTermImpl) (action.getTerms().get(0))).solve());
                	this.players.get(player_num).player.move(((NumberTermImpl) (action.getTerms().get(1))).solve(), ((NumberTermImpl) (action.getTerms().get(2))).solve());
                    break;
                case "move_too":
                	//calculate player pos
                	
                	//turn to point 
                	
                	//dash
                	
                	//calc player new pos
                	
                	//if there world.addpercept(there)
                	
                	break;
                case "join_team":
                	this.joinTeam(ag,action.getTerms().get(1).toString(),player_num);///////////////////////////////////////////////////////////////////////////////
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

    @Override
    public void stop() {
        super.stop();
    }
}

