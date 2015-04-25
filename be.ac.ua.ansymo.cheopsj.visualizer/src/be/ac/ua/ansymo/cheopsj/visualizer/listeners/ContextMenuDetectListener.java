package be.ac.ua.ansymo.cheopsj.visualizer.listeners;

import org.eclipse.draw2d.FreeformViewport;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.zest.core.widgets.Graph;

import be.ac.ua.ansymo.cheopsj.visualizer.data.DataStore;
import be.ac.ua.ansymo.cheopsj.visualizer.views.graph.ChangeGraphContentProvider;
import be.ac.ua.ansymo.cheopsj.visualizer.views.graph.figures.FamixFigure;

public class ContextMenuDetectListener implements MenuDetectListener {
	/*
	 * Private members
	 */
	private Graph control = null;
	private Composite parent = null;
	private ChangeGraphContentProvider provider = null;
	
	private Point selectedPoint = null;
	private IFigure figure = null;
	
	private Menu noSelectionMenu = null;
	private Menu selectionMenu = null;
	
	public ContextMenuDetectListener(Graph cont, Composite par, ChangeGraphContentProvider prov) {
		this.control = cont;
		this.parent = par;
		this.provider = prov;
		this.constructMenus();
	}
	
	/**
	 * 
	 */
	@Override
	public void menuDetected(MenuDetectEvent e) {
		if (e.detail == 0)
			return;
		
		this.selectedPoint = this.control.toControl(e.x, e.y);
		this.figure = this.control.getViewport().findFigureAt(this.selectedPoint.x, this.selectedPoint.y);
		
		if (this.figure instanceof FreeformViewport) {
			this.noSelectionMenu.setVisible(true);
		} else {
			this.selectionMenu.setVisible(true);
		}
	}
	
	private void constructMenus() {
		// CONSTRUCT THE "NOTHING SELECTED" MENU
		this.noSelectionMenu = new Menu(this.parent.getShell(), SWT.POP_UP);
		MenuItem exit = new MenuItem(this.noSelectionMenu, SWT.NONE);
		exit.setText("Exit");
		
		// CONSTRUCT THE "SELECTED" MENU
		this.selectionMenu = new Menu(this.parent.getShell(), SWT.POP_UP);
		MenuItem toParent = new MenuItem(this.selectionMenu, SWT.NONE);
		toParent.setText("Go to parent...");
		toParent.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				provider.goToParent();
			}
		});
		MenuItem focusChild = new MenuItem(this.selectionMenu, SWT.NONE);
		focusChild.setText("Go to child...");
		focusChild.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (figure instanceof ImageFigure) {
					FamixFigure ff = (FamixFigure) ((ImageFigure)figure).getParent();
					String entityID = ff.getEntityID();
					provider.setFocusEntity(entityID);
				}
			}
		});
		MenuItem sep1 = new MenuItem(this.selectionMenu, SWT.SEPARATOR);
		MenuItem info = new MenuItem(this.selectionMenu, SWT.NONE);
		info.setText("Show package info");
		MenuItem sep2 = new MenuItem(this.selectionMenu, SWT.SEPARATOR);
		MenuItem exit2 = new MenuItem(this.selectionMenu, SWT.NONE);
		exit2.setText("Exit");
	
	}

}
