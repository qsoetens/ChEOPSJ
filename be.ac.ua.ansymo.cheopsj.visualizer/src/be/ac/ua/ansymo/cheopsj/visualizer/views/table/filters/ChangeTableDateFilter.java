/***************************************************
 * Copyright (c) 2014 Nicolas Demarbaix
 * 
 * Contributors: 
 * 		Nicolas Demarbaix - Initial Implementation
 ***************************************************/
package be.ac.ua.ansymo.cheopsj.visualizer.views.table.filters;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import be.ac.ua.ansymo.cheopsj.model.changes.IChange;

/**
 * Change Table Date Filter
 * @author nicolasdemarbaix
 *
 */
public class ChangeTableDateFilter extends ViewerFilter {

	private Date from;
	private Date to;
	
	/**
	 * Set the start date for the filter
	 * @param start (java.util.Date) start date
	 */
	public void setStartDate(Date start) {
		this.from = start;
		if (this.to == null) {
			this.to = Calendar.getInstance().getTime();
		} else if (this.to.before(this.from)) {
			this.to.setTime(this.from.getTime() + 60000);
		}
	}
	
	/**
	 * Set end date for the filter
	 * @param end (java.util.Date) end date
	 */
	public void setEndDate(Date end) {
		this.to = end;
		if (this.from == null) {
			this.from = this.to;
			this.from.setTime(this.from.getTime() - 60000);
		} else if (this.from.after(this.to)) {
			this.from.setTime(this.to.getTime() - 60000);
		}
	}
	
	/**
	 * Set both the start and end date of the filter
	 * @param start (java.util.Date) start date
	 * @param end (java.util.Date) end date
	 */
	public void setDates(Date start, Date end) {
		this.from = start;
		this.to = end;
		if (this.to.before(this.from)) {
			this.to.setTime(this.from.getTime() + (24 * 60 * 60 * 1000));
		}
	}
	
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (this.from == null || this.to == null)
			return true;
		
		Date elemDate = ((IChange) element).getTimeStamp();
		
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
