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
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import be.ac.ua.ansymo.cheopsj.model.changes.Add;
import be.ac.ua.ansymo.cheopsj.model.changes.AtomicChange;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixClass;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixEntity;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixPackage;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeEntity;

//TODO need to fix inheritance relationships and links to interfaces
public class ClassRecorder extends AbstractEntityRecorder {
	private FamixClass famixClass;
	private FamixEntity parent;
	private String uniqueName = "";
	private int flags;
	private String name = "";
	private boolean isTestClass = false;

	private ClassRecorder(){
		//get manager instance
	}

	public ClassRecorder(FamixClass element){
		famixClass = element;
		parent = element.getBelongsToPackage();
		if(parent == null)
			parent = element.getBelongsToClass();

		uniqueName = element.getUniqueName();
		name = element.getName();
		flags = element.getFlags();
		isTestClass = element.isTestClass();
	}

	public ClassRecorder(IType element) {
		this();

		//can be null!!!!
		parent = findParentEntity(element);

		//set the unique (fully qualified) name of the class entity.
		uniqueName = element.getFullyQualifiedName();
		uniqueName = uniqueName.replace('$', '.');
		name = element.getElementName();

		//set the flags
		/*try {
			//When Using jUnit 3, we can identify TestClasses by it using the TestCase interface
			if(element.getSuperclassName().equals("TestCase")){
				isTestClass = true;
			}
			//In jUnit 4, testclasses are identified by them containing @Test methods, so we can't identify them at this time!
			
			flags = element.getFlags();
		} catch (JavaModelException e) {
			e.printStackTrace();
		}*/		
	}

	public ClassRecorder(TypeDeclaration declaration) {
		this();

		name = declaration.getName().getIdentifier();

		//find the parent famix entity (if any)
		parent = findParentEntity(declaration);

		//set the unique (fully qualified) name of the class entity
		if(parent != null && parent.getUniqueName() != ""){
			uniqueName = parent.getUniqueName() + "." + declaration.getName().getFullyQualifiedName();
		}else{
			//there was no package declaration == default package
			uniqueName = declaration.getName().getFullyQualifiedName();
		}
		
		//When Using jUnit 3, we can identify TestClasses by it using the TestCase interface
		/*if(declaration.getSuperclass().getFullyQualifiedName().equals("TestCase")){
			isTestClass = true;
		}*/
		//In jUnit 4, testclasses are identified by them containing @Test methods, so we can't identify them at this time!

		//set the flags
		flags = declaration.getFlags();
	}

	public ClassRecorder(SourceCodeEntity entity, SourceCodeEntity parentEntity) {
		this();
		if(manager.famixClassExists(parentEntity.getUniqueName()))
			parent = manager.getFamixClass(parentEntity.getUniqueName());

		uniqueName = entity.getUniqueName();
		int j = uniqueName.lastIndexOf('.');
		name = uniqueName.substring(j,uniqueName.length());

		flags = entity.getModifiers();
		//TODO check supertype
	}

	public ClassRecorder(String uniquename, String classname, String packagename) {
		this();
		if(manager.famixClassExists(packagename))
			parent = manager.getFamixClass(packagename);
		uniqueName = uniquename;
		name = classname;
	}

	private FamixEntity findParentEntity(IType element) {
		IJavaElement parentJavaElement = element.getParent();
		if (parentJavaElement != null) {
			if (parentJavaElement instanceof ICompilationUnit) {
				parentJavaElement = parentJavaElement.getParent();
			}
			if (parentJavaElement instanceof IPackageFragment) {
				return manager.getFamixPackage(parentJavaElement.getElementName());
			}
			if (parentJavaElement instanceof IType) {
				return manager.getFamixClass(((IType) parentJavaElement).getFullyQualifiedName());
			}
		}
		return null;
	}

	private FamixEntity findParentEntity(TypeDeclaration declaration) {
		ASTNode parentASTNode = declaration.getParent();
		String parentName = "";
		if(parentASTNode != null){
			if (parentASTNode instanceof CompilationUnit) {
				PackageDeclaration pack = ((CompilationUnit) parentASTNode).getPackage();
				if(pack != null){
					parentName = pack.getName().getFullyQualifiedName();
					return manager.getFamixPackage(parentName);
				}
			}
			if (parentASTNode instanceof TypeDeclaration) {
				return manager.getFamixClass(findParentName(parentASTNode));
			}
		}
		return null;
	}

	private String findParentName(ASTNode node){
		if(node instanceof TypeDeclaration){
			String name = ((TypeDeclaration)node).getName().getFullyQualifiedName();
			ASTNode parentASTNode = node.getParent();
			if(parentASTNode != null){
				if (parentASTNode instanceof CompilationUnit) {
					PackageDeclaration pack = ((CompilationUnit) parentASTNode).getPackage();
					if(pack != null){
						return pack.getName().getFullyQualifiedName() + "." + name;
					}
				}
				if (parentASTNode instanceof TypeDeclaration) {
					return findParentName(parentASTNode) + "." + name;
				}
			}
		}
		return "";
	}



	/*public ClassRecorder(String name) {
		manager = ModelManager.getInstance();
		famixClass = manager.getFamixClass(name);
		parent = famixClass.getBelongsToPackage();
		// causes nullpointerexceptions!!!
	}*/

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.ac.ua.cheopsj.Controller.changeRecorders.AbstractEntityRecorder#
	 * createAndLinkFamixElement()
	 */
	@Override
	protected void createAndLinkFamixElement() {
		if (!manager.famixClassExists(uniqueName)) {
			famixClass = new FamixClass();

			famixClass.setUniqueName(uniqueName);
			//famixClass.setName(declaration.getName().getIdentifier());

			setClassFlagsAndParent(famixClass);
			famixClass.setName(name);
			famixClass.setTestClass(isTestClass);
			manager.addFamixElement(famixClass);
		} else {
			famixClass = manager.getFamixClass(uniqueName);
			if(famixClass.isDummy()){
				//If it was a dummy, undummy it!
				setClassFlagsAndParent(famixClass);

				famixClass.setDummy(false);
			}else{
				parent = famixClass.getBelongsToPackage();
			}
		}
	}

	private void setClassFlagsAndParent(FamixClass famixClass) {
		famixClass.setFlags(flags);

		if(parent instanceof FamixClass){
			parent = manager.getFamixClass(parent.getUniqueName());
			if(parent != null){
				famixClass.setBelongsToClass((FamixClass) parent);
				((FamixClass) parent).addNestedClass(famixClass);
			}
		}else if(parent instanceof FamixPackage){
			parent = manager.getFamixPackage(parent.getUniqueName());
			if(parent != null){
				famixClass.setBelongsToPackage((FamixPackage) parent);
				((FamixPackage) parent).addClass(famixClass);
			}
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
		if(change instanceof Add){
			Add a = managerChange.getLastestAddition(famixClass);
			if(a != null && a.isDummy()){
				change = a;
				change.setDummy(false);
			}
		}

		change.setChangeSubject(famixClass);
		famixClass.addChange(change);

		setStructuralDependencies(change, famixClass, parent);
		managerChange.addChange(change);
	}

}
