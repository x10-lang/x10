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
package x10dt.ui.launch.java.wizards;

import x10dt.ui.launch.core.wizards.X10ProjectNameDefWizardPage;

public class X10ProjectWizardFirstPage extends X10ProjectNameDefWizardPage {

  
  public X10ProjectWizardFirstPage() {
    super();
    setTitle("New X10 Project (Java back-end)");
    setDescription("Creates a new X10 project");
  }
}
