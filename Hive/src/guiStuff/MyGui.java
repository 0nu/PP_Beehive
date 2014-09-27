package guiStuff;

import hive.World;

import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import javax.imageio.stream.FileImageInputStream;
import javax.swing.AbstractButton;
import javax.swing.JButton;
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
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.LookupPaintScale;
import org.jfree.chart.renderer.xy.XYBlockRenderer;
import org.jfree.data.xy.XYZDataset;
/**
 * This builds the gui.
 * @author ole
 *
 */
public class MyGui extends JPanel implements ActionListener{
	private static final long serialVersionUID = 1L;
	World world;
	private JTable jTableTrees;
	private JTable jTableBeehives;
	SetUpTableData setUpTableDataTree;
	SetUpTableData setUpTableDataBeehive;
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
	/**
	 * Constructor. At the moment it creates 3 panels: 
	 * - Table for sources
	 * - Table for beehives
	 * - Graphic panel for objects in the world.
	 * @param world the world
	 * @param frame the frame to draw into
	 */
	public MyGui(World world, JFrame frame) {
		this.world = world;
		// this is the Tree table
		String[] treeColName = { "Name", "Size", "Max Size", "Quality",
				"Refresh", "Type", "X", "Y" };
		JTable treeTable = getJTable(treeColName, jTableTrees);
		setUpTableDataTree = new SetUpTableData(treeTable,
				world, "Trees");
		treeScrollpane = new JScrollPane();
		treeScrollpane.getViewport().add(treeTable);
		add(treeScrollpane);
		setUpTableDataTree.update();

		// this is the beehive table
		String[] BeehiveColName1 = { "Name", "Size", "X", "Y", "Waiting Bees" };
		JTable beehiveTable = getJTable(BeehiveColName1, jTableBeehives);
		setUpTableDataBeehive = new SetUpTableData(beehiveTable,
				world, "Beehives");
		beehiveScrollpane = new JScrollPane();
		beehiveScrollpane.getViewport().add(beehiveTable);
		add(beehiveScrollpane);
		setUpTableDataBeehive.update();

		// this could create the xyblockrenderer stuff
		// CreateXYBlockData blockData = new CreateXYBlockData(world);
		// Thread q = new Thread(blockData);
		// q.start();

		chart = createChart(new CreateXYBlockData(this.world)
		.createDataset(this.world.getWidth(), this.world.getHeight()));
		chartPanel = new ChartPanel(null);
		chartPanel.setChart(chart);

		// ChartPanel blockrenderer = new createChart();
		add(chartPanel);


		// this is for the control stuff
		JPanel control = new JPanel();

		// change hunger settings
		JPanel panelSliderHunger = new JPanel();
		final JLabel sliderLabelHunger = new JLabel("Hunger: " + getHunger());
		sliderLabelHunger.setAlignmentY(LEFT_ALIGNMENT);
		sliderHunger = new JSlider (0,200, getHunger());
		sliderHunger.setPaintTicks(true);
		sliderHunger.setMajorTickSpacing( 25 );
		sliderHunger.setMinorTickSpacing( 5 );
		sliderHunger.setPaintLabels(true);
		sliderHunger.addChangeListener( new ChangeListener() 
		{
			public void stateChanged( ChangeEvent e ) {
				MyGui.this.world.setHunger( ((JSlider)e.getSource()).getValue() );
				sliderLabelHunger.setText("Hunger: " + (int) (MyGui.this.world.getHunger() * 10000));
			}
		} 				)				;
		panelSliderHunger.add(sliderLabelHunger);
		panelSliderHunger.add(sliderHunger);

		// change size of beehives (only the first beehive at the moment)
		JPanel panelSliderBeehiveSize = new JPanel();
		final JLabel sliderLabelBeehiveSize = new JLabel("Max food of Beehive : " + (this.world.getBeehives().getFirst().getSize()));
		sliderLabelBeehiveSize.setAlignmentX(LEFT_ALIGNMENT);
		sliderBeehiveSize = new JSlider(0,5000, this.world.getBeehives().getFirst().getSize());
		sliderBeehiveSize.setPaintTicks(true);
		sliderBeehiveSize.setMajorTickSpacing( 1000 );
		sliderBeehiveSize.setMinorTickSpacing( 500 );
		sliderBeehiveSize.setPaintLabels(true);
		sliderBeehiveSize.addChangeListener( new ChangeListener() 
		{
			public void stateChanged( ChangeEvent e ) {
				MyGui.this.world.getBeehives().getFirst().setSize( ((JSlider)e.getSource()).getValue() );
				sliderLabelBeehiveSize.setText("Max food of Beehive : " + (MyGui.this.world.getBeehives().getFirst().getSize()));
			}
		} 				)				;
		panelSliderBeehiveSize.add(sliderLabelBeehiveSize);
		panelSliderBeehiveSize.add(sliderBeehiveSize);

		//JSlider for update speed
		JPanel panelSliderUpdateSpeed = new JPanel();
		final JLabel sliderLabelUpdateSpeed = new JLabel("Update Speed :" + (this.world.getUpdateSpeed()));
		sliderLabelUpdateSpeed.setAlignmentX(LEFT_ALIGNMENT);
		sliderUpdateSpeed = new JSlider(0,100,this.world.getUpdateSpeed());
		sliderUpdateSpeed.setPaintTicks(true);
		sliderUpdateSpeed.setMajorTickSpacing( 20 );
		sliderUpdateSpeed.setMinorTickSpacing( 10 );
		sliderUpdateSpeed.setPaintLabels(true);
		sliderUpdateSpeed.addChangeListener( new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				MyGui.this.world.setUpdateSpeed(((JSlider)e.getSource()).getValue());
				sliderLabelUpdateSpeed.setText("Update Speed : " + MyGui.this.world.getUpdateSpeed());
			}
		});
		panelSliderUpdateSpeed.add(sliderLabelUpdateSpeed);
		panelSliderUpdateSpeed.add(sliderUpdateSpeed);

		//JSlider for world speed
		JPanel panelSliderWorldSpeed = new JPanel();
		final JLabel sliderLabelWorldSpeed = new JLabel("World Speed: " + (this.world.getWorldSpeed()));
		sliderLabelWorldSpeed.setAlignmentX(LEFT_ALIGNMENT);
		sliderWorldSpeed = new JSlider(1,200,this.world.getWorldSpeed());
		sliderWorldSpeed.setPaintTicks(true);
		sliderWorldSpeed.setMajorTickSpacing( 50 );
		sliderWorldSpeed.setMinorTickSpacing( 25 );
		sliderWorldSpeed.setPaintLabels(true);
		sliderWorldSpeed.addChangeListener( new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				MyGui.this.world.setWorldSpeed(((JSlider)e.getSource()).getValue());
				sliderLabelWorldSpeed.setText(("World Speed: " + (MyGui.this.world.getWorldSpeed())));
			}
		});
		panelSliderWorldSpeed.add(sliderLabelWorldSpeed);
		panelSliderWorldSpeed.add(sliderWorldSpeed);

		//JSlider for bee count
		JPanel panelSliderBeeCount = new JPanel();
		final JLabel sliderLabelBeeCount = new JLabel("Bee Count: " + (this.world.getBeeCount()));
		sliderLabelBeeCount.setAlignmentX(LEFT_ALIGNMENT);
		sliderBeeCount= new JSlider(0,5000,this.world.getBeeCount());
		sliderBeeCount.setPaintTicks(true);
		sliderBeeCount.setMajorTickSpacing( 1000 );
		sliderBeeCount.setMinorTickSpacing( 500 );
		sliderBeeCount.setPaintLabels(true);
		sliderBeeCount.addChangeListener( new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				MyGui.this.world.setBeeCount(((JSlider)e.getSource()).getValue());
				sliderLabelBeeCount.setText(("Bee Count: " + (MyGui.this.world.getBeeCount())));
			}
		});
		panelSliderBeeCount.add(sliderLabelBeeCount);
		panelSliderBeeCount.add(sliderBeeCount);



		// JSpinner for setting Trees number
		SpinnerModel trees = new SpinnerNumberModel(MyGui.this.world.getTreeCount(), //initial value
				0, //min
				100, //max
				1);   //step
		treeSpinner = addLabeledSpinner(control,
				"Anzahl der Baeume",
				trees);
		treeSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSpinner source = (JSpinner) e.getSource();
				MyGui.this.world.setTreeCount((int) source.getValue());
				setUpTableDataTree.update();
			}
		});

		// This is the button to start the odel
		startBtn = new JButton("Start Model");
		startBtn.setActionCommand("start");
		startBtn.addActionListener(this);

		//This is the button to save the data
		saveGame = new JButton("Save Model");
		saveGame.setActionCommand("save");
		saveGame.addActionListener(this);

		//This is the button to load data
		JButton loadGame = new JButton("Load Model");
		loadGame.setActionCommand("load");
		loadGame.addActionListener(this);

		//This is for stopping all the threads
		stopBtn = new JButton("Kill Threads");
		stopBtn.setActionCommand("kill");
		stopBtn.addActionListener(this);






		// add all the sliders and labels
		control.add(panelSliderHunger);
		control.add(panelSliderBeehiveSize);
		control.add(panelSliderUpdateSpeed);
		control.add(panelSliderWorldSpeed);
		control.add(panelSliderBeeCount);
		control.add(startBtn);
		control.add(stopBtn);
		control.add(saveGame);
		control.add(loadGame);



		add(control);

		setLayout(new GridLayout(2, 2));
		validate();
		repaint();

		refresh = new RefreshChart(chart, chartPanel, this.world, frame);
		Thread ref = new Thread(refresh,"refresh");
		ref.start();

	}


	static protected JSpinner addLabeledSpinner(Container c,
			String label,
			SpinnerModel model) {
		JLabel l = new JLabel(label);
		c.add(l);

		JSpinner spinner = new JSpinner(model);
		l.setLabelFor(spinner);
		c.add(spinner);

		return spinner;
	}

	/**
	 * Creates the chart for the visualization.
	 * 
	 * @param dataset
	 *            the dataset
	 * 
	 * @return the chart
	 */
	JFreeChart createChart(XYZDataset dataset) {
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
		//LookupPaintScale scale = new LookupPaintScale(0, 500,     Color.gray);
		//PaintScale scale = new GrayPaintScale(-10000, 100);
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
	 * not used anymore, is it?
	 * @param world the world it's all about
	 */
	public void createAndShowGUI(World world) {
		// Create and set up the window.
		// JFrame frame = new JFrame("MyGui");
		// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Create and set up the content pane.

		// MyGui newContentPane = new MyGui(world,frame);
		// newContentPane.setOpaque(true); // content panes must be opaque
		// frame.setContentPane(newContentPane);
		//
		// // Display the window.
		// frame.pack();
		// frame.setVisible(true);
	}

	/**
	 * Creates or sets the actual tablemodel for the table.
	 * @param colName strings[] of the column names
	 * @param jTable a jTable
	 * @return the jTable
	 */
	private JTable getJTable(String[] colName, JTable jTable) {

		if (jTable == null) {
			jTable = new JTable() {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

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
		// TODO Auto-generated method stub
		JButton source = (JButton) e.getSource(); 
		switch (source.getActionCommand()) {
		case "start" : 
			this.world.startModel();
			source.setText("Pause Model");
			source.setActionCommand("pause");
			break;

		case "pause" :
			this.world.stopModel();
			source.setText("Start Model");
			source.setActionCommand("start");
			break;

		case "save":

			// Dialog zum Speichern von Dateien anzeigen
			this.world.stopModel();
			JFileChooser chooser = new JFileChooser();
			chooser.setSelectedFile(new File("data.dat"));
			int buttonPressed = chooser.showSaveDialog(this);
			if (buttonPressed == JFileChooser.APPROVE_OPTION) {

				try { 
					OutputStream os = new FileOutputStream(chooser.getSelectedFile());
					ObjectOutputStream oos = new ObjectOutputStream(os);
					oos.writeObject(world);
					oos.close();
				}	catch (IOException e1) {
					System.out.println("Error " + e1);
					e1.printStackTrace();
				}
				this.world.startModel();
			}
			break;

		case "load":
			this.world.stopModel();
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
					InputStream is = new FileInputStream(chooser2.getSelectedFile());
					ObjectInputStream ois = new ObjectInputStream(is);
					world_new = (World) ois.readObject();
					ois.close();
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					failure = true;
				}

				if (!failure) {
					MyGui.this.world.setTreeCount(0);
					MyGui.this.world.setBeeCount(0);
					setUpTableDataTree.update();
					if (MyGui.this.world.getBeehives().size() != 0) {
						MyGui.this.world.getBeehives().getFirst().setAlive(false);
						}			
					setUpTableDataBeehive.update();
					world = world_new;
					//				change beehive table to actual one
					String[] BeehiveColName1 = { "Name", "Size", "X", "Y", "Waiting Bees" };
					JTable beehiveTable = getJTable(BeehiveColName1, jTableBeehives);
					setUpTableDataBeehive = new SetUpTableData(beehiveTable,
							world, "Beehives");
					beehiveScrollpane.setViewportView(beehiveTable);
					setUpTableDataBeehive.update();

					// this is the Tree table
					String[] treeColName = { "Name", "Size", "Max Size", "Quality",
							"Refresh", "Type", "X", "Y" };
					JTable treeTable = getJTable(treeColName, jTableTrees);
					setUpTableDataTree = new SetUpTableData(treeTable,
							world, "Trees");
					//JScrollPane treeScrollpane = new JScrollPane();
					treeScrollpane.setViewportView(treeTable);
					//add(treeScrollpane);


					chart = createChart(new CreateXYBlockData(this.world)
					.createDataset(this.world.getWidth(), this.world.getHeight()));
					chartPanel.setChart(chart);
					refresh.setChart(world, chart);

					this.world.startBeehives();
					this.world.startSources();
					this.world.startBees();
					setUpTableDataTree.update();
					sliderBeeCount.setEnabled(true);
					sliderBeeCount.setValue(this.world.getBeeCount());
					sliderBeehiveSize.setEnabled(true);
					sliderBeehiveSize.setValue(this.world.getBeehives().getFirst().getSize());
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

		}



	}


	private int getHunger() {
		// TODO Auto-generated method stub
		return (int) (this.world.getHunger() * 10000);
	}

}
