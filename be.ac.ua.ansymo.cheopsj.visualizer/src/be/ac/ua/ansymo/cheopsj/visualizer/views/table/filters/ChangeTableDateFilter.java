package be.ac.ua.ansymo.cheopsj.visualizer.views.table.filters;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import be.ac.ua.ansymo.cheopsj.model.changes.IChange;

public class ChangeTableDateFilter extends ViewerFilter {

	private Date from;
	private Date to;
	
	public void setStartDate(Date start) {
		this.from = start;
		if (this.to == null) {
			this.to = Calendar.getInstance().getTime();
		} else if (this.to.before(this.from)) {
			this.to.setTime(this.from.getTime() + 60000);
		}
	}
	
	public void setEndDate(Date end) {
		this.to = end;
		if (this.from == null) {
			this.from = this.to;
			this.from.setTime(this.from.getTime() - 60000);
		} else if (this.from.after(this.to)) {
			this.from.setTime(this.to.getTime() - 60000);
		}
	}
	
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (this.from == null || this.to == null)
			return true;
		
		Date elemDate = new Date(((IChange) element).getTimeStamp().getTime());
		
		if (this.from.before(elemDate) && this.to.after(elemDate)) {
			return true;
		} else {
			if (this.from.equals(elemDate)) {
				return true;
			} else if (this.to.equals(elemDate)) {
				return true;
			} else {
				return false;
			}
		}
	}

}
