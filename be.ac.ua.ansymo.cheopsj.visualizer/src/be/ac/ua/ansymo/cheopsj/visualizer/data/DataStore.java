package be.ac.ua.ansymo.cheopsj.visualizer.data;

import java.sql.Struct;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import be.ac.ua.ansymo.cheopsj.model.ModelManager;
import be.ac.ua.ansymo.cheopsj.model.changes.AtomicChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Change;
import be.ac.ua.ansymo.cheopsj.model.changes.IChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Subject;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixAssociation;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixAttribute;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixClass;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixEntity;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixMethod;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixObject;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixPackage;

/**
 * Common Data store that can be used to access certain information from the views
 * Singleton Class
 * @author nicolasdemarbaix
 *
 */
public class DataStore {
	
	private static DataStore instance = null;
	
	// Static identifiers for elements.
	private static String FAMIX_PACKAGE = "Package";
	private static String FAMIX_CLASS = "Class";
	private static String FAMIX_METHOD = "Method";
	
	// Preferences for the settings dialogs
	private ViewPreferences summary_preferences = null;
	private ViewPreferences table_preferences = null;
	
	// Modelmanager for accessing the underlying model.
	private ModelManager manager = null;
	
	/**
	 * DataStore is a singleton class. An instance is created if it does not yet exist
	 * @return
	 */
	public static DataStore getInstance() {
		if (instance == null) {
			instance = new DataStore();
		}
		return instance;
	}
	
	/**
	 * Private constructor. DataStore can only be accessed through the singleton instance.
	 */
	private DataStore() {
		this.manager = ModelManager.getInstance();
	}
	
	// ---------------
	// COUNTER METHODS
	// ---------------
	/**
	 * Counts the number of packages in the project
	 * @return (int) The number of packages
	 */
	public int getPackageCount() {
		return this.manager.getFamixPackagesMap().size();
	}
	
	/**
	 * Counts the number of changes registered by the model
	 * @return (int) The number of changes
	 */
	public int getChangeCount() {
		return this.manager.getModelManagerChange().getChanges().size();
	}
	
	// ------------
	// LIST METHODS
	// ------------
	/**
	 * Returns a list of all package names that are registered by the model
	 * @param sorted (boolean) - whether the list has to be sorted.
	 * @return (List<String>) a (sorted) list of package names.
	 */
	public List<String> getPackageNames(boolean sorted) {
		List<String> names = new ArrayList<String>();
		
		for (Map.Entry<String, FamixPackage> entry : this.manager.getFamixPackagesMap().entrySet()) {
			names.add(entry.getKey());
		}
		
		if (sorted) 
			Collections.sort(names);
		
		return names;
	}
	
	/**
	 * Returns a list of all class names that are registered by the model
	 * @param sorted (boolean) - whether the list has to be sorted.
	 * @return (List<String>) a (sorted) list of class names.
	 */
	public List<String> getClassesNames(boolean sorted) {
		List<String> names = new ArrayList<String>();
		
		for (Map.Entry<String, FamixClass> entry : this.manager.getFamixClassesMap().entrySet()) {
			names.add(entry.getKey());
		}
		
		if (sorted)
			Collections.sort(names);
		
		return names;
	}
	
	/**
	 * Returns a list of all changes that are registered by the model
	 * @param sorted (boolean) - whether the list has to be sorted (sorting happens based on the time stamp of the changes).
	 * @return (List<String>) a (sorted) list of changes.
	 */
	public List<IChange> getChanges(boolean sorted) {
		List<IChange> changes = this.manager.getModelManagerChange().getChanges();
		
		if (sorted)
			Collections.sort(changes, new Comparator<IChange>() {

				@Override
				public int compare(IChange o1, IChange o2) {
					if (o1.getTimeStamp().equals(o2.getTimeStamp())) {
						return 0;
					} else if (o2.getTimeStamp().after(o1.getTimeStamp())) {
						return -1;
					} else {
						return 1;
					}
				}
			});
		
		return changes;
	}
	
	/**
	 * Find the date of the earliest change registered by the model
	 * @return (java.util.Date) Earliest change date 
	 */
	public Date getEarliestChangeDate() {
		List<IChange> changes = getChanges(true);
		
		if (changes == null || changes.size() == 0) {
			return null;
		}
		
		return changes.get(0).getTimeStamp();
	}
	
	/**
	 * Find the date of the latest change registered by the model
	 * @return (java.util.Date) Latest change date
	 */
	public Date getLatestChangeDate() {
		List<IChange> changes = getChanges(true);
		int size = changes.size();
		
		if (changes == null || size == 0) {
			return null;
		}
		
		return changes.get(size-1).getTimeStamp();
	}
	
	/**
	 * Get all changes for a certain package
	 * @param pName (String) - The name of the package
	 * @return (List<IChange>) The list of all changes for package pName.
	 */
	public List<IChange> getChangesForPackage(String pName) {
		List<IChange> changes = new ArrayList<IChange>();

		for (IChange c : this.manager.getModelManagerChange().getChanges()) {
			if (c.getFamixType().equals(FAMIX_PACKAGE)) {
				if (c.getName().equals(pName)) {
					changes.add(c);
				}
			} else if (c.getFamixType().equals(FAMIX_CLASS)) {
				FamixClass fClass = (FamixClass) ((AtomicChange) c).getChangeSubject();
				if (fClass.getBelongsToPackage().equals(pName)) {
					changes.add(c);
				}
			}
		}
		return changes;
	}
	
	/**
	 * Construct the data set to be used by the Summary view
	 * @return (SummaryData) Wrapper data set for the summary view
	 */
	public SummaryData constructSummaryTableData() {
		int packcount = 0;
		int classcount = 0;
		int methodcount = 0;
		int attcount = 0;
		int other = 0;

		for (Subject sub : this.manager.getFamixEntities()) {
			if (sub == null)
				continue; 
			
			if (sub.getLatestChange() == null)
				continue;
			
			if (sub.getLatestChange().isDummy()) 
				continue;
						
			if (sub.getFamixType().equals("Package")) {
				packcount++;
			} else if (sub.getFamixType().equals("Class")) {
				classcount++;
			} else if (sub.getFamixType().equals("Method")) {
				methodcount++;
			} else if (sub.getFamixType().equals("Attribute")) {
				attcount++;
			} else {
				other++;
			}
		}
		return new SummaryData(this.manager.getModelManagerChange().getChanges(),
							   packcount,
							   classcount,
							   methodcount,
							   attcount);
	}
	
	/**
	 * Construct the data set for the timeline view based on a focus entity
	 * @param entityName (String) - the entity that is being considered
	 * @return (TimelineData) Wrapper data set for the timeline view
	 */
	public TimelineData constructTimelineData(String entityName) {
		if (entityName == null) {
			return null;
		}
		
		FamixEntity entity = findEntityByName(entityName);
		if (entity == null) {
			return null;
		}
		
		Map<String, List<IChange>> data = new HashMap<String, List<IChange>>();
		Vector<Date> date_vec = new Vector<Date>();
		Date earliest = null;
		Date latest = null;
		
		for (IChange ichange : entity.getAffectingChanges()) {
			Change change = (Change) ichange;
			date_vec.add(ichange.getTimeStamp());
			
			if (earliest == null || earliest.after(ichange.getTimeStamp())) {
				earliest = ichange.getTimeStamp();
			}
			if (latest == null || latest.before(ichange.getTimeStamp())) {
				latest = ichange.getTimeStamp();
			}
			
			String key;
			Subject subject = ((AtomicChange) change).getChangeSubject();
			if (subject instanceof FamixEntity) {
				key = ((FamixEntity) subject).getUniqueName();
			} else {
				key = ((FamixAssociation)subject).getStringRepresentation();
			}
			if (!data.containsKey(key)) {
				data.put(key, new ArrayList<IChange>());
			}
			data.get(key).add(change);
		}
		for (IChange ichange : entity.getAffectingChanges()) {	
			Change change = (Change)ichange;
			for (Change structDept : change.getStructuralDependencies()) {
				date_vec.add(structDept.getTimeStamp());
				if (structDept.getTimeStamp().before(earliest)) {
					earliest = structDept.getTimeStamp();
				}
				if (structDept.getTimeStamp().after(latest)) {
					latest = structDept.getTimeStamp();
				}
				String structDeptKey;
				Subject structDeptSub = ((AtomicChange) structDept).getChangeSubject();
				if (structDeptSub instanceof FamixEntity) {
					structDeptKey = ((FamixEntity)structDeptSub).getUniqueName();
				} else {
					structDeptKey = ((FamixAssociation)structDeptSub).getStringRepresentation();
				}
				if (!data.containsKey(structDeptKey)) {
					data.put(structDeptKey, new ArrayList<IChange>());
				}
				if (!data.get(structDeptKey).contains(structDept))
					data.get(structDeptKey).add(structDept);
				
			}
			for (Change structDepe : change.getStructuralDependees()) {
				date_vec.add(structDepe.getTimeStamp());
				if (structDepe.getTimeStamp().before(earliest)) {
					earliest = structDepe.getTimeStamp();
				}
				if (structDepe.getTimeStamp().after(latest)) {
					latest = structDepe.getTimeStamp();
				}
				String structDepeKey;
				Subject structDepeSub = ((AtomicChange) structDepe).getChangeSubject();
				if (structDepeSub instanceof FamixEntity) {
					structDepeKey = ((FamixEntity)structDepeSub).getUniqueName();
				} else {
					structDepeKey = ((FamixAssociation)structDepeSub).getStringRepresentation();
				}
				if (!data.containsKey(structDepeKey)) {
					data.put(structDepeKey, new ArrayList<IChange>());
				}
				if (!data.get(structDepeKey).contains(structDepe))
					data.get(structDepeKey).add(structDepe);
				
			}
			for (Change semDept : change.getSemanticalDependencies()) {
				date_vec.add(semDept.getTimeStamp());
				if (semDept.getTimeStamp().before(earliest)) {
					earliest = semDept.getTimeStamp();
				}
				if (semDept.getTimeStamp().after(latest)) {
					latest = semDept.getTimeStamp();
				}
				String semDeptKey;
				Subject semDeptSub = ((AtomicChange) semDept).getChangeSubject(); 
				if (semDeptSub instanceof FamixEntity) {
					semDeptKey = ((FamixEntity)semDeptSub).getUniqueName();
				} else {
					semDeptKey = ((FamixAssociation)semDeptSub).getStringRepresentation();
				}
				if (!data.containsKey(semDeptKey)) {
					data.put(semDeptKey, new ArrayList<IChange>());
				}
				if (!data.get(semDeptKey).contains(semDept))
					data.get(semDeptKey).add(semDept);
				
			}
			for (Change semDepe : change.getSemanticalDependees()) {
				date_vec.add(semDepe.getTimeStamp());
				if (semDepe.getTimeStamp().before(earliest)) {
					earliest = semDepe.getTimeStamp();
				}
				if (semDepe.getTimeStamp().after(latest)) {
					latest = semDepe.getTimeStamp();
				}
				String semDepeKey;
				Subject semDepeSub = ((AtomicChange) semDepe).getChangeSubject(); 
				if (semDepeSub instanceof FamixEntity) {
					semDepeKey = ((FamixEntity)semDepeSub).getUniqueName();
				} else {
					semDepeKey = ((FamixAssociation)semDepeSub).getStringRepresentation();
				}
				if (!data.containsKey(semDepeKey)) {
					data.put(semDepeKey, new ArrayList<IChange>());
				}
				if (!data.get(semDepeKey).contains(semDepe))
					data.get(semDepeKey).add(semDepe);
			}
		}
		
		return new TimelineData(data, earliest, latest, entityName, date_vec);
	}
	
	/**
	 * Find an entity registered by the model based on its unique identifier
	 * @param name (String) - the identifier of the entity
	 * @return (FamixEntity) the entity. Can be null!
	 */
	private FamixEntity findEntityByName(String name) {
		for (Subject subject : this.manager.getFamixEntities()) {
			if (subject instanceof FamixEntity) {
				if (((FamixEntity) subject).getUniqueName().equals(name)) {
					return (FamixEntity)subject;
				}
			}
		}
		return null;
	}
	
	/**
	 * Constructs the data for the plot in the summary view
	 * @return (SummaryPlotData) Wrapper for the data of the plot
	 */
	public SummaryPlotData constructSummaryPlotData() {
		return new SummaryPlotData(getChanges(true));
	}
	
	/**
	 * Store the preferences of the summary settings dialog
	 * @param pref (ViewPreferences) - the preferences
	 */
	public void setSummaryPreferences(ViewPreferences pref) {
		this.summary_preferences = pref;
	}
	
	/**
	 * Get the preferences of the summary settings dialog
	 * @return (ViewPreferences) Wrapper for the preferences of the summary settings dialog
	 */
	public ViewPreferences getSummaryPreferences() {
		return this.summary_preferences;
	}
	
	/**
	 * Store the preferences of the table settings dialog
	 * @param pref (ViewPreferences) - the preferences
	 */
	public void setTablePreferences(ViewPreferences pref) {
		this.table_preferences = pref;
	}
	
	/**
	 * Get the preferences of the table settings dialog
	 * @return (ViewPreferences) Wrapper for the preferences of the table settings dialog
	 */
	public ViewPreferences getTablePreferences() {
		return this.table_preferences;
	}
}
