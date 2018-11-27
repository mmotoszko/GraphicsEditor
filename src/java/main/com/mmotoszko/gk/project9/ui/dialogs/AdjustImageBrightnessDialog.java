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
import com.mmotoszko.gk.project9.events.menu.edit.BrightnessPreviewRefreshRunner;
import com.mmotoszko.gk.project9.ui.SliderColorBar;

public class AdjustImageBrightnessDialog extends JOptionPane
		implements CustomDialog, ActionListener, DocumentListener, MouseListener, MouseMotionListener {
	private static final long serialVersionUID = 1L;
	private static int DEFAULT_VALUE = 100;
	private static int MAXIMUM_VALUE = 200;
	private BrightnessPreviewRefreshRunner imageRefresher;
	private JPanel mainPanel;
	private float value = DEFAULT_VALUE;
	private boolean add = true;
	private JTextField brightnessTextField;
	private JSlider brightnessSlider;

	public AdjustImageBrightnessDialog(JPanel parent, GraphicsEditor graphicsEditor, CustomImage image) {
		super();
		mainPanel = new JPanel();
		mainPanel.add(getSlider(this));
		this.imageRefresher = new BrightnessPreviewRefreshRunner(graphicsEditor, image);
		Thread imageRefresherThread = new Thread(imageRefresher);
		imageRefresherThread.start();
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
		return "Brightness";
	}

	private JPanel getSlider(final JOptionPane optionPane) {
		JPanel sliderPanel = new JPanel(new BorderLayout(6, 0));

		brightnessSlider = new JSlider(0, MAXIMUM_VALUE);
		brightnessSlider.setPaintTicks(true);
		brightnessSlider.setPaintLabels(true);
		brightnessSlider.setSnapToTicks(true);
		Hashtable<Integer, JLabel> sliderLabels = new Hashtable<>();
		sliderLabels.put(0, new JLabel("-100 %"));
		sliderLabels.put(100, new JLabel("+0 %"));
		sliderLabels.put(200, new JLabel("+100 %"));
		brightnessSlider.setLabelTable(sliderLabels);
		brightnessSlider.setMinorTickSpacing(1);
		brightnessSlider.setMajorTickSpacing(20);
		brightnessSlider.setValue(DEFAULT_VALUE);
		ChangeListener changeListener = new ChangeListener() {
			public void stateChanged(ChangeEvent changeEvent) {
				JSlider theSlider = (JSlider) changeEvent.getSource();
				value = new Integer(theSlider.getValue());
				optionPane.setInputValue(value);
				imageRefresher.update(value - 100);
			}
		};
		brightnessSlider.addMouseMotionListener(this);
		brightnessSlider.addMouseListener(this);
		brightnessSlider.addChangeListener(changeListener);

		SliderColorBar description = new SliderColorBar();
		description.setBackground(Color.gray);
		BufferedImage image = null;
		int[][] imageData = new int[1][];
		imageData[0] = new int[] { Functions2D.getColor(Color.black), Functions2D.getColor(Color.gray),
				Functions2D.getColor(Color.white) };
		image = Functions2D.getScaledImage(imageData, brightnessSlider.getPreferredSize().width + 42, 10);
		description.setImage(image);

		ButtonGroup group = new ButtonGroup();
		JRadioButton addButton = new JRadioButton("add");
		addButton.setActionCommand("add");
		addButton.addActionListener(this);
		addButton.setSelected(true);
		JRadioButton multiplyButton = new JRadioButton("mult");
		multiplyButton.setActionCommand("multiply");
		multiplyButton.addActionListener(this);
		group.add(addButton);
		group.add(multiplyButton);

		JPanel leftPanel = new JPanel(new BorderLayout());
		JPanel rightPanel = new JPanel(new BorderLayout());

		brightnessTextField = new JTextField("0");
		brightnessTextField.setPreferredSize(new Dimension(40, 15));
		brightnessTextField.getDocument().addDocumentListener(this);

		leftPanel.add(description, BorderLayout.PAGE_START);
		leftPanel.add(brightnessSlider, BorderLayout.CENTER);

		rightPanel.add(brightnessTextField, BorderLayout.PAGE_START);
		rightPanel.add(addButton, BorderLayout.CENTER);
		rightPanel.add(multiplyButton, BorderLayout.PAGE_END);

		sliderPanel.add(leftPanel, BorderLayout.CENTER);
		sliderPanel.add(rightPanel, BorderLayout.LINE_END);

		return sliderPanel;
	}

	public void updateSliderValue(JSlider slider, float newValue) {
		slider.setValue((int) newValue);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JRadioButton source = (JRadioButton) e.getSource();
		String command = source.getActionCommand();

		if ("add".equals(command)) {
			add = true;
		} else if ("multiply".equals(command)) {
			add = false;
		}
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		try {
			float tempValue = Float.parseFloat(brightnessTextField.getText());
			if (add) {
				tempValue = Math.min(255, Math.max(tempValue, -255));
				value = Math.round(tempValue / 255 * 100) + 100;
			} else {
				tempValue = Math.min(2, Math.max(tempValue, 0));
				value = (tempValue - 1) * 100 + 100;
			}
			updateSliderValue(brightnessSlider, value);
		} catch (NumberFormatException e1) {
			value = 100;
			updateSliderValue(brightnessSlider, 100);
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

	@Override
	public void mouseDragged(MouseEvent e) {
		if (add) {
			brightnessTextField.setText("" + Math.round((value - 100) / 100 * 255));
		} else {
			brightnessTextField.setText("" + (value / 100));
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (add) {
			brightnessTextField.setText("" + Math.round((value - 100) / 100 * 255));
		} else {
			brightnessTextField.setText("" + (value / 100));
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

	@Override
	public JDialog createDialog(JComponent component, String title) {
		return super.createDialog(component, title);
	}
}
