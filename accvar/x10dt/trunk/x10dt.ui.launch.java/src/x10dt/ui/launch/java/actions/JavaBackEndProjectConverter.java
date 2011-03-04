/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.java.actions;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.window.IShellProvider;

import x10dt.core.X10DTCorePlugin;
import x10dt.ui.launch.core.actions.IBackEndX10ProjectConverter;
import x10dt.ui.launch.java.Activator;

/**
 * Implementation of {@link IBackEndX10ProjectConverter} when converting an X10 project to use the Java back-end.
 * 
 * @author egeay
 */
public final class JavaBackEndProjectConverter implements IBackEndX10ProjectConverter {

  // --- Interface methods implementation
  
  public String getProjectNatureId() {
    return X10DTCorePlugin.X10_PRJ_JAVA_NATURE_ID;
  }
  
  public void postProjectSetup(final IShellProvider shellProvider, final IProject project) {
    final IJavaProject javaProject = JavaCore.create(project);
    final Collection<IClasspathEntry> cpEntries = new ArrayList<IClasspathEntry>();
    try {
      boolean foundEntry = false;
      for (final IClasspathEntry cpEntry : javaProject.getRawClasspath()) {
        if (JavaRuntime.JRE_CONTAINER.equals(cpEntry.getPath().toString())) {
          foundEntry = true;
        } else {
          cpEntries.add(cpEntry);
        }
      }
      if (! foundEntry) {
        cpEntries.add(JavaCore.newContainerEntry(new Path(JavaRuntime.JRE_CONTAINER)));
        javaProject.setRawClasspath(cpEntries.toArray(new IClasspathEntry[cpEntries.size()]), new NullProgressMonitor());
      }
    } catch (JavaModelException except) {
      Activator.getDefault().getLog().log(except.getStatus());
    }
  }

  public void preProjectSetup(final IShellProvider shellProvider, final IProject project) {
    // Nothing to do.
  }

}
