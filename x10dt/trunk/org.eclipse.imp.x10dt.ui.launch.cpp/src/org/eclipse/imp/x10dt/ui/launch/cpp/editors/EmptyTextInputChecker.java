/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.cpp.editors;

import org.eclipse.imp.x10dt.ui.launch.cpp.LaunchMessages;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessageManager;


final class EmptyTextInputChecker implements IFormChecker {
  
  EmptyTextInputChecker(final Text text, final String fieldNameInfo) {
    this.fText = text;
    this.fFieldNameInfo = fieldNameInfo;
  }
  
  // --- Interface methods implementation

  public void check(final IMessageManager messageManager) {
    final String text = this.fText.getText().trim();
    if (this.fText.isEnabled() && text.length() == 0) {
      messageManager.addMessage(this.fText, NLS.bind(LaunchMessages.ETIC_NoEmptyContent, this.fFieldNameInfo), 
                                null /* data */, IMessageProvider.ERROR);
    } else {
      messageManager.removeMessage(this.fText);
    }
  }
  
  public void check(final IMessageManager messageManager, final Control control, final IManagedForm managedForm) {
    final String text = this.fText.getText().trim();
    if (this.fText.isEnabled() && text.length() == 0) {
      messageManager.addMessage(this.fText, NLS.bind(LaunchMessages.ETIC_NoEmptyContent, this.fFieldNameInfo), 
                                null /* data */, IMessageProvider.ERROR, control);
    } else {
      messageManager.removeMessage(this.fText, control);
    }
  }
  
  // --- Fields
  
  private final Text fText;
  
  private final String fFieldNameInfo;

}
