package test;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.*;
import java.awt.*;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;


public class NotebookSecurity extends KeyAdapter implements SerialPortEventListener {
    SerialPort serialPort = null;
    Date date;
    Webcam webcam = Webcam.getDefault();
    
    DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    final JFrame f = new JFrame("FullScreenTest");
    JLabel label1=new JLabel("Warning",SwingConstants.CENTER);
    JLabel label2=new JLabel("Vidoe is taken",SwingConstants.CENTER);
    JLabel label3=new JLabel("Do not touch my Notebook",SwingConstants.CENTER);
    
    private static final String PORT_NAMES[] = { 
        "/dev/tty.usbmodem", // Mac OS X
        "/dev/usbdev", // Linux
        "/dev/tty", // Linux
        "/dev/serial", // Linux
        "COM3", // Windows
    };
    
    private String appName;
    private BufferedReader input;
    private OutputStream output;
    
    private static final int TIME_OUT = 1000; // Port open timeout
    private static final int DATA_RATE = 9600; // Arduino serial port

    public NotebookSecurity() {
        appName = getClass().getName();
        
        f.dispose();
    	f.setUndecorated(true); //好像是去除标题栏    	
    	f.getGraphicsConfiguration().getDevice().setFullScreenWindow(f);
    	f.getContentPane().setBackground(Color.black); 
        
    	f.setLayout(new GridLayout(3, 1));
    	Container cp=f.getContentPane();

    	Font font = new Font(Font.DIALOG_INPUT, Font.ITALIC, 60);
    	label1.setFont(font);
    	label1.setForeground(Color.RED);
    	label1.setVisible(false);
    	cp.add(label1);
    	
    	label2.setFont(font);
    	label2.setForeground(Color.RED);
    	label2.setVisible(false);
    	cp.add(label2);

    	label3.setFont(font);
    	label3.setForeground(Color.RED);
    	label3.setVisible(false);
    	cp.add(label3);
    	
    	Webcam webcam = Webcam.getDefault();
		
        
        webcam.setViewSize(WebcamResolution.VGA.getSize());

		WebcamPanel panel = new WebcamPanel(webcam);
		panel.setFPSDisplayed(true);
		panel.setDisplayDebugInfo(true);
		panel.setImageSizeDisplayed(true);
		panel.setMirrored(true);

		JFrame window = new JFrame("Test webcam panel");
		window.add(panel);
		window.setResizable(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.pack();
		window.setVisible(true);
    	
       
    }

    public boolean initialize() {
        try {
            CommPortIdentifier portId = null;
            Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

            // Enumerate system ports and try connecting to Arduino over each
            //
            System.out.println( "Trying:");
            while (portId == null && portEnum.hasMoreElements()) {
                // Iterate through your host computer's serial port IDs
                //
                CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
                System.out.println( "   port" + currPortId.getName() );
                for (String portName : PORT_NAMES) {
                    if ( currPortId.getName().equals(portName) 
                      || currPortId.getName().startsWith(portName)) {

                        // Try to connect to the Arduino on this port
                        //
                        // Open serial port
                        serialPort = (SerialPort)currPortId.open(appName, TIME_OUT);
                        portId = currPortId;
                        System.out.println( "Connected on port" + currPortId.getName() );
                        break;
                    }
                }
            }
        
            if (portId == null || serialPort == null) {
                System.out.println("Oops... Could not connect to Arduino");
                return false;
            }
        
            // set port parameters
            serialPort.setSerialPortParams(DATA_RATE,
                            SerialPort.DATABITS_8,
                            SerialPort.STOPBITS_1,
                            SerialPort.PARITY_NONE);

            // add event listeners
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);

            // Give the Arduino some time
            try { Thread.sleep(2000); } catch (InterruptedException ie) {}
            
            return true;
        }
        catch ( Exception e ) { 
            e.printStackTrace();
        }
        return false;
    }
    //
    // This should be called when you stop using the port
    //
    public synchronized void close() {
        if ( serialPort != null ) {
            serialPort.removeEventListener();
            serialPort.close();
        }
    }
    //
    // Handle serial port event
    //
    public synchronized void serialEvent(SerialPortEvent oEvent) {
        //System.out.println("Event received: " + oEvent.toString());
        try {
            switch (oEvent.getEventType() ) {
                case SerialPortEvent.DATA_AVAILABLE: 
                    if ( input == null ) {
                        input = new BufferedReader(
                            new InputStreamReader(
                                    serialPort.getInputStream()));
                    }
                    String inputLine = input.readLine();
                    System.out.println(inputLine);
                    
//                    
                    
                    if (inputLine.equals("someone moving")){
                    	 
                    	BufferedImage image = webcam.getImage();
                    	
                		// save image to PNG file
                    	
                    	date = new Date();
                    	ImageIO.write(image, "PNG", new File(dateFormat.format(date)+".png"));
//                		ImageIO.write(image, "PNG", new File("test.png"));
                    	String dFormat = dateFormat1.format(date);
                    	
                    	
                    	uploadMySQL(dFormat,dateFormat.format(date)+".png");
                    	
                    	
                    	Waringtext(true);
                    	
                		try {
                		    Thread.sleep(2000);                 //1000 milliseconds is one second.
                		} catch(InterruptedException ex) {
                		    Thread.currentThread().interrupt();
                		}
                		Waringtext(false);
                		
                		sendPhoto(dateFormat.format(date));
//                		System.out.println("Photo has been taken");
                		
                    }
                    
                    break;

                default:
                    break;
            }
        } 
        catch (Exception e) {
            System.err.println(e.toString());
        }
    }
    
    public void sendPhoto(String fileName) throws UnknownHostException, IOException {
    	System.out.println("Transport begin");
    Socket socket = new Socket("140.113.73.131", 13267);
	System.out.println("Filename:"+fileName);
    
    //Send file
    File myFile = new File(fileName+".png");
    byte[] mybytearray = new byte[(int) myFile.length()];
     
    FileInputStream fis = new FileInputStream(myFile);
    BufferedInputStream bis = new BufferedInputStream(fis);
    //bis.read(mybytearray, 0, mybytearray.length);
     
    DataInputStream dis = new DataInputStream(bis);   
    dis.readFully(mybytearray, 0, mybytearray.length);
     
    OutputStream os = socket.getOutputStream();
     
    //Sending file name and file size to the server
    DataOutputStream dos = new DataOutputStream(os);   
    dos.writeUTF(myFile.getName());   
    dos.writeLong(mybytearray.length);   
    dos.write(mybytearray, 0, mybytearray.length);   
    dos.flush();
     
    //Sending file data to the server
     
//    os.write(mybytearray, 0, mybytearray.length);

    os.flush();
    dos.close();
    os.close();
    
    //Closing socket
    socket.close();
    dis.close();
    
    return;
}
    
    public void uploadMySQL(String Format,String fileName ) {
    	String driver = "com.mysql.jdbc.Driver";
		String url = "";
		
        	String user = "root"; 
        	String password = "";
        	try {
        		Class.forName(driver);
        		Connection conn = DriverManager.getConnection(url, user, password);

//            		insert into 資料表名 (欄位名1, 欄位名2) values(對應欄位名1之資料, 對應欄位名1之資料)
            	String SQL = "INSERT INTO `Arduino` (`start`,`content`) VALUES ('"+Format+"','"+fileName+"')";
            	Statement stmt = conn.createStatement();
            	stmt.executeUpdate(SQL);
            	conn.close();
        	} 
        	catch(ClassNotFoundException e) { 
            		e.printStackTrace(); 
        	}	 
        	catch(SQLException e) { 
            		e.printStackTrace(); 
        	}
    }
    
    public void Waringtext(boolean a) {
    	label1.setVisible(a);
    	label2.setVisible(a);
    	label3.setVisible(a);    
    
    }
    
    
    
    
    public static void main(String[] args) throws Exception {
        NotebookSecurity test = new NotebookSecurity();
        
        
        if ( test.initialize() ) {
//            
        }

  
        try { Thread.sleep(2000); } catch (InterruptedException ie) {}
    }
}


