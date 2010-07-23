/*****************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                       *
 * All rights reserved. This program and the accompanying materials          *
 * are made available under the terms of the Eclipse Public License v1.0     *
 * which accompanies this distribution, and is available at                  *
 * http://www.eclipse.org/legal/epl-v10.html                                 *
 *****************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.core.builder;

import org.eclipse.core.runtime.IPath;
import org.eclipse.imp.x10dt.ui.launch.core.utils.IFunctor;

/**
 * Functor converting an {@link IPath} into a string.
 * 
 * @author egeay
 */
public final class CpEntryAsStringFunc implements IFunctor<IPath, String> {
  
  // --- Interface methods implementation

  public String apply(final IPath path) {
    return path.toOSString();
  }

}
