/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.core.project;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;


final class X10Container implements IClasspathContainer {
  
  X10Container(final IPath path, final IClasspathEntry[] classPathEntries) {
    this.fPath = path;
    this.fClassPathEntries = classPathEntries;
  }

  // --- Interface methods implementation
  
  public IClasspathEntry[] getClasspathEntries() {
    return this.fClassPathEntries;
  }

  public String getDescription() {
    return "X10 Runtime Container"; //$NON-NLS-1$
  }

  public int getKind() {
    return IClasspathContainer.K_APPLICATION;
  }

  public IPath getPath() {
    return this.fPath;
  }
  
  // --- Fields
  
  private final IPath fPath;
  
  private final IClasspathEntry[] fClassPathEntries;

}
