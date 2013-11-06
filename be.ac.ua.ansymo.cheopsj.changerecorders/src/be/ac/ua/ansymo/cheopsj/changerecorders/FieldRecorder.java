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
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeEntity;

import be.ac.ua.ansymo.cheopsj.model.ModelManager;
import be.ac.ua.ansymo.cheopsj.model.ModelManagerChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Add;
import be.ac.ua.ansymo.cheopsj.model.changes.AtomicChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Change;
import be.ac.ua.ansymo.cheopsj.model.changes.Remove;
import be.ac.ua.ansymo.cheopsj.model.changes.Subject;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixAttribute;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixClass;

public class FieldRecorder extends AbstractEntityRecorder {
	private FamixAttribute famixField;
	//private FamixClass ContainingClass;
	private FamixClass declaredClass;
	
	
	private String parentUniqueName = "";
	private String uniquename = "";
	private int flags;
	private String name = "";

	private FieldRecorder(){
	}
	
	public FieldRecorder(IField field) {
		this();
		
		declaredClass = findDeclaredClass(field);
		
		parentUniqueName = ((IType) field.getParent()).getFullyQualifiedName();
		//ContainingClass = manager.getFamixClass(classname);
		
			
		name = field.getElementName();
		uniquename = parentUniqueName + '.' + name;
						
		try {
			flags = field.getFlags();
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		
	}

	public FieldRecorder(FieldDeclaration field) {
		this();

		parentUniqueName = findParentName(field);
		
		List<?> fragments = field.fragments();
		VariableDeclarationFragment var = (VariableDeclarationFragment)fragments.get(0);
		//TODO what if more than one field is declared!
		uniquename = parentUniqueName + "." + var.getName().getIdentifier();
		name = var.getName().getIdentifier();
		
		declaredClass = findDeclaredClass(field);
		
		flags = field.getFlags();		
	}

	public FieldRecorder(SourceCodeEntity entity, SourceCodeEntity parentEntity) {
		//TOFIX 
		this();
		
		if(entity.getType().isField()){
			uniquename = entity.getUniqueName();
			
			int i = uniquename.indexOf(' ');
			uniquename = uniquename.substring(0,i);
			
			int j = uniquename.lastIndexOf('.');
			name = uniquename.substring(j+1,uniquename.length());
			
			if(parentEntity.getType().isClass()){
				parentUniqueName = parentEntity.getUniqueName();
			}
		}
		flags = entity.getModifiers(); //FIXME fix this, this is not right		
		
		String declaredClassName = "";
		//TODO what's the declared class name?
		declaredClass = manager.getFamixClass(declaredClassName);
	}

	private FamixClass findDeclaredClass(IField field) {
		String declaredClassName = "";
		try {
			String typesignature = field.getTypeSignature();
			declaredClassName = Signature.getSignatureSimpleName(typesignature);
			// XXX find out if nested class? and deal with it!
			// XXX deal with primitive types

			// type package to be found in import statmenets OR in the same
			// package!!!
			boolean found = false;
			ICompilationUnit cu = field.getCompilationUnit();
			IImportDeclaration[] imports = cu.getImports();
			for (IImportDeclaration imp : imports) {
				if (imp.isOnDemand()) {
					// FIXME CRAP FUCKING WILDCARDS IN IMPORT DECLARATIONS!!!!
					// need to deal with this
				} else {
					if (imp.getElementName().endsWith(declaredClassName)) {
						// YES WE FOUNDS IT
						declaredClassName = imp.getElementName();
						found = true;
						// leave for loop
						continue;
					}
				}
			}
			if (!found) {
				IPackageDeclaration pack = cu.getPackageDeclarations()[0];
				declaredClassName = pack.getElementName() + '.' + declaredClassName;
			}

		} catch (Exception e) {
			// TODO what to do with this exception?
		}
		
		return manager.getFamixClass(declaredClassName);
	}
	
	private FamixClass findDeclaredClass(FieldDeclaration field) {
		Type type = field.getType();
		String declaredClassName = "";
		if (type.isSimpleType()) {
			declaredClassName = ((SimpleType) type).getName().getFullyQualifiedName();
			
			//TODO first need to go up all classlevels to see if there is a typemember in the class with this name
			
			//THEN Search for fully qualified classname in import statments
			CompilationUnit cu = (CompilationUnit)field.getRoot();
			
			List<?> temp = cu.imports();
			List<ImportDeclaration> imports = new ArrayList<ImportDeclaration>();
			for(Object t : temp){
				imports.add((ImportDeclaration)t);
			}
			
			for(ImportDeclaration imp : imports){
				Name impname = imp.getName();
				if(impname.getFullyQualifiedName().endsWith(declaredClassName)){
					declaredClassName = impname.getFullyQualifiedName();
					break;
				}
			}
			
			//if still not found there, need to assume that the class is in same package as this class.
			if(!declaredClassName.contains(".")){
				declaredClassName = cu.getPackage().getName().getFullyQualifiedName() + "." + declaredClassName;
			}
			
		}
		return manager.getFamixClass(declaredClassName);
	}

	/*private FamixClass findParentFamixEntity(FieldDeclaration field) {
		//find parent famix entity
		String parentName = findParentName(field);
	
		return manager.getFamixClass(parentName);	
	}*/
	
	private String findParentName(ASTNode node){
		ASTNode parent = node.getParent();
		if(parent instanceof TypeDeclaration){
			return findParentName(parent) + "." + ((TypeDeclaration) parent).getName().getIdentifier();
		}
		if(parent instanceof CompilationUnit){
			return ((CompilationUnit) parent).getPackage().getName().getFullyQualifiedName();
		}
		
		return "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.ac.ua.cheopsj.Controller.changeRecorders.AbstractEntityRecorder#
	 * createAndLinkFamixElement()
	 */
	@Override
	protected void createAndLinkFamixElement() {
		if (!manager.famixFieldExists(uniquename)) {
			famixField = new FamixAttribute();

			famixField.setUniqueName(uniquename);
			
			famixField.setFlags(flags);
			
			FamixClass ContainingClass = manager.getFamixClass(parentUniqueName);

			if (ContainingClass != null) {
				famixField.setBelongsToClass(ContainingClass);
				ContainingClass.addAttribute(famixField);
			}

			if (declaredClass != null) {
				famixField.setDeclaredClass(declaredClass);
			}

			famixField.setName(name);
			
			manager.addFamixElement(famixField);
		} else {
			famixField = manager.getFamixField(uniquename);
			declaredClass = famixField.getDeclaredClass();
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
		change.setChangeSubject(famixField);
		famixField.addChange(change);

		setStructuralDependencies(change, famixField);
		managerChange.addChange(change);
	}

	protected void setStructuralDependencies(AtomicChange change, Subject subject) {
		if (change instanceof Add) {
			setStructDepAdd(change, subject);
		}
		else if (change instanceof Remove) {
			// set dependency to addition of this entity
			// Subject removedSubject = change.getChangeSubject();
			AtomicChange additionChange = managerChange.getLastestAddition(subject); 
			if (additionChange != null) {
				change.addStructuralDependency(additionChange);
			}
		}
	}
	
	private void setStructDepAdd(AtomicChange change, Subject subject) {
		FamixClass ContainingClass = manager.getFamixClass(parentUniqueName);
		if (ContainingClass != null) {
			Change parentChange = managerChange.getLastestAddition(ContainingClass);
			if (parentChange != null) {
				change.addStructuralDependency(parentChange);
			}
		}
		Remove removalChange = managerChange.getLatestRemoval(subject);
		if (removalChange != null) {
			change.addStructuralDependency(removalChange);
		}
		if (declaredClass != null) {
			Change declChange = managerChange.getLastestAddition(declaredClass); 
			if (declChange != null) {
				change.addStructuralDependency(declChange);
			}
		}
	}
}