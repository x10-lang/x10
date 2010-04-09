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
  
  public static String AXB_IncompleteConfMsg;
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
  public static String CPPB_FileReadingErrorMessage;
  public static String CPPB_IOErrorDuringCompilation;
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
  public static String EQ_URIErrorMsg;
  public static String JPU_ResourceErrorMsg;
  public static String JPU_UnexpectedEntryKindMsg;
  public static String RIS_StreamClosed;
  public static String XB_NoPlatformConfError;
  public static String XBU_ArchNameNotInEnum;
  public static String XBU_NoCheckerForLanguage;
 
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
