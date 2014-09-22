package hive;

import java.util.Random;

public class Tree extends Source {

	/**
	 * @param args
	 */

	Random rand;
	private World world;

	// constructor
	Tree(int x, int y,World world) {
		this.rand = new Random();
		this.x = x;
		this.y = y;
		this.world = world;

		this.quality = this.rand.nextInt(10);
		this.size = this.rand.nextInt(10000);
		maxsize = this.size;
		this.recovery = this.rand.nextInt(100) + 1;


	}

	public void run() {
		addToSourceMap(world);
		grow(world);


	}




}
