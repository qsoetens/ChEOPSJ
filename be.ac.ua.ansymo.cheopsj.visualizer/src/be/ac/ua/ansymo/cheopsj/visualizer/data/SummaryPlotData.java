package be.ac.ua.ansymo.cheopsj.visualizer.data;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.jfree.chart.axis.DateTickUnit;

import be.ac.ua.ansymo.cheopsj.model.changes.IChange;
import be.ac.ua.ansymo.cheopsj.visualizer.util.DateUtil;

public class SummaryPlotData {
	private Date earliest = null;
	private Date latest = null;
	private Map<String, int[]> date_changecount_map;
	
	public SummaryPlotData(List<IChange> sorted_changes) {
		if (sorted_changes.size() != 0) {
			this.earliest = sorted_changes.get(0).getTimeStamp();
			this.latest = sorted_changes.get(sorted_changes.size()-1).getTimeStamp();
		}
		this.date_changecount_map = new HashMap<String, int[]>();
		
		this.setup(sorted_changes);
	}
	
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
	
	public Date getEarliestChangeDate() {
		return this.earliest;
	}
	
	public Date getLatestChangeDate() {
		return this.latest;
	}
	
	public Map<String, int[]> getDateChangeCountMap() {
		return this.date_changecount_map;
	}
	
}
