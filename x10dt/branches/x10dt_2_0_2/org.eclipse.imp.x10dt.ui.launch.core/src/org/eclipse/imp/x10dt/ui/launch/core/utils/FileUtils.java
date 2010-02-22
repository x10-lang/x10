/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.core.utils;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Utility methods for {@link File}.
 * 
 * @author egeay
 */
public final class FileUtils {
  
  /**
   * Collects locally the files accepting a given filter from a provided directory.
   * 
   * @param directory The directory of interest.
   * @param fileFilter The filter to use.
   * @param recurse True if one wants to recurse in the sub-directories found, false otherwise.
   * @return The list of files collected. May be empty. Never <b>null</b>.
   */
  public static Collection<File> collect(final File directory, final IFilter<File> fileFilter, final boolean recurse) {
    if (directory.isDirectory()) {
      final Collection<File> files = new LinkedList<File>();
      collect(files, directory, fileFilter, recurse);
      return files;
    } else {
      return Collections.singleton(directory);
    }
  }
  
  /**
   * Deletes a local directory whether it is empty or not.
   * 
   * @param directory The directory to delete.
   */
  public static void deleteDirectory(final File directory) {
    for (final File file : directory.listFiles()) {
      if (file.isDirectory()) {
        deleteDirectory(file);
      } else {
        file.delete();
      }
    }
    directory.delete();
  }
  
  // --- Private code
  
  private static void collect(final Collection<File> files, final File directory, final IFilter<File> fileFilter, 
                              final boolean recurse) {
    for (final File file : directory.listFiles()) {
      if (file.isDirectory()) {
        if (recurse) {
          collect(files, file, fileFilter, recurse);
        }
      } else if (fileFilter.accepts(file)) {
        files.add(file);
      }
    }
  }

}
