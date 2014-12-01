package be.ac.ua.ansymo.cheopsj.distiller.changeextractor;

import java.util.List;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;

import be.ac.ua.ansymo.cheopsj.changerecorders.ClassRecorder;
import be.ac.ua.ansymo.cheopsj.changerecorders.FieldRecorder;
import be.ac.ua.ansymo.cheopsj.changerecorders.LocalVariableRecorder;
import be.ac.ua.ansymo.cheopsj.changerecorders.MethodInvocationRecorder;
import be.ac.ua.ansymo.cheopsj.changerecorders.MethodRecorder;
import be.ac.ua.ansymo.cheopsj.changerecorders.PackageRecorder;
import be.ac.ua.ansymo.cheopsj.changerecorders.StatementRecorder;
import be.ac.ua.ansymo.cheopsj.distiller.asts.LocalVariableFinder;
import be.ac.ua.ansymo.cheopsj.distiller.asts.MethodInvocationVisitor;
import be.ac.ua.ansymo.cheopsj.model.ModelManager;

public class ChangeExtractorAdditions {
	
	private ChangeExtractorProduct changeExtractorProduct = null;
			
	public void storeFieldAdditions(String contents) {
		TypeDeclaration bigType = ChangeExtractor.getDeclaredType(contents);
		if(bigType != null){
			FieldDeclaration[] fields = bigType.getFields();
			for(FieldDeclaration field : fields){
				FieldRecorder recorder = new FieldRecorder(field);
				recorder.storeChange(changeExtractorProduct.createAddition());
			}
			
			TypeDeclaration[] localtypes = bigType.getTypes();
			for(TypeDeclaration type: localtypes){
				FieldDeclaration[] subfields = type.getFields();
				for(FieldDeclaration field : subfields){
					FieldRecorder recorder = new FieldRecorder(field);
					recorder.storeChange(changeExtractorProduct.createAddition());
				}
				
			}
		}
	}

	public void storeMethodAdditions(String contents) {
		TypeDeclaration bigType = ChangeExtractor.getDeclaredType(contents);
		if(bigType != null){
			MethodDeclaration[] methods = bigType.getMethods();
			for(MethodDeclaration method : methods){
				MethodRecorder recorder = new MethodRecorder(method);
				recorder.storeChange(changeExtractorProduct.createAddition());
				//storeLocalVariableAdditions(method);
			}			
			
			TypeDeclaration[] localtypes = bigType.getTypes();
			for(TypeDeclaration type: localtypes){
				MethodDeclaration[] submethods = type.getMethods();
				for(MethodDeclaration method : submethods){
					MethodRecorder recorder = new MethodRecorder(method);
					recorder.storeChange(changeExtractorProduct.createAddition());
					//storeLocalVariableAdditions(method);
				}
				
			}
		}
	}

	public void storeLocalVariableAdditions(MethodDeclaration method){
		//List<SingleVariableDeclaration> params = method.parameters();
		LocalVariableFinder finder = new LocalVariableFinder();
		//method.getBody().accept(finder);
		method.accept(finder);

		List<VariableDeclaration> vars = finder.getFoundVariables();		
		//vars.addAll(params);

		for(VariableDeclaration var: vars){
			LocalVariableRecorder recorder = new LocalVariableRecorder(var);
			recorder.storeChange(changeExtractorProduct.createAddition());
		}
	}

	public void storeMethodInvocationAdditions(String contents) {
		TypeDeclaration bigType = ChangeExtractor.getDeclaredType(contents);
		if(bigType != null){
			MethodDeclaration[] methods = bigType.getMethods();
			for(MethodDeclaration method : methods){
				MethodInvocationVisitor visitor = new MethodInvocationVisitor();
				method.accept(visitor);
				for(MethodInvocation invocation: visitor.getMethodInvocations()){
					StatementRecorder recorder = new MethodInvocationRecorder(invocation);
					recorder.storeChange(changeExtractorProduct.createAddition());
				}
			}
		}
	}

	public void storeClassAddition(String contents) {
		CompilationUnit cu = ChangeExtractor.getASTFromString(contents);
		List<?> types = cu.types();
		if(types.get(0) instanceof TypeDeclaration){
			//XXX are there any more types in this list?
			TypeDeclaration bigType = (TypeDeclaration) types.get(0);

			PackageDeclaration pack = cu.getPackage();
			storePackageAddition(pack);

			ClassRecorder recorder = new ClassRecorder(bigType);
			recorder.storeChange(changeExtractorProduct.createAddition());
			
			TypeDeclaration[] localtypes = bigType.getTypes();
			for(TypeDeclaration type: localtypes){
				ClassRecorder subrec = new ClassRecorder(type);
				subrec.storeChange(changeExtractorProduct.createAddition());
			}
			
		}
	}	

	private void storePackageAddition(PackageDeclaration pack) {
		//only create packageaddition, if this package was not previously created
		if(pack != null){
			String packname = pack.getName().getFullyQualifiedName();
			//only create packageaddition, if this package was not previously created
			if(packname != null && !packname.isEmpty()){
				if(!ModelManager.getInstance().famixPackageExists(packname)){
					PackageRecorder recorder = new PackageRecorder(packname);
					recorder.storeChange(changeExtractorProduct.createAddition());
				}
			}
		}
	}
	
	public ChangeExtractorProduct getChangeExtractorProduct() {
		return changeExtractorProduct;
	}

	public void setChangeExtractorProduct(
			ChangeExtractorProduct changeExtractorProduct) {
		this.changeExtractorProduct = changeExtractorProduct;
	}	
	
}
