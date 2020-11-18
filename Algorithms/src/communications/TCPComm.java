package communications;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;

import entities.Map;
import entities.Robot;
import main.Main;

import javax.swing.*;

public class TCPComm {
	public static final String SERVER_IP = "192.168.31.31";
	public static final int PORT = 8000;//4042
	public static final char BLUETOOTH = 'b', PC = 'p', RASP_PI = 'r', ARDUINO = 'a';
	private static Socket toServer = null;
	private BufferedWriter streamToServer;
	private BufferedReader streamFromServer;
	
	private Socket clientSocket;
	private BufferedWriter outgoingStream;
	private BufferedReader incomingStream;
//	private InputStream din;

	/**
	 * Constructor for TCP Communication.
	 */
	public TCPComm() {
		try {
			System.out.println("Initiating TCP connection with IP: " + SERVER_IP + ":" + PORT + "... ");
			clientSocket = new Socket(SERVER_IP, PORT);
			System.out.println("TCP connection successfully established.");
			
			outgoingStream = new BufferedWriter(new OutputStreamWriter(new BufferedOutputStream(clientSocket.getOutputStream())));
//			din = clientSocket.getInputStream();
			incomingStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		} catch (IOException e) {
			System.err.format("TCP Connection IOException: %s%n", e);
			Main.gui.setModeColour(false);
		}
	}

	/**
	 * Send a message/command to the specified recipient.
	 * 
	 * @param recipient
	 * @param message
	 */
	public void send(char recipient, String message) {
		System.out.println("Sending a message!");
		try {
			if (this.clientSocket != null) {
				String outgoingString = new String();
				if (recipient == TCPComm.RASP_PI){
					outgoingString = "RP,PC," + message;
				}
				else if (recipient == TCPComm.BLUETOOTH){
					outgoingString = "AN,PC," + message;
				}
				else if (recipient == TCPComm.ARDUINO){
					outgoingString = "AR,PC," + message;
				}
				this.outgoingStream.write(outgoingString);
				this.outgoingStream.flush();
				System.out.println("TCP Sent: " + outgoingString);
			}
		} catch (IOException e) {
			System.err.format("TCP Connection IOException: %s%n", e);
			Main.gui.setModeColour(false);
		}
	}

	/**
	 * Read incoming stream.
	 * 
	 * @return message
	 */
	public String read() {
		try {
			StringBuilder sb = new StringBuilder();
			String toReturn = incomingStream.readLine();
			while (toReturn == null || toReturn.isEmpty()) {
				toReturn = incomingStream.readLine();
			}
			sb.append(toReturn);
			System.out.println("Receiving message :" + sb.toString());
			return sb.toString();
		} catch (Exception e) {
			System.out.println("DEBUG :: Receiving Message failed -> IOException");
			System.out.println(e.toString());
		}
		
		return null;
	}
//		try {
//			toReturn = this.incomingStream.readLine();
//			System.out.println("TCP Received: " + toReturn);
//		} catch (IOException e) {
//			System.err.format("TCP Connection IOException: %s%n", e);
//			Main.gui.setModeColour(false);
//		}
//
//		return toReturn;

	/**
	 * STRICTLY read incoming stream to find a message from the specified device. Any messages from a
	 * non-specified device will be discarded.
	 * <p>
	 * This function is implemented in a very strict (or bad) way as the while(true) loop is blocking
	 * and it discards everything else. Might need to be re-implemented.
	 * </p>
	 * 
	 * @param device
	 * @return
	 */
	public String readFrom(char device) {
		while (true) {
			System.out.println("TCP: Waiting for message from " + device + "...");
			String toReturn = read();

		//	if (toReturn != null && toReturn.charAt(0) == device)
		//		return toReturn.substring(1);
//			System.out.println(toReturn);
			return toReturn;
		}
	}

	/**
	 * Get connection status of <tt>TCPComm</tt>.
	 * 
	 * @return
	 */
	public boolean isConnected() {
		if (this.clientSocket == null)
			return false;

		return this.clientSocket.isConnected();
	}

	/**
	 * Closes the socket.
	 * 
	 * @throws IOException
	 */
	public void close() {
		if (this.clientSocket != null) {
			try {
				this.clientSocket.close();
			} catch (IOException e) {
			}
		}
	}

	/**
	 * Send movement instructions to Serial and Bluetooth.
	 * 
	 * @param navigateSteps
	 */
	
	public void sendFastestPath(LinkedList<String> navigateSteps) {
		if (navigateSteps.isEmpty()) {
			System.err.println("Unable to send Fastest Path as it does not exist. Call runAStar() again.");
		}
		
		String pointer = navigateSteps.poll();
		String forArduino = "";
		while (pointer != null){
			int numOfForwards = 0;
			while (pointer == "F"){
				numOfForwards++;
				pointer = navigateSteps.poll();
			}
			if (numOfForwards > 9){
				while (numOfForwards>0 && numOfForwards/9 > 0){
					forArduino += String.format("%d",9);
					numOfForwards = numOfForwards - 9;
				}
				forArduino += String.format("%d",numOfForwards);
			}
			else{
				forArduino += String.format("%d",numOfForwards);
			}
			if (pointer != null){
				forArduino += pointer;
			}
			pointer = navigateSteps.poll();
		}
		forArduino = forArduino.toLowerCase();
		Main.comms.send(TCPComm.ARDUINO, forArduino);
	}
	
	
	
	public void sendFastestPathOnebyOne(LinkedList<String> navigateSteps) {
		if (navigateSteps.isEmpty()) {
			System.err.println("Unable to send Fastest Path as it does not exist. Call runAStar() again.");
		}
		
		String pointer = navigateSteps.poll();
		String forArduino = "";
		String forAndroid = "";
		while (pointer != null){
			int numOfForwards = 0;
			try{
				while (pointer == "F"){
					numOfForwards++;
					pointer = navigateSteps.poll();
				}
				if (numOfForwards > 9){
					for (int i = 0; i < numOfForwards; i++){
						forAndroid += "F";
					}
					Main.comms.send(TCPComm.BLUETOOTH, forAndroid);
					forAndroid = "";
					Thread.sleep(1000);
					while (numOfForwards > 0){
						if (numOfForwards > 9) {
							Main.comms.send(TCPComm.ARDUINO, String.valueOf(9));
							Thread.sleep(1000);
							Main.comms.send(TCPComm.ARDUINO, String.valueOf(numOfForwards - 9));
							break;
						}
//						else
//							Main.comms.send(TCPComm.ARDUINO, String.valueOf(numOfForwards));
//						for (int i = 0; i < numOfForwards; i++){
//							forAndroid += "F";
//						}
					}
				}
				else if (numOfForwards > 0){
					Main.comms.send(TCPComm.ARDUINO, String.valueOf(numOfForwards));
					Thread.sleep(1000);
					for (int i = 0; i < numOfForwards; i++){
						forAndroid+="F";
					}
					Main.comms.send(TCPComm.BLUETOOTH, forAndroid);
					Thread.sleep(1000);
					
					forAndroid = "";
				}
				
				if (pointer != null){
					Thread.sleep(100);
					Main.comms.send(TCPComm.ARDUINO, pointer);
					Thread.sleep(100);
					Main.comms.send(TCPComm.BLUETOOTH, pointer);
					Thread.sleep(1000);
					
				}
			}catch (Exception e){
				break;
			}
			pointer = navigateSteps.poll();
		}
	}
	/**
	 * Generate MDF String for Bluetooth (Android tablet).
	 * 
	 * @param map
	 * @param robot
	 * @return
	 */
	public static String genMDFBluetooth(Map map, Robot robot) {
		String toReturn = new String();

		toReturn = map.getP1Descriptors() + "|" + map.getP2Descriptors() + "|" + robot.getCurrDir() + "|"
				+ (19 - robot.getCurrPos().getY()) + "|" + robot.getCurrPos().getX() + "|" + "0";

		return toReturn;
	}




//	private void startListening() {
//		// Only 1 instance should be running. Cancel previous executor if it exists.
//		if (tcpListenerExecutor != null)
//			tcpListenerExecutor.shutdown();
//
//		// Assign a new thread pool
//		tcpListenerExecutor = Executors.newScheduledThreadPool(1);
//
//		// Create a Runnable task
//		Runnable tcpListener = new Runnable() {
//			@Override
//			public void run() {
//				while (true) {
//					String msg = read();
//					if (msg[0] == 's') {
//						
//					}
//				}
//			}
//		};
//
//		tcpListenerExecutor.execute(tcpListener);
//	}
}
