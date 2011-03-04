/*
 * Created on Feb 6, 2006
 */
package com.ibm.watson.safari.x10.wizards;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.uide.core.ProjectNatureBase;
import org.eclipse.uide.wizards.NewProjectWizardSecondPage;
import org.osgi.framework.Bundle;

import com.ibm.watson.safari.x10.builder.X10ProjectNature;

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
