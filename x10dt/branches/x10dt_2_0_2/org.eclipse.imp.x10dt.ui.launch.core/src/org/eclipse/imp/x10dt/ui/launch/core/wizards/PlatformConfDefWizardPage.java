/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.core.wizards;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.imp.x10dt.ui.launch.core.Constants;
import org.eclipse.imp.x10dt.ui.launch.core.Messages;
import org.eclipse.imp.x10dt.ui.launch.core.platform_conf.EArchitecture;
import org.eclipse.imp.x10dt.ui.launch.core.platform_conf.ETargetOS;
import org.eclipse.imp.x10dt.ui.launch.core.platform_conf.EValidStatus;
import org.eclipse.imp.x10dt.ui.launch.core.platform_conf.IX10PlatformConfiguration;
import org.eclipse.imp.x10dt.ui.launch.core.utils.ErrorUtils;
import org.eclipse.imp.x10dt.ui.launch.core.utils.PTPUtils;
import org.eclipse.imp.x10dt.ui.launch.core.utils.WizardUtils;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ptp.core.IModelManager;
import org.eclipse.ptp.core.PTPCorePlugin;
import org.eclipse.ptp.core.elements.IPUniverse;
import org.eclipse.ptp.core.elements.IResourceManager;
import org.eclipse.ptp.core.elements.attributes.ResourceManagerAttributes;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.osgi.framework.Bundle;


final class PlatformConfDefWizardPage extends WizardPage implements IWizardPage, IPlaftormConfWizardPage {
  
  PlatformConfDefWizardPage(final IX10PlatformConfiguration platformConfiguration) {
    super("DefWP"); //$NON-NLS-1$
    
    setPageComplete(false);
    
    this.fDefaultPlatformConf = platformConfiguration;
    this.fIsCplusPlus = (platformConfiguration != null) ? platformConfiguration.isCplusPlus() : false;
    this.fIsLocal = (platformConfiguration != null) ? platformConfiguration.isLocal() : false;
    
    final Bundle x10DistBundle = Platform.getBundle(Constants.X10_DIST_PLUGIN_ID);
    if (x10DistBundle == null) {
      ErrorUtils.dialogWithLog(getShell(), Messages.PCDWP_NoBundleDialogTitle, IStatus.ERROR, 
                               NLS.bind(Messages.PCDWP_NoBundleDialogMsg, Constants.X10_DIST_PLUGIN_ID));
      throw new AssertionError();
    }
    try {
      this.fLocalHeadersFile = getLocalFile(x10DistBundle.getResource(INCLUDE_DIR));
      this.fLocalLibsFile = getLocalFile(x10DistBundle.getResource(LIB_DIR));
    } catch (Exception except) {
      ErrorUtils.dialogWithLog(getShell(), Messages.PCDWP_URLErrorDialogTitle, IStatus.ERROR, 
                               NLS.bind(Messages.PCDWP_URLErrorDialogMsg, Constants.X10_DIST_PLUGIN_ID), except);
      throw new AssertionError();
    }
  }
  
  // --- IDialogPage's interface methods implementation

  public void createControl(final Composite parent) {
    initializeDialogUnits(parent);
    
    final Composite composite = new Composite(parent, SWT.NONE);
    composite.setFont(parent.getFont());
    composite.setLayout(new GridLayout(1, false));
    composite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, false, false));
    
    createResourceManager(composite);
    createTargetOperatingSystem(composite);
    
    if (! this.fIsLocal) {
      final Group distribLocGroup = new Group(composite, SWT.NONE);
      distribLocGroup.setFont(composite.getFont());
      distribLocGroup.setLayout(new GridLayout(1, false));
      distribLocGroup.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
      distribLocGroup.setText(Messages.PCDWP_DistribGroup);
      this.fPGASLocText = createX10PathLocation(distribLocGroup, Messages.PCDWP_PGASDistribLoc);
      this.fX10LocText = createX10PathLocation(distribLocGroup, Messages.PCDWP_X10DistribLoc);
    }
    
    if (! this.fIsLocal || this.fIsCplusPlus) {
      final Group compilationGroup = new Group(composite, SWT.NONE);
      compilationGroup.setFont(composite.getFont());
      compilationGroup.setLayout(new GridLayout(1, false));
      compilationGroup.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
      compilationGroup.setText(Messages.PCDWP_CompilationGroup);
      createCompilation(compilationGroup);
    
      if (this.fIsCplusPlus) {
        final Group archivingGroup = new Group(composite, SWT.NONE);
        archivingGroup.setFont(composite.getFont());
        archivingGroup.setLayout(new GridLayout(1, false));
        archivingGroup.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
        archivingGroup.setText(Messages.PCDWP_ArchivingGroup);
        createArchiving(archivingGroup);
        
        final Group linkingGroup = new Group(composite, SWT.NONE);
        linkingGroup.setFont(composite.getFont());
        linkingGroup.setLayout(new GridLayout(1, false));
        linkingGroup.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
        linkingGroup.setText(Messages.PCDWP_LinkingGroup);
        createLinking(linkingGroup);
      }
    }
    
    if (this.fDefaultPlatformConf != null) {
      initDefaultConfValues();
    } else if (this.fIsLocal) {
      this.fTargetOSCombo.select(getLocalOS().ordinal());
      
      this.fArchLabel.setEnabled(true);
      this.fArchitectureBt.setEnabled(true);
      defineDefaultCommands();
    }
    updateMessage();
    
    setControl(composite);
  }
    
  // --- IPlaftormConfWizardPage's interface methods implementation
  
  public boolean performFinish(final X10PlatformConfiguration platformConfiguration) {
    if (this.fDefaultPlatformConf != null) {
      platformConfiguration.setName(this.fDefaultPlatformConf.getName());
    }
    platformConfiguration.setArchitecture(this.fArchitectureBt.getSelection() ? EArchitecture.E64Arch : EArchitecture.E32Arch);
    platformConfiguration.setX10HeadersLoc(getX10HeadersLocs());
    platformConfiguration.setX10LibsLoc(getX10LibsLocs());
    platformConfiguration.setX10DistLoc(getX10DistLoc());
    platformConfiguration.setPGASLoc(getPGASDistLoc());
    platformConfiguration.setFlags(this.fIsCplusPlus, this.fIsLocal);
    platformConfiguration.setCompiler(this.fCompilerText.getText());
    platformConfiguration.setCompilerOpts(this.fCompilerOptsText.getText());
    if (this.fIsCplusPlus) {
      platformConfiguration.setArchiver(this.fArchiverText.getText());
      platformConfiguration.setArchivingOpts(this.fArchivingOptsText.getText());
      
      platformConfiguration.setLinker(this.fLinkerText.getText());
      platformConfiguration.setLinkingOpts(this.fLinkingOptsText.getText());
      platformConfiguration.setLinkingLibs(this.fLinkingLibsText.getText());
    }
    final IResourceManager resourceManager = getResourceManager();
    if (resourceManager != null) {
      platformConfiguration.setResManagerId(resourceManager.getUniqueName());
    }
    final String osName = this.fTargetOSCombo.getItem(this.fTargetOSCombo.getSelectionIndex());
    platformConfiguration.setTargetOS(ETargetOS.valueOf(osName));
    platformConfiguration.defineStatus(EValidStatus.UNKNOWN);
    return true;
  }
  
  // --- Internal services
  
  void updateFlags(final boolean isLocal, final boolean isCplusPlus) {
    this.fIsLocal = isLocal;
    this.fIsCplusPlus = isCplusPlus;
    setControl(null);
  }
  
  // --- Private code
  
  private void createArchiving(final Composite parent) {
    final Composite composite = new Composite(parent, SWT.NONE);
    composite.setFont(parent.getFont());
    composite.setLayout(new GridLayout(2, false));
    composite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    final ModifyListener listener = new UpdateMessageModifyListener();
    this.fArchiverText = WizardUtils.createLabelAndText(composite, Messages.PCDWP_Archiver, listener);
    this.fArchivingOptsText = WizardUtils.createLabelAndText(composite, Messages.PCDWP_ArchivingOpts, listener);
  }
  
  private void createCompilation(final Composite parent) {
    final Composite composite = new Composite(parent, SWT.NONE);
    composite.setFont(parent.getFont());
    composite.setLayout(new GridLayout(2, false));
    composite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    final ModifyListener listener = new UpdateMessageModifyListener();
    this.fCompilerText = WizardUtils.createLabelAndText(composite, Messages.PCDWP_CompilerText, listener);
    this.fCompilerOptsText = WizardUtils.createLabelAndText(composite, Messages.PCDWP_CompilerOptText, listener);
  }
  
  private void createLinking(final Composite parent) {
    final Composite composite2 = new Composite(parent, SWT.NONE);
    composite2.setFont(parent.getFont());
    composite2.setLayout(new GridLayout(2, false));
    composite2.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    final ModifyListener listener = new UpdateMessageModifyListener();
    this.fLinkerText = WizardUtils.createLabelAndText(composite2, Messages.PCDWP_LinkerText, listener);
    this.fLinkingOptsText = WizardUtils.createLabelAndText(composite2, Messages.PCDWP_LinkingOptText, listener);
    this.fLinkingLibsText = WizardUtils.createLabelAndText(composite2, Messages.PCDWP_LinkingLibText, listener);
  }
  
  private void createResourceManager(final Composite parent) {
    final Composite composite = new Composite(parent, SWT.NONE);
    composite.setFont(parent.getFont());
    composite.setLayout(new GridLayout(2, false));
    composite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    
    final IModelManager modelManager = PTPCorePlugin.getDefault().getModelManager();
    final IPUniverse universe = modelManager.getUniverse();
    
    new Label(composite, SWT.NONE).setText(Messages.PCDWP_ResourceManager);
    
    this.fResManagerCombo = new Combo(composite, SWT.READ_ONLY);
    this.fResManagerCombo.setFont(composite.getFont());
    this.fResManagerCombo.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    int nbStarted = 0;
    for (final IResourceManager resourceManager : universe.getResourceManagers()) {
      if (resourceManager.getState() == ResourceManagerAttributes.State.STARTED) {
        this.fResManagerCombo.add(resourceManager.getName());
        this.fResManagerCombo.setData(resourceManager.getName(), resourceManager.getUniqueName());
        ++nbStarted;
      }
    }
    this.fResManagerCombo.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        updateMessage();
        for (final Button button : PlatformConfDefWizardPage.this.fResMgrDepBts) {
          button.setEnabled(PlatformConfDefWizardPage.this.fResManagerCombo.getSelectionIndex() != -1);
        }
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
      
    });
    if (nbStarted == 1) {
      this.fResManagerCombo.select(0);
    } else if ((this.fDefaultPlatformConf != null) && (this.fDefaultPlatformConf.getResourceManagerId() != null)) {
      int index = -1;
      for (final IResourceManager resourceManager : universe.getResourceManagers()) {
        if (resourceManager.getState() == ResourceManagerAttributes.State.STARTED) {
          ++index;
          if (resourceManager.getUniqueName().equals(this.fDefaultPlatformConf.getResourceManagerId())) {
            this.fResManagerCombo.select(index);
            break;
          }
        }
      }
    }
  }
  
  private void createTargetOperatingSystem(final Composite parent) {
    final Composite composite1 = new Composite(parent, SWT.NONE);
    composite1.setFont(parent.getFont());
    composite1.setLayout(new GridLayout(2, false));
    composite1.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    
    new Label(composite1, SWT.NONE).setText(Messages.PCDWP_TargetOS);
    
    this.fTargetOSCombo = new Combo(composite1, SWT.READ_ONLY);
    this.fTargetOSCombo.setFont(composite1.getFont());
    this.fTargetOSCombo.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    for (final ETargetOS targetOs : ETargetOS.values()) {
      this.fTargetOSCombo.add(targetOs.name());
      this.fTargetOSCombo.setData(targetOs.name(), targetOs);
    }
    if ((this.fDefaultPlatformConf != null)) {
      for (final ETargetOS targetOs : ETargetOS.values()) {
        if (targetOs.name().equals(this.fDefaultPlatformConf.getTargetOS())) {
          this.fTargetOSCombo.select(targetOs.ordinal());
          break;
        }
      }
    }
    
    final Composite composite2 = new Composite(parent, SWT.NONE);
    composite2.setFont(parent.getFont());
    composite2.setLayout(new GridLayout(4, false));
    composite2.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    
    this.fEnvLabel = new Label(composite2, SWT.NONE);
    this.fEnvLabel.setText(Messages.PCDWP_EnvironmentLabel);
    this.fEnvLabel.setVisible(false);
    
    this.fWinEnvCombo = new Combo(composite2, SWT.READ_ONLY);
    this.fWinEnvCombo.setFont(composite2.getFont());
    this.fWinEnvCombo.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    this.fWinEnvCombo.add(Messages.PCDWP_CygwinEnv);
    this.fWinEnvCombo.setVisible(false);
    
    this.fArchLabel = new Label(composite2, SWT.NONE);
    this.fArchLabel.setText(Messages.PCDWP_64ArchLabel);
    this.fArchLabel.setEnabled(false);
    
    this.fArchitectureBt = new Button(composite2, SWT.CHECK);
    this.fArchitectureBt.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
    this.fArchitectureBt.setEnabled(false);
    this.fArchitectureBt.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        defineDefaultCommands();
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
      
    });
    
    this.fTargetOSCombo.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        PlatformConfDefWizardPage.this.fArchLabel.setEnabled(true);
        PlatformConfDefWizardPage.this.fArchitectureBt.setEnabled(true);
        
        defineDefaultCommands();
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
    });
  }
  
  private Text createX10PathLocation(final Composite parent, final String labelText) {
    final Composite composite = new Composite(parent, SWT.NONE);
    composite.setFont(parent.getFont());
    composite.setLayout(new GridLayout(2, false));
    composite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    
    final Label label = new Label(composite, SWT.NONE);
    label.setText(labelText);
    label.setFont(parent.getFont());
    label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
    
    final Text locText = new Text(composite, SWT.BORDER);
    locText.setFont(composite.getFont());
    locText.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    locText.addModifyListener(new UpdateMessageModifyListener());
    
    final Button browseBt = new Button(composite, SWT.PUSH);
    browseBt.setFont(parent.getFont());
    browseBt.setText(Messages.PCDWP_BrowseText);
    browseBt.setLayoutData(new GridData(SWT.RIGHT, SWT.NONE, false, false));
    browseBt.setEnabled(this.fResManagerCombo.getSelectionIndex() >= 0);
    this.fResMgrDepBts.add(browseBt);
    
    browseBt.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        final String path = PTPUtils.browseDirectory(getShell(), PlatformConfDefWizardPage.this.fResManagerCombo,
                                                     Messages.PCDWP_DirectoryLocation, ""); //$NON-NLS-1$
        if (path != null) {
          locText.setText(path);
          updateMessage();
        }
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
    });
    
    return locText;
  }
  
  private void defineDefaultCommands() {
    final Combo combo = PlatformConfDefWizardPage.this.fTargetOSCombo;
    final String osName = combo.getItem(combo.getSelectionIndex());
    final ETargetOS targetOs = (ETargetOS) combo.getData(osName);
    
    this.fEnvLabel.setVisible(targetOs == ETargetOS.WINDOWS);
    this.fWinEnvCombo.setVisible(targetOs == ETargetOS.WINDOWS);
    if (this.fWinEnvCombo.isVisible()) {
      this.fWinEnvCombo.select(0);
    }
    final boolean is64Arch = this.fArchitectureBt.getSelection();
    
    final IDefaultX10Platform defaultX10Platform;
    switch (targetOs) {
      case AIX:
        defaultX10Platform = new AixPlatform(is64Arch);
        break;
      case LINUX:
        defaultX10Platform = new LinuxPlatform(is64Arch);
        break;
      case MAC:
        defaultX10Platform = new MacPlatform(is64Arch);
        break;
      case WINDOWS:
        defaultX10Platform = new CygwinPlatform(is64Arch);
        break;
      default:
        defaultX10Platform = new UnknownUnixPlatform(is64Arch);
        break;
    }
    if (PlatformConfDefWizardPage.this.fCompilerText != null) {
      this.fCompilerText.setText(defaultX10Platform.getCompiler());
      this.fCompilerOptsText.setText(defaultX10Platform.getCompilerOptions());
    }
    if (this.fArchiverText != null) {
      this.fArchiverText.setText(defaultX10Platform.getArchiver());
      this.fArchivingOptsText.setText(defaultX10Platform.getArchivingOpts());
    }
    if (this.fLinkerText != null) {
      this.fLinkerText.setText(defaultX10Platform.getLinker());
      this.fLinkingOptsText.setText(defaultX10Platform.getLinkingOptions());
      this.fLinkingLibsText.setText(defaultX10Platform.getLinkingLibraries());
    }
    
    updateMessage();
    if (this.fIsLocal && (targetOs != getLocalOS())) {
      setMessage(NLS.bind(Messages.PCDWP_OSMismatch, targetOs.name()), IMessageProvider.WARNING);
    }
  }
  
  private File getLocalFile(final URL url) throws IOException {
    return new File(FileLocator.resolve(url).getFile());
  }
  
  private ETargetOS getLocalOS() {
    final String osName = System.getProperty(OS_NAME_VAR);
    if (osName.startsWith("AIX")) { //$NON-NLS-1$
      return ETargetOS.AIX;
    } else if (osName.startsWith("Linux")) { //$NON-NLS-1$
      return ETargetOS.LINUX;
    } else if (osName.startsWith("Mac")) { //$NON-NLS-1$
      return ETargetOS.MAC;
    } else if (osName.startsWith("Windows")) { //$NON-NLS-1$
      return ETargetOS.WINDOWS;
    } else {
      return ETargetOS.UNIX;
    }
  }
  
  private String getPGASDistLoc() {
    if (this.fIsLocal) {
      return this.fLocalLibsFile.getParentFile().getAbsolutePath();
    } else {
      return this.fPGASLocText.getText().trim();
    }
  }
  
  private IResourceManager getResourceManager() {
    if (this.fResManagerCombo.getSelectionIndex() == -1) {
      return null;
    } else {
      final String resName = this.fResManagerCombo.getItem(this.fResManagerCombo.getSelectionIndex());
      final String resUniqueName = (String) this.fResManagerCombo.getData(resName);
      return PTPCorePlugin.getDefault().getModelManager().getResourceManagerFromUniqueName(resUniqueName);
    }
  }
  
  private String getX10DistLoc() {
    if (this.fIsLocal) {
      return this.fLocalLibsFile.getParentFile().getAbsolutePath();
    } else {
      return this.fX10LocText.getText().trim();
    }
  }
  
  private String[] getX10HeadersLocs() {
    if (this.fIsLocal) {
      return new String[] { this.fLocalHeadersFile.getAbsolutePath().replace('\\', '/') };
    } else {
      return new String[] {
        this.fX10LocText.getText().trim() + '/' + INCLUDE_DIR,
        this.fPGASLocText.getText().trim() + '/' + INCLUDE_DIR
      };
    }
  }
  
  private String[] getX10LibsLocs() {
    if (this.fIsLocal) {
      return new String[] { this.fLocalLibsFile.getAbsolutePath().replace('\\', '/') };
    } else {
      return new String[] {
        this.fX10LocText.getText() + '/' + LIB_DIR,
        this.fPGASLocText.getText() + '/' + LIB_DIR
      };
    }
  }
    
  private boolean hasAllData() {
    if (this.fResManagerCombo.getSelectionIndex() == -1) {
      return false;
    }
    if (this.fTargetOSCombo.getSelectionIndex() == -1) {
      return false;
    }
    if (! this.fIsLocal) {
      if ((this.fPGASLocText.getText() == null) || (this.fPGASLocText.getText().length() == 0)) {
        return false;
      }
      if ((this.fX10LocText.getText() == null) || (this.fX10LocText.getText().length() == 0)) {
        return false;
      }
    }
    if (this.fCompilerText != null) {
      if ((this.fCompilerText.getText() == null) || (this.fCompilerText.getText().length() == 0)) {
        return false;
      }
      if ((this.fCompilerOptsText.getText() == null) || (this.fCompilerOptsText.getText().length() == 0)) {
        return false;
      }      
    }
    if (this.fArchiverText != null) {
      if ((this.fArchiverText == null) || (this.fArchiverText.getText().length() == 0)) {
        return false;
      }
      if ((this.fArchivingOptsText == null) || (this.fArchivingOptsText.getText().length() == 0)) {
        return false;
      }
    }
    if (this.fLinkerText != null) {
      if ((this.fLinkerText.getText() == null) || (this.fLinkerText.getText().length() == 0)) {
        return false;
      }
      if ((this.fLinkingOptsText.getText() == null) || (this.fLinkingOptsText.getText().length() == 0)) {
        return false;
      }
      if ((this.fLinkingLibsText.getText() == null) || (this.fLinkingLibsText.getText().length() == 0)) {
        return false;
      }
    }
    return true;
  }
  
  private void initDefaultConfValues() {
    final String defaultRMId = this.fDefaultPlatformConf.getResourceManagerId();
    int index = -1;
    for (final String item : this.fResManagerCombo.getItems()) {
      ++index;
      final String rmID = (String) this.fResManagerCombo.getData(item);
      if (defaultRMId.equals(rmID)) {
        this.fResManagerCombo.select(index);
        break;
      }
    }
    
    final String defaultOSName = this.fDefaultPlatformConf.getTargetOS().name();
    index = -1;
    for (final String item : this.fTargetOSCombo.getItems()) {
      ++index;
      if (defaultOSName.equals(item)) {
        this.fTargetOSCombo.select(index);
        break;
      }
    }
    
    if (this.fDefaultPlatformConf.getTargetOS() == ETargetOS.WINDOWS) {
      this.fEnvLabel.setVisible(true);
      this.fWinEnvCombo.setVisible(true);
      this.fWinEnvCombo.select(0);
    }
    
    this.fArchLabel.setEnabled(true);
    this.fArchitectureBt.setEnabled(true);
    this.fArchitectureBt.setSelection(this.fDefaultPlatformConf.getArchitecture() == EArchitecture.E64Arch);
    
    if (! this.fIsLocal) {
      this.fX10LocText.setText(this.fDefaultPlatformConf.getX10DistribLocation());
      this.fPGASLocText.setText(this.fDefaultPlatformConf.getPGASLocation());
    }
    
    this.fCompilerText.setText(this.fDefaultPlatformConf.getCompiler());
    this.fCompilerOptsText.setText(this.fDefaultPlatformConf.getCompilerOpts());
    
    if (this.fArchiverText != null) {
      this.fArchiverText.setText(this.fDefaultPlatformConf.getArchiver());
      this.fArchivingOptsText.setText(this.fDefaultPlatformConf.getArchivingOpts());
    }
    
    if (this.fLinkerText != null) {
      this.fLinkerText.setText(this.fDefaultPlatformConf.getLinker());
      this.fLinkingOptsText.setText(this.fDefaultPlatformConf.getLinkingOpts());
      this.fLinkingLibsText.setText(this.fDefaultPlatformConf.getLinkingLibs());
    }
  }
  
  private void updateMessage() {
    final boolean hasAllData = hasAllData();
    if (! hasAllData) {
      if (this.fResManagerCombo.getSelectionIndex() == -1) {
        setMessage(Messages.PCDWP_SelectRMMsg);
      } else if (this.fTargetOSCombo.getSelectionIndex() == -1) {
        setMessage(Messages.PCDWP_SelectOSMsg);
      } else if ((this.fPGASLocText != null) && (this.fPGASLocText.getText().length() == 0)) {
        setMessage(Messages.PCDWP_DefinePGASLocMsg);
      } else if ((this.fX10LocText != null) && (this.fX10LocText.getText().length() == 0)) {
        setMessage(Messages.PCDWP_DefineX10DistLocMsg);
      } else {
        if (this.fIsCplusPlus) {
          setMessage(Messages.PCDWP_DefineCompLinkCmdsMsg);
        } else {
          setMessage(Messages.PCDWP_DefineCompCmdsMsg);
        }
      }
    }
    setPageComplete(hasAllData);
  }
  
  // --- Private classes
  
  private final class UpdateMessageModifyListener implements ModifyListener {
    
    UpdateMessageModifyListener() {}

    // --- Interface methods implementation

    public void modifyText(final ModifyEvent event) {
      updateMessage();
    }
    
  }
  
  // --- Fields
  
  private final IX10PlatformConfiguration fDefaultPlatformConf;
  
  private final Collection<Button> fResMgrDepBts = new ArrayList<Button>();
  
  private final File fLocalHeadersFile;
  
  private final File fLocalLibsFile;
  
  private boolean fIsLocal;
  
  private boolean fIsCplusPlus;
  
  private Combo fResManagerCombo;
  
  private Combo fTargetOSCombo;
  
  private Label fEnvLabel;
  
  private Combo fWinEnvCombo;
  
  private Label fArchLabel;
  
  private Button fArchitectureBt;
  
  private Text fPGASLocText;
  
  private Text fX10LocText;
  
  private Text fCompilerText;
  
  private Text fCompilerOptsText;
  
  private Text fArchiverText;
  
  private Text fArchivingOptsText;
  
  private Text fLinkerText;
  
  private Text fLinkingOptsText;
  
  private Text fLinkingLibsText;
    
  
  
  private static final String INCLUDE_DIR = "include"; //$NON-NLS-1$
  
  private static final String LIB_DIR = "lib"; //$NON-NLS-1$
  
  private static final String OS_NAME_VAR = "os.name"; //$NON-NLS-1$
  
}
