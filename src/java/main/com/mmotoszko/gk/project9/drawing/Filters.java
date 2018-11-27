/*******************************************************************************
 * Micha³ Motoszko
 * 2018-11-27
 * Grafika komputerowa PS 9
 * Projekt 9
 ******************************************************************************/
package com.mmotoszko.gk.project9.drawing;

public enum Filters {
	AVERAGE("Average blur"), MEDIAN("Median"), EDGEFINDER("Edge finder"), HIGHPASS("High pass"), GAUSS(
			"Gauss blur"), SPLICE("Splice"), CUSTOM("Custom"), SOBELLEFT("Sobel left"), SOBELRIGHT(
					"Sobel right"), SOBELTOP("Sobel top"), SOBELBOTTOM("Sobel bottom"), SPECKLE(
							"Speckle"), GRAYSCALE("Grayscale"), GRAYSCALEMEAN("Grayscale mean"), BRIGHTNESS(
									"Brightness"), COLOR("Color"), DILATION("Dilation"), EROSION("Erosion"), OPENING(
											"Opening"), CLOSING("Closing"), HIT_OR_MISS("Hit-or-miss"), FIND_COLOR(
													"Find color"), FIND_COLOR_CREEPING("Find color creeping");
	private final String value;

	Filters(final String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}
}
