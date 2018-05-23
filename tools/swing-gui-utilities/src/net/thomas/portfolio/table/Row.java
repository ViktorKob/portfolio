package net.thomas.portfolio.table;

public interface Row<COLUMN extends Enum<COLUMN> & ColumnDefinitionEnum> {
	Object getId();

	Object getCell(COLUMN column);
}