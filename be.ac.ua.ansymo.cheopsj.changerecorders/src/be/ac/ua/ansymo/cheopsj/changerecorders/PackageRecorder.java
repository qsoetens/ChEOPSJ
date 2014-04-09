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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.PackageDeclaration;

import be.ac.ua.ansymo.cheopsj.model.ModelManager;
import be.ac.ua.ansymo.cheopsj.model.ModelManagerChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Add;
import be.ac.ua.ansymo.cheopsj.model.changes.AtomicChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Change;
import be.ac.ua.ansymo.cheopsj.model.changes.IChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Remove;
import be.ac.ua.ansymo.cheopsj.model.changes.Subject;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixClass;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixPackage;


/**
 * The PackageRecorder, is a changerecorder that records changes made to a package. 
 * A package is represented by it's unique name : <superpackage unique name>'.' <package name>
 * @author quinten
 */
public class PackageRecorder extends AbstractEntityRecorder {
	private FamixPackage famixPackage; //the package to which we link a change
	private FamixPackage parent; //the super package
	private ModelManager manager; //the model manager
	private ModelManagerChange managerChange;
	private String uniqueName; //the unique name of our package
	private String name = "";
	private String parentName = "";

	private List<IType> typeTransporter = new ArrayList<IType>();


	private PackageRecorder(){
		manager = ModelManager.getInstance();
		managerChange = ModelManagerChange.getInstance();
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

						childsTypes = ((ICompilationUnit) childrenList[i]).getAllTypes();
						for(j=0;j<childsTypes.length;j++)
							typeTransporter.add(childsTypes[j]);

					}
					else if (childrenList[i] instanceof IType)
						typeTransporter.add((IType) childrenList[i]);

					// there shouldn't be anything else at this level.
				}
			}
		}
		catch (JavaModelException e) {
			//if there's an exception there, we're in a remove.
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
		name = declaration.getName().getFullyQualifiedName();
	}

	/**
	 * @param uniquename
	 */
	public PackageRecorder(String uniquename) {
		this();
		uniqueName = uniquename;
		name = uniquename;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.ac.ua.cheopsj.Controller.changeRecorders.AbstractEntityRecorder#
	 * createAndLinkFamixElement()
	 */
	

	@Override
	public void storeChange(IChange change) {
		createAndLinkFamixElement();
		createAndLinkChange((AtomicChange) change);
	
		int j=typeTransporter.size();
		
		// if there's something in the transporter, then we must be in a rename
		if (j > 0)
		{
		   
			int i;
			Add[] rename;
			rename = new Add[j];
			for (i=0;i<j;i++)
			{
				rename[i] = new Add(); //added because of hibernate. most likely not needed anymore.
				new ClassRecorder(typeTransporter.get(i)).storeChange(rename[i]);
			}
		}
	}

	
	
	@Override
	protected void createAndLinkFamixElement() {
		if (!manager.famixPackageExists(uniqueName)) {
			famixPackage = new FamixPackage();


			famixPackage.setUniqueName(uniqueName);
			//famixPackage.setName(packageName);

			linkToParent(famixPackage);
			parent = famixPackage.getBelongsToPackage();

			famixPackage.setName(name);
			
			manager.addFamixElement(famixPackage);
		} else {
			famixPackage = manager.getFamixPackage(uniqueName);
			parent = famixPackage.getBelongsToPackage();
		}
	}

	private void linkToParent(FamixPackage pack){
		String packagename = pack.getUniqueName();
		if(packagename.lastIndexOf('.') > 0){ //if there is a '.' in the name, then there is a parent package
			String superPackageName = packagename.substring(0, packagename.lastIndexOf('.'));
			FamixPackage parentPack = manager.getFamixPackage(superPackageName);
			if (parentPack != null) {
				pack.setBelongsToPackage(parentPack);
				parentPack.addPackage(pack);
			} else {
				//parent package did not yet exist, so we have to make it now! + link that to HIS parent
				parentPack = new FamixPackage();
				parentPack.setUniqueName(superPackageName);
				pack.setBelongsToPackage(parentPack);
				parentPack.addPackage(pack);

				linkToParent(parentPack);

				manager.addFamixElement(parentPack);

			}
		}//else there is NO parent package, then do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.ac.ua.cheopsj.Controller.changeRecorders.AbstractEntityRecorder#
	 * createAndLinkChange()
	 */
	@Override
	protected void createAndLinkChange(AtomicChange change) {
		Change latestChange = famixPackage.getLatestChange();
		
		//make sure you don't add or remove the same method twice
		if(latestChange != null && !latestChange.isDummy())
			if(change instanceof Remove && latestChange instanceof Remove)
				return;
			if(change instanceof Add && latestChange instanceof Add)
				return;
			
		change.setChangeSubject(famixPackage);
		famixPackage.addChange(change);

		setStructuralDependencies(change, famixPackage);
		managerChange.addChange(change);
	}

	protected void setStructuralDependencies(AtomicChange change, Subject subject) {
		if (change instanceof Add) {
			if (parent != null) {
				Change parentChange = parent.getLatestAddition();
				if (parentChange != null) {
					change.addStructuralDependency(parentChange);
				}else{
					linkToParentAdditions((FamixPackage)subject);		
				}
			}
			Remove removalChange = subject.getLatestRemoval();
			if (removalChange != null) {
				change.addStructuralDependency(removalChange);
			}
		} else if (change instanceof Remove) {
			//Dependencies to removes of child entities.
			//Subpackages:
			setDependenciesToSubPackages(change, subject);

			//Classes
			setDependenciesToContainingClasses(change, subject);

			// set dependency to addition of this entity
			AtomicChange additionChange = subject.getLatestAddition();
			if (additionChange != null) {
				change.addStructuralDependency(additionChange);
			}
		}
	}

	private void setDependenciesToSubPackages(AtomicChange change,
			Subject subject) {
		Collection<FamixPackage> subpacks = ((FamixPackage)subject).getPackages();
		if (!subpacks.isEmpty()) {
			for(FamixPackage child: subpacks){
				Change childChange = child.getLatestRemoval();
				if (childChange != null) {
					change.addStructuralDependency(childChange);
				}else{
					linkToChildRemoves((FamixPackage)subject);
				}
			}
		}
	}

	private void setDependenciesToContainingClasses(AtomicChange change,
			Subject subject) {
		Collection<FamixClass> classes = ((FamixPackage)subject).getClasses();
		if(!classes.isEmpty()){
			for(FamixClass child: classes){
				Change childChange = child.getLatestRemoval();
				if (childChange != null) {
					change.addStructuralDependency(childChange);
				}else{
					Remove classrem = new Remove();
					child.addChange(classrem);
					classrem.setChangeSubject(child);
					change.addStructuralDependency(classrem);

					classrem.addStructuralDependency(child.getLatestAddition());

					managerChange.addChange(classrem);
					
					//TODO remove all within the class?
				}
			}
		}
	}

	private void linkToChildRemoves(FamixPackage pack) {
		Remove packrem = pack.getLatestRemoval();
		Collection<FamixPackage> subPacks = pack.getPackages();

		for(FamixPackage subpack: subPacks){
			if(subpack.getLatestRemoval() == null){
				Remove subpackrem = new Remove();
				subpack.addChange(subpackrem);
				subpackrem.setChangeSubject(subpack);
				packrem.addStructuralDependency(subpackrem);

				subpackrem.addStructuralDependency(subpack.getLatestAddition());

				managerChange.addChange(subpackrem);
				linkToChildRemoves(subpack);
			}
		}		
	}

	private void linkToParentAdditions(FamixPackage pack) {
		AtomicChange packadd = pack.getLatestAddition();
		FamixPackage superPack = pack.getBelongsToPackage();

		if(superPack != null && superPack.getLatestAddition() == null){
			AtomicChange superpackadd = new Add();
			superpackadd.setIntent(packadd.getIntent());
			superpackadd.setTimeStamp(packadd.getTimeStamp());
			superpackadd.setUser(packadd.getUser());
			superpackadd.setIsApplied(packadd.getIsApplied());
			
			superpackadd.setChangeSubject(superPack);
			superPack.addChange(superpackadd);

			packadd.addStructuralDependency(superpackadd);

			managerChange.addChange(superpackadd);
			linkToParentAdditions(superPack);
		}
	}
}
