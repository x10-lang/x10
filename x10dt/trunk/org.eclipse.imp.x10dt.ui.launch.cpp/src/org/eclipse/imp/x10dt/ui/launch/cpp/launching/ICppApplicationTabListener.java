/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.cpp.launching;

import org.eclipse.imp.x10dt.ui.launch.cpp.platform_conf.IX10PlatformConf;

/**
 * Listener of some events for the C++ Application Launch tab.
 * 
 * @author egeay
 */
public interface ICppApplicationTabListener {
  
  /**
   * Gets notified that a platform configuration has been selected as a result of a project selection.
   * 
   * @param platformConf The platform configuration mentioned.
   */
  public void platformConfSelected(final IX10PlatformConf platformConf);

}
