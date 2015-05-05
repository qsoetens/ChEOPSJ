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
 * Change Table Name filter class
 * @author nicolasdemarbaix
 *
 */
public class ChangeTableNameFilter extends ViewerFilter {
	
	private String searchString;
	
	/**
	 * Set the search string for the filter
	 * @param search (String) filter query
	 */
	public void setSearchString(String search) {
		this.searchString = ".*" + search +  ".*";
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (searchString == null || searchString.length() == 0)
			return true;
		
		IChange change = (IChange) element;
		if (change.getName().matches(searchString))
			return true;
		
		return false;
	}

}
