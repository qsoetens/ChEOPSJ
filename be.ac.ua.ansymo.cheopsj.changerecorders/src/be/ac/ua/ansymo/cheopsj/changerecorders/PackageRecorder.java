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

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.PackageDeclaration;

import be.ac.ua.ansymo.cheopsj.model.changes.Add;
import be.ac.ua.ansymo.cheopsj.model.changes.AtomicChange;
import be.ac.ua.ansymo.cheopsj.model.changes.IChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Subject;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixPackage;

import java.util.List;
import java.util.ArrayList;


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
	private List<IType> typeTransporter = new ArrayList<IType>();
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
		
		
		// TODO fix: doesn't link subclasses to the new package, even though the names are correct.
		try {
			if (element.hasChildren()) // see if there are any classes out there under this newly created package. this happens in case of a rename.
			{
				IJavaElement[] childrenList = element.getChildren();
				IType[] childsTypes;
				int i,j;
				for(i=0;i<childrenList.length;i++)
					{
					if (childrenList[i] instanceof ICompilationUnit)
					{

						childsTypes = ((ICompilationUnit) childrenList[i]).getTypes();
						for(j=0;j<childsTypes.length;j++)
					//		new ClassRecorder(childsTypes[j]).storeChange(new Add());
							typeTransporter.add(childsTypes[j]);
					}
					else if (childrenList[i] instanceof IType)
					//	new ClassRecorder((IType) childrenList[i]).storeChange(new Add());
						typeTransporter.add((IType) childrenList[i]);
						
					// there shouldn't be anything else at this level.
					}
				
				
			}
		} catch (JavaModelException e) {
			//if there's an exception there, something is terribly wrong, so do nothing.
			e.printStackTrace();
		}
		
		
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

	
	@Override
	public void storeChange(IChange change) {
		if (typeTransporter.size() > 0)
		{
			int i;
			Add rename;
			for (i=0;i<typeTransporter.size();i++)
			{
				rename = new Add();
				rename.addStructuralDependee((AtomicChange) change);
				new ClassRecorder(typeTransporter.get(i)).storeChange(rename);
			}
			
		}
		
		createAndLinkFamixElement();
		createAndLinkChange((AtomicChange) change);
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
