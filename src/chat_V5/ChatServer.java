package chat_V5;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer implements Runnable {

	private ServerSocket server = null;
	private Thread thread = null;
	private ChatServerThread[] clients = new ChatServerThread[50];
	private int clientCount = 0;

	public ChatServer(int port) {// same as previous version
		try {
			System.out.println("Will start server on port " + port);
			server = new ServerSocket(port);// step 1 create a Server Socket remember to pass in the port
			start();
		} catch (IOException e) {
			System.err.println("My Server does not work " + e.getMessage());
		}
	}

	public void start() {// same as previous version
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
		}
	}

	@Override
	public void run() {// same as previous version
		while (thread != null) {
			try {
				System.out.println("Will wait for a client to connect");
				// add a thread and accept a client on it... ChatServerThread
				addThread(server.accept());

			} catch (IOException e) {
				System.err.println("YIKES!!! " + e.getMessage());
			}
		}
	}

	public synchronized void handle(int ID, String msg) {// introduced in v4
		if (msg.equalsIgnoreCase("bye")) {
			int left = ID;
			remove(ID);
			for (int i = 0; i < clientCount; i++) {
				clients[i].send("User: " + left + " has left");
			}
		}
		if (msg.contains("pm_to")) {
			String rID = msg.substring(msg.indexOf("[") + 1, msg.indexOf("]"));
			String text = msg.substring(msg.indexOf("]") + 1, msg.length());
			int usrID = Integer.parseInt(rID.trim());
			System.out.println("resulting msg =" + text);
			int clientIndex = findClient(usrID);
			clients[clientIndex].send("User " + ID + " sent privately: " + text);
			if (clients[findClient(ID)].getID() == ID) {
				clients[findClient(ID)].send("You sent a private message to: " + usrID + " " + text);
			}
		} else {

			for (int i = 0; i < clientCount; i++) {
				if (clients[i].getID() != ID) {
					System.out.println("will send to " + clients[i].getID() + " with msg= " + msg);
					clients[i].send("User: " + ID + " said: " + msg);
				} else {
					clients[i].send("You Said: " + msg);
				}
			}
		}
	}

	public synchronized void remove(int ID) {// v4
		int loc = findClient(ID);// 0
		if (loc >= 0 && loc < clientCount) {
			ChatServerThread tempToClose = clients[loc];// clients[loc].close();
			for (int i = loc + 1; (i <= clientCount && i < clients.length); i++) {
				clients[i - 1] = clients[i];// shift all from right to left
			}
			if (loc == clients.length - 1) {// special circumstance last index loc
				clients[loc] = null;
			}
			clientCount--;
			System.out.println("removed " + ID + " from index location " + loc);
			try {
				tempToClose.close();// end connections ... io streams and ChatServerThread
				System.out.println("closed streams on " + tempToClose.getId());
			} catch (IOException e) {
				System.out.println("Problem removing client " + e.getMessage());
			}
		}
	}

	private synchronized int findClient(int ID) {
		for (int i = 0; i < clientCount; i++) {
			if (clients[i].getID() == ID) {
				return i;// location of the client
			}
		}
		return -1; // client not in the array
	}

	public synchronized void addThread(Socket socket) {// similar but using the array in v4
		if (clientCount < clients.length) {
			clients[clientCount] = new ChatServerThread(this, socket);
			try {
				clients[clientCount].open();// open stream for ChatServerThread to handle input
				clients[clientCount].start();// start running ChatServerThread to handle the client
				clientCount++; // increment count
			} catch (IOException e) {
				System.err.println("Exception in addThread of ChatServer" + e.getMessage());
			}
		} else {
			System.out.println("Client refused max num of clients is " + clients.length);
		}
	}

	public static void main(String[] args) {
		// ChatServer myServer = new ChatServer(8080);
		if (args.length != 1) {
			System.out.println("You need a port number to run your server");
		} else {
			int port = Integer.parseInt(args[0]);
			ChatServer myServer = new ChatServer(port);
		}
	}

}
