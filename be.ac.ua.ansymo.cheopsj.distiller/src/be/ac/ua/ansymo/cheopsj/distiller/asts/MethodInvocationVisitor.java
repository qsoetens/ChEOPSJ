package be.ac.ua.ansymo.cheopsj.distiller.asts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;

public class MethodInvocationVisitor extends ASTVisitor {

	private List<MethodInvocation> invocations = new ArrayList<MethodInvocation>();

	@Override
	public boolean visit(EnumDeclaration node){
		//Ignore Enum's
		return false;
	}
	
	@Override
	public boolean visit(MethodInvocation node) {
		invocations.add(node);
		return true;
	}

	public List<MethodInvocation> getMethodInvocations() {
		return invocations;			
	}
}
