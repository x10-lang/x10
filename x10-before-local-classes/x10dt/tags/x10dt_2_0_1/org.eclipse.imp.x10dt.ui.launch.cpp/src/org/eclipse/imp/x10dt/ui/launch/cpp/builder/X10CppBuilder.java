/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.cpp.builder;

import org.eclipse.imp.x10dt.ui.launch.core.builder.AbstractX10Builder;
import org.eclipse.imp.x10dt.ui.launch.core.builder.ELanguage;

/**
 * X10 builder for C++ back-end.
 * 
 * @author egeay
 */
public final class X10CppBuilder extends AbstractX10Builder {
  
  // --- Abstract methods implementation

  protected ELanguage getLanguage() {
    return ELanguage.CPP;
  }
}
