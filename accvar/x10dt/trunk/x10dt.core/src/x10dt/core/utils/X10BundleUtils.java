/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.core.utils;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.osgi.framework.Bundle;

import x10dt.core.Messages;
import x10dt.core.X10DTCorePlugin;

/**
 * Utility methods for the management of X10 bundles.
 * 
 * @author egeay
 */
public final class X10BundleUtils {

  /**
   * X10 Runtime Bundle unique id.
   */
  public static final String X10_RUNTIME_BUNDLE_ID = "x10.runtime"; //$NON-NLS-1$

  /**
   * X10 Common Bundle unique id.
   */
  public static final String X10_COMMON_BUNDLE_ID = "x10.common"; //$NON-NLS-1$

  /**
   * X10 Constraints Bundle unique id.
   */
  public static final String X10_CONSTRAINTS_BUNDLE_ID = "x10.constraints"; //$NON-NLS-1$
  
  /**
   * X10 Dist Host Bundle unique id.
   */
  public static final String X10_DIST_HOST_BUNDLE_ID = "x10.dist.host"; //$NON-NLS-1$

  // --- Public services

  /**
   * Returns a Java native URL for the X10 Common bundle location.
   * 
   * @return A non-null URL identifying the bundle location.
   * @throws CoreException Occurs if we could not find the bundle or resolve the URL returned by Equinox.
   */
  public static URL getX10CommonURL() throws CoreException {
    return getBundleResourceURL(X10_COMMON_BUNDLE_ID, OUTPUT_DIR);
  }

  /**
   * Returns a Java native URL for the X10 Constraints bundle location.
   * 
   * @return A non-null URL identifying the bundle location.
   * @throws CoreException Occurs if we could not find the bundle or resolve the URL returned by Equinox.
   */
  public static URL getX10ConstraintsURL() throws CoreException {
    return getBundleResourceURL(X10_CONSTRAINTS_BUNDLE_ID, OUTPUT_DIR);
  }
  
  /**
   * Returns a Java native URL for the resource location present within X10 Dist Host bundle.
   * 
   * @param resourcePath The resource path to look for relative to X10 Dist Host location.
   * @return A non-null URL identifying the resource location.
   * @throws IOException Occurs if the URL resolution failed.
   */
  public static URL getX10DistHostResource(final String resourcePath) throws IOException {
    final Bundle x10DistBundle = Platform.getBundle(X10_DIST_HOST_BUNDLE_ID);
    final URL url = x10DistBundle.getResource(resourcePath);
    return FileLocator.resolve(url);
  }

  /**
   * Returns a Java native URL for the X10 Runtime bundle location.
   * 
   * <p>
   * Note that the URL resolved takes into a development vs deployment mode for X10DT. In deployment mode this will resolve to
   * "x10.runtime" jar location, while in development mode this will resolve to "x10.jar" location. In the latter, the method
   * may return <b>null</b> if we can find "x10.jar".
   * 
   * @return May return <b>null</b> in development mode if we can't find "x10.jar", otherwise a non-null URL.
   * @throws CoreException Occurs if we could not find the bundle or resolve the URL returned by Equinox.
   */
  public static URL getX10RuntimeURL() throws CoreException {
    final Bundle runtimeBundle = Platform.getBundle(X10_RUNTIME_BUNDLE_ID);
    final URL x10JarURL = runtimeBundle.getResource(X10_JAR_LOC);
    if (x10JarURL == null) {
      return getBundleResourceURL(X10_RUNTIME_BUNDLE_ID, OUTPUT_DIR);
    } else {
      return resolveURL(x10JarURL);
    }
  }

  /**
   * Indicates if the X10 Runtime URL given is encountered at deployment time.
   * 
   * <p>
   * Simply calls {@link #isDeployedX10Runtime(String)}.
   * 
   * @param x10RuntimeURL The X10 Runtime URL to consider.
   * @return True if it is defined only at deployment time, false otherwise.
   */
  public static boolean isDeployedX10Runtime(final URL x10RuntimeURL) {
    return isDeployedX10Runtime(x10RuntimeURL.getPath());
  }

  /**
   * Indicates if the X10 Runtime path given is encountered at deployment time.
   * 
   * @param x10RuntimePath The X10 Runtime path to consider.
   * @return True if it is defined only at deployment time, false otherwise.
   */
  public static boolean isDeployedX10Runtime(final String x10RuntimePath) {
    final String lastSegment = x10RuntimePath.substring(x10RuntimePath.lastIndexOf('/') + 1);
    return lastSegment.contains(X10_RUNTIME_BUNDLE_ID);
  }

  // --- Private code

  private X10BundleUtils() {
  }

  private static URL getBundleResourceURL(final String bundleName, final String folder) throws CoreException {
    final Bundle bundle = Platform.getBundle(bundleName);
    if (bundle == null) {
      throw new CoreException(new Status(IStatus.ERROR, X10DTCorePlugin.kPluginID, NLS.bind(Messages.XCCI_BundleNotFoundError,
                                                                                            bundleName)));
    } else {
      URL wURL = bundle.getResource(folder);
      if (wURL == null) {
        // We access the root of the jar where the resources should be located.
        wURL = bundle.getResource(""); //$NON-NLS-1$
      }
      return resolveURL(wURL);
    }
  }

  private static URL resolveURL(final URL url) throws CoreException {
    try {
      final URL resolvedURL = FileLocator.resolve(url);
      String resolvedURLPath = resolvedURL.getPath();
      if (resolvedURLPath.startsWith("file:")) { //$NON-NLS-1$
        if (resolvedURLPath.endsWith("!/")) { //$NON-NLS-1$
          resolvedURLPath = resolvedURLPath.substring(0, resolvedURLPath.length() - 2);
        }
        return new URL(resolvedURLPath);
      } else {
        return resolvedURL;
      }
    } catch (Exception except) {
      throw new CoreException(new Status(IStatus.ERROR, X10DTCorePlugin.kPluginID, Messages.XBU_URLResolutionError, except));
    }
  }

  // --- Fields

  private static final String OUTPUT_DIR = "classes"; //$NON-NLS-1$

  private static final String X10_JAR_LOC = "src-java/gen/x10.jar"; //$NON-NLS-1$

}
