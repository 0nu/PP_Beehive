package hive;

import java.io.Serializable;
import java.util.Random;
//import java.util.LinkedList;
import java.util.ArrayList;

import sources.Source;
import sources.SourceInterface;

/**
 * The bee - lots of information is stored here. Each bee object is meant to be
 * run as single thread, as there are lots of actions, interactions, decisions
 * and death (not implemented yet).sdfcc
 * 
 * @author ole
 * 
 */

// TODO: Death of the bee. still.
public class Bee implements Runnable, SourceInterface, BeehiveInterface,
		Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8026916698774991257L;
	private int actualX;
	private int actualY;
	int age;
	private boolean alive;
	boolean atHome;
	private Bee bee;
	private Beehive beehive;
	int food;
	boolean hasKnowledge;
	int homeX;
	int homeY;
	int knowledge;
	Beehive ownHive;
	Random rand;
	Source source;
	double sourceQual;
	double sourceSize;
	String sourceType;
	int sourceX;
	int sourceY;
	private String status;
	ArrayList<Bee> waitingQueue;
	World world;

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
		this.homeX = ownHive.getPositionX();
		this.homeY = ownHive.getPositionY();
		this.actualX = ownHive.getPositionX();
		this.actualY = ownHive.getPositionY();
		this.atHome = true;
		this.hasKnowledge = false;
		this.age = 100;
		this.ownHive = ownHive;
		this.status = "waiting";
		this.world = world;
		this.rand = new Random();
		this.beehive = ownHive;
		this.setAlive(true);
	}

	@Override
	public void addToQueue(Beehive beehive, Bee bee) {
		// Add submitted bee to submitted beehive.waitingQueue

		beehive.waitingQueueAdd(bee);

		refreshTableModel();
	}

	/**
	 * Bee has knowledge and arrived at source -> Get food
	 */
	private void arrived() {
		foundSth();
	}

	/**
	 * bien is at home and gives other bees information about trach better
	 * quality -> longer dance bigger size of tracht -> longer dance less food
	 * in beehive -> longer dance (not implemented) far away -> shorter dance
	 */
	private void dance() {
		int count = 6;
		// compute the distance between beehive and tracht
		int diffX = this.sourceX - this.homeX;
		int diffY = this.sourceY - this.homeY;
		double distance = (int) Math.sqrt(diffX * diffX + diffY * diffY);

		// better quality -> higher value
		// max(size) = 10.000
		// max(quality) = 100
		// max(distance) = depending on size of world AND/OR bee.max(flight)
		// TODO: create constants for source quality etc

		// this shouldn't get bigger than 100, so we could use that as
		// probability
		double overallQuality = this.sourceQual / 100 + this.sourceSize / 10000
				- distance / 6000;
		// take the first 6 bees of the queue
		// dance.queue.size() may be smaller than 6
		ArrayList<Bee> danceQueue = this.removeFromQueue(beehive, count);

		ArrayList<Bee> removeQueue = new ArrayList<Bee>();
		for (int k = 1; k < 100 * overallQuality; k++) {

			for (Bee b : danceQueue) {
				if (rand.nextInt(100) > 96) {
					b.sourceQual = this.sourceQual;
					b.sourceSize = this.sourceSize;
					b.sourceType = this.sourceType;
					b.sourceX = this.sourceX;
					b.sourceY = this.sourceY;
					b.source = this.source;
					b.setStatus("starting");

					// I can't change danceQUeue at this moment because i iter
					// through it
					removeQueue.add(b);
				}

			}

			// now we can remove the bees that are already on their way to the
			// source from the danceQueue
			for (Bee b : removeQueue) {
				danceQueue.remove(b);

				// if there's no Bee in the waitingqueue, we will get a new
				// ArrayList<Bee> with .size() == 0.
				ArrayList<Bee> nextBee = removeFromQueue(b.ownHive, 1);
				if (nextBee.size() > 0) {
					danceQueue.add(nextBee.get(0));
				}
			}

			removeQueue.clear();

		}
		for (Bee b : danceQueue) {
			synchronized (b) {
				b.setStatus("waiting");
				this.beehive.waitingQueueAdd(b);
			}
		}
		this.setStatus("starting");

	}

	/*
	 * Eat something from time to time. This should only happen if the bee is at
	 * home.
	 */
	private void eat() {
		// when a bee is born, she stays at home and eats
		do {

			try {
				while (!world.isStartModel() && this.alive) {
					Thread.sleep(500);
				}
				Thread.sleep((int) Math.round(900 * getWaitTime())); // 1000
																		// milliseconds
																		// is
																		// one
																		// second.
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}

			// this.eat(this.beehive);

			// ... until status is changed
		} while ((this.getStatus().equals("waiting")) && this.alive);

		// what do next? depending on the status.

	}

	@Override
	public void eat(Beehive beehive) {
		// just a little helper to run eat() synced
		this.ownHive.eat();
	}

	/**
	 * Flying to a destination
	 * 
	 * @param destX
	 *            destination x-point
	 * @param destY
	 *            destination y-point
	 * @param destination
	 *            destination type {"home", "getFood",...}
	 */
	private void flight(int destX, int destY, String destination) {

		double diffX = destX - this.actualX;
		double diffY = destY - this.actualY;
		double flightX = this.actualX;
		double flightY = this.actualY;

		// we need the shortest way between Bee and destination
		double length = Math.sqrt(diffX * diffX + diffY * diffY);

		// and now divide that up by the way a bee flights during 1 timeperiod
		int steps = (int) Math.round(length / 2);
		if (steps == 0) {
			steps = 1;
		}
		// and now divide the two differences by step count
		diffX = diffX / steps;
		diffY = diffY / steps;

		for (int i = 1; i < steps; i++) {
			flightX = flightX + diffX;
			flightY = flightY + diffY;
			this.actualX = (int) flightX;
			this.actualY = (int) flightY;
			try {
				while (!world.isStartModel() && this.alive) {
					Thread.sleep(500);
				}
				Thread.sleep((int) Math.round(100 * getWaitTime())); // 1000
																		// milliseconds
																		// is
																		// one
																		// second.
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}

			if (destination == "searching") {
				foundSth();
				if (this.getStatus().equals("full")) {
					break;
				}
			}
		}
		/*
		 * System.out.println("actx: " + this.actualX + ", destX: " + destX +
		 * ", diff: " + (this.actualX - destX)); System.out.println("acty: " +
		 * this.actualY + ", destY: " + destY + ", diff: " + (this.actualY -
		 * destY)); System.out.println("diffx: " + diffX + ", diffy: " + diffY +
		 * ", steps:" + steps);
		 */

		/*
		 * this.actualX = destX; this.actualY = destY;
		 */

		// there are several possibilities what to do next, depending on the
		// _destination_ string submitted:
		switch (destination) {
		case "searching":
			break;

		case "getFood":
			this.setStatus("arrived");
			break;

		case "home":
			this.setStatus("arrivedathome");
			break;
		}

	}

	/**
	 * Did the bee hit a source?
	 */
	private void foundSth() {
		// TODO: badly coded

		Source foundSource = this.world.hitSource(this.actualX, this.actualY);
		if (foundSource != null) {
			// System.out.println("src_X: " + foundSource.x + ", src_Y: " +
			// foundSource.y + ", x: " + this.actualX + ", y: " + this.actualY);
			this.actualX = foundSource.x;
			this.actualY = foundSource.y;
			this.getStuff(foundSource, this);
		} else if (getStatus().equals("arrived")) {
			setStatus("searching");
		}

	}

	/**
	 * @return the actualX
	 */
	public int getActualX() {
		return actualX;
	}

	/**
	 * @return the actualY
	 */
	public int getActualY() {
		return actualY;
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
			this.bee.setStatus("searching");
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

	/**
	 * @return the wait time, depending on world speed
	 */
	private double getWaitTime() {
		// Returns WaitTime factor depending on worldSpeed
		// init value is 40
		double factor = 20;
		factor = factor / this.world.getWorldSpeed();
		return factor;
	}

	@Override
	public void giveStuff(Beehive beehive, Bee bee) {
		// +1 on food for submitted beehive, remove food from bee

		synchronized (this.ownHive) {
			if (beehive.getFood() < beehive.getSize()) {
				beehive.setFood(beehive.getFood() + 1);
				bee.food = 0;
			} else {
				bee.setStatus("waiting");
				this.bee.removeKnow();
			}
		}
	}

	/**
	 * Grab food from source and go back home
	 */
	private void goBackHome() {
		this.setStatus("BackHome");
		flight(this.homeX, this.homeY, "home");
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
		for (Bee b : beeSublist) {
			b.setStatus("gettingInformation");
		}

		refreshTableModel();
		return beeSublist;
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

	/**
	 * Run method, go to switcher
	 */
	@Override
	public void run() {
		// main function for bee behaviour
		//

		switcher();

	}

	/**
	 * Search for sources.
	 */
	private void search() {
		// TODO: better searching, only 3kms away from beehive
		this.atHome = false;
		setStatus("searching");

		do {
			int xNext = this.actualX - this.rand.nextInt(5) + 2;
			int yNext = this.actualY - this.rand.nextInt(5) + 2;

			if (xNext > 0 && xNext < this.beehive.world.getWidth()) {
				this.actualX = xNext;
			}

			if (yNext > 0 && yNext < this.beehive.world.getHeight()) {
				this.actualY = yNext;
			}
			/*
			 * this.actualX = this.actualX + this.rand.nextInt(10) - 5;
			 * this.actualY = this.actualY + this.rand.nextInt(10) - 5;
			 */
			try {
				while (!world.isStartModel()) {
					Thread.sleep(500);
				}
				Thread.sleep((int) Math.round(500 * getWaitTime())); // 1000
																		// milliseconds
																		// is
																		// one
																		// second.
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
			foundSth();

		} while (this.getStatus().equals("searching"));
	}

	/**
	 * @param actualX
	 *            the actualX to set
	 */
	public void setActualX(int actualX) {
		this.actualX = actualX;
	}

	/**
	 * @param actualY
	 *            the actualY to set
	 */
	public void setActualY(int actualY) {
		this.actualY = actualY;
	}

	/**
	 * @param alive
	 *            alive flag
	 */
	public void setAlive(boolean alive) {
		if (!alive) {
			while (this.getStatus().equals("gettingInformation")) {
				try {
					Thread.currentThread();
					Thread.sleep(500);
				} catch (InterruptedException a) {

				}
			}
			switch (this.getStatus()) {
			case "waiting":
				removeFromQueue(ownHive, this);
				break;
			}
			this.alive = alive;

		} else {
			this.alive = alive;
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
	 * Bee is at home, but has information about source and wants to get the
	 * food.
	 */
	private void starting() {
		this.atHome = false;
		this.actualX = this.actualX + (rand.nextInt(6) - 3);
		this.actualY = this.actualY + (rand.nextInt(6) - 3);
		flight(this.sourceX, this.sourceY, "getFood");

	}

	/**
	 * When status of bee is changed, we should always get back here. Depending
	 * on the status field of the bee, the next method to be run is chosen.
	 */
	private void switcher() {
		// this method is called every time the status changes.

		while (this.alive) {
			if (world.isStartModel()) {
				switch (this.getStatus()) {
				case "arrived":
					arrived();
					break;
				case "waiting":
					this.ownHive.waitingQueueAdd(this);
					eat();
					break;
				case "searching":
					// let her fly to a random start point
					// this.removeFromQueue(this.beehive, actualBee);
					flight(this.rand.nextInt(this.beehive.world.getWidth()),
							this.rand.nextInt(this.beehive.world.getHeight()),
							"searching");
					/*
					 * if (this.getStatus() == "searching") { search(); }
					 */
					break;
				case "starting":
					starting();
					break;
				case "empty":
					search();
					break;
				case "foundSth":
					foundSth();
					break;
				case "full":
					goBackHome();
					break;
				case "arrivedathome":
					this.actualX = this.homeX;
					this.actualY = this.homeY;
					this.atHome = true;
					this.giveStuff(this.beehive, this);
					if (this.getStatus() == "waiting") {
						break;
					} else {

						this.setStatus("startDancing");
						break;
					}

				case "startDancing":
					dance();
					break;

				}
			}
			try {
				Thread.currentThread();
				Thread.sleep(500);
			} catch (InterruptedException a) {

			}
		}
	}
}
