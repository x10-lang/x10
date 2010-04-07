/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.cpp.editors;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.imp.x10dt.ui.launch.cpp.platform_conf.IX10PlatformConfWorkCopy;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.editor.IFormPage;


class X10FormPage extends FormPage implements IFormPage, ICompletePartListener {
  
  protected X10FormPage(final IX10PlatformConfWorkCopy x10PlatformConf, final FormEditor editor, final String id, 
                        final String title) {
    super(editor, id, title);
    this.fX10PlatformConf = x10PlatformConf;
  }
  
  // --- ICompletePartListener's interface methods implementation
  
  public final void completePartChanged(final IFormPart formPart, final boolean isComplete) {
    if (isComplete) {
      boolean isAllComplete = true;
      for (final IFormPart part : getManagedForm().getParts()) {
        if ((part != formPart) && ! ((AbstractCompleteFormPart) part).isPartComplete()) {
          isAllComplete = false;
          break;
        }
      }
      if (isAllComplete != isPageComplete()) {
        updateCompletePageStatus(isAllComplete);
      }
    } else {
      if (isPageComplete()) {
        updateCompletePageStatus(false);
      }
    }
  }
  
  // --- Public services
  
  public final void addCompletePageChangedListener(final ICompletePageChangedListener listener) {
    this.fListeners.add(listener);
  }
    
  public final boolean isPageComplete() {
    return this.fIsComplete;
  }
  
  public final void removeCompletePageChangedListener(final ICompletePageChangedListener listener) {
    this.fListeners.remove(listener);
  }
  
  // --- Code for descendants
  
  protected final IX10PlatformConfWorkCopy getPlatformConf() {
    return this.fX10PlatformConf;
  }
  
  // --- Private code
  
  private void updateCompletePageStatus(final boolean isComplete) {
    if (isComplete != this.fIsComplete) {
      this.fIsComplete = isComplete;
      for (final ICompletePageChangedListener listener : this.fListeners) {
        listener.completePageChanged(this, this.fIsComplete);
      }
    }
  }
  
  // --- Fields
  
  private final Collection<ICompletePageChangedListener> fListeners = new ArrayList<ICompletePageChangedListener>();
  
  private final IX10PlatformConfWorkCopy fX10PlatformConf;
  
  private boolean fIsComplete;

}
