/***************************************************
 * Copyright (c) 2014 Nicolas Demarbaix
 * 
 * Contributors: 
 * 		Nicolas Demarbaix - Initial Implementation
 ***************************************************/
package be.ac.ua.ansymo.cheopsj.visualizer.views.table;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;

import be.ac.ua.ansymo.cheopsj.model.ModelManager;
import be.ac.ua.ansymo.cheopsj.model.ModelManagerChange;
import be.ac.ua.ansymo.cheopsj.model.ModelManagerEvent;
import be.ac.ua.ansymo.cheopsj.model.ModelManagerListener;
import be.ac.ua.ansymo.cheopsj.model.ModelManagerListeners;
import be.ac.ua.ansymo.cheopsj.visualizer.data.DataStore;

/**
 * Content provider for the Change Table view
 * @author nicolasdemarbaix
 *
 */
public class ChangeTableContentProvider implements IStructuredContentProvider, ModelManagerListener {

	private TableViewer viewer = null;
	@SuppressWarnings("unused")
	private ChangeTable view = null;
	private ModelManager manager = null;
	private ModelManagerChange change_manager = null;
	private ModelManagerListeners listen_manager = null;
	
	/**
	 * Private constructor
	 */
	private ChangeTableContentProvider() {
		this.change_manager = ModelManagerChange.getInstance();
		this.listen_manager = ModelManagerListeners.getInstance();
	}
	
	/**
	 * Public Constructor
	 * @param view (ChangeTable) Change Table view
	 */
	public ChangeTableContentProvider(ChangeTable view) {
		this();
		this.view = view;
	}
	
	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = (TableViewer) viewer;
		if (this.manager != null) {
			listen_manager.removeModelManagerListener(this);
		}
		this.manager = (ModelManager) newInput;
		if (this.manager != null) {
			listen_manager.addModelManagerListener(this);
		}
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return this.change_manager.getChanges().toArray();
	}
	
	/**
	 * Get the summary from the modelmanagerchange
	 * @return (String) summary
	 */
	public String getChangeSummary() {
		return this.change_manager.getSummary();
	}

	@Override
	public void changesAdded(ModelManagerEvent event) {
		if (Display.getCurrent() != null) {
			updateView(event);
			return;
		}
		
		Display.getDefault().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				updateView(event);				
			}
		});
	}
	
	/**
	 * Update the view when an event occurs for the modelmanager
	 * @param e (ModelManagerEvent) event in the modelmanager
	 */
	private void updateView(ModelManagerEvent e) {
		viewer.getTable().setRedraw(false);
		DataStore.getInstance().updateUserNames();
		try {
			viewer.add(e.getNewChanges());
			viewer.refresh();
		} finally {
			viewer.getTable().setRedraw(true);
		}
	}
	
	/**
	 * Get the summary from the modelmanagerchange
	 * @return (String) summary
	 */
	public String getSummary() {
		return this.change_manager.getSummary();
	}
	
	

}
