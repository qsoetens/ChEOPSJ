package be.ac.ua.ansymo.cheopsj.model;

import hibernate.session.SessionHandler;
import hibernate.session.api.ISession;

import java.util.ArrayList;
import java.util.List;

import be.ac.ua.ansymo.cheopsj.model.changes.Add;
import be.ac.ua.ansymo.cheopsj.model.changes.AtomicChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Change;
import be.ac.ua.ansymo.cheopsj.model.changes.IChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Remove;
import be.ac.ua.ansymo.cheopsj.model.changes.Subject;

public class ModelManagerChange {

	//The ModelManagerChange is a Singleton entity, hence the constructor is private.
	//You should always call the static method getInstance() to get the ModelManager instance.
	private static ModelManagerChange INSTANCE = null;

	private ModelManagerChange() {
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
	public List<Change> getChanges() {
		List<Change> changes = new ArrayList<Change>();

		try {
			ISession lSession = SessionHandler.getHandler().getCurrentSession();
			changes = lSession.query("from Change", Change.class);
		} catch (Exception ee) {
		}

		return changes;
	}

	/**
	 * Add a change to the ModelManager and alert the listeners of a new change.
	 * @param change
	 */
	public void addChange(Change change) {
		ISession lSession = null;
		try {
			lSession = SessionHandler.getHandler().getCurrentSession();
			lSession.startTransaction();
			
			lSession.saveObject(change);

			lSession.flush();
			lSession.clear();

			//alert listeners that a change was added
			getModelManagerListeners().fireChangeAdded(change);
		} catch (Exception e) {
			if (lSession != null) {
				try {
					lSession.rollbackTransaction();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		} finally {
			if (lSession != null) {
				try {
					lSession.endTransaction();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
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
		int changeCount = 0;
		int addCount = 0;
		int removeCount = 0;
		
		try {
			ISession lSession = SessionHandler.getHandler().getCurrentSession();
			changeCount = lSession.query("select count(*) from Change");
			addCount = lSession.query("select count(*) from Add");
			removeCount = lSession.query("select count(*) from Remove");
		} catch (Exception ee) {
		}
		
		return changeCount + " changes; " + addCount + " additions and " + removeCount + " removals";
	}
	
	public Change getLatestChange(Subject sub){
		List<AtomicChange> changes = null;
		try {
			ISession lSession = SessionHandler.getHandler().getCurrentSession();
			 changes = lSession.query(
					 "select change from AtomicChange as change inner join change.changeSubject as subject "
					 + "where subject.id =" + sub.getId()
					 +"order by change.timeStamp desc"
					 ,AtomicChange.class);
		} catch (Exception ee) {
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
					 +"order by change.timeStamp desc"
					 ,Add.class);
		} catch (Exception ee) {
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
					 "select change from Remove as change inner join change.changeSubject as subject "
					 + "where subject.id =" + sub.getId()
					 +"order by change.timeStamp desc"
					 ,Remove.class);
		} catch (Exception ee) {
		}
		if(changes == null || changes.isEmpty())
			return null;
		else
			return changes.get(0);
	}
	
}
