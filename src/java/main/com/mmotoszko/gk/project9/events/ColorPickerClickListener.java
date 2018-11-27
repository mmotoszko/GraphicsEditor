/*******************************************************************************
 * Micha³ Motoszko
 * 2018-11-27
 * Grafika komputerowa PS 9
 * Projekt 9
 ******************************************************************************/
package com.mmotoszko.gk.project9.events;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import com.mmotoszko.gk.project9.GraphicsEditor;
import com.mmotoszko.gk.project9.drawing.Functions2D;
import com.mmotoszko.gk.project9.ui.dialogs.ColorPickerDialog;

public class ColorPickerClickListener implements ActionListener {
	private final GraphicsEditor ge;

	public ColorPickerClickListener(GraphicsEditor graphicsEditor) {
		super();
		this.ge = graphicsEditor;
	}

	public void actionPerformed(ActionEvent e) {
		JButton source = (JButton) e.getSource();
		Color newColor = Color.pink;

		ColorPickerDialog colorPickerDialog = new ColorPickerDialog(ge.mainPanel, ge.drawingColor);

		int result = JOptionPane.showOptionDialog(ge.mainPanel, colorPickerDialog.getMessage(),
				colorPickerDialog.getTitle(), colorPickerDialog.getOptionType(), colorPickerDialog.getMessageType(),
				null, null, null);

		if (result == JOptionPane.OK_OPTION) {
			newColor = colorPickerDialog.getColor();
		} else {
			newColor = ge.drawingColor;
		}

		ge.setDrawingColor(newColor);
		Image icon = Functions2D.getImage(ge.drawingColor, 22, 22);
		source.setIcon(new ImageIcon(icon));
	}
}
