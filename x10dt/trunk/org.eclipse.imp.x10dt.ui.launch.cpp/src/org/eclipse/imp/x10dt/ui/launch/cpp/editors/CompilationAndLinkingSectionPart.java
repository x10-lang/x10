/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.cpp.editors;

import org.eclipse.imp.utils.Pair;
import org.eclipse.imp.x10dt.ui.launch.core.platform_conf.EArchitecture;
import org.eclipse.imp.x10dt.ui.launch.core.platform_conf.ETargetOS;
import org.eclipse.imp.x10dt.ui.launch.core.platform_conf.EValidationStatus;
import org.eclipse.imp.x10dt.ui.launch.core.utils.SWTFormUtils;
import org.eclipse.imp.x10dt.ui.launch.cpp.LaunchMessages;
import org.eclipse.imp.x10dt.ui.launch.cpp.platform_conf.ICppCompilationConf;
import org.eclipse.imp.x10dt.ui.launch.cpp.platform_conf.IX10PlatformConfWorkCopy;
import org.eclipse.imp.x10dt.ui.launch.cpp.platform_conf.cpp_commands.DefaultCPPCommandsFactory;
import org.eclipse.imp.x10dt.ui.launch.cpp.platform_conf.cpp_commands.IDefaultCPPCommands;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;


final class CompilationAndLinkingSectionPart extends AbstractCommonSectionFormPart 
                                             implements IConnectionTypeListener, IFormPart {

  CompilationAndLinkingSectionPart(final Composite parent, final X10FormPage formPage, 
                                   final IX10PlatformConfWorkCopy x10PlatformConf) {
    super(parent, formPage, x10PlatformConf);
    
    getSection().setFont(parent.getFont());
    getSection().setText(LaunchMessages.XPCP_CompilationLinkingSection);
    getSection().setDescription(LaunchMessages.XPCP_CompilationLinkingSectionDescr);
    getSection().setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    
    createClient(formPage.getManagedForm(), formPage.getManagedForm().getToolkit(), x10PlatformConf);
    addCompletePartListener(formPage);
  }

  
  // --- IConnectionTypeListener's interface methods implementation
  
  public void connectionChanged(final boolean isLocal, final String remoteConnectionName, 
                                final EValidationStatus validationStatus) {
    this.fCompilerBrowseBt.setEnabled(isLocal || validationStatus == EValidationStatus.VALID);
    this.fArchiverBrowseBt.setEnabled(isLocal || validationStatus == EValidationStatus.VALID);
    this.fLinkerBrowseBt.setEnabled(isLocal || validationStatus == EValidationStatus.VALID);
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
  
  private void addListeners(final IManagedForm managedForm, final Text compilerText, final Text compilingOptsText,
                            final Text archiverText, final Text archivingOptsText, final Text linkerText, 
                            final Text linkingOptsText, final Text linkingLibsText, final Button archButton,
                            final Combo osCombo) {
    compilerText.addModifyListener(new ModifyListener() {
      
      public void modifyText(final ModifyEvent event) {
        handleTextValidation(new EmptyTextInputChecker(compilerText, LaunchMessages.XPCP_CompilerLabel), managedForm,
                             compilerText);
        getPlatformConf().setCompiler(compilerText.getText());
        setPartCompleteFlag(hasCompleteInfo());
        updateDirtyState(managedForm);
      }
      
    });
    compilingOptsText.addModifyListener(new ModifyListener() {
      
      public void modifyText(final ModifyEvent event) {
        handleTextValidation(new EmptyTextInputChecker(compilingOptsText, LaunchMessages.XPCP_CompilingOptsLabel), managedForm,
                             compilingOptsText);
        getPlatformConf().setCompilingOpts(compilingOptsText.getText());
        setPartCompleteFlag(hasCompleteInfo());
        updateDirtyState(managedForm);
      }
      
    });
    archiverText.addModifyListener(new ModifyListener() {
      
      public void modifyText(final ModifyEvent event) {
        handleTextValidation(new EmptyTextInputChecker(archiverText, LaunchMessages.XPCP_ArchiverLabel), managedForm,
                             archiverText);
        getPlatformConf().setArchiver(archiverText.getText());
        setPartCompleteFlag(hasCompleteInfo());
        updateDirtyState(managedForm);
      }
      
    });
    archivingOptsText.addModifyListener(new ModifyListener() {
      
      public void modifyText(final ModifyEvent event) {
        handleTextValidation(new EmptyTextInputChecker(archivingOptsText, LaunchMessages.XPCP_ArchivingOptsLabel), managedForm,
                             archivingOptsText);
        getPlatformConf().setArchivingOpts(archivingOptsText.getText());
        setPartCompleteFlag(hasCompleteInfo());
        updateDirtyState(managedForm);
      }
      
    });
    linkerText.addModifyListener(new ModifyListener() {
      
      public void modifyText(final ModifyEvent event) {
        handleTextValidation(new EmptyTextInputChecker(linkerText, LaunchMessages.XPCP_LinkerLabel), managedForm,
                             linkerText);
        getPlatformConf().setLinker(linkerText.getText());
        setPartCompleteFlag(hasCompleteInfo());
        updateDirtyState(managedForm);
      }
      
    });
    linkingOptsText.addModifyListener(new ModifyListener() {
      
      public void modifyText(final ModifyEvent event) {
        handleTextValidation(new EmptyTextInputChecker(linkingOptsText, LaunchMessages.XPCP_LinkingOptsLabel), managedForm,
                             linkingOptsText);
        getPlatformConf().setLinkingOpts(linkingOptsText.getText());
        setPartCompleteFlag(hasCompleteInfo());
        updateDirtyState(managedForm);
      }
      
    });
    linkingLibsText.addModifyListener(new ModifyListener() {
      
      public void modifyText(final ModifyEvent event) {
        handleTextValidation(new EmptyTextInputChecker(linkingLibsText, LaunchMessages.XPCP_LinkingLibsLabel), managedForm,
                             linkingLibsText);
        getPlatformConf().setLinkingLibs(linkingLibsText.getText());
        setPartCompleteFlag(hasCompleteInfo());
        updateDirtyState(managedForm);
      }
      
    });
    archButton.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        getPlatformConf().setArchitecture(archButton.getSelection() ? EArchitecture.E64Arch : EArchitecture.E32Arch);
        updateDirtyState(managedForm);
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
      
    });
    osCombo.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        final String osName = osCombo.getItem(osCombo.getSelectionIndex());
        final ETargetOS targetOs = (ETargetOS) osCombo.getData(osName);
        getPlatformConf().setTargetOS(targetOs);
        
        final boolean is64Arch = archButton.getSelection();
        getPlatformConf().setArchitecture(is64Arch ? EArchitecture.E64Arch : EArchitecture.E32Arch);
        
        final IDefaultCPPCommands defaultCPPCommands;
        switch (targetOs) {
        case AIX:
          defaultCPPCommands = DefaultCPPCommandsFactory.createAixCommands(is64Arch);
          break;
        case LINUX:
          defaultCPPCommands = DefaultCPPCommandsFactory.createLinuxCommands(is64Arch);
          break;
        case MAC:
          defaultCPPCommands = DefaultCPPCommandsFactory.createMacCommands(is64Arch);
          break;
        case WINDOWS:
          defaultCPPCommands = DefaultCPPCommandsFactory.createCygwinCommands(is64Arch);
          break;
        default:
          defaultCPPCommands = DefaultCPPCommandsFactory.createUnkownUnixCommands(is64Arch);
        }
        compilerText.setText(defaultCPPCommands.getCompiler());
        compilingOptsText.setText(defaultCPPCommands.getCompilerOptions());
        archiverText.setText(defaultCPPCommands.getArchiver());
        archivingOptsText.setText(defaultCPPCommands.getArchivingOpts());
        linkerText.setText(defaultCPPCommands.getLinker());
        linkingOptsText.setText(defaultCPPCommands.getLinkingOptions());
        linkingLibsText.setText(defaultCPPCommands.getLinkingLibraries());
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
      
    });
  }
  
  private void createClient(final IManagedForm managedForm, final FormToolkit toolkit, 
                            final IX10PlatformConfWorkCopy x10PlatformConf) {
    final Composite sectionClient = toolkit.createComposite(getSection());
    sectionClient.setLayout(new GridLayout(1, false));
    sectionClient.setFont(getSection().getFont());
    sectionClient.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    
    final Composite osComposite = toolkit.createComposite(sectionClient);
    osComposite.setFont(getSection().getFont());
    osComposite.setLayout(new GridLayout(2, false));
    osComposite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    
    toolkit.createLabel(osComposite, LaunchMessages.XPCP_OSLabel);
    this.fOSCombo = new Combo(osComposite, SWT.READ_ONLY);
    this.fOSCombo.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    
    final Group compilingGroup = new Group(sectionClient, SWT.NONE);
    compilingGroup.setFont(sectionClient.getFont());
    compilingGroup.setLayout(new GridLayout(1, false));
    compilingGroup.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    compilingGroup.setText(LaunchMessages.XPCP_CompilingGroup);
        
    final Pair<Text, Button> compPair = createLabelTextBrowseBt(compilingGroup, LaunchMessages.XPCP_CompilerLabel, 
                                                      LaunchMessages.XPCP_BrowseBt, toolkit);
    this.fCompilerText = compPair.first;
    this.fCompilerBrowseBt = compPair.second;
    
    this.fCompilingOptsText = SWTFormUtils.createLabelAndText(compilingGroup, LaunchMessages.XPCP_CompilingOptsLabel,
                                                              toolkit, 3);    
    
    final Group archivingGroup = new Group(sectionClient, SWT.NONE);
    archivingGroup.setFont(sectionClient.getFont());
    archivingGroup.setLayout(new GridLayout(1, false));
    archivingGroup.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    archivingGroup.setText(LaunchMessages.XPCP_ArchivingGroup);
    
    final Pair<Text, Button> archPair = createLabelTextBrowseBt(archivingGroup, LaunchMessages.XPCP_ArchiverLabel, 
                                                                LaunchMessages.XPCP_BrowseBt, toolkit);
    this.fArchiverText = archPair.first;
    this.fArchiverBrowseBt = archPair.second;
    
    this.fArchivingOptsText = SWTFormUtils.createLabelAndText(archivingGroup, LaunchMessages.XPCP_ArchivingOptsLabel,
                                                              toolkit, 3);
    
    final Group linkingGroup = new Group(sectionClient, SWT.NONE);
    linkingGroup.setFont(sectionClient.getFont());
    linkingGroup.setLayout(new GridLayout(1, false));
    linkingGroup.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    linkingGroup.setText(LaunchMessages.XPCP_LinkingGroup);
    
    final Pair<Text, Button> linkPair = createLabelTextBrowseBt(linkingGroup, LaunchMessages.XPCP_LinkerLabel, 
                                                                LaunchMessages.XPCP_BrowseBt, toolkit);
    this.fLinkerText = linkPair.first;
    this.fLinkerBrowseBt = linkPair.second;
    
    this.fLinkingOptsText = SWTFormUtils.createLabelAndText(linkingGroup, LaunchMessages.XPCP_LinkingOptsLabel, toolkit, 3);

    this.fLinkingLibsText = SWTFormUtils.createLabelAndText(linkingGroup, LaunchMessages.XPCP_LinkingLibsLabel, toolkit, 3);
    
    final Composite archComposite = toolkit.createComposite(sectionClient);
    archComposite.setFont(getSection().getFont());
    archComposite.setLayout(new GridLayout(1, false));
    archComposite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    
    this.fArchBt = toolkit.createButton(archComposite, LaunchMessages.XPCP_ArchitectureBt, SWT.CHECK);
    
    for (final ETargetOS targetOs : ETargetOS.values()) {
      this.fOSCombo.add(targetOs.name());
      this.fOSCombo.setData(targetOs.name(), targetOs);
    }
    
    initializeControls(managedForm);
    
    addListeners(managedForm, this.fCompilerText, this.fCompilingOptsText, this.fArchiverText, this.fArchivingOptsText, 
                 this.fLinkerText, this.fLinkingOptsText, this.fLinkingLibsText, this.fArchBt, this.fOSCombo);

    getSection().setClient(sectionClient);
  }
  
  private boolean hasCompleteInfo() {
    return (this.fOSCombo.getSelectionIndex() != -1) && (this.fCompilerText.getText().trim().length() > 0) &&
           (this.fCompilingOptsText.getText().trim().length() > 0) && (this.fArchiverText.getText().trim().length() > 0) &&
           (this.fArchivingOptsText.getText().trim().length() > 0) && (this.fLinkerText.getText().trim().length() > 0) &&
           (this.fLinkingOptsText.getText().trim().length() > 0) && (this.fLinkingLibsText.getText().trim().length() > 0);
  }
  
  private void initializeControls(final IManagedForm managedForm) {
    final ICppCompilationConf cppCompConf = getPlatformConf().getCppCompilationConf();
    if (cppCompConf.getTargetOS() != null) {
      int index = -1;
      for (final ETargetOS targetOS : ETargetOS.values()) {
        ++index;
        if (targetOS == cppCompConf.getTargetOS()) {
          this.fOSCombo.select(index);
        }
      }
      this.fCompilerText.setText(cppCompConf.getCompiler());
      handleTextValidation(new EmptyTextInputChecker(this.fCompilerText, LaunchMessages.XPCP_CompilerLabel), managedForm,
                           this.fCompilerText);
      this.fCompilingOptsText.setText(cppCompConf.getCompilingOpts(false));
      handleTextValidation(new EmptyTextInputChecker(this.fCompilingOptsText, LaunchMessages.XPCP_CompilingOptsLabel), 
                           managedForm, this.fCompilingOptsText);
      this.fArchiverText.setText(cppCompConf.getArchiver());
      handleTextValidation(new EmptyTextInputChecker(this.fArchiverText, LaunchMessages.XPCP_ArchiverLabel), managedForm,
                           this.fArchiverText);
      this.fArchivingOptsText.setText(cppCompConf.getArchivingOpts(false));
      handleTextValidation(new EmptyTextInputChecker(this.fArchivingOptsText, LaunchMessages.XPCP_ArchivingOptsLabel), 
                           managedForm, this.fArchivingOptsText);
      this.fLinkerText.setText(cppCompConf.getLinker());
      handleTextValidation(new EmptyTextInputChecker(this.fLinkerText, LaunchMessages.XPCP_LinkerLabel), managedForm,
                           this.fLinkerText);
      this.fLinkingOptsText.setText(cppCompConf.getLinkingOpts(false));
      handleTextValidation(new EmptyTextInputChecker(this.fLinkingOptsText, LaunchMessages.XPCP_LinkingOptsLabel), managedForm,
                           this.fLinkingOptsText);
      this.fLinkingLibsText.setText(cppCompConf.getLinkingLibs(false));
      handleTextValidation(new EmptyTextInputChecker(this.fLinkingLibsText, LaunchMessages.XPCP_LinkingLibsLabel), managedForm,
                           this.fLinkingLibsText);
    }

    this.fArchBt.setSelection(cppCompConf.getArchitecture() == EArchitecture.E64Arch);
  }
  
  // --- Fields
  
  private Combo fOSCombo;
  
  private Text fCompilerText;
  
  private Text fCompilingOptsText;
  
  private Text fArchiverText;
  
  private Text fArchivingOptsText;
  
  private Text fLinkerText;
  
  private Text fLinkingOptsText;
  
  private Text fLinkingLibsText;
  
  private Button fArchBt;
  
  private Button fCompilerBrowseBt;
  
  private Button fArchiverBrowseBt;
  
  private Button fLinkerBrowseBt;

}
