package org.ratchetgx.orion.common.message;

public interface MessageService {

	public static enum MessageType {
		Mail("邮件", 1);
		String label;
		int value;

		MessageType(String label, int value) {
			this.label = label;
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}

	}

	public void send(String sender, String[] address, String title,
			String content);
}
