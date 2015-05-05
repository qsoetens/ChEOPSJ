/***************************************************
 * Copyright (c) 2014 Nicolas Demarbaix
 * 
 * Contributors: 
 * 		Nicolas Demarbaix - Initial Implementation
 ***************************************************/
package be.ac.ua.ansymo.cheopsj.visualizer.views.timeline.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * Legend view for the timeline
 * @author nicolasdemarbaix
 *
 */
public class TimelineLegend extends Composite {
	// IMAGES
	private static Image img_addition = PlatformUI.getWorkbench().getSharedImages()
			.getImage(ISharedImages.IMG_OBJ_ADD);
	private static Image img_removal = PlatformUI.getWorkbench().getSharedImages()
			.getImage(ISharedImages.IMG_ETOOL_DELETE);
	
	/**
	 * Public Constructor
	 * @param parent (Composite) parent component
	 * @param style (int) SWT graphics style
	 */
	public TimelineLegend(Composite parent, int style) {
		super(parent, SWT.BORDER);
		this.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		
		this.setLayout(new GridLayout(2,false));
		
		this.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		
		Canvas canvas_add = new Canvas(this, SWT.BORDER);
		GridData gdAdd = new GridData();
		gdAdd.widthHint = 20;
		gdAdd.heightHint = 20;
		gdAdd.verticalSpan = 1;
		gdAdd.horizontalSpan = 1;
		canvas_add.setLayoutData(gdAdd);
		canvas_add.addPaintListener(new PaintListener() {
			
			@Override
			public void paintControl(PaintEvent e) {
				e.gc.drawImage(img_addition, 0, 0);				
			}
		});
		
		new Label(this, SWT.NONE).setText("Addition");
		
		Canvas canvas_del = new Canvas(this, SWT.BORDER);
		GridData gdDel = new GridData();
		gdDel.widthHint = 20;
		gdDel.heightHint = 20;
		gdDel.verticalSpan = 1;
		gdDel.horizontalSpan = 1;
		canvas_del.setLayoutData(gdDel);
		canvas_del.addPaintListener(new PaintListener() {
			
			@Override
			public void paintControl(PaintEvent e) {
				e.gc.drawImage(img_removal, 0, 0);
			}
		});
		
		new Label(this, SWT.NONE).setText("Removal");
	}
}
