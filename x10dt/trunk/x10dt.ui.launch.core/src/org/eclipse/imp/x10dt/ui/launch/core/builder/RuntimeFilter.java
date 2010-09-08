/*****************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                       *
 * All rights reserved. This program and the accompanying materials          *
 * are made available under the terms of the Eclipse Public License v1.0     *
 * which accompanies this distribution, and is available at                  *
 * http://www.eclipse.org/legal/epl-v10.html                                 *
 *****************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.core.builder;

import org.eclipse.core.runtime.IPath;
import org.eclipse.imp.x10dt.ui.launch.core.utils.IFilter;

/**
 * Filter selecting the "x10.runtime" packaged plugin.
 * 
 * @author egeay
 */
public final class RuntimeFilter implements IFilter<IPath> {
  
  // --- Interface methods implementation

  public boolean accepts(final IPath path) {
    return path.toOSString().contains(X10_RUNTIME);
  }
  
  // --- Fields
  
  private static final String X10_RUNTIME = "x10.runtime"; //$NON-NLS-1$

}
