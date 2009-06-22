/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.cpp.debug.utils;

import org.eclipse.ptp.core.util.BitList;
import org.eclipse.ptp.debug.core.pdi.PDIException;

import com.ibm.debug.internal.pdt.PICLDebugTarget;
import com.ibm.debug.internal.pdt.model.DebuggeeProcess;
import com.ibm.debug.internal.pdt.model.Module;
import com.ibm.debug.internal.pdt.model.Part;
import com.ibm.debug.internal.pdt.model.View;
import com.ibm.debug.internal.pdt.model.ViewFile;

/**
 * 
 * @author egeay
 */
public final class PDTUtils {
  
  public static ViewFile searchViewFile(final PICLDebugTarget target, final BitList tasks, 
                                        final DebuggeeProcess process, final String fileName) throws PDIException {
    for (final Module module : process.getModules(false)) {
      if (module != null) {
        final Part[] parts = module.getParts();
        if (parts == null || parts.length == 0) {
          continue;
        }
        for (final Part part : parts) {
          if (part != null) {
            final View view = part.getView(target.getDebugEngine().getSourceViewInformation());
            if (view != null) {
              for (final ViewFile vf : view.getViewFiles()) {
                if (vf != null) {
                  if (vf.getBaseFileName().equals(fileName)) {
                    return vf;
                  }
                }
              }
            }
          }
        }
      }
    }
    return null;
  }

}
