/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.core.wizards;

import org.eclipse.imp.x10dt.ui.launch.core.preferences.X10PlatformConfiguration;
import org.eclipse.imp.x10dt.ui.launch.core.utils.WizardUtils;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardNode;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardSelectionPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;


final class PlatformConfNameWizardPage extends WizardSelectionPage implements IWizardPage {
  
  PlatformConfNameWizardPage(final X10PlatformConfiguration platformConf) {
    super("ConfName");
    setTitle("Configuration Name Page");
    setPageComplete(false);
    setDescription("Creates a new X10 platform");
    
    setMessage("You must define a configuration name");
    
    this.fPlatformConf = platformConf;
  }
  
  // --- IDialogPage's interface methods implementation

  public void createControl(final Composite parent) {
    initializeDialogUnits(parent);
    
    final Composite composite = new Composite(parent, SWT.NULL);
    composite.setFont(parent.getFont());
    composite.setLayout(new GridLayout(1, false));
    composite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, false, false));
    
    createName(composite);
    createBackEnd(composite);
    createLocation(composite);
    
    setSelectedNode(new NextWizardNode());
    
    setControl(composite);
  }
  
  // --- Private code
  
  private void createBackEnd(final Composite composite) {
    final Group backEndGroup = new Group(composite, SWT.NONE);
    backEndGroup.setFont(composite.getFont());
    backEndGroup.setLayout(new GridLayout(1, false));
    backEndGroup.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    backEndGroup.setText("Back-End");
    
    final Button cppBackEndBt = new Button(backEndGroup, SWT.RADIO);
    cppBackEndBt.setFont(composite.getFont());
    cppBackEndBt.setText("C++");
    cppBackEndBt.setSelection(true);
    
    this.fJavaBackEndBt = new Button(backEndGroup, SWT.RADIO);
    this.fJavaBackEndBt.setFont(composite.getFont());
    this.fJavaBackEndBt.setText("Java");
    this.fJavaBackEndBt.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        PlatformConfNameWizardPage.this.fLocalLocBt.setEnabled(! PlatformConfNameWizardPage.this.fJavaBackEndBt.getSelection());
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
      
    });
  }
  
  private void createLocation(final Composite composite) {
    final Group locationGroup = new Group(composite, SWT.NONE);
    locationGroup.setFont(composite.getFont());
    locationGroup.setLayout(new GridLayout(1, false));
    locationGroup.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    locationGroup.setText("Location");
    
    this.fLocalLocBt = new Button(locationGroup, SWT.RADIO);
    this.fLocalLocBt.setFont(composite.getFont());
    this.fLocalLocBt.setText("Local");
    this.fLocalLocBt.setSelection(true);
    this.fLocalLocBt.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        PlatformConfNameWizardPage.this.fJavaBackEndBt.setEnabled(! PlatformConfNameWizardPage.this.fLocalLocBt.getSelection());
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
      
    });
    
    final Button remoteLocBt = new Button(locationGroup, SWT.RADIO);
    remoteLocBt.setFont(composite.getFont());
    remoteLocBt.setText("Remote");
  }
  
  private void createName(final Composite parent) {
    final Composite composite = new Composite(parent, SWT.NONE);
    composite.setFont(parent.getFont());
    composite.setLayout(new GridLayout(2, false));
    composite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    
    WizardUtils.createLabelAndText(composite, "Configuration Name", new ModifyListener() {
      public void modifyText(final ModifyEvent event) {
        final Text text = (Text) event.widget;
        if ((text.getText() == null) || (text.getText().length() == 0)) {
          setMessage("You must define a configuration name");
          setPageComplete(false);
        } else {
          setMessage(null);
          setPageComplete(true);
        }
      }
    });
  }
  
  private class NextWizardNode implements IWizardNode {
    
    NextWizardNode() {
      this.fWizardPage = new PlatformConfDefWizardPage(PlatformConfNameWizardPage.this.fPlatformConf);
    }
    
    // --- Interface methods implementation

    public void dispose() {
      if (this.fWizard != null) {
        this.fWizard.dispose();
      }
    }

    public Point getExtent() {
      return new Point(-1, -1);
    }

    public IWizard getWizard() {
      if (this.fWizard == null) {
        this.fWizard = new PlatformConfDefWizard(this.fWizardPage);
      }
      this.fWizardPage.updateFlags(PlatformConfNameWizardPage.this.fLocalLocBt.getSelection(),
                                   ! PlatformConfNameWizardPage.this.fJavaBackEndBt.getSelection());
      return this.fWizard;
    }

    public boolean isContentCreated() {
      return true;
    }
    
    // --- Fields
    
    private INewWizard fWizard;
    
    private PlatformConfDefWizardPage fWizardPage;
    
  }
  
  private static final class PlatformConfDefWizard extends Wizard implements INewWizard {
    
    PlatformConfDefWizard(final IWizardPage page) {
      addPage(page);
    }
    
    // --- IWizard's interface methods implementation
    
    public boolean performFinish() {
      return true;
    }
    
    // --- IWorkbenchWizard's interface methods implementation

    public void init(final IWorkbench workbench, final IStructuredSelection selection) {
      // Nothing to do.
    }
    
  }
  
  // --- Fields
  
  private final X10PlatformConfiguration fPlatformConf;
  
  private Button fLocalLocBt;
  
  private Button fJavaBackEndBt;

}
