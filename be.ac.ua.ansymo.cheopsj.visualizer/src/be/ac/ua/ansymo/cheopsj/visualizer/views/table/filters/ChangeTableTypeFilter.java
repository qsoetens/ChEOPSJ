/***************************************************
 * Copyright (c) 2014 Nicolas Demarbaix
 * 
 * Contributors: 
 * 		Nicolas Demarbaix - Initial Implementation
 ***************************************************/
package be.ac.ua.ansymo.cheopsj.visualizer.views.table.filters;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import be.ac.ua.ansymo.cheopsj.model.changes.IChange;

/**
 * Change table for filtering change and famix types
 * @author nicolasdemarbaix
 *
 */
public class ChangeTableTypeFilter extends ViewerFilter {

	private static final String DEFAULT_CHANGE_TYPE = "ALL_CHANGES";
	private static final String DEFAULT_FAMIX_TYPE = "ALL_FAMIX";
	
	private String change_type = DEFAULT_CHANGE_TYPE;
	private String famix_type = DEFAULT_FAMIX_TYPE;
	
	/**
	 * Set the default value for the famix type
	 */
	public void setFamixDefault() {
		this.famix_type = DEFAULT_FAMIX_TYPE;
	}
	
	/**
	 * Set the default value for the change type
	 */
	public void setChangeDefault() {
		this.change_type = DEFAULT_CHANGE_TYPE;
	}
	
	/**
	 * Set the famix type for the filter
	 * @param type (String) famix type
	 */
	public void setFamixType(String type) {
		this.famix_type = type;
	}
	
	/**
	 * Set the change type for the filter
	 * @param type (String) change type
	 */
	public void setChangeType(String type) {
		this.change_type = type;
	}
	
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (this.change_type.equals(DEFAULT_CHANGE_TYPE) && this.famix_type.equals(DEFAULT_FAMIX_TYPE))
			return true;
		
		IChange change = (IChange) element;
		
		if (this.famix_type.equals(change.getFamixType()) && this.change_type.equals(DEFAULT_CHANGE_TYPE)) {
			return true;
		} else if (this.change_type.equals(change.getChangeType()) && this.famix_type.equals(DEFAULT_FAMIX_TYPE)) {
			return true;
		} else if (this.change_type.equals(change.getChangeType()) && this.famix_type.equals(change.getFamixType())) {
			return true;
		}
		
		return false;
	}

}
