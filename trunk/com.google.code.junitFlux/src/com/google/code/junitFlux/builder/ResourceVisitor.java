package com.google.code.junitFlux.builder;

import static org.eclipse.core.resources.IResourceDelta.ADDED;
import static org.eclipse.core.resources.IResourceDelta.CHANGED;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IType;

import com.google.code.junitFlux.Activator;
import com.google.code.junitFlux.internal.invoker.JUnitInvoker;
import com.google.code.junitFlux.internal.utils.Util;

/** walks through changed resources and runs tests on them*/
class ResourceVisitor implements IResourceVisitor, IResourceDeltaVisitor {
	
	private JUnitTestFinder testFinder;
	private List<IResource> changedclasses = new ArrayList<IResource>();

	public ResourceVisitor(JUnitTestFinder testFinder) {
		this.testFinder = testFinder;
	}

	public boolean visit(IResource resource) {
		record(resource);
		return true;
	}

	public boolean visit(IResourceDelta delta) throws CoreException {
		IResource resource = delta.getResource();
		int kind = delta.getKind();
		if (kind == ADDED || kind == CHANGED)
			record(resource);
		return true;
	}
	
	private void record(IResource resource) {
		if (Util.isJavaFile(resource))
			changedclasses.add(resource);
	}

	public void processResources(IProgressMonitor monitor, JUnitInvoker invoker) throws CoreException {
		Set<IType> testsToRun = new HashSet<IType>();
		for (IResource candidate : changedclasses) {
			testsToRun.addAll(testFinder.findTestFor(candidate));
		}
		testsToRun.remove(null);
		monitor.beginTask("Running tests: "+testsToRun, IProgressMonitor.UNKNOWN);
		try {
			invoker.run(testsToRun);
		} catch (Exception e) {
			Activator.getDefault().log("Failed to invoke: "+testsToRun, e);
		} finally {
			monitor.done();
		}
	}
}