package GUI;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JTextArea;

public class JLogArea extends JTextArea {
	private DateFormat df = new SimpleDateFormat("HH:mm:ss");
	private Date date = new Date();

	@Deprecated
	public void append(String s) {
		super.append(s);
	}

	public void log(LogMessageType lmt, String message) {
		super.append(" [" + df.format(date) + "] [" + lmt.toString() + "]: " + message + "\n");
	}
}
