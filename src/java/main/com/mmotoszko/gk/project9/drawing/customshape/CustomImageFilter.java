/*******************************************************************************
 * Micha³ Motoszko
 * 2018-11-27
 * Grafika komputerowa PS 9
 * Projekt 9
 ******************************************************************************/
package com.mmotoszko.gk.project9.drawing.customshape;

import java.util.List;
import java.util.Random;

import com.mmotoszko.gk.project9.drawing.Filters;
import com.mmotoszko.gk.project9.drawing.Functions2D;

import java.util.ArrayList;

public class CustomImageFilter {
	private float[][] mask;
	private int size;
	private int index = -10;
	private Filters type;
	private byte[][] graphicMarks = null;
	private boolean complex = false;

	private CustomImageFilter setComplex() {
		complex = true;
		return this;
	}

	public boolean isComplex() {
		return complex;
	}

	public Filters getType() {
		return type;
	}

	public CustomImageFilter(float[][] mask) {
		this.size = mask.length;
		this.mask = mask;
		this.type = Filters.CUSTOM;
	}

	public CustomImageFilter(boolean normalize, float[][] mask) {
		this.size = mask.length;

		if (normalize) {
			this.mask = normalize(mask);
		} else {
			this.mask = mask;
		}

		this.type = Filters.CUSTOM;
	}

	public CustomImageFilter(boolean normalize, float[][] mask, Filters type) {
		this.size = mask.length;

		if (normalize) {
			this.mask = normalize(mask);
		} else {
			this.mask = mask;
		}

		if (type != Filters.DILATION && type != Filters.EROSION && type != Filters.OPENING && type != Filters.CLOSING
				&& type != Filters.HIT_OR_MISS) {
			type = Filters.CUSTOM;
		}

		this.type = type;
	}

	private CustomImageFilter(float[][] mask, Filters type) {
		this.size = mask.length;
		this.mask = mask;
		this.type = type;
	}

	private CustomImageFilter(float[][] mask, Filters type, int size) {
		this.size = size;
		this.mask = mask;
		this.type = type;
	}

	public CustomImageFilter(boolean normalize, int[][] mask) {
		this.size = mask.length;
		float tempMask[][] = new float[size][];

		for (int i = 0; i < this.size; i++) {
			for (int j = 0; j < this.size; j++) {
				tempMask[i][j] = mask[i][j];
			}
		}

		if (normalize) {
			tempMask = normalize(tempMask);
		}

		this.type = Filters.CUSTOM;
		this.mask = tempMask;
	}

	public CustomImageFilter(float... maskNumbers) throws Exception {
		double tempSize = Math.sqrt(maskNumbers.length);
		if (tempSize % 1 != 0) {
			throw new Exception("Incorrect mask size");
		}

		this.size = (int) tempSize;
		float[][] tempMask = new float[this.size][this.size];

		int index = 0;
		for (int i = 0; i < this.size; i++) {
			for (int j = 0; j < this.size; j++) {
				tempMask[i][j] = maskNumbers[index];
				index++;
			}
		}

		this.type = Filters.CUSTOM;
		this.mask = tempMask;
	}

	public CustomImageFilter(boolean normalize, int... maskNumbers) throws Exception {
		double tempSize = Math.sqrt(maskNumbers.length);
		if (tempSize % 1 != 0) {
			throw new Exception("Incorrect mask size");
		}

		this.size = (int) tempSize;
		float[][] tempMask = new float[this.size][this.size];

		int index = 0;
		for (int i = 0; i < this.size; i++) {
			for (int j = 0; j < this.size; j++) {
				tempMask[i][j] = maskNumbers[index];
				index++;
			}
		}

		if (normalize) {
			tempMask = normalize(tempMask);
		}

		this.type = Filters.CUSTOM;
		this.mask = tempMask;
	}

	public static float[][] normalize(float[][] mask) {
		float sum = 0;
		for (int i = 0; i < mask.length; i++) {
			for (int j = 0; j < mask[i].length; j++) {
				sum += mask[i][j];
			}
		}

		for (int i = 0; i < mask.length; i++) {
			for (int j = 0; j < mask[i].length; j++) {
				mask[i][j] = mask[i][j] / sum;
			}
		}

		return mask;
	}

	public static float[][] normalize(int[][] mask) {
		float sum = 0;
		float[][] newMask = new float[mask.length][mask[0].length];
		for (int i = 0; i < mask.length; i++) {
			for (int j = 0; j < mask[i].length; j++) {
				sum += mask[i][j];
			}
		}

		for (int i = 0; i < mask.length; i++) {
			for (int j = 0; j < mask[i].length; j++) {
				newMask[i][j] = (float) mask[i][j] / sum;
			}
		}

		return newMask;
	}

	public float[][] getMask() {
		return mask;
	}

	public int getSize() {
		return size;
	}

	public boolean hasNext() {
		if (index == -10) {
			return true;
		}
		if (index == -2) {
			index++;
			return false;
		}

		return index + 1 < size * size;
	}

	public boolean hasPrevious() {
		if (index == -10) {
			return true;
		}
		if (index == size * size + 1) {
			index--;
			return false;
		}

		return index - 1 > 0;
	}

	public float next() {
		if (index == -10) {
			index = -2;
		}
		if (index == -2) {
			index++;
		}
		index++;
		float maskValue = mask[index / size][index % size];
		if (index == size * size - 1) {
			index = -2;
		}
		return maskValue;
	}

	public float previous() {
		if (index == -10) {
			index = size + 1;
		}
		if (index == size * size + 1) {
			index--;
		}
		index--;
		float maskValue = mask[index / size][index % size];
		if (index == 0) {
			index = size * size + 1;
		}
		return maskValue;
	}

	private int getGrayscaleValue(final int[][] graphic, final int x, final int y) {
		byte[] rgb = Functions2D.getRGBValues(graphic[x][y]);
		int r = rgb[1];
		int g = rgb[1];
		int b = rgb[1];
		return Functions2D.getIntRGBPixel((byte) r, (byte) g, (byte) b);
	}

	private int getGrayscaleMeanValue(final int[][] graphic, final int x, final int y) {
		byte[] rgb = Functions2D.getRGBValues(graphic[x][y]);
		int mean = Math.round((rgb[0] + rgb[1] + rgb[2]) / 3);
		int r = mean;
		int g = mean;
		int b = mean;
		return Functions2D.getIntRGBPixel((byte) r, (byte) g, (byte) b);
	}

	private int getMedianValue(final int[][] graphic, final int x, final int y) {
		List<Integer> accumulatorR = new ArrayList<>();
		List<Integer> accumulatorG = new ArrayList<>();
		List<Integer> accumulatorB = new ArrayList<>();
		for (int row = y - size / 2; row <= y + size / 2; row++) {
			for (int col = x - size / 2; col <= x + size / 2; col++) {
				int tempRow = row;
				int tempCol = col;
				if (tempRow < 0 && tempCol < 0) {
					while (tempRow < 0 || tempCol < 0) {
						tempRow++;
						tempCol++;
					}
				}
				while (tempRow < 0) {
					tempRow++;
				}
				while (tempCol < 0) {
					tempCol++;
				}
				if (tempRow > graphic[0].length - 1 && tempCol > graphic.length - 1) {
					while (tempRow > graphic[0].length - 1 || tempCol > graphic.length - 1) {
						tempRow--;
						tempCol--;
					}
				}
				while (tempRow > graphic[0].length - 1) {
					tempRow--;
				}
				while (tempCol > graphic.length - 1) {
					tempCol--;
				}

				byte[] rgb = Functions2D.getRGBValues(graphic[tempCol][tempRow]);
				accumulatorR.add(Functions2D.getValue(rgb[0]));
				accumulatorG.add(Functions2D.getValue(rgb[1]));
				accumulatorB.add(Functions2D.getValue(rgb[2]));
			}
		}

		for (int n = 0; n < accumulatorG.size(); n++) {
			for (int i = 0; i < accumulatorG.size(); i++) {
				if (i == accumulatorG.size() - 1) {
					continue;
				}
				if (accumulatorG.get(i + 1) < accumulatorG.get(i)) {
					accumulatorR.add(i, accumulatorR.get(i + 1));
					accumulatorR.remove(i + 2);
					accumulatorG.add(i, accumulatorG.get(i + 1));
					accumulatorG.remove(i + 2);
					accumulatorB.add(i, accumulatorB.get(i + 1));
					accumulatorB.remove(i + 2);
				}
			}
		}

		int r = accumulatorR.get(accumulatorR.size() / 2);
		int g = accumulatorG.get(accumulatorG.size() / 2);
		int b = accumulatorB.get(accumulatorB.size() / 2);

		return Functions2D.getIntRGBPixel((byte) r, (byte) g, (byte) b);
	}

	private int getSpeckleValue(final int[][] graphic, final int x, final int y) {
		Random rand = new Random();
		if (rand.nextInt(40) == 0) {
			return Functions2D.getIntRGBPixel((byte) 255, (byte) 255, (byte) 255);
		}

		return graphic[x][y];
	}

	private int getBrightnessValue(final int[][] graphic, final int x, final int y) {
		byte[] rgb = Functions2D.getRGBValues(graphic[x][y]);
		int r = Functions2D.getValue(rgb[0]);
		int g = Functions2D.getValue(rgb[1]);
		int b = Functions2D.getValue(rgb[2]);

		float brightnessModifier = (float) this.mask[0][0] / 100;

		if (brightnessModifier <= 0) {
			r = Math.round(r + r * brightnessModifier);
			g = Math.round(g + g * brightnessModifier);
			b = Math.round(b + b * brightnessModifier);
		} else {
			r = Math.round(r + (255 - r) * brightnessModifier);
			g = Math.round(g + (255 - g) * brightnessModifier);
			b = Math.round(b + (255 - b) * brightnessModifier);
		}

		return Functions2D.getIntRGBPixel((byte) r, (byte) g, (byte) b);
	}

	private int getColorValue(final int[][] graphic, final int x, final int y) {
		byte[] rgb = Functions2D.getRGBValues(graphic[x][y]);
		int r = Functions2D.getValue(rgb[0]);
		int g = Functions2D.getValue(rgb[1]);
		int b = Functions2D.getValue(rgb[2]);

		float rModifier = (float) this.mask[0][0] / 100;
		float gModifier = (float) this.mask[0][1] / 100;
		float bModifier = (float) this.mask[0][2] / 100;

		if (rModifier <= 0) {
			r = Math.round(r + r * rModifier);
		} else {
			r = Math.round(r + (255 - r) * rModifier);
		}

		if (gModifier <= 0) {
			g = Math.round(g + g * gModifier);
		} else {
			g = Math.round(g + (255 - g) * gModifier);
		}

		if (bModifier <= 0) {
			b = Math.round(b + b * bModifier);
		} else {
			b = Math.round(b + (255 - b) * bModifier);
		}

		return Functions2D.getIntRGBPixel((byte) r, (byte) g, (byte) b);
	}

	private int getSobelValue(final int[][] graphic, final int x, final int y) {
		int r = 0;
		int g = 0;
		int b = 0;
		for (int row = y - size / 2; row <= y + size / 2; row++) {
			for (int col = x - size / 2; col <= x + size / 2; col++) {
				int tempRow = row;
				int tempCol = col;
				if (tempRow < 0 && tempCol < 0) {
					while (tempRow < 0 || tempCol < 0) {
						tempRow++;
						tempCol++;
					}
				}
				while (tempRow < 0) {
					tempRow++;
				}
				while (tempCol < 0) {
					tempCol++;
				}
				if (tempRow > graphic[0].length - 1 && tempCol > graphic.length - 1) {
					while (tempRow > graphic[0].length - 1 || tempCol > graphic.length - 1) {
						tempRow--;
						tempCol--;
					}
				}
				while (tempRow > graphic[0].length - 1) {
					tempRow--;
				}
				while (tempCol > graphic.length - 1) {
					tempCol--;
				}

				float modifier = next();

				byte[] rgb = Functions2D.getRGBValues(graphic[tempCol][tempRow]);
				r += Functions2D.getValue(rgb[0]) * modifier;
				g += Functions2D.getValue(rgb[1]) * modifier;
				b += Functions2D.getValue(rgb[2]) * modifier;
			}
		}

		r = Math.max(0, Math.min(r, 255));
		g = Math.max(0, Math.min(g, 255));
		b = Math.max(0, Math.min(b, 255));

		if (r > g || g > r) {
			r = b;
			g = b;
		}

		return Functions2D.getIntRGBPixel((byte) r, (byte) g, (byte) b);
	}

	private int getConvolutedValue(final int[][] graphic, final int x, final int y) {
		int r = 0;
		int g = 0;
		int b = 0;
		for (int row = y - size / 2; row <= y + size / 2; row++) {
			for (int col = x - size / 2; col <= x + size / 2; col++) {
				int tempRow = row;
				int tempCol = col;
				if (tempRow < 0 && tempCol < 0) {
					while (tempRow < 0 || tempCol < 0) {
						tempRow++;
						tempCol++;
					}
				}
				while (tempRow < 0) {
					tempRow++;
				}
				while (tempCol < 0) {
					tempCol++;
				}
				if (tempRow > graphic[0].length - 1 && tempCol > graphic.length - 1) {
					while (tempRow > graphic[0].length - 1 || tempCol > graphic.length - 1) {
						tempRow--;
						tempCol--;
					}
				}
				while (tempRow > graphic[0].length - 1) {
					tempRow--;
				}
				while (tempCol > graphic.length - 1) {
					tempCol--;
				}

				float modifier = next();

				byte[] rgb = Functions2D.getRGBValues(graphic[tempCol][tempRow]);
				r += Functions2D.getValue(rgb[0]) * modifier;
				g += Functions2D.getValue(rgb[1]) * modifier;
				b += Functions2D.getValue(rgb[2]) * modifier;
			}
		}

		r = Math.max(0, Math.min(r, 255));
		g = Math.max(0, Math.min(g, 255));
		b = Math.max(0, Math.min(b, 255));

		return Functions2D.getIntRGBPixel((byte) r, (byte) g, (byte) b);
	}

	private int getDialationValue(final int[][] graphic, final int x, final int y) {
		int r = 0;
		int g = 0;
		int b = 0;
		boolean pixelOn = false;
		for (int row = y - size / 2; row <= y + size / 2; row++) {
			for (int col = x - size / 2; col <= x + size / 2; col++) {
				int tempRow = row;
				int tempCol = col;
				while (tempRow < 0) {
					tempRow++;
				}
				while (tempCol < 0) {
					tempCol++;
				}
				while (tempRow > graphic[0].length - 1) {
					tempRow--;
				}
				while (tempCol > graphic.length - 1) {
					tempCol--;
				}

				float modifier = next();
				if (modifier == 0F) {
					pixelOn = false;
				} else {
					pixelOn = true;
				}

				if (pixelOn) {
					byte[] rgb = Functions2D.getRGBValues(graphic[tempCol][tempRow]);
					if (Functions2D.getValue(rgb[0]) > r) {
						r = Functions2D.getValue(rgb[0]);
					}
					if (Functions2D.getValue(rgb[1]) > g) {
						g = Functions2D.getValue(rgb[1]);
					}
					if (Functions2D.getValue(rgb[2]) > b) {
						b = Functions2D.getValue(rgb[2]);
					}
				}
			}
		}

		r = Math.max(0, Math.min(r, 255));
		g = Math.max(0, Math.min(g, 255));
		b = Math.max(0, Math.min(b, 255));

		return Functions2D.getIntRGBPixel((byte) r, (byte) g, (byte) b);
	}

	private int getErosionValue(final int[][] graphic, final int x, final int y) {
		int r = 255;
		int g = 255;
		int b = 255;
		boolean pixelOn = false;
		for (int row = y - size / 2; row <= y + size / 2; row++) {
			for (int col = x - size / 2; col <= x + size / 2; col++) {
				int tempRow = row;
				int tempCol = col;
				if (tempRow < 0 && tempCol < 0) {
					while (tempRow < 0 || tempCol < 0) {
						tempRow++;
						tempCol++;
					}
				}
				while (tempRow < 0) {
					tempRow++;
				}
				while (tempCol < 0) {
					tempCol++;
				}
				if (tempRow > graphic[0].length - 1 && tempCol > graphic.length - 1) {
					while (tempRow > graphic[0].length - 1 || tempCol > graphic.length - 1) {
						tempRow--;
						tempCol--;
					}
				}
				while (tempRow > graphic[0].length - 1) {
					tempRow--;
				}
				while (tempCol > graphic.length - 1) {
					tempCol--;
				}

				float modifier = next();
				if (modifier == 0F) {
					pixelOn = false;
				} else {
					pixelOn = true;
				}

				if (pixelOn) {
					byte[] rgb = Functions2D.getRGBValues(graphic[tempCol][tempRow]);
					if (Functions2D.getValue(rgb[0]) < r) {
						r = Functions2D.getValue(rgb[0]);
					}
					if (Functions2D.getValue(rgb[1]) < g) {
						g = Functions2D.getValue(rgb[1]);
					}
					if (Functions2D.getValue(rgb[2]) < b) {
						b = Functions2D.getValue(rgb[2]);
					}
				}

			}
		}

		r = Math.max(0, Math.min(r, 255));
		g = Math.max(0, Math.min(g, 255));
		b = Math.max(0, Math.min(b, 255));

		return Functions2D.getIntRGBPixel((byte) r, (byte) g, (byte) b);
	}

	private int getHitOrMissValue(final int[][] graphic, final int x, final int y) {
		byte[] rgb = Functions2D.getRGBValues(graphic[x][y]);
		int r = rgb[0];
		int g = rgb[1];
		int b = rgb[2];
		return Functions2D.getIntRGBPixel((byte) r, (byte) g, (byte) b);
	}

	private int getFindColorValue(final int[][] graphic, final int x, final int y) {
		byte[] focusedRGB = Functions2D.getRGBValues(graphic[x][y]);
		int focusedR = Functions2D.getValue(focusedRGB[0]);
		int focusedG = Functions2D.getValue(focusedRGB[1]);
		int focusedB = Functions2D.getValue(focusedRGB[2]);

		int colorR = (int) this.mask[this.mask.length - 1][0];
		int colorG = (int) this.mask[this.mask.length - 1][1];
		int colorB = (int) this.mask[this.mask.length - 1][2];
		int tolerance = (int) this.mask[this.mask.length - 1][3];

		int averageR = 0;
		int averageG = 0;
		int averageB = 0;

		for (int row = y - size / 2; row <= y + size / 2; row++) {
			for (int col = x - size / 2; col <= x + size / 2; col++) {
				int tempRow = row;
				int tempCol = col;
				if (tempRow < 0 && tempCol < 0) {
					while (tempRow < 0 || tempCol < 0) {
						tempRow++;
						tempCol++;
					}
				}
				while (tempRow < 0) {
					tempRow++;
				}
				while (tempCol < 0) {
					tempCol++;
				}
				if (tempRow > graphic[0].length - 1 && tempCol > graphic.length - 1) {
					while (tempRow > graphic[0].length - 1 || tempCol > graphic.length - 1) {
						tempRow--;
						tempCol--;
					}
				}
				while (tempRow > graphic[0].length - 1) {
					tempRow--;
				}
				while (tempCol > graphic.length - 1) {
					tempCol--;
				}

				byte[] rgb = Functions2D.getRGBValues(graphic[tempCol][tempRow]);
				int r = Functions2D.getValue(rgb[0]);
				int g = Functions2D.getValue(rgb[1]);
				int b = Functions2D.getValue(rgb[2]);

				averageR += r;
				averageG += g;
				averageB += b;

				int difference = 0;

				difference += Math.abs(r - colorR);
				difference += Math.abs(g - colorG);
				difference += Math.abs(b - colorB);

				if (difference <= (float) tolerance / 100 * 255 * 3) {

				}
			}
		}

		int focusedColorDifference = 0;

		// check if focused pixel matches the color
		focusedColorDifference = Math.abs(focusedR - colorR);
		focusedColorDifference += Math.abs(focusedG - colorG);
		focusedColorDifference += Math.abs(focusedB - colorB);

		if (focusedColorDifference <= (float) tolerance / 100 * 255 * 3) {
			focusedR = Math.max(focusedR - 150, 0);
			focusedG = 222;
			return Functions2D.getIntRGBPixel((byte) focusedR, (byte) focusedG, (byte) focusedB);
		}
		//

		averageR = (int) (averageR / (float) (size * size));
		averageG = (int) (averageG / (float) (size * size));
		averageB = (int) (averageB / (float) (size * size));

		// check if the average of neighbouring pixels matches the color
		focusedColorDifference = Math.abs(averageR - colorR);
		focusedColorDifference += Math.abs(averageG - colorG);
		focusedColorDifference += Math.abs(averageB - colorB);

		if (focusedColorDifference <= (float) tolerance / 100 * 255 * 3) {
			focusedR = Math.max(focusedR - 150, 0);
			focusedG = 222;
			return Functions2D.getIntRGBPixel((byte) focusedR, (byte) focusedG, (byte) focusedB);
		}
		//

		// return not matching pixel
		focusedR = 222;
		focusedG = Math.max(focusedG - 150, 0);

		return Functions2D.getIntRGBPixel((byte) focusedR, (byte) focusedG, (byte) focusedB);
	}

	public int getValue(final int[][] graphic, final int x, final int y) {
		if (type == Filters.MEDIAN) {
			return getMedianValue(graphic, x, y);
		}

		if (type == Filters.SOBELLEFT || type == Filters.SOBELRIGHT || type == Filters.SOBELBOTTOM
				|| type == Filters.SOBELBOTTOM) {
			return getSobelValue(graphic, x, y);
		}

		if (type == Filters.BRIGHTNESS) {
			return getBrightnessValue(graphic, x, y);
		}

		if (type == Filters.COLOR) {
			return getColorValue(graphic, x, y);
		}

		if (type == Filters.SPECKLE) {
			return getSpeckleValue(graphic, x, y);
		}

		if (type == Filters.GRAYSCALE) {
			return getGrayscaleValue(graphic, x, y);
		}

		if (type == Filters.GRAYSCALEMEAN) {
			return getGrayscaleMeanValue(graphic, x, y);
		}

		if (type == Filters.DILATION) {
			return getDialationValue(graphic, x, y);
		}

		if (type == Filters.EROSION) {
			return getErosionValue(graphic, x, y);
		}

		if (type == Filters.HIT_OR_MISS) {
			return getHitOrMissValue(graphic, x, y);
		}

		if (type == Filters.FIND_COLOR) {
			return getFindColorValue(graphic, x, y);
		}

		return getConvolutedValue(graphic, x, y);
	}

	private int[][] getFindColorCreepingGraphic(final int[][] graphic) {
		int[][] newGraphic = graphic.clone();
		graphicMarks = new byte[graphic.length][graphic[0].length];

		System.out.println("graphic.length: " + graphic.length);

		for (int i = 0; i < graphicMarks[0].length; i++) {
			for (int j = 0; j < graphicMarks.length; j++) {
				graphicMarks[j][i] = 0;
			}
		}

		int colorR = (int) this.mask[this.mask.length - 1][0];
		int colorG = (int) this.mask[this.mask.length - 1][1];
		int colorB = (int) this.mask[this.mask.length - 1][2];
		int tolerance = (int) this.mask[this.mask.length - 1][3];

		for (int y = 0; y < newGraphic[0].length; y++) {
			for (int x = 0; x < newGraphic.length; x++) {

				byte[] focusedRGB = Functions2D.getRGBValues(graphic[x][y]);
				int focusedR = Functions2D.getValue(focusedRGB[0]);
				int focusedG = Functions2D.getValue(focusedRGB[1]);
				int focusedB = Functions2D.getValue(focusedRGB[2]);

				int focusedDifference = 0;

				focusedDifference += Math.abs(focusedR - colorR);
				focusedDifference += Math.abs(focusedG - colorG);
				focusedDifference += Math.abs(focusedB - colorB);

				if (focusedDifference <= (float) tolerance / 100 * 255 * 3) {
					graphicMarks[x][y]++;
					recursivePixelCheck(graphic, colorR, colorG, colorB, x, y, tolerance);
				}

			}
		}

		for (int i = 0; i < graphicMarks[0].length; i++) {
			for (int j = 0; j < graphicMarks.length; j++) {
				byte[] rgb = Functions2D.getRGBValues(graphic[j][i]);
				int r = Functions2D.getValue(rgb[0]);
				int g = Functions2D.getValue(rgb[1]);
				int b = Functions2D.getValue(rgb[2]);

				if (graphicMarks[j][i] > 0) {
					r = Math.max(r - 150, 0);
					g = 222;
				} else {
					r = 222;
					g = Math.max(g - 150, 0);
				}

				newGraphic[j][i] = Functions2D.getIntRGBPixel((byte) r, (byte) g, (byte) b);
			}
		}

		System.out.println("done");

		return newGraphic;
	}

	private void recursivePixelCheck(int[][] graphic, int colorR, int colorG, int colorB, int x, int y, int tolerance) {
		for (int row = y - size / 2; row <= y + size / 2; row++) {
			for (int col = x - size / 2; col <= x + size / 2; col++) {
				if (row == col && row == y) {
					continue;
				}

				int tempRow = row;
				int tempCol = col;
				if (tempRow < 0 && tempCol < 0) {
					while (tempRow < 0 || tempCol < 0) {
						tempRow++;
						tempCol++;
					}
				}
				while (tempRow < 0) {
					tempRow++;
				}
				while (tempCol < 0) {
					tempCol++;
				}
				if (tempRow > graphic[0].length - 1 && tempCol > graphic.length - 1) {
					while (tempRow > graphic[0].length - 1 || tempCol > graphic.length - 1) {
						tempRow--;
						tempCol--;
					}
				}
				while (tempRow > graphic[0].length - 1) {
					tempRow--;
				}
				while (tempCol > graphic.length - 1) {
					tempCol--;
				}

				byte[] rgb = Functions2D.getRGBValues(graphic[tempCol][tempRow]);
				int r = Functions2D.getValue(rgb[0]);
				int g = Functions2D.getValue(rgb[1]);
				int b = Functions2D.getValue(rgb[2]);

				int difference = 0;

				difference += Math.abs(r - colorR);
				difference += Math.abs(g - colorG);
				difference += Math.abs(b - colorB);

				if (difference <= (float) tolerance / 100 * 255 * 3
						&& 0 > graphicMarks[Math.max(0, Math.min(graphicMarks.length - 1, x + col))][Math.max(0,
								Math.min(graphicMarks[0].length - 1, y + row))]
//						&& 0 - size > graphicMarks[Math.max(0, Math.min(graphicMarks.length - 1, x + col))][Math.max(0,
//								Math.min(graphicMarks[0].length - 1, y + row))]
										) {
					graphicMarks[Math.max(0, Math.min(graphicMarks.length - 1, x + col))][Math.max(0,
							Math.min(graphicMarks[0].length - 1, y + row))] = 1;

					recursivePixelCheck(graphic, r, g, b, Math.max(0, Math.min(graphicMarks.length - 1, x + col)),
							Math.max(0, Math.min(graphicMarks[0].length - 1, y + row)), tolerance);
				} else {
					graphicMarks[Math.max(0, Math.min(graphicMarks.length - 1, x + col))][Math.max(0,
							Math.min(graphicMarks[0].length - 1, y + row))] = -1;
				}

			}
		}
	}

	public int[][] getGraphic(final int[][] graphic) {

		if (type == Filters.FIND_COLOR_CREEPING) {
			return getFindColorCreepingGraphic(graphic);
		}

		return null;
	}

	public static CustomImageFilter getFilter(Filters filterType, Object... size) {
		switch (filterType) {
		case BRIGHTNESS:
			return CustomImageFilter.getBrightnessFilter((int) size[0]);
		case COLOR:
			return CustomImageFilter.getColorFilter((int) size[0], (int) size[1], (int) size[2]);
		case AVERAGE:
			return CustomImageFilter.getAverageFilter((int) size[0]);
		case MEDIAN:
			return CustomImageFilter.getMedianFilter((int) size[0]);
		case EDGEFINDER:
			return CustomImageFilter.getEdgeFinderFilter();
		case HIGHPASS:
			return CustomImageFilter.getSharpenFilter();
		case GAUSS:
			return CustomImageFilter.getGaussFilter();
		case GRAYSCALE:
			return CustomImageFilter.getGrayscaleFilter();
		case GRAYSCALEMEAN:
			return CustomImageFilter.getGrayscaleMeanFilter();
		case SPECKLE:
			return CustomImageFilter.getSpeckleFilter();
		case SOBELTOP:
			return CustomImageFilter.getTopSobelFilter();
		case SOBELBOTTOM:
			return CustomImageFilter.getBottomSobelFilter();
		case SOBELLEFT:
			return CustomImageFilter.getLeftSobelFilter();
		case SOBELRIGHT:
			return CustomImageFilter.getRightSobelFilter();
		case SPLICE:
			return CustomImageFilter.getSpliceFilter();
		case CUSTOM:
			return CustomImageFilter.getCustomFilter();
		case DILATION:
			return CustomImageFilter.getDilationFilter();
		case EROSION:
			return CustomImageFilter.getErosionFilter();
		case OPENING:
			return CustomImageFilter.getOpeningFilter();
		case CLOSING:
			return CustomImageFilter.getClosingFilter();
		case HIT_OR_MISS:
			return CustomImageFilter.getHitOrMissFilter();
		case FIND_COLOR:
			return CustomImageFilter.getFindColorFilter((int) size[0], (int) size[1], (int) size[2], (int) size[3],
					(int) size[4]);
		case FIND_COLOR_CREEPING:
			return CustomImageFilter.getFindColorCreepingFilter((int) size[0], (int) size[1], (int) size[2],
					(int) size[3], (int) size[4]);
		default:
			return null;
		}
	}

	public static CustomImageFilter getAverageFilter(int size) {
		return new CustomImageFilter(getAverageFilterMask(size), Filters.AVERAGE);
	}

	public static float[][] getAverageFilterMask(int size) {
		if (size % 2 == 0) {
			size++;
		}

		float[][] mask = new float[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				mask[i][j] = 1;
			}
		}

		return normalize(mask);
	}

	public static CustomImageFilter getEdgeFinderFilter() {
		return new CustomImageFilter(getEdgeFinderFilterMask(), Filters.EDGEFINDER);
	}

	public static float[][] getEdgeFinderFilterMask() {
		float[][] mask = new float[3][];
		mask[0] = new float[] { -1, -1, -1 };
		mask[1] = new float[] { -1, 8, -1 };
		mask[2] = new float[] { -1, -1, -1 };

		return mask;
	}

	public static CustomImageFilter getSharpenFilter() {
		return new CustomImageFilter(getSharpenFilterMask(), Filters.HIGHPASS);
	}

	public static float[][] getSharpenFilterMask() {
		float[][] mask = new float[3][];
		mask[0] = new float[] { 0, -1, 0 };
		mask[1] = new float[] { -1, 5, -1 };
		mask[2] = new float[] { 0, -1, 0 };

		return mask;
	}

	public static CustomImageFilter getGaussFilter() {
		return new CustomImageFilter(getGaussFilterMask(), Filters.GAUSS);
	}

	public static float[][] getGaussFilterMask() {
		float[][] mask = new float[5][];
		mask[0] = new float[] { 1, 3, 7, 4, 1 };
		mask[1] = new float[] { 4, 16, 26, 16, 4 };
		mask[2] = new float[] { 7, 26, 41, 26, 7 };
		mask[3] = new float[] { 4, 16, 26, 16, 4 };
		mask[4] = new float[] { 1, 4, 7, 4, 1 };

		return normalize(mask);
	}

	public static CustomImageFilter getLeftSobelFilter() {
		return new CustomImageFilter(getLeftSobelFilterMask(), Filters.SOBELLEFT);
	}

	public static float[][] getLeftSobelFilterMask() {
		float[][] mask = new float[3][];
		mask[0] = new float[] { 1, 0, -1 };
		mask[1] = new float[] { 2, 0, -2 };
		mask[2] = new float[] { 1, 0, -1 };

		return mask;
	}

	public static CustomImageFilter getRightSobelFilter() {
		return new CustomImageFilter(getRightSobelFilterMask(), Filters.SOBELRIGHT);
	}

	public static float[][] getRightSobelFilterMask() {
		float[][] mask = new float[3][];
		mask[0] = new float[] { -1, 0, 1 };
		mask[1] = new float[] { -2, 0, 2 };
		mask[2] = new float[] { -1, 0, 1 };

		return mask;
	}

	public static CustomImageFilter getTopSobelFilter() {
		return new CustomImageFilter(getTopSobelFilterMask(), Filters.SOBELTOP);
	}

	public static float[][] getTopSobelFilterMask() {
		float[][] mask = new float[3][];
		mask[0] = new float[] { 1, 2, 1 };
		mask[1] = new float[] { 0, 0, 0 };
		mask[2] = new float[] { -1, -2, -1 };

		return mask;
	}

	public static CustomImageFilter getBottomSobelFilter() {
		return new CustomImageFilter(getBottomSobelFilterMask(), Filters.SOBELBOTTOM);
	}

	public static float[][] getBottomSobelFilterMask() {
		float[][] mask = new float[3][];
		mask[0] = new float[] { -1, -2, -1 };
		mask[1] = new float[] { 0, 0, 0 };
		mask[2] = new float[] { 1, 2, 1 };

		return mask;
	}

	public static CustomImageFilter getMedianFilter(int size) {
		return new CustomImageFilter(getMedianFilterMask(size), Filters.MEDIAN);
	}

	private static float[][] getMedianFilterMask(int size) {
		if (size % 2 == 0) {
			size++;
		}

		float[][] mask = new float[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				mask[i][j] = 1;
			}
		}

		return normalize(mask);
	}

	public static CustomImageFilter getSpeckleFilter() {
		return new CustomImageFilter(getSpeckleFilterMask(), Filters.SPECKLE);
	}

	private static float[][] getSpeckleFilterMask() {
		float[][] mask = new float[1][1];
		mask[0][0] = 1;

		return mask;
	}

	public static CustomImageFilter getSpliceFilter() {
		return new CustomImageFilter(getSpliceFilterMask(), Filters.SPLICE);
	}

	private static float[][] getSpliceFilterMask() {
		// TODO Auto-generated method stub
		return null;
	}

	public static CustomImageFilter getGrayscaleFilter() {
		return new CustomImageFilter(getGrayscaleFilterMask(), Filters.GRAYSCALE);
	}

	private static float[][] getGrayscaleFilterMask() {
		int size = 3;

		float[][] mask = new float[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				mask[i][j] = 1;
			}
		}

		return mask;
	}

	public static CustomImageFilter getGrayscaleMeanFilter() {
		return new CustomImageFilter(getGrayscaleMeanFilterMask(), Filters.GRAYSCALEMEAN);
	}

	private static float[][] getGrayscaleMeanFilterMask() {
		int size = 3;

		float[][] mask = new float[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				mask[i][j] = 1;
			}
		}

		return mask;
	}

	public static CustomImageFilter getBrightnessFilter(int brightness) {
		return new CustomImageFilter(getBrightnessFilterMask(brightness), Filters.BRIGHTNESS);
	}

	private static float[][] getBrightnessFilterMask(int brightness) {
		float[][] mask = new float[1][1];
		mask[0][0] = brightness;

		return mask;
	}

	public static CustomImageFilter getColorFilter(int r, int g, int b) {
		return new CustomImageFilter(getColorFilterMask(r, g, b), Filters.COLOR);
	}

	private static float[][] getColorFilterMask(int r, int g, int b) {
		float[][] mask = new float[1][3];
		mask[0][0] = r;
		mask[0][1] = g;
		mask[0][2] = b;

		return mask;
	}

	public static CustomImageFilter getCustomFilter() {
		return new CustomImageFilter(getCustomFilterMask(), Filters.CUSTOM);
	}

	private static float[][] getCustomFilterMask() {
		float[][] mask = new float[3][3];
		mask[0] = new float[] { 0F, 0F, 0F };
		mask[1] = new float[] { 0F, 1F, 0F };
		mask[2] = new float[] { 0F, 0F, 0F };

		return mask;
	}

	public static CustomImageFilter getDilationFilter() {
		return new CustomImageFilter(getCustomFilterMask(), Filters.DILATION);
	}

	public static CustomImageFilter getErosionFilter() {
		return new CustomImageFilter(getCustomFilterMask(), Filters.EROSION);
	}

	public static CustomImageFilter getOpeningFilter() {
		return new CustomImageFilter(getCustomFilterMask(), Filters.OPENING);
	}

	public static CustomImageFilter getClosingFilter() {
		return new CustomImageFilter(getCustomFilterMask(), Filters.CLOSING);
	}

	public static CustomImageFilter getHitOrMissFilter() {
		return new CustomImageFilter(getCustomFilterMask(), Filters.HIT_OR_MISS);
	}

	public static CustomImageFilter getFindColorFilter(int r, int g, int b, int tolerance, int size) {
		if (size % 2 == 0) {
			size++;
		}
		return new CustomImageFilter(getFindColorFilterMask(r, g, b, tolerance, size), Filters.FIND_COLOR, size);
	}

	private static float[][] getFindColorFilterMask(int r, int g, int b, int tolerance, int size) {
		float[][] mask = new float[size + 1][4];
		mask[size][0] = r;
		mask[size][1] = g;
		mask[size][2] = b;
		mask[size][3] = tolerance;

		return mask;
	}

	public static CustomImageFilter getFindColorCreepingFilter(int r, int g, int b, int tolerance, int size) {
		if (size % 2 == 0) {
			size++;
		}
		return new CustomImageFilter(getFindColorCreepingFilterMask(r, g, b, tolerance, size),
				Filters.FIND_COLOR_CREEPING, size).setComplex();
	}

	private static float[][] getFindColorCreepingFilterMask(int r, int g, int b, int tolerance, int size) {
		float[][] mask = new float[size + 1][4];
		mask[size][0] = r;
		mask[size][1] = g;
		mask[size][2] = b;
		mask[size][3] = tolerance;

		return mask;
	}
}
