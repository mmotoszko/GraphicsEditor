/*******************************************************************************
 * Micha³ Motoszko
 * 2018-11-27
 * Grafika komputerowa PS 9
 * Projekt 9
 ******************************************************************************/
package com.mmotoszko.gk.project9.ui.dialogs;

import java.awt.Dialog;

import javax.swing.JOptionPane;

import com.mmotoszko.gk.project9.GraphicsEditor;
import com.mmotoszko.gk.project9.drawing.Filters;
import com.mmotoszko.gk.project9.drawing.customshape.CustomImage;

public class FilterDialogController {
	public int openDialog(GraphicsEditor ge, CustomImage image, Filters type) {
		CustomDialog customDialog = null;
		boolean createDialog = false;
		if (type == Filters.BRIGHTNESS) {
			customDialog = new AdjustImageBrightnessDialog(ge.mainPanel, ge, image);
		} else if (type == Filters.COLOR) {
			customDialog = new AdjustImageColorDialog(ge.mainPanel, ge, image);
		} else if (type == Filters.CUSTOM) {
			customDialog = new CustomFilterDialog(ge.mainPanel, ge, image);
			createDialog = true;
		} else if (type == Filters.FIND_COLOR) {
			customDialog = new FindColorDialog(ge.mainPanel, ge, image, type);
			createDialog = true;
		} else if (type == Filters.DILATION || type == Filters.EROSION || type == Filters.CLOSING
				|| type == Filters.OPENING || type == Filters.HIT_OR_MISS) {
			customDialog = new CustomMorphologicalFilterDialog(ge.mainPanel, ge, image, type);
			createDialog = true;
		}

		if (customDialog == null) {
			return JOptionPane.showConfirmDialog(ge.mainPanel, "Would you like to apply this filter?", "Filter preview",
					JOptionPane.OK_CANCEL_OPTION);
		}

		if (createDialog) {
			Dialog dialog = customDialog.createDialog(ge.mainPanel, customDialog.getTitle());
			dialog.setResizable(true);
			dialog.pack();
			dialog.setVisible(true);

			if (customDialog.getValue() == null) {
				return JOptionPane.CANCEL_OPTION;
			}

			return (int) customDialog.getValue();
		}

		return JOptionPane.showOptionDialog(ge.mainPanel, customDialog.getMessage(), customDialog.getTitle(),
				customDialog.getOptionType(), customDialog.getMessageType(), null, null, null);
	}
}
