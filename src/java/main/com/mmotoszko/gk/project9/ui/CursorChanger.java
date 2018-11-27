/*******************************************************************************
 * Micha³ Motoszko
 * 2018-11-27
 * Grafika komputerowa PS 9
 * Projekt 9
 ******************************************************************************/
package com.mmotoszko.gk.project9.ui;

import java.awt.Cursor;

import javax.swing.JFrame;

public class CursorChanger {
	JFrame frame;
	int currentCursor;
	
	public CursorChanger (JFrame frame) {
		this.frame = frame;
		this.currentCursor = Cursor.DEFAULT_CURSOR;
	}
	
	public void setCursor(int cursorType) {
		frame.setCursor(Cursor.getPredefinedCursor(cursorType));
	}
	
	public void setCursorDefault() {
		setCursor(Cursor.DEFAULT_CURSOR);
	}
	
	public void setCursorWait() {
		setCursor(Cursor.WAIT_CURSOR);
	}
}
