/***************************************************
 * Copyright (c) 2014 Nicolas Demarbaix
 * 
 * Contributors: 
 * 		Nicolas Demarbaix - Initial Implementation
 ***************************************************/
package be.ac.ua.ansymo.cheopsj.visualizer.views.summary;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.Range;
import org.jfree.data.time.DateRange;
import org.jfree.data.time.Day;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.experimental.chart.swt.ChartComposite;

import be.ac.ua.ansymo.cheopsj.model.changes.IChange;
import be.ac.ua.ansymo.cheopsj.visualizer.data.DataStore;

public class ChangeSummary extends ViewPart {
	
	public static final String ID = "be.ac.ua.ansymo.cheopsj.visualizer.views.ChangeSummary";
	
	/*
	 * Below members are part of the SWT eclipse Widget Suite
	 * They are used to display the data and are stored as members for access
	 * in the update method
	 */
	private Table elementTable = null;
	private Table changeTable = null;
	private DateTime beginDate = null;
	private DateTime beginTime = null;
	private DateTime endDate = null;
	private DateTime endTime = null;
	private Text rangeStart = null;
	private Text rangeEnd = null;
	private Combo domainDetail = null;
	private Combo rangeDetail = null;
	private Button showAllPlot = null;
	private Button showAddPlot = null;
	private Button showDelPlot = null;
	private ChartComposite changePlot = null;
	
	@Override
	public void createPartControl(Composite parent) {
		Color color_white = Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
		Color color_grey = Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);
		
		ScrolledComposite scroll = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
		scroll.setLayout(new FillLayout());
		scroll.setBackground(color_white);
		Composite top = new Composite(scroll, SWT.NONE);
		top.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		scroll.setContent(top);
		top.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL, GridData.VERTICAL_ALIGN_BEGINNING,true,false));
		GridLayout layout = new GridLayout();
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		layout.numColumns = 4;
		layout.verticalSpacing = 10;
		top.setLayout(layout);
				
		Label title = new Label(top, SWT.NONE);
		title.setText("Project Summary");
		title.setFont(new Font(null, "SansSerif", 14,SWT.BOLD));
		GridData gData = new GridData(GridData.FILL, GridData.FILL, false, false);
		gData.horizontalSpan = 4;
		title.setLayoutData(gData);
		
		Label elemInfo = new Label(top, SWT.NONE);
		elemInfo.setText("Element Info: ");
		elemInfo.setFont(new Font(null, "SansSerif",12, SWT.NORMAL));
		gData = new GridData(GridData.FILL, GridData.FILL, false, false);
		gData.horizontalSpan = 2;
		elemInfo.setLayoutData(gData);
		
		Label changeInfo = new Label(top, SWT.NONE);
		changeInfo.setText("Change Info:");
		changeInfo.setFont(new Font(null, "SansSerif",12, SWT.NORMAL));
		gData = new GridData(GridData.FILL, GridData.FILL, false, false);
		gData.horizontalSpan = 2;
		changeInfo.setLayoutData(gData);
				
		this.elementTable = new Table(top, SWT.MULTI | SWT.BORDER);
		this.elementTable.setLinesVisible(true);
		this.elementTable.setHeaderVisible(true);
		gData = new GridData(GridData.FILL, GridData.FILL, false, false);
		gData.horizontalSpan = 2;
		this.elementTable.setLayoutData(gData);
		
		TableColumn col3 = new TableColumn(this.elementTable, SWT.NONE);
		col3.setText("Type");
		TableColumn col4 = new TableColumn(this.elementTable, SWT.NONE);
		col4.setText("Number of elements");
		
		TableItem packs = new TableItem(this.elementTable, SWT.NONE);
		packs.setText(0, "Packages");
		packs.setText(1, "20");
		TableItem classes = new TableItem(this.elementTable, SWT.NONE);
		classes.setText(0, "Classes");
		classes.setText(1, "155");
		TableItem methods = new TableItem(this.elementTable, SWT.NONE);
		methods.setText(0, "Methods");
		methods.setText(1, "495");
		TableItem total = new TableItem(this.elementTable, SWT.NONE);
		total.setText(0, "Total");
		total.setText(1, "670");
		
		this.elementTable.getColumn(0).pack();
		this.elementTable.getColumn(1).pack();
		
		this.changeTable = new Table(top, SWT.MULTI | SWT.BORDER);
		this.changeTable.setLinesVisible(true);
		this.changeTable.setHeaderVisible(true);
		gData = new GridData(GridData.FILL, GridData.FILL, false, false);
		gData.horizontalSpan = 1;
		this.changeTable.setLayoutData(gData);
		
		TableColumn col = new TableColumn(this.changeTable, SWT.NONE);
		col.setText("Type");
		TableColumn col2 = new TableColumn(this.changeTable, SWT.NONE);
		col2.setText("Number of changes");
		
		TableItem add = new TableItem(this.changeTable, SWT.NONE);
		add.setText(0, "Additions");
		add.setText(1, "3487");
		TableItem del = new TableItem(this.changeTable, SWT.NONE);
		del.setText(0, "Deletions");
		del.setText(1, "1539");
		TableItem total2 = new TableItem(this.changeTable, SWT.NONE);
		total2.setText(0, "Total");
		total2.setText(1, "5026");
		
		this.changeTable.getColumn(0).pack();
		this.changeTable.getColumn(1).pack();
		
		Label empty3 = new Label(top, SWT.NONE);
		
		Label empty5 = new Label(top, SWT.NONE);
		gData = new GridData(GridData.FILL, GridData.FILL, false, false);
		gData.horizontalSpan = 4;
		gData.verticalSpan = 2;
		empty5.setLayoutData(gData);
		
		Label plotLabel = new Label(top, SWT.NONE);
		plotLabel.setText("Change plot");
		plotLabel.setFont(new Font(null, "SansSerif", 14,SWT.BOLD));
		gData = new GridData(GridData.FILL, GridData.FILL, false, false);
		gData.horizontalSpan = 4;
		plotLabel.setLayoutData(gData);
				
		Label bdate = new Label(top, SWT.NONE);
		bdate.setText("Begin date: ");
		this.beginDate = new DateTime(top, SWT.DATE | SWT.BORDER);
		this.beginDate.setBackground(color_white);
		gData = new GridData(GridData.FILL, GridData.FILL, false, false);
		this.beginDate.setLayoutData(gData);
		this.beginDate.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateView();
			}
		});
		
		this.beginTime = new DateTime(top, SWT.TIME | SWT.BORDER);
		this.beginTime.setBackground(color_white);
		gData = new GridData(GridData.FILL, GridData.FILL, false, false);
		this.beginTime.setLayoutData(gData);
		this.beginTime.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateView();
			}
		});
		
		Label empty = new Label(top, SWT.NONE);
		
		Label edate = new Label(top, SWT.NONE);
		edate.setText("End date: ");
		this.endDate = new DateTime(top, SWT.DATE | SWT.BORDER);
		this.endDate.setBackground(color_white);
		gData = new GridData(GridData.FILL, GridData.FILL, false, false);
		this.endDate.setLayoutData(gData);
		this.endDate.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateView();
			}
		});
		
		this.endTime = new DateTime(top, SWT.TIME | SWT.BORDER);
		this.endTime.setBackground(color_white);
		gData = new GridData(GridData.FILL, GridData.FILL, false, false);
		this.endTime.setLayoutData(gData);
		this.endTime.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateView();
			}
		});
		
		Label empty2 = new Label(top, SWT.NONE);
		
		Label rangeLabel = new Label(top, SWT.NONE);
		rangeLabel.setText("Range: ");
		
		this.rangeStart = new Text(top, SWT.BORDER);
		this.rangeStart.setMessage("from...");
		this.rangeStart.addListener(SWT.Traverse, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				if (event.detail == SWT.TRAVERSE_RETURN) {
					updateView();
				}
			}
		});
		this.rangeEnd = new Text(top, SWT.BORDER);
		this.rangeEnd.setMessage("to...");
		this.rangeEnd.addListener(SWT.Traverse, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				if (event.detail == SWT.TRAVERSE_RETURN) {
					updateView();
				}
			}
		});
		
		Label empty7 = new Label(top, SWT.NONE);
		
		Label domain = new Label(top, SWT.NONE);
		domain.setText("Domain tick step: ");
		gData = new GridData(GridData.FILL, GridData.FILL, false, false);
		gData.horizontalSpan = 2;
		domain.setLayoutData(gData);
				
		Label range = new Label(top, SWT.NONE);
		range.setText("Range tick step: ");
		gData = new GridData(GridData.FILL, GridData.FILL, false, false);
		gData.horizontalSpan = 2;
		range.setLayoutData(gData);
		
		this.domainDetail = new Combo(top, SWT.READ_ONLY);
		this.domainDetail.setItems(new String [] {"Month", "Week", "Day", "Hour", "Minute"});
		gData = new GridData(GridData.FILL, GridData.FILL, false, false);
		gData.horizontalSpan = 1;
		this.domainDetail.setLayoutData(gData);
		this.domainDetail.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateView();
			}
		});

		Label empty8 = new Label(top, SWT.NONE);

		this.rangeDetail = new Combo(top, SWT.READ_ONLY);
		this.rangeDetail.setItems(new String [] {"1", "10", "100", "1000"});
		gData = new GridData(GridData.FILL, GridData.FILL, false, false);
		gData.horizontalSpan = 1;
		this.rangeDetail.setLayoutData(gData);
		this.rangeDetail.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateView();
			}
		});
		
		Label empty4 = new Label(top, SWT.NONE);
		
		this.showAllPlot = new Button(top, SWT.CHECK);
		this.showAllPlot.setText("Show total changes");
		this.showAllPlot.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateView();
			}
		});
		this.showAddPlot = new Button(top, SWT.CHECK);
		this.showAddPlot.setText("Show additions");
		this.showAddPlot.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateView();
			}
		});
		this.showDelPlot = new Button(top, SWT.CHECK);
		this.showDelPlot.setText("Show deletions");
		this.showDelPlot.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateView();
			}
		});
		
		Label empty6 = new Label(top, SWT.NONE);
		
		JFreeChart chart = createChart();
		this.changePlot = new ChartComposite(top, SWT.NONE, chart, true);
		gData = new GridData(GridData.FILL, GridData.FILL, true, true);
		gData.horizontalSpan = 4;
		gData.minimumWidth = 680;
		gData.minimumHeight = 420;
		this.changePlot.setLayoutData(gData);
		
		top.setSize(top.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
	}
	
	private XYDataset createDataset() {
		final TimeSeries total_changes = new TimeSeries("Total Changes");
		final TimeSeries add_changes = new TimeSeries("Additions");
		final TimeSeries del_changes = new TimeSeries("Deletions");
		
		// Construct the timeseries
		for (int i = 1; i < 32; ++i) {
			int adds = (int) (Math.random() * 25);
			int dels = (int) (Math.random() * 25);
			int total = adds + dels;
			
			Day day = new Day(i, 1, 2015);
			total_changes.add(day, total);
			add_changes.add(day, adds);
			del_changes.add(day, dels);
		}
		
		total_changes.add(new Day(1, 2, 2015), 0);
		add_changes.add(new Day(1, 2, 2015), 0);
		del_changes.add(new Day(1, 2, 2015), 0);

        final TimeSeriesCollection dataset = new TimeSeriesCollection();
        if (this.showAllPlot.getSelection()) dataset.addSeries(total_changes);
        if (this.showAddPlot.getSelection()) dataset.addSeries(add_changes);
        if (this.showDelPlot.getSelection()) dataset.addSeries(del_changes);
        return dataset;
	}
	
	private JFreeChart createChart() {
		JFreeChart chart = ChartFactory.createTimeSeriesChart("Change History", "Date", "changes", createDataset(),true,true,false);
		XYPlot plot = chart.getXYPlot();
		
		// SETUP THE DOMAIN AXIS
		// TODO Find decent heuristic to determine the actual amount of ticks 
		//		based on the domain and user selection
		DateAxis domain = (DateAxis) plot.getDomainAxis();
		
		String tick_unit = this.domainDetail.getText();
		DateTickUnitType tick_type = null;
		int times = 1;
		String dateformat = "";
		if (tick_unit.equals("Month")) {
			tick_type = DateTickUnitType.MONTH;
			dateformat = "M-yy";
		} else if (tick_unit.equals("Week")) {
			tick_type = DateTickUnitType.DAY;
			times = 7;
			dateformat = "dd-M-yy";
		} else if (tick_unit.equals("Day")) {
			tick_type = DateTickUnitType.DAY;
			dateformat = "dd-M-yy";
		} else if (tick_unit.equals("Hour")) {
			tick_type = DateTickUnitType.HOUR;
			dateformat = "hh dd-M-yy";
		} else if (tick_unit.equals("Minute")){
			tick_type = DateTickUnitType.MINUTE;
			dateformat = "mm::hh dd-M-yy";
		} else {
			tick_type = DateTickUnitType.MONTH;
			dateformat = "M-yy";
		}
		domain.setDateFormatOverride(new SimpleDateFormat(dateformat));
		domain.setTickUnit(new DateTickUnit(tick_type, 1));
		
		Date lower = null;
		Date upper = null;
		Calendar calender = new GregorianCalendar();
		calender.set(this.beginDate.getYear(), this.beginDate.getMonth(), this.beginDate.getDay(),
				this.beginTime.getHours(), this.beginTime.getMinutes());
		lower = calender.getTime();
		calender.set(this.endDate.getYear(), this.endDate.getMonth(), this.endDate.getDay(),
				this.endTime.getHours(), this.endTime.getMinutes());
		DateRange domain_range = new DateRange(lower, upper);
		
		// SETUP THE RANGE AXIS
		NumberAxis range = (NumberAxis) plot.getRangeAxis();
		if (this.rangeDetail.getText().equals("")) {
			range.setTickUnit(new NumberTickUnit(1));
		} else {
			range.setTickUnit(new NumberTickUnit(Integer.valueOf(this.rangeDetail.getText())));
		}
		int range_start = 0;
		int range_end = 10;
		
		if (!this.rangeStart.getText().equals("")) {
			range_start = Integer.valueOf(this.rangeStart.getText());
			if (range_start < 0) {
				range_start = 0;
				this.rangeStart.setText(String.valueOf(range_start));
			}
		}
		
		if (!this.rangeEnd.getText().equals("")) {
			range_end = Integer.valueOf(this.rangeEnd.getText());
			if (range_end < 0 || range_end <= range_start) {
				range_end = range_start + 10;
				this.rangeEnd.setText(String.valueOf(range_end));
			}
		}
		range.setRange(new Range(range_start, range_end));
		return chart;
	}
	
	private void updateView() {
		this.changePlot.setChart(createChart());
		this.changePlot.redraw();
	}

}
