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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.imp.x10dt.ui.launch.core.LaunchCore;
import org.eclipse.imp.x10dt.ui.launch.core.Messages;
import org.eclipse.imp.x10dt.ui.launch.core.builder.ELanguage;
import org.eclipse.imp.x10dt.ui.launch.core.builder.ICompilerX10ExtInfo;
import org.eclipse.imp.x10dt.ui.launch.core.platform_conf.EArchitecture;
import org.eclipse.osgi.util.NLS;

import polyglot.util.QuotedStringTokenizer;

/**
 * Utility methods for X10 Builder.
 * 
 * @author egeay
 */
public final class X10BuilderUtils {
  
  /**
   * Creates the compiler extension info from the extension registry.
   * 
   * @param language The language to consider for the compiler.
   * @return A non-null instance of {@link ICompilerX10ExtInfo}.
   * @throws CoreException Occurs if we could not find the class for the language in the extension registry.
   */
  public static ICompilerX10ExtInfo createCompilerX10ExtInfo(final ELanguage language) throws CoreException {
    final IExtensionPoint point = Platform.getExtensionRegistry().getExtensionPoint(COMP_EXT_INFO_EXTENSION_POINT_ID);
   
    IConfigurationElement targetConfigElement = null;
    final String langName = language.name();
    for (final IConfigurationElement configElement : point.getConfigurationElements()) {
      if (configElement.getAttribute(LANGUAGE_EP_ATTR).equals(langName)) {
        targetConfigElement = configElement;
        break;
      }
    }
    if (targetConfigElement == null) {
      throw new CoreException(new Status(IStatus.ERROR, LaunchCore.PLUGIN_ID, 
                                         NLS.bind(Messages.XBU_NoCheckerForLanguage, language)));
    } else {
      final Object clazz = targetConfigElement.createExecutableExtension(CLASS_EP_ATTR);
      return (ICompilerX10ExtInfo) clazz;
    }
  }
  
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
   * Returns the {@link EArchitecture} instance for a given architecture name.
   * 
   * @param architecture The architecture name for which one wants the associated EArchitecture.
   * @return A non-null value.
   * @throws AssertionError Occurs if the architecture name given is not in the {@link EArchitecture} enumeration.
   */
  public static EArchitecture getArchitecture(final String architecture) {
    for (final EArchitecture arch : EArchitecture.values()) {
      if (arch.name().equals(architecture)) {
        return arch;
      }
    }
    throw new AssertionError(NLS.bind(Messages.XBU_ArchNameNotInEnum, architecture));
  }
  
  // --- Private code
  
  private X10BuilderUtils() {}
  
  // --- Fields
  
  private static final String COMP_EXT_INFO_EXTENSION_POINT_ID = "org.eclipse.imp.x10dt.ui.launch.core.x10_compiler_extinfo"; //$NON-NLS-1$
  
  private static final String CLASS_EP_ATTR = "class"; //$NON-NLS-1$
  
  private static final String LANGUAGE_EP_ATTR = "language"; //$NON-NLS-1$
  
}
