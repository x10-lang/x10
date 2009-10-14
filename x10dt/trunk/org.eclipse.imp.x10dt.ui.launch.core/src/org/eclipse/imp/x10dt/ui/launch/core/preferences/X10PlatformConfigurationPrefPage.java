/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.core.preferences;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.imp.x10dt.ui.launch.core.LaunchCore;
import org.eclipse.imp.x10dt.ui.launch.core.Messages;
import org.eclipse.imp.x10dt.ui.launch.core.platform_conf.IX10PlatformConfiguration;
import org.eclipse.imp.x10dt.ui.launch.core.platform_conf.X10PlatformsManager;
import org.eclipse.imp.x10dt.ui.launch.core.wizards.X10NewPlatformConfWizard;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Defines X10 Platform Configuration preference page.
 * 
 * @author egeay
 */
public final class X10PlatformConfigurationPrefPage extends PreferencePage 
                                                    implements IWorkbenchPreferencePage, SelectionListener {
  
  // --- Interface methods implementation

  public void init(final IWorkbench workbench) {
    setPreferenceStore(LaunchCore.getInstance().getPreferenceStore());
  }
  
  // --- SelectionListener's interface methods implementation
  
  public void widgetDefaultSelected(final SelectionEvent event) {
    widgetSelected(event);
  }

  public void widgetSelected(final SelectionEvent event) {
    final String[] selection = ((List) event.widget).getSelection();
    for (final Button button : this.fUpdatableButtons) {
      button.setEnabled(selection.length == 1);
    }
    if (selection.length == 1) {
      updateSummaryText(selection[0]);
    }
  }
  
  // --- Abstract methods implementation
  
  protected Control createContents(final Composite parent) {
    final Composite composite = new Composite(parent, SWT.NONE);
    composite.setFont(parent.getFont());
    composite.setLayout(new GridLayout(1, false));
    composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    
    final Label label = new Label(composite, SWT.NONE);
    label.setFont(parent.getFont());
    label.setText(Messages.XPCPP_X10PlatformsMsg);
    
    final SashForm sashFrom = new SashForm(composite, SWT.VERTICAL);
    sashFrom.setLayout(new GridLayout(1, false));
    sashFrom.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    
    try {
      this.fX10Platforms = X10PlatformsManager.loadPlatformsConfiguration();
    } catch (Exception except) {
      setMessage(Messages.XPCPP_LoadingErrorMsg, IMessageProvider.WARNING);
      LaunchCore.log(IStatus.ERROR, Messages.XPCPP_LoadingErrorLogMsg, except);
    }
    
    addPlatformConfsContainer(sashFrom);
    
    this.fPlatformConfSummaryText = new StyledText(sashFrom, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.READ_ONLY);
    this.fPlatformConfSummaryText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    this.fPlatformConfSummaryText.addListener (SWT.Resize, new Listener () {
      public void handleEvent (Event event) {
        final Rectangle rect = X10PlatformConfigurationPrefPage.this.fPlatformConfSummaryText.getClientArea ();
        
        final Image newImage = new Image (getShell().getDisplay(), 1, Math.max (1, rect.height));
        final GC gc = new GC (newImage);
        gc.setForeground (getShell().getDisplay().getSystemColor (SWT.COLOR_WIDGET_BACKGROUND));
        gc.setBackground (getShell().getDisplay().getSystemColor (SWT.COLOR_WHITE));
        gc.fillGradientRectangle (rect.x, rect.y, 1, rect.height, true);
        gc.dispose();
        X10PlatformConfigurationPrefPage.this.fPlatformConfSummaryText.setBackgroundImage (newImage);
        
        if (X10PlatformConfigurationPrefPage.this.fOldImage != null) {
          X10PlatformConfigurationPrefPage.this.fOldImage.dispose ();
        }
        X10PlatformConfigurationPrefPage.this.fOldImage = newImage;
      }
    });

    return composite;
  }
  
 // --- Overridden methods
  
  public boolean performOk() {
    try {
      X10PlatformsManager.savePlatformsConfiguration(this.fX10Platforms);
    } catch (IOException except) {
      final IStatus status = new Status(IStatus.ERROR, LaunchCore.getInstance().getBundle().getSymbolicName(), 
                                        IStatus.OK, Messages.XPCPP_SavingErrorLogMsg, except);
      ErrorDialog.openError(getShell(), Messages.XPCPP_SavingErrorDialogTitle, Messages.XPCPP_SavingErrorDialogDescr, 
                            status);
      LaunchCore.log(status);
    }
    return true;
  }
  
  // --- Private code
  
  private void addPlatformConfsContainer(final Composite parent) {
    final Composite composite = new Composite(parent, SWT.NONE);
    composite.setFont(parent.getFont());
    composite.setLayout(new GridLayout(2, false));
    composite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    
    final List platformConfList = new List (composite, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
    platformConfList.setFont(composite.getFont());
    platformConfList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 5));
    platformConfList.addSelectionListener(this);
        
    for (final String name : this.fX10Platforms.keySet()) {
      platformConfList.add(name);
    }
    
    final Button addButton = new Button(composite, SWT.PUSH);
    addButton.setFont(composite.getFont());
    addButton.setText(Messages.XPCPP_AddBt);
    addButton.addSelectionListener(new SelectionListener() {
      
      // --- Interface methods implementation
      
      public void widgetSelected(final SelectionEvent event) {
        final Map<String,IX10PlatformConfiguration> platforms = X10PlatformConfigurationPrefPage.this.fX10Platforms;
        final X10NewPlatformConfWizard wizard = new X10NewPlatformConfWizard(platforms.keySet());
        final WizardDialog dialog = new WizardDialog(getShell(), wizard);
        if (dialog.open() == Window.OK) {
          final IX10PlatformConfiguration platformConfiguration = wizard.getPlatformConfiguration();
          platformConfList.add(platformConfiguration.getName());
          platforms.put(platformConfiguration.getName(), platformConfiguration);
        }
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
      
    });
    
    final Button editButton = new Button(composite, SWT.PUSH);
    editButton.setFont(composite.getFont());
    editButton.setText(Messages.XPCPP_EditBt);
    editButton.setEnabled(false);
    editButton.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        final String name = platformConfList.getSelection()[0];
        final IX10PlatformConfiguration platformConfiguration = X10PlatformConfigurationPrefPage.this.fX10Platforms.get(name);
        final X10NewPlatformConfWizard wizard = new X10NewPlatformConfWizard(platformConfiguration);
        final WizardDialog dialog = new WizardDialog(getShell(), wizard);
        if (dialog.open() == Window.OK) {
          X10PlatformConfigurationPrefPage.this.fX10Platforms.put(name, wizard.getPlatformConfiguration());
          updateSummaryText(platformConfList.getSelection()[0]);
        }
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
    });
    
    final Button removeButton = new Button(composite, SWT.PUSH);
    removeButton.setFont(composite.getFont());
    removeButton.setText(Messages.XPCPP_RemoteBt);
    removeButton.setEnabled(false);
    removeButton.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        X10PlatformConfigurationPrefPage.this.fX10Platforms.remove(platformConfList.getSelection()[0]);
        platformConfList.remove(platformConfList.getSelectionIndex());
        X10PlatformConfigurationPrefPage.this.fPlatformConfSummaryText.setText(""); //$NON-NLS-1$
        for (final Button button : X10PlatformConfigurationPrefPage.this.fUpdatableButtons) {
          button.setEnabled(false);
        }
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
    });
    
    final Button renameBt = new Button(composite, SWT.PUSH);
    renameBt.setFont(composite.getFont());
    renameBt.setText(Messages.XPCPP_RenameBt);
    renameBt.setEnabled(false);
    renameBt.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        final Map<String, IX10PlatformConfiguration> platforms = X10PlatformConfigurationPrefPage.this.fX10Platforms;
        final InputDialog dialog = new InputDialog(getShell(), Messages.XPCPP_RenameDialogTitle, 
                                                   Messages.XPCPP_RenameDialogMsg, null /* initialValue */, 
                                                   new RenamingInputValidor(platforms.keySet()));
        if (dialog.open() == Window.OK) {
          final IX10PlatformConfiguration configuration = platforms.remove(platformConfList.getSelection()[0]);
          platformConfList.setItem(platformConfList.getSelectionIndex(), dialog.getValue());
          platforms.put(dialog.getValue(), X10PlatformsManager.createNewConfigurationName(configuration, dialog.getValue()));
        }
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
    });
    
    this.fUpdatableButtons = new Button[] { editButton, removeButton, renameBt };
  }
  
  private void updateSummaryText(final String selection) {
    final IX10PlatformConfiguration platformConfiguration = this.fX10Platforms.get(selection);
    final StringBuilder sb = new StringBuilder();
    sb.append(NLS.bind(Messages.XPCPP_ConfSummary, selection));
    sb.append(NLS.bind(Messages.XPCPP_X10DistribLoc, platformConfiguration.getX10DistribLocation()));
    sb.append(NLS.bind(Messages.XPCPP_PGASLoc, platformConfiguration.getPGASLocation()));
    sb.append(NLS.bind(Messages.XPCPP_Compiler, platformConfiguration.getCompiler()));
    sb.append(NLS.bind(Messages.XPCPP_CompilerOpts, platformConfiguration.getCompilerOpts()));
    if (platformConfiguration.hasArchivingStep()) {
      sb.append(NLS.bind(Messages.XPCPP_Archiver, platformConfiguration.getArchiver()));
      sb.append(NLS.bind(Messages.XPCPP_ArchivingOpts, platformConfiguration.getArchivingOpts()));
    }
    if (platformConfiguration.hasLinkingStep()) {
      sb.append(NLS.bind(Messages.XPCPP_Linker, platformConfiguration.getLinker()));
      sb.append(NLS.bind(Messages.XPCPP_LinkingOpts, platformConfiguration.getLinkingOpts()));
      sb.append(NLS.bind(Messages.XPCPP_LinkingLibs, platformConfiguration.getLinkingLibs()));
    }
    if (platformConfiguration.getResourceManagerId() != null) {
      sb.append(NLS.bind(Messages.XPCPP_ResourceManagerId, platformConfiguration.getResourceManagerId()));
    }
    if (platformConfiguration.getTargetOS() != null) {
      sb.append(NLS.bind(Messages.XPCPP_TargetOS, platformConfiguration.getTargetOS()));
    }
    this.fPlatformConfSummaryText.setText(sb.toString());
  }
  
  // --- Private classes
  
  private static final class RenamingInputValidor implements IInputValidator {
    
    RenamingInputValidor(final Set<String> confNames) {
      this.fConfNames = confNames;
    }

    // --- Interface methods implementation
    
    public String isValid(final String newText) {
      if (this.fConfNames.contains(newText)) {
        return NLS.bind(Messages.XPCPP_ConfNameAlreadyExists, newText);
      } else {
        return null;
      }
    }
    
    // --- Fields
    
    private final Set<String> fConfNames;
    
  }
  
  // --- Fields
  
  private StyledText fPlatformConfSummaryText;
  
  private Map<String, IX10PlatformConfiguration> fX10Platforms;
  
  private Button[] fUpdatableButtons;
  
  private Image fOldImage;

}
