package com.google.code.junitFlux.internal.invoker;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.junit.launcher.JUnitLaunchShortcut;

import com.google.code.junitFlux.internal.utils.Util;

/** This object can be used to create or reuse existing JUnit launch configuration.
 *  If configuration requested for multiple tests, launch configuration of underlying 
 *  project will be created/reused. 
 */
public class ConfigurationFactory extends JUnitLaunchShortcut {
	
	private IJavaProject project;
	private ILaunchManager manager;

	ConfigurationFactory(IJavaProject project) {
		this(project, Util.getLaunchManager());
	}
	
	ConfigurationFactory(IJavaProject project, ILaunchManager manager) {
		this.project = project;
		this.manager = manager;
	}
	
	/** Create+save or reuse launch configuration for specified types
	 * @param testsToRun - tests, assumed to be from one project
	 * @return ILaunchConfiguration
	 * @throws CoreException
	 */
	ILaunchConfiguration getConfiguration(Set<IType> testsToRun) throws CoreException {
		ILaunchConfigurationWorkingCopy wc = createDefaultLaunchConfig(testsToRun);
		ILaunchConfiguration existingConfig = findExistingLaunchConfiguration(wc);
		return existingConfig == null ? wc.doSave() : existingConfig;
	}
	
	private ILaunchConfigurationWorkingCopy createDefaultLaunchConfig(Set<IType> testsToRun) throws CoreException {
		if (testsToRun.size() > 1) {
			return createLaunchConfiguration(project);
		} else {
			IType test = testsToRun.iterator().next();
			return createLaunchConfiguration(test);
		}
	}
	
	protected ILaunchConfiguration findExistingLaunchConfiguration(ILaunchConfigurationWorkingCopy template) throws CoreException {
		List<ILaunchConfiguration> candidateConfigs= findExistingLaunchConfigurations(template);
		int candidateCount= candidateConfigs.size();
		if (candidateCount == 0) 
			return null;
		return candidateConfigs.get(0);
	}
	
	private List<ILaunchConfiguration> findExistingLaunchConfigurations(ILaunchConfigurationWorkingCopy temporary) throws CoreException {
		ILaunchConfigurationType configType= temporary.getType();

		ILaunchConfiguration[] configs= manager.getLaunchConfigurations(configType);
		String[] attributeToCompare= getAttributeNamesToCompare();

		List<ILaunchConfiguration> candidateConfigs= new ArrayList<ILaunchConfiguration>(configs.length);
		for (int i= 0; i < configs.length; i++) {
			ILaunchConfiguration config= configs[i];
			if (hasSameAttributes(config, temporary, attributeToCompare)) {
				candidateConfigs.add(config);
			}
		}
		return candidateConfigs;
	}

	private static boolean hasSameAttributes(ILaunchConfiguration config1, ILaunchConfiguration config2, String[] attributeToCompare) {
		try {
			for (int i= 0; i < attributeToCompare.length; i++) {
				String val1= config1.getAttribute(attributeToCompare[i], "");
				String val2= config2.getAttribute(attributeToCompare[i], "");
				if (!val1.equals(val2)) {
					return false;
				}
			}
			return true;
		} catch (CoreException e) {
			// ignore access problems here, return false
		}
		return false;
	}
}