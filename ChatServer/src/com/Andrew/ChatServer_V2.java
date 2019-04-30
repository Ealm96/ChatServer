package com.Andrew;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer_V2 {

	private Socket socket = null;
	private ServerSocket server = null;
	private DataInputStream strIn = null;

	public ChatServer_V2(int port) {
		try {
			System.out.println("Will start server on port " + port);
			server = new ServerSocket(port);// step 1 create a Server Socket remember to pass in the port.
			System.out.println("will wait for client to connect");
			socket = server.accept();// step 2 wait for a client to connect and accept.
			System.out.println("Accepted a client");
			open();
			boolean done = false;
			do {
				String line = strIn.readUTF();
				System.out.println("user: " + socket.getPort() + " said: " + line);// step 4 communicate
				if (line.equalsIgnoreCase("bye")) {
					done = true;
				}

			} while (!done);
			close();// step 5 close streams and sockets
			System.out.println("Connection closed");
		} catch (IOException e) {

			System.err.println("My Server doesn't work " + e.getMessage());

		}
	}

	protected void open() throws IOException {
		strIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));// open streams.
	}

	protected void close() throws IOException {
		if (strIn != null) {
			strIn.close();
		}
		if (socket != null) {
			socket.close();
		}
	}

	public static void main(String[] args) {
		// ChatServer myServer = new ChatServer(8080);
		ChatServer_V2 myServer = null;
		if (args.length != 1) {
			System.out.println("yo need a port number to run your server");
		} else {
			int port = Integer.parseInt(args[0]);
			myServer = new ChatServer_V2(port);
		}
	}

}
