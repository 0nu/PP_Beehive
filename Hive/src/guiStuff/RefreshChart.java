package guiStuff;

import hive.World;

import java.awt.Color;

import javax.swing.JFrame;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.LookupPaintScale;
import org.jfree.chart.renderer.xy.XYBlockRenderer;
import org.jfree.data.xy.XYZDataset;

/**
 * This refreshes the xyblockrenderer Chart from time to time.
 * 
 * @author ole
 * 
 */
public class RefreshChart implements Runnable {

	private JFreeChart chart;
	private World world;
	public ChartPanel chartPanel;
	public RefreshChart(JFreeChart chart, ChartPanel chartPanel, World world,
			JFrame frame) {
		this.chart = chart;
		this.world = world;
		this.chartPanel = chartPanel;
	}

	/**
	 * Run method. This is not coeded in a sophisticated way. Need to adjust
	 * refresh rate - or code it in a better way.
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub

        LookupPaintScale scale = new LookupPaintScale(-10000, 10000, Color.white);
        
        // these are the gradient colors for the beehive and the trees
        // green -> full, red -> empty
        int red = 0;
        int green = 255;
        int blue = 0;
        double scaling = 10000 / 255;
        double gradient = -10000;
        
        for (green = 255; green >= 0; green--) {
        	scale.add(gradient = gradient + scaling, new Color(red, green, blue));
        	red++;
        }
        
        scale.add(0.0, Color.red);
        scale.add(1.0, Color.orange);
        scale.add(2.0, Color.yellow);
        scale.add(3.0, Color.red);
        
        // and then we need colors for the bees
        // yellow -> 1 bee
        // white -> lots of bees
        red = 255;
        green = 255;
        blue = 0;
        scaling = 10000 / 255;
        gradient = 1;
        
        for (blue = 0; blue <= 255; blue++) {
        	scale.add(gradient = gradient + scaling, new Color(red, green, blue));
        	        }
        
        
		while (true) {
			/*
			 * try { Thread.sleep(100); // 1000 milliseconds is one second. }
			 * catch (InterruptedException ex) {
			 * Thread.currentThread().interrupt(); }
			 */
			
			this.chart = createChart(new CreateXYBlockData(world).getDataset(), scale);
			this.chart.fireChartChanged();
			new ChartPanel(chart);
			// this.chartPanel = chartPanel;
			this.chartPanel.setChart(this.chart);
			this.chartPanel.updateUI();
			// this.frame.setContentPane(this.chartPanel);

		}
	}

	/**
	 * Creates a new chart. Is run to set the new dataset to the xyblockrenderer object.
	 * @param dataset the dataset that was created just before
	 * @return the JFreeChart
	 */
	JFreeChart createChart(XYZDataset dataset, LookupPaintScale scale) {
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
}
