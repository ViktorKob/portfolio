package net.thomas.portfolio.table;

import static java.awt.event.MouseEvent.BUTTON3;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JTable;

public class TableControl<COLUMN extends Enum<COLUMN> & ColumnDefinitionEnum> {
	private final PagingRowDataModel<COLUMN> model;
	private final TableColumnManager<COLUMN> columnManager;
	private final JTable table;

	public TableControl(TableView view, PagingRowDataLoader<COLUMN> loader, Class<COLUMN> columnClass, COLUMN[] columns) {
		table = view.getTable();
		model = new PagingRowDataModel<>(view.getTable(), loader, columns);
		table.setModel(model);
		columnManager = new TableColumnManager<>(table, columnClass, columns);
		table.getTableHeader().setVisible(true);
		table.getTableHeader().addMouseListener(new HeaderMouseListener(columns));
	}

	public JTable getTable() {
		return table;
	}

	public PagingRowDataModel<COLUMN> getModel() {
		return model;
	}

	public void setColumnOrder(List<COLUMN> columnOrder) {
		columnManager.setColumnOrder(columnOrder);
		table.repaint();
	}

	public void hideColumn(COLUMN column) {
		columnManager.setColumnVisibility(column, false);
		table.repaint();
	}

	public void showColumn(COLUMN column) {
		columnManager.setColumnVisibility(column, true);
		table.repaint();
	}

	class HeaderMouseListener extends MouseAdapter {
		private final COLUMN[] columns;

		public HeaderMouseListener(COLUMN[] columns) {
			this.columns = columns;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			final int modelIndex = table.convertColumnIndexToModel(table.columnAtPoint(e.getPoint()));
			final COLUMN column = columns[modelIndex];
			if (e.getButton() == BUTTON3) {
				hideColumn(column);
			}
		}
	}
}