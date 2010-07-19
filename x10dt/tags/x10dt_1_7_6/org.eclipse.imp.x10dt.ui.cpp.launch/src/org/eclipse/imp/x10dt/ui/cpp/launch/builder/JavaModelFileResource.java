/*****************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                       *
 * All rights reserved. This program and the accompanying materials          *
 * are made available under the terms of the Eclipse Public License v1.0     *
 * which accompanies this distribution, and is available at                  *
 * http://www.eclipse.org/legal/epl-v10.html                                 *
 *****************************************************************************/
package org.eclipse.imp.x10dt.ui.cpp.launch.builder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

import polyglot.frontend.Resource;

final class JavaModelFileResource implements Resource {
  
  JavaModelFileResource(final IFile file) {
    this.fFile = file;
  }
  
  // --- Interface methods implementation

  public File file() {
    return new File(this.fFile.getLocationURI());
  }

  public InputStream getInputStream() throws IOException {
    try {
      return this.fFile.getContents();
    } catch (CoreException except) {
      throw new IOException((except.getStatus() == null) ? except.getMessage() : except.getStatus().getMessage());
    }
  }

  public String name() {
    return this.fFile.getName();
  }
  
  // --- Overridden methods
  
  public String toString() {
    return this.fFile.getLocationURI().toString();
  }
  
  // --- Fields
  
  private final IFile fFile;

}
