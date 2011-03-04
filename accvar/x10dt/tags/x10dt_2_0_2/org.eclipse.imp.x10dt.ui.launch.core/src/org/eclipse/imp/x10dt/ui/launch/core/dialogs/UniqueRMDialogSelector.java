/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.core.dialogs;

import java.util.List;

import org.eclipse.imp.x10dt.ui.launch.core.Messages;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.ptp.core.elements.IResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


final class UniqueRMDialogSelector extends Dialog {

  protected UniqueRMDialogSelector(final Shell parentShell, final List<IResourceManager> resourceManagers) {
    super(parentShell);
    this.fResourceManagers = resourceManagers;
  }
  
  // --- Overridden methods
  
  protected void configureShell(Shell shell) {
    super.configureShell(shell);
    shell.setText(Messages.URMDS_RMSelectionDialogTitle);
  }
  
  protected Control createButtonBar(final Composite parent) {
    final Composite composite = new Composite(parent, SWT.NONE);
    // Create a layout with spacing and margins appropriate for the font size.
    final GridLayout layout = new GridLayout();
    layout.numColumns = 0; // this is incremented by createButton
    layout.makeColumnsEqualWidth = true;
    layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
    layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
    layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
    layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
    composite.setLayout(layout);
    composite.setLayoutData(new GridData(SWT.CENTER, SWT.NONE, true, false));
    composite.setFont(parent.getFont());
    
    createButton(composite, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
    
    return composite;
  }
  
  protected Control createDialogArea(final Composite parent) {
    final Composite composite = new Composite(parent, SWT.NONE);
    composite.setFont(parent.getFont());
    final GridLayout layout = new GridLayout(1, false);
    layout.marginLeft = 15;
    layout.marginRight = 15;
    layout.marginTop = 15;
    layout.marginBottom = 15;
    composite.setLayout(layout);
    composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    
    final Text text = new Text(composite, SWT.READ_ONLY);
    text.setFont(composite.getFont());
    text.setText(Messages.URMDS_RMSelectionDialogMsg);
    
    final Composite buttonsComposite = new Composite(composite, SWT.NONE);
    buttonsComposite.setFont(composite.getFont());
    final GridLayout buttonsLayout = new GridLayout(1, false);
    buttonsLayout.marginLeft = 15;
    buttonsComposite.setLayout(buttonsLayout);
    buttonsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    
    int i = -1;
    final Button[] rmButtons = new Button[this.fResourceManagers.size()];
    for (final IResourceManager resourceManager : this.fResourceManagers) {
      rmButtons[++i] = new Button(buttonsComposite, SWT.RADIO);
      final int index = i;
      rmButtons[i].setFont(buttonsComposite.getFont());
      rmButtons[i].setText(resourceManager.getName());
      rmButtons[i].addSelectionListener(new SelectionListener() {
        
        public void widgetSelected(final SelectionEvent event) {
          UniqueRMDialogSelector.this.fCurSelectionIndex = index;
        }
        
        public void widgetDefaultSelected(final SelectionEvent event) {
          widgetSelected(event);
        }
        
      });
    }
    rmButtons[0].setSelection(true);
    
    return composite;
  }
   
  // --- Internal services
  
  IResourceManager getSelectedResourceManager() {
    return this.fResourceManagers.get(this.fCurSelectionIndex);
  }
  
  // --- Fields
  
  private final List<IResourceManager> fResourceManagers;
  
  private int fCurSelectionIndex;

}
