/***************************************************
 * Copyright (c) 2014 Nicolas Demarbaix
 * 
 * Contributors: 
 * 		Nicolas Demarbaix - Initial Implementation
 ***************************************************/
package be.ac.ua.ansymo.cheopsj.visualizer.views.table;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IMemento;

/**
 * Sorter for the change table
 * @author nicolasdemarbaix
 *
 */
public class ChangeTableSorter extends ViewerSorter {
	private static final String TAG_DESCENDING = "descending";
	private static final String TAG_COLUMN_INDEX = "columnIndex";
	private static final String TAG_TYPE = "SortInfo";
	private static final String TAG_TRUE = "true";
	
	/**
	 * Info for the sort object
	 * @author nicolasdemarbaix
	 *
	 */
	private class SortInfo {
		int columnIndex;
		Comparator<Object> comparator;
		boolean descending;
	}
	
	private TableViewer viewer;
	private SortInfo[] info_list;
	
	/**
	 * Public constructor
	 * @param viewer (TableViewer) the tableviewer of the Change Table
	 * @param columns (TableColumn[]) columns of the tableviewer
	 * @param comparators (Comparator<Object>[]) list of comparators
	 */
	public ChangeTableSorter(TableViewer viewer, TableColumn[] columns, Comparator<Object>[] comparators) {
		this.viewer = viewer;
		this.info_list = new SortInfo[columns.length];
		for (int i = 0; i < this.info_list.length; ++i) {
			this.info_list[i] = new SortInfo();
			this.info_list[i].columnIndex = i;
			this.info_list[i].comparator = comparators[i];
			this.info_list[i].descending = false;
			createSelectionListener(columns[i], this.info_list[i]);
		}
	}
	
	/**
	 * Create a selection listener for a column
	 * @param column (TableColumn)
	 * @param info (SortInfo) sortinfo object for the comparator
	 */
	private void createSelectionListener(final TableColumn column, SortInfo info) {
		column.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				sortUsing(info);
			}
		});
	}
	
	/**
	 * Sort the column using the specified info
	 * @param info (SortInfo)
	 */
	protected void sortUsing(SortInfo info) {
		if (info == this.info_list[0]) {
			info.descending = !info_list[0].descending;
		} else {
			for (int i = 0; i < this.info_list.length; ++i) {
				if (info == this.info_list[i]) {
					System.arraycopy(this.info_list, 0, this.info_list, 1, i);
					this.info_list[0] = info;
					info.descending = false;
					break;
				}
			}
		}
		viewer.refresh();
	}
	
	/**
	 * Save the state of the table
	 * @param memento (IMemento)
	 */
	public void saveState(IMemento memento) {
		for (int i = 0; i < this.info_list.length; ++i) {
			SortInfo info = this.info_list[i];
			IMemento mem = memento.createChild(TAG_TYPE);
			mem.putInteger(TAG_COLUMN_INDEX, info.columnIndex);
			if (info.descending) {
				mem.putString(TAG_DESCENDING, TAG_TRUE);
			}
		}
	}
	
	/**
	 * Initialize the sortinfo object
	 * @param memento (IMemento)
	 */
	public void init(IMemento memento) {
		List<SortInfo> newInfos = new ArrayList<SortInfo>(info_list.length);
		IMemento[] mementos = memento.getChildren(TAG_TYPE);
		for (int i = 0; i < mementos.length; ++i) {
			IMemento mem = mementos[i];
			Integer value = mem.getInteger(TAG_COLUMN_INDEX);
			if (value == null) {
				continue;
			}
			int index = value.intValue();
			if (index < 0 || index > this.info_list.length) {
				continue;
			}
			SortInfo info = this.info_list[index];
			if (newInfos.contains(info)) {
				continue;
			}
			info.descending = TAG_TRUE.equals(mem.getString(TAG_DESCENDING));
			newInfos.add(info);
		}
		for (int i = 0; i < this.info_list.length; i++)
	         if (!newInfos.contains(this.info_list[i]))
	            newInfos.add(this.info_list[i]);
		this.info_list = newInfos.toArray(new SortInfo[newInfos.size()]);
	}
	
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		for (int i = 0; i < this.info_list.length; ++i) {
			int result = this.info_list[i].comparator.compare(e1, e2);
			if (result != 0) {
				if (this.info_list[i].descending) {
					return -result;
				}
				return result;
			}
		}
		return 0;
	}
}
