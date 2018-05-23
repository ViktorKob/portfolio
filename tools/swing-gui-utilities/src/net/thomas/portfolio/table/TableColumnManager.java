package net.thomas.portfolio.table;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class TableColumnManager<COLUMN extends Enum<COLUMN> & ColumnDefinitionEnum> {
	private final TableColumnModel model;
	private final Map<COLUMN, TableColumn> columns;
	private final List<COLUMN> order;
	private final Map<COLUMN, Boolean> visibilityMap;

	public TableColumnManager(JTable table, Class<COLUMN> columnClass, COLUMN[] columns) {
		model = table.getColumnModel();
		this.columns = new EnumMap<>(columnClass);
		order = new LinkedList<>(Arrays.asList(columns));
		visibilityMap = new EnumMap<>(columnClass);
		for (final COLUMN columnDefinition : columns) {
			final TableColumn column = model.getColumn(columnDefinition.ordinal());
			this.columns.put(columnDefinition, column);
			visibilityMap.put(columnDefinition, true);
			column.setIdentifier(columnDefinition);
		}
	}

	public void setColumnOrder(List<COLUMN> columnOrder) {
		order.clear();
		order.addAll(columnOrder);
		rebuildTable();
	}

	public void setColumnVisibility(COLUMN column, boolean isVisible) {
		if (column.isHideable()) {
			visibilityMap.put(column, isVisible);
			rebuildTable();
		}
	}

	public void rebuildTable() {
		while (model.getColumnCount() > 0) {
			model.removeColumn(model.getColumn(0));
		}
		for (final COLUMN column : order) {
			if (visibilityMap.get(column)) {
				model.addColumn(columns.get(column));
			}
		}
	}
}