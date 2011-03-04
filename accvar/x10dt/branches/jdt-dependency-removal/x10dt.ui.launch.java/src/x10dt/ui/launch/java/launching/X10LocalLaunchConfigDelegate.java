/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.java.launching;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.imp.language.Language;
import org.eclipse.imp.language.LanguageRegistry;
import org.eclipse.imp.model.IPathEntry;
import org.eclipse.imp.model.IPathEntry.PathEntryType;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.imp.model.ModelFactory.ModelException;
import org.eclipse.imp.utils.BuildPathUtils;
import org.eclipse.jdt.internal.launching.LaunchingMessages;
import org.eclipse.jdt.internal.launching.LaunchingPlugin;
import org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.osgi.framework.Bundle;

import x10dt.ui.launch.core.Constants;
import x10dt.ui.launch.java.Activator;
import x10dt.ui.launch.java.Messages;


final class X10LocalLaunchConfigDelegate extends AbstractJavaLaunchConfigurationDelegate {

  // --- Interface methods implementation
  
  public void launch(final ILaunchConfiguration configuration, final String mode, final ILaunch launch, 
                     final IProgressMonitor monitor) throws CoreException {
    // Do nothing.
  }
  
  // --- Internal services
  
  File getX10DistHostLauncherDir(final String fileName) throws CoreException {
    final Bundle x10DistBundle = Platform.getBundle(Constants.X10_DIST_PLUGIN_ID);
    final URL url = x10DistBundle.getResource(fileName);
    try {
      return new File(FileLocator.resolve(url).getFile());
    } catch (IOException except) {
      throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.XLCD_NoLaunchersDir, except));
    }
  }
  
  File getX10DistHostLibDir() throws CoreException {
    final Bundle x10DistBundle = Platform.getBundle(Constants.X10_DIST_PLUGIN_ID);
    final URL url = x10DistBundle.getResource("lib"); //$NON-NLS-1$
    try {
      return new File(FileLocator.resolve(url).getFile());
    } catch (IOException except) {
      throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.XLCD_NoLibDir, except));
    }
  }
  
  // --- Overridden methods
  
  public String[] getClasspath(final ILaunchConfiguration configuration) throws CoreException {
    final List<String> cp = new ArrayList<String>();
        
    final File libDir = getX10DistHostLibDir();
    File commonMathFile = null;
    for (final File file : libDir.listFiles()) {
      if (file.getName().startsWith("commons-math-") && file.getName().endsWith(Constants.JAR_EXT)) { //$NON-NLS-1$
        commonMathFile = file;
        break;
      }
    }
    if (commonMathFile == null) {
      throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.XLCD_NoCommonMath));
    }
    cp.add(commonMathFile.getAbsolutePath());
    
    for (final String element : superGetClasspath(configuration)) {
      cp.add(element);
    }
    return cp.toArray(new String[cp.size()]);
  }

	public String[] superGetClasspath(ILaunchConfiguration configuration)
			throws CoreException {
		Language lang = LanguageRegistry.findLanguage("X10");
		ISourceProject jp = getJavaProjectFromConfig(configuration);
		List<String> userEntries = new ArrayList<String>(10);
		try {
			for (IPathEntry entry : jp.getResolvedBuildPath(lang, true)) {
				if (entry.getEntryType() != PathEntryType.SOURCE_FOLDER) {
					userEntries.add(entry.getRawPath().toString());
				}
			}
		} catch (ModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		IPath path = jp.getOutputLocation(lang);
		userEntries.add(jp.getRawProject().getLocation()
				.append(path.removeFirstSegments(1)).toString());
		return (String[]) userEntries.toArray(new String[userEntries.size()]);
	}

	static ISourceProject getJavaProjectFromConfig(
			ILaunchConfiguration configuration) throws CoreException {
		String projectName = configuration.getAttribute(
				IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME,
				(String) null);
		if ((projectName == null) || (projectName.trim().length() < 1)) {
			return null;
		}
		ISourceProject javaProject = BuildPathUtils.getProject(projectName);
		if (javaProject != null && javaProject.getRawProject().exists()
				&& !javaProject.getRawProject().isOpen()) {
			abort(MessageFormat.format(LaunchingMessages.JavaRuntime_28,
					new String[] { configuration.getName(), projectName }),
					IJavaLaunchConfigurationConstants.ERR_PROJECT_CLOSED, null);
		}
		if ((javaProject == null) || !javaProject.getRawProject().exists()) {
			abort(MessageFormat.format(
					LaunchingMessages.JavaRuntime_Launch_configuration__0__references_non_existing_project__1___1,
					new String[] { configuration.getName(), projectName }),
					IJavaLaunchConfigurationConstants.ERR_NOT_A_JAVA_PROJECT,
					null);
		}
		return javaProject;
	}

	static void abort(String message, int code, Throwable exception)
			throws CoreException {
		throw new CoreException(
				new Status(IStatus.ERROR,
						LaunchingPlugin.getUniqueIdentifier(), code, message,
						exception));
	}
}
