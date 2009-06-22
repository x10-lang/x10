/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.cpp.debug.view;

import org.eclipse.debug.ui.AbstractDebugView;
import org.eclipse.debug.ui.IDebugView;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;

/**
 * Defines the view to show the registered clocks for a given X10 activity.
 * 
 * @author egeay
 */
public final class ClocksView extends AbstractDebugView implements IDebugView {

  // --- Abstract methods implementation
  
  protected void configureToolBar(final IToolBarManager toolbarManager) {
  }

  protected void createActions() {
  }

  protected Viewer createViewer(final Composite parent) {
    return null;
  }

  protected void fillContextMenu(final IMenuManager menuManager) {
  }

  protected String getHelpContextId() {
    return null;
  }

}
