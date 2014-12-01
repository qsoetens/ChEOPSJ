package be.ac.ua.ansymo.cheopsj.model;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import be.ac.ua.ansymo.cheopsj.model.changes.Change;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixAttribute;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixClass;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixInvocation;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixLocalVariable;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixMethod;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixPackage;

public class TestClassList {
	private ModelManager model;
	
	
	
	@Before
	public void setUp() throws Exception {
		model = ModelManager.getInstance();
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testClassWithNameExists() {
		FamixClass c = new FamixClass();
		c.setName("ClassName");
		c.setUniqueName("package.ClassName");
		
		model.addFamixElement(c);
		
		assertTrue(model.famixClassWithNameExists("ClassName"));
		assertFalse(model.famixClassWithNameExists("Name"));
	}

}
