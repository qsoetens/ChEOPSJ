/***************************************************
 * Copyright (c) 2014 Nicolas Demarbaix
 * 
 * Contributors: 
 * 		Nicolas Demarbaix - Initial Implementation
 ***************************************************/
package be.ac.ua.ansymo.cheopsj.visualizer.views.timeline.views;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;

/**
 * Layout for the timelinecomposite
 * @author nicolasdemarbaix
 *
 */
public class TimelineLayout extends Layout {

	@Override
	protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {
		return new Point(((TimelineComposite) composite).getWidth(),((TimelineComposite) composite).getHeight());
	}

	@Override
	protected void layout(Composite composite, boolean flushCache) {

	}

}
