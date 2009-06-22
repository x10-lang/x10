/*
 * Created on Feb 6, 2006
 */
package org.eclipse.imp.x10dt.core.wizards;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.imp.builder.ProjectNatureBase;
import org.eclipse.imp.wizards.NewProjectWizardSecondPage;
import org.eclipse.imp.x10dt.core.builder.X10ProjectNature;
import org.osgi.framework.Bundle;

public class X10ProjectWizardSecondPage extends NewProjectWizardSecondPage {
    public X10ProjectWizardSecondPage(X10ProjectWizardFirstPage firstPage) {
	super(firstPage);
    }

    protected ProjectNatureBase getProjectNature() {
	return new X10ProjectNature();
    }

    protected IPath getLanguageRuntimePath() {
	Bundle x10RuntimeBundle= Platform.getBundle("x10.runtime");
	String bundleVersion= (String) x10RuntimeBundle.getHeaders().get("Bundle-Version");
	IPath x10RuntimePath= new Path("ECLIPSE_HOME/plugins/x10.runtime_" + bundleVersion + ".jar");

	return x10RuntimePath;
    }
}
