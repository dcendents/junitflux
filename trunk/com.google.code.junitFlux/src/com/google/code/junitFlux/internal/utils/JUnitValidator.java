package com.google.code.junitFlux.internal.utils;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.junit.util.TestSearchEngine;

import com.google.code.junitFlux.Activator;

public class JUnitValidator {

	public boolean isValidTest(IType type) {
		try {
			return TestSearchEngine.isTestOrTestSuite(type);
		} catch (Exception e) {
			Activator.getDefault().log(e);
			return false;
		}
	}
	
	public boolean isJUnitProject(IProject project) {
		IJavaProject javaProject = Util.toJavaProject(project);
		return javaProject != null && (TestSearchEngine.hasTestAnnotation(javaProject) ||
			TestSearchEngine.hasTestCaseType(javaProject));
	}
}
