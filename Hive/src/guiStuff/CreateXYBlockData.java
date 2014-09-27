package guiStuff;

/**
 * This can create a dataset for the xyblockrenderer.
 */
import hive.Bee;
import hive.Beehive;
import hive.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import org.jfree.data.DomainOrder;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.general.DatasetGroup;
import org.jfree.data.xy.XYZDataset;

import sources.Source;
import sources.Tree;

public class CreateXYBlockData implements Runnable {
	/**
	 * 
	 */
	private World world;

	/**
	 * @param world
	 *            the world the data is to be taken from
	 */
	public CreateXYBlockData(World world) {
		this.world = world;
	}

	/**
	 * Getter mehod.
	 * 
	 * @param height
	 * @param width
	 * 
	 * @return the dataset. Ever used? Don't know.
	 */
	public XYZDataset getDataset(int width, int height) {
		return createDataset(width, height);
	}

	/**
	 * Creates the dataset. Maybe not the fastest solution. A problem is, that
	 * we don't know where all the bees are. So we have to create an array which
	 * every possible point in the world. See source comments.
	 * 
	 * @param height
	 * @param width
	 * 
	 * @return the dataset.
	 */
	public XYZDataset createDataset(int width, int height) {

		// TODO: without sleeping wait for creation of .bees
		try {
			while ((this.world.getUpdateSpeed() == 0)
					&& (this.world.getUpdateSpeed() != 100)) {
				Thread.sleep(1000);
			}
			Thread.sleep((this.world.getUpdateSpeed() + 1) * (-20) + 2020); // 1000
																			// milliseconds
																			// is
																			// one
																			// second.
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}

		// this is for the Bees
		final ArrayList<int[]> densityDataLL = new ArrayList<int[]>();
		final ArrayList<int[]> densityDataSources = new ArrayList<int[]>();
		// int[][] densityData = new int[width][height];
		int[] densityData = new int[width * height];
		int actX;
		int actY;
		int r;
		int p;
		int q;
		Bee bee;
		Tree tree;

		// bees are round...
		int radius = 4;
		int[][] radiusData = new int[9][9];
		int square;
		int yIter;
		int xIter;
		for (xIter = -radius; xIter <= radius; xIter++) {
			for (yIter = -radius; yIter <= radius; yIter++) {
				if (((xIter * xIter) + (yIter * yIter)) <= (radius * radius)) {
					square = Math.abs(xIter) + Math.abs(yIter);
					for (r = 8; r > square; r--) {
						radiusData[xIter + 4][yIter + 4] = radiusData[xIter + 4][yIter + 4] + 1;

					}
				}
			}
		}

		// for every beehive
		// long startTimeNano = System.nanoTime();

		for (Iterator<Beehive> i = this.world.getBeehives().iterator(); i
				.hasNext();) {
			LinkedList<Bee> bees = i.next().getBees();
			synchronized (bees) {
				// ... for every bee
				for (Iterator<Bee> b = bees.iterator(); b.hasNext();) {
					// ... +1 for the position -> two bees on same place -> 2
					bee = b.next();
					actX = bee.getActualX();
					actY = bee.getActualY();
					for (r = 4; r >= 0; r--) {
						for (p = actX - r; p <= actX + r; p++) {
							for (q = actY - r; q <= actY + r; q++) {
								if ((p > 0) && (p < width) && (q > 0)
										&& (q < height)) {
									densityData[q * width + p] = densityData[q
											* width + p]
											+ radiusData[p - actX + radius][q
													- actY + radius];
								}
							}
						}
					}

				}
			}
		}

		Source[][] sourcesMap = this.world.getSourcesMap();
		for (p = 0; p < width; p++) {
			for (q = 0; q < height; q++) {
				/*
				 * if (densityData[j][k] != 0) { densityDataLL.add(new int[] {
				 * p, k, densityData[p][k] }); }
				 */
				if ((densityData[q * width + p]) != 0) {
					densityDataLL.add(new int[] { p, q,
							densityData[q * width + p] });
				}
				if (sourcesMap[p][q] != null) {
					densityDataSources
							.add(new int[] {
									p,
									q,
									Math.round(((sourcesMap[p][q].quality - 1) * 20 + (sourcesMap[p][q].size / 500))
											* (-1)) });
					// System.out.println("qual: " + sourcesMap[p][q].quality +
					// ", size: " + sourcesMap[p][q].size + ", value: " +
					// (((sourcesMap[p][q].quality -1 )*20 +
					// (sourcesMap[p][q].size / 500)) * (-1)));
				}

			}
		}

		for (Beehive b : this.world.getBeehives()) {
			for (p = b.getPositionX() - 10; p < b.getPositionX() + 10; p++) {
				for (q = b.getPositionY() - 10; q < b.getPositionY() + 10; q++) {
					densityDataSources
							.add(new int[] {
									p,
									q,
									((int) (b.getFood() / (b.getSize() + 1) * (-10)) * 20) });
				}
			}
		}

		XYZDataset dataset = new XYZDataset() {
			@Override
			public int getSeriesCount() {
				return 2;
			}

			@Override
			public int getItemCount(int series) {
				int count = 0;
				if (series == 1) {
					count = densityDataLL.size();
				} else {
					count = densityDataSources.size();
				}

				return count;
			}

			@Override
			public Number getX(int series, int item) {
				return new Double(getXValue(series, item));
			}

			@Override
			public double getXValue(int series, int item) {
				if (series == 1) {
					return densityDataLL.get(item)[0];
				} else {
					return densityDataSources.get(item)[0];
				}
			}

			@Override
			public Number getY(int series, int item) {
				return new Double(getYValue(series, item));
			}

			@Override
			public double getYValue(int series, int item) {
				// return world.Beehives.getFirst().bees.get(item).actualY;
				if (series == 1) {
					return densityDataLL.get(item)[1];
				} else {
					return densityDataSources.get(item)[1];
				}
			}

			@Override
			public Number getZ(int series, int item) {
				return new Double(getZValue(series, item));
			}

			@Override
			public double getZValue(int series, int item) {
				if (series == 1) {
					return densityDataLL.get(item)[2];
				} else {
					return densityDataSources.get(item)[2];
				}
			}

			@Override
			public void addChangeListener(DatasetChangeListener listener) {
				// ignore - this dataset never changes
			}

			@Override
			public void removeChangeListener(DatasetChangeListener listener) {
				// ignore
			}

			@Override
			public DatasetGroup getGroup() {
				return null;
			}

			@Override
			public void setGroup(DatasetGroup group) {
				// ignore
			}

			@Override
			public Comparable getSeriesKey(int series) {
				return "sin(sqrt(x + y))";
			}

			@Override
			public int indexOf(Comparable seriesKey) {
				return 0;
			}

			@Override
			public DomainOrder getDomainOrder() {
				return DomainOrder.ASCENDING;
			}
		};
		return dataset;
	}

	@Override
	public void run() {
		// createDataset();

	}

}
