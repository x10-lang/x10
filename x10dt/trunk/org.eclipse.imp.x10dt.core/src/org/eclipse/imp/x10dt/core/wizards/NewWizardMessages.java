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

    static {
        NLS.initializeMessages(BUNDLE_NAME, NewWizardMessages.class);
    }
}
