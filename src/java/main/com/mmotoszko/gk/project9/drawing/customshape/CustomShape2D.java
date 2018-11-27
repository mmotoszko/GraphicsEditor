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

public class CustomShape2D implements CustomShape {
	private static final long serialVersionUID = 1L;
	public ShapeType type;
	public ArrayList<Point2D> vertices = new ArrayList<>();
	public Color color = Color.BLACK;
	public boolean fill = false;
	public boolean dirty = true;
	public Point2D rotationOrigin = new Point2D(0, 0);
	public int x = 0;
	public int y = 0;
	public double xRot = 0;
	public double yRot = 0;
	public double zRot = 0;
	public int height = 0;
	public int width = 0;
	public Rotation rotation = null;

	protected CustomShape2D() {

	}

	public CustomShape2D(Point2D center, Point2D Point2D) {
		type = ShapeType.CIRCLE;
		vertices.add(center);
		vertices.add(Point2D);
		calculateCenter();
		calculateDimensions();
		redraw();
	}

	public CustomShape2D(Point2D startPoint, Point2D secondPoint, Point2D thirdPoint) {
		type = ShapeType.TRIANGLE;
		vertices.add(startPoint);
		vertices.add(secondPoint);
		vertices.add(thirdPoint);
		calculateCenter();
		calculateDimensions();
		redraw();
	}

	public CustomShape2D(Point2D startPoint, Point2D otherPoint, ShapeType type) {
		this.type = type;
		if (type == ShapeType.RECTANGLE || type == ShapeType.IMAGE) {
			vertices.add(startPoint);
			vertices.add(new Point2D(startPoint.getX(), otherPoint.getY()));
			vertices.add(otherPoint);
			vertices.add(new Point2D(otherPoint.getX(), startPoint.getY()));
		} else if (type == ShapeType.SQUARE) {
			vertices.add(startPoint);
			int width = 0;
			if (Math.abs(otherPoint.getX() - startPoint.getX()) > Math.abs(otherPoint.getY() - startPoint.getY())) {
				width = (int) Math.round(otherPoint.getX() - startPoint.getX());
			} else {
				width = (int) Math.round(otherPoint.getY() - startPoint.getY());
			}
			vertices.add(new Point2D(startPoint.getX(), startPoint.getY() + width));
			vertices.add(new Point2D(startPoint.getX() + width, startPoint.getY() + width));
			vertices.add(new Point2D(otherPoint.getX() + width, startPoint.getY()));
		} else if (type == ShapeType.CIRCLE) {
			vertices.add(startPoint);
			vertices.add(otherPoint);
		} else if (type == ShapeType.INCOMPLETE || type == ShapeType.INCOMPLETE_TRIANGLE) {
			vertices.add(startPoint);
			vertices.add(otherPoint);
		} else if (type == ShapeType.LINE) {
			vertices.add(startPoint);
			vertices.add(otherPoint);
		} else if (type == ShapeType.FREE_LINE) {
			vertices.add(startPoint);
		} else if (type == ShapeType.INCOMPLETE_BEZIER_CURVE) {
			vertices.add(startPoint);
			vertices.add(otherPoint);
			redraw();
			return;
		} else {
			return;
		}

		calculateCenter();
		calculateDimensions();
		redraw();
	}

	public CustomShape2D(Point2D startPoint, int width, int height, ShapeType type) {
		this.type = type;
		if (type == ShapeType.RECTANGLE || type == ShapeType.IMAGE) {
			vertices.add(startPoint);
			vertices.add(new Point2D(startPoint.getX(), startPoint.getY() + height));
			vertices.add(new Point2D(startPoint.getX() + width, startPoint.getY() + height));
			vertices.add(new Point2D(startPoint.getX() + width, startPoint.getY()));
		} else if (type == ShapeType.SQUARE) {
			vertices.add(startPoint);
			vertices.add(new Point2D(startPoint.getX(), startPoint.getY() + width));
			vertices.add(new Point2D(startPoint.getX() + width, startPoint.getY() + width));
			vertices.add(new Point2D(startPoint.getX() + width, startPoint.getY()));
		} else if (type == ShapeType.CIRCLE) {
			vertices.add(startPoint);
			vertices.add(new Point2D(startPoint.getX() + width / 2, startPoint.getY()));
		} else if (type == ShapeType.LINE) {
			vertices.add(startPoint);
			vertices.add(new Point2D(startPoint.getX() + width, startPoint.getY() + width));
		} else {
			System.out.println(this);
			return;
		}

		calculateCenter();
		calculateDimensions();
		redraw();
	}

	public void move(int x, int y) {
		for (Point2D p : vertices) {
			p.move(x, y);
		}

		if (isType(ShapeType.BEZIER_CURVE)) {
			redraw();
			return;
		}

		calculateCenter();
		calculateDimensions();
		redraw();
	}

	public void move(Point2D vector) {
		for (Point2D p : vertices) {
			p.move(vector.getX(), vector.getY());
		}

		if (isType(ShapeType.BEZIER_CURVE)) {
			redraw();
			return;
		}

		calculateCenter();
		calculateDimensions();
		redraw();
	}

	@Override
	public void move(int x, int y, int z) {
		move(x, y);
	}

	@Override
	public void move(Point3D vector) {
		move(vector.getX(), vector.getY());
	}

	@Override
	public void move(double x, double y) {
		for (Point2D p : vertices) {
			p.move(x, y);
		}

		if (isType(ShapeType.BEZIER_CURVE)) {
			redraw();
			return;
		}

		calculateCenter();
		calculateDimensions();
		redraw();
	}

	@Override
	public void scale(Point2D referencePoint, double scaleX, double scaleY) {
		int newWidth = (int) Math.round(width * scaleX);
		int newHeight = (int) Math.round(height * scaleY);

		if (referencePoint == null) {
			referencePoint = getCenterPoint();
		}

		Point2D vector = new Point2D(x - referencePoint.getX(), y - referencePoint.getY());

		setCenter(new Point2D((int) Math.round(x + vector.getX() * (scaleX - 1)),
				(int) Math.round(y + vector.getY() * (scaleY - 1))));

		updateVerticesToWidth(newWidth);
		updateVerticesToHeight(newHeight);
	}

	public boolean isType(ShapeType type) {
		return this.type == type;
	}

	public void rotate(int xRotation, int yRotation) {
		xRot += xRotation;
		yRot += yRotation;

		calculateCenter();
		calculateDimensions();
		redraw();
	}

	public void rotate(Point2D mouseLocation) {
		Point2D vector = new Point2D(rotationOrigin.getX() - mouseLocation.getX(),
				rotationOrigin.getY() - mouseLocation.getY());

		double zRotation = vector.getY() * Math.PI / 256 + vector.getX() * Math.PI / 256;

		rotation.rotate(0, 0, zRotation);

		rotationOrigin = mouseLocation;
		redraw();
	}

	public void startRotating(Point2D pivot, Point2D mouseLocation) {
		if (pivot == null) {
			pivot = getCenterPoint();
		}

		rotation = new Rotation(pivot);
		rotationOrigin = mouseLocation;
	}

	public void stopRotating() {
		vertices = (ArrayList<Point2D>) getRotatedVertices();
		rotation = null;
	}

	public Point2D getCenterPoint() {
		if (isType(ShapeType.CIRCLE)) {
			return new Point2D(x, y);
		}

		Point2D centerPoint = new Point2D(0, 0);
		for (Point2D v : vertices) {
			centerPoint.setLocation(centerPoint.getX() + v.getX(), centerPoint.getY() + v.getY());
		}

		centerPoint.setLocation(centerPoint.getX() / vertices.size(), centerPoint.getY() / vertices.size());

		return centerPoint;
	}

	public void calculateCenter() {
		if (type == ShapeType.CIRCLE) {
			System.out.println("Centering from x: " + x + "  y: " + y);
			x = getV(0).getRoundX();
			y = getV(0).getRoundY();
			System.out.println("to x: " + x + "  y: " + y);
		} else {
			x = getLeftVertice().getRoundX();
			y = getLeftVertice().getRoundY();
		}
	}

	public void setCenter(Point2D newCenter) {
		Point2D vector;
		if (type == ShapeType.CIRCLE) {
			vector = Functions2D.getVector(getV(0), newCenter);
		} else {
			vector = Functions2D.getVector(getLeftVertice(), newCenter);
		}
		move(vector);

		calculateCenter();
	}

	public int getX() {
		calculateCenter();
		return x;
	}

	public int getY() {
		calculateCenter();
		return y;
	}

	public void calculateDimensions() {
		calculateHeight();
		calculateWidth();
	}

	public Point2D getTopVertice() {
		Point2D topPoint = getV(0);

		for (Point2D v : vertices) {
			if (v.getY() < topPoint.getY()) {
				topPoint = v;
			} else if (v.getY() == topPoint.getY() && v.getX() < topPoint.getX()) {
				topPoint = v;
			}
		}

		return topPoint;
	}

	public Point2D getBottomVertice() {
		Point2D bottomPoint = getV(0);

		for (Point2D v : vertices) {
			if (v.getY() > bottomPoint.getY()) {
				bottomPoint = v;
			} else if (v.getY() == bottomPoint.getY() && v.getX() < bottomPoint.getX()) {
				bottomPoint = v;
			}
		}

		return bottomPoint;
	}

	public Point2D getLeftVertice() {
		Point2D leftPoint = getV(0);

		for (Point2D v : vertices) {
			if (v.getX() < leftPoint.getX()) {
				leftPoint = v;
			} else if (v.getX() == leftPoint.getX() && v.getY() < leftPoint.getY()) {
				leftPoint = v;
			}
		}

		return leftPoint;
	}

	public Point2D getRightVertice() {
		Point2D rightPoint = getV(0);

		for (Point2D v : vertices) {
			if (v.getX() > rightPoint.getX()) {
				rightPoint = v;
			} else if (v.getX() == rightPoint.getX() && v.getY() < rightPoint.getY()) {
				rightPoint = v;
			}
		}

		return rightPoint;
	}

	public Point2D getTopLeftPosition() {
		return new Point2D(getLeftVertice().getX(), getTopVertice().getY());
	}

	public Point2D getBottomRightPosition() {
		return new Point2D(getRightVertice().getX(), getBottomVertice().getY());
	}

	public int getVerticeIndex(Point2D vertice) {
		return vertices.indexOf(vertice);
	}

	public Point2D getNextVertice(Point2D vertice) {
		int index = getVerticeIndex(vertice);
		index++;
		if (index >= vertices.size()) {
			index = 0;
		}

		return vertices.get(index);
	}

	public Point2D getPreviousVertice(Point2D vertice) {
		int index = getVerticeIndex(vertice);
		index--;
		if (index < 0) {
			index = vertices.size() - 1;
		}

		return vertices.get(index);
	}

	public void setVertice(int index, Point2D newVertice) {
		vertices.set(index, newVertice);
	}

	public void calculateHeight() {
		int result = 0;
		if (type == ShapeType.CIRCLE) {
			result = (int) getRadius() * 2;
		} else {
			result = (int) Math.round(Math.abs(getBottomVertice().getY() - getTopVertice().getY()));
		}

		height = result;
	}

	public void updateVerticesToHeight(int heightValue) {
		if (height != 0) {
			double heightModifier = (double) heightValue / (double) height;
			if (type == ShapeType.CIRCLE) {
				setRadius(heightValue / 2);
			} else {
				for (Point2D v : vertices) {
					v.setLocation(v.getX(),
							getTopVertice().getY() + Math.abs(v.getY() - getTopVertice().getY()) * heightModifier);
				}
			}
		} else {
			for (Point2D v : vertices) {
				v.setLocation(v.getX(), getTopVertice().getY());
			}
		}

		calculateHeight();
		calculateCenter();
		redraw();
	}

	public void calculateWidth() {
		int result = 0;
		if (type == ShapeType.CIRCLE) {
			result = (int) getRadius() * 2;
		} else {
			result = (int) Math.round(Math.abs(getRightVertice().getX() - getLeftVertice().getX()));
		}

		width = result;
	}

	public void updateVerticesToWidth(int widthValue) {
		if (width != 0) {
			if (type == ShapeType.CIRCLE) {
				setRadius(widthValue);
			} else {
				double widthModifier = (double) widthValue / (double) width;
				for (Point2D v : vertices) {
					v.setLocation(
							getLeftVertice().getX() + Math.abs(v.getX() - getLeftVertice().getX()) * widthModifier,
							v.getY());
				}
			}
		} else {
			for (Point2D v : vertices) {
				v.setLocation(getLeftVertice().getX(), v.getY());
			}
		}

		calculateWidth();
		calculateCenter();
		redraw();
	}

	public void setRadius(int newRadius) {
		if (type == ShapeType.CIRCLE) {
			Point2D center = getV(0);
			updateVertice(getV(1), new Point2D(center.getX() + newRadius, center.getY()));
		} else {

		}
	}

	public Point2D getV(int index) {
		return vertices.get(index);
	}

	public Point2D getCenter() {
		return new Point2D(x, y);
	}

	public double getRadius() {
		double result = 0;

		if (type == ShapeType.CIRCLE) {
			result = Math
					.sqrt(Math.pow(getV(1).getX() - getV(0).getX(), 2) + Math.pow(getV(1).getY() - getV(0).getY(), 2));
		}

		return result;
	}

	public int getWidth() {
		calculateWidth();
		return width;
	}

	public int getHeight() {
		calculateHeight();
		return height;
	}

	public int updateShape(Point2D newPoint) {
		if (type == ShapeType.CIRCLE) {
			vertices.set(1, newPoint);
		} else if (type == ShapeType.SQUARE) {
			Point2D startPoint = getV(0);
			int width = (int) Math.round(newPoint.getX() - startPoint.getX());
			int height = (int) Math.round(newPoint.getY() - startPoint.getY());
			boolean useWidth = Math.abs(newPoint.getX() - startPoint.getX()) > Math
					.abs(newPoint.getY() - startPoint.getY());
			if (useWidth) {
				width = (int) Math.round(newPoint.getX() - startPoint.getX());
				if ((height < 0 && width >= 0) || (height >= 0 && width < 0)) {
					height = -width;
				} else {
					height = width;
				}
			} else {
				height = (int) Math.round(newPoint.getY() - startPoint.getY());
				if ((height < 0 && width >= 0) || (height >= 0 && width < 0)) {
					width = -height;
				} else {
					width = height;
				}
			}

			vertices.set(1, new Point2D(startPoint.getX(), startPoint.getY() + height));
			vertices.set(2, new Point2D(startPoint.getX() + width, startPoint.getY() + height));
			vertices.set(3, new Point2D(startPoint.getX() + width, startPoint.getY()));
		} else if (type == ShapeType.LINE) {
			vertices.set(1, newPoint);
		} else if (type == ShapeType.FREE_LINE) {
			vertices.add(newPoint);
		} else if (type == ShapeType.RECTANGLE || type == ShapeType.IMAGE || type == ShapeType.WORKSPACE) {
			Point2D vert1 = getV(1);
			Point2D vert3 = getV(3);

			vert1.setY(newPoint.getY());
			vert3.setX(newPoint.getX());

			vertices.set(1, vert1);
			vertices.set(2, newPoint);
			vertices.set(3, vert3);
		} else if (type == ShapeType.INCOMPLETE_TRIANGLE) {
			if (vertices.size() == 2) {
				vertices.add(newPoint);
			} else if (vertices.size() == 3) {
				type = ShapeType.TRIANGLE;
			}
		} else if (type == ShapeType.INCOMPLETE) {
			vertices.add(newPoint);
		} else if (type == ShapeType.INCOMPLETE_BEZIER_CURVE) {
			vertices.add(newPoint);
		}

		calculateCenter();
		calculateDimensions();
		redraw();
		return vertices.size();
	}

	public void updateLastVertice(Point2D newPoint) {
		setVertice(vertices.size() - 1, newPoint);

		calculateCenter();
		calculateDimensions();
		redraw();
	}

	public Point2D updateVertice(Point2D vertice, Point2D newLocation) {
		Point2D newVertice = null;
		if (type == ShapeType.FREE_LINE) {
			move(Functions2D.getVector(vertice, newLocation));
			newVertice = newLocation;
		} else if (type == ShapeType.CIRCLE) {
			setVertice(1, newLocation);
			newVertice = getV(1);
		} else if (type == ShapeType.RECTANGLE || type == ShapeType.IMAGE || type == ShapeType.WORKSPACE) {
			Point2D vert1 = getNextVertice(vertice);
			Point2D vert2 = getV(getVerticeIndex(vertice));
			Point2D vert3 = getPreviousVertice(vertice);

			if (vert2.getX() + 1 >= vert1.getX() && vert2.getX() - 1 <= vert1.getX() && vert2.getY() + 1 >= vert3.getY()
					&& vert2.getY() - 1 <= vert3.getY()) {
				vert1.setLocation(newLocation.getX(), vert1.getY());
				vert3.setLocation(vert3.getX(), newLocation.getY());
				vert2.setLocation(newLocation.getX(), newLocation.getY());
			} else if (vert2.getX() + 1 >= vert3.getX() && vert2.getX() - 1 <= vert3.getX()
					&& vert2.getY() + 1 >= vert1.getY() && vert2.getY() - 1 <= vert1.getY()) {
				vert1.setLocation(vert1.getX(), newLocation.getY());
				vert3.setLocation(newLocation.getX(), vert3.getY());
				vert2.setLocation(newLocation.getX(), newLocation.getY());
			} else {
				System.out.println("Oops");
			}

			calculateDimensions();
			newVertice = vert2;
		} else if (type == ShapeType.SQUARE) {
			// Point2D vert1 = getNextVertice(vertice);
			Point2D vert2 = getV(getVerticeIndex(vertice));
			// Point2D vert3 = getPreviousVertice(vertice);

			calculateDimensions();
			newVertice = vert2;
		} else if (type == ShapeType.INCOMPLETE_BEZIER_CURVE) {
			newVertice = newLocation;
		} else {
			setVertice(getVerticeIndex(vertice), newLocation);
			newVertice = getV(getVerticeIndex(newLocation));
		}

		calculateCenter();
		calculateDimensions();
		redraw();
		return newVertice;
	}

	public ShapeType checkShape() {
		if (vertices.size() == 4) {
			Point2D v1 = getV(0);
			Point2D v2 = getV(1);
			Point2D v3 = getV(2);
			Point2D v4 = getV(3);

			if ((v1.getX() == v2.getX() && v2.getY() == v3.getY() && v3.getX() == v4.getX() && v4.getY() == v1.getY())
					|| (v1.getY() == v2.getY() && v2.getX() == v3.getX() && v3.getY() == v4.getY()
							&& v4.getX() == v1.getX())) {
				type = ShapeType.RECTANGLE;
			} else {
				type = ShapeType.POLYGON;
			}
		}
		return type;
	}

	@SuppressWarnings("unchecked")
	public List<Point2D> getVertices() {
		if (rotation == null) {
			return (ArrayList<Point2D>) vertices.clone();
		}

		return getRotatedVertices();
	}

	private List<Point2D> getRotatedVertices() {
		ArrayList<Point2D> rotatedVertices = new ArrayList<Point2D>();

		for (int i = 0; i < vertices.size(); i++) {
			rotatedVertices.add(new Point2D(vertices.get(i)));
		}

		if (rotation != null) {
			for (Point2D v : rotatedVertices) {
				Point2D newVertex = Functions2D.getRotatedPoint2D(v, rotation.pivot, rotation.z);
				v.setLocation(newVertex);
			}
		}

		return rotatedVertices;
	}

	public int getVerticeCount() {
		return vertices.size();
	}

	public boolean isComplete() {
		return type != ShapeType.INCOMPLETE && type != ShapeType.INCOMPLETE_TRIANGLE
				&& type != ShapeType.INCOMPLETE_BEZIER_CURVE;
	}

	public void forceComplete() {
		if (type == ShapeType.INCOMPLETE) {
			if (getVerticeCount() == 2) {
				type = ShapeType.LINE;
			} else if (getVerticeCount() == 3) {
				type = ShapeType.TRIANGLE;
			} else if (getVerticeCount() > 3) {
				type = ShapeType.POLYGON;
			}
		} else if (type == ShapeType.INCOMPLETE_TRIANGLE) {
			if (getVerticeCount() == 2) {
				type = ShapeType.LINE;
			} else if (getVerticeCount() == 3) {
				type = ShapeType.TRIANGLE;
			}
		} else if (type == ShapeType.INCOMPLETE_BEZIER_CURVE) {
			type = ShapeType.BEZIER_CURVE;
		}

		calculateCenter();
		calculateDimensions();
		redraw();
	}

	public Point2D getVertice(Point2D location) {
		if (type == ShapeType.FREE_LINE) {
			if (Functions2D.distance(getV(0), location) <= TOUCHING_DISTANCE) {
				return getV(0);
			}
			if (Functions2D.distance(getV(vertices.size() - 1), location) <= TOUCHING_DISTANCE) {
				return getV(vertices.size() - 1);
			}
		} else if (type == ShapeType.CIRCLE) {
			Point2D center = getV(0);
			Point2D v1 = new Point2D(center.getX() + (int) getRadius(), center.getY());
			Point2D v2 = new Point2D(center.getX(), center.getY() + (int) getRadius());
			Point2D v3 = new Point2D(center.getX(), center.getY() - (int) getRadius());
			Point2D v4 = new Point2D(center.getX() - (int) getRadius(), center.getY());

			if (Functions2D.distance(getV(1), location) <= TOUCHING_DISTANCE) {
				return getV(1);
			} else if (Functions2D.distance(v1, location) <= TOUCHING_DISTANCE) {
				vertices.set(1, v1);
				return getV(1);
			} else if (Functions2D.distance(v2, location) <= TOUCHING_DISTANCE) {
				vertices.set(1, v2);
				return getV(1);
			} else if (Functions2D.distance(v3, location) <= TOUCHING_DISTANCE) {
				vertices.set(1, v3);
				return getV(1);
			} else if (Functions2D.distance(v4, location) <= TOUCHING_DISTANCE) {
				vertices.set(1, v4);
				return getV(1);
			}
		} else {
			for (Point2D v : vertices) {
				if (Functions2D.distance(v, location) <= TOUCHING_DISTANCE) {
					return v;
				}
			}
		}
		return null;
	}

	public boolean arePointsTouching(Point2D point1, Point2D point2) {
		return Functions2D.distance(point1, point2) <= TOUCHING_DISTANCE;
	}

	public boolean isTouching(Point2D location) {
		if (fill && type != ShapeType.WORKSPACE) {
			if (type == ShapeType.FREE_LINE) {
				for (Point2D v : vertices) {
					if (arePointsTouching(v, location)) {
						return true;
					}
				}
			} else if (type == ShapeType.CIRCLE) {
				if (Functions2D.distance(getV(0), location) <= getRadius() + TOUCHING_DISTANCE) {
					return true;
				}
			} else if (type == ShapeType.RECTANGLE || type == ShapeType.IMAGE || type == ShapeType.SQUARE) {
				if ((((location.getX() >= getV(0).getX() && location.getX() <= getV(2).getX())
						|| (location.getX() >= getV(2).getX() && location.getX() <= getV(0).getX()))
						&& ((location.getY() >= getV(3).getY() && location.getY() <= getV(1).getY())
								|| (location.getY() >= getV(1).getY() && location.getY() <= getV(3).getY())))
						|| (arePointsTouching(getV(0), location) || arePointsTouching(getV(1), location)
								|| arePointsTouching(getV(2), location) || arePointsTouching(getV(3), location))) {
					return true;
				}
			} else {
				int i;
				for (i = 0; i < vertices.size() - 1; i++) {
					if (Functions2D.isPointTouchingSegment(location, getV(i), getV(i + 1), STRICT_TOUCHING_DISTANCE)) {
						return true;
					}
					if (arePointsTouching(getV(i), location)) {
						return true;
					}
				}
				if (Functions2D.isPointTouchingSegment(location, getV(i), getV(0), STRICT_TOUCHING_DISTANCE)) {
					return true;
				}
				if (arePointsTouching(getV(i), location)) {
					return true;
				}
			}
			return false;
		} else {
			if (type == ShapeType.FREE_LINE) {
				for (Point2D v : vertices) {
					if (Functions2D.distance(v, location) <= TOUCHING_DISTANCE) {
						return true;
					}
				}
			} else if (type == ShapeType.CIRCLE) {
				System.out.println(location);
				if (arePointsTouching(getV(0), location)
						|| (Functions2D.distance(getV(0), location) <= getRadius() + TOUCHING_DISTANCE
								&& Functions2D.distance(getV(0), location) >= getRadius() - TOUCHING_DISTANCE)) {
					return true;
				}
			} else if (type == ShapeType.WORKSPACE) {
				for (int i = 0; i < vertices.size(); i++) {
					if (arePointsTouching(getV(i), location)) {
						return true;
					}
				}
			} else {
				int i;
				for (i = 0; i < vertices.size() - 1; i++) {
					if (Functions2D.isPointTouchingSegment(location, getV(i), getV(i + 1), STRICT_TOUCHING_DISTANCE)) {
						return true;
					}
					if (arePointsTouching(getV(i), location)) {
						return true;
					}
				}
				if (Functions2D.isPointTouchingSegment(location, getV(i), getV(0), STRICT_TOUCHING_DISTANCE)) {
					return true;
				}
				if (arePointsTouching(getV(i), location)) {
					return true;
				}
			}
			return false;
		}
	}

	@Override
	public ShapeType getType() {
		return type;
	}

	@Override
	public void rotate(int xRotation, int yRotation, int zRotation) {
		rotate(xRotation, yRotation);
	}

	@Override
	public int getZ() {
		return 0;
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

	public void setType(ShapeType type) {
		this.type = type;
	}

	@Override
	public void setColor(Color color) {
		this.color = color;

	}

	@Override
	public void setFill(boolean fill) {
		this.fill = fill;

	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public boolean getFill() {
		return fill;
	}

}
