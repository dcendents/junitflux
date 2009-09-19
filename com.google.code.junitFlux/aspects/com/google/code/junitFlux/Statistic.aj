package com.google.code.junitFlux;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.code.junitFlux.Activator;
import com.google.code.junitFlux.internal.invoker.JUnitInvoker;
import com.google.code.junitFlux.internal.invoker.DelegateFactory;
import com.google.code.junitFlux.internal.invoker.ConfigurationFactory;
import com.google.code.junitFlux.builder.JUnitFluxBuilder;
import com.google.code.junitFlux.builder.JUnitTestFinder;

public aspect Statistic {
	private Map<String, List<Long>> durations = new HashMap<String, List<Long>>();
	
	pointcut builder() : 
		execution(* JUnitFluxBuilder.doBuild(..)); 
	pointcut finder() : 
		execution(* JUnitTestFinder.findTestFor(..));
	pointcut invoker() : 
		execution(* JUnitInvoker.run(..));
	pointcut delegate() :
		execution(* JUnitInvoker.invokeDelegate(..));
	pointcut delegateFactory() :
		execution(* DelegateFactory.getDelegate(..));
	pointcut configurationFactory() :
		execution(* ConfigurationFactory.getConfiguration(..));

	pointcut interestingCall() :
		builder() || finder() || invoker() || delegate() || delegateFactory() || configurationFactory();
	
	Object around() : interestingCall() {
		long start = System.currentTimeMillis();
		try {
			return proceed();
		} finally {
			long duration = System.currentTimeMillis() - start;
			if (!durations.containsKey(thisJoinPoint.toString())) {
				durations.put(thisJoinPoint.toString(), new ArrayList<Long>());
			}
			List<Long> thisDurations = durations.get(thisJoinPoint.toString());
			thisDurations.add(duration);
			Activator.getDefault().debug("Duration of "+thisJoinPoint.toShortString()+": "+duration);
			long sum = 0;
			for (Long one : thisDurations) {
				sum += one;
			}
			Activator.getDefault().debug("Average of "+thisJoinPoint.toShortString()+": "+(double) sum / (double)thisDurations.size());
		}
	}
}