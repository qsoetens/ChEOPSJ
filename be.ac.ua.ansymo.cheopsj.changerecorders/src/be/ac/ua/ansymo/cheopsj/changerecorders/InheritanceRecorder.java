package be.ac.ua.ansymo.cheopsj.changerecorders;

import java.util.ArrayList;
import java.util.Collection;

import javax.jws.WebParam.Mode;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import be.ac.ua.ansymo.cheopsj.model.ModelManager;
import be.ac.ua.ansymo.cheopsj.model.ModelManagerChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Add;
import be.ac.ua.ansymo.cheopsj.model.changes.AtomicChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Change;
import be.ac.ua.ansymo.cheopsj.model.changes.IChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Remove;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixClass;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixInheritanceDefinition;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixMethod;

public class InheritanceRecorder{

	//Need to see if superclass changed , ---> remove previous superclass inheritance relationship + add new superlass inheritance relationship
	//Compare old interfaces to new interfaces.
	String typename = "";
	String[] interfaces;
	String supertypename = "";
	FamixClass type;
	
	private ModelManager manager;
	private ModelManagerChange managerChange;
	
	private InheritanceRecorder(){
		manager = ModelManager.getInstance();
		managerChange = ModelManagerChange.getInstance();
	}
	
	public InheritanceRecorder(IType element){
		this();
		typename = element.getFullyQualifiedName();
		typename = typename.replace('$', '.');
		
		try {
			supertypename = ((IType)element).getSuperclassName();
			interfaces = ((IType)element).getSuperInterfaceNames();
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void storeChanges() {
		//first see, what has changed!
		if(manager.famixClassExists(typename)){
			type = manager.getFamixClass(typename);
			//Collection<FamixInheritanceDefinition> oldSuperClasses = type.getSuperClasses();
			
			Collection<String> oldSuperClasses = type.getSuperClassNames();
			Collection<String> newSuperClasses = new ArrayList<String>();
			newSuperClasses.add(supertypename);
			CollectionUtils.addAll(newSuperClasses, interfaces);
			
			//Compare oldSuperClasses with newSuperClasses to see which was added and which was removed.
			Collection<String> addedSuperClasses = ListUtils.removeAll(
						newSuperClasses, CollectionUtils.intersection(oldSuperClasses, newSuperClasses));
			Collection<String> removedSuperClasses = ListUtils.removeAll(
					oldSuperClasses, CollectionUtils.intersection(oldSuperClasses, newSuperClasses));
			
			
			//create Add for all in addedSuperClasses
			for(String added: addedSuperClasses){
				this.storeChange(new Add(), added);
			}
			
			//create Remove for all in removedSuperClasses
			for(String removed: removedSuperClasses){
				this.storeChange(new Remove(), removed);
			}
			
			
			
		}
	}

	private void storeChange(IChange change, String added) {
		if(added != null){
			FamixInheritanceDefinition inh = createAndLinkFamixElement(added);
			createAndLinkChange((AtomicChange) change, inh);
		}
	}

	private void createAndLinkChange(AtomicChange change,
			FamixInheritanceDefinition inh) {

		Change latestChange = inh.getLatestChange();
		
		//make sure you don't add or remove the same method twice
		if(latestChange != null && !latestChange.isDummy())
			if(change instanceof Remove && latestChange instanceof Remove)
				return;
			if(change instanceof Add && latestChange instanceof Add)
				return;
		
		change.setChangeSubject(inh);
		inh.addChange(change);

		setStructuralDependencies(change, inh);
		managerChange.addChange(change);
		
	}

	private void setStructuralDependencies(AtomicChange change,
			FamixInheritanceDefinition subject) {
		if(change instanceof Add){
			//set dependency to addition of superclass
			FamixClass superClass = subject.getSuperClass();
			Add superClassAddition = superClass.getLatestAddition();
			if(superClassAddition != null){
				change.addStructuralDependency(superClassAddition);
			}
			//set dependency to addition of subclass
			FamixClass subClass = subject.getSubClass();
			Add subClassAddition = subClass.getLatestAddition();
			if(subClassAddition != null){
				change.addStructuralDependency(subClassAddition);
			}
		} else if (change instanceof Remove){
			// set dependency to addition of this entity
			AtomicChange additionChange = subject.getLatestAddition();
			if(additionChange != null){
				change.addStructuralDependency(additionChange);
			}
		}
		
		
		
		
//		if (change instanceof Add) {
//			
//			if(!calledMethodCandidates.isEmpty()){
//				for(FamixMethod candidate : calledMethodCandidates){
//					Change calledMethodChange = candidate.getLatestAddition();
//					if (calledMethodChange != null) {
//						change.addStructuralDependency(calledMethodChange);
//					}
//				}
//			}
//			/*if(calledmethod != null){
//				Change calledMethodChange = calledmethod.getLatestAddition();
//				if (calledMethodChange != null) {
//					change.addStructuralDependency(calledMethodChange);
//				}
//			}*/
//			if (invokedby != null) {
//				Change invokedByChange = invokedby.getLatestAddition();
//				if (invokedByChange != null) {
//					change.addStructuralDependency(invokedByChange);
//				}
//			}
//			Remove removalChange = subject.getLatestRemoval();
//			if (removalChange != null) {
//				change.addStructuralDependency(removalChange);
//			}
//		} else if (change instanceof Remove) {
//			// set dependency to addition of this entity
//			// Subject removedSubject = change.getChangeSubject();
//			AtomicChange additionChange = subject.getLatestAddition();
//			if (additionChange != null) {
//				change.addStructuralDependency(additionChange);
//			}
//		}
		
	}

	private FamixInheritanceDefinition createAndLinkFamixElement(String superClassName) {
		FamixInheritanceDefinition inh = new FamixInheritanceDefinition();
		FamixClass superClass = null;
		//FamixClass superClass = manager.getFamixClass(superClassName);
		if(manager.famixClassWithNameExists(superClassName))
			superClass = manager.getFamixClassesWithName(superClassName).get(0);
		if(superClass == null){
			//creating a dummy superclass node! This should be undummied when the Class itself is added.
			superClass = new FamixClass();
			superClass.setIsDummy(true);
			superClass.setName(superClassName);
			superClass.setUniqueName(superClassName);
			
			manager.addFamixElement(superClass);
		}
		inh.setSuperClass(superClass);
		inh.setSubClass(type);
		inh.setStringRepresentation(type.getName()+"->"+superClassName);
		
		manager.addFamixElement(inh);
		
		return inh;
	}

}
