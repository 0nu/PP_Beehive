import java.util.Random;


public class Treetest {

	private Random rand;
	private static int y;
	private int quality;
	private int size;
	private int maxsize;
	private int recovery;
	private static int x;
	/**
	 * @param args
	 */
	
	Treetest(int x, int y) {
		this.rand = new Random();
		this.x = x;
		this.y = y;

		this.quality = this.rand.nextInt(10);
		this.size = this.rand.nextInt(10000);
		this.maxsize = this.size;
		this.recovery = this.rand.nextInt(100);
		System.out.println(Thread.currentThread().getName() + " Position X:"
				+ x + " Y: " + y);
		System.out.println("size: " + this.size);
		System.out.println("maxsize: " + this.maxsize);
		this.size = 1000;
		System.out.println("size changed to : " + this.size);
		System.out.println("maxsize: " + this.maxsize);
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(Thread.currentThread().getName() + " Position X:"
				+ x + " Y: " + y);
		Treetest bh = new Treetest(5,4);
	}
	
public void getFood() {
	synchronized (this) {
		this.size--;
	}
}
}
