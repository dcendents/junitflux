package com.google.code.junitFlux.internal.invoker;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IType;
import org.junit.Test;

import com.google.code.junitFlux.PluginTest;

public class DelegateFactoryTest extends PluginTest {
	private DelegateFactory factory = new DelegateFactory();
	
	@Test
	/** replace test specification with filename specification (@see DelegateFactory)*/
	public void testReplacement() throws CoreException {
		String testName = "test.test1";
		IType test1 = mock(IType.class);
		when(test1.getFullyQualifiedName()).thenReturn(testName);
		List<IType> tests = Arrays.asList(new IType[] {test1});
		List<String> arguments = new ArrayList<String>();
		factory.addTestSpecification(arguments,  tests);
		assertEquals(2, arguments.size());
		assertEquals("-testNameFile", arguments.get(0));
		int lineSeparatorLength = System.getProperty("line.separator").length();
		assertEquals(testName.length()+lineSeparatorLength, new File(arguments.get(1)).length());
	}
}