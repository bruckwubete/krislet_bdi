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
    public ObjectInfo getFlag(String flag) {
    	return m_brain.getFlag(flag);
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
    throws IOException {
        // First check kind of information
        Matcher m = message_pattern.matcher(message);
        // System.out.println(message);
        if (!m.matches()) {
            throw new IOException(message);
        }
        if (m.group(1).compareTo("see") == 0) {
            // System.out.println("RECEIVED SEE INFO FROM SERVER" + message);
            VisualInfo info = new VisualInfo(message);
            info.parse();
            m_brain.see(info);
        } else if (m.group(1).compareTo("hear") == 0)
            parseHear(message);
        else if (m.group(1).compareTo("sense_body") == 0) {
            //System.out.println("GOT SENSE BODY MESSAGE: " + message);
            int indexOfSpeed = message.indexOf("speed");
            String speeds = message.substring(indexOfSpeed+6, message.indexOf(")", indexOfSpeed+6));
            //System.out.println("INDEX OF SPEED: " + indexOfSpeed + "SPEED IS " + speeds);
            float direction = Float.parseFloat(speeds.split(" ")[1]);
            //System.out.println("SPEED DIRECTION IS: " + direction);
            m_brain.setSpeedDirection(new Float(direction));
        }
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
