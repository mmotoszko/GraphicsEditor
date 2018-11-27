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

public class ColorPreviewRefreshRunner implements Runnable {
	private static final long REFRESH_WAIT_TIME = 60;
	private final GraphicsEditor ge;
	private CustomImage image;
	private boolean dirty;
	private long timer;
	private int r;
	private int g;
	private int b;

	public ColorPreviewRefreshRunner(GraphicsEditor graphicsEditor, CustomImage image) {
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
				image.previewFilter(CustomImageFilter.getColorFilter(r, g, b));
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

	public void update(int r, int g, int b) {
		this.r = r;
		this.g = g;
		this.b = b;
		dirty = true;
		timer = System.nanoTime();
	}
}
