/*******************************************************************************
 * Micha³ Motoszko
 * 2018-11-27
 * Grafika komputerowa PS 9
 * Projekt 9
 ******************************************************************************/
package com.mmotoszko.gk.project9.ui.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.mmotoszko.gk.project9.GraphicsEditor;
import com.mmotoszko.gk.project9.drawing.customshape.CustomImage;
import com.mmotoszko.gk.project9.events.menu.edit.CustomFilterPreviewRunner;

public class CustomFilterDialog extends JOptionPane
		implements CustomDialog, ActionListener, DocumentListener, FocusListener {
	private static final long serialVersionUID = 1L;
	private CustomFilterPreviewRunner imageRefresher;
	private JPanel mainPanel;
	private int size;
	private boolean normalize;
	private float[][] mask;
	private JTextField[][] textFields;

	public CustomFilterDialog(JPanel parent, GraphicsEditor graphicsEditor, CustomImage image) {
		super();
		size = 3;
		normalize = false;
		mask = new float[3][];
		mask[0] = new float[] { 0F, 0F, 0F };
		mask[1] = new float[] { 0F, 1F, 0F };
		mask[2] = new float[] { 0F, 0F, 0F };
		mainPanel = getMainPanel(size);
		imageRefresher = new CustomFilterPreviewRunner(graphicsEditor, image);
		Thread imageRefresherThread = new Thread(imageRefresher);
		imageRefresherThread.start();
		this.setMessage(getMessage());
		this.setOptionType(getOptionType());
		this.setOptions(getOptions());
	}

	private JPanel getCenterPanel(int size) {
		JPanel centerPanel = new JPanel(new GridLayout(size, 0, 3, 3));

		textFields = new JTextField[size][size];
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				textFields[col][row] = new JTextField(mask[col][row] + "");
				textFields[col][row].setHorizontalAlignment(JTextField.CENTER);
				textFields[col][row].addFocusListener(this);
				textFields[col][row].getDocument().addDocumentListener(this);
				centerPanel.add(textFields[col][row]);
			}
		}

		return centerPanel;
	}

	private JPanel getBottomPanel() {
		JPanel bottomPanel = new JPanel(new GridLayout(1, 0, 6, 6));

		JButton removeButton = new JButton("-");
		JButton previewButton = new JButton("Preview");
		JButton addButton = new JButton("+");
		JCheckBox normalizeCheck = new JCheckBox("Normalize");

		removeButton.addActionListener(this);
		normalizeCheck.addActionListener(this);
		previewButton.addActionListener(this);
		addButton.addActionListener(this);

		normalizeCheck.setSelected(normalize);

		removeButton.setActionCommand("remove");
		normalizeCheck.setActionCommand("normalize");
		previewButton.setActionCommand("preview");
		addButton.setActionCommand("add");

		bottomPanel.add(removeButton);
		bottomPanel.add(normalizeCheck);
		bottomPanel.add(previewButton);
		bottomPanel.add(addButton);

		return bottomPanel;
	}

	private JPanel getMainPanel(int size) {
		JPanel mainPanel = new JPanel(new BorderLayout(6, 6));

		mainPanel.add(getCenterPanel(size), BorderLayout.CENTER);
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
		JPanel centerPanel = getCenterPanel(size);
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
		return "Custom filter";
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();

		if ("preview".equals(command)) {
			imageRefresher.update(normalize, mask.clone());
		} else if ("add".equals(command)) {
			size += 2;
			updateSize(size);
		} else if ("remove".equals(command)) {
			if (size > 1) {
				size -= 2;
				updateSize(size);
			}
		} else if ("normalize".equals(command)) {
			normalize = !normalize;
		}
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				float tempValue = 0;

				try {
					tempValue = Float.parseFloat(textFields[col][row].getText());
				} catch (NumberFormatException e1) {
				}

				mask[col][row] = tempValue;
			}
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
	public void focusGained(FocusEvent e) {
		JTextField source = (JTextField) e.getSource();
		source.selectAll();
	}

	@Override
	public void focusLost(FocusEvent e) {

	}

	@Override
	public JDialog createDialog(JComponent component, String title) {
		return super.createDialog(component, title);
	}
}
