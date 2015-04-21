package be.ac.ua.ansymo.cheopsj.visualizer.data;

import java.util.Date;

import org.eclipse.swt.graphics.Point;

import be.ac.ua.ansymo.cheopsj.model.changes.AtomicChange;
import be.ac.ua.ansymo.cheopsj.model.changes.IChange;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixEntity;

public class TimelinePoint {
	private IChange change;
	private Point timelinePoint;
	private String entityID;
	private String changeDate;
	
	public TimelinePoint(String entID, String changeDate, Point tPoint) {
		this.timelinePoint = tPoint;
		
		this.changeDate = changeDate;
		this.entityID = entID;
	}
	
	public boolean isPointForChange(String entID, Date cDate) {
		@SuppressWarnings("deprecation")
		String sDate = cDate.getDate() + "-" + (cDate.getMonth() +1) 
				+ "-" + (cDate.getYear()+1900);
		if (this.entityID.equals(entID) && this.changeDate.equals(sDate)) {
			return true;
		}
		return false;
	}
	
	public boolean isPointForChange(String entID, String cDate) {
		if (this.entityID.equals(entID) && this.changeDate.equals(cDate)) {
			return true;
		}
		return false;
	}
	
	public String getEntityID() {
		return this.entityID;
	}
	
	public String getChangeDate() {
		return this.changeDate;
	}
	
	public Point getTimelinePoint() {
		return this.timelinePoint;
	}
	
	public String toString() {
		String result = "";
		result += "\n---- Timeline Point ----\n";
		result += "   Entity:      " + this.entityID + "\n";
		result += "   Change date: " + this.changeDate + "\n";
		result += "   At point:    " + this.timelinePoint.toString() + "\n";
		return result;
	}
}
