package com.google.code.junitFlux.internal.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.graphics.Point;

import com.google.code.junitFlux.Activator;

/** had to write that, weird but rcp doesn't seem to have normal
 * error dialog with exception details
 */
public class Dialogs {
	
	public static void openError(final String title, final String message, Exception e) {
		final String errorDetails = Dialogs.toString(e);
		Activator.getDisplay().syncExec(new Runnable() {
			public void run() {
				Status status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, errorDetails);
				ErrorDialog dialog = new ErrorDialog(null, title, message, status, IStatus.ERROR) {
					@Override
					protected Point getInitialSize() {
						return new Point(700, 580);
					}
				};
				dialog.open();
			}
		});			
	}

	static String toString(Exception e) {
		StringWriter writer = new StringWriter();
		writer.append(e.getLocalizedMessage()+"\n");
		e.printStackTrace(new PrintWriter(writer));
		return writer.getBuffer().toString().replace("\r", "");//extra \r (on windows at least), remove it
	}
}
