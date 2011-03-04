/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.cpp.utils;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.imp.x10dt.core.builder.X10Builder;

/**
 * Utility methods around services provided by {@link IResource}.
 * 
 * @author egeay
 */
public final class IResourceUtils {
  
  /**
   * Adds an X10 Problem marker to the resource and the different characteristics provided.
   * 
   * @param resource The resource with which the marker will be associated to.
   * @param msg The marker message.
   * @param severity The marker severity.
   * @param loc The marker location.
   * @param priority The marker priority.
   * @throws CoreException Occurs if the resource does not exists or if it is a project and it could not be open.
   */
  public static void addMarkerTo(final IResource resource, final String msg, final int severity, final String loc, 
                                 final int priority) throws CoreException {
    addMarkerTo(resource, msg, severity, loc, priority, -1, 0, 0);
  }
  
  /**
   * Adds an X10 Problem marker to the resource and the different characteristics provided.
   * 
   * @param resource The resource with which the marker will be associated to.
   * @param msg The marker message.
   * @param severity The marker severity.
   * @param loc The marker location.
   * @param priority The marker priority.
   * @param lineNum The marker line number.
   * @throws CoreException Occurs if the resource does not exists or if it is a project and it could not be open.
   */
  public static void addMarkerTo(final IResource resource, final String msg, final int severity, final String loc, 
                                 final int priority, final int lineNum) throws CoreException {
    addMarkerTo(resource, msg, severity, loc, priority, lineNum, 0, 0);
  }
  
  /**
   * Adds an X10 Problem marker to the resource and the different characteristics provided.
   * 
   * @param resource The resource with which the marker will be associated to.
   * @param msg The marker message.
   * @param severity The marker severity.
   * @param loc The marker location.
   * @param priority The marker priority.
   * @param lineNum The marker line number.
   * @param startOffset The marker start offset.
   * @param endOffset The marker end offset.
   * @throws CoreException Occurs if the resource does not exists or if it is a project and it could not be open.
   */
  public static void addMarkerTo(final IResource resource, final String msg, final int severity, final String loc, 
                                 final int priority, final int lineNum, final int startOffset, 
                                 final int endOffset) throws CoreException {
    final IMarker marker = resource.createMarker(X10Builder.PROBLEMMARKER_ID); 

    marker.setAttribute(IMarker.MESSAGE, msg);
    marker.setAttribute(IMarker.SEVERITY, severity);
    marker.setAttribute(IMarker.LOCATION, loc);
    marker.setAttribute(IMarker.PRIORITY, priority);
    if (lineNum != -1) {
      marker.setAttribute(IMarker.LINE_NUMBER, lineNum);
    }
    if (startOffset >= 0) {
      marker.setAttribute(IMarker.CHAR_START, startOffset);
      marker.setAttribute(IMarker.CHAR_END, endOffset + 1);
    }
  }

}
