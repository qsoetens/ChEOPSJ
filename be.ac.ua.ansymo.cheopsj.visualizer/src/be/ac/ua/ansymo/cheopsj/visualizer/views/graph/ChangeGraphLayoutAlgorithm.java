/***************************************************
 * Copyright (c) 2014 Nicolas Demarbaix
 * 
 * Contributors: 
 * 		Nicolas Demarbaix - Initial Implementation
 ***************************************************/
package be.ac.ua.ansymo.cheopsj.visualizer.views.graph;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.layouts.InvalidLayoutConfiguration;
import org.eclipse.zest.layouts.algorithms.AbstractLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.RadialLayoutAlgorithm;
import org.eclipse.zest.layouts.dataStructures.DisplayIndependentRectangle;
import org.eclipse.zest.layouts.dataStructures.InternalNode;
import org.eclipse.zest.layouts.dataStructures.InternalRelationship;

import be.ac.ua.ansymo.cheopsj.model.famix.FamixObject;

/**
 * Layout algorithm for the Change Graph View
 * @author nicolasdemarbaix
 *
 */
public class ChangeGraphLayoutAlgorithm extends AbstractLayoutAlgorithm {

	private double xBound;
	private double yBound;
	private double widthBound;
	private double heightBound;
	private DisplayIndependentRectangle layoutBound = null;
	
	int style;
	private InternalNode[] famixObjectsArray;
	private InternalRelationship[] famixRelationshipsArray;
	
	/**
	 * Public constructor
	 * @param styles (int) Layout styles
	 */
	public ChangeGraphLayoutAlgorithm(int styles) {
		super(styles);
		this.style = styles;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.zest.layouts.algorithms.AbstractLayoutAlgorithm#
	 * applyLayoutInternal
	 * (org.eclipse.zest.layouts.dataStructures.InternalNode[],
	 * org.eclipse.zest.layouts.dataStructures.InternalRelationship[], double,
	 * double, double, double)
	 */
	@Override
	protected void applyLayoutInternal(InternalNode[] entitiesToLayout, InternalRelationship[] relationshipsToConsider, double boundsX,
			double boundsY, double boundsWidth, double boundsHeight) {
		if (entitiesToLayout.length > 0) {
			int totalProgress = 3;

			fireProgressEvent(1, totalProgress);
			buildFamixTree(entitiesToLayout, relationshipsToConsider);

			/*fireProgressEvent(2, totalProgress);
			buildChangeFamixChanges(entitiesToLayout, relationshipsToConsider);*/

			fireProgressEvent(2, totalProgress);
			for (int i = 0; i < entitiesToLayout.length; i++) {
				
				entitiesToLayout[i].getLayoutEntity().setLocationInLayout(entitiesToLayout[i].getXInLayout(), entitiesToLayout[i].getYInLayout());
			}
			defaultFitWithinBounds(entitiesToLayout, layoutBound);
		}
	}

	/**
	 * @param entitiesToLayout
	 * @param relationshipsToConsider
	 */
	private void buildFamixTree(InternalNode[] entitiesToLayout, InternalRelationship[] relationshipsToConsider) {
		try {

			famixObjectsArray = findFamixNodes(entitiesToLayout);

			famixRelationshipsArray = findFamixRelationships(relationshipsToConsider);

			RadialLayoutAlgorithm layout = new RadialLayoutAlgorithm();

			layout.applyLayout(famixObjectsArray, famixRelationshipsArray, this.xBound, this.yBound, this.widthBound, this.heightBound,
					this.internalAsynchronous, this.internalContinuous);

		} catch (InvalidLayoutConfiguration e) {
			e.printStackTrace();
		}

	}

	private InternalRelationship[] findFamixRelationships(InternalRelationship[] relationshipsToConsider) {
		Collection<InternalRelationship> famixRelationships = new ArrayList<InternalRelationship>();
		for (InternalRelationship relationship : relationshipsToConsider) {
			InternalNode sourceNode = relationship.getSource();
			InternalNode destNode = relationship.getDestination();

			if (sourceNode.getLayoutEntity().getGraphData() instanceof GraphNode && destNode.getLayoutEntity().getGraphData() instanceof GraphNode) {
				GraphNode sNode = (GraphNode) sourceNode.getLayoutEntity().getGraphData();
				GraphNode dNode = (GraphNode) destNode.getLayoutEntity().getGraphData();
				if (sNode.getData() instanceof FamixObject && dNode.getData() instanceof FamixObject) {
					famixRelationships.add(relationship);
				}
			}
		}

		return convertToArrayOfRelationships(famixRelationships);
	}

	private InternalRelationship[] convertToArrayOfRelationships(Collection<InternalRelationship> relationships) {
		InternalRelationship[] arrayOfRelationships = new InternalRelationship[relationships.size()];

		int counter = 0;
		for (InternalRelationship r : relationships) {
			arrayOfRelationships[counter] = r;
			counter++;
		}
		return arrayOfRelationships;
	}

	private InternalNode[] findFamixNodes(InternalNode[] entitiesToLayout) {
		Collection<InternalNode> famixObjects = new ArrayList<InternalNode>();
		for (InternalNode entity : entitiesToLayout) {
			if (entity.getLayoutEntity().getGraphData() instanceof GraphNode) {
				GraphNode node = (GraphNode) entity.getLayoutEntity().getGraphData();
				if (node.getData() instanceof FamixObject) {
					famixObjects.add(entity);
				}
			}
		}

		return convertToArrayOfNodes(famixObjects);
	}

	private InternalNode[] convertToArrayOfNodes(Collection<InternalNode> nodes) {
		InternalNode[] arrayOfNodes = new InternalNode[nodes.size()];
		int counter = 0;
		for (InternalNode n : nodes) {
			arrayOfNodes[counter] = n;
			counter++;
		}
		return arrayOfNodes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.zest.layouts.algorithms.AbstractLayoutAlgorithm#
	 * isValidConfiguration(boolean, boolean)
	 */
	@Override
	protected boolean isValidConfiguration(boolean asynchronous, boolean continuous) {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.zest.layouts.algorithms.AbstractLayoutAlgorithm#
	 * getTotalNumberOfLayoutSteps()
	 */
	@Override
	protected int getTotalNumberOfLayoutSteps() {
		return 3;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.zest.layouts.algorithms.AbstractLayoutAlgorithm#
	 * getCurrentLayoutStep()
	 */
	@Override
	protected int getCurrentLayoutStep() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.zest.layouts.algorithms.AbstractLayoutAlgorithm#
	 * preLayoutAlgorithm
	 * (org.eclipse.zest.layouts.dataStructures.InternalNode[],
	 * org.eclipse.zest.layouts.dataStructures.InternalRelationship[], double,
	 * double, double, double)
	 */
	@Override
	protected void preLayoutAlgorithm(InternalNode[] entitiesToLayout, InternalRelationship[] relationshipsToConsider, double x, double y,
			double width, double height) {

		this.heightBound = height;
		this.widthBound = width;
		this.xBound = x;
		this.yBound = y;
		layoutBound = new DisplayIndependentRectangle(xBound, yBound, widthBound, heightBound);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.zest.layouts.algorithms.AbstractLayoutAlgorithm#
	 * postLayoutAlgorithm
	 * (org.eclipse.zest.layouts.dataStructures.InternalNode[],
	 * org.eclipse.zest.layouts.dataStructures.InternalRelationship[])
	 */
	@Override
	protected void postLayoutAlgorithm(InternalNode[] entitiesToLayout, InternalRelationship[] relationshipsToConsider) {
		// do nothing

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.zest.layouts.algorithms.AbstractLayoutAlgorithm#setLayoutArea
	 * (double, double, double, double)
	 */
	@Override
	public void setLayoutArea(double x, double y, double width, double height) {
		// do nothing

	}

}
