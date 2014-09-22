package sources;

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
		while (true) {
			if (this.size < this.maxsize) {
				this.size = this.size + this.recovery;
				if (this.size > this.maxsize) {
					this.size = this.maxsize;
				}
			}
			

			try {
				Thread.currentThread();
				Thread.sleep(10000); // 1000 milliseconds is one
										// second.
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
		}
	}

	/**
	 * When a be arrives, she takes some food/water/whatever.
	 * @return foodreturn == 1 if source != empty
	 */
	public int getFood() {
		int foodreturn;

		synchronized (this) {
			if (this.size >= 1) {
				foodreturn = 1;
				this.size--;
				if (world.getTableModelTrees() != null) {
					world.getTableModelTrees().setValueAt(
							Integer.toString(this.size),
							ListIndex, 1);
				}
			} else {
				foodreturn = 0;
				//System.out.println(this + " is empty");
			}
			return foodreturn;

		}
	}
	void addToSourceMap (World world) {
		this.world = world;
		this.world.addToSourceMap(this.x,this.y,this.size, this);
		
	}
	
	public void setPositionInTrees(int indexOf) {
		// sets index information of trees list -> no need for a time consuming search on every update
		ListIndex = indexOf;
		
	}
}
