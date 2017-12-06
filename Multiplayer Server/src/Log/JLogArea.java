package Log;

import java.awt.Color;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

public class JLogArea extends JTextPane {
	private final ReentrantLock lock;
	private ArrayList<LogMessage> log;
	private final int MAX_SIZE = 10000;
	private final JLogUpdater lu;
	private final Queue<LogMessage> queue;
	// private boolean updating = false;
	private boolean[] filter;
	private int type = 0;

	public JLogArea() {
		super();
		queue = new LinkedList<LogMessage>();
		log = new ArrayList<LogMessage>();
		lu = new JLogUpdater(this);
		lock = new ReentrantLock();
		Thread t = new Thread(lu);
		t.start();
	}

	public void log(LogMessageType lmt, String message) {
		queue.add(new LogMessage(lmt, message));
		updateLog(false);
	}

	public void log(LogMessageType[] lmt, String message) {
		queue.add(new LogMessage(lmt, message));
		updateLog(false);
	}

	public void setFilter(boolean[] filter) {
		this.filter = filter;
		updateLog(true);
	}

	public void setType(int type) {
		this.type = type;
		updateLog(true);
	}

	public void updateLog(boolean force) {
		lock.lock();
		try {
			if (!force && queue.isEmpty())
				return;

			setText("");

			while (!queue.isEmpty())
				log.add(queue.poll());
			while (log.size() > MAX_SIZE)
				log.remove(0);

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
				append("[" + lm.time + "]", Color.BLACK, true);
				for (LogMessageType lmt : lm.lmt) {
					append("[" + lmt.toString() + "]", lmt.getColor(), true);
				}
				append(": " + lm.message + "\n", Color.BLACK, false);
			}
		} finally {
			lock.unlock();
		}
	}

	private void append(String msg, Color c, boolean bold) {
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

		aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Arial");
		aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);
		aset = sc.addAttribute(aset, StyleConstants.Bold, bold);

		setEditable(true);
		int len = getDocument().getLength();
		setCaretPosition(len);
		setCharacterAttributes(aset, false);
		replaceSelection(msg);
		setEditable(false);
	}

	public void clear() {
		log.clear();
		updateLog(true);
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

class JLogUpdater implements Runnable {

	private final JLogArea jla;

	public JLogUpdater(JLogArea jla) {
		this.jla = jla;
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// jla.updateLog();
		}
	}

}