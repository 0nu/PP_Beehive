import guiStuff.MyGui;
import hive.World;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
/**
 * This is the starting point of the Bee-Project.
 *
 * @author ole
 *
 */
public class Startup {
	static double hungry;
	static int numOfBees;
	static int numOfBeehives;
	static int worldX;
	static int worldY;
	static int numOfSources;
	static int numOfTrees;
	private static int numOfWaters;
	public static void main(String[] args) {
		hungry = 0.001; // How much food does each bee take at a time
		numOfBees = 2050;
		numOfBeehives = 1;
		worldX = 1000;
		worldY = 600;
		numOfTrees = 25; // TODO: this as command line input.
		numOfWaters = 25;
		// this will create the world
		final World myWorld = new World(worldX, worldY, numOfBees,
				numOfBeehives, hungry);
		myWorld.createSources(numOfTrees, "Trees");
		myWorld.createSources(numOfWaters, "Waters");
		// create the beehives and bees
		for (int i = 0; i < numOfBeehives; i++) {
			// the beehive gets the number of bees to create in it, the number
			// of this beehive (only for naming the object/thread, and the
			// hunger of the bees
			myWorld.createBeehive(i);
		}
		myWorld.startSources();
		myWorld.startBeehives();
		myWorld.createBees(numOfBees);
		myWorld.startBees();
		// following stuff is for the Gui
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new JFrame("MyGui");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				MyGui newContentPane = new MyGui(myWorld, frame);
				newContentPane.setOpaque(true); // content panes must be opaque
				frame.setContentPane(newContentPane);
				// Display the window.
				frame.setExtendedState(frame.MAXIMIZED_BOTH);
				frame.pack();
				frame.setVisible(true);
				
				// MyGui myGui = new MyGui(myWorld,null);
				// myGui.createAndShowGUI(myWorld);
			}
		});
	}
}