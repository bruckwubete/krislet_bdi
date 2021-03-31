import jason.asSyntax.ASSyntax;
import jason.asSyntax.Literal;
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

    public static final Literal ball_not_in_view = ASSyntax.createLiteral("ball_not_in_view");
    public static final Literal ball_in_view_far = ASSyntax.createLiteral("ball_in_view_far");
    public static final Literal ball_in_view_close_goal_not_in_view = ASSyntax.createLiteral("ball_in_view_close_goal_not_in_view");
    public static final Literal ball_in_view_close_goal_in_view = ASSyntax.createLiteral("ball_in_view_close_goal_in_view");
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
        logger.info("doing "+action);

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
                case "kick_to_gaol":
                    this.playerContext.player.kick(100, 0);
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

