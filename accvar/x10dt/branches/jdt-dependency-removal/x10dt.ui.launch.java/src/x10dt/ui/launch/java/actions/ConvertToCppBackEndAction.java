/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.java.actions;

import org.eclipse.ui.IObjectActionDelegate;

import x10dt.ui.launch.core.actions.AbstractConvertX10ProjectAction;

/**
 * Action to convert a Java back-end nature to C++ back-end nature.
 * 
 * @author egeay
 */
public final class ConvertToCppBackEndAction extends AbstractConvertX10ProjectAction implements IObjectActionDelegate {
  
  // --- Abstract methods implementation
  
  protected String getTargetBackEndType() {
    return "c++"; //$NON-NLS-1$
  }
  
}

