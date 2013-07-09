package be.ac.ua.ansymo.cheopsj.distiller.asts;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class ASTHelper {
	
	private static ASTHelper INSTANCE = new ASTHelper();
	ASTParser parser = null;
	
	public ASTHelper() {
		parser = ASTParser.newParser(AST.JLS3);
	}
	
	public static ASTHelper getInstance(){
		if (INSTANCE == null)
			INSTANCE = new ASTHelper();
		return INSTANCE;
	}
	
	public CompilationUnit getCompilationUnit(String FileContents){
		parser.setSource(FileContents.toString().toCharArray());
		return (CompilationUnit) parser.createAST(null);
	}
	
	public  CompilationUnit getCompilationUnit(ICompilationUnit unit) {
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit);
		parser.setResolveBindings(true);
		return (CompilationUnit) parser.createAST(null); // parse
	}
}
