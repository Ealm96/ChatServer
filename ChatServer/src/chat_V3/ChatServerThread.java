package chat_V3;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ChatServerThread extends Thread {

	private ChatServer server = null;
	private Socket socket = null;
	private DataInputStream strIn = null;
	private int ID = -1;

	public ChatServerThread(ChatServer _server, Socket _socket) {
		super();
		server = _server;
		socket = _socket;
		ID = socket.getPort(); // becomes client's ID
		System.out.println("INFO: server= " + server + " socket= " + socket + " ID=" + ID);
	}

	@Override
	public void run() {
		try {
			while (ID != -1) {
				// server.handle(ID, strIn.readUTF());
				close();
			}
		} catch (IOException e) {
			System.out.println("Exception running ChatServerThread " + e.getMessage());
		}
	}

	public void open() throws IOException {// step 3 open streams
		strIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
		System.out.println("Opened Input Stream Successfully");
	}

	public void close() throws IOException {// step 5 close streams and sockets
		if (strIn != null) {
			strIn.close();
		}
		if (socket != null) {
			socket.close();
		}
	}

}
