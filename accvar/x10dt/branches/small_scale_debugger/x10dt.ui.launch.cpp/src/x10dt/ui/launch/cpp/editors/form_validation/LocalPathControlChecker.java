/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.editors.form_validation;

import java.io.File;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.editor.IFormPage;

import x10dt.ui.launch.cpp.LaunchMessages;


final class LocalPathControlChecker extends AbstractFormControlChecker implements IFormControlChecker {

  LocalPathControlChecker(final IFormPage formPage, final Control control, final String controlInfo) {
    super(formPage, control);
    this.fControlInfo = controlInfo;
  }
  
  // --- Interface methods implementation

  public boolean validate(final String text) {
    removeMessages();
    if (getControl().isEnabled()) {
      final File file = new File(text);
      if (file.exists()) {
        return true;
      } else {
        addMessages(NLS.bind(LaunchMessages.LPCC_NonExistentPath, this.fControlInfo), IMessageProvider.ERROR);
        return false;
      }
    } else {
      return true;
    }
  }
  
  // --- Fields
  
  private final String fControlInfo;

}
