package hive;

import java.util.ArrayList;


/**
 * Interface for communication bee <-> beehive.
 * 
 * @author ole
 * 
 */
public interface BeehiveInterface {

	/**
	 * Eat some food.
	 * 
	 * @param beehive
	 *            beehive to take some food from
	 */
	public void eat(Beehive beehive);

	/**
	 * Put food into beehive.
	 * 
	 * @param beehive
	 *            the beehive to put food into
	 * @param bee
	 *            the bee that puts food into the beehive
	 */
	public void giveStuff();
	
	/**
	 * Remove a given number of bees from the waiting queue. -> beehvie
	 * interfac?
	 * 
	 * @param beehive
	 *            the beehive to remove the bees from
	 * @param count
	 *            the number of bees to remove from the queue
	 * @return arraylist of removed bees
	 */
	// TODO: -> beehive interface
	public ArrayList<Bee> removeFromQueue(int count);

	void removeFromQueue(Bee bee);

	void addToQueue(Bee bee);

}
