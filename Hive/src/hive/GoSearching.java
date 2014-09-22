package hive;

import java.util.ArrayList;

/**
 * The class sets most of the actions of the bees. Watch out for endless
 * structure.
 * 
 * @author ole
 * 
 */
public class GoSearching implements Runnable {

	Beehive beehive;
	private Bee actualBee;

	/**
	 * Constructor, nothing hiere
	 * 
	 * @param bee
	 *            bee object to take care of
	 */
	GoSearching(Bee bee) {
		this.actualBee = bee;
		this.beehive = bee.ownHive;

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
	 * When status of bee is changed, we should always get back here. Depending
	 * on the status field of the bee, the next method to be run is chosen.
	 */
	private void switcher() {
		// this method is called every time the status changes.

		while (true) {
			switch (this.actualBee.getStatus()) {
			case "arrived":
				arrived();
				break;
			case "waiting":
				this.actualBee.ownHive.waitingQueueAdd(this.actualBee);
				eat();
				break;
			case "searching":
				// let her fly to a random start point
				//this.actualBee.removeFromQueue(this.beehive, actualBee);
				this.actualBee = flight(
						this.actualBee.rand.nextInt(this.beehive.world.width),
						this.actualBee.rand.nextInt(this.beehive.world.height),
						"searching");
				if (this.actualBee.getStatus() == "searching") {
					search();
				}
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
				this.actualBee.actualX = this.actualBee.homeX;
				this.actualBee.actualY = this.actualBee.homeY;
				this.actualBee.atHome = true;
				this.actualBee.giveStuff(this.beehive, this.actualBee);
				if (this.actualBee.getStatus() == "waiting") {
					break; 
				} else {

					this.actualBee.setStatus("startDancing");
					break;
				}
				
			case "startDancing":
				dance();
				break;

			}
		}
	}

	/**
	 * Bee has knowledge and arrived at source -> Get food
	 */
	private void arrived() {

		this.actualBee.getStuff(this.actualBee.source, this.actualBee);

	}

	/**
	 * Search for sources.
	 */
	private void search() {
		// TODO: better searching, only 3kms away from beehive
		this.actualBee.atHome = false;
		actualBee.setStatus("searching");


		do {
			int xNext = this.actualBee.actualX
					- this.actualBee.rand.nextInt(11) + 5;
			int yNext = this.actualBee.actualY
					- this.actualBee.rand.nextInt(11) + 5;

			if (xNext > 0 && xNext < this.beehive.world.width) {
				this.actualBee.actualX = xNext;
			}

			if (yNext > 0 && yNext < this.beehive.world.height) {
				this.actualBee.actualY = yNext;
			}
			/*
			 * this.actualBee.actualX = this.actualBee.actualX +
			 * this.actualBee.rand.nextInt(10) - 5; this.actualBee.actualY =
			 * this.actualBee.actualY + this.actualBee.rand.nextInt(10) - 5;
			 */
			try {
				Thread.currentThread();
				Thread.sleep(500); // 1000 milliseconds is one
				// second.
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
			foundSth();

		} while (this.actualBee.getStatus().equals("searching"));
	}

	/*
	 * Eat something from time to time. This should only happen if the bee is at
	 * home.
	 */
	private void eat() {
		// when a bee is born, she stays at home and eats
		do {

			try {
				Thread.sleep(900); // 1000 milliseconds is one second.
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}

			this.actualBee.eat(this.beehive);

			// ... until status is changed
		} while (this.actualBee.getStatus().equals("waiting"));

		// what do next? depending on the status.

	}

	/**
	 * Did the bee hit a source?
	 */
	private void foundSth() {
		// TODO: badly coded


		Source foundSource = this.actualBee.world.hitSource(this.actualBee.actualX,this.actualBee.actualY);
		if (foundSource != null) {
			this.actualBee.actualX = foundSource.x;
			this.actualBee.actualY = foundSource.y;
			this.actualBee.getStuff(foundSource, actualBee);
		}

	}

	/**
	 * Grab food from source and go back home
	 */
	private void goBackHome() {
		this.actualBee.setStatus("BackHome");
		flight(this.actualBee.homeX, this.actualBee.homeY, "home");
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
	 * @return bee object, probably not needed (?)
	 */
	private Bee flight(int destX, int destY, String destination) {
		// TODO: code a better method for shorter flights, return bee - why?
		double diffX = destX - this.actualBee.actualX;
		double diffY = destY - this.actualBee.actualY;

		// we need the shortest way between Bee and destination
		double length = Math.sqrt(diffX * diffX + diffY * diffY);

		// and now divide that up by the way a bee flights during 1 timeperiod
		int steps = (int) (length / 20);
		if (steps == 0) {
			steps = 1;
		}
		// and now divide the two differences by step count
		diffX = (int) diffX / steps;
		diffY = (int) diffY / steps;
		int i;

		for (i = 1; i < steps; i++) {
			this.actualBee.actualX = (int) (this.actualBee.actualX + diffX);
			this.actualBee.actualY = (int) (this.actualBee.actualY + diffY);
			try {
				Thread.currentThread();
				Thread.sleep(1000); // 1000 milliseconds is one
				// second.
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
			if (destination == "search") {
				foundSth();
				if (this.actualBee.getStatus().equals("full")) {
					break;
				}
			}
		}

		// TODO: smarter way of computing pathway, problem is the int vs double
		// thing
		this.actualBee.actualX = destX;
		this.actualBee.actualY = destY;


		// there are several possibilities what to do next, depending on the
		// _destination_ string submitted:
		switch (destination) {
		case "searching":
			break;

		case "getFood":
			this.actualBee.setStatus("arrived");
			break;

		case "home":
			this.actualBee.setStatus("arrivedathome");
			break;

		}

		return actualBee;
	}

	/**
	 * bien is at home and gives other bees information about trach better
	 * quality -> longer dance bigger size of tracht -> longer dance less food
	 * in beehive -> longer dance (not implemented) far away -> shorter dance
	 */
	private void dance() {
		int count = 6;
		// compute the distance between beehive and tracht
		int diffX = this.actualBee.sourceX - this.actualBee.homeX;
		int diffY = this.actualBee.sourceY - this.actualBee.homeY;
		double distance = (int) Math.sqrt(diffX * diffX + diffY * diffY);

		// better quality -> higher value
		// max(size) = 10.000
		// max(quality) = 100
		// max(distance) = depending on size of world AND/OR bee.max(flight)
		// TODO: create constants for source quality etc

		// this shouldn't get bigger than 100, so we could use that as
		// probability
		double overallQuality = this.actualBee.sourceQual / 100
				+ this.actualBee.sourceSize / 10000 - distance / 6000;
		// take the first 6 bees of the queue
		// dance.queue.size() may be smaller than 6
		ArrayList<Bee> danceQueue = this.actualBee.removeFromQueue(beehive,
				count);

		ArrayList<Bee> removeQueue = new ArrayList<Bee>();
		for (int k = 1; k < 100 * overallQuality; k++) {

			for (Bee b : danceQueue) {
				if (actualBee.rand.nextInt(100) > 96) {
					b.sourceQual = this.actualBee.sourceQual;
					b.sourceSize = this.actualBee.sourceSize;
					b.sourceType = this.actualBee.sourceType;
					b.sourceX = this.actualBee.sourceX;
					b.sourceY = this.actualBee.sourceY;
					b.source = this.actualBee.source;
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
				ArrayList<Bee> nextBee = actualBee.removeFromQueue(b.ownHive, 1);
				if (nextBee.size() > 0) {
					danceQueue.add(nextBee.get(0));
				}
			}

			removeQueue.clear();

		}
		for (Bee b : danceQueue) {
			this.beehive.waitingQueueAdd(b);
		}
		this.actualBee.setStatus("starting");

	}

	/**
	 * Bee is at home, but has information about source and wants to get the
	 * food.
	 */
	private void starting() {
		this.actualBee.atHome = false;
		flight(this.actualBee.sourceX, this.actualBee.sourceY, "getFood");

	}
}
