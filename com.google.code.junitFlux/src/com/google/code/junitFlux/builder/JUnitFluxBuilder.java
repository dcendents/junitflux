package com.google.code.junitFlux.builder;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.IJavaProject;

import com.google.code.junitFlux.Activator;
import com.google.code.junitFlux.internal.invoker.JUnitInvoker;
import com.google.code.junitFlux.internal.utils.Util;

/** Builder, entry-point, will do processing only on incremental changes*/
public class JUnitFluxBuilder extends IncrementalProjectBuilder {

	public static final String BUILDER_ID = "com.google.code.junitFlux.builder";
	
	protected JUnitTestFinder testFinder;
	protected JUnitInvoker invoker;
	
	@Override
	protected void startupOnInitialize() {
		super.startupOnInitialize();
		IJavaProject javaProject = Util.toJavaProject(getProject());
		this.testFinder = new JUnitTestFinder(javaProject);
		this.invoker = new JUnitInvoker(javaProject);
	}
	
	@SuppressWarnings("unchecked")
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException {
		if (kind == INCREMENTAL_BUILD || kind == AUTO_BUILD) {
			try {
				doBuild(getDelta(getProject()), monitor);
			} catch (OperationCanceledException e) {
				throw e;
			} catch (Exception e) {
				Activator.getDefault().logAndNotify(e);
			}
		}
		return null;
	}
	
	void doBuild(IResourceDelta delta, IProgressMonitor monitor) throws CoreException {
		if (delta == null) return;
		ResourceVisitor visitor = new ResourceVisitor(testFinder);
		delta.accept(visitor);
		visitor.processResources(monitor, invoker);
	}
}