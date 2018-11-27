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
import com.mmotoszko.gk.project9.drawing.Point2D;
import com.mmotoszko.gk.project9.drawing.ShapeType;
import com.mmotoszko.gk.project9.drawing.customshape.CustomShape2D;
import com.mmotoszko.gk.project9.ui.TextFieldContainer;

public class DetailsSaveButtonClickListener implements ActionListener {
	private final GraphicsEditor ge;
	private TextFieldContainer textFieldContainer;

	public DetailsSaveButtonClickListener(GraphicsEditor graphicsEditor, TextFieldContainer textFieldContainer) {
		super();
		this.textFieldContainer = textFieldContainer;
		this.ge = graphicsEditor;
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (ge.drawing) {
			System.out.println("\nCan't modify a shape by text while drawing.");
			return;
		}

		if (command.equalsIgnoreCase(ActionType.SET_SHAPE_VALUES.toString())) {
			if (textFieldContainer != null) {
				if (ge.focusedVertice != null && textFieldContainer.gotVertice()) {
					Point2D newLocation = new Point2D(textFieldContainer.getVerticeX(),
							textFieldContainer.getVerticeY());
					if (newLocation.getX() != ge.focusedVertice.getX()
							|| newLocation.getY() != ge.focusedVertice.getY()) {
						ge.focusedVertice = ge.focusedShape.updateVertice(ge.focusedVertice, newLocation);
						ge.updateDetailsPanel();
						ge.canvasPanel.repaint();
						return;
					}
				}

				boolean fieldsValid = textFieldContainer.gotCenter() && (textFieldContainer.gotHeight()
						|| ge.mouseModeIs(ShapeType.CIRCLE) || ge.mouseModeIs(ShapeType.CUBE)
						|| ge.mouseModeIs(ActionType.SELECT) && textFieldContainer.gotWidth());

				if (fieldsValid) {
					if (textFieldContainer.gotCenter() && (textFieldContainer.getX() != ge.focusedShape.getX()
							|| textFieldContainer.getY() != ge.focusedShape.getY())) {
						ge.focusedShape.setCenter(new Point2D(textFieldContainer.getX(), textFieldContainer.getY()));
					}
					if (textFieldContainer.gotHeight()
							&& ge.focusedShape.getHeight() != textFieldContainer.getHeight()) {
						ge.focusedShape.updateVerticesToHeight(textFieldContainer.getHeight());
					}
					if (textFieldContainer.gotWidth() && ge.focusedShape.getWidth() != textFieldContainer.getWidth()) {
						ge.focusedShape.updateVerticesToWidth(textFieldContainer.getWidth());
					}
				} else {
					ge.updateDetailsPanel();
				}
			}
		} else if (command.equalsIgnoreCase(ActionType.CREATE_SHAPE.toString())) {
			if (textFieldContainer != null) {
				boolean fieldsValid = textFieldContainer.gotCenter() && (textFieldContainer.gotHeight()
						|| ge.mouseModeIs(ShapeType.CIRCLE) || ge.mouseModeIs(ShapeType.CUBE))
						&& textFieldContainer.gotWidth();

				if (fieldsValid) {
					try {
						ge.focusedShape = new CustomShape2D(
								new Point2D(textFieldContainer.getX(), textFieldContainer.getY()),
								textFieldContainer.getWidth(), textFieldContainer.getHeight(),
								ShapeType.valueOf(ge.mouseMode));
						ge.focusedShape.setColor(ge.drawingColor);
					} catch (Exception e1) {
						ge.updateDetailsPanel();
						return;
					}
					ge.addShape(ge.focusedShape);
				}
			}
		}

		ge.updateDetailsPanel();
		ge.canvasPanel.repaint();
	}
}
