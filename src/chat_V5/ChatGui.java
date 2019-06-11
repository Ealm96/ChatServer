package chat_V5;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChatGui extends JFrame {

	private String _serverName;
	private int _serverPort;

	public ChatGui(String serverName, int serverPort) {
		_serverName = serverName;
		_serverPort = serverPort;
		setTitle("ChatServer");
		ChatPanel chatPanel = new ChatPanel(serverName, serverPort);
		add(chatPanel);
	}

	private class ChatPanel extends JPanel implements Runnable, ActionListener {
		private OneTimePad otp;

		private Socket socket = null;
		private DataInputStream streamIn = null;
		private DataOutputStream streamOut = null;
		boolean done = false;
		boolean enterPressed = false;

		private JTextArea displayArea;
		private JTextField tInput, t_priv_ID_Input;
		private JTextField tf = new JTextField(40);
		private JButton jbSend, jbSendPriv, jbSendPrivEncr, jbConnect, jbQuit;
		private String[] jbtnNames = { "SEND", "SEND PRIV", "SEND PRIV ENCR", "CONNECT", "DISCONNECT" };
		private JButton[] jbtns = new JButton[jbtnNames.length];
		private final int BTN_INDEX_SEND = 0;
		private final int BTN_INDEX_SEND_PRIV = 1;
		private final int BTN_INDEX_SEND_PRIV_ENCR = 2;
		private final int BTN_INDEX_CONNECT = 3;
		private final int BTN_INDEX_DISCONNECT = 4;
		private final String ENC_INDICATOR = "encr-";

		public ChatPanel(String serverName, int serverPort) {
			setLayout(new BorderLayout());
			displayArea = new JTextArea();
			displayArea.setEditable(false);
			displayArea.setBackground(Color.RED);
			add(displayArea, BorderLayout.CENTER);

			JPanel bottomPanel = new JPanel();
			bottomPanel.setBackground(Color.BLUE);

			JPanel btnPanel = new JPanel();
			btnPanel.setLayout(new GridLayout(4, 6));
			btnPanel.setBackground(Color.BLUE);
			for (int i = 0; i < jbtns.length; i++) {
				jbtns[i] = new JButton(jbtnNames[i]);
				jbtns[i].addActionListener(this);
				jbtns[i].setEnabled(false);
				btnPanel.add(jbtns[i]);
			}
			jbtns[BTN_INDEX_CONNECT].setEnabled(true);

			JPanel tfPanel = new JPanel(new FlowLayout());
			t_priv_ID_Input = new JTextField(5);
			tfPanel.add(t_priv_ID_Input);
			tfPanel.add(tf);

			bottomPanel.add(tfPanel);
			bottomPanel.add(btnPanel);
			add(bottomPanel, BorderLayout.SOUTH);

		}

		@Override
		public void run() {
			while (!done) {
				try {
					String line = streamIn.readUTF();
					updateDisplay(line);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		public void connect() {
			String conStatus = "Connected to server " + _serverName + " on port: " + _serverPort + "\n";
			try {
				socket = new Socket(_serverName, _serverPort);
				open();
				enableButtons();
			} catch (UnknownHostException e) {
				conStatus = e.getMessage();
			} catch (IOException e) {
				conStatus = e.getMessage();
			} finally {
				updateDisplay(conStatus);
			}
		}

		public void disconnect() {
			disableButtons();
			send("bye");
			done = true;
		}

		public void open() {
			try {
				streamOut = new DataOutputStream(socket.getOutputStream());
				streamIn = new DataInputStream(socket.getInputStream());
				new Thread(this).start();// to be able to listen in
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void enableButtons() {
			for (int i = 0; i < jbtns.length; i++) {
				jbtns[i].setEnabled(true);
			}
			jbtns[BTN_INDEX_CONNECT].setEnabled(false);
		}

		public void disableButtons() {
			for (int i = 0; i < jbtns.length; i++) {
				jbtns[i].setEnabled(false);
			}
			jbtns[BTN_INDEX_CONNECT].setEnabled(true);
		}

		public void send(String msg) {
			try {
				streamOut.writeUTF(msg);
				streamOut.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void sendPrivate(String ID, String msg) {

			send("pm_to " + "[" + ID.trim() + "]" + msg);

		}

		public void sendPrivateEncr(String ID, String msg) {
			otp = new OneTimePad(msg);

			String encryptedKey = otp.getEncryptedKey();

			String encryptedMessageAndKey = otp.encrypt();

			encryptedMessageAndKey += encryptedKey;
			send("pm_to" + "[" + ID.trim() + "]" + ENC_INDICATOR + encryptedMessageAndKey);
		}

		public void updateDisplay(String text) {
			if (text.contains(ENC_INDICATOR)) {
				String encrMsg = text.substring(text.indexOf(ENC_INDICATOR) + ENC_INDICATOR.length() + 1,
						text.length());
				OneTimePad otp2 = new OneTimePad();
				otp2.setEncr(encrMsg.substring(0, (encrMsg.length() / 2)));
				otp2.setKey(encrMsg.substring(encrMsg.length() / 2));
				// encr|key so substring the lenght/2
				// setENC on a new otp object
				// setKey on that same new otp object
				// call .decrypt() on that object

				String plainMsg = otp2.decrypt();
				System.out.println("this");
				System.out.println(encrMsg.substring(0, (encrMsg.length() / 2)));
				System.out.print(encrMsg.substring(encrMsg.length() / 2));
				displayArea.append(" " + plainMsg);
			} else {
				displayArea.append("\n" + text);// plain incoming message
			}
		}

		@Override
		public void actionPerformed(ActionEvent e) {

			JButton btnClicked = (JButton) e.getSource();
			if (btnClicked.equals(jbtns[BTN_INDEX_CONNECT])) {
				connect();
			} else if (btnClicked.equals(jbtns[BTN_INDEX_DISCONNECT])) {
				disconnect();
			} else if (btnClicked.equals(jbtns[BTN_INDEX_SEND])) {
				String msg = tf.getText();
				send(msg);
				tf.setText("");
			} else if (btnClicked.equals(jbtns[BTN_INDEX_SEND_PRIV])) {
				String msg = tf.getText();
				String ID = t_priv_ID_Input.getText();
				sendPrivate(ID, msg);
				tf.setText("");
				t_priv_ID_Input.setText("");
			} else if (btnClicked.equals(jbtns[BTN_INDEX_SEND_PRIV_ENCR])) {
				String msg = tf.getText();
				String ID = t_priv_ID_Input.getText();
				sendPrivateEncr(ID, msg);
			}
		}

	}

	public static void main(String[] args) {
		// ChatGUI gui = new ChatGUI("192.168.1.101", 8080);

		ChatGui gui = new ChatGui(args[0], Integer.parseInt(args[1]));
		gui.setSize(530, 500);
		gui.setResizable(false);
		gui.setDefaultCloseOperation(EXIT_ON_CLOSE);
		gui.setLocationRelativeTo(null);
		gui.setVisible(true);
	}
}
