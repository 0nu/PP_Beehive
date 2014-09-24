package hive;

public class BeeSearch implements Runnable {
	Beehive beehive;

	BeeSearch(Beehive beehive) {
		this.beehive = beehive;

	}

	@Override
	public void run() {
		// bees from waiting to searching.
		// TODO change values for BeeSearch start to realistic ones
		while (true) {
			if (this.beehive.getFood() < 990) {
				this.beehive.waitingQueueSetStatus("searching",0);
				// wait a little
				try {
					while (!this.beehive.world.isStartModel()) {
						Thread.sleep(500);
					}
					Thread.sleep(1000); // 1000 milliseconds is one second.
				} catch (InterruptedException ex) {
					Thread.currentThread().interrupt();
				}
			}
		}
	}
	/**
	 * @param args
	 */
}

