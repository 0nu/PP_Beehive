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

	/**
	 * @param x x-value
	 * @param y y-value
	 * @param world the world this tree belongs to
	 */
	public Tree(int x, int y, World world) {
		this.rand = new Random();
		this.setX(x);
		this.setY(y);
		this.world = world;
		this.setQuality(this.rand.nextInt(9) + 1);
		this.setSize(this.rand.nextInt(10000));
		setMaxsize(this.getSize());
		this.setRecovery(this.rand.nextInt(100) + 1);
		setAlive(true);
		this.world = world;
		setType("tree");
	}

	
}
