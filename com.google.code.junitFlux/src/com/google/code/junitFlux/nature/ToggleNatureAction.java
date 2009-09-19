package com.google.code.junitFlux.nature;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import com.google.code.junitFlux.Activator;
import com.google.code.junitFlux.internal.utils.JUnitValidator;


public class ToggleNatureAction implements IObjectActionDelegate {
	
	private JUnitValidator validator = new JUnitValidator();
	private List<IProject> projects = new ArrayList<IProject>();

	public void run(IAction action) {
		try {
			for (IProject project : projects) {
				toggleNature(project);
			}
		} catch (Exception e) {
			Activator.getDefault().logAndNotify(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void selectionChanged(IAction action, ISelection selection) {
		try {
			projects = new ArrayList<IProject>();
			if (selection != null && selection instanceof IStructuredSelection) {
				for (Iterator it = ((IStructuredSelection) selection).iterator(); it.hasNext();) {
					Object element = it.next();
					projects.add(castToProject(element));
				}
				projects.remove(null);
			}
			updateActionState(action);
		} catch (Exception e) {
			Activator.getDefault().log(e);
		}
	}
	
	private void updateActionState(IAction action) throws CoreException {
		int nWithNature = 0;
		int nWithoutNature = 0;
		for (IProject project : projects) {
			if (hasNature(project)) {
				nWithNature++;
			} else if (validator.isJUnitProject(project)){
				nWithoutNature++;
			}
		}
		if (nWithNature > 0 && nWithoutNature == 0) {
			action.setEnabled(true);
			action.setText("Remove JUnit Flux Nature");
		} else if (nWithNature == 0 && nWithoutNature > 0) {
			action.setEnabled(true);
			action.setText("Add JUnit Flux Nature");
		} else {
			action.setEnabled(false);
			action.setText("Add/Remove JUnit Flux Nature");
		}
	}

	protected void toggleNature(IProject project) throws CoreException {
		if (hasNature(project)) {
			removeNature(project);
		} else {
			addNature(project);
		}
	}

	private void addNature(IProject project) throws CoreException {
		IProjectDescription description = project.getDescription();
		String[] natures = description.getNatureIds();
		String[] newNatures = new String[natures.length + 1];
		System.arraycopy(natures, 0, newNatures, 0, natures.length);
		newNatures[natures.length] = JUnitFluxNature.NATURE_ID;
		description.setNatureIds(newNatures);
		project.setDescription(description, null);
	}
	
	private void removeNature(IProject project) throws CoreException {
		IProjectDescription description = project.getDescription();
		String[] natures = description.getNatureIds();
		for (int i = 0; i < natures.length; ++i) {
			if (!JUnitFluxNature.NATURE_ID.equals(natures[i])) continue;
			String[] newNatures = new String[natures.length - 1];
			System.arraycopy(natures, 0, newNatures, 0, i);
			System.arraycopy(natures, i + 1, newNatures, i,	natures.length - i - 1);
			description.setNatureIds(newNatures);
			project.setDescription(description, null);
		}
	}
	
	private boolean hasNature(IProject project) throws CoreException {
		IProjectDescription description = project.getDescription();
		String[] natures = description.getNatureIds();
		for (int i = 0; i < natures.length; ++i) {
			if (JUnitFluxNature.NATURE_ID.equals(natures[i])) {
				return true;
			}
		}
		return false;
	}
	
	private IProject castToProject(Object element) {
		if (element instanceof IProject) 
			return (IProject) element;
		if (element instanceof IAdaptable)
			return (IProject) ((IAdaptable) element).getAdapter(IProject.class);
		return null;
	}
	
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		//no-op
	}
}