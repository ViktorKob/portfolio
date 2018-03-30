package table;

import java.util.List;

public interface PagingDataChangeListener<COLUMN extends Enum<COLUMN> & ColumnDefinitionEnum> {
	void pagingStarted();

	void pagingEnded(List<Row<COLUMN>> data);
}