package com.google.code.junitFlux.nature;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.junit.Test;

public class ToggleNatureActionTest {
	private ToggleNatureAction action = new ToggleNatureAction();
	
	public IProject initProject(String[] initialNatures) throws CoreException {
		IProjectDescription projectDescription = mock(IProjectDescription.class);
		when(projectDescription.getNatureIds()).thenReturn(initialNatures);
		IProject project = mock(IProject.class);
		when(project.getDescription()).thenReturn(projectDescription);
		return project;
	}
	
	@Test
	public void testAdd() throws CoreException {
		IProject project = initProject(new String[] {});
		IProjectDescription description = project.getDescription();
		action.toggleNature(project);
		verify(description).setNatureIds(new String[] {JUnitFluxNature.NATURE_ID});
	}
	
	@Test
	public void testRemove() throws CoreException {
		IProject project = initProject(new String[] {JUnitFluxNature.NATURE_ID});
		IProjectDescription description = project.getDescription();
		action.toggleNature(project);
		verify(description).setNatureIds(new String[] {});
	}
}
