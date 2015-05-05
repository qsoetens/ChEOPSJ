/***************************************************
 * Copyright (c) 2014 Nicolas Demarbaix
 * 
 * Contributors: 
 * 		Nicolas Demarbaix - Initial Implementation
 ***************************************************/
package be.ac.ua.ansymo.cheopsj.visualizer.views.summary;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;

import be.ac.ua.ansymo.cheopsj.visualizer.views.summary.widgets.SummaryComposite;

/**
 * Change Summary view
 * @author nicolasdemarbaix
 *
 */
public class ChangeSummary extends ViewPart {
	
	public static final String ID = "be.ac.ua.ansymo.cheopsj.visualizer.views.ChangeSummary";
	
	@Override
	public void createPartControl(Composite parent) {
		Color color_white = Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
		
		ScrolledComposite scroll = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
		scroll.setLayout(new FillLayout());
		scroll.setBackground(color_white);
		Composite top = new SummaryComposite(scroll, SWT.NONE);
		top.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		scroll.setContent(top);
		top.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL, GridData.VERTICAL_ALIGN_BEGINNING,true,false));		
		top.setSize(top.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	@Override
	public void setFocus() {
	}

}
