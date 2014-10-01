package guiStuff;

import hive.World;

import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.print.Pageable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.LookupPaintScale;
import org.jfree.chart.renderer.xy.XYBlockRenderer;
import org.jfree.data.xy.XYZDataset;

import sources.Tree;

import java.awt.BorderLayout;
import javax.swing.SwingConstants;
import java.awt.FlowLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;

/**
 * This builds the gui.
 * 
 * @author ole
 * 
 */
public class MyGui extends JPanel implements ActionListener, MouseListener {
	private static final long serialVersionUID = 1L;
	private World world;
	private JTable jTableTrees;
	private JTable jTableBeehives;
	private SetUpTableData setUpTableDataTree;
	private SetUpTableData setUpTableDataBeehive;
	private JSlider sliderBeeCount;
	private JSpinner treeSpinner;
	private JSlider sliderUpdateSpeed;
	private JSlider sliderBeehiveSize;
	private JSlider sliderHunger;
	private JButton startBtn;
	private JSlider sliderWorldSpeed;
	private AbstractButton saveGame;
	private JScrollPane beehiveScrollpane;
	private JScrollPane treeScrollpane;
	private JFreeChart chart;
	private ChartPanel chartPanel;
	private RefreshChart refresh;
	private JButton stopBtn;
	private JPanel control;
	private JCheckBox checkBoxShowPics;
	private AtomicBoolean showPics;
	private JPanel tablePanel;

	/**
	 * Constructor. At the moment it creates 3 panels: - Table for sources -
	 * Table for beehives - Graphic panel for objects in the world.
	 * 
	 * @param world
	 *            the world
	 * @param frame
	 *            the frame to draw into
	 */
	public MyGui(World world, JFrame frame) {
		Dimension windowSize = frame.getBounds().getSize();
		showPics = new AtomicBoolean(false);
		this.world = world;
		setLayout(new BorderLayout(0, 0));
		// this is the Tree table
		String[] treeColName = { "Name", "Size", "Max Size", "Quality",
				"Refresh", "Type", "X", "Y" };
		JTable treeTable = getJTable(treeColName, jTableTrees);
		setUpTableDataTree = new SetUpTableData(treeTable, world, "Trees");

		treeScrollpane = new JScrollPane();
		treeScrollpane.setViewportView(treeTable);
		treeScrollpane.setPreferredSize(new Dimension(500,100));
		//add(treeScrollpane, BorderLayout.SOUTH);
		setUpTableDataTree.update();

		// this is the beehive table
		String[] BeehiveColName1 = { "Name", "Size", "X", "Y", "Waiting Bees" };
		JTable beehiveTable = getJTable(BeehiveColName1, jTableBeehives);
		setUpTableDataBeehive = new SetUpTableData(beehiveTable, world,
				"Beehives");
		beehiveScrollpane = new JScrollPane();
		beehiveScrollpane.setViewportView(beehiveTable);
		beehiveScrollpane.setPreferredSize(new Dimension(500,100));
		//add(beehiveScrollpane);
		setUpTableDataBeehive.update();

		tablePanel = new JPanel();
		tablePanel.add(beehiveScrollpane);
		tablePanel.add(treeScrollpane);
		add(tablePanel,BorderLayout.SOUTH);
		// this could create the xyblockrenderer stuff
		// CreateXYBlockData blockData = new CreateXYBlockData(world);
		// Thread q = new Thread(blockData);
		// q.start();

		chart = createChart(new CreateXYBlockData(this.world).createDataset(
				this.world.getWidth(), this.world.getHeight(),showPics.get()));
		chartPanel = new ChartPanel(null);
		chartPanel.setChart(chart);
		chartPanel.addMouseListener(this);
		chartPanel.setBackground(Color.black);
		chartPanel.setLayout(null);

		// ChartPanel blockrenderer = new createChart();

		System.out.println(chartPanel.getComponents());
		add(chartPanel,BorderLayout.CENTER);

		// this is for the control stuff
		control = new JPanel();

		// change hunger settings
		JPanel panelSliderHunger = new JPanel();
		final JLabel sliderLabelHunger = new JLabel("Hunger: " + getHunger());
		sliderLabelHunger.setHorizontalTextPosition(SwingConstants.LEFT);
		sliderLabelHunger.setHorizontalAlignment(SwingConstants.RIGHT);
		sliderLabelHunger.setAlignmentY(LEFT_ALIGNMENT);
		sliderHunger = new JSlider(0, 400, getHunger());
		sliderLabelHunger.setLabelFor(sliderHunger);
		sliderHunger.setPaintTicks(true);
		sliderHunger.setMajorTickSpacing(100);
		sliderHunger.setMinorTickSpacing(50);
		sliderHunger.setPaintLabels(true);
		sliderHunger.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				MyGui.this.world.setHunger(((JSlider) e.getSource()).getValue());
				sliderLabelHunger.setText("Hunger: "
						+ (int) (MyGui.this.world.getHunger() * 10000));
			}
		});
		panelSliderHunger.setLayout(new GridLayout(0, 2, 0, 0));
		panelSliderHunger.add(sliderLabelHunger);
		panelSliderHunger.add(sliderHunger);

		// change size of beehives (only the first beehive at the moment)
		JPanel panelSliderBeehiveSize = new JPanel();
		final JLabel sliderLabelBeehiveSize = new JLabel(
				"Max food of Beehive : "
						+ (this.world.getBeehives().getFirst().getSize()));
		sliderLabelBeehiveSize.setHorizontalAlignment(SwingConstants.RIGHT);
		sliderLabelBeehiveSize.setAlignmentX(LEFT_ALIGNMENT);
		sliderBeehiveSize = new JSlider(0, 5000, this.world.getBeehives()
				.getFirst().getSize());
		sliderBeehiveSize.setPaintTicks(true);
		sliderBeehiveSize.setMajorTickSpacing(1000);
		sliderBeehiveSize.setMinorTickSpacing(500);
		sliderBeehiveSize.setPaintLabels(true);
		sliderBeehiveSize.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				MyGui.this.world.getBeehives().getFirst()
				.setSize(((JSlider) e.getSource()).getValue());
				sliderLabelBeehiveSize.setText("Max food of Beehive : "
						+ (MyGui.this.world.getBeehives().getFirst().getSize()));
			}
		});
		panelSliderBeehiveSize.setLayout(new GridLayout(0, 2, 0, 0));
		panelSliderBeehiveSize.add(sliderLabelBeehiveSize);
		panelSliderBeehiveSize.add(sliderBeehiveSize);

		// JSlider for update speed
		JPanel panelSliderUpdateSpeed = new JPanel();
		final JLabel sliderLabelUpdateSpeed = new JLabel("Update Speed :"
				+ (this.world.getUpdateSpeed()));
		sliderLabelUpdateSpeed.setHorizontalAlignment(SwingConstants.RIGHT);
		sliderLabelUpdateSpeed.setAlignmentX(LEFT_ALIGNMENT);
		sliderUpdateSpeed = new JSlider(0, 100, this.world.getUpdateSpeed());
		sliderLabelUpdateSpeed.setLabelFor(sliderUpdateSpeed);
		sliderUpdateSpeed.setPaintTicks(true);
		sliderUpdateSpeed.setMajorTickSpacing(20);
		sliderUpdateSpeed.setMinorTickSpacing(10);
		sliderUpdateSpeed.setPaintLabels(true);
		sliderUpdateSpeed.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				MyGui.this.world.setUpdateSpeed(((JSlider) e.getSource())
						.getValue());
				sliderLabelUpdateSpeed.setText("Update Speed : "
						+ MyGui.this.world.getUpdateSpeed());
			}
		});
		panelSliderUpdateSpeed.setLayout(new GridLayout(0, 2, 0, 0));
		panelSliderUpdateSpeed.add(sliderLabelUpdateSpeed);
		panelSliderUpdateSpeed.add(sliderUpdateSpeed);

		// JSlider for world speed
		JPanel panelSliderWorldSpeed = new JPanel();
		final JLabel sliderLabelWorldSpeed = new JLabel("World Speed: "
				+ (this.world.getWorldSpeed()));
		sliderLabelWorldSpeed.setHorizontalAlignment(SwingConstants.RIGHT);
		sliderLabelWorldSpeed.setAlignmentX(LEFT_ALIGNMENT);
		sliderWorldSpeed = new JSlider(1, 200, this.world.getWorldSpeed());
		sliderWorldSpeed.setPaintTicks(true);
		sliderWorldSpeed.setMajorTickSpacing(50);
		sliderWorldSpeed.setMinorTickSpacing(25);
		sliderWorldSpeed.setPaintLabels(true);
		sliderWorldSpeed.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				MyGui.this.world.setWorldSpeed(((JSlider) e.getSource())
						.getValue());
				sliderLabelWorldSpeed
				.setText(("World Speed: " + (MyGui.this.world
						.getWorldSpeed())));
			}
		});
		panelSliderWorldSpeed.setLayout(new GridLayout(0, 2, 0, 0));
		panelSliderWorldSpeed.add(sliderLabelWorldSpeed);
		panelSliderWorldSpeed.add(sliderWorldSpeed);

		// JSlider for bee count
		JPanel panelSliderBeeCount = new JPanel();
		final JLabel sliderLabelBeeCount = new JLabel("Bee Count: "
				+ (this.world.getBeeCount()));
		sliderLabelBeeCount.setHorizontalAlignment(SwingConstants.RIGHT);
		sliderLabelBeeCount.setAlignmentX(LEFT_ALIGNMENT);
		sliderBeeCount = new JSlider(0, 5000, this.world.getBeeCount());
		sliderBeeCount.setPaintTicks(true);
		sliderBeeCount.setMajorTickSpacing(1000);
		sliderBeeCount.setMinorTickSpacing(500);
		sliderBeeCount.setPaintLabels(true);
		sliderBeeCount.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				MyGui.this.world.setBeeCount(((JSlider) e.getSource())
						.getValue());
				sliderLabelBeeCount.setText(("Bee Count: " + (MyGui.this.world
						.getBeeCount())));
			}
		});
		panelSliderBeeCount.setLayout(new GridLayout(0, 2, 0, 0));
		panelSliderBeeCount.add(sliderLabelBeeCount);
		panelSliderBeeCount.add(sliderBeeCount);

		// JSpinner for setting Trees number
		SpinnerModel tree = new SpinnerNumberModel(
				MyGui.this.world.getTreeCount(), // initial value
				0, // min
				100, // max
				1); // step
		treeSpinner = new JSpinner(tree);
		treeSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSpinner source = (JSpinner) e.getSource();
				MyGui.this.world.setTreeCount((int) source.getValue());
				setUpTableDataTree.update();
				if (showPics.get()) {
					setPics();
				}
			}
		});
		JPanel panelTreeSpinner = new JPanel();
		JLabel labelTreeSpinner = new JLabel("Anzahl der Bäume");

		// add all the sliders and labels
		control.add(panelTreeSpinner);
		panelTreeSpinner.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		panelTreeSpinner.add(labelTreeSpinner);
		panelTreeSpinner.add(treeSpinner);
		control.add(panelSliderHunger);
		control.add(panelSliderBeehiveSize);
		control.add(panelSliderUpdateSpeed);
		control.add(panelSliderWorldSpeed);
		control.add(panelSliderBeeCount);
		control.setLayout(new BoxLayout(control, BoxLayout.PAGE_AXIS));
		add(control, BorderLayout.EAST);


		// This is the button to start the odel
		startBtn = new JButton("Start Model");
		startBtn.setActionCommand("start");
		startBtn.addActionListener(this);

		// This is the button to save the data
		saveGame = new JButton("Save Model");
		saveGame.setActionCommand("save");
		saveGame.addActionListener(this);

		// This is the button to load data
		JButton loadGame = new JButton("Load Model");
		loadGame.setActionCommand("load");
		loadGame.addActionListener(this);

		// This is for stopping all the threads
		stopBtn = new JButton("Kill Threads");
		stopBtn.setActionCommand("kill");
		stopBtn.addActionListener(this);

		// This is the save image button
		JButton saveImgBtn = new JButton("Save Image");
		saveImgBtn.setActionCommand("saveImage");
		saveImgBtn.addActionListener(this);

		// this is for fullscreen mode
		JButton fullscreenBtn = new JButton("Fullscreen");
		fullscreenBtn.setActionCommand("fullscreen");
		fullscreenBtn.addActionListener(this);

		checkBoxShowPics = new JCheckBox("Show Images",showPics.get());
		checkBoxShowPics.addActionListener(this);

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(startBtn);
		buttonPanel.add(stopBtn);
		buttonPanel.add(saveGame);
		buttonPanel.add(loadGame);
		buttonPanel.add(fullscreenBtn);
		buttonPanel.add(saveImgBtn);
		buttonPanel.add(checkBoxShowPics);


		add(buttonPanel,BorderLayout.NORTH);

		validate();
		repaint();




		refresh = new RefreshChart(chart, chartPanel, this.world, showPics);
		Thread ref = new Thread(refresh, "refresh");
		ref.start();


		//		when windows size is changed, the plot size may change as well -> so replace images 
		frame.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				if (showPics.get()) {
					setPics();
				}
			}
		});

	}



	/**
	 * Creates the chart for the visualization.
	 * 
	 * @param dataset
	 *            the dataset
	 * 
	 * @return the chart
	 */
	private JFreeChart createChart(XYZDataset dataset) {
		NumberAxis xAxis = new NumberAxis("X");
		xAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		xAxis.setLowerMargin(0.0);
		xAxis.setUpperMargin(0.0);
		xAxis.setRange(0, world.getWidth());
		NumberAxis yAxis = new NumberAxis("Y");
		yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		yAxis.setLowerMargin(0.0);
		yAxis.setUpperMargin(0.0);
		yAxis.setRange(0, world.getHeight());
		XYBlockRenderer renderer = new XYBlockRenderer();
		// LookupPaintScale scale = new LookupPaintScale(0, 500, Color.gray);
		// PaintScale scale = new GrayPaintScale(-10000, 100);
		LookupPaintScale scale = new LookupPaintScale(0.5D, 4.5D, Color.blue);
		scale.add(-10000.0, Color.blue);
		scale.add(0.0, Color.red);
		scale.add(100.0, Color.green);

		renderer.setPaintScale(scale);
		XYPlot plot = new XYPlot(dataset, xAxis, yAxis, renderer);
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinesVisible(false);
		// plot.setRangeGridlinePaint(Color.white);
		JFreeChart chart = new JFreeChart("XYBlockChartDemo1", plot);
		chart.removeLegend();
		chart.setBackgroundPaint(Color.white);
		return chart;
	}

	/**
	 * Creates or sets the actual tablemodel for the table.
	 * 
	 * @param colName
	 *            strings[] of the column names
	 * @param jTable
	 *            a jTable
	 * @return the jTable
	 */
	private JTable getJTable(String[] colName, JTable jTable) {

		if (jTable == null) {
			jTable = new JTable() {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isCellEditable(int nRow, int nCol) {
					return false;
				}
			};
		}
		DefaultTableModel contactTableModel = (DefaultTableModel) jTable
				.getModel();
		contactTableModel.setColumnIdentifiers(colName);
		jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		return jTable;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == checkBoxShowPics)  {
			JCheckBox cb = (JCheckBox) e.getSource();
			showPics.set(cb.isSelected());
			if (cb.isSelected()) {
				setPics();
				synchronized (this.chartPanel) {
					//this.chartPanel.setChart(null);
					this.chartPanel.validate();
				}	
			}	else {
				this.chartPanel.removeAll();
			}


			
		}else {

			// TODO Auto-generated method stub
			boolean startagain = false;
			JButton source = (JButton) e.getSource();
			switch (source.getActionCommand()) {
			case "start":
				if (!this.world.isStartModel()) {
					this.world.startModel();
					source.setText("Pause Model");
					source.setActionCommand("pause");
				}
				break;

			case "pause":
				if (this.world.isStartModel()) {
					this.world.stopModel();
					source.setText("Start Model");
					source.setActionCommand("start");
				}
				break;

			case "save":
				// Dialog zum Speichern von Dateien anzeigen
				if (this.world.isStartModel()) {
					this.world.stopModel();
					startagain = true;
				}
				JFileChooser chooser = new JFileChooser();
				chooser.setSelectedFile(new File("data.dat"));
				int buttonPressed = chooser.showSaveDialog(this);
				if (buttonPressed == JFileChooser.APPROVE_OPTION) {

					try {
						OutputStream os = new FileOutputStream(
								chooser.getSelectedFile());
						ObjectOutputStream oos = new ObjectOutputStream(os);
						oos.writeObject(world);
						oos.close();
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(null, e1.getMessage(),
								"Error", JOptionPane.ERROR_MESSAGE);
					}
					if (startagain) {
						this.world.startModel();
						startagain = false;
					}
				}
				break;

			case "load":
				if (this.world.isStartModel()) {
					this.world.stopModel();
				}
				JFileChooser chooser2 = new JFileChooser();
				chooser2.setSelectedFile(new File("data.dat"));
				int buttonPressed2 = chooser2.showOpenDialog(this);
				if (buttonPressed2 == JFileChooser.APPROVE_OPTION) {
					this.world.stopModel();
					boolean failure = false;
					startBtn.setText("Start Model");
					startBtn.setActionCommand("start");
					World world_new = null;
					try {
						InputStream is = new FileInputStream(
								chooser2.getSelectedFile());
						ObjectInputStream ois = new ObjectInputStream(is);
						world_new = (World) ois.readObject();
						ois.close();
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(null, e1.getMessage(),
								"Error", JOptionPane.ERROR_MESSAGE);
						failure = true;
					}

					if (!failure) {
						MyGui.this.world.setTreeCount(0);
						MyGui.this.world.setBeeCount(0);
						setUpTableDataTree.update();
						if (MyGui.this.world.getBeehives().size() != 0) {
							MyGui.this.world.getBeehives().getFirst()
							.setAlive(false);
						}
						setUpTableDataBeehive.update();
						world = world_new;
						// change beehive table to actual one
						String[] BeehiveColName1 = { "Name", "Size", "X", "Y",
						"Waiting Bees" };
						JTable beehiveTable = getJTable(BeehiveColName1,
								jTableBeehives);
						setUpTableDataBeehive = new SetUpTableData(beehiveTable,
								world, "Beehives");
						beehiveScrollpane.setViewportView(beehiveTable);
						setUpTableDataBeehive.update();

						// this is the Tree table
						String[] treeColName = { "Name", "Size", "Max Size",
								"Quality", "Refresh", "Type", "X", "Y" };
						JTable treeTable = getJTable(treeColName, jTableTrees);
						setUpTableDataTree = new SetUpTableData(treeTable, world,
								"Trees");
						// JScrollPane treeScrollpane = new JScrollPane();
						treeScrollpane.setViewportView(treeTable);
						// add(treeScrollpane);

						chart = createChart(new CreateXYBlockData(this.world)
						.createDataset(this.world.getWidth(),
								this.world.getHeight(), showPics.get()));
						chartPanel.setChart(chart);
						refresh.setChart(world, chart);

						this.world.startBeehives();
						this.world.startSources();
						this.world.startBees();
						setUpTableDataTree.update();
						sliderBeeCount.setEnabled(true);
						sliderBeeCount.setValue(this.world.getBeeCount());
						sliderBeehiveSize.setEnabled(true);
						sliderBeehiveSize.setValue(this.world.getBeehives()
								.getFirst().getSize());
						sliderHunger.setEnabled(true);
						sliderHunger.setValue(getHunger());
						sliderUpdateSpeed.setEnabled(true);
						sliderUpdateSpeed.setValue(this.world.getUpdateSpeed());
						sliderWorldSpeed.setEnabled(true);
						sliderWorldSpeed.setValue(this.world.getWorldSpeed());
						treeSpinner.setEnabled(true);
						treeSpinner.setValue(this.world.getTreeCount());
						stopBtn.setText("Kill Model");
						stopBtn.setActionCommand("kill");
						startBtn.setEnabled(true);
					}
				}
				break;

			case "kill":
				this.world.stopModel();

				MyGui.this.world.setTreeCount(0);
				MyGui.this.world.setBeeCount(0);
				setUpTableDataTree.update();

				if (MyGui.this.world.getBeehives() != null) {
					MyGui.this.world.getBeehives().getFirst().setAlive(false);
				}
				setUpTableDataBeehive.update();

				sliderBeeCount.setEnabled(false);
				sliderBeehiveSize.setEnabled(false);
				sliderHunger.setEnabled(false);
				sliderUpdateSpeed.setEnabled(false);
				sliderWorldSpeed.setEnabled(false);

				treeSpinner.setEnabled(false);

				startBtn.setEnabled(false);
				startBtn.setText("Start Model");
				startBtn.setActionCommand("start");
				saveGame.setEnabled(false);

				source.setText("Init Model");
				source.setActionCommand("init");
				break;

			case "init":
				this.world.createBeehive(1);
				this.world.setNumOfBeehives(1);
				this.world.createSources((int) treeSpinner.getValue());
				this.world.createBees(sliderBeeCount.getValue());
				setUpTableDataBeehive.update();
				setUpTableDataTree.update();

				this.world.startBeehives();
				this.world.startSources();
				this.world.startBees();

				sliderBeeCount.setEnabled(true);
				sliderBeehiveSize.setEnabled(true);
				sliderHunger.setEnabled(true);
				sliderUpdateSpeed.setEnabled(true);
				sliderWorldSpeed.setEnabled(true);

				treeSpinner.setEnabled(true);

				startBtn.setEnabled(true);
				saveGame.setEnabled(true);

				source.setText("Kill Model");
				source.setActionCommand("kill");
				break;

			case "fullscreen": 
				remove(control);
				remove(tablePanel);
				validate();
				source.setText("Show Controls");
				source.setActionCommand("showControls");
				setPics();
				break;

			case "saveImage":
				if (this.world.isStartModel()) {
					this.world.stopModel();
					startagain = false;
				}
				JFileChooser chooser3 = new JFileChooser();
				chooser3.setSelectedFile(new File("data.png"));
				int buttonPressed3 = chooser3.showSaveDialog(this);
				if (buttonPressed3 == JFileChooser.APPROVE_OPTION) {

					try {
						ChartUtilities.writeChartAsPNG(new FileOutputStream(chooser3.getSelectedFile()), chartPanel.getChart(), this.world.getWidth(), this.world.getHeight());
					} catch (IOException e1) {
						System.out.println("Error " + e1);
						e1.printStackTrace();
					}

				}
				if (startagain) {
					this.world.startModel();
					startagain = false;

				}
				break;

			case "showControls":
				add(control, BorderLayout.EAST);
				add(tablePanel, BorderLayout.SOUTH);
				validate();
				source.setText("Fullscreen");
				source.setActionCommand("fullscreen");
				setPics();
				break;
			}
		}
	}

	private void setPics() {
		//		this sets the pics on the chartpanel
		if (showPics.get()) {
			this.chartPanel.removeAll();
			Dimension size = this.chartPanel.getSize();
			BufferedImage picBeehive =null;
			BufferedImage picTree = null;
			//		load the pics for beehive and tree
			try {
				picBeehive = ImageIO.read(this.getClass().getResource("beehive.png"));
				picTree = ImageIO.read(this.getClass().getResource("tree.png"));

			} catch (IOException e) {
				e.printStackTrace();
			}
			JLabel icon= new JLabel(new ImageIcon(picBeehive));
			this.chartPanel.add(icon);


			Dimension sizeIcon = icon.getPreferredSize();
			double factorX = size.width / (double)world.getWidth();
			double factorY = (size.height / (double)world.getHeight());

			int xStart = (int)(world.getBeehives().getFirst().getPositionX() * factorX);
			int yStart = size.height - (int)(world.getBeehives().getFirst().getPositionY() * factorY);
			xStart = xStart - (sizeIcon.width /2);
			yStart = yStart - (sizeIcon.height / 2);
			int xEnd = xStart + sizeIcon.width;
			int yEnd = yStart + sizeIcon.height;
			icon.setBounds(xStart, yStart, sizeIcon.width, sizeIcon.height);

			LinkedList<JLabel> treePics = new LinkedList<>();

			for (Tree t: world.trees) {
				JLabel icon1 = (new JLabel(new ImageIcon(picTree)));
				treePics.add(icon1);
				this.chartPanel.add(icon1);
				sizeIcon = icon1.getPreferredSize();

				xStart = (int) (t.x * factorX);
				xStart = xStart - (sizeIcon.width / 2);
				yStart = (int) (t.y * factorY);
				yStart = size.height - yStart - (sizeIcon.height / 2);
				icon1.setBounds(xStart, yStart,sizeIcon.width,sizeIcon.height);
			}
		}

	}

	private int getHunger() {
		return (int) (this.world.getHunger() * 10000);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		System.out.println(e.getButton());
		System.out.println(e.getClickCount());
		System.out.println(e.getSource());
		System.out.println(e.getX());
		System.out.println(e.getXOnScreen());
		System.out.println(e.getPoint());
		ChartPanel me = (ChartPanel) e.getSource();
		System.out.println(me.getChart());
		System.out.println(me.getEntityForPoint(e.getX(), e.getY()));
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
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

}
