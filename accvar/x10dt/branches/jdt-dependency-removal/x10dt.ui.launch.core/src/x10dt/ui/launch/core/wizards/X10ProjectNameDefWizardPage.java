/*****************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                       *
 * All rights reserved. This program and the accompanying materials          *
 * are made available under the terms of the Eclipse Public License v1.0     *
 * which accompanies this distribution, and is available at                  *
 * http://www.eclipse.org/legal/epl-v10.html                                 *
 *****************************************************************************/
package x10dt.ui.launch.core.wizards;

import org.eclipse.core.runtime.Path;
import org.eclipse.imp.model.IPathEntry;
import org.eclipse.imp.model.ModelFactory;
import org.eclipse.imp.ui.wizards.NewProjectWizardPageOne;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import x10dt.core.utils.X10DTCoreConstants;
import x10dt.ui.launch.core.Messages;


public class X10ProjectNameDefWizardPage extends NewProjectWizardPageOne {

  public X10ProjectNameDefWizardPage() {
	super();
    setPageComplete(false);
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
    this.fGenHelloProgBt.setText(Messages.PWFP_SampleCodeGenButton);
    this.fGenHelloProgBt.setSelection(true);

    setControl(composite);
  }
  
  public IPathEntry[] getDefaultClasspathEntries() {
    final IPathEntry[] entries = new IPathEntry[0];
    final IPathEntry[] all = new IPathEntry[entries.length + 1];
    System.arraycopy(entries, 0, all, 0, entries.length);
    all[entries.length] = ModelFactory.createContainerEntry(new Path(X10DTCoreConstants.X10_CONTAINER_ENTRY_ID));
    return all;
  }

  // --- Internal Services

  public boolean shouldGenerateHelloWorldProgram() {
    return this.fGenHelloProgBt.getSelection();
  }

  // --- Fields

  private Button fGenHelloProgBt;

}
