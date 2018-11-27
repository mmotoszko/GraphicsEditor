/*******************************************************************************
 * Micha³ Motoszko
 * 2018-11-27
 * Grafika komputerowa PS 9
 * Projekt 9
 ******************************************************************************/
package com.mmotoszko.gk.project9.drawing;

import java.awt.Point;
import java.io.Serializable;

public class Point2D implements Serializable {
	private static final long serialVersionUID = 1L;
	private double preciseX;
	private double preciseY;
	private int x;
	private int y;

	public Point2D() {

	}

	public Point2D(Point2D Point2D) {
		this.preciseX = Point2D.getX();
		this.preciseY = Point2D.getY();
		this.x = getRoundX();
		this.y = getRoundY();
	}

	public Point2D(int x, int y) {
		this.preciseX = x;
		this.preciseY = y;
		this.x = getRoundX();
		this.y = getRoundY();
	}

	public Point2D(double x, double y) {
		this.preciseX = x;
		this.preciseY = y;
		this.x = getRoundX();
		this.y = getRoundY();
	}

	public Point2D(Point point) {
		this.preciseX = point.x;
		this.preciseY = point.y;
		this.x = getRoundX();
		this.y = getRoundY();
	}

	public void setLocation(int x, int y) {
		this.preciseX = x;
		this.preciseY = y;
		this.x = getRoundX();
		this.y = getRoundY();
	}

	public void setLocation(double x, double y) {
		this.preciseX = x;
		this.preciseY = y;
		this.x = getRoundX();
		this.y = getRoundY();
	}

	public void setLocation(Point point) {
		this.preciseX = point.x;
		this.preciseY = point.y;
		this.x = getRoundX();
		this.y = getRoundY();
	}

	public void setLocation(Point2D point) {
		this.preciseX = point.x;
		this.preciseY = point.y;
		this.x = getRoundX();
		this.y = getRoundY();
	}

	public void move(int x, int y) {
		this.preciseX += x;
		this.preciseY += y;
		this.x = getRoundX();
		this.y = getRoundY();
	}

	public void move(double x, double y) {
		this.preciseX += x;
		this.preciseY += y;
		this.x = getRoundX();
		this.y = getRoundY();
	}

	public void setX(double x) {
		preciseX = x;
		this.x = getRoundX();
	}

	public void setY(double y) {
		preciseY = y;
		this.y = getRoundY();
	}

	public double getX() {
		return preciseX;
	}

	public double getY() {
		return preciseY;
	}

	public int getRoundX() {
		return (int) Math.round(preciseX);
	}

	public int getRoundY() {
		return (int) Math.round(preciseY);
	}

	@Override
	public String toString() {
		return "x=" + x + ", y=" + y;
	}
}
