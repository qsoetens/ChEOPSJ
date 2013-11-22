package be.ac.ua.ansymo.cheopsj.model;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import be.ac.ua.ansymo.cheopsj.model.changes.Add;
import be.ac.ua.ansymo.cheopsj.model.changes.AtomicChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Change;
import be.ac.ua.ansymo.cheopsj.model.changes.IChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Remove;
import be.ac.ua.ansymo.cheopsj.model.changes.Subject;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixAssociation;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixAttribute;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixClass;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixEntity;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixInvocation;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixLocalVariable;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixMethod;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixPackage;

public class TestModel {

	private Change change;
	private ModelManager model;
	private ModelManagerChange modelChange;
	private FamixEntity fe1, fe2, fe3, fe4, fe6;
	private FamixAssociation fe5;
	
	@Before
	public void setUp() throws Exception {
		change = new Change();
		model = ModelManager.getInstance();
		modelChange = ModelManagerChange.getInstance();
		fe1 = new FamixClass();
		fe1.setUniqueName("be.ac.ua.famixclassentity");
		fe2 = new FamixMethod();
		fe2.setUniqueName("be.ac.ua.famixclassentity.someMethod()");
		fe3 = new FamixPackage();
		fe3.setUniqueName("be.ac.ua");
		fe4 = new FamixAttribute();
		fe4.setUniqueName("be.ac.ua.famixclassentity.someField");
		fe5 = new FamixInvocation();
		fe5.setStringRepresentation("be.ac.ua.famixclassentity.boo{foo}");
		fe6 = new FamixLocalVariable();
		fe6.setUniqueName("be.ac.ua.famixclassentity.someMethod.localvar");
	}

	@After
	public void tearDown() throws Exception {
		model.clearModel();
	}

	@Test
	public void testAddChange() {
		int size;
		size = modelChange.getChanges().size();
		
		modelChange.addChange(change);
		
		assertTrue(!modelChange.getChanges().isEmpty());
		assertTrue(modelChange.getChanges().size() == (size + 1));
	}
	
	@Test
	public void testAddFamixElement() {
		//int sizeFamEnt;
		Subject[] fes = {fe1, fe2, fe3, fe4, fe5, fe6};
		String nameFamPack, nameFamClass, nameFamMeth, nameFamAtt, nameFamVar, nameFamInv;
		
		for(Subject fe : fes) {
			//sizeFamEnt = model.getFamixEntities().size();
			model.addFamixElement(fe);
			
			if (fe instanceof FamixPackage) {
				nameFamPack = ((FamixPackage) fe).getUniqueName();
				//model.getFamixPackage(nameFamPack);
				assertTrue(model.famixPackageExists(nameFamPack));
			} 
			else if (fe instanceof FamixClass) {
				nameFamClass = ((FamixClass) fe).getUniqueName();
				//model.getFamixClass(nameFamClass);
				assertTrue(model.famixClassExists(nameFamClass));
			} 
			else if (fe instanceof FamixMethod) {
				nameFamMeth = ((FamixMethod) fe).getUniqueName();
				//model.getFamixMethod(nameFamMeth);
				assertTrue(model.famixMethodExists(nameFamMeth));
			} 
			else if (fe instanceof FamixAttribute) {
				nameFamAtt = ((FamixAttribute) fe).getUniqueName();
				//model.getFamixField(nameFamAtt);
				assertTrue(model.famixFieldExists(nameFamAtt));
			} 
			else if (fe instanceof FamixInvocation) {
				nameFamInv = ((FamixInvocation) fe).getStringRepresentation();
				//model.getFamixInvocation(nameFamInv);
				assertTrue(model.famixInvocationExists(nameFamInv));
			} 
			else if (fe instanceof FamixLocalVariable) {
				nameFamVar = ((FamixLocalVariable) fe).getUniqueName();
				//model.getFamixVariable(nameFamVar);
				assertTrue(model.famixVariableExists(nameFamVar));
			}
			
			/*assertTrue(!model.getFamixEntities().isEmpty());
			assertTrue(model.getFamixEntities().size() == (sizeFamEnt + 1));*/
		}
	}
	
	
	@Test
	public void testGetSummary() {
		modelChange.addChange(new Change());
		modelChange.addChange(new Add());
		modelChange.addChange(new Add());
		modelChange.addChange(new Remove());
		modelChange.addChange(new Remove());
		modelChange.addChange(new Remove());
		modelChange.addChange(new AtomicChange());
		//modelChange.addChange(new CompositeChange());
		//modelChange.addChange(new CompositeChange());
		
		int changeCountLoc = modelChange.getChanges().size();
		String result;
		int changeCount, addCount, removeCount;
		int addCountLoc = 0, removeCountLoc = 0;
		
		assertTrue(changeCountLoc >= 0);
		
		for(IChange change: modelChange.getChanges()){
			if(change instanceof Add){
				addCountLoc++;
			}else if(change instanceof Remove){
				removeCountLoc++;
			}
		}
		
		result = modelChange.getSummary();
		
		String[] res_parts = result.split(" ");
		changeCount = Integer.parseInt(res_parts[0]);
		addCount = Integer.parseInt(res_parts[2]);
		removeCount = Integer.parseInt(res_parts[5]);
		
		assertTrue(changeCountLoc == changeCount);
		assertTrue(addCountLoc == addCount);
		assertTrue(removeCountLoc == removeCount);
		assertTrue(changeCount >= (addCount + removeCount));
	}

	/*
	@SuppressWarnings("rawtypes")
	@Test
	public void testLoadModel() throws NoSuchMethodException, SecurityException, 
	IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		model.loadModel();
		
	}*/
}
