package hive;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.ArrayList;

/**
 * The Beehive Class. To be run as own thread, as here are lots of information
 * and probably lots of synchronized methods. Plus all the bees.
 * 
 * @author ole
 * 
 */

public class Beehive implements Runnable, Serializable, BeeInterface {

	private static final long serialVersionUID = -5404284857273245055L;
	Boolean alive;
	private double food;
	int IndexInBeehiveList;
	private String name;
	private int positionX;
	private int positionY;
	private int size;
	private ArrayList<Bee> waitingQueue;
	World world;
	private double water;
	private boolean empty;
	private double hunger;


	/**
	 * @param x
	 *            x-value
	 * @param y
	 *            y-value
	 * @param world
	 *            the world it belongs to
	 * @param num
	 *            own number
	 * @param hunger
	 *            hunger of bees
	 */
	Beehive(int x, int y, World world, int num, double hunger) {
		positionX = x;
		positionY = y;
		food = 1000;
		water = 1000;
		this.world = world;
		this.waitingQueue = new ArrayList<Bee>();
		this.size = (int) food;

		this.name = "Beehive " + num;
		this.alive = true;
	}

	/**
	 * Here we reduce the food amount of the beehive
	 * 
	 * @return empty == 1 if beehive is empty, otherweise empty == 0
	 */
	boolean eat() {
		empty = false;
		this.hunger = this.world.getHunger();
		if (this.food >= this.hunger) {
			synchronized (this.alive) {
				if (this.alive) {
					takeFood();
					world.setValue("beehive", this.food,this.IndexInBeehiveList,1);	
				}
			}
		} else {
			empty = true;
		}


		if (this.water >= this.hunger){
			synchronized (this.alive) {
				if (this.alive) {
					takeWater();
					world.setValue("beehive", this.food, this.IndexInBeehiveList, 2);
				}
			}

		} else {
			empty = true;
		}

		// change the tableModel for the gui
		// but only change when the table is already created
		// ... and rand = 10 <- this is ugly but saves some cpu time
		// if (world.tableModelBeehives != null &&
		// this.rand.nextInt(world.getUpdateSpeed() + 1) == 2) {// TODO:
		// tableModelBeehives
		// update
		// more
		// pretty
		// &
		// fast

		// I don't want negative food values
		if (this.food < 0) {
			this.food = 0;
		}
		if (this.water < 0) {
			this.water = 0;
		}
		return empty;
	}

	synchronized private void takeWater() {
		this.food = this.food - this.hunger;
	}

	synchronized private void takeFood() {
		this.water = this.water - this.hunger;
	}

	/**
	 * @return the bees
	 */
	public LinkedList<Bee> getBees() {
		return this.world.getBees();
	}

	/**
	 * @param sourceType 
	 * @return the food
	 */
	public double getFood(String sourceType) {
		double returnValue = 0;
		if (sourceType.equals("water")) {
			returnValue = water;
		} else if (sourceType.equals("tree")) {
			returnValue = food;
		}
		return returnValue;

	}

	/**
	 * Returns the name of this object.
	 * 
	 * @return the name of this object
	 */
	public String getName() {
		// returns Name of Beehive
		return this.name;
	}

	/**
	 * @return the positionX
	 */
	public int getPositionX() {
		return positionX;
	}

	/**
	 * @return the positionY
	 */
	public int getPositionY() {
		return positionY;
	}

	/**
	 * @return max size of beehive
	 */
	public int getSize() {
		// returns MaxSize of Beehive
		return this.size;
	}

	/**
	 * @return waiting queue size
	 */
	public int getWaitingQueueSize() {
		return waitingQueue.size();
	}

	/**
	 * @return wait time, depending on world spped
	 */
	private double getWaitTime() {
		// init value is 40
		double factor = 20;
		factor = factor / this.world.getWorldSpeed();
		return factor;
	}

	/**
	 * Sets value in table model.
	 */
	void refreshTableModel() {
		if (this.world.getTableModel("beehive") != null) {
			// the setValueAt method fires out a change event to the table ->
			// table is refreshed in the gui -> new number is shown.
			//			this.world.tableModelBeehives.setValueAt(
			//					this.getWaitingQueueSize(), this.IndexInBeehiveList, 5);
			this.world.setValue("beehive", this.getWaitingQueueSize(), this.IndexInBeehiveList, 5);
		}
	}

	/**
	 * Run Method.
	 */
	@Override
	public void run() {
		while (this.alive) {
			try {
				while (!world.isStartModel() && (alive)) {
					Thread.sleep(500);
				}
				// System.out.println(getWaitTime());
				Thread.sleep((int) Math.round(900 * getWaitTime())); // 1000
				// milliseconds
				// is
				// one
				// second.
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
			synchronized (this.alive) {
				if (this.alive) {
					sendout();

				}

			}
		}

	}

	private void sendout() {
		if (this.food < (0.9 * this.size)) {
			this.waitingQueueSetStatus("searching", 0);
		}
	}

	/**
	 * Setter for alive flag.
	 * 
	 * @param alive
	 *            alive flag
	 */
	public void setAlive(Boolean alive) {
		if (!alive) {
			synchronized (this.alive) {
				this.world.Beehives.remove(this);
				this.world
				.setNumOfBeehives(this.world.getCountOfBeehives() - 1);
			}
			this.alive = alive;
		}
	}

	/**
	 * @param bees
	 *            the bees to set
	 */
	public void setBees(LinkedList<Bee> bees) {
		this.world.setBees(bees);
	}

	/**
	 * @param sourceType 
	 * @param food
	 *            the food to set
	 */
	public void setFood(String sourceType, double newValue) {
		if (newValue < this.size) {
			if (sourceType.equals("water")) {
				this.water = newValue;
			} else if (sourceType.equals("tree")) {
				this.food = newValue;
			}
		}
	}

	/**
	 * @param indexOf
	 *            sets the index in beehive list. This is for the tablemodel.
	 */
	public void setIndexInBeehiveList(int indexOf) {
		// sets index of this beehive in beehive list
		IndexInBeehiveList = indexOf;

	}

	/**
	 * @param positionX
	 *            the positionX to set
	 */
	public void setPositionX(int positionX) {
		this.positionX = positionX;
	}

	/**
	 * @param positionY
	 *            the positionY to set
	 */
	public void setPositionY(int positionY) {
		this.positionY = positionY;
	}

	/**
	 * @param newSize
	 *            new value for size
	 */
	public void setSize(int newSize) {
		// Sets MaxSize of Beehive
		this.size = newSize;
	}

	/**
	 * @param beeToAdd
	 *            the bee to add to the waiting queue
	 */
	@Override
	public void waitingQueueAdd(Bee beeToAdd) {
		// adds bee to waitingQueue
		synchronized (waitingQueue) {
			waitingQueue.add(beeToAdd);
		}
		refreshTableModel();

	}

	/**
	 * @param beeToRemove
	 *            the bee to remove from waiting queue
	 */
	void waitingQueueRemove(Bee beeToRemove) {
		// removes bee from waiting queue
		synchronized (waitingQueue) {
			waitingQueue.remove(beeToRemove);
		}

	}

	/**
	 * @param status
	 *            the new status
	 * @param position
	 *            the position of the bee to change to status
	 */
	private void waitingQueueSetStatus(String status, int position) {
		// sets status at given position of waitingQueue
		synchronized (waitingQueue) {
			if (waitingQueue.size() != 0) {
				waitingQueue.get(position).setStatus(status);
				if (status.equals("searching")) {
					waitingQueue.remove(0);
				}
			}
		}
		refreshTableModel();
	}

	/**
	 * @return waiting queue size
	 */
	public int waitingQueueSize() {
		// returns waitingQueueSize, not synced
		return waitingQueue.size();
	}

	/**
	 * @param count
	 *            number of bees to try to get from the sublist
	 * @return count bees or less it waiting queue doesn't have that much bees
	 *         in it
	 */
	ArrayList<Bee> waitingQueueSublist(int count) {
		// returns sublist of waiting queue and removes the returned bees
		ArrayList<Bee> beeSublist;
		synchronized (waitingQueue) {

			// check whether queue is big enough
			if (waitingQueue.size() >= count) {

				beeSublist = new ArrayList<Bee>(waitingQueue.subList(0, count));
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

	public double getRatio(String sourceType) {
		// TODO Auto-generated method stub
		double returnValue = 0;
		if ((this.food <= 0) && (this.water <= 0)) {
			returnValue = 1;
		} else if (sourceType.equals("tree")) {
			if (this.food <= 0) {
				returnValue = 10;
			} else {
				returnValue = this.water / this.food;
			}
		} else if (sourceType.equals("water")) {
			if (this.water <= 0) {
				returnValue = 10;
			} else {
				returnValue = this.food / this.water;
			}
		}
		//		System.out.println("Wanted: " + sourceType + ", " + returnValue + ", food: " + this.food + ", water: " + this.water);
		return returnValue;
	}

}
