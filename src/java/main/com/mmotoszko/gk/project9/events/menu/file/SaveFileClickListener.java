/*******************************************************************************
 * Micha³ Motoszko
 * 2018-11-27
 * Grafika komputerowa PS 9
 * Projekt 9
 ******************************************************************************/
package com.mmotoszko.gk.project9.events.menu.file;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.mmotoszko.gk.project9.GraphicsEditor;
import com.mmotoszko.gk.project9.file.FileConverter;
import com.mmotoszko.gk.project9.file.FileExtension;

public class SaveFileClickListener implements ActionListener {
	private final GraphicsEditor ge;

	public SaveFileClickListener(GraphicsEditor graphicsEditor) {
		super();
		this.ge = graphicsEditor;
	}

	public void actionPerformed(ActionEvent e) {
		String fileType = FileExtension.MMGE.toString().toLowerCase();

		final JFileChooser fc = new JFileChooser();
		FileFilter filter = new FileNameExtensionFilter("*." + fileType, new String[] { fileType });
		fc.setFileFilter(filter);
		int returnVal = fc.showSaveDialog(fc);

		ge.cursor.setCursorWait();

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			if (!FileConverter.isMMGE(file)) {
				file = new File(file.getAbsolutePath() + '.' + fileType);
			}
			try (FileOutputStream fileOut = new FileOutputStream(file);
					ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
				out.writeObject(ge.getShapeList());
				out.close();
				fileOut.close();
			} catch (IOException i) {
				i.printStackTrace();
				JOptionPane.showMessageDialog(ge.mainPanel, "Failed to save workspace to file.", "Saving error",
						JOptionPane.ERROR_MESSAGE);
			}
		} else {
			System.out.print("Export dialog cancelled by user.");
		}
		ge.cursor.setCursorDefault();
	}
}
