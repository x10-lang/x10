/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.cpp.editors;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.imp.utils.Pair;
import org.eclipse.imp.x10dt.ui.launch.core.platform_conf.EValidationStatus;
import org.eclipse.imp.x10dt.ui.launch.cpp.LaunchMessages;
import org.eclipse.imp.x10dt.ui.launch.cpp.platform_conf.ICppCompilationConf;
import org.eclipse.imp.x10dt.ui.launch.cpp.platform_conf.IX10PlatformConfWorkCopy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;


final class RemoteOutputFolderSectionPart extends AbstractCommonSectionFormPart implements IConnectionTypeListener, IFormPart {

  RemoteOutputFolderSectionPart(final Composite parent, final X10FormPage formPage, 
                                final IX10PlatformConfWorkCopy x10PlatformConf) {
    super(parent, formPage, x10PlatformConf);
    
    getSection().setFont(parent.getFont());
    getSection().setText(LaunchMessages.XPCP_RemoteOutputFolderSection);
    getSection().setDescription(LaunchMessages.XPCP_RemoteOutputFolderSectionDescr);
    getSection().setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    
    createClient(formPage.getManagedForm(), formPage.getManagedForm().getToolkit(), x10PlatformConf);
    addCompletePartListener(getFormPage());
  }
  
  // --- IConnectionTypeListener's interface methods implementation
  
  public void connectionChanged(final boolean isLocal, final String remoteConnectionName,
                                final EValidationStatus validationStatus) {
    for (final Control control : this.fControlsAffectedByLocalRM) {
      control.setEnabled(! isLocal);
    }
    this.fBrowseBt.setEnabled(! isLocal && validationStatus == EValidationStatus.VALID);
    handleTextValidation(new EmptyTextInputChecker(this.fRemoteOutputFolderText, LaunchMessages.XPCP_FolderLabel), 
                         getFormPage().getManagedForm(), this.fRemoteOutputFolderText);
    setPartCompleteFlag(hasCompleteInfo());
  }
  
  // --- IFormPart's interface methods implementation

  public void dispose() {
    removeCompletePartListener(getFormPage());
  }
  
  // --- Overridden methods
  
  public boolean setFormInput(final Object input) {
    setPartCompleteFlag(hasCompleteInfo());
    return false;
  }
  
  // --- Private code
  
  private void addListeners(final IManagedForm managedForm, final Text remoteOutputFolderText) {
    remoteOutputFolderText.addModifyListener(new ModifyListener() {
      
      public void modifyText(final ModifyEvent event) {
        handleTextValidation(new EmptyTextInputChecker(remoteOutputFolderText, LaunchMessages.XPCP_FolderLabel), managedForm,
                             remoteOutputFolderText);
        getPlatformConf().setRemoteOutputFolder(remoteOutputFolderText.getText());
        setPartCompleteFlag(hasCompleteInfo());
        updateDirtyState(managedForm);
      }
      
    });
  }
  
  private void createClient(final IManagedForm managedForm, final FormToolkit toolkit, 
                            final IX10PlatformConfWorkCopy x10PlatformConf) {
    this.fControlsAffectedByLocalRM = new ArrayList<Control>();
    this.fControlsAffectedByLocalRM.add(getSection());
    
    final Composite sectionClient = toolkit.createComposite(getSection());
    sectionClient.setLayout(new GridLayout(3, false));
    sectionClient.setFont(getSection().getFont());
    sectionClient.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));

    final Pair<Text, Button> pair = createLabelTextBrowseBt(sectionClient, LaunchMessages.XPCP_FolderLabel, 
                                                            LaunchMessages.XPCP_BrowseBt, toolkit, 
                                                            this.fControlsAffectedByLocalRM);
    this.fRemoteOutputFolderText = pair.first;
    this.fBrowseBt = pair.second;
    
    initializeControls(managedForm);
    
    addListeners(managedForm, this.fRemoteOutputFolderText);
     
    getSection().setClient(sectionClient);
  }
  
  private boolean hasCompleteInfo() {
    final boolean isLocal = getPlatformConf().getConnectionConf().isLocal();
    return isLocal || this.fRemoteOutputFolderText.getText().trim().length() > 0;
  }
  
  private void initializeControls(final IManagedForm managedForm) {
    final boolean isLocal = getPlatformConf().getConnectionConf().isLocal();
    for (final Control control : this.fControlsAffectedByLocalRM) {
      control.setEnabled(! isLocal);
    }
    
    final ICppCompilationConf cppCompConf = getPlatformConf().getCppCompilationConf();
    if (! isLocal) {
      if (cppCompConf.getRemoteOutputFolder() != null) {
        this.fRemoteOutputFolderText.setText(cppCompConf.getRemoteOutputFolder());
      }
      handleTextValidation(new EmptyTextInputChecker(this.fRemoteOutputFolderText, LaunchMessages.XPCP_FolderLabel), 
                           managedForm, this.fRemoteOutputFolderText);
    }
  }
  
  // --- Fields
  
  private Text fRemoteOutputFolderText;
  
  private Button fBrowseBt;
  
  private Collection<Control> fControlsAffectedByLocalRM;

}
