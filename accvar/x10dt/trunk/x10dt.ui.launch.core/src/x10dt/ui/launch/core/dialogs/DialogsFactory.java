/*******************************************************************************
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.core.dialogs;


/**
 * Factory methods to create some useful dialogs for X10.
 * 
 * @author egeay
 */
public final class DialogsFactory {
  
  /**
   * Creates the builder that can allow the creation of an general error dialog.
   * 
   * @return A non-null instance.
   */
  public static IErrorDialogBuilder createErrorBuilder() {
    return new ErrorDialogBuilder();
  }
  
  // --- Private code
  
  private DialogsFactory() {}
  
}
