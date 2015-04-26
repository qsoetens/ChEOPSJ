package be.ac.ua.ansymo.cheopsj.visualizer.listeners;

import org.eclipse.draw2d.FreeformViewport;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.zest.core.widgets.Graph;

import be.ac.ua.ansymo.cheopsj.model.changes.IChange;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixEntity;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixInvocation;
import be.ac.ua.ansymo.cheopsj.visualizer.data.DataStore;
import be.ac.ua.ansymo.cheopsj.visualizer.views.graph.ChangeGraphContentProvider;
import be.ac.ua.ansymo.cheopsj.visualizer.views.graph.figures.FamixFigure;
import be.ac.ua.ansymo.cheopsj.visualizer.views.timeline.ChangeTimeline;

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
				provider.updateAndRefresh();
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
					provider.updateAndRefresh();
				}
			}
		});
		MenuItem sep1 = new MenuItem(this.selectionMenu, SWT.SEPARATOR);
		MenuItem info = new MenuItem(this.selectionMenu, SWT.NONE);
		info.setText("Show info");
		info.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FamixFigure ff;
				if (figure instanceof ImageFigure) {
					ff = (FamixFigure)((ImageFigure)figure).getParent();
				} else {
					return;
				}
				String msg = "Information about the selected element:\n\n";
				if (ff.isInvocation()) {
					FamixInvocation fi = ff.getInvocation();
					msg += "   name: '" + fi.getStringRepresentation() + "'\n";
					msg += "   type: " + fi.getFamixType() + "\n";
					int[] changes = fi.aggregateChanges();
					msg += "   " + changes[0] + " change(s) occured on this element. (" + changes[1]
							+ " addition(s); " + changes[2] + " removal(s))\n\n";
					msg += "Information about the changes for the selected element: \n\n";
					for (IChange change : fi.getAffectingChanges()) {
						msg += " * " + change.getChangeType() + " of the element on " + change.getTimeStamp().toString()
								+ " by " + change.getUser() + " with intent '" + change.getIntent() + "'\n";
					}
					
				} else {
					FamixEntity fe = ff.getEntity();
					msg += "   name: '" + fe.getUniqueName() + "'\n";
					msg += "   type: " + fe.getFamixType() + "\n";
					int[] changes = fe.aggregateChanges();
					msg += "   " + changes[0] + " change(s) occured on this element. (" + changes[1]
							+ " addition(s); " + changes[2] + " removal(s))\n\n";
					msg += "Information about the changes for the selected element: \n\n";
					for (IChange change : fe.getAffectingChanges()) {
						msg += " * " + change.getChangeType() + " of the element on " + change.getTimeStamp().toString()
								+ " by " + change.getUser() + " with intent '" + change.getIntent() + "'\n";
					}
				}
				
				MessageDialog dialog = new MessageDialog(Display.getCurrent().getActiveShell(), "Element Information", null, msg, MessageDialog.INFORMATION, new String [] {"Ok"}, 0);
				dialog.open();
			}
		});
		MenuItem sep2 = new MenuItem(this.selectionMenu, SWT.SEPARATOR);
		MenuItem timeline = new MenuItem(this.selectionMenu, SWT.NONE);
		timeline.setText("Show in timeline");
		timeline.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				String entityName = "";
				if (figure instanceof ImageFigure) {
					FamixFigure ff = (FamixFigure)((ImageFigure)figure).getParent();
					entityName = ff.getEntityID();
				} else {
					return;
				}
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
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		MenuItem sep = new MenuItem(this.selectionMenu, SWT.SEPARATOR);
		MenuItem exit2 = new MenuItem(this.selectionMenu, SWT.NONE);
		exit2.setText("Exit");
	
	}

}
