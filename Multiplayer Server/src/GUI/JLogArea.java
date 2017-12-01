package GUI;

import java.awt.Color;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

public class JLogArea extends JTextPane {
	private ArrayList<LogMessage> log;
	private final int MAX_SIZE = 10000;

	private boolean[] filter;
	private int type = 0;// Union=0 Intersect=1

	public JLogArea() {
		super();
		log = new ArrayList<LogMessage>();
		setText("Test");
		setVisible(true);
		System.out.println(getHeight() + " " + getWidth());
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
		for (int i = 0; i < log.size(); i++) {
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
			append(" [" + lm.time + "]", Color.BLACK);
			for (LogMessageType lmt : lm.lmt) {
				append("[" + lmt.toString() + "]", lmt.getColor());
			}
			append(": " + lm.message + "\n", Color.BLACK);
		}
	}

	private void append(String msg, Color c) {
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

		aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
		aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

		int len = getDocument().getLength();
		setCaretPosition(len);
		setCharacterAttributes(aset, false);
		replaceSelection(msg);
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