/*******************************************************************************
 * Micha³ Motoszko
 * 2018-11-27
 * Grafika komputerowa PS 9
 * Projekt 9
 ******************************************************************************/
package com.mmotoszko.gk.project9.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import com.mmotoszko.gk.project9.GraphicsEditor;
import com.mmotoszko.gk.project9.drawing.Functions2D;
import com.mmotoszko.gk.project9.drawing.ShapeType;
import com.mmotoszko.gk.project9.drawing.customshape.CustomShape;

public class CustomCanvas extends JPanel {
	private static final long serialVersionUID = 1L;
	private final GraphicsEditor ge;

	public CustomCanvas(GraphicsEditor graphicsEditor) {
		super();
		this.ge = graphicsEditor;
	}

	@Override
	public void paintComponent(Graphics g) {
		this.setOpaque(true);
		this.setBackground(Color.LIGHT_GRAY);
		Graphics2D g2d = (Graphics2D) g;
		super.paintComponent(g2d);

		Functions2D.drawWorkspaceBoundries(g2d, ge.getShapeList().get(0), ge.lookOffset);
		Functions2D.drawWorkspaceBase(g2d, ge.getShapeList().get(0), ge.lookOffset);

		for (CustomShape shape : ge.shapeList) {
			if (shape.getType() != ShapeType.WORKSPACE) {
				if (shape != ge.focusedShape) {
					Functions2D.drawShape(g2d, shape, ge.lookOffset, GraphicsEditor.VERTICE_RADIUS, this.getWidth(),
							this.getHeight());
				} else {
					Functions2D.drawFocusedShape(g2d, shape, ge.lookOffset, GraphicsEditor.VERTICE_RADIUS,
							this.getWidth(), this.getHeight());
				}
			}
		}

		Functions2D.drawWorkspaceRest(g2d, ge.getShapeList().get(0), ge.lookOffset, this.getWidth(), this.getHeight());

		if (ge.focusedVertice != null) {
			g2d.setColor(Color.RED);
			Functions2D.drawFocusedVertice(g2d, ge.focusedVertice, ge.lookOffset, GraphicsEditor.VERTICE_RADIUS);

		}
	}
}
