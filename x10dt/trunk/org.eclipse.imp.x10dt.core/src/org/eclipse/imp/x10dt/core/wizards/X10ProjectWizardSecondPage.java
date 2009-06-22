/*
 * Created on Feb 6, 2006
 */
package com.ibm.watson.safari.x10.wizards;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.eclipse.jdt.internal.ui.util.CoreUtility;
import org.eclipse.jdt.internal.ui.wizards.ClassPathDetector;
import org.eclipse.jdt.internal.ui.wizards.NewWizardMessages;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jdt.ui.wizards.JavaCapabilityConfigurationPage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.uide.core.ProjectNatureBase;
import org.eclipse.uide.wizards.NewProjectWizardSecondPage;
import org.osgi.framework.Bundle;

import com.ibm.watson.safari.x10.X10Plugin;
import com.ibm.watson.safari.x10.builder.X10Builder;
import com.ibm.watson.safari.x10.builder.X10ProjectNature;
import com.ibm.watson.safari.x10.preferences.X10Preferences;

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
