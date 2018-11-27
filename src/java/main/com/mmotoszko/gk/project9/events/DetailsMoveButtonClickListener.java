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
import com.mmotoszko.gk.project9.ui.TextFieldContainer;

public class DetailsMoveButtonClickListener implements ActionListener {
	private final GraphicsEditor ge;
	private TextFieldContainer textFieldContainer;

	public DetailsMoveButtonClickListener(GraphicsEditor graphicsEditor, TextFieldContainer textFieldContainer) {
		super();
		this.textFieldContainer = textFieldContainer;
		this.ge = graphicsEditor;
	}

	public void actionPerformed(ActionEvent e) {
		if (ge.drawing) {
			System.out.println("\nCan't modify a shape by text while drawing.");
			return;
		}

		if (textFieldContainer != null && ge.focusedShape != null) {
			boolean fieldsValid = textFieldContainer.gotMoveVector();

			if (fieldsValid) {
				ge.focusedShape.move(textFieldContainer.getMoveVectorX(), textFieldContainer.getMoveVectorY());
			} else {
				ge.updateDetailsPanel();
			}
		}

		ge.updateDetailsPanel();
		ge.canvasPanel.repaint();
	}
}
