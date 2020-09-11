// Patricia Vines
// 1000536317

package clients;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;


public class message_broker {
	// message broker class manages the queues and performs conversions
	
	private ArrayList<String> queueA = new ArrayList<String>(); // queue A
	private ArrayList<String> queueB = new ArrayList<String>(); // queue B
	private ArrayList<String> queueC = new ArrayList<String>(); // queue C
	private ArrayList<ArrayList<String>> queues = new ArrayList<ArrayList<String>>(
			Arrays.asList(queueA,queueB,queueC)); // arrayList of queues A, B, and C
	private double[] conversionList = new double[15];  // converions factors for each unit
	private ArrayList<String> units = new ArrayList<String>( // names of units
            Arrays.asList("Meters", "Millimeters", "Centimeters", 
			       "Kilometers", "Astronomical Units", "Parsecs", "Light Years", 
			       "Inches", "Feet", "Yards", "Miles", "Nautical Miles", 
			       "American football fields", "Hands", "Horses"));
	private int numMessages = 0;
	
	private PrintWriter A,B,C; // PrintWriters for each queue text file
		
	public message_broker() {
		// constructor for message broker
		// no inputs or outputs
		
		// flag to indicate if the file was opened
		boolean fileFound = false;
		
		// create a file for the conversions text file
		File conversionsFile = new File("conversions.txt");
		// scanner to read conversions text file
		Scanner inFile;
		try {
			// connect scanner to conversions text file and set flag to indicate file was openend
			inFile = new Scanner(conversionsFile);
			fileFound = true;
		} 
		catch (FileNotFoundException fnfe) {
			// print error message to console if conversions file cannot be opened and set
			// scanner to read from the keyboard
			System.out.println("Could not open conversion.txt");
			inFile = new Scanner(System.in);
		}
		if (fileFound) {
			// if the file was opened, read all the lines of the file
			while(inFile.hasNextLine()) {
				String unit = "";
				// read all words until the numbers start and trim spaces
				inFile.useDelimiter("[0-9]");
				unit = inFile.next().trim();
				// read the numbers to multiply meters by for conversion
				String multiplier = inFile.nextLine();
				// save converion multipliers in the same index as the units for easy reference
				conversionList[units.indexOf(unit)] = Double.parseDouble(multiplier);
			}
			inFile.close();
		}
		// read the saved queue files into memory
		readQueueFile();
	}
	
	public String getMessages(String queue) {
		// getMessages gets the messages from a queue
		// input: queue name (A, B, or C)
		// output: messages in queue
		
		// get the appropriate index for the queue
		int index = getIndex(queue);
		// set the number of message to help with client GUI size
		numMessages = queues.get(index).size();
		// set the beginning of the message to <html> so it will print correctly on the GUI
		String message = "<html>";
		// if the queue isn't empty...
		if (queues.get(index).size() > 0) {
			// go through the queue arrayList
			for (int i = 0; i < queues.get(index).size(); i++) {
				// add each message to the message variable
				message += queues.get(index).get(i);
				// if this is not the last message, add a blank line after the message
				if (i != queues.get(index).size()-1) message += "<br><br>";
				// if this is the last message, end the </html> tag
				else message += "</html>";
			}
			// delete messages from program queue and from file
			deleteMessages(index);
		}
		else // if the queue is empty
			message = "No messages in queue "+queue;
			
		return message;
	}
	
	public String addToQueue(String queue, double meterLength) {
		// addToQueue adds a message to a queue
		// inputs: 
		// queue - queue name (A, B, or C)
		// meterLength - double length in meters from user
		// output: integer to indicate success or failure
				
		if (queue.equals("A")) {
			// convert meters to the appropriate units for queue A
			String message = convertA(meterLength);
			// add converted message to queue in program memory
			queues.get(0).add(message);
			try {
				// set up PrintWriter to append to the text file that stores queue A
				A = new PrintWriter(new FileOutputStream("AQueue.txt",true));
			}
			catch(FileNotFoundException fnfe) {
				// print error message to console if file can't be opened
				System.out.println("couldn't open file to write");
			}
			// print the message to the file
			A.println(message);
			A.close();
			// return message to display on server GUI
			return "<html>"+message+"</html>";
		}
		else if (queue.equals("B")) {
			// convert meters to the appropriate units for queue B
			String message = convertB(meterLength);
			// add converted message to queue in program memory
			queues.get(1).add(message);
			try {
				// set up PrintWriter to append to the text file that stores queue B
				B = new PrintWriter(new FileOutputStream("BQueue.txt",true));
			}
			catch(FileNotFoundException fnfe) {
				// print error message to console if file can't be opened
				System.out.println("couldn't open file to write");
			}
			// print the message to the file
			B.println(message);
			B.close();
			// return message to display on server GUI
			return "<html>"+message+"</html>";
		}
		else if (queue.equals("C")) {
			// convert meters to the appropriate units for queue C
			String message = convertC(meterLength);
			// add converted message to queue in program memory
			queues.get(2).add(message);
			try {
				// set up PrintWriter to append to the text file that stores queue C
				C = new PrintWriter(new FileOutputStream("CQueue.txt",true));
			}
			catch(FileNotFoundException fnfe) {
				// print error message to console if file can't be opened
				System.out.println("couldn't open file to write");
			}
			// print the message to the file
			C.println(message);
			C.close();
			// return message to display on server GUI
			return "<html>"+message+"</html>";
		}
		return "";
		
	}
	
	private String convertA(double meters) {
		// converts from meters to meters, millimeters, centimeters,
		// kilometers, and astronomical units
		// input: meters - user input
		// output: message - 5 lines of converted units
		
		// start out message with the user input for reference
		String message = meters+" Meters:<br>";
		for (int i = 0; i < 5; i++) {
			// go through the first 5 units and multiply by user's length
			message += meters*conversionList[i]+" "+units.get(i);
			// break to the next line unless it is the last line
			if (i!=4) message +="<br>";
		}
		
		return message;
	}
	
	private String convertB(double meters) {
		// converts from meters to parsecs, light years, inches,
		// feet, and yards
		// input: meters - user input
		// output: message - 5 lines of converted units
		
		// start out message with the user input for reference
		String message = meters+" Meters:<br>";
		for (int i = 5; i < 10; i++) {
			// go through  units 5-9 and multiply by user's length
			message += meters*conversionList[i]+" "+units.get(i);
			// break to the next line unless it is the last line
			if (i!=9) message +="<br>";
		}
		
		return message;
	}
	
	private String convertC(double meters) {
		// converts from meters to miles, nautical miles, american football fields,
		// hands, and horses
		// input: meters - user input
		// output: message - 5 lines of converted units
				
		// start out message with the user input for reference
		String message = meters+" Meters:<br>";
		for (int i = 10; i < 15; i++) {
			// go through  units 10-14 and multiply by user's length
			message += meters*conversionList[i]+" "+units.get(i);
			// break to the next line unless it is the last line
			if (i!=14) message +="<br>";
		}
		
		return message;
	}
	
	private void readQueueFile() {
		// readQueueFile reads all the queues into program memory
		// in case of a server crash or disconnect
		// no inputs or outputs
		
		// create files for the queue text files
		File AFile = new File("AQueue.txt");
		File BFile = new File("BQueue.txt");
		File CFile = new File("CQueue.txt");
		// create scanners for queue text files
		Scanner Aqueue, Bqueue, Cqueue;
		// create boolean to indicate if the files were successfully opened
		boolean filesFound = false;
		try {
			// connect the scanners to the files and indicate the files were opened
			Aqueue = new Scanner(AFile);
			Bqueue = new Scanner(BFile);
			Cqueue = new Scanner(CFile);
			filesFound = true;
		}
		catch(FileNotFoundException fnfe) {
			// connect scanners to the keyboard
			Aqueue = new Scanner(System.in);
			Bqueue = new Scanner(System.in);
			Cqueue = new Scanner(System.in);
		}
		// if the files were successfully opened...
		if (filesFound) {
			// create a blank message
			String message = "";
			// go through file for A queue and read each line into
			// the queue 
			while (Aqueue.hasNextLine()) {
				message = Aqueue.nextLine();
				queues.get(0).add(message);
			}
			// go through file for B queue and read each line into
			// the queue 
			while (Bqueue.hasNextLine()) {
				message = Bqueue.nextLine();
				queues.get(1).add(message);
			}
			// go through file for C queue and read each line into
			// the queue 
			while (Cqueue.hasNextLine()) {
				message = Cqueue.nextLine();
				queues.get(2).add(message);
			}
			// close scanners
			Aqueue.close();
			Bqueue.close();
			Cqueue.close();
		}
	}
	
	private void deleteMessages(int index) {
		// deletMessages deletes messages from the queue in program
		// memory and from the queue text file
		// input: index of the queue (A=0, B=1, C=2)
		// no outputs
		
		// empty the queue in program memory
		queues.get(index).removeAll(queues.get(index));
		switch(index) {
		case 0: 
			PrintWriter AP;
			try {
				// connect a PrintWriter to the A Queue file to overwrite
				AP = new PrintWriter(new FileOutputStream("AQueue.txt",false));
				// print an empty string and close
				AP.print("");
				AP.close();
			}
			catch(FileNotFoundException fnfe) {
				// if PrintWriter didn't connect, print an error to the console
				System.out.println("AQueue.txt not found for deleting.");
			}
			
			break;
		case 1:
			PrintWriter BP;
			try {
				// connect a PrintWriter to the B Queue file to overwrite
				BP = new PrintWriter(new FileOutputStream("BQueue.txt",false));
				// print an empty string and close
				BP.print("");
				BP.close();
			}
			catch(FileNotFoundException fnfe) {
				// if PrintWriter didn't connect, print an error to the console
				System.out.println("BQueue.txt not found for deleting.");
			}
			break;
		case 2: 
			PrintWriter CP;
			try {
				// connect a PrintWriter to the C Queue file to overwrite
				CP = new PrintWriter(new FileOutputStream("CQueue.txt",false));
				// print an empty string and close
				CP.print("");
				CP.close();
			}
			catch(FileNotFoundException fnfe) {
				// if PrintWriter didn't connect, print an error to the console
				System.out.println("CQueue.txt not found for deleting.");
			}
			break;
		}
	}
	
	private int getIndex(String queue) {
		// getIndex returns index for given queue
		// input: queue - queue name
		// output: integer corresponding to queue name or -1 if invalid queue name
		
		if (queue.equals("A"))
			return 0;
		else if (queue.equals("B"))
			return 1;
		else if (queue.equals("C"))
			return 2;
		else
			return -1;
	}

	public int getNumMessages() {
		return numMessages;
	}
}
