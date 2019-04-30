package chat_V5;

public class OneTimePad {

	protected String plainMessage = "";
	private String encrMessage = "";
	private String keyMessage = "";

	public OneTimePad(String msg) {

		plainMessage = msg;
		keyMessage = getKey();
		encrMessage = encrypt();

	}

	public OneTimePad() {

	}

	public String getEncryptedKey() {
		return this.keyMessage;
	}

	protected String getKey() {

		String key = "";
		for (int i = 0; i < plainMessage.length(); i++) {
			char randomChar = Character.toChars(7 + (int) (Math.random() * 50))[0];
			key += randomChar;
		}

		return key;

	}

	protected void setKey(String key) {
		keyMessage = key;
	}

	protected String encrypt() {
		String encryptedMessage = "";
		for (int i = 0; i < plainMessage.length(); i++) {
			encryptedMessage += Character.toChars(keyMessage.charAt(i) + plainMessage.charAt(i))[0];
		}

		return encryptedMessage;
	}

	protected void setEncr(String msg) {
		encrMessage = msg;
	}

	protected String decrypt() {
		String decryptedMessage = "";
		for (int i = 0; i < encrMessage.length(); i++) {
			decryptedMessage += Character.toChars(encrMessage.charAt(i) - keyMessage.charAt(i))[0];
			;
		}

		return decryptedMessage;
	}

	public static void main(String[] args) {
		OneTimePad otp = new OneTimePad("abcdefghijklmnopqrstuvwxyz");
		System.out.println(otp.encrMessage);
		System.out.println(otp.keyMessage);
		System.out.println(otp.plainMessage);
	}

}
