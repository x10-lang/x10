/*****************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                       *
 * All rights reserved. This program and the accompanying materials          *
 * are made available under the terms of the Eclipse Public License v1.0     *
 * which accompanies this distribution, and is available at                  *
 * http://www.eclipse.org/legal/epl-v10.html                                 *
 *****************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.cpp;

import org.eclipse.osgi.util.NLS;

/**
 * Bundle class for all messages of "oorg.eclipse.imp.x10dt.ui.cpp.launch".
 *
 * @author egeay
 */
@SuppressWarnings("all")
public class LaunchMessages extends NLS {
  
  public static String AT_ConfigReadingError;

  public static String AT_ProgArgsGroupName;

  public static String AT_TabName;

  public static String AT_VariablesBtName;
  
  public static String AWTA_DialogMessage;

  public static String AWTA_DialogTitle;
  
  public static String CAT_AbsoluteAndRelativePathMsg;

  public static String CAT_BrowseButton;

  public static String CAT_ClosedProject;

  public static String CAT_IllegalPrjName;

  public static String CAT_IOError;

  public static String CAT_MainTypeGroupName;

  public static String CAT_MainTypeSearchError;

  public static String CAT_MainTypeSearchInternalError;

  public static String CAT_LinkApp;
  
  public static String CAT_LinkAppToolTip;

  public static String CAT_NoCPPFileForMainType;

  public static String CAT_NoExistingProject;

  public static String CAT_NoProjectDefinition;

  public static String CAT_NotExistingPathError;
  
  public static String CAT_NotExistingProject;

  public static String CAT_OutputToConsoleMsg;

  public static String CAT_ParsingX10FilesMsg;

  public static String CAT_PrjSelectionDialogMsg;

  public static String CAT_PrjSelectionDialogTitle;

  public static String CAT_ProjectGroupName;

  public static String CAT_ReadConfigError;
  
  public static String CAT_RequiredMainTypeName;

  public static String CAT_RequiredPrjName;

  public static String CAT_ResourcesAccessError;

  public static String CAT_SearchButton;

  public static String CAT_SearchingMainMethodsMsg;

  public static String CAT_SetMainTypeError;

  public static String CAT_TabName;

  public static String CAT_X10FileSearchOrCPFailure;

  public static String CAT_X10ParsingInternalError;

  public static String CLCD_CmdUsedMsg;

  public static String CLCD_ExecCreationTaskName;

  public static String CLCD_LaunchCreationTaskName;

  public static String CLCD_LinkCmdError;

  public static String CLCD_LinkingInterrupted;
  
  public static String CLCD_NoMainFileAccessIOError;

  public static String CLCD_NoMainFileAccessOpError;

  public static String CLCD_NoResManagerError;

  public static String CPWSP_AddOrStartMsg;

  public static String CPWSP_AddPlatformMsg;

  public static String CPWSP_LocationLabel;

  public static String CPWSP_BrowseBt;

  public static String CPWSP_BrowseDescription;

  public static String CPWSP_DefinePlatformFirstMsg;

  public static String CPWSP_MustDefineOrStartMsg;

  public static String CPWSP_MustDefineRMMsg;

  public static String CPWSP_ResManagerGroupName;

  public static String CPWSP_ResManagerLabel;

  public static String CPWSP_ResourceManager;

  public static String CPWSP_RMStartFailure;

  public static String CPWSP_SelectResMsg;

  public static String CPWSP_SelectWorkspaceMsg;

  public static String CPWSP_StartExistingRMMsg;

  public static String CPWSP_TargetWorkspaceGroupName;

  public static String CPWSP_WizardDescription;

  public static String CPWSP_WizardName;

  public static String CPWSP_WizardTitle;

  public static String CPWSP_X10PlatformConf;

  public static String CPWSP_X10PlatformGroup;

  public static String CPWSP_X10Platforms;

  public static String CPWSP_X10PlatformSelectionMsg;
  
  public static String ES_CompileTaskName;
  
  public static String ES_LinkingTaskName;
  
  public static String FFSD_DialogTitle;
  
  public static String PW_PrjCancelationErrorMsg;

  public static String PW_PrjCancelationErrorTitle;

  public static String PW_ProjectCreationErrorMessage;

  public static String PW_ProjectCreationErrorTitle;

  public static String PW_WindowTitle;

  public static String PWFP_PageDescription;

  public static String PWFP_PageTitle;

  public static String PWFP_RuntimeEntryErrorMsg;

  public static String PWFP_RuntimeEntryErrorTitle;

  public static String PWFP_SampleCodeGenButton;
  
  public static String PWSP_BuildPathInitTaskName;

  public static String PWSP_CPEntriesError;

  public static String PWSP_FinishTaskName;

  public static String PWSP_PageDescription;

  public static String PWSP_PageTitle;

  public static String PWSP_SourceProjectError;

  public static String PWSP_UpdateProjectTaskName;

  public static String DBG_Attributes_1;
  
  public static String XCTD_DialogMsg;

  public static String XCTD_DialogTitle;

  public static String XPCPP_NoResManagerStartedError;

  public static String XTEPP_CleanPreviousWDir;

  public static String XTEPP_DataStoringError;

  public static String XTEPP_InitializationError;

  public static String XTEPP_MissingRMError;

  public static String XTEPP_MissingTargetWorkspaceError;

  public static String XTEPP_MissingX10PlatformError;

  // --- Private code

  public static String XTEPP_RebuildProject;

  public static String XTEPP_RebuildTargetWorkspace;

  private static final String BUNDLE_NAME = "org.eclipse.imp.x10dt.ui.launch.cpp.messages"; //$NON-NLS-1$

  static {
    // initialize resource bundle
    NLS.initializeMessages(BUNDLE_NAME, LaunchMessages.class);
  }

  private LaunchMessages() {
  }
}
