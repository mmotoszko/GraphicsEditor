/*******************************************************************************
 * Micha³ Motoszko
 * 2018-11-27
 * Grafika komputerowa PS 9
 * Projekt 9
 ******************************************************************************/
package com.mmotoszko.gk.project9.events.menu.edit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import com.mmotoszko.gk.project9.GraphicsEditor;
import com.mmotoszko.gk.project9.drawing.Filters;
import com.mmotoszko.gk.project9.drawing.ShapeType;
import com.mmotoszko.gk.project9.drawing.customshape.CustomImage;
import com.mmotoszko.gk.project9.drawing.customshape.CustomImageFilter;
import com.mmotoszko.gk.project9.ui.dialogs.FilterDialogController;

public class AddImageFilterClickListener implements ActionListener {
	private final GraphicsEditor ge;
	private Filters filterType;

	public AddImageFilterClickListener(GraphicsEditor graphicsEditor, Filters filterType) {
		super();
		this.ge = graphicsEditor;
		this.filterType = filterType;
	}

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
		CustomImageFilter filter = getFilter(filterType);

		if (filter == null) {
			JOptionPane.showMessageDialog(ge.mainPanel, "Incorrect filter", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		image.previewFilter(filter);
		ge.refresh();
		
		FilterDialogController dialogController = new FilterDialogController();

		int result = dialogController.openDialog(ge, image, filterType);

		if (result == JOptionPane.OK_OPTION) {
			image.applyPreviewFilter();
		} else {
			image.removePreviewFilter();
		}

		ge.refresh();
	}

	private CustomImageFilter getFilter(Filters filterType) {
		if (filterType == Filters.BRIGHTNESS) {
			return CustomImageFilter.getFilter(filterType, 0);
		} else if (filterType == Filters.COLOR) {
			return CustomImageFilter.getFilter(filterType, 0, 0, 0);
		} else if (filterType == Filters.FIND_COLOR) {
			return CustomImageFilter.getFilter(filterType, 0, 0, 0, 0, 0);
		} 
		
		return CustomImageFilter.getFilter(filterType, 3);
	}
}
