package sources;

import hive.World;

import java.io.Serializable;
import java.util.Random;

/**
 * A tree, extends source. 
 * @author ole
 *
 */
public class Tree extends Source implements Serializable {

	private static final long serialVersionUID = -2921153093843445284L;
	private World world;


	/**
	 * @param x x-value
	 * @param y y-value
	 * @param world the world this tree belongs to
	 */
	public Tree(int x, int y, World world) {
		this.rand = new Random();
		this.x = x;
		this.y = y;
		this.world = world;
		this.updateCount = updateCount;
		this.quality = this.rand.nextInt(9) + 1;
		this.size = this.rand.nextInt(10000);
		maxsize = this.size;
		this.recovery = this.rand.nextInt(100) + 1;
		setAlive(true);
		this.rand = new Random();
	}

	@Override
	public void run() {
		addToSourceMap(world);
		grow(world);

	}

}
