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

import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.QualifiedName;

import be.ac.ua.ansymo.cheopsj.model.changes.Add;
import be.ac.ua.ansymo.cheopsj.model.changes.AtomicChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Change;
import be.ac.ua.ansymo.cheopsj.model.changes.Remove;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixInvocation;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixMethod;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeEntity;

/**
 * @author quinten
 * 
 */
public class MethodInvocationRecorder extends StatementRecorder {
	private FamixInvocation famixInvocation;
	private FamixMethod invokedby;
	//private FamixMethod calledmethod;
	private List<FamixMethod> calledMethodCandidates;

	private String calledMethodName;

	private String stringrepresentation;



	private MethodInvocationRecorder(){
		calledMethodCandidates = new ArrayList<FamixMethod>();
	}

	/**
	 * @param node
	 */
	public MethodInvocationRecorder(MethodInvocation node) {
		this();

		containingMethodName = getContainingMethod(node);
		if(manager.famixMethodExists(containingMethodName)){
			invokedby = manager.getFamixMethod(containingMethodName);
		}

		if(invokedby == null)
			return;

		/*//This only works when logging changes!!! 
		IMethodBinding binding = node.resolveMethodBinding();
		if(binding != null){
			calledMethodName = binding.getName();
			if(binding.getDeclaringClass() != null){
				calledMethodName = binding.getDeclaringClass().getName() + '.'+ calledMethodName;
				if(binding.getDeclaringClass().getPackage() != null)
					calledMethodName = binding.getDeclaringClass().getPackage().getName() + '.'+ calledMethodName;
			}
		}else{
			//TODO needs to be fixed
			calledMethodName = node.getName().getFullyQualifiedName();
			if (node.getExpression() != null) {
				String localVarOrField = node.getExpression().toString();

				calledmethod = findMethodInType(localVarOrField, calledMethodName);
				if(calledmethod == null){
					calledmethod = new FamixMethod();
					calledmethod.setUniqueName(calledMethodName);
					calledmethod.setIsDummy(true);
					manager.addFamixElement(calledmethod);
				}
			}else{
				calledMethodName = invokedby.getBelongsToClass().getUniqueName() + '.' + calledMethodName;
				getOrAddCalledMethod();
				
			}

		}*/
		Expression expr = node.getExpression();
		if(expr instanceof QualifiedName){
			calledMethodName = ((QualifiedName)expr).getFullyQualifiedName();
			calledMethodName += "."+node.getName().getIdentifier();
		}else{
			calledMethodName = invokedby.getBelongsToClass().getUniqueName() + '.' + node.getName().getIdentifier();
		}
		
		if(manager.famixMethodExists(calledMethodName)){
			calledMethodCandidates = manager.getFamixMethodsWithName(calledMethodName);
		}else{
			FamixMethod calledmethod = new FamixMethod();
			calledmethod.setUniqueName(calledMethodName);
			calledmethod.setDummy(true);
			manager.addFamixElement(calledmethod);
			calledMethodCandidates.add(calledmethod);
		}
	}
	
	/*public void getOrAddCalledMethod() {
		
		if(manager.famixMethodExists(calledMethodName)){
			calledmethod = manager.getFamixMethod(calledMethodName);
		}else{
			calledmethod = new FamixMethod();
			calledmethod.setUniqueName(calledMethodName);
			calledmethod.setIsDummy(true);
			manager.addFamixElement(calledmethod);
		}
	}*/

	public MethodInvocationRecorder(SourceCodeEntity entity){
		this();
		entity.getUniqueName();
	}

	/**
	 * @param localVarOrField
	 * @return
	 */
	/*private FamixMethod findMethodInType(String localVarOrField, String invokedMethodName) {

		Subject famixVar = null;

		if(invokedby.containsVariable(invokedby.getUniqueName() + '{' + localVarOrField + '}')){
			//is it a variable or parameter?
			localVarOrField = invokedby.getUniqueName() +'{' + localVarOrField + '}';
			famixVar = invokedby.findLocalVariable(localVarOrField);
		}else{
			//is it mayhaps a field?
			localVarOrField = invokedby.getBelongsToClass().getUniqueName() + '.' + localVarOrField;
			famixVar = manager.getFamixField(localVarOrField);
		}

		//TODO expression can also be a type.

		// find out what type it is
		FamixClass type = new FamixClass();
		if (famixVar != null) {
			if (famixVar instanceof FamixLocalVariable) {
				type = ((FamixLocalVariable) famixVar).getDeclaredClass();
			} else if (famixVar instanceof FamixAttribute) {
				type = ((FamixAttribute) famixVar).getDeclaredClass();
			}
			// XXX handle primitive types!!!
		}
		String methodName;
		if(type != null){
			methodName = type.getUniqueName() + '.' + invokedMethodName;
			return type.findMethod(methodName);
		}else{
			methodName = invokedMethodName;
			return manager.getFamixMethod(methodName);
		}


	}*/


	/*
	 * (non-Javadoc)
	 * 
	 * @see be.ac.ua.cheopsj.Controller.changeRecorders.AbstractEntityRecorder#
	 * createAndLinkFamixElement()
	 */
	@Override
	protected void createAndLinkFamixElement() {
		if(invokedby == null)
			return;

		if (manager.famixMethodExists(containingMethodName)) {
			invokedby = manager.getFamixMethod(containingMethodName);
		} //invokedby should exist

		//getOrAddCalledMethod();

		stringrepresentation = invokedby.getUniqueName() + '{' + calledMethodName + '}';

		//TODO make sure a method can be called several times from another method!!!! (use invocation counter?) > don't care for now!
		if (!manager.famixInvocationExists(stringrepresentation)) {
			famixInvocation = new FamixInvocation();
			famixInvocation.setStringRepresentation(stringrepresentation);
			
			for(FamixMethod calledmethod: calledMethodCandidates){
				famixInvocation.addCandidate(calledmethod);
				calledmethod.addInvokedBy(famixInvocation);
			}
			//famixInvocation.setCandidate(calledmethod);
			//calledmethod.addInvokedBy(famixInvocation);

			famixInvocation.setInvokedBy(invokedby);
			invokedby.addInvocation(famixInvocation);

			manager.addFamixElement(famixInvocation);
		} else {
			famixInvocation = manager.getFamixInvocation(stringrepresentation);
			invokedby = (FamixMethod) famixInvocation.getInvokedBy();

			//calledMethodCandidates = convert(famixInvocation.getCandidates());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.ac.ua.cheopsj.Controller.changeRecorders.AbstractEntityRecorder#
	 * createAndLinkChange(be.ac.ua.cheopsj.Model.Changes.AtomicChange)
	 */
	@Override
	protected void createAndLinkChange(AtomicChange change) {
		if(invokedby == null)
			return;

		change.setChangeSubject(famixInvocation);
		famixInvocation.addChange(change);

		setStructuralDependencies(change, famixInvocation);
		managerChange.addChange(change);
	}

	/**
	 * @param change
	 * @param famixInvocation2
	 */
	private void setStructuralDependencies(AtomicChange change, FamixInvocation subject) {
		if (change instanceof Add) {
			
			if(!calledMethodCandidates.isEmpty()){
				for(FamixMethod candidate : calledMethodCandidates){
					Change calledMethodChange = managerChange.getLastestAddition(candidate); 
					if (calledMethodChange != null) {
						change.addStructuralDependency(calledMethodChange);
					}
				}
			}
			/*if(calledmethod != null){
				Change calledMethodChange = calledmethod.getLatestAddition();
				if (calledMethodChange != null) {
					change.addStructuralDependency(calledMethodChange);
				}
			}*/
			if (invokedby != null) {
				Change invokedByChange = managerChange.getLastestAddition(invokedby); 
				if (invokedByChange != null) {
					change.addStructuralDependency(invokedByChange);
				}
			}
			Remove removalChange = managerChange.getLatestRemoval(subject);
			if (removalChange != null) {
				change.addStructuralDependency(removalChange);
			}
		} else if (change instanceof Remove) {
			// set dependency to addition of this entity
			// Subject removedSubject = change.getChangeSubject();
			AtomicChange additionChange = managerChange.getLastestAddition(subject); 
			if (additionChange != null) {
				change.addStructuralDependency(additionChange);
			}
		}

	}
}
