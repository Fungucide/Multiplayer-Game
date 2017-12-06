package Log;

import java.awt.Color;

public enum LogMessageType {
	SERVER(Color.BLUE),
	CLIENT(Color.GREEN),
	DATA(Color.BLACK),
	COMMAND(Color.GRAY),
	MESSAGE(Color.BLACK),
	ERROR(Color.RED),
	DISCONNECT(Color.GRAY),
	CLOSE(Color.GRAY),
	LOAD_WORLD(Color.GRAY),
	SET_WORLD(Color.GRAY);

	private final Color c;

	private LogMessageType(Color c) {
		this.c = c;
	}

	public Color getColor() {
		return c;
	}
}
