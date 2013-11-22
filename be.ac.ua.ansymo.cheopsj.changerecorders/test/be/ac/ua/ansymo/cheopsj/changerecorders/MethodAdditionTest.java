package be.ac.ua.ansymo.cheopsj.changerecorders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import be.ac.ua.ansymo.cheopsj.model.ModelManager;
import be.ac.ua.ansymo.cheopsj.model.changes.Add;
import be.ac.ua.ansymo.cheopsj.model.changes.AtomicChange;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixEntity;

public class MethodAdditionTest {

	private MethodRecorder recorder1;
	private ModelManager manager;

	private String packname = "be.ac.ua.test.pack";
	private String classname = "Boo";
	private String declaredTypeName = "be.ac.ua.test.otherpack.Foo";

	private String methodname = "method";
	private AtomicChange classadd;
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp(){
		manager = ModelManager.getInstance();
		manager.clearModel();

		AST ast = AST.newAST(AST.JLS3);
		CompilationUnit cu = ast.newCompilationUnit();
		PackageDeclaration pack = ast.newPackageDeclaration();
		pack.setName(ast.newName(packname));
		cu.setPackage(pack);
		TypeDeclaration type = ast.newTypeDeclaration();
		type.setName(ast.newSimpleName(classname));

		cu.types().add(type);

		PackageRecorder prec = new PackageRecorder(pack);
		prec.storeChange(new Add());//store the package addition
		ClassRecorder crec = new ClassRecorder(type);
		classadd = new Add();
		crec.storeChange(classadd);

		//Class and package created and changes logged, now create the Field.

		MethodDeclaration method = ast.newMethodDeclaration();
		method.setName(ast.newSimpleName(methodname));
		type.bodyDeclarations().add(method);

		//created mock compilationunit containing package and class

		recorder1 = new MethodRecorder(method);
		
	}


	@After
	public void tearDown(){
		
	}

	@Test
	public void test0(){
		try {
			Field uniqueNameField = MethodRecorder.class.getDeclaredField("uniquename");
			uniqueNameField.setAccessible(true);
			String fieldValue = (String) uniqueNameField.get(recorder1);
			assertEquals(packname+"."+classname+"."+methodname, fieldValue);

			Field parentField = MethodRecorder.class.getDeclaredField("parent");
			parentField.setAccessible(true);
			FamixEntity fieldValue2 = (FamixEntity)parentField.get(recorder1);
			assertEquals(packname+"."+classname,fieldValue2.getUniqueName());

		} catch (Exception e) {
			fail();
		}
	}


}
