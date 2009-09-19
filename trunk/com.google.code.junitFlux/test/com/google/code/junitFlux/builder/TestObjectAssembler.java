package com.google.code.junitFlux.builder;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;

import com.google.code.junitFlux.builder.JUnitFluxBuilder;
import com.google.code.junitFlux.builder.JUnitTestFinder;
import com.google.code.junitFlux.internal.invoker.JUnitInvoker;
import com.google.code.junitFlux.internal.utils.JUnitValidator;

public class TestObjectAssembler {
	
	/** returns finder that will not prove existence of Junit4 annotations or Junit3 conventions*/
	public static JUnitTestFinder getAllTestsValidFinder(IJavaProject project) {
		JUnitValidator validator = mock(JUnitValidator.class);
		when(validator.isValidTest(any(IType.class))).thenReturn(true);//ignore this
		return new JUnitTestFinder(project, validator);
	}
	
	/** returns builder that will use specified finder and invoker*/
	public static JUnitFluxBuilder getBuilder(final JUnitTestFinder finder, final JUnitInvoker pinvoker) {
		JUnitFluxBuilder builder = new JUnitFluxBuilder() {
			@Override
			protected void startupOnInitialize() {
				this.testFinder = finder;
				this.invoker = pinvoker;
			}
		};
		builder.startupOnInitialize();
		return builder;
	}
}
