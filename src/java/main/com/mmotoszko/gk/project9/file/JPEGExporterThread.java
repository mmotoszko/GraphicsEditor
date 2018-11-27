/*******************************************************************************
 * Micha³ Motoszko
 * 2018-11-27
 * Grafika komputerowa PS 9
 * Projekt 9
 ******************************************************************************/
package com.mmotoszko.gk.project9.file;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.FileImageOutputStream;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.mmotoszko.gk.project9.GraphicsEditor;
import com.mmotoszko.gk.project9.drawing.Functions2D;
import com.mmotoszko.gk.project9.ui.dialogs.JPEGQualityDialog;

public class JPEGExporterThread implements Runnable {
	private String fileType;
	GraphicsEditor ge;

	public JPEGExporterThread(GraphicsEditor graphicsEditor) {
		this.ge = graphicsEditor;
	}

	@Override
	public void run() {
		fileType = FileExtension.JPG.toString().toLowerCase();

		final JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Export image");
		FileFilter filter = new FileNameExtensionFilter("*." + fileType, new String[] { fileType });
		fc.setFileFilter(filter);
		int returnVal = fc.showSaveDialog(fc);

		ge.cursor.setCursorWait();

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			if (!FileConverter.isJPEG(file)) {
				file = new File(file.getAbsolutePath() + '.' + fileType);
			}

			JPEGQualityDialog qualityDialog = new JPEGQualityDialog(ge.mainPanel);

			JPEGImageWriteParam jpegParams = new JPEGImageWriteParam(null);
			jpegParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			jpegParams.setCompressionQuality(qualityDialog.getSliderScalarValue());

			BufferedImage image = Functions2D.getBufferedImage(ge.getShapeList(), ge.getWorkspace());

			if (image == null) {
				JOptionPane.showMessageDialog(ge.mainPanel, "Failed to save workspace into an image.",
						"Exporting error", JOptionPane.ERROR_MESSAGE);
			} else {
				try {
					final ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
					FileImageOutputStream inputStream = new FileImageOutputStream(file);
					writer.setOutput(inputStream);
					writer.write(null, new IIOImage(image, null, null), jpegParams);
					writer.dispose();
					inputStream.close();
					
				} catch (IOException e) {
					JOptionPane.showMessageDialog(ge.mainPanel, "Failed to parse workspace image into .jpg file.",
							"Exporting error", JOptionPane.ERROR_MESSAGE);
				}
			}
		} else {
			System.out.print("Export dialog cancelled by user.");
		}
		ge.cursor.setCursorDefault();
	}
}
