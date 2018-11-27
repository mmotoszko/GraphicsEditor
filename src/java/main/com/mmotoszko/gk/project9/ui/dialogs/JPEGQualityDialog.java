/*******************************************************************************
 * Micha³ Motoszko
 * 2018-11-27
 * Grafika komputerowa PS 9
 * Projekt 9
 ******************************************************************************/
package com.mmotoszko.gk.project9.ui.dialogs;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class JPEGQualityDialog extends JOptionPane {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static int DEFAULT_VALUE = 10;
	private static int MAXIMUM_VALUE = 12;

	public JPEGQualityDialog(JPanel parent) {
		super();
		JSlider slider = getSlider(this);
		this.setMessage(new Object[] { "Select compressed image quality:", slider });
		this.setMessageType(JOptionPane.QUESTION_MESSAGE);
		this.setOptionType(JOptionPane.OK_CANCEL_OPTION);
		JDialog dialog = this.createDialog(parent, "Exporting JPEG");
		dialog.setVisible(true);
	}

	private JSlider getSlider(final JOptionPane optionPane) {
		JSlider slider = new JSlider(0, MAXIMUM_VALUE);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.setSnapToTicks(true);
		slider.setMajorTickSpacing(1);
		slider.setValue(DEFAULT_VALUE);
		ChangeListener changeListener = new ChangeListener() {
			public void stateChanged(ChangeEvent changeEvent) {
				JSlider theSlider = (JSlider) changeEvent.getSource();
				if (!theSlider.getValueIsAdjusting()) {
					optionPane.setInputValue(new Integer(theSlider.getValue()));
				}
			}
		};
		slider.addChangeListener(changeListener);
		return slider;
	}

	public int getSliderIntValue() {
		int value = DEFAULT_VALUE;
		try {
			value = Integer.parseInt((String) this.getInputValue());
		} catch (NumberFormatException e1) {
		} catch (ClassCastException e2) {
			value = (int) this.getInputValue();
		}
		return value;
	}

	public float getSliderScalarValue() {
		return (float) getSliderIntValue() / (float) MAXIMUM_VALUE;
	}
}
