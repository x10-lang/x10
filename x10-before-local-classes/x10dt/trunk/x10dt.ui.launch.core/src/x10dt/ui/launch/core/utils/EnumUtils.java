/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.core.utils;

import x10dt.ui.launch.core.platform_conf.ETargetOS;

/**
 * Utility methods around Java enumerations.
 * 
 * @author egeay
 */
public final class EnumUtils {
  
  /**
   * Returns the local target operating system as defined in the Java Runtime Environment variable "os.name".
   * 
   * @return A non-null value in all cases.
   */
  public static ETargetOS getLocalOS() {
    final String osName = System.getProperty(OS_NAME_VAR);
    if (osName.startsWith("AIX")) { //$NON-NLS-1$
      return ETargetOS.AIX;
    } else if (osName.startsWith("Linux")) { //$NON-NLS-1$
      return ETargetOS.LINUX;
    } else if (osName.startsWith("Mac")) { //$NON-NLS-1$
      return ETargetOS.MAC;
    } else if (osName.startsWith("Windows")) { //$NON-NLS-1$
      return ETargetOS.WINDOWS;
    } else {
      return ETargetOS.UNIX;
    }
  }
  
  // --- Private code
  
  private EnumUtils() {}
  
  // --- Fields

  private static final String OS_NAME_VAR = "os.name"; //$NON-NLS-1$

}
