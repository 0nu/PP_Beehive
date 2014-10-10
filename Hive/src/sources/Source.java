package sources;

import java.io.Serializable;
import java.util.Random;

import javax.swing.table.TableModel;

import hive.World;

/**
 * Parent method for the trees, rivers, whatever to come.
 * 
 * @author ole
 * 
 */
public class Source extends Thread implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4812401940274190773L;
	private boolean alive;
	private int foodreturn;
	private int ListIndex;
	private int maxsize;
	private int quality;
	protected Random rand;
	private int recovery;
	int size;
	private String type;
	private int updateCount;
	public World world;
	private int x;
	private int y;
	TableModel thisTableModel;




	void addToSourceMap() {
		this.world.addToSourceMap(this);

	}

	/**
	 * When a be arrives, she takes some food/water/whatever.
	 * 
	 * @return foodreturn == 1 if source != empty
	 */
	public int getFood() {
		if (this.size >= 1) {
			foodreturn = 1;
			synchronized (this) {
				if (this.size >= 1) {
					this.size--;
				}
			}

			this.updateCount++;
			if ((world.getTableModel(type) != null)
					&& (this.updateCount > (world.getUpdateSpeed() - 100)
							* (-1)) && world.getUpdateSpeed() != 0) {
				this.updateCount = 0;
				try {
					world.getTableModel(type).setValueAt(
							Integer.toString(this.size), ListIndex, 1);
				} catch (ArrayIndexOutOfBoundsException e) {
					foodreturn = 0;
				}
			}
		} else {
			foodreturn = 0;
		}
		return foodreturn;
	}

	private double getWaitTime() {
		// Returns WaitTime factor depending on worldSpeed
		// init value is 40
		double factor = 20;
		factor = factor / this.world.getWorldSpeed();
		return factor;
	}

	/**
	 * A tree grows, a river get fresh food when something is taken, etc
	 * 
	 * @param world
	 *            the world we live in
	 */
	void grow() {
		// this is the superclass for all the possible types of foodstuff
		// what information does it need?
		// - position in world X & Y
		//
		while (this.alive) {
			if (this.size < this.maxsize) {
				this.size = this.size + this.recovery;
				if (this.size > this.maxsize) {
					this.size = this.maxsize;
				}
			}

			try {
				while (!world.isStartModel() && this.alive) {
					Thread.sleep(500);
				}
				Thread.sleep((int) Math.round(10000 * getWaitTime())); 
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
		}
	}

	@Override
	public void run() {
		addToSourceMap();
		grow();

	}


	/**
	 * @param alive
	 *            the alive to set
	 */
	public void setAlive(boolean alive) {
		this.alive = alive;
	}

	/**
	 * sets index information of trees list -> no need for a time consuming
	 * search on every update
	 * 
	 * @param indexOf
	 *            index of this tree
	 */
	public void setPosition(int indexOf) {
		ListIndex = indexOf;

	}

	/**
	 * @return the size
	 */
	public int getSize() {
		return size;
	}

	/**
	 * @param size the size to set
	 */
	public void setSize(int size) {
		this.size = size;
	}

	/**
	 * @return the maxsize
	 */
	public int getMaxsize() {
		return maxsize;
	}

	/**
	 * @param maxsize the maxsize to set
	 */
	public void setMaxsize(int maxsize) {
		this.maxsize = maxsize;
	}

	/**
	 * @return the quality
	 */
	public int getQuality() {
		return quality;
	}

	/**
	 * @param quality the quality to set
	 */
	public void setQuality(int quality) {
		this.quality = quality;
	}

	/**
	 * @return the recovery
	 */
	public int getRecovery() {
		return recovery;
	}

	/**
	 * @param recovery the recovery to set
	 */
	public void setRecovery(int recovery) {
		this.recovery = recovery;
	}

	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}

	/**
	 * @param x the x to set
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
}
