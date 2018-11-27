/*******************************************************************************
 * Micha³ Motoszko
 * 2018-11-27
 * Grafika komputerowa PS 9
 * Projekt 9
 ******************************************************************************/
package com.mmotoszko.gk.project9.drawing.customshape;

import java.util.ArrayList;

public enum BinarizationMode {
	MANUAL_SELECTION("Manual Selection"), PERCENT_BLACK_SELECTION("Percent Black Selection"), MEAN_ITERATIVE_SELECTION(
			"Mean Iterative Selection"), ENTROPY_SELECTION(
					"Entropy Selection"), MINIMUM_ERROR("Minimum Error"), FUZZY_MINIMUM_ERROR("Fuzzy Minimum Error");

	private final String value;

	BinarizationMode(final String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}

	public static String[] getAllValues() {
		ArrayList<String> values = new ArrayList<>();

		values.add(MANUAL_SELECTION.toString());
		values.add(PERCENT_BLACK_SELECTION.toString());
		values.add(MEAN_ITERATIVE_SELECTION.toString());
		values.add(ENTROPY_SELECTION.toString());
		values.add(MINIMUM_ERROR.toString());
		values.add(FUZZY_MINIMUM_ERROR.toString());

		String[] arrayValues = new String[values.size()];
		arrayValues = values.toArray(arrayValues);

		return arrayValues;
	}

	public static BinarizationMode stringValueOf(String stringValue) {
		BinarizationMode mode = BinarizationMode.MANUAL_SELECTION;

		switch (stringValue) {
		case "Manual Selection":
			mode = BinarizationMode.MANUAL_SELECTION;
			break;
		case "Percent Black Selection":
			mode = BinarizationMode.PERCENT_BLACK_SELECTION;
			break;
		case "Mean Iterative Selection":
			mode = BinarizationMode.MEAN_ITERATIVE_SELECTION;
			break;
		case "Entropy Selection":
			mode = BinarizationMode.ENTROPY_SELECTION;
			break;
		case "Minimum Error":
			mode = BinarizationMode.MINIMUM_ERROR;
			break;
		case "Fuzzy Minimum Error":
			mode = BinarizationMode.FUZZY_MINIMUM_ERROR;
			break;
		default:
			;
		}

		return mode;
	}
}
