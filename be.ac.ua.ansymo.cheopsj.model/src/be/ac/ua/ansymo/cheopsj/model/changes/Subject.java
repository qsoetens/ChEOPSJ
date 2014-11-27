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
package be.ac.ua.ansymo.cheopsj.model.changes;

import hibernate.model.api.IModelEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public abstract class Subject implements IModelEntity, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1779222541504273975L;
	/*
	 * (non-javadoc)
	 */
	private Collection<Change> affectingChanges;
	private static int IDCounter = 0;
	private String uniqueID;

	public void setUniqueID(String uniqueID) {
		this.uniqueID = uniqueID;
	}

	public Subject() {
		
		affectingChanges = new ArrayList<Change>();
		
		uniqueID = "f" + Integer.toString(IDCounter);
		IDCounter++;
	}
	
	@Column(unique=true)
	public String getUniqueID(){
		return uniqueID;
	}

	/**
	 * Getter of the property <tt>affectingChanges</tt>
	 * 
	 * @return Returns the affectingChanges.
	 * 
	 */
	@OneToMany(
			mappedBy="changeSubject", 
			targetEntity=be.ac.ua.ansymo.cheopsj.model.changes.AtomicChange.class)
	public Collection<Change> getAffectingChanges() {
		return affectingChanges;
	}

	/**
	 * Setter of the property <tt>affectingChanges</tt>
	 * 
	 * @param affectingChanges
	 *            The affectingChanges to set.
	 * 
	 */
	public void setAffectingChanges(Collection<Change> affectingChanges) {
		this.affectingChanges = affectingChanges;
	}

	public void addChange(Change change) {
		this.affectingChanges.add(change);
	}

	@Transient
	public abstract String getFamixType();

//	/**
//	 * Finds the latest Addition Change related to this Subject
//	 * 
//	 * @return
//	 */
//	@Transient
//	public Add getLatestAddition() {
//		Add latestAddition = null;
//		for (Change change : affectingChanges) {
//			if (change instanceof Add) {
//				if (latestAddition == null) {
//					latestAddition = (Add) change;
//				} else {
//					if (latestAddition.getTimeStamp().compareTo(change.getTimeStamp()) < 0) {
//						latestAddition = (Add) change;
//					}
//				}
//			}
//		}
//		return latestAddition;
//	}
//
//	/**
//	 * @return
//	 */
//	@Transient
//	public Remove getLatestRemoval() {
//		Remove latestRemoval = null;
//		for (Change change : affectingChanges) {
//			if (change instanceof Remove) {
//				if (latestRemoval == null) {
//					latestRemoval = (Remove) change;
//				} else {
//					if (latestRemoval.getTimeStamp().compareTo(change.getTimeStamp()) < 0) {
//						latestRemoval = (Remove) change;
//					}
//				}
//			}
//		}
//		return latestRemoval;
//	}
//
//	/**
//	 * @return
//	 */
//	@Transient
//	public Change getLatestChange() {
//		Change latestChange = null;
//		for (Change change : affectingChanges) {
//			if (latestChange == null) {
//				latestChange = change;
//			} else {
//				if (latestChange.getTimeStamp().compareTo(change.getTimeStamp()) < 0) {
//					latestChange = change;
//				}
//			}
//		}
//		return latestChange;
//	}

	private Long id;

    @Id @GeneratedValue(strategy = GenerationType.AUTO )
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    @Transient
	@Override
	public String getURI() {
		// TODO Auto-generated method stub
		return null;
	}

    @Transient
	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return null;
	}

}
