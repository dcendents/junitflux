package com.google.code.junitFlux.internal.utils;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DialogsTest {
	
	@Test
	public void testExceptionDetails() throws Exception {
		String result = Dialogs.toString(new Exception("details"));
		assertTrue(result.contains("details"));
		assertTrue(result.contains("DialogsTest"));
		assertTrue(result.contains("java.lang.Exception"));
	}
}
