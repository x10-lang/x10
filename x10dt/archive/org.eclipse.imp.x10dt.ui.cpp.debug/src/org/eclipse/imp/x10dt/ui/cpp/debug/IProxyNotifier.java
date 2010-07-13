/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.cpp.debug;

import org.eclipse.ptp.proxy.debug.event.IProxyDebugEvent;

/**
 * Notifies debug event(s) from internal engine to PTP.
 * 
 * @author egeay
 */
public interface IProxyNotifier {
  
  public void notify(final IProxyDebugEvent debugEvent);

}
