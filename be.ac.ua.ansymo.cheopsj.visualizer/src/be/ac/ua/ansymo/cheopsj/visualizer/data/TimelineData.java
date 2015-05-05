/***************************************************
 * Copyright (c) 2014 Nicolas Demarbaix
 * 
 * Contributors: 
 * 		Nicolas Demarbaix - Initial Implementation
 ***************************************************/
package be.ac.ua.ansymo.cheopsj.visualizer.data;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import org.eclipse.swt.graphics.Point;

import be.ac.ua.ansymo.cheopsj.model.changes.AtomicChange;
import be.ac.ua.ansymo.cheopsj.model.changes.IChange;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixAssociation;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixEntity;
import be.ac.ua.ansymo.cheopsj.visualizer.util.DateUtil;

/**
 * Wrapper class for the data used in the timeline
 * @author nicolasdemarbaix
 *
 */
public class TimelineData {
	private Map<String, List<IChange>> mec; // MEC = Map Element Change

	private Vector<TimelinePoint> point_vector; // all points that have been used on the timeline view
	
	private Vector<String> entity_names; // names of all entities visible on the timeline
	private Vector<String> change_dates; // dates of all changes visible on the timeline
	private String focusEntity; // the name of the entity on which the user is focusing
	
	private Date earliestChangeDate = null;
	private Date currentChangeDate = null;
	private Date latestChangeDate = null;
	
	/**
	 * Public constructor
	 * @param data (Map<String, List<IChange>>) Data set of all entities and their corresponding changes
	 * @param earliest (java.util.Date) earliest change date
	 * @param latest (java.util.Date) latest change date
	 * @param focus (String) name of the entity under focus
	 */
	public TimelineData(Map<String, List<IChange>> data, Date earliest, Date latest, String focus) {
		this.mec = data;
		this.earliestChangeDate = earliest;
		this.latestChangeDate = latest;
		this.focusEntity = focus;
		
		this.entity_names = new Vector<String>();
		for (Entry<String, List<IChange>> entry : this.mec.entrySet()) {
			this.entity_names.add(entry.getKey());
		}
		
		if (this.earliestChangeDate != null && this.latestChangeDate != null) 
			if (this.earliestChangeDate.after(this.latestChangeDate))
				System.err.println("The last change occured before the first change. THIS IS WRONG!!");
		
		this.currentChangeDate = DateUtil.getInstance().getPrevious(this.earliestChangeDate);

		this.point_vector = new Vector<TimelinePoint>();
		this.change_dates = new Vector<String>();
	}
	
	/**
	 * Public constructor
	 * @param data (Map<String, List<IChange>>) Data set of all entities and their corresponding changes
	 * @param earliest (java.util.Date) earliest change date
	 * @param latest (java.util.Date) latest change date
	 * @param focus (String) name of the entity under focus
	 * @param date_vec (Vector<java.util.Date>) List of all dates to be displayed in the time line
	 */
	@SuppressWarnings("deprecation")
	public TimelineData(Map<String, List<IChange>> data, Date earliest, Date latest, String focus, 
						Vector<Date> date_vec) {
		this(data, earliest, latest, focus);
		Collections.sort(date_vec, Collections.reverseOrder());
		for (int i = 0; i < date_vec.size(); ++i) {
			Date d = date_vec.get(i);
			String changeDateLabel = d.getDate() + "-" + (d.getMonth() +1) 
					+ "-" + (d.getYear()+1900);
			if (!this.change_dates.contains(changeDateLabel)) {
				this.change_dates.insertElementAt(changeDateLabel, 0);
			}
		}
	}
	
	/**
	 * Get all the changes for a certain entity
	 * @param key (String) unique name of the entity
	 * @return (List<IChange>) All changes for entity 'key'
	 */
	public List<IChange> getChangesForKey(String key) {
		return this.mec.get(key);
	}
	
	/**
	 * Get the Entity Change Map 
	 * @return (Map<String, List<IChange>>) The Entity Change Map for the current time line
	 */
	public Map<String, List<IChange>> getMEC() {
		return this.mec;
	}
	
	/**
	 * Get the next date based on the current change date
	 * @return (java.util.Date) next change date
	 */
	public Date getNextDate() {
		this.currentChangeDate = DateUtil.getInstance().getNext(this.currentChangeDate);
		return this.currentChangeDate;
	}
	
	/**
	 * Check whether the currentChangeDate does not equal the last change date
	 * @return (Boolean) true if a next date can be found given the specified conditions
	 */
	public boolean hasNextDate() {
		if (this.currentChangeDate == null) {
			return false;
		}
		
		if (this.currentChangeDate.equals(this.latestChangeDate) || this.currentChangeDate.after(this.latestChangeDate)) {
			return false;
		}
		return true;
	}
	
	/**
	 * Get the number of days between the earliest and the latest recorded change
	 * @return (int) the number of days
	 */
	public int getDaysBetween() {
		if (this.earliestChangeDate == null || this.latestChangeDate == null) {
			return 0;
		}
		
		return DateUtil.getInstance().daysBetween(this.earliestChangeDate, this.latestChangeDate);
	}
	
	/**
	 * Get the total number of entities for this timeline
	 * @return (int) total number of entities
	 */
	public int getNumberOfEntities() {
		return this.mec.entrySet().size();
	}
	
	/**
	 * Get the unique name of an entity given its index for the list of entity names
	 * @param index (int) list index
	 * @return (String) unique name of entity
	 */
	public String getEntityIDForIndex(int index) {
		return this.entity_names.get(index);
	}
	
	/**
	 * Get the unique name of the focus entity for this timeline
	 * @return (String) unique name of entity
	 */
	public String getEntityOfFocus() {
		return this.focusEntity;
	}
	
	/**
	 * Add a point to this timeline
	 * @param data (TimelinePoint) the point to add
	 */
	public void addTimelinePoint(TimelinePoint data) {
		this.point_vector.add(data);
	}
	
	/**
	 * Get the labels of all change dates
	 * @return (Vector<String>) Change date labels
	 */
	public Vector<String> getChangeDateLabels() {
		return this.change_dates;
	}
	
	/**
	 * Get the change for a given entity name and change date
	 * @param entID (String) unique name of entity
	 * @param cDate (String) string representation of the change date
	 * @return (IChange) the corresponding change
	 */
	@SuppressWarnings("deprecation")
	public IChange getChangeForKey(String entID, String cDate) {
		List<IChange> changes = this.mec.get(entID);
		if (changes == null) {
			return null;
		}
		
		for (IChange change : changes) {
			String sDate = change.getTimeStamp().getDate() + "-" + (change.getTimeStamp().getMonth() +1) 
					+ "-" + (change.getTimeStamp().getYear()+1900);
			if (sDate.equals(cDate)) {
				return change;
			}
		}
		
		return null;
	}
	
	/**
	 * Get all the dependencies of a change for a certain entity and change date
	 * @param entID (String) unique name of entity
	 * @param cDate (String) string representation of the change date
	 * @return (Vector<IChange>) All dependencies of the given change
	 */
	public Vector<IChange> getDependenciesForKey(String entID, String cDate) {
		IChange change = this.getChangeForKey(entID, cDate);
		if (change == null) {
			return null;
		}
		
		Vector<IChange> dep = new Vector<IChange>();
		for (IChange c : ((AtomicChange)change).getStructuralDependencies()) {
			dep.add(c);
		}
		for (IChange c : ((AtomicChange)change).getSemanticalDependencies()) {
			dep.add(c);
		}
		return dep;		
	}
	
	/**
	 * Get all dependent changes of a change for a certain entity and change date
	 * @param entID (String) unique name of entity
	 * @param cDate (String) string representation of the change date
	 * @return (Vector<IChange>) All dependents of the given change
	 */
	public Vector<IChange> getDependentsForKey(String entID, String cDate) {
		IChange change = this.getChangeForKey(entID, cDate);
		if (change == null) {
			return null;
		}
		
		Vector<IChange> dep = new Vector<IChange>();
		for (IChange c : ((AtomicChange)change).getStructuralDependees()) {
			dep.add(c);
		}
		for (IChange c : ((AtomicChange)change).getSemanticalDependees()) {
			dep.add(c);
		}
		return dep;	
	}
	
	/**
	 * Get the point on the timeline for a certain change
	 * @param change (IChange) the change under consideration
	 * @return (org.eclipse.swt.graphics.Point) The point on the view
	 */
	public Point getPointForChange(IChange change) {
		Date cDate = change.getTimeStamp();
		
		String entId;
		if (((AtomicChange) change).getChangeSubject() instanceof FamixAssociation) {
			entId = ((FamixAssociation) ((AtomicChange) change).getChangeSubject()).getStringRepresentation();
		} else {
			entId = ((FamixEntity) ((AtomicChange) change).getChangeSubject()).getUniqueName();
		}
		for (int i = 0; i < this.point_vector.size(); ++i) {
			if (this.point_vector.get(i).isPointForChange(entId, cDate)) {
				return this.point_vector.get(i).getTimelinePoint();
			}
		}
		
		return null;
	}
	
	/**
	 * Check whether a change occurred for a certain entity on a certain date
	 * @param entID (String) unique name of entity
	 * @param sDate (String) string representation of the change date
	 * @return (boolean) true if the change exists
	 */
	@SuppressWarnings("deprecation")
	public boolean changeOccured(String entID, String sDate) {
		for (IChange change : this.mec.get(entID)) {
			Date date = change.getTimeStamp();
			String changeDate = date.getDate() + "-" + (date.getMonth() +1) 
					+ "-" + (date.getYear()+1900);
			if (changeDate.equals(sDate)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Get all the points of the timeline
	 * @return (Vector<TimelinePoint>) time line points
	 */
	public Vector<TimelinePoint> getTimelinePoints() {
		return this.point_vector;
	}
	
	/**
	 * Get the location on the time line view of all dependents of a certain change
	 * @param entID (String) unique entity name
	 * @param cDate (String) string representation of the change date
	 * @return (Vector<org.eclipse.swt.graphics.Point>) All points on the time line view for the given change
	 */
	@SuppressWarnings({ "deprecation" })
	public Vector<Point> getDependentsLocations(String entID, String cDate) {
		Vector<Point> dep_loc = new Vector<Point>();

		IChange change = null;
		for (IChange c : this.mec.get(entID)) {
			Date d = c.getTimeStamp();
			String sDate = d.getDate() + "-" + (d.getMonth() +1) 
					+ "-" + (d.getYear()+1900);
			if (sDate.equals(cDate)) {
				change = c;
				break;
			}
		}
		
		for (IChange c : ((AtomicChange)change).getStructuralDependees()) {
			Point p = getPointForChange(c);
			if (p == null)
				continue;
			dep_loc.add(p);
		}
		for (IChange c : ((AtomicChange)change).getSemanticalDependees()) {
			Point p = getPointForChange(c);
			if (p == null)
				continue;
			dep_loc.add(p);
		}
		return dep_loc;
	}
	
	/**
	 * Get the location on the time line view of all dependencies of a certain change
	 * @param entID (String) unique entity name
	 * @param cDate (String) string representation of the change date
	 * @return (Vector<org.eclipse.swt.graphics.Point>) All points on the time line view for the given change
	 */
	@SuppressWarnings("deprecation")
	public Vector<Point> getDependenciesLocations(String entID, String cDate) {
		Vector<Point> dep_loc = new Vector<Point>();
		IChange change = null;
		for (IChange c : this.mec.get(entID)) {
			Date d = c.getTimeStamp();
			String sDate = d.getDate() + "-" + (d.getMonth() +1) 
					+ "-" + (d.getYear()+1900);
			if (sDate.equals(cDate)) {
				change = c;
				break;
			}
		}
		
		for (IChange c : ((AtomicChange)change).getStructuralDependencies()) {
			Point p = getPointForChange(c);
			if (p == null)
				continue;
			dep_loc.add(p);
		}
		for (IChange c : ((AtomicChange)change).getSemanticalDependencies()) {
			Point p = getPointForChange(c);
			if (p == null)
				continue;
			dep_loc.add(p);
		}
		return dep_loc;
	}
	
	/**
	 * Get the location of the focus entity
	 * @param entID (String) unique name of entity
	 * @param cDate (String) string representation of the change date
	 * @return (org.eclipse.swt.graphics.Point) location on the time line for the given change (can be null)
	 */
	public Point getPointForFocusEntity(String entID, String cDate) {
		
		for (TimelinePoint tlp : this.point_vector) {
			if (tlp.isPointForChange(entID, cDate)) {
				return tlp.getTimelinePoint();
			}
		}
		return null;
	}
	
}
