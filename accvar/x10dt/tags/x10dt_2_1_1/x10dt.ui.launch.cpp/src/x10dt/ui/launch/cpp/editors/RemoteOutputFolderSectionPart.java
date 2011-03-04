/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.editors;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.imp.utils.Pair;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import x10dt.ui.launch.core.platform_conf.EValidationStatus;
import x10dt.ui.launch.cpp.LaunchMessages;
import x10dt.ui.launch.cpp.platform_conf.ICppCompilationConf;


final class RemoteOutputFolderSectionPart extends AbstractCommonSectionFormPart implements IConnectionTypeListener, IFormPart {

  RemoteOutputFolderSectionPart(final Composite parent, final X10FormPage formPage) {
    super(parent, formPage);
    
    getSection().setFont(parent.getFont());
    getSection().setText(LaunchMessages.XPCP_RemoteOutputFolderSection);
    getSection().setDescription(LaunchMessages.XPCP_RemoteOutputFolderSectionDescr);
    getSection().setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
    
    createClient(formPage.getManagedForm(), formPage.getManagedForm().getToolkit());
    addCompletePartListener(getFormPage());
  }
  
  // --- IConnectionTypeListener's interface methods implementation
  
  public void connectionChanged(final boolean isLocal, final String remoteConnectionName,
                                final EValidationStatus validationStatus, final boolean shouldDeriveInfo) {
    for (final Control control : this.fControlsAffectedByLocalRM) {
      control.setEnabled(! isLocal);
    }
    if (! isLocal) {
      final String outputFolder = this.fRemoteOutputFolderText.getText().trim();
      getPlatformConf().setRemoteOutputFolder(outputFolder.length() == 0 ? null : outputFolder);
    }
    this.fBrowseBt.setEnabled(! isLocal && validationStatus == EValidationStatus.VALID);
    handleEmptyTextValidation(this.fRemoteOutputFolderText, LaunchMessages.XPCP_FolderLabel);
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
        handleEmptyTextValidation(remoteOutputFolderText, LaunchMessages.XPCP_FolderLabel);
        getPlatformConf().setRemoteOutputFolder(remoteOutputFolderText.getText());
        setPartCompleteFlag(hasCompleteInfo());
        updateDirtyState(managedForm);
      }
      
    });
  }
  
  private void createClient(final IManagedForm managedForm, final FormToolkit toolkit) {
    this.fControlsAffectedByLocalRM = new ArrayList<Control>();
    this.fControlsAffectedByLocalRM.add(getSection());
    
    final Composite sectionClient = toolkit.createComposite(getSection());
    final TableWrapLayout twLayout = new TableWrapLayout();
    twLayout.numColumns = 3;
    sectionClient.setLayout(twLayout);
    sectionClient.setFont(getSection().getFont());
    sectionClient.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

    final Pair<Text, Button> pair = createLabelTextBrowseBt(sectionClient, LaunchMessages.XPCP_FolderLabel, 
                                                            LaunchMessages.XPCP_BrowseBt, toolkit, 
                                                            this.fControlsAffectedByLocalRM);
    this.fRemoteOutputFolderText = pair.first;
    this.fBrowseBt = pair.second;
    
    initializeControls();
    
    addListeners(managedForm, this.fRemoteOutputFolderText);
     
    getSection().setClient(sectionClient);
  }
  
  private boolean hasCompleteInfo() {
    final boolean isLocal = getPlatformConf().getConnectionConf().isLocal();
    return isLocal || this.fRemoteOutputFolderText.getText().trim().length() > 0;
  }
  
  private void initializeControls() {
    final boolean isLocal = getPlatformConf().getConnectionConf().isLocal();
    for (final Control control : this.fControlsAffectedByLocalRM) {
      control.setEnabled(! isLocal);
    }
    
    final ICppCompilationConf cppCompConf = getPlatformConf().getCppCompilationConf();
    if (! isLocal) {
      if (cppCompConf.getRemoteOutputFolder() != null) {
        this.fRemoteOutputFolderText.setText(cppCompConf.getRemoteOutputFolder());
      }
      handleEmptyTextValidation(this.fRemoteOutputFolderText, LaunchMessages.XPCP_FolderLabel);
    }
  }
  
  // --- Fields
  
  private Text fRemoteOutputFolderText;
  
  private Button fBrowseBt;
  
  private Collection<Control> fControlsAffectedByLocalRM;

}
