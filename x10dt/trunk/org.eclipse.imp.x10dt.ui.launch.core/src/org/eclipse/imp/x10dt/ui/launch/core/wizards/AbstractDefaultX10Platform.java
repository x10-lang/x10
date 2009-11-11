/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.core.wizards;


abstract class AbstractDefaultX10Platform implements IDefaultX10Platform {
  
  protected AbstractDefaultX10Platform(final boolean is64Arch) {
    this.fIs64Arch = is64Arch;
  }
  
  // --- Code for descendants
  
  protected final boolean is64Arch() {
    return this.fIs64Arch;
  }
  
  // --- Fields
  
  private final boolean fIs64Arch;

}
