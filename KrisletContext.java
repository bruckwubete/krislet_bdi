import java.net.InetAddress;

public class KrisletContext implements Runnable {
    public  Krislet player;
    public Double initY = 0.0;
    public Double initX = 0.0;
    private KrisletWorld world;
    private String name;
    public String team;
    public String station = "";
    public String stationDetails = "";
    
    

    
    public KrisletContext(String ag, KrisletWorld world, String team) {
        super();
        this.world = world;
        this.team = team;
        this.name = ag;
    }

    public void run() {
        try {
            String  hostName = new String("");
            int         port = 6000;
            //String  team = new String("Krislet3");

            this.player = new Krislet(InetAddress.getByName(hostName), port, team, name);

            // enter main loop
            this.player.mainLoop(world, name, this);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getLocalizedMessage());
        }
    }

    public Krislet getPlayer() {
        return this.player;
    }
    
};
