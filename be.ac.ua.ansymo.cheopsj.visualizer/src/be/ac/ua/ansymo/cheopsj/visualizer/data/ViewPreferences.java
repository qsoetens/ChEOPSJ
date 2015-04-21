package be.ac.ua.ansymo.cheopsj.visualizer.data;

import java.util.HashMap;
import java.util.Map;

public class ViewPreferences {
	private Map<String, Object> preferences;
	
	public ViewPreferences() {
		preferences = new HashMap<String, Object>();
	}
	
	public void add(String key, Object entry) {
		this.preferences.put(key, entry);
	}
	
	public Object get(String key) {
		if (!this.preferences.containsKey(key))
			return null;
		
		return this.preferences.get(key);
	}
}
