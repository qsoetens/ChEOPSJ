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
import be.ac.ua.ansymo.cheopsj.model.famix.FamixAttribute;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixClass;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixEntity;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixMethod;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixPackage;
import be.ac.ua.ansymo.cheopsj.visualizer.views.graph.figures.FPackageFigure;

public class ChangeGraphLabelProvider extends LabelProvider implements IConnectionStyleProvider, IFigureProvider {

	private ModelManagerChange changeManager = null;
	
	public ChangeGraphLabelProvider() {
		this.changeManager = ModelManagerChange.getInstance();
	}
	/*
	 * ================================
	 * LabelProvider Methods
	 * ================================
	 */
	
	@Override
	public String getText(Object element) {
		return "node";
	}
	
	private Image constructFamixImage(FamixEntity ent) {
		System.out.println("CHANGEGRAPHLABELPROVIDER::CONSTRUCTFAMIXIMAGE::ACCESSED");
		if (!(ent instanceof FamixPackage)) {
			return null;
		}
		Collection<FamixClass> classes = ((FamixPackage) ent).getClasses();
		double totalChanges = 0;
		double addChanges = 0;
		double deleteChanges = 0;
		double modificationChanges = 0;
		
		for (FamixClass c : classes) {
			totalChanges += this.changeManager.getChangeCount(c);
			addChanges += this.changeManager.getAddCount(c);
			deleteChanges += this.changeManager.getRemoveCount(c);
			
/*			try {
				Collection<FamixAttribute> attribute_col = c.getAttributes();
				for (FamixAttribute a : attribute_col) {
					totalChanges += this.changeManager.getChangeCount(a);
					addChanges += this.changeManager.getAddCount(a);
					deleteChanges += this.changeManager.getRemoveCount(a);
				}
				
				Collection<FamixMethod> method_col = c.getMethods();
				for (FamixMethod m : method_col) {
					totalChanges += this.changeManager.getChangeCount(m);
					addChanges += this.changeManager.getAddCount(m);
					deleteChanges += this.changeManager.getRemoveCount(m);
				}

				Collection<FamixClass> class_col = c.getNestedClasses();
				for (FamixClass cc : class_col) {
					totalChanges += this.changeManager.getChangeCount(cc);
					addChanges += this.changeManager.getAddCount(cc);
					deleteChanges += this.changeManager.getRemoveCount(cc);
				}
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}*/
		}
		
		System.out.println("Total changes == " + totalChanges);
		System.out.println("Add changes == " + addChanges);
		System.out.println("Delete changes == " + deleteChanges);
		if (totalChanges == 0) {
			return ent.getIcon();
		} else {
			Image icon = ent.getIcon();
			int iwidth = icon.getBounds().width;
			int iheight = icon.getBounds().height;
		  
			double sizeH = getSizeHeuristic(this.changeManager.getLatestChange(ent).getTimeStamp());
			int imWidth = (int) (iwidth*sizeH);
			int imHeight = (int) (iheight*sizeH);
			Image img = new Image(null, imWidth,imHeight);
			GC gc = new GC(img);
			gc.setBackground(new org.eclipse.swt.graphics.Color(null, 0, 255, 0));
			int addAngle = (int)(360*(addChanges/totalChanges));
			System.out.println("Add angle == " + addAngle);
		  	gc.fillArc(0, 0, imWidth, imHeight, 0, addAngle);
		  	gc.setBackground(new org.eclipse.swt.graphics.Color(null, 255, 255, 0));
		  	int modAngle = (int)(360*(modificationChanges/totalChanges));
		  	gc.fillArc(0, 0, imWidth, imHeight, addAngle, modAngle);
		  	gc.setBackground(new org.eclipse.swt.graphics.Color(null, 255, 0, 0));
		  	int remAngle = (int)(360*(deleteChanges/totalChanges));
		  	gc.fillArc(0, 0, imWidth, imHeight, addAngle+modAngle, remAngle);
		  
		  	int xcoord = (imWidth/2)-(iwidth/2);
		  	int ycoord = (imHeight/2)-(iheight/2);
		  	gc.drawImage(icon, xcoord, ycoord);
		  
		  	return img;
		}
	}
	
	static public Timestamp now() {
		Calendar calendar = Calendar.getInstance();
		java.util.Date now = calendar.getTime();
		java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());
		return currentTimestamp;
	}
	
	private double getSizeHeuristic(Date d) {
		long HOUR = 360000;
		long DAY = 86400000;
		long WEEK = 604800000;
		
		long age = now().getTime() - d.getTime();
		
		if (age < HOUR) {
			return 3.0;
		} else if (age < DAY) {
			return 2.5;
		} else if (age < WEEK) {
			return 2.0;
		} else {
			return 1.5;
		}
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
			Image img = constructFamixImage((FamixEntity) element);
			Figure result = null;
			if (element instanceof FamixPackage) {
				result = new FPackageFigure(img, ((FamixEntity) element).getUniqueName());
				result.setSize(-1, -1);
			}
			return result;
		}
		return null;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}
