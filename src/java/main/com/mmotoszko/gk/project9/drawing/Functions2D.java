/*******************************************************************************
 * Micha³ Motoszko
 * 2018-11-27
 * Grafika komputerowa PS 9
 * Projekt 9
 ******************************************************************************/
package com.mmotoszko.gk.project9.drawing;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.mmotoszko.gk.project9.drawing.customshape.CustomImage;
import com.mmotoszko.gk.project9.drawing.customshape.CustomShape;
import com.mmotoszko.gk.project9.drawing.customshape.CustomShape3D;

public class Functions2D {
	public static boolean drawCubeFaces = false;
	private static long[][] binomialCoefficient = new long[100][100];
	private static int UNDEFINED = (int) System.nanoTime();

	public static void drawPoint(Graphics2D g2d, Point2D Point2D, Point2D lookOffset) {
		g2d.drawLine(Point2D.getRoundX() + lookOffset.getRoundX(), Point2D.getRoundY() + lookOffset.getRoundY(), Point2D.getRoundX() + lookOffset.getRoundX(),
				Point2D.getRoundY() + lookOffset.getRoundY());
	}

	public static void drawPoint(Graphics2D g2d, Point2D Point2D, Point2D lookOffset, int radius) {
		g2d.drawOval(Point2D.getRoundX() + lookOffset.getRoundX() - (int) ((float) radius / 2),
				Point2D.getRoundY() + lookOffset.getRoundY() - (int) ((float) radius / 2), radius, radius);
	}

	public static Point2D getVector(Point2D from, Point2D to) {
		return new Point2D(to.getRoundX() - from.getRoundX(), to.getRoundY() - from.getRoundY());
	}

	public static double distance(Point2D p1, Point2D p2) {
		return Math.hypot(p1.getX() - p2.getX(), p1.getY() - p2.getY());
		// return Math.sqrt(Math.pow(p2.getX() - p1.getX(), 2) +
		// Math.pow(p2.getY() - p1.getY(),
		// 2));
	}

	public static Point2D getPointProjectedOntoSegment(Point2D A, Point2D B, Point2D C) {
		int x1 = A.getRoundX(), y1 = A.getRoundY(), x2 = B.getRoundX(), y2 = B.getRoundY(), x3 = C.getRoundX(), y3 = C.getRoundY();
		int px = x2 - x1, py = y2 - y1, dAB = px * px + py * py;
		int u = ((x3 - x1) * px + (y3 - y1) * py) / dAB;
		int x = x1 + u * px, y = y1 + u * py;
		return new Point2D(x, y);
	}

	public static double getPointDistanceFromLine(Point2D Point2D, Point2D l1, Point2D l2) {
		return Math
				.abs((l2.getY() - l1.getY()) * Point2D.getX() - (l2.getX() - l1.getX()) * Point2D.getY()
						+ l2.getX() * l1.getY() - l2.getY() * l1.getX())
				/ Math.sqrt(Math.pow((l2.getY() - l1.getY()), 2) + Math.pow((l2.getX() - l1.getX()), 2));
	}

	public static boolean isPointTouchingSegment(Point2D Point2D, Point2D s1, Point2D s2, double touchingDistance) {
		return distance(Point2D, s1) + distance(Point2D, s2) >= distance(s1, s2) - touchingDistance
				&& distance(Point2D, s1) + distance(Point2D, s2) <= distance(s1, s2) + touchingDistance;
	}

	public static void drawShape(Graphics2D g2d, CustomShape shape, Point2D lookOffset, int verticeRadiusInt,
			int canvasWidth, int canvasHeight) {
		g2d.setColor(shape.getColor());

		if (shape.getType() == ShapeType.CUBE) {
			drawShape3D(g2d, shape, lookOffset);
		} else if (shape.getType() == ShapeType.CIRCLE) {
			drawCircle(g2d, shape, lookOffset);
		} else if (shape.getType() == ShapeType.IMAGE) {
			drawImage(g2d, (CustomImage) shape, lookOffset, canvasWidth, canvasHeight);
		} else if (shape.getType() == ShapeType.BEZIER_CURVE || shape.getType() == ShapeType.INCOMPLETE_BEZIER_CURVE) {
			drawBezierCurve(g2d, shape, lookOffset);
		} else {
			drawPolygon(g2d, shape, lookOffset);
		}
		shape.completedRedrawing();
	}

	public static void drawCircle(Graphics2D g2d, CustomShape shape, Point2D lookOffset) {
		Point2D center = shape.getVertices().get(0);

		if (shape.getFill()) {
			g2d.fillOval(center.getRoundX() - (int) shape.getRadius() + lookOffset.getRoundX(),
					center.getRoundY() - (int) shape.getRadius() + lookOffset.getRoundY(), (int) shape.getWidth(), (int) shape.getHeight());
		} else {
			g2d.drawOval(center.getRoundX() - (int) shape.getRadius() + lookOffset.getRoundX(),
					center.getRoundY() - (int) shape.getRadius() + lookOffset.getRoundY(), (int) shape.getWidth(), (int) shape.getHeight());
		}
	}

	public static void drawBezierCurve(Graphics2D g2d, CustomShape shape, Point2D lookOffset) {
		ArrayList<Point2D> vertices = (ArrayList<Point2D>) shape.getVertices();
		Point2D[] verticesArray = new Point2D[vertices.size()];
		verticesArray = vertices.toArray(verticesArray);
		double distanceSum = 0;

		int i;
		for (i = 0; i < vertices.size() - 1; i++) {
			distanceSum += distance(vertices.get(i), vertices.get(i + 1));
		}

		Point2D previous = new Point2D(UNDEFINED, UNDEFINED);
		Point2D temp = new Point2D(UNDEFINED, UNDEFINED);

		for (int j = 0; j <= distanceSum; j++) {
			previous = temp;
			temp = getBezierValue(j / distanceSum, verticesArray);
			if (previous.getRoundX() != UNDEFINED) {
				g2d.drawLine(previous.getRoundX() + lookOffset.getRoundX(), previous.getRoundY() + lookOffset.getRoundY(), temp.getRoundX() + lookOffset.getRoundX(),
						temp.getRoundY() + lookOffset.getRoundY());
			}
		}
	}

	public static Point2D getBezierValue(double t, Point2D[] p) {
		t = Math.max(Math.min(t, 1), 0);
		double x = 0;
		double y = 0;

		for (int i = 0; i < p.length; i++) {
			double sumModifier = binomial(p.length - 1, i) * power((1 - t), p.length - i - 1) * power(t, i);
			x += p[i].getRoundX() * sumModifier;
			y += p[i].getRoundY() * sumModifier;
		}

		return new Point2D((int) Math.round(x), (int) Math.round(y));
	}

	private static void drawBezierCurveSketch(Graphics2D g2d, CustomShape shape, Point2D lookOffset) {
		ArrayList<Point2D> vertices = (ArrayList<Point2D>) shape.getVertices();

		g2d.setColor(Color.lightGray);
		for (int i = 0; i < vertices.size() - 1; i++) {
			g2d.drawLine(vertices.get(i).getRoundX() + lookOffset.getRoundX(), vertices.get(i).getRoundY() + lookOffset.getRoundY(),
					vertices.get(i + 1).getRoundX() + lookOffset.getRoundX(), vertices.get(i + 1).getRoundY() + lookOffset.getRoundY());
		}
	}

	public static double power(double value, int exponent) {
		if (exponent == 0) {
			return 1;
		}

		double newValue = value;

		for (int i = 1; i < exponent; i++) {
			newValue *= value;
		}

		return newValue;
	}

	public static long binomial(int n, int k) {
		if (k > n - k) {
			k = n - k;
		}

		if (binomialCoefficient[n][k] == 0) {
			long b = 1;
			for (int i = 1, m = n; i <= k; i++, m--) {
				b = b * m / i;
			}
			binomialCoefficient[n][k] = b;
		}

		return binomialCoefficient[n][k];
	}

	public static void drawFocusedShape(Graphics2D g2d, CustomShape shape, Point2D lookOffset, int verticeRadius,
			int canvasWidth, int canvasHeight) {

		if (shape.getType() == ShapeType.CUBE) {
			drawFocusedShape3D(g2d, shape, lookOffset);
			return;
		}

		int verticeOffset = verticeRadius;
		int verticeDiameter = verticeRadius * 2;
		if (shape.isType(ShapeType.BEZIER_CURVE) || shape.isType(ShapeType.INCOMPLETE_BEZIER_CURVE)) {
			drawBezierCurveSketch(g2d, shape, lookOffset);
		}
		drawShape(g2d, shape, lookOffset, verticeRadius, canvasWidth, canvasHeight);
		g2d.setColor(Color.BLACK);

		if (shape.getType() == ShapeType.CIRCLE) {
			Point2D center = shape.getVertices().get(0);
			g2d.drawOval(center.getRoundX() - verticeOffset + (int) shape.getRadius() + lookOffset.getRoundX(),
					center.getRoundY() - verticeOffset + lookOffset.getRoundY(), verticeDiameter, verticeDiameter);
			g2d.drawOval(center.getRoundX() - verticeOffset - (int) shape.getRadius() + lookOffset.getRoundX(),
					center.getRoundY() - verticeOffset + lookOffset.getRoundY(), verticeDiameter, verticeDiameter);
			g2d.drawOval(center.getRoundX() - verticeOffset + lookOffset.getRoundX(),
					center.getRoundY() - verticeOffset + (int) shape.getRadius() + lookOffset.getRoundY(), verticeDiameter,
					verticeDiameter);
			g2d.drawOval(center.getRoundX() - verticeOffset + lookOffset.getRoundX(),
					center.getRoundY() - verticeOffset - (int) shape.getRadius() + lookOffset.getRoundY(), verticeDiameter,
					verticeDiameter);
			g2d.drawLine(center.getRoundX() - verticeOffset + lookOffset.getRoundX(), center.getRoundY() + lookOffset.getRoundY(),
					center.getRoundX() + verticeOffset + lookOffset.getRoundX(), center.getRoundY() + lookOffset.getRoundY());
			g2d.drawLine(center.getRoundX() + lookOffset.getRoundX(), center.getRoundY() - verticeOffset + lookOffset.getRoundY(), center.getRoundX() + lookOffset.getRoundX(),
					center.getRoundY() + verticeOffset + lookOffset.getRoundY());
		} else {
			drawPolygonVertices(g2d, shape, lookOffset, verticeRadius);
		}
	}

	public static void drawWorkspaceBase(Graphics2D g2d, CustomShape workspace, Point2D lookOffset) {
		g2d.setColor(workspace.getColor());
		g2d.fillRect(workspace.getX() + lookOffset.getRoundX(), workspace.getY() + lookOffset.getRoundY(), workspace.getWidth(),
				workspace.getHeight());
	}

	public static void drawWorkspaceBoundries(Graphics2D g2d, CustomShape workspace, Point2D lookOffset) {
		g2d.setColor(Color.GRAY);
		int cornerRadius = 3;
		int cornerSize = 6;
		g2d.fillRect(workspace.getX() + lookOffset.getRoundX() - cornerRadius, workspace.getY() + lookOffset.getRoundY() - cornerRadius,
				cornerSize, cornerSize);
		g2d.fillRect(workspace.getX() + lookOffset.getRoundX() + workspace.getWidth() - cornerRadius,
				workspace.getY() + lookOffset.getRoundY() - cornerRadius, cornerSize, cornerSize);
		g2d.fillRect(workspace.getX() + lookOffset.getRoundX() - cornerRadius,
				workspace.getY() + lookOffset.getRoundY() + workspace.getHeight() - cornerRadius, cornerSize, cornerSize);
		g2d.fillRect(workspace.getX() + lookOffset.getRoundX() + workspace.getWidth() - cornerRadius,
				workspace.getY() + lookOffset.getRoundY() + workspace.getHeight() - cornerRadius, cornerSize, cornerSize);
	}

	public static void drawWorkspaceRest(Graphics2D g2d, CustomShape workspace, Point2D lookOffset, int canvasWidth,
			int canvasHeight) {
		g2d.setColor(Color.LIGHT_GRAY);
		g2d.drawRect(workspace.getX() - 1 + lookOffset.getRoundX(), workspace.getY() - 1 + lookOffset.getRoundY(), workspace.getWidth() + 1,
				workspace.getHeight() + 1);
	}

	public static void drawPolygon(Graphics2D g2d, CustomShape shape, Point2D lookOffset) {
		
			ArrayList<Point2D> vertices = (ArrayList<Point2D>) shape.getVertices();
			int i;
			for (i = 0; i < vertices.size() - 1; i++) {
				g2d.drawLine(vertices.get(i).getRoundX() + lookOffset.getRoundX(), vertices.get(i).getRoundY() + lookOffset.getRoundY(),
						vertices.get(i + 1).getRoundX() + lookOffset.getRoundX(), vertices.get(i + 1).getRoundY() + lookOffset.getRoundY());
			}
			if (shape.getType() != ShapeType.FREE_LINE) {
				g2d.drawLine(vertices.get(i).getRoundX() + lookOffset.getRoundX(), vertices.get(i).getRoundY() + lookOffset.getRoundY(),
						vertices.get(0).getRoundX() + lookOffset.getRoundX(), vertices.get(0).getRoundY() + lookOffset.getRoundY());
			}
	}

	public static void drawPolygonVertices(Graphics2D g2d, CustomShape shape, Point2D lookOffset, int verticeRadius) {
		ArrayList<Point2D> vertices = (ArrayList<Point2D>) shape.getVertices();
		int verticeOffset = verticeRadius;
		int verticeDiameter = verticeRadius * 2;
		int i;

		if (shape.getType() == ShapeType.FREE_LINE) {
			g2d.drawOval(vertices.get(0).getRoundX() - verticeOffset + lookOffset.getRoundX(),
					vertices.get(0).getRoundY() - verticeOffset + lookOffset.getRoundY(), verticeDiameter, verticeDiameter);
			g2d.drawOval(vertices.get(vertices.size() - 1).getRoundX() - verticeOffset + lookOffset.getRoundX(),
					vertices.get(vertices.size() - 1).getRoundY() - verticeOffset + lookOffset.getRoundY(), verticeDiameter,
					verticeDiameter);
		} else {
			for (i = 0; i < vertices.size(); i++) {
				g2d.drawOval(vertices.get(i).getRoundX() - verticeOffset + lookOffset.getRoundX(),
						vertices.get(i).getRoundY() - verticeOffset + lookOffset.getRoundY(), verticeDiameter, verticeDiameter);
			}
		}
	}

	public static void drawFocusedVertice(Graphics2D g2d, Point2D focusedVertice, Point2D lookOffset,
			int verticeRadius) {
		int verticeOffset = verticeRadius;
		int verticeDiameter = verticeRadius * 2;

		g2d.setColor(Color.black);
		g2d.fillOval(focusedVertice.getRoundX() - verticeOffset + lookOffset.getRoundX(), focusedVertice.getRoundY() - verticeOffset + lookOffset.getRoundY(),
				verticeDiameter, verticeDiameter);
		g2d.setColor(Color.white);
		g2d.drawLine(focusedVertice.getRoundX() - verticeOffset + lookOffset.getRoundX() + 2, focusedVertice.getRoundY() + lookOffset.getRoundY(),
				focusedVertice.getRoundX() + verticeOffset + lookOffset.getRoundX() - 2, focusedVertice.getRoundY() + lookOffset.getRoundY());
		g2d.drawLine(focusedVertice.getRoundX() + lookOffset.getRoundX(), focusedVertice.getRoundY() - verticeOffset + lookOffset.getRoundY() + 2,
				focusedVertice.getRoundX() + lookOffset.getRoundX(), focusedVertice.getRoundY() + verticeOffset + lookOffset.getRoundY() - 2);
	}

	public static void drawShape3D(Graphics2D g2d, CustomShape shape, Point2D lookOffset) {
		g2d.setColor(shape.getColor());
		CustomShape3D shape3D = (CustomShape3D) shape;

		if (shape.getType() == ShapeType.CUBE) {
			drawCube(g2d, shape3D, lookOffset);
		}
	}

	private static void drawFocusedShape3D(Graphics2D g2d, CustomShape shape, Point2D lookOffset) {
		g2d.setColor(shape.getColor());

		drawShape3D(g2d, shape, lookOffset);
		drawFocusedVertice(g2d, shape.getCenter(), lookOffset, 6);
	}

	public static BufferedImage getScaledImage(int[][] colors, int width, int height) {
		if (width == 0 || height == 0) {
			return null;
		}

		BufferedImage image = new BufferedImage(colors[0].length, colors.length, BufferedImage.TYPE_INT_RGB);

		for (int row = 0; row < colors.length; row++) {
			for (int col = 0; col < colors[0].length; col++) {
				image.setRGB(col, row, colors[row][col]);
			}
		}

		BufferedImage after = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		AffineTransform at = new AffineTransform();
		at.scale((double) width / colors[0].length, (double) height / colors.length);
		AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BICUBIC);
		after = scaleOp.filter(image, after);
		return after;
	}

	public static Image getImage(Color color, int width, int height) {
		return Functions2D.getScaledImage(new int[][] { new int[] { Functions2D.getColor(color) } }, width, height);
	}

	public static int getColor(Color color) {
		return getIntRGBPixel((byte) color.getRed(), (byte) color.getGreen(), (byte) color.getBlue());
	}

	public static int[][][] makeCubeFaces() {
		int[][][] faces = new int[6][][];
		int[][] faceFront = new int[2][];
		faceFront[0] = new int[] { getColor(Color.red), getColor(Color.green) };
		faceFront[1] = new int[] { getColor(Color.blue), getColor(Color.black) };

		int[][] faceTop = new int[2][];
		faceTop[0] = new int[] { getColor(Color.cyan), getColor(Color.yellow) };
		faceTop[1] = new int[] { getColor(Color.red), getColor(Color.green) };

		int[][] faceRight = new int[2][];
		faceRight[0] = new int[] { getColor(Color.orange), getColor(Color.red) };
		faceRight[1] = new int[] { getColor(Color.lightGray), getColor(Color.blue) };

		int[][] faceBottom = new int[2][];
		faceBottom[0] = new int[] { getColor(Color.blue), getColor(Color.black) };
		faceBottom[1] = new int[] { getColor(Color.magenta), getColor(Color.white) };

		int[][] faceLeft = new int[2][];
		faceLeft[0] = new int[] { getColor(Color.green), getColor(Color.orange) };
		faceLeft[1] = new int[] { getColor(Color.black), getColor(Color.lightGray) };

		int[][] faceBack = new int[2][];
		faceBack[0] = new int[] { getColor(Color.magenta), getColor(Color.white) };
		faceBack[1] = new int[] { getColor(Color.cyan), getColor(Color.yellow) };

		faces[0] = faceFront;
		faces[1] = faceTop;
		faces[2] = faceRight;
		faces[3] = faceBottom;
		faces[4] = faceLeft;
		faces[5] = faceBack;
		return faces;
	}

	public static boolean isCubeFaceVisible(CustomShape3D cube, Point3D v1, Point3D v2, Point3D v3, Point3D v4) {
		ArrayList<Point3D> vertices = getRotatedVertices3D(cube);
		Point3D mostBackPoint = vertices.get(0);
		for (int i = 0; i < vertices.size(); i++) {
			if (vertices.get(i).z < mostBackPoint.z) {
				mostBackPoint = vertices.get(i);
			}
		}

		return !(mostBackPoint.z == v1.z || mostBackPoint.z == v2.z || mostBackPoint.z == v3.z
				|| mostBackPoint.z == v4.z);
	}

	private static Point3D getTopLeftPoint(Point3D[] points) {
		if (points.length == 0) {
			return null;
		}

		// Point3D mostLeft = points[0];
		// Point3D mostRight = points[1];
		Point3D topLeft = points[2];
		// for (int i = 0; i < points.length; i++) {
		// if (mostLeft.getRoundX() > points[i].getRoundX()) {
		// mostLeft = points[i];
		// }
		// if (mostRight.getRoundX() < points[i].getRoundX()) {
		// mostRight = points[i];
		// }
		// }

		for (int i = 0; i < points.length; i++) {
			if (topLeft.getRoundY() > points[i].getRoundY()) {
				topLeft = points[i];
			} else if (topLeft.getRoundY() == points[i].getRoundY()) {
				if (topLeft.getRoundX() > points[i].getRoundX()) {
					topLeft = points[i];
				}
			}
		}
		return topLeft;
	}

	private static Point3D getTopRightPoint(Point3D[] points) {
		if (points.length == 0) {
			return null;
		}

		// Point3D mostLeft = points[0];
		// Point3D mostRight = points[1];
		Point3D topRight = points[2];
		// for (int i = 0; i < points.length; i++) {
		// if (mostLeft.getRoundX() > points[i].getRoundX()) {
		// mostLeft = points[i];
		// }
		// if (mostRight.getRoundX() < points[i].getRoundX()) {
		// mostRight = points[i];
		// }
		// }

		for (int i = 0; i < points.length; i++) {
			if (topRight.getRoundY() > points[i].getRoundY()) {
				topRight = points[i];
			} else if (topRight.getRoundY() == points[i].getRoundY()) {
				if (topRight.getRoundX() < points[i].getRoundX()) {
					topRight = points[i];
				}
			}
		}
		return topRight;
	}

	private static Point3D getBottomLeftPoint(Point3D[] points) {
		if (points.length == 0) {
			return null;
		}

		Point3D bottomLeft = points[0];
		for (int i = 0; i < points.length; i++) {
			if (bottomLeft.getRoundY() < points[i].getRoundY()) {
				bottomLeft = points[i];
			} else if (bottomLeft.getRoundY() == points[i].getRoundY()) {
				if (bottomLeft.getRoundX() > points[i].getRoundX()) {
					bottomLeft = points[i];
				}
			}
		}
		return bottomLeft;
	}

	private static Point3D getBottomRightPoint(Point3D[] points) {
		if (points.length == 0) {
			return null;
		}

		Point3D bottomLeft = points[0];
		for (int i = 0; i < points.length; i++) {
			if (bottomLeft.getRoundY() < points[i].getRoundY()) {
				bottomLeft = points[i];
			} else if (bottomLeft.getRoundY() == points[i].getRoundY()) {
				if (bottomLeft.getRoundX() < points[i].getRoundX()) {
					bottomLeft = points[i];
				}
			}
		}
		return bottomLeft;
	}

	private static void drawCubeFace(Graphics2D g2d, BufferedImage faceImage, Point2D lookOffset, CustomShape3D cube,
			Point3D v1, Point3D v2, Point3D v3, Point3D v4) {
		if (drawCubeFaces) {

			Point3D[] vertices = new Point3D[] { v1, v2, v3, v4 };
			Point3D topLeft = getTopLeftPoint(vertices);
			Point3D topRight = getTopRightPoint(vertices);
			Point3D bottomLeft = getBottomLeftPoint(vertices);
			Point3D bottomRight = getBottomRightPoint(vertices);

			int skewedWidth = Math.max(topRight.getRoundX() - bottomLeft.getRoundX(), bottomRight.getRoundX() - topLeft.getRoundX());
			int skewedHeight = bottomLeft.getRoundY() - topLeft.getRoundY();

			if (skewedWidth > 0 && skewedHeight > 0) {
				BufferedImage skewedFace = new BufferedImage(skewedWidth, skewedHeight, BufferedImage.TYPE_INT_ARGB);
				System.out.println(topRight.getRoundX() - topLeft.getRoundX());

				if (topRight.getRoundX() - topLeft.getRoundX() > 0) {
					Image resizedFaceImage = faceImage.getScaledInstance(topRight.getRoundX() - topLeft.getRoundX(), skewedHeight,
							BufferedImage.TYPE_INT_ARGB);
					BufferedImage resizedFaceBufferedImage = new BufferedImage(topRight.getRoundX() - topLeft.getRoundX(), skewedHeight,
							BufferedImage.TYPE_INT_ARGB);
					resizedFaceBufferedImage.createGraphics().drawImage(resizedFaceImage, 0, 0, null);

					double a = Math.pow(topLeft.getRoundX() - bottomLeft.getRoundX(), 2) + Math.pow(topLeft.getRoundY() - bottomLeft.getRoundY(), 2);
					double b = Math.pow(topLeft.getRoundX() - topRight.getRoundX(), 2) + Math.pow(topLeft.getRoundY() - topRight.getRoundY(), 2);
					double c = Math.pow(topRight.getRoundX() - bottomLeft.getRoundX(), 2) + Math.pow(topRight.getRoundY() - bottomLeft.getRoundY(), 2);
					double angle = Math.acos((a + b - c) / Math.sqrt(4 * a * b));

					AffineTransform at = AffineTransform.getTranslateInstance(Math.max(0, topLeft.getRoundX() - bottomLeft.getRoundX()), 0);
					at.shear(1 / Math.tan(angle), 0);
					RenderingHints hints = new RenderingHints(RenderingHints.KEY_INTERPOLATION,
							RenderingHints.VALUE_INTERPOLATION_BICUBIC);
					AffineTransformOp op = new AffineTransformOp(at, hints);

					op.filter(resizedFaceBufferedImage, skewedFace);

					System.out.println("skew: " + 1 / Math.tan(angle));
					g2d.drawImage(skewedFace, Math.min(topLeft.getRoundX(), bottomLeft.getRoundX()) + cube.getX() + lookOffset.getRoundX(),
							topLeft.getRoundY() + cube.getY() + lookOffset.getRoundY(), Color.getColor("", Color.TRANSLUCENT), null);
				}
			}
		}
	}

	private static void drawCube(Graphics2D g2d, CustomShape3D shape3D, Point2D lookOffset) {
		ArrayList<Point3D> vertices = getRotatedVertices3D(shape3D);

		int[][][] faces = makeCubeFaces();

		BufferedImage faceFront = getScaledImage(faces[0], shape3D.getWidth(), shape3D.getHeight());
		BufferedImage faceTop = getScaledImage(faces[1], shape3D.getWidth(), shape3D.getHeight());
		BufferedImage faceRight = getScaledImage(faces[2], shape3D.getWidth(), shape3D.getHeight());
		BufferedImage faceBottom = getScaledImage(faces[3], shape3D.getWidth(), shape3D.getHeight());
		BufferedImage faceLeft = getScaledImage(faces[4], shape3D.getWidth(), shape3D.getHeight());
		BufferedImage faceBack = getScaledImage(faces[5], shape3D.getWidth(), shape3D.getHeight());
		if (drawCubeFaces) {
			g2d.drawImage(faceFront, shape3D.getWidth() + shape3D.getCenter().getRoundX() + shape3D.getWidth() + lookOffset.getRoundX(),
					shape3D.getWidth() + shape3D.getCenter().getRoundY() + shape3D.getWidth() + lookOffset.getRoundY(), null);
			g2d.drawImage(faceTop, shape3D.getWidth() + shape3D.getCenter().getRoundX() + shape3D.getWidth() + lookOffset.getRoundX(),
					shape3D.getCenter().getRoundY() + shape3D.getWidth() + lookOffset.getRoundY(), null);
			g2d.drawImage(faceRight, shape3D.getCenter().getRoundX() + shape3D.getWidth() + lookOffset.getRoundX(),
					shape3D.getWidth() + shape3D.getCenter().getRoundY() + shape3D.getWidth() + lookOffset.getRoundY(), null);
			g2d.drawImage(faceBottom, shape3D.getWidth() + shape3D.getCenter().getRoundX() + shape3D.getWidth() + lookOffset.getRoundX(),
					shape3D.getWidth() * 2 + shape3D.getCenter().getRoundY() + shape3D.getWidth() + lookOffset.getRoundY(), null);
			g2d.drawImage(faceLeft, shape3D.getWidth() * 2 + shape3D.getCenter().getRoundX() + shape3D.getWidth() + lookOffset.getRoundX(),
					shape3D.getWidth() + shape3D.getCenter().getRoundY() + shape3D.getWidth() + lookOffset.getRoundY(), null);
			g2d.drawImage(faceBack, shape3D.getWidth() + shape3D.getCenter().getRoundX() + shape3D.getWidth() + lookOffset.getRoundX(),
					shape3D.getWidth() * 3 + shape3D.getCenter().getRoundY() + shape3D.getWidth() + lookOffset.getRoundY(), null);
		}

		// Front face 0 1 2 3
		if (isCubeFaceVisible(shape3D, vertices.get(0), vertices.get(1), vertices.get(2), vertices.get(3))) {
			drawCubeFace(g2d, faceFront, lookOffset, shape3D, vertices.get(0), vertices.get(1), vertices.get(2),
					vertices.get(3));
			g2d.drawLine(vertices.get(0).getRoundX() + shape3D.getX() + lookOffset.getRoundX(),
					vertices.get(0).getRoundY() + shape3D.getY() + lookOffset.getRoundY(),
					vertices.get(1).getRoundX() + shape3D.getX() + lookOffset.getRoundX(),
					vertices.get(1).getRoundY() + shape3D.getY() + lookOffset.getRoundY());
			g2d.drawLine(vertices.get(1).getRoundX() + shape3D.getX() + lookOffset.getRoundX(),
					vertices.get(1).getRoundY() + shape3D.getY() + lookOffset.getRoundY(),
					vertices.get(2).getRoundX() + shape3D.getX() + lookOffset.getRoundX(),
					vertices.get(2).getRoundY() + shape3D.getY() + lookOffset.getRoundY());
			g2d.drawLine(vertices.get(2).getRoundX() + shape3D.getX() + lookOffset.getRoundX(),
					vertices.get(2).getRoundY() + shape3D.getY() + lookOffset.getRoundY(),
					vertices.get(3).getRoundX() + shape3D.getX() + lookOffset.getRoundX(),
					vertices.get(3).getRoundY() + shape3D.getY() + lookOffset.getRoundY());
			g2d.drawLine(vertices.get(3).getRoundX() + shape3D.getX() + lookOffset.getRoundX(),
					vertices.get(3).getRoundY() + shape3D.getY() + lookOffset.getRoundY(),
					vertices.get(0).getRoundX() + shape3D.getX() + lookOffset.getRoundX(),
					vertices.get(0).getRoundY() + shape3D.getY() + lookOffset.getRoundY());
		}

		// Top face - 0 1 5 4
		if (isCubeFaceVisible(shape3D, vertices.get(0), vertices.get(1), vertices.get(5), vertices.get(4))) {
			drawCubeFace(g2d, faceTop, lookOffset, shape3D, vertices.get(0), vertices.get(1), vertices.get(5),
					vertices.get(4));
			g2d.drawLine(vertices.get(0).getRoundX() + shape3D.getX() + lookOffset.getRoundX(),
					vertices.get(0).getRoundY() + shape3D.getY() + lookOffset.getRoundY(),
					vertices.get(1).getRoundX() + shape3D.getX() + lookOffset.getRoundX(),
					vertices.get(1).getRoundY() + shape3D.getY() + lookOffset.getRoundY());
			g2d.drawLine(vertices.get(1).getRoundX() + shape3D.getX() + lookOffset.getRoundX(),
					vertices.get(1).getRoundY() + shape3D.getY() + lookOffset.getRoundY(),
					vertices.get(5).getRoundX() + shape3D.getX() + lookOffset.getRoundX(),
					vertices.get(5).getRoundY() + shape3D.getY() + lookOffset.getRoundY());
			g2d.drawLine(vertices.get(5).getRoundX() + shape3D.getX() + lookOffset.getRoundX(),
					vertices.get(5).getRoundY() + shape3D.getY() + lookOffset.getRoundY(),
					vertices.get(4).getRoundX() + shape3D.getX() + lookOffset.getRoundX(),
					vertices.get(4).getRoundY() + shape3D.getY() + lookOffset.getRoundY());
			g2d.drawLine(vertices.get(4).getRoundX() + shape3D.getX() + lookOffset.getRoundX(),
					vertices.get(4).getRoundY() + shape3D.getY() + lookOffset.getRoundY(),
					vertices.get(0).getRoundX() + shape3D.getX() + lookOffset.getRoundX(),
					vertices.get(0).getRoundY() + shape3D.getY() + lookOffset.getRoundY());
		}

		// Right face 0 3 7 4
		if (isCubeFaceVisible(shape3D, vertices.get(0), vertices.get(3), vertices.get(7), vertices.get(4))) {
			drawCubeFace(g2d, faceRight, lookOffset, shape3D, vertices.get(0), vertices.get(3), vertices.get(7),
					vertices.get(4));
			g2d.drawLine(vertices.get(0).getRoundX() + shape3D.getX() + lookOffset.getRoundX(),
					vertices.get(0).getRoundY() + shape3D.getY() + lookOffset.getRoundY(),
					vertices.get(3).getRoundX() + shape3D.getX() + lookOffset.getRoundX(),
					vertices.get(3).getRoundY() + shape3D.getY() + lookOffset.getRoundY());
			g2d.drawLine(vertices.get(3).getRoundX() + shape3D.getX() + lookOffset.getRoundX(),
					vertices.get(3).getRoundY() + shape3D.getY() + lookOffset.getRoundY(),
					vertices.get(7).getRoundX() + shape3D.getX() + lookOffset.getRoundX(),
					vertices.get(7).getRoundY() + shape3D.getY() + lookOffset.getRoundY());
			g2d.drawLine(vertices.get(7).getRoundX() + shape3D.getX() + lookOffset.getRoundX(),
					vertices.get(7).getRoundY() + shape3D.getY() + lookOffset.getRoundY(),
					vertices.get(4).getRoundX() + shape3D.getX() + lookOffset.getRoundX(),
					vertices.get(4).getRoundY() + shape3D.getY() + lookOffset.getRoundY());
			g2d.drawLine(vertices.get(4).getRoundX() + shape3D.getX() + lookOffset.getRoundX(),
					vertices.get(4).getRoundY() + shape3D.getY() + lookOffset.getRoundY(),
					vertices.get(0).getRoundX() + shape3D.getX() + lookOffset.getRoundX(),
					vertices.get(0).getRoundY() + shape3D.getY() + lookOffset.getRoundY());
		}

		// Bottom face 3 2 6 7
		if (isCubeFaceVisible(shape3D, vertices.get(3), vertices.get(2), vertices.get(6), vertices.get(7))) {
			drawCubeFace(g2d, faceBottom, lookOffset, shape3D, vertices.get(3), vertices.get(2), vertices.get(6),
					vertices.get(7));
			g2d.drawLine(vertices.get(3).getRoundX() + shape3D.getX() + lookOffset.getRoundX(),
					vertices.get(3).getRoundY() + shape3D.getY() + lookOffset.getRoundY(),
					vertices.get(2).getRoundX() + shape3D.getX() + lookOffset.getRoundX(),
					vertices.get(2).getRoundY() + shape3D.getY() + lookOffset.getRoundY());
			g2d.drawLine(vertices.get(2).getRoundX() + shape3D.getX() + lookOffset.getRoundX(),
					vertices.get(2).getRoundY() + shape3D.getY() + lookOffset.getRoundY(),
					vertices.get(6).getRoundX() + shape3D.getX() + lookOffset.getRoundX(),
					vertices.get(6).getRoundY() + shape3D.getY() + lookOffset.getRoundY());
			g2d.drawLine(vertices.get(6).getRoundX() + shape3D.getX() + lookOffset.getRoundX(),
					vertices.get(6).getRoundY() + shape3D.getY() + lookOffset.getRoundY(),
					vertices.get(7).getRoundX() + shape3D.getX() + lookOffset.getRoundX(),
					vertices.get(7).getRoundY() + shape3D.getY() + lookOffset.getRoundY());
			g2d.drawLine(vertices.get(7).getRoundX() + shape3D.getX() + lookOffset.getRoundX(),
					vertices.get(7).getRoundY() + shape3D.getY() + lookOffset.getRoundY(),
					vertices.get(3).getRoundX() + shape3D.getX() + lookOffset.getRoundX(),
					vertices.get(3).getRoundY() + shape3D.getY() + lookOffset.getRoundY());
		}

		// Left face 1 2 6 5
		if (isCubeFaceVisible(shape3D, vertices.get(1), vertices.get(2), vertices.get(6), vertices.get(5))) {
			drawCubeFace(g2d, faceLeft, lookOffset, shape3D, vertices.get(1), vertices.get(2), vertices.get(6),
					vertices.get(5));
			g2d.drawLine(vertices.get(1).getRoundX() + shape3D.getX() + lookOffset.getRoundX(),
					vertices.get(1).getRoundY() + shape3D.getY() + lookOffset.getRoundY(),
					vertices.get(2).getRoundX() + shape3D.getX() + lookOffset.getRoundX(),
					vertices.get(2).getRoundY() + shape3D.getY() + lookOffset.getRoundY());
			g2d.drawLine(vertices.get(2).getRoundX() + shape3D.getX() + lookOffset.getRoundX(),
					vertices.get(2).getRoundY() + shape3D.getY() + lookOffset.getRoundY(),
					vertices.get(6).getRoundX() + shape3D.getX() + lookOffset.getRoundX(),
					vertices.get(6).getRoundY() + shape3D.getY() + lookOffset.getRoundY());
			g2d.drawLine(vertices.get(6).getRoundX() + shape3D.getX() + lookOffset.getRoundX(),
					vertices.get(6).getRoundY() + shape3D.getY() + lookOffset.getRoundY(),
					vertices.get(5).getRoundX() + shape3D.getX() + lookOffset.getRoundX(),
					vertices.get(5).getRoundY() + shape3D.getY() + lookOffset.getRoundY());
			g2d.drawLine(vertices.get(5).getRoundX() + shape3D.getX() + lookOffset.getRoundX(),
					vertices.get(5).getRoundY() + shape3D.getY() + lookOffset.getRoundY(),
					vertices.get(1).getRoundX() + shape3D.getX() + lookOffset.getRoundX(),
					vertices.get(1).getRoundY() + shape3D.getY() + lookOffset.getRoundY());
		}

		// Back face 4 5 6 7
		if (isCubeFaceVisible(shape3D, vertices.get(4), vertices.get(5), vertices.get(6), vertices.get(7))) {
			drawCubeFace(g2d, faceBack, lookOffset, shape3D, vertices.get(4), vertices.get(5), vertices.get(6),
					vertices.get(7));
			g2d.drawLine(vertices.get(4).getRoundX() + shape3D.getX() + lookOffset.getRoundX(),
					vertices.get(4).getRoundY() + shape3D.getY() + lookOffset.getRoundY(),
					vertices.get(5).getRoundX() + shape3D.getX() + lookOffset.getRoundX(),
					vertices.get(5).getRoundY() + shape3D.getY() + lookOffset.getRoundY());
			g2d.drawLine(vertices.get(5).getRoundX() + shape3D.getX() + lookOffset.getRoundX(),
					vertices.get(5).getRoundY() + shape3D.getY() + lookOffset.getRoundY(),
					vertices.get(6).getRoundX() + shape3D.getX() + lookOffset.getRoundX(),
					vertices.get(6).getRoundY() + shape3D.getY() + lookOffset.getRoundY());
			g2d.drawLine(vertices.get(6).getRoundX() + shape3D.getX() + lookOffset.getRoundX(),
					vertices.get(6).getRoundY() + shape3D.getY() + lookOffset.getRoundY(),
					vertices.get(7).getRoundX() + shape3D.getX() + lookOffset.getRoundX(),
					vertices.get(7).getRoundY() + shape3D.getY() + lookOffset.getRoundY());
			g2d.drawLine(vertices.get(7).getRoundX() + shape3D.getX() + lookOffset.getRoundX(),
					vertices.get(7).getRoundY() + shape3D.getY() + lookOffset.getRoundY(),
					vertices.get(4).getRoundX() + shape3D.getX() + lookOffset.getRoundX(),
					vertices.get(4).getRoundY() + shape3D.getY() + lookOffset.getRoundY());
		}
	}

	private static ArrayList<Point3D> getRotatedVertices3D(CustomShape3D shape) {
		ArrayList<Point3D> vertices = (ArrayList<Point3D>) shape.getVertices3D();

		for (int i = 0; i < vertices.size(); i++) {
			vertices.set(i, getRotatedPoint3D(vertices.get(i), shape.getXRotation(), shape.getYRotation(),
					shape.getZRotation()));
		}

		return vertices;
	}

	public static Point3D getRotatedPoint3D(Point3D Point2D, double xRotation, double yRotation, double zRotation) {

		double heading = yRotation;
		double attitude = zRotation;
		double bank = xRotation;

		double ch = Math.cos(heading);
		double sh = Math.sin(heading);
		double ca = Math.cos(attitude);
		double sa = Math.sin(attitude);
		double cb = Math.cos(bank);
		double sb = Math.sin(bank);

		double m00 = ch * ca;
		double m01 = sh * sb - ch * sa * cb;
		double m02 = ch * sa * sb + sh * cb;
		double m10 = sa;
		double m11 = ca * cb;
		double m12 = -ca * sb;
		double m20 = -sh * ca;
		double m21 = sh * sa * cb + ch * sb;
		double m22 = -sh * sa * sb + ch * cb;

		return new Point3D(Point2D.getRoundX() * m00 + Point2D.getRoundY() * m01 + Point2D.z * m02,
				Point2D.getRoundX() * m10 + Point2D.getRoundY() * m11 + Point2D.z * m12,
				Point2D.getRoundX() * m20 + Point2D.getRoundY() * m21 + Point2D.z * m22);
	}

	public static Point2D getRotatedPoint2D(Point2D rotatedPoint, Point2D pivotPoint, double angle) {
		Point2D newPoint = new Point2D(0, 0);
		newPoint.setLocation(rotatedPoint);

		double s = Math.sin(angle);
		double c = Math.cos(angle);

		// translate Point2D back to origin:
		newPoint.setLocation(rotatedPoint.getX() - pivotPoint.getX(), rotatedPoint.getY() - pivotPoint.getY());

		// rotate Point2D
		double xnew = newPoint.getX() * c - newPoint.getY() * s;
		double ynew = newPoint.getX() * s + newPoint.getY() * c;

		// translate Point2D back:
		newPoint.setLocation(xnew + pivotPoint.getX(), ynew + pivotPoint.getY());
		return newPoint;
	}

	public static double getScalar(int value, int maximumValue) {
		return (double) value / (double) maximumValue;
	}

	public static byte getByteValue(int value, int maximumValue) {
		return (byte) (255 * (double) value / (double) maximumValue);
	}

	public static int getValue(byte value) {
		return (int) value & 0xFF;
	}

	public static int getIntRGBPixel(byte r, byte g, byte b) {
		int pixelRGB = ((int) r & 0xFF);
		pixelRGB = (pixelRGB << 8) | ((int) g & 0xFF);
		pixelRGB = (pixelRGB << 8) | ((int) b & 0xFF);
		return pixelRGB;
	}

	public static int getIntRGBPixel(int r, int g, int b) {
		return getIntRGBPixel((byte) r, (byte) g, (byte) b);
	}

	public static byte[] getRGBValues(int pixel) {
		byte b = (byte) (pixel & 0xFF);
		byte g = (byte) ((pixel >> 8) & 0xFF);
		byte r = (byte) ((pixel >> 16) & 0xFF);
		return new byte[] { r, g, b };
	}

	public static void drawImage(Graphics2D g2d, CustomImage image, Point2D lookOffset, int canvasWidth,
			int canvasHeight) {
		drawPlaceHolderImage(g2d, image, lookOffset);

		Point2D drawingPoint = new Point2D(Math.min(canvasWidth, Math.max(image.getX() + lookOffset.getRoundX(), 0)),
				Math.min(canvasHeight, (Math.max(image.getY() + lookOffset.getRoundY(), 0))));
		int startingWidth = Math.abs(Math.min(0, image.getX() + lookOffset.getRoundX()));
		int startingHeight = Math.abs(Math.min(0, image.getY() + lookOffset.getRoundY()));
		int drawingWidth = Math.min(Math.min(image.getWidth() - startingWidth, canvasWidth),
				canvasWidth - drawingPoint.getRoundX());
		int drawingHeight = Math.min(Math.min(image.getHeight() - startingHeight, canvasHeight),
				canvasHeight - drawingPoint.getRoundY());

		// System.out.println(drawingPoint.getX() + ", " + drawingPoint.getY());
		// System.out.println("startingWidth: " + startingWidth + "
		// startingHeight: " + startingHeight + " drawingWidth: "
		// + drawingWidth + " drawingHeight: " + drawingHeight);

		if (drawingWidth >= 1 && drawingHeight >= 1 && drawingPoint.getX() < canvasWidth
				&& drawingPoint.getY() < canvasHeight) {
			byte[] graphic = image.getGraphic(image.getWidth(), image.getHeight());

			BufferedImage bufferedImage = new BufferedImage(drawingWidth, drawingHeight, BufferedImage.TYPE_INT_RGB);

			byte[][] drawingGraphic = new byte[drawingHeight][drawingWidth * 3];

			int index = startingHeight * image.getWidth() * 3 + startingWidth * 3;
			// System.out.println("graphic length: " + graphic.length);

			for (int row = 0; row < drawingHeight; row++) {
				for (int col = 0; col < drawingWidth; col++) {
					drawingGraphic[row][col * 3] = graphic[index];
					drawingGraphic[row][col * 3 + 1] = graphic[index + 1];
					drawingGraphic[row][col * 3 + 2] = graphic[index + 2];
					index += 3;
				}
				index += startingWidth * 3 + (image.getWidth() - startingWidth - drawingWidth) * 3;
			}

			for (int row = 0; row < drawingHeight; row++) {
				for (int col = 0; col < drawingWidth; col++) {
					bufferedImage.setRGB(col, row, getIntRGBPixel(drawingGraphic[row][col * 3],
							drawingGraphic[row][col * 3 + 1], drawingGraphic[row][col * 3 + 2]));
					index += 3;
				}
			}

			g2d.drawImage(bufferedImage, drawingPoint.getRoundX(), drawingPoint.getRoundY(), drawingWidth, drawingHeight, null);
		}
	}

	public static byte[] getScaledGraphic(byte[] graphic, int oldWidth, int oldHeight, int newWidth, int newHeight) {
		byte[] scaledGraphic = new byte[newWidth * newHeight * 3];

		int index = 0;
		double heightScalar = (double) oldHeight / (double) newHeight;
		double widthScalar = (double) oldWidth / (double) newWidth;
		for (int row = 0; row < newHeight; row++) {
			for (int col = 0; col < newWidth; col++) {
				int oldIndex = (int) (((int) ((double) row * heightScalar)) * (double) oldWidth * 3)
						+ (int) ((double) col * 3 * widthScalar);
				if (oldIndex % 3 == 1) {
					oldIndex -= 1;
				} else if (oldIndex % 3 == 2) {
					oldIndex -= 2;
				}
				scaledGraphic[index] = graphic[oldIndex];
				index++;
				scaledGraphic[index] = graphic[oldIndex + 1];
				index++;
				scaledGraphic[index] = graphic[oldIndex + 2];
				index++;
			}
		}

		return scaledGraphic;
	}

	private static void drawPlaceHolderImage(Graphics2D g2d, CustomImage image, Point2D lookOffset) {
		g2d.setColor(Color.BLACK);
		g2d.fillRect(image.getX() + lookOffset.getRoundX(), image.getY() + lookOffset.getRoundY(), (int) image.getWidth(),
				(int) image.getHeight());
		g2d.setColor(Color.WHITE);
		g2d.drawLine(image.getX() + lookOffset.getRoundX(), image.getY() + lookOffset.getRoundY(),
				image.getX() + lookOffset.getRoundX() + (int) image.getWidth(),
				image.getY() + lookOffset.getRoundY() + (int) image.getHeight());
		g2d.drawLine(image.getX() + lookOffset.getRoundX(), image.getY() + lookOffset.getRoundY() + (int) image.getHeight(),
				image.getX() + lookOffset.getRoundX() + (int) image.getWidth(), image.getY() + lookOffset.getRoundY());
		g2d.setColor(Color.BLACK);
	}

	public static BufferedImage getBufferedImage(ArrayList<CustomShape> shapeList, CustomShape workspace) {
		BufferedImage bufferedImage = new BufferedImage(workspace.getWidth(), workspace.getHeight(),
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = bufferedImage.createGraphics();

		CustomShape workspaceShape = shapeList.get(0);
		Functions2D.drawWorkspaceBase(g2d, workspaceShape, new Point2D(-workspaceShape.getX(), -workspaceShape.getY()));

		for (int i = 1; i < shapeList.size(); i++) {
			Functions2D.drawShape(g2d, shapeList.get(i), new Point2D(-workspaceShape.getX(), -workspaceShape.getY()), 0,
					workspace.getWidth(), workspace.getHeight());
		}

		return bufferedImage;
	}

	public static BufferedImage getBufferedImage(CustomImage imageShape) {
		BufferedImage img = new BufferedImage(imageShape.graphicWidth, imageShape.graphicHeight,
				BufferedImage.TYPE_INT_RGB);
		byte[] graphic = imageShape.getGraphic(imageShape.graphicWidth, imageShape.graphicHeight);
		int[] graphicAsIntArray = new int[graphic.length / 3];

		for (int i = 0; i < graphicAsIntArray.length; i++) {
			graphicAsIntArray[i] = Functions2D.getIntRGBPixel(graphic[i * 3], graphic[i * 3 + 1], graphic[i * 3 + 2]);
		}

		img.setRGB(0, 0, img.getWidth(), img.getHeight(), graphicAsIntArray, 0, 0);

		return img;
	}

	public static int[][] getRGBIntGraphic(byte[] graphic, int width, int height) {
		int[][] newGraphic = new int[width][];
		for (int i = 0; i < width; i++) {
			newGraphic[i] = new int[height];
		}

		int index = 0;
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				byte r = graphic[index];
				byte g = graphic[index + 1];
				byte b = graphic[index + 2];
				index += 3;
				newGraphic[col][row] = Functions2D.getIntRGBPixel(r, g, b);
			}
		}

		return newGraphic;
	}

	public static byte[] getRGBGraphic(int[][] graphic, int width, int height) {
		return new byte[] {};
	}

}
