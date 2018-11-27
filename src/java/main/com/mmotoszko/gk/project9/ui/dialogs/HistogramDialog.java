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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.mmotoszko.gk.project9.GraphicsEditor;
import com.mmotoszko.gk.project9.drawing.Functions2D;
import com.mmotoszko.gk.project9.drawing.customshape.BinarizationMode;
import com.mmotoszko.gk.project9.drawing.customshape.CustomImage;
import com.mmotoszko.gk.project9.drawing.customshape.Histogram;
import com.mmotoszko.gk.project9.events.menu.edit.HistogramPreviewRefreshRunner;

public class HistogramDialog extends JOptionPane implements ActionListener, ChangeListener {
	private static final long serialVersionUID = 1L;
	private HistogramPreviewRefreshRunner imageRefresher;
	private JPanel mainPanel;
	private Histogram histogram;
	private boolean stretched = false;
	private boolean equalized = false;
	public boolean plotRefreshLock = false;
	public boolean sliderVisible = true;
	private static int MAXIMUM_SLIDER_VALUE = 256;
	private int focusedTab = 0;
	private int threshold = 122;
	private int sliderValue = 122;
	private JLabel thresholdTextField;
	private JSlider thresholdSlider;
	private boolean showFullHistogramValues = false;
	private BinarizationMode selectedOption = BinarizationMode.MANUAL_SELECTION;

	public HistogramDialog(JPanel parent, GraphicsEditor graphicsEditor, CustomImage image) {
		super();
		mainPanel = getMainPanel();
		this.imageRefresher = new HistogramPreviewRefreshRunner(graphicsEditor, image, this);
		histogram = new Histogram(image);
		Thread imageRefresherThread = new Thread(imageRefresher);
		imageRefresherThread.start();
		this.setMessage(getMessage());
		this.setOptionType(getOptionType());
		this.setOptions(getOptions());
		this.setPreferredSize(new Dimension(600, 400));
	}

	private JPanel getMainPanel() {
		JPanel mainPanel = new JPanel(new BorderLayout());

		mainPanel.add(getHistogramPlotPanel(), BorderLayout.CENTER);
		mainPanel.add(getBottomPanel(), BorderLayout.PAGE_END);

		return mainPanel;
	}

	private JPanel getHistogramPlotPanel() {
		JPanel histogramPlot = new HistogramPlot();

		return histogramPlot;
	}

	private JPanel getBottomPanel() {
		JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));

		bottomPanel.add(getThresholdingPanel(), BorderLayout.CENTER);

		return bottomPanel;
	}

	private JPanel getModifyPanel() {
		JPanel checkboxesPanel = new JPanel();

		JCheckBox stretchCheckBox = new JCheckBox("Stretch");
		JCheckBox equalizeCheckBox = new JCheckBox("Equalize");

		stretchCheckBox.setSelected(stretched);
		equalizeCheckBox.setSelected(equalized);

		stretchCheckBox.addActionListener(this);
		equalizeCheckBox.addActionListener(this);

		stretchCheckBox.setActionCommand("stretch");
		equalizeCheckBox.setActionCommand("equalize");

		checkboxesPanel.add(stretchCheckBox);
		checkboxesPanel.add(equalizeCheckBox);

		return checkboxesPanel;
	}

	private JPanel getThresholdPanel() {
		JPanel thresholdPanel = new JPanel(new BorderLayout(5, 5));

		JComboBox<String> optionsComboBox = new JComboBox<String>(BinarizationMode.getAllValues());
		optionsComboBox.setSelectedIndex(0);
		optionsComboBox.setActionCommand("combo");
		optionsComboBox.addActionListener(this);

		thresholdTextField = new JLabel(BinarizationMode.MANUAL_SELECTION.toString());

		thresholdSlider = new JSlider(0, MAXIMUM_SLIDER_VALUE);
		thresholdSlider.setPaintTicks(false);
		thresholdSlider.setPaintLabels(false);
		thresholdSlider.setSnapToTicks(true);
		thresholdSlider.setMinorTickSpacing(1);
		thresholdSlider.setValue(threshold);
		ChangeListener changeListener = new ChangeListener() {
			public void stateChanged(ChangeEvent changeEvent) {
				JSlider theSlider = (JSlider) changeEvent.getSource();
				sliderValue = new Integer(theSlider.getValue());
				setThreshold();
				updateRefresher();
				mainPanel.repaint();
			}
		};
		thresholdSlider.addChangeListener(changeListener);

		thresholdPanel.add(thresholdSlider, BorderLayout.PAGE_START);
		thresholdPanel.add(optionsComboBox, BorderLayout.LINE_START);
		thresholdPanel.add(thresholdTextField, BorderLayout.CENTER);

		return thresholdPanel;
	}

	private void setThreshold() {
		if (selectedOption == BinarizationMode.MANUAL_SELECTION) {
			histogram.setThreshhold(sliderValue);
			thresholdTextField.setText(sliderValue + "");
			threshold = sliderValue;
		} else if (selectedOption == BinarizationMode.PERCENT_BLACK_SELECTION) {
			histogram.setThreshhold(sliderValue);
			thresholdTextField.setText((int) ((float) sliderValue / MAXIMUM_SLIDER_VALUE * 100) + " %");
			threshold = histogram.setBlackPercentageThreshold();
		}
	}

	private void setThreshold(int value) {
		thresholdTextField.setText(value + "");
		threshold = value;
	}

	private JTabbedPane getThresholdingPanel() {
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setTabPlacement(JTabbedPane.BOTTOM);

		tabbedPane.addTab("Modify Histogram", null, getModifyPanel(), null);
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

		tabbedPane.addTab("Set Binary Threshold", null, getThresholdPanel(), null);
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

		tabbedPane.addChangeListener(this);
		return tabbedPane;
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
		return "Histogram";
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();

		if ("combo".equals(command)) {
			@SuppressWarnings("unchecked")
			JComboBox<String> source = (JComboBox<String>) e.getSource();
			String currentMode = source.getSelectedItem().toString();

			selectedOption = BinarizationMode.stringValueOf(currentMode);
			histogram.setBinarizationMode(selectedOption);

			switch (selectedOption) {
			case MANUAL_SELECTION:
				sliderVisible = true;
				break;
			case PERCENT_BLACK_SELECTION:
				sliderVisible = true;
				break;
			default:
				sliderVisible = false;
			}

			thresholdSlider.setEnabled(sliderVisible);
			setThreshold(histogram.binarize(selectedOption));

		} else if ("stretch".equals(command)) {
			if (stretched) {
				histogram.undoStretch();
			} else {
				histogram.stretch();
			}

			stretched = !stretched;
		} else if ("equalize".equals(command)) {
			if (equalized) {
				histogram.undoEqualize();
			} else {
				histogram.equalize();
			}

			equalized = !equalized;
		}

		updateRefresher();
		mainPanel.repaint();

	}

	private void updateRefresher() {
		plotRefreshLock = true;
		imageRefresher.update();

		while (plotRefreshLock) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e1) {
			}
		}
	}

	public Histogram getHistogram() {
		return histogram;
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		JTabbedPane source = ((JTabbedPane) e.getSource());
		source.requestFocus();
		focusedTab = source.getSelectedIndex();
		if (focusedTab == 1) {
			histogram.setThreshhold(threshold);
			histogram.binarize(selectedOption);
		} else {
			histogram.undoBinarize();
		}
		updateRefresher();
		mainPanel.repaint();
	}

	private class HistogramPlot extends JPanel {
		private static final long serialVersionUID = 1L;

		@Override
		public void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			super.paintComponent(g2d);
			int width = mainPanel.getWidth();
			int height = mainPanel.getHeight() - mainPanel.getComponent(1).getHeight() - 13;

			// Draw background
			g2d.setColor(Color.white);
			g2d.fillRect(0, 0, width, height);

			// Draw spectrum bar
			BufferedImage image = null;
			int[][] imageData = new int[1][];
			imageData[0] = new int[256];
			for (int i = 0; i < 256; i++) {
				imageData[0][i] = Functions2D.getIntRGBPixel(i, i, i);
			}
			image = Functions2D.getScaledImage(imageData, width, 10);
			g2d.drawImage(image, 0, height + 3, this);

			// Draw lines
			g2d.setColor(Color.lightGray);
			int lineScalar = height / 5;
			for (int i = 1; i <= 4; i++) {
				g2d.drawLine(0, lineScalar * i, width, lineScalar * i);
			}

			// Draw histogram
			int[] values = histogram.getValues();
			int maxValue = values.length - 1;
			float indexModifier = maxValue / (float) width;
			float maximumValueModifier = histogram.getMaximumValueCount();
			if(!showFullHistogramValues) {
				maximumValueModifier /= histogram.getMaximumValueCount() / (float)histogram.getAverageValues() / 5;
			}
			float valueModifier = (float) height / maximumValueModifier;
			g2d.setColor(Color.black);
			for (int col = 0; col < width; col++) {
				g2d.drawLine(col, height, col, height - Math.round(
						Math.min(maximumValueModifier, values[Math.round(col * indexModifier)])
								* valueModifier));
			}

			// Draw selection line
			g2d.setColor(Color.red);
			if (focusedTab != 0) {
				int drawX = (int) ((float) threshold / 255 * width);

				if (drawX == width) {
					drawX--;
				}

				g2d.drawLine(drawX, 0, drawX, height);
			}
		}
	}
}
