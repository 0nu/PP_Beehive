package hive;

import java.awt.Frame;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;

import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.DomainOrder;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.general.DatasetGroup;
import org.jfree.data.xy.XYZDataset;
import org.jfree.chart.renderer.GrayPaintScale;
import org.jfree.chart.renderer.LookupPaintScale;
import org.jfree.chart.renderer.PaintScale;
import org.jfree.chart.renderer.xy.XYBlockRenderer;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
/**
 * This builds the gui.
 * @author ole
 *
 */
public class MyGui extends JPanel {
	private static final long serialVersionUID = 1L;
	World world;
	private JTable jTableTrees;
	private JTable jTableBeehives;
	private JFrame frame;
	private CreateXYBlockData blockData;
	private JLabel sliderLabel;

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
		this.frame = frame;

		/*
		 * ÜVector<Vector<String>> rowData = new Vector<Vector<String>>(50, 5);
		 * 
		 * Vector<String> columnNames = new Vector<String>(50, 5); for (int x =
		 * 0; x < this.world.trees.size(); x++) { Vector<String> rowData1 = new
		 * Vector<String>(50, 5);
		 * rowData1.add(this.world.trees.get(x).getName());
		 * rowData1.add(Integer.toString(this.world.trees.get(x).size));
		 * rowData1.add(Integer.toString(this.world.trees.get(x).maxsize));
		 * rowData1.add(Integer.toString(this.world.trees.get(x).quality));
		 * rowData1.add(this.world.trees.get(x).type); rowData.add(rowData1); }
		 * 
		 * columnNames.add("Name"); columnNames.add("Size");
		 * columnNames.add("Max Size"); columnNames.add("Quality");
		 * columnNames.add("Type"); JTable table = new JTable(rowData,
		 * columnNames);
		 */

		// this is the Tree table
		jTableTrees = null;
		String[] treeColName = { "Name", "Size", "Max Size", "Quality",
				"Refresh", "Type", "X", "Y" };
		JTable treeTable = getJTable(treeColName, jTableTrees);
		SetUpTableData setUpTableDataTree = new SetUpTableData(treeTable,
				world, "Trees");
		Thread t = new Thread(setUpTableDataTree);
		t.start();
		JScrollPane treeScrollpane = new JScrollPane(treeTable);
		add(treeScrollpane);

		// this is the beehive table
		String[] BeehiveColName1 = { "Name", "Size", "X", "Y", "Waiting Bees" };
		JTable beehiveTable = getJTable(BeehiveColName1, jTableBeehives);
		SetUpTableData setUpTableDataBeehive = new SetUpTableData(beehiveTable,
				world, "Beehives");
		Thread p = new Thread(setUpTableDataBeehive);
		p.start();
		JScrollPane beehiveScrollpane = new JScrollPane(beehiveTable);
		add(beehiveScrollpane);

		// this could create the xyblockrenderer stuff
		// CreateXYBlockData blockData = new CreateXYBlockData(world);
		// Thread q = new Thread(blockData);
		// q.start();

		JFreeChart chart = createChart(new CreateXYBlockData(world)
		.createDataset());
		ChartPanel chartPanel = new ChartPanel(chart);

		// ChartPanel blockrenderer = new createChart();
		add(chartPanel);


		// this is for the control stuff
		JPanel control = new JPanel();

		// change hunger settings
		final JLabel sliderLabelHunger = new JLabel("Hunger: " + (int) (this.world.getHunger() * 10000));
		JSlider sliderHunger = new JSlider (0,100,(int) (this.world.getHunger() * 1000));
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

		
		// change size of beehives (only the first beehive at the moment)
		final JLabel sliderLabelBeehiveSize = new JLabel("Max food of Beehive : " + (this.world.Beehives.getFirst().getSize()));
		JSlider sliderBeehiveSize = new JSlider(0,5000, this.world.Beehives.getFirst().getSize());
		sliderBeehiveSize.setPaintTicks(true);
		sliderBeehiveSize.setMajorTickSpacing( 1000 );
		sliderBeehiveSize.setMinorTickSpacing( 500 );
		sliderBeehiveSize.setPaintLabels(true);
		sliderBeehiveSize.addChangeListener( new ChangeListener() 
		{
			public void stateChanged( ChangeEvent e ) {
				MyGui.this.world.Beehives.getFirst().setSize( ((JSlider)e.getSource()).getValue() );
				sliderLabelBeehiveSize.setText("Max food of Beehive : " + (MyGui.this.world.Beehives.getFirst().getSize()));
			}
		} 				)				;
		
		
		
		// add all the sliders and labels
		control.add(sliderLabelHunger);
		control.add(sliderHunger);
		control.add(sliderLabelBeehiveSize);
		control.add(sliderBeehiveSize);

		add(control);

		setLayout(new GridLayout(2, 2));
		validate();
		repaint();

		RefreshChart refresh = new RefreshChart(chart, chartPanel, world, frame);
		Thread ref = new Thread(refresh);
		ref.start();

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
		xAxis.setRange(0, world.width);
		NumberAxis yAxis = new NumberAxis("Y");
		yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		yAxis.setLowerMargin(0.0);
		yAxis.setUpperMargin(0.0);
		yAxis.setRange(0, world.height);
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

}
