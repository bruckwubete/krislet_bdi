import java.net.InetAddress;

public class KrisletContext implements Runnable {
    public  Krislet player;
    private VCWorld world;
    public KrisletContext(VCWorld world) {
        super();
        this.world = world;
    }
    public void run() {
        try {
            String  hostName = new String("");
            int         port = 6000;
            String  team = new String("Krislet3");
            this.player = new Krislet(InetAddress.getByName(hostName), port, team);

            // enter main loop
            this.player.mainLoop(world);

        } catch (Exception e) {}
    }
};
