/***************************************************
 * Copyright (c) 2014 Nicolas Demarbaix
 * 
 * Contributors: 
 * 		Nicolas Demarbaix - Initial Implementation
 ***************************************************/
package be.ac.ua.ansymo.cheopsj.visualizer.data;

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import be.ac.ua.ansymo.cheopsj.model.ModelManager;
import be.ac.ua.ansymo.cheopsj.model.changes.Change;
import be.ac.ua.ansymo.cheopsj.model.changes.IChange;

/**
 * Singleton class to provide access to the underlying model data
 * 	To be used by all visualization techniques (some might use another class on top of
 * 	class due to implementation specific details, e.g. the contentprovider for the graph view)
 * @author nicolasdemarbaix
 *
 */
public class DataStore {
	private static DataStore instance = null;
	private ModelManager manager = null;
		
	protected DataStore() {
		// TODO complete initialization
		this.manager = ModelManager.getInstance();
	}
	
	public static DataStore getInstance() {
		if (instance == null) {
			instance = new DataStore();
		}
		return instance;
	}
	
	public List<IChange> getChanges() {
		return this.manager.getModelManagerChange().getChanges();
	}
}
