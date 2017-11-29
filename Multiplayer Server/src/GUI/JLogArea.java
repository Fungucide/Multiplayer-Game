package GUI;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JTextArea;

public class JLogArea extends JTextArea {
	private ArrayList<LogMessage> log;
	private final int MAX_SIZE = 10000;

	private boolean[] filter;

	public JLogArea() {
		log = new ArrayList<LogMessage>();
	}
	
	@Deprecated
	public void append(String s) {
		super.append(s);
	}

	public void log(LogMessageType lmt, String message) {
		while (log.size() > MAX_SIZE)
			log.remove(0);
		log.add(new LogMessage(lmt, message));
		update();
	}

	public void setFilter(boolean[] filter) {
		this.filter = filter;
		update();
	}

	public void update() {
		setText("");
		for (int i = log.size() - 1; i >= 0; i--) {
			LogMessage lm = log.get(i);
			if (filter[lm.lmt.ordinal()])
				super.append("[" + lm.time + "][" + lm.lmt.toString() + "]: " + lm.message + "\n");
		}
	}
}

class LogMessage {
	protected final String time;
	protected final LogMessageType lmt;
	protected final String message;
	private DateFormat df = new SimpleDateFormat("HH:mm:ss");
	private Date date = new Date();

	public LogMessage(LogMessageType lmt, String message) {
		time = df.format(date);
		this.lmt = lmt;
		this.message = message;
	}
}