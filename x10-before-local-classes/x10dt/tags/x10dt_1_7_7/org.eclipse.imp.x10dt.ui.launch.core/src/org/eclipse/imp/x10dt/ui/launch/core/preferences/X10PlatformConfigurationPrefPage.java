/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.core.preferences;

import org.eclipse.imp.x10dt.ui.launch.core.LaunchCore;
import org.eclipse.imp.x10dt.ui.launch.core.wizards.X10NewPlatformConfWizard;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Defines X10 Platform Configuration preference page.
 * 
 * @author egeay
 */
public final class X10PlatformConfigurationPrefPage extends PreferencePage 
                                                    implements IWorkbenchPreferencePage, SelectionListener {
  
  // --- Interface methods implementation

  public void init(final IWorkbench workbench) {
    setPreferenceStore(LaunchCore.getInstance().getPreferenceStore());
  }
  
  // --- SelectionListener's interface methods implementation
  
  public void widgetDefaultSelected(final SelectionEvent event) {
    widgetSelected(event);
  }

  public void widgetSelected(final SelectionEvent event) {
    final String selectedElement = ((List) event.widget).getSelection()[0];
    final StringBuilder sb = new StringBuilder();
    sb.append("Configuration Summary for ").append(selectedElement).append(':');
    sb.append("\n    - Some info");
    this.fPlatformConfSummaryText.setText(sb.toString());
  }
  
  // --- Abstract methods implementation
  
  protected Control createContents(final Composite parent) {
    final Composite composite = new Composite(parent, SWT.NONE);
    composite.setFont(parent.getFont());
    composite.setLayout(new GridLayout(1, false));
    composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    
    final Label label = new Label(composite, SWT.NONE);
    label.setFont(parent.getFont());
    label.setText("X10 Platforms");
    
    final SashForm sashFrom = new SashForm(composite, SWT.VERTICAL);
    sashFrom.setLayout(new GridLayout(1, false));
    sashFrom.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    
    addPlatformConfsContainer(sashFrom);
    
    this.fPlatformConfSummaryText = new Link(sashFrom, SWT.WRAP);
    this.fPlatformConfSummaryText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    initializeValues();
    
    return composite;
  }
  
  // --- Private code
  
  private void addPlatformConfsContainer(final Composite parent) {
    final Composite composite = new Composite(parent, SWT.NONE);
    composite.setFont(parent.getFont());
    composite.setLayout(new GridLayout(2, false));
    composite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    
    final List platormConfList = new List (composite, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
    platormConfList.setFont(composite.getFont());
    platormConfList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 4));
    platormConfList.addSelectionListener(this);
    
    final Button addButton = new Button(composite, SWT.PUSH);
    addButton.setFont(composite.getFont());
    addButton.setText("&Add");
    addButton.addSelectionListener(new SelectionListener() {
      
      // --- Interface methods implementation
      
      public void widgetSelected(final SelectionEvent event) {
        final X10PlatformConfiguration newPlatformConf = new X10PlatformConfiguration();
        final WizardDialog dialog = new WizardDialog(getShell(), new X10NewPlatformConfWizard(newPlatformConf));
        if (dialog.open() == Window.OK) {
          
        }
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
      
    });
    
    final Button editButton = new Button(composite, SWT.PUSH);
    editButton.setFont(composite.getFont());
    editButton.setText("&Edit");
    
    final Button removeButton = new Button(composite, SWT.PUSH);
    removeButton.setFont(composite.getFont());
    removeButton.setText("&Remove");
  }

  private void initializeValues() {
  }
  
  // --- Private classes
  
  
  // --- Fields
  
  private Link fPlatformConfSummaryText;

}
