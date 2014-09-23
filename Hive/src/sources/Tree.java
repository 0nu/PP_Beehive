package sources;

import hive.World;

import java.util.Random;

public class Tree extends Source {

	/**
	 * @param args
	 */

	Random rand;
	private World world;

	// constructor
	public Tree(int x, int y,World world) {
		this.rand = new Random();
		this.x = x;
		this.y = y;
		this.world = world;

		this.quality = this.rand.nextInt(10);
		this.size = this.rand.nextInt(10000);
		maxsize = this.size;
		this.recovery = this.rand.nextInt(100) + 1;
		setAlive(true);


	}

	public void run() {
		addToSourceMap(world);
		grow(world);


	}

	public void removePositionInTrees(int i) {
		// TODO Auto-generated method stub
		
	}




}
