/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.core.utils;

import java.util.ArrayList;
import java.util.List;

import polyglot.util.QuotedStringTokenizer;

/**
 * Utility methods for X10 Builder.
 * 
 * @author egeay
 */
public final class X10BuilderUtils {
  
  /**
   * Transforms the command into a succession of tokens with handling of quotes and escaping characters.
   * 
   * @param command The command to tokenize.
   * @return A non-null collection but possibly empty.
   */
  public static List<String> getAllTokens(final String command) {
    final List<String> tokens = new ArrayList<String>();
    final QuotedStringTokenizer quotedStringTokenizer = new QuotedStringTokenizer(command);
    while (quotedStringTokenizer.hasMoreTokens()) {
      tokens.add(quotedStringTokenizer.nextToken());
    }
    return tokens;
  }
  
  /**
   * Creates the include compiling option. It will create either "-I`directory`", or "-I`directory`/include" depending of
   * the flag value provided.
   * 
   * @param directory The directory to consider.
   * @param withIncludeSubDir True if we need to add "include" directory at the end, false otherwise.
   * @return A non-null non-empty string.
   */
  public static String getCompilingIncludeOpt(final String directory, final boolean withIncludeSubDir) {
    final StringBuilder sb = new StringBuilder();
    sb.append(INCLUDE_COMP_OPT).append(directory);
    if (withIncludeSubDir) {
      sb.append(INCLUDE_SUBDIR);
    }
    return sb.toString();
  }
  
  /**
   * Creates the library compiling option. It will create either "-L`directory`", or "-L`directory`/lib" depending of
   * the flag value provided.
   * 
   * @param directory The directory to consider.
   * @param withLibSubDir True if we need to add "lib" directory at the end, false otherwise.
   * @return A non-null non-empty string.
   */
  public static String getCompilingLibraryOpt(final String directory, final boolean withLibSubDir) {
    final StringBuilder sb = new StringBuilder();
    sb.append(LIB_COMP_OPT).append(directory);
    if (withLibSubDir) {
      sb.append(LIB_SUBDIR);
    }
    return sb.toString();
  }
  
  // --- Fields
  
  private static final String INCLUDE_COMP_OPT = "-I"; //$NON-NLS-1$
  
  private static final String INCLUDE_SUBDIR = "/include"; //$NON-NLS-1$
  
  private static final String LIB_COMP_OPT = "-L"; //$NON-NLS-1$
  
  private static final String LIB_SUBDIR = "/lib"; //$NON-NLS-1$

}
