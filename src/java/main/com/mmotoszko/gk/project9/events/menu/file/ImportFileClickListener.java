/*******************************************************************************
 * Micha³ Motoszko
 * 2018-11-27
 * Grafika komputerowa PS 9
 * Projekt 9
 ******************************************************************************/
package com.mmotoszko.gk.project9.events.menu.file;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.mmotoszko.gk.project9.GraphicsEditor;
import com.mmotoszko.gk.project9.file.ImageLoaderThread;

public class ImportFileClickListener implements ActionListener {
	private final GraphicsEditor ge;

	public ImportFileClickListener(GraphicsEditor graphicsEditor) {
		super();
		this.ge = graphicsEditor;
	}

	public void actionPerformed(ActionEvent e) {
		ImageLoaderThread imageLoader = new ImageLoaderThread(ge);
		new Thread(imageLoader).start();
	}
}
