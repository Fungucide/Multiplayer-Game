package GUI;

import java.awt.event.MouseEvent;

import javax.swing.JCheckBoxMenuItem;

public class FilterCheckBox extends JCheckBoxMenuItem {
	
	private boolean[] filter;
	private JLogArea jla;
	
	//Take in JMenu and call the event when the button is pressed so that the log can update the text
	public FilterCheckBox(String s,boolean[] filter,JLogArea jla) {
		super(s);
		this.filter= filter;
		this.jla=jla;
	}
	
	@Override
	protected void processMouseEvent(MouseEvent evt) {
		if (evt.getID() == MouseEvent.MOUSE_RELEASED && contains(evt.getPoint())) {
			doClick();
			setArmed(true);
			filter[LogMessageType.valueOf(getText()).ordinal()]=isSelected();
			jla.setFilter(filter);
		} else {
			super.processMouseEvent(evt);
		}
	}
}
