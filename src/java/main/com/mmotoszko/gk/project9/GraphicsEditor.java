/*******************************************************************************
 * Micha³ Motoszko
 * 2018-11-27
 * Grafika komputerowa PS 9
 * Projekt 9
 ******************************************************************************/
package com.mmotoszko.gk.project9;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.*;

import com.mmotoszko.gk.project9.drawing.Filters;
import com.mmotoszko.gk.project9.drawing.Point2D;
import com.mmotoszko.gk.project9.drawing.ShapeType;
import com.mmotoszko.gk.project9.drawing.customshape.CustomShape;
import com.mmotoszko.gk.project9.drawing.customshape.CustomShape2D;
import com.mmotoszko.gk.project9.events.ActionType;
import com.mmotoszko.gk.project9.events.ButtonClickListener;
import com.mmotoszko.gk.project9.events.CanvasClickListener;
import com.mmotoszko.gk.project9.events.ColorPickerClickListener;
import com.mmotoszko.gk.project9.events.DetailsMoveButtonClickListener;
import com.mmotoszko.gk.project9.events.DetailsSaveButtonClickListener;
import com.mmotoszko.gk.project9.events.DetailsScaleButtonClickListener;
import com.mmotoszko.gk.project9.events.menu.edit.AddImageFilterClickListener;
import com.mmotoszko.gk.project9.events.menu.edit.ModifyHistogramClickListener;
import com.mmotoszko.gk.project9.events.menu.file.ExportFileClickListener;
import com.mmotoszko.gk.project9.events.menu.file.ImportFileClickListener;
import com.mmotoszko.gk.project9.events.menu.file.NewFileClickListener;
import com.mmotoszko.gk.project9.events.menu.file.OpenFileClickListener;
import com.mmotoszko.gk.project9.events.menu.file.SaveFileClickListener;
import com.mmotoszko.gk.project9.ui.CursorChanger;
import com.mmotoszko.gk.project9.ui.CustomCanvas;
import com.mmotoszko.gk.project9.ui.RulerLeft;
import com.mmotoszko.gk.project9.ui.RulerTop;
import com.mmotoszko.gk.project9.ui.TextFieldContainer;
import com.mmotoszko.gk.project9.ui.TopMenuButton;

public class GraphicsEditor extends JFrame {
	private static final long serialVersionUID = 1L;
	public static final int VERTICE_RADIUS = 4;
	public static final String WINDOW_NAME = "GK Project 9";

	private static GraphicsEditor instance = null;

	public CursorChanger cursor;
	public Color drawingColor = Color.blue;
	public JPanel mainPanel = new JPanel();
	public JPanel centerPanel = new JPanel();
	public JPanel canvasPanel;
	public JPanel detailsPanel;
	public String mouseMode = ActionType.LOOK.toString();
	public JLabel mouseModeLabel = new JLabel(mouseMode);
	public boolean drawing = false;
	public ArrayList<CustomShape> shapeList = new ArrayList<>();
	public CustomShape focusedShape = null;
	public Point2D focusedVertice = null;
	public Point2D pressPoint = new Point2D(0, 0);
	public Point2D lookOffset = new Point2D(0, 0);
	private int shapeListIteratorIndex = 0;

	public static GraphicsEditor getInstance() {
		if (instance == null) {
			instance = new GraphicsEditor();
			instance.canvasPanel = new CustomCanvas(instance);
			try {
				CustomShape workspace = new CustomShape2D(new Point2D(0, 0), 1000, 500, ShapeType.RECTANGLE);
				workspace.setColor(Color.WHITE);
				workspace.setFill(true);
				workspace.setType(ShapeType.WORKSPACE);
				instance.shapeList.add(workspace);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return instance;
	}

	public boolean mouseModeIs(ActionType mode) {
		return mouseMode.equalsIgnoreCase(mode.toString());
	}

	public boolean mouseModeIs(ShapeType mode) {
		return mouseMode.equalsIgnoreCase(mode.toString());
	}

	public JPanel getTopPanel() {
		JPanel topPanel = new JPanel();
		JToolBar toolBar = new JToolBar();

		topPanel.setLayout(new BorderLayout());

		toolBar.setMargin(new Insets(4, 8, 4, 10));
		toolBar.setFloatable(false);

		JButton lookButton = new TopMenuButton("/look.png", ActionType.LOOK.toString(),
				new ButtonClickListener(instance));
		JButton selectButton = new TopMenuButton("/select.png", ActionType.SELECT.toString(),
				new ButtonClickListener(instance));
		JButton rotateButton = new TopMenuButton("/rotate.png", ActionType.ROTATE.toString(),
				new ButtonClickListener(instance));
		JButton colorPickerButton = new TopMenuButton(drawingColor, "", new ColorPickerClickListener(instance));
		JButton circleButton = new TopMenuButton("/circle.png", ShapeType.CIRCLE.toString(),
				new ButtonClickListener(instance));
		JButton triangleButton = new TopMenuButton("/triangle.png", ShapeType.TRIANGLE.toString(),
				new ButtonClickListener(instance));
		JButton rectangleButton = new TopMenuButton("/rectangle.png", ShapeType.RECTANGLE.toString(),
				new ButtonClickListener(instance));
		JButton polygonButton = new TopMenuButton("/polygon.png", ShapeType.POLYGON.toString(),
				new ButtonClickListener(instance));
		JButton lineButton = new TopMenuButton("/line.png", ShapeType.LINE.toString(),
				new ButtonClickListener(instance));
		JButton freeLineButton = new TopMenuButton("/free_line.png", ShapeType.FREE_LINE.toString(),
				new ButtonClickListener(instance));
		JButton bezierCurveButton = new TopMenuButton("/bezier_curve.png", ShapeType.BEZIER_CURVE.toString(),
				new ButtonClickListener(instance));
		JButton cubeButton = new TopMenuButton("/cube.png", ShapeType.CUBE.toString(),
				new ButtonClickListener(instance));
		JButton drawCubeFacesButton = new TopMenuButton(Color.GRAY, "drawCubeFaces", new ButtonClickListener(instance));

		toolBar.add(lookButton);
		toolBar.add(selectButton);
		toolBar.add(rotateButton);
		toolBar.add(new JToolBar.Separator());
		toolBar.add(colorPickerButton);
		toolBar.add(new JToolBar.Separator());
		toolBar.add(circleButton);
		toolBar.add(triangleButton);
		toolBar.add(rectangleButton);
		toolBar.add(polygonButton);
		toolBar.add(lineButton);
		toolBar.add(freeLineButton);
		toolBar.add(bezierCurveButton);
		toolBar.add(new JToolBar.Separator());
		toolBar.add(cubeButton);
		toolBar.add(drawCubeFacesButton);

		toolBar.add(mouseModeLabel);

		JMenuBar menuBar = getCustomMenuBar();

		menuBar.setPreferredSize(new Dimension((int) mainPanel.getSize().getWidth(), 25));// new
																							// Dimension(584,
																							// 25));
		toolBar.setPreferredSize(new Dimension((int) mainPanel.getSize().getWidth(), 45));// new
																							// Dimension(584,
																							// 45));

		topPanel.add(menuBar, BorderLayout.PAGE_START);
		topPanel.add(toolBar, BorderLayout.PAGE_END);

		return topPanel;
	}

	public JMenuBar getCustomMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menuFile = new JMenu("File");
		JMenu menuEdit = new JMenu("Edit");
		JMenu menuHelp = new JMenu("Help");

		JMenuItem fileNewMenuItem = new JMenuItem("New", KeyEvent.VK_N);
		fileNewMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
		JMenuItem fileOpenMenuItem = new JMenuItem("Open...", KeyEvent.VK_O);
		fileOpenMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
		JMenuItem fileSaveMenuItem = new JMenuItem("Save...", KeyEvent.VK_S);
		fileSaveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
		JMenuItem fileImportMenuItem = new JMenuItem("Import...", KeyEvent.VK_I);
		fileImportMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.CTRL_DOWN_MASK));
		JMenuItem fileExportMenuItem = new JMenuItem("Export...", KeyEvent.VK_E);
		fileExportMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_DOWN_MASK));

		fileNewMenuItem.addActionListener(new NewFileClickListener(instance));
		fileOpenMenuItem.addActionListener(new OpenFileClickListener(instance));
		fileSaveMenuItem.addActionListener(new SaveFileClickListener(instance));
		fileImportMenuItem.addActionListener(new ImportFileClickListener(instance));
		fileExportMenuItem.addActionListener(new ExportFileClickListener(instance));

		menuFile.add(fileNewMenuItem);
		menuFile.add(fileOpenMenuItem);
		menuFile.add(fileSaveMenuItem);
		menuFile.addSeparator();
		menuFile.add(fileImportMenuItem);
		menuFile.add(fileExportMenuItem);

		// Adjust submenu
		JMenu editAdjustMenu = new JMenu("Adjust");

		JMenuItem editAdjustColorMenuItem = new JMenuItem("Color...", KeyEvent.VK_C);
		JMenuItem editAdjustBrightnessMenuItem = new JMenuItem("Brightness...", KeyEvent.VK_C);

		editAdjustColorMenuItem.addActionListener(new AddImageFilterClickListener(instance, Filters.COLOR));
		editAdjustBrightnessMenuItem.addActionListener(new AddImageFilterClickListener(instance, Filters.BRIGHTNESS));

		editAdjustMenu.add(editAdjustColorMenuItem);
		editAdjustMenu.add(editAdjustBrightnessMenuItem);
		//

		// Add filter submenu
		JMenu editAddFilterMenu = new JMenu("Filters");

		JMenuItem editAddFilterAveragingMenuItem = new JMenuItem("Average blur...", KeyEvent.VK_A);
		JMenuItem editAddFilterGaussMenuItem = new JMenuItem("Gauss blur...", KeyEvent.VK_G);
		JMenuItem editAddFilterHighPassMenuItem = new JMenuItem("Sharpen...", KeyEvent.VK_H);
		JMenuItem editAddFilterMedianMenuItem = new JMenuItem("Median...", KeyEvent.VK_M);
		JMenuItem editAddFilterEdgeFinderMenuItem = new JMenuItem("Outline...", KeyEvent.VK_E);
		JMenuItem editAddFilterSobelMenuItem = new JMenuItem("Sobel...", KeyEvent.VK_S);
		JMenuItem editAddFilterGrayscaleMenuItem = new JMenuItem("Grayscale...", KeyEvent.VK_G);
		JMenuItem editAddFilterGrayscaleMeanMenuItem = new JMenuItem("Grayscale mean...", KeyEvent.VK_M);
		JMenuItem editAddFilterSpecklingMenuItem = new JMenuItem("Speckle...", KeyEvent.VK_P);
		JMenuItem editAddFilterCustomMenuItem = new JMenuItem("Custom...", KeyEvent.VK_C);

		editAddFilterAveragingMenuItem.addActionListener(new AddImageFilterClickListener(instance, Filters.AVERAGE));
		editAddFilterGaussMenuItem.addActionListener(new AddImageFilterClickListener(instance, Filters.GAUSS));
		editAddFilterHighPassMenuItem.addActionListener(new AddImageFilterClickListener(instance, Filters.HIGHPASS));
		editAddFilterMedianMenuItem.addActionListener(new AddImageFilterClickListener(instance, Filters.MEDIAN));
		editAddFilterEdgeFinderMenuItem
				.addActionListener(new AddImageFilterClickListener(instance, Filters.EDGEFINDER));
		editAddFilterSobelMenuItem.addActionListener(new AddImageFilterClickListener(instance, Filters.SOBELLEFT));
		editAddFilterGrayscaleMenuItem.addActionListener(new AddImageFilterClickListener(instance, Filters.GRAYSCALE));
		editAddFilterGrayscaleMeanMenuItem
				.addActionListener(new AddImageFilterClickListener(instance, Filters.GRAYSCALEMEAN));
		editAddFilterSpecklingMenuItem.addActionListener(new AddImageFilterClickListener(instance, Filters.SPECKLE));
		editAddFilterCustomMenuItem.addActionListener(new AddImageFilterClickListener(instance, Filters.CUSTOM));

		editAddFilterMenu.add(editAddFilterAveragingMenuItem);
		editAddFilterMenu.add(editAddFilterGaussMenuItem);
		editAddFilterMenu.add(editAddFilterHighPassMenuItem);
		editAddFilterMenu.addSeparator();
		editAddFilterMenu.add(editAddFilterMedianMenuItem);
		editAddFilterMenu.addSeparator();
		editAddFilterMenu.add(editAddFilterEdgeFinderMenuItem);
		editAddFilterMenu.add(editAddFilterSobelMenuItem);
		editAddFilterMenu.addSeparator();
		editAddFilterMenu.add(editAddFilterGrayscaleMenuItem);
		editAddFilterMenu.add(editAddFilterGrayscaleMeanMenuItem);
		editAddFilterMenu.add(editAddFilterSpecklingMenuItem);
		editAddFilterMenu.add(editAddFilterCustomMenuItem);
		//

		// Add morphological filter submenu
		JMenu editMFilterMenu = new JMenu("Morph. filters");

		JMenuItem editMFilterDilationMenuItem = new JMenuItem("Dilation...", KeyEvent.VK_D);
		JMenuItem editMFilterErosionMenuItem = new JMenuItem("Erosion...", KeyEvent.VK_E);
		JMenuItem editMFilterOpeningMenuItem = new JMenuItem("Opening...", KeyEvent.VK_O);
		JMenuItem editMFilterClosingMenuItem = new JMenuItem("Closing...", KeyEvent.VK_C);
		JMenuItem editMFilterHitOrMissMenuItem = new JMenuItem("Hit-or-miss...", KeyEvent.VK_H);

		editMFilterDilationMenuItem.addActionListener(new AddImageFilterClickListener(instance, Filters.DILATION));
		editMFilterErosionMenuItem.addActionListener(new AddImageFilterClickListener(instance, Filters.EROSION));
		editMFilterOpeningMenuItem.addActionListener(new AddImageFilterClickListener(instance, Filters.OPENING));
		editMFilterClosingMenuItem.addActionListener(new AddImageFilterClickListener(instance, Filters.CLOSING));
		editMFilterHitOrMissMenuItem.addActionListener(new AddImageFilterClickListener(instance, Filters.HIT_OR_MISS));

		editMFilterMenu.add(editMFilterDilationMenuItem);
		editMFilterMenu.add(editMFilterErosionMenuItem);
		editMFilterMenu.addSeparator();
		editMFilterMenu.add(editMFilterOpeningMenuItem);
		editMFilterMenu.add(editMFilterClosingMenuItem);
		editMFilterMenu.addSeparator();
		editMFilterMenu.add(editMFilterHitOrMissMenuItem);

		editMFilterHitOrMissMenuItem.setEnabled(false);
		//

		// Add find color filter menu item
		JMenuItem editMFilterFindColorMenuItem = new JMenuItem("Find color...", KeyEvent.VK_F);
		editMFilterFindColorMenuItem.addActionListener(new AddImageFilterClickListener(instance, Filters.FIND_COLOR));
		//

		// Histogram
		JMenuItem editHistogramMenuItem = new JMenuItem("Histogram...");
		editHistogramMenuItem.addActionListener(new ModifyHistogramClickListener(instance));
		//

		menuEdit.add(editAdjustMenu);
		menuEdit.addSeparator();
		menuEdit.add(editAddFilterMenu);
		menuEdit.add(editMFilterMenu);
		menuEdit.addSeparator();
		menuEdit.add(editMFilterFindColorMenuItem);
		menuEdit.addSeparator();
		menuEdit.add(editHistogramMenuItem);

		// Help submenu
		JMenuItem helpAboutMenuItem = new JMenuItem("About...");
		helpAboutMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Object[] options = { "Got it!" };
				JOptionPane.showOptionDialog(mainPanel, getAboutMessage(), "About", JOptionPane.PLAIN_MESSAGE,
						JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
			}
		});

		menuHelp.add(helpAboutMenuItem);
		//

		menuBar.add(menuFile);
		menuBar.add(menuEdit);
		menuBar.add(menuHelp);

		return menuBar;
	}

	private String getAboutMessage() {
		StringBuilder message = new StringBuilder();

		message.append("Created by:\n").append("        Micha³ Motoszko\n").append("        www.mmotoszko.com\n")
				.append("        2018-11-23\n").append("\n").append("Controls:\n")
				.append("        Left mouse click:         most actions\n")
				.append("        Right mouse click:      stop drawing\n")
				.append("        Middle mouse click:   set arbitrary point used in rotation\n");

		return message.toString();
	}

	public JPanel getCenterPanel() {
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BorderLayout());

		centerPanel.add(new RulerTop(instance), BorderLayout.PAGE_START);
		centerPanel.add(new RulerLeft(instance), BorderLayout.LINE_START);
		centerPanel.add(canvasPanel, BorderLayout.CENTER);

		return centerPanel;
	}

	public JPanel getMoveAndScalePanel(JTextField moveXTextField, JTextField moveYTextField, JButton moveButton,
			JTextField scaleXTextField, JTextField scaleYTextField, JButton scaleButton) {
		JPanel moveAndScalePanel = new JPanel();

		JPanel moveVectorPanel = new JPanel(new BorderLayout(0, 0));
		moveXTextField.setPreferredSize(new Dimension(60, 25));

		JPanel moveVectorPanelTitle = new JPanel();
		JPanel moveVectorPanelFields = new JPanel(new GridLayout(2, 4));

		moveVectorPanelTitle.add(new JLabel("Move by vector:"));
		moveVectorPanelFields.add(new JLabel("x: ", JLabel.RIGHT));
		moveVectorPanelFields.add(moveXTextField);
		moveVectorPanelFields.add(new JLabel("y: ", JLabel.RIGHT));
		moveVectorPanelFields.add(moveYTextField);

		moveVectorPanel.add(moveVectorPanelTitle, BorderLayout.PAGE_START);
		moveVectorPanel.add(moveVectorPanelFields, BorderLayout.CENTER);
		moveVectorPanel.add(moveButton, BorderLayout.LINE_END);

		JPanel scaleVectorPanel = new JPanel(new BorderLayout(2, 1));
		scaleXTextField.setPreferredSize(new Dimension(60, 25));

		JPanel scaleVectorPanelTitle = new JPanel();
		JPanel scaleVectorPanelFields = new JPanel(new GridLayout(2, 4));

		scaleVectorPanelTitle.add(new JLabel("Scale by vector:"));
		scaleVectorPanelFields.add(new JLabel("x: ", JLabel.RIGHT));
		scaleVectorPanelFields.add(scaleXTextField);
		scaleVectorPanelFields.add(new JLabel("y: ", JLabel.RIGHT));
		scaleVectorPanelFields.add(scaleYTextField);

		scaleVectorPanel.add(scaleVectorPanelTitle, BorderLayout.PAGE_START);
		scaleVectorPanel.add(scaleVectorPanelFields, BorderLayout.CENTER);
		scaleVectorPanel.add(scaleButton, BorderLayout.LINE_END);

		moveAndScalePanel.add(moveVectorPanel);
		moveAndScalePanel.add(scaleVectorPanel);

		return moveAndScalePanel;
	}

	public JPanel getDetailsPanel() {
		JPanel mainDetailsPanel = new JPanel(new BorderLayout(5, 5));

		JTextField shapeXTextField = null;
		JTextField shapeYTextField = null;
		JTextField shapeWidthTextField = null;
		JTextField shapeHeightTextField = null;
		JTextField verticeXTextField = null;
		JTextField verticeYTextField = null;
		JTextField moveXTextField = null;
		JTextField moveYTextField = null;
		JTextField scaleXTextField = null;
		JTextField scaleYTextField = null;

		JButton saveButton = new JButton("Save");
		JPanel saveButtonPanel = new JPanel();
		saveButtonPanel.setLayout(new FlowLayout());
		saveButton.setHorizontalAlignment(JButton.LEFT);
		saveButtonPanel.add(saveButton);

		JButton moveButton = new JButton("Move");
		JPanel moveButtonPanel = new JPanel();
		moveButtonPanel.setLayout(new FlowLayout());
		saveButton.setHorizontalAlignment(JButton.LEFT);
		moveButtonPanel.add(moveButton);

		JButton scaleButton = new JButton("Scale");
		JPanel scaleButtonPanel = new JPanel();
		scaleButtonPanel.setLayout(new FlowLayout());
		saveButton.setHorizontalAlignment(JButton.LEFT);
		scaleButtonPanel.add(scaleButton);

		JPanel detailsPanel = new JPanel(new GridLayout(1, 0, 15, 0));
		detailsPanel.setPreferredSize(new Dimension(100, 50));

		if (focusedShape != null) {
			moveXTextField = new JTextField(0 + "");
			moveYTextField = new JTextField(0 + "");
			scaleXTextField = new JTextField(1 + ".0");
			scaleYTextField = new JTextField(1 + ".0");
			mainDetailsPanel.add(getMoveAndScalePanel(moveXTextField, moveYTextField, moveButton, scaleXTextField,
					scaleYTextField, scaleButton), BorderLayout.PAGE_END);

			JPanel dimensionsPanel = new JPanel(new GridLayout(0, 4));

			dimensionsPanel.add(new JLabel("x: ", JLabel.RIGHT));
			shapeXTextField = new JTextField(focusedShape.getX() + "");
			dimensionsPanel.add(shapeXTextField);

			if (focusedShape.getType() != ShapeType.CIRCLE) {
				dimensionsPanel.add(new JLabel("width: ", JLabel.RIGHT));
				shapeWidthTextField = new JTextField(focusedShape.getWidth() + "");
				dimensionsPanel.add(shapeWidthTextField);
			} else {
				dimensionsPanel.add(new JLabel("radius: ", JLabel.RIGHT));
				shapeWidthTextField = new JTextField(focusedShape.getRadius() + "");
				dimensionsPanel.add(shapeWidthTextField);
			}

			dimensionsPanel.add(new JLabel("y: ", JLabel.RIGHT));
			shapeYTextField = new JTextField(focusedShape.getY() + "");
			dimensionsPanel.add(shapeYTextField);

			if (focusedShape.getType() != ShapeType.CIRCLE && focusedShape.getType() != ShapeType.CUBE) {
				dimensionsPanel.add(new JLabel("height: ", JLabel.RIGHT));
				shapeHeightTextField = new JTextField(focusedShape.getHeight() + "");
				dimensionsPanel.add(shapeHeightTextField);
			}

			detailsPanel.add(dimensionsPanel);
			saveButton.setActionCommand(ActionType.SET_SHAPE_VALUES.toString());

			// editing a single shape vertex via textfields
			if (focusedVertice != null) {
				JPanel singleVerticePanel = new JPanel();
				singleVerticePanel.setLayout(new GridLayout(0, 3));
				JLabel labelSelected = new JLabel("Selected", JLabel.RIGHT);
				singleVerticePanel.add(labelSelected);

				singleVerticePanel.add(new JLabel("x: ", JLabel.RIGHT));
				verticeXTextField = new JTextField(focusedVertice.getRoundX() + "");
				singleVerticePanel.add(verticeXTextField);

				singleVerticePanel.add(new JLabel("point:", JLabel.RIGHT));

				singleVerticePanel.add(new JLabel("y: ", JLabel.RIGHT));
				verticeYTextField = new JTextField(focusedVertice.getRoundY() + "");
				singleVerticePanel.add(verticeYTextField);
				detailsPanel.add(singleVerticePanel);
			} else {

			}

			detailsPanel.add(saveButtonPanel);
		} else if (mouseModeIs(ShapeType.CIRCLE) || mouseModeIs(ShapeType.LINE) || mouseModeIs(ShapeType.RECTANGLE)) {
			JPanel dimensionsPanel = new JPanel(new GridLayout(0, 4));

			dimensionsPanel.add(new JLabel("x: ", JLabel.RIGHT));
			shapeXTextField = new JTextField("");
			dimensionsPanel.add(shapeXTextField);

			if (!mouseModeIs(ShapeType.CIRCLE)) {
				dimensionsPanel.add(new JLabel("width: ", JLabel.RIGHT));
				shapeWidthTextField = new JTextField("");
				dimensionsPanel.add(shapeWidthTextField);
			} else {
				dimensionsPanel.add(new JLabel("radius: ", JLabel.RIGHT));
				shapeWidthTextField = new JTextField("");
				dimensionsPanel.add(shapeWidthTextField);
			}

			dimensionsPanel.add(new JLabel("y: ", JLabel.RIGHT));
			shapeYTextField = new JTextField("");
			dimensionsPanel.add(shapeYTextField);

			if (!mouseModeIs(ShapeType.CIRCLE) && !mouseModeIs(ShapeType.CUBE)) {
				dimensionsPanel.add(new JLabel("height: ", JLabel.RIGHT));
				shapeHeightTextField = new JTextField("");
				dimensionsPanel.add(shapeHeightTextField);
			}

			detailsPanel.add(dimensionsPanel);
			saveButton.setActionCommand(ActionType.CREATE_SHAPE.toString());

			detailsPanel.add(saveButtonPanel);
		}

		TextFieldContainer allFields = new TextFieldContainer(shapeXTextField, shapeYTextField, shapeWidthTextField,
				shapeHeightTextField, verticeXTextField, verticeYTextField, moveXTextField, moveYTextField,
				scaleXTextField, scaleYTextField);

		saveButton.addActionListener(new DetailsSaveButtonClickListener(instance, allFields));
		moveButton.addActionListener(new DetailsMoveButtonClickListener(instance, allFields));
		scaleButton.addActionListener(new DetailsScaleButtonClickListener(instance, allFields));

		mainDetailsPanel.add(detailsPanel, BorderLayout.PAGE_START);

		return mainDetailsPanel;
	}

	public void resetEnvironmentVariables() {
		// mouseMode = ActionType.LOOK.toString();
		// mouseModeLabel = new JLabel(mouseMode);
		drawing = false;
		focusedShape = null;
		focusedVertice = null;
		shapeList.clear();
		shapeList = new ArrayList<>();
		pressPoint.setLocation(0, 0);
		lookOffset.setLocation(0, 0);
		shapeListIteratorIndex = 0;
		CustomShape2D workspace;
		try {
			workspace = new CustomShape2D(new Point2D(0, 0), 1000, 500, ShapeType.RECTANGLE);
			workspace.color = Color.WHITE;
			workspace.fill = true;
			workspace.type = ShapeType.WORKSPACE;
			instance.shapeList.add(workspace);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void newFile() {
		resetEnvironmentVariables();
		updateDetailsPanel();
	}

	public void refresh() {
		canvasPanel.repaint();
		updateDetailsPanel();
	}

	public void addShape(CustomShape shape) {
		shapeList.add(shape);
		focusedVertice = null;
		focusedShape = shape;
		updateDetailsPanel();
		canvasPanel.repaint();
		shapeListIteratorIndex = shapeList.size();
	}

	public void updateDetailsPanel() {
		mainPanel.remove(detailsPanel);
		detailsPanel = getDetailsPanel();
		mainPanel.add(detailsPanel, BorderLayout.PAGE_END);
		mainPanel.updateUI();
	}

	public CustomShape getNextShape() {
		shapeListIteratorIndex--;
		if (shapeListIteratorIndex < 0) {
			shapeListIteratorIndex = shapeList.size() - 1;
		}
		CustomShape viableShape = shapeList.get(shapeListIteratorIndex);
		if (viableShape == focusedShape) {
			if (shapeList.size() > 1) {
				return getNextShape();
			}
		}
		return viableShape;
	}

	public Point2D getMouseLocation() {
		Point2D mousePosition = new Point2D(canvasPanel.getMousePosition());
		mousePosition.move(-lookOffset.getX(), -lookOffset.getY());
		return mousePosition;// MouseInfo.getPointerInfo().getLocation();
	}

	public void start() {
		JFrame mainFrame = new JFrame(WINDOW_NAME);
		mainFrame.setSize(600, 600);
		mainFrame.add(mainPanel);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		cursor = new CursorChanger(mainFrame);

		canvasPanel.setBackground(Color.WHITE);
		canvasPanel.addMouseListener(new CanvasClickListener(instance));
		canvasPanel.addMouseMotionListener(new CanvasClickListener(instance));

		detailsPanel = getDetailsPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(detailsPanel, BorderLayout.PAGE_END);
		mainPanel.add(getTopPanel(), BorderLayout.PAGE_START);
		mainPanel.add(getCenterPanel(), BorderLayout.CENTER);

		mainFrame.setVisible(true);
	}

	public ArrayList<CustomShape> getShapeList() {
		return new ArrayList<CustomShape>(shapeList);
	}

	@SuppressWarnings("unchecked")
	public void setShapeList(ArrayList<CustomShape> shapeList) {
		this.shapeList = (ArrayList<CustomShape>) shapeList.clone();
	}

	@SuppressWarnings("unchecked")
	public void loadShapeList(Object shapeList) {
		resetEnvironmentVariables();
		this.shapeList = (ArrayList<CustomShape>) shapeList;
	}

	public CustomShape getWorkspace() {
		return shapeList.get(0);
	}

	public void setDrawingColor(Color newColor) {
		this.drawingColor = newColor;
	}
}
