package hive;

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
	public void giveStuff(Beehive beehive, Bee bee);
}
