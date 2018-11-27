/*******************************************************************************
 * Micha³ Motoszko
 * 2018-11-27
 * Grafika komputerowa PS 9
 * Projekt 9
 ******************************************************************************/
package com.mmotoszko.gk.project9.ui;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import com.mmotoszko.gk.project9.GraphicsEditor;

public class RulerTop extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final GraphicsEditor ge;

	public RulerTop(GraphicsEditor graphicsEditor) {
		super();
		this.ge = graphicsEditor;
	}

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		super.paintComponent(g2d);

		for (int i = 0 - ge.lookOffset.getRoundX() - 50; i < ge.canvasPanel.getWidth() - ge.lookOffset.getRoundX(); i++) {
			if (i % 100 == 0) {
				g2d.drawLine(i + ge.lookOffset.getRoundX() + 10, this.getHeight(), i + ge.lookOffset.getRoundX() + 10, this.getHeight() - 7);
				g2d.drawString(i + "", i + ge.lookOffset.getRoundX() + 13, 9);
			} else if (i % 10 == 0) {
				g2d.drawLine(i + ge.lookOffset.getRoundX() + 10, this.getHeight(), i + ge.lookOffset.getRoundX() + 10, this.getHeight() - 3);
			}
		}
	}
}
