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
import java.awt.geom.AffineTransform;

import javax.swing.JPanel;

import com.mmotoszko.gk.project9.GraphicsEditor;

public class RulerLeft extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final GraphicsEditor ge;

	public RulerLeft(GraphicsEditor graphicsEditor) {
		super();
		this.ge = graphicsEditor;
	}

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		super.paintComponent(g2d);

		for (int i = 0 - ge.lookOffset.getRoundY() - 50; i < ge.canvasPanel.getHeight()
				- ge.lookOffset.getRoundY(); i++) {
			if (i % 100 == 0) {
				g2d.drawLine(this.getWidth(), i + ge.lookOffset.getRoundY(), this.getWidth() - 7,
						i + ge.lookOffset.getRoundY());
				AffineTransform orig = g2d.getTransform();
				g2d.rotate(-Math.PI / 2);
				g2d.setColor(Color.BLACK);
				g2d.drawString(i + "", 0 - (i + ge.lookOffset.getRoundY()) + 3, 9);
				g2d.setTransform(orig);
			} else if (i % 10 == 0) {
				g2d.drawLine(this.getWidth(), i + ge.lookOffset.getRoundY(), this.getWidth() - 3,
						i + ge.lookOffset.getRoundY());
			}
		}
	}
}
