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

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import be.ac.ua.ansymo.cheopsj.model.famix.FamixAssociation;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixEntity;

@Entity
public class AtomicChange extends Change {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1879288093953723902L;
	/*
	 * (non-javadoc)
	 */
	private Subject changeSubject;

	public void undo() {
	}

	public void apply() {
	}

	/**
	 * Getter of the property <tt>changeSubject</tt>
	 * 
	 * @return Returns the changeSubject.
	 */
	@ManyToOne(targetEntity=be.ac.ua.ansymo.cheopsj.model.changes.Subject.class)
	public Subject getChangeSubject() {
		return changeSubject;
	}

	/**
	 * Setter of the property <tt>changeSubject</tt>
	 * 
	 * @param changeSubject
	 *            The changeSubject to set.
	 */
	public void setChangeSubject(Subject changeSubject) {
		this.changeSubject = changeSubject;
	}

	@Transient
	@Override
	public String getFamixType() {
		if (changeSubject != null)
			return changeSubject.getFamixType();
		else
			return "";
	}

	@Transient
	@Override
	public String getName() {
		if (changeSubject != null && changeSubject instanceof FamixEntity)
			return ((FamixEntity) changeSubject).getUniqueName();
		else if (changeSubject instanceof FamixAssociation)
			return ((FamixAssociation) changeSubject).getStringRepresentation();
		else
			return "";
	}	
}