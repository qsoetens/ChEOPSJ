/***************************************************
 * Copyright (c) 2014 Nicolas Demarbaix
 * 
 * Contributors: 
 * 		Nicolas Demarbaix - Initial Implementation
 ***************************************************/
package be.ac.ua.ansymo.cheopsj.visualizer.views.table;

import java.util.Comparator;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import be.ac.ua.ansymo.cheopsj.model.ModelManager;
import be.ac.ua.ansymo.cheopsj.model.changes.IChange;
import be.ac.ua.ansymo.cheopsj.visualizer.data.DataStore;
import be.ac.ua.ansymo.cheopsj.visualizer.listeners.TableMenuDetectListener;
import be.ac.ua.ansymo.cheopsj.visualizer.views.table.filters.ChangeTableDateFilter;
import be.ac.ua.ansymo.cheopsj.visualizer.views.table.filters.ChangeTableNameFilter;
import be.ac.ua.ansymo.cheopsj.visualizer.views.table.filters.ChangeTableTypeFilter;
import be.ac.ua.ansymo.cheopsj.visualizer.views.table.filters.ChangeTableUserFilter;
import be.ac.ua.ansymo.cheopsj.visualizer.views.table.widgets.ChangeTableSettings;

/**
 * Change Table view
 * @author nicolasdemarbaix
 *
 */
public class ChangeTable extends ViewPart {
	public static final String ID = "be.ac.ua.ansymo.cheopsj.visualizer.views.table.ChangeTable";	
	
	// Table Related Members
	private TableViewer viewer = null;
	private TableViewerColumn changeTypeColumn = null;
	private TableViewerColumn nameColumn = null;
	private TableViewerColumn famixTypeColumn = null;
	private TableViewerColumn changeDateColumn = null;
	private TableViewerColumn userColumn = null;
	@SuppressWarnings("unused")
	private TableViewerColumn intentColumn = null;
	
	private ChangeTableSorter sorter = null;
	private ChangeTableNameFilter filter_name = null;
	private ChangeTableDateFilter filter_date = null;
	private ChangeTableTypeFilter filter_type = null;
	private ChangeTableUserFilter filter_user = null;
	
	private ChangeTableContentProvider contentProvider = null;
	private ChangeTableLabelProvider labelProvider = null;
	
	private ChangeTableSettings filter_settings = null;
	
	private Label summaryLabel = null;
	private Text searchText = null;
	
	/**
	 * Public Constructor
	 */
	public ChangeTable() {
		this.contentProvider = new ChangeTableContentProvider(this);
		this.labelProvider = new ChangeTableLabelProvider();
	}

	@Override
	public void createPartControl(Composite parent) {
		GridLayout layout = new GridLayout(3, false);
		parent.setLayout(layout);
		Button searchButton = new Button(parent, SWT.PUSH);
		searchButton.setText("Search");
		GridData sbD = new GridData();
		sbD.horizontalAlignment = GridData.FILL;
		searchButton.setLayoutData(sbD);
		searchText = new Text(parent, SWT.BORDER | SWT.SEARCH);
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = false;
		gd.horizontalSpan = 2;
		gd.widthHint = 200;
		searchText.setLayoutData(gd);
		searchButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				filter_name.setSearchString(searchText.getText());
				viewer.refresh();
				updateSummaryLabel();
			}
		});
		
		Button settingsButton = new Button(parent, SWT.PUSH);
		settingsButton.setText("Filter Settings");
		settingsButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				openFilterSettings();
			}
		});
		
		Button refresh = new Button(parent, SWT.PUSH);
		refresh.setText("Refresh table");
		refresh.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				refresh();
				updateSummaryLabel();
			}
		});
		
		Button clearText = new Button(parent, SWT.PUSH);
		clearText.setText("Clear search query");
		clearText.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				searchText.setText("");
				filter_name.setSearchString("");
				viewer.refresh();
				updateSummaryLabel();
			}
		});
		
		summaryLabel = new Label(parent, SWT.NONE);
		summaryLabel.setText(this.contentProvider.getChangeSummary());
		GridData gd2 = new GridData();
		gd2.horizontalSpan = 3;
		gd2.grabExcessHorizontalSpace = true;
		gd2.grabExcessVerticalSpace = false;
		gd2.widthHint = 350;
		summaryLabel.setLayoutData(gd2);
		
		this.createViewer(parent);
		
		this.filter_name = new ChangeTableNameFilter();
		this.filter_date = new ChangeTableDateFilter();
		this.filter_type = new ChangeTableTypeFilter();
		this.filter_user = new ChangeTableUserFilter();
		this.viewer.setFilters(new ViewerFilter [] {this.filter_date, this.filter_name, this.filter_type, this.filter_user});
	}
	
	/**
	 * Create the table viewer for the Change Table view
	 * @param parent (Composite) parent component
	 */
	private void createViewer(Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		createColumns(parent, viewer);
		final Table table = this.viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		table.addMenuDetectListener(new TableMenuDetectListener(parent, table));
		
		viewer.setContentProvider(this.contentProvider);
		viewer.setLabelProvider(this.labelProvider);
		viewer.setInput(ModelManager.getInstance());
		getSite().setSelectionProvider(viewer);
		
		GridData data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalSpan = 10;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		viewer.getControl().setLayoutData(data);
		
		createTableSorter();
	}
	
	/**
	 * Get the viewer 
	 * @return (TableViewer) viewer
	 */
	public TableViewer getViewer() {
		return this.viewer;
	}
	
	/**
	 * Create the columns of the table
	 * @param parent (Composite) parent component
	 * @param viewer (TableViewer) viewer component
	 */
	private void createColumns(final Composite parent, final TableViewer viewer) {
		String [] headers = {"Change Type", "Name", "Type", "Date", "User", "Intent"};
		int [] bounds = {100, 100, 100, 100, 100, 100};
		
		changeTypeColumn = createTableViewerColumn(headers[0], bounds[0], 0);
		
		nameColumn = createTableViewerColumn(headers[1], bounds[1], 0);
		
		famixTypeColumn = createTableViewerColumn(headers[2], bounds[2], 0);
		
		changeDateColumn = createTableViewerColumn(headers[3], bounds[3], 0);
		
		userColumn = createTableViewerColumn(headers[4], bounds[4], 0);
		
		intentColumn = createTableViewerColumn(headers[5], bounds[5], 0);
	}
	
	/**
	 * Create a column for the tableviewer
	 * @param title (String) header label
	 * @param bound (int) the preferred width of the column
	 * @param colNumber (int) column number
	 * @return
	 */
	private TableViewerColumn createTableViewerColumn(String title, int bound, final int colNumber) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		return viewerColumn;
	}
	
	/**
	 * Create the sorter for the table
	 */
	@SuppressWarnings("unchecked")
	private void createTableSorter() {
		Comparator<IChange> changeTypeComparator = new Comparator<IChange>() {
			public int compare(IChange i1, IChange i2) {
				return i1.getChangeType().compareTo(i2.getChangeType());
			}
		};
		Comparator<IChange> famixTypeComparator = new Comparator<IChange>() {
			public int compare(IChange i1, IChange i2) {
				return i1.getFamixType().compareTo(i2.getFamixType());
			}
		};
		Comparator<IChange> nameComparator = new Comparator<IChange>() {
			public int compare(IChange i1, IChange i2) {
				return i1.getName().compareTo(i2.getName());
			}
		};
		Comparator<IChange> timeComparator = new Comparator<IChange>() {
			public int compare(IChange i1, IChange i2) {
				return i1.getTimeStamp().compareTo(i2.getTimeStamp());
			}
		};
		Comparator<IChange> userComparator = new Comparator<IChange>() {
			@Override
			public int compare(IChange o1, IChange o2) {
				return o1.getUser().compareTo(o2.getUser());
			}
		};
		this.sorter = new ChangeTableSorter(this.viewer, 
											new TableColumn[] {this.changeTypeColumn.getColumn(), 
															   this.nameColumn.getColumn(), 
															   this.famixTypeColumn.getColumn(),
															   this.changeDateColumn.getColumn(),
															   this.userColumn.getColumn()}, 
											new Comparator[] {changeTypeComparator,
															  famixTypeComparator,
															  nameComparator,
															  timeComparator,
															  userComparator});
		this.viewer.setSorter(this.sorter);
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}
	
	/**
	 * Refresh the tableviewer
	 */
	public void refresh() {
		DataStore.getInstance().updateUserNames();
		viewer.refresh();
	}
	
	/**
	 * Open the filter settings dialog and update the view
	 */
	private void openFilterSettings() {
		if (this.filter_settings == null) {
			this.filter_settings = new ChangeTableSettings(new Shell(Display.getCurrent()));
			this.filter_settings.create();
		}
		
		if (this.filter_settings.open() == Window.OK) {
			String ftext = this.filter_settings.getFamixText();
			if (ftext.equals("All")) {
				this.filter_type.setFamixDefault();
			} else {
				this.filter_type.setFamixType(ftext);
			}
			String ctext = this.filter_settings.getChangeText();
			if (ctext.equals("All")) {
				this.filter_type.setChangeDefault();
			} else {
				this.filter_type.setChangeType(ctext);
			}
			this.filter_date.setDates(this.filter_settings.getDateFrom(), this.filter_settings.getDateTo());
			
			this.filter_user.setSearchString(this.filter_settings.getUserText());
			
			viewer.refresh();
			boolean canUseSummary = false;
			if (canUseSummary) {
				summaryLabel.setText(this.contentProvider.getChangeSummary());
			} else {
				updateSummaryLabel();
			}
		} else {
			System.out.println("User Pressed Cancel");
		}
	}
	
	/**
	 * Update the summary label
	 */
	private void updateSummaryLabel() {
		int changes = 0;
		int additions = 0;
		int removals = 0;
		for (TableItem ti : viewer.getTable().getItems()) {
			changes++;
			if (ti.getText(0).equals("Addition")) {
				additions++;
			} else {
				removals++;
			}
		}
		summaryLabel.setText(String.valueOf(changes) + " Changes; " + String.valueOf(additions) + " Additions & "
				+ String.valueOf(removals) + " Removals.");
	}

}
