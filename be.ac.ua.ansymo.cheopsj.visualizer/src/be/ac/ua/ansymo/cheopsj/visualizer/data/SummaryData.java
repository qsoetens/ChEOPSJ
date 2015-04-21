package be.ac.ua.ansymo.cheopsj.visualizer.data;

import java.util.List;

import be.ac.ua.ansymo.cheopsj.model.changes.AtomicChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Change;
import be.ac.ua.ansymo.cheopsj.model.changes.IChange;

public class SummaryData {
	private int		packageCount = 0;
	private int		packageChangeCount = 0;
	private int		packageAdditions = 0;
	private int		packageRemovals = 0;
	
	private int		classCount = 0;
	private int 	classChangeCount = 0;
	private int		classAdditions = 0;
	private int 	classRemovals = 0;
	
	private int		methodCount = 0;
	private int		methodChangeCount = 0;
	private	int 	methodAdditions = 0;
	private int		methodRemovals = 0;
	
	private int		attributeCount = 0;
	private int		attributeChangeCount = 0;
	private int		attributeAdditions = 0;
	private int		attributeRemovals = 0;
	
	public SummaryData(List<IChange> changes, int pCount, int cCount, int mCount, int aCount) {
		this.packageCount = pCount;
		this.classCount = cCount;
		this.methodCount = mCount;
		this.attributeCount = aCount;
		
		int changecount = 0;
		int packcount = 0;
		int methodcount = 0;
		int attcount = 0;
		int classc = 0;
		int invoccount = 0;
		
		for (IChange change : changes) {
			//if (((Change) change).isDummy()) 
			//	continue;
			
			changecount++;
			if (change.getFamixType().equals("Package")) {
				packcount++;
				this.packageChangeCount++;
				if (change.getChangeType().equals("Addition")) {
					this.packageAdditions++;
				} else {
					this.packageRemovals++;
				}
			} else if (change.getFamixType().equals("Class")) {
				classc++;
				this.classChangeCount++;
				if (change.getChangeType().equals("Addition")) {
					this.classAdditions++;
				} else {
					this.classRemovals++;
				}
			} else if (change.getFamixType().equals("Method")) {
				methodcount++;
				this.methodChangeCount++;
				if (change.getChangeType().equals("Addition")) {
					this.methodAdditions++;
				} else {
					this.methodRemovals++;
				}
			} else if (change.getFamixType().equals("Attribute")) {
				attcount++;
				this.attributeChangeCount++;
				if (change.getChangeType().equals("Addition")) {
					this.attributeAdditions++;
				} else {
					this.attributeRemovals++;
				}
			} else {
				invoccount++;
			}
		}
	}
	
	public int getPackageCount() {return this.packageCount;}
	public int getPackageChangeCount() {return this.packageChangeCount;}
	public int getPackageAdditions() {return this.packageAdditions;}
	public int getPackageRemovals() {return this.packageRemovals;}
	
	public int getClassCount() {return this.classCount;}
	public int getClassChangeCount() {return this.classChangeCount;}
	public int getClassAdditions() {return this.classAdditions;}
	public int getClassRemovals() {return this.classRemovals;}
	
	public int getMethodCount() {return this.methodCount;}
	public int getMethodChangeCount() {return this.methodChangeCount;}
	public int getMethodAdditions() {return this.methodAdditions;}
	public int getMethodRemovals() {return this.methodRemovals;}
	
	public int getAttributeCount() {return this.attributeCount;}
	public int getAttributeChangeCount() {return this.attributeChangeCount;}
	public int getAttributeAdditions() {return this.attributeAdditions;}
	public int getAttributeRemovals() {return this.attributeRemovals;}
	
	public int getTotalCount() {
		return this.packageCount + this.classCount + this.methodCount + this.attributeCount;
	}
	public int getTotalChangeCount() {
		return this.packageChangeCount + this.classChangeCount + this.methodChangeCount + this.attributeChangeCount;
	}
	public int getTotalAdditions() {
		return this.packageAdditions + this.classAdditions + this.methodAdditions + this.attributeAdditions;
	}
	public int getTotalRemovals() {
		return this.packageRemovals + this.classRemovals + this.methodRemovals + this.attributeRemovals;
	}
}
