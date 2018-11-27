/*******************************************************************************
 * Micha³ Motoszko
 * 2018-11-27
 * Grafika komputerowa PS 9
 * Projekt 9
 ******************************************************************************/
package com.mmotoszko.gk.project9.events.menu.edit;

import com.mmotoszko.gk.project9.GraphicsEditor;
import com.mmotoszko.gk.project9.drawing.customshape.CustomImage;
import com.mmotoszko.gk.project9.ui.dialogs.HistogramDialog;

public class HistogramPreviewRefreshRunner implements Runnable {
	private static final long REFRESH_WAIT_TIME = 60;
	private final GraphicsEditor ge;
	private CustomImage image;
	private boolean dirty;
	private HistogramDialog dialog;
	private long timer;

	public HistogramPreviewRefreshRunner(GraphicsEditor graphicsEditor, CustomImage image, HistogramDialog dialog) {
		this.ge = graphicsEditor;
		this.dirty = false;
		this.image = image;
		this.dialog = dialog;
		this.timer = System.nanoTime();
	}

	@Override
	public void run() {
		while (true) {
			if (dirty && System.nanoTime() - timer > REFRESH_WAIT_TIME) {
				dirty = false;
				image.removeHistogramChanges();
				image.previewHistogram(dialog.getHistogram());
				dialog.plotRefreshLock = false;
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

	public void update() {
		dirty = true;
		timer = System.nanoTime();
	}
}
