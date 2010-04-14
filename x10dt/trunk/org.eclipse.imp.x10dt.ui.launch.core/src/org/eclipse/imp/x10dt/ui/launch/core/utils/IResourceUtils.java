/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.core.utils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.imp.x10dt.core.builder.X10Builder;
import org.eclipse.imp.x10dt.ui.launch.core.LaunchCore;

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
   */
  public static void addBuildMarkerTo(final IResource resource, final String msg, final int severity, final String loc, 
                                      final int priority) {
    createMarker(X10Builder.PROBLEMMARKER_ID, resource, msg, severity, loc, priority, -1, 0, 0);
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
   */
  public static void addBuildMarkerTo(final IResource resource, final String msg, final int severity, final String loc, 
                                      final int priority, final int lineNum) {
    createMarker(X10Builder.PROBLEMMARKER_ID, resource, msg, severity, loc, priority, lineNum, 0, 0);
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
   */
  public static void addBuildMarkerTo(final IResource resource, final String msg, final int severity, final String loc, 
                                      final int priority, final int lineNum, final int startOffset, 
                                      final int endOffset) {
  	createMarker(X10Builder.PROBLEMMARKER_ID, resource, msg, severity, loc, priority, lineNum, startOffset, endOffset);
  }
  
  /**
   * Adds a marker for a platform configuration file with the different parameters provided.
   * 
   * @param file The platform configuration file to consider.
   * @param msg The The marker message.
   * @param severity The marker severity.
   * @param priority The marker priority.
   */
  public static void addPlatformConfMarker(final IFile file, final String msg, final int severity, 
                                           final int priority) {
  	createMarker(PLATFORM_CONF_MARKER_ID, file, msg, severity, file.getLocation().toString(), priority, -1, 0, 0);
  }
  
  /**
   * Adds a marker for a platform configuration file with the different parameters provided.
   * 
   * @param file The platform configuration file to consider.
   * @param msg The The marker message.
   * @param severity The marker severity.
   * @param priority The marker priority.
   * @param loc The marker location.
   * @param lineNum The marker line number.
   */
  public static void addPlatformConfMarker(final IFile file, final String msg, final int severity, final int priority, 
                                           final String loc, final int lineNum) {
  	createMarker(PLATFORM_CONF_MARKER_ID, file, msg, severity, loc, priority, lineNum, 0, 0);
  }
  
  /**
   * Adds a marker for a platform configuration file with the different parameters provided.
   * 
   * @param file The platform configuration file to consider.
   * @param msg The The marker message.
   * @param severity The marker severity.
   * @param priority The marker priority.
   * @param loc The marker location.
   * @param lineNum The marker line number.
   * @param startOffset The marker start offset.
   * @param endOffset The marker end offset.
   */
  public static void addPlatformConfMarker(final IFile file, final String msg, final int severity, final int priority,
                                           final String loc, final int lineNum, final int startOffset,
                                           final int endOffset) {
  	createMarker(PLATFORM_CONF_MARKER_ID, file, msg, severity, loc, priority, lineNum, startOffset, endOffset);
  }
  
  /**
   * Deletes all the X10 build markers for a given project.
   * 
   * @param project The project to consider.
   */
  public static void deleteBuildMarkers(final IProject project) {
  	try {
			project.deleteMarkers(X10Builder.PROBLEMMARKER_ID, true /* includeSubtypes */, IResource.DEPTH_INFINITE);
		} catch (CoreException except) {
			LaunchCore.log(except.getStatus());
		}
  }
  
  /**
   * Deletes all the X10 build markers for a given file.
   * 
   * @param file The file to consider.
   */
  public static void deleteBuildMarkers(final IFile file) {
  	try {
			file.deleteMarkers(X10Builder.PROBLEMMARKER_ID, false /* includeSubtypes */, IResource.DEPTH_ZERO);
		} catch (CoreException except) {
			LaunchCore.log(except.getStatus());
		}
  }
  
  /**
   * Deletes all the X10 platform configuration markers for a given project.
   * 
   * @param file The platform configuration file to consider.
   */
  public static void deletePlatformConfMarkers(final IFile file) {
  	try {
			file.deleteMarkers(PLATFORM_CONF_MARKER_ID, false /* includeSubtypes */, IResource.DEPTH_ZERO);
		} catch (CoreException except) {
			LaunchCore.log(except.getStatus());
		}
  }
  
  /**
   * Returns all the X10 build markers for a given project.
   * 
   * @param project The project to consider.
   * @return A non-null array but possibly empty.
   * @throws CoreException Occurs if the project does not exist or is not open.
   */
  public static IMarker[] getBuildMarkers(final IProject project) throws CoreException {
  	return project.findMarkers(X10Builder.PROBLEMMARKER_ID, true /* includeSubtypes */, IResource.DEPTH_INFINITE);
  }
  
  /**
   * Returns all the X10 platform configuration markers for a given project.
   * 
   * @param file The platform configuration file to consider.
   * @return A non-null array but possibly empty.
   * @throws CoreException Occurs if the file does not exist or is within a project not open.
   */
  public static IMarker[] getPlatformConfMarkers(final IFile file) throws CoreException {
  	return file.findMarkers(PLATFORM_CONF_MARKER_ID, false/* includeSubtypes */, IResource.DEPTH_ZERO);
  }
  
  /**
   * Returns the number of X10 build error markers for a given project.
   * 
   * @param project The project to consider.
   * @return A natural number.
   */
  public static int getNumberOfBuildErrorMarkers(final IProject project) {
  	int errorCount = 0;
  	try {
			for (final IMarker marker : getBuildMarkers(project)) {
				if (marker.getAttribute(IMarker.SEVERITY, -1) == IMarker.SEVERITY_ERROR) {
					++errorCount;
				}
			}
			return errorCount;
		} catch (CoreException except) {
			LaunchCore.log(except.getStatus());
			return -1;
		}
  }
  
  /**
   * Returns the number of X10 platform configuration error markers for a given project.
   * 
   * @param file The platform configuration file to consider.
   * @return A natural number.
   */
  public static int getNumberOfPlatformConfErrorMarkers(final IFile file) {
  	int errorCount = 0;
  	try {
			for (final IMarker marker : getPlatformConfMarkers(file)) {
				if (marker.getAttribute(IMarker.SEVERITY, -1) == IMarker.SEVERITY_ERROR) {
					++errorCount;
				}
			}
			return errorCount;
		} catch (CoreException except) {
			LaunchCore.log(except.getStatus());
			return -1;
		}
  }
  
  // --- Private code
  
  private static void createMarker(final String markerId, final IResource resource, final String msg, final int severity,
                                   final String loc, final int priority, final int lineNum, final int startOffset, 
                                   final int endOffset) {
  	try {
			final IMarker marker = resource.createMarker(markerId); 

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
		} catch (CoreException except) {
			LaunchCore.log(except.getStatus());
		}
  }
  
  // --- Fields
  
  private static final String PLATFORM_CONF_MARKER_ID = "org.eclipse.imp.x10dt.ui.launch.cpp.platformConfMarker"; //$NON-NLS-1$

}
