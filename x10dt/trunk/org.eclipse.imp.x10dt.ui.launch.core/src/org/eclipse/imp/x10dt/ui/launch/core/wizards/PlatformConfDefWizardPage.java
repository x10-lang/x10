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
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.imp.x10dt.ui.launch.core.Messages;
import org.eclipse.imp.x10dt.ui.launch.core.platform_conf.ETargetOS;
import org.eclipse.imp.x10dt.ui.launch.core.platform_conf.IX10PlatformConfiguration;
import org.eclipse.imp.x10dt.ui.launch.core.utils.ErrorUtils;
import org.eclipse.imp.x10dt.ui.launch.core.utils.WizardUtils;
import org.eclipse.imp.x10dt.ui.launch.core.utils.X10BuilderUtils;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ptp.core.IModelManager;
import org.eclipse.ptp.core.PTPCorePlugin;
import org.eclipse.ptp.core.elementcontrols.IResourceManagerControl;
import org.eclipse.ptp.core.elements.IPUniverse;
import org.eclipse.ptp.core.elements.IResourceManager;
import org.eclipse.ptp.core.elements.attributes.ResourceManagerAttributes;
import org.eclipse.ptp.remote.core.IRemoteConnection;
import org.eclipse.ptp.remote.core.IRemoteServices;
import org.eclipse.ptp.remote.core.PTPRemoteCorePlugin;
import org.eclipse.ptp.remote.ui.IRemoteUIConstants;
import org.eclipse.ptp.remote.ui.IRemoteUIFileManager;
import org.eclipse.ptp.remote.ui.IRemoteUIServices;
import org.eclipse.ptp.remote.ui.PTPRemoteUIPlugin;
import org.eclipse.ptp.rmsystem.IResourceManagerConfiguration;
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
  }
  
  // --- IDialogPage's interface methods implementation

  public void createControl(final Composite parent) {
    initializeDialogUnits(parent);
    
    final Composite composite = new Composite(parent, SWT.NULL);
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
      if (this.fDefaultPlatformConf != null) {
        this.fPGASLocText.setText(this.fDefaultPlatformConf.getPGASLocation());
        this.fX10LocText.setText(this.fDefaultPlatformConf.getX10DistribLocation());
      }
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
        archivingGroup.setText(Messages.PCDWP_Archiving);
        createArchiving(archivingGroup);
        
        final Group linkingGroup = new Group(composite, SWT.NONE);
        linkingGroup.setFont(composite.getFont());
        linkingGroup.setLayout(new GridLayout(1, false));
        linkingGroup.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
        linkingGroup.setText(Messages.PCDWP_LinkingGroup);
        createLinking(linkingGroup);
      }
    }
    
    setControl(composite);
  }
    
  // --- IPlaftormConfWizardPage's interface methods implementation
  
  public boolean performFinish(final X10PlatformConfiguration platformConfiguration) {
    if (this.fDefaultPlatformConf != null) {
      platformConfiguration.setName(this.fDefaultPlatformConf.getName());
    }
    if (this.fIsLocal) {
      final Bundle x10DistBundle = Platform.getBundle(X10_DIST_PLUGIN_ID);
      if (x10DistBundle == null) {
        ErrorUtils.dialogWithLog(getShell(), Messages.PCDWP_NoBundleDialogTitle, IStatus.ERROR, 
                                 NLS.bind(Messages.PCDWP_NoBundleDialogMsg, X10_DIST_PLUGIN_ID));
        return false;
      }
      final URL headerURL = x10DistBundle.getResource(INCLUDE_DIR);
      final URL libsURL = x10DistBundle.getResource(LIB_DIR);
      try {
        platformConfiguration.setX10HeadersLoc(new String[] { getLocalFile(headerURL).getAbsolutePath() });
        final File libDir = getLocalFile(libsURL);
        platformConfiguration.setX10LibsLoc(new String[] { libDir.getAbsolutePath() });
        
        platformConfiguration.setX10DistLoc(libDir.getParentFile().getAbsolutePath());
        platformConfiguration.setPGASLoc(platformConfiguration.getX10DistribLocation());
      } catch (Exception except) {
        except.printStackTrace();
      }
    } else {
      platformConfiguration.setPGASLoc(this.fPGASLocText.getText().trim());
      platformConfiguration.setX10DistLoc(this.fX10LocText.getText().trim());
      final String[] headersLoc = new String[] {
        this.fX10LocText.getText().trim() + '/' + INCLUDE_DIR,
        this.fPGASLocText.getText().trim() + '/' + INCLUDE_DIR
      };
      platformConfiguration.setX10HeadersLoc(headersLoc);
      final String[] libsLoc = new String[] {
        this.fX10LocText.getText() + '/' + LIB_DIR,
        this.fPGASLocText.getText() + '/' + LIB_DIR
      };
      platformConfiguration.setX10LibsLoc(libsLoc);
    }
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
      platformConfiguration.setResManagerId(resourceManager.getID());
    }
    if (this.fTargetOSCombo.getSelectionIndex() != -1) {
      final String osName = this.fTargetOSCombo.getItem(this.fTargetOSCombo.getSelectionIndex());
      platformConfiguration.setTargetOS(X10BuilderUtils.getTargetOS(osName));
    }
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
    final Button button = WizardUtils.createLabelAndPushButton(composite, Messages.PCDWP_Archiving, 
                                                               Messages.PCDWP_AutoDetectBt, null);
    button.setEnabled(false);
    
    final Composite composite2 = new Composite(parent, SWT.NONE);
    composite2.setFont(parent.getFont());
    composite2.setLayout(new GridLayout(2, false));
    composite2.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    final ModifyListener listener = new UpdateMessageModifyListener();
    this.fArchiverText = WizardUtils.createLabelAndText(composite2, Messages.PCDWP_Archiver, listener);
    this.fArchivingOptsText = WizardUtils.createLabelAndText(composite2, Messages.PCDWP_ArchivingOpts, listener);
    
    if (this.fDefaultPlatformConf != null) {
      this.fArchiverText.setText(this.fDefaultPlatformConf.getArchiver());
      this.fArchivingOptsText.setText(this.fDefaultPlatformConf.getArchivingOpts());
    }
  }
  
  private void createCompilation(final Composite parent) {
    final Composite composite = new Composite(parent, SWT.NONE);
    composite.setFont(parent.getFont());
    composite.setLayout(new GridLayout(2, false));
    composite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    final Button button = WizardUtils.createLabelAndPushButton(composite, Messages.PCDWP_CompilationBt, 
                                                               Messages.PCDWP_AutoDetectBt, null);
    button.setEnabled(false);
    
    final Composite composite2 = new Composite(parent, SWT.NONE);
    composite2.setFont(parent.getFont());
    composite2.setLayout(new GridLayout(2, false));
    composite2.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    final ModifyListener listener = new UpdateMessageModifyListener();
    this.fCompilerText = WizardUtils.createLabelAndText(composite2, Messages.PCDWP_CompilerText, listener);
    this.fCompilerOptsText = WizardUtils.createLabelAndText(composite2, Messages.PCDWP_CompilerOptText, listener);
    
    if (this.fDefaultPlatformConf != null) {
      this.fCompilerText.setText(this.fDefaultPlatformConf.getCompiler());
      this.fCompilerOptsText.setText(this.fDefaultPlatformConf.getCompilerOpts());
    }
  }
  
  private void createLinking(final Composite parent) {
    final Composite composite = new Composite(parent, SWT.NONE);
    composite.setFont(parent.getFont());
    composite.setLayout(new GridLayout(2, false));
    composite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    final Button button = WizardUtils.createLabelAndPushButton(composite, Messages.PCDWP_LinkingBt, 
                                                               Messages.PCDWP_AutoDetectBt, null);
    button.setEnabled(false);
    
    final Composite composite2 = new Composite(parent, SWT.NONE);
    composite2.setFont(parent.getFont());
    composite2.setLayout(new GridLayout(2, false));
    composite2.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    final ModifyListener listener = new UpdateMessageModifyListener();
    this.fLinkerText = WizardUtils.createLabelAndText(composite2, Messages.PCDWP_LinkerText, listener);
    this.fLinkingOptsText = WizardUtils.createLabelAndText(composite2, Messages.PCDWP_LinkingOptText, listener);
    this.fLinkingLibsText = WizardUtils.createLabelAndText(composite2, Messages.PCDWP_LinkingLibText, listener);
    
    if (this.fDefaultPlatformConf != null) {
      this.fLinkerText.setText(this.fDefaultPlatformConf.getLinker());
      this.fLinkingOptsText.setText(this.fDefaultPlatformConf.getLinkingOpts());
      this.fLinkingLibsText.setText(this.fDefaultPlatformConf.getLinkingLibs());
    }
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
    for (final IResourceManager resourceManager : universe.getResourceManagers()) {
      if (resourceManager.getState() == ResourceManagerAttributes.State.STARTED) {
        this.fResManagerCombo.add(resourceManager.getName());
        this.fResManagerCombo.setData(resourceManager.getName(), resourceManager.getID());
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
    if ((this.fDefaultPlatformConf != null) && (this.fDefaultPlatformConf.getResourceManagerId() != null)) {
      int index = -1;
      for (final IResourceManager resourceManager : universe.getResourceManagers()) {
        if (resourceManager.getState() == ResourceManagerAttributes.State.STARTED) {
          ++index;
          if (resourceManager.getID().equals(this.fDefaultPlatformConf.getResourceManagerId())) {
            this.fResManagerCombo.select(index);
            break;
          }
        }
      }
    }
  }
  
  private void createTargetOperatingSystem(final Composite parent) {
    final Composite composite = new Composite(parent, SWT.NONE);
    composite.setFont(parent.getFont());
    composite.setLayout(new GridLayout(2, false));
    composite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    
    new Label(composite, SWT.NONE).setText(Messages.PCDWP_TargetOS);
    
    this.fTargetOSCombo = new Combo(composite, SWT.READ_ONLY);
    this.fTargetOSCombo.setFont(composite.getFont());
    this.fTargetOSCombo.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    for (final ETargetOS targetOs : ETargetOS.values()) {
      this.fTargetOSCombo.add(targetOs.name());
      this.fTargetOSCombo.setData(targetOs.name(), targetOs);
    }
    if ((this.fDefaultPlatformConf != null) && (this.fDefaultPlatformConf.getTargetOS() != null)) {
      for (final ETargetOS targetOs : ETargetOS.values()) {
        if (targetOs.name().equals(this.fDefaultPlatformConf.getTargetOS())) {
          this.fTargetOSCombo.select(targetOs.ordinal());
          break;
        }
      }
    }
    this.fTargetOSCombo.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        final Combo combo = PlatformConfDefWizardPage.this.fTargetOSCombo;
        final String osName = combo.getItem(combo.getSelectionIndex());
        final ETargetOS targetOs = (ETargetOS) combo.getData(osName);
        final IDefaultX10Platform defaultX10Platform;
        switch ( targetOs ) {
        case AIX:
          defaultX10Platform = null;
          break;
        case LINUX:
          defaultX10Platform = new LinuxPlatform();
          break;
        case MAC:
          defaultX10Platform = new MacPlatform();
          break;
        case UNIX:
          defaultX10Platform = null;
          break;
        case WINDOWS:
          defaultX10Platform = null;
          break;
        default:
          defaultX10Platform = null;
        }
        if (defaultX10Platform == null) {
          setMessage(osName + Messages.PCDWP_OSNotHandledWarning, IMessageProvider.WARNING);
        } else {
          if (PlatformConfDefWizardPage.this.fCompilerText != null) {
            PlatformConfDefWizardPage.this.fCompilerText.setText(defaultX10Platform.getCompiler());
            PlatformConfDefWizardPage.this.fCompilerOptsText.setText(defaultX10Platform.getCompilerOptions());
          }
          if (PlatformConfDefWizardPage.this.fArchiverText != null) {
            PlatformConfDefWizardPage.this.fArchiverText.setText(defaultX10Platform.getArchiver());
            PlatformConfDefWizardPage.this.fArchivingOptsText.setText(defaultX10Platform.getArchivingOpts());
          }
          if (PlatformConfDefWizardPage.this.fLinkerText != null) {
            PlatformConfDefWizardPage.this.fLinkerText.setText(defaultX10Platform.getLinker());
            PlatformConfDefWizardPage.this.fLinkingOptsText.setText(defaultX10Platform.getLinkingOptions());
            PlatformConfDefWizardPage.this.fLinkingLibsText.setText(defaultX10Platform.getLinkingLibraries());
          }
          updateMessage();
        }
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
    label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    
    final Button autoDetectBt = new Button(composite, SWT.PUSH);
    autoDetectBt.setFont(parent.getFont());
    autoDetectBt.setText(Messages.PCDWP_AutoDetectBt);
    autoDetectBt.setLayoutData(new GridData(SWT.RIGHT, SWT.NONE, false, false));
    autoDetectBt.setEnabled(false);
    
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
        final IResourceManagerControl rmControl = (IResourceManagerControl) getResourceManager();
        final IResourceManagerConfiguration rmConf = rmControl.getConfiguration();
        final IRemoteServices rmServices = PTPRemoteCorePlugin.getDefault().getRemoteServices(rmConf.getRemoteServicesId());
        final IRemoteUIServices rmUIServices = PTPRemoteUIPlugin.getDefault().getRemoteUIServices(rmServices);
        if (rmServices != null && rmUIServices != null) {
          final IRemoteConnection rmConnection = rmServices.getConnectionManager().getConnection(rmConf.getConnectionName());
          if (rmConnection != null) {
            final IRemoteUIFileManager fileManager = rmUIServices.getUIFileManager();
            if (fileManager != null) {
              fileManager.setConnection(rmConnection);
              fileManager.showConnections(false);
              final String path = fileManager.browseDirectory(getShell(), Messages.PCDWP_DirectoryLocation, "",IRemoteUIConstants.NONE); //$NON-NLS-1$
              if (path != null) {
                locText.setText(path);
                updateMessage();
              }
            }
          }
        }
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
    });
    
    return locText;
  }
  
  private File getLocalFile(final URL url) throws URISyntaxException, IOException {
    return new File(FileLocator.resolve(url).toURI());
  }
  
  private IResourceManager getResourceManager() {
    if (this.fResManagerCombo.getSelectionIndex() == -1) {
      return null;
    } else {
      final String resName = this.fResManagerCombo.getItem(this.fResManagerCombo.getSelectionIndex());
      final String resId = (String) this.fResManagerCombo.getData(resName);
      final IModelManager modelManager = PTPCorePlugin.getDefault().getModelManager();
      return modelManager.getUniverse().getResourceManager(resId);
    }
  }
    
  private boolean isComplete() {
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
  
  private void updateMessage() {
    if (this.fResManagerCombo.getSelectionIndex() == -1) {
      setMessage(Messages.PCDWP_DefaultPageMsg);
    } else {
      setMessage(null);
    }
    setPageComplete(isComplete());
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
  
  private boolean fIsLocal;
  
  private boolean fIsCplusPlus;
  
  private Combo fResManagerCombo;
  
  private Combo fTargetOSCombo;
  
  private Text fPGASLocText;
  
  private Text fX10LocText;
  
  private Text fCompilerText;
  
  private Text fCompilerOptsText;
  
  private Text fArchiverText;
  
  private Text fArchivingOptsText;
  
  private Text fLinkerText;
  
  private Text fLinkingOptsText;
  
  private Text fLinkingLibsText;
  
  
  private static final String X10_DIST_PLUGIN_ID = "x10.dist.common"; //$NON-NLS-1$
  
  private static final String INCLUDE_DIR = "include"; //$NON-NLS-1$
  
  private static final String LIB_DIR = "lib"; //$NON-NLS-1$

}
