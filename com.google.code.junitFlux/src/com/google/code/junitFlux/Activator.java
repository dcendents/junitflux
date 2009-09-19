package com.google.code.junitFlux;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.google.code.junitFlux.internal.utils.Dialogs;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "com.google.code.junitFlux";

	// The shared instance
	private static Activator plugin;
	
	public Activator() {
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static Activator getDefault() {
		return plugin;
	}
	
	public void log(Exception e) {
		log(e.getMessage(), e);
	}

	public void log(String message, Exception e) {
		getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, message, e));
	}

	public void debug(String message) {
		if (getDefault().isDebugging())
			getLog().log(new Status(IStatus.INFO, PLUGIN_ID, message));
	}

	public void logAndNotify(Exception e) {
		log(e);
		Dialogs.openError("JUnit Flux", "Unexpected JUnit Flux error", e);
	}
	
	public static Display getDisplay() {
		return Display.getCurrent() == null ? Display.getDefault() : Display.getCurrent();
	}
}