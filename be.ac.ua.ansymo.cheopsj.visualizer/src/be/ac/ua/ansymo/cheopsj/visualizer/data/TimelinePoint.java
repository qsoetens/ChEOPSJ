/***************************************************
 * Copyright (c) 2014 Nicolas Demarbaix
 * 
 * Contributors: 
 * 		Nicolas Demarbaix - Initial Implementation
 ***************************************************/
package be.ac.ua.ansymo.cheopsj.visualizer.data;

import java.util.Date;

import org.eclipse.swt.graphics.Point;

/**
 * Wrapper class for information about a change point on the time line view
 * @author nicolasdemarbaix
 *
 */
public class TimelinePoint {
	private Point timelinePoint; // The location of the change
	private String entityID; // unique name of the entity
	private String changeDate; // string representation of the change date
	
	/**
	 * Public constructor
	 * @param entID (String) unique name of the entity
	 * @param changeDate (String) string representation of the change date
	 * @param tPoint (org.eclipse.swt.graphics.Point) location of the change on the timeline
	 */
	public TimelinePoint(String entID, String changeDate, Point tPoint) {
		this.timelinePoint = tPoint;
		
		this.changeDate = changeDate;
		this.entityID = entID;
	}
	
	/**
	 * Check whether this timeline point is the wrapper for a certain change
	 * @param entID (String) unique name of entity
	 * @param cDate (java.util.Date) change date
	 * @return (boolean) true if this is the timeline point for the given change
	 */
	public boolean isPointForChange(String entID, Date cDate) {
		@SuppressWarnings("deprecation")
		String sDate = cDate.getDate() + "-" + (cDate.getMonth() +1) 
				+ "-" + (cDate.getYear()+1900);
		if (this.entityID.equals(entID) && this.changeDate.equals(sDate)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Check whether this timeline point is the wrapper for a certain change
	 * @param entID (String) unique name of entity
	 * @param cDate (String) string representation of the change date
	 * @return (boolean) true if this is the timeline point for the given change
	 */
	public boolean isPointForChange(String entID, String cDate) {
		if (this.entityID.equals(entID) && this.changeDate.equals(cDate)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Getter for the entity ID
	 * @return (String) unique name of entity
	 */
	public String getEntityID() {
		return this.entityID;
	}
	
	/**
	 * Getter for the change date
	 * @return (String) string representation of the change date
	 */
	public String getChangeDate() {
		return this.changeDate;
	}
	
	/**
	 * Getter for the point
	 * @return (org.eclipse.swt.graphics.Point) location of the change
	 */
	public Point getTimelinePoint() {
		return this.timelinePoint;
	}
	
	/**
	 * toString method for the TimelinePoint class
	 */
	public String toString() {
		String result = "";
		result += "\n---- Timeline Point ----\n";
		result += "   Entity:      " + this.entityID + "\n";
		result += "   Change date: " + this.changeDate + "\n";
		result += "   At point:    " + this.timelinePoint.toString() + "\n";
		return result;
	}
}
