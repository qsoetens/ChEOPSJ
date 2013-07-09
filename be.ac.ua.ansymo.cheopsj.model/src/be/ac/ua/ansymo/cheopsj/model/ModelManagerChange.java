package be.ac.ua.ansymo.cheopsj.model;

import java.util.ArrayList;
import java.util.List;

import be.ac.ua.ansymo.cheopsj.model.changes.Add;
import be.ac.ua.ansymo.cheopsj.model.changes.Change;
import be.ac.ua.ansymo.cheopsj.model.changes.IChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Remove;

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
}
