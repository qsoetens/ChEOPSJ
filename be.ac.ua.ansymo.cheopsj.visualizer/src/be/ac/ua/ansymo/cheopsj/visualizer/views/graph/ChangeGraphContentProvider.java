/***************************************************
 * Copyright (c) 2014 Nicolas Demarbaix
 * 
 * Contributors: 
 * 		Nicolas Demarbaix - Initial Implementation
 ***************************************************/
package be.ac.ua.ansymo.cheopsj.visualizer.views.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.viewers.IGraphEntityContentProvider;

import be.ac.ua.ansymo.cheopsj.model.ModelManager;
import be.ac.ua.ansymo.cheopsj.model.ModelManagerEvent;
import be.ac.ua.ansymo.cheopsj.model.ModelManagerListener;
import be.ac.ua.ansymo.cheopsj.model.changes.Change;
import be.ac.ua.ansymo.cheopsj.model.changes.IChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Subject;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixAttribute;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixClass;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixEntity;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixInvocation;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixMethod;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixPackage;

/**
 * The content provider for the Change Graph view
 * @author nicolasdemarbaix
 *
 */
public class ChangeGraphContentProvider implements IGraphEntityContentProvider, ModelManagerListener {
	private GraphViewer viewer = null;
	private ModelManager manager = null;
	private String focusEntityID = null;
	private boolean focusEntityIsPackage = false;
	private boolean focusEntityIsClass = false;
	private boolean focusEntityIsMethod = false;
	private boolean focusEntityIsAttribute = false;
	private boolean focusEntityIsInvocation = false;
	

	/* =============================
	 * IGRAPHCONTENTPROVIDER METHODS
	 * =============================
	 */
	@Override
	public void dispose() {
		
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
		
		if (focusEntityIsPackage) {
			result.addAll(getElementsForPackageFocus());
		} else if (focusEntityIsClass) {
			result.addAll(getElementsForClassFocus());
		} else if (focusEntityIsMethod) {
			result.addAll(getElementsForMethodFocus());
		} else if (focusEntityIsAttribute) {
			result.addAll(getElementsForAttributeFocus());
		} else if (focusEntityIsInvocation) {
			result.addAll(getElementsForInvocationFocus());
		}
		
		return result.toArray();
	}
	
	/**
	 * Get all elements that should be displayed for the current focus element (in case of package)
	 * @return (Collection<Object>) All famixobjects that should be displayed
	 */
	private Collection<Object> getElementsForPackageFocus() {
		Collection<Object> result = new ArrayList<Object>();
		for (Entry<String, FamixPackage> entry : this.manager.getFamixPackagesMap().entrySet()) {
			if (entry.getValue().getUniqueName().equals(this.focusEntityID)) {
				result.add(entry.getValue());
				System.out.println("Result added for: " + entry.getValue().getUniqueName());
			}
			
			if (entry.getValue().getBelongsToPackage() != null) {
				if (entry.getValue().getBelongsToPackage().getUniqueName().equals(this.focusEntityID)) {
					result.add(entry.getValue());
					System.out.println("Result added for: " + entry.getValue().getUniqueName());
				}
			}
		}
		for (Entry<String, FamixClass> entry : this.manager.getFamixClassesMap().entrySet()) {
			if (entry.getValue().getBelongsToPackage() != null) {
				if (entry.getValue().getBelongsToPackage().getUniqueName().equals(this.focusEntityID)) {
					result.add(entry.getValue());
					System.out.println("Result added for: " + entry.getValue().getUniqueName());
				}
			}
		}
		return result;
	}
	
	/**
	 * Get all elements that should be displayed for the current focus element (in case of class)
	 * @return (Collection<Object>) All famixobjects that should be displayed
	 */
	private Collection<Object> getElementsForClassFocus() {
		Collection<Object> result = new ArrayList<Object>();
		for (Entry<String, FamixClass> entry : this.manager.getFamixClassesMap().entrySet()) {
			if (entry.getValue().getUniqueName().equals(focusEntityID)) {
				result.add(entry.getValue());
				System.out.println("Result added for: " + entry.getValue().getUniqueName());
			}
			
			if (entry.getValue().getBelongsToClass() != null) {
				if (entry.getValue().getBelongsToClass().getUniqueName().equals(focusEntityID)) {
					result.add(entry.getValue());
					System.out.println("Result added for: " + entry.getValue().getUniqueName());
				}
			}
		}
		for (Entry<String, FamixMethod> entry : this.manager.getFamixMethodsMap().entrySet()) {
			if (entry.getValue().getBelongsToClass() != null) {
				if (entry.getValue().getBelongsToClass().getUniqueName().equals(focusEntityID)) {
					result.add(entry.getValue());
					System.out.println("Result added for: " + entry.getValue().getUniqueName());
				}
			}
		}
		for (Entry<String, FamixAttribute> entry : this.manager.getFamixFieldsMap().entrySet()) {
			if (entry.getValue().getBelongsToClass() != null) {
				if (entry.getValue().getBelongsToClass().getUniqueName().equals(focusEntityID)) {
					result.add(entry.getValue());
					System.out.println("Result added for: " + entry.getValue().getUniqueName());
				}
			}
		}
		return result;
	}
	
	/**
	 * Get all elements that should be displayed for the current focus element (in case of method)
	 * @return (Collection<Object>) All famixobjects that should be displayed
	 */
	private Collection<Object> getElementsForMethodFocus() {
		Collection<Object> result = new ArrayList<Object>();
		for (Entry<String, FamixMethod> entry : this.manager.getFamixMethodsMap().entrySet()) {
			if (entry.getValue().getUniqueName().equals(focusEntityID)) {
				result.add(entry.getValue());
				System.out.println("Result added for: " + entry.getValue().getUniqueName());
			}
		}
		for (Entry<String, FamixInvocation> entry : this.manager.getFamixInvocationsMap().entrySet()) {
			if (entry.getValue().getInvokedBy() != null) {
				if (entry.getValue().getInvokedBy().getUniqueName().equals(focusEntityID)) {
					result.add(entry.getValue());
					System.out.println("Result added for: " + entry.getValue().getStringRepresentation());
				}
			}
		}
		
		return result;
	}
	
	/**
	 * Get all elements that should be displayed for the current focus element (in case of attribute)
	 * @return (Collection<Object>) All famixobjects that should be displayed
	 */
	private Collection<Object> getElementsForAttributeFocus() {
		Collection<Object> result = new ArrayList<Object>();
		for (Entry<String, FamixAttribute> entry : this.manager.getFamixFieldsMap().entrySet()) {
			if (entry.getValue().getUniqueName().equals(focusEntityID)) {
				result.add(entry.getValue());
				System.out.println("Result added for: " + entry.getValue().getUniqueName());
			}
		}
		return result;
	}
	
	/**
	 * Get all elements that should be displayed for the current focus element (in case of invocation)
	 * @return (Collection<Object>) All famixobjects that should be displayed
	 */
	private Collection<Object> getElementsForInvocationFocus() {
		Collection<Object> result = new ArrayList<Object>();
		for (Entry<String, FamixInvocation> entry : this.manager.getFamixInvocationsMap().entrySet()) {
			if (entry.getValue().getStringRepresentation().equals(focusEntityID)) {
				result.add(entry.getValue());
				System.out.println("Result added for: " + entry.getValue().getStringRepresentation());
			}
		}
		return result;
	}
	
	@Override
	public Object[] getConnectedTo(Object entity) {
		Collection<Object> result = new ArrayList<Object>();
		
		if (entity instanceof FamixPackage) {
			FamixPackage fp = (FamixPackage)entity;
			if (focusEntityIsPackage) {
				if (fp.getBelongsToPackage() != null) {
					if (fp.getBelongsToPackage().getUniqueName().equals(focusEntityID)) {
						result.add(fp.getBelongsToPackage());
						System.out.println("Connection added from " + fp.getUniqueName() + " to " + fp.getBelongsToPackage().getUniqueName());
					}
				}
			}
		}
		if (entity instanceof FamixClass) {
			FamixClass fc = (FamixClass)entity;
			if (focusEntityIsPackage) {
				if (fc.getBelongsToPackage() != null) {
					if (fc.getBelongsToPackage().getUniqueName().equals(focusEntityID)) {
						result.add(fc.getBelongsToPackage());
						System.out.println("Connection added from " + fc.getUniqueName() + " to " + fc.getBelongsToPackage().getUniqueName());
					}
				}
			} else if (focusEntityIsClass) {
				if (fc.getBelongsToClass() != null) {
					if (fc.getBelongsToClass().getUniqueName().equals(focusEntityID)) {
						result.add(fc.getBelongsToClass());
						System.out.println("Connection added from " + fc.getUniqueName() + " to " + fc.getBelongsToClass().getUniqueName());
					}
				}
			}
		} else if (entity instanceof FamixMethod) {
			FamixMethod fm = (FamixMethod)entity;
			if (fm.getBelongsToClass() != null) {
				if (fm.getBelongsToClass().getUniqueName().equals(focusEntityID)) {
					result.add(fm.getBelongsToClass());
					System.out.println("Connection added from " + fm.getUniqueName() + " to " + fm.getBelongsToClass().getUniqueName());
				}
			}
		} else if (entity instanceof FamixAttribute) {
			FamixAttribute fa = (FamixAttribute)entity;
			if (fa.getBelongsToClass() != null) {
				if (fa.getBelongsToClass().getUniqueName().equals(focusEntityID)) {
					result.add(fa.getBelongsToClass());
					System.out.println("Connection added from " + fa.getUniqueName() + " to " + fa.getBelongsToClass().getUniqueName());
				}
			}
		} else if (entity instanceof FamixInvocation) {
			FamixInvocation fi = (FamixInvocation)entity;
			if (fi.getInvokedBy() != null) {
				if (fi.getInvokedBy().getUniqueName().equals(focusEntityID)) {
					result.add(fi.getInvokedBy());
					System.out.println("Connection added from " + fi.getStringRepresentation() + " to " + fi.getInvokedBy().getUniqueName());
				}
			}
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
	
	/**
	 * Update the viewer when a modelmanager event occurs
	 * @param event (ModelManagerEvent) the event
	 */
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

	/**
	 * Refresh the viewer
	 */
	public void refresh() {
		viewer.refresh();
	}

	/**
	 * Refresh the viewer and re-apply the layout
	 */
	public void updateAndRefresh() {
		viewer.refresh();
		viewer.applyLayout();
	}
	
	/* ============================
	 * CHANGE CONTENT METHODS
	 * ============================
	 */
	/**
	 * Set the focus entity in the change graph content provider
	 * @param focus (String) unique name of focus entity
	 */
	public void setFocusEntity(String focus) {
		this.focusEntityID = focus;
		for (Subject sub : this.manager.getFamixEntities()) {
			if (sub instanceof FamixEntity) {
				if (((FamixEntity)sub).getUniqueName().equals(focus)) {
					setCheck(sub.getFamixType());
				}
			} else if (sub instanceof FamixInvocation) {
				if (((FamixInvocation)sub).getStringRepresentation().equals(focus)) {
					setCheck(sub.getFamixType());
				}
			}
		}
		
	}
	
	/**
	 * Set the necessary checks for the getElements methods to distinguish between focus entity types
	 * @param type (String) the type of the focus entity
	 */
	private void setCheck(String type) {
		allChecksToFalse();
		if (type.equals("Package")) {
			focusEntityIsPackage = true;
		} else if (type.equals("Class")) {
			focusEntityIsClass = true;
		} else if (type.equals("Method")) {
			focusEntityIsMethod = true;
		} else if (type.equals("Attribute")) {
			focusEntityIsAttribute = true;
		} else if (type.equals("Invocation")) {
			focusEntityIsInvocation = true;
		}
	}
	
	/**
	 * Reset all checks
	 */
	private void allChecksToFalse() {
		focusEntityIsPackage = false;
		focusEntityIsClass = false;
		focusEntityIsMethod = false;
		focusEntityIsAttribute = false;
		focusEntityIsInvocation = false;
	}
	
	/**
	 * Move the current focus to the parent entity of the focus entity
	 */
	public void goToParent() {
		for (Subject sub : this.manager.getFamixEntities()) {
			if (sub instanceof FamixPackage) {
				FamixPackage fp = (FamixPackage)sub;
				if (fp.getUniqueName().equals(focusEntityID)) {
					if (fp.getBelongsToPackage() != null) {
						this.setFocusEntity(fp.getBelongsToPackage().getUniqueName());
					}
				}
			} else if (sub instanceof FamixClass) {
				if (((FamixClass)sub).getUniqueName().equals(focusEntityID)) {
					FamixClass fc = (FamixClass)sub;
					if (fc.getBelongsToClass() != null) {
						this.setFocusEntity(fc.getBelongsToClass().getUniqueName());
					} else {
						this.setFocusEntity(fc.getBelongsToPackage().getUniqueName());
					}
				}
			} else if (sub instanceof FamixMethod) {
				FamixMethod fm = (FamixMethod)sub;
				if (fm.getUniqueName().equals(focusEntityID)) {
					this.setFocusEntity(fm.getBelongsToClass().getUniqueName());
				}
				
			} else if (sub instanceof FamixAttribute) {
				FamixAttribute fa = (FamixAttribute)sub;
				if (fa.getUniqueName().equals(focusEntityID)) {
					this.setFocusEntity(fa.getBelongsToClass().getUniqueName());
				}
			} else if (sub instanceof FamixInvocation) {
				if (((FamixInvocation)sub).getStringRepresentation().equals(focusEntityID)) {
					this.setFocusEntity(((FamixInvocation)sub).getInvokedBy().getUniqueName());
				}
			}
		}
	}

}
