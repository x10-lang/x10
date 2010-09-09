/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.cpp.debug.pdi;

import java.util.Observable;

import org.eclipse.imp.x10dt.ui.cpp.debug.IProxyNotifier;
import org.eclipse.ptp.proxy.debug.event.IProxyDebugEvent;


final class ProxyNotifier extends Observable implements IProxyNotifier {

  // --- Interface methods implementation
  
  public void notify(final IProxyDebugEvent debugEvent) {
    setChanged();
    notifyObservers(debugEvent);
  }

}
