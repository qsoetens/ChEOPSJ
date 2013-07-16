package be.ac.ua.ansymo.cheopsj.distiller.changeextractor;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;
import org.tmatesoft.svn.core.SVNLogEntryPath;

import be.ac.ua.ansymo.cheopsj.changerecorders.MethodInvocationRecorder;
import be.ac.ua.ansymo.cheopsj.distiller.asts.ASTHelper;
import be.ac.ua.ansymo.cheopsj.distiller.cd.ChangeDistillerProxy;
import be.ac.ua.ansymo.cheopsj.distiller.connection.Connector;
import be.ac.ua.ansymo.cheopsj.distiller.connection.LogEntryHandler.Change;
import be.ac.ua.ansymo.cheopsj.logger.astdiffer.ASTComparator;
import be.ac.ua.ansymo.cheopsj.model.changes.Add;
import be.ac.ua.ansymo.cheopsj.model.changes.IChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Remove;

public class ChangeExtractor {

	private ChangeExtractorProduct changeExtractorProduct = new ChangeExtractorProduct();
	private ChangeExtractorAdditions changeExtractorAdditions = new ChangeExtractorAdditions();
	private ChangeExtractorRemovals changeExtractorRemovals = new ChangeExtractorRemovals();

	public ChangeExtractor(String message, Date date, String user) {
		changeExtractorProduct.setChangeIntent(message);
		changeExtractorProduct.setChangeDate(date);
		changeExtractorProduct.setChangeUser(user);
		changeExtractorAdditions.setChangeExtractorProduct(changeExtractorProduct);
		changeExtractorRemovals.setChangeExtractorProduct(changeExtractorProduct);
	}

	// #############################
	// ###### CHANGEDISTILLER ######
	// #############################

	public void convertChanges(List<SourceCodeChange> sourceCodeChanges) {
		changeExtractorProduct.convertChanges(sourceCodeChanges);
	}

	// #############################
	// ######## REMOVALS ###########
	// #############################

	// you can either use separately or together to store all removals
	// components
	private void storeAllRemovals(String contents) {
		changeExtractorRemovals.storeMethodInvocationRemovals(contents);
		changeExtractorRemovals.storeMethodRemoval(contents);
		changeExtractorRemovals.storeFieldRemovals(contents);
		changeExtractorRemovals.storeClassRemoval(contents);
	}

	public void storeMethodInvocationRemovals(String contents) {
		changeExtractorRemovals.storeMethodInvocationRemovals(contents);
	}

	public void storeMethodRemoval(String contents) {
		changeExtractorRemovals.storeMethodRemoval(contents);
	}

	public void storeFieldRemovals(String contents) {
		changeExtractorRemovals.storeFieldRemovals(contents);
	}

	public void storeClassRemoval(String contents) {
		changeExtractorRemovals.storeClassRemoval(contents);
	}

	// #############################
	// ######## ADDITIONS ##########
	// #############################

	// you can either use separately or together to store all components
	private void storeAllAdditions(String contents) {
		changeExtractorAdditions.storeClassAddition(contents);
		changeExtractorAdditions.storeFieldAdditions(contents);
		changeExtractorAdditions.storeMethodAdditions(contents);
		changeExtractorAdditions.storeMethodInvocationAdditions(contents);
	}

	public void storeFieldAdditions(String contents) {
		changeExtractorAdditions.storeFieldAdditions(contents);
	}

	public void storeMethodAdditions(String contents) {
		changeExtractorAdditions.storeMethodAdditions(contents);
	}

	public void storeLocalVariableAdditions(MethodDeclaration method) {
		changeExtractorAdditions.storeLocalVariableAdditions(method);
	}

	public void storeMethodInvocationAdditions(String contents) {
		changeExtractorAdditions.storeMethodInvocationAdditions(contents);
	}

	public void storeClassAddition(String contents) {
		changeExtractorAdditions.storeClassAddition(contents);
	}

	// #############################
	// ########## STATICS ##########
	// #############################

	/**
	 * getASTFromString is a static method
	 */
	public static CompilationUnit getASTFromString(String str) {
		CompilationUnit cu = null;

		ASTParser parser = ASTParser.newParser(AST.JLS3);

		parser.setSource(str.toString().toCharArray());
		cu = (CompilationUnit) parser.createAST(null);

		return cu;
	}

	/**
	 * getDeclaredType is a static method
	 */
	public static TypeDeclaration getDeclaredType(String contents) {
		CompilationUnit cu = ChangeExtractor.getASTFromString(contents);
		List<?> types = cu.types();
		if (types.get(0) instanceof TypeDeclaration) {
			return (TypeDeclaration) types.get(0);
		} else
			return null;
	}

	// #############################
	// ########## MODIFIED #########
	// #############################

	// commit & update
	public void extractChangesFromJavaFiles(long rev, Change changeType, String path, Connector connector) throws Exception {
		//@SuppressWarnings("rawtypes")
		assert (path.endsWith(".java"));

		switch (changeType) {
		case ADDED:
			// System.out.println("ADDED: " + path);
			// This file was added --> create addition changes for
			// everything in this file!
			String addedFileContents = connector.getFileContents(path,rev + 1);
			this.storeAllAdditions(addedFileContents);
			break;
		case DELETED:
			// System.out.println("DELETED: " + path);
			// This file was removed --> create remove changes for
			// everything in this file!
			String removedFileContents = connector.getFileContents(path, rev);
			this.storeAllRemovals(removedFileContents);
			break;
		case MODIFIED:
			// System.out.println("CHANGED: " + path);
			// This file was modified --> run evolizer.ChangeDistiller on
			// old and new files to find out differences.
			String targetFileContents = connector.getFileContents(path, rev + 1);
			String sourceFileContents = connector.getFileContents(path,	rev);

			ChangeDistillerProxy cd = new ChangeDistillerProxy(sourceFileContents, targetFileContents);
			cd.performDistilling();
			// Convert cd changes into cheops changes
			if (cd.getSourceCodeChanges() != null && !cd.getSourceCodeChanges().isEmpty())
				this.convertChanges(cd.getSourceCodeChanges());

			CompilationUnit oldAST = ASTHelper.getInstance().getCompilationUnit(sourceFileContents);
			CompilationUnit newAST = ASTHelper.getInstance().getCompilationUnit(targetFileContents);

			ASTComparator differ = new ASTComparator();
			differ.setSource(oldAST);
			differ.setTarget(newAST);
			differ.diff();

			storeChange(differ.getAddedElements(), new Add());
			storeChange(differ.getRemovedElements(), new Remove());
			break;
		default:
			break;
		}
	}


	private static void storeChange(Collection<ASTNode> collection,
			IChange change) {
		for (ASTNode node : collection) {
			if (node instanceof MethodInvocation) {
				new MethodInvocationRecorder((MethodInvocation) node)
				.storeChange(change);
			} else if (node instanceof VariableDeclaration) {
				// This is to get changes to other local vars.
				// new LocalVariableRecorder((VariableDeclaration)
				// node).storeChange(change);
			}
		}
	}

}
