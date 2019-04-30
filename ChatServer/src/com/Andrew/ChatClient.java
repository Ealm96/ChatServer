package com.Andrew;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ChatClient {

	private Socket socket = null;
	private BufferedReader console = null;
	private DataOutputStream strOut = null;

	public ChatClient(String serverName, int serverPort) {

		try {
			socket = new Socket(serverName, serverPort);// step 1 connect to server using socket
			// step 2 open stream
			start();
			// step 3 communicate
			communicate();
			// step 4 close stream and sockets
			stop();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void start() throws IOException {
		console = new BufferedReader(new InputStreamReader(System.in));
		strOut = new DataOutputStream(socket.getOutputStream());
	}

	public void communicate() throws IOException {
		String line = "";
		do {
			line = console.readLine();
			strOut.writeUTF(line);
			strOut.flush();
		} while (!line.equalsIgnoreCase("bye"));
	}

	public void stop() throws IOException {
		if (console != null) {
			console.close();
		}
		if (strOut != null) {
			strOut.close();
		}
		if (socket != null) {
			socket.close();
		}
	}

	public static void main(String[] args) {

		ChatClient client = null;
		if (args.length < 1) {
			System.out.println("yo need a port number to run your server");
		} else {
			String serverName = args[0];
			int port = Integer.parseInt(args[1]);
			client = new ChatClient(serverName, port);
		}

	}

}
