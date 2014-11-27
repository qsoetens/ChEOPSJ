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
package be.ac.ua.ansymo.cheopsj.model.famix;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.eclipse.jdt.ui.ISharedImages;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.swt.graphics.Image;

@Entity
public class FamixPackage extends FamixEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4653287231040376260L;

	/**
	 *
	 */
	private Collection<FamixClass> classes = null;

	/*
	 * (non-javadoc)
	 */
	private FamixPackage belongsToPackage = null;

	/**
	 *
	 */
	private Collection<FamixPackage> packages = null;

	/**
	 *
	 */
	private Collection<FamixGlobalVariable> globalVariable = null;

	public FamixPackage() {
		super();
		this.classes = new ArrayList<FamixClass>();
		this.packages = new ArrayList<FamixPackage>();
	}
	
	/**
	 * Getter of the property <tt>classes</tt>
	 *
	 * @return Returns the classes.
	 * 
	 */
	@OneToMany(
			mappedBy="belongsToPackage",
			targetEntity=be.ac.ua.ansymo.cheopsj.model.famix.FamixClass.class)
	public Collection<FamixClass> getClasses()
	{
		return classes;
	}

	/**
	 * Setter of the property <tt>class</tt>
	 *
	 * @param class the class to set.
	 *
	 */
	public void setClasses(Collection<FamixClass> classes){
		this.classes = classes;
	}


	/**
	 * Ensures that this collection contains the specified element (optional
	 * operation). 
	 *
	 * @param element whose presence in this collection is to be ensured.
	 * @see	java.util.Collection#add(Object)
	 *
	 */
	public boolean addClass(FamixClass clazz){
		return this.classes.add(clazz);
	}

	/**
	 * Getter of the property <tt>famixPackage1</tt>
	 *
	 * @return Returns the famixPackage1.
	 * 
	 */
	@OneToMany(
			mappedBy="belongsToPackage",
			targetEntity=be.ac.ua.ansymo.cheopsj.model.famix.FamixPackage.class)
	public Collection<FamixPackage> getPackages()
	{
		return packages;
	}

	/**
	 * Setter of the property <tt>famixPackage1</tt>
	 *
	 * @param famixPackage1 the famixPackage1 to set.
	 *
	 */
	public void setPackages(Collection<FamixPackage> packages){
		this.packages = packages;
	}


	/**
	 * Ensures that this collection contains the specified element (optional
	 * operation). 
	 *
	 * @param element whose presence in this collection is to be ensured.
	 * @see	java.util.Collection#add(Object)
	 *
	 */
	public boolean addPackage(FamixPackage Package){
		return this.packages.add(Package);
	}

	/**
	 * Getter of the property <tt>belongsToPackage</tt>
	 *
	 * @return Returns the belongsToPackage.
	 * 
	 */
	@ManyToOne(
			targetEntity=be.ac.ua.ansymo.cheopsj.model.famix.FamixPackage.class)
	public FamixPackage getBelongsToPackage()
	{
		return belongsToPackage;
	}

	/**
	 * Setter of the property <tt>belongsToPackage</tt>
	 *
	 * @param belongsToPackage The belongsToPackage to set.
	 *
	 */
	public void setBelongsToPackage(FamixPackage belongsToPackage){
		this.belongsToPackage = belongsToPackage;
	}



	/**
	 * Getter of the property <tt>globalVariable</tt>
	 *
	 * @return Returns the globalVariable.
	 * 
	 */
	@Transient
	public Collection<FamixGlobalVariable> getGlobalVariable()
	{
		return globalVariable;
	}

	/**
	 * Setter of the property <tt>globalVariable</tt>
	 *
	 * @param globalVariable the globalVariable to set.
	 *
	 */
	public void setGlobalVariable(Collection<FamixGlobalVariable> globalVariable){
		this.globalVariable = globalVariable;
	}


	/**
	 * Ensures that this collection contains the specified element (optional
	 * operation). 
	 *
	 * @param element whose presence in this collection is to be ensured.
	 * @see	java.util.Collection#add(Object)
	 *
	 */
	public boolean addGlobalVariable(FamixGlobalVariable globalVariable){
		return this.globalVariable.add(globalVariable);
	}

	@Override
	@Transient
	public String getFamixType() {
		return "Package";
	}
	
	/* (non-Javadoc)
	 * @see be.ac.ua.cheopsj.Model.Famix.FamixEntity#getIcon()
	 */
	@Override
	@Transient
	public Image getIcon() {
		return JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_PACKAGE);
	}
}
