package chat_V4;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ChatClient implements Runnable {
	private Socket socket = null;
	private BufferedReader console = null;
	private DataOutputStream strOut = null;
	private ChatClientThread client = null;
	private Thread thread = null;
	private String line = "";
	private static final String ENC_MARKER = "enc_xyz";

	public ChatClient(String serverName, int serverPort) {
		try {
			socket = new Socket(serverName, serverPort);// step 1 connect to server using Socket
			start();// step 2 open streams
			communicate();// step 3 communicate

			// stop();//step 4 close streams and sockets
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void start() throws IOException {// step 2 open streams
		console = new BufferedReader(new InputStreamReader(System.in));
		strOut = new DataOutputStream(socket.getOutputStream());
		if (thread == null) {
			client = new ChatClientThread(this, socket);
			thread = new Thread(this);
			thread.start();
		}
	}

	public void run() {
		while ((thread != null)) {
			try {
				communicate();
			} catch (IOException e) {
				System.out.println("Chat Client IO problem " + "running thread to" + " read line and send it");
			}
		}
	}

	public void communicate() throws IOException {
		do {
			line = console.readLine();
			strOut.writeUTF(line);
			strOut.flush();
		} while (!line.equalsIgnoreCase("bye"));
	}

	public void handle(String msg) {
		if (msg.contains(ENC_MARKER	)) {
			line = "bye";
			stop();
		} else {
			System.out.println(msg);
		}

	}

	public void stop() {
		try {
			if (console != null) {
				console.close();
			}
			if (strOut != null) {
				strOut.close();
			}
			if (socket != null) {
				socket.close();
			}
		} catch (IOException e) {
			System.out.println("problem inside stop of " + "ChatClient " + e.getMessage());
		}
	}

	public static void main(String[] args) {
		// ChatClient myClient = new ChatClient("localhost", 8080);
		if (args.length != 2) {
			System.out.println("You need a hostname and a port number to connect your client to a server");
		} else {
			String serverName = args[0];
			int port = Integer.parseInt(args[1]);
			ChatClient client = new ChatClient(serverName, port);
		}
	}
}
