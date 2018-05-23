package net.thomas.portfolio.table;

import static java.awt.BorderLayout.CENTER;
import static net.thomas.portfolio.table.Tester.SampleTableColumn.COLUMN_A;
import static net.thomas.portfolio.table.Tester.SampleTableColumn.COLUMN_B;
import static net.thomas.portfolio.table.Tester.SampleTableColumn.COLUMN_C;
import static net.thomas.portfolio.table.Tester.SampleTableColumn.COLUMN_D;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;

public class Tester extends JFrame {
	private static final long serialVersionUID = 1L;

	private final TableControl<SampleTableColumn> control;

	private final TableView view;

	public Tester() {
		view = new TableView();
		control = new TableControl<>(view, new SampleRowLoader(), SampleTableColumn.class, SampleTableColumn.values());

		setLayout(new BorderLayout());
		add(view, CENTER);
		setSize(1000, 600);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
	}

	public TableControl<SampleTableColumn> getControl() {
		return control;
	}

	public TableView getView() {
		return view;
	}

	public static void main(String[] args) throws InterruptedException {
		final Tester tester = new Tester();
		Thread.sleep(3000);
		tester.getControl().hideColumn(COLUMN_B);
		Thread.sleep(3000);
		tester.getControl().showColumn(COLUMN_B);
		Thread.sleep(3000);
		tester.getControl().setColumnOrder(Arrays.asList(COLUMN_A, COLUMN_C, COLUMN_B, COLUMN_D));
		Thread.sleep(3000);
		tester.getControl().hideColumn(COLUMN_D);
		tester.getControl().hideColumn(COLUMN_B);
		Thread.sleep(3000);
		tester.getControl().showColumn(COLUMN_D);
		tester.getControl().showColumn(COLUMN_B);
		tester.getControl().hideColumn(COLUMN_A);
	}

	static enum SampleTableColumn implements ColumnDefinitionEnum {
		COLUMN_A("Column A", false), COLUMN_B("Column B", true), COLUMN_C("Column C", true), COLUMN_D("Column D", true);

		private final String prettyName;
		private final boolean isHideable;

		private SampleTableColumn(String prettyName, boolean isHideable) {
			this.prettyName = prettyName;
			this.isHideable = isHideable;
		}

		@Override
		public String getPrettyName() {
			return prettyName;
		}

		@Override
		public boolean isHideable() {
			return isHideable;
		}
	}

	static class SampleRowLoader implements PagingRowDataLoader<SampleTableColumn> {
		private static final int PAGE_SIZE = 20;
		private int nextRowIndex;
		private boolean isPaging;

		private final List<Row<SampleTableColumn>> loadedRows;

		private final LinkedList<PagingDataChangeListener<SampleTableColumn>> listeners;

		public SampleRowLoader() {
			nextRowIndex = 0;
			isPaging = false;
			loadedRows = new ArrayList<>();
			listeners = new LinkedList<>();
		}

		@Override
		public void addPagingDataChangeListener(PagingDataChangeListener<SampleTableColumn> listener) {
			listeners.add(listener);
		}

		@Override
		public void reset() {
			nextRowIndex = 0;
			loadedRows.clear();
		}

		@Override
		public boolean hasMorePages() {
			return true;
		}

		@Override
		public synchronized void initiatePaging() {
			if (!isPaging) {
				isPaging = true;
				startPagingJob();
			}
		}

		private void startPagingJob() {
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						Thread.sleep(1000);
					} catch (final InterruptedException e) {}
					generateNextPage();
					informListeners();
					isPaging = false;
				}

				private void generateNextPage() {
					for (int i = nextRowIndex; i < nextRowIndex + PAGE_SIZE; i++) {
						final Map<SampleTableColumn, Object> item = new EnumMap<>(SampleTableColumn.class);
						for (final SampleTableColumn column : SampleTableColumn.values()) {
							item.put(column, column.name() + "_" + i);
						}
						loadedRows.add(new SampleRow(item));
					}
					nextRowIndex += PAGE_SIZE;
				}

				private void informListeners() {
					for (final PagingDataChangeListener<SampleTableColumn> listener : listeners) {
						listener.pagingEnded(loadedRows);
					}
				}
			}).start();
		}
	}

	static class SampleRow implements Row<SampleTableColumn> {
		private final Map<SampleTableColumn, Object> rowData;

		public SampleRow(Map<SampleTableColumn, Object> rowData) {
			this.rowData = rowData;
		}

		@Override
		public Object getId() {
			return rowData.get(COLUMN_A);
		}

		@Override
		public Object getCell(SampleTableColumn column) {
			return rowData.get(column);
		}
	}
}
