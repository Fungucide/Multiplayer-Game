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
	private int type = 0;// Union=0 Intersect=1

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

	public void log(LogMessageType[] lmt, String message) {
		while (log.size() > MAX_SIZE)
			log.remove(0);
		log.add(new LogMessage(lmt, message));
		update();
	}

	public void setFilter(boolean[] filter) {
		this.filter = filter;
		update();
	}

	public void setType(int type) {
		this.type = type;
		update();
	}

	public void update() {
		setText("");
		for (int i = log.size() - 1; i >= 0; i--) {
			LogMessage lm = log.get(i);
			boolean flag;
			if (type == 0) {
				flag = false;
				for (LogMessageType lmt : lm.lmt) {
					if (filter[lmt.ordinal()]) {
						flag = true;
						break;
					}
				}
			} else {
				flag = true;
				for (int j = 0; j < filter.length && flag; j++) {
					boolean has = false;
					for (LogMessageType lmt : lm.lmt) {
						if (lmt.ordinal() == j && !filter[j]) {
							flag = false;
							break;
						} else if (lmt.ordinal() == j) {
							has = true;
						}
					}
					if (filter[j] && !has) {
						flag = false;
					}
				}
			}
			if (!flag)
				continue;
			super.append(" [" + lm.time + "]");
			for (LogMessageType lmt : lm.lmt) {
				super.append("[" + lmt.toString() + "]");
			}
			super.append(": " + lm.message + "\n");
		}
	}
}

class LogMessage {
	protected final String time;
	protected final LogMessageType[] lmt;
	protected final String message;
	private DateFormat df = new SimpleDateFormat("HH:mm:ss");
	private Date date = new Date();

	public LogMessage(LogMessageType lmt, String message) {
		time = df.format(date);
		this.lmt = new LogMessageType[] { lmt };
		this.message = message;
	}

	public LogMessage(LogMessageType[] lmt, String message) {
		time = df.format(date);
		this.lmt = lmt;
		this.message = message;
	}
}