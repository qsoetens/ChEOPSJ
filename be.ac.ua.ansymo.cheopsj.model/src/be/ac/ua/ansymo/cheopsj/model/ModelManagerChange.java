package be.ac.ua.ansymo.cheopsj.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import be.ac.ua.ansymo.cheopsj.model.changes.Add;
import be.ac.ua.ansymo.cheopsj.model.changes.AtomicChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Change;
import be.ac.ua.ansymo.cheopsj.model.changes.IChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Remove;
import be.ac.ua.ansymo.cheopsj.model.changes.Subject;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixClass;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixPackage;

public class ModelManagerChange {

	//This List contains all changes
	private List<IChange> changes;
	
	//The ModelManagerChange is a Singleton entity, hence the constructor is private.
	//You should always call the static method getInstance() to get the ModelManager instance.
	private static ModelManagerChange INSTANCE = null;
	
	private ModelManagerChange() {
		changes = new ArrayList<IChange>();
	}
	
	/**
	 * The ModelMangerChange is a Singleton entity. Therefore the constructor is private.
	 * This method returns an instance of the ModelMangerChange. If no instance existed 
	 * before it will call the private constructor to create a new instance. Else
	 * It will return the existing instance. 
	 *  
	 * @return the Singleton ModelManager instance
	 */
	public static ModelManagerChange getInstance() {
		if (INSTANCE == null)
			INSTANCE = new ModelManagerChange();
		return INSTANCE;
	}
	
	/**
	 * @return the list of changes maintained in the ModelManagerChange
	 */
	public List<IChange> getChanges() {
		return changes;
	}
	
	public void setChanges(List<IChange> changes) {
		this.changes = changes;
	}
	
	/**
	 * Add a change to the ModelManager and alert the listeners of a new change.
	 * @param change
	 */
	public void addChange(Change change) {
		//add change to list
		changes.add(change);
		//alert listeners that a change was added		
		getModelManagerListeners().fireChangeAdded(change);
	}
	
	public ModelManagerListeners getModelManagerListeners() {
		return ModelManagerListeners.getInstance();
	}
	
	/**
	 * This method is used to count the number of changes in the model as well as how many of those changes are additions and removals.
	 * Used in the ChangeInspector View
	 * @return a string containing the counted changes
	 */
	public String getSummary() {
		int changeCount = changes.size();
		int addCount = 0;
		int removeCount = 0;
		for(IChange change: changes){
			if(change instanceof Add){
				addCount++;
			}else if(change instanceof Remove){
				removeCount++;
			}
		}
		return changeCount + " changes; " + addCount + " additions and " + removeCount + " removals";
	}
		
	/*
	 * For testing purposes only!
	 */
	public void clearModel() {
		INSTANCE = new ModelManagerChange();
	}
	
	/*public int getChangeCount(Subject sub) {
		int changeCount = 0;
		System.out.println("TOTAL NUMBER OF CHANGES = " + this.changes.size());
		for (IChange change : this.changes) {
			if (change instanceof AtomicChange) {
				System.out.println("Change instanceof atomicchange");
				System.out.println("SUB ID = " + sub.getID() + "\n"
						+ "CHANGE NAME = " + ((AtomicChange)change).getName());
				if (sub.getID().equals(((AtomicChange)change).getName())) {
					changeCount++;
				}
			}
		}
		return changeCount;
	}
	
<<<<<<< HEAD
	public int getAddCount(Subject sub) {
=======
	public int getChangeCount(Subject sub) {
		int changeCount = 0;
		
		try {
			ISession lSession = SessionHandler.getHandler().getCurrentSession();
			changeCount = lSession.query("select count(*) from Change as change inner join change.changeSubject as subject "
					+ "where subject.id =" + sub.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return changeCount;
	}
	
	public int getAddCount() {
>>>>>>> a57fb2214de6615e1b5131c7d6af7f2dcaf19100
		int addCount = 0;
		for (IChange change : this.changes) {
			if (change instanceof Add) {
				if (sub.getID().equals(((AtomicChange)change).getName())) {
					addCount++;
				}
			}
		}
		return addCount;
	}
	
<<<<<<< HEAD
	public int getRemoveCount(Subject sub) {
=======
	public int getAddCount(Subject sub) {
		int addCount = 0;
		
		try {
			ISession lSession = SessionHandler.getHandler().getCurrentSession();
			addCount = lSession.query("select count(*) from Add as add inner join add.changeSubject as subject "
					+ "where subject.id =" + sub.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return addCount;
	}
	
	public int getRemoveCount() {
>>>>>>> a57fb2214de6615e1b5131c7d6af7f2dcaf19100
		int removeCount = 0;
		for (IChange change : this.changes) {
			if (change instanceof Remove) {
				if (sub.getID().equals(((AtomicChange)change).getName())) {
					removeCount++;
				}
			}
		}
		return removeCount;
	}
<<<<<<< HEAD
=======
	
	public int getRemoveCount(Subject sub) {
		int removeCount = 0;
		
		try {
			ISession lSession = SessionHandler.getHandler().getCurrentSession();
			removeCount = lSession.query("select count(*) from Remove as change inner join change.changeSubject as subject "
					+ "where subject.id =" + sub.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return removeCount;
	}
	
	
	public Change getLatestChange(Subject sub){
		List<AtomicChange> changes = null;
		try {
			ISession lSession = SessionHandler.getHandler().getCurrentSession();
			 changes = lSession.query(
					 "select change from AtomicChange as change inner join change.changeSubject as subject "
					 + "where subject.id =" + sub.getId()
					 +" order by change.timeStamp desc"
					 ,AtomicChange.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(changes == null || changes.isEmpty())
			return null;
		else
			return (Change)changes.get(0);
	}
	
	public Add getLastestAddition(Subject sub){
		List<Add> changes = null;
		try {
			ISession lSession = SessionHandler.getHandler().getCurrentSession();
			 changes = lSession.query(
					 "select change from Add as change inner join change.changeSubject as subject "
					 + "where subject.id = " + sub.getId()
					 +" order by change.timeStamp desc"
					 ,Add.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(changes == null || changes.isEmpty())
			return null;
		else
			return changes.get(0);
	}
	
	public Remove getLatestRemoval(Subject sub){
		List<Remove> changes = null;
		try {
			ISession lSession = SessionHandler.getHandler().getCurrentSession();
			 changes = lSession.query(
					 "select change from Remove as change inner join change.changeSubject as subject"
					 + " where subject.id =" + sub.getId()
					 +" order by change.timeStamp desc"
					 ,Remove.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(changes == null || changes.isEmpty())
			return null;
		else
			return changes.get(0);
	}
>>>>>>> a57fb2214de6615e1b5131c7d6af7f2dcaf19100

	public Change getLatestChange(Subject sub) {
		Change latest = null;
		
		for (IChange change : this.changes) {
			if (change instanceof AtomicChange) {
				if (sub.getID().equals(((AtomicChange)change).getName())) {
					if (latest == null) {
						latest = (Change) change;
					} else if (latest.getTimeStamp().after(((AtomicChange)change).getTimeStamp())) {
						latest = (Change) change;
					} else {
						// Do nothing
					}
				}
			}
		}
		
		return latest;
	}*/
}
