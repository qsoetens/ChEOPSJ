/***************************************************
 * Copyright (c) 2014 Nicolas Demarbaix
 * 
 * Contributors: 
 * 		Nicolas Demarbaix - Initial Implementation
 ***************************************************/
package be.ac.ua.ansymo.cheopsj.visualizer.views.graph;

import org.eclipse.draw2d.FreeformViewport;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.part.*;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.internal.GraphLabel;
import org.eclipse.zest.layouts.LayoutStyles;

import be.ac.ua.ansymo.cheopsj.model.ModelManager;
import be.ac.ua.ansymo.cheopsj.visualizer.views.graph.figures.FamixFigure;

public class ChangeGraph extends ViewPart {
	public static final String ID = "be.ac.ua.ansymo.cheopsj.visualizer.views.ChangeGraph";
	
	private static GraphViewer viewer = null;
	private Graph graph = null;
	private Composite parent = null;

	@Override
	public void createPartControl(Composite par) {
		parent = par;
		viewer = new GraphViewer(par, SWT.BORDER);
		viewer.setContentProvider(new ChangeGraphContentProvider());
		viewer.setLabelProvider(new ChangeGraphLabelProvider());
		viewer.setInput(ModelManager.getInstance());
		
		viewer.setLayoutAlgorithm(new ChangeGraphLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
		viewer.applyLayout();
		
		this.graph = this.viewer.getGraphControl();
		setupListeners();
	}
	
	private void setupListeners() {
		graph.addMenuDetectListener(new MenuDetectListener() {
			
			@Override
			public void menuDetected(MenuDetectEvent e) {
				/*
				 * MenuDetectEvent.detail equals:
				 *  - 0 for mouse click events
				 *  - 1 for keyboard events (include keyboard + mouse click)
				 *  
				 * Limit to context menu via CTRL+LEFT_MOUSE for now do to platform
				 * specific issues (Mac Os X right click opens context menu twice).
				 * 
				 */
				if (e.detail == 0) 
					return;
					
				Point point = graph.toControl(e.x, e.y);
				final IFigure fig = graph.getViewport().findFigureAt(point.x, point.y);

				/*
				 * Figure is an instance of FreeformViewport when the users selects a
				 * point in the white area.
				 * 
				 * I use this functionality to be able to find the correct figure when the
				 * user scrolls along the view (e.g. when the graph becomes too large to
				 * fit on screen). 
				 */
				if (fig instanceof FreeformViewport) {
					Menu menu = new Menu(parent.getShell(), SWT.POP_UP);
					MenuItem exit = new MenuItem(menu, SWT.NONE);
					exit.setText("Nothing here...");
					menu.setVisible(true);
				} else {
					Menu menu = new Menu(parent.getShell(), SWT.POP_UP);
					MenuItem expand = new MenuItem(menu, SWT.NONE);
					expand.setText("Expand package");
					expand.addSelectionListener(new SelectionListener() {
						
						@Override
						public void widgetSelected(SelectionEvent e) {
							String figLabel = "label";
							if (fig instanceof ImageFigure) {
								figLabel = ((FamixFigure) (((ImageFigure) fig).getParent())).getLabel();
							} else if (fig instanceof Label) {
								figLabel = ((Label) fig).getText();
							}
							((ChangeGraphContentProvider) viewer.getContentProvider()).setPackageNameToExpand(figLabel);
						}
						
						@Override
						public void widgetDefaultSelected(SelectionEvent e) {
							String figLabel = "label";
							if (fig instanceof ImageFigure) {
								figLabel = ((FamixFigure) (((ImageFigure) fig).getParent())).getLabel();
							} else if (fig instanceof Label) {
								figLabel = ((Label) fig).getText();
							}
							((ChangeGraphContentProvider) viewer.getContentProvider()).setPackageNameToExpand(figLabel);							
						}
					});
					MenuItem sep = new MenuItem(menu, SWT.SEPARATOR);
					MenuItem compress = new MenuItem(menu, SWT.NONE);
					compress.setText("Compress Package");
					compress.addSelectionListener(new SelectionListener() {
						
						@Override
						public void widgetSelected(SelectionEvent e) {
							((ChangeGraphContentProvider) viewer.getContentProvider()).removePackageNameToExpand();
						}
						
						@Override
						public void widgetDefaultSelected(SelectionEvent e) {
							((ChangeGraphContentProvider) viewer.getContentProvider()).removePackageNameToExpand();							
						}
					});
					MenuItem sep2 = new MenuItem(menu, SWT.SEPARATOR);
					MenuItem exit = new MenuItem(menu, SWT.NONE);
					exit.setText("Close...");
					menu.setVisible(true);
				}
			}
		});		
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}
	
	public void resetViewLayout() {
		viewer.applyLayout();
	}

}