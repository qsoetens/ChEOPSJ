package be.ac.ua.ansymo.cheopsj.distiller.popup.actions;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodInvocation;
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

import java.util.HashMap;
import java.util.Map;

import be.ac.ua.ansymo.cheopsj.distiller.asts.MethodInvocationVisitor;
import be.ac.ua.ansymo.cheopsj.model.ModelManagerListeners;

public class CountMethodDeclarationsAndReferences implements IObjectActionDelegate {
	private IResource selectedResource;
	private IJavaProject javaProject;

	private Map<String,Integer> declarationcounter;
	private Map<String,Integer> referencecounter;

	private IResource getProjectForSelection(ISelection selection){
		if(selection == null){ return null; }
		Object selectedElement = ((IStructuredSelection)selection).getFirstElement();

		if (selectedElement instanceof IJavaProject){
			javaProject = (IJavaProject) selectedElement;
			return ((IJavaProject) selectedElement).getProject();
		} else if (selectedElement instanceof IFolder){
			return (IFolder) selectedElement;
		}

		return null;
	}

	private void getSelectedProject(){
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		ISelectionService selectionService = window.getSelectionService();
		ISelection selection = selectionService.getSelection("org.eclipse.jdt.ui.PackageExplorer");
		selectedResource = getProjectForSelection(selection);
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
					doit(monitor);
				}
			});
			dialog.close();
		} catch (InterruptedException e) {

		} catch (InvocationTargetException e) {
			Throwable target = e.getTargetException();
			ErrorDialog.openError(getShell(), "TargetError",
					"Error Occured While Running Experiment", new Status(0,
							"MetricsExperiment", 0, "no message", target));
		}
	}

	private void doit(IProgressMonitor monitor){
		if(javaProject instanceof IJavaProject){
			try{
				declarationcounter = new HashMap<String,Integer>();
				referencecounter = new HashMap<String,Integer>();
				ModelManagerListeners.setAlertListeners(false);
				IPackageFragment[] packages = javaProject.getPackageFragments();
				countMethodDeclarations(packages);
				countMethodReferences(packages);
				printMethodDeclarations();
				
			}catch(JavaModelException e){

			}
		}	
	}
	
	private void printMethodDeclarations() {
		try {
			FileWriter fstream = new FileWriter("/Users/quinten/Desktop/count.txt");
			BufferedWriter out = new BufferedWriter(fstream);			
		
			out.write("method identifier, nr of declarations, nr of invocations"+'\n');
			for(String methodname : declarationcounter.keySet()){
				int declarations = declarationcounter.get(methodname);
				if(referencecounter.containsKey(methodname)){
					int invocations = referencecounter.get(methodname);
					out.write(methodname + ',' + declarations + ',' + invocations +'\n');
				}
			}

			
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}		

	private void countMethodDeclarations(IPackageFragment[] packages) throws JavaModelException  {
		for (IPackageFragment mypackage : packages) {
			if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
				//store additions for each class inside the package.
				for (ICompilationUnit unit : mypackage.getCompilationUnits()) {
					IType[] allTypes = unit.getAllTypes();
					//store additions of type
					for(IType type: allTypes){
						IMethod[] methods = type.getMethods();

						for(IMethod method: methods){
							String methodname = method.getElementName();
							if(declarationcounter.containsKey(methodname))
								declarationcounter.put(methodname, declarationcounter.get(methodname) + 1) ;
							else
								declarationcounter.put(methodname, 1); 
						}

						//and store any nested classes
						countNestedMethodDeclarations(type);
					}			
				}
			}
		}

	}

	private void countNestedMethodDeclarations(IType type) throws JavaModelException {
		IType[] membertypes = type.getTypes();
		for(IType member: membertypes){
			IMethod[] methods = member.getMethods();

			for(IMethod method: methods){
				String methodname = method.getElementName();
				if(declarationcounter.containsKey(methodname))
					declarationcounter.put(methodname, declarationcounter.get(methodname) + 1) ;
				else
					declarationcounter.put(methodname, 1); 
			}

			//and store any nested classes
			countNestedMethodDeclarations(member);
		}
	}


	private void countMethodReferences(IPackageFragment[] packages) throws JavaModelException {
		for (IPackageFragment mypackage : packages) {
			if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
				//store additions for each class inside the package.
				for (ICompilationUnit unit : mypackage.getCompilationUnits()) {

					CompilationUnit parse = parse(unit);
					MethodInvocationVisitor visitor = new MethodInvocationVisitor();
					parse.accept(visitor);

					for(MethodInvocation invocation : visitor.getMethodInvocations()){
						String methodname = invocation.getName().getIdentifier();
						if(referencecounter.containsKey(methodname))
							referencecounter.put(methodname, referencecounter.get(methodname) + 1) ;
						else
							referencecounter.put(methodname, 1); 
						
					}
				}
			}

		}
	}

	private  CompilationUnit parse(ICompilationUnit unit) {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit);
		parser.setResolveBindings(true);
		return (CompilationUnit) parser.createAST(null); // parse
	}


	@Override
	public void selectionChanged(IAction action, ISelection selection) {

	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {

	}

}
