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

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.ui.ISharedImages;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.swt.graphics.Image;

@Entity
public class FamixMethod extends FamixBehaviouralEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -904732987714442286L;

	/*
	 * (non-javadoc)
	 */
	private FamixClass belongsToClass = null;

	private boolean hasClassScope;
	private boolean isAbstract;
	private boolean isConstructor;

	
	
	/**
	 * Getter of the property <tt>belongsToClass</tt>
	 * 
	 * @return Returns the belongsToClass.
	 * 
	 */
	@ManyToOne(
			targetEntity=be.ac.ua.ansymo.cheopsj.model.famix.FamixClass.class)
	public FamixClass getBelongsToClass() {
		return belongsToClass;
	}

	/**
	 * Setter of the property <tt>belongsToClass</tt>
	 * 
	 * @param belongsToClass
	 *            The belongsToClass to set.
	 * 
	 */
	public void setBelongsToClass(FamixClass belongsToClass) {
		this.belongsToClass = belongsToClass;
	}

	@Transient
	public boolean isHasClassScope() {
		return hasClassScope;
	}

	public void setHasClassScope(boolean hasClassScope) {
		this.hasClassScope = hasClassScope;
	}

	@Transient
	public boolean isAbstract() {
		return isAbstract;
	}

	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}

	@Transient
	public boolean isConstructor() {
		return isConstructor;
	}

	public void setConstructor(boolean isConstructor) {
		this.isConstructor = isConstructor;
	}

	@Transient
	@Override
	public String getFamixType() {
		return "Method";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.ac.ua.cheopsj.Model.Famix.FamixEntity#getIcon()
	 */
	@Transient
	@Override
	public Image getIcon() {
		Image icon = null;
		if (Flags.isPublic(this.getFlags())) {
			icon = JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_PUBLIC);
		} else if (Flags.isPrivate(getFlags())) {
			icon = JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_PRIVATE);
		} else if (Flags.isProtected(this.getFlags())) {
			icon = JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_PROTECTED);
		} else if (Flags.isPackageDefault(this.getFlags())) {
			icon = JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_DEFAULT);
		}

		// XXX decorate icon with Abstract, static final, .... decorators
		return icon;
	}

	private boolean isTest = false;

	public void setTest(boolean b) {
		this.isTest = b;
	}

	public boolean isTest() {
		return this.isTest;
	}
	
}
