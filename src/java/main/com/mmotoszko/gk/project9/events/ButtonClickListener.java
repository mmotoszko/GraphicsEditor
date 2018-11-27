/*******************************************************************************
 * Micha³ Motoszko
 * 2018-11-27
 * Grafika komputerowa PS 9
 * Projekt 9
 ******************************************************************************/
package com.mmotoszko.gk.project9.events;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.mmotoszko.gk.project9.GraphicsEditor;
import com.mmotoszko.gk.project9.drawing.Functions2D;

public class ButtonClickListener implements ActionListener {
	private final GraphicsEditor ge;
	
	public ButtonClickListener(GraphicsEditor graphicsEditor) {
		super();
		this.ge = graphicsEditor;
	}
	
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		//JButton source = (JButton)e.getSource();
		
		if("drawCubeFaces".equals(command)) {
			Functions2D.drawCubeFaces = !Functions2D.drawCubeFaces;
			return;
		}
		
		ge.mouseMode = command;
		ge.mouseModeLabel.setText(command);
		ge.updateDetailsPanel();
	}
}
