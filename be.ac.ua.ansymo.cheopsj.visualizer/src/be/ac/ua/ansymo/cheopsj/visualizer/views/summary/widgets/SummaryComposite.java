/***************************************************
 * Copyright (c) 2014 Nicolas Demarbaix
 * 
 * Contributors: 
 * 		Nicolas Demarbaix - Initial Implementation
 ***************************************************/
package be.ac.ua.ansymo.cheopsj.visualizer.views.summary.widgets;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * Composite class for the change summary
 * @author nicolasdemarbaix
 *
 */
public class SummaryComposite extends Composite {	
	// Colors
	private static Color COLOR_WHITE = Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
	
	// Fonts
	private static Font FONT_TITLE = new Font(Display.getCurrent(), "SansSerif", 15, SWT.BOLD);
	
	// Buttons
	private Button open_plot_settings = null;
	
	// Tables
	private CustomTableComposite table = null;
	
	// Dialogs
	private PlotSettings plot_settings = null;
	
	// Plot
	private ChangePlot plot = null;
	
	/**
	 * Public constructor
	 * @param parent (Composite) parent component
	 * @param style (int) SWT graphics style
	 */
	@SuppressWarnings("unused")
	public SummaryComposite(Composite parent, int style) {
		super(parent, style);
		
		// SummaryComposite Setup
		this.setBackground(COLOR_WHITE);
		this.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL, 
										GridData.VERTICAL_ALIGN_BEGINNING, 
										true, 
										true));
		
		// Layout Setup
		GridLayout layout = new GridLayout();
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		layout.verticalSpacing = 10;
		layout.numColumns = 2;
		this.setLayout(layout);
		
		// Widgets
		Label general_title = new Label(this, SWT.NONE);
		general_title.setText("Project Summary");
		general_title.setFont(FONT_TITLE);
		general_title.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 2,1));
		
		this.table = new CustomTableComposite(this, SWT.NONE);
		this.table.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 2,1));
		
		Label empty1 = new Label(this, SWT.NONE);
		empty1.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 2,2));
		
		Label plot_title = new Label(this, SWT.NONE);
		plot_title.setText("Changes Plot");
		plot_title.setFont(FONT_TITLE);
		plot_title.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 2,1));
		
		this.open_plot_settings = new Button(this, SWT.PUSH);
		this.open_plot_settings.setText("Plot Settings...");
		this.open_plot_settings.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));
		this.open_plot_settings.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				openPlotSettings();
			}
		});
		
		Label empty2 = new Label(this, SWT.NONE);
		
		this.plot = new ChangePlot(this, SWT.NONE);
		GridData gData_plot = new GridData(GridData.FILL, GridData.FILL, true, true);
		gData_plot.horizontalSpan = 2;
		gData_plot.minimumHeight = 420;
		gData_plot.minimumWidth = 680;
		this.plot.setLayoutData(gData_plot);		
	}
	
	/**
	 * Open the settings dialog and apply the updated settings
	 */
	private void openPlotSettings() {
		if (this.plot_settings == null) {
			this.plot_settings = new PlotSettings(new Shell(Display.getCurrent()));
			this.plot_settings.create();
		}
		
		if (this.plot_settings.open() == Window.OK) {
			this.plot.updateVisibleData(this.plot_settings.getShowAllChecked(), 
										this.plot_settings.getShowAddChecked(), 
										this.plot_settings.getShowDelChecked());
			this.plot.updateDomainAxis(this.plot_settings.getDomainDetail(), 
									   this.plot_settings.getDomainDetailMultiple(), 
									   this.plot_settings.getBeginDate(), 
									   this.plot_settings.getEndDate(), 
									   "dd-M-yy");
			this.plot.updateRangeAxis(this.plot_settings.getRangeStart(), 
									  this.plot_settings.getRangeEnd(), 
									  this.plot_settings.getRangeDetail());
			this.plot.updatePlot();
		} else {
			System.out.println("User Pressed Cancel");
		}
	}

}
