package be.ac.ua.ansymo.cheopsj.visualizer.views.graph;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.layouts.InvalidLayoutConfiguration;
import org.eclipse.zest.layouts.algorithms.AbstractLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.RadialLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;
import org.eclipse.zest.layouts.dataStructures.DisplayIndependentRectangle;
import org.eclipse.zest.layouts.dataStructures.InternalNode;
import org.eclipse.zest.layouts.dataStructures.InternalRelationship;

import be.ac.ua.ansymo.cheopsj.model.changes.Add;
import be.ac.ua.ansymo.cheopsj.model.changes.AtomicChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Remove;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixObject;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixPackage;

public class ChangeGraphLayoutAlgorithm extends AbstractLayoutAlgorithm {

	private double xBound;
	private double yBound;
	private double widthBound;
	private double heightBound;
	private DisplayIndependentRectangle layoutBound = null;
	
	int style;
	private InternalNode[] famixObjectsArray;
	private InternalRelationship[] famixRelationshipsArray;
	
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
	private void buildChangeFamixChanges(InternalNode[] entitiesToLayout, InternalRelationship[] relationshipsToConsider) {
		for (InternalNode famixNode : famixObjectsArray) {
			InternalNode[] changesOfFamixObject = findChangesOfThisFamixObject(relationshipsToConsider, famixNode);
			/*
			 * InternalRelationship[] relationsOfFamixObject =
			 * findRelationsOfThisFamixObject( relationshipsToConsider,
			 * famixNode, changesOfFamixObject);
			 */

			/*
			 * TreeLayoutAlgorithm layout = new TreeLayoutAlgorithm(
			 * this.styles);
			 */

			for (InternalNode node : changesOfFamixObject) {
				if (((GraphNode) node.getLayoutEntity().getGraphData()).getData() instanceof Add) {
					node.setLocationInLayout(famixNode.getXInLayout() + famixNode.getWidthInLayout() - node.getWidthInLayout() / 2,
							famixNode.getYInLayout() + famixNode.getHeightInLayout() * 2);
				}
				if (((GraphNode) node.getLayoutEntity().getGraphData()).getData() instanceof Remove) {
					node.setLocationInLayout(famixNode.getXInLayout() - node.getWidthInLayout() / 2,
							famixNode.getYInLayout() + famixNode.getHeightInLayout() * 2);
				}
			}

			/*
			 * double xBound = famixNode.getXInLayout() + 30; double yBound =
			 * famixNode.getYInLayout() + 30; double hBound =
			 * famixNode.getHeightInLayout() + 40; double wBound =
			 * famixNode.getWidthInLayout() + 40;
			 */
		}
	}

	/**
	 * @param relationshipsToConsider
	 * @param famixNode
	 * @param changesOfFamixObject
	 * @return
	 */
	/*
	 * private InternalRelationship[] findRelationsOfThisFamixObject(
	 * InternalRelationship[] relationshipsToConsider, InternalNode famixNode,
	 * InternalNode[] changesOfFamixObject) {
	 * 
	 * Collection<InternalRelationship> relsToConsider = new
	 * ArrayList<InternalRelationship>();
	 * 
	 * for (InternalRelationship relationship : relationshipsToConsider) {
	 * InternalNode sourceNode = relationship.getSource(); InternalNode destNode
	 * = relationship.getDestination();
	 * 
	 * if (destNode.equals(famixNode) &&
	 * sourceNode.getLayoutEntity().getGraphData() instanceof GraphNode) {
	 * GraphNode sNode = (GraphNode) sourceNode.getLayoutEntity()
	 * .getGraphData(); if (sNode.getData() instanceof AtomicChange) {
	 * relsToConsider.add(relationship); } } }
	 * 
	 * return convertToArrayOfRelationships(relsToConsider); }
	 */

	/**
	 * @param entitiesToLayout
	 * @param famixNode
	 * @return
	 */
	private InternalNode[] findChangesOfThisFamixObject(InternalRelationship[] relationshipsToConsider, InternalNode famixNode) {

		Collection<InternalNode> nodesToConsider = new ArrayList<InternalNode>();
		nodesToConsider.add(famixNode);

		for (InternalRelationship relationship : relationshipsToConsider) {
			InternalNode sourceNode = relationship.getSource();
			InternalNode destNode = relationship.getDestination();

			if (destNode.equals(famixNode) && sourceNode.getLayoutEntity().getGraphData() instanceof GraphNode) {
				GraphNode sNode = (GraphNode) sourceNode.getLayoutEntity().getGraphData();
				if (sNode.getData() instanceof AtomicChange) {
					nodesToConsider.add(sourceNode);
				}
			}
		}

		return convertToArrayOfNodes(nodesToConsider);
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