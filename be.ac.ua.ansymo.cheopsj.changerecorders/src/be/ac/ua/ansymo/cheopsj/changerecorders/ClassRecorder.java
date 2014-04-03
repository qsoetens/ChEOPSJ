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
import java.util.Collection;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeEntity;
import be.ac.ua.ansymo.cheopsj.model.ModelManager;
import be.ac.ua.ansymo.cheopsj.model.ModelManagerChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Add;
import be.ac.ua.ansymo.cheopsj.model.changes.AtomicChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Change;
import be.ac.ua.ansymo.cheopsj.model.changes.IChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Remove;
import be.ac.ua.ansymo.cheopsj.model.changes.Subject;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixClass;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixEntity;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixPackage;

//TODO need to fix inheritance relationships and links to interfaces
public class ClassRecorder extends AbstractEntityRecorder {
	private FamixClass famixClass;
	private FamixEntity parent;
	private ModelManager manager;
	private ModelManagerChange managerChange;
	private String uniqueName;
	private int flags;
	private String name = "";
	private List<IMethod> methodTransporter = new ArrayList<IMethod>();
	private List<IField> fieldTransporter = new ArrayList<IField>();
	private List<IType> typeTransporter = new ArrayList<IType>();

	
	private ClassRecorder(){
		//get manager instance
		manager = ModelManager.getInstance();
		managerChange = ModelManagerChange.getInstance();
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
		try {
			flags = element.getFlags();
		} catch (JavaModelException e) {
			e.printStackTrace();
		}	
		
		try {
			if (element.hasChildren()) // see if there are any classes out there under this newly created package. this happens in case of a rename.
			{
				IField[] fieldList = element.getFields();
				for (IField f : fieldList) 
					fieldTransporter.add(f);
				
				IMethod[] methodList = element.getMethods();
				for (IMethod m : methodList) 
					methodTransporter.add(m);
				
				
				IType[] typelist = element.getTypes();
				for(IType t: typelist)
					typeTransporter.add(t);
					// there shouldn't be anything else at this level.
				
			}
		}
		catch (JavaModelException e) {
			//if there's an exception there, we're in a remove.
		}
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

		//set the flags
		flags = declaration.getFlags();
		
//		String superclassname = declaration.getSuperclass().getFullyQualifiedName();
//		System.out.println(superclassname);
		
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

	

	@Override
	public void storeChange(IChange change) {
		createAndLinkFamixElement();
		createAndLinkChange((AtomicChange) change);
	
		int fC=fieldTransporter.size();
		int mC=methodTransporter.size();
		int tC=typeTransporter.size();
		
		// if there's something in any transporter, then we must be in a rename
		if (fC + mC + tC > 0)
		{
		   
			int i;
			Add[] renameF,renameM,renameT;
			renameF = new Add[fC];
			renameM = new Add[mC];
			renameT = new Add[tC];
			for (i=0;i<fC;i++)
			{
				renameF[i] = new Add(); //added because of hibernate. most likely not needed anymore.
				new FieldRecorder(fieldTransporter.get(i)).storeChange(renameF[i]);
			}
			
			for (i=0;i<mC;i++)
			{
				renameM[i] = new Add(); //added because of hibernate. most likely not needed anymore.
				new MethodRecorder(methodTransporter.get(i)).storeChange(renameM[i]);
			}
			
			for (i=0;i<tC;i++)
			{
				renameT[i] = new Add(); //added because of hibernate. most likely not needed anymore.
				new ClassRecorder(typeTransporter.get(i)).storeChange(renameT[i]);
			}
			
			
		}
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
			if(!manager.famixClassWithNameExists(name)){
				famixClass = new FamixClass();

				famixClass.setUniqueName(uniqueName);
			//famixClass.setName(declaration.getName().getIdentifier());

				setClassFlagsAndParent(famixClass);
				famixClass.setName(name);
				manager.addFamixElement(famixClass);
			}else{
				famixClass = manager.getFamixClassesWithName(name).get(0);
				famixClass.setUniqueName(uniqueName);
				if(famixClass.isDummy()){
					//If it was a dummy, undummy it!
					setClassFlagsAndParent(famixClass);
					
					famixClass.setIsDummy(false);
				}else{
					parent = famixClass.getBelongsToPackage();
				}
			}
		} else {
			famixClass = manager.getFamixClass(uniqueName);
			if(famixClass.isDummy()){
				//If it was a dummy, undummy it!
				setClassFlagsAndParent(famixClass);
				
				famixClass.setIsDummy(false);
			}else{
				parent = famixClass.getBelongsToPackage();
			}
		}
	}

	private void setClassFlagsAndParent(FamixClass famixClass) {
		famixClass.setFlags(flags);

		if (parent != null && parent instanceof FamixPackage) {
			famixClass.setBelongsToPackage((FamixPackage) parent);
			((FamixPackage) parent).addClass(famixClass);
		} else if (parent != null && parent instanceof FamixClass) {
			famixClass.setBelongsToClass((FamixClass) parent);
			((FamixClass) parent).addNestedClass(famixClass);
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
		Change latestChange = famixClass.getLatestChange();
		//make sure you don't add or remove the same method twice
		if(latestChange != null && !latestChange.isDummy())
			if(change instanceof Remove && latestChange instanceof Remove)
				return;
			if(change instanceof Add && latestChange instanceof Add)
				return;
		
		if(change instanceof Add){
			Add a = famixClass.getLatestAddition();
			if(a != null && a.isDummy()){
				change = a;
				change.setDummy(false);
			}
		}
		
		change.setChangeSubject(famixClass);
		famixClass.addChange(change);

		setStructuralDependencies(change, famixClass, parent, this);
		managerChange.addChange(change);
	}

	protected void removeAllContainedWithin(AtomicChange change, AtomicChange additionChange) {
		Collection<Change> dependees = additionChange.getStructuralDependees();
		for (Change dependee : dependees) {
			if (dependee instanceof Add) {
				Subject changesubject = ((AtomicChange) dependee).getChangeSubject();
				Change latestChange = changesubject.getLatestChange();
				if (latestChange instanceof Add) {
					// only remove if it wasn't removed yet

					Remove removal = new Remove();
					removal.setChangeSubject(changesubject);
					changesubject.addChange(removal);
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
