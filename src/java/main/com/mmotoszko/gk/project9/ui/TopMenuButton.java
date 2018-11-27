/*******************************************************************************
 * Micha³ Motoszko
 * 2018-11-27
 * Grafika komputerowa PS 9
 * Projekt 9
 ******************************************************************************/
package com.mmotoszko.gk.project9.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import com.mmotoszko.gk.project9.Main;
import com.mmotoszko.gk.project9.drawing.Functions2D;

public class TopMenuButton extends JButton {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TopMenuButton(String imageResource, String command, ActionListener clickListener) {
		super(new ImageIcon(Main.class.getResource(imageResource)));
		this.setSize(new Dimension(34, 34));
		this.setActionCommand(command);
		this.addActionListener(clickListener);
	}

	public TopMenuButton(Color color, String command, ActionListener clickListener) {
		super();
		Image icon = Functions2D.getImage(color, 22, 22);
		this.setIcon(new ImageIcon(icon));
		this.setSize(new Dimension(34, 34));
		this.setActionCommand(command);
		this.addActionListener(clickListener);
	}

	public TopMenuButton(Color color, int width, int height, int marginX, int marginY, String command, ActionListener clickListener) {
		super();
		Image icon = Functions2D.getImage(color, width - marginX, height - marginY);
		this.setIcon(new ImageIcon(icon));
		this.setSize(new Dimension(width, height));
		this.setActionCommand(command);
		this.addActionListener(clickListener);
	}
}
