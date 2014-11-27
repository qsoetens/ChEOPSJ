/***************************************************
 * Copyright (c) 2014 Nicolas Demarbaix
 * 
 * Contributors: 
 * 		Nicolas Demarbaix - Initial Implementation
 ***************************************************/
package be.ac.ua.ansymo.cheopsj.visualizer.views.graph;



import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.*;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.layouts.LayoutStyles;

import be.ac.ua.ansymo.cheopsj.model.ModelManager;

public class ChangeGraph extends ViewPart {
	public static final String ID = "be.ac.ua.ansymo.cheopsj.visualizer.views.ChangeGraph";
	
	private static GraphViewer viewer = null;

	@Override
	public void createPartControl(Composite parent) {
		viewer = new GraphViewer(parent, SWT.BORDER);
		viewer.setContentProvider(new ChangeGraphContentProvider());
		viewer.setLabelProvider(new ChangeGraphLabelProvider());
		viewer.setInput(ModelManager.getInstance());
		
		viewer.setLayoutAlgorithm(new ChangeGraphLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
		viewer.applyLayout();
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}
	
	public void resetViewLayout() {
		viewer.applyLayout();
	}

}