package be.ac.ua.ansymo.cheopsj.visualizer.data;

import java.util.List;
import java.util.Map;

import be.ac.ua.ansymo.cheopsj.model.changes.AtomicChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Change;
import be.ac.ua.ansymo.cheopsj.model.changes.IChange;

public class SummaryData {
	private Map<String, int[]> type_map = null;
	
	public SummaryData(List<IChange> changes, Map<String, int[]> type_map) {
		this.type_map = type_map;
		
		for (IChange change : changes) {
			//if (((Change) change).isDummy()) 
			//	continue;
			
			type_map.get(change.getFamixType())[1]++;
			if (change.getChangeType().equals("Addition")) {
				type_map.get(change.getFamixType())[2]++;
			} else {
				type_map.get(change.getFamixType())[3]++;
			}
		}
	}
	
	public Map<String, int[]> getTypeMap() {
		return this.type_map;
	}
}
