package be.ac.ua.ansymo.cheopsj.visualizer.views.graph.figures;

import java.awt.Color;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import be.ac.ua.ansymo.cheopsj.model.famix.FamixEntity;

/**
 * FPackageFigure class
 * @author nicolasdemarbaix
 *
 * This class is used for drawing a famix package in the change graph. It provides 
 */
public class FamixFigure extends Figure {
	private Image fImg = null;
	private Label label = null;
	private FamixEntity fEnt = null;
	private Date lastChange = null;
	
	public FamixFigure(int[] changes, FamixEntity ent, Date lchange) {
		this.fEnt = ent;
		this.lastChange = lchange;
		this.label = new Label(this.fEnt.getUniqueName());
		this.label.setLabelAlignment(Label.CENTER);
		
		this.fImg = constructFamixImage(changes);
		
		ToolbarLayout layout = new ToolbarLayout();
		setLayoutManager(layout);
		setOpaque(true);
		setBorder(new LineBorder(ColorConstants.black, 1));
		setBackgroundColor(ColorConstants.lightGray);
				
		ImageFigure imgFig = new ImageFigure(this.fImg);
		add(imgFig);
		add(this.label);
		
	}
	
	private Image constructFamixImage(int[] changes) {
		double totalChanges = changes[0];
		double addChanges = changes[1];
		double deleteChanges = changes[2];
		double modificationChanges = changes[3];
		
		System.out.println("Total changes == " + totalChanges);
		System.out.println("Add changes == " + addChanges);
		System.out.println("Delete changes == " + deleteChanges);
		if (totalChanges == 0) {
			return this.fEnt.getIcon();
		} else {
			Image icon = this.fEnt.getIcon();
			int iwidth = icon.getBounds().width;
			int iheight = icon.getBounds().height;
		  
			double sizeH = getSizeHeuristic(this.lastChange);
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
	
	public String getLabel() {
		return this.label.getText();
	}
}
