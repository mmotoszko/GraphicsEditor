/*******************************************************************************
 * Micha³ Motoszko
 * 2018-11-27
 * Grafika komputerowa PS 9
 * Projekt 9
 ******************************************************************************/
package com.mmotoszko.gk.project9.ui.dialogs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.mmotoszko.gk.project9.drawing.Functions2D;
import com.mmotoszko.gk.project9.drawing.Point2D;
import com.mmotoszko.gk.project9.drawing.customshape.CustomImage;

public class ImageColorSamplerDialog extends JOptionPane implements CustomDialog, MouseListener {
	private static final long serialVersionUID = 1L;
	private JPanel mainPanel;
	private ArrayList<Point2D> selectedPoints = new ArrayList<>();
	private CustomImage image;

	public ImageColorSamplerDialog(CustomDialog parent, CustomImage image) {
		super();
		this.image = new CustomImage(image);
		this.mainPanel = getMainPanel();
		this.setMessage(getMessage());
		this.setOptionType(getOptionType());
		this.setOptions(getOptions());
	}

	private JPanel getMainPanel() {
		JPanel mainPanel = new ImageViewerPanel(image);

		mainPanel.addMouseListener(this);

		return mainPanel;
	}

	public Object[] getMessage() {
		return new Object[] { mainPanel };
	}

	public int getMessageType() {
		return JOptionPane.PLAIN_MESSAGE;
	}

	public int getOptionType() {
		return JOptionPane.OK_CANCEL_OPTION;
	}

	public String getTitle() {
		return "Color sampler";
	}

	public Color getColor() {
		int r = 0;
		int g = 0;
		int b = 0;

		for (Color color : ((ImageViewerPanel) mainPanel).getColors()) {
			byte[] rgb = Functions2D.getRGBValues(Functions2D.getColor(color));
			r += Functions2D.getValue(rgb[0]);
			g += Functions2D.getValue(rgb[1]);
			b += Functions2D.getValue(rgb[2]);
		}

		r = (int) (r / (float) selectedPoints.size());
		g = (int) (g / (float) selectedPoints.size());
		b = (int) (b / (float) selectedPoints.size());

		return new Color(r, g, b);
	}

	public ArrayList<Color> getColors() {
		return ((ImageViewerPanel) mainPanel).getColors();
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		Point2D mouseLocation = new Point2D(mainPanel.getMousePosition());

		if (SwingUtilities.isLeftMouseButton(arg0)) {
			selectedPoints.add(mouseLocation);
		} else if (SwingUtilities.isRightMouseButton(arg0)) {
			selectedPoints.clear();
		}

		mainPanel.repaint();
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}

	@Override
	public JDialog createDialog(JComponent component, String title) {
		return super.createDialog(component, title);
	}

	private class ImageViewerPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		int width = 768;
		int height = 384;
		CustomImage image;

		public ImageViewerPanel(CustomImage image) {
			try {
				this.image = new CustomImage(new Point2D(0, 0), width, height, image.getGraphic(width, height), width,
						height);
			} catch (Exception e) {
				this.image = image;
			}
			this.setPreferredSize(new Dimension(this.image.getWidth(), this.image.getHeight()));
		}

		@Override
		public void paintComponent(Graphics g) {
			this.setOpaque(true);
			this.setBackground(Color.LIGHT_GRAY);
			Graphics2D g2d = (Graphics2D) g;
			super.paintComponent(g2d);

			Functions2D.drawImage(g2d, this.image, new Point2D(0, 0), getWidth(), getHeight());

			for (Point2D vertex : selectedPoints) {
				g2d.setColor(Color.red);
				Functions2D.drawPoint(g2d, vertex, new Point2D(0, 0), 4);
				g2d.setColor(Color.black);
				Functions2D.drawPoint(g2d, vertex, new Point2D(0, 0), 6);
				g2d.setColor(Color.white);
				Functions2D.drawPoint(g2d, vertex, new Point2D(0, 0), 8);
			}
		}

		public ArrayList<Color> getColors() {
			ArrayList<Color> colorList = new ArrayList<>();

			for (Point2D vertex : selectedPoints) {
				byte[] rgb = this.image.getRGBVales(vertex.getRoundX(), vertex.getRoundY());
				colorList.add(new Color(Functions2D.getValue(rgb[0]), Functions2D.getValue(rgb[1]),
						Functions2D.getValue(rgb[2])));
			}

			return colorList;
		}
	}
}
