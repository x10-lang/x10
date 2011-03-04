/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.tests.services.swbot.conditions;

import org.eclipse.osgi.util.NLS;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotWorkbenchPart;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.waits.ICondition;
import org.eclipse.ui.IWorkbenchPartReference;


final class WorkbenchPartIsActiveCondition<T extends IWorkbenchPartReference> extends DefaultCondition implements ICondition {
  
  WorkbenchPartIsActiveCondition(final SWTBotWorkbenchPart<T> workbenchPart) {
    this.fWorkbenchPart = workbenchPart;
  }

  // --- Interface methods implementation
  
  public boolean test() throws Exception {
    return this.fWorkbenchPart.isActive();
  }

  public String getFailureMessage() {
    return NLS.bind("The workbench part ''{0}'' was not activated.", this.fWorkbenchPart); //$NON-NLS-1$
  }
  
  // --- Fields
  
  private final SWTBotWorkbenchPart<T> fWorkbenchPart;

}
