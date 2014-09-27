package hive;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Random;
import java.util.ArrayList;

import javax.swing.table.DefaultTableModel;

import sources.Source;
import sources.Tree;

/**
 * 
 * Constructor-method for the world.
 * 
 * @author ole
 * 
 */
public class World  implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6425586782660600708L;
	private int width;
	private int height;
	ArrayList<Integer> sourcesList;
	public LinkedList<Tree> trees;
	LinkedList<Beehive> Beehives;
	transient DefaultTableModel tableModelBeehives;
	private transient DefaultTableModel tableModelTrees;
	private int numOfBeehives;
	private int numOfThisBeehives;
	private Source[][] sourcesMap;
	private double hunger;
	private Random rand;
	private LinkedList<Bee> bees;
	private boolean startModel;
	private int updateSpeed;
	private int worldSpeed;
	private int numOfBees;

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

	// constructor
	World(int width, int height, int numOfBees, int numOfBeehives, int numOfSources,
			int numOfTrees, double hungry) {
		startModel = false;
		this.setWidth(width);
		this.setHeight(height);
		this.Beehives = new LinkedList<Beehive>();
		this.numOfBeehives = numOfBeehives;
		Source[][] sourcesMap = new Source[this.getWidth()][this.getHeight()];
		this.sourcesMap = sourcesMap;
		this.hunger = hungry;
		this.rand = new Random();
		bees = new LinkedList<Bee>();
		updateSpeed = 100;
		worldSpeed = 80;
		this.numOfBees = numOfBees;
	}

	/**
	 * On .start() a println is given out.
	 */


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

	public void addToSourceMap(int x, int y, int size, Source source) {
		Integer radius = Integer.valueOf((int) Math.round(size/1000));

		int xCheck = 0;
		int yCheck = 0;
		for (int yIter = -radius; yIter <= radius; yIter++) {
			for (int xIter = -radius; xIter <= radius; xIter++) {
				xCheck = xIter+x;
				yCheck = yIter+y;
				if ( ((xIter * xIter) + (yIter * yIter) <= (radius * radius)) && (xCheck< this.getWidth()) && (yCheck < this.getHeight()) && (xCheck > 0) && (yCheck > 0)) {
					this.sourcesMap[xCheck][yCheck] = source;
					//System.out.println("Sx: " + x + " Source: " + source);
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
		Beehive bh = new Beehive(rand.nextInt(width),
				rand.nextInt(height), this,
				this.numOfThisBeehives, this.hunger);

		addBeeHiveToList(bh);
		return bh;
	}


	public void createBees(int numOfBees) {
		// create all the bees and add each one to the list. start each bee with
		// own thread.
		for (int j = 0; j < numOfBeehives; j++)
			for (int i = 0; i < numOfBees; i++) {
				this.bees.add(new Bee(this, Beehives.get(j)));


			}
	}

	/**
	 * Creates the different sources. At this moment only trees.
	 * 
	 * @param numOfSources
	 *            how many sources to create
	 * @return List of all sources in this world
	 */
	// this is for creating all the different sources
	public LinkedList<Tree> createSources(int numOfSources) {

		// create sourcesList with the X and Y positions for the later created
		// sources and beehives
		trees = new LinkedList<Tree>();

		// now create trees with the positions saved in _scourcesList_
		for (int i = 1; i <= numOfSources; i++) { 

			trees.add(new Tree(rand.nextInt(width), rand.nextInt(height), this));
			trees.getLast().setName("Tree " + i);
			trees.getLast().setPositionInTrees(trees.size()-1);

		}
		return trees;
	}
	
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

	public double getHunger() {
		// TODO Auto-generated method stub
		return this.hunger;
	}
	/**
	 * @return the numOfBeehives
	 */
	public int getNumOfBeehives() {
		return this.numOfBeehives;
	}

	/**
	 * Helper function for getting random positions in world for sources and
	 * beehive.
	 * 
	 * @param maxX in most times x-size of the world
	 * @param maxY in most times y-size of the world
	 * @param count number of xy-pairs to create
	 * @param array not really needed, is it?
	 * @return array of all xy-pairs
	 */
	//
	ArrayList<Integer> getPosition(int maxX, int maxY, int count,
			ArrayList<Integer> array) {
		// TODO is array needed be submitted to this method?
		Random rand = new Random();
		array = new ArrayList<Integer>();
		for (int j = 0; j < count + 1; j++) {
			array.add(rand.nextInt(maxX));
			array.add(rand.nextInt(maxY));

		}
		return array;

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
	public DefaultTableModel getTableModelTrees() {
		return tableModelTrees;
	}

	public Number getTreeCount() {
		// TODO Auto-generated method stub
		return trees.size();
	}

	public LinkedList<Tree> getTrees() {
		// TODO Auto-generated method stub
		return trees;
	}

	public int getUpdateSpeed() {
		// Returns the Update Speed
		return updateSpeed;
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	public int getWorldSpeed() {
		// Setter for worldSpeed
		return worldSpeed;
	}

	public Source hitSource(int x, int y) {
		return this.sourcesMap[x][y];
	}

	/**
	 * @return the startModel
	 */
	public boolean isStartModel() {
		return startModel;
	}

	public void removeFromSourceMap(int x, int y, int size, Source source) {
		Integer radius = Integer.valueOf((int) Math.round(size/1000));

		int xCheck = 0;
		int yCheck = 0;
		for (int yIter = -radius; yIter <= radius; yIter++) {
			for (int xIter = -radius; xIter <= radius; xIter++) {
				xCheck = xIter+x;
				yCheck = yIter+y;
				if ( ((xIter * xIter) + (yIter * yIter) <= (radius * radius)) && (xCheck< width) && (yCheck < height) && (xCheck > 0) && (yCheck > 0)) {
					if (this.sourcesMap[xCheck][yCheck] == source) {
						this.sourcesMap[xCheck][yCheck] = null;     
					}
				}
			}
		}

	}

	public void setBeeCount(int value) {
		// Setter for numOfBees

		int diff = value - this.bees.size();
		if (diff > 0) {
			for (int j = 0; j < numOfBeehives; j++)
				for (int i = 0; i < diff; i++) {
					synchronized (this.bees) {
						this.bees.add(new Bee(this, Beehives.get(j)));
					}
					Thread t = new Thread(this.bees.getLast(), "Bee " + (i + this.numOfBees));
					// Thread t = new Thread(bees.get(i), "Bee " + i);
					//Thread t = new Thread(new GoSearching(bees.get(i)), "Bee " + i);
					t.start();

				}

		}
		else if (diff < 0) {
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
	 * @param bees the bees to set
	 */
	public void setBees(LinkedList<Bee> bees) {
		this.bees = bees;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	public void setHunger(int value) {
		// This sets the hunger of the bees.
		this.hunger = (double) value / 10000;
	}
	/**
	 * @param numOfBeehives the numOfBeehives to set
	 */
	public void setNumOfBeehives(int numOfBeehives) {
		this.numOfBeehives = numOfBeehives;
	}

	/**
	 * @param sourcesMap the sourcesMap to set
	 */
	public void setSourcesMap(Source[][] sourcesMap) {
		this.sourcesMap = sourcesMap;
	}

	/**
	 * @param startModel the startModel to set
	 */
	public void setStartModel(boolean startModel) {
		this.startModel = startModel;
	}

	/**
	 * Setter method for the tableModels for the Gui.
	 * @param tableModel the table model to add to the world
	 * @param string only "Beehives" or "Trees" (at this moment)
	 */
	public void setTableModel(DefaultTableModel tableModel, String string) {

		switch (string) {
		case "Beehives":
			this.tableModelBeehives = tableModel;
			break;
		case "Trees":
			this.tableModelTrees = tableModel;
			break;
		}
	}

	/**
	 * @param tableModelTrees the tableModelTrees to set
	 */
	public void setTableModelTrees(DefaultTableModel tableModelTrees) {
		this.tableModelTrees = tableModelTrees;
	}

	public void setTreeCount(int newCount) {
		// This changes number of Trees
		int diff = newCount - this.trees.size();
		if (diff > 0) {
			for (int i = 0; i < diff; i++) {
				trees.add(new Tree(rand.nextInt(width), rand.nextInt(height), this));
				int size = this.trees.size();
				trees.getLast().setName("Tree " + (size));

				Thread t = new Thread(trees.getLast(), "Tree " + (size));
				t.start();
				trees.getLast().setPositionInTrees(size-1);
			}

		}
		else if (diff < 0) {
			for (int i = 0; i > diff; i--) {
				Tree t = trees.getLast();
				this.removeFromSourceMap(t.x, t.y, t.maxsize, t);
				t.setAlive(false);
				trees.removeLast();
			}
		}
	}

	public void setUpdateSpeed(int value) {
		// Setter Method for Update Speed
		updateSpeed = value;

	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	public void setWorldSpeed(int value) {
		// Setter for worldSpeed
		worldSpeed = value;

	}

	public void startBeehives() {
		// this creates the threads and starts them for the beehives
		for (int i = 0; i < this.Beehives.size();i++) {
			Thread p = new Thread(this.Beehives.get(i));
			p.start();
			p.setName("Beehive " + i+1);
		}
	}

	public void startBees() {
		for (int i = 0; i < this.bees.size(); i++) {
			Thread t = new Thread(this.bees.get(i), "Bee " + i);
			t.start();
		}

	}

	public void startModel() {
		// Here the threads are started
		startModel = true;

	}

	public void startSources() {
		for (int i = 0; i < this.trees.size(); i++) {
			Thread t = new Thread(trees.get(i), "Tree " + i+1);
			trees.get(i).setAlive(true);
			trees.get(i).setName("Tree " + i);
			t.start();
			trees.getLast().setPositionInTrees(i+1);
		}
	}

	public void stopModel() {
		// This pauses the threads
		startModel = false;

	}

}
