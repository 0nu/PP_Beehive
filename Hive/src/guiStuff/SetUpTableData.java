package guiStuff;

import java.awt.Dimension;
import java.util.LinkedList;

import hive.World;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import sources.Source;
import sources.Water;

/**
 * This sets up the table data for the different tables. 
 * 
 * @author ole
 *
 */
class SetUpTableData implements Runnable {
	private World world;
	private JTable jTable;
	private String tableTyp;

	/**
	 * @param jTable
	 *            table to set up
	 * @param world
	 *            the world the table belongs to
	 * @param string
	 *            "Beehives" or "Trees"
	 */
	SetUpTableData(JTable jTable, World world, String string) {
		this.jTable = jTable;
		this.world = world;
		this.tableTyp = string;
	}

	@Override
	public void run() {
		update();
	}

	/**
	 * This redraws the table completely.
	 */
	void update() {
		DefaultTableModel tableModel = (DefaultTableModel) jTable.getModel();
		tableModel.setRowCount(0);
		LinkedList<Source> list = null;
		switch (tableTyp) {
		case "Trees": 
			list = this.world.getTrees();
			for (Source t : list) {
				String[] data = new String[8];

				data[0] = t.getName();
				data[1] = Integer.toString(t.getSize());
				data[2] = Integer.toString(t.getMaxsize());
				data[3] = Integer.toString(t.getQuality());
				data[4] = Integer.toString(t.getRecovery());
				data[5] = t.getType();
				data[6] = Integer.toString(t.getX());
				data[7] = Integer.toString(t.getY());
				tableModel.addRow(data);
			}
			world.setTableModel(tableModel, tableTyp);
			jTable.setModel(tableModel);

			break;
		case "Waters": 
			list = this.world.getWaters();
			for (Source w : list) {
				String[] data = new String[8];
				data[0] = w.getName();
				data[1] = Integer.toString(w.getSize());
				data[2] = Integer.toString(w.getMaxsize());
				data[3] = Integer.toString(w.getQuality());
				data[4] = Integer.toString(w.getRecovery());
				data[5] = w.getType();
				data[6] = Integer.toString(w.getX());
				data[7] = Integer.toString(w.getY());
				tableModel.addRow(data);
			}
			world.setTableModel(tableModel, tableTyp);
			jTable.setModel(tableModel);

			break;
		case "Beehives": 
			// TODO: Add support for multiple beehives, this code works only
			// with one!
			for (int t = 0; t < this.world.getCountOfBeehives(); t++) {
				String[] data = new String[6];
				data[0] = this.world.getBeehives().get(t).getName();
				data[1] = Double.toString(this.world.getBeehives().get(t)
						.getFood("tree"));
				data[2] = Double.toString(this.world.getBeehives().get(t)
						.getFood("water"));
				data[3] = Integer.toString(this.world.getBeehives().get(t)
						.getPositionX());
				data[4] = Integer.toString(this.world.getBeehives().get(t)
						.getPositionY());
				data[5] = Integer.toString(this.world.getBeehives().get(t)
						.waitingQueueSize());
				tableModel.addRow(data);
			}
			world.setTableModel(tableModel, tableTyp);
			jTable.setModel(tableModel);
			break;
		}
	}
}
