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

package org.eclipse.imp.x10dt.core.wizards;

import org.eclipse.osgi.util.NLS;

public class NewWizardMessages extends NLS {

    private static final String BUNDLE_NAME= "org.eclipse.imp.x10dt.core.wizards.NewWizardMessages";//$NON-NLS-1$

    private NewWizardMessages() {
        // Do not instantiate
    }

    public static String NewPackageCreationWizard_title;

    public static String NewPackageWizardPage_description;
    public static String NewPackageWizardPage_package_label;
    public static String NewPackageWizardPage_info;

    public static String NewPackageWizardPage_error_InvalidPackageName;
    public static String NewPackageWizardPage_warning_DiscouragedPackageName;
    public static String NewPackageWizardPage_error_EnterName;
    public static String NewPackageWizardPage_title;
    public static String NewPackageWizardPage_error_IsOutputFolder;
    public static String NewPackageWizardPage_error_PackageExists;
    public static String NewPackageWizardPage_error_PackageNotShown;

    public static String NewClassWizardPage_methods_label;
    public static String NewClassWizardPage_methods_main;
    public static String NewClassWizardPage_methods_constructors;
    public static String NewClassWizardPage_methods_inherited;

    static {
        NLS.initializeMessages(BUNDLE_NAME, NewWizardMessages.class);
    }
}
