/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.core.utils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.osgi.util.NLS;

import polyglot.util.QuotedStringTokenizer;
import x10dt.ui.launch.core.Messages;
import x10dt.ui.launch.core.platform_conf.EBitsArchitecture;

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
   * Returns the {@link EBitsArchitecture} instance for a given architecture name.
   * 
   * @param architecture The architecture name for which one wants the associated EArchitecture.
   * @return A non-null value.
   * @throws AssertionError Occurs if the architecture name given is not in the {@link EBitsArchitecture} enumeration.
   */
  public static EBitsArchitecture getArchitecture(final String architecture) {
    for (final EBitsArchitecture arch : EBitsArchitecture.values()) {
      if (arch.name().equals(architecture)) {
        return arch;
      }
    }
    throw new AssertionError(NLS.bind(Messages.XBU_ArchNameNotInEnum, architecture));
  }
  
  // --- Private code
  
  private X10BuilderUtils() {}
  
}
