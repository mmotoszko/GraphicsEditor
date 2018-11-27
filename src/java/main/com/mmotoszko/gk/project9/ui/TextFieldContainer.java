/*******************************************************************************
 * Micha³ Motoszko
 * 2018-11-27
 * Grafika komputerowa PS 9
 * Projekt 9
 ******************************************************************************/
package com.mmotoszko.gk.project9.ui;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JTextField;

public class TextFieldContainer {
	private static final int UNDEFINED = (int) System.nanoTime();

	private JTextField shapeXTextField;
	private JTextField shapeYTextField;
	private JTextField shapeWidthTextField;
	private JTextField shapeHeightTextField;
	private JTextField verticeXTextField;
	private JTextField verticeYTextField;
	private JTextField moveXTextField;
	private JTextField moveYTextField;
	private JTextField scaleXTextField;
	private JTextField scaleYTextField;

	public TextFieldContainer(JTextField shapeXTextField, JTextField shapeYTextField, JTextField shapeWidthTextField,
			JTextField shapeHeightTextField, JTextField verticeXTextField, JTextField verticeYTextField,
			JTextField moveXTextField, JTextField moveYTextField, JTextField scaleXTextField,
			JTextField scaleYTextField) {
		this.shapeXTextField = shapeXTextField;
		this.shapeYTextField = shapeYTextField;
		this.shapeWidthTextField = shapeWidthTextField;
		this.shapeHeightTextField = shapeHeightTextField;
		this.verticeXTextField = verticeXTextField;
		this.verticeYTextField = verticeYTextField;
		this.moveXTextField = moveXTextField;
		this.moveYTextField = moveYTextField;
		this.scaleXTextField = scaleXTextField;
		this.scaleYTextField = scaleYTextField;
	}

	public int getInt(JTextField textField) {
		if (textField == null) {
			return UNDEFINED;
		}

		try {
			return Integer.parseInt(textField.getText());
		} catch (NumberFormatException e) {
			return UNDEFINED;
		}
	}

	public double getDouble(JTextField textField) {
		if (textField == null) {
			return UNDEFINED;
		}

		try {
			return Double.parseDouble(textField.getText());
		} catch (NumberFormatException e) {
			return UNDEFINED;
		}
	}

	public boolean gotCenter() {
		boolean fieldsExist = shapeXTextField != null && shapeYTextField != null;
		boolean fieldsValid = false;

		if (fieldsExist) {
			fieldsValid = getX() != UNDEFINED && getY() != UNDEFINED;
		}

		if (!fieldsValid && fieldsExist) {
			shapeXTextField.setBorder(BorderFactory.createDashedBorder(Color.red));
			shapeYTextField.setBorder(BorderFactory.createDashedBorder(Color.red));
		}

		return fieldsValid && fieldsExist;
	}

	public boolean gotWidth() {
		boolean fieldsExist = shapeWidthTextField != null;
		boolean fieldsValid = false;

		if (fieldsExist) {
			fieldsValid = getWidth() != UNDEFINED;
		}

		if (!fieldsValid && fieldsExist) {
			shapeWidthTextField.setBorder(BorderFactory.createDashedBorder(Color.red));
		}

		return fieldsValid && fieldsExist;
	}

	public boolean gotHeight() {
		boolean fieldsExist = shapeHeightTextField != null;
		boolean fieldsValid = false;

		if (fieldsExist) {
			fieldsValid = getHeight() != UNDEFINED;
		}

		if (!fieldsValid && fieldsExist) {
			shapeHeightTextField.setBorder(BorderFactory.createDashedBorder(Color.red));
		}

		return fieldsValid && fieldsExist;
	}

	public boolean gotVertice() {
		boolean fieldsExist = verticeXTextField != null && verticeYTextField != null;
		boolean fieldsValid = false;

		if (fieldsExist) {
			fieldsValid = getVerticeX() != UNDEFINED && getVerticeY() != UNDEFINED;
		}

		if (!fieldsValid && fieldsExist) {
			verticeXTextField.setBorder(BorderFactory.createDashedBorder(Color.red));
			verticeYTextField.setBorder(BorderFactory.createDashedBorder(Color.red));
		}

		return fieldsValid && fieldsExist;
	}

	public boolean gotMoveVector() {
		boolean fieldsExist = moveXTextField != null && moveYTextField != null;
		boolean fieldsValid = false;

		if (fieldsExist) {
			fieldsValid = getMoveVectorX() != UNDEFINED && getMoveVectorY() != UNDEFINED;
		}

		if (!fieldsValid && fieldsExist) {
			moveXTextField.setBorder(BorderFactory.createDashedBorder(Color.red));
			moveYTextField.setBorder(BorderFactory.createDashedBorder(Color.red));
		}

		return fieldsValid && fieldsExist;
	}
	
	public boolean gotScaleVector() {
		boolean fieldsExist = scaleXTextField != null && scaleYTextField != null;
		boolean fieldsValid = false;

		if (fieldsExist) {
			fieldsValid = getScaleX() != UNDEFINED && getScaleY() != UNDEFINED;
		}

		if (!fieldsValid && fieldsExist) {
			scaleXTextField.setBorder(BorderFactory.createDashedBorder(Color.red));
			scaleYTextField.setBorder(BorderFactory.createDashedBorder(Color.red));
		}

		return fieldsValid && fieldsExist;
	}
	
	public int getX() {
		if (shapeXTextField != null) {
			return getInt(shapeXTextField);
		} else {
			return 0;
		}
	}

	public int getY() {
		if (shapeYTextField != null) {
			return getInt(shapeYTextField);
		} else {
			return 0;
		}
	}

	public int getWidth() {
		if (shapeWidthTextField != null) {
			return getInt(shapeWidthTextField);
		} else {
			return 0;
		}
	}

	public int getHeight() {
		if (shapeHeightTextField != null) {
			return getInt(shapeHeightTextField);
		} else {
			return 0;
		}
	}

	public int getVerticeX() {
		if (verticeXTextField != null) {
			return getInt(verticeXTextField);
		} else {
			return 0;
		}
	}

	public int getVerticeY() {
		if (verticeYTextField != null) {
			return getInt(verticeYTextField);
		} else {
			return 0;
		}
	}

	public int getMoveVectorX() {
		if (moveXTextField != null) {
			return getInt(moveXTextField);
		} else {
			return 0;
		}
	}

	public int getMoveVectorY() {
		if (moveYTextField != null) {
			return getInt(moveYTextField);
		} else {
			return 0;
		}
	}

	public double getScaleX() {
		if (scaleXTextField != null) {
			return getDouble(scaleXTextField);
		} else {
			return 0;
		}
	}

	public double getScaleY() {
		if (scaleYTextField != null) {
			return getDouble(scaleYTextField);
		} else {
			return 0;
		}
	}

}
