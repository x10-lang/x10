/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.editors.form_validation;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.editor.IFormPage;

import x10dt.ui.launch.cpp.LaunchMessages;
import x10dt.ui.launch.cpp.builder.target_op.ITargetOpHelper;

final class ValidPathControlChecker extends AbstractFormControlChecker implements IFormControlChecker {

  protected ValidPathControlChecker(final ITargetOpHelper targetOpHelper, final IFormPage formPage, final Control control,
                                    final String controlInfo) {
    super(formPage, control);
    this.fTargetOpHelper = targetOpHelper;
    this.fControlInfo = controlInfo;
  }

  // --- Interface methods implementation

  public boolean validate(final String path) {
    removeMessages();
    if (getControl().isEnabled()) {
      if (path.length() == 0) {
        addMessages(NLS.bind(LaunchMessages.ETIC_NoEmptyContent, this.fControlInfo), IMessageProvider.ERROR);
        return false;
      } else if (! validatePath(path)) {
        addMessages(NLS.bind(LaunchMessages.LPCC_NonExistentPath, this.fControlInfo), IMessageProvider.ERROR);
        return false;
      }
    }
    return true;
  }

  private boolean validatePath(final String path) {
    return this.fTargetOpHelper.getStore(path).fetchInfo().exists();
  }

  // --- Overridden methods

  public boolean equals(final Object rhs) {
    if (! (rhs instanceof ValidPathControlChecker)) {
      return false;
    }
    return getControl().equals(((AbstractFormControlChecker) rhs).getControl());
  }

  public int hashCode() {
    return getControl().hashCode();
  }

  // --- Fields

  private final String fControlInfo;

  private final ITargetOpHelper fTargetOpHelper;
}
