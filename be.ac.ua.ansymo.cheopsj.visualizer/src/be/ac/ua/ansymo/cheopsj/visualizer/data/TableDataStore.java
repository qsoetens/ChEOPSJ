package be.ac.ua.ansymo.cheopsj.visualizer.data;

import java.util.ArrayList;
import java.util.List;

import be.ac.ua.ansymo.cheopsj.model.ModelManagerChange;
import be.ac.ua.ansymo.cheopsj.model.changes.IChange;

// TODO IMPLEMENT INTERFACE DATASTORE
public class TableDataStore {
	private static TableDataStore instance = null;
	private ModelManagerChange manager = null;
	
	private TableDataStore() {
		// TODO SETUP
		this.manager = ModelManagerChange.getInstance();
	}
	
	public static TableDataStore getInstance() {
		if (instance == null) {
			instance = new TableDataStore();
		}
		return instance;
	}
	
	public List<IChange> getChanges() {
		return this.manager.getChanges();
	}
}
