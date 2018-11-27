/*******************************************************************************
 * Micha³ Motoszko
 * 2018-11-27
 * Grafika komputerowa PS 9
 * Projekt 9
 ******************************************************************************/
package com.mmotoszko.gk.project9.events.menu.edit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import com.mmotoszko.gk.project9.GraphicsEditor;
import com.mmotoszko.gk.project9.drawing.ShapeType;
import com.mmotoszko.gk.project9.drawing.customshape.CustomImage;
import com.mmotoszko.gk.project9.ui.dialogs.HistogramDialog;

public class ModifyHistogramClickListener implements ActionListener {
	private final GraphicsEditor ge;

	public ModifyHistogramClickListener(GraphicsEditor graphicsEditor) {
		super();
		this.ge = graphicsEditor;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ge.focusedShape == null) {
			JOptionPane.showMessageDialog(ge.mainPanel, "No image selected.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (ge.focusedShape.getType() != ShapeType.IMAGE) {
			JOptionPane.showMessageDialog(ge.mainPanel, "Selected shape is not an image.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		CustomImage image = (CustomImage) ge.focusedShape;

		Integer result = JOptionPane.CANCEL_OPTION;
		HistogramDialog histogramDialog = new HistogramDialog(ge.mainPanel, ge, image);

		JDialog dialog = histogramDialog.createDialog(ge.mainPanel, histogramDialog.getTitle());
		dialog.setResizable(true);
		dialog.setVisible(true);

		result = (Integer) histogramDialog.getValue();

		if (result != null && result == JOptionPane.OK_OPTION) {
			image.applyHistogramChanges();
		} else {
			image.removeHistogramChanges();
		}

		ge.refresh();
	}
}
