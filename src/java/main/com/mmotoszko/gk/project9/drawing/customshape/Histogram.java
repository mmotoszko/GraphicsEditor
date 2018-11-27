/*******************************************************************************
 * Micha³ Motoszko
 * 2018-11-27
 * Grafika komputerowa PS 9
 * Projekt 9
 ******************************************************************************/
package com.mmotoszko.gk.project9.drawing.customshape;

import com.mmotoszko.gk.project9.drawing.Functions2D;

public class Histogram {
	private CustomImage image;
	private int size = 256;
	private int[] originalValues;
	private int[] values;
	private boolean stretched = false;
	private boolean equalized = false;
	private boolean binarized = false;
	private BinarizationMode binarizationMode = BinarizationMode.MANUAL_SELECTION;
	private int threshold = size / 2;
	private int originalMin = size;
	private int originalMax = 0;
	private int pixelCount;
	private int[] cdf = new int[size];
	private int cdfMin;
	private int calculatedThreashold = threshold;

	public Histogram(CustomImage image) {
		this.image = image;
		originalValues = getValuesFromImage(image);
		values = originalValues.clone();
		originalMax = getMaximumValue();
		originalMin = getMinimumValue();
		pixelCount = image.getWidth() * image.getHeight();
	}

	private int[] getValuesFromImage(CustomImage image) {
		byte[] graphic = image.getGraphic(image.getWidth(), image.getHeight());
		int[] newValues = new int[size];

		for (int row = 0; row < image.getHeight(); row++) {
			for (int col = 0; col < image.getWidth(); col++) {
				newValues[Functions2D.getValue(graphic[row * image.getWidth() * 3 + col * 3 + 1])]++;
			}
		}

		return newValues;
	}

	public int getSize() {
		return size;
	}

	public int[] getValues() {
		return values.clone();
	}

	public int[] getOriginalValues() {
		return originalValues.clone();
	}

	public int getValuesSumToIndex(int k) {
		int sum = 0;
		for (int i = 0; i < k; i++) {
			if (values[i] != 0) {
				sum += values[i];
			}
		}
		return sum;
	}

	public int getPixelCount() {
		return pixelCount;
	}

	public int getMaximumValueCount() {
		int max = 0;

		for (int value : values) {
			if (value > max) {
				max = value;
			}
		}

		return max;
	}

	public int getAverageValues() {
		return Math.round(getPixelCount() / (float) size);
	}

	public int getMaximumValue() {
		for (int i = size - 1; i >= 0; i--) {
			if (values[i] > 0) {
				return i;
			}
		}

		return 0;
	}

	public int getMinimumValue() {
		for (int i = 0; i < size; i++) {
			if (values[i] > 0) {
				return i;
			}
		}

		return 0;
	}

	public byte[] getModifiedGraphic(byte[] graphic) {
		byte[] newGraphic = graphic.clone();
		int min = size;
		int max = 0;

		for (int i = 0; i < graphic.length; i++) {
			int value = Functions2D.getValue(graphic[i]);
			if (value < min) {
				min = value;
			}
			if (value > max) {
				max = value;
			}
		}

		for (int i = 0; i < graphic.length; i++) {
			if (stretched) {
				newGraphic[i] = Functions2D
						.getByteValue(getModifedStretchedValue(Functions2D.getValue(graphic[i]), min, max), size);
			}
			if (equalized) {
				newGraphic[i] = Functions2D.getByteValue(getModifedEqualizedValue(Functions2D.getValue(graphic[i])),
						size);
			}
			if (binarized) {
				newGraphic[i] = Functions2D.getByteValue(getModifedBinarizedValue(Functions2D.getValue(graphic[i])),
						size);
			}
		}

		return newGraphic;
	}

	public void updateValues() {
		if (!binarized) {
			values = getValuesFromImage(image);
		}
	}

	public void stretch() {
		stretched = true;
	}

	public int getAverageStretchedValues(int index) {
		float oldIndex = originalMin + ((float) (originalMax - originalMin) / size) * index;
		float leftMargin = oldIndex - (int) oldIndex;
		int leftSideValues = values[(int) Math.floor(oldIndex)];
		int rightSideValues = values[(int) Math.floor(oldIndex + 1)];
		if (leftSideValues == 0) {
			leftMargin = 1;
		}
		if (rightSideValues == 0) {
			leftMargin = 0;
		}
		int stretchedValues = (int) Math.ceil(leftSideValues * (1 - leftMargin))
				+ (int) Math.ceil(rightSideValues * leftMargin);
		return stretchedValues;
	}

	private int getModifedStretchedValue(int value, int min, int max) {
		int newValue = Math.round((value - min) / (float) (max - min) * (size - 1));
		return Math.min(Math.max(newValue, 0), size - 1);
	}

	public void equalize() {
		calculateCdf();
		equalized = true;
	}

	private void calculateCdf() {
		cdf = new int[size];
		cdfMin = 0;
		for (int index = 0; index < size; index++) {
			for (int i = 0; i <= index; i++) {
				cdf[index] += values[i];
				if (cdfMin == 0 && values[i] != 0) {
					cdfMin = values[i];
				}
			}
		}
	}

	private int getModifedEqualizedValue(int value) {
		int newValue = Math.round((cdf[value] - cdfMin) / (float) (getPixelCount() - cdfMin) * (size - 1));
		return Math.min(Math.max(newValue, 0), size - 1);
	}

	public void undoStretch() {
		values = originalValues.clone();

		if (equalized) {
			equalize();
		}

		stretched = false;
	}

	public void undoEqualize() {
		values = originalValues.clone();

		if (stretched) {
			stretch();
		}

		equalized = false;
	}

	public void setThreshhold(int threshold) {
		this.threshold = threshold;
	}

	public int getThreshhold() {
		return threshold;
	}

	public int binarize(BinarizationMode binarizationMode) {
		this.binarizationMode = binarizationMode;
		binarized = true;
		return setThreshold();
	}

	private int getModifedBinarizedValue(int value) {
		switch (binarizationMode) {
		case MANUAL_SELECTION:
			return value >= threshold ? 255 : 0;
		default:
			return value >= calculatedThreashold ? 255 : 0;
		}
	}

	public void undoBinarize() {
		values = originalValues.clone();

		if (stretched) {
			System.out.println("stretching");
			stretch();
		}

		if (equalized) {
			System.out.println("equalizing");
			equalize();
		}

		binarized = false;
	}

	public boolean isBinarized() {
		return binarized;
	}

	public boolean isStretched() {
		return stretched;
	}

	public boolean isEqualized() {
		return equalized;
	}

	public void setBinarizationMode(BinarizationMode mode) {
		this.binarizationMode = mode;
	}

	private int setThreshold() {
		switch (binarizationMode) {
		case MANUAL_SELECTION:
			calculatedThreashold = threshold;
			break;
		case PERCENT_BLACK_SELECTION:
			setBlackPercentageThreshold();
			break;
		case MEAN_ITERATIVE_SELECTION:
			setMeanIterativeSelectionThreshold();
			break;
		case ENTROPY_SELECTION:
			setEntropySelectionThreshold();
			break;
		case MINIMUM_ERROR:
			setMinimumErrorThreshold();
			break;
		case FUZZY_MINIMUM_ERROR:
			setFuzzyMinimumErrorThreshold();
			break;
		default:
			break;
		}

		return calculatedThreashold;
	}

	public int setBlackPercentageThreshold() {
		for (int i = 0; i < size - 1; i++) {
			if (getValuesSumToIndex(i) >= getPixelCount() * (float) threshold / size) {
				calculatedThreashold = i;
				break;
			}
		}
		return calculatedThreashold;
	}

	private int setMeanIterativeSelectionThreshold() {
		int k = size / 2;
		int tempThreshold = 0;
		calculatedThreashold = -1;
		int whiteValues = 0;
		int whiteSum = 0;
		int blackValues = 0;
		int blackSum = 0;

		while (tempThreshold != calculatedThreashold) {
			calculatedThreashold = tempThreshold;

			whiteValues = 0;
			whiteSum = 0;
			for (int i = 0; i < k; i++) {
				whiteValues += i * values[i];
				whiteSum += values[i];
			}
			whiteSum *= 2;

			blackValues = 0;
			blackSum = 0;
			for (int j = k + 1; j < size; j++) {
				blackValues += j * values[j];
				blackSum += values[j];
			}
			blackSum *= 2;

			tempThreshold = Math.round(whiteValues / (float) whiteSum + blackValues / (float) blackSum);

			if (whiteValues / (float) whiteSum == blackValues / (float) blackSum) {
				calculatedThreashold = tempThreshold;
				break;
			}
		}

		return calculatedThreashold;
	}

	private int setEntropySelectionThreshold() {
		double[] entropy = new double[size];
		double[] probability = new double[size];
		int N = getMaximumValueCount();

		for (int k = 0; k < size; k++) {
			probability[k] = values[k] / (double) N;
		}

		for (int k = 0; k < size; k++) {
			double whiteSum = 0;
			double blackSum = 0;
			double whiteSumDivider = 0;
			double blackSumDivider = 0;

			for (int j = 0; j < k; j++) {
				if (probability[j] > 0) {
					whiteSumDivider += probability[j];
				}
			}

			for (int i = 0; i < k; i++) {
				if (probability[i] > 0) {
					whiteSum += probability[i] / whiteSumDivider * Math.log(probability[i] / whiteSumDivider);
				}
			}

			for (int j = k + 1; j < size; j++) {
				if (probability[j] > 0) {
					blackSumDivider += probability[j];
				}
			}

			for (int i = k + 1; i < size; i++) {
				if (probability[i] > 0) {
					blackSum += probability[i] / blackSumDivider * Math.log(probability[i] / blackSumDivider);
				}
			}

			entropy[k] = -whiteSum - blackSum;
		}

		calculatedThreashold = 0;
		for (int k = 0; k < size; k++) {
			if (entropy[k] > entropy[calculatedThreashold]) {
				calculatedThreashold = k;
			}
		}

		return calculatedThreashold;
	}

	private int setMinimumErrorThreshold() {
		// TODO
		return calculatedThreashold;
	}

	private int setFuzzyMinimumErrorThreshold() {
		// TODO
		return calculatedThreashold;
	}
}
