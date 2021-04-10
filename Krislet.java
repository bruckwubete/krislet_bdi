//
//  File:           Krislet.java
//  Author:     Krzysztof Langner
//  Date:           1997/04/28
//
//********************************************
//      Updated:               2008/03/01
//      By:               Edgar Acosta
//
//********************************************
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;


//***************************************************************************
//
//  This is main object class
//
//***************************************************************************

class Krislet implements SendCommand
{
    //===========================================================================
    // Initialization member functions

    //---------------------------------------------------------------------------
    // The main appllication function.
    // Command line format:
    //
    // krislet [-parameter value]
    //
    // Parameters:
    //
    //  host (default "localhost")
    //      The host name can either be a machine name, such as "java.sun.com" 
    //      or a string representing its IP address, such as "206.26.48.100."
    //
    //  port (default 6000)
    //      Port number for communication with server
    //
    //  team (default Kris)
    //      Team name. This name can not contain spaces.
    //
    //  
    public static void main(String a[]) 
    throws SocketException, IOException
    {
    String  hostName = new String("");
    int         port = 6000;
    String  team = new String("Krislet3");
    
    try
        {
        // First look for parameters
        for( int c = 0 ; c < a.length ; c += 2 )
            {
            if( a[c].compareTo("-host") == 0 )
                {
                hostName = a[c+1];
                }
            else if( a[c].compareTo("-port") == 0 )
                {
                port = Integer.parseInt(a[c+1]);
                }
            else if( a[c].compareTo("-team") == 0 )
                {
                team = a[c+1];
                }
            else
                {
                throw new Exception();
                }
            }
        }
    catch(Exception e)
        {
        System.err.println("");
        System.err.println("USAGE: krislet [-parameter value]");
        System.err.println("");
        System.err.println("    Parameters  value        default");
        System.err.println("   ------------------------------------");
        System.err.println("    host        host_name    localhost");
        System.err.println("    port        port_number  6000");
        System.err.println("    team        team_name    Kris");
        System.err.println("");
        System.err.println("    Example:");
        System.err.println("      krislet -host www.host.com -port 6000 -team Poland");
        System.err.println("    or");
        System.err.println("      krislet -host 193.117.005.223");
        return;
        }

    Krislet player = new Krislet(InetAddress.getByName(hostName),
                     port, team, "");

    // enter main loop
    player.mainLoop(null,"");
    }  

    //---------------------------------------------------------------------------
    // This constructor opens socket for  connection with server
    public Krislet(InetAddress host, int port, String team, String ag)
    throws SocketException
    {
    m_socket = new DatagramSocket();
    m_host = host;
    m_port = port;
    m_team = team;
    m_playing = true;
    }
                                                                 
    //---------------------------------------------------------------------------
    // This destructor closes socket to server
    public void finalize()
    {
    m_socket.close();
    }


    //===========================================================================
    // Protected member functions

    //---------------------------------------------------------------------------
    // This is main loop for player
    protected void mainLoop(VCWorld world,String ag) throws IOException
    {
    byte[] buffer = new byte[MSG_SIZE];
    DatagramPacket packet = new DatagramPacket(buffer, MSG_SIZE);

    // first we need to initialize connection with server
    init();

    m_socket.receive(packet);
    parseInitCommand(world, new String(buffer),ag);
    m_port = packet.getPort();

    // Now we should be connected to the server
    // and we know side, player number and play mode
    while( m_playing )
        parseSensorInformation(receive());
    finalize();
    }

    public class Coordinates{
        private double X;
        private double Y;
        public Coordinates(double X, double Y) {
            this.X = X;
            this.Y = Y;
        }
        public double getX() {
            return X;
        }
        public double getY() {
            return Y;
        }
    }
    public void getPlayerLocation(){
        if(m_brain == null) {
            System.out.println("Brain Not Initialized Yet, Can't determine location");
            return;
        }
        FlagInfo center_flag = m_brain.getFlag("flag c"); // (0,0)
        FlagInfo center_top_flag = m_brain.getFlag("flag c t"); // (0, -34)
        FlagInfo center_bottom_flag = m_brain.getFlag("flag c b"); // (0, 34)
        FlagInfo left_top_flag = m_brain.getFlag("flag l t"); // (-52.54, -34)
        FlagInfo left_bottom_flag = m_brain.getFlag("flag l b"); // (-52.54, 34)
        FlagInfo right_top_flag = m_brain.getFlag("flag r t"); // (52.54, -34)
        FlagInfo right_bottom_flag = m_brain.getFlag("flag r b"); // (52.54, 34)
        ArrayList<FlagInfo> availableFlags = new ArrayList<FlagInfo>();
        ArrayList<String> availableFlagNames = new ArrayList<String>();
        ArrayList<Coordinates> flagCoordinates = new ArrayList<Coordinates>();
        double x1 = 0;
        double y1 = 0;
        String flagName = "flag c";
        if(center_flag != null) {
            flagName = "flag c";
            x1 = 0;
            y1 = 0;
            availableFlags.add(center_flag);
            availableFlagNames.add(flagName);
            flagCoordinates.add(new Coordinates(x1, y1));
        }
        if(center_top_flag != null) {
            x1 = 0;
            y1 = -34;
            flagName = "flag c t";
            availableFlags.add(center_flag);
            availableFlagNames.add(flagName);
            flagCoordinates.add(new Coordinates(x1, y1));
        }
        if(center_bottom_flag != null) {
            x1 = 0;
            y1 = 34;
            flagName = "flag c b";
            availableFlags.add(center_flag);
            availableFlagNames.add(flagName);
            flagCoordinates.add(new Coordinates(x1, y1));
        }
        if(left_top_flag != null) {
            x1 = -52.54;
            y1 = -34;
            flagName = "flag l t";
            availableFlags.add(center_flag);
            availableFlagNames.add(flagName);
            flagCoordinates.add(new Coordinates(x1, y1));
        }
        if(left_bottom_flag != null) {
            x1 = -52.54;
            y1 = 34;
            flagName = "flag l b";
            availableFlags.add(center_flag);
            availableFlagNames.add(flagName);
            flagCoordinates.add(new Coordinates(x1, y1));
        }
        if(right_top_flag != null) {
            x1 = 52.54;
            y1 = -34;
            flagName = "flag r t";
            availableFlags.add(center_flag);
            availableFlagNames.add(flagName);
            flagCoordinates.add(new Coordinates(x1, y1));
        }
        if(right_bottom_flag != null) {
            x1 = 52.54;
            y1 = 34;
            flagName = "flag r b";
            availableFlags.add(center_flag);
            availableFlagNames.add(flagName);
            flagCoordinates.add(new Coordinates(x1, y1));
        }
        if(availableFlags.size() < 2) {
            System.out.println("Cannot Determine Location, Need at least 2 flags to determine locations");
        } else if(availableFlags.size() == 2){
            System.out.println("Number of Flags =======>" + availableFlags.size());
            FlagInfo firstFlag = availableFlags.get(0);
            String firstFlagName = availableFlagNames.get(0);
            Coordinates firstFlagCoods = flagCoordinates.get(0);
            FlagInfo secondFlag = availableFlags.get(1);
            String secondFlagName = availableFlagNames.get(1);
            Coordinates secondFlagCoods = flagCoordinates.get(1);
        } else {
        }
//        x1 = x1 + 52.54;
//        y1 = y1 + 34;
//
//        FlagInfo used_flag =  m_brain.getFlag(flagName);
//        System.out.println("======= Using "+flagName+" =========");
//        double distance = used_flag.getDistance();
//        double direction = used_flag.getDirection();
//        double direction_radian = Math.toRadians(direction);
//
//
//        System.out.println("Flag Coods  ======>(" + x1 + ", "+y1+")" );
//        System.out.println("Flag Direction Degree==========>" + direction);
//        System.out.println("Flag Direction Radian==========>" + direction_radian);
//        System.out.println("Flag Distance ==========>" + distance);
//
//        // Problem in calculating coods here: [Maths Formula]
//
//        double y2 = y1 + (distance * Math.sin(direction_radian));
//        double x2 = x1 + (distance * Math.cos(direction_radian));
//
//        if(direction<90 && direction>-90) {
//             x2 = x1 - (distance * Math.cos(direction_radian));
//        }
//
//        x2 = x2 - 52.54;
//        y2 = y2 - 34;
//
//        System.out.println("Player Coods  ======>(" + x2 + ", "+y2+")" );
    }


    //===========================================================================
    // Implementation of SendCommand Interface

    //---------------------------------------------------------------------------
    // This function sends move command to the server
    public void move(double x, double y)
    {
    send("(move " + Double.toString(x) + " " + Double.toString(y) + ")");
    }

    //---------------------------------------------------------------------------
    // This function sends turn command to the server
    public void turn(double moment)
    {
    send("(turn " + Double.toString(moment) + ")");
    }

    public void turn_neck(double moment)
    {
    send("(turn_neck " + Double.toString(moment) + ")");
    }

    //---------------------------------------------------------------------------
    // This function sends dash command to the server
    public void dash(double power)
    {
    send("(dash " + Double.toString(power) + ")");
    }

    //---------------------------------------------------------------------------
    // This function sends kick command to the server
    public void kick(double power, double direction)
    {
    send("(kick " + Double.toString(power) + " " + Double.toString(direction) + ")");
    }

    //---------------------------------------------------------------------------
    // This function sends say command to the server
    public void say(String message)
    {
    send("(say " + message + ")");
    }

    //---------------------------------------------------------------------------
    // This function sends chage_view command to the server
    public void changeView(String angle, String quality)
    {
    send("(change_view " + angle + " " + quality + ")");
    }
    
    public ObjectInfo getBall() {
    	return m_brain.getBall();
    }
    public ObjectInfo getGoal() {
    	return m_brain.getGoal();
    }
    //---------------------------------------------------------------------------
    // This function sends bye command to the server
    public void bye()
    {
    m_playing = false;
    send("(bye)");
    }

    //---------------------------------------------------------------------------
    // This function parses initial message from the server
    protected void parseInitCommand(VCWorld world, String message,String ag)
    throws IOException
    {
    Matcher m = Pattern.compile("^\\(init\\s(\\w)\\s(\\d{1,2})\\s(\\w+?)\\).*$").matcher(message);
    if(!m.matches())
        {
        throw new IOException(message);
        }

    // initialize player's brain
    m_brain = new Brain(world,this,
                m_team,
                m.group(1).charAt(0),
                Integer.parseInt(m.group(2)),
                m.group(3), ag);
    }



    //===========================================================================
    // Here comes collection of communication function
    //---------------------------------------------------------------------------
    // This function sends initialization command to the server
    private void init()
    {
    send("(init " + m_team + " (version 9))");
    }

    //---------------------------------------------------------------------------
    // This function parses sensor information
    private void parseSensorInformation(String message)
    throws IOException
    {
    // First check kind of information      
    Matcher m=message_pattern.matcher(message);
    if(!m.matches())
        {
        throw new IOException(message);
        }
    if( m.group(1).compareTo("see") == 0 )
        {
        VisualInfo  info = new VisualInfo(message);
        info.parse();
        m_brain.see(info);
        this.getPlayerLocation();
        }
    else if( m.group(1).compareTo("hear") == 0 )
        parseHear(message);
    }


    //---------------------------------------------------------------------------
    // This function parses hear information
    private void parseHear(String message)
    throws IOException
    {
    // get hear information
    Matcher m=hear_pattern.matcher(message);
    int time;
    String sender;
    String uttered;
    if(!m.matches())
        {
        throw new IOException(message);
        }
    time = Integer.parseInt(m.group(1));
    sender = m.group(2);
    uttered = m.group(3);
    if( sender.compareTo("referee") == 0 )
        m_brain.hear(time,uttered);
    //else if( coach_pattern.matcher(sender).find())
    //    m_brain.hear(time,sender,uttered);
    else if( sender.compareTo("self") != 0 )
        m_brain.hear(time,Integer.parseInt(sender),uttered);
    }



    //---------------------------------------------------------------------------
    // This function sends via socket message to the server
    private void send(String message)
    {
    byte[] buffer = Arrays.copyOf(message.getBytes(),MSG_SIZE);
    try{
        DatagramPacket packet = new DatagramPacket(buffer, MSG_SIZE, m_host, m_port);
        m_socket.send(packet);
    }
    catch(IOException e){
        System.err.println("socket sending error " + e);
    }

    }

    //---------------------------------------------------------------------------

    // This function waits for new message from server
    private String receive() 
    {
    byte[] buffer = new byte[MSG_SIZE];
    DatagramPacket packet = new DatagramPacket(buffer, MSG_SIZE);
    try{
        m_socket.receive(packet);
    }catch(SocketException e){ 
        System.out.println("shutting down...");
    }catch(IOException e){
        System.err.println("socket receiving error " + e);
    }
    return new String(buffer);
    }
                
                                 
    //===========================================================================
    // Private members
    // class members
    private DatagramSocket  m_socket;       // Socket to communicate with server
    private InetAddress     m_host;         // Server address
    private int         m_port;         // server port
    private String      m_team;         // team name
    private SensorInput     m_brain;        // input for sensor information
    private boolean             m_playing;              // controls the MainLoop
    private Pattern message_pattern = Pattern.compile("^\\((\\w+?)\\s.*");
    private Pattern hear_pattern = Pattern.compile("^\\(hear\\s(\\w+?)\\s(\\w+?)\\s(.*)\\).*");
    //private Pattern coach_pattern = Pattern.compile("coach");
    // constants
    private static final int    MSG_SIZE = 4096;    // Size of socket buffer

}
