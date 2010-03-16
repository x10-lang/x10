/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.core.wizards;

import java.util.Set;

import org.eclipse.imp.x10dt.ui.launch.core.LaunchCore;
import org.eclipse.imp.x10dt.ui.launch.core.Messages;
import org.eclipse.imp.x10dt.ui.launch.core.utils.WizardUtils;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardNode;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardSelectionPage;
import org.eclipse.osgi.util.NLS;
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


final class PlatformConfNameWizardPage extends WizardSelectionPage implements IWizardPage, IPlaftormConfWizardPage {
  
  PlatformConfNameWizardPage(final Set<String> confNames) {
    super("ConfName"); //$NON-NLS-1$
    setTitle(Messages.PCNWP_WizTitle);
    setPageComplete(false);
    setDescription(Messages.PCNWP_WizDescr);
    
    this.fConfNames = confNames;
    
    setMessage(Messages.PCNWP_WizDefaultMsg);
  }
  
  // --- IDialogPage's interface methods implementation

  public void createControl(final Composite parent) {
    initializeDialogUnits(parent);
    
    final Composite composite = new Composite(parent, SWT.NONE);
    composite.setFont(parent.getFont());
    composite.setLayout(new GridLayout(1, false));
    composite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, false, false));
    
    createName(composite);
    createBackEnd(composite);
    createLocation(composite);
    
    setSelectedNode(new NextWizardNode());
    
    setControl(composite);
  }
  
  // --- IPlaftormConfWizardPage's interface methods implementation
  
  public boolean performFinish(final X10PlatformConfiguration platformConfiguration) {
    platformConfiguration.setName(this.fNameText.getText());
    
    return ((NextWizardNode) getSelectedNode()).getWizardPage().performFinish(platformConfiguration);
  }
  
  // --- Overridden methods
  
  public boolean canFlipToNextPage() {
    return (this.fNameText.getText() != null) && (this.fNameText.getText().length() > 0) &&
           (! this.fConfNames.contains(this.fNameText.getText()));
  }
  
  // --- Private code
  
  private void createBackEnd(final Composite composite) {
    final Group backEndGroup = new Group(composite, SWT.NONE);
    backEndGroup.setFont(composite.getFont());
    backEndGroup.setLayout(new GridLayout(1, false));
    backEndGroup.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    backEndGroup.setText(Messages.PCNWP_BackEndGroup);
    
    final Button cppBackEndBt = new Button(backEndGroup, SWT.RADIO);
    cppBackEndBt.setFont(composite.getFont());
    cppBackEndBt.setText(Messages.PCNWP_CPPBt);
    cppBackEndBt.setSelection(true);
    
    this.fJavaBackEndBt = new Button(backEndGroup, SWT.RADIO);
    this.fJavaBackEndBt.setFont(composite.getFont());
    this.fJavaBackEndBt.setText(Messages.PCNWP_JavaBt);
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
    locationGroup.setText(Messages.PCNWP_LocationGroup);
    
    this.fLocalLocBt = new Button(locationGroup, SWT.RADIO);
    this.fLocalLocBt.setFont(composite.getFont());
    this.fLocalLocBt.setText(Messages.PCNWP_LocalBt);
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
    remoteLocBt.setText(Messages.PCNWP_RemoteBt);
  }
  
  private void createName(final Composite parent) {
    final Composite composite = new Composite(parent, SWT.NONE);
    composite.setFont(parent.getFont());
    composite.setLayout(new GridLayout(2, false));
    composite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    
    this.fNameText = WizardUtils.createLabelAndText(composite, Messages.PCNWP_ConfigurationName, new ModifyListener() {
      public void modifyText(final ModifyEvent event) {
        setErrorMessage(null);
        final Text text = (Text) event.widget;
        if ((text.getText() == null) || (text.getText().length() == 0)) {
          setMessage(Messages.PCNWP_WizDefaultMsg);
        } else {
          if (PlatformConfNameWizardPage.this.fConfNames.contains(text.getText())) {
            setErrorMessage(NLS.bind(Messages.XPCPP_ConfNameAlreadyExists, text.getText()));
          } else {
            setMessage(null);
          }
        }
        getContainer().updateButtons();
      }
    });
  }
  
  private class NextWizardNode implements IWizardNode {
    
    NextWizardNode() {
      this.fWizardPage = new PlatformConfDefWizardPage(null);
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
      final boolean isLocal = PlatformConfNameWizardPage.this.fLocalLocBt.getSelection();
      final boolean isCplusPlus = ! PlatformConfNameWizardPage.this.fJavaBackEndBt.getSelection();
      this.fWizardPage.updateFlags(isLocal, isCplusPlus);
      final String location = (isLocal) ? Messages.PCNWP_LocalStr : Messages.PCNWP_RemoteStr;
      final String backEnd = (isCplusPlus) ? Messages.PCNWP_CPPBt : Messages.PCNWP_JavaBt;
      this.fWizardPage.setTitle(NLS.bind(Messages.PCNWP_DefWizPageTitle, location, backEnd));
      this.fWizardPage.setDescription(NLS.bind(Messages.PCNWP_DefWizPageDescr, location, backEnd));
      return this.fWizard;
    }

    public boolean isContentCreated() {
      return true;
    }
    
    // --- Internal services
    
    PlatformConfDefWizardPage getWizardPage() {
      return this.fWizardPage;
    }
    
    // --- Fields
    
    private INewWizard fWizard;
    
    private PlatformConfDefWizardPage fWizardPage;
    
  }
  
  private static final class PlatformConfDefWizard extends Wizard implements INewWizard {
    
    PlatformConfDefWizard(final IWizardPage page) {
      setWindowTitle(Messages.XNPC_WindowTitle);
      setDialogSettings(LaunchCore.getInstance().getDialogSettings());
      
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
  
  private final Set<String> fConfNames;
  
  private Button fLocalLocBt;
  
  private Button fJavaBackEndBt;
  
  private Text fNameText;

}
