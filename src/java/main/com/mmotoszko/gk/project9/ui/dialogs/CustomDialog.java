/*******************************************************************************
 * Micha³ Motoszko
 * 2018-11-27
 * Grafika komputerowa PS 9
 * Projekt 9
 ******************************************************************************/
package com.mmotoszko.gk.project9.ui.dialogs;

import javax.swing.JComponent;
import javax.swing.JDialog;

public interface CustomDialog {

	public String getTitle();

	public Object[] getMessage();

	public int getOptionType();

	public int getMessageType();

	public JDialog createDialog(JComponent component, String title);
	
	public Object getValue();
}
