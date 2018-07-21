package net.thomas.portfolio.table;

public interface RowDataChangeListener<COLUMN extends Enum<COLUMN> & ColumnDefinitionEnum> {

	boolean startDataChange();

	void endDataChange();
}