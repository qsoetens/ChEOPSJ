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
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.ui.ISharedImages;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.swt.graphics.Image;

@Entity
public class FamixClass extends FamixEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2205748144617065011L;
	private FamixPackage belongsToPackage;
	private FamixClass belongsToClass;
	private boolean isAbstract;
	private Collection<FamixMethod> methods = null;
	private Collection<FamixAttribute> attributes = null;
	private Collection<FamixClass> nestedClasses = null;
	
	private Collection<FamixInheritanceDefinition> superclasses = null;
	private Collection<FamixInheritanceDefinition> subclasses = null;

	public FamixClass() {
		methods = new ArrayList<FamixMethod>();
		attributes = new ArrayList<FamixAttribute>();
		nestedClasses = new ArrayList<FamixClass>();
		
		superclasses = new ArrayList<FamixInheritanceDefinition>(); 
		subclasses = new ArrayList<FamixInheritanceDefinition>();
	}

	@ManyToOne(
			targetEntity=be.ac.ua.ansymo.cheopsj.model.famix.FamixPackage.class)
	public FamixPackage getBelongsToPackage() {
		return belongsToPackage;
	}

	public void setBelongsToPackage(FamixPackage pack) {
		this.belongsToPackage = pack;
	}

	@ManyToOne(
			targetEntity=be.ac.ua.ansymo.cheopsj.model.famix.FamixClass.class)
	public FamixClass getBelongsToClass() {
		return belongsToClass;
	}

	public void setBelongsToClass(FamixClass clazz) {
		this.belongsToClass = clazz;
		// setBelongsToPackage(clazz.getBelongsToPackage());
	}

	@Transient
	public boolean isAbstract() {
		return isAbstract;
	}

	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}

	/**
	 * Ensures that this collection contains the specified element (optional
	 * operation).
	 * 
	 * @param element
	 *            whose presence in this collection is to be ensured.
	 * @see java.util.Collection#add(Object)
	 * 
	 */
	public boolean addMethod(FamixMethod method) {
		return this.methods.add(method);
	}

	/**
	 * @param methodName
	 * @return
	 */
	public FamixMethod findMethod(String methodName) {
		for (FamixMethod method : this.methods) {
			if (method.getUniqueName().equals(methodName)) {
				return method;
			}
		}

		return null;
	}

	@OneToMany(
			mappedBy="belongsToClass",
			targetEntity=be.ac.ua.ansymo.cheopsj.model.famix.FamixMethod.class)
	public Collection<FamixMethod> getMethods() {
		return methods;
	}

	public void setMethods(Collection<FamixMethod> methods) {
		this.methods = methods;
	}

	/**
	 * Ensures that this collection contains the specified element (optional
	 * operation).
	 * 
	 * @param element
	 *            whose presence in this collection is to be ensured.
	 * @see java.util.Collection#add(Object)
	 * 
	 */
	public boolean addAttribute(FamixAttribute attribute) {
		return this.attributes.add(attribute);
	}

	@OneToMany(
			mappedBy="belongsToClass",
			targetEntity=be.ac.ua.ansymo.cheopsj.model.famix.FamixAttribute.class)
	public Collection<FamixAttribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(Collection<FamixAttribute> attributes) {
		this.attributes = attributes;
	}

	@Transient
	@Override
	public String getFamixType() {
		return "Class";
	}

	public void addNestedClass(FamixClass clazz) {
		nestedClasses.add(clazz);
	}
	
	@OneToMany(
			mappedBy="belongsToClass",
			targetEntity=be.ac.ua.ansymo.cheopsj.model.famix.FamixClass.class)
	public Collection<FamixClass> getNestedClasses() {
		return nestedClasses;
	}

	public void setNestedClasses(Collection<FamixClass> nestedClasses) {
		this.nestedClasses = nestedClasses;
	}

	public void addSuperClass(FamixInheritanceDefinition superclass){
		if(superclass.getSubClass().equals(this)){
			superclasses.add(superclass);
		}
	}
	
	public void addSubClass(FamixInheritanceDefinition subclass){
		if(subclass.getSuperClass().equals(this)){
			subclasses.add(subclass);
		}
	}
	
	@OneToMany(
			mappedBy="superClass",
			targetEntity=be.ac.ua.ansymo.cheopsj.model.famix.FamixInheritanceDefinition.class)
	public Collection<FamixInheritanceDefinition> getSuperclasses(){
		return superclasses;
	}
	
	@OneToMany(
			mappedBy="subClass",
			targetEntity=be.ac.ua.ansymo.cheopsj.model.famix.FamixInheritanceDefinition.class)
	public Collection<FamixInheritanceDefinition> getSubclasses(){
		return subclasses;
	}
	

	public void setSuperclasses(Collection<FamixInheritanceDefinition> superclasses) {
		this.superclasses = superclasses;
	}

	public void setSubclasses(Collection<FamixInheritanceDefinition> subclasses) {
		this.subclasses = subclasses;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.ac.ua.cheopsj.Model.Famix.FamixEntity#getIcon()
	 */
	@Transient
	@Override
	public Image getIcon() {
		Image icon = null;
		if (this.getBelongsToClass() != null) { // it's a nested class;
			if (Flags.isPublic(this.getFlags())) {
				icon = JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_INNER_CLASS_PUBLIC);
			} else if (Flags.isPrivate(getFlags())) {
				icon = JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_INNER_CLASS_PRIVATE);
			} else if (Flags.isProtected(this.getFlags())) {
				icon = JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_INNER_CLASS_PROTECTED);
			} else if (Flags.isPackageDefault(this.getFlags())) {
				icon = JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_INNER_CLASS_DEFAULT);
			}
		} else {
			// it's just a class
			if (Flags.isPublic(this.getFlags())) {
				icon = JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_CLASS);
			} else if (Flags.isPackageDefault(this.getFlags())) {
				icon = JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_CLASS_DEFAULT);
			}
		}
		// XXX decorate with stuff ... abstract, static etc
		return icon;
	}

	@Transient
	public Collection<String> getSuperClassNames() {
		Collection<String> superClassNames = new ArrayList<String>();
		for(FamixInheritanceDefinition def : superclasses){
			superClassNames.add(def.getSuperClass().getUniqueName());
		}
		return superClassNames;
	}
}
