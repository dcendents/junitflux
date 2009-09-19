package com.google.code.junitFlux.internal.invoker;

import static com.google.code.junitFlux.internal.utils.Util.getLaunchManager;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.Launch;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;

import com.google.code.junitFlux.Activator;

/** Invokes JUnit for specified tests*/
public class JUnitInvoker { 
	private IJavaProject project;
	
	public JUnitInvoker(IJavaProject project) {
		this.project = project;
	}
  
	public void run(final Set<IType> testsToRun) throws CoreException {
		if (testsToRun.size() == 0)
			return;
		final ILaunchConfiguration launchConfiguration = new ConfigurationFactory(project).getConfiguration(testsToRun);
		final ILaunchConfigurationDelegate delegate = new DelegateFactory().getDelegate(testsToRun);
		Activator.getDisplay().asyncExec(new Runnable() {
			public void run() {
				launch(launchConfiguration, delegate);
			}
		});
		Activator.getDefault().debug("Invoked builder on: "+testsToRun);	
	}

	private void launch(final ILaunchConfiguration config, final ILaunchConfigurationDelegate delegate) {
		Job job = new Job("JUnit Flux") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask("Launching JUnit", 100);
				try {
					invokeDelegate(config, delegate, monitor);
				} catch (CoreException e) {
					Activator.getDefault().log(e);
					return e.getStatus();
				} finally {
					monitor.done();
				}
				return Status.OK_STATUS;
			}
		};
		job.setPriority(Job.INTERACTIVE);
		job.schedule();
	}
	
	private void invokeDelegate(final ILaunchConfiguration config, final ILaunchConfigurationDelegate delegate,
			IProgressMonitor monitor) throws CoreException {
		final ILaunch launch = new Launch(config, ILaunchManager.RUN_MODE, null);
		launch.setAttribute(DebugPlugin.ATTR_CONSOLE_ENCODING, getLaunchManager().getEncoding(config));
		getLaunchManager().addLaunch(launch);
		delegate.launch(config, ILaunchManager.RUN_MODE, launch, monitor);
	}
}