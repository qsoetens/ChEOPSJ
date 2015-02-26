package be.ac.ua.ansymo.cheopsj.visualizer.views.timeline.views;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import be.ac.ua.ansymo.cheopsj.visualizer.views.timeline.data.ItemData;

public class ItemDataSeries {
	private static String DEFAULT_TYPE_NAME = "unknown";
	private static String DEFAULT_ITEM_NAME = "element";
	
	private List<ItemData> data;
	
	private Date earliest = null;
	private Date latest = null;
	
	private String items_type = "";
	private String items_name = "";
	private Image items_img = null;
	
	public ItemDataSeries() {
		this(DEFAULT_TYPE_NAME, DEFAULT_ITEM_NAME);
	}
	
	public ItemDataSeries(String type) {
		this(type, DEFAULT_ITEM_NAME);
	}
	
	public ItemDataSeries(String type, String name) {
		this.data = new ArrayList<ItemData>();
		this.items_type = type;
		this.items_name = name;
		this.items_img = getItemsImage(type);
	}
	
	public void add(ItemData item) {
		this.data.add(item);
		
		if (this.earliest == null) {
			this.earliest = item.getItemDate();
		} else if (this.earliest.after(item.getItemDate())) {
			this.earliest = item.getItemDate();
		}
		
		if (this.latest == null) {
			this.latest = item.getItemDate();
		} else if (this.latest.before(item.getItemDate())) {
			this.latest = item.getItemDate();
		}
	}
	
	public void add(String name) {
		this.add(new ItemData(name));
	}
	
	public void add(String name, Date time) {
		this.add(new ItemData(name, time));
	}
	
	public void setType(String type) {
		this.items_type = type;
		this.items_img = getItemsImage(type);
	}
	
	public void setName(String name) {
		this.items_name = name;
	}
	
	public String getElementType() {
		return this.items_type;
	}
	
	public String getElementName() {
		return this.items_name;
	}
	
	public Date getEarliest() {
		return this.earliest;
	}
	
	public Date getLatest() {
		return this.latest;
	}
	
	public List<ItemData> getItems() {
		return this.data;
	}
	
	public ItemData getItems(int index) {
		return this.data.get(index);
	}
	
	private Image getItemsImage(String type) {
		// TODO implement
		return null;
	}
}
