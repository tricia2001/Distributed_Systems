// Patricia Vines
// 1000536317

package clients;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.Timer;

public class client_view extends JFrame {
	// JFrame window used for each client GUI.
	
	// displays messages from the server on the client GUI
	private JLabel message = new JLabel("");  
	// button used to upload messages to a queue
	private JButton uploadButton = new JButton("Upload Message");
	// button used to check for messages in a queue
	private JButton checkButton = new JButton("Check for Messages");
	// button that sends queue to server for message check
	private JButton c2Button = new JButton("Check for Messages");  
	// button that sends length and queue to server for upload
	private JButton addButton = new JButton("Add to Queue"); 
	// button that disconnects the client from the server and ends the client process
	private JButton quitButton = new JButton("Quit");  
	// displays server connection status of client
	private JLabel connected = new JLabel("Client is not connected to the server");
	// create a label that prompts the user for a length
	private JLabel lengthm = new JLabel("Enter length in meters: "); 
	// input field for length
	private JTextField length = new JTextField(5);  
	// create a label that prompts the user for a queue
	private JLabel queuep = new JLabel("Enter queue (A, B, or C): "); 
	// input field for length
	private JTextField queue = new JTextField(5);  
	
	
	private int text = 20;  // text size for all labels and buttons
	
    public client_view(String title, int x, int y, int num_messages) {
    	// constructor method for the client GUI
    	// inputs:
    	// title - String used to identify the type of window needed
    	// x - int x position of window
    	// y - int y position of window
    	
    	// Set text size for all labels and buttons to work on my 4K screen
    	message.setFont(new Font("Serif", Font.PLAIN, text));
    	uploadButton.setFont(new Font("Serif", Font.PLAIN, text));
    	checkButton.setFont(new Font("Serif", Font.PLAIN, text));
    	addButton.setFont(new Font("Serif", Font.PLAIN, text));
    	quitButton.setFont(new Font("Serif", Font.PLAIN, text));
    	connected.setFont(new Font("Serif", Font.PLAIN, text));
    	lengthm.setFont(new Font("Serif", Font.PLAIN, text)); 
    	length.setFont(new Font("Serif", Font.PLAIN, text)); 
    	queuep.setFont(new Font("Serif", Font.PLAIN, text)); 
    	queue.setFont(new Font("Serif", Font.PLAIN, text)); 
    	
        // If "upload" is in the title, the GUI needs to request a length in meters and a queue
    	if (title.contains("upload")) {
    		// parse title to get client number
    		String[] message = title.split(" ");   
    		// call upload method to create GUI and request queue and length in meters
    		upload(message[0]+" "+message[1], x, y);  
    	}
    	// If "connected" is in the title, the GUI needs to  remove the username input field and "Connect" button
    	else if (title.contains("check")) {
    		// parse title to get client number
    		String[] message = title.split(" ");  
    		check(message[0]+" "+message[1], x, y); // call connected method to create new GUI 
    	}
    	//Else either the process has just been started or the server has shut down or the client has disconnected after transaction
    	else
    		initUI(title, x, y, num_messages);// call initUI method to create initial GUI 
    }
    
    public void upload(String title, int x, int y) {
    	// upload method creates GUI to request queue name and 
    	// length in meters from user
    	// inputs:
    	// title - title to add to the title of the window
    	// x - x position of window
    	// y - y position of window
    	
        // set the top label to show that the client is connected to the server
        connected.setText(title+" is connected to the server for upload."); 
        
        // set the layout for labels and buttons
        uploadLayout(connected, lengthm, length, queuep, queue, addButton, quitButton, message); 
        
        // set the "Add" button to default
        this.getRootPane().setDefaultButton(addButton);
        
        setTitle(title);  // set GUI title to the client title
        setSize(500, 300);  // set size of GUI
        setLocation(x,y);  // set location of GUI
        setDefaultCloseOperation(EXIT_ON_CLOSE);  // set default close operation to exit
	}
    
    public void check(String title, int x, int y) {
    	// check method creates GUI to request queue name from user
    	// inputs:
    	// title - title to add to the title of the window
    	// x - x position of window
    	// y - y position of window
    	
        // set the top label to show that the client is connected to the server
        connected.setText(title+" is connected to the server for checking messages."); 
        
        // set the layout for labels and buttons
        checkLayout(connected, queuep, queue, c2Button, quitButton, message); 
        
        // set the "Check" button to default
        this.getRootPane().setDefaultButton(c2Button);
        
        setTitle(title);  // set GUI title to the client title
        setSize(500, 300);  // set size of GUI
        setLocation(x,y);  // set location of GUI
        setDefaultCloseOperation(EXIT_ON_CLOSE);  // set default close operation to exit
	}
    
    private void initUI(String title, int x, int y, int num_messages) {
    	// sets username and labels/buttons for initial window or after server disconnect
    	// modified from code found at
    	// http://zetcode.com/javaswing/firstprograms/
    	// inputs:
    	// title - Client # for window title
    	// x - x position of window
    	// y - y position of window
    	
    	// create a label that prompts the user for a username
    	JLabel username = new JLabel("Enter Username: "); 
    	// set label text formatting to match other labels
    	username.setFont(new Font("Serif", Font.PLAIN, text));  
    	
    	// set the layout for labels and buttons
        createLayout(connected, message, uploadButton, checkButton, quitButton);  
        
        // set the "Upload" button to default
        this.getRootPane().setDefaultButton(uploadButton);
        
        setTitle(title);  // set GUI title to the client #
        setSize(500, 300+num_messages*150);  // set size of GUI, adjust height when there are more messages
        setLocation(x,y);  // set location of GUI
        setDefaultCloseOperation(EXIT_ON_CLOSE);  // set default close operation to exit
    }
    
    
    private void createLayout(JComponent... arg) {
    	// sets JFrame layout for the initial window or window if server has disconnected
    	// modified from code found at
    	// http://zetcode.com/javaswing/firstprograms/
    	// inputs: 
    	// connected message JLabel - arg[0]
    	// username prompt JLabel - arg[1]
    	// username text field - arg[2]
    	// message from server JLabel - arg[3]
    	// connect JButton - arg[4]
    	// quit JButton - arg[5]
    	

    	// get the layer of the JFrame that holds the objects
        Container pane = getContentPane();  
        // create the layout for the objects
        GroupLayout gl = new GroupLayout(pane);
        // set the created layout to the container layer
        pane.setLayout(gl);

        // set gaps to auto
        gl.setAutoCreateGaps(true);
        gl.setAutoCreateContainerGaps(true);

        // set the buttons and labels in the horizontal group
        gl.setHorizontalGroup(gl.createParallelGroup()
        		// connection message on top
        		.addComponent(arg[0])
                // message from server
                .addComponent(arg[1])
                // add upload and check messages buttons next to each other
                .addGroup(gl.createSequentialGroup()
                		// upload button
                		.addComponent(arg[2])
                		// check for messages button
                		.addComponent(arg[3])
                // quit button
                .addComponent(arg[4]))
        );
        // set the buttons and labels in the vertical group
        gl.setVerticalGroup(gl.createSequentialGroup()
        		// connection message on top
        		.addComponent(arg[0])
        		// message from server
                .addComponent(arg[1])
                // add upload and check messages buttons next to each other
                .addGroup(gl.createParallelGroup()
                		// upload button
                		.addComponent(arg[2])
                		// check for messages button
                		.addComponent(arg[3])
                // quit button
                .addComponent(arg[4]))
        );
    }
    
    public void uploadLayout(JComponent... arg) {
    	// sets JFrame layout for the window after client has connected to the server for upload
    	// removes username inputs and connect button from createLayout
    	// modified from code found at
    	// http://zetcode.com/javaswing/firstprograms/
    	// inputs: 
    	// connected message JLabel - arg[0]
    	// length prompt JLabel - arg[1]
    	// length input JTextField - arg[2]
    	// queue prompt JLabel - arg[3]
    	// queue input JTextField - arg[4]
    	// add to queue JButton - arg[5]
    	// quit JButton - arg[6]
    	// message from server JLabel - arg[7]
    	
    	// get the layer of the JFrame that holds the objects
        Container pane = getContentPane();
        // create the layout for the objects
        GroupLayout gl = new GroupLayout(pane);
        // set the created layout to the container layer
        pane.setLayout(gl);

        // set gaps to auto
        gl.setAutoCreateGaps(true);
        gl.setAutoCreateContainerGaps(true);

        // set the buttons and labels in the horizontal group
        gl.setHorizontalGroup(gl.createParallelGroup()
        		// connection message
        		.addComponent(arg[0])
        		// server message
        		.addComponent(arg[7])
        		// length prompt and input
        		.addGroup(gl.createSequentialGroup()
        				// length prompt
        				.addComponent(arg[1])
                 		// length entry field
                 		.addComponent(arg[2]))
        		// queue prompt and input
        		.addGroup(gl.createSequentialGroup()
                     	// queue prompt
                     	.addComponent(arg[3])
                     	// queue entry field
                     	.addComponent(arg[4]))
        		.addGroup(gl.createSequentialGroup()
        				// add to queue button
        				.addComponent(arg[5])
        				// quit button
        				.addComponent(arg[6]))
        );

        // set the buttons and labels in the vertical group
        gl.setVerticalGroup(gl.createSequentialGroup()
        		// connection message
        		.addComponent(arg[0])
        		// server message
        		.addComponent(arg[7])
        		// length prompt and input
        		.addGroup(gl.createParallelGroup()
	                 	// length prompt
	                 	.addComponent(arg[1])
	                 	// length entry field
	                 	.addComponent(arg[2]))
        		// queue prompt and input
        		.addGroup(gl.createParallelGroup()
                     	// queue prompt
                     	.addComponent(arg[3])
                     	// queue entry field
                     	.addComponent(arg[4]))
        		.addGroup(gl.createParallelGroup()
        				// add to queue button
        				.addComponent(arg[5])
        				// quit button
        				.addComponent(arg[6]))
        );
    }
    
    public void checkLayout(JComponent... arg) {
    	// sets JFrame layout for the window after client has connected to the server for upload
    	// removes username inputs and connect button from createLayout
    	// modified from code found at
    	// http://zetcode.com/javaswing/firstprograms/
    	// inputs: 
    	// connected message JLabel - arg[0]
    	// queue prompt JLabel - arg[1]
    	// queue input JTextField - arg[2]
    	// add to queue JButton - arg[3]
    	// quit JButton - arg[4]
    	// server message JButton - arg[5]
    	
    	// get the layer of the JFrame that holds the objects
        Container pane = getContentPane();
        // create the layout for the objects
        GroupLayout gl = new GroupLayout(pane);
        // set the created layout to the container layer
        pane.setLayout(gl);

        // set gaps to auto
        gl.setAutoCreateGaps(true);
        gl.setAutoCreateContainerGaps(true);

        // set the buttons and labels in the horizontal group
        gl.setHorizontalGroup(gl.createParallelGroup()
        		// connection message
        		.addComponent(arg[0])
        		// server message
        		.addComponent(arg[5])
        		// length prompt and input
        		.addGroup(gl.createSequentialGroup()
        				// length prompt
        				.addComponent(arg[1])
                 		// length entry field
                 		.addComponent(arg[2]))
        		// queue prompt and input
        		.addGroup(gl.createSequentialGroup()
                     	// queue prompt
                     	.addComponent(arg[3])
                     	// queue entry field
                     	.addComponent(arg[4]))
        );

        // set the buttons and labels in the vertical group
        gl.setVerticalGroup(gl.createSequentialGroup()
        		// connection message
        		.addComponent(arg[0])
        		// server message
        		.addComponent(arg[5])
        		// length prompt and input
        		.addGroup(gl.createParallelGroup()
	                 	// length prompt
	                 	.addComponent(arg[1])
	                 	// length entry field
	                 	.addComponent(arg[2]))
        		// queue prompt and input
        		.addGroup(gl.createParallelGroup()
                     	// queue prompt
                     	.addComponent(arg[3])
                     	// queue entry field
                     	.addComponent(arg[4]))
        );
    }
    
    //Getters for labels, buttons, timer, and username input field
	public JLabel getMessage() {
		return message;
	}

	public JButton getUploadButton() {
		return uploadButton;
	}
	
	public JButton getCheckButton() {
		return checkButton;
	}
	
	public JButton getC2Button() {
		return c2Button;
	}

	public JButton getQuitButton() {
		return quitButton;
	}

	public JLabel getConnected() {
		return connected;
	}

	public JButton getAddButton() {
		return addButton;
	}

	public JTextField getLength() {
		return length;
	}

	public JTextField getQueue() {
		return queue;
	}
	
}