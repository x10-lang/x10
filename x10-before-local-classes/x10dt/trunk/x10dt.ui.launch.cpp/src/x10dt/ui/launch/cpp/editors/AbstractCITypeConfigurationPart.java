/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.editors;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.IMessageManager;


abstract class AbstractCITypeConfigurationPart implements ICITypeConfigurationPart {
  
  // --- Interface methods implementation

  public void dispose(final IMessageManager ... messageManagers) {
    for (final Control control : this.fAllControls) {
      for (final IMessageManager messageManager : messageManagers) {
        messageManager.removeMessage(control);
        messageManager.removeMessage(control, control);
      }
      control.dispose();
    }
    this.fAllControls.clear();
  }
  
  // --- Code for descendants
  
  protected final void addControl(final Control control) {
    this.fAllControls.add(control);
  }
  
  protected final void addControls(final Collection<Control> controls) {
    this.fAllControls.addAll(controls);
  }
  
  protected final Collection<Control> getCtrlsContainer() {
    return this.fAllControls;
  }
  
  // --- Fields
  
  private final Collection<Control> fAllControls = new ArrayList<Control>();

}
