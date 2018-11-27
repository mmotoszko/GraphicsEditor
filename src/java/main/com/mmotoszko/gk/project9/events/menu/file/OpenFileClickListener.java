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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.mmotoszko.gk.project9.GraphicsEditor;
import com.mmotoszko.gk.project9.file.FileConverter;

public class OpenFileClickListener implements ActionListener {
	private final GraphicsEditor ge;

	public OpenFileClickListener(GraphicsEditor graphicsEditor) {
		super();
		this.ge = graphicsEditor;
	}

	public void actionPerformed(ActionEvent e) {

		final JFileChooser fc = new JFileChooser();
		FileFilter filter = new FileNameExtensionFilter("*.mmge", new String[] { "mmge" });
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

			try (FileInputStream fileIn = new FileInputStream(file);
					ObjectInputStream in = new ObjectInputStream(fileIn)) {

				ge.loadShapeList(in.readObject());
				in.close();
				fileIn.close();
			} catch (IOException i) {
				JOptionPane.showMessageDialog(ge.mainPanel, "Failed to read file.", "File error",
						JOptionPane.ERROR_MESSAGE);
				return;
			} catch (ClassNotFoundException c) {
				JOptionPane.showMessageDialog(ge.mainPanel, "Failed to parse file.", "File content error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		ge.cursor.setCursorDefault();
		ge.refresh();
	}
}
