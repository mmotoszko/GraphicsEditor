/*******************************************************************************
 * Micha³ Motoszko
 * 2018-11-27
 * Grafika komputerowa PS 9
 * Projekt 9
 ******************************************************************************/
package com.mmotoszko.gk.project9.file;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.mmotoszko.gk.project9.GraphicsEditor;
import com.mmotoszko.gk.project9.drawing.Point2D;
import com.mmotoszko.gk.project9.drawing.customshape.CustomImage;

public class ImageLoaderThread implements Runnable {
	GraphicsEditor ge;

	public ImageLoaderThread(GraphicsEditor graphicsEditor) {
		this.ge = graphicsEditor;
	}
	
	@Override
	public void run() {
		final JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Import image");
		FileFilter filter = new FileNameExtensionFilter("*.jpeg, *.jpg, *.ppm", new String[] {"jpeg", "jpg", "ppm"});
		fc.setFileFilter(filter);
		int returnVal = fc.showOpenDialog(fc);

		ge.cursor.setCursorWait();

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();

			if (!FileConverter.canBeRead(file)) {
				JOptionPane.showMessageDialog(ge.mainPanel,
						"Unsupported file format \"" + FileConverter.getLiteralFileExtension(file) + "\".",
						"File format error", JOptionPane.ERROR_MESSAGE);
				ge.cursor.setCursorDefault();
				return;
			}

			CustomImage image = FileConverter.getImage(file, new Point2D(-ge.lookOffset.getX(), -ge.lookOffset.getY()));

			if (image == null) {
				JOptionPane.showMessageDialog(ge.mainPanel, "Failed to retreive image from file.", "File format error",
						JOptionPane.ERROR_MESSAGE);
			} else {
				ge.addShape(image);
			}
		} else {
			System.out.print("Import dialog cancelled by user.");
		}
		ge.cursor.setCursorDefault();
	}
}
