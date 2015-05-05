/***************************************************
 * Copyright (c) 2014 Nicolas Demarbaix
 * 
 * Contributors: 
 * 		Nicolas Demarbaix - Initial Implementation
 ***************************************************/
package be.ac.ua.ansymo.cheopsj.visualizer.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Wrapper for the settings in the different preferences dialogs
 * @author nicolasdemarbaix
 *
 */
public class ViewPreferences {
	private Map<String, Object> preferences;
	
	/**
	 * Public constructor
	 */
	public ViewPreferences() {
		preferences = new HashMap<String, Object>();
	}
	
	/**
	 * Add a new entry to the preferences
	 * @param key (String) key for the preferences
	 * @param entry (Object) value of the preference
	 */
	public void add(String key, Object entry) {
		this.preferences.put(key, entry);
	}
	
	/**
	 * Get the value of a certain preference given its key
	 * @param key (String) key of the preference
	 * @return (Object) value of the preference
	 */
	public Object get(String key) {
		if (!this.preferences.containsKey(key))
			return null;
		
		return this.preferences.get(key);
	}
}
