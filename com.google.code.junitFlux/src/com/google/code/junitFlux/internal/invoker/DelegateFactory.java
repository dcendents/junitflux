package com.google.code.junitFlux.internal.invoker;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.junit.launcher.JUnitLaunchConfigurationDelegate;

import com.google.code.junitFlux.Activator;

@SuppressWarnings("unchecked")
/** Creates delegate to launch several tests in one go*/ 
public class DelegateFactory {
	
	/** Returns customized version of JUnitLaunchConfigurationDelegate 
	 * capable of running more then one specific test*/
	ILaunchConfigurationDelegate getDelegate(final Collection<IType> testsToRun) {
		return new JUnitLaunchConfigurationDelegate() {
			@Override
			protected void collectExecutionArguments(ILaunchConfiguration configuration, List vmArguments, List programArguments) throws CoreException {
				super.collectExecutionArguments(configuration, vmArguments, programArguments);
				addTestSpecification(programArguments, testsToRun);
			}
			
			@Override
			protected IMember[] evaluateTests(ILaunchConfiguration configuration, IProgressMonitor monitor) throws CoreException {
				return new IMember[]{};//skip that, we will supply custom test specification
			}
		};
	}

	/** remove all tests specified by JUnitLaunchConfigurationDelegate and instead supply our file*/
	void addTestSpecification(List programArguments, Collection<IType> testsToRun) throws CoreException {
		programArguments.add("-testNameFile");
		programArguments.add(createTestNamesFile(testsToRun));
	}
	
	private String createTestNamesFile(Collection<IType> testsToRun) throws CoreException {
		try {
			File file= File.createTempFile("fluxTestNames", ".txt"); 
			file.deleteOnExit();
			BufferedWriter bw= null;
			try {
				bw= new BufferedWriter(new FileWriter(file));
				for (IType test : testsToRun) {
					bw.write(test.getFullyQualifiedName());
					bw.newLine();
				}
			} finally {
				if (bw != null) {
					bw.close();
				}
			}
			return file.getAbsolutePath();
		} catch (Exception e) {
			throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, IStatus.ERROR, null, e));
		}
	}
}