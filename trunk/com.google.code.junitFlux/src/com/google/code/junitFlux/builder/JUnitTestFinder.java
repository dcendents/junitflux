package com.google.code.junitFlux.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import com.google.code.junitFlux.Activator;
import com.google.code.junitFlux.internal.utils.JUnitValidator;
import com.google.code.junitFlux.internal.utils.Util;

/** Given a resource will find a corresponding test,
 * provided it lays in the same package and satisfies naming convention,
 * i.e. has "Test" suffix or prefix
 */
public class JUnitTestFinder {
 
	private IJavaProject project;
	private JUnitValidator validator;
	
	JUnitTestFinder(IJavaProject project) {
		this(project, new JUnitValidator());
	}
	
	JUnitTestFinder(IJavaProject project, JUnitValidator validator) {
		this.project = project;
		this.validator = validator;
	}
	
	public List<IType> findTestFor(IResource candidate) throws CoreException {
		List<IType> result = new ArrayList<IType>();
		if (Util.isTest(candidate)) 
			addResource(result, Util.toCompilationUnit(candidate));

		Set<ICompilationUnit> units = getPackageChildren(candidate.getParent());
		for (ICompilationUnit unit : units) {
			if (!unit.getResource().equals(candidate) && Util.isTestFor(unit.getResource(), candidate)) 
				addResource(result, unit);
		}
		return result;
	}

	protected void addResource(List<IType> result, ICompilationUnit candidate) {
		if (hasCompileErrors(candidate))
			return;
		IType type = candidate.findPrimaryType();
		if (validator.isValidTest(type))
			result.add(type);
	}

	private boolean hasCompileErrors(ICompilationUnit candidate) {
		try {
			return candidate == null || !candidate.isStructureKnown();
		} catch (JavaModelException e) {
			Activator.getDefault().debug("Will ignore candidate: "+candidate, e);
			return true;
		}
	}

	/** return package children across all source folders*/ 
	private Set<ICompilationUnit> getPackageChildren(IContainer packageResource) throws JavaModelException {
		Set<ICompilationUnit> result = new HashSet<ICompilationUnit>();
		IPackageFragment javaPackage = Util.toJavaPackage(packageResource);
		if (javaPackage == null) 
			return result;
		String packageName = javaPackage.getElementName();
		for (IPackageFragmentRoot source : getSourceLocations()) {
			IPackageFragment packageFragment = source.getPackageFragment(packageName);
			if (packageFragment.exists())
				result.addAll(Arrays.asList(packageFragment.getCompilationUnits()));
		}
		return result;
	}
	
	/** return all source folders for the project*/
	private Set<IPackageFragmentRoot> getSourceLocations() throws JavaModelException {
		Set<IPackageFragmentRoot> result = new HashSet<IPackageFragmentRoot>();
		IPackageFragmentRoot[] packageFragmentRoots = project.getPackageFragmentRoots();
		for (IPackageFragmentRoot packageFragmentRoot : packageFragmentRoots) {
			if (packageFragmentRoot.isArchive() || !packageFragmentRoot.exists() || !packageFragmentRoot.isStructureKnown())
				continue;
			if (packageFragmentRoot.getKind() == IPackageFragmentRoot.K_SOURCE)
				result.add(packageFragmentRoot);			
		}
		return result;
	}
	
	public IJavaProject getProject() {
		return project;
	}	
}