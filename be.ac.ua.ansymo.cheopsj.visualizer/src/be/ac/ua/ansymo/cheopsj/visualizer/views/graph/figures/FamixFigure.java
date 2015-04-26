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
import be.ac.ua.ansymo.cheopsj.model.famix.FamixInvocation;

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
	private FamixInvocation fInv = null;
	private boolean isInvocation = false;
	private Date lastChange = null;
	private String fType = "";
	
	public FamixFigure(FamixEntity ent) {
		this.fEnt = ent;
		this.fType = ent.getFamixType();
		this.lastChange = ent.getLatestChange().getTimeStamp();
		String[] nameArr = fEnt.getUniqueName().split("\\.");
		if (nameArr.length == 0) {
			this.label = new Label(fEnt.getUniqueName());
		} else {
			this.label = new Label(nameArr[nameArr.length-1]);
		}
		this.label.setLabelAlignment(Label.CENTER);
		
		this.fImg = constructFamixImage(ent.aggregateChanges());
		
		ToolbarLayout layout = new ToolbarLayout();
		setLayoutManager(layout);
		setOpaque(true);
		setBorder(new LineBorder(ColorConstants.black, 1));
		setBackgroundColor(ColorConstants.lightGray);
				
		ImageFigure imgFig = new ImageFigure(this.fImg);
		add(imgFig);
		add(this.label);
		
	}
	
	public FamixFigure(FamixInvocation ent) {
		this.fInv = ent;
		this.fType = fInv.getFamixType();
		this.lastChange = fInv.getLatestChange().getTimeStamp();
		String[] nameArr= fInv.getStringRepresentation().split("\\.");
		if (nameArr.length == 0) {
			this.label = new Label(this.fInv.getStringRepresentation());
		} else {
			this.label = new Label(nameArr[nameArr.length-1]);
		}
		this.label.setLabelAlignment(Label.CENTER);
		
		this.fImg = constructFamixInvocationImage(fInv.aggregateChanges());
		
		ToolbarLayout layout = new ToolbarLayout();
		setLayoutManager(layout);
		setOpaque(true);
		setBorder(new LineBorder(ColorConstants.black, 1));
		setBackgroundColor(ColorConstants.lightGray);
				
		ImageFigure imgFig = new ImageFigure(this.fImg);
		add(imgFig);
		add(this.label);
	}
	
	private Image constructFamixInvocationImage(int[] changes) {
		double totalChanges = changes[0];
		double addChanges = changes[1];
		double deleteChanges = changes[2];

		int iwidth = 20;
		int iheight = 20;
	  
		double sizeH = getSizeHeuristic(this.lastChange);
		int imWidth = (int) (iwidth*sizeH);
		int imHeight = (int) (iheight*sizeH);
		Image img = new Image(null, imWidth,imHeight);
		GC gc = new GC(img);
		gc.setBackground(new org.eclipse.swt.graphics.Color(null, 0, 255, 0));
		int addAngle = (int)(360*(addChanges/totalChanges));
	  	gc.fillArc(0, 0, imWidth, imHeight, 0, addAngle);
	  	gc.setBackground(new org.eclipse.swt.graphics.Color(null, 255, 0, 0));
	  	int remAngle = (int)(360*(deleteChanges/totalChanges));
	  	gc.fillArc(0, 0, imWidth, imHeight, addAngle, remAngle);
	  
	  	return img;
	}
	
	private Image constructFamixImage(int[] changes) {
		double totalChanges = changes[0];
		double addChanges = changes[1];
		double deleteChanges = changes[2];

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
		  	gc.fillArc(0, 0, imWidth, imHeight, 0, addAngle);
		  	gc.setBackground(new org.eclipse.swt.graphics.Color(null, 255, 0, 0));
		  	int remAngle = (int)(360*(deleteChanges/totalChanges));
		  	gc.fillArc(0, 0, imWidth, imHeight, addAngle, remAngle);
		  
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
	
	public String getEntityID() {
		if (isInvocation) {
			return this.fInv.getStringRepresentation();
		} else {
			return this.fEnt.getUniqueName();
		}
	}
	
	public String getType() {
		return this.fType;
	}
	
	public boolean isInvocation() {
		return this.isInvocation;
	}
	
	public FamixEntity getEntity() {
		return this.fEnt;
	}
	public FamixInvocation getInvocation() {
		return this.fInv;
	}
}
