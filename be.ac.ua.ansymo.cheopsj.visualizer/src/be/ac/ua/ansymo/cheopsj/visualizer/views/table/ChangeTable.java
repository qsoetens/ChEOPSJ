package be.ac.ua.ansymo.cheopsj.visualizer.views.table;

import java.util.Comparator;
import java.util.Date;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import be.ac.ua.ansymo.cheopsj.model.ModelManager;
import be.ac.ua.ansymo.cheopsj.model.changes.IChange;
import be.ac.ua.ansymo.cheopsj.visualizer.util.DateUtil;
import be.ac.ua.ansymo.cheopsj.visualizer.views.table.filters.ChangeTableDateFilter;
import be.ac.ua.ansymo.cheopsj.visualizer.views.table.filters.ChangeTableNameFilter;
import be.ac.ua.ansymo.cheopsj.visualizer.views.table.filters.ChangeTableTypeFilter;

public class ChangeTable extends ViewPart {
	public static final String ID = "be.ac.ua.ansymo.cheopsj.visualizer.views.table.ChangeTable";	
	
	// Table Related Members
	private TableViewer viewer = null;
	private TableViewerColumn changeTypeColumn = null;
	private TableViewerColumn nameColumn = null;
	private TableViewerColumn famixTypeColumn = null;
	private TableViewerColumn changeDateColumn = null;
	
	private ChangeTableSorter sorter = null;
	private ChangeTableNameFilter filter_name = null;
	private ChangeTableDateFilter filter_date = null;
	private ChangeTableTypeFilter filter_type = null;
	
	private ChangeTableContentProvider contentProvider = null;
	private ChangeTableLabelProvider labelProvider = null;
	
	public ChangeTable() {
		this.contentProvider = new ChangeTableContentProvider(this);
		this.labelProvider = new ChangeTableLabelProvider();
	}

	@Override
	public void createPartControl(Composite parent) {
		GridLayout layout = new GridLayout(10, false);
		parent.setLayout(layout);
		Label searchLabel = new Label(parent, SWT.NONE);
		searchLabel.setText("Search: ");
		final Text searchText = new Text(parent, SWT.BORDER | SWT.SEARCH);
		searchText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		
		searchText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				filter_name.setSearchString(searchText.getText());
				viewer.refresh();
			}
		});
		
		Label changeLabel = new Label(parent, SWT.NONE);
		changeLabel.setText("Change Type: ");
		
		final Combo changeCombo = new Combo(parent, SWT.BORDER | SWT.READ_ONLY);
		changeCombo.setItems(new String [] {"All", "Addition","Modification", "Removal"});
		changeCombo.select(0);
		changeCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (changeCombo.getSelectionIndex() == 0) {
					filter_type.setChangeDefault();
				} else {
					filter_type.setChangeType(changeCombo.getText());
				}
				viewer.refresh();
			}
		});
		
		Label famixLabel = new Label(parent, SWT.NONE);
		famixLabel.setText("Famix Type: ");
		
		final Combo famixCombo = new Combo(parent, SWT.BORDER | SWT.READ_ONLY);
		famixCombo.setItems(new String [] {"All", "Package", "Class", "Method"});
		famixCombo.select(0);
		famixCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (famixCombo.getSelectionIndex() == 0) {
					filter_type.setFamixDefault();
				} else {				
					filter_type.setFamixType(famixCombo.getText());
				}
				viewer.refresh();
			}
		});
		
		Label dateFromLabel = new Label(parent, SWT.NONE);
		dateFromLabel.setText("From: ");
		
		DateTime dateFrom = new DateTime(parent, SWT.DATE | SWT.BORDER);
		dateFrom.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		dateFrom.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				filter_date.setStartDate(DateUtil.getInstance().constructDate(dateFrom.getYear(), 
																			  dateFrom.getMonth(), 
																			  dateFrom.getDay()));
				viewer.refresh();
			}
		});
		
		Label dateToLabel = new Label(parent, SWT.NONE);
		dateToLabel.setText("To: ");
		
		DateTime dateTo = new DateTime(parent, SWT.DATE | SWT.BORDER);
		dateTo.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		dateTo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				filter_date.setEndDate(DateUtil.getInstance().constructDate(dateTo.getYear(), 
																			dateTo.getMonth(), 
																			dateTo.getDay()));
				boolean wrong_order = DateUtil.getInstance().before(dateTo.getYear(), 
																	dateTo.getMonth(), 
																	dateTo.getDay(), 
																	dateFrom.getYear(), 
																	dateFrom.getMonth(), 
																	dateFrom.getDay());
				if (wrong_order) {
					dateFrom.setDate(dateTo.getYear(), dateTo.getMonth(), dateTo.getDay());
					dateFrom.setMinutes(0);
					dateFrom.setSeconds(0);
				}
				viewer.refresh();
			}
		});
		
		this.createViewer(parent);
		
		this.filter_name = new ChangeTableNameFilter();
		this.filter_date = new ChangeTableDateFilter();
		this.filter_type = new ChangeTableTypeFilter();
		this.viewer.setFilters(new ViewerFilter [] {this.filter_date, this.filter_name, this.filter_type});
	}
	
	private void createViewer(Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		createColumns(parent, viewer);
		final Table table = this.viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
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
	
	public TableViewer getViewer() {
		return this.viewer;
	}
	
	private void createColumns(final Composite parent, final TableViewer viewer) {
		String [] headers = {"Change Type", "Name", "Type", "Date"};
		int [] bounds = {100, 100, 100, 100};
		
		changeTypeColumn = createTableViewerColumn(headers[0], bounds[0], 0);
		
		nameColumn = createTableViewerColumn(headers[1], bounds[1], 0);
		
		famixTypeColumn = createTableViewerColumn(headers[2], bounds[2], 0);
		
		changeDateColumn = createTableViewerColumn(headers[3], bounds[3], 0);
	}
	
	private TableViewerColumn createTableViewerColumn(String title, int bound, final int colNumber) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		return viewerColumn;
	}
	
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
		this.sorter = new ChangeTableSorter(this.viewer, 
											new TableColumn[] {this.changeTypeColumn.getColumn(), 
															   this.nameColumn.getColumn(), 
															   this.famixTypeColumn.getColumn(),
															   this.changeDateColumn.getColumn()}, 
											new Comparator[] {changeTypeComparator,
															  famixTypeComparator,
															  nameComparator,
															  timeComparator});
		this.viewer.setSorter(this.sorter);
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}
	
	public void refresh() {
		viewer.refresh();
	}

}
