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

import org.eclipse.jdt.core.IParent;
import org.eclipse.jdt.core.JavaModelException;

import be.ac.ua.ansymo.cheopsj.model.ModelManager;
import be.ac.ua.ansymo.cheopsj.model.ModelManagerChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Add;
import be.ac.ua.ansymo.cheopsj.model.changes.AtomicChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Change;
import be.ac.ua.ansymo.cheopsj.model.changes.IChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Remove;
import be.ac.ua.ansymo.cheopsj.model.changes.Subject;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixEntity;

/**
 * @author quinten
 * 
 */
public abstract class AbstractEntityRecorder {

	protected ModelManager manager;
	protected ModelManagerChange managerChange;
	protected FamixEntity[] OldChildrenEntities; 
	
	public AbstractEntityRecorder(){
		manager = ModelManager.getInstance();
		managerChange = ModelManagerChange.getInstance();
	}
	
	/**
	 * @param change
	 */
	public void storeChange(IChange change) {
		createAndLinkFamixElement();
		createAndLinkChange((AtomicChange) change);
	}

	abstract protected void createAndLinkFamixElement();

	abstract protected void createAndLinkChange(AtomicChange change);
	
	protected void initializeOldChildrenEntities(IParent element)
	{
		try {
			OldChildrenEntities = new FamixEntity[element.getChildren().length];
		} catch (JavaModelException e) {
			OldChildrenEntities = null;
			e.printStackTrace();
		}
		
		
		
	}
	protected void setStructuralDependencies(AtomicChange change, Subject subject, 
			FamixEntity parent) {
		
		if (change instanceof Add) {
			if (parent != null) {
				Change parentChange = managerChange.getLastestAddition(parent); 
				if (parentChange != null) {
					change.addStructuralDependency(parentChange);
				}//The parent of the class, be it a class or a package should already exist.
			}
			Remove removalChange = managerChange.getLatestRemoval(subject); 
			if (removalChange != null) {
				change.addStructuralDependency(removalChange);
			}
		} else if (change instanceof Remove) {
			// set dependency to addition of this entity
			AtomicChange additionChange = managerChange.getLastestAddition(subject);
			if (additionChange != null) {
				change.addStructuralDependency(additionChange);
				
				//Dependencies to removes of child entities:
				removeAllContainedWithin(change, additionChange, parent);
			}
		}
	}
	
	protected void removeAllContainedWithin(AtomicChange change, AtomicChange additionChange, FamixEntity parent) {
		Collection<Change> dependees = additionChange.getStructuralDependees();
		for (Change dependee : dependees) {
			if (dependee instanceof Add) {
				Subject changesubject = ((AtomicChange) dependee).getChangeSubject();
				changesubject = manager.getFamixEntity(changesubject.getId());
				Change latestChange = managerChange.getLatestChange(changesubject);
						
				if (latestChange instanceof Add) {
					// only remove if it wasn't removed yet

					Remove removal = new Remove();
					removal.setChangeSubject(changesubject);
					changesubject.addChange(removal);
					setStructuralDependencies(removal, removal.getChangeSubject(), parent);

					change.addStructuralDependency(removal);

					managerChange.addChange(removal);
				} else if (latestChange instanceof Remove) {
					change.addStructuralDependency(latestChange);
				}
			}
		}
	}
}
