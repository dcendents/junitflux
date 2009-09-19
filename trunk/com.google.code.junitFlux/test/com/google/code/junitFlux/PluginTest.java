package com.google.code.junitFlux;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.Before;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public class PluginTest {
	
	@Before
	public void setupPlugin() throws Exception {
		Activator activator = new Activator();
		BundleContext context = mock(BundleContext.class);
		Bundle bundle = mock(Bundle.class);
		when(context.getBundle()).thenReturn(bundle);
		activator.start(context);
	}
	
	protected IJavaProject getMockProject(IPackageFragment mockPackage) throws JavaModelException {
		IJavaProject project = mock(IJavaProject.class);
		IPackageFragmentRoot packageRoot = getMockPackageRoot(mockPackage);
		when(project.getPackageFragmentRoots()).thenReturn(new IPackageFragmentRoot[] {packageRoot});
		when(project.getElementType()).thenReturn(IJavaElement.JAVA_PROJECT);
		return project;
	}

	private IPackageFragmentRoot getMockPackageRoot(IPackageFragment mockPackage) throws JavaModelException {
		IPackageFragmentRoot packageRoot = mock(IPackageFragmentRoot.class);
		when(packageRoot.exists()).thenReturn(true);
		when(packageRoot.isStructureKnown()).thenReturn(true);
		when(packageRoot.getKind()).thenReturn(IPackageFragmentRoot.K_SOURCE);
		when(packageRoot.getPackageFragment(anyString())).thenReturn(mockPackage);
		return packageRoot;
	}
	
	protected IPackageFragment getMockPackage() throws JavaModelException {
		IPackageFragment mockPackage = mock(IPackageFragment.class);
		when(mockPackage.exists()).thenReturn(true);
		IContainer resource = mock(IContainer.class);
		when(mockPackage.getResource()).thenReturn(resource);
		when(resource.getAdapter(IJavaElement.class)).thenReturn(mockPackage);
		return mockPackage;
	}

	protected IResource getMockResource(IContainer parent, String name) {
		IResource resource = mock(IResource.class);
		when(resource.getParent()).thenReturn(parent);
		when(resource.getName()).thenReturn(name);
		when(resource.getType()).thenReturn(IResource.FILE);
		when(resource.getFileExtension()).thenReturn(name.substring(name.lastIndexOf(".")+1));
		//when(resource.getFullPath()).thenReturn(new Path(parentName+"/"+name));
		when(resource.exists()).thenReturn(true);
		return resource;
	}
	
	protected ICompilationUnit getMockUnit(IPackageFragment parent, String name) throws JavaModelException {
		ICompilationUnit unit = mock(ICompilationUnit.class);
		when(unit.getParent()).thenReturn(parent);
		when(unit.isStructureKnown()).thenReturn(true);
		
		IContainer packageResource = (IContainer) parent.getResource();
		IResource resource = getMockResource(packageResource, name);
		when(resource.getAdapter(IJavaElement.class)).thenReturn(unit);
		when(unit.getResource()).thenReturn(resource);
		return unit;
	}
}