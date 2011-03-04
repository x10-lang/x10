/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.tests.services.swbot.constants;

/**
 * Contains string constants that appear in the various X10DT Wizards.
 * 
 * @author egeay
 */
@SuppressWarnings("all")
public final class WizardConstants {
  
  // --- Common constants for Wizard buttons
  
  public static final String FINISH_BUTTON = "Finish"; //$NON-NLS-1$
  
  public static final String YES_BUTTON = "Yes"; //$NON-NLS-1$
  
  public static final String OK_BUTTON = "OK"; //$NON-NLS-1$

  public static final String YES_TO_ALL_BUTTON = "Yes To All"; //$NON-NLS-1$

  public static final String NEXT_BUTTON = "Next >"; //$NON-NLS-1$
  
  public static final String CANCEL_BUTTON = "Cancel"; //$NON-NLS-1$

  // --- Constants for project creation
  
  public static final String FILE_MENU = "File"; //$NON-NLS-1$
  
  public static final String EDIT_MENU = "Edit"; //$NON-NLS-1$
  
  public static final String NEW_MENU_ITEM = "New"; //$NON-NLS-1$
  
  public static final String PROJECTS_SUB_MENU_ITEM = "Project..."; //$NON-NLS-1$
  
  public static final String NEW_PROJECT_DIALOG_TITLE = "New Project"; //$NON-NLS-1$
  
  public static final String X10_FOLDER = "X10"; //$NON-NLS-1$
  
  public static final String OPEN_ASSOCIATED_PERSPECTIVE_DIALOG_TITLE = "Open Associated Perspective?"; //$NON-NLS-1$
  
  // --- Constants for project import
  
  public static final String IMPORT_MENU_ITEM = "Import..."; //$NON-NLS-1$
  
  public static final String IMPORT_DIALOG_TITLE = "Import"; //$NON-NLS-1$
  
  public static final String IMPORT_ARCHIVE_DIALOG_TITLE = "Import"; //$NON-NLS-1$
  
  public static final String GENERAL_FOLDER = "General"; //$NON-NLS-1$
  
  public static final String IMPORT_ARCHIVE = "Archive File"; //$NON-NLS-1$

  public static final String FROM_ARCHIVE_FILE_FIELD = "From archive file:"; //$NON-NLS-1$

  public static final String INTO_FOLDER_FIELD = "Into folder:"; //$NON-NLS-1$

  public static final String OVERWRITE_EXISTING_CHECKBOX = "Overwrite existing resources without warning"; //$NON-NLS-1$

 // --- Constants for project creation with C++ back-end
  
  public static final String X10_PROJECT_CPP_BACKEND = "X10 Project (C++ back-end)"; //$NON-NLS-1$
  
  public static final String NEW_CPP_PROJECT_NAME = "Project name:"; //$NON-NLS-1$
  
  public static final String X10_PROJECT_SHELL_CPP_BACKEND = "New X10 project (C++ back-end)"; //$NON-NLS-1$
  
  public static final String NEW_CPP_PROJECT_NAME_FIELD = "Project name:"; //$NON-NLS-1$
  
  public static final String NEW_X10_PROJECT_HELLO_SOURCE_CHECKBOX= "NEW_X10_PROJECT_HELLO_SOURCE_RADIO"; //$NON-NLS-1$

  // --- Constants for project creation with Java back-end
  
  public static final String X10_PROJECT_JAVA_BACKEND = "X10 Project (Java back-end)"; //$NON-NLS-1$
  
  public static final String X10_PROJECT_SHELL_JAVA_BACKEND = "New X10 Project (Java back-end)"; //$NON-NLS-1$
  
  public static final String X10_PROJECT_JAVA_BACKEND_NAME_FIELD = "Name:"; //$NON-NLS-1$
  
  public static final String NEW_X10_PACKAGE_MENU_ITEM = "X10 Package"; //$NON-NLS-1$

  public static final String NEW_X10_PACKAGE_SHELL = "New X10 Package"; //$NON-NLS-1$

  public static final String NEW_X10_PACKAGE_NAME_FIELD= "Name:"; //$NON-NLS-1$

  public static final String NEW_X10_CLASS_MENU_ITEM= "X10 Class"; //$NON-NLS-1$

  public static final String NEW_X10_CLASS_WIZARD = "X10 Class"; //$NON-NLS-1$

  public static final String NEW_X10_CLASS_SHELL = ""; // Should be "New X10 Class", but isn't //$NON-NLS-1$

  public static final String NEW_X10_CLASS_SOURCE_FIELD= "Source folder:"; //$NON-NLS-1$

  public static final String NEW_X10_CLASS_NAME_FIELD= "Name:"; //$NON-NLS-1$

  public static final String NEW_X10_PROJECT_SAMPLE_SOURCE_GROUP= "Sample source"; //$NON-NLS-1$

  public static final String NEW_X10_PROJECT_NO_SAMPLE_SOURCE_RADIO= "Create no sample source code"; //$NON-NLS-1$

  public static final String NEW_X10_PROJECT_HELLO_SOURCE_RADIO= "Create a sample 'Hello World' X10 application"; //$NON-NLS-1$

  public static final String NEW_OTHER_MENU= "Other..."; //$NON-NLS-1$

  public static final String NEW_OTHER_SHELL= "New"; //$NON-NLS-1$

  public static final String QUICK_OUTLINE_SHELL= ""; // This value is really correct, at least for now //$NON-NLS-1$

  
  // --- Constants for project and file navigation
  
  public static final String PACKAGE_EXPLORER_VIEW_TITLE = "Package Explorer"; //$NON-NLS-1$

  public static final String NAVIGATE_MENU = "Navigate"; //$NON-NLS-1$
  
  public static final String OPEN_ITEM = "Open"; //$NON-NLS-1$
  
  public static final String OPEN_X10_TYPE_ITEM = "Open X10 Type..."; //$NON-NLS-1$
 
  public static final String OPEN_X10_TYPE_DIALOG_TITLE = "Open X10 Type"; //$NON-NLS-1$
  
  public static final String X10_TYPE_SEARCH_PATTERN = "Enter type name prefix or pattern (*, ?, or camel case):"; //$NON-NLS-1$
  
  public static final String X10_TYPE_SEARCH_LISTBOX = "Matching items:"; //$NON-NLS-1$

  
  // --- Constants for project and file deletion
  
  public static final String DELETE_MENU_ITEM = "Delete"; //$NON-NLS-1$
  
  public static final String DELETE_RESOURCES_SHELL = "Delete Resources"; //$NON-NLS-1$
  
  public static final String DELETE_CONFIRMATION_SHELL = "Confirm Delete"; //$NON-NLS-1$
  
  public static final String DELETE_PROJECT_CONTENTS = "Delete project contents on disk (cannot be undone)"; //$NON-NLS-1$
  
  // --- Private code
  
  private WizardConstants() {}

}
