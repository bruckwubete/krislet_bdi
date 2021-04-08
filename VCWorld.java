import jason.asSyntax.ASSyntax;
import jason.asSyntax.Literal;
import jason.asSyntax.NumberTermImpl;
import jason.asSyntax.Structure;
import jason.environment.Environment;

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
    public static final Literal play_on = ASSyntax.createLiteral("play_on");
    public static final Literal kick_off_l = ASSyntax.createLiteral("kick_off_l");
    public static final Literal kick_off_r = ASSyntax.createLiteral("kick_off_r");
    public static final Literal ball_not_in_view = ASSyntax.createLiteral("ball_not_in_view");
    public static final Literal ball_in_view_far = ASSyntax.createLiteral("ball_in_view_far");
    public static final Literal ball_in_view_close = ASSyntax.createLiteral("ball_in_view_close");

    private KrisletContext playerContext;

    public VCWorld() {
        clearPercepts();
        playerContext = new KrisletContext(this);
        new Thread(playerContext).start();
        try {
            Thread.sleep(200);
        } catch (Exception e) {}
    }

    @Override
    public boolean executeAction(String ag, Structure action) {
        logger.info(ag + " EXECUTING:  "+action);

        synchronized (modelLock) {
            // Change the world model based on action
            switch (action.getFunctor()) {
                case "turn_to_ball":
                    this.playerContext.player.turn(10);
                    break;
                case "dash_to_ball":
                    this.playerContext.player.dash(100);
                    break;
                case "turn_to_goal":
                    this.playerContext.player.turn(30);
                    break;
                case "kick_to_goal":
                    this.playerContext.player.kick(100, 0);
                    break;
                case "move":
                    //System.out.println("IN ACTION MOVE WITH PARAMS: first param is: " + ((NumberTermImpl) (action.getTerms().get(0))).solve());
                    this.playerContext.player.move(((NumberTermImpl) (action.getTerms().get(0))).solve(), ((NumberTermImpl) (action.getTerms().get(1))).solve());
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

