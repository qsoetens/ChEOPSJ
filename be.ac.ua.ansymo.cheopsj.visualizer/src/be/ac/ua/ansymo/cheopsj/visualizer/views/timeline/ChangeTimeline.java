/***************************************************
 * Copyright (c) 2014 Nicolas Demarbaix
 * 
 * Contributors: 
 * 		Nicolas Demarbaix - Initial Implementation
 ***************************************************/
package be.ac.ua.ansymo.cheopsj.visualizer.views.timeline;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import be.ac.ua.ansymo.cheopsj.visualizer.views.timeline.views.TimelineComposite;

/**
 * Change Timeline View
 * @author nicolasdemarbaix
 *
 */
public class ChangeTimeline extends ViewPart {
	
	public static final String ID = "be.ac.ua.ansymo.cheopsj.visualizer.views.ChangeTimeline";
	private String subjectID = null;
	private Composite parent = null;
	
	@Override
	public void createPartControl(Composite par) {
		parent = par;
		parent.setLayout(new FillLayout());
		
		if (subjectID == null) 
			return;
		
		@SuppressWarnings("unused")
		TimelineComposite tlc = new TimelineComposite(parent, subjectID);
	}
	
	/**
	 * Recreate the part control after the focus entity has been set
	 */
	public void reCreatePartControl() {
		this.createPartControl(this.parent);
	}
	
	/**
	 * Set the id of the focus entity
	 * @param id (String) unique name of entity
	 */
	public void setSubjectID(String id) {
		this.subjectID = id;
		String[] nameArr = id.split("\\.");
		String result = "";
		if (nameArr.length == 0) {
			result = id;
		} else {
			result = nameArr[nameArr.length-1];
		}
		this.setPartName("Timeline: " + result);
	}
	
	@Override
	public void setFocus() {

	}

}
