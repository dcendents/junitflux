package com.google.code.junitFlux.nature;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.junit.Test;

import com.google.code.junitFlux.PluginTest;
import com.google.code.junitFlux.builder.JUnitFluxBuilder;

public class JUnitFluxNatureTest extends PluginTest {
	
	private JUnitFluxNature nature = new JUnitFluxNature();

	public IProjectDescription initProject(ICommand[] initialCommands) throws CoreException {
		IProjectDescription projectDescription = mock(IProjectDescription.class);
		when(projectDescription.getBuildSpec()).thenReturn(initialCommands);
		IProject project = mock(IProject.class);
		when(project.getDescription()).thenReturn(projectDescription);
		nature.setProject(project);
		return projectDescription;
	}
	
	@Test
	public void testConfigure() throws CoreException {
		IProjectDescription projectDescription = initProject(new ICommand[] {});
		ICommand commandMock = mock(ICommand.class);
		when(projectDescription.newCommand()).thenReturn(commandMock);
		nature.configure();
		verify(projectDescription).setBuildSpec(new ICommand[]{commandMock});
	}
	
	@Test
	public void testDeconfigure() throws CoreException {
		ICommand commandMock = mock(ICommand.class);
		when(commandMock.getBuilderName()).thenReturn(JUnitFluxBuilder.BUILDER_ID);
		IProjectDescription projectDescription = initProject(new ICommand[] {commandMock});
		nature.deconfigure();
		verify(projectDescription).setBuildSpec(new ICommand[]{});
	}
}
