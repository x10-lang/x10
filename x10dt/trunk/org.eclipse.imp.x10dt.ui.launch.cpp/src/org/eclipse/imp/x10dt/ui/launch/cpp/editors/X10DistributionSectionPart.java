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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;


final class X10DistributionSectionPart extends AbstractCommonSectionFormPart implements IConnectionTypeListener, IFormPart {

  X10DistributionSectionPart(final Composite parent, final X10FormPage formPage, 
                             final IX10PlatformConfWorkCopy x10PlatformConf) {
    super(parent, formPage, x10PlatformConf);
    
    getSection().setText(LaunchMessages.XPCP_X10DistribSection);
    getSection().setDescription(LaunchMessages.XPCP_X10DistribSectionDescr);
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
    final ICppCompilationConf cppCompConf = getPlatformConf().getCppCompilationConf();
    final boolean useSameLoc = (cppCompConf.getPGASLocation() == null) || 
                                cppCompConf.getPGASLocation().equals(cppCompConf.getX10DistribLocation());
    this.fUseSameLocBt.setSelection(useSameLoc);
    for (final Control control : this.fPGASControls) {
      control.setEnabled(! useSameLoc);
    }
    
    this.fX10DistBrowseBt.setEnabled(! isLocal && validationStatus == EValidationStatus.VALID);
    this.fPGASDistBrowseBt.setEnabled(! isLocal && ((validationStatus == EValidationStatus.VALID) && ! useSameLoc));
    
    handleTextValidation(new EmptyTextInputChecker(this.fX10DistLocText, LaunchMessages.XPCP_X10DistLabel), 
                         getFormPage().getManagedForm(), this.fX10DistLocText);
    handleTextValidation(new EmptyTextInputChecker(this.fX10DistLocText, LaunchMessages.XPCP_X10DistLabel), 
                         getFormPage().getManagedForm(), this.fX10DistLocText);
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
  
  private void addListeners(final IManagedForm managedForm, final Text x10DistLocText, final Button x10DistBrowseBt, 
                            final Button useSameLocBt, final Text pgasLocText, final Button pgasDistBrowseBt,
                            final Collection<Control> pgasControls) {
    x10DistLocText.addModifyListener(new ModifyListener() {
      
      public void modifyText(final ModifyEvent event) {
        handleTextValidation(new EmptyTextInputChecker(x10DistLocText, LaunchMessages.XPCP_X10DistLabel), managedForm, 
                             x10DistLocText);
        getPlatformConf().setX10DistribLocation(x10DistLocText.getText());
        if (useSameLocBt.getSelection()) {
          pgasLocText.setText(x10DistLocText.getText());
        }
        setPartCompleteFlag(hasCompleteInfo());
        updateDirtyState(managedForm);
      }
      
    });
    useSameLocBt.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        for (final Control control : pgasControls) {
          control.setEnabled(! useSameLocBt.getSelection());
        }
        pgasDistBrowseBt.setEnabled(! useSameLocBt.getSelection() && x10DistBrowseBt.isEnabled());
        if (useSameLocBt.getSelection()) {
          pgasLocText.setText(x10DistLocText.getText());
        } else {
          handleTextValidation(new EmptyTextInputChecker(pgasLocText, LaunchMessages.XPCP_PGASDistLabel), managedForm, 
                               pgasLocText);
        }
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
      
    });
    pgasLocText.addModifyListener(new ModifyListener() {
      
      public void modifyText(final ModifyEvent event) {
        handleTextValidation(new EmptyTextInputChecker(pgasLocText, LaunchMessages.XPCP_PGASDistLabel), managedForm, 
                             pgasLocText);
        getPlatformConf().setPGASLocation(pgasLocText.getText());
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
    sectionClient.setLayout(new GridLayout(1, false));
    sectionClient.setFont(getSection().getFont());
    sectionClient.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    
    final Pair<Text, Button> pair1 = createLabelTextBrowseBt(sectionClient, LaunchMessages.XPCP_X10DistLabel, 
                                                            LaunchMessages.XPCP_BrowseBt, toolkit, 
                                                            this.fControlsAffectedByLocalRM);
    this.fX10DistLocText = pair1.first;
    this.fX10DistBrowseBt = pair1.second;
    
    final Button useSameLocBt = toolkit.createButton(sectionClient, LaunchMessages.XPCP_UseX10DistLoc, SWT.CHECK);
    this.fUseSameLocBt = useSameLocBt;
    this.fControlsAffectedByLocalRM.add(useSameLocBt);
    
    this.fPGASControls = new ArrayList<Control>();
    final Pair<Text, Button> pair2 = createLabelTextBrowseBt(sectionClient, LaunchMessages.XPCP_PGASDistLabel, 
                                                             LaunchMessages.XPCP_BrowseBt, toolkit, this.fPGASControls);
    this.fControlsAffectedByLocalRM.addAll(this.fPGASControls);
    this.fPGASLocText = pair2.first;
    this.fPGASDistBrowseBt = pair2.second;

    initializeControls(managedForm);
    
    addListeners(managedForm, this.fX10DistLocText, this.fX10DistBrowseBt, useSameLocBt, this.fPGASLocText, 
                 this.fPGASDistBrowseBt, this.fPGASControls);

    getSection().setClient(sectionClient);
  }
  
  private boolean hasCompleteInfo() {
    final boolean isLocal = getPlatformConf().getConnectionConf().isLocal();
    return isLocal || ((this.fX10DistLocText.getText().trim().length() > 0) && 
                       (this.fPGASLocText.getText().trim().length() > 0));
  }
  
  private void initializeControls(final IManagedForm managedForm) {
    final boolean isLocal = getPlatformConf().getConnectionConf().isLocal();
    for (final Control control : this.fControlsAffectedByLocalRM) {
      control.setEnabled(! isLocal);
    }
    
    final ICppCompilationConf cppCompConf = getPlatformConf().getCppCompilationConf();
    if (! isLocal) {
      if (cppCompConf.getX10DistribLocation() != null) {
        this.fX10DistLocText.setText(cppCompConf.getX10DistribLocation());
      }
      handleTextValidation(new EmptyTextInputChecker(this.fX10DistLocText, LaunchMessages.XPCP_X10DistLabel), managedForm, 
                           this.fX10DistLocText);
      if (cppCompConf.getPGASLocation() != null) {
        this.fPGASLocText.setText(cppCompConf.getPGASLocation());
      }
      handleTextValidation(new EmptyTextInputChecker(this.fX10DistLocText, LaunchMessages.XPCP_X10DistLabel), managedForm, 
                           this.fX10DistLocText);
      final boolean useSameLoc = (cppCompConf.getPGASLocation() == null) || 
                                  cppCompConf.getPGASLocation().equals(cppCompConf.getX10DistribLocation());
      if (useSameLoc) {
        this.fPGASLocText.setText(this.fX10DistLocText.getText());
      }
      for (final Control control : this.fPGASControls) {
        control.setEnabled(! useSameLoc);
      }
      this.fUseSameLocBt.setSelection(useSameLoc);
    }
  }
  
  // --- Fields

  private Text fX10DistLocText;
  
  private Text fPGASLocText;
  
  private Button fUseSameLocBt;
  
  private Button fX10DistBrowseBt;
  
  private Button fPGASDistBrowseBt;
  
  private Collection<Control> fControlsAffectedByLocalRM;
  
  private Collection<Control> fPGASControls;
  
}
