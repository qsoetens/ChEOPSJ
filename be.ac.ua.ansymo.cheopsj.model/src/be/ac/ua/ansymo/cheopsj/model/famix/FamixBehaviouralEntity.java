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
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

@Entity
public abstract class FamixBehaviouralEntity extends FamixEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2135904417291359238L;

//	private FamixClass declaredReturnClass;
//
//	private PrimitiveTypes declaredReturnType;

//	private boolean isPureAccessor;

//	private String signature;

	// private String accessControlQualifier;

	private Map<String, FamixLocalVariable> localVariables = null;

	private Collection<FamixInvocation> invokedBy = null;

	private Collection<FamixInvocation> invocations = null;

//	private Collection<FamixAccess> accesses = null;

	public FamixBehaviouralEntity() {
		localVariables = new HashMap<String,FamixLocalVariable>();
		invocations = new ArrayList<FamixInvocation>();
		invokedBy = new ArrayList<FamixInvocation>();
//		accesses = new ArrayList<FamixAccess>();
	}

//	/**
//	 * Getter of the property <tt>declaredReturnClass</tt>
//	 * 
//	 * @return Returns the declaredReturnClass.
//	 * 
//	 */
//	@Transient
//	public FamixClass getDeclaredReturnClass() {
//		return declaredReturnClass;
//	}
//
//	/**
//	 * Setter of the property <tt>declaredReturnClass</tt>
//	 * 
//	 * @param declaredReturnClass
//	 *            The declaredReturnClass to set.
//	 * 
//	 */
//	public void setDeclaredReturnClass(FamixClass declaredReturnClass) {
//		this.declaredReturnClass = declaredReturnClass;
//	}
//
//	@Transient
//	public PrimitiveTypes getDeclaredReturnType() {
//		return declaredReturnType;
//	}
//
//	public void setDeclaredReturnType(PrimitiveTypes declaredReturnType) {
//		this.declaredReturnType = declaredReturnType;
//	}
//
//	@Transient
//	public boolean isPureAccessor() {
//		return isPureAccessor;
//	}
//
//	public void setPureAccessor(boolean isPureAccessor) {
//		this.isPureAccessor = isPureAccessor;
//	}
//
//	public String getSignature() {
//		return signature;
//	}
//
//	public void setSignature(String signature) {
//		this.signature = signature;
//	}

	/**
	 * Ensures that this collection contains the specified element (optional
	 * operation).
	 * 
	 * @param element
	 *            whose presence in this collection is to be ensured.
	 * @see java.util.Collection#add(Object)
	 * 
	 */
	public void addLocalVariable(FamixLocalVariable localVariable) {
		this.localVariables.put(localVariable.getUniqueName(), localVariable);
	}
	

	
	public boolean containsVariable(String uniquename) {
		return this.localVariables.containsKey(uniquename);
	}

	/**
	 * @param localVarOrField
	 * @return
	 */
	public FamixLocalVariable findLocalVariable(String localVarOrField) {
		return this.localVariables.get(localVarOrField);
	}
	
	@OneToMany(
			mappedBy="belongsToBehaviour",
			targetEntity=be.ac.ua.ansymo.cheopsj.model.famix.FamixLocalVariable.class)
	public Collection<FamixLocalVariable> getLocalVariables(){
		return this.localVariables.values();
	}

	public void setLocalVariables(Collection<FamixLocalVariable> localVariables){
		for(FamixLocalVariable localvar : localVariables){
			this.localVariables.put(localvar.getUniqueName(), localvar);
		}		
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
	public boolean addInvokedBy(FamixInvocation invocation) {
		return this.invokedBy.add(invocation);
	}
	
	@ManyToMany(
			mappedBy="candidates", 
			targetEntity=be.ac.ua.ansymo.cheopsj.model.famix.FamixInvocation.class)
	public Collection<FamixInvocation> getInvokedBy() {
		return invokedBy;
	}

	public void setInvokedBy(Collection<FamixInvocation> invokedBy) {
		this.invokedBy = invokedBy;
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
	public boolean addInvocation(FamixInvocation invocation) {
		return this.invocations.add(invocation);
	}

	@OneToMany(
			mappedBy="invokedBy", 
			targetEntity=be.ac.ua.ansymo.cheopsj.model.famix.FamixInvocation.class)
	public Collection<FamixInvocation> getInvocations() {
		return invocations;
	}

	public void setInvocations(Collection<FamixInvocation> invocations) {
		this.invocations = invocations;
	}

//	/**
//	 * Ensures that this collection contains the specified element (optional
//	 * operation).
//	 * 
//	 * @param element
//	 *            whose presence in this collection is to be ensured.
//	 * @see java.util.Collection#add(Object)
//	 * 
//	 */
//	public boolean addAccess(FamixAccess access) {
//		return this.accesses.add(access);
//	}
}
