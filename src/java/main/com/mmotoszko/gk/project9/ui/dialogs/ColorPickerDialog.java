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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.mmotoszko.gk.project9.drawing.Functions2D;
import com.mmotoszko.gk.project9.drawing.Point2D;

public class ColorPickerDialog extends JOptionPane implements ActionListener, DocumentListener, FocusListener,
		ChangeListener, MouseListener, MouseMotionListener, WindowListener {
	private static final long serialVersionUID = 1L;

	private static final int COLOR_BAR_WIDTH = 510;
	private static final int COLOR_BAR_HEIGHT = 20;
	private static final int COLOR_BAR_MAX_INDEX = 1530;
	private static final int COLOR_CANVAS_HEIGHT = 200;
	private static final int MAX_RGB_VALUE = 255;
	private static final float MAX_CMYK_VALUE = 1;
	private int r = 0;
	private int g = 0;
	private int b = 0;
	private int colorBarR = 255;
	private int colorBarG = 0;
	private int colorBarB = 0;
	private float c = 0;
	private float m = 0;
	private float y = 0;
	private float k = 0;
	private boolean colorCanvasDirty = true;
	private boolean colorBarDirty = true;
	private boolean handPickingColor = false;
	private BufferedImage colorCanvasImage;
	private BufferedImage colorBarImage;
	private JPanel colorPreview = new JPanel();
	private ColorCanvas colorCanvas = new ColorCanvas();
	private ColorBar colorBar = new ColorBar();
	private boolean changedRGB = false;
	private boolean changedCMYK = false;
	private JComponent mainPanel;
	private int colorBarIndex = 0;
	private Point2D colorCanvasPointerLastKnownPosition = new Point2D(COLOR_BAR_WIDTH - 1, 0);
	private Point2D colorCanvasPointer = new Point2D(COLOR_BAR_WIDTH - 1, 0);
	private boolean changeReuquired = true;
	private int focusedTab = 0;
	private JTextField textFieldR, textFieldG, textFieldB, textFieldC, textFieldM, textFieldY, textFieldK;

	public ColorPickerDialog(JPanel parent, Color color) {
		super();
		this.addMouseListener(this);
		getRootFrame().addWindowListener(this);
		r = color.getRed();
		g = color.getGreen();
		b = color.getBlue();
		colorBarR = r;
		colorBarG = g;
		colorBarB = b;
		parseCMYKFromRGB();
		initHandPickedValues(r, g, b);
		mainPanel = getMainPanel();
		mainPanel.setFocusable(true);
		mainPanel.addMouseListener(this);
		mainPanel.addFocusListener(this);
	}

	private void setColorBarIndex(int index) {
		colorBarIndex = index;
		colorCanvasDirty = true;
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
		return "Color";
	}

	private void initHandPickedValues(int r, int g, int b) {
		float drainModifier = Math.max(r, Math.max(g, b)) / (float) MAX_RGB_VALUE;
		int R = Math.round(r / drainModifier);
		int G = Math.round(g / drainModifier);
		int B = Math.round(b / drainModifier);

		if (R >= G && G == B) {
			G = 0;
			B = 0;
		} else if (G >= B && B == R) {
			R = 0;
			B = 0;
		} else if (B >= G && G == R) {
			R = 0;
			G = 0;
		} else if (R <= G && R <= B) {
			R = 0;
		} else if (G <= R && G <= B) {
			G = 0;
		} else if (B <= G && B <= R) {
			B = 0;
		}

		colorBarIndex = getColorBarIndex(R, G, B);

		int canvasY = COLOR_CANVAS_HEIGHT
				- Math.round((Math.max(r, Math.max(g, b)) / (float) MAX_RGB_VALUE) * (float) COLOR_CANVAS_HEIGHT);
		int canvasX = COLOR_BAR_WIDTH - Math
				.round((Math.min(r, Math.min(g, b)) / (float) Math.max(r, Math.max(g, b))) * (float) COLOR_BAR_WIDTH);
		if (canvasY == COLOR_CANVAS_HEIGHT) {
			canvasY--;
		}
		if (canvasX == COLOR_BAR_WIDTH) {
			canvasX--;
		}
		colorCanvasPointer.setLocation(canvasX, canvasY);
		colorCanvasPointerLastKnownPosition.setLocation(canvasX, canvasY);
		updateColorBarImage();
		updateColorCanvasImage();
	}

	private void updateColorBarImage() {
		if (colorBarDirty) {
			BufferedImage img = new BufferedImage(COLOR_BAR_WIDTH, COLOR_BAR_HEIGHT, BufferedImage.TYPE_INT_RGB);

			for (int row = 0; row < COLOR_BAR_HEIGHT; row++) {
				for (int col = 0; col < COLOR_BAR_WIDTH; col++) {
					int[] currentPixelRGB = getColorBarValues(
							col * (int) (COLOR_BAR_MAX_INDEX / (float) COLOR_BAR_WIDTH));
					int currentPixel = Functions2D.getIntRGBPixel((byte) currentPixelRGB[0], (byte) currentPixelRGB[1],
							(byte) currentPixelRGB[2]);
					img.setRGB(col, row, currentPixel);
				}
			}

			colorBarImage = img;
		}
		colorBar.revalidate();
		colorBar.repaint();
		colorBarDirty = false;
	}

	private void updateColorCanvasImage() {
		if (colorCanvasDirty) {
			BufferedImage img = new BufferedImage(COLOR_BAR_WIDTH, COLOR_CANVAS_HEIGHT, BufferedImage.TYPE_INT_RGB);

			int[] currentSpectrumRGB = getColorBarValues(colorBarIndex);
			int[] currentPixelRGB = new int[3];

			for (int row = 0; row < COLOR_CANVAS_HEIGHT; row++) {
				for (int col = 0; col < COLOR_BAR_WIDTH; col++) {
					float fillModifier = 1 - col / (float) COLOR_BAR_WIDTH;
					float drainModifier = 1 - row / (float) COLOR_CANVAS_HEIGHT;
					currentPixelRGB[0] = Math.round(
							(currentSpectrumRGB[0] + (255 - currentSpectrumRGB[0]) * fillModifier) * drainModifier);
					currentPixelRGB[1] = Math.round(
							(currentSpectrumRGB[1] + (255 - currentSpectrumRGB[1]) * fillModifier) * drainModifier);
					currentPixelRGB[2] = Math.round(
							(currentSpectrumRGB[2] + (255 - currentSpectrumRGB[2]) * fillModifier) * drainModifier);
					int currentPixel = Functions2D.getIntRGBPixel((byte) currentPixelRGB[0], (byte) currentPixelRGB[1],
							(byte) currentPixelRGB[2]);
					img.setRGB(col, row, currentPixel);
				}
			}

			colorCanvasImage = img;
		}
		colorBar.revalidate();
		colorBar.repaint();
		colorCanvasDirty = false;
	}

	private int getColorInt() {
		return Functions2D.getIntRGBPixel((byte) r, (byte) g, (byte) b);
	}

	public Color getColor() {
		return new Color(getColorInt());
	}

	private void parseCMYKFromRGB() {
		if (r == 0 && g == 0 && b == 0) {
			c = 0;
			m = 0;
			y = 0;
			k = 1;
		} else {
			float R = (float) r / MAX_RGB_VALUE;
			float G = (float) g / MAX_RGB_VALUE;
			float B = (float) b / MAX_RGB_VALUE;
			k = MAX_CMYK_VALUE - Math.max(R, Math.max(G, B));
			c = (MAX_CMYK_VALUE - R - k) / (MAX_CMYK_VALUE - k);
			m = (MAX_CMYK_VALUE - G - k) / (MAX_CMYK_VALUE - k);
			y = (MAX_CMYK_VALUE - B - k) / (MAX_CMYK_VALUE - k);
		}
	}

	private void parseRGBFromCMYK() {

		r = Math.round(MAX_RGB_VALUE * (MAX_CMYK_VALUE - c) * (MAX_CMYK_VALUE - k));
		g = Math.round(MAX_RGB_VALUE * (MAX_CMYK_VALUE - m) * (MAX_CMYK_VALUE - k));
		b = Math.round(MAX_RGB_VALUE * (MAX_CMYK_VALUE - y) * (MAX_CMYK_VALUE - k));
	}

	private void setPreviewColor() {
		colorPreview.setBackground(new Color(r, g, b));
	}

	private JComponent getMainPanel() {
		JPanel mainPanel = new JPanel(new BorderLayout(6, 6));

		mainPanel.add(getLeftPanel(), BorderLayout.CENTER);
		mainPanel.add(getRightPanel(), BorderLayout.LINE_END);
		setPreviewColor();

		return mainPanel;
	}

	private JPanel getRightPanel() {
		JPanel newRightPanel = new JPanel(new BorderLayout(6, 6));

		colorPreview.setPreferredSize(new Dimension(100, 100));
		colorPreview.setName("colorPreview");
		colorPreview.setFocusable(true);
		colorPreview.addMouseListener(this);
		newRightPanel.add(colorPreview, BorderLayout.PAGE_START);
		JTabbedPane tabbedPane = new JTabbedPane();

		tabbedPane.addTab("RGB", null, getRGBTab(), null);
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

		tabbedPane.addTab("CMYK", null, getCMYKTab(), null);
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

		setPreviewColor();
		tabbedPane.addChangeListener(this);
		newRightPanel.add(tabbedPane, BorderLayout.CENTER);

		return newRightPanel;
	}

	private JPanel getRGBTab() {
		JPanel valuesPanel = new JPanel();
		valuesPanel.setLayout(new BorderLayout(0, 6));
		valuesPanel.addMouseListener(this);
		JPanel individualValuesPanel = new JPanel();
		individualValuesPanel.setLayout(new GridLayout(0, 2));

		JLabel l1 = new JLabel("Red");
		JLabel l2 = new JLabel("Green");
		JLabel l3 = new JLabel("Blue");
		textFieldR = new JTextField(r + "");
		textFieldG = new JTextField(g + "");
		textFieldB = new JTextField(b + "");
		textFieldR.addFocusListener(this);
		textFieldG.addFocusListener(this);
		textFieldB.addFocusListener(this);
		textFieldR.getDocument().addDocumentListener(this);
		textFieldG.getDocument().addDocumentListener(this);
		textFieldB.getDocument().addDocumentListener(this);
		individualValuesPanel.add(l1);
		individualValuesPanel.add(textFieldR);
		individualValuesPanel.add(l2);
		individualValuesPanel.add(textFieldG);
		individualValuesPanel.add(l3);
		individualValuesPanel.add(textFieldB);

		valuesPanel.add(individualValuesPanel, BorderLayout.PAGE_START);
		valuesPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

		return valuesPanel;
	}

	private JPanel getCMYKTab() {
		JPanel valuesPanel = new JPanel();
		valuesPanel.setLayout(new BorderLayout(0, 6));
		valuesPanel.addMouseListener(this);
		JPanel individualValuesPanel = new JPanel();
		individualValuesPanel.setLayout(new GridLayout(0, 2));

		JLabel l1 = new JLabel("Cyan");
		JLabel l2 = new JLabel("Magenta");
		JLabel l3 = new JLabel("Yellow");
		JLabel l4 = new JLabel("Black");
		textFieldC = new JTextField(getPercentageText(c));
		textFieldM = new JTextField(getPercentageText(m));
		textFieldY = new JTextField(getPercentageText(y));
		textFieldK = new JTextField(getPercentageText(k));
		textFieldC.addFocusListener(this);
		textFieldM.addFocusListener(this);
		textFieldY.addFocusListener(this);
		textFieldK.addFocusListener(this);
		textFieldC.getDocument().addDocumentListener(this);
		textFieldM.getDocument().addDocumentListener(this);
		textFieldY.getDocument().addDocumentListener(this);
		textFieldK.getDocument().addDocumentListener(this);
		individualValuesPanel.add(l1);
		individualValuesPanel.add(textFieldC);
		individualValuesPanel.add(l2);
		individualValuesPanel.add(textFieldM);
		individualValuesPanel.add(l3);
		individualValuesPanel.add(textFieldY);
		individualValuesPanel.add(l4);
		individualValuesPanel.add(textFieldK);

		valuesPanel.add(individualValuesPanel, BorderLayout.PAGE_START);
		valuesPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

		return valuesPanel;

	}

	private JPanel getLeftPanel() {
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BorderLayout(0, 6));

		colorCanvas.setPreferredSize(new Dimension(COLOR_BAR_WIDTH, COLOR_CANVAS_HEIGHT));
		colorCanvas.setBackground(Color.LIGHT_GRAY);

		colorBar.setPreferredSize(new Dimension(COLOR_BAR_WIDTH, COLOR_BAR_HEIGHT));
		colorBar.setBackground(Color.LIGHT_GRAY);

		colorCanvas.addMouseListener(this);
		colorCanvas.addMouseMotionListener(this);
		colorCanvas.setName("colorCanvas");
		colorBar.addMouseListener(this);
		colorBar.addMouseMotionListener(this);
		colorBar.setName("colorBar");
		leftPanel.add(colorCanvas, BorderLayout.PAGE_START);
		leftPanel.add(colorBar, BorderLayout.PAGE_END);

		return leftPanel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		if (changeReuquired) {
			if (focusedTab == 0) {
				try {
					int newR = Integer.parseInt(textFieldR.getText());
					changedRGB = newR != r || changedRGB;
					r = newR;
					if (r < 0) {
						r = 0;
					} else if (r > MAX_RGB_VALUE) {
						r = MAX_RGB_VALUE;
					}
				} catch (NumberFormatException eR) {
					r = 0;
					changedRGB = true;
				}

				try {
					int newG = Integer.parseInt(textFieldG.getText());
					changedRGB = newG != g || changedRGB;
					g = newG;
					if (g < 0) {
						g = 0;
					} else if (g > MAX_RGB_VALUE) {
						g = MAX_RGB_VALUE;
					}
				} catch (NumberFormatException eG) {
					g = 0;
					changedRGB = true;
				}

				try {
					int newB = Integer.parseInt(textFieldB.getText());
					changedRGB = newB != b || changedRGB;
					b = newB;
					if (b < 0) {
						b = 0;
					} else if (b > MAX_RGB_VALUE) {
						b = MAX_RGB_VALUE;
					}
				} catch (NumberFormatException eB) {
					b = 0;
					changedRGB = true;
				}

				if (changedRGB) {
					System.out.println("Recalculating cmyk");
					colorCanvasDirty = true;
					colorBarDirty = true;
					changedRGB = false;
					parseCMYKFromRGB();
				}

			} else if (focusedTab == 1) {
				try {
					float newC = getFloatFromPercentageText(textFieldC.getText());
					changedCMYK = newC != c || changedCMYK;
					c = newC;
					if (c < 0) {
						c = 0;
					} else if (c > MAX_CMYK_VALUE) {
						c = MAX_CMYK_VALUE;
					}
				} catch (NumberFormatException eC) {
					c = 0;
					changedCMYK = true;
				}

				try {
					float newM = getFloatFromPercentageText(textFieldM.getText());
					changedCMYK = newM != m || changedCMYK;
					m = newM;
					if (m < 0) {
						m = 0;
					} else if (m > MAX_CMYK_VALUE) {
						m = MAX_CMYK_VALUE;
					}
				} catch (NumberFormatException eM) {
					m = 0;
					changedCMYK = true;
				}

				try {
					float newY = getFloatFromPercentageText(textFieldY.getText());
					changedCMYK = newY != y || changedCMYK;
					y = newY;
					if (y < 0) {
						y = 0;
					} else if (y > MAX_CMYK_VALUE) {
						y = MAX_CMYK_VALUE;
					}
				} catch (NumberFormatException eY) {
					y = 0;
					changedCMYK = true;
				}

				try {
					float newK = getFloatFromPercentageText(textFieldK.getText());
					changedCMYK = newK != k || changedCMYK;
					k = newK;
					if (k < 0) {
						k = 0;
					} else if (k > MAX_CMYK_VALUE) {
						k = MAX_CMYK_VALUE;
					}
				} catch (NumberFormatException eK) {
					k = 0;
					changedCMYK = true;
				}

				if (changedCMYK) {
					System.out.println("Recalculating rgb");
					colorCanvasDirty = true;
					colorBarDirty = true;
					changedCMYK = false;
					parseRGBFromCMYK();
				}
			}
		}

		if (!handPickingColor) {
			initHandPickedValues(r, g, b);
		}
		colorBar.repaint();
		colorCanvas.repaint();
		setPreviewColor();
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		changedUpdate(e);
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		changedUpdate(e);
	}

	private String getPercentageText(float value) {
		value = value * 100;
		String valueString = String.valueOf(value);
		return valueString.split("[.]")[0];
	}

	private float getFloatFromPercentageText(String text) throws NumberFormatException {
		float value = Float.parseFloat(text);
		return value / 100;
	}

	@Override
	public void focusGained(FocusEvent e) {
		// TODO Auto-generated method stub

	}

	private void updateTextFields() {
		focusLost(new FocusEvent(mainPanel, FocusEvent.FOCUS_GAINED));
	}

	@Override
	public void focusLost(FocusEvent e) {
		changeReuquired = false;
		if (!textFieldR.getText().equals(r + "")) {
			textFieldR.setText(r + "");
		}
		if (!textFieldG.getText().equals(g + "")) {
			textFieldG.setText(g + "");
		}
		if (!textFieldB.getText().equals(b + "")) {
			textFieldB.setText(b + "");
		}
		if (!textFieldC.getText().equals(getPercentageText(c))) {
			textFieldC.setText(getPercentageText(c));
		}
		if (!textFieldM.getText().equals(getPercentageText(m))) {
			textFieldM.setText(getPercentageText(m));
		}
		if (!textFieldY.getText().equals(getPercentageText(y))) {
			textFieldY.setText(getPercentageText(y));
		}
		if (!textFieldK.getText().equals(getPercentageText(k))) {
			textFieldK.setText(getPercentageText(k));
		}
		changeReuquired = true;
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		JTabbedPane source = ((JTabbedPane) e.getSource());
		source.requestFocus();
		focusedTab = source.getSelectedIndex();

		updateTextFields();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		JPanel source = (JPanel) e.getSource();
		source.requestFocus();

		if ("colorCanvas".equals(source.getName())) {
			handPickingColor = true;
			try {
				colorCanvasPointerLastKnownPosition.setLocation(
						Math.min(colorCanvasPointer.getX(), COLOR_BAR_WIDTH - 1),
						Math.min(colorCanvasPointer.getY(), COLOR_BAR_WIDTH - 1));
			} catch (NullPointerException e1) {

			}
			colorCanvasPointer = new Point2D(source.getMousePosition());
			calculateHandPickedValues();

			r = colorBarR;
			g = colorBarG;
			b = colorBarB;
			parseCMYKFromRGB();
			updateTextFields();
			colorBar.repaint();
			colorCanvas.repaint();

		} else if ("colorBar".equals(source.getName())) {
			handPickingColor = true;
			try {
				setColorBarIndex(source.getMousePosition().x * (COLOR_BAR_MAX_INDEX / COLOR_BAR_WIDTH));
			} catch (NullPointerException e1) {

			}
			calculateHandPickedValues();

			r = colorBarR;
			g = colorBarG;
			b = colorBarB;
			parseCMYKFromRGB();
			updateTextFields();
			updateColorCanvasImage();
			colorBar.repaint();
			colorCanvas.repaint();
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		JPanel source = (JPanel) e.getSource();
		source.requestFocus();

		if ("colorCanvas".equals(source.getName())) {
			handPickingColor = true;
			try {
				colorCanvasPointerLastKnownPosition.setLocation(
						Math.min(colorCanvasPointer.getX(), COLOR_BAR_WIDTH - 1),
						Math.min(colorCanvasPointer.getY(), COLOR_BAR_WIDTH - 1));
			} catch (NullPointerException e1) {

			}
			colorCanvasPointer = new Point2D(source.getMousePosition());
			calculateHandPickedValues();

			r = colorBarR;
			g = colorBarG;
			b = colorBarB;
			parseCMYKFromRGB();
			updateTextFields();
			updateColorBarImage();
			colorBar.repaint();
			colorCanvas.repaint();

		} else if ("colorBar".equals(source.getName())) {
			handPickingColor = true;
			try {
				setColorBarIndex(source.getMousePosition().x * (COLOR_BAR_MAX_INDEX / COLOR_BAR_WIDTH));
			} catch (NullPointerException e1) {

			}
			calculateHandPickedValues();

			r = colorBarR;
			g = colorBarG;
			b = colorBarB;
			parseCMYKFromRGB();
			updateTextFields();
			updateColorBarImage();
			updateColorCanvasImage();
			colorBar.repaint();
			colorCanvas.repaint();
		}
	}

	// 255 0 0 - 0
	// 255 255 0 - 255
	// 0 255 0 - 510
	// 0 255 255 - 765
	// 0 0 255 - 1020
	// 255 0 255 - 1275
	// 255 0 0 - 1530
	private void calculateHandPickedValues() {
		int canvasPointerX = colorCanvasPointerLastKnownPosition.getRoundX();
		int canvasPointerY = colorCanvasPointerLastKnownPosition.getRoundY();

		try {
			canvasPointerX = colorCanvasPointer.getRoundX();
			canvasPointerY = colorCanvasPointer.getRoundY();
		} catch (NullPointerException e) {
		}

		float fillModifier = 1 - canvasPointerX / (float) COLOR_BAR_WIDTH;
		float drainModifier = 1 - canvasPointerY / (float) COLOR_CANVAS_HEIGHT;
		if (colorBarIndex >= 0 && colorBarIndex <= 255) {
			colorBarR = Math.round(255 * drainModifier);
			colorBarG = Math.round((colorBarIndex + (fillModifier * (255 - colorBarIndex))) * drainModifier);
			colorBarB = Math.round(fillModifier * 255 * drainModifier);
		} else if (colorBarIndex >= 255 && colorBarIndex < 510) {
			colorBarR = Math
					.round(((510 - colorBarIndex) + (fillModifier * (255 - (510 - colorBarIndex)))) * drainModifier);
			colorBarG = Math.round(255 * drainModifier);
			colorBarB = Math.round(fillModifier * 255 * drainModifier);
		} else if (colorBarIndex >= 510 && colorBarIndex < 765) {
			colorBarR = Math.round(fillModifier * 255 * drainModifier);
			colorBarG = Math.round(255 * drainModifier);
			colorBarB = Math
					.round(((colorBarIndex - 510) + (fillModifier * (255 - (colorBarIndex - 510)))) * drainModifier);
		} else if (colorBarIndex >= 765 && colorBarIndex < 1020) {
			colorBarR = Math.round(fillModifier * 255 * drainModifier);
			colorBarG = Math
					.round(((1020 - colorBarIndex) + (fillModifier * (255 - (1020 - colorBarIndex)))) * drainModifier);
			colorBarB = Math.round(255 * drainModifier);
		} else if (colorBarIndex >= 1020 && colorBarIndex < 1275) {
			colorBarR = Math
					.round(((colorBarIndex - 1020) + (fillModifier * (255 - (colorBarIndex - 1020)))) * drainModifier);
			colorBarG = Math.round(fillModifier * 255 * drainModifier);
			colorBarB = Math.round(255 * drainModifier);
		} else if (colorBarIndex >= 1275 && colorBarIndex <= COLOR_BAR_MAX_INDEX) {
			colorBarR = Math.round(255 * drainModifier);
			colorBarG = Math.round(fillModifier * 255 * drainModifier);
			colorBarB = Math.round(((COLOR_BAR_MAX_INDEX - colorBarIndex)
					+ (fillModifier * (255 - (COLOR_BAR_MAX_INDEX - colorBarIndex)))) * drainModifier);
		} else {
			System.out.println("Color bar index out of bounds");
		}
	}

	private int[] getColorBarValues(int index) {
		int colorBarR;
		int colorBarG;
		int colorBarB;
		if (index >= 0 && index <= 255) {
			colorBarR = 255;
			colorBarG = index;
			colorBarB = 0;
		} else if (index >= 255 && index <= 510) {
			colorBarR = 510 - index;
			colorBarG = 255;
			colorBarB = 0;
		} else if (index >= 510 && index <= 765) {
			colorBarR = 0;
			colorBarG = 255;
			colorBarB = index - 510;
		} else if (index >= 765 && index <= 1020) {
			colorBarR = 0;
			colorBarG = 1020 - index;
			colorBarB = 255;
		} else if (index >= 1020 && index <= 1275) {
			colorBarR = index - 1020;
			colorBarG = 0;
			colorBarB = 255;
		} else if (index >= 1275 && index <= COLOR_BAR_MAX_INDEX) {
			colorBarR = 255;
			colorBarG = 0;
			colorBarB = COLOR_BAR_MAX_INDEX - index;
		} else {
			colorBarR = 0;
			colorBarG = 0;
			colorBarB = 0;
			System.out.println("Color bar index out of bounds");
		}

		return new int[] { colorBarR, colorBarG, colorBarB };
	}

	private int getColorBarIndex(int r, int g, int b) {
		int index = 0;

		if (r == 255 && b == 0) {
			index = g;
		} else if (g == 255 && b == 0) {
			index = 510 - r;
		} else if (r == 0 && g == 255) {
			index = 510 + b;
		} else if (r == 0 && b == 255) {
			index = 1020 - g;
		} else if (g == 0 && b == 255) {
			index = 1020 + r;
		} else if (r == 255 && g == 0) {
			index = 1530 - b;
		} else {
			index = 0;
			System.out.println("Color bar index out of bounds");
		}

		return index;
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

	@Override
	public void mouseReleased(MouseEvent e) {
		handPickingColor = false;
	}

	private class ColorCanvas extends JPanel {
		private static final long serialVersionUID = 1L;

		@Override
		public void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			super.paintComponent(g2d);
			g2d.drawImage(colorCanvasImage, 0, 0, this);

			try {
				g2d.setColor(Color.BLACK);
				g2d.drawOval(colorCanvasPointer.getRoundX() - 3, colorCanvasPointer.getRoundY() - 3, 6, 6);
				g2d.setColor(Color.WHITE);
				g2d.drawOval(colorCanvasPointer.getRoundX() - 4, colorCanvasPointer.getRoundY() - 4, 8, 8);
			} catch (NullPointerException e) {
				g2d.setColor(Color.BLACK);
				g2d.drawOval(colorCanvasPointerLastKnownPosition.getRoundX() - 3,
						colorCanvasPointerLastKnownPosition.getRoundY() - 3, 6, 6);
				g2d.setColor(Color.WHITE);
				g2d.drawOval(colorCanvasPointerLastKnownPosition.getRoundX() - 4,
						colorCanvasPointerLastKnownPosition.getRoundY() - 4, 8, 8);
			}
		}
	}

	private class ColorBar extends JPanel {
		private static final long serialVersionUID = 1L;

		@Override
		public void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			super.paintComponent(g2d);
			g2d.drawImage(colorBarImage, 0, 0, this);

			g2d.setColor(Color.BLACK);
			int lineX = Math.round(colorBarIndex / (float) (COLOR_BAR_MAX_INDEX / (float) COLOR_BAR_WIDTH));
			if (lineX == COLOR_BAR_WIDTH) {
				lineX--;
			}
			g2d.drawLine(lineX, 0, lineX, COLOR_BAR_HEIGHT);
			g2d.setColor(Color.WHITE);
			g2d.drawLine(lineX - 1, 0, lineX - 1, COLOR_BAR_HEIGHT);
			g2d.drawLine(lineX + 1, 0, lineX + 1, COLOR_BAR_HEIGHT);
		}
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		System.out.println("windowActivated");
		updateColorBarImage();
		updateColorCanvasImage();
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}
}
