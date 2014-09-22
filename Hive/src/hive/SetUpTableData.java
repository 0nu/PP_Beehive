package hive;


import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class SetUpTableData implements Runnable {
	private World world;
	private JTable jTable;
	private String tableTyp;

	public SetUpTableData(JTable jTable, World world, String string) {
		this.jTable = jTable;
		this.world = world;
		this.tableTyp = string;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		update();
	}

	public void update() {
		// TODO Auto-generated method stub
		DefaultTableModel tableModel = (DefaultTableModel) jTable.getModel();
		tableModel.setRowCount(0);
		switch (tableTyp) {
		case "Trees": {
			for (int t = 0; t < this.world.trees.size(); t++) {
				String[] data = new String[8];

				data[0] = this.world.trees.get(t).getName();
				data[1] = Integer.toString(this.world.trees.get(t).size);
				data[2] = Integer.toString(this.world.trees.get(t).maxsize);
				data[3] = Integer.toString(this.world.trees.get(t).quality);
				data[4] = Integer.toString(this.world.trees.get(t).recovery);
				data[5] = this.world.trees.get(t).type;
				data[6] = Integer.toString((this.world.trees.get(t).x));
				data[7] = Integer.toString((this.world.trees.get(t).y));

				tableModel.addRow(data);
			}
			world.setTableModel(tableModel,tableTyp);
			jTable.setModel(tableModel);
			
			//tableModel.fireTableDataChanged();
		}
		case "Beehives": {
			// TODO: Add support for multiple beehives, this code works only with one!
			for (int t = 0; t < this.world.numOfBeehives; t++) {
				String[] data = new String[5];

				data[0] = this.world.getBeehives().get(t).getName();
				data[1] = Double.toString(this.world.getBeehives().get(t).food);
				data[2] = Integer.toString(this.world.getBeehives().get(t).positionX);
				data[3] = Integer.toString(this.world.getBeehives().get(t).positionY);
				data[4] = Integer.toString(this.world.getBeehives().get(t).waitingQueueSize());
			
				tableModel.addRow(data);
			}
			world.setTableModel(tableModel, tableTyp);
			jTable.setModel(tableModel);
			//tableModel.fireTableDataChanged();
		}
		}
		}
	}

