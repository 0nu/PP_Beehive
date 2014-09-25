package guiStuff;

/**
 * This can create a dataset for the xyblockrenderer.
 */
import hive.Bee;
import hive.Beehive;
import hive.World;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;

import org.jfree.data.DomainOrder;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.general.DatasetGroup;
import org.jfree.data.xy.XYZDataset;

import sources.Source;

public class CreateXYBlockData implements Runnable
{
	/**
	 * 
	 */
	private World world;

	public CreateXYBlockData(World world) {
		this.world = world;
	}

	/**
	 * Getter mehod.
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
	 * @param height 
	 * @param width 
	 * 
	 * @return the dataset.
	 */
	public XYZDataset createDataset(int width, int height) {

		// TODO: without sleeping wait for creation of .bees
		try {
			while ((this.world.getUpdateSpeed() == 0) && (this.world.getUpdateSpeed() != 100)) {
				Thread.sleep(1000);
			}
			Thread.sleep((this.world.getUpdateSpeed() +1)  * (-20) + 2020); // 1000 milliseconds is one second.
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}

		// this is for the Bees
		final LinkedList<int[]> densityDataLL = new LinkedList<int[]>();
		final LinkedList<int[]> densityDataSources = new LinkedList<int[]>();
		int[][] densityData = new int[width][height];
		int actX;
		int actY;
		int r;
		int p;
		int q;
		Bee bee;

		// bees are round...
		int radius = 4;
		int[][] radiusData = new int[9][9];
		int square;
		int yIter;
		int xIter;
		for (xIter = -radius; xIter <= radius; xIter++) {
			for (yIter = -radius; yIter <= radius; yIter++) {
				if ( ((xIter * xIter) + (yIter * yIter)) <= (radius * radius)) {
					square = Math.abs(xIter) + Math.abs(yIter);
					for (int diff = 8; diff > square; diff--) {
						radiusData[xIter+4][yIter+4] = radiusData[xIter+4][yIter+4] + 1;


					}
				}
			}
		}

		// for every beehive		
		//long startTimeNano = System.nanoTime();

		for (Iterator<Beehive> i = this.world.getBeehives().iterator(); i.hasNext();) {
		

				LinkedList<Bee> bees = i.next().getBees();
				synchronized (bees) {
				// ... for every bee



				for (Iterator<Bee> b = bees.iterator(); b.hasNext();) {

					// ... +1 for the position -> two bees on same place -> 2
					bee = b.next(); 
					actX = bee.getActualX();
					actY = bee.getActualY();

					for ( r = 4; r >=0; r--) {
						for ( p = actX - r;p <= actX+r;p++) {
							for ( q = actY - r; q <= actY + r; q++) {
								if ((p > 0) && (p < width) && (q > 0) && (q < height)) {
									densityData[p][q] = densityData[p][q] + radiusData[p-actX+radius][q-actY+radius];

									//}
								}
							}
						}
					}

				}
			} 
		}
		/*		long endTimeNano = System.nanoTime( );
		System.out.println("create beeinfo   : " + (endTimeNano - startTimeNano));*/
		// it's a big world, so lets reduce the data to places where at least
		// one bee is

		//startTimeNano = System.nanoTime( );

		Source[][] sourcesMap = this.world.getSourcesMap();
		for (int j = 0; j < width; j++) {
			for (int k = 0; k < height; k++) {
				if (densityData[j][k] != 0) {
					densityDataLL.add(new int[] { j, k, densityData[j][k] });
				}
				if (sourcesMap[j][k] != null) {
					densityDataSources.add(new int[] {j, k, (int) sourcesMap[j][k].size * (-1)});
				}

			}
		}
		/*endTimeNano = System.nanoTime( );

		System.out.println("create sourcesmap: " + (endTimeNano - startTimeNano));*/

		for (Beehive b: this.world.getBeehives()) {
			for (int j = b.getPositionX() - 10; j < b.getPositionX() + 10; j++) {
				for (int k = b.getPositionY() -10; k < b.getPositionY() + 10; k++) {
					densityDataSources.add(new int[] {j, k, (int) (b.getFood() * (-1) * 10)});
				}
			}
		}

		//System.out.println("-----------------------------------------------------");


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
		//createDataset();

	}

}
