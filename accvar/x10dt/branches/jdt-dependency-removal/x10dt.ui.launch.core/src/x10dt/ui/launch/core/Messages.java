/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.core;

import org.eclipse.osgi.util.NLS;

/**
 * Message bundle class for all internationalized string of characters of this plugin.
 * 
 * @author egeay
 */
@SuppressWarnings("all")
public class Messages extends NLS {
  
  public static String ACXPA_ClassCreationError;
  public static String ACXPA_ConversionFailureMessage;
  public static String ACXPA_ConversionFailureTitle;
  public static String ACXPA_ExtensionPointErrorMsg;
  public static String ACXPA_FoundMultipleExtPoints;
  public static String ACXPA_NoExtensionFound;
  public static String AXB_IncompleteConfMsg;
  public static String CLCD_CmdUsedMsg;
  public static String CPPB_LibCreationError;
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
  public static String CPPB_ConsoleName;
  public static String CPPB_IOErrorDuringCompilation;
	public static String CPPB_NoPTPConnectionForName;
  public static String CPPB_NoValidConnectionError;
  public static String CPPB_OutputStreamReadingError;
  public static String CPPB_RemoteCompilTaskName;
  public static String CPPB_TargetOpError;
  public static String CPPB_TransferError;
  public static String CPPB_TransferTaskName;
  public static String DF_LineBreak;
  public static String EALD_CopyMsg;
  public static String EALD_ErrorLogStartMsg;
  public static String EALD_ErrorLogView;
  public static String EDB_NoShellError;
  public static String EQ_PosErrorMsg;
  public static String JPU_ResourceErrorMsg;
  public static String JPU_UnexpectedEntryKindMsg;
  public static String RIS_StreamClosed;
  public static String XB_NoPlatformConfError;
  public static String XBU_ArchNameNotInEnum;
  public static String XBU_NoCheckerForLanguage;
  public static String XEQ_InternalCompilerErrorMsg;
  public static String XTBH_NoHandler;
  public static String XTBH_NoHandlerForCppBackEnd;
  public static String XTBH_NoHandlerForJavaBackEnd;
  public static String AXB_EchoIOException;
  public static String AXB_BuildException;
  public static String AXB_CompilerInternalError;
  public static String AXB_NonExistentSRCFolder;
  public static String AXB_EmptySRCFolder;
  public static String AXB_NoSRCFolder;
  public static String AXBO_NoRemoteOutputFolder;
  public static String CPD_PackageDeclError;
  
  public static String PW_ProjectCreationErrorMessage;
  public static String PW_ProjectCreationErrorTitle;
  
  public static String PWFP_SampleCodeGenButton;
  
  public static String PWSP_FinishTaskName;
  public static String PWSP_BuildPathInitTaskName;
  public static String PWSP_CPEntriesError;
  public static String PWSP_EditorOpeningError;
  public static String PWSP_UpdateProjectTaskName;
  public static String PWSP_SourceProjectError;
  public static String PWSP_noSourceEntry;
  public static String PWSP_PageDescription;
  public static String PWSP_PageTitle;

  // --- Private code
  
  private Messages() {
  }
  
  // --- Fields
  
  private static final String BUNDLE_NAME = "x10dt.ui.launch.core.messages"; //$NON-NLS-1$
  
  static {
    // initialize resource bundle
    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
  }
}
