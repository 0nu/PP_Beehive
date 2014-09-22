package hive;

import java.lang.management.ManagementFactory;
/**
 * This class only shows some information about a hive. Needed?
 * @author olenhjx x
 *
 */
public class ShowFood implements Runnable {

	Beehive beehive;

	ShowFood(Beehive beehive) {

		this.beehive = beehive;
	}

	/**
	 * run method.
	 */
	@Override
	public void run() {
		// This is for showing the food of a beehive from time to time
		while (true) {
			System.out.println(Thread.currentThread().getName() + " has " + this.beehive.food
					+ " food.");
			System.out.println("Threads in Group: " + java.lang.Thread.activeCount());
			System.out.println("Threads overall: "+ ManagementFactory.getThreadMXBean().getThreadCount());
			try {
				Thread.currentThread();
				Thread.sleep(15000); // 1000 milliseconds is one
										// second.
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
		}
	}
	

}
