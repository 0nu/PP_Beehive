package hive;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * This is the starting point of the Bee-Project.
 * 
 * @author ole
 * rgfscacsdfecdas
 * yyyxxy
 */
public class startup {

	static double hungry;
	static int numOfBees;
	static int numOfBeehives;
	static int worldX;
	static int worldY;
	static int numOfSources;
	static int numOfTrees;

	public static void main(String[] args) {

		hungry = 0.001; // How much food does each bee take at a time
		numOfBees = 1000;
		numOfBeehives = 1;
		worldX = 500;
		worldY = 600;
		numOfSources = 50;
		numOfTrees = 50; // TODO: check for sinnvolle number

		// this will create the world
		final World myWorld = new World(worldX, worldY, numOfBeehives,
				numOfSources, numOfTrees,hungry);
		myWorld.createSources(numOfTrees);

		// create the beehives as own threads
		for (int i = 0; i < numOfBeehives; i++) {
			// the beehive gets the number of bees to create in it, the number
			// of this beehive (only for naming the object/thread, and the
			// hunger of the bees
			Thread p = new Thread(myWorld.createBeehive(numOfBees, i));
			p.start();

		}

		// following stuff is for the Gui
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame("MyGui");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				MyGui newContentPane = new MyGui(myWorld, frame);
				newContentPane.setOpaque(true); // content panes must be opaque
				frame.setContentPane(newContentPane);

				// Display the window.
				frame.pack();
				frame.setVisible(true);

				// MyGui myGui = new MyGui(myWorld,null);
				// myGui.createAndShowGUI(myWorld);
			}
		});

	}
}
