/*****************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                       *
 * All rights reserved. This program and the accompanying materials          *
 * are made available under the terms of the Eclipse Public License v1.0     *
 * which accompanies this distribution, and is available at                  *
 * http://www.eclipse.org/legal/epl-v10.html                                 *
 *****************************************************************************/
package x10dt.ui.launch.cpp.wizards;

import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPageOne;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import x10dt.core.utils.X10DTCoreConstants;
import x10dt.ui.launch.cpp.LaunchMessages;


final class CppProjectNameDefWizardPage extends NewJavaProjectWizardPageOne {

  CppProjectNameDefWizardPage() {
    setPageComplete(false);
    setTitle(LaunchMessages.PWFP_PageTitle);
    setDescription(LaunchMessages.PWFP_PageDescription);
  }

  // --- Overridden methods

  public void createControl(Composite parent) {
    initializeDialogUnits(parent);

    final Composite composite = new Composite(parent, SWT.NULL);
    composite.setFont(parent.getFont());
    composite.setLayout(new GridLayout(1, false));
    composite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

    final Control nameControl = createNameControl(composite);
    nameControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    final Control locationControl = createLocationControl(composite);
    locationControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    final Control layoutControl = createProjectLayoutControl(composite);
    layoutControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    final Control workingSetControl = createWorkingSetControl(composite);
    workingSetControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    final Control infoControl = createInfoControl(composite);
    infoControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    this.fGenHelloProgBt = new Button(composite, SWT.CHECK);
    this.fGenHelloProgBt.setText(LaunchMessages.PWFP_SampleCodeGenButton);
    this.fGenHelloProgBt.setSelection(true);

    setControl(composite);
  }
  
  public IClasspathEntry[] getDefaultClasspathEntries() {
    final IClasspathEntry[] entries = new IClasspathEntry[0];
    final IClasspathEntry[] all = new IClasspathEntry[entries.length + 1];
    System.arraycopy(entries, 0, all, 0, entries.length);
    all[entries.length] = JavaCore.newContainerEntry(new Path(X10DTCoreConstants.X10_CONTAINER_ENTRY_ID));
    return all;
  }

  // --- Internal Services

  boolean shouldGenerateHelloWorldProgram() {
    return this.fGenHelloProgBt.getSelection();
  }

  // --- Fields

  private Button fGenHelloProgBt;

}
