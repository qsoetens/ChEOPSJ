package be.ac.ua.ansymo.cheopsj.visualizer.views.timeline.data;

import java.awt.Image;
import java.util.Calendar;
import java.util.Date;

public class ItemData {
	private String item_change = "";
	private Date item_date = null;
	
	public ItemData(String change) {
		this.item_change = change;
		this.item_date = new Date();
	}
	
	public ItemData(String change, Date time) {
		this.item_change = change;
		this.item_date = time;
	}
	
	public String getItemChange() {
		return this.item_change;
	}
	
	public Date getItemDate() {
		return this.item_date;
	}
	
}
