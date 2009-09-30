/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.core;

import org.eclipse.osgi.util.NLS;

/**
 * Message bundle class for all internationalized string of characters of this plugin.
 * 
 * @author egeay
 */
@SuppressWarnings("all")
public class Messages extends NLS {
  
  public static String CPPB_LibCreationError;
  public static String CPPB_NoResManagerError;
  public static String CPPB_ResManagerNotStarted;
  public static String CPPB_ArchivingCmdLabel;
  public static String CPPB_ArchivingTaskName;
  public static String CPPB_CompilationCmdLabel;
  public static String CPPB_EnvVarGroupName;
  public static String CPPB_ErrorStreamReadingError;
  public static String CPPB_LinkingCmdLabel;
  public static String CPPB_PGasLocVarLabel;
  public static String CPPB_RemoteCommandsGroupName;
  public static String CPPB_X10DistLocVarLabel;
  public static String CPPB_CancelOpMsg;
  public static String CPPB_CleanTaskName;
  public static String CPPB_CollectingSourcesTaskName;
  public static String CPPB_CompilationInterrupted;
  public static String CPPB_CompiledMsg;
  public static String CPPB_CompilErrorMsg;
  public static String CPPB_ConnError;
  public static String CPPB_ConsoleName;
  public static String CPPB_DeletingTaskName;
  public static String CPPB_DeletionTaskName;
  public static String CPPB_FileReadingErrorMessage;
  public static String CPPB_IOErrorDuringCompilation;
  public static String CPPB_RemoteCompilTaskName;
  public static String CPPB_RemoteOpError;
  public static String CPPB_TransferError;
  public static String CPPB_TransferTaskName;
  public static String EQ_PosErrorMsg;
  public static String EQ_URIErrorMsg;
  public static String JPU_ResourceErrorMsg;
  public static String JPU_UnexpectedEntryKindMsg;
  public static String PCDWP_Archiver;
  public static String PCDWP_Archiving;
  public static String PCDWP_ArchivingOpts;
  public static String PCDWP_AutoDetectBt;
  public static String PCDWP_BrowseText;
  public static String PCDWP_CompilationBt;
  public static String PCDWP_CompilationGroup;
  public static String PCDWP_CompilerOptText;
  public static String PCDWP_CompilerText;
  public static String PCDWP_DefaultPageMsg;
  public static String PCDWP_DirectoryLocation;
  public static String PCDWP_DistribGroup;
  public static String PCDWP_LinkerText;
  public static String PCDWP_LinkingBt;
  public static String PCDWP_LinkingGroup;
  public static String PCDWP_LinkingLibText;
  public static String PCDWP_LinkingOptText;
  public static String PCDWP_OSNotHandledWarning;
  public static String PCDWP_PGASDistribLoc;
  public static String PCDWP_ResourceManager;
  public static String PCDWP_TargetOS;
  public static String PCDWP_X10DistribLoc;
  public static String PCNWP_BackEndGroup;
  public static String PCNWP_ConfigurationName;
  public static String PCNWP_CPPBt;
  public static String PCNWP_DefWizPageDescr;
  public static String PCNWP_DefWizPageTitle;
  public static String PCNWP_JavaBt;
  public static String PCNWP_LocalBt;
  public static String PCNWP_LocalStr;
  public static String PCNWP_LocationGroup;
  public static String PCNWP_RemoteBt;
  public static String PCNWP_RemoteStr;
  public static String PCNWP_WizDefaultMsg;
  public static String PCNWP_WizDescr;
  public static String PCNWP_WizTitle;
  public static String XNPC_EditWindowTitle;
  public static String XNPC_WindowTitle;
  public static String XPCPP_AddBt;
  public static String XPCPP_Archiver;
  public static String XPCPP_ArchivingOpts;
  public static String XPCPP_Compiler;
  public static String XPCPP_CompilerOpts;
  public static String XPCPP_ConfNameAlreadyExists;
  public static String XPCPP_ConfSummary;
  public static String XPCPP_EditBt;
  public static String XPCPP_Linker;
  public static String XPCPP_LinkingLibs;
  public static String XPCPP_LinkingOpts;
  public static String XPCPP_LoadingErrorLogMsg;
  public static String XPCPP_LoadingErrorMsg;
  public static String XPCPP_PGASLoc;
  public static String XPCPP_RemoteBt;
  public static String XPCPP_RenameBt;
  public static String XPCPP_RenameDialogMsg;
  public static String XPCPP_RenameDialogTitle;
  public static String XPCPP_ResourceManagerId;
  public static String XPCPP_SavingErrorDialogDescr;
  public static String XPCPP_SavingErrorDialogTitle;
  public static String XPCPP_SavingErrorLogMsg;
  public static String XPCPP_TargetOS;
  public static String XPCPP_X10DistribLoc;
  public static String XPCPP_X10PlatformsMsg;
 
  // --- Private code
  
  private Messages() {
  }
  
  // --- Fields
  
  private static final String BUNDLE_NAME = "org.eclipse.imp.x10dt.ui.launch.core.messages"; //$NON-NLS-1$
  
  static {
    // initialize resource bundle
    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
  }
}
