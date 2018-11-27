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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.mmotoszko.gk.project9.GraphicsEditor;
import com.mmotoszko.gk.project9.drawing.Functions2D;
import com.mmotoszko.gk.project9.drawing.customshape.CustomImage;
import com.mmotoszko.gk.project9.events.menu.edit.ColorPreviewRefreshRunner;
import com.mmotoszko.gk.project9.ui.SliderColorBar;

public class AdjustImageColorDialog extends JOptionPane implements CustomDialog {
	private enum Types {
		R, G, B
	}

	private static final long serialVersionUID = 1L;
	private static int DEFAULT_VALUE = 100;
	private static int MAXIMUM_VALUE = 200;
	private ColorPreviewRefreshRunner imageRefresher;
	private JPanel mainPanel;
	private float r = DEFAULT_VALUE;
	private float g = DEFAULT_VALUE;
	private float b = DEFAULT_VALUE;
	private JTextField rTextField;
	private JTextField gTextField;
	private JTextField bTextField;
	private JSlider rSlider;
	private JSlider gSlider;
	private JSlider bSlider;
	private boolean rAdd = true;
	private boolean gAdd = true;
	private boolean bAdd = true;

	public AdjustImageColorDialog(JPanel parent, GraphicsEditor graphicsEditor, CustomImage image) {
		super();
		mainPanel = getMainPanel();
		this.imageRefresher = new ColorPreviewRefreshRunner(graphicsEditor, image);
		Thread imageRefresherThread = new Thread(imageRefresher);
		imageRefresherThread.start();
	}

	private JPanel getMainPanel() {
		JPanel mainPanel = new JPanel(new GridLayout(0, 1, 0, 15));

		mainPanel.add(getRSlider(this));
		mainPanel.add(getGSlider(this));
		mainPanel.add(getBSlider(this));

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
		return "Color adjustment";
	}

	private JPanel getRSlider(final JOptionPane optionPane) {
		JPanel sliderPanel = new JPanel(new BorderLayout(6, 0));

		Hashtable<Integer, JLabel> sliderLabels = new Hashtable<>();
		sliderLabels.put(0, new JLabel("-100 %"));
		sliderLabels.put(100, new JLabel("+0 %"));
		sliderLabels.put(200, new JLabel("+100 %"));

		rTextField = new JTextField("0");
		rTextField.setPreferredSize(new Dimension(40, 15));
		rTextField.getDocument().addDocumentListener(new SliderDocumentListener());

		rSlider = new JSlider(0, MAXIMUM_VALUE);
		rSlider.setPaintTicks(true);
		rSlider.setPaintLabels(true);
		rSlider.setSnapToTicks(true);
		rSlider.setLabelTable(sliderLabels);
		rSlider.setMinorTickSpacing(1);
		rSlider.setMajorTickSpacing(20);
		rSlider.setValue(DEFAULT_VALUE);
		rSlider.setPreferredSize(new Dimension(200, 40));
		SliderChangeListener changeListener = new SliderChangeListener(rSlider, 0);
		SliderMouseListener mouseListener = new SliderMouseListener(rTextField, Types.R);
		rSlider.addMouseMotionListener(mouseListener);
		rSlider.addMouseListener(mouseListener);
		rSlider.addChangeListener(changeListener);

		SliderColorBar description = new SliderColorBar();
		description.setBackground(Color.gray);
		BufferedImage image = null;
		int[][] imageData = new int[1][];
		imageData[0] = new int[] { Functions2D.getColor(Color.cyan), Functions2D.getColor(Color.white),
				Functions2D.getColor(Color.red) };
		image = Functions2D.getScaledImage(imageData, rSlider.getPreferredSize().width + 42, 10);

		description.setImage(image);

		ButtonGroup group = new ButtonGroup();
		JRadioButton addButton = new JRadioButton("add");
		addButton.setActionCommand("rAdd");
		addButton.addActionListener(new SliderActionListener());
		addButton.setSelected(true);
		JRadioButton multiplyButton = new JRadioButton("mult");
		multiplyButton.setActionCommand("rMultiply");
		multiplyButton.addActionListener(new SliderActionListener());
		group.add(addButton);
		group.add(multiplyButton);

		JPanel leftPanel = new JPanel(new BorderLayout());
		JPanel rightPanel = new JPanel(new BorderLayout());

		leftPanel.add(description, BorderLayout.PAGE_START);
		leftPanel.add(rSlider, BorderLayout.CENTER);

		rightPanel.add(rTextField, BorderLayout.PAGE_START);
		rightPanel.add(addButton, BorderLayout.CENTER);
		rightPanel.add(multiplyButton, BorderLayout.PAGE_END);

		sliderPanel.add(leftPanel, BorderLayout.CENTER);
		sliderPanel.add(rightPanel, BorderLayout.LINE_END);

		return sliderPanel;
	}

	private JPanel getGSlider(final JOptionPane optionPane) {
		JPanel sliderPanel = new JPanel(new BorderLayout(6, 0));

		Hashtable<Integer, JLabel> sliderLabels = new Hashtable<>();
		sliderLabels.put(0, new JLabel("-100 %"));
		sliderLabels.put(100, new JLabel("+0 %"));
		sliderLabels.put(200, new JLabel("+100 %"));

		gTextField = new JTextField("0");
		gTextField.setPreferredSize(new Dimension(40, 15));
		gTextField.getDocument().addDocumentListener(new SliderDocumentListener());

		gSlider = new JSlider(0, MAXIMUM_VALUE);
		gSlider.setPaintTicks(true);
		gSlider.setPaintLabels(true);
		gSlider.setSnapToTicks(true);
		gSlider.setLabelTable(sliderLabels);
		gSlider.setMinorTickSpacing(1);
		gSlider.setMajorTickSpacing(20);
		gSlider.setValue(DEFAULT_VALUE);
		gSlider.setPreferredSize(new Dimension(200, 40));
		SliderChangeListener changeListener = new SliderChangeListener(gSlider, 1);
		SliderMouseListener mouseListener = new SliderMouseListener(gTextField, Types.G);
		gSlider.addMouseMotionListener(mouseListener);
		gSlider.addMouseListener(mouseListener);
		gSlider.addChangeListener(changeListener);

		SliderColorBar description = new SliderColorBar();
		description.setBackground(Color.gray);
		BufferedImage image = null;
		int[][] imageData = new int[1][];
		imageData[0] = new int[] { Functions2D.getColor(Color.magenta), Functions2D.getColor(Color.white),
				Functions2D.getColor(Color.green) };
		image = Functions2D.getScaledImage(imageData, gSlider.getPreferredSize().width + 42, 10);

		description.setImage(image);

		ButtonGroup group = new ButtonGroup();
		JRadioButton addButton = new JRadioButton("add");
		addButton.setActionCommand("gAdd");
		addButton.addActionListener(new SliderActionListener());
		addButton.setSelected(true);
		JRadioButton multiplyButton = new JRadioButton("mult");
		multiplyButton.setActionCommand("gMultiply");
		multiplyButton.addActionListener(new SliderActionListener());
		group.add(addButton);
		group.add(multiplyButton);

		JPanel leftPanel = new JPanel(new BorderLayout());
		JPanel rightPanel = new JPanel(new BorderLayout());

		leftPanel.add(description, BorderLayout.PAGE_START);
		leftPanel.add(gSlider, BorderLayout.CENTER);

		rightPanel.add(gTextField, BorderLayout.PAGE_START);
		rightPanel.add(addButton, BorderLayout.CENTER);
		rightPanel.add(multiplyButton, BorderLayout.PAGE_END);

		sliderPanel.add(leftPanel, BorderLayout.CENTER);
		sliderPanel.add(rightPanel, BorderLayout.LINE_END);

		return sliderPanel;
	}

	private JPanel getBSlider(final JOptionPane optionPane) {
		JPanel sliderPanel = new JPanel(new BorderLayout(6, 0));

		Hashtable<Integer, JLabel> sliderLabels = new Hashtable<>();
		sliderLabels.put(0, new JLabel("-100 %"));
		sliderLabels.put(100, new JLabel("+0 %"));
		sliderLabels.put(200, new JLabel("+100 %"));

		bTextField = new JTextField("0");
		bTextField.setPreferredSize(new Dimension(40, 15));
		bTextField.getDocument().addDocumentListener(new SliderDocumentListener());

		bSlider = new JSlider(0, MAXIMUM_VALUE);
		bSlider.setPaintTicks(true);
		bSlider.setPaintLabels(true);
		bSlider.setSnapToTicks(true);
		bSlider.setLabelTable(sliderLabels);
		bSlider.setMinorTickSpacing(1);
		bSlider.setMajorTickSpacing(20);
		bSlider.setValue(DEFAULT_VALUE);
		bSlider.setPreferredSize(new Dimension(200, 40));
		SliderChangeListener changeListener = new SliderChangeListener(bSlider, 2);
		SliderMouseListener mouseListener = new SliderMouseListener(bTextField, Types.B);
		bSlider.addMouseMotionListener(mouseListener);
		bSlider.addMouseListener(mouseListener);
		bSlider.addChangeListener(changeListener);

		SliderColorBar description = new SliderColorBar();
		description.setBackground(Color.gray);
		BufferedImage image = null;
		int[][] imageData = new int[1][];
		imageData[0] = new int[] { Functions2D.getColor(Color.yellow), Functions2D.getColor(Color.white),
				Functions2D.getColor(Color.blue) };
		image = Functions2D.getScaledImage(imageData, bSlider.getPreferredSize().width + 42, 10);

		description.setImage(image);

		ButtonGroup group = new ButtonGroup();
		JRadioButton addButton = new JRadioButton("add");
		addButton.setActionCommand("bAdd");
		addButton.addActionListener(new SliderActionListener());
		addButton.setSelected(true);
		JRadioButton multiplyButton = new JRadioButton("mult");
		multiplyButton.setActionCommand("bMultiply");
		multiplyButton.addActionListener(new SliderActionListener());
		group.add(addButton);
		group.add(multiplyButton);

		JPanel leftPanel = new JPanel(new BorderLayout());
		JPanel rightPanel = new JPanel(new BorderLayout());

		leftPanel.add(description, BorderLayout.PAGE_START);
		leftPanel.add(bSlider, BorderLayout.CENTER);

		rightPanel.add(bTextField, BorderLayout.PAGE_START);
		rightPanel.add(addButton, BorderLayout.CENTER);
		rightPanel.add(multiplyButton, BorderLayout.PAGE_END);

		sliderPanel.add(leftPanel, BorderLayout.CENTER);
		sliderPanel.add(rightPanel, BorderLayout.LINE_END);

		return sliderPanel;
	}

	protected boolean getAddValueFor(Types type) {
		switch (type) {
		case R:
			return rAdd;
		case G:
			return gAdd;
		case B:
			return bAdd;
		default:
			return false;
		}
	}

	protected float getValueFor(Types type) {
		switch (type) {
		case R:
			return r;
		case G:
			return g;
		case B:
			return b;
		default:
			return 0;
		}
	}

	private class SliderChangeListener implements ChangeListener {
		JSlider slider;
		int color;

		public SliderChangeListener(JSlider slider, int color) {
			this.slider = slider;
			this.color = color;
		}

		@Override
		public void stateChanged(ChangeEvent changeEvent) {
			int value = new Integer(slider.getValue());
			if (color == 0) {
				r = value;
			} else if (color == 1) {
				g = value;
			} else if (color == 2) {
				b = value;
			}
			imageRefresher.update((int) r - 100, (int) g - 100, (int) b - 100);
		}
	}

	private class SliderMouseListener implements MouseListener, MouseMotionListener {
		private Types type;
		private JTextField textField;

		public SliderMouseListener(JTextField textField, Types type) {
			this.textField = textField;
			this.type = type;
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (getAddValueFor(type)) {
				textField.setText("" + Math.round((getValueFor(type) - 100) / 100 * 255));
			} else {
				textField.setText("" + (getValueFor(type) / 100));
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if (getAddValueFor(type)) {
				textField.setText("" + Math.round((getValueFor(type) - 100) / 100 * 255));
			} else {
				textField.setText("" + (getValueFor(type) / 100));
			}
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
		}
	}

	private class SliderDocumentListener implements DocumentListener {
		@Override
		public void changedUpdate(DocumentEvent e) {

			try {
				float tempValue = Float.parseFloat(rTextField.getText());
				if (rAdd) {
					tempValue = Math.min(255, Math.max(tempValue, -255));
					r = Math.round(tempValue / 255 * 100) + 100;
				} else {
					tempValue = Math.min(2, Math.max(tempValue, 0));
					r = (tempValue - 1) * 100 + 100;
				}
				updateSliderValue(rSlider, r);
			} catch (NumberFormatException e1) {
				r = 100;
				updateSliderValue(rSlider, 100);
			}

			try {
				float tempValue = Float.parseFloat(gTextField.getText());
				if (gAdd) {
					tempValue = Math.min(255, Math.max(tempValue, -255));
					g = Math.round(tempValue / 255 * 100) + 100;
				} else {
					tempValue = Math.min(2, Math.max(tempValue, 0));
					g = (tempValue - 1) * 100 + 100;
				}
				updateSliderValue(gSlider, g);
			} catch (NumberFormatException e1) {
				g = 100;
				updateSliderValue(gSlider, 100);
			}

			try {
				float tempValue = Float.parseFloat(bTextField.getText());
				if (bAdd) {
					tempValue = Math.min(255, Math.max(tempValue, -255));
					b = Math.round(tempValue / 255 * 100) + 100;
				} else {
					tempValue = Math.min(2, Math.max(tempValue, 0));
					b = (tempValue - 1) * 100 + 100;
				}
				updateSliderValue(bSlider, b);
			} catch (NumberFormatException e1) {
				b = 100;
				updateSliderValue(bSlider, 100);
			}
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			changedUpdate(e);

		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			changedUpdate(e);

		}
	}

	public void updateSliderValue(JSlider slider, float newValue) {
		slider.setValue((int) newValue);
	}

	private class SliderActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			JRadioButton source = (JRadioButton) e.getSource();
			String command = source.getActionCommand();

			if ("rAdd".equals(command)) {
				rAdd = true;
			} else if ("rMultiply".equals(command)) {
				rAdd = false;
			} else if ("gAdd".equals(command)) {
				gAdd = true;
			} else if ("gMultiply".equals(command)) {
				gAdd = false;
			} else if ("bAdd".equals(command)) {
				bAdd = true;
			} else if ("bMultiply".equals(command)) {
				bAdd = false;
			}
		}
	}

	@Override
	public JDialog createDialog(JComponent component, String title) {
		return super.createDialog(component, title);
	}
}
