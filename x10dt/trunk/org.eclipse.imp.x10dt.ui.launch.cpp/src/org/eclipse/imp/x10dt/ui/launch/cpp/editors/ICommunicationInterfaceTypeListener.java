/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.cpp.editors;

/**
 * Listener for getting notified when end-user selects different communication interface type.
 * 
 * @author egeay
 */
public interface ICommunicationInterfaceTypeListener {
  
  /**
   * Indicates that a communication interface type has changed.
   * 
   * @param name The new communication interface type name.
   */
  public void communicationTypeChanged(final String name);

}
