/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.editors;

import java.util.Arrays;

import org.eclipse.imp.utils.Pair;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import x10dt.ui.launch.core.platform_conf.EArchitecture;
import x10dt.ui.launch.core.platform_conf.EBitsArchitecture;
import x10dt.ui.launch.core.platform_conf.ETargetOS;
import x10dt.ui.launch.core.platform_conf.ETransport;
import x10dt.ui.launch.core.platform_conf.EValidationStatus;
import x10dt.ui.launch.core.utils.IProcessOuputListener;
import x10dt.ui.launch.core.utils.KeyboardUtils;
import x10dt.ui.launch.core.utils.PTPConstants;
import x10dt.ui.launch.core.utils.SWTFormUtils;
import x10dt.ui.launch.cpp.LaunchMessages;
import x10dt.ui.launch.cpp.builder.target_op.ITargetOpHelper;
import x10dt.ui.launch.cpp.builder.target_op.TargetOpHelperFactory;
import x10dt.ui.launch.cpp.editors.form_validation.FormCheckerFactory;
import x10dt.ui.launch.cpp.editors.form_validation.IFormControlChecker;
import x10dt.ui.launch.cpp.platform_conf.IConnectionConf;
import x10dt.ui.launch.cpp.platform_conf.ICppCompilationConf;
import x10dt.ui.launch.cpp.platform_conf.IX10PlatformConf;
import x10dt.ui.launch.cpp.platform_conf.cpp_commands.DefaultCPPCommandsFactory;
import x10dt.ui.launch.cpp.platform_conf.cpp_commands.IDefaultCPPCommands;
import x10dt.ui.launch.cpp.utils.PlatformConfUtils;


final class CompilationAndLinkingSectionPart extends AbstractCommonSectionFormPart 
                                             implements IConnectionTypeListener, IServiceProviderChangeListener, IFormPart {

  CompilationAndLinkingSectionPart(final Composite parent, final X10FormPage formPage) {
    super(parent, formPage);
    
    getSection().setFont(parent.getFont());
    getSection().setText(LaunchMessages.XPCP_CompilationLinkingSection);
    getSection().setDescription(LaunchMessages.XPCP_CompilationLinkingSectionDescr);
    final TableWrapData twData = new TableWrapData(TableWrapData.FILL_GRAB);
    twData.rowspan = 2;
    getSection().setLayoutData(twData);
    
    createClient(formPage.getManagedForm(), formPage.getManagedForm().getToolkit());
    addCompletePartListener(formPage);
  }

  
  // --- IConnectionTypeListener's interface methods implementation
  
  public void connectionChanged(final boolean isLocal, final String remoteConnectionName, 
                                final EValidationStatus validationStatus, final boolean newCurrent) {
    final boolean shouldEnable = isLocal || validationStatus == EValidationStatus.VALID;
    this.fCompilerBrowseBt.setEnabled(shouldEnable);
    this.fArchiverBrowseBt.setEnabled(shouldEnable);
    this.fLinkerBrowseBt.setEnabled(shouldEnable);
    if (shouldEnable && newCurrent) {
      selectOsAndArchitecture();
      checkCompilerVersion(this.fCompilerText, this.fOSCombo, this.fArchCombo);
    }
  }
  
  // --- IServiceProviderChangeListener's interface methods implementation
  
  public void serviceTypeChange(final String serviceTypeId) {
    final String archName = this.fArchCombo.getItem(this.fArchCombo.getSelectionIndex());
    final EArchitecture architecture = (EArchitecture) this.fArchCombo.getData(archName);
    final String osName = this.fOSCombo.getItem(this.fOSCombo.getSelectionIndex());
    final ETargetOS targetOS = (ETargetOS) this.fOSCombo.getData(osName);
    
    this.fDebugBt.setEnabled(PTPConstants.OPEN_MPI_SERVICE_PROVIDER_ID.equals(serviceTypeId));
    
    updateCompilationCommands(this.fCompilerText, this.fCompilingOptsText, this.fArchiverText, this.fArchivingOptsText, 
                              this.fLinkerText, this.fLinkingOptsText, this.fLinkingLibsText, this.fBitsArchBt, 
                              architecture, targetOS);
  }


  public void serviceModeChange(final String serviceModeId) {
    // Nothing to do.
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
                            final Text linkingOptsText, final Text linkingLibsText, final Button bitsArchBt,
                            final Combo osCombo, final Combo archCombo, final Button debugBt) {
		compilerText.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent event) {
			  handleEmptyTextValidation(compilerText, LaunchMessages.XPCP_CompilerLabel);

				getPlatformConf().setCompiler(compilerText.getText());
				setPartCompleteFlag(hasCompleteInfo());
				updateDirtyState(managedForm);
			}
		});
    compilingOptsText.addModifyListener(new ModifyListener() {
      
      public void modifyText(final ModifyEvent event) {
        handleEmptyTextValidation(compilingOptsText, LaunchMessages.XPCP_CompilingOptsLabel);
        getPlatformConf().setCompilingOpts(compilingOptsText.getText());
        setPartCompleteFlag(hasCompleteInfo());
        updateDirtyState(managedForm);
      }
      
    });
    archiverText.addModifyListener(new ModifyListener() {
      
      public void modifyText(final ModifyEvent event) {
        handleEmptyTextValidation(archiverText, LaunchMessages.XPCP_ArchiverLabel);
        getPlatformConf().setArchiver(archiverText.getText());
        setPartCompleteFlag(hasCompleteInfo());
        updateDirtyState(managedForm);
      }
      
    });
    archivingOptsText.addModifyListener(new ModifyListener() {
      
      public void modifyText(final ModifyEvent event) {
        handleEmptyTextValidation(archivingOptsText, LaunchMessages.XPCP_ArchivingOptsLabel);
        getPlatformConf().setArchivingOpts(archivingOptsText.getText());
        setPartCompleteFlag(hasCompleteInfo());
        updateDirtyState(managedForm);
      }
      
    });
    linkerText.addModifyListener(new ModifyListener() {
      
      public void modifyText(final ModifyEvent event) {
        handleEmptyTextValidation(linkerText, LaunchMessages.XPCP_LinkerLabel);
        getPlatformConf().setLinker(linkerText.getText());
        setPartCompleteFlag(hasCompleteInfo());
        updateDirtyState(managedForm);
      }
      
    });
    linkingOptsText.addModifyListener(new ModifyListener() {
      
      public void modifyText(final ModifyEvent event) {
        handleEmptyTextValidation(linkingOptsText, LaunchMessages.XPCP_LinkingOptsLabel);
        getPlatformConf().setLinkingOpts(linkingOptsText.getText());
        setPartCompleteFlag(hasCompleteInfo());
        updateDirtyState(managedForm);
      }
      
    });
    linkingLibsText.addModifyListener(new ModifyListener() {
      
      public void modifyText(final ModifyEvent event) {
        handleEmptyTextValidation(linkingLibsText, LaunchMessages.XPCP_LinkingLibsLabel);
        getPlatformConf().setLinkingLibs(linkingLibsText.getText());
        setPartCompleteFlag(hasCompleteInfo());
        updateDirtyState(managedForm);
      }
      
    });
    bitsArchBt.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        getPlatformConf().setBitsArchitecture(bitsArchBt.getSelection() ? EBitsArchitecture.E64Arch : 
                                                                          EBitsArchitecture.E32Arch);
        
        final String archName = archCombo.getItem(archCombo.getSelectionIndex());
        final EArchitecture architecture = (EArchitecture) archCombo.getData(archName);
        final String osName = osCombo.getItem(osCombo.getSelectionIndex());
        final ETargetOS targetOs = (ETargetOS) osCombo.getData(osName);
        
        updateCompilationCommands(compilerText, compilingOptsText, archiverText, archivingOptsText, linkerText,
                                  linkingOptsText, linkingLibsText, bitsArchBt, architecture, targetOs);
        
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
        
        final String archName = archCombo.getItem(archCombo.getSelectionIndex());
        final EArchitecture architecture = (EArchitecture) archCombo.getData(archName);
        
        updateCompilationCommands(compilerText, compilingOptsText, archiverText, archivingOptsText, linkerText,
                                  linkingOptsText, linkingLibsText, bitsArchBt, architecture, targetOs);
        updateDirtyState(managedForm);
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
      
    });
    archCombo.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        final String archName = archCombo.getItem(archCombo.getSelectionIndex());
        final EArchitecture architecture = (EArchitecture) archCombo.getData(archName);
        getPlatformConf().setArchitecture(architecture);
        
        final String osName = osCombo.getItem(osCombo.getSelectionIndex());
        final ETargetOS targetOs = (ETargetOS) osCombo.getData(osName);
        
        updateCompilationCommands(compilerText, compilingOptsText, archiverText, archivingOptsText, linkerText,
                                  linkingOptsText, linkingLibsText, bitsArchBt, architecture, targetOs);
        updateDirtyState(managedForm);
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
      
    });
    debugBt.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        final String archName = archCombo.getItem(archCombo.getSelectionIndex());
        final EArchitecture architecture = (EArchitecture) archCombo.getData(archName);
        final String osName = osCombo.getItem(osCombo.getSelectionIndex());
        final ETargetOS targetOs = (ETargetOS) osCombo.getData(osName);
        
        updateCompilationCommands(compilerText, compilingOptsText, archiverText, archivingOptsText, linkerText,
                                  linkingOptsText, linkingLibsText, bitsArchBt, architecture, targetOs);
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
      
    });
  }
  
  private void checkCompilerVersion(final Text compilerText, final Combo osCombo, final Combo archCombo) {
    final IX10PlatformConf platformConf = getPlatformConf();
    final ICppCompilationConf cppCompConf = platformConf.getCppCompilationConf();
    final IConnectionConf connConf = platformConf.getConnectionConf();
    final ITargetOpHelper targetOpHelper = TargetOpHelperFactory.create(connConf.isLocal(), 
                                                                        cppCompConf.getTargetOS() ==  ETargetOS.WINDOWS, 
                                                                        connConf.getConnectionName());
    if (targetOpHelper != null) {
      final String osName = osCombo.getItem(osCombo.getSelectionIndex());
      final ETargetOS targetOs = (ETargetOS) osCombo.getData(osName);
      final String archName = archCombo.getItem(archCombo.getSelectionIndex());
      final EArchitecture architecture = (EArchitecture) archCombo.getData(archName);
    
      final IFormControlChecker versionChecker = FormCheckerFactory.createCPPCompilerVersionChecker(targetOpHelper, targetOs, 
                                                                                                    architecture, 
                                                                                                    getFormPage(),
                                                                                                    compilerText);
      setPartCompleteFlag(hasCompleteInfo() && versionChecker.validate(compilerText.getText().trim()));
    }
  }

  
  private void createClient(final IManagedForm managedForm, final FormToolkit toolkit) {
    final Composite sectionClient = toolkit.createComposite(getSection());
    sectionClient.setLayout(new TableWrapLayout());
    sectionClient.setFont(getSection().getFont());
    sectionClient.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
    
    final Composite osComposite = toolkit.createComposite(sectionClient);
    osComposite.setFont(getSection().getFont());
    final TableWrapLayout osLayout = new TableWrapLayout();
    osLayout.numColumns = 2;
    osComposite.setLayout(osLayout);
    osComposite.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
    
    final Label osLabel = toolkit.createLabel(osComposite, LaunchMessages.XPCP_OSLabel);
    osLabel.setLayoutData(new TableWrapData(TableWrapData.FILL, TableWrapData.MIDDLE));
    this.fOSCombo = new Combo(osComposite, SWT.READ_ONLY);
    this.fOSCombo.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
    for (final ETargetOS targetOs : ETargetOS.values()) {
      this.fOSCombo.add(targetOs.name());
      this.fOSCombo.setData(targetOs.name(), targetOs);
    }
    
    final Composite archComposite = toolkit.createComposite(sectionClient);
    archComposite.setFont(getSection().getFont());
    final TableWrapLayout archLayout = new TableWrapLayout();
    archLayout.numColumns = 3;
    archComposite.setLayout(archLayout);
    archComposite.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
    
    final Label archLabel = toolkit.createLabel(archComposite, LaunchMessages.XPCP_Architecture);
    archLabel.setLayoutData(new TableWrapData(TableWrapData.FILL, TableWrapData.MIDDLE));
    this.fArchCombo = new Combo(archComposite, SWT.READ_ONLY);
    this.fArchCombo.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
    for (final EArchitecture arch : EArchitecture.values()) {
      this.fArchCombo.add(arch.name());
      this.fArchCombo.setData(arch.name(), arch);
    }
    
    this.fBitsArchBt = toolkit.createButton(archComposite, LaunchMessages.XPCP_64BitsArchitectureBt, SWT.CHECK);
    
    this.fDebugBt = toolkit.createButton(archComposite, LaunchMessages.XPCP_UseMPILibForDebugger, SWT.CHECK);
    this.fDebugBt.setLayoutData(new TableWrapData(TableWrapData.FILL, TableWrapData.MIDDLE, 1, 3));
    this.fDebugBt.setSelection(true);
    final String serviceTypeId = getPlatformConf().getCommunicationInterfaceConf().getServiceTypeId();
    this.fDebugBt.setEnabled(PTPConstants.OPEN_MPI_SERVICE_PROVIDER_ID.equals(serviceTypeId));
    
    final Group compilingGroup = new Group(sectionClient, SWT.NONE);
    compilingGroup.setFont(sectionClient.getFont());
    compilingGroup.setLayout(new TableWrapLayout());
    compilingGroup.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
    compilingGroup.setText(LaunchMessages.XPCP_CompilingGroup);
        
    final Pair<Text, Button> compPair = createLabelTextBrowseBt(compilingGroup, LaunchMessages.XPCP_CompilerLabel, 
                                                                LaunchMessages.XPCP_BrowseBt, toolkit);
    this.fCompilerText = compPair.first;
    this.fCompilerBrowseBt = compPair.second;
    
    this.fCompilingOptsText = SWTFormUtils.createLabelAndText(compilingGroup, LaunchMessages.XPCP_CompilingOptsLabel,
                                                              toolkit, 3);    
    
    final Group archivingGroup = new Group(sectionClient, SWT.NONE);
    archivingGroup.setFont(sectionClient.getFont());
    archivingGroup.setLayout(new TableWrapLayout());
    archivingGroup.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
    archivingGroup.setText(LaunchMessages.XPCP_ArchivingGroup);
    
    final Pair<Text, Button> archPair = createLabelTextBrowseBt(archivingGroup, LaunchMessages.XPCP_ArchiverLabel, 
                                                                LaunchMessages.XPCP_BrowseBt, toolkit);
    this.fArchiverText = archPair.first;
    this.fArchiverBrowseBt = archPair.second;
    
    this.fArchivingOptsText = SWTFormUtils.createLabelAndText(archivingGroup, LaunchMessages.XPCP_ArchivingOptsLabel,
                                                              toolkit, 3);
    
    final Group linkingGroup = new Group(sectionClient, SWT.NONE);
    linkingGroup.setFont(sectionClient.getFont());
    linkingGroup.setLayout(new TableWrapLayout());
    linkingGroup.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
    linkingGroup.setText(LaunchMessages.XPCP_LinkingGroup);
    
    final Pair<Text, Button> linkPair = createLabelTextBrowseBt(linkingGroup, LaunchMessages.XPCP_LinkerLabel, 
                                                                LaunchMessages.XPCP_BrowseBt, toolkit);
    this.fLinkerText = linkPair.first;
    this.fLinkerBrowseBt = linkPair.second;
    
    this.fLinkingOptsText = SWTFormUtils.createLabelAndText(linkingGroup, LaunchMessages.XPCP_LinkingOptsLabel, toolkit, 3);

    this.fLinkingLibsText = SWTFormUtils.createLabelAndText(linkingGroup, LaunchMessages.XPCP_LinkingLibsLabel, toolkit, 3);
    
    initializeControls();
    
    addListeners(managedForm, this.fCompilerText, this.fCompilingOptsText, this.fArchiverText, this.fArchivingOptsText, 
                 this.fLinkerText, this.fLinkingOptsText, this.fLinkingLibsText, this.fBitsArchBt, this.fOSCombo,
                 this.fArchCombo, this.fDebugBt);

    getSection().setClient(sectionClient);
  }
  
  private boolean hasCompleteInfo() {
    return (this.fOSCombo.getSelectionIndex() != -1) && (this.fCompilerText.getText().trim().length() > 0) &&
           (this.fCompilingOptsText.getText().trim().length() > 0) && (this.fArchiverText.getText().trim().length() > 0) &&
           (this.fArchivingOptsText.getText().trim().length() > 0) && (this.fLinkerText.getText().trim().length() > 0) &&
           (this.fLinkingOptsText.getText().trim().length() > 0) && (this.fLinkingLibsText.getText().trim().length() > 0);
  }
  
  private void initializeControls() {
    final ICppCompilationConf cppCompConf = getPlatformConf().getCppCompilationConf();
    int index = -1;
    for (final ETargetOS targetOS : ETargetOS.values()) {
      ++index;
      if (targetOS == cppCompConf.getTargetOS()) {
        this.fOSCombo.select(index);
      }
    }
    index = -1;
    for (final EArchitecture arch : EArchitecture.values()) {
      ++index;
      if (arch == cppCompConf.getArchitecture()) {
        this.fArchCombo.select(index);
      }
    }
    
    this.fCompilerText.setText(cppCompConf.getCompiler());
    handleEmptyTextValidation(this.fCompilerText, LaunchMessages.XPCP_CompilerLabel);
    KeyboardUtils.addDelayedActionOnControl(this.fCompilerText, new Runnable() {
      
      public void run() {
        getFormPage().getEditorSite().getShell().getDisplay().asyncExec(new Runnable() {
          
          public void run() {
            checkCompilerVersion(CompilationAndLinkingSectionPart.this.fCompilerText, 
                                 CompilationAndLinkingSectionPart.this.fOSCombo,
                                 CompilationAndLinkingSectionPart.this.fArchCombo);
          }
          
        });
      }
      
    });
    this.fCompilingOptsText.setText(cppCompConf.getCompilingOpts(false));
    handleEmptyTextValidation(this.fCompilingOptsText, LaunchMessages.XPCP_CompilingOptsLabel);
    this.fArchiverText.setText(cppCompConf.getArchiver());
    handleEmptyTextValidation(this.fArchiverText, LaunchMessages.XPCP_ArchiverLabel);
    this.fArchivingOptsText.setText(cppCompConf.getArchivingOpts(false));
    handleEmptyTextValidation(this.fArchivingOptsText, LaunchMessages.XPCP_ArchivingOptsLabel);
    this.fLinkerText.setText(cppCompConf.getLinker());
    handleEmptyTextValidation(this.fLinkerText, LaunchMessages.XPCP_LinkerLabel);
    this.fLinkingOptsText.setText(cppCompConf.getLinkingOpts(false));
    handleEmptyTextValidation(this.fLinkingOptsText, LaunchMessages.XPCP_LinkingOptsLabel);
    this.fLinkingLibsText.setText(cppCompConf.getLinkingLibs(false));
    handleEmptyTextValidation(this.fLinkingLibsText, LaunchMessages.XPCP_LinkingLibsLabel);

    this.fBitsArchBt.setSelection(cppCompConf.getBitsArchitecture() == EBitsArchitecture.E64Arch);
    
    if (this.fDebugBt.isEnabled() && this.fDebugBt.getSelection()) {
      updateCompilationCommands(this.fCompilerText, this.fCompilingOptsText, this.fArchiverText, this.fArchivingOptsText, 
                                this.fLinkerText, this.fLinkingOptsText, this.fLinkingLibsText, this.fBitsArchBt, 
                                cppCompConf.getArchitecture(), cppCompConf.getTargetOS());
    }
  }
  
  private void selectArchitecture(final ITargetOpHelper targetOpHelper) {
    final LastLineOutputListener processorListener = new LastLineOutputListener();
    try {
      targetOpHelper.run(Arrays.asList(UNAME, UNAME_P_OPT), processorListener);
    } catch (Exception except) {
      // Do nothing.
    }

    String output = processorListener.getOutput();
    if (output == null) {
      // We fail to get an output with "uname -p". Last option is to try with "uname -m".
      final LastLineOutputListener machineListener = new LastLineOutputListener();
      try {
        targetOpHelper.run(Arrays.asList(UNAME, UNAME_M_OPT), machineListener);
        
        output = machineListener.getOutput();
      } catch (Exception except) {
        // Do nothing.
      }
    }
      
    EArchitecture architecture = null;
    if (output != null) {  
      if (output.matches(I86_REGEX) || output.startsWith(X86_PROC)) {
        architecture = EArchitecture.x86;
      } else if (output.contains(POWER) || output.contains(PPC)) {
        architecture = EArchitecture.Power;
      }
      this.fBitsArchBt.setSelection(output.contains(A64BITS));
      this.fBitsArchBt.notifyListeners(SWT.Selection, new Event());
    }
    
    if (architecture != null) {
      int index = -1;
      for (final String archName : this.fArchCombo.getItems()) {
        ++index;
        final EArchitecture curArch = (EArchitecture) this.fArchCombo.getData(archName);
        if (curArch == architecture) {
          this.fArchCombo.select(index);
          this.fArchCombo.notifyListeners(SWT.Selection, new Event());
          break;
        }
      }
    }
  }
  
  private void selectOS(final ITargetOpHelper targetOpHelper) {
    final OSDetectionListener osDetectionListener = new OSDetectionListener();
    try {
      targetOpHelper.run(Arrays.asList(UNAME, UNAME_S_OPT), osDetectionListener);
      final ETargetOS detectedOS = osDetectionListener.getDetectedOS();
      int index = -1;
      for (final String osName : this.fOSCombo.getItems()) {
        ++index;
        final ETargetOS targetOS = (ETargetOS) this.fOSCombo.getData(osName);
        if (detectedOS == targetOS) {
          this.fOSCombo.select(index);
          break;
        }
      }
      this.fOSCombo.notifyListeners(SWT.Selection, new Event());
    } catch (Exception except) {
      // Do nothing. Simply forgets.
    }
  }
  
  private void selectOsAndArchitecture() {
    final IX10PlatformConf platformConf = getPlatformConf();
    final ICppCompilationConf cppCompConf = platformConf.getCppCompilationConf();
    final IConnectionConf connConf = platformConf.getConnectionConf();
    final ITargetOpHelper targetOpHelper = TargetOpHelperFactory.create(connConf.isLocal(), 
                                                                        cppCompConf.getTargetOS() ==  ETargetOS.WINDOWS, 
                                                                        connConf.getConnectionName());
    if (targetOpHelper != null) {
      selectOS(targetOpHelper);
      selectArchitecture(targetOpHelper);
    }
  }
  
  private void updateCompilationCommands(final Text compilerText, final Text compilingOptsText, final Text archiverText,
                                         final Text archivingOptsText, final Text linkerText, final Text linkingOptsText,
                                         final Text linkingLibsText, final Button bitsArchBt, final EArchitecture architecture,
                                         final ETargetOS targetOS) {
    final boolean is64Arch = bitsArchBt.getSelection();
    final String serviceTypeId = getPlatformConf().getCommunicationInterfaceConf().getServiceTypeId();
    final ETransport transport = PlatformConfUtils.getTransport(serviceTypeId, targetOS);
    
    final IDefaultCPPCommands defaultCPPCommands;
    switch (targetOS) {
      case AIX:
        defaultCPPCommands = DefaultCPPCommandsFactory.createAixCommands(is64Arch, architecture, transport);
        break;
      case LINUX:
        defaultCPPCommands = DefaultCPPCommandsFactory.createLinuxCommands(is64Arch, architecture, transport);
        break;
      case MAC:
        defaultCPPCommands = DefaultCPPCommandsFactory.createMacCommands(is64Arch, architecture, transport);
        break;
      case WINDOWS:
        defaultCPPCommands = DefaultCPPCommandsFactory.createCygwinCommands(is64Arch, architecture, transport);
        break;
      default:
        defaultCPPCommands = DefaultCPPCommandsFactory.createUnkownUnixCommands(is64Arch, architecture, transport);
    }
    compilerText.setText(defaultCPPCommands.getCompiler());
    compilingOptsText.setText(defaultCPPCommands.getCompilerOptions());
    archiverText.setText(defaultCPPCommands.getArchiver());
    archivingOptsText.setText(defaultCPPCommands.getArchivingOpts());
    linkerText.setText(defaultCPPCommands.getLinker());
    linkingOptsText.setText(defaultCPPCommands.getLinkingOptions());
    linkingLibsText.setText(defaultCPPCommands.getLinkingLibraries());
    
    if (this.fDebugBt.isEnabled() && this.fDebugBt.getSelection()) {
      compilerText.setText(MPICXX);
      linkerText.setText(MPICXX);
      linkingLibsText.setText(this.fLinkingLibsText.getText().replace(X10RT_PGAS_SOCKETS, X10RT_MPI));
    }
  }
  
  // --- Private classes
  
  private static final class OSDetectionListener implements IProcessOuputListener {

    // --- Interface methods implementation
    
    public void read(final String line) {
      this.fOutput = line;
    }

    public void readError(final String line) {
    }
    
    // --- Internal services
    
    ETargetOS getDetectedOS() {
      final String output = this.fOutput.toLowerCase();
      if (LINUX.equals(output)) {
        return ETargetOS.LINUX;
      } else if (AIX.equals(output)) {
        return ETargetOS.AIX;
      } else if (DARWIN.equals(output)) {
        return ETargetOS.MAC;
      } else if (output.startsWith(CYGWIN)) {
        return ETargetOS.WINDOWS;
      } else {
        return ETargetOS.UNIX;
      }
    }
    
    // --- Fields
    
    private String fOutput;
    
    
    private static final String LINUX = "linux"; //$NON-NLS-1$
    
    private static final String CYGWIN = "cygwin"; //$NON-NLS-1$
    
    private static final String AIX = "aix"; //$NON-NLS-1$
    
    private static final String DARWIN = "darwin"; //$NON-NLS-1$
    
  }
  
  private static final class LastLineOutputListener implements IProcessOuputListener {

    // --- Interface methods implementation
    
    public void read(final String line) {
      this.fOutput = line;
    }

    public void readError(final String line) {
    }
    
    // --- Internal services
    
    String getOutput() {
      return this.fOutput;
    }
    
    // --- Fields
    
    private String fOutput;
    
  }
  
  // --- Fields
  
  private Combo fOSCombo;
  
  private Combo fArchCombo;
  
  private Text fCompilerText;
  
  private Text fCompilingOptsText;
  
  private Text fArchiverText;
  
  private Text fArchivingOptsText;
  
  private Text fLinkerText;
  
  private Text fLinkingOptsText;
  
  private Text fLinkingLibsText;
  
  private Button fBitsArchBt;
  
  private Button fCompilerBrowseBt;
  
  private Button fArchiverBrowseBt;
  
  private Button fLinkerBrowseBt;
  
  private Button fDebugBt;

  
  
  private static final String UNAME = "uname"; //$NON-NLS-1$
  
  private static final String UNAME_S_OPT = "-s"; //$NON-NLS-1$
  
  private static final String UNAME_P_OPT = "-p"; //$NON-NLS-1$
  
  private static final String UNAME_M_OPT = "-m"; //$NON-NLS-1$
  
  private static final String X86_PROC = "x86"; //$NON-NLS-1$
  
  private static final String I86_REGEX = "i.86"; //$NON-NLS-1$
  
  private static final String POWER = "power"; //$NON-NLS-1$
  
  private static final String PPC = "ppc"; //$NON-NLS-1$
  
  private static final String A64BITS = "64"; //$NON-NLS-1$
  
  private static final String MPICXX = "mpicxx"; //$NON-NLS-1$
  
  private static final String X10RT_MPI = "-lx10rt_mpi"; //$NON-NLS-1$
  
  private static final String X10RT_PGAS_SOCKETS = "-lx10rt_pgas_sockets"; //$NON-NLS-1$
  
}
