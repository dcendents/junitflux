package com.google.code.junitFlux.builder;

import static com.google.code.junitFlux.builder.TestObjectAssembler.getAllTestsValidFinder;
import static com.google.code.junitFlux.builder.TestObjectAssembler.getBuilder;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.code.junitFlux.PluginTest;
import com.google.code.junitFlux.internal.invoker.JUnitInvoker;

/** Test interplay between Builder, Visitor and Finder*/
public class JUnitFluxBuilderAndResourceVisitorTest extends PluginTest {
	
	private Set<IType> invokedTests = new HashSet<IType>();
	
	private JUnitFluxBuilder builder;

	private IJavaProject project;

	private IPackageFragment mockPackage;
	
	@Before
	public void setUp() throws JavaModelException {
		mockPackage = getMockPackage();
		project = getMockProject(mockPackage);
		
		invokedTests.clear();
		final JUnitInvoker invokationRecorder = new JUnitInvoker(project) {
			@Override
			public void run(Set<IType> testsToRun) throws CoreException {
				invokedTests.addAll(testsToRun);
			}
		};
		
		builder = getBuilder(getAllTestsValidFinder(project), invokationRecorder);
	}
	
	/** test when invoking*/
	@Test
	public void testInvocationFlow() throws CoreException {
		IResourceDelta delta = getDelta("MyclassTest");
		builder.doBuild(delta, new NullProgressMonitor());
		assertEquals(1, invokedTests.size());
	}
	
	/** test when not invoking*/
	@Test
	public void testNonInvocationFlow() throws CoreException {
		IResourceDelta delta = getDelta("Myclass");
		builder.doBuild(delta, new NullProgressMonitor());
		assertEquals(0, invokedTests.size());
	}

	/** setup delta that will call visitor on accept and will return java test when searching in project*/
	private IResourceDelta getDelta(String name) throws CoreException, JavaModelException {
		IFolder parent = mock(IFolder.class);
		when(parent.members()).thenReturn(new IResource[] {});
		
		IResourceDelta delta = mock(IResourceDelta.class);
		ICompilationUnit mockUnit = getMockUnit(mockPackage, name+".java");
		when(mockPackage.getCompilationUnits()).thenReturn(new ICompilationUnit[] {mockUnit});
		final IResource resource = mockUnit.getResource();
		doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) throws Throwable {
				((IResourceVisitor) invocation.getArguments()[0]).visit(resource);
				return null;
			}
		}).when(delta).accept(any(IResourceDeltaVisitor.class));
		
		IType type = mock(IType.class);
		when(mockUnit.findPrimaryType()).thenReturn(type);
		return delta;
	}
}