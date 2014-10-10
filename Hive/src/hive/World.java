package hive;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Random;
import javax.swing.table.DefaultTableModel;

import sources.Source;
import sources.Tree;
import sources.Water;

/**
 * 
 * Constructor-method for the world.
 * 
 * @author ole
 * 
 */
public class World implements Serializable {
	private static final long serialVersionUID = 6425586782660600708L;
	LinkedList<Beehive> Beehives;
	private LinkedList<Bee> bees;
	private int height;
	private double hunger;
	private int countOfBeehives;
	private int numOfBees;
	private int numOfThisBeehives;
	private Random rand;
	private Source[][] sourcesMap;
	private boolean startModel;
	private transient DefaultTableModel tableModelBeehives;
	private transient DefaultTableModel tableModelTrees;
	public LinkedList<Source> trees;
	private int updateSpeed;
	private int width;
	private int worldSpeed;
	private LinkedList<Source> waters;
	private transient DefaultTableModel tableModelWaters;

	/**
	 * Creates the world.
	 * 
	 * @param numOfBeehives
	 *            the number of (actual) bees
	 * @param numOfTrees
	 *            the number of (actual) trees
	 * @param numOfSources
	 *            the number of (actual) sources
	 * @param width
	 *            x-dimension of this world
	 * @param height
	 *            y-dimension of this world
	 * @param hungry
	 */
	public World(int width, int height, int numOfBees, int numOfBeehives,
			double hungry) {
		startModel = false;
		this.setWidth(width);
		this.setHeight(height);
		this.Beehives = new LinkedList<Beehive>();
		this.countOfBeehives = numOfBeehives;
		Source[][] sourcesMap = new Source[this.getWidth()][this.getHeight()];
		this.sourcesMap = sourcesMap;
		this.hunger = hungry;
		this.rand = new Random();
		bees = new LinkedList<Bee>();
		updateSpeed = 100;
		worldSpeed = 80;
		this.numOfBees = numOfBees;
		trees = new LinkedList<Source>();
		waters = new LinkedList<Source>();
	}

	/**
	 * Adds beehive to beehive list.
	 * 
	 * @param bh
	 *            the beehive to add to the beehive list
	 */
	private void addBeeHiveToList(Beehive bh) {
		// adds beehive to list
		this.Beehives.add(bh);
		bh.setIndexInBeehiveList(this.Beehives.indexOf(bh));
	}

	/**
	 * Adds given Source to the sourcemap.
	 * 
	 * @param x
	 *            x-Value
	 * @param y
	 *            y-Value
	 * @param size
	 *            MaxSize of Source
	 * @param source
	 *            which Source to add
	 */
	public void addToSourceMap(Source source) {
		Integer radius = Integer.valueOf(Math.round(source.getSize() / 1000));

		int xCheck = 0;
		int yCheck = 0;
		for (int yIter = -radius; yIter <= radius; yIter++) {
			for (int xIter = -radius; xIter <= radius; xIter++) {
				xCheck = xIter + source.getX();
				yCheck = yIter + source.getY();
				if (((xIter * xIter) + (yIter * yIter) <= (radius * radius))
						&& (xCheck < this.getWidth())
						&& (yCheck < this.getHeight()) && (xCheck > 0)
						&& (yCheck > 0)) {
					this.sourcesMap[xCheck][yCheck] = source;
					// System.out.println("Sx: " + x + " Source: " + source);
				}
			}
		}
	}

	/**
	 * Creates the beehive(s), one per time. Adds the created beehive to the
	 * list.
	 * 
	 * @param numOfThisBeehive
	 *            number of this beehive
	 * @return the created beehive
	 */
	public Beehive createBeehive(int numOfThisBeehive) {

		// Beehive(this_is_x_position, this_is_y_position,
		// numberOfBees, the_world,
		// the_number_of_the_actual_beehive_to_Create, hunger_of_bees)
		Beehive bh = new Beehive(rand.nextInt(width), rand.nextInt(height),
				this, this.numOfThisBeehives, this.hunger);

		addBeeHiveToList(bh);
		return bh;
	}

	public void createBees(int numOfBees) {
		// create all the bees and add each one to the list. start each bee with
		// own thread.
		for (int j = 0; j < countOfBeehives; j++)
			for (int i = 0; i < numOfBees; i++) {
				this.bees.add(new Bee(this, Beehives.get(j)));
			}
	}

	/**
	 * Creates the different sources. At this moment only trees.
	 * 
	 * @param numOfSources
	 *            how many sources to create
	 * @return 
	 * @return List of all sources in this world
	 */
	// this is for creating all the different sources
	public void createSources(int numOfSource, String type) {

		// create sourcesList with the X and Y positions for the later created
		// sources and beehives

		// now create trees with the positions saved in _scourcesList_
		if (type.equals("Trees")) {
			for (int i = 1; i <= numOfSource; i++) {

				trees.add(new Tree(rand.nextInt(width), rand.nextInt(height), this));
				trees.getLast().setName("Tree " + i);
				trees.getLast().setPosition(trees.size() - 1);
			}
		} else if (type.equals("Waters")) {
			for (int i = 1; i <= numOfSource; i++) {

				waters.add(new Water(rand.nextInt(width), rand.nextInt(height), this));
				waters.getLast().setName("Water " + i);
				waters.getLast().setPosition(waters.size() - 1);
			}

		}
		//return trees;
	}

	/**
	 * Returns the count of the bees.
	 * 
	 * @return the count of bees.
	 */
	public int getBeeCount() {
		// getter for numOfBees
		return this.numOfBees;
	}

	/**
	 * Returns beehive list.
	 * 
	 * @return the beehive list
	 */
	public LinkedList<Beehive> getBeehives() {
		// returns beehives list
		return this.Beehives;
	}

	/**
	 * @return the bees
	 */
	public LinkedList<Bee> getBees() {
		return bees;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Returns the hunger of the bees
	 * 
	 * @return hunger of the bees
	 */
	public double getHunger() {
		return this.hunger;
	}

	/**
	 * Returns count of Beehives
	 * 
	 * @return the numOfBeehives
	 */
	public int getCountOfBeehives() {
		return this.countOfBeehives;
	}

	/**
	 * @return the sourcesMap
	 */
	public Source[][] getSourcesMap() {
		return sourcesMap;
	}

	/**
	 * @return the tableModelTrees
	 */
	public DefaultTableModel getTableModel(String type) {
		if (type.equals("tree")) {
			return tableModelTrees;
		} else if (type.equals("water")) {
			return tableModelWaters;
		} else if (type.equals("beehive")) {
			return tableModelBeehives;
		}
		return null;
		
	}

	/**
	 * @return count of trees in this world
	 */
	public Number getTreeCount() {
		// TODO Auto-generated method stub
		return trees.size();
	}

	/**
	 * @return LinkedList of trees of this world
	 */
	public LinkedList<Source> getTrees() {
		// TODO Auto-generated method stub
		return trees;
	}

	/**
	 * @return updateSpeed of model
	 */
	public int getUpdateSpeed() {
		// Returns the Update Speed
		return updateSpeed;
	}

	/**
	 * @return width of the world
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Setter for worldSpeed
	 * 
	 * @return world Speed
	 */
	public int getWorldSpeed() {
		return worldSpeed;
	}

	/**
	 * This gives back source in x-y of null if there's none
	 * 
	 * @param x
	 *            x-Value to look for
	 * @param y
	 *            y-Value to look for
	 * @return Source if there's one, null otherwise
	 */
	Source hitSource(int x, int y) {
		return this.sourcesMap[x][y];
	}

	/**
	 * @return is model active?
	 */
	public boolean isStartModel() {
		return startModel;
	}

	/**
	 * This methods removes sources from the sources map
	 * 
	 * @param x
	 *            x-value
	 * @param y
	 *            y-value
	 * @param size
	 *            size of source that will be removed
	 * @param source
	 *            source to remove
	 */
	private void removeFromSourceMap(int x, int y, int size, Source source) {
		Integer radius = Integer.valueOf(Math.round(size / 1000));

		int xCheck = 0;
		int yCheck = 0;
		for (int yIter = -radius; yIter <= radius; yIter++) {
			for (int xIter = -radius; xIter <= radius; xIter++) {
				xCheck = xIter + x;
				yCheck = yIter + y;
				if (((xIter * xIter) + (yIter * yIter) <= (radius * radius))
						&& (xCheck < width) && (yCheck < height)
						&& (xCheck > 0) && (yCheck > 0)) {
					if (this.sourcesMap[xCheck][yCheck] == source) {
						this.sourcesMap[xCheck][yCheck] = null;
					}
				}
			}
		}

	}

	/**
	 * @param value
	 *            the new value for the bee count
	 */
	public void setBeeCount(int value) {
		int diff = value - this.bees.size();
		if (diff > 0) {
			for (int j = 0; j < countOfBeehives; j++)
				for (int i = 0; i < diff; i++) {
					synchronized (this.bees) {
						this.bees.add(new Bee(this, Beehives.get(j)));
					}
					Thread t = new Thread(this.bees.getLast(), "Bee "
							+ (i + this.numOfBees));
					t.start();
				}

		} else if (diff < 0) {
			for (int i = 0; i > diff; i--) {
				this.bees.getLast().setAlive(false);
				synchronized (this.bees) {
					this.bees.removeLast();
				}
			}
		}
		this.numOfBees = value;
	}

	/**
	 * @param bees
	 *            the bees to set
	 */
	public void setBees(LinkedList<Bee> bees) {
		this.bees = bees;
	}

	/**
	 * @param height
	 *            the height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * @param hunger
	 *            the hunger to set
	 */
	public void setHunger(int hunger) {
		// This sets the hunger of the bees.
		this.hunger = (double) hunger / 10000;
	}

	/**
	 * @param countOfBeehives
	 *            the countOfBeehives to set
	 */
	public void setNumOfBeehives(int countOfBeehives) {
		this.countOfBeehives = countOfBeehives;
	}

	/**
	 * @param sourcesMap
	 *            the sourcesMap to set
	 */
	public void setSourcesMap(Source[][] sourcesMap) {
		this.sourcesMap = sourcesMap;
	}

	/**
	 * @param startModel
	 *            the startModel to set
	 */
	public void setStartModel(boolean startModel) {
		this.startModel = startModel;
	}

	/**
	 * Setter method for the tableModels for the Gui.
	 * 
	 * @param tableModel
	 *            the table model to add to the world
	 * @param string
	 *            only "Beehives" or "Trees" (at this moment)
	 */
	public void setTableModel(DefaultTableModel tableModel, String string) {

		switch (string) {
		case "Beehives":
			this.tableModelBeehives = tableModel;
			break;
		case "Trees":
			this.tableModelTrees = tableModel;
			break;
		case "Waters":
			this.tableModelWaters = tableModel;
			break;
		}
	}

	/**
	 * @param tableModelTrees
	 *            the tableModelTrees to set
	 */
	//public void setTableModelTrees(DefaultTableModel tableModelTrees) {
	//	this.tableModelTrees = tableModelTrees;
	//}

	/**
	 * @param newCount
	 *            the new tree count to set
	 */
	public void setSourceCount(int newCount, String type) {
		// This changes number of Sources
		Source newSource = null;
		LinkedList<Source> list = null;
		String sourceString = null;
		if (type.equals("Trees")) {
			list = trees;
		} else if (type.equals("Waters")) {
			list = waters;
		}
		int diff = newCount - list.size();
		if (diff > 0) {
			for (int i = 0; i < diff; i++) {
				createSources(1, type);
				Thread t = new Thread(list.getLast(), type + (list.size()));
				t.start();
				list.getLast().setPosition(list.size() - 1);
			}

		} else if (diff < 0) {
			for (int i = 0; i > diff; i--) {
				Source t = list.getLast();
				this.removeFromSourceMap(t.getX(), t.getY(), t.getMaxsize(), t);
				t.setAlive(false);
				list.removeLast();
			}
		}
	}

	/**
	 * @param newUpdateSpeed
	 *            the new update speed to set
	 */
	public void setUpdateSpeed(int newUpdateSpeed) {
		// Setter Method for Update Speed
		updateSpeed = newUpdateSpeed;

	}

	/**
	 * @param width
	 *            the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * @param newWorldSpeed
	 *            the new world speed to set
	 */
	public void setWorldSpeed(int newWorldSpeed) {
		// Setter for worldSpeed
		worldSpeed = newWorldSpeed;

	}

	/**
	 * Starts the threads for the beehives.
	 */
	public void startBeehives() {
		// this creates the threads and starts them for the beehives
		for (int i = 0; i < this.Beehives.size(); i++) {
			Thread p = new Thread(this.Beehives.get(i));
			p.start();
			p.setName("Beehive " + i + 1);
		}
	}

	/**
	 * Starts threads for bees.
	 */
	public void startBees() {
		for (int i = 0; i < this.bees.size(); i++) {
			Thread t = new Thread(this.bees.get(i), "Bee " + i);
			t.start();
		}

	}

	/**
	 * Sets startModel = true.
	 */
	public void startModel() {
		// Here the threads are started
		startModel = true;

	}

	/**
	 * Starts threads for the sources.
	 */
	public void startSources() {
		for (int i = 0; i < this.trees.size(); i++) {
			Thread t = new Thread(trees.get(i), "Tree " + i + 1);
			trees.get(i).setAlive(true);
			trees.get(i).setName("Tree " + i);
			t.start();
			trees.getLast().setPosition(i + 1);
		}

		for (int i = 0; i < this.waters.size(); i++) {
			Thread t = new Thread(waters.get(i), "Water " + i + 1);
			waters.get(i).setAlive(true);
			waters.get(i).setName("Water " + i);
			t.start();
			waters.getLast().setPosition(i + 1);
		}
	}

	/**
	 * Stops model.
	 */
	public void stopModel() {
		// This pauses the threads
		startModel = false;

	}

	public Number getWaterCount() {
		// TODO Auto-generated method stub
		return this.waters.size();
	}

	public LinkedList<Source> getWaters() {
		// TODO Auto-generated method stub
		return waters;
	}

	/**
	 * Removes bee from bee-list.
	 * @param bee the bee to remove
	 */
	public void removeBee(Bee bee) {
		this.bees.remove(bee);
	}

	/**
	 * Setter for table cell.
	 * @param tableName which table
	 * @param newValue new value
	 * @param rowNumber row number
	 * @param columnNumber column number
	 */
	public void setValue(String tableName, double newValue, int rowNumber,
			int columnNumber) {
		if ((tableName.equals("beehive")) && (this.tableModelBeehives != null )) {
		this.tableModelBeehives.setValueAt(Double.toString(newValue),
				rowNumber, columnNumber);
			}
	}



}
