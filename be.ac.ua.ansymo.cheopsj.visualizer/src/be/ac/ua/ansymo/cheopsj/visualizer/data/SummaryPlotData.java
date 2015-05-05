/***************************************************
 * Copyright (c) 2014 Nicolas Demarbaix
 * 
 * Contributors: 
 * 		Nicolas Demarbaix - Initial Implementation
 ***************************************************/
package be.ac.ua.ansymo.cheopsj.visualizer.data;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import be.ac.ua.ansymo.cheopsj.model.changes.IChange;
import be.ac.ua.ansymo.cheopsj.visualizer.util.DateUtil;

/**
 * Wrapper class for the data to be used in the plot of the summary view
 * @author nicolasdemarbaix
 *
 */
public class SummaryPlotData {
	private Date earliest = null;
	private Date latest = null;
	private Map<String, int[]> date_changecount_map; // Key = string representation of date
	
	/**
	 * Public constructor
	 * @param sorted_changes (List<IChange>): list of changes recorded by the model manager
	 * 										  list has been sorted by date
	 */
	public SummaryPlotData(List<IChange> sorted_changes) {
		if (sorted_changes.size() != 0) {
			this.earliest = sorted_changes.get(0).getTimeStamp();
			this.latest = sorted_changes.get(sorted_changes.size()-1).getTimeStamp();
		}
		this.date_changecount_map = new HashMap<String, int[]>();
		
		this.setup(sorted_changes);
	}
	
	/**
	 * Setup the data
	 * @param sorted_changes (List<IChange>): sorted list of changes recorded by the model manager
	 */
	private void setup(List<IChange> sorted_changes) {
		for (IChange change : sorted_changes) {
			String key = DateUtil.getInstance().constructDateString(change.getTimeStamp());
			if (this.date_changecount_map.containsKey(key)) {
				this.date_changecount_map.get(key)[0]++;
				if (change.getChangeType().equals("Addition")) {
					this.date_changecount_map.get(key)[1]++;
				} else {
					this.date_changecount_map.get(key)[2]++;
				}
			} else {
				int[] changes = {1,0,0};
				if (change.getChangeType().equals("Addition")) {
					changes[1] = 1;
				} else {
					changes[2] = 1;
				}
				this.date_changecount_map.put(key, changes);
			}
		}
	}
	
	/**
	 * Get the earliest date on which a change to the project occurred
	 * @return (java.util.Date) Earliest recorded change date
	 */
	public Date getEarliestChangeDate() {
		return this.earliest;
	}
	
	/**
	 * Get the latest date on which a change to the projcect occurred
	 * @return (java.util.Date) Latest recorded change date
	 */
	public Date getLatestChangeDate() {
		return this.latest;
	}
	
	/**
	 * Get the entire data set of dates and their corresponding change totals
	 * @return (Map<String, int[]>) Data set of changes
	 */
	public Map<String, int[]> getDateChangeCountMap() {
		return this.date_changecount_map;
	}
	
}
