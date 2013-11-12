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
package be.ac.ua.ansymo.cheopsj.changerecorders;

import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.dom.PackageDeclaration;

import be.ac.ua.ansymo.cheopsj.model.changes.Add;
import be.ac.ua.ansymo.cheopsj.model.changes.AtomicChange;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixPackage;


/**
 * The PackageRecorder, is a changerecorder that records changes made to a package. 
 * A package is represented by it's unique name : <superpackage unique name>'.' <package name>
 * @author quinten
 */
public class PackageRecorder extends AbstractEntityRecorder {
	private FamixPackage famixPackage; //the package to which we link a change
	private FamixPackage parent; //the super package
	private String uniqueName = ""; //the unique name of our package
	private String parentName = "";
	private String name = "";

	private PackageRecorder(){
	}

	/**
	 * Constructor to create a PackageRecorder based on an IPackageFragment, which is a representation of a package in the JDT JavaModel.
	 * @see be.ac.ua.ansymo.cheopsj.logger.listeners.ChangeRecorder
	 * @see org.eclipse.jdt.core.IPackageFragment
	 * @param element The IPackageFragment.
	 */
	public PackageRecorder(IPackageFragment element) {
		this();
		uniqueName = element.getElementName();
		if(uniqueName.lastIndexOf('.') > 0){ //if there is a '.' in the name, then there is a parent package
			parentName = uniqueName.substring(0, uniqueName.lastIndexOf('.'));
		}		
		
		name = element.getElementName();
	}

	/**
	 * Constructor to create a PackageRecorder based on a PackageDeclaration, which is a representation of a package in the AST of a Compilation Unit. 
	 * @see be.ac.ua.ansymo.cheopsj.logger.listeners.ChangeRecorder
	 * @see org.eclipse.jdt.core.dom.PackageDeclaration
	 * @param declaration The PackageDeclaration 
	 */
	public PackageRecorder(PackageDeclaration declaration) {
		this();
		uniqueName = declaration.getName().getFullyQualifiedName();
		if(uniqueName.lastIndexOf('.') > 0){ //if there is a '.' in the name, then there is a parent package
			parentName = uniqueName.substring(0, uniqueName.lastIndexOf('.'));
		}
		name = declaration.getName().getFullyQualifiedName();
	}

	/**
	 * @param uniquename
	 */
	public PackageRecorder(String uniquename) {
		this();
		uniqueName = uniquename;
		if(uniqueName.lastIndexOf('.') > 0){ //if there is a '.' in the name, then there is a parent package
			parentName = uniqueName.substring(0, uniqueName.lastIndexOf('.'));
		}
		name = uniquename;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.ac.ua.cheopsj.Controller.changeRecorders.AbstractEntityRecorder#
	 * createAndLinkFamixElement()
	 */
	@Override
	protected void createAndLinkFamixElement() {
		if (!manager.famixPackageExists(uniqueName)) {
			famixPackage = new FamixPackage();

			famixPackage.setUniqueName(uniqueName);
			famixPackage.setName(name);

			if(parentName != "" && !manager.famixPackageExists(parentName)){
				PackageRecorder recorder = new PackageRecorder(parentName);
				recorder.storeChange(new Add());
			}
			if(manager.famixPackageExists(parentName)){
				FamixPackage parentpack = manager.getFamixPackage(parentName);
				famixPackage.setBelongsToPackage(parentpack);
				parentpack.addPackage(famixPackage);
			}
			
			manager.addFamixElement(famixPackage);
		} else {
			famixPackage = manager.getFamixPackage(uniqueName);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.ac.ua.cheopsj.Controller.changeRecorders.AbstractEntityRecorder#
	 * createAndLinkChange()
	 */
	@Override
	protected void createAndLinkChange(AtomicChange change) {
		change.setChangeSubject(famixPackage);
		famixPackage.addChange(change);

		parent = manager.getFamixPackage(parentName);
		setStructuralDependencies(change, famixPackage, parent);
		managerChange.addChange(change);
	}
}
