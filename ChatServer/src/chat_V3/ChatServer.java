package chat_V3;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer implements Runnable {

	// private Socket socket = null;
	private ServerSocket server = null;
	// private DataInputStream strIn = null;
	private Thread thread = null;
	ChatServerThread[] clients = new ChatServerThread[50];
	int clientCount = 0;

	public ChatServer(int port) {
		try {
			System.out.println("Will start server on port " + port);
			server = new ServerSocket(port);// step 1 create a Server Socket remember to pass in the port
			start();
		} catch (IOException e) {
			System.err.println("My Server does not work " + e.getMessage());
		}
	}

	public void start() {
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
		}
	}

	@Override
	public void run() {
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

	public void remove(int ID) {
		int loc = findClient(ID);
		if (loc >= 0 && loc < clientCount) {

			ChatServerThread tempToClose = clients[loc];
			for (int i = loc + 1; (i <= clientCount && i < clients.length - 1); i++) {
				clients[i - 1] = clients[i];
			}
			if (loc == clients.length - 1) {
				clients[loc] = null;
			}

			clientCount--;
			System.out.println("removed " + ID + " from index location " + loc);
			try {
				tempToClose.close();
			} catch (IOException e) {
				System.out.println("Problem removing client from the server " + e.getMessage());
			}
		}
	}

	private int findClient(int ID) {
		for (int i = 0; i < clients.length; i++) {
			if (clients[i].getId() == ID) {
				return i;
			}
		}

		return -1;
	}

	public void addThread(Socket socket) {
		if (clientCount < clients.length) {
			clients[clientCount] = new ChatServerThread(this, socket);
			try {
				clients[clientCount].open();// open stream for ChatServerThread to handle input
				clients[clientCount].start();// start running ChatServerThread to handle the client
				clientCount++;
			} catch (IOException e) {
				System.err.println("Exception in addThread of ChatServer" + e.getMessage());
			}
		} else {
			System.out.println("Something went wrong");
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
