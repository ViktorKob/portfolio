package net.thomas.portfolio.table;

public interface PagingRowDataLoader<COLUMN extends Enum<COLUMN> & ColumnDefinitionEnum> {
	void addPagingDataChangeListener(PagingDataChangeListener<COLUMN> listener);

	void reset();

	boolean hasMorePages();

	void initiatePaging();
}