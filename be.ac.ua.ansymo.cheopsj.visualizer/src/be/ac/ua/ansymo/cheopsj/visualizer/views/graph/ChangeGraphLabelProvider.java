/***************************************************
 * Copyright (c) 2014 Nicolas Demarbaix
 * 
 * Contributors: 
 * 		Nicolas Demarbaix - Initial Implementation
 ***************************************************/
package be.ac.ua.ansymo.cheopsj.visualizer.views.graph;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.zest.core.viewers.IConnectionStyleProvider;
import org.eclipse.zest.core.viewers.IFigureProvider;

import be.ac.ua.ansymo.cheopsj.model.famix.FamixEntity;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixInvocation;
import be.ac.ua.ansymo.cheopsj.visualizer.views.graph.figures.FamixFigure;

/**
 * Label provider for the Change Graph View
 * @author nicolasdemarbaix
 *
 */
public class ChangeGraphLabelProvider extends LabelProvider implements IConnectionStyleProvider, IFigureProvider {
	
	public ChangeGraphLabelProvider() {
	}
	/*
	 * ================================
	 * LabelProvider Methods
	 * ================================
	 */
	
	@Override
	public String getText(Object element) {
		return "";
	}
	
	/*
	 * ================================
	 * IConnectionStyleProvider Methods
	 * ================================
	 */
	@Override
	public int getConnectionStyle(Object rel) {
		return 0;
	}

	@Override
	public Color getColor(Object rel) {
		return null;
	}

	@Override
	public Color getHighlightColor(Object rel) {
		return null;
	}

	@Override
	public int getLineWidth(Object rel) {
		return 0;
	}

	@Override
	public IFigure getTooltip(Object entity) {
		return null;
	}
	
	
	/*
	 * ================================
	 * IFigureProvider Methods
	 * ================================
	 */
	@Override
	public IFigure getFigure(Object element) {
		if (element instanceof FamixEntity) {
			Figure fig = new FamixFigure((FamixEntity)element);
			fig.setSize(-1, -1);
			return fig;
		} else if (element instanceof FamixInvocation) {
			Figure fig = new FamixFigure((FamixInvocation)element);
			fig.setSize(-1, -1);
			return fig;
		}
		return null;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}
