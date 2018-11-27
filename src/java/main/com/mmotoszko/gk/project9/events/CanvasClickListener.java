/*******************************************************************************
 * Micha³ Motoszko
 * 2018-11-27
 * Grafika komputerowa PS 9
 * Projekt 9
 ******************************************************************************/
package com.mmotoszko.gk.project9.events;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.SwingUtilities;

import com.mmotoszko.gk.project9.GraphicsEditor;
import com.mmotoszko.gk.project9.drawing.Point2D;
import com.mmotoszko.gk.project9.drawing.ShapeType;
import com.mmotoszko.gk.project9.drawing.customshape.CustomShape;
import com.mmotoszko.gk.project9.drawing.customshape.CustomShape2D;
import com.mmotoszko.gk.project9.drawing.customshape.CustomShape3D;

public class CanvasClickListener implements MouseListener, MouseMotionListener {
	private final GraphicsEditor ge;

	public CanvasClickListener(GraphicsEditor graphicsEditor) {
		super();
		this.ge = graphicsEditor;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	public void addNewShape2D(ShapeType type) {
		Point2D mouseLoc = ge.getMouseLocation();
		System.out.println(mouseLoc);
		CustomShape newShape = new CustomShape2D(mouseLoc, mouseLoc, type);
		newShape.setColor(ge.drawingColor);
		ge.addShape(newShape);
		ge.focusedShape = newShape;
		ge.drawing = true;
	}

	public void addNewShape3D(ShapeType type) {
		Point2D mouseLoc = ge.getMouseLocation();
		CustomShape newShape = new CustomShape3D(mouseLoc, mouseLoc, type);
		newShape.setColor(ge.drawingColor);
		ge.addShape(newShape);
		ge.focusedShape = newShape;
		ge.drawing = true;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		Point2D previousPressPoint = ge.pressPoint;
		ge.pressPoint = ge.getMouseLocation();

		if (ge.mouseModeIs(ActionType.LOOK)) {

		} else if (ge.mouseModeIs(ActionType.ROTATE)) {
			if (ge.focusedShape != null) {
				if (SwingUtilities.isMiddleMouseButton(e)) {
					ge.focusedVertice = ge.pressPoint;
				} else {
					ge.focusedShape.startRotating(ge.focusedVertice, ge.pressPoint);
				}
			}
		} else if (ge.mouseModeIs(ActionType.SELECT)) {
			if (SwingUtilities.isLeftMouseButton(e)) {
				if (ge.shapeList.size() > 0) {
					int counter = 0;
					CustomShape viableShape = null;
					while (true) {
						counter++;
						CustomShape testedShape = ge.getNextShape();
						if (testedShape.isTouching(ge.pressPoint)) {
							viableShape = testedShape;
							break;
						}
						if (counter >= ge.shapeList.size()) {
							break;
						}
					}

					if (ge.focusedShape != null) {
						if ((previousPressPoint.getX() != ge.pressPoint.getX()
								|| previousPressPoint.getY() != ge.pressPoint.getY())
								&& ge.focusedShape.isTouching(ge.pressPoint)) {
							ge.focusedVertice = ge.focusedShape.getVertice(ge.pressPoint);
						} else if (ge.focusedVertice == null) {
							ge.focusedVertice = ge.focusedShape.getVertice(ge.pressPoint);

							if (ge.focusedVertice == null) {
								ge.focusedShape = viableShape;
							}
						} else if (ge.focusedVertice != null) {
							Point2D testedVertice = ge.focusedShape.getVertice(ge.pressPoint);
							ge.focusedVertice = testedVertice;

							if (ge.focusedShape.isTouching(ge.pressPoint)) {

							} else if (ge.focusedShape != viableShape) {
								ge.focusedShape = viableShape;
								ge.focusedVertice = null;
							}
						}
					} else {
						ge.focusedShape = viableShape;
						ge.focusedVertice = null;
					}
				}
			} else if (SwingUtilities.isMiddleMouseButton(e)) {
				ge.focusedVertice = ge.pressPoint;
			} else {
				ge.focusedShape = null;
				ge.focusedVertice = null;
			}

		} else if (SwingUtilities.isRightMouseButton(e)) {
			if (ge.focusedShape != null) {
				ge.focusedShape.forceComplete();
			}
			ge.drawing = false;
			ge.focusedShape = null;

		} else if (ge.mouseModeIs(ShapeType.CIRCLE)) {
			addNewShape2D(ShapeType.CIRCLE);

		} else if (SwingUtilities.isLeftMouseButton(e) && ge.mouseModeIs(ShapeType.TRIANGLE)) {
			if (ge.drawing) {
				ge.focusedShape.updateShape(ge.getMouseLocation());
				ge.drawing = !ge.focusedShape.isComplete();
			} else {
				addNewShape2D(ShapeType.INCOMPLETE_TRIANGLE);
			}

		} else if (ge.mouseModeIs(ShapeType.RECTANGLE)) {
			addNewShape2D(ShapeType.RECTANGLE);

		} else if (ge.mouseModeIs(ShapeType.SQUARE)) {
			addNewShape2D(ShapeType.SQUARE);

		} else if (ge.mouseModeIs(ShapeType.CUBE)) {
			addNewShape3D(ShapeType.CUBE);

		} else if (SwingUtilities.isLeftMouseButton(e) && ge.mouseModeIs(ShapeType.POLYGON)) {
			if (ge.drawing) {
				ge.focusedShape.updateShape(ge.getMouseLocation());
			} else {
				addNewShape2D(ShapeType.INCOMPLETE);
			}

		} else if (SwingUtilities.isLeftMouseButton(e) && ge.mouseModeIs(ShapeType.BEZIER_CURVE)) {
			if (ge.drawing) {
				ge.focusedShape.updateShape(ge.getMouseLocation());
			} else {
				addNewShape2D(ShapeType.INCOMPLETE_BEZIER_CURVE);
			}

		} else if (ge.mouseModeIs(ShapeType.LINE)) {
			addNewShape2D(ShapeType.LINE);

		} else if (ge.mouseModeIs(ShapeType.FREE_LINE)) {
			addNewShape2D(ShapeType.FREE_LINE);

		} else {

		}

		if (ge.drawing) {
			ge.focusedVertice = null;
		}

		ge.updateDetailsPanel();
		ge.canvasPanel.repaint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (ge.drawing) {
			if (ge.mouseModeIs(ShapeType.TRIANGLE) || ge.mouseModeIs(ShapeType.POLYGON)
					|| ge.mouseModeIs(ShapeType.BEZIER_CURVE)) {

			} else {
				ge.drawing = false;
			}
		} else {

		}

		if (ge.mouseModeIs(ActionType.ROTATE)) {
			if (ge.focusedShape != null) {
				ge.focusedShape.stopRotating();
			}
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		Point2D mouseLocation = ge.getMouseLocation();

		if (ge.focusedShape != null) {
			if (ge.drawing) {
				if (ge.mouseModeIs(ShapeType.TRIANGLE) || ge.mouseModeIs(ShapeType.POLYGON)
						|| ge.mouseModeIs(ShapeType.BEZIER_CURVE)) {
					ge.focusedShape.updateLastVertice(ge.getMouseLocation());
				} else {
					ge.focusedShape.updateShape(ge.getMouseLocation());
				}
			} else {

			}
		}

		if (ge.mouseModeIs(ActionType.LOOK)) {
			mouseLocation = ge.getMouseLocation();
			Point2D vector = new Point2D((ge.pressPoint.getX() - mouseLocation.getX()) * -1,
					(ge.pressPoint.getY() - mouseLocation.getY()) * -1);
			ge.lookOffset.move(vector.getX(), vector.getY());
		}

		if (ge.focusedShape != null && ge.mouseModeIs(ActionType.SELECT)) {
			if (SwingUtilities.isMiddleMouseButton(e)) {
				ge.focusedVertice = mouseLocation;
			} else if (ge.focusedVertice != null) {
				ge.focusedVertice = ge.focusedShape.updateVertice(ge.focusedVertice, ge.getMouseLocation());
			} else {
				mouseLocation = ge.getMouseLocation();
				Point2D vector = new Point2D((ge.pressPoint.getX() - mouseLocation.getX()) * -1,
						(ge.pressPoint.getY() - mouseLocation.getY()) * -1);
				if (ge.focusedShape.getType() != ShapeType.WORKSPACE) {
					ge.focusedShape.move(vector);
				}
				ge.pressPoint = mouseLocation;
			}
		}

		if (ge.focusedShape != null && ge.mouseModeIs(ActionType.ROTATE)) {
			if (ge.focusedShape.getType() == ShapeType.CUBE) {
				((CustomShape3D) ge.focusedShape).rotate(mouseLocation);
			} else if (SwingUtilities.isMiddleMouseButton(e)) {
				ge.focusedVertice = mouseLocation;
			} else {
				((CustomShape2D) ge.focusedShape).rotate(mouseLocation);
			}

		}

		ge.updateDetailsPanel();
		ge.canvasPanel.repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (ge.focusedShape != null) {
			if (ge.drawing) {
				if (ge.mouseModeIs(ShapeType.TRIANGLE) || ge.mouseModeIs(ShapeType.POLYGON)
						|| ge.mouseModeIs(ShapeType.BEZIER_CURVE)) {
					ge.focusedShape.updateLastVertice(ge.getMouseLocation());
				} else {
					ge.focusedShape.updateShape(ge.getMouseLocation());
				}
				ge.updateDetailsPanel();
				ge.canvasPanel.repaint();
			} else {

			}
		}
	}
}
