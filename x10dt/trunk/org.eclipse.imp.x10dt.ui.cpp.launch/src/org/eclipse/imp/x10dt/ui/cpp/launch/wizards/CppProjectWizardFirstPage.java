/*****************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                       *
 * All rights reserved. This program and the accompanying materials          *
 * are made available under the terms of the Eclipse Public License v1.0     *
 * which accompanies this distribution, and is available at                  *
 * http://www.eclipse.org/legal/epl-v10.html                                 *
 *****************************************************************************/
package org.eclipse.imp.x10dt.ui.cpp.launch.wizards;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.imp.x10dt.ui.cpp.launch.LaunchMessages;
import org.eclipse.imp.x10dt.ui.cpp.launch.utils.ErrorUtils;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPageOne;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.osgi.framework.Bundle;


final class CppProjectWizardFirstPage extends NewJavaProjectWizardPageOne {

  CppProjectWizardFirstPage() {
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
    try {
      final IClasspathEntry[] x10CPEntries = createX10RuntimeEntries();
      final IClasspathEntry[] all = new IClasspathEntry[entries.length + x10CPEntries.length];
      System.arraycopy(entries, 0, all, 0, entries.length);
      System.arraycopy(x10CPEntries, 0, all, entries.length, x10CPEntries.length);
      return all;
    } catch (Error except) {
      return new IClasspathEntry[0];
    } catch (IOException except) {
      return new IClasspathEntry[0];
    }
  }
  
  // --- Internal Services

  boolean shouldGenerateHelloWorldProgram() {
    return this.fGenHelloProgBt.getSelection();
  }

  // --- Private code

  private IClasspathEntry[] createX10RuntimeEntries() throws Error, IOException {
    final List<IClasspathEntry> cpEntries = new ArrayList<IClasspathEntry>();
    addClassPathEntries(cpEntries, X10_RUNTIME_BUNDLE, CLASSES_DIR);
    addClassPathEntries(cpEntries, X10_RUNTIME_BUNDLE, SRC_X10_DIR);
    addClassPathEntries(cpEntries, X10_COMMON_BUNDLE, CLASSES_DIR);
    addClassPathEntries(cpEntries, X10_CONSTRAINTS_BUNDLE, CLASSES_DIR);
    return cpEntries.toArray(new IClasspathEntry[cpEntries.size()]);
  }
  
  private void addClassPathEntries(final List<IClasspathEntry> cpEntries, final String bundleName,
                                   final String folder) throws IOException {
    final Bundle bundle = Platform.getBundle(bundleName);
    if (bundle == null) {
      ErrorUtils.dialogWithLog(getShell(), LaunchMessages.CPWFP_NoBundleDialogTitle, IStatus.ERROR, 
                               NLS.bind(LaunchMessages.CPWFP_NoBundleDialogMsg, bundleName));
      throw new Error();
    } else {
      URL wURL = bundle.getResource(folder);
      if (wURL == null) {
        // We access the root of the jar where the resources should be located.
        wURL = bundle.getResource("."); //$NON-NLS-1$
      }
      final URL url = FileLocator.resolve(wURL);
      cpEntries.add(JavaCore.newLibraryEntry(new Path(url.getFile()), null /* sourceAttachmentPath */, 
                                             null /* sourceAttachmentRootPath */));
    }
  }

  // --- Fields

  private Button fGenHelloProgBt;
  
  
  private static final String X10_RUNTIME_BUNDLE = "x10.runtime.17"; //$NON-NLS-1$
  
  private static final String X10_COMMON_BUNDLE = "x10.common.17"; //$NON-NLS-1$
  
  private static final String X10_CONSTRAINTS_BUNDLE = "x10.constraints"; //$NON-NLS-1$
  
  private static final String CLASSES_DIR = "classes"; //$NON-NLS-1$
  
  private static final String SRC_X10_DIR = "src-x10"; //$NON-NLS-1$

}
