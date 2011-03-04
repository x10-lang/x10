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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import x10dt.ui.launch.core.Constants;
import x10dt.ui.launch.core.platform_conf.EValidationStatus;
import x10dt.ui.launch.core.utils.KeyboardUtils;
import x10dt.ui.launch.cpp.LaunchMessages;
import x10dt.ui.launch.cpp.platform_conf.ICppCompilationConf;


final class X10DistributionSectionPart extends AbstractCommonSectionFormPart implements IConnectionTypeListener, IFormPart {

  X10DistributionSectionPart(final Composite parent, final X10FormPage formPage) {
    super(parent, formPage);
    
    getSection().setText(LaunchMessages.XPCP_X10DistribSection);
    getSection().setDescription(LaunchMessages.XPCP_X10DistribSectionDescr);
    getSection().setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
    
    createClient(formPage.getManagedForm(), formPage.getManagedForm().getToolkit());
    addCompletePartListener(getFormPage());
  }
  
  // --- IConnectionTypeListener's interface methods implementation
  
  public void connectionChanged(final boolean isLocal, final String remoteConnectionName,
                                final EValidationStatus validationStatus, final boolean newCurrent) {
    for (final Control control : this.fControlsAffectedByLocalRM) {
      control.setEnabled(! isLocal);
    }
    if (! isLocal) {
      final String x10DistLoc = this.fX10DistLocText.getText().trim();
      getPlatformConf().setX10DistribLocation(x10DistLoc.length() == 0 ? null : x10DistLoc);
      final String pgasLoc = this.fPGASLocText.getText().trim();
      getPlatformConf().setPGASLocation(pgasLoc.length() == 0 ? null : pgasLoc);
    }
    final ICppCompilationConf cppCompConf = getPlatformConf().getCppCompilationConf();
    final boolean useSameLoc = (cppCompConf.getPGASLocation() == null) ||
                               cppCompConf.getPGASLocation().equals(cppCompConf.getX10DistribLocation());
    this.fUseSameLocBt.setSelection(useSameLoc);
    for (final Control control : this.fPGASControls) {
      control.setEnabled(!useSameLoc);
    }

    this.fX10DistBrowseBt.setEnabled(! isLocal && validationStatus == EValidationStatus.VALID);
    this.fPGASDistBrowseBt.setEnabled(! isLocal && ((validationStatus == EValidationStatus.VALID) && ! useSameLoc));

    if (isLocal) {
      this.fX10DistLocText.setText(Constants.EMPTY_STR);
      this.fPGASLocText.setText(Constants.EMPTY_STR);
      handleEmptyTextValidation(this.fX10DistLocText, LaunchMessages.XPCP_X10DistLabel);
      handleEmptyTextValidation(this.fPGASLocText, LaunchMessages.XPCP_PGASDistLabel);
    } else if (validationStatus == EValidationStatus.VALID) {
      handlePathValidation(this.fX10DistLocText, LaunchMessages.XPCP_X10DistLabel);
      handlePathValidation(this.fPGASLocText, LaunchMessages.XPCP_PGASDistLabel);
    } else {
      handleEmptyTextValidation(this.fX10DistLocText, LaunchMessages.XPCP_X10DistLabel);
      handleEmptyTextValidation(this.fPGASLocText, LaunchMessages.XPCP_PGASDistLabel);
    }
    
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
        if (! getPlatformConf().getConnectionConf().isLocal()) {
          handleEmptyTextValidation(x10DistLocText, LaunchMessages.XPCP_X10DistLabel);
          getPlatformConf().setX10DistribLocation(x10DistLocText.getText());
          if (useSameLocBt.getSelection()) {
            pgasLocText.setText(x10DistLocText.getText());
          }
          setPartCompleteFlag(hasCompleteInfo());
          updateDirtyState(managedForm);
        }
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
        	handleEmptyTextValidation(pgasLocText, LaunchMessages.XPCP_PGASDistLabel);
        }
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
      
    });
    pgasLocText.addModifyListener(new ModifyListener() {
      public void modifyText(final ModifyEvent event) {
        if (! getPlatformConf().getConnectionConf().isLocal()) {
          handleEmptyTextValidation(pgasLocText, LaunchMessages.XPCP_PGASDistLabel);
          getPlatformConf().setPGASLocation(pgasLocText.getText());
          setPartCompleteFlag(hasCompleteInfo());
          updateDirtyState(managedForm);
        }
      }
    });
  }
  
  private void createClient(final IManagedForm managedForm, final FormToolkit toolkit) {
    this.fControlsAffectedByLocalRM = new ArrayList<Control>();
    this.fControlsAffectedByLocalRM.add(getSection());
    
    final Composite sectionClient = toolkit.createComposite(getSection());
    sectionClient.setLayout(new TableWrapLayout());
    sectionClient.setFont(getSection().getFont());
    sectionClient.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
    
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

    initializeControls();
    
    addListeners(managedForm, this.fX10DistLocText, this.fX10DistBrowseBt, useSameLocBt, this.fPGASLocText, 
                 this.fPGASDistBrowseBt, this.fPGASControls);

    getSection().setClient(sectionClient);
  }
  
  private boolean hasCompleteInfo() {
    final boolean isLocal = getPlatformConf().getConnectionConf().isLocal();
    return isLocal || ((this.fX10DistLocText.getText().trim().length() > 0) && 
                       (this.fPGASLocText.getText().trim().length() > 0));
  }
  
  private void initializeControls() {
    final boolean isLocal = getPlatformConf().getConnectionConf().isLocal();
    for (final Control control : this.fControlsAffectedByLocalRM) {
      control.setEnabled(! isLocal);
    }
    
    final ICppCompilationConf cppCompConf = getPlatformConf().getCppCompilationConf();
    if (! isLocal) {
      if (cppCompConf.getX10DistribLocation() != null) {
        this.fX10DistLocText.setText(cppCompConf.getX10DistribLocation());
      }
      handleEmptyTextValidation(this.fX10DistLocText, LaunchMessages.XPCP_X10DistLabel);
      if (cppCompConf.getPGASLocation() != null) {
        this.fPGASLocText.setText(cppCompConf.getPGASLocation());
      }
      handleEmptyTextValidation(this.fX10DistLocText, LaunchMessages.XPCP_X10DistLabel);
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
    
    KeyboardUtils.addDelayedActionOnControl(this.fX10DistLocText, new Runnable() {
      
      public void run() {
        getFormPage().getEditorSite().getShell().getDisplay().asyncExec(new Runnable() {
          
          public void run() {
            handlePathValidation(X10DistributionSectionPart.this.fX10DistLocText, LaunchMessages.XPCP_X10DistLabel);
          }
          
        });
      }
      
    });
    KeyboardUtils.addDelayedActionOnControl(this.fPGASLocText, new Runnable() {

      public void run() {
        getFormPage().getEditorSite().getShell().getDisplay().asyncExec(new Runnable() {

          public void run() {
            handlePathValidation(X10DistributionSectionPart.this.fPGASLocText, LaunchMessages.XPCP_PGASDistLabel);
          }

        });
      }

    });
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
