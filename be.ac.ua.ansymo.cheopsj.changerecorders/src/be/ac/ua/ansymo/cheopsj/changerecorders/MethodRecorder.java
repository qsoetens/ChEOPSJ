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

import java.util.Collection;
import java.util.List;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import be.ac.ua.ansymo.cheopsj.model.ModelManager;
import be.ac.ua.ansymo.cheopsj.model.ModelManagerChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Add;
import be.ac.ua.ansymo.cheopsj.model.changes.AtomicChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Change;
import be.ac.ua.ansymo.cheopsj.model.changes.Remove;
import be.ac.ua.ansymo.cheopsj.model.changes.Subject;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixClass;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixInvocation;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixMethod;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeEntity;



public class MethodRecorder extends AbstractEntityRecorder {
	private FamixMethod famixMethod;
	private FamixClass parent; // TODO is there something like a nested method inside another method?
	private ModelManager manager;
	private ModelManagerChange managerChange;
	private String uniquename = ""; //TODO need to add parameters to unique naming
	//TODO need to link method to return type
	private int flags = 0;
	private String name = "";
	private boolean isTest = false;

	private MethodRecorder(){
		manager = ModelManager.getInstance();
		managerChange = ModelManagerChange.getInstance();
	}

	public MethodRecorder(IMethod method) {
		this();
		String classname = ((IType) method.getParent()).getFullyQualifiedName();
		name = method.getElementName();
		//uniquename = classname + '.' + name;
		uniquename = classname + '.' + toStringName(method);

		//System.out.println(uniquename);

		IJavaElement parentJavaElement = method.getParent();
		if (parentJavaElement != null && parentJavaElement instanceof IType) {
			parent = manager.getFamixClass(((IType) parentJavaElement).getFullyQualifiedName());
		}
		try {
			flags = method.getFlags();
		} catch (JavaModelException e) {
			//When the method is removed, the java model entity for this method no longer exists, so you can not access its flags anymore.
			//e.printStackTrace();
		}
		
		
		IAnnotation annotation = method.getAnnotation("Test");
		if(annotation.exists()){
			//System.out.println("TEST: " + uniquename);
			isTest = true;
		}
		
		
		
		
	}

	private String toStringName(IMethod method) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(method.getElementName());
		buffer.append('(');
		String[] parameters = method.getParameterTypes();
		int length;
		if (parameters != null && (length = parameters.length) > 0) {
			for (int i = 0; i < length; i++) {
				buffer.append(Signature.toString(parameters[i]));
				if (i < length - 1) {
					buffer.append(", "); //$NON-NLS-1$
				}
			}
		}
		buffer.append(')');
		return buffer.toString();
	}

	public MethodRecorder(MethodDeclaration method) {
		this();

		parent = findParentFamixEntity(method);
		name = method.getName().getIdentifier();
		if(parent != null){
			uniquename = parent.getUniqueName() + "."  + toStringName(method);
		}else{
			uniquename = toStringName(method);
		}

		flags = method.getFlags();
		
		//TODO get @Test annotation out of method.MODIFIERS2_PROPERTY;
		//method.MODIFIERS2_PROPERTY.
		
	}

	private String toStringName(MethodDeclaration method){
		StringBuffer buffer = new StringBuffer();
		buffer.append(method.getName().getIdentifier());
		buffer.append('(');
		List<SingleVariableDeclaration> parameters = method.parameters();
		int length;
		if (parameters != null && (length = parameters.size()) > 0) {
			for (int i = 0; i < length; i++) {
				SingleVariableDeclaration param = parameters.get(i);
				buffer.append(param.getType().toString());
				if (i < length - 1) {
					buffer.append(", "); //$NON-NLS-1$
				}
			}
		}
		buffer.append(')');
		return buffer.toString();
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
			//uniquename = uniquename.substring(0, i);

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

				famixMethod.setIsDummy(false);
			} else {
				parent = famixMethod.getBelongsToClass();
			}
		}
	}

	private void setMethodFlagsAndParent() {
		
		/*if (uniquename.contains("test")) {
			famixMethod.setIsTest(true);
		}*/
				
		famixMethod.setIsTest(isTest);

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
		Change latestChange = famixMethod.getLatestChange();

		//make sure you don't add or remove the same method twice
		if(latestChange != null && !latestChange.isDummy())
			if(change instanceof Remove && latestChange instanceof Remove)
				return;
		if(change instanceof Add && latestChange instanceof Add)
			return;

		change.setChangeSubject(famixMethod);
		famixMethod.addChange(change);

		setStructuralDependencies(change, famixMethod, parent, this);
		managerChange.addChange(change);

	}

	protected void removeAllContainedWithin(AtomicChange change, AtomicChange additionChange) {
		Collection<Change> dependees = additionChange.getStructuralDependees();
		for (Change dependee : dependees) {
			if (dependee instanceof Add) {
				Subject changesubject = ((AtomicChange) dependee).getChangeSubject();

				//only remove invocations that are actually inside this method body
				if(changesubject instanceof FamixInvocation &&
						!((FamixInvocation) changesubject).getInvokedBy().equals(famixMethod) )
					continue;

				Change latestChange = changesubject.getLatestChange();
				if (latestChange instanceof Add) {
					// only remove if it wasn't removed yet

					Remove removal = new Remove();
					removal.setChangeSubject(changesubject);
					setStructuralDependencies(removal, removal.getChangeSubject(), parent, this);

					change.addStructuralDependency(removal);

					managerChange.addChange(removal);
				} else if (latestChange instanceof Remove) {
					change.addStructuralDependency(latestChange);
				}
			}
		}
	}
}
