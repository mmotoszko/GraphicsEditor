/*******************************************************************************
 * Micha³ Motoszko
 * 2018-11-27
 * Grafika komputerowa PS 9
 * Projekt 9
 ******************************************************************************/
package com.mmotoszko.gk.project9.drawing.customshape;

import com.mmotoszko.gk.project9.drawing.Functions2D;
import com.mmotoszko.gk.project9.drawing.Point2D;
import com.mmotoszko.gk.project9.drawing.ShapeType;

public class CustomImage extends CustomShape2D {
	private static final long serialVersionUID = 1L;
	private byte[] graphic;
	private byte[] tempGraphic;
	public int graphicWidth;
	public int graphicHeight;
	public int tempGraphicWidth;
	public int tempGraphicHeight;

	public CustomImage(Point2D startPoint, int width, int height, byte[] graphic, int graphicWidth, int graphicHeight)
			throws Exception {
		super(startPoint, width, height, ShapeType.RECTANGLE);
		this.fill = true;
		this.type = ShapeType.IMAGE;
		this.graphic = graphic.clone();
		this.tempGraphic = null;
		this.graphicWidth = graphicWidth;
		this.graphicHeight = graphicHeight;
		this.tempGraphicWidth = 0;
		this.tempGraphicHeight = 0;
	}

	public CustomImage(CustomImage image) {
		super(image.getCenter(), image.width, image.height, ShapeType.RECTANGLE);
		this.fill = image.fill;
		this.type = ShapeType.IMAGE;
		this.graphic = image.graphic.clone();
		this.tempGraphic = image.tempGraphic.clone();
		this.graphicWidth = image.graphicWidth;
		this.graphicHeight = image.graphicHeight;
		this.tempGraphicWidth = image.tempGraphicWidth;
		this.tempGraphicHeight = image.tempGraphicHeight;
	}

	public byte[] getGraphic(int width, int height) {
		if (width == tempGraphicWidth && height == tempGraphicHeight && tempGraphic != null) {
			return tempGraphic.clone();
		} else {
			tempGraphicWidth = width;
			tempGraphicHeight = height;
			tempGraphic = Functions2D.getScaledGraphic(graphic, graphicWidth, graphicHeight, width, height);
			return tempGraphic.clone();
		}
	}

	public byte[] getTempGraphic() {
		return tempGraphic.clone();
	}

	public void previewFilter(CustomImageFilter filter) {
		if (filter == null) {
			return;
		}
		
		if (filter.isComplex()) {
			previewComplexFilter(filter);
			return;
		}

		int[][] imageGraphic = Functions2D.getRGBIntGraphic(tempGraphic, tempGraphicWidth, tempGraphicHeight);

		int index = 0;
		for (int row = 0; row < tempGraphicHeight; row++) {
			for (int col = 0; col < tempGraphicWidth; col++) {
				byte[] rgb = Functions2D.getRGBValues(filter.getValue(imageGraphic, col, row));
				tempGraphic[index] = rgb[0];
				tempGraphic[index + 1] = rgb[1];
				tempGraphic[index + 2] = rgb[2];
				index += 3;
			}
		}
	}

	public void previewComplexFilter(CustomImageFilter filter) {
		if (filter == null) {
			return;
		}

		int[][] imageGraphic = Functions2D.getRGBIntGraphic(tempGraphic, tempGraphicWidth, tempGraphicHeight);

		int[][] newGraphic = filter.getGraphic(imageGraphic);

		int index = 0;
		for (int row = 0; row < tempGraphicHeight; row++) {
			for (int col = 0; col < tempGraphicWidth; col++) {
				byte[] rgb = Functions2D.getRGBValues(newGraphic[col][row]);
				tempGraphic[index] = rgb[0];
				tempGraphic[index + 1] = rgb[1];
				tempGraphic[index + 2] = rgb[2];
				index += 3;
			}
		}
	}

	public byte[] getRGBVales(int x, int y) {
		byte r = graphic[y * graphicWidth * 3 + x * 3];
		byte g = graphic[y * graphicWidth * 3 + x * 3 + 1];
		byte b = graphic[y * graphicWidth * 3 + x * 3 + 2];

		return new byte[] { r, g, b };
	}

	public void previewHistogram(Histogram histogram) {
		tempGraphic = histogram.getModifiedGraphic(tempGraphic);
		histogram.updateValues();
	}

	public void removePreviewFilter() {
		tempGraphic = Functions2D.getScaledGraphic(graphic, graphicWidth, graphicHeight, tempGraphicWidth,
				tempGraphicHeight);
	}

	public void applyPreviewFilter() {
		graphicWidth = tempGraphicWidth;
		graphicHeight = tempGraphicHeight;
		graphic = tempGraphic.clone();
	}

	public void removeHistogramChanges() {
		removePreviewFilter();
	}

	public void applyHistogramChanges() {
		applyPreviewFilter();
	}
}
