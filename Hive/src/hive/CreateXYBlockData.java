package hive;

/**
 * This can create a dataset for the xyblockrenderer.
 */
import java.awt.Color;
import java.awt.Container;
import java.util.LinkedList;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.GrayPaintScale;
import org.jfree.chart.renderer.PaintScale;
import org.jfree.chart.renderer.xy.XYBlockRenderer;
import org.jfree.data.DomainOrder;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.general.DatasetGroup;
import org.jfree.data.xy.XYZDataset;

public class CreateXYBlockData implements Runnable {
	private World world;
	private XYZDataset dataset;


	public CreateXYBlockData(World world) {
		this.world = world;
	}

	/**
	 * Getter mehod.
	 * 
	 * @return the dataset. Ever used? Don't know.
	 */
	public XYZDataset getDataset() {
		return createDataset();
	}

	/**
	 * Creates the dataset. Maybe not the fastest solution. A problem is, that
	 * we don't know where all the bees are. So we have to create an array which
	 * every possible point in the world. See source comments.
	 * 
	 * @return the dataset.
	 */
	public XYZDataset createDataset() {

		// TODO: without sleeping wait for creation of .bees
		try {
			Thread.sleep(100); // 1000 milliseconds is one second.
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}

		// this is for the Bees
		final LinkedList<int[]> densityDataLL = new LinkedList<int[]>();
		final LinkedList<int[]> densityDataSources = new LinkedList<int[]>();
		int[][] densityData = new int[this.world.width][this.world.height];

		// for every beehive
		for (int i = 0; i < this.world.Beehives.size(); i++) {
			LinkedList<Bee> bees = this.world.Beehives.get(i).bees;

			// ... for every bee
			for (Bee b : bees) {

				// ... +1 for the position -> two bees on same place -> 2
				int actX = b.actualX;
				int actY = b.actualY;
				for (int p = actX - 2;p < actX+2;p++) {
					for (int q = actY - 2; q < actY + 2; q++) {
						if ((p > 0) && (p < this.world.width) && (q > 0) && (q < this.world.height)) {
							densityData[p][q] = densityData[p][q] + 1;
						}
					}
				}
				densityData[actX][actY] = densityData[actX][actY] + 1;

			}
		}

		// it's a big world, so lets reduce the data to places where at least
		// one bee is
		for (int j = 0; j < this.world.width; j++) {
			for (int k = 0; k < this.world.height; k++) {
				if (densityData[j][k] != 0) {
					densityDataLL.add(new int[] { j, k, densityData[j][k] });
				}
				if (this.world.sourcesMap[j][k] != null) {
					densityDataSources.add(new int[] {j, k, (int) this.world.sourcesMap[j][k].size * (-1)});
				}

			}
		}
		for (Beehive b: this.world.Beehives) {
			for (int j = b.positionX - 10; j < b.positionX + 10; j++) {
				for (int k = b.positionY -10; k < b.positionY + 10; k++) {
					densityDataSources.add(new int[] {j, k, (int) (b.food * (-1) * 10)});
				}
			}
		}




		XYZDataset dataset = new XYZDataset() {
			public int getSeriesCount() {
				return 2;
			}

			public int getItemCount(int series) {
				int count = 0;
				if (series == 1) {
					count = densityDataLL.size();
				}
				else {
					count = densityDataSources.size();
				}

				return count;
			}

			public Number getX(int series, int item) {
				return new Double(getXValue(series, item));
			}

			public double getXValue(int series, int item) {
				if (series == 1) {
					return densityDataLL.get(item)[0];
				} else {
					return densityDataSources.get(item)[0];
				}
			}

			public Number getY(int series, int item) {
				return new Double(getYValue(series, item));
			}

			public double getYValue(int series, int item) {
				//return world.Beehives.getFirst().bees.get(item).actualY;
				if (series == 1) {
					return densityDataLL.get(item)[1];
				} else {
					return densityDataSources.get(item)[1];
				}
				// return
				// world.Beehives.getFirst().bees.get(item).rand.nextInt(100);
			}

			public Number getZ(int series, int item) {
				return new Double(getZValue(series, item));
			}

			public double getZValue(int series, int item) {
				if (series == 1) {
					return densityDataLL.get(item)[2];
				} else { 
					return densityDataSources.get(item)[2];
				}
			}

			public void addChangeListener(DatasetChangeListener listener) {
				// ignore - this dataset never changes
			}

			public void removeChangeListener(DatasetChangeListener listener) {
				// ignore
			}

			public DatasetGroup getGroup() {
				return null;
			}

			public void setGroup(DatasetGroup group) {
				// ignore
			}

			public Comparable getSeriesKey(int series) {
				return "sin(sqrt(x + y))";
			}

			public int indexOf(Comparable seriesKey) {
				return 0;
			}

			public DomainOrder getDomainOrder() {
				return DomainOrder.ASCENDING;
			}
		};
		return dataset;
	}

	@Override
	public void run() {
		createDataset();

	}

}
