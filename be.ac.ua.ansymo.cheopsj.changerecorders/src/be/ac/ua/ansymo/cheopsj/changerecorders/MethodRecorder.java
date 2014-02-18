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

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import be.ac.ua.ansymo.cheopsj.model.changes.AtomicChange;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixClass;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixMethod;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeEntity;



public class MethodRecorder extends AbstractEntityRecorder {
	private FamixMethod famixMethod;
	private FamixClass parent; // TODO is there something like a nested method inside another method?
	private String uniquename = ""; //TODO need to add parameters to unique naming
	//TODO need to link method to return type
	private int flags = 0;
	private boolean isTest = false;
	private String name = "";
	
	private MethodRecorder(){
	}

	public MethodRecorder(IMethod method) {
		this();
		String classname = ((IType) method.getParent()).getFullyQualifiedName();
		name = method.getElementName();
		uniquename = classname + '.' + name;
		
		IJavaElement parentJavaElement = method.getParent();
		if (parentJavaElement != null && parentJavaElement instanceof IType) {
			parent = manager.getFamixClass(((IType) parentJavaElement).getFullyQualifiedName());
		}
		
		try {
			flags = method.getFlags();
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		
		//PROBLEM: annotations can be added after the method was created
		IAnnotation annotation = method.getAnnotation("Test");
		if(annotation.exists()){
			isTest = true;
		}
		
	}
	
	public MethodRecorder(MethodDeclaration method) {
		this();
		
		
		
		parent = findParentFamixEntity(method);
		name = method.getName().getIdentifier();
		if(parent != null){
			uniquename = parent.getUniqueName() + "."  + method.getName().getFullyQualifiedName();
		}else{
			uniquename = method.getName().getFullyQualifiedName();
		}
		
		flags = method.getFlags();
	}
	
	private FamixClass findParentFamixEntity(MethodDeclaration method) {
		//find parent famix entity
		String parentName = findParentName(method);
	
		return manager.getFamixClass(parentName);	
	}
	
	private String findParentName(ASTNode node){
		ASTNode parent = node.getParent();
		if(parent instanceof TypeDeclaration){
			return findParentName(parent) + "." + ((TypeDeclaration) parent).getName().getIdentifier();
		}
		if(parent instanceof CompilationUnit){
			
			if(((CompilationUnit) parent).getPackage() != null)
				return ((CompilationUnit) parent).getPackage().getName().getFullyQualifiedName();
			else //default package
				return "";
		}
		
		return "";
	}
	
	public MethodRecorder(SourceCodeEntity entity, SourceCodeEntity parentEntity){
		this();
		if(entity.getType().isMethod()){
			uniquename = entity.getUniqueName();
			int i = uniquename.indexOf('(');
			uniquename = uniquename.substring(0, i);
			
			int j = uniquename.lastIndexOf('.');
			name = uniquename.substring(j+1,i);
			
			if(parentEntity.getType().isClass()){
				String parentUniqueName = parentEntity.getUniqueName();
				parent = manager.getFamixClass(parentUniqueName);
			}
		}
		flags = entity.getModifiers(); //TOFIX
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.ac.ua.cheopsj.Controller.changeRecorders.AbstractEntityRecorder#
	 * createAndLinkFamixElement()
	 */
	@Override
	protected void createAndLinkFamixElement() {
		// TODO use signature in unique name (includes parameter types)

		if (!manager.famixMethodExists(uniquename)) {
			famixMethod = new FamixMethod();
			famixMethod.setUniqueName(uniquename);
			famixMethod.setName(name);
			setMethodFlagsAndParent();
			manager.addFamixElement(famixMethod);
		} else {
			famixMethod = manager.getFamixMethod(uniquename);
			if (famixMethod.isDummy()) {
				setMethodFlagsAndParent();
				
				famixMethod.setDummy(false);
			} else {
				parent = famixMethod.getBelongsToClass();
			}
		}
	}

	private void setMethodFlagsAndParent() {
		if (uniquename.contains("test")) {
			famixMethod.setTest(true);
		}
		
		famixMethod.setFlags(flags);
		
		if (parent != null) {
			famixMethod.setBelongsToClass(parent);
			parent.addMethod(famixMethod);
		}

		/*XXX Fix this too!
		 * try {
		 
			FamixClass returnType = manager.getFamixClass(element.getReturnType());
			famixMethod.setDeclaredReturnClass(returnType);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}*/
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.ac.ua.cheopsj.Controller.changeRecorders.AbstractEntityRecorder#
	 * createAndLinkChange()
	 */
	@Override
	protected void createAndLinkChange(AtomicChange change) {
		change.setChangeSubject(famixMethod);
		famixMethod.addChange(change);

		setStructuralDependencies(change, famixMethod, parent);
		managerChange.addChange(change);
	}

}
