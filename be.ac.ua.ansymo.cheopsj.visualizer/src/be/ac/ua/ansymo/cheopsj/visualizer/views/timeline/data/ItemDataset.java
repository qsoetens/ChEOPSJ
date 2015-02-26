package be.ac.ua.ansymo.cheopsj.visualizer.views.timeline.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import be.ac.ua.ansymo.cheopsj.visualizer.views.timeline.views.ItemDataSeries;

public class ItemDataset {
	private List<ItemDataSeries> data;
	
	private Date earliest = null;
	private Date latest = null;
	
	public ItemDataset() {
		this.data = new ArrayList<ItemDataSeries>();
	}
	
	public void add(ItemDataSeries series) {
		this.data.add(series);
		
		if (this.earliest == null) {
			this.earliest = series.getEarliest();
		} else if (this.earliest.after(series.getEarliest())) {
			this.earliest = series.getEarliest();
		}
		
		if (this.latest == null) {
			this.latest = series.getLatest();
		} else if (this.latest.before(series.getLatest())) {
			this.latest = series.getLatest();
		}
	}
	
	public Date getEarliest() {
		return this.earliest;
	}
	
	public Date getLatest() {
		return this.latest;
	}
	
	
}
