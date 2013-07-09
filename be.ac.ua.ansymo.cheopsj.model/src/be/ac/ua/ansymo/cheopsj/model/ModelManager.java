/*******************************************************************************
 * Copyright (c) 2011 Quinten David Soetens
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Quinten David Soetens - initial API and implementation
 ******************************************************************************/
package be.ac.ua.ansymo.cheopsj.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.ac.ua.ansymo.cheopsj.model.changes.Add;
import be.ac.ua.ansymo.cheopsj.model.changes.AtomicChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Change;
import be.ac.ua.ansymo.cheopsj.model.changes.IChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Remove;
import be.ac.ua.ansymo.cheopsj.model.changes.Subject;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixAttribute;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixBehaviouralEntity;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixClass;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixEntity;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixInvocation;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixLocalVariable;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixMethod;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixPackage;

/**
 * This class is the entity that holds and maintains the entire famix and change model.
 * All famixEntities and all changes that act upon those famixEntities are stored in this ModelManager.
 * 
 * @author quinten
 *
 */
public class ModelManager implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4107886630686152745L;

	//This list contains all FamixEntities
	private List<Subject> famixEntities;

	//We also keep maps to specific FamixEntities to allow easier lookup.
	private Map<String, FamixPackage> famixPackagesMap;

	private Map<String, FamixClass> famixClassesMap;
	private Map<String, FamixMethod> famixMethodsMap;
	private Map<String, FamixAttribute> famixFieldsMap;

	private Map<String, FamixInvocation> famixInvocationsMap;

	private Map<String, FamixLocalVariable> famixVariablesMap;

	//The modelmanager is a Singleton entity, hence the constructor is private.
	//You should always call the static method getInstance() to get the ModelManager instance.
	private static ModelManager INSTANCE = null;

	private ModelManager() {
		famixEntities = new ArrayList<Subject>();

		famixPackagesMap = new HashMap<String, FamixPackage>();
		famixClassesMap = new HashMap<String, FamixClass>();
		famixMethodsMap = new HashMap<String, FamixMethod>();
		famixFieldsMap = new HashMap<String, FamixAttribute>();
		famixInvocationsMap = new HashMap<String, FamixInvocation>();
		famixVariablesMap = new HashMap<String, FamixLocalVariable>();
		// loadModel();
	}

	/**
	 * The ModelManger is a Singleton entity. Therefore the constructor is private.
	 * This method returns an instance of the ModelManger. If no instance existed 
	 * before it will call the private constructor to create a new instance. Else
	 * It will return the existing instance. 
	 *  
	 * @return the Singleton ModelManager instance
	 */
	public static ModelManager getInstance() {
		if (INSTANCE == null)
			INSTANCE = new ModelManager();
		return INSTANCE;
	}

	/**
	 * Method to add a famix entity to the ModelManager. It will add the entity to the large list of famixentitites, but also add it to its resepective map.
	 */
	public void addFamixElement(Subject fe) {
		famixEntities.add(fe);
		if (fe instanceof FamixPackage) {
			famixPackagesMap.put(((FamixPackage) fe).getUniqueName(), (FamixPackage) fe);
		} else if (fe instanceof FamixClass) {
			famixClassesMap.put(((FamixClass) fe).getUniqueName(), (FamixClass) fe);
		} else if (fe instanceof FamixMethod) {
			famixMethodsMap.put(((FamixMethod) fe).getUniqueName(), (FamixMethod) fe);
		} else if (fe instanceof FamixAttribute) {
			famixFieldsMap.put(((FamixAttribute) fe).getUniqueName(), (FamixAttribute) fe);
		} else if (fe instanceof FamixInvocation) {
			famixInvocationsMap.put(((FamixInvocation) fe).getStringRepresentation(), (FamixInvocation) fe);
		} else if (fe instanceof FamixLocalVariable) {
			famixVariablesMap.put(((FamixLocalVariable) fe).getUniqueName(), (FamixLocalVariable) fe);
		}
	}

	/**
	 * Method to check if a given famixobject exists in the list of famixentities.
	 * @param fe
	 * @return
	 */
	public boolean famixElementExists(Subject fe) {
		return famixEntities.contains(fe);
	}

	/**
	 * @return the list of famixentities
	 */
	public Collection<Subject> getFamixElements() {
		return famixEntities;
	}


	/*
	public FamixObject getFamixElement(FamixObject fe) {
		int index = famixEntities.indexOf(fe);
		if (index != -1)
			return famixEntities.get(index);
		else
			return null;
	}*/

	/*
	 * For testing purposes only!
	 */
	public void clearModel() {
		/*
		 * changes = new ArrayList<IChange>(); famixEntities = new
		 * ArrayList<FamixObject>(); listeners = new
		 * ArrayList<ModelManagerListener>();
		 */
		INSTANCE = new ModelManager();
		getModelManagerChange().clearModel();
	}

	/*
	 * public void printAllChanges(){
	 * System.out.println("--------------------------------------"); for(IChange
	 * ch : changes){ System.out.println(ch.toString()); }
	 * System.out.println("--------------------------------------"); }
	 */

	// /////////////////////////////////////////////////////////////////////////
	//
	// Persisting Model
	//
	// /////////////////////////////////////////////////////////////////////////

	public void saveModel() {
		// CheopsjLog.logInfo("Saving Model");
		File file = getModelFile();
		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		try {
			fos = new FileOutputStream(file);
			out = new ObjectOutputStream(fos);
			out.writeObject(getModelManagerChange().getChanges());
			out.writeObject(famixEntities);

			out.writeObject(famixPackagesMap);
			out.writeObject(famixClassesMap);
			out.writeObject(famixMethodsMap);
			out.writeObject(famixFieldsMap);
			out.writeObject(famixInvocationsMap);
			out.writeObject(famixVariablesMap);

			out.close();
			// CheopsjLog.logInfo("Model Saved");
		} catch (IOException ex) {
			// CheopsjLog.logError(ex);
		}
	}

	public void loadModel() {
		// CheopsjLog.logInfo("Loading Model");
		File file = getModelFile();
		if(file.exists()){
			
			FileInputStream fis = null;
			ObjectInputStream in = null;
			try {
				fis = new FileInputStream(file);
				in = new ObjectInputStream(fis);
				loadFamixEntities(in);

				//TODO load maps
				in.close();
				// CheopsjLog.logInfo("Model Loaded");
			} catch (IOException ex) {
				// CheopsjLog.logError(ex);
			}
			getModelManagerListeners().fireChangesAdded(
					getModelManagerChange().getChanges().toArray(
							new IChange[getModelManagerChange().getChanges().size()]));
		}
	}
	
	@SuppressWarnings("unchecked")
	private void loadFamixEntities(ObjectInputStream in) {
		try {
			List<IChange> changes = getModelManagerChange().getChanges();
			changes = (List<IChange>) in.readObject();
			getModelManagerChange().setChanges(changes);
			
			famixEntities = (List<Subject>) in.readObject();
			
			famixPackagesMap = (Map<String, FamixPackage>) in.readObject();
			famixClassesMap = (Map<String, FamixClass>) in.readObject();
			famixMethodsMap = (Map<String, FamixMethod>) in.readObject();
			famixFieldsMap = (Map<String, FamixAttribute>) in.readObject();
			famixInvocationsMap = (Map<String, FamixInvocation>) in.readObject();
			famixVariablesMap = (Map<String, FamixLocalVariable>) in.readObject();
		}
		catch (IOException ex) {
			// CheopsjLog.logError(ex);
		} catch (ClassNotFoundException ex) {
			// CheopsjLog.logError(ex);
		}
	}

	private File getModelFile() {
		return Activator.getDefault().getStateLocation().append("changemodel.ser").toFile();
	}
	
	public Map<String, FamixPackage> getFamixPackagesMap() {
		return famixPackagesMap;
	}

	public Map<String, FamixClass> getFamixClassesMap() {
		return famixClassesMap;
	}

	public Map<String, FamixMethod> getFamixMethodsMap() {
		return famixMethodsMap;
	}
	
	public List<Subject> getFamixEntities() {
		return famixEntities;
	}
	
	public Map<String, FamixAttribute> getFamixFieldsMap() {
		return famixFieldsMap;
	}
	
	public Map<String, FamixLocalVariable> getFamixVariablesMap() {
		return famixVariablesMap;
	}
	
	public Map<String, FamixInvocation> getFamixInvocationsMap() {
		return famixInvocationsMap;
	}
	
	public ModelManagerListeners getModelManagerListeners() {
		return ModelManagerListeners.getInstance();
	}
	
	public ModelManagerChange getModelManagerChange() {
		return ModelManagerChange.getInstance();
	}
	
	// /////////////////////////////////////////////////////////////////////////
	//
	// Searching the Maps for specific FamixEntities
	//
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * @param identifier
	 */
	public FamixMethod getFamixMethodWithName(String identifier) {
		for (Subject fo : famixEntities) {
			if (fo instanceof FamixMethod) {
				if (((FamixMethod) fo).getUniqueName().equals(identifier)) {
					return (FamixMethod) fo;
				}
			}
		}

		return null;

	}
	
	/**
	 * @param elementName
	 * @return
	 */
	public boolean famixPackageExists(String elementName) {
		return famixPackagesMap.containsKey(elementName);
	}

	/**
	 * @param elementName
	 * @return
	 */
	public FamixPackage getFamixPackage(String elementName) {
		return famixPackagesMap.get(elementName);
	}

	/**
	 * @param elementName
	 * @return
	 */
	public boolean famixClassExists(String elementName) {
		return famixClassesMap.containsKey(elementName);
	}

	/**
	 * @param elementName
	 * @return
	 */
	public FamixClass getFamixClass(String elementName) {
		return famixClassesMap.get(elementName);
	}

	/**
	 * @param elementName
	 * @return
	 */
	public boolean famixMethodExists(String elementName) {
		return famixMethodsMap.containsKey(elementName);
	}

	/**
	 * @param elementName
	 * @return
	 */
	public FamixMethod getFamixMethod(String elementName) {
		return famixMethodsMap.get(elementName);
	}

	/**
	 * @param elementName
	 * @return
	 */
	public boolean famixFieldExists(String elementName) {
		return famixFieldsMap.containsKey(elementName);
	}

	/**
	 * @param elementName
	 * @return
	 */
	public FamixAttribute getFamixField(String elementName) {
		return famixFieldsMap.get(elementName);
	}

	/**
	 * @param stringrepresentation
	 * @return
	 */
	public FamixInvocation getFamixInvocation(String stringrepresentation) {
		return famixInvocationsMap.get(stringrepresentation);
	}

	/**
	 * @param stringrepresentation
	 * @return
	 */
	public boolean famixInvocationExists(String stringrepresentation) {
		return famixInvocationsMap.containsKey(stringrepresentation);
	}

	/**
	 * @param variableName
	 * @return
	 */
	public boolean famixVariableExists(String variableName) {
		return famixVariablesMap.containsKey(variableName);
	}

	/**
	 * @param variableName
	 * @return
	 */
	public FamixLocalVariable getFamixVariable(String variableName) {
		return famixVariablesMap.get(variableName);
	}
}
