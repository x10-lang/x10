/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.editors; 
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.ui.forms.IFormPart;


abstract class AbstractCompleteFormPart implements IFormPart {

  // --- Internal services
  
  final void addCompletePartListener(final ICompletePartListener listener) {
    this.fListeners.add(listener);
  }
  
  boolean isPartComplete() {
    return this.fIsComplete;
  }
  
  final void removeCompletePartListener(final ICompletePartListener listener) {
    this.fListeners.remove(listener);
  }
  
  // --- Code for descendants
  
  protected final void setPartCompleteFlag(final boolean isComplete) {
    this.fIsComplete = isComplete;
    for (final ICompletePartListener listener : this.fListeners) {
      listener.completePartChanged(this, isComplete);
    }
  }
  
  // --- Fields
  
  private boolean fIsComplete;

  private final Collection<ICompletePartListener> fListeners = new ArrayList<ICompletePartListener>();
  
}
