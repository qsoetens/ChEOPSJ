package be.ac.ua.ansymo.cheopsj.model.ui.views.changegraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.viewers.IGraphEntityContentProvider;

import be.ac.ua.ansymo.cheopsj.model.changes.AtomicChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Change;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixAttribute;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixClass;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixInheritanceDefinition;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixInvocation;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixLocalVariable;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixMethod;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixPackage;

public class ChangeGraphViewContentProvider implements IGraphEntityContentProvider{
	private GraphViewer viewer;
	private List<?> selectedChanges;
	
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = (GraphViewer) viewer;
		selectedChanges = (List<?>) newInput;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		
		Collection<Object> result = new ArrayList<Object>();
		
		result.addAll(selectedChanges);
		
		for(Object o : selectedChanges){
			if(o instanceof AtomicChange)
				result.add(((AtomicChange) o).getChangeSubject());
		}
		
		return result.toArray();
	}

	@Override
	public Object[] getConnectedTo(Object entity) {

		Collection<Object> result = new ArrayList<Object>();

		if (entity instanceof Change) {
			Change node = (Change) entity;
			result.addAll(node.getStructuralDependencies());

			if (entity instanceof AtomicChange) {
				result.add(((AtomicChange) entity).getChangeSubject());
			}

		} else if (entity instanceof FamixPackage) {
			result.add(((FamixPackage) entity).getBelongsToPackage());
		} else if (entity instanceof FamixClass) {
			result.add(((FamixClass) entity).getBelongsToPackage());
			result.add(((FamixClass) entity).getBelongsToClass());
		} else if (entity instanceof FamixMethod) {
			result.add(((FamixMethod) entity).getBelongsToClass());
			result.add(((FamixMethod) entity).getDeclaredReturnClass());
		} else if (entity instanceof FamixAttribute) {
			result.add(((FamixAttribute) entity).getBelongsToClass());
			result.add(((FamixAttribute) entity).getDeclaredClass());
		} else if (entity instanceof FamixInvocation) {
			result.add(((FamixInvocation) entity).getCandidate());
			result.add(((FamixInvocation) entity).getInvokedBy());
		} else if (entity instanceof FamixLocalVariable) {
			result.add(((FamixLocalVariable) entity).getDeclaredClass());
			result.add(((FamixLocalVariable) entity).getBelongsToBehaviour());
		} else if (entity instanceof FamixInheritanceDefinition) {
			result.add(((FamixInheritanceDefinition) entity).getSubClass());
			result.add(((FamixInheritanceDefinition) entity).getSuperClass());
		}
		return result.toArray();
	}
}
