package sources;

import hive.World;

import java.io.Serializable;
import java.util.Random;

public class Tree extends Source implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2921153093843445284L;
	private World world;

	// constructor
	public Tree(int x, int y,World world) {
		this.rand = new Random();
		this.x = x;
		this.y = y;
		this.world = world;
		this.updateCount = updateCount;

		this.quality = this.rand.nextInt(9) +1;
		this.size = this.rand.nextInt(10000);
		maxsize = this.size;
		this.recovery = this.rand.nextInt(100) + 1;
		setAlive(true);
		this.rand = new Random();
		this.alive = alive;


	}

	public void run() {
		addToSourceMap(world);
		grow(world);
		

	}

	public void removePositionInTrees(int i) {
		// TODO Auto-generated method stub
		
	}




}
