/***************************************************
 * Copyright (c) 2014 Nicolas Demarbaix
 * 
 * Contributors: 
 * 		Nicolas Demarbaix - Initial Implementation
 ***************************************************/
package be.ac.ua.ansymo.cheopsj.visualizer.data;

import java.util.List;
import java.util.Map;

import be.ac.ua.ansymo.cheopsj.model.changes.IChange;

/**
 * Data for the summary view. This data will be used to fill the overview table
 * of the changes made to the project. It is simply a wrapper for the data.
 * @author nicolasdemarbaix
 *
 */
public class SummaryData {
	/**
	 * Map of all types and their corresponding total changes
	 * total changes (int[]): - index 0: total entities of this type
	 * 						  - index 1: total changes recorded for this entity type
	 * 						  - index 2: total additions recorded for this entity type
	 * 						  - index 3: total removals recorded for this entity type
	 */
	private Map<String, int[]> type_map = null;
	
	/**
	 * Public constructor
	 * @param changes (List<IChange>): Current changes recorded in the model manager
	 * @param type_map (Map<String, int[]>) preconstructed map of types and corresponding total values
	 */
	public SummaryData(List<IChange> changes, Map<String, int[]> type_map) {
		this.type_map = type_map;
		
		for (IChange change : changes) {
			type_map.get(change.getFamixType())[1]++;
			if (change.getChangeType().equals("Addition")) {
				type_map.get(change.getFamixType())[2]++;
			} else {
				type_map.get(change.getFamixType())[3]++;
			}
		}
	}
	
	/**
	 * Getter method for the type map
	 * @return (Map<String, int[]) Map containing all types and their corresponding total values.
	 */
	public Map<String, int[]> getTypeMap() {
		return this.type_map;
	}
}
