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

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.mmotoszko.gk.project9.GraphicsEditor;
import com.mmotoszko.gk.project9.drawing.Filters;
import com.mmotoszko.gk.project9.drawing.Functions2D;
import com.mmotoszko.gk.project9.drawing.customshape.CustomImage;
import com.mmotoszko.gk.project9.events.menu.edit.FindColorFilterPreviewRunner;
import com.mmotoszko.gk.project9.ui.TopMenuButton;

public class FindColorDialog extends JOptionPane implements CustomDialog, ActionListener {
	private static final long serialVersionUID = 1L;
	private FindColorFilterPreviewRunner imageRefresher;
	private JPanel mainPanel;
	private Filters filterType;
	private Filters defaultFilterType;
	private CustomImage image;
	private Color color;
	private JButton colorButton;
	private JLabel resultLabel;
	private JTextField toleranceTextField;
	private JTextField neighbourTextField;
	private JCheckBox creepingCheckBox;

	public FindColorDialog(JPanel parent, GraphicsEditor graphicsEditor, CustomImage image, Filters filterType) {
		super();
		color = Color.black;
		this.defaultFilterType = filterType;
		this.filterType = filterType;
		this.image = image;
		imageRefresher = new FindColorFilterPreviewRunner(graphicsEditor, image);
		Thread imageRefresherThread = new Thread(imageRefresher);
		imageRefresherThread.start();
		this.mainPanel = getMainPanel();
		this.setMessage(getMessage());
		this.setOptionType(getOptionType());
		this.setOptions(getOptions());
		imageRefresher.updateClear();
	}

	private JPanel getBottomPanel() {
		JPanel bottomPanel = new JPanel(new GridLayout(1, 0, 6, 6));

		JButton previewButton = new JButton("Find");
		previewButton.addActionListener(this);
		previewButton.setActionCommand("find");

		JButton clearButton = new JButton("Clear");
		clearButton.addActionListener(this);
		clearButton.setActionCommand("clear");

		bottomPanel.add(previewButton);
		bottomPanel.add(clearButton);

		return bottomPanel;
	}

	private JPanel getCenterPanel() {
		JPanel centerPanel = new JPanel(new BorderLayout(6, 6));
		centerPanel.setPreferredSize(new Dimension(300, 100));

		colorButton = new TopMenuButton(color, 50, 50, 0, 0, "color", this);
		toleranceTextField = new JTextField("1");
		toleranceTextField.setPreferredSize(new Dimension(40, 18));
		neighbourTextField = new JTextField("0");
		neighbourTextField.setPreferredSize(new Dimension(40, 18));
		creepingCheckBox = new JCheckBox();
		creepingCheckBox.setSelected(false);
		resultLabel = new JLabel(" ");

		// color panel
		JButton samplerButton = new JButton("Sample");
		samplerButton.addActionListener(this);
		samplerButton.setActionCommand("sample");

		JPanel colorPanel = new JPanel(new BorderLayout(6, 6));
		colorPanel.add(colorButton, BorderLayout.CENTER);
		colorPanel.add(samplerButton, BorderLayout.PAGE_END);
		//

		// tolerance panel
		JPanel tolerancePanel = new JPanel();
		tolerancePanel.add(new JLabel("Tolerance: "));
		tolerancePanel.add(toleranceTextField);
		tolerancePanel.add(new JLabel(" %"));
		//

		// neighbour panel
		JPanel neighbourPanel = new JPanel();
		neighbourPanel.add(new JLabel("Neighbour check radius: "));
		neighbourPanel.add(neighbourTextField);
		//

		// creeping panel
		JPanel creepingPanel = new JPanel();
		creepingPanel.add(new JLabel("Filtering: "));
		creepingPanel.add(creepingCheckBox);
		//

		// result panel
		JPanel resultPanel = new JPanel();
		resultPanel.add(new JLabel("Color area: "));
		resultPanel.add(resultLabel);
		resultPanel.add(new JLabel(" %"));
		//

		// right panel
		JPanel textfields = new JPanel(new GridLayout(0, 1, 6, 0));
		textfields.add(tolerancePanel);
		textfields.add(neighbourPanel);
		textfields.add(creepingPanel);

		JPanel rightPanel = new JPanel(new BorderLayout(6, 6));
		rightPanel.add(textfields, BorderLayout.PAGE_START);
		rightPanel.add(resultPanel, BorderLayout.CENTER);
		//

		centerPanel.add(colorPanel, BorderLayout.LINE_START);
		centerPanel.add(rightPanel, BorderLayout.CENTER);

		return centerPanel;
	}

	private JPanel getMainPanel() {
		JPanel mainPanel = new JPanel(new BorderLayout(6, 6));

		mainPanel.add(getCenterPanel(), BorderLayout.CENTER);
		mainPanel.add(getBottomPanel(), BorderLayout.PAGE_END);

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
		return filterType.toString();
	}

	private int getResult() {
		byte[] graphic = image.getTempGraphic();
		int foundPixels = 0;
		byte g = 0;
		for (int row = 0; row < image.getHeight(); row++) {
			for (int col = 0; col < image.getWidth(); col++) {
				g = graphic[row * image.getWidth() * 3 + col * 3 + 1];

				if (Functions2D.getValue(g) == 222) {
					foundPixels++;
				}
			}
		}

		float result = (float) foundPixels / (image.getWidth() * image.getHeight()) * 100;

		if (result < 99) {
			return (int) Math.round(result);
		} else {
			return (int) result;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();

		if ("clear".equals(command)) {
			imageRefresher.updateClear();

		} else if ("color".equals(command)) {
			ColorPickerDialog colorPickerDialog = new ColorPickerDialog(null, color);

			int result = JOptionPane.showOptionDialog(null, colorPickerDialog.getMessage(),
					colorPickerDialog.getTitle(), colorPickerDialog.getOptionType(), colorPickerDialog.getMessageType(),
					null, null, null);

			if (result == JOptionPane.OK_OPTION) {
				color = colorPickerDialog.getColor();
			}

			colorButton.setIcon(new ImageIcon(Functions2D.getImage(color, 50, 50)));

		} else if ("sample".equals(command)) {
			ImageColorSamplerDialog colorSamplerDialog = new ImageColorSamplerDialog(this, image);

			int result = JOptionPane.showOptionDialog(null, colorSamplerDialog.getMessage(),
					colorSamplerDialog.getTitle(), colorSamplerDialog.getOptionType(),
					colorSamplerDialog.getMessageType(), null, null, null);

			if (result == JOptionPane.OK_OPTION) {
				color = colorSamplerDialog.getColor();
			}

			colorButton.setIcon(new ImageIcon(Functions2D.getImage(color, 50, 50)));

		} else if ("find".equals(command)) {
			int tolerance = 1;
			try {
				tolerance = Integer.parseInt(toleranceTextField.getText());
			} catch (NumberFormatException ex) {
				toleranceTextField.setText("1");
				resultLabel.setText(" ");
				return;
			}

			if (tolerance < 0) {
				toleranceTextField.setText("0");
				tolerance = 0;
			}

			if (tolerance > 100) {
				toleranceTextField.setText("100");
				tolerance = 100;
			}

			int neighbourSize = 0;
			try {
				neighbourSize = Integer.parseInt(neighbourTextField.getText());
			} catch (NumberFormatException ex) {
				neighbourTextField.setText("0");
				resultLabel.setText(" ");
				return;
			}

			if (neighbourSize < 0) {
				neighbourTextField.setText("0");
				neighbourSize = 0;
			}
			
			boolean creeping = creepingCheckBox.isSelected();

			if(creeping) {
				filterType = Filters.FIND_COLOR_CREEPING;
			} else {
				filterType = defaultFilterType;
			}

			//imageRefresher.update(color, tolerance, neighbourSize, filterType);
			imageRefresher.update(color, tolerance, neighbourSize, creeping);

			new Thread() {
				public void run() {
					try {
						Thread.sleep(333);
					} catch (InterruptedException e) {
					}
					resultLabel.setText(getResult() + "");
				}
			}.start();
		}
	}

	@Override
	public JDialog createDialog(JComponent component, String title) {
		return super.createDialog(component, title);
	}
}
