package Log;

import java.awt.event.MouseEvent;

import javax.swing.JCheckBoxMenuItem;

public class FilterTypeCheckBox extends JCheckBoxMenuItem {
	private JLogArea jla;
	private int type;

	public FilterTypeCheckBox(String s, JLogArea jla, int type) {
		super(s);
		this.jla = jla;
		this.type = type;
	}

	@Override
	protected void processMouseEvent(MouseEvent evt) {
		if (evt.getID() == MouseEvent.MOUSE_RELEASED && contains(evt.getPoint())) {
			doClick();
			setArmed(true);
			if(isSelected()) {
				type=1;
			}else {
				type=0;
			}
			jla.setType( type);
		} else {
			super.processMouseEvent(evt);
		}
	}
}
