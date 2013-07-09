package be.ac.ua.ansymo.cheopsj.distiller.popup.actions;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import be.ac.ua.ansymo.cheopsj.changerecorders.MethodInvocationRecorder;
import be.ac.ua.ansymo.cheopsj.distiller.cd.ChangeDistillerProxy;
import be.ac.ua.ansymo.cheopsj.distiller.changeextractor.ChangeExtractor;
import be.ac.ua.ansymo.cheopsj.distiller.connection.Connector;
import be.ac.ua.ansymo.cheopsj.distiller.connection.ConnectorFactory;
import be.ac.ua.ansymo.cheopsj.distiller.connection.LogEntryHandler;
import be.ac.ua.ansymo.cheopsj.distiller.connection.LogEntryHandler.Change;
import be.ac.ua.ansymo.cheopsj.logger.astdiffer.ASTComparator;
import be.ac.ua.ansymo.cheopsj.model.changes.Add;
import be.ac.ua.ansymo.cheopsj.model.changes.IChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Remove;


public class DistillChanges implements IObjectActionDelegate {

	private IProject selectedProject;
	private Connector connector;
	
	private IProject getProjectForSelection(ISelection selection){
		if(selection == null){ return null; }
		Object selectedElement = ((IStructuredSelection)selection).getFirstElement();
				
		if (selectedElement instanceof IProject) {
			return (IProject) selectedElement;
		} else if (selectedElement instanceof IJavaProject){
			return ((IJavaProject) selectedElement).getProject();
		}
		
		return null;
	}
	
	private void getSelectedProject(){
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		ISelectionService selectionService = window.getSelectionService();
		ISelection selection = selectionService.getSelection("org.eclipse.jdt.ui.PackageExplorer");
		selectedProject = getProjectForSelection(selection);
	}
	
	private Shell getShell() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		if (window == null)
			return null;
		return window.getShell();
	}

	@Override
	public void run(IAction action) {
		getSelectedProject();
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(getShell());
		try {
			dialog.run(true, true, new IRunnableWithProgress() {

				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					iterateRevisions(monitor);
				}
			});
		} catch (InterruptedException e) {

		} catch (InvocationTargetException e) {
			Throwable target = e.getTargetException();
			ErrorDialog.openError(getShell(), "TargetError",
					"Error Occured While Running Experiment", new Status(0,
							"MetricsExperiment", 0, "no message", target));
		}
	}
		
	private void iterateRevisions(IProgressMonitor monitor){
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
					
					double percent = ((double)rev/targetRev)*100;
					if (monitor != null) {
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
	
	private void storeChange(ASTNode node, IChange change) {
		if (node instanceof MethodInvocation) {
			new MethodInvocationRecorder((MethodInvocation) node).storeChange(change);
		}else if (node instanceof VariableDeclaration){
			//This is to get changes to other local vars.
			//new LocalVariableRecorder((VariableDeclaration) node).storeChange(change);
		}
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
	}

}