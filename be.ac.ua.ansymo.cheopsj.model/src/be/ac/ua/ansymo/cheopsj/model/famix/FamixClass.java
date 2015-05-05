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

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.ui.ISharedImages;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.swt.graphics.Image;

import be.ac.ua.ansymo.cheopsj.model.changes.Add;
import be.ac.ua.ansymo.cheopsj.model.changes.Change;
import be.ac.ua.ansymo.cheopsj.model.changes.Remove;

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

	public FamixPackage getBelongsToPackage() {
		return belongsToPackage;
	}

	public void setBelongsToPackage(FamixPackage pack) {
		this.belongsToPackage = pack;
	}

	public FamixClass getBelongsToClass() {
		return belongsToClass;
	}

	public void setBelongsToClass(FamixClass clazz) {
		this.belongsToClass = clazz;
		// setBelongsToPackage(clazz.getBelongsToPackage());
	}

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

	@Override
	public String getFamixType() {
		return "Class";
	}

	public void addNestedClass(FamixClass clazz) {
		nestedClasses.add(clazz);
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
	
	public Collection<FamixInheritanceDefinition> getSuperClasses(){
		return superclasses;
	}
	
	public Collection<FamixInheritanceDefinition> getSubClasses(){
		return subclasses;
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.ac.ua.cheopsj.Model.Famix.FamixEntity#getIcon()
	 */
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
			} else {
				icon = JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_CLASS);
			}
		}
		// XXX decorate with stuff ... abstract, static etc
		return icon;
	}

	public Collection<String> getSuperClassNames() {
		Collection<String> superClassNames = new ArrayList<String>();
		for(FamixInheritanceDefinition def : superclasses){
			superClassNames.add(def.getSuperClass().getUniqueName());
		}
		return superClassNames;
	}
	
	public int[] aggregateChanges() {
		int[] changes = {0,0,0};
		
		changes[0] += getAffectingChanges().size();
		for (Change change : getAffectingChanges()) {
			if (change instanceof Add) {
				changes[1]++;
			} else if (change instanceof Remove) {
				changes[2]++;
			}
		}
		
		for (FamixMethod m : this.methods) {
			int[] mChanges = m.aggregateChanges();
			changes[0] += mChanges[0];
			changes[1] += mChanges[1];
			changes[2] += mChanges[2];
		}
		
		for (FamixAttribute a : this.attributes) {
			int[] aChanges = a.aggregateChanges();
			changes[0] += aChanges[0];
			changes[1] += aChanges[1];
			changes[2] += aChanges[2];
		}
		
		for (FamixClass c : this.nestedClasses) {
			int[] cChanges = c.aggregateChanges();
			changes[0] += cChanges[0];
			changes[1] += cChanges[1];
			changes[2] += cChanges[2];
		}
		
		return changes;
	}
}
