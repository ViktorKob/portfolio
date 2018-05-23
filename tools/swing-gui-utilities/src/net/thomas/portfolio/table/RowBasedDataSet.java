package net.thomas.portfolio.table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RowBasedDataSet<COLUMN extends Enum<COLUMN> & ColumnDefinitionEnum> {
	private final ArrayList<Object> rowIds;
	private final Map<Object, Row<COLUMN>> rows;

	public RowBasedDataSet() {
		rowIds = new ArrayList<>();
		rows = new HashMap<>();
	}

	public synchronized void update(List<Row<COLUMN>> data) {
		rowIds.clear();
		for (final Row<COLUMN> row : data) {
			rowIds.add(row.getId());
			if (!rows.containsKey(row.getId())) {
				rows.put(row.getId(), row);
			}
		}
	}

	public synchronized void addRow(Row<COLUMN> row) {
		rowIds.add(row.getId());
		rows.put(row.getId(), row);
	}

	public Object getCell(int row, COLUMN column) {
		return rows.get(rowIds.get(row)).getCell(column);
	}

	public int size() {
		return rows.size();
	}
}
