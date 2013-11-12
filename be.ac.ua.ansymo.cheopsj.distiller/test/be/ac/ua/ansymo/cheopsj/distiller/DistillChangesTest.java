package be.ac.ua.ansymo.cheopsj.distiller;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.team.svn.core.SVNTeamPlugin;

import be.ac.ua.ansymo.cheopsj.model.changes.Change;
import be.ac.ua.ansymo.cheopsj.distiller.popup.actions.DistillChanges;
import be.ac.ua.ansymo.cheopsj.logger.JavaProjectHelper;
import be.ac.ua.ansymo.cheopsj.model.ModelManager;
import be.ac.ua.ansymo.cheopsj.model.ModelManagerChange;
import be.ac.ua.ansymo.cheopsj.model.changes.IChange;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class DistillChangesTest {
		
	private SVNURL rep_url;
	private File rep_dir;
	private File work_dir;
	private IProject project;
	
	private SVNClientManager clientManager;

	@Before
	public void setUp() throws Exception {	
		ModelManager.getInstance().clearModel();
		IJavaProject newProject = JavaProjectHelper.createJavaProject("TestProject1", "bin");//$NON-NLS-1$//$NON-NLS-2$
		project  = newProject.getProject();
		work_dir = project.getLocation().toFile();
		
		/* Create repository*/
		FSRepositoryFactory.setup();
		rep_dir = new File(ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile(), "repository");
		if (!rep_dir.mkdir()) {
			System.out.println("Error while making the directory for the repository.");
			//return;
		}
		rep_url = SVNRepositoryFactory.createLocalRepository(rep_dir, false, true);
		SVNRepository rep = SVNRepositoryFactory.create(rep_url);
		ISVNOptions options = SVNWCUtil.createDefaultOptions(true);
		ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager("","");
		rep.setAuthenticationManager(authManager);	
		
		clientManager = SVNClientManager.newInstance(options, authManager);	
		//Check-out the repository
		clientManager.getUpdateClient().setIgnoreExternals(true);
		clientManager.getUpdateClient().doCheckout(rep_url, work_dir, SVNRevision.HEAD, SVNRevision.HEAD, SVNDepth.INFINITY, true);
	}

	@After
	public void tearDown() throws Exception {
		//Stop everything SVN related, else it whines about secure storage which we don't need.
		SVNTeamPlugin.instance().setLocationsDirty(false);
		SVNTeamPlugin.instance().stop(null);
		//Clear the workspace (Has to because setUp and tearDown get called for each test)
		//project has to be deleted using eclipse interface else trouble
		project.delete(true, null);
		deleteFile(rep_dir);
		//ModelManager.getInstance().clearModel();
	}

	/**
	 * 
	 * Scenario's to test:
	 *  - Working copy = remote repository				OK
	 *    - Initial Revision	OK
	 *    - Second Revision		OK
	 *    - Multiple Files		OK
	 *    - No (source) Files	OK
	 *  - Working copy is behind of remote repository	OK
	 *  - Working copy is ahead of remote repository	IMPOSSIBLE WITH SVN
	 *  - Working copy has uncommitted changes			OK
	 *  - Working copy is not under version control		OK
	 *  
	 **/
	
	/**
	 * 
	 * This method tests a repository with only an initial revision.
	 * 
	 **/
	@Test
	public void testInitialRevision() {
		setUpProject(this.project);
		generateInitialRevision(this.project);
		commit(this.clientManager, this.work_dir, "First commit");
		callDistillChanges(this.project);
		
		// And now actually test that we have found the changes.
		List<Change> changes = ModelManagerChange.getInstance().getChanges();
		checkInitialRevision(changes);
	}
	
	/**
	 * 
	 * This method tests changing something in the first revision.
	 * 
	 **/
	@Test
	public void testSecondRevision() {
		setUpProject(this.project);
		generateInitialRevision(this.project);
		commit(this.clientManager, this.work_dir, "First commit");
		generateSecondRevision(this.project);
		commit(this.clientManager, this.work_dir, "Second commit");
		callDistillChanges(this.project);
		
		// And now actually test that we have found the changes.
		List<Change> changes = ModelManagerChange.getInstance().getChanges();
		checkSecondRevision(changes);
		checkInitialRevision(changes);
	}
	
	/**
	 * 
	 * This method tests that all source files are found.
	 * 
	 **/
	@Test
	public void testMultipleFiles() {
		setUpProject(this.project);
		generateInitialRevision(this.project);
		// Create an additional file.
		try {
			IFile jMain =  project.getFolder("src").getFolder("package1").getFile("Base.java");	

			// Create file.
			String content =
				"package package1;\n\n" +
				"public class Base {\n" +
				"\tprivate int w = 0;\n" +
				"\tprivate int h = 0;\n\n" +
				"\tpublic void add(int w, int h) {\n" +
				"\t\tthis.w += w;\n" +
				"\t\tthis.h += h;\n" +
				"\t}\n" +
				"}\n";
			
			// Create the file now in eclipse.
			InputStream is = new ByteArrayInputStream(content.getBytes());
			jMain.create(is, false, null);
		} catch (CoreException e) {
			e.printStackTrace();
			fail("Creating the additional file went wrong.");
		}

		commit(this.clientManager, this.work_dir, "First commit");
		generateSecondRevision(this.project);
		// Change the second file as well.
		try {
			IFile jMain =  project.getFolder("src").getFolder("package1").getFile("Base.java");	

			// Create file.
			String content =
				"package package1;\n\n" +
				"public class Base {\n" +
				"\tprivate int w = 1;\n" +
				"\tprivate int h = 1;\n\n" +
				"\tpublic void add(int w, int h) {\n" +
				"\t\tthis.w += w;\n" +
				"\t\tthis.h += h;\n" +
				"\t}\n\n" +
				"\tpublic void doubleIt() {\n" + 
				"\t\tadd(this.w, this.h);\n"+
				"\t}\n" +
				"}\n";
			
			// Create the file now in eclipse.
			InputStream is = new ByteArrayInputStream(content.getBytes());
			jMain.setContents(is, false, false, null);
		} catch (CoreException e) {
			e.printStackTrace();
			fail("Changing the additional file went wrong.");
		}
		
		commit(this.clientManager, this.work_dir, "Second commit");
		callDistillChanges(this.project);
		
		// And now actually test that we have found the changes.
		List<Change> changes = ModelManagerChange.getInstance().getChanges();
		check(changes, "Addition", "package1.Base", "First commit", "Class");
		check(changes, "Addition", "package1.Base.w", "First commit", "Attribute");
		check(changes, "Addition", "package1.Base.h", "First commit", "Attribute");
		check(changes, "Addition", "package1.Base.add", "First commit", "Method");
		check(changes, "Addition", "package1.Base.doubleIt", "Second commit", "Method");
		// FIXME: fix this bug: adding invocations inside a new method are not found.
		check(changes, "Addition", "package1.Base.doubleIt{package1.Base.add}", "", "Invocation");
		checkSecondRevision(changes);
		checkInitialRevision(changes);	
	}
	
	/**
	 * 
	 * This method tests a very unrealistic project, one without source files.
	 * Though there is a file present, it is simply a text file.
	 * 
	 **/
	@Test
	public void testNoSourceFiles() {
		setUpProject(this.project);
		generateInitialRevision(this.project);
		// The main.java file is kept, but does not contain any source code.
		IFile jMain = this.project.getFolder("src").getFolder("package1").getFile("Main.java");
		try {
			jMain.delete(true, null);
		} catch (CoreException e) {
			e.printStackTrace();
			fail("Removing source file went wrong.");
		}
		
		IFile mainTXT = this.project.getFolder("src").getFolder("package1").getFile("Main.txt");
		String content =
			"Main Methods In Java\n" +
			"====================\n\n" +
			"As most modern day programming languages Java demands that the application " +
			"has a main method, a method that functions as the starting point of the " +
			"application. This common sense, yet implementing this in Java may not be " +
			"that obvious. Because in Java everything belongs to a class, so should " +
			"the main method. The problem that might arise is that we first need " +
			"to instantiate the class before we can start the application, but how " +
			"can we instantiate a class if the application didn't even start?\n" +
			"Java handles this in a pretty smart way: by simply having so cold static " +
			"stuff, we can allow it to be part of the class and not the object, thus " +
			"making instantiating the class obsolete. Despite their effort it still " +
			"feels weird that the main method is part of a class.\n\n" +
			"To prove that I am not the only one a common way to organize the main method " +
			"is by creating a Main class, this class does nothing but setting up the " +
			"application. The entire class will now handle as a starting point of the " +
			"application instead of only the main method. Thefore the Main class tends " +
			"to be static as a whole.";
		
		InputStream is = new ByteArrayInputStream(content.getBytes());
		try {
			mainTXT.create(is, false, null);
		} catch (CoreException e) {
			e.printStackTrace();
			fail("Setting the contents of the file went wrong");
		}
		commit(this.clientManager, this.work_dir, "First commit");
		callDistillChanges(this.project);
		
		// And now actually test that we have found the changes.
		List<Change> changes = ModelManagerChange.getInstance().getChanges();
		for (Change change: changes) {
			System.out.println(change.getName());
			System.out.println(change.getChangeType());
			System.out.println(change.getFamixType());
		}
		assertEquals(0, changes.size());
	}
	
	/**
	 * 
	 * This method tests what happens when the working copy is behind of the repository.
	 * What should happen? Currently it is the working copy that is used.
	 * 
	 **/
	@Test
	public void testOutdatedWorkingCopy() {
		setUpProject(this.project);
		generateInitialRevision(this.project);
		commit(this.clientManager, this.work_dir, "First commit");
		
		try {	
			IJavaProject newProject = JavaProjectHelper.createJavaProject("TestProject2", "bin");//$NON-NLS-1$//$NON-NLS-2$
			IProject project  = newProject.getProject();
			File work_dir = project.getLocation().toFile();
			setUpProject(project);

			ISVNOptions options = SVNWCUtil.createDefaultOptions(true);
			ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager("","");
			SVNClientManager clientManager = SVNClientManager.newInstance(options, authManager);	
			clientManager.getUpdateClient().setIgnoreExternals(true);
			clientManager.getUpdateClient().doCheckout(rep_url, work_dir, SVNRevision.HEAD, SVNRevision.HEAD, SVNDepth.INFINITY, true);
			
			generateSecondRevision(project);
			commit(clientManager, work_dir, "Second commit");
			
			project.delete(true, null);
		} catch (SVNException e) {
			e.printStackTrace();
			fail("Third party changing the repository went wrong.");
		} catch (CoreException e) {
			e.printStackTrace();
			fail("Creating third party working directory went wrong.");
		}
		
		callDistillChanges(this.project);
		List<Change> changes = ModelManagerChange.getInstance().getChanges();
		checkInitialRevision(changes);
	}
	
	/**
	 * 
	 * This method tests what happens when there are uncommitted changes.
	 * 
	 **/
	@Test
	public void testUncommittedChanges() {
		setUpProject(this.project);
		generateInitialRevision(this.project);
		commit(this.clientManager, this.work_dir, "First commit");
		generateSecondRevision(this.project);
		callDistillChanges(this.project);
		List<Change> changes = ModelManagerChange.getInstance().getChanges();
		checkInitialRevision(changes);
	}
	
	/**
	 * 
	 * This method tests running the distiller on a project that does not have version control.
	 * 
	 * 
	 **/
	@Test
	public void testNoVersionControl()
	{
		try {
			IJavaProject newProject = JavaProjectHelper.createJavaProject("TestProject2", "bin");
			IProject project  = newProject.getProject();
			setUpProject(project);
			generateInitialRevision(project);
			callDistillChanges(project);
			
			List<Change> changes = ModelManagerChange.getInstance().getChanges();
			assertEquals(0, changes.size());
		
			project.delete(true, null);
		} catch (CoreException e) {
			e.printStackTrace();
			fail("Creating project went wrong.");
		}
	}

	/**
	 * 
	 * This method will handle the actual call of the change distiller.
	 * @param project The project on which we want to run the distiller.
	 * 
	 **/
	private void callDistillChanges(IProject project){
		try{	
			//We need to make an object to test the methods
			DistillChanges distiller = new DistillChanges();
			
			//Set the private selectedProject field
			//Field selectedProject =  DistillChanges.class.getField("selectedProject");
			Field selectedProject = DistillChanges.class.getDeclaredField("selectedProject");
			selectedProject.setAccessible(true);
			selectedProject.set(distiller, project);
			
			//Call the private iterateRevisions method of DistillChanges. 
			Method methodUnderTest = DistillChanges.class.getDeclaredMethod("iterateRevisions", IProgressMonitor.class);
			methodUnderTest.setAccessible(true);
			methodUnderTest.invoke(distiller, new Object[]{null});
		}catch(Exception e){
			fail("Calling distill changes went wrong.");
		}
	}
	
	/** 
	 * 
	 * This method will set up the entire project hierarchy.
	 * @param project The project in which we will generate the hierarchy.
	 * 
	 **/
	private void setUpProject(IProject project) {
		try {
			// Create src folder.
			IFolder jSrc = project.getFolder("src");
			if (!jSrc.exists()) {
				jSrc.create(false, true, null);
			}
				
			// Create package.
			IFolder jPack = jSrc.getFolder("package1");
			if (!jPack.exists()) {
				jPack.create(false, true, null);
			}
		
			// Create the file.
			IFile jMain = jPack.getFile("Main.java");
			InputStream is = new ByteArrayInputStream("".getBytes());
			if (!jMain.exists()) {
				jMain.create(is, false, null);
			}
		} catch (CoreException e) {
			e.printStackTrace();
			fail("Setting up the project went wrong.");
		}
	}
	
	/**
	 * 
	 * This method will generate the initial revision.
	 * @param project The project in which the changes occur.
	 * 
	 **/
	private void generateInitialRevision(IProject project) {
		try {
			IFile jMain =  project.getFolder("src").getFolder("package1").getFile("Main.java");	
			
			// Create file.
			String content =
				"package package1;\n\n" +
				"public class Main {\n" +
				"\tprivate int result = 0;\n" +
				"\tpublic int shared = 1;\n\n" +
				"\tpublic static void main(String[] args) {\n" +
				"\t\tSystem.out.println(\"Main method started.\");\n" +
				"\t}\n" +
				"\n" +
				"\tpublic void stupidMethod() {\n" +
				"\t\tint a = 0;\n" +
				"\t\tint b = 0;\n" +
				"\t\tSystem.out.println(a + \" \" + b);\n" + 
				"\t}\n" +
				"}\n";
			
			// Create the file now in eclipse.
			InputStream is = new ByteArrayInputStream(content.getBytes());
			jMain.setContents(is, false, false, null);
		} catch (CoreException e) {
			e.printStackTrace();
			fail("Creating the initial commit went wrong.");
		}
	}
	
	/**
	 * 
	 * This method applies some changes to the file create by the initial revision.
	 * @param project The project in which the changes occur.
	 * 
	 **/
	private void generateSecondRevision(IProject project) {
		try {
			IFile jMain =  project.getFolder("src").getFolder("package1").getFile("Main.java");	

			String content =
				"package package1;\n\n" +
				"public class Main {\n" +
				"\tprivate int result = 0;\n\n" +
				"\tpublic static void main(String[] args) {\n" +
				"\t\tSystem.out.println(\"Main method started.\");\n" +
				"\t\trun();\n" +
				"\t}\n" +
				"\n" +
				"\tprivate void run() {\n" +
				"\t\tresult++;\n" +
				"\t}\n" +
				"}\n";
			
			InputStream is = new ByteArrayInputStream(content.getBytes());
			jMain.setContents(is, false, false, null);	
		} catch (CoreException e) {
			e.printStackTrace();
			fail("Chaning the java code went wrong.");
		}
	}
	
	/**
	 * 
	 * This method commits changes that occured.
	 * 
	 * @param clientManager The clientmanager used to commit.
	 * @param work_dir The directory to commit.
	 * @param message The message of the commit.
	 *
	 **/
	private void commit(SVNClientManager clientManager, File work_dir, String message) {
		try {
			clientManager.getWCClient().doAdd(work_dir, true, false , false , SVNDepth.INFINITY, false, false); 
			clientManager.getCommitClient().doCommit(new File[] {work_dir} , false, message, null, null, false, false, SVNDepth.INFINITY); 
			clientManager.getUpdateClient().doUpdate(work_dir, SVNRevision.HEAD, SVNDepth.INFINITY, false, false);
		}catch (SVNException e) {
			e.printStackTrace();
			fail("Commiting and/or updating the repository went wrong.");
		}
	}
	
	/**
	 * 
	 * This method will assert that the initial revision went correctly
	 * 
	 **/
	private void checkInitialRevision(List<Change> changes){
		// We will now remove all expected changes one by one.
		check(changes, "Addition", "package1", "First commit", "Package");
		check(changes, "Addition", "package1.Main", "First commit", "Class");
		check(changes, "Addition", "package1.Main.result", "First commit", "Attribute");
		check(changes, "Addition", "package1.Main.shared", "First commit", "Attribute");
		check(changes, "Addition", "package1.Main.main", "First commit", "Method");
		check(changes, "Addition", "package1.Main.stupidMethod", "First commit", "Method");
		check(changes, "Addition", "package1.Main.main{System.out.println}", "First commit", "Invocation");
		check(changes, "Addition", "package1.Main.stupidMethod{System.out.println}", "First commit", "Invocation");
		assertEquals(0, changes.size());
	}
	
	/**
	 * 
	 * This method will assert that the second revision went correctly
	 * 
	 **/
	private void checkSecondRevision(List<Change> changes) {
		check(changes, "Removal", "package1.Main.shared", "Second commit", "Attribute");
		// FIXME: fix this bug: removing invocations in a new method have no intent.
		check(changes, "Removal", "package1.Main.stupidMethod{System.out.println}", "", "Invocation");
		check(changes, "Removal", "package1.Main.stupidMethod", "Second commit", "Method");
		check(changes, "Addition", "package1.Main.run", "Second commit", "Method");
		// FIXME: fix this bug: adding invocations of a new method have no intent.
		check(changes, "Addition", "package1.Main.main{package1.Main.run}", "", "Invocation");
	}
	
	/**
	 * 
	 * This method checks the present of a certain change.
	 * It will remove the first change that matches the criteria.
	 * 
	 * @param changes The list of changes we will work in.
	 * @param type The type of change.
	 * @param name The name of the thing that changed.
	 * @param intent The message of the commit in which the change occurred.
	 * @param famixType The famixtype of the change
	 * 
	 **/
	private void check(List<Change> changes, String type, String name, String intent, String famixType) {
		for (Change change: changes) {
			if (change.getChangeType().equals(type) && 
					change.getName().equals(name) && 
					change.getIntent().equals(intent) &&
					change.getFamixType().equals(famixType)) {
				changes.remove(change);
				return;
			}
		}
		fail("Could not find an expected change: " + type + " " + name + " " + intent + " " + famixType);
	}
	
	/**
	 * 
	 * This method deletes folders and files recursively
	 * @param f Folder or file to be deleted
	 * 
	 **/
	private void deleteFile(File f){
		if (f.isDirectory()) {
			for (File c : f.listFiles()) deleteFile(c);
		}
		f.delete();
	}
}