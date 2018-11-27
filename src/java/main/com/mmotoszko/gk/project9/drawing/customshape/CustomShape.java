/*******************************************************************************
 * Micha³ Motoszko
 * 2018-11-27
 * Grafika komputerowa PS 9
 * Projekt 9
 ******************************************************************************/
package com.mmotoszko.gk.project9.drawing.customshape;

import java.awt.Color;
import java.io.Serializable;
import java.util.List;

import com.mmotoszko.gk.project9.drawing.Point2D;
import com.mmotoszko.gk.project9.drawing.Point3D;
import com.mmotoszko.gk.project9.drawing.ShapeType;

public interface CustomShape extends Serializable {
	public static final double TOUCHING_DISTANCE = 4;
	public static final double STRICT_TOUCHING_DISTANCE = 0.3;

	public boolean isType(ShapeType type);

	public void move(int x, int y);
	
	public void move(double x, double y);

	public void move(int x, int y, int z);

	public void move(Point2D vector);

	public void move(Point3D vector);

	public void scale(Point2D focusedVertice, double scaleX, double scaleY);

	public void rotate(int xRotation, int yRotation);

	public void rotate(int xRotation, int yRotation, int zRotation);

	public void rotate(Point2D mouseLocation);

	public void startRotating(Point2D pivot, Point2D mouseLocation);

	public void stopRotating();

	void calculateCenter();

	public void setCenter(Point2D newCenter);

	public int getX();

	public int getY();

	public int getZ();

	void calculateDimensions();

	Point2D getTopVertice();

	Point2D getBottomVertice();

	Point2D getLeftVertice();

	Point2D getRightVertice();

	public Point2D getTopLeftPosition();

	public Point2D getBottomRightPosition();

	public Point2D getNextVertice(Point2D vertice);

	public Point2D getPreviousVertice(Point2D vertice);

	void setVertice(int index, Point2D newVertice);

	void calculateHeight();

	public void updateVerticesToHeight(int heightValue);

	void calculateWidth();

	public void updateVerticesToWidth(int widthValue);

	public void setRadius(int newRadius);

	public Point2D getV(int index);

	public Point2D getCenter();

	public double getRadius();

	public int getWidth();

	public int getHeight();

	public int updateShape(Point2D newPoint);

	public void updateLastVertice(Point2D newPoint);

	public Point2D updateVertice(Point2D vertice, Point2D newLocation);

	public ShapeType checkShape();

	public List<Point2D> getVertices();

	public int getVerticeCount();

	public boolean isComplete();

	public void forceComplete();

	public Point2D getVertice(Point2D location);

	public boolean arePointsTouching(Point2D point1, Point2D point2);

	public boolean isTouching(Point2D location);

	public boolean needsRedrawing();

	public void redraw();

	public void completedRedrawing();

	public ShapeType getType();

	public void setType(ShapeType type);

	public void setColor(Color color);

	public Color getColor();

	public void setFill(boolean fill);

	public boolean getFill();
}
