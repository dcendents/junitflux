package com.google.code.junitFlux.internal.utils;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;

public class Util {
	
	public static boolean isJavaFile(IResource resource) {
		if (resource.getType() == IResource.FILE && resource.exists()) {
			return resource.getAdapter(IJavaElement.class) != null;
		}
		return false;
	}
	
	public static boolean isTest(IResource test) {
		return test.getName().matches(".*Test+.*");
	}

	public static boolean isTestFor(IResource test, IResource underTest) {
		String name = getNameWithoutExtension(underTest);
		return test.getName().matches(".*"+name+".*Test.*") ||
			test.getName().matches(".*Test.*"+name+".*");
	}
	
	public static IJavaProject toJavaProject(IResource resource) {
		IJavaElement result = getJavaElement(resource);
		return result instanceof IJavaProject ? (IJavaProject) result : null;
	}

	public static IPackageFragment toJavaPackage(IResource resource) {
		IJavaElement result = getJavaElement(resource);
		return result instanceof IPackageFragment ? (IPackageFragment) result : null;
	}
	
	public static ICompilationUnit toCompilationUnit(IResource resource) {
		IJavaElement result = getJavaElement(resource);
		return result instanceof ICompilationUnit ? (ICompilationUnit) result : null;
	}
	
	public static ILaunchManager getLaunchManager() {
		return DebugPlugin.getDefault().getLaunchManager();
	}
	
	private static String getNameWithoutExtension(IResource resource) {
		String name = resource.getName();
		return name.substring(0, name.lastIndexOf('.'));
	}
	
	private static IJavaElement getJavaElement(IResource resource) {
		if (resource instanceof IAdaptable)
			return (IJavaElement) ((IAdaptable) resource).getAdapter(IJavaElement.class);
		return null;
	}
}