package hive;

import java.util.LinkedList;
import java.util.Random;
import java.util.ArrayList;

import javax.swing.table.DefaultTableModel;

/**
 * 
 * Constructor-method for the world.
 * 
 * @author ole
 * 
 */
public class World implements Runnable {
	int width;
	int height;
	ArrayList<Integer> sourcesList;
	public LinkedList<Tree> trees;
	LinkedList<Beehive> Beehives;
	DefaultTableModel tableModelBeehives;
	DefaultTableModel tableModelTrees;
	int numOfBeehives;
	private int numOfSources;
	private int numOfTrees;
	private int numOfThisBeehives;
	Source[][] sourcesMap;
	private double hunger;

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
	World(int width, int height, int numOfBeehives, int numOfSources,
			int numOfTrees, double hungry) {
		this.width = width;
		this.height = height;
		this.Beehives = new LinkedList<Beehive>();
		this.numOfBeehives = numOfBeehives;
		this.numOfSources = numOfSources;
		this.numOfTrees = numOfTrees;
		Source[][] sourcesMap = new Source[this.width][this.height];
		this.sourcesMap = sourcesMap;
		this.hunger = hungry;
	}

	/**
	 * On .start() a println is given out.
	 */
	public void run() {
		// some basic information of the world we live in
		System.out.println("World created");
	}

	/**
	 * Creates the beehive(s), one per time. Adds the created beehive to the
	 * list.
	 * 
	 * @param numberOfBees
	 *            number of bees
	 * @param numOfThisBeehive
	 *            number of this beehive
	 * @param hunger
	 *            how much food does each bee take from the beehive per time
	 * @return the created beehive
	 */
	Beehive createBeehive(int numberOfBees, int numOfThisBeehive) {
		int numOfSources = this.sourcesList.size();

		// Beehive(this_is_x_position, this_is_y_position,
		// numberOfBees, the_world,
		// the_number_of_the_actual_beehive_to_Create, hunger_of_bees)
		Beehive bh = new Beehive(this.sourcesList.get(numOfSources - 2),
				this.sourcesList.get(numOfSources - 1), numberOfBees, this,
				this.numOfThisBeehives, this.hunger);

		addBeeHiveToList(bh);
		return bh;
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
	 * Returns beehive list.
	 * 
	 * @return the beehive list
	 */
	public LinkedList<Beehive> getBeehives() {
		// returns beehives list
		return this.Beehives;
	}

	/**
	 * Creates the different sources. At this moment only trees.
	 * 
	 * @param numOfSources
	 *            how many sources to create
	 * @return List of all sources in this world
	 */
	// this is for creating all the different sources
	LinkedList<Tree> createSources(int numOfSources) {

		// create sourcesList with the X and Y positions for the later created
		// sources and beehives
		this.sourcesList = getPosition(this.width, this.height, numOfSources,
				this.sourcesList);
		trees = new LinkedList<Tree>();

		// now create trees with the positions saved in _scourcesList_
		for (int i = 0; i < 2 * numOfSources; i = i + 2) { // TODO: iterator as
			// int[][]

			trees.add(new Tree(this.sourcesList.get(i), this.sourcesList
					.get(i + 1), this));
			trees.getLast().setName("Tree " + i);
			Thread t = new Thread(trees.getLast(), "Tree " + i);
			t.start();
			trees.getLast().setPositionInTrees(trees.indexOf(trees.getLast()));

		}
		return trees;
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

	public void addToSourceMap(int x, int y, int size, Source source) {
		Integer radius = Integer.valueOf((int) Math.round(size/1000));

		int xCheck = 0;
		int yCheck = 0;
		for (int yIter = -radius; yIter <= radius; yIter++) {
			for (int xIter = -radius; xIter <= radius; xIter++) {
				xCheck = xIter+x;
				yCheck = yIter+y;
				if ( ((xIter * xIter) + (yIter * yIter) <= (radius * radius)) && (xCheck< this.width) && (yCheck < this.height) && (xCheck > 0) && (yCheck > 0)) {
					this.sourcesMap[xCheck][yCheck] = source;     
				}
			}
		}
	}

	public Source hitSource(int x, int y) {
		return this.sourcesMap[x][y];
	}

	public double getHunger() {
		// TODO Auto-generated method stub
		return this.hunger;
	}

	public void setHunger(int value) {
		// TODO Auto-generated method stub
		this.hunger = (double) value / 10000;
		System.out.println(value + " " + this.hunger);
	}
}
