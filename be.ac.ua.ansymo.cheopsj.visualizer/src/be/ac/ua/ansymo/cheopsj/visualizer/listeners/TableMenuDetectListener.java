package be.ac.ua.ansymo.cheopsj.visualizer.listeners;


import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import be.ac.ua.ansymo.cheopsj.visualizer.views.timeline.ChangeTimeline;

public class TableMenuDetectListener implements MenuDetectListener {

	private Table table = null;
	private TableItem selectedItem = null;
	private Composite parent = null;
	
	private Menu itemMenu = null;
	
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
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
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
					view = (ChangeTimeline) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(ChangeTimeline.ID);
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
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		MenuItem sep2 = new MenuItem(itemMenu, SWT.SEPARATOR);
		MenuItem exit = new MenuItem(itemMenu, SWT.NONE);
		exit.setText("Exit");
	}

}
