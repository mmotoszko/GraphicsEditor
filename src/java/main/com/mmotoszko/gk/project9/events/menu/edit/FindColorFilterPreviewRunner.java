/*******************************************************************************
 * Micha³ Motoszko
 * 2018-11-27
 * Grafika komputerowa PS 9
 * Projekt 9
 ******************************************************************************/
package com.mmotoszko.gk.project9.events.menu.edit;

import java.awt.Color;

import com.mmotoszko.gk.project9.GraphicsEditor;
import com.mmotoszko.gk.project9.drawing.Filters;
import com.mmotoszko.gk.project9.drawing.Functions2D;
import com.mmotoszko.gk.project9.drawing.customshape.CustomImage;
import com.mmotoszko.gk.project9.drawing.customshape.CustomImageFilter;

public class FindColorFilterPreviewRunner implements Runnable {
	private static final long REFRESH_WAIT_TIME = 1000;
	private final GraphicsEditor ge;
	private CustomImage image;
	private boolean dirty;
	private long timer;
	private Color color;
	private int tolerance;
	private int size;
	private Filters filterType;
	private boolean filtering;
	private float[][] openingMask = new float[][] { new float[] { 1f, 1f, 1f }, new float[] { 1f, 1f, 1f },
			new float[] { 1f, 1f, 1f } };

	public FindColorFilterPreviewRunner(GraphicsEditor graphicsEditor, CustomImage image) {
		this.ge = graphicsEditor;
		this.dirty = false;
		this.timer = System.nanoTime();
		this.image = image;
	}

	@Override
	public void run() {
		while (true) {
			if (dirty && System.nanoTime() - timer > REFRESH_WAIT_TIME) {
				dirty = false;
				byte[] rgb = Functions2D.getRGBValues(Functions2D.getColor(color));

				image.removePreviewFilter();

				if (filtering) {
					image.previewFilter(new CustomImageFilter(false, openingMask, Filters.DILATION));
					image.previewFilter(new CustomImageFilter(false, openingMask, Filters.EROSION));
					image.previewFilter(CustomImageFilter.getFilter(filterType, Functions2D.getValue(rgb[0]),
							Functions2D.getValue(rgb[1]), Functions2D.getValue(rgb[2]), tolerance, size));
				} else {
					image.previewFilter(CustomImageFilter.getFilter(filterType, Functions2D.getValue(rgb[0]),
							Functions2D.getValue(rgb[1]), Functions2D.getValue(rgb[2]), tolerance, size));
				}
				ge.refresh();
				try {
					Thread.sleep(REFRESH_WAIT_TIME);
				} catch (InterruptedException e) {
				}
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		}
	}

	public void update(Color color, int tolerance, int size, Filters filterType) {
		this.color = color;
		this.tolerance = tolerance;
		this.size = size;
		this.filtering = false;
		this.filterType = filterType;
		dirty = true;
		timer = System.nanoTime();
	}

	public void update(Color color, int tolerance, int size, boolean filtering) {
		this.color = color;
		this.tolerance = tolerance;
		this.size = size;
		this.filtering = filtering;
		this.filterType = Filters.FIND_COLOR;
		dirty = true;
		timer = System.nanoTime();
	}

	public void updateClear() {
		image.removePreviewFilter();
		ge.refresh();
		timer = System.nanoTime();
	}
}
