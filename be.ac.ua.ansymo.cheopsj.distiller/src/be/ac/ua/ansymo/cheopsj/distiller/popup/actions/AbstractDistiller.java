package be.ac.ua.ansymo.cheopsj.distiller.popup.actions;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
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

public abstract class AbstractDistiller implements IObjectActionDelegate  {

	protected IProject selectedProject;
	protected IJavaProject selectedJavaProject;

	public AbstractDistiller() {
		super();
	}

	private void getProjectForSelection(ISelection selection) {
		if(selection == null){ return; }
		Object selectedElement = ((IStructuredSelection)selection).getFirstElement();
				
		if (selectedElement instanceof IProject) {
			selectedProject = (IProject) selectedElement;
		} else if (selectedElement instanceof IJavaProject){
			selectedJavaProject = (IJavaProject) selectedElement;
		}
	}

	private void getSelectedProject() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		ISelectionService selectionService = window.getSelectionService();
		ISelection selection = selectionService.getSelection("org.eclipse.jdt.ui.PackageExplorer");
		getProjectForSelection(selection);
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
		
		if(selectedJavaProject == null && selectedProject == null)
			return;
		
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(getShell());
		try {
			dialog.run(true, true, new IRunnableWithProgress() {
	
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					doYourThing(monitor);
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
	
	abstract void doYourThing(IProgressMonitor monitor);

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
	}

}