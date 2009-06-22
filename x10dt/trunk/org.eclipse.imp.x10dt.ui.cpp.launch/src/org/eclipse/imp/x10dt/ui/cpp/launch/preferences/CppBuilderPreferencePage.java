/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.cpp.launch.preferences;

import org.eclipse.imp.x10dt.ui.cpp.launch.Constants;
import org.eclipse.imp.x10dt.ui.cpp.launch.LaunchCore;
import org.eclipse.imp.x10dt.ui.cpp.launch.LaunchMessages;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Defines X10 C++ Builder preference page.
 * 
 * @author egeay
 */
public final class CppBuilderPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
  
  // --- Interface methods implementation
  
  public void init(final IWorkbench workbench) {
    setPreferenceStore(LaunchCore.getInstance().getPreferenceStore());
  }
  
  // --- Abstract methods implementation
  
  protected Control createContents(final Composite parent) {
    final Composite composite = new Composite(parent, SWT.NONE);
    composite.setFont(parent.getFont());
    composite.setLayout(new GridLayout(1, false));
    composite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    
    final Group environmentGroup = new Group(composite, SWT.NONE);
    environmentGroup.setFont(composite.getFont());
    environmentGroup.setLayout(new GridLayout(1, false));
    environmentGroup.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    environmentGroup.setText(LaunchMessages.CBPP_EnvVarGroupName);
    
    this.fX10DistEnvText = createSingleTextWithLabel(environmentGroup, LaunchMessages.CBPP_X10DistLocVarLabel);
    this.fPGASEnvText = createSingleTextWithLabel(environmentGroup, LaunchMessages.CBPP_PGasLocVarLabel);
    
    final Group commandsGroup = new Group(composite, SWT.NONE);
    commandsGroup.setFont(composite.getFont());
    commandsGroup.setLayout(new GridLayout(1, false));
    commandsGroup.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    commandsGroup.setText(LaunchMessages.CBPP_RemoteCommandsGroupName);
      
    this.fCompilationText = createMultiTextWithLabel(commandsGroup, LaunchMessages.CBPP_CompilationCmdLabel);
    this.fArchivingText = createMultiTextWithLabel(commandsGroup, LaunchMessages.CBPP_ArchivingCmdLabel);
    this.fLinkingText = createMultiTextWithLabel(commandsGroup, LaunchMessages.CBPP_LinkingCmdLabel);
    
    initializeValues();
    
    return composite;
  }
  
  // --- Overridden methods
  
  protected void performDefaults() {
    initializeDefaultValues();    
    super.performDefaults();
  }
  
  public boolean performOk() {
    storeValues();
    return true;
  }
  
  // --- Private code
  
  private Text createMultiTextWithLabel(final Composite parent, final String labelText) {
    final Label label = new Label(parent, SWT.NONE);
    label.setFont(parent.getFont());
    label.setText(labelText);
    
    final Composite composite = new Composite(parent, SWT.NONE);
    composite.setFont(parent.getFont());
    final GridLayout compilationLayout = new GridLayout(1, false);
    compilationLayout.marginLeft = 5;
    composite.setLayout(compilationLayout);
    final GridData data = new GridData(SWT.DEFAULT, 50);
    data.horizontalAlignment = SWT.FILL;
    data.grabExcessHorizontalSpace = true;
    composite.setLayoutData(data);
    
    final Text text = new Text(composite, SWT.BORDER | SWT.MULTI | SWT.WRAP);
    text.setFont(composite.getFont());
    text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    
    return text;
  }
  
  private Text createSingleTextWithLabel(final Composite parent, final String labelText) {
    final Composite composite = new Composite(parent, SWT.NONE);
    composite.setFont(parent.getFont());
    composite.setLayout(new GridLayout(2, false));
    composite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    
    final Label label = new Label(composite, SWT.NONE);
    label.setFont(parent.getFont());
    label.setText(labelText);
        
    final Text text = new Text(composite, SWT.BORDER | SWT.SINGLE);
    text.setFont(composite.getFont());
    text.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    
    return text;
  }
  
  // --- Private code
  
  private void initializeDefaultValues() {
    final IPreferenceStore store = getPreferenceStore();
    this.fX10DistEnvText.setText(store.getDefaultString(Constants.P_CPP_BUILDER_X10_DIST_LOC));
    this.fPGASEnvText.setText(store.getDefaultString(Constants.P_CPP_BUILDER_PGAS_LOC));
    this.fCompilationText.setText(store.getDefaultString(Constants.P_CPP_BUILDER_COMPILE_CMD));
    this.fArchivingText.setText(store.getDefaultString(Constants.P_CPP_BUILDER_ARCHIVE_CMD));
    this.fLinkingText.setText(store.getDefaultString(Constants.P_CPP_BUILDER_LINK_CMD));
  }
  
  private void initializeValues() {
    final IPreferenceStore store = getPreferenceStore();
    this.fX10DistEnvText.setText(store.getString(Constants.P_CPP_BUILDER_X10_DIST_LOC));
    this.fPGASEnvText.setText(store.getString(Constants.P_CPP_BUILDER_PGAS_LOC));
    this.fCompilationText.setText(store.getString(Constants.P_CPP_BUILDER_COMPILE_CMD));
    this.fArchivingText.setText(store.getString(Constants.P_CPP_BUILDER_ARCHIVE_CMD));
    this.fLinkingText.setText(store.getString(Constants.P_CPP_BUILDER_LINK_CMD));
  }
  
  private void storeValues() {
    final IPreferenceStore store = getPreferenceStore();
    store.setValue(Constants.P_CPP_BUILDER_X10_DIST_LOC, this.fX10DistEnvText.getText().trim());
    store.setValue(Constants.P_CPP_BUILDER_PGAS_LOC, this.fPGASEnvText.getText().trim());
    store.setValue(Constants.P_CPP_BUILDER_COMPILE_CMD, this.fCompilationText.getText().trim());
    store.setValue(Constants.P_CPP_BUILDER_ARCHIVE_CMD, this.fArchivingText.getText().trim());
    store.setValue(Constants.P_CPP_BUILDER_LINK_CMD, this.fLinkingText.getText().trim());
  }

  // --- Fields
  
  private Text fX10DistEnvText;
  
  private Text fPGASEnvText;
  
  private Text fCompilationText;
  
  private Text fArchivingText;
  
  private Text fLinkingText;
  
}
