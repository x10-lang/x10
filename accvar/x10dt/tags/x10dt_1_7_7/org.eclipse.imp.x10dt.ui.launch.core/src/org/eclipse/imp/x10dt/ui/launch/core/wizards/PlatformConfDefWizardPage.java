/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.core.wizards;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.imp.x10dt.ui.launch.core.preferences.X10PlatformConfiguration;
import org.eclipse.imp.x10dt.ui.launch.core.utils.WizardUtils;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.ptp.core.IModelManager;
import org.eclipse.ptp.core.PTPCorePlugin;
import org.eclipse.ptp.core.elements.IPUniverse;
import org.eclipse.ptp.core.elements.IResourceManager;
import org.eclipse.ptp.core.elements.attributes.ResourceManagerAttributes;
import org.eclipse.swt.SWT;
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


final class PlatformConfDefWizardPage extends WizardPage implements IWizardPage {
  
  PlatformConfDefWizardPage(final X10PlatformConfiguration platformConf) {
    super("DefWP");
    
    setPageComplete(false);
    
    this.fPlatformConf = platformConf;
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
    
    if (this.fIsLocal) {
    } else {
      final Group distribLocGroup = new Group(composite, SWT.NONE);
      distribLocGroup.setFont(composite.getFont());
      distribLocGroup.setLayout(new GridLayout(1, false));
      distribLocGroup.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
      distribLocGroup.setText("X10 Distribution");
      createX10PathLocation(distribLocGroup, "X10 Distribution Location");
      createX10PathLocation(distribLocGroup, "PGAS Distribution Location");
    }
    
    if (! this.fIsLocal || this.fIsCplusPlus) {
      final Group compilationGroup = new Group(composite, SWT.NONE);
      compilationGroup.setFont(composite.getFont());
      compilationGroup.setLayout(new GridLayout(1, false));
      compilationGroup.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
      compilationGroup.setText("Compilation");
      createCompilation(compilationGroup);
    
      if (this.fIsCplusPlus) {
        final Group linkingGroup = new Group(composite, SWT.NONE);
        linkingGroup.setFont(composite.getFont());
        linkingGroup.setLayout(new GridLayout(1, false));
        linkingGroup.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
        linkingGroup.setText("Linking");
        createLinking(linkingGroup);
      }
    }
    
    setControl(composite);
  }
    
  // --- Internal services
  
  void updateFlags(final boolean isLocal, final boolean isCplusPlus) {
    this.fIsLocal = isLocal;
    this.fIsCplusPlus = isCplusPlus;
    setControl(null);
  }
  
  // --- Private code
  
  private void createCompilation(final Composite parent) {
    final Composite composite = new Composite(parent, SWT.NONE);
    composite.setFont(parent.getFont());
    composite.setLayout(new GridLayout(2, false));
    composite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    final Button button = WizardUtils.createLabelAndPushButton(composite, "Compilation", "Auto Detect", null);
    button.setEnabled(false);
    this.fResMgrDepBts.add(button);
    
    final Composite composite2 = new Composite(parent, SWT.NONE);
    composite2.setFont(parent.getFont());
    composite2.setLayout(new GridLayout(2, false));
    composite2.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    WizardUtils.createLabelAndText(composite2, "Compiler", null);
    WizardUtils.createLabelAndText(composite2, "Compilation Options", null);
  }
  
  private void createLinking(final Composite parent) {
    final Composite composite = new Composite(parent, SWT.NONE);
    composite.setFont(parent.getFont());
    composite.setLayout(new GridLayout(2, false));
    composite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    final Button button = WizardUtils.createLabelAndPushButton(composite, "Linking", "Auto Detect", null);
    button.setEnabled(false);
    this.fResMgrDepBts.add(button);
    
    final Composite composite2 = new Composite(parent, SWT.NONE);
    composite2.setFont(parent.getFont());
    composite2.setLayout(new GridLayout(2, false));
    composite2.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    WizardUtils.createLabelAndText(composite2, "Linker", null);
    WizardUtils.createLabelAndText(composite2, "Linking Options", null);
    WizardUtils.createLabelAndText(composite2, "Linking Libraries", null);
  }
  
  private void createResourceManager(final Composite parent) {
    final Composite composite = new Composite(parent, SWT.NONE);
    composite.setFont(parent.getFont());
    composite.setLayout(new GridLayout(2, false));
    composite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    
    final IModelManager modelManager = PTPCorePlugin.getDefault().getModelManager();
    final IPUniverse universe = modelManager.getUniverse();
    
    new Label(composite, SWT.NONE).setText("Resource Manager");
    
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
  }
  
  private void createTargetOperatingSystem(final Composite parent) {
    final Composite composite = new Composite(parent, SWT.NONE);
    composite.setFont(parent.getFont());
    composite.setLayout(new GridLayout(2, false));
    composite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    
    new Label(composite, SWT.NONE).setText("Target Operating System");
    
    this.fTargetOSCombo = new Combo(composite, SWT.READ_ONLY);
    this.fTargetOSCombo.setFont(composite.getFont());
    this.fTargetOSCombo.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    for (final ETargetOS targetOs : ETargetOS.values()) {
      this.fTargetOSCombo.add(targetOs.name());
    }
  }
  
  private void createX10PathLocation(final Composite parent, final String labelText) {
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
    autoDetectBt.setText("Auto Detect");
    autoDetectBt.setLayoutData(new GridData(SWT.RIGHT, SWT.NONE, false, false));
    autoDetectBt.setEnabled(false);
    
    final Text locText = new Text(composite, SWT.BORDER);
    locText.setFont(composite.getFont());
    locText.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    
    final Button browseBt = new Button(composite, SWT.PUSH);
    browseBt.setFont(parent.getFont());
    browseBt.setText("Browse");
    browseBt.setLayoutData(new GridData(SWT.RIGHT, SWT.NONE, false, false));
    browseBt.setEnabled(false);
    this.fResMgrDepBts.add(browseBt);
  }
  
  private void updateMessage() {
    
  }
  
  // --- Private classes
  
  private enum ETargetOS {
    
    WINDOWS,
    
    LINUX,
    
    AIX,
    
    UNIX,
    
    MAC
    
  }
  
  // --- Fields
  
  private final X10PlatformConfiguration fPlatformConf;
  
  private final Collection<Button> fResMgrDepBts = new ArrayList<Button>();
  
  private boolean fIsLocal;
  
  private boolean fIsCplusPlus;
  
  private Combo fResManagerCombo;
  
  private Combo fTargetOSCombo;

}
