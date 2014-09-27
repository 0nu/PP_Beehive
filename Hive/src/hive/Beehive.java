package hive;

import java.io.Serializable;
import java.util.Random;
import java.util.LinkedList;
import java.util.ArrayList;

/**
 * The Beehive Class. To be run as own thread, as here are lots of information
 * and probably lots of synchronized methods. Plus all the bees.
 * 
 * @author ole
 * 
 */
public class Beehive implements Runnable, Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5404284857273245055L;
	private int positionX;
	private int positionY;
	int numOfBees;
	private double food;

	World world;
	private ArrayList<Bee> waitingQueue;
	private Random rand;
	boolean sendout;
	private String name;
	int size;
	public int IndexInBeehiveList;
	private Boolean alive;

	/**
	 * Constructor method for beehive class.
	 * 
	 * @param x
	 *            x-position of this beehive
	 * @param y
	 *            y-position of this beehive
	 * @param world
	 *            world-object
	 * @param num
	 *            the number of this beehive in the world
	 * @param hunger
	 *            how much food does each bee take a time
	 */

	Beehive(int x, int y, World world, int num, double hunger) {
		positionX = x;
		positionY = y;
		food = 1000;
		this.world = world;
		this.waitingQueue = new ArrayList<Bee>();
		this.rand = new Random();
		this.size = (int)food;
		food = 880;
		this.name = "Beehive " + num;
		this.alive = true;
	}

	/**
	 * run method. nothing happens here.
	 */
	public void run() {
		while (this.alive) {
			try {
				while (!world.isStartModel() && (alive)) {
					Thread.sleep(500);
				}
				//System.out.println(getWaitTime());
				Thread.sleep((int) Math.round(900 * getWaitTime())); // 1000 milliseconds is one second.
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
			synchronized (this.alive) {
				if (this.alive) {
					eat();	
					sendout();

				}

			}
		}


	}

	private void sendout() {
		if (this.food < (0.9 * this.size)) {
			this.waitingQueueSetStatus("searching",0);
		}
	}


	private double getWaitTime() {
		// Returns WaitTime factor depending on worldSpeed
		// init value is 40 
		double factor = 20;
		factor = factor / this.world.getWorldSpeed();
		return factor;
	}


	/**
	 * Not needed at the moment. Can probably be removed.
	 */
	void beehiveSleep() {
		do {
			try {
				Thread.sleep(1000); // 1000 milliseconds is one second.
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
		} while (this.food > 500);
		System.out.println("Food below 300");

	}

	// 
	// TODO: setter-Method for food.
	/**
	 * Here we reduce the food amount of the beehive
	 * @return empty == 1 if beehive is empty, otherweise empty == 0
	 */
	public synchronized int eat() {
		int empty;
		if (this.food >= (this.world.getHunger() * this.getWaitingQueueSize()) ) {
			this.food = this.food - (this.world.getHunger() * this.getWaitingQueueSize());
			empty = 0;

			// change the tableModel for the gui
			// but only change when the table is already created
			// ... and rand = 10 <- this is ugly but saves some cpu time
			//if (world.tableModelBeehives != null && this.rand.nextInt(world.getUpdateSpeed() + 1) == 2) {// TODO:
			// tableModelBeehives
			// update
			// more
			// pretty
			// &
			// fast


			world.tableModelBeehives.setValueAt(Double.toString(this.food),
					this.IndexInBeehiveList, 1); // TODO:
		//	System.out.println(world + " " + world.tableModelBeehives);
		} else {
			this.food= 0;
			empty = 1;

		}

		// I don't want negative food values
		if (this.food < 0) {
			this.food = 0;
		}
		return empty;
	}

	/**
	 * Returns the name of this object.
	 * @return the name of this object
	 */
	public String getName() {
		// returns Name of Beehive
		return this.name;
	}

	public int getSize() {
		// returns MaxSize of Beehive
		return this.size;
	}

	public void setSize(int value) {
		// Sets MaxSize of Beehive
		this.size = value;
	}

	public void waitingQueueRemove(Bee bee) {
		// removes bee from waiting queue
		synchronized (waitingQueue) {
			waitingQueue.remove(bee);	
		}

	}

	public ArrayList<Bee> waitingQueueSublist(int count) {
		// returns sublist of waiting queue and removes the returned bees
		ArrayList<Bee> beeSublist;
		synchronized(waitingQueue) {

			// check whether queue is big enough
			if (waitingQueue.size() >= count) {

				beeSublist = new ArrayList<Bee>(
						waitingQueue.subList(0, count));
				waitingQueue.subList(0, count).clear();

				// if it's empty, give back empty array
			} else if (waitingQueue.isEmpty()) {
				beeSublist = new ArrayList<Bee>();

				// if it's smaller than _count_, adjust _count_
			} else {
				count = waitingQueue.size();
				beeSublist = new ArrayList<Bee>(waitingQueue.subList(0, count));
				waitingQueue.subList(0, count).clear();
			}
		}

		return beeSublist;
	}

	public int getWaitingQueueSize() {
		// TODO Auto-generated method stub
		return waitingQueue.size();
	}

	public void waitingQueueAdd(Bee bee) {
		// adds bee to waitingQueue
		synchronized (waitingQueue) {
			waitingQueue.add(bee);	
		}

	}

	public void waitingQueueSetStatus(String status, int position) {
		// sets status at given position of waitingQueue
		synchronized (waitingQueue) {
			if (waitingQueue.size() != 0 ) {
				waitingQueue.get(position).setStatus(status);
				if (status.equals("searching")) {
					waitingQueue.remove(0);
				}
			}
		}
		refreshTableModel();
	}

	public int waitingQueueSize() {
		// returns waitingQueueSize, not synced
		return waitingQueue.size();
	}

	public void setIndexInBeehiveList(int indexOf) {
		// sets index of this beehive in beehive list
		IndexInBeehiveList = indexOf;

	}
	private void refreshTableModel() {
		if (this.world.tableModelBeehives != null) {
			// the setValueAt method fires out a change event to the table ->
			// table is refreshed in the gui -> new number is shown.
			this.world.tableModelBeehives.setValueAt(
					this.getWaitingQueueSize(),
					this.IndexInBeehiveList, 4);
		}
	}

	/**
	 * @return the bees
	 */
	public LinkedList<Bee> getBees() {
		return this.world.getBees();
	}

	/**
	 * @param bees the bees to set
	 */
	public void setBees(LinkedList<Bee> bees) {
		this.world.setBees(bees);
	}

	/**
	 * @return the positionX
	 */
	public int getPositionX() {
		return positionX;
	}

	/**
	 * @param positionX the positionX to set
	 */
	public void setPositionX(int positionX) {
		this.positionX = positionX;
	}

	/**
	 * @return the positionY
	 */
	public int getPositionY() {
		return positionY;
	}

	/**
	 * @param positionY the positionY to set
	 */
	public void setPositionY(int positionY) {
		this.positionY = positionY;
	}

	/**
	 * @return the food
	 */
	public double getFood() {
		return food;
	}

	/**
	 * @param food the food to set
	 */
	public void setFood(double food) {
		this.food = food;
	}

	public void setAlive(Boolean alive){
		if (!alive) {
			synchronized (this.alive) {
				this.world.Beehives.remove(this);
				this.world.setNumOfBeehives(this.world.getNumOfBeehives() - 1);
			}
			this.alive = alive;
		}
	}

}
