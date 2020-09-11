// Patricia Vines
// 1000536317

package clients;

import java.awt.Container;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicBoolean;

public class client_controller {
	// client controller class manages the client GUI and server connection
	// Portions of code modified from these websites:
	// https://javatutorial.net/jframe-buttons-listeners-text-fields
	// https://www.geeksforgeeks.org/socket-programming-in-java/
	// https://www.tutorialspoint.com/compute-elapsed-time-in-seconds-in-java#:~:text=To%20compute%20the%20elapsed%20time,The%20java.
	// https://www.tutorialspoint.com/java/java_thread_communication.htm

	private String address; // IP address of the server (127.0.0.1 - local host)
	private int port; // port of the server (16993)
	public client_view view = null; // Client GUI class object
	private String svr_message = ""; // Message from the Server - connection or thread time
	private int svr_num = 0; // Number sent from server used in a switch to determine action
	public volatile boolean quit = false; // boolean used when quit button is clicked
	private int x = 0; // x position of client window
	private int y = 0; // y position of client window
	private Thread t = null; // thread used to manage connection to server
	private boolean flag = true; // flag for thread communication in synchronized method
	String title = ""; // title for client GUI (client #)
	private final AtomicBoolean running = new AtomicBoolean(false); // thread running boolean
	private String length = ""; // length provided by user in meters
	private String queue = ""; // queue name given by user
	private boolean uploadMessage = false; // flag when there is a message to upload
	private boolean checkMessage = false; // flag when a message needs to be checked
	private String connection = ""; // connection type (upload or check)
	private int num_messages = 0; // number of messages from the server

	public client_controller(String address, int port, String title, int x, int y) {
		// constructor method for the client controller
		// inputs:
		// address - IP address of server to connect to
		// port - port number of server process
		// title - String used to identify the type of window needed
		// x - int x position of window
		// y - int y position of window

		// set class variables to inputs
		this.x = x;
		this.y = y;
		this.view = new client_view(title, x, y,0); // create initial GUI
		this.address = address;
		this.port = port;
		this.title = title;

		// Add the action to make the GUI visible to the event queue of the event
		// dispatching thread
		EventQueue.invokeLater(() -> {

			view.setVisible(true);
		});

		// listen for the upload button and send title to thread_connection method
		// modified from https://javatutorial.net/jframe-buttons-listeners-text-fields
		view.getUploadButton().addActionListener(new ActionListener() {
			@Override
			// Perform this action when the connect button is pressed:
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("upload button pressed for "+title);
				// set connection type
				connection = "upload";
				// call thread_connection method to upload a length to a queue
				thread_connection();
			}
		});
		
		// listen for the check button and send title to thread_connection method
		// modified from https://javatutorial.net/jframe-buttons-listeners-text-fields
		view.getCheckButton().addActionListener(new ActionListener() {
			@Override
			// Perform this action when the connect button is pressed:
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("check button pressed for "+title);
				// set connection type
				connection = "check";
				// call thread_connection method to check a queue
				thread_connection();
			}
		});

		// listen for the quit button and exit the process from the initial GUI
		// modified from https://javatutorial.net/jframe-buttons-listeners-text-fields
		view.getQuitButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});

		// main thread goes into the waiting synchronized loop to await messages from
		// the connection thread
		Waiting();

	}

	public void thread_connection() {
		// method creates a new thread to manage the server connection
		// code modified from https://www.geeksforgeeks.org/socket-programming-in-java/

		t = new Thread() {

			public void run() { // code that the connection thread should execute
				try {
					// create a socket and connect to the address and port of the server
					Socket socket = new Socket(address, port);
					System.out.println("socket created");
					System.out.println("title = "+title);

					// set up input and output streams to read and write messages
					// to/from the socket
					BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

					// send title to the server
					out.println(title);
					
					// send connection type to the server
					out.println(connection);
					
					// keep waiting for quit boolean and socket messages
					while (true) {
						try {
							// while there are no incoming socket messages, check
							// the quit boolean
							while (!input.ready()) {
								if (quit) { // means quit button was pressed
									// send message to server indicating client is disconnecting
									out.println("OVER");
									// end client process
									System.exit(0);
								}
								else if (uploadMessage) { // means the user has entered a length and queue
									// send message to server with length in meters and queue
									out.println(length);
									out.println(queue);
									uploadMessage = false;
								}
								else if (checkMessage) { // means the user has entered a queue
									// send message to server with length in meters and queue
									out.println(queue);
								}
							}
							// assign the first incoming socket message to svr_num
							// this is a number identifying which action to take
							svr_num = Integer.parseInt(input.readLine());
							
							// assign the second incoming socket message to svr_message
							// this is a String message or the number of seconds the
							// thread should sleep
							svr_message = input.readLine();
							if (checkMessage) {
								num_messages = Integer.parseInt(input.readLine());
								checkMessage = false;
							}

							// notify the main thread that there is a new message from the server
							Notify();
							
							if (svr_num==5) {
								socket.close(); // close the socket if the server disconnected
								break; // break from the loop, ending the thread
							}

							
						}
						// this catch handles a server disconnection
						catch (IOException i) {
							// set svr_num and svr_message variables to let the GUI thread know what
							// happened
							svr_num = 5;
							svr_message = "Server disconnected.";

							// notify the GUI thread that messages are waiting
							Notify();

							socket.close(); // close the socket
							break; // break from the loop, ending the thread
							// disconnect code here because the server quit
						}
					} // end of while loop
				}
				// couldn't connect to server:
				catch (UnknownHostException u) {
					System.out.println(u);
				}
				// couldn't connect input/output streams:
				catch (IOException i) {
					System.out.println(i);
				}

				System.out.println("connection thread ending");
			}
		}; // end of thread code
		t.start(); // starts the new thread
	}

	public synchronized void Waiting() {
		// the main thread controlling the GUI waits here for messages from the
		// thread managing the server connection
		// code modified from
		// https://www.tutorialspoint.com/java/java_thread_communication.htm
		// no inputs
		// communication occurs through global variables:
		// flag - true if there is a new message
		// svr_num - number from server or connection thread used in switch
		// svr_message - message from server or connection thread

		// run until the quit button is pressed
		while (true) {
			if (flag) { // if there is no message from the connection thread wait
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			switch (svr_num) {
			// initial value - default case
			case 0:
				break;

			// case 1 is client connected for uploading message
			case 1:
				// turn off the current view which is the initial GUI
				EventQueue.invokeLater(() -> {

					view.setVisible(false);
				});

				// create a new GUI showing the server connection
				view = new client_view(title + " is now connected to the server for upload.", x, y,0);

				view.getMessage().setText(svr_message); // sets message on GUI
				
				// show the new GUI
				EventQueue.invokeLater(() -> {

					view.setVisible(true);
				});

				// listen for the add to queue button to be pressed
				view.getAddButton().addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						length = view.getLength().getText(); // get length from text field
						queue = view.getQueue().getText().toUpperCase(); // get queue from text field
						try {
							// make sure length given in meters is a number
							Double.parseDouble(length);
							// Make sure queue input is a valid letter
							if (!(queue.equals("A") || queue.equals("B") || queue.equals("C"))) {
								view.getMessage().setText("Queue input must be A, B, or C."); // sets message on GUI
							}	
							else { // if length and queue are valid, upload message
								num_messages = 0;
								uploadMessage = true;
							}
						}
						catch(NumberFormatException nfe) { // length is not a number
							view.getMessage().setText("Length input must be a number."); // sets message on GUI
						}
					}
				});
				
				// listen for the quit button to be pressed
				view.getQuitButton().addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						quit = true; // set quit = true if quit button pressed
					}
				});
				break;

			// case 2 is client connected for checking messages
			case 2:

				// turn off the current view which is the initial GUI
				EventQueue.invokeLater(() -> {

					view.setVisible(false);
				});

				// create a new GUI showing the server connection
				view = new client_view(title + " is now connected to the server for checking messages.", x, y,0);
				
				view.getMessage().setText(svr_message); // sets message on GUI

				// show the new GUI
				EventQueue.invokeLater(() -> {

					view.setVisible(true);
				});

				// listen for the add to queue button to be pressed
				view.getC2Button().addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						queue = view.getQueue().getText().toUpperCase(); // get queue from text field
						// Make sure queue input is a valid letter
						if (!(queue.equals("A") || queue.equals("B") || queue.equals("C")))
							view.getMessage().setText("Queue input must be A, B, or C."); // sets message on GUI
						else {// if queue is valid, check for messages 
							checkMessage = true;
							System.out.println("checkMessage = "+checkMessage);
						}
					}
				});
				
				// listen for the quit button to be pressed
				view.getQuitButton().addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						quit = true; // set quit = true if quit button pressed
					}
				});
				break;

			// case 5 is message from connection thread - server disconnected
			case 5: // turn off the current view
				EventQueue.invokeLater(() -> {

					view.setVisible(false);
				});

				// create new view like initial view where client can choose upload or check messages
				view = new client_view(title, x, y, num_messages);

				// set message to display on GUI
				view.getMessage().setText(svr_message);
				
				// show the new GUI
				EventQueue.invokeLater(() -> {

					view.setVisible(true);
				});

				// listen for the upload button and send title to thread_connection method
				// modified from https://javatutorial.net/jframe-buttons-listeners-text-fields
				view.getUploadButton().addActionListener(new ActionListener() {
					@Override
					// Perform this action when the connect button is pressed:
					public void actionPerformed(ActionEvent arg0) {
						System.out.println("upload button pressed for "+title);
						// set connection type
						connection = "upload";
						// call thread_connection method to upload a length to a queue
						thread_connection();
					}
				});
				
				// listen for the check button and send title to thread_connection method
				// modified from https://javatutorial.net/jframe-buttons-listeners-text-fields
				view.getCheckButton().addActionListener(new ActionListener() {
					@Override
					// Perform this action when the connect button is pressed:
					public void actionPerformed(ActionEvent arg0) {
						System.out.println("check button pressed for "+title);
						// set connection type
						connection = "check";
						// call thread_connection method to check a queue
						thread_connection();
					}
				});

				// listen for the quit button and exit the process from the initial GUI
				// modified from https://javatutorial.net/jframe-buttons-listeners-text-fields
				view.getQuitButton().addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						System.exit(0);
					}
				});
			}
			
			flag = true; // this means there is no message from the connection thread
		}
	}

	public synchronized void Notify() {
		// the thread managing the server connection notifies the main thread
		// controlling the GUI
		// that there is a message from the connection thread or the server
		// code modified from
		// https://www.tutorialspoint.com/java/java_thread_communication.htm
		// no inputs

		flag = false; // there is a message from the connection thread
		notify(); // notifies the main thread
	}
	

}
