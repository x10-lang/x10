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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessageManager;


final class EmptyComboInputChecker implements IFormChecker {
  
  EmptyComboInputChecker(final Combo combo, final String fieldNameInfo) {
    this.fCombo = combo;
    this.fFieldNameInfo = fieldNameInfo;
  }
  
  // --- Interface methods implementation

  public void check(final IMessageManager messageManager) {
    final String text = this.fCombo.getText().trim();
    if (this.fCombo.isEnabled() && text.length() == 0) {
      messageManager.addMessage(this.fCombo, NLS.bind(LaunchMessages.ETIC_NoEmptyContent, this.fFieldNameInfo), 
                                null /* data */, IMessageProvider.ERROR);
    } else {
      messageManager.removeMessage(this.fCombo);
    }
  }
  
  public void check(final IMessageManager messageManager, final Control control, final IManagedForm managedForm) {
    final String text = this.fCombo.getText().trim();
    if (this.fCombo.isEnabled() && text.length() == 0) {
      messageManager.addMessage(this.fCombo, NLS.bind(LaunchMessages.ETIC_NoEmptyContent, this.fFieldNameInfo), 
                                null /* data */, IMessageProvider.ERROR, control);
    } else {
      messageManager.removeMessage(this.fCombo, control);
    }
    managedForm.reflow(true);
  }
  
  // --- Fields
  
  private final Combo fCombo;
  
  private final String fFieldNameInfo;

}
