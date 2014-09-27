package sources;

import java.util.Random;

import hive.World;

/**
 * Parent method for the trees, rivers, whatever to come.
 * @author ole
 *
 */
public class Source extends Thread {
	public int x;
	public int y;
	public int quality;
	public int size;
	public int maxsize;
	public int recovery;
	public String type;
	private World world;
	private int ListIndex;
	protected boolean alive;
	protected Random rand;
	public int updateCount;
	int foodreturn;


	/**
	 * run method, nothing here.
	 */
	public void run() {
		// this is the superclass for all the possible types of foodstuff
		// what information does it need?
		// - position in world X & Y
		//

	}

	/**
	 * A tree grows, a river get fresh food when something is taken, etc
	 * @param world the world we live in 
	 */
	public void grow(World world) {
		// this is the superclass for all the possible types of foodstuff
		// what information does it need?
		// - position in world X & Y
		//
		this.world = world;
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
				Thread.sleep((int) Math.round(10000 * getWaitTime())); // 1000 milliseconds is one second.
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
		}
	}

	private double getWaitTime() {
		// Returns WaitTime factor depending on worldSpeed
		// init value is 40 
		double factor = 20;
		factor = factor / this.world.getWorldSpeed();
		return factor;
	}

	/**
	 * When a be arrives, she takes some food/water/whatever.
	 * @return foodreturn == 1 if source != empty
	 */
	public int getFood() {
		if (this.size >= 1) {
			foodreturn = 1;
			synchronized (this) {
				if (this.size >=1){
					this.size--;
				}
			}
			this.updateCount++;
			if ((world.getTableModelTrees() != null) && (this.updateCount > (world.getUpdateSpeed() -100) * (-1)) && world.getUpdateSpeed() != 0) {
				this.updateCount = 0;
				try {
					world.getTableModelTrees().setValueAt(
							Integer.toString(this.size),
							ListIndex, 1);
				}
				catch (ArrayIndexOutOfBoundsException e){
					foodreturn = 0;
				} 
			} 
		} else {
			foodreturn = 0;
		}
		return foodreturn;
	}

void addToSourceMap (World world) {
	this.world = world;
	this.world.addToSourceMap(this.x,this.y,this.size, this);

}

public void setPositionInTrees(int indexOf) {
	// sets index information of trees list -> no need for a time consuming search on every update
	ListIndex = indexOf;

}

/**
 * @return the alive
 */


/**
 * @param alive the alive to set
 */
public void setAlive(boolean alive) {
	this.alive = alive;
}
}
