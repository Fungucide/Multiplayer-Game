package GUI;

import java.awt.Color;

public enum LogMessageType {
	SERVER(Color.BLACK), CLIENT(Color.BLACK), DATA(Color.BLACK), COMMAND(Color.BLACK), MESSAGE(Color.BLACK), ERROR(Color.RED);

	private final Color c;

	private LogMessageType(Color c) {
		this.c = c;
	}

	public Color getColor() {
		return c;
	}
}
