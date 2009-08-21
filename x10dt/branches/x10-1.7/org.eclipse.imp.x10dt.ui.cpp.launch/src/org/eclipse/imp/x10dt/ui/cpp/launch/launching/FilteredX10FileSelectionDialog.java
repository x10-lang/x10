/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.cpp.launch.launching;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.imp.x10dt.ui.cpp.launch.LaunchMessages;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.FilteredResourcesSelectionDialog;


final class FilteredX10FileSelectionDialog extends FilteredResourcesSelectionDialog {

  FilteredX10FileSelectionDialog(final Shell shell, final IContainer container) {
    super(shell, false /* multi */, container, IResource.FILE);
  
    addListFilter(new X10ViewerFilter());
    
    setTitle(LaunchMessages.FFSD_DialogTitle);
  }
  
  // --- Private classes
  
  private static final class X10ViewerFilter extends ViewerFilter {

    // --- Abstract methods implementation
    
    public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
      IResource resource = null;
      if (element instanceof IResource) {
        resource = (IResource) element;
      } else if (element instanceof IAdaptable) {
        final IAdaptable adaptable = (IAdaptable) element;
        resource = (IResource) adaptable.getAdapter(IResource.class);
      }
      if (resource != null) {
        return resource.getName().endsWith(".x10"); //$NON-NLS-1$
      }
      return false;
    }

  }
  
}
