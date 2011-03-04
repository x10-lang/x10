/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.rms;

import org.eclipse.osgi.util.NLS;

/**
 * Bundle class for all messages of "x10dt.ui.launch.cpp.rms".
 * 
 * @author egeay
 */
@SuppressWarnings("all")
public class Messages extends NLS {

  public static String AXRS_ConnNotFound;

  public static String AXRS_defaultQueueName;

  public static String AXRS_InitRemoteServTaskName;

  public static String AXRS_InitRMTaskName;

  public static String AXRS_NoHostNameFound;

  public static String AXRS_OpeningConnTaskName;

  public static String AXRS_ProcsNumberError;

  public static String AXRS_RemoteServInitError;

  public static String AXRS_RemoteServNotFound;

  public static String AXRSJ_ErrorStreamReadingError;

  public static String AXRSJ_JobCancelationMsg;

  public static String AXRSJ_JobCanceled;

  public static String AXRSJ_JobExitValueErrorMsg;

  public static String AXRSJ_OutputStreamReadingError;

  public static String AXRSJ_ProcessStartError;

  public static String AXRSJ_SuccessfulRunMsg;

  public static String LA_SocketsHostFileDescr;

  public static String LA_SocketsHostFileName;

  public static String LA_SocketsHostListDescr;

  public static String LA_SocketsHostListName;

  public static String SRMLCDT_AddBt;

  public static String SRMLCDT_AtLeastOneHostNameMsg;

  public static String SRMLCDT_AtLeastOnePlaceMsg;

  public static String SRMLCDT_BrowseBt;

  public static String SRMLCDT_HostFileBt;

  public static String SRMLCDT_HostFileRequiredMsg;

  public static String SRMLCDT_HostListBt;

  public static String SRMLCDT_HostsGroupName;

  public static String SRMLCDT_InvalidPlacesNb;

  public static String SRMLCDT_NoEmptyHostNameMsg;

  public static String SRMLCDT_PageInitializationError;

  public static String SRMLCDT_PlacesNumber;
  
  public static String SRMLCDT_RemoveBt;

  public static String SRMLCDT_SelectHostFileDialogTitle;
  
  // --- Private code
  
  public static String SSS_Sockets;

  public static String SSS_SocketsRMDescr;

  public static String SSS_Standalone;

  public static String SSS_StandaloneRMDescr;

  public static String SXRS_ProcessStartError;

  public static String SXRS_ValidatesSSHTaskName;

  public static String SXRS_ValidationError;

  public static String SXRS_SSHValidationErrorWithErrOutput;

  public static String SXRS_SSHValidationTaskName;

  private Messages() {
  }
  
  // --- Fields
  
  private static final String BUNDLE_NAME = "x10dt.ui.launch.cpp.rms.messages"; //$NON-NLS-1$
  
  static {
    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
  }

}
