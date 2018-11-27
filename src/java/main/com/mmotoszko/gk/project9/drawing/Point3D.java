/*******************************************************************************
 * Micha³ Motoszko
 * 2018-11-27
 * Grafika komputerowa PS 9
 * Projekt 9
 ******************************************************************************/
package com.mmotoszko.gk.project9.drawing;

import java.io.Serializable;

public class Point3D implements Serializable {
	private static final long serialVersionUID = 1L;
	public double preciseX = 0;
	public double preciseY = 0;
	public double preciseZ = 0;
	public int x = 0;
	public int y = 0;
	public int z = 0;

	public Point3D() {

	}

	public Point3D(int x, int y, int z) {
		preciseX = x;
		preciseY = y;
		preciseZ = z;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Point3D(double x, double y, double z) {
		preciseX = x;
		preciseY = y;
		preciseZ = z;
		this.x = (int) Math.round(preciseX);
		this.y = (int) Math.round(preciseY);
		this.z = (int) Math.round(preciseZ);
	}

	public void setLocation(int x, int y, int z) {
		preciseX = x;
		preciseY = y;
		preciseZ = z;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void setLocation(double x, double y, double z) {
		preciseX = x;
		preciseY = y;
		preciseZ = z;
		this.x = (int) Math.round(preciseX);
		this.y = (int) Math.round(preciseY);
		this.z = (int) Math.round(preciseZ);
	}

	public void move(int x, int y, int z) {
		preciseX = x;
		preciseY = y;
		preciseZ = z;
		this.x += x;
		this.y += y;
		this.z += z;
	}

	public void move(double x, double y, double z) {
		preciseX = x;
		preciseY = y;
		preciseZ = z;
		this.x = (int) Math.round(preciseX);
		this.y = (int) Math.round(preciseY);
		this.z = (int) Math.round(preciseZ);
	}

	public double getX() {
		return preciseX;
	}

	public double getY() {
		return preciseY;
	}

	public double getZ() {
		return preciseZ;
	}

	public int getRoundX() {
		return (int) Math.round(preciseX);
	}

	public int getRoundY() {
		return (int) Math.round(preciseY);
	}

	public int getRoundZ() {
		return (int) Math.round(preciseZ);
	}

	@Override
	public String toString() {
		return "x=" + x + ", y=" + y + ", z=" + z;
	}
}
