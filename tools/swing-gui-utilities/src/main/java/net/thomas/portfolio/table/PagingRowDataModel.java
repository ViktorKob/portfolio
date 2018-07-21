package net.thomas.portfolio.table;

import java.awt.Rectangle;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

public class PagingRowDataModel<COLUMN extends Enum<COLUMN> & ColumnDefinitionEnum> extends AbstractTableModel
		implements PagingDataChangeListener<COLUMN>, RowDataChangeListener<COLUMN> {

	private static final long serialVersionUID = 1L;

	private final JTable table;
	private final COLUMN[] columns;
	private final RowBasedDataSet<COLUMN> data;
	private final AtomicInteger changeCounter;

	private final PagingRowDataLoader<COLUMN> loader;

	private Rectangle lastVisibleArea;
	private int lastSelectedRow;

	public PagingRowDataModel(JTable table, PagingRowDataLoader<COLUMN> loader, COLUMN[] columns) {
		this.table = table;
		this.loader = loader;
		this.columns = columns;
		data = new RowBasedDataSet<>();
		changeCounter = new AtomicInteger();
		loader.addPagingDataChangeListener(this);
		lastVisibleArea = null;
		lastSelectedRow = -1;
	}

	@Override
	public int getRowCount() {
		return data.size() + (loader.hasMorePages() ? 1 : 0);
	}

	@Override
	public int getColumnCount() {
		return columns.length;
	}
	
	@Override
	public String getColumnName(int column) {
		return columns[column].getPrettyName();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (rowIndex < data.size()) {
			return data.getCell(rowIndex, columns[columnIndex]);
		} else {
			lastVisibleArea = table.getVisibleRect();
			if (table.getSelectedRowCount() == 1) {
				lastSelectedRow = table.getSelectedRow() - 1;
			}
			loader.initiatePaging();
			return "?";
		}
	}

	@Override
	public void pagingStarted() {
		changeCounter.incrementAndGet();
	}

	@Override
	public void pagingEnded(List<Row<COLUMN>> newData) {
		changeCounter.decrementAndGet();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				data.update(newData);
				fireTableDataChanged();
				if (lastVisibleArea != null) {
					table.scrollRectToVisible(lastVisibleArea);
					lastVisibleArea = null;
				}
				if (lastSelectedRow != -1) {
					table.getSelectionModel().clearSelection();
					table.getSelectionModel().setSelectionInterval(lastSelectedRow, lastSelectedRow);
					lastSelectedRow = -1;
				}
			}
		});
	}

	@Override
	public boolean startDataChange() {
		return false;
	}

	@Override
	public void endDataChange() {

	}
}