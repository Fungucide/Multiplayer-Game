package GUI;

import java.awt.event.MouseEvent;

import javax.swing.JCheckBoxMenuItem;

public class FilterCheckBox extends JCheckBoxMenuItem {
	
	//Take in JMenu and call the event when the button is pressed so that the log can update the text
	public FilterCheckBox(String s) {
		super(s);
	}
	
	@Override
	protected void processMouseEvent(MouseEvent evt) {
		if (evt.getID() == MouseEvent.MOUSE_RELEASED && contains(evt.getPoint())) {
			doClick();
			setArmed(true);
		} else {
			super.processMouseEvent(evt);
		}
	}
}
