package com.google.code.junitFlux.internal.invoker;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.junit.Test;

import com.google.code.junitFlux.PluginTest;

public class ConfigurationFactoryTest extends PluginTest {
	

	private ConfigurationFactory setupFactory(ILaunchConfiguration... configs) throws CoreException {
		ILaunchManager launchManagerMock = mock(ILaunchManager.class);
		when(launchManagerMock.getLaunchConfigurations(any(ILaunchConfigurationType.class))).thenReturn(configs);
		return new ConfigurationFactory(getMockProject(null), launchManagerMock);
	}
	
	@Test
	public void testFindZeroExisting() throws CoreException {
		ILaunchConfigurationWorkingCopy test1 = mock(ILaunchConfigurationWorkingCopy.class);
		ConfigurationFactory configurationFactory = setupFactory();
		assertNull(configurationFactory.findExistingLaunchConfiguration(test1));
	}
	
	@Test
	public void testFindSameExists() throws CoreException {
		ILaunchConfigurationWorkingCopy test1 = mock(ILaunchConfigurationWorkingCopy.class);
		when(test1.getAttribute(anyString(), anyString())).thenReturn("value");
		ConfigurationFactory configurationFactory = setupFactory(test1);
		assertEquals(test1, configurationFactory.findExistingLaunchConfiguration(test1));
	}
	
	@Test
	public void testFindDiffExists() throws CoreException {
		ILaunchConfigurationWorkingCopy test1 = mock(ILaunchConfigurationWorkingCopy.class);
		when(test1.getAttribute(anyString(), anyString())).thenReturn("value1");
		
		ILaunchConfigurationWorkingCopy test2 = mock(ILaunchConfigurationWorkingCopy.class);
		when(test1.getAttribute(anyString(), anyString())).thenReturn("value2");
		
		ConfigurationFactory configurationFactory = setupFactory(test1);
		assertNull(configurationFactory.findExistingLaunchConfiguration(test2));
	}
}
