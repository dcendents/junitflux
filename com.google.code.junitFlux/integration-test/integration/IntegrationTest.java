package integration;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.junit.JUnitCore;
import org.eclipse.jdt.junit.TestRunListener;
import org.eclipse.jdt.junit.model.ITestCaseElement;
import org.eclipse.jdt.junit.model.ITestRunSession;
import org.eclipse.jdt.junit.model.ITestElement.Result;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.ui.wizards.JavaCapabilityConfigurationPage;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredSelection;
import org.junit.Before;
import org.junit.Test;

import com.google.code.junitFlux.nature.ToggleNatureAction;

/** run me as eclipse plugin test*/
public class IntegrationTest extends TestRunListener {
	
	private IProgressMonitor monitor = new NullProgressMonitor();
	private CountDownLatch doneSignal;
	
	private IProject project;
	private IFile classUnderTest;
	
	private Result testResult;
	
	@Before
	public void setUp() throws Exception {
		setupProject();
		addFluxNature(project);
		project.build(IncrementalProjectBuilder.FULL_BUILD, monitor);
		JUnitCore.addTestRunListener(this);
	}
	
	@Test
	public void test() throws Exception {
		//change class, runner should triggered automatically
		classUnderTest.setContents(getClass().getResourceAsStream("underTestFailure.source"), IResource.NONE, monitor);
		//wait for run or timeout
		doneSignal = new CountDownLatch(1);
		doneSignal.await(10, TimeUnit.SECONDS);
		assertEquals(Result.FAILURE, testResult);

		//correct error
		classUnderTest.setContents(getClass().getResourceAsStream("underTestOK.source"), IResource.NONE, monitor);
		doneSignal = new CountDownLatch(1);
		doneSignal.await(10, TimeUnit.SECONDS);
		assertEquals(Result.OK, testResult);
	}
	
	@Override
	public void sessionFinished(ITestRunSession session) {
		doneSignal.countDown();
	}
	
	@Override
	public void testCaseFinished(ITestCaseElement testCaseElement) {
		testResult = testCaseElement.getTestResult(true);
	}	

	private void addFluxNature(IProject project) {
		ToggleNatureAction action = new ToggleNatureAction();
		action.selectionChanged(new Action(){}, new StructuredSelection(project));
		action.run(null);
	}

	/** Create simple project with src and test source folders, JUnit4 configuration, one class and one test */
	private void setupProject() throws CoreException, InterruptedException {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		project = root.getProject("project");
		project.create(monitor);
		project.open(monitor);

		IFolder srcFolder = createFolder(project, "src");
		IFolder srcPackage = createFolder(srcFolder, "mypackage");
		classUnderTest = createFile(srcPackage, "MyClass.java", "");
		
		IFolder testFolder = createFolder(project, "test");
		IFolder testPackage = createFolder(testFolder, "mypackage");
		createFile(testPackage, "MyClassTest.java", "test.source");

		JavaCapabilityConfigurationPage page = new JavaCapabilityConfigurationPage();
		List<IClasspathEntry> classpath = new ArrayList<IClasspathEntry>();
		classpath.add(JavaCore.newSourceEntry(srcFolder.getFullPath()));
		classpath.add(JavaCore.newSourceEntry(testFolder.getFullPath()));
		classpath.add(JavaRuntime.getDefaultJREContainerEntry());
		classpath.add(JavaCore.newContainerEntry(new Path("org.eclipse.jdt.junit.JUNIT_CONTAINER/4")));
		Path outputPath = new Path("/project/bin");
		page.init(JavaCore.create(project),  outputPath, classpath.toArray(new IClasspathEntry[]{}), true);
		page.configureJavaProject(monitor);
	}
	
	private IFile createFile(IFolder parent, String name, String sourceResource) throws CoreException {
		IFile file = parent.getFile(name);
		file.create(getClass().getResourceAsStream(sourceResource), true, monitor);
		return file;
	}

	private IFolder createFolder(IContainer container, String name) throws CoreException {
		IFolder folder  = container.getFolder(new Path(name));
		folder.create(true, true, monitor);
		return folder;
	}
}