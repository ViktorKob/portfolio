package net.thomas.portfolio.table;

import static java.awt.BorderLayout.CENTER;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class TableView extends JPanel {
	private static final long serialVersionUID = 1L;

	private final JTable table;

	public TableView() {
		table = new JTable();
		setLayout(new BorderLayout());
		add(new JScrollPane(table), CENTER);
	}

	public JTable getTable() {
		return table;
	}
}
