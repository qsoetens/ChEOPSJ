package be.ac.ua.ansymo.cheopsj.distiller.changeextractor;

import java.util.List;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import be.ac.ua.ansymo.cheopsj.changerecorders.ClassRecorder;
import be.ac.ua.ansymo.cheopsj.changerecorders.FieldRecorder;
import be.ac.ua.ansymo.cheopsj.changerecorders.MethodInvocationRecorder;
import be.ac.ua.ansymo.cheopsj.changerecorders.MethodRecorder;
import be.ac.ua.ansymo.cheopsj.changerecorders.StatementRecorder;
import be.ac.ua.ansymo.cheopsj.distiller.asts.MIVisitor;

public class ChangeExtractorRemovals {
	
	private ChangeExtractorProduct changeExtractorProduct = null;
	
	public void storeMethodInvocationRemovals(String contents) {
		TypeDeclaration bigType = ChangeExtractor.getDeclaredType(contents);
		if(bigType != null){
			MethodDeclaration[] methods = bigType.getMethods();
			for(MethodDeclaration method : methods){
				MIVisitor visitor = new MIVisitor();
				method.accept(visitor);
				for(MethodInvocation invocation: visitor.getMethodInvocations()){
					StatementRecorder recorder = new MethodInvocationRecorder(invocation);
					recorder.storeChange(changeExtractorProduct.createRemoval());
				}
			}
		}
	}

	public void storeMethodRemoval(String contents) {
		TypeDeclaration bigType = ChangeExtractor.getDeclaredType(contents);
		if(bigType != null){
			MethodDeclaration[] methods = bigType.getMethods();
			for(MethodDeclaration method : methods){
				MethodRecorder recorder = new MethodRecorder(method);
				recorder.storeChange(changeExtractorProduct.createRemoval());
			}	
		}
	}

	public void storeFieldRemovals(String contents) {
		TypeDeclaration bigType = ChangeExtractor.getDeclaredType(contents);
		if(bigType != null){
			FieldDeclaration[] fields = bigType.getFields();
			for(FieldDeclaration field : fields){
				FieldRecorder recorder = new FieldRecorder(field);
				recorder.storeChange(changeExtractorProduct.createRemoval());
			}
		}
	}

	public void storeClassRemoval(String contents) {
		CompilationUnit cu = ChangeExtractor.getASTFromString(contents);
		List<?> types = cu.types();
		TypeDeclaration bigType = (TypeDeclaration) types.get(0);

		//TODO deal with package removal;
		PackageDeclaration pack = cu.getPackage();
		storePackageRemoval(pack);
		
		ClassRecorder recorder = new ClassRecorder(bigType);
		recorder.storeChange(changeExtractorProduct.createRemoval());
	}

	private void storePackageRemoval(PackageDeclaration pack) {
		//Somehow find out how many types are declared in this Package.
	}
	
	public ChangeExtractorProduct getChangeExtractorProduct() {
		return changeExtractorProduct;
	}

	public void setChangeExtractorProduct(
			ChangeExtractorProduct changeExtractorProduct) {
		this.changeExtractorProduct = changeExtractorProduct;
	}
	
}
