package hive;

public interface BeeInterface {

	/**
	 * @param beeToAdd
	 *            the bee to add to the waiting queue
	 */
	public abstract void waitingQueueAdd(Bee beeToAdd);

}