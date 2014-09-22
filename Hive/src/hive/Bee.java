package hive;

import java.util.Random;
//import java.util.LinkedList;
import java.util.ArrayList;

/**
 * The bee - lots of information is stored here. Each bee object is meant to be
 * run as single thread, as there are lots of actions, interactions, decisions
 * and death (not implemented yet).dcvd
 * 
 * @author ole
 * 
 */

// TODO: Death of the bee.
public class Bee implements Runnable, SourceInterface, BeehiveInterface {
	int homeX;
	int homeY;
	int actualX;
	int actualY;
	boolean hasKnowledge;
	int knowledge;
	int food;
	int age;
	boolean atHome;
	Beehive ownHive;
	private String status;
	ArrayList<Bee> waitingQueue;
	World world;
	Random rand;
	Source source;
	private Bee bee;
	int sourceX;
	int sourceY;
	double sourceQual;
	String sourceType;
	double sourceSize;

	/**
	 * Constructor method. Add to waiting queue, as bee is born in beehive and
	 * has nothing to do.
	 * 
	 * @param world
	 *            world object of the bee.
	 * @param ownHive
	 *            beehive object of the bee.
	 */
	Bee(World world, Beehive ownHive) {
		this.homeX = ownHive.positionX;
		this.homeY = ownHive.positionY;
		this.actualX = ownHive.positionX;
		this.actualY = ownHive.positionY;
		this.atHome = true;
		this.hasKnowledge = false;
		this.age = 100;
		this.ownHive = ownHive;
		this.status = "waiting";
		this.world = world;
		this.rand = new Random();
	}

	/**
	 * Run method. Nothing here at the moment.
	 */
	public void run() {
		// this is the class for the bees
		// which information do we need?
		// belongs to which beehive? -> postion beehive
		// age
		// actual position
		// actual action
		// knowledge
		// food
		// ...?
		// System.out.println("Biene wird erzeugt");

		// the bee is born
		// ... it is in the beehive
		// ... status -> wating for information

	}

	@Override
	public void getStuff(Source source, Bee bee) {
		// grab all the information and some food/water from source
		// and reduce size of source by 1
		this.bee = bee;
		this.bee.source = source;
		this.bee.food = this.source.getFood();

		// .. but if the source is empty, delete knowledge
		if (this.bee.food == 0) {
			this.bee.hasKnowledge = false;
			this.bee.sourceX = 0;
			this.bee.sourceY = 0;
			this.bee.sourceQual = 0;
			this.bee.sourceType = "";
			this.bee.sourceSize = 0;
			this.bee.setStatus("empty");
			this.bee.removeKnow();
			// ... if the source is not empty, bee has food now.
		} else {
			this.bee.hasKnowledge = true;
			this.bee.sourceX = this.source.x;
			this.bee.sourceY = this.source.y;
			this.bee.sourceQual = this.source.quality;
			this.bee.sourceType = this.source.type;
			this.bee.sourceSize = this.source.size;
			this.bee.setStatus("full");
		}

	}

	@Override
	public void giveStuff(Beehive beehive, Bee bee) {
		// +1 on food for submitted beehive, remove food from bee

		synchronized (this.ownHive) {
			if (beehive.food < beehive.getSize()) {
			beehive.food = beehive.food + 1;
			bee.food = 0;
			} else {
				bee.setStatus("waiting");
				this.bee.removeKnow();
			}
		}
	}

	@Override
	public void eat(Beehive beehive) {
		// just a little helper to run eat() synced
		this.ownHive.eat();
	}
	

	@Override
	public void addToQueue(Beehive beehive, Bee bee) {
		// Add submitted bee to submitted beehive.waitingQueue
		
			beehive.waitingQueueAdd(bee);
		
		refreshTableModel();
	}

	@Override
	public void removeFromQueue(Beehive beehive, Bee bee) {
		// remove submitted Bee from submitted Beehive.waitingQueue
			beehive.waitingQueueRemove(bee);
		
		refreshTableModel();
	}

	@Override
	public ArrayList<Bee> removeFromQueue(Beehive beehive, int count) {
		// this is mainly to get the first 6 bees of the waiting list for
		// dancing lessons
		ArrayList<Bee> beeSublist = new ArrayList<Bee>();
		beeSublist = beehive.waitingQueueSublist(count);
		
		refreshTableModel();
		return beeSublist;
	}

	/**
	 * Adjust waitingqueue size in the table model. For the Gui.
	 */
	private void refreshTableModel() {
		if (this.world.tableModelBeehives != null) {
			// the setValueAt method fires out a change event to the table ->
			// table is refreshed in the gui -> new number is shown.
			this.world.tableModelBeehives.setValueAt(
					this.ownHive.getWaitingQueueSize(),
					this.ownHive.IndexInBeehiveList, 4);
		}
	}

	/**
	 * Setter method for the status of this bee.
	 * 
	 * @param string
	 *            {"waiting", "searching", etc}
	 */
	public void setStatus(String string) {
		synchronized (this) {
			this.status = string;
		}
	}

	/**
	 * Getter method for the status.
	 * 
	 * @return the status as string
	 */
	public String getStatus() {
		synchronized (this) {
			return this.status;
		}
	}

	/**
	 * Removes any knowledge of sources. 
	 */
	public void removeKnow() {
		this.hasKnowledge = false;
		this.sourceQual = 0;
		this.sourceSize = 0;
		this.sourceX = 0;
		this.sourceY = 0;

	}
}
