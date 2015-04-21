package be.ac.ua.ansymo.cheopsj.visualizer.views.graph;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.zest.core.viewers.IConnectionStyleProvider;
import org.eclipse.zest.core.viewers.IFigureProvider;

import be.ac.ua.ansymo.cheopsj.model.ModelManagerChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Change;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixAttribute;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixClass;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixEntity;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixMethod;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixPackage;
import be.ac.ua.ansymo.cheopsj.visualizer.views.graph.figures.FamixFigure;

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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Color getColor(Object rel) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Color getHighlightColor(Object rel) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getLineWidth(Object rel) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IFigure getTooltip(Object entity) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	/*
	 * ================================
	 * IFigureProvider Methods
	 * ================================
	 */
	@Override
	public IFigure getFigure(Object element) {
		System.out.println("CHANGEGRAPHLABELPROVIDER::GETFIGURE::ACCESSED");
		if (element instanceof FamixEntity) {
			int[] changes = {0,0,0,0};
			
			if (element instanceof FamixPackage)
				changes = ((FamixPackage) element).aggregateChanges();
			
			Change lastChange = ((FamixEntity)element).getLatestChange();
			Date lchange = null;
			if (lastChange == null) {
				lchange = Calendar.getInstance().getTime();
				System.out.println("LCHANGE WAS NULL, SET MOST RECENT CHANGE STAMP TO NOW");
			} else {
				lchange = lastChange.getTimeStamp();
			}
			System.out.println("CHANGEGRAPHLABELPROVIDER::GETFIGURE::BUILDING FIGURE");
			Figure fig = new FamixFigure((FamixEntity)element);
			fig.setSize(-1, -1);
			System.out.println("CHANGEGRAPHLABELPROVIDER::GETFIGURE:: FIGURE BUILT");
			System.out.println("RETURNING FIGURE FOR ENTITY: " + ((FamixEntity)element).getUniqueName());
			return fig;
		}
		return null;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}