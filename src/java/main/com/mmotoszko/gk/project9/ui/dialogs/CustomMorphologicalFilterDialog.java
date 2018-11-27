/*******************************************************************************
 * Micha³ Motoszko
 * 2018-11-27
 * Grafika komputerowa PS 9
 * Projekt 9
 ******************************************************************************/
package com.mmotoszko.gk.project9.ui.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.mmotoszko.gk.project9.GraphicsEditor;
import com.mmotoszko.gk.project9.drawing.Filters;
import com.mmotoszko.gk.project9.drawing.customshape.CustomImage;
import com.mmotoszko.gk.project9.events.menu.edit.CustomMorphologicalFilterPreviewRunner;

public class CustomMorphologicalFilterDialog extends JOptionPane
		implements CustomDialog, ActionListener, MouseListener, MouseMotionListener {
	private static final long serialVersionUID = 1L;
	private CustomMorphologicalFilterPreviewRunner imageRefresher;
	private JPanel mainPanel;
	private int size;
	private float mouseDownValue = -1F;
	private Filters filterType;
	private float[][] mask;

	public CustomMorphologicalFilterDialog(JPanel parent, GraphicsEditor graphicsEditor, CustomImage image,
			Filters filterType) {
		super();
		size = 3;
		this.filterType = filterType;
		mask = new float[3][];
		mask[0] = new float[] { 0F, 0F, 0F };
		mask[1] = new float[] { 0F, 1F, 0F };
		mask[2] = new float[] { 0F, 0F, 0F };
		mainPanel = getMainPanel(size);
		imageRefresher = new CustomMorphologicalFilterPreviewRunner(graphicsEditor, image);
		Thread imageRefresherThread = new Thread(imageRefresher);
		imageRefresherThread.start();
		this.setMessage(getMessage());
		this.setOptionType(getOptionType());
		this.setOptions(getOptions());
	}

	private JPanel getBottomPanel() {
		JPanel bottomPanel = new JPanel(new GridLayout(1, 0, 6, 6));

		JButton removeButton = new JButton("-");
		JButton previewButton = new JButton("Preview");
		JButton addButton = new JButton("+");

		removeButton.addActionListener(this);
		previewButton.addActionListener(this);
		addButton.addActionListener(this);

		removeButton.setActionCommand("remove");
		previewButton.setActionCommand("preview");
		addButton.setActionCommand("add");

		bottomPanel.add(removeButton);
		bottomPanel.add(previewButton);
		bottomPanel.add(addButton);

		return bottomPanel;
	}

	private JPanel getMaskPanel() {
		MaskPanel maskPanel = new MaskPanel();

		maskPanel.setPreferredSize(new Dimension(300, 300));
		maskPanel.addMouseListener(this);
		maskPanel.addMouseMotionListener(this);

		return maskPanel;
	}

	private JPanel getMainPanel(int size) {
		JPanel mainPanel = new JPanel(new BorderLayout(6, 6));

		mainPanel.add(getMaskPanel(), BorderLayout.CENTER);
		mainPanel.add(getBottomPanel(), BorderLayout.PAGE_END);

		return mainPanel;
	}

	public Object[] getMessage() {
		return new Object[] { mainPanel };
	}

	private void updateSize(int size) {
		float[][] newMask = new float[size][size];

		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				if (size > mask.length) {
					if (row >= 1 && row < size - 1 && col >= 1 && col < size - 1) {
						newMask[col][row] = mask[col - 1][row - 1];
					} else {
						newMask[col][row] = 0;
					}
				} else if (size < mask.length) {
					newMask[col][row] = mask[col + 1][row + 1];
				} else {
					newMask[col][row] = mask[col][row];
				}
			}
		}

		mask = newMask;
		mainPanel.removeAll();
		JPanel centerPanel = getMaskPanel();
		mainPanel.add(centerPanel, BorderLayout.CENTER);
		mainPanel.add(getBottomPanel(), BorderLayout.PAGE_END);
		mainPanel.setPreferredSize(new Dimension(132 + 40 * (size - 3), 38 + 15 * (size - 3)));
		mainPanel.revalidate();
	}

	public int getMessageType() {
		return JOptionPane.PLAIN_MESSAGE;
	}

	public int getOptionType() {
		return JOptionPane.OK_CANCEL_OPTION;
	}

	public String getTitle() {
		return filterType.toString();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();

		if ("preview".equals(command)) {
			imageRefresher.update(false, mask.clone(), filterType);
		} else if ("add".equals(command)) {
			size += 2;
			updateSize(size);
		} else if ("remove".equals(command)) {
			if (size > 1) {
				size -= 2;
				updateSize(size);
			}
		}
	}

	@Override
	public JDialog createDialog(JComponent component, String title) {
		return super.createDialog(component, title);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mousePressed(e);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		Point mouseLocation = mainPanel.getComponent(0).getMousePosition();
		Dimension dimension = mainPanel.getComponent(0).getSize();

		int col = (int) ((float) mouseLocation.x / (dimension.width / (float) mask.length));
		int row = (int) ((float) mouseLocation.y / (dimension.height / (float) mask.length));

		if (mouseDownValue == 0F || mouseDownValue == 1F) {
			if (mask[col][row] != mouseDownValue) {
				mask[col][row] = mouseDownValue;
			}
		} else {
			if (mask[col][row] == 0F) {
				mask[col][row] = 1F;
			} else {
				mask[col][row] = 0F;
			}
		}

		mouseDownValue = mask[col][row];

		mainPanel.getComponent(0).repaint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		mouseDownValue = -1F;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	private class MaskPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		@Override
		public void paintComponent(Graphics g) {
			this.setOpaque(true);
			this.setBackground(Color.LIGHT_GRAY);
			Graphics2D g2d = (Graphics2D) g;
			super.paintComponent(g2d);

			float cellWidth = this.getWidth() / (float) mask.length;
			float cellHeight = this.getHeight() / (float) mask.length;

			for (int row = 0; row < mask.length; row++) {
				for (int col = 0; col < mask.length; col++) {
					if (mask[col][row] == 0F) {
						g2d.setColor(Color.white);
					} else {
						if (col == row && col == (int) (mask.length / (float) 2)) {
							g2d.setColor(Color.black);
						} else {
							g2d.setColor(Color.blue);
						}
					}

					g2d.fillRect(Math.round(col * cellWidth), Math.round(row * cellHeight), (int) Math.ceil(cellWidth),
							(int) Math.ceil(cellHeight));
				}
			}

			g2d.setColor(Color.black);
			for (int row = 0; row < mask.length; row++) {
				for (int col = 0; col < mask.length; col++) {
					g2d.drawLine(Math.round(col * cellWidth), 0, Math.round(col * cellWidth), this.getHeight());
					g2d.drawLine(0, Math.round(row * cellHeight), this.getWidth(), Math.round(row * cellHeight));
				}
			}
			g2d.drawLine(this.getWidth() - 1, 0, this.getWidth() - 1, this.getHeight() - 1);
			g2d.drawLine(0, this.getHeight() - 1, this.getWidth() - 1, this.getHeight() - 1);
		}
	}
}
