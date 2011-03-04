/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation

 *******************************************************************************/

/*
 * (C) Copyright IBM Corporation 2007
 * 
 * This file is part of the Eclipse IMP.
 */
/*
 * Created on Feb 6, 2006
 */
package x10dt.ui.launch.java.wizards;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.Path;
import org.eclipse.imp.builder.ProjectNatureBase;
import org.eclipse.imp.model.IPathEntry;
import org.eclipse.imp.model.ModelFactory;
import org.eclipse.imp.ui.wizards.NewProjectWizardPageOne;

import x10dt.core.X10DTCorePlugin;
import x10dt.core.utils.X10DTCoreConstants;
import x10dt.ui.launch.core.wizards.X10ProjectPropertiesWizardPage;
import x10dt.ui.launch.java.nature.X10ProjectNature;

public class X10ProjectWizardSecondPage extends X10ProjectPropertiesWizardPage {

  public X10ProjectWizardSecondPage(final NewProjectWizardPageOne firstPage) {
	super(firstPage);
  }

  protected ProjectNatureBase getProjectNature() {
    return new X10ProjectNature();
  }

  protected List<IPathEntry> createLanguageRuntimeEntries() {
    return Arrays.asList(ModelFactory.createContainerEntry(new Path(X10DTCoreConstants.X10_CONTAINER_ENTRY_ID)));
  }

  protected String[] getNatureIds()
  {
	  return new String[] { X10DTCorePlugin.X10_PRJ_JAVA_NATURE_ID};
  }
}
