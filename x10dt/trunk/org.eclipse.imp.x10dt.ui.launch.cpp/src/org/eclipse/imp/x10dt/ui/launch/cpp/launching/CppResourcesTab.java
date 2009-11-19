/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.cpp.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.ptp.core.elements.IResourceManager;
import org.eclipse.ptp.core.elements.attributes.ResourceManagerAttributes.State;
import org.eclipse.ptp.launch.ui.ResourcesTab;


final class CppResourcesTab extends ResourcesTab {
  
  // --- Overridden methods
  
  public void initializeFrom(final ILaunchConfiguration configuration) {
    super.initializeFrom(configuration);
    if (getErrorMessage() == null) {
      final IResourceManager resourceManager = getResourceManager(configuration);
      if (resourceManager.getState() != State.STARTED) {
        try {
          resourceManager.startUp(new NullProgressMonitor());
        } catch (CoreException except) {
          // Do nothing here. It will be reported later on.
        }
      }
    }
  }

}
