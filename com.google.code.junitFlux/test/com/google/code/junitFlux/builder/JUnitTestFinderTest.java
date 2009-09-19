package com.google.code.junitFlux.builder;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.Before;
import org.junit.Test;

import com.google.code.junitFlux.PluginTest;

public class JUnitTestFinderTest extends PluginTest {
	
	private IPackageFragment mockPackage;
	private JUnitTestFinder finder;


	@Before
	public void setupWorkspace() throws JavaModelException {
		this.mockPackage = getMockPackage();
		IJavaProject project = getMockProject(mockPackage);
		this.finder = TestObjectAssembler.getAllTestsValidFinder(project);
	}
	
	
	/** test most examplary case of lookup of test by class*/
	@Test
	public void testFindTestByUndertest() throws CoreException {
		ICompilationUnit javaclass = getMockUnit(mockPackage, "Myclass.java");
		ICompilationUnit testclass = getMockUnit(mockPackage, "MyclassTest.java");
		
		when(mockPackage.getCompilationUnits()).thenReturn(new ICompilationUnit[] {javaclass});
		assertEquals(0, finder.findTestFor(javaclass.getResource()).size());
		
		when(mockPackage.getCompilationUnits()).thenReturn(new ICompilationUnit[] {javaclass, testclass});
		assertEquals(1, finder.findTestFor(javaclass.getResource()).size());
	}
	
	/** if test itself has changed we should run it*/
	@Test
	public void testFindTestByItself() throws CoreException {
		ICompilationUnit testclass = getMockUnit(mockPackage, "MyclassTest.java");
		
		when(mockPackage.getCompilationUnits()).thenReturn(new ICompilationUnit[] {testclass});
		assertEquals(1, finder.findTestFor(testclass.getResource()).size());
	}
	
}