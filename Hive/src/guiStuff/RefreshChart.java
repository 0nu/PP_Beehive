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
	 * Run method. This is not coded in a sophisticated way. Need to adjust
	 * refresh rate - or code it in a better way.
	 */
	@Override
	public void run() {
		// set up the paintscale for the xyblockrenderer

		LookupPaintScale scale = new LookupPaintScale(-200, 50000, Color.orange);

		// these are the gradient colors for the beehive and the trees
		// green -> high quality, red -> low quality
		// alpha 1 -> full, alpha 0 -> empty
		int red = 0;
		int green = 255;
		int blue = 0;
		double gradient = 0;
		red = 255;
		green = 0;
		blue = 0;
		int factor;
		int j;
		for (int i = 0; i < 10; i++) {
			gradient = 0;
			factor = i * 20;
			gradient = gradient - factor;
			for (j = -1; j >= -20; j--) {
				scale.add(j - (i * 20), new Color(red, green, blue, 12 * (-1)
						* j + 15));
			}
			red = red - 24;
			green = green + 24;

		}

		// some colors for the bees
		scale.add(0.9, Color.red);
		scale.add(1, new Color(255, 255, 0, 0));
		scale.add(2, new Color(255, 255, 0, 0));
		scale.add(3, new Color(255, 255, 0, 0));
		scale.add(5, new Color(255, 255, 0, 0));
		scale.add(9, new Color(255, 255, 0, 10));

		// and then we need more colors for many bees on one place
		// yellow -> 1 bee
		// white -> lots of bees
		red = 255;
		green = 255;
		blue = 0;
		double scaling = 3000 / 255;
		gradient = 10 - scaling;
		int alpha = 25;

		for (blue = 0; blue <= 255; blue++) {
			scale.add(gradient = gradient + scaling, new Color(red, green,
					blue, alpha));
			if (alpha < 255) {
				alpha++;
			}
		}

		// and now start the loop for updating the table
		int height = this.world.getHeight();
		int width = this.world.getWidth();
		while (true) {
			if (!this.world.isStartModel()) {
				try {
					Thread.sleep(500); // 1000 milliseconds is one second. }
				} catch (InterruptedException ex) {
					Thread.currentThread().interrupt();
				}
			}
			this.chart = createChart(
					new CreateXYBlockData(world).getDataset(width, height),
					scale);
			this.chartPanel.setChart(this.chart);
		}
	}

	/**
	 * Creates a new chart. Is run to set the new dataset to the xyblockrenderer
	 * object.
	 * 
	 * @param dataset
	 *            the dataset that was created just before
	 * @return the JFreeChart
	 */
	JFreeChart createChart(XYZDataset dataset, LookupPaintScale scale) {
		NumberAxis xAxis = new NumberAxis();
		xAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		xAxis.setLowerMargin(0.0);
		xAxis.setUpperMargin(0.0);
		xAxis.setRange(0, world.getWidth());
		xAxis.setTickLabelsVisible(false);
		xAxis.setTickMarksVisible(false);
		NumberAxis yAxis = new NumberAxis();
		yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		yAxis.setLowerMargin(0.0);
		yAxis.setUpperMargin(0.0);
		yAxis.setRange(0, world.getHeight());
		yAxis.setTickLabelsVisible(false);
		yAxis.setTickMarksVisible(false);
		XYBlockRenderer renderer = new XYBlockRenderer();
		// LookupPaintScale scale = new LookupPaintScale(0, 500, Color.gray);
		// PaintScale scale = new GrayPaintScale(-10000, 100);
		renderer.setPaintScale(scale);
		XYPlot plot = new XYPlot(dataset, xAxis, yAxis, renderer);
		plot.setBackgroundPaint(Color.black);
		plot.setDomainGridlinesVisible(false);
		plot.setRangeGridlinesVisible(false);
		plot.setDomainMinorGridlinesVisible(false);
		plot.setRangeMinorGridlinesVisible(false);
		// plot.setRangeGridlinePaint(Color.white);
		JFreeChart chart = new JFreeChart(plot);
		chart.removeLegend();
		chart.setBackgroundPaint(Color.white);
		return chart;

	}

	/**
	 * @param world
	 *            the world to which the chart belongs
	 * @param chart
	 *            the chart so set
	 */
	public void setChart(World world, JFreeChart chart) {
		this.chartPanel.setChart(chart);
		this.world = world;
	}
}
