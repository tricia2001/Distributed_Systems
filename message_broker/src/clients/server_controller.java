// Patricia Vines
// 1000536317

package clients;

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
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class server_controller {
	// client controller class manages the client GUI and server connection
	// Portions of code modified from these websites:
	// https://www.geeksforgeeks.org/socket-programming-in-java/
	// https://stackoverflow.com/questions/12908412/print-hello-world-every-x-seconds

	private Socket socket = null; // socket that connects to clients
	private ServerSocket server = null; // server socket that listens for clients
	private BufferedReader in = null; // input stream for client sockets
	private PrintWriter out = null; // output stream for client sockets
	private ArrayList<String> clients = new ArrayList<String>(); // list of connected clients
	private server_view view = null; // Server GUI class object
	private Semaphore mutex = new Semaphore(1); // semaphore for setting and retrieving client and time
	private boolean quit = false; // boolean used when quit button is clicked
	private message_broker broker = null; // message broker to handle queues and conversions

	public server_controller(int port, int x, int y) {
		// constructor for server controller
		// code modified from https://www.geeksforgeeks.org/socket-programming-in-java/
		// inputs:
		// port - port number of server process
		// x - int x position of window
		// y - int y position of window
		
		// start the message broker
		broker = new message_broker();
		// create server GUI
		this.view = new server_view(x, y);

		// Add the action to make the GUI visible to the event queue of the event
		// dispatching thread
		EventQueue.invokeLater(() -> {

			view.setVisible(true);
		});

		// listen for the quit button and exit the process from the initial GUI
		// modified from https://javatutorial.net/jframe-buttons-listeners-text-fields
		view.getQuitButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				quit = true; // set quit boolean to true if quit button pressed
				if (clients.isEmpty()) System.exit(0);
			}
		});

		try {
			// create the server socket using the given port number
			server = new ServerSocket(port);

			// server keeps listening for clients until the process ends
			while (true) {
				// set up the socket when the server is contacted by a client
				socket = server.accept();
				System.out.println("new socket accept");
				// set up input and output streams to read and write messages
				// to/from the client socket
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);

				// title variable is the title of the client
				String title = "";
				// connection type of the client
				String connection = "";

				try {
					// wait for the client to send the title and connection type
					while (!in.ready()) {
					}

					// store the client title in title
					title = in.readLine();
					// store the connection type in connection
					connection = in.readLine();
					
					// add the user to client list
					clients.add(title);

					// create a new thread to manage the connection
					// in server_connection method
					server_connection(title, connection);
					
					// send code 1 and message to client for upload a message
					if (connection.equals("upload")) {
						out.println("1");
						out.println("Client connected for "+connection);
					}
					// send code 2 and message to client for check messages
					else if (connection.equals("check")) {
						out.println("2");
						out.println("Client connected for "+connection);
					}
					// loop through clients list and create a string m with names of clients
					String m = "";
					if (clients.size() > 0)
						m = clients.get(0);
					for (int i = 1; i < clients.size(); i++) {
						m = m + ", " + clients.get(i);
					}
					// update the connected message with list of all connected users
					view.getConnected().setText(m + " connected to server");
					// set message to say that the latest user just connected
					view.getMessage().setText(title + " connected for "+connection);
					
				}
				// couldn't connect to client:
				catch (IOException i) {
					// remove client from list of clients if client is not connected
					if (clients.contains(title))
						clients.remove(title);
				}
			}

		}
		// couldn't connect server socket:
		catch (

		IOException i) {
		}
	}

	public void server_connection(String user, String connection) {
		// method creates a new thread to manage the connection with each client
		// code modified from https://www.geeksforgeeks.org/socket-programming-in-java/
		// inputs:
		// user - username of registered client

		// set user1 to user
		String user1 = user;
		Thread t = new Thread() {

			public void run() { // code that the connection thread should execute
				try {
					// set the socket to the socket just created for the client
					Socket socket1 = socket;

					// set the input and output to input/output just created for client
					BufferedReader in1 = in;
					PrintWriter out1 = out;

					// line variable stores what is read in from the client
					String line = "";
					// length is the length in meters from the user
					Double length = 0.0;
					// queue is the queue name specified by the user
					String queue = "";

					// keep going until the client quits (by sending "OVER" or the
					// quit button is pressed and quit boolean is set to true
					while (!quit) {
						// if there is input from the client, read it
						if (in1.ready()) {
							line = in1.readLine();
						
							// break if the client sends "OVER" while connected
							if (line.equals("OVER")) break;
							
							// if this is an upload connection...
							if (connection.equals("upload")) {
								// the first message from the client is the user supplied length
								length = Double.parseDouble(line);
								// the second message from the client is the queue name
								queue = in1.readLine();
								// set message on GUI
								view.getInputMessage().setText(user1+" uploading length "+length+" meters to queue "+queue);
								
								// display converted lengths in server GUI after
								// converting new length to appropriate units and adding to queue
								String message = broker.addToQueue(queue, length);
								view.getConversionMessage().setText(message);
								// send the client code 5 and a message
								out1.println("5");
								out1.println("Successfully added "+length+" meters to queue "+queue);
								break;
							}
							// if this is a check connection...
							else if (connection.equals("check")) {
								// the message from the client is a queue name
								queue = line;
								// send the client code 5 and a message with all messages in the queue
								// concatenated
								// set message on GUI
								view.getInputMessage().setText(user1+" checking for messages in queue "+queue);
								view.getConversionMessage().setText("");
								out1.println("5");
								String message = broker.getMessages(queue);
								out1.println(message);
								out1.println(broker.getNumMessages()); // return the number of messages to adjust client GUI size
								break;
							}
						}
					}

					// if the quit button was pressed, send code 5 and quit message to client
					// then exit process
					if (quit) {
						out1.println("5");
						out1.println("Server quit.");
						System.exit(0);
					} else { // client quit by sending "OVER" message or disconnected because operation is complete
						// remove client title from clients list
						if (clients.contains(user1))
							clients.remove(user1);
						// close connection
						socket1.close();

						// loop through clients list and create a string m with names of clients
						String m = "";
						if (clients.size() > 0)
							m = clients.get(0);
						for (int i = 1; i < clients.size(); i++) {
							m = m + ", " + clients.get(i);
						}
						// if there are no clients, left, set m to "No clients
						if (m.equals("")) {
							m = "No clients";
							view.getThread().setText("");
						}

						// update the connected message with list of all connected users
						view.getConnected().setText(m + " connected to server");
						// set message to say that the user just disconnected
						view.getMessage().setText(user1 + " disconnected");
					}
				}
				// client socket connection error:
				catch (IOException i) {
					// remove client username from clients list
					if (clients.contains(user1))
						clients.remove(user1);

					// loop through clients list and create a string m with names of clients
					String m = "";
					if (clients.size() > 0)
						m = clients.get(0);
					for (int j = 1; j < clients.size(); j++) {
						m = m + ", " + clients.get(j);
					}
					// if there are no clients, left, set m to "No clients
					if (m.equals("")) {
						m = "No clients";
						view.getThread().setText("");
					}

					// update the connected message with list of all connected users
					view.getConnected().setText(m + " connected to server");
					// set message to say that the user just disconnected
					view.getMessage().setText(user1 + " disconnected");
				}
			}
		};
		t.start();

	}


	public static void main(String args[]) { // Starts server with arguments port: 16993
												// and x and y positions of window
		server_controller server = new server_controller(16993, 700, 200);
	}
}