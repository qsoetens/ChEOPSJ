package be.ac.ua.ansymo.cheopsj.visualizer.views.graph;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.zest.core.viewers.IGraphContentProvider;
import org.eclipse.zest.core.viewers.GraphViewer;

import be.ac.ua.ansymo.cheopsj.model.ModelManager;
import be.ac.ua.ansymo.cheopsj.model.ModelManagerEvent;
import be.ac.ua.ansymo.cheopsj.model.ModelManagerListener;

public class ChangeGraphContentProvider implements IGraphContentProvider, ModelManagerListener {
	private GraphViewer viewer = null;
	private ModelManager manager = null;

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
	public Object getSource(Object rel) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getDestination(Object rel) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] getElements(Object input) {
		// TODO Auto-generated method stub
		return null;
	}

	/* ============================
	 * MODELMANAGERLISTENER METHODS
	 * ============================
	 */
	@Override
	public void changesAdded(ModelManagerEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub
		
	}

}
