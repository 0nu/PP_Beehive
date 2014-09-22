package sources;

import hive.Bee;
import hive.Beehive;

import java.util.ArrayList;

/**
 * Interface for interaction bee <-> source.
 * 
 * @author ole
 * 
 */
public interface SourceInterface {

	/**
	 * The bee takes some food from the source.
	 * 
	 * @param source
	 *            source to take food from
	 * @param bee
	 *            bee that takes food from the source
	 */
	public void getStuff(Source source, Bee bee);

	/**
	 * Adds the bee to the waiting queue. This probably should be in the beehive
	 * interface.
	 * 
	 * @param beehive
	 *            the beehive to add the bee to the waiting queue
	 * @param bee
	 *            the bee to add to the waiting queue
	 */
	// TODO: -> beehive interface
	public void addToQueue(Beehive beehive, Bee bee);

	/**
	 * Removes the bee from the waiting queue. -> beehive interface ??
	 * 
	 * @param beehive
	 * @param bee
	 */
	// TODO: -> beehive interface
	public void removeFromQueue(Beehive beehive, Bee bee);

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
	public ArrayList<Bee> removeFromQueue(Beehive beehive, int count);
}
