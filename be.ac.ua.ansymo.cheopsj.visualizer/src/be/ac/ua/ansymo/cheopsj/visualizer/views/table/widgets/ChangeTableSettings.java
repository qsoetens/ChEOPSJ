package be.ac.ua.ansymo.cheopsj.visualizer.views.table.widgets;

import java.util.Date;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import be.ac.ua.ansymo.cheopsj.visualizer.data.DataStore;
import be.ac.ua.ansymo.cheopsj.visualizer.data.ViewPreferences;
import be.ac.ua.ansymo.cheopsj.visualizer.util.DateUtil;

public class ChangeTableSettings extends TitleAreaDialog {

	// COLORS
	private static Color COLOR_WHITE = Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);

	// Settings
	private ViewPreferences preferences = null;
	
	// Dialog Elements
	private Label changeLabel = null;
	private Combo changeCombo = null;
	private Label famixLabel = null;
	private Combo famixCombo = null;
	private Label dateFromLabel = null;
	private DateTime dateFrom = null;
	private Label dateToLabel = null;
	private DateTime dateTo = null;
	private Label userLabel = null;
	private Text userText = null;
	
	// Result
	private Date from = null;
	private Date to = null;
	private String change = "";
	private String famix = "";
	private String user = "";
	
	public ChangeTableSettings(Shell parentShell) {
		super(parentShell);
	}
	
	@Override
	public void create() {
		this.preferences = DataStore.getInstance().getTablePreferences();

		super.create();
		
		setTitle("Change Table Settings");
		setMessage("Please select the appropriate settings", IMessageProvider.INFORMATION);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);
		
		createCombo(container);
		createDate(container);
		createUserText(container);
		
		return area;
	}
	
	@Override
	protected boolean isResizable() {
		return false;
	}
	
	@Override
	protected void okPressed() {
		save();
		super.okPressed();
	}
	
	@SuppressWarnings("deprecation")
	private void createDate(Composite parent) {
		dateFromLabel = new Label(parent, SWT.NONE);
		dateFromLabel.setText("Begin Date:");
		
		dateFrom = new DateTime(parent, SWT.BORDER | SWT.DATE);
		dateFrom.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));
		dateFrom.setTime(0, 0, 0);
		
		dateToLabel = new Label(parent, SWT.NONE);
		dateToLabel.setText("End Date:");
		
		dateTo = new DateTime(parent, SWT.BORDER | SWT.DATE);
		dateTo.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));
		dateTo.setTime(23, 59, 59);
		
		if (this.preferences != null) {
			if (this.preferences.get("date_from") instanceof Date) {
				Date start = (Date) this.preferences.get("date_from");
				if (start != null) {
					dateFrom.setDate(start.getYear() + 1900, start.getMonth(), start.getDate());
					dateFrom.setTime(0, 0, 0);
				}
			}
			if (this.preferences.get("date_to") instanceof Date) {
				Date end = (Date) this.preferences.get("date_to");
				if (end != null) {
					dateTo.setDate(end.getYear() + 1900, end.getMonth(), end.getDate());
					dateTo.setTime(23, 59, 59);
				}
			}
		}
	}
	
	private void createCombo(Composite parent) {
		changeLabel = new Label(parent, SWT.NONE);
		changeLabel.setText("Change Type: ");
		
		changeCombo = new Combo(parent, SWT.BORDER);
		changeCombo.setItems(new String [] {"All", "Addition", "Removal"});
		changeCombo.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));
		changeCombo.select(0);
		
		famixLabel = new Label(parent, SWT.NONE);
		famixLabel.setText("Famix Type:");
		
		famixCombo = new Combo(parent, SWT.BORDER);
		famixCombo.setItems(new String [] {"All", "Package", "Class", "Method", "Attribute", "Invocation"});
		famixCombo.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));
		famixCombo.select(0);
		
		if (this.preferences != null) {
			if (this.preferences.get("change_index") != null) {
				changeCombo.select((int) this.preferences.get("change_index")); 
			}
			if (this.preferences.get("famix_index") != null) {
				famixCombo.select((int) this.preferences.get("famix_index"));
			}
		}
	}
	
	private void createUserText(Composite parent) {
		userLabel = new Label(parent, SWT.NONE);
		userLabel.setText("User:");
		
		userText = new Text(parent, SWT.BORDER);
		userText.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));
		userText.setMessage("Enter username. Leave blank to display all users");
		
		if (this.preferences != null) {
			if (this.preferences.get("user") != null) {
				userText.setText((String) this.preferences.get("user"));
			}
		}
	}
	
	private void save() {
		this.change = this.changeCombo.getText();
		this.user = this.userText.getText();
		this.famix = this.famixCombo.getText();
		this.from = DateUtil.getInstance().constructDateAndTime(dateFrom.getYear(), 
					dateFrom.getMonth(), 
					dateFrom.getDay(), 0, 0, 0);
		this.to = DateUtil.getInstance().constructDateAndTime(dateTo.getYear(), 
					dateTo.getMonth(), 
					dateTo.getDay(), 23, 59, 59);
		this.preferences = new ViewPreferences();
		this.preferences.add("date_from", from);
		this.preferences.add("date_to", to);
		this.preferences.add("change_index", this.changeCombo.getSelectionIndex());
		this.preferences.add("famix_index", this.famixCombo.getSelectionIndex());
		this.preferences.add("user", user);
		DataStore.getInstance().setTablePreferences(this.preferences);
	}
	
	/*
	 * GETTERS
	 */
	public String getChangeText() {
		return this.change;
	}
	public String getFamixText() {
		return this.famix;
	}
	public Date getDateFrom() {
		return this.from;
	}
	public Date getDateTo() {
		return this.to;
	}
	public String getUserText() {
		return this.user;
	}

}
