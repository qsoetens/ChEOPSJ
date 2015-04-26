/***************************************************
 * Copyright (c) 2014 Nicolas Demarbaix
 * 
 * Contributors: 
 * 		Nicolas Demarbaix - Initial Implementation
 ***************************************************/
package be.ac.ua.ansymo.cheopsj.visualizer.views.graph;

import org.eclipse.draw2d.FreeformViewport;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.part.*;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.layouts.LayoutStyles;

import be.ac.ua.ansymo.cheopsj.model.ModelManager;
import be.ac.ua.ansymo.cheopsj.visualizer.listeners.ContextMenuDetectListener;
import be.ac.ua.ansymo.cheopsj.visualizer.views.graph.figures.FamixFigure;

public class ChangeGraph extends ViewPart {
	public static final String ID = "be.ac.ua.ansymo.cheopsj.visualizer.views.ChangeGraph";
	
	private static GraphViewer viewer = null;
	private Graph graph = null;
	private Composite parent = null;

	@Override
	public void createPartControl(Composite par) {
		parent = par;
	}
	
	private void setupListeners() {
		graph.addMenuDetectListener(new ContextMenuDetectListener(this.graph, this.parent, (ChangeGraphContentProvider) this.viewer.getContentProvider()));	
	}
	
	public void setFocusEntity(String focus) {
		String[] nameArr = focus.split("\\.");
		String result = "";
		if (nameArr.length == 0) {
			result = focus;
		} else {
			result = nameArr[nameArr.length-1];
		}
		this.setPartName("Graph: " + result);
		viewer = new GraphViewer(this.parent, SWT.BORDER);
		viewer.setContentProvider(new ChangeGraphContentProvider());
		viewer.setLabelProvider(new ChangeGraphLabelProvider());
		viewer.setInput(ModelManager.getInstance());
		
		viewer.setLayoutAlgorithm(new ChangeGraphLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
		viewer.applyLayout();
		
		this.graph = this.viewer.getGraphControl();
		setupListeners();
		((ChangeGraphContentProvider)this.viewer.getContentProvider()).setFocusEntity(focus);	
		viewer.refresh();
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}
	
	public void resetViewLayout() {
		viewer.applyLayout();
	}

}