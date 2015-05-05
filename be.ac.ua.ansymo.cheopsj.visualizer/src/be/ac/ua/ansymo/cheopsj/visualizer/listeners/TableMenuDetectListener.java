/***************************************************
 * Copyright (c) 2014 Nicolas Demarbaix
 * 
 * Contributors: 
 * 		Nicolas Demarbaix - Initial Implementation
 ***************************************************/
package be.ac.ua.ansymo.cheopsj.visualizer.listeners;


import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import be.ac.ua.ansymo.cheopsj.visualizer.views.graph.ChangeGraph;
import be.ac.ua.ansymo.cheopsj.visualizer.views.timeline.ChangeTimeline;

/**
 * Menu detect listener for the Change Table tableviewer
 * @author nicolasdemarbaix
 *
 */
public class TableMenuDetectListener implements MenuDetectListener {

	private Table table = null;
	private TableItem selectedItem = null;
	private Composite parent = null;
	
	private Menu itemMenu = null;
	
	/**
	 * Public constructor
	 * @param parent (Composite) the parent component
	 * @param table (Table) the displayed table component of the tableviewer
	 */
	public TableMenuDetectListener(Composite parent, Table table) {
		this.parent = parent;
		this.table = table;
		constructMenu();
	}
	
	@Override
	public void menuDetected(MenuDetectEvent e) {
		if (table.getSelectionIndex() == -1) 
			return;
		
		this.selectedItem = table.getItem(table.getSelectionIndex());
		this.itemMenu.setVisible(true);
	}
	
	/**
	 * Construct the menu that will be displayed when a menu detect event occurs
	 */
	@SuppressWarnings({ "unused"})
	private void constructMenu() {
		this.itemMenu = new Menu(this.parent.getShell(), SWT.POP_UP);
		MenuItem summary = new MenuItem(itemMenu, SWT.NONE);
		summary.setText("Show cell summary");
		summary.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				String msg = "";
				msg += "Entity: " + selectedItem.getText(1) + "\n";
				msg += "Change Type: " + selectedItem.getText(0) + "\n";
				msg += "Famix Type" + selectedItem.getText(2) + "\n";
				msg += "Change Date: " + selectedItem.getText(3) + "\n";
				msg += "By User: " + selectedItem.getText(4) + "\n";
				msg += "Commit Message: " + selectedItem.getText(5) + "\n";
				
				MessageDialog dialog = new MessageDialog(parent.getShell(), 
														 "Item Information", 
														 null, 
														 msg, 
														 MessageDialog.INFORMATION, 
														 new String [] {"Close"}, 
														 0);
				dialog.open();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		
		MenuItem sep1 = new MenuItem(itemMenu, SWT.SEPARATOR);
		MenuItem timeline = new MenuItem(itemMenu, SWT.NONE);
		timeline.setText("Show in timeline");
		timeline.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				String entityName = selectedItem.getText(1);
				ChangeTimeline view;
				try {
					IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					if (page.findViewReference(ChangeTimeline.ID, entityName) != null)
						return;
					view = (ChangeTimeline) page.showView(ChangeTimeline.ID, entityName, IWorkbenchPage.VIEW_CREATE);
					view.setSubjectID(entityName);
					view.reCreatePartControl();
				} catch (PartInitException e1) {
					MessageDialog dialog = new MessageDialog(parent.getShell(), 
							 								 "Error!",
							 								 null,
							 								 "Unable to open the timeline view",
							 								 MessageDialog.ERROR,
							 								 new String [] {"Close"},
							 								 0);
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		MenuItem graph = new MenuItem(itemMenu, SWT.NONE);
		graph.setText("Show in graph");
		graph.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String entityName = selectedItem.getText(1);
				ChangeGraph view;
				try {
					IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					if (page.findViewReference(ChangeGraph.ID, entityName) != null)
						return;
					view = (ChangeGraph) page.showView(ChangeGraph.ID, entityName, IWorkbenchPage.VIEW_CREATE);
					view.setFocusEntity(entityName);
				} catch (PartInitException e1) {
					MessageDialog dialog = new MessageDialog(parent.getShell(), 
							 								 "Error!",
							 								 null,
							 								 "Unable to open the graph view",
							 								 MessageDialog.ERROR,
							 								 new String [] {"Close"},
							 								 0);
				}
			}
		});
		MenuItem sep2 = new MenuItem(itemMenu, SWT.SEPARATOR);
		MenuItem exit = new MenuItem(itemMenu, SWT.NONE);
		exit.setText("Exit");
	}

}
