import jason.asSyntax.ASSyntax;
import jason.asSyntax.NumberTermImpl;
import jason.asSyntax.Structure;
import jason.environment.Environment;

import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Krislet world environment
 */
public class KrisletWorld extends Environment {

    private HashMap<String, KrisletContext> players = new HashMap<String, KrisletContext>();

    public KrisletWorld() {
        super(5);
    }

    private void joinTeam(String ag, String team) {
        clearPercepts();
        players.put(ag, new KrisletContext(ag, this, team));
        new Thread(players.get(ag)).start();
    }

    @Override
    public boolean executeAction(String ag, Structure action) {
        getLogger().info(ag + " EXECUTING:  " + action);

        try {
            switch (action.getFunctor()) {
                // main commands
                case "catch":
                    Double c = Double.parseDouble(action.getTerm(0).toString());
                    this.players.get(ag).player.send_catch(0.3 * c);
                    break;
                case "turn":
                    Double a = Double.parseDouble(action.getTerm(0).toString());
                    this.players.get(ag).player.turn(0.3 * a);
                    break;
                case "dash":
                    Double d = Double.parseDouble(action.getTerm(0).toString());
                    this.players.get(ag).player.dash(8 * Math.pow(d, 2));
                    break;
                case "kick":
                    Double p = Double.parseDouble(action.getTerm(0).toString());
                    Double ka = 0.0;
                    if (action.getTerm(1) != null) {
                        ka = Double.parseDouble(action.getTerm(1).toString());
                    }
                    this.players.get(ag).player.kick(p * 100, ka);
                    break;
                case "move":
                    this.players.get(ag).player.move(((NumberTermImpl) (action.getTerms().get(0))).solve(), ((NumberTermImpl) (action.getTerms().get(1))).solve());
                    break;

                // admin commands
                case "join_team":
                    String team = action.getTerms().get(0).toString();
                    if (team.equals("right")) {
                        Thread.sleep(100);
                    }
                    this.joinTeam(ag, team);
                    break;
                case "init_x":
                    waitForPlay(ag);
                    Double x = Double.parseDouble(action.getTerms().get(0).toString());
                    this.players.get(ag).initX = x;
                    break;
                case "init_y":
                    Double y = Double.parseDouble(action.getTerms().get(0).toString());
                    this.players.get(ag).initY = y;
                    break;
                case "station":
                    String station = action.getTerms().get(0).toString();
                    String detals = action.getTerms().get(1).toString();
                    this.players.get(ag).station = station;
                    this.players.get(ag).stationDetails = detals;
                    break;
                default:
                    getLogger().info("The action " + action + " is not implemented!");
                    return false;
            }

            Thread.sleep(1 * SoccerParams.simulator_step);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getLocalizedMessage());
        }

        //clearPercepts(); // resets perceptions. Percepts are set by brain
        return true;
    }

    private void waitForPlay(String ag) {
        while (this.players.get(ag) == null || this.players.get(ag).getPlayer() == null) {
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

