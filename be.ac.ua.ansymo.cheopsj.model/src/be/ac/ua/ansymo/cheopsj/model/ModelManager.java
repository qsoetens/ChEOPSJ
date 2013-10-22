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

import hibernate.session.SessionHandler;
import hibernate.session.api.ISession;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import be.ac.ua.ansymo.cheopsj.model.changes.Add;
import be.ac.ua.ansymo.cheopsj.model.changes.AtomicChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Change;
import be.ac.ua.ansymo.cheopsj.model.changes.Remove;
import be.ac.ua.ansymo.cheopsj.model.changes.Subject;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixAttribute;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixClass;
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

	//The modelmanager is a Singleton entity, hence the constructor is private.
	//You should always call the static method getInstance() to get the ModelManager instance.
	private static ModelManager INSTANCE = null;

	private ModelManager() {}

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
		ISession lSession = null;
		try {
			lSession = SessionHandler.getHandler().getCurrentSession();
			lSession.startTransaction();
			lSession.saveObject(fe);

			lSession.flush();
			lSession.clear();
		} catch (Exception e) {
			if (lSession != null) {
				try {
					lSession.rollbackTransaction();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		} finally {
			if (lSession != null) {
				try {
					lSession.endTransaction();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	//	/**
	//	 * Method to check if a given famixobject exists in the list of famixentities.
	//	 * @param fe
	//	 * @return
	//	 */
	//	public boolean famixElementExists(Subject fe) {
	//		return famixEntities.contains(fe);
	//	}

	/**
	 * @return the list of famixentities
	 */
	public Collection<Subject> getFamixElements() {
		List<Subject> subjects = new ArrayList<Subject>();

		try {
			ISession lSession = SessionHandler.getHandler().getCurrentSession();
			subjects = lSession.query("from Subject", Subject.class);
		} catch (Exception ee) {
		}

		return subjects;
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



	//	public List<Subject> getFamixEntities() {
	//		return famixEntities;
	//	}

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
		List<FamixMethod> methods = new ArrayList<FamixMethod>();
		try {
			ISession lSession = SessionHandler.getHandler().getCurrentSession();
			methods = lSession.query("from FamixMethod as method where method.name='"+calledMethodName+"'", FamixMethod.class);
		} catch (Exception ee) {
		}
		if(methods.isEmpty())
			return new ArrayList<FamixMethod>();
		else
			return methods;
	}

	public boolean famixMethodWithNameExists(String name){
		List<FamixMethod> methods = new ArrayList<FamixMethod>();
		try {
			ISession lSession = SessionHandler.getHandler().getCurrentSession();
			methods = lSession.query("from FamixMethod as method where method.name='"+name+"'", FamixMethod.class);
		} catch (Exception ee) {
		}
		if(methods.isEmpty())
			return false;
		else
			return true;
	}


	/**
	 * @param elementName
	 * @return
	 */
	public boolean famixPackageExists(String elementName) {
		List<FamixPackage> packages = new ArrayList<FamixPackage>();

		try {
			ISession lSession = SessionHandler.getHandler().getCurrentSession();
			packages = lSession.query("from FamixPackage as pack where pack.uniqueName'"+elementName+"'", FamixPackage.class);
		} catch (Exception ee) {
		}

		if(packages.isEmpty())
			return false;
		else
			return true;

	}

	/**
	 * @param elementName
	 * @return
	 */
	public FamixPackage getFamixPackage(String elementName) {
		List<FamixPackage> packages = new ArrayList<FamixPackage>();

		try {
			ISession lSession = SessionHandler.getHandler().getCurrentSession();
			packages = lSession.query("from FamixPackage as pack where pack.uniqueName='"+elementName+"'", FamixPackage.class);
		} catch (Exception ee) {
		}

		if(packages.isEmpty())
			return null;
		else
			return packages.get(0);
	}

	/**
	 * @param elementName
	 * @return
	 */
	public boolean famixClassExists(String elementName) {
		List<FamixClass> classes = new ArrayList<FamixClass>();
		try {
			ISession lSession = SessionHandler.getHandler().getCurrentSession();
			classes = lSession.query("from FamixClass as clazz where clazz.uniqueName='"+elementName+"'", FamixClass.class);
		} catch (Exception ee) {
		}
		if(classes.isEmpty())
			return false;
		else
			return true;
	}

	/**
	 * @param elementName
	 * @return
	 */
	public FamixClass getFamixClass(String elementName) {
		List<FamixClass> classes = new ArrayList<FamixClass>();

		try {
			ISession lSession = SessionHandler.getHandler().getCurrentSession();
			classes = lSession.query("from FamixClass as clazz where clazz.uniqueName='"+elementName+"'", FamixClass.class);
		} catch (Exception ee) {
			ee.printStackTrace();
		}

		if(classes.isEmpty())
			return null;
		else
			return classes.get(0);
	}

	/**
	 * @param elementName
	 * @return
	 */
	public boolean famixMethodExists(String elementName) {
		List<FamixMethod> methods = new ArrayList<FamixMethod>();
		try {
			ISession lSession = SessionHandler.getHandler().getCurrentSession();
			methods = lSession.query("from FamixMethod as method where method.uniqueName='"+elementName+"'", FamixMethod.class);
		} catch (Exception ee) {
		}
		if(methods.isEmpty())
			return false;
		else
			return true;
	}

	/**
	 * @param elementName
	 * @return
	 */
	public FamixMethod getFamixMethod(String elementName) {
		List<FamixMethod> methods = new ArrayList<FamixMethod>();
		try {
			ISession lSession = SessionHandler.getHandler().getCurrentSession();
			methods = lSession.query("from FamixMethod as method where method.uniqueName='"+elementName+"'", FamixMethod.class);
		} catch (Exception ee) {
		}
		if(methods.isEmpty())
			return null;
		else
			return methods.get(0);
	}

	/**
	 * @param elementName
	 * @return
	 */
	public boolean famixFieldExists(String elementName) {
		List<FamixAttribute> fields = new ArrayList<FamixAttribute>();
		try {
			ISession lSession = SessionHandler.getHandler().getCurrentSession();
			fields = lSession.query("from FamixAttribute as field where field.uniqueName='"+elementName+"'", FamixAttribute.class);
		} catch (Exception ee) {
		}
		if(fields.isEmpty())
			return false;
		else
			return true;
	}

	/**
	 * @param elementName
	 * @return
	 */
	public FamixAttribute getFamixField(String elementName) {
		List<FamixAttribute> fields = new ArrayList<FamixAttribute>();
		try {
			ISession lSession = SessionHandler.getHandler().getCurrentSession();
			fields = lSession.query("from FamixAttribute as field where field.uniqueName='"+elementName+"'", FamixAttribute.class);
		} catch (Exception ee) {
		}
		if(fields.isEmpty())
			return null;
		else
			return fields.get(0);
	}

	/**
	 * @param stringrepresentation
	 * @return
	 */
	public FamixInvocation getFamixInvocation(String stringrepresentation) {
		List<FamixInvocation> invs = new ArrayList<FamixInvocation>();
		try {
			ISession lSession = SessionHandler.getHandler().getCurrentSession();
			invs = lSession.query("from FamixInvocation as inv where inv.stringRepresentation='"+stringrepresentation+"'", FamixInvocation.class);
		} catch (Exception ee) {
		}
		if(invs.isEmpty())
			return null;
		else
			return invs.get(0);
	}

	/**
	 * @param stringrepresentation
	 * @return
	 */
	public boolean famixInvocationExists(String stringrepresentation) {
		List<FamixInvocation> invs = new ArrayList<FamixInvocation>();
		try {
			ISession lSession = SessionHandler.getHandler().getCurrentSession();
			invs = lSession.query("from FamixInvocation as inv where inv.stringRepresentation='"+stringrepresentation+"'", FamixInvocation.class);
		} catch (Exception ee) {
		}
		if(invs.isEmpty())
			return false;
		else
			return true;
	}

	/**
	 * @param variableName
	 * @return
	 */
	public boolean famixVariableExists(String variableName) {
		List<FamixLocalVariable> vars = new ArrayList<FamixLocalVariable>();
		try {
			ISession lSession = SessionHandler.getHandler().getCurrentSession();
			vars = lSession.query("from FamixLocalVariable as var where var.uniqueName='"+variableName+"'", FamixLocalVariable.class);
		} catch (Exception ee) {
		}
		if(vars.isEmpty())
			return false;
		else
			return true;
	}

	/**
	 * @param variableName
	 * @return
	 */
	public FamixLocalVariable getFamixVariable(String variableName) {
		List<FamixLocalVariable> vars = new ArrayList<FamixLocalVariable>();
		try {
			ISession lSession = SessionHandler.getHandler().getCurrentSession();
			vars = lSession.query("from FamixLocalVariable as var where var.uniqueName='"+variableName+"'", FamixLocalVariable.class);
		} catch (Exception ee) {
		}
		if(vars.isEmpty())
			return null;
		else
			return vars.get(0);
	}

}
