package be.ac.ua.ansymo.cheopsj.visualizer.views.graph;

import org.eclipse.zest.layouts.algorithms.AbstractLayoutAlgorithm;
import org.eclipse.zest.layouts.dataStructures.DisplayIndependentRectangle;
import org.eclipse.zest.layouts.dataStructures.InternalNode;
import org.eclipse.zest.layouts.dataStructures.InternalRelationship;

public class ChangeGraphLayoutAlgorithm extends AbstractLayoutAlgorithm {

	private double xBound;
	private double yBound;
	private double widthBound;
	private double heightBound;
	private DisplayIndependentRectangle layoutBound;
	
	public ChangeGraphLayoutAlgorithm(int styles) {
		super(styles);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setLayoutArea(double x, double y, double width, double height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean isValidConfiguration(boolean asynchronous, boolean continuous) {
		// TODO Auto-generated method stub (Needs an Implementation????)
		return true;
	}

	@Override
	protected void applyLayoutInternal(InternalNode[] entitiesToLayout, InternalRelationship[] relationshipsToConsider,
			double boundsX, double boundsY, double boundsWidth, double boundsHeight) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void preLayoutAlgorithm(InternalNode[] entitiesToLayout, InternalRelationship[] relationshipsToConsider,
			double x, double y, double width, double height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void postLayoutAlgorithm(InternalNode[] entitiesToLayout, InternalRelationship[] relationshipsToConsider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected int getTotalNumberOfLayoutSteps() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected int getCurrentLayoutStep() {
		// TODO Auto-generated method stub
		return 0;
	}

}
