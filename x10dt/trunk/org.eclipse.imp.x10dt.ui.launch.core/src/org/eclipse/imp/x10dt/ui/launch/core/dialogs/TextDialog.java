/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.core.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;


final class TextDialog extends Dialog {

  protected TextDialog(final Shell parentShell, final String dialogTitle, final String text) {
    super(parentShell);
    setShellStyle(SWT.DIALOG_TRIM | getDefaultOrientation());
    this.fDialogTitle = dialogTitle;
    this.fText = text;
  }
  
  // --- Overridden methods
  
  protected Control createButtonBar(Composite parent) {
    final Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(new GridLayout(1, false));
    composite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    composite.setFont(parent.getFont());
    
    final Button okButton = createButton(composite, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
    final GridData data = (GridData) okButton.getLayoutData();
    data.horizontalAlignment = SWT.CENTER;
    data.grabExcessHorizontalSpace = true;    
    return composite;
  }
  
  protected void configureShell(Shell shell) {
    super.configureShell(shell); 
    shell.setText(this.fDialogTitle);
  }
  
  protected Control createDialogArea(Composite parent) {
    final Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(new GridLayout(1, false));
    composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    composite.setFont(parent.getFont());
    
    final StyledText styledText = new StyledText(composite, SWT.BORDER | SWT.WRAP | SWT.FULL_SELECTION | SWT.H_SCROLL | 
                                                 SWT.V_SCROLL);
    final GridData styledTextGd = new GridData(SWT.FILL, SWT.FILL, true, true);
    final Point shellSize = getShell().getSize();
    styledTextGd.minimumWidth = shellSize.x / 2;
    styledTextGd.minimumHeight = shellSize.y / 2;
    styledText.setLayoutData(styledTextGd);
    styledText.setBackground(getShell().getDisplay().getSystemColor (SWT.COLOR_INFO_BACKGROUND));
    styledText.setText(this.fText);
    
    return composite;
  }
  
  protected boolean isResizable() {
    return true;
  }
  
  // --- Fields
  
  private final String fDialogTitle;
  
  private final String fText;

}
