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
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.editor.IFormPage;


class X10FormPage extends FormPage implements IFormPage, ICompletePartListener {
  
  protected X10FormPage(final FormEditor editor, final String id, final String title) {
    super(editor, id, title);
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
      updateCompletePageStatus(isAllComplete);
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
  
  // --- Private code
  
  private void updateCompletePageStatus(final boolean isComplete) {
    this.fIsComplete = isComplete;
    for (final ICompletePageChangedListener listener : this.fListeners) {
      listener.completePageChanged(this, this.fIsComplete);
    }
  }
  
  // --- Fields
  
  private final Collection<ICompletePageChangedListener> fListeners = new ArrayList<ICompletePageChangedListener>();
  
  private boolean fIsComplete;

}
