/*******************************************************************************
 * Micha³ Motoszko
 * 2018-11-27
 * Grafika komputerowa PS 9
 * Projekt 9
 ******************************************************************************/
package com.mmotoszko.gk.project9.drawing.customshape;

import com.mmotoszko.gk.project9.drawing.Point2D;

public class Rotation {
	public Point2D pivot = new Point2D(0, 0);
	public double x = 0;
	public double y = 0;
	public double z = 0;

	public Rotation(Point2D pivot) {
		this.pivot = new Point2D(pivot);
	}

	public void rotate(double x, double y, double z) {
		this.x += x;
		this.y += y;
		this.z += z;

		if (x >= Math.PI * 2 || x <= -Math.PI * 2) {
			x = 0;
		}

		if (y >= Math.PI * 2 || y <= -Math.PI * 2) {
			y = 0;
		}

		if (z >= Math.PI * 2 || z <= -Math.PI * 2) {
			z = 0;
		}
	}

	public void reset() {
		x = 0;
		y = 0;
		z = 0;
	}
}
