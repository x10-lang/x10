/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.core.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Utility methods for string operations, conversions or others.
 * 
 * @author egeay
 */
public final class StringUtils {
  
  /**
   * Returns the exception trace in the form of string. If the exception transmitted as a cause object exception non-null, 
   * this will be taken as the source for the string extraction.
   * 
   * @param exception The exception to convert as a string.
   * @return A non-null string but possibly empty.
   */
  public static String getStackTrace(final Throwable exception) {
    final StringWriter strWriter = new StringWriter();
    if (exception.getCause() == null) {
      exception.printStackTrace(new PrintWriter(strWriter, true));
    } else {
      exception.getCause().printStackTrace(new PrintWriter(strWriter, true));
    }
    strWriter.flush();
    return strWriter.toString();
  }

}
