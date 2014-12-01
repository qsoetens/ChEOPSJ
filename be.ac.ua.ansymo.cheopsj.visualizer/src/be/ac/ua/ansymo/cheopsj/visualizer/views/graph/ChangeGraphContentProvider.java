package be.ac.ua.ansymo.cheopsj.visualizer.views.graph;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.zest.core.viewers.IGraphContentProvider;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.viewers.IGraphEntityContentProvider;

import be.ac.ua.ansymo.cheopsj.model.ModelManager;
import be.ac.ua.ansymo.cheopsj.model.ModelManagerEvent;
import be.ac.ua.ansymo.cheopsj.model.ModelManagerListener;
import be.ac.ua.ansymo.cheopsj.model.changes.Change;
import be.ac.ua.ansymo.cheopsj.model.changes.IChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Subject;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixClass;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixPackage;

public class ChangeGraphContentProvider implements IGraphEntityContentProvider, ModelManagerListener {
	private GraphViewer viewer = null;
	private ModelManager manager = null;
	private String packageToExpand = "";

	/* =============================
	 * IGRAPHCONTENTPROVIDER METHODS
	 * =============================
	 */
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = (GraphViewer) viewer;
		
		if (this.manager != null) {
			this.manager.getModelManagerListeners().removeModelManagerListener(this);
		}
			
		this.manager = (ModelManager) newInput;
		
		if (this.manager != null) {
			this.manager.getModelManagerListeners().addModelManagerListener(this);
		}
		
	}
	
	@Override
	public Object[] getElements(Object input) {
		Collection<Object> result = new ArrayList<Object>();
		
		Collection<Subject> famixElems = manager.getFamixElements();
		for (Subject elem : famixElems) {
			if (elem instanceof FamixPackage) {
				result.add(elem);
			} else if (elem instanceof FamixClass) {
				String packName = ((FamixClass) elem).getBelongsToPackage().getUniqueName();
				if (packName.equals(this.packageToExpand)) {
					result.add(elem);
				}
			}
		}
		
		return result.toArray();
	}
	
	@Override
	public Object[] getConnectedTo(Object entity) {
		Collection<Object> result = new ArrayList<Object>();
		
		if (entity instanceof FamixPackage) {
			String packName = ((FamixPackage) entity).getUniqueName();
			if (packName.equals(this.packageToExpand))
				result.addAll(((FamixPackage) entity).getClasses());
		}
		
		return result.toArray();
	}

	/* ============================
	 * MODELMANAGERLISTENER METHODS
	 * ============================
	 */
	@Override
	public void changesAdded(final ModelManagerEvent event) {
		// If this is the UI thread, then make the change.
		if (Display.getCurrent() != null) {
			updateViewer(event);
			return;
		}

		// otherwise, redirect to execute on the UI thread.
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				updateViewer(event);
			}
		});		
	}
	
	@SuppressWarnings("restriction")
	private void updateViewer(ModelManagerEvent event) {
		// Use the setRedraw method to reduce flicker
		// when adding or removing multiple items in a table.
		viewer.getGraphControl().setRedraw(false);
		try {
			for (IChange change : event.getNewChanges()) {
				viewer.addNode((Change) change);
			}
		} finally {
			viewer.getGraphControl().setRedraw(true);
			viewer.refresh();
			viewer.applyLayout();
		}
	}

<<<<<<< HEAD
=======
	@Override
>>>>>>> a57fb2214de6615e1b5131c7d6af7f2dcaf19100
	public void refresh() {
		viewer.refresh();
	}
	
	public void updateAndRefresh() {
		viewer.refresh();
		viewer.applyLayout();
	}
	
	/* ============================
	 * CHANGE CONTENT METHODS
	 * ============================
	 */
	public void setPackageNameToExpand(String pack) {
		this.packageToExpand = pack;
		System.out.println("I just set the member packageToExpand to " + this.packageToExpand);
		updateAndRefresh();
	}
	
	public void removePackageNameToExpand() {
		this.packageToExpand = "";
		refresh();
	}

}
