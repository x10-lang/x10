/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.core.utils;

/**
 * Listener that gets notified of the stream characters that are sent to the standard and error outputs when running a 
 * particular command in a separate process.
 * 
 * @author egeay
 */
public interface IProcessOuputListener {
  
  /**
   * Reads a line from the standard output.
   * 
   * @param line The line read.
   */
  public void read(final String line);
  
  /**
   * Reads a line from the error output.
   * 
   * @param line The line read.
   */
  public void readError(final String line);

}
