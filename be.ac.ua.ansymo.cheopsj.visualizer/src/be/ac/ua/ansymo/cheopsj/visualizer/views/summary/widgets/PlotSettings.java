package be.ac.ua.ansymo.cheopsj.visualizer.views.summary.widgets;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.dialogs.DialogSettings;
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

public class PlotSettings extends TitleAreaDialog {
	// Colors
	private static Color COLOR_WHITE = Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
	private static Color COLOR_GRAY = Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);
	
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
	private DialogSettings settings = null;
	
	private int beginDate_year = 2015;
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
	
	public PlotSettings(Shell parentShell) {
		super(parentShell);
	}
	
	@Override
	public void create() {
		super.create();
		this.settings = new DialogSettings("root");
		
		setTitle("Plot Settings");
		setMessage("Please select the appropriate settings", IMessageProvider.INFORMATION);
	}
	
	private void loadSettings() {
		try {
			settings.load("plotsettings.xml");
			
			this.beginDate_year = settings.getInt("beginDate_year");
			this.beginDate_month = settings.getInt("beginDate_month");
			this.beginDate_day = settings.getInt("beginDate_day");
			this.beginDate_hour = settings.getInt("beginDate_hour");
			this.beginDate_minute = settings.getInt("beginDate_minute");
			
			this.endDate_year = settings.getInt("endDate_year");
			this.endDate_month = settings.getInt("endDate_month");
			this.endDate_day = settings.getInt("endDate_day");
			this.endDate_hour = settings.getInt("endDate_hour");
			this.endDate_minute = settings.getInt("endDate_minute");
			
			this.rangeStart_store = settings.getInt("range_start");
			this.rangeEnd_store = settings.getInt("range_end");
			
			this.parseDomainDetail(settings.get("domain_detail"));
			this.rangeDetail_store = settings.getInt("range_detail");
			
			this.showAll_store = settings.getBoolean("show_all");
			this.showAdd_store = settings.getBoolean("show_add");
			this.showDel_store = settings.getBoolean("show_del");
			
		} catch (Exception e) {}
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
		this.rangeDetail.setItems(new String [] {"1", "5", "10", "25", "50", "100"});
		this.rangeDetail.select(getRangeDetailIndex(this.rangeDetail_store));
		this.rangeDetail.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 2, 1));
	}
	
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
		saveSettings();
		super.okPressed();
	}
	
	private void saveSettings() {				
		settings.put("beginDate_year", this.beginDate.getYear());
		settings.put("beginDate_month", this.beginDate.getMonth());
		settings.put("beginDate_day", this.beginDate.getDay());
		settings.put("beginDate_hour", this.beginTime.getHours());
		settings.put("beginDate_minute", this.beginTime.getMinutes());
		
		settings.put("endDate_year", this.endDate.getYear());
		settings.put("endDate_month", this.endDate.getMonth());
		settings.put("endDate_day", this.endDate.getDay());
		settings.put("endDate_hour", this.endTime.getHours());
		settings.put("endDate_minute", this.endTime.getMinutes());
		
		settings.put("range_start", this.rangeStart_store);
		settings.put("range_end", this.rangeEnd_store);
		
		settings.put("domain_detail", getDomainDetailString());
		settings.put("range_detail", this.rangeDetail_store);
		
		settings.put("show_all", this.showAll_store);
		settings.put("show_add", this.showAdd_store);
		settings.put("show_del", this.showDel_store);
		
		try {
			settings.save("plotsettings.xml");
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
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
	public Date getBeginDate() {
		return this.beginDate_store;
	}
	
	public Date getEndDate() {
		return this.endDate_store;
	}
	
	public int getRangeStart() {
		return this.rangeStart_store;
	}
	
	public int getRangeEnd() {
		return this.rangeEnd_store;
	}
	
	public DateTickUnitType getDomainDetail() {
		return this.domainDetail_store;
	}
	
	public int getDomainDetailMultiple() {
		return this.domainDetail_multiple;
	}
	
	public int getRangeDetail() {
		return this.rangeDetail_store;
	}
	
	public boolean getShowAllChecked() {
		return this.showAll_store;
	}
	
	public boolean getShowAddChecked() {
		return this.showAdd_store;
	}
	
	public boolean getShowDelChecked() {
		return this.showDel_store;
	}
}
