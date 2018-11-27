/*******************************************************************************
 * Micha³ Motoszko
 * 2018-11-27
 * Grafika komputerowa PS 9
 * Projekt 9
 ******************************************************************************/
package com.mmotoszko.gk.project9.drawing.customshape;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.mmotoszko.gk.project9.drawing.Functions2D;
import com.mmotoszko.gk.project9.drawing.Point2D;
import com.mmotoszko.gk.project9.drawing.Point3D;
import com.mmotoszko.gk.project9.drawing.ShapeType;

public class CustomShape3D implements CustomShape {
	private static final long serialVersionUID = 1L;
	public ShapeType type = ShapeType.INCOMPLETE;
	public ArrayList<Point3D> vertices = new ArrayList<>();
	public Color color = Color.BLACK;
	public boolean fill = false;
	public boolean dirty = true;
	public Point2D rotationOrigin = new Point2D(0, 0);
	public int x = 0;
	public int y = 0;
	public double xRot = 0;
	public double yRot = 0;
	public int height = 0;
	public int width = 0;
	public int z = 0;
	public double zRot = 0;
	public int depth = 0;

	public boolean isType(ShapeType type) {
		return this.type == type;
	}

	public Point3D getPoint3D(Point2D point2D) {
		return new Point3D(point2D.getX(), point2D.getY(), 0);
	}

	public void addVertice(Point2D point2D) {
		vertices.add(getPoint3D(point2D));
	}

	public void addVertice(Point3D point3D) {
		vertices.add(point3D);
	}

	public void setVertice(int index, Point2D point2D) {
		Point3D v = vertices.get(index);
		v.setLocation(point2D.getX(), point2D.getY(), 0);
	}

	public void setVertice(int index, Point3D point3D) {
		Point3D v = vertices.get(index);
		v.setLocation(point3D.getX(), point3D.getY(), point3D.z);
	}

	public CustomShape3D(Point2D startPoint, Point2D otherPoint, ShapeType type) {
		this.type = type;
		if (type == ShapeType.CUBE) {
			boolean useWidth = Math.abs(otherPoint.getX() - startPoint.getX()) > Math.abs(otherPoint.getY() - startPoint.getY());
			if (useWidth) {
				width = (int) Math.round(Math.abs(otherPoint.getX() - startPoint.getX()));
				height = width;
				depth = width;

			} else {
				height = (int) Math.round(Math.abs(otherPoint.getY() - startPoint.getY()));
				width = height;
				depth = height;
			}

			Point3D center = new Point3D(startPoint.getX() + width / 2, startPoint.getY() + width / 2, 0);
			this.x = center.x;
			this.y = center.y;
			this.z = center.z;

			addVertice(new Point3D(-width / 2, -height / 2, -depth / 2));
			addVertice(new Point3D(+width / 2, -height / 2, -depth / 2));
			addVertice(new Point3D(+width / 2, +height / 2, -depth / 2));
			addVertice(new Point3D(-width / 2, +height / 2, -depth / 2));
			addVertice(new Point3D(-width / 2, -height / 2, +depth / 2));
			addVertice(new Point3D(+width / 2, -height / 2, +depth / 2));
			addVertice(new Point3D(+width / 2, +height / 2, +depth / 2));
			addVertice(new Point3D(-width / 2, +height / 2, +depth / 2));
		} else {
			return;
		}

		calculateDimensions();
		redraw();
	}

	public CustomShape3D(Point3D center, int width, int height, int depth, ShapeType type) {
		this.type = type;
		this.width = width;
		this.height = height;
		this.depth = depth;
		this.x = center.x;
		this.y = center.y;
		this.z = center.z;
		if (type == ShapeType.CUBE) {
			addVertice(new Point3D(-width / 2, -height / 2, -depth / 2));
			addVertice(new Point3D(+width / 2, -height / 2, -depth / 2));
			addVertice(new Point3D(+width / 2, +height / 2, -depth / 2));
			addVertice(new Point3D(-width / 2, +height / 2, -depth / 2));
			addVertice(new Point3D(-width / 2, -height / 2, +depth / 2));
			addVertice(new Point3D(+width / 2, -height / 2, +depth / 2));
			addVertice(new Point3D(+width / 2, +height / 2, +depth / 2));
			addVertice(new Point3D(-width / 2, +height / 2, +depth / 2));
		} else {
			return;
		}

		calculateDimensions();
		redraw();
	}

	public void rotate(int xRotation, int yRotation, int zRotation) {
		xRot += xRotation;
		yRot += yRotation;
		zRot += zRotation;

		redraw();
	}

	public void rotate(Point2D mouseLocation) {
		Point2D vector = new Point2D(rotationOrigin.getX() - mouseLocation.getX(), rotationOrigin.getY() - mouseLocation.getY());

		xRot += vector.getY() * Math.PI / 128;
		yRot -= vector.getX() * Math.PI / 128;

		if (xRot >= Math.PI * 2 || xRot <= -Math.PI * 2) {
			xRot = 0;
		}

		if (yRot >= Math.PI * 2 || yRot <= -Math.PI * 2) {
			yRot = 0;
		}

		rotationOrigin = mouseLocation;
		redraw();
	}

	public void startRotating(Point2D pivot, Point2D mouseLocation) {
		rotationOrigin = mouseLocation;
	}

	public void setCenter(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;

		redraw();
	}

	public Point3D getCenter3D() {
		return new Point3D(x, y, z);
	}

	public void move(int x, int y, int z) {
		this.x += x;
		this.y += y;
		this.z += z;

		redraw();
	}

	@Override
	public void scale(Point2D referencePoint, double scaleX, double scaleY) {
		// TODO Auto-generated method stub
	}

	public Point3D[] getFrontFace() {
		if (type == ShapeType.CUBE) {
			return new Point3D[] { getV3D(0), getV3D(1), getV3D(2), getV3D(3) };
		}
		return null;
	}

	public Point3D[] getBackFace() {
		if (type == ShapeType.CUBE) {
			return new Point3D[] { getV3D(4), getV3D(5), getV3D(6), getV3D(7) };
		}
		return null;
	}

	public void calculateWidth() {
		int result = 0;
		result = Math.abs(getFrontFace()[1].x - getFrontFace()[0].x);

		width = result;
	}

	public void calculateHeight() {
		int result = 0;
		result = Math.abs(getFrontFace()[3].y - getFrontFace()[0].y);

		height = result;
	}

	public void calculateDepth() {
		int result = 0;
		result = Math.abs(getBackFace()[0].z - getFrontFace()[0].z);

		depth = result;
	}

	public void calculateDimensions() {
		calculateWidth();
		calculateHeight();
		calculateDepth();
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public double getXRotation() {
		return xRot;
	}

	public double getYRotation() {
		return yRot;
	}

	public double getZRotation() {
		return zRot;
	}

	@Override
	public void move(int x, int y) {
		move(x, y, 0);

	}

	@Override
	public void move(Point2D vector) {
		move(vector.getRoundX(), vector.getRoundY(), 0);

	}

	@Override
	public void move(Point3D vector) {
		move(vector.x, vector.y, vector.z);

	}

	@Override
	public void rotate(int xRotation, int yRotation) {
		rotate(xRotation, yRotation, 0);

	}

	@Override
	public void calculateCenter() {
		// Not required - center is always (x, y, z) and doesn't change unless
		// the shape is moved.
	}

	@Override
	public void setCenter(Point2D newCenter) {
		x = newCenter.getRoundX();
		y = newCenter.getRoundY();
	}

	@Override
	public Point2D getTopVertice() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Point2D getBottomVertice() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Point2D getLeftVertice() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Point2D getRightVertice() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Point2D getTopLeftPosition() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Point2D getBottomRightPosition() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getVerticeIndex(Point3D vertice) {
		return vertices.indexOf(vertice);
	}

	@Override
	public Point2D getNextVertice(Point2D vertice) {
		// Shouldn't be used
		return null;
	}

	@Override
	public Point2D getPreviousVertice(Point2D vertice) {
		// Shouldn't be used
		return null;
	}

	@Override
	public void updateVerticesToHeight(int heightValue) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateVerticesToWidth(int widthValue) {
		if (width != 0) {
			if (type == ShapeType.CUBE) {
				setRadius(widthValue);
			}
		}

		calculateDimensions();
		calculateCenter();
		redraw();
	}

	@Override
	public void setRadius(int newRadius) {
		width = newRadius;
		height = width;
		depth = width;
		setVertice(0, new Point3D(-width / 2, -height / 2, -depth / 2));
		setVertice(1, new Point3D(+width / 2, -height / 2, -depth / 2));
		setVertice(2, new Point3D(+width / 2, +height / 2, -depth / 2));
		setVertice(3, new Point3D(-width / 2, +height / 2, -depth / 2));
		setVertice(4, new Point3D(-width / 2, -height / 2, +depth / 2));
		setVertice(5, new Point3D(+width / 2, -height / 2, +depth / 2));
		setVertice(6, new Point3D(+width / 2, +height / 2, +depth / 2));
		setVertice(7, new Point3D(-width / 2, +height / 2, +depth / 2));
	}

	@Override
	public Point2D getV(int index) {
		// Shouldn't be used
		return null;
	}

	public Point3D getV3D(int index) {
		return vertices.get(index);
	}

	@Override
	public Point2D getCenter() {
		return new Point2D(x, y);
	}

	@Override
	public double getRadius() {
		return width / 2;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	public int getDepth() {
		return depth;
	}

	@Override
	public int updateShape(Point2D newPoint) {
		if (type == ShapeType.CUBE) {
			width = (int) Functions2D.distance(getCenter(), newPoint);
			height = width;
			depth = width;

			setVertice(0, new Point3D(-width / 2, -height / 2, -depth / 2));
			setVertice(1, new Point3D(+width / 2, -height / 2, -depth / 2));
			setVertice(2, new Point3D(+width / 2, +height / 2, -depth / 2));
			setVertice(3, new Point3D(-width / 2, +height / 2, -depth / 2));
			setVertice(4, new Point3D(-width / 2, -height / 2, +depth / 2));
			setVertice(5, new Point3D(+width / 2, -height / 2, +depth / 2));
			setVertice(6, new Point3D(+width / 2, +height / 2, +depth / 2));
			setVertice(7, new Point3D(-width / 2, +height / 2, +depth / 2));
		}

		calculateCenter();
		calculateDimensions();
		redraw();
		return vertices.size();
	}

	@Override
	public void updateLastVertice(Point2D newPoint) {
		// Souhldn't be used.
	}

	@Override
	public Point2D updateVertice(Point2D vertice, Point2D newLocation) {
		// Souhldn't be used.
		return null;
	}

	@Override
	public ShapeType checkShape() {
		// Souhldn't be used.
		return null;
	}

	@Override
	public List<Point2D> getVertices() {
		// Souhldn't be used.
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<Point3D> getVertices3D() {
		return (List<Point3D>) vertices.clone();
	}

	@Override
	public int getVerticeCount() {
		return vertices.size();
	}

	@Override
	public boolean isComplete() {
		// Souhldn't be used.
		return false;
	}

	@Override
	public void forceComplete() {
		// Souhldn't be used.
	}

	@Override
	public Point2D getVertice(Point2D location) {
		for (Point3D v : vertices) {
			Point2D point2D = new Point2D(v.x, v.y);
			if (Functions2D.distance(point2D, location) <= TOUCHING_DISTANCE) {
				return point2D;
			}
		}
		return null;
	}

	@Override
	public boolean arePointsTouching(Point2D point1, Point2D point2) {
		return Functions2D.distance(point1, point2) <= TOUCHING_DISTANCE;
	}

	@Override
	public boolean isTouching(Point2D location) {
		if (type == ShapeType.CUBE) {
			if (Functions2D.distance(getCenter(), location) <= getRadius() + TOUCHING_DISTANCE) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean needsRedrawing() {
		return dirty;
	}

	@Override
	public void redraw() {
		dirty = true;

	}

	@Override
	public void completedRedrawing() {
		dirty = false;

	}

	@Override
	public ShapeType getType() {
		return type;
	}

	@Override
	public void setType(ShapeType type) {
		this.type = type;

	}

	@Override
	public void setColor(Color color) {
		this.color = color;

	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public void setFill(boolean fill) {
		this.fill = fill;

	}

	@Override
	public boolean getFill() {
		return fill;
	}

	@Override
	public void stopRotating() {
		// TODO Auto-generated method stub

	}

	@Override
	public void move(double x, double y) {
		move((int) x, (int) y, 0);
	}
}
