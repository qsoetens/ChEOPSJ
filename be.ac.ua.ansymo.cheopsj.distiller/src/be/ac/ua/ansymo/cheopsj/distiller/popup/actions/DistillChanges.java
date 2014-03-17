package be.ac.ua.ansymo.cheopsj.distiller.popup.actions;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.IProgressMonitor;

import be.ac.ua.ansymo.cheopsj.distiller.changeextractor.ChangeExtractor;
import be.ac.ua.ansymo.cheopsj.distiller.connection.Connector;
import be.ac.ua.ansymo.cheopsj.distiller.connection.ConnectorFactory;
import be.ac.ua.ansymo.cheopsj.distiller.connection.LogEntryHandler;
import be.ac.ua.ansymo.cheopsj.distiller.connection.LogEntryHandler.Change;


public class DistillChanges extends AbstractDistiller{

	private Connector connector;
	
	void doYourThing(IProgressMonitor monitor){
		try {			
			//TODO record additions for initial project!
			File projectFile = selectedProject.getLocation().toFile();
			connector = ConnectorFactory.createConnector("","",projectFile);
			if (connector == null) {
				return;
			}
			
			long rev = 0;
			//Get current revision number.
			long targetRev = connector.getCurrentRevision(projectFile);
			int diff = (int) (targetRev - rev); //nr of revisions that will be processed
			
			if (monitor != null) {
				monitor.beginTask("Extracting changes", diff);
			}
			
			while (rev < targetRev) {
				try{
					//update to revision rev
					//svnConnector.updateToRevision(file, rev, new SubProgressMonitor(monitor, 1));
					//updateOneRev(file, rev, new SubProgressMonitor(monitor, 1));//update working copy of svn rep one revision
					//refresh project!
					//selectedProject.refreshLocal(IProject.DEPTH_INFINITE, new SubProgressMonitor(monitor, 1));
					
					if (monitor != null) {
						double percent = ((double)rev/targetRev)*100;
						monitor.subTask("from revision: " + rev + "/" + targetRev + " (" +(int)percent+ "%)");
					}
					LogEntryHandler entryHandler = connector.getLogEntryHandler();
					connector.getCommitMessage(projectFile, rev + 1, entryHandler); //Lookahead at changes in next revision!

					Iterator<Entry<String, Change>> it = entryHandler.getChangedPaths().entrySet().iterator();
					//iterate changed files
					while (it.hasNext()) {
						Map.Entry <String, Change> entry = it.next();
						// Check whether or not the file is a java file.
						if (!entry.getKey().endsWith(".java")) {
							continue;
						}
							
						try{
							//extractChangesFromJavaFiles(rev, , entry.getKey(), entry.getValue());
										
							ChangeExtractor extractor = entryHandler.getExtractor();
							extractor.extractChangesFromJavaFiles(rev, entry.getValue(), entry.getKey(), connector);
						}catch(Exception e){
							//ignore and try next file!!!
						}
					}
					
					if (monitor != null) {
						monitor.worked(1);
					}
					
					rev++;
				} catch(Exception e) {
					e.printStackTrace();
					//break;//if exception occurred: leave loop
					rev++; //if exception occurred, go to next revision
					//make a note of revision where exception occurred, data may be faulty
				}
				
				if (monitor != null && monitor.isCanceled()) {
					break; //if cancel was pressed leave loop
				}
			}
			//svnConnector.updateToRevision(file, rev, monitor);
			if (monitor != null) {
				monitor.done();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*private void storeChange(ASTNode node, IChange change) {
		if (node instanceof MethodInvocation) {
			new MethodInvocationRecorder((MethodInvocation) node).storeChange(change);
		}else if (node instanceof VariableDeclaration){
			//This is to get changes to other local vars.
			//new LocalVariableRecorder((VariableDeclaration) node).storeChange(change);
		}
	}*/

}