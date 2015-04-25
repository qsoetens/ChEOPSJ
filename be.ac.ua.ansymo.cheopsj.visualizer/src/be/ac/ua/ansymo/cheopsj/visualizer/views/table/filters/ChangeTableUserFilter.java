package be.ac.ua.ansymo.cheopsj.visualizer.views.table.filters;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import be.ac.ua.ansymo.cheopsj.model.changes.IChange;
import be.ac.ua.ansymo.cheopsj.visualizer.data.DataStore;

public class ChangeTableUserFilter extends ViewerFilter {

	private String searchString;
	
	public void setSearchString(String search) {
		this.searchString = ".*" + search + ".*";
	}
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (searchString == null || searchString.length() == 0) 
			return true;
		
		if (searchString.equals("All")) 
			return true;
		
		IChange change = (IChange) element;
		if (searchString.equals("Other...") && !DataStore.getInstance().getUserNames().contains(change.getUser()))
			return true;
		
		if (change.getUser().matches(searchString))
			return true;
		
		return false;
	}

}
