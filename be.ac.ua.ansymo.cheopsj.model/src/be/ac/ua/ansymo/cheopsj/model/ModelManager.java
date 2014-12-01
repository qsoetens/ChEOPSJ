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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import be.ac.ua.ansymo.cheopsj.model.changes.IChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Subject;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixAttribute;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixClass;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixInheritanceDefinition;
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
	private Map<String, List<FamixMethod>> famixMethodListMap;
	private Map<String, FamixAttribute> famixFieldsMap;
	private Map<String, FamixInvocation> famixInvocationsMap;
	private Map<String, FamixLocalVariable> famixVariablesMap;

	private Map<String, FamixInheritanceDefinition> famixInheritanceMap;

	//The modelmanager is a Singleton entity, hence the constructor is private.
	//You should always call the static method getInstance() to get the ModelManager instance.
	private static ModelManager INSTANCE = null;

	private ModelManager() {
		famixEntities = new ArrayList<Subject>();

		famixPackagesMap = new HashMap<String, FamixPackage>();
		famixClassesMap = new HashMap<String, FamixClass>();
		famixMethodsMap = new HashMap<String, FamixMethod>();

		famixMethodListMap = new HashMap<String, List<FamixMethod>>();

		famixFieldsMap = new HashMap<String, FamixAttribute>();
		famixInvocationsMap = new HashMap<String, FamixInvocation>();
		famixVariablesMap = new HashMap<String, FamixLocalVariable>();

		famixInheritanceMap = new HashMap<String, FamixInheritanceDefinition>();
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
			//FIX NULLPOINTEREXCEPTION HERE!!!
			if(!famixMethodListMap.containsKey(((FamixMethod) fe).getName())){
				famixMethodListMap.put(((FamixMethod) fe).getName(), new ArrayList<FamixMethod>());	
			}
			famixMethodListMap.get(((FamixMethod) fe).getName()).add((FamixMethod) fe);


		} else if (fe instanceof FamixAttribute) {
			famixFieldsMap.put(((FamixAttribute) fe).getUniqueName(), (FamixAttribute) fe);
		} else if (fe instanceof FamixInvocation) {
			famixInvocationsMap.put(((FamixInvocation) fe).getStringRepresentation(), (FamixInvocation) fe);
		} else if (fe instanceof FamixLocalVariable) {
			famixVariablesMap.put(((FamixLocalVariable) fe).getUniqueName(), (FamixLocalVariable) fe);
		} else if (fe instanceof FamixInheritanceDefinition){
			famixInheritanceMap.put(((FamixInheritanceDefinition) fe).getStringRepresentation(), (FamixInheritanceDefinition)fe);
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
		Subject.resetIDCounter();
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
			out.writeObject(famixInheritanceMap);
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
			famixInheritanceMap = (Map<String, FamixInheritanceDefinition>)in.readObject();
			famixVariablesMap = (Map<String, FamixLocalVariable>) in.readObject();
		}
		catch (IOException ex) {
			// CheopsjLog.logError(ex);
		} catch (ClassNotFoundException ex) {
			// CheopsjLog.logError(ex);
		}
	}

	private File getModelFile() {
		//TODO don't store changes in the workspace, but store them per project.
		//return Activator.getDefault().getStateLocation().append("changemodel.ser").toFile();
		
		return new File("/Users/quinten/Desktop/EXPERIMENT/changemodel.ser");
		
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

	public Map<String, FamixInheritanceDefinition> getFamixInheritanceMap(){
		return famixInheritanceMap;
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

	/*/**
	 * @param identifier
	 */
	/*public FamixMethod getFamixMethodWithName(String identifier) {

		return famixMethodsMap.get(identifier);

		/*
		for (Subject fo : famixEntities) {
			if (fo instanceof FamixMethod) {
				if (((FamixMethod) fo).getUniqueName().equals(identifier)) {
					return (FamixMethod) fo;
				}
			}
		}

		return null;*/

	//}


	public List<FamixMethod> getFamixMethodsWithName(String calledMethodName) {
		if(famixMethodListMap.containsKey(calledMethodName))
			return famixMethodListMap.get(calledMethodName);
		else
			return new ArrayList<FamixMethod>();
	}

	public boolean famixMethodWithNameExists(String name){
		return famixMethodListMap.containsKey(name);
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

	public boolean famixClassWithNameExists(String className){
		for (String key : famixClassesMap.keySet()) {
			String name = key.substring(key.lastIndexOf('.')+1);
			if(name.equals(className)){
				return true;
			}
		}
		return false;
	}

	/**
	 * @param elementName
	 * @return
	 */
	public FamixClass getFamixClass(String elementName) {
		return famixClassesMap.get(elementName);
	}

	public List<FamixClass> getFamixClassesWithName(String className){
		List<FamixClass> resultSet = new ArrayList<FamixClass>();
		for (String key : famixClassesMap.keySet()) {
			String name = key.substring(key.lastIndexOf('.')+1);
			if(name.equals(className)){
				resultSet.add(famixClassesMap.get(key));
			}   
		}
		return resultSet;
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
	 * @param stringrepresentation
	 * @return
	 */
	public FamixInheritanceDefinition getFamixInheritance(String stringrepresentation) {
		return famixInheritanceMap.get(stringrepresentation);
	}

	/**
	 * @param stringrepresentation
	 * @return
	 */
	public boolean famixInheritanceExists(String stringrepresentation) {
		return famixInheritanceMap.containsKey(stringrepresentation);
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
