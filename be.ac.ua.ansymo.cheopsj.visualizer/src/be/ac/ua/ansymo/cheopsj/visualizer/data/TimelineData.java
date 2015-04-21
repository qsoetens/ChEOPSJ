package be.ac.ua.ansymo.cheopsj.visualizer.data;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
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

public class TimelineData {
	private Map<String, List<IChange>> mec; // MEC = Map Element Change

	private Vector<TimelinePoint> point_vector;
	
	private Vector<String> entity_names;
	private Vector<String> change_dates;
	private String focusEntity;
	
	private Date earliestChangeDate = null;
	private Date currentChangeDate = null;
	private Date latestChangeDate = null;
	
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
	
	public List<IChange> getChangesForKey(String key) {
		return this.mec.get(key);
	}
	
	public Map<String, List<IChange>> getMEC() {
		return this.mec;
	}
	
	private Date getNextDate(Date current) {
		return DateUtil.getInstance().getNext(current);
	}
	
	public Date getNextDate() {
		this.currentChangeDate = DateUtil.getInstance().getNext(this.currentChangeDate);
		return this.currentChangeDate;
	}
	
	public boolean hasNextDate() {
		if (this.currentChangeDate == null) {
			return false;
		}
		
		if (this.currentChangeDate.equals(this.latestChangeDate) || this.currentChangeDate.after(this.latestChangeDate)) {
			return false;
		}
		return true;
	}
	
	public int getDaysBetween() {
		if (this.earliestChangeDate == null || this.latestChangeDate == null) {
			return 0;
		}
		
		return DateUtil.getInstance().daysBetween(this.earliestChangeDate, this.latestChangeDate);
	}
	
	public int getNumberOfEntities() {
		return this.mec.entrySet().size();
	}
	
	public String getEntityIDForIndex(int index) {
		return this.entity_names.get(index);
	}
	
	public String getEntityOfFocus() {
		return this.focusEntity;
	}
	
	public void addTimelinePoint(TimelinePoint data) {
		this.point_vector.add(data);
	}
	
	public Vector<String> getChangeDateLabels() {
		return this.change_dates;
	}
	
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
	
	public void printTimelinePoints() {
		for (int i = 0; i < this.point_vector.size(); ++i) {
			System.out.println(this.point_vector.get(i).toString());
		}
		System.out.println("DATA STORE CONTAINS " + this.point_vector.size() + " TIMELINE POINTS");
	}
	
	public Vector<TimelinePoint> getTimelinePoints() {
		return this.point_vector;
	}
	
	public Vector<Point> getDependentsLocations(String entID, String cDate) {
		Vector<Point> dep_loc = new Vector<Point>();
		
		/*for (DependencyData data : this.dependency_vector) {
			String source = ((FamixEntity) ((AtomicChange) data.getSource()).getChangeSubject()).getUniqueName();
			Date d = data.getSource().getTimeStamp();
			String sDate = d.getDate() + "-" + (d.getMonth() +1) 
					+ "-" + (d.getYear()+1900);;
			if (source.equals(entID) && cDate.equals(sDate)) {
				dep_loc.add(this.getPointForChange(data.getTarget()));
			}
		}*/
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
	
	public Vector<Point> getDependenciesLocations(String entID, String cDate) {
		Vector<Point> dep_loc = new Vector<Point>();
		/*for (DependencyData data : this.dependency_vector) {
			String target = ((FamixEntity) ((AtomicChange) data.getTarget()).getChangeSubject()).getUniqueName();
			Date d = data.getTarget().getTimeStamp();
			String sDate = d.getDate() + "-" + (d.getMonth() +1) 
					+ "-" + (d.getYear()+1900);;
			if (target.equals(entID) && cDate.equals(sDate)) {
				dep_loc.add(this.getPointForChange(data.getSource()));
			}
		}*/
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
	
	public Point getPointForFocusEntity(String entID, String cDate) {
		
		for (TimelinePoint tlp : this.point_vector) {
			if (tlp.isPointForChange(entID, cDate)) {
				return tlp.getTimelinePoint();
			}
		}
		return null;
	}
	
}
