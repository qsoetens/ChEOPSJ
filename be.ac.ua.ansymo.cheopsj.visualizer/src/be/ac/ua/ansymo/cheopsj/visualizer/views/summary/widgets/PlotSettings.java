/***************************************************
 * Copyright (c) 2014 Nicolas Demarbaix
 * 
 * Contributors: 
 * 		Nicolas Demarbaix - Initial Implementation
 ***************************************************/
package be.ac.ua.ansymo.cheopsj.visualizer.views.summary.widgets;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.jfree.chart.axis.DateTickUnitType;

import be.ac.ua.ansymo.cheopsj.visualizer.data.DataStore;
import be.ac.ua.ansymo.cheopsj.visualizer.data.ViewPreferences;

/**
 * Settings dialog class for changing the Change Summary View Plot settings
 * @author nicolasdemarbaix
 *
 */
public class PlotSettings extends TitleAreaDialog {
	// Colors
	private static Color COLOR_WHITE = Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
	
	// Widgets
	private DateTime beginDate = null;
	private DateTime endDate = null;
	private DateTime beginTime = null;
	private DateTime endTime = null; 
	
	private Text rangeStart = null;
	private Text rangeEnd = null;
	
	private Combo domainDetail = null;
	private Combo rangeDetail = null;
	
	private Button showAll = null;
	private Button showAdd = null;
	private Button showDel = null;
	
	// Store
	private ViewPreferences preferences = null;
	
	private int beginDate_year = 1990;
	private int beginDate_month = 0;
	private int beginDate_day = 1;
	private int beginDate_hour = 0;
	private int beginDate_minute = 0;
	
	private int endDate_year = 2015;
	private int endDate_month = 1;
	private int endDate_day = 1;
	private int endDate_hour = 0;
	private int endDate_minute = 0;
	
	private Date beginDate_store = null;
	private Date endDate_store = null;
	
	private int rangeStart_store = 0;
	private int rangeEnd_store = 10;
	
	private DateTickUnitType domainDetail_store = DateTickUnitType.MONTH;
	private int domainDetail_multiple = 1;
	
	private int rangeDetail_store = 10;
	
	private boolean showAll_store = false;
	private boolean showAdd_store = false;
	private boolean showDel_store = false;
	
	/**
	 * Public Constructor
	 * @param parentShell (Shell) the parent component
	 */
	public PlotSettings(Shell parentShell) {
		super(parentShell);
	}
	
	@Override
	public void create() {
		this.preferences = DataStore.getInstance().getSummaryPreferences();
		loadPreferences();
		super.create();
		
		setTitle("Plot Settings");
		setMessage("Please select the appropriate settings", IMessageProvider.INFORMATION);
	}
	
	/**
	 * Load the previously set preferences if they exist
	 */
	private void loadPreferences() {
		if (this.preferences == null)
			return;
		
		beginDate_year = (int) this.preferences.get("begin_date_year");
		beginDate_month = (int) this.preferences.get("begin_date_month");
		beginDate_day = (int) this.preferences.get("begin_date_day");
		beginDate_hour = (int) this.preferences.get("begin_date_hour");
		beginDate_minute = (int) this.preferences.get("begin_date_minute");
		
		endDate_year = (int) this.preferences.get("end_date_year");
		endDate_month = (int) this.preferences.get("end_date_month");
		endDate_day = (int) this.preferences.get("end_date_day");
		endDate_hour = (int) this.preferences.get("end_date_hour");
		endDate_minute = (int) this.preferences.get("end_date_minute");
		
		beginDate_store = (Date) this.preferences.get("date_from");
		endDate_store = (Date) this.preferences.get("date_to");
		
		rangeStart_store = (int) this.preferences.get("range_start");
		rangeEnd_store = (int) this.preferences.get("range_end");
		
		domainDetail_store = (DateTickUnitType) this.preferences.get("domain_detail_type");
		domainDetail_multiple = (int) this.preferences.get("domain_detail_multiple");
		
		rangeDetail_store = (int) this.preferences.get("range_detail");
		
		showAll_store = (boolean) this.preferences.get("show_all");
		showAdd_store = (boolean) this.preferences.get("show_add");
		showDel_store = (boolean) this.preferences.get("show_del");
	}
	
	/**
	 * Save the currently set preferences when the dialog closes
	 */
	private void savePreferences() {
		this.preferences = new ViewPreferences();
		
		this.preferences.add("begin_date_year", beginDate.getYear());
		this.preferences.add("begin_date_month", beginDate.getMonth());
		this.preferences.add("begin_date_day", beginDate.getDay());
		this.preferences.add("begin_date_hour", beginTime.getHours());
		this.preferences.add("begin_date_minute", beginTime.getMinutes());
		this.preferences.add("end_date_year", endDate.getYear());
		this.preferences.add("end_date_month", endDate.getMonth());
		this.preferences.add("end_date_day", endDate.getDay());
		this.preferences.add("end_date_hour", endTime.getHours());
		this.preferences.add("end_date_minute", endTime.getMinutes());
		
		this.preferences.add("date_from", this.beginDate_store);
		this.preferences.add("date_to", this.endDate_store);
		
		this.preferences.add("range_start", rangeStart_store);
		this.preferences.add("range_end", rangeEnd_store);
		
		this.preferences.add("domain_detail_type", domainDetail_store);
		this.preferences.add("domain_detail_multiple", domainDetail_multiple);
		
		this.preferences.add("range_detail", rangeDetail_store);
		
		this.preferences.add("show_all", showAll_store);
		this.preferences.add("show_add", showAdd_store);
		this.preferences.add("show_del", showDel_store);
		
		DataStore.getInstance().setSummaryPreferences(this.preferences);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		GridLayout layout = new GridLayout(3, false);
		container.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true,true));
		container.setLayout(layout);
		
		createDate(container);
		createRange(container);
		createPlotDetail(container);
		createChecks(container);
		
		return area;
	}
	
	/**
	 * Create the date related widgets
	 * @param parent (Composite) parent component
	 */
	private void createDate(Composite parent) {
		Label begin = new Label(parent, SWT.NONE);
		begin.setText("Begin Date:");
		
		this.beginDate = new DateTime(parent, SWT.BORDER | SWT.DATE);
		this.beginDate.setDate(this.beginDate_year, this.beginDate_month, this.beginDate_day);
		this.beginDate.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));
		
		this.beginTime = new DateTime(parent, SWT.BORDER | SWT.TIME);
		this.beginTime.setTime(this.beginDate_hour, this.beginDate_minute, 0);
		this.beginTime.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));
		
		Label end = new Label(parent, SWT.NONE);
		end.setText("End Date:");
		
		this.endDate = new DateTime(parent, SWT.BORDER | SWT.DATE);
		this.endDate.setDate(this.endDate_year, this.endDate_month, this.endDate_day);
		this.endDate.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));
		
		this.endTime = new DateTime(parent, SWT.BORDER | SWT.TIME);
		this.endTime.setTime(this.endDate_hour, this.endDate_minute, 0);
		this.endTime.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));
		
		
	}
	
	/**
	 * Create the range axis related widgets
	 * @param parent (Composite) parent component
	 */
	private void createRange(Composite parent) {
		Label range = new Label(parent, SWT.NONE);
		range.setText("Range:");
		
		this.rangeStart = new Text(parent, SWT.BORDER);
		this.rangeStart.setBackground(COLOR_WHITE);
		this.rangeStart.setMessage("From...");
		this.rangeStart.setText(String.valueOf(this.rangeStart_store));
		this.rangeStart.setEditable(true);
		this.rangeStart.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));
		
		this.rangeEnd = new Text(parent, SWT.BORDER);
		this.rangeEnd.setBackground(COLOR_WHITE);
		this.rangeEnd.setMessage("To...");
		this.rangeEnd.setText(String.valueOf(this.rangeEnd_store));
		this.rangeEnd.setEditable(true);
		this.rangeEnd.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));
	}
	
	/**
	 * Create plot axis detail widgets
	 * @param parent (Composite) parent component
	 */
	private void createPlotDetail(Composite parent) {
		Label domain = new Label(parent, SWT.NONE);
		domain.setText("Domain tick step: ");
		
		this.domainDetail = new Combo(parent, SWT.READ_ONLY);
		this.domainDetail.setItems(new String [] {"Month", "Week", "Day", "Hour", "Minute"});
		this.domainDetail.select(getDomainDetailIndex(getDomainDetailString()));
		this.domainDetail.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 2, 1));
		
		Label range = new Label(parent, SWT.NONE);
		range.setText("Range tick step: ");
		
		this.rangeDetail = new Combo(parent, SWT.READ_ONLY);
		this.rangeDetail.setItems(new String [] {"1", "5", "10", "25", "50", "100", "250", "500", "1000"});
		this.rangeDetail.select(getRangeDetailIndex(this.rangeDetail_store));
		this.rangeDetail.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 2, 1));
	}
	
	/**
	 * Get the index of the combobox domain detail widget based on its description
	 * @param detail (String) description of the detail
	 * @return (int) index of the detail in the combobox
	 */
	private int getDomainDetailIndex(String detail) {
		if (detail.equals("Month")) {
			return 0;
		} else if (detail.equals("Week")) {
			return 1;
		} else if (detail.equals("Day")) {
			return 2;
		} else if (detail.equals("Hour")) {
			return 3;
		} else {		
			return 4;
		}
	}
	
	/**
	 * Get the index of the combobox range detail widget based on its value
	 * @param val (int) value of the detail
	 * @return (int) index of the detail in the combobox
	 */
	private int getRangeDetailIndex(int val) {
		switch (val) {
		case 1:
			return 0;
		case 5:
			return 1;
		case 10:
			return 2;
		case 25:
			return 3;
		case 50:
			return 4;
		case 100:
			return 5;
		default:
			return 0;
		}
		
	}
	
	/**
	 * Create the check widgets
	 * @param parent (Composite) parent component
	 */
	private void createChecks(Composite parent) {
		Label all = new Label(parent, SWT.NONE);
		all.setText("Show the plot for all changes");
		all.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 2, 1));
		
		this.showAll = new Button(parent, SWT.CHECK);
		this.showAll.setSelection(this.showAll_store);
		
		Label add = new Label(parent, SWT.NONE);
		add.setText("Show the plot for all additions");
		add.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 2, 1));
		
		this.showAdd = new Button(parent, SWT.CHECK);
		this.showAdd.setSelection(this.showAdd_store);
		
		Label del = new Label(parent, SWT.NONE);
		del.setText("Show the plot for all deletions");
		del.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 2, 1));
		
		this.showDel = new Button(parent, SWT.CHECK);
		this.showDel.setSelection(this.showDel_store);
	}
	
	@Override
	protected boolean isResizable() {
		return false;
	}

	@Override
	protected void okPressed() {
		saveInput();
		savePreferences();
		super.okPressed();
	}
	
	/**
	 * Get the string value of the current selected domain detail
	 * @return (String) domain detail
	 */
	private String getDomainDetailString() {
		if (this.domainDetail_store == DateTickUnitType.MONTH) {
			return "Month";
		} else if (this.domainDetail_store == DateTickUnitType.DAY) {
			if (this.domainDetail_multiple == 1) {
				return "Day";
			} else {
				return "Week";
			}
		} else if (this.domainDetail_store == DateTickUnitType.HOUR) {
			return "Hour";
		} else if (this.domainDetail_store == DateTickUnitType.MINUTE) {
			return "Minute";
		}
		return "Month";
	}
	
	/**
	 * Save the input to be retreived when the dialog closes
	 */
	private void saveInput() {
		// Save the date window information
		Calendar cal = new GregorianCalendar();
		cal.set(this.beginDate.getYear(), 
				this.beginDate.getMonth(), 
				this.beginDate.getDay(), 
				this.beginDate.getHours(), 
				this.beginDate.getMinutes());
		this.beginDate_store = cal.getTime();
		
		cal.set(this.endDate.getYear(), 
				this.endDate.getMonth(), 
				this.endDate.getDay(), 
				this.endDate.getHours(), 
				this.endDate.getMinutes());
		this.endDate_store = cal.getTime();
		
		// Save the range information
		this.parseRange(this.rangeStart.getText(), this.rangeEnd.getText());
		
		// Save the tick step information
		this.parseDomainDetail(this.domainDetail.getText());
		this.parseRangeDetail(this.rangeDetail.getText());
		
		// Save the check button information
		this.showAll_store = this.showAll.getSelection();
		this.showAdd_store = this.showAdd.getSelection();
		this.showDel_store = this.showDel.getSelection();
	}
	
	/**
	 * Parse the range based on the contents of the fill in fields
	 * @param start (String) range start
	 * @param end (String) range end
	 */
	private void parseRange(String start, String end) {
		try {
			this.rangeStart_store = Integer.valueOf(start);
		} catch (Exception e) {
			System.err.println("PlotSettings::Please use only integer values for the range");
			this.rangeStart_store = 0;
		}
		try {
			this.rangeEnd_store = Integer.valueOf(end);
		} catch (Exception e) {
			System.err.println("PlotSettings::Please use only integer values for the range");
			this.rangeEnd_store = 10;
		}
	}
	
	/**
	 * Parse the domain detail to retreive the correct DateTickUnitType
	 * @param detail (String) the selected domain detail
	 */
	private void parseDomainDetail(String detail) {
		if (detail.equals("Month")) {
			this.domainDetail_store = DateTickUnitType.MONTH;
			this.domainDetail_multiple = 1;
		} else if (detail.equals("Week")) {
			this.domainDetail_store = DateTickUnitType.DAY;
			this.domainDetail_multiple = 7;
		} else if (detail.equals("Day")) {
			this.domainDetail_store = DateTickUnitType.DAY;
			this.domainDetail_multiple = 1;
		} else if (detail.equals("Hour")) {
			this.domainDetail_store = DateTickUnitType.HOUR;
			this.domainDetail_multiple = 1;
		} else {
			this.domainDetail_store = DateTickUnitType.MINUTE;
			this.domainDetail_multiple = 1;
		}
	}
	
	/**
	 * Parse the range detail to retreive the correct value
	 * @param detail (String) the selected range detail
	 */
	private void parseRangeDetail(String detail) {
		try {
			this.rangeDetail_store = Integer.valueOf(detail);
		} catch (Exception e) {
			System.err.println("PlotSettings::RangeDetail::Oops something went wrong!");
		}
	}
	
	/*
	 * GETTER METHODS
	 */
	/**
	 * @return (java.util.Date) begin date of the plot
	 */
	public Date getBeginDate() {
		return this.beginDate_store;
	}
	
	/**
	 * @return (java.util.Date) end date of the plot
	 */
	public Date getEndDate() {
		return this.endDate_store;
	}
	
	/**
	 * @return (int) lower range axis value
	 */
	public int getRangeStart() {
		return this.rangeStart_store;
	}
	
	/**
	 * @return (int) upper range axis value
	 */
	public int getRangeEnd() {
		return this.rangeEnd_store;
	}
	
	/**
	 * @return (DateTickUnitType) domain detail value
	 */
	public DateTickUnitType getDomainDetail() {
		return this.domainDetail_store;
	}
	
	/**
	 * @return (int) multiple of the tick type
	 */
	public int getDomainDetailMultiple() {
		return this.domainDetail_multiple;
	}
	
	/**
	 * @return (int) get the detail of the range axis
	 */
	public int getRangeDetail() {
		return this.rangeDetail_store;
	}
	
	/**
	 * @return (boolean) show the total changes plot
	 */
	public boolean getShowAllChecked() {
		return this.showAll_store;
	}
	
	/**
	 * @return (boolean) show the addition changes plot
	 */
	public boolean getShowAddChecked() {
		return this.showAdd_store;
	}
	
	/**
	 * @return (boolean) show the removal changes plot
	 */
	public boolean getShowDelChecked() {
		return this.showDel_store;
	}
}
