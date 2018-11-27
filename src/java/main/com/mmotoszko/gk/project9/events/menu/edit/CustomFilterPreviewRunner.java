/*******************************************************************************
 * Micha³ Motoszko
 * 2018-11-27
 * Grafika komputerowa PS 9
 * Projekt 9
 ******************************************************************************/
package com.mmotoszko.gk.project9.events.menu.edit;

import com.mmotoszko.gk.project9.GraphicsEditor;
import com.mmotoszko.gk.project9.drawing.customshape.CustomImage;
import com.mmotoszko.gk.project9.drawing.customshape.CustomImageFilter;

public class CustomFilterPreviewRunner implements Runnable {
	private static final long REFRESH_WAIT_TIME = 1000;
	private final GraphicsEditor ge;
	private CustomImage image;
	private boolean dirty;
	private long timer;
	private boolean normalize;
	private float[][] mask;

	public CustomFilterPreviewRunner(GraphicsEditor graphicsEditor, CustomImage image) {
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
				image.removePreviewFilter();
				image.previewFilter(new CustomImageFilter(normalize, mask));
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

	public void update(boolean normalize, float[][] mask) {
		this.mask = mask;
		this.normalize = normalize;
		dirty = true;
		timer = System.nanoTime();
	}
}
