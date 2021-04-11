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

    private HashMap<String, KrisletContext> players = new HashMap<String, KrisletContext>();


    
    public VCWorld() {}
    
    private void joinTeam(String ag, String team, int player_num) {
    	clearPercepts();
        players.put(ag, new KrisletContext(ag,this,team));
        new Thread(players.get(ag)).start();
    }

    @Override
    public boolean executeAction(String ag, Structure action) {
        logger.info(ag + " EXECUTING:  "+action);
        synchronized (modelLock) {
        	ObjectInfo goal;
            switch (action.getFunctor()) {
                case "turn":
                    Double a = Double.parseDouble(action.getTerm(0).toString());
                    this.players.get(ag).player.turn(0.1 *  a);
                    break;
                case "dash":
                    Double d = Double.parseDouble(action.getTerm(0).toString());
                	this.players.get(ag).player.dash(8 * Math.pow(d, 2));
                    break;
                case "kick":
                    Double p = Double.parseDouble(action.getTerm(0).toString());
                    Double ka = 0.0;
                    if(action.getTerm(1) !=null) {
                        ka = Double.parseDouble(action.getTerm(1).toString());
                    }
                    this.players.get(ag).player.kick(p * 100, ka);
                    break;
                case "move":
                    waitForPlay(ag);
                    //System.out.println("IN ACTION MOVE WITH PARAMS: first param is: " + ((NumberTermImpl) (action.getTerms().get(0))).solve());
                	this.players.get(ag).player.move(((NumberTermImpl) (action.getTerms().get(0))).solve(), ((NumberTermImpl) (action.getTerms().get(1))).solve());
                    break;
                case "join_team":
                	this.joinTeam(ag, action.getTerms().get(1).toString(),Integer.parseInt(action.getTerms().get(0).toString()));///////////////////////////////////////////////////////////////////////////////
                	break;
                default:
                    logger.info("The action " + action + " is not implemented!");
                    return false;
            }
        }

        try {
            Thread.sleep(1 * SoccerParams.simulator_step);
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

