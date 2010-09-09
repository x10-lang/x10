/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.cpp.launch.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.imp.x10dt.ui.cpp.launch.Constants;
import org.eclipse.imp.x10dt.ui.cpp.launch.LaunchCore;

/**
 * Provides the default preference value for CPP Builder preference page.
 * 
 * @author egeay
 */
public class CppBuilderPrefPageInitializer extends AbstractPreferenceInitializer {

  // --- Abstract methods implementation

  public void initializeDefaultPreferences() {
    final IEclipsePreferences preferences = new DefaultScope().getNode(LaunchCore.PLUGIN_ID);
    preferences.put(Constants.P_CPP_BUILDER_COMPILE_CMD, COMPILE_CMD);
    preferences.put(Constants.P_CPP_BUILDER_ARCHIVE_CMD, ARCHIVE_CMD);
    preferences.put(Constants.P_CPP_BUILDER_LINK_CMD, LINK_CMD);
  }
  
  // --- Fields
  
  private static final String COMPILE_CMD = "mpCC_r -g -q64 -qsuppress=1540-0809:1500-029 -qrtti=all -I${X10_DIST_LOC}/include -I${PGAS_LOC}/include"; //$NON-NLS-1$
//  private static final String COMPILE_CMD = "g++ -g -DTRANSPORT=sockets -I${X10_DIST_LOC}/include -I${PGAS_LOC}/include"; //$NON-NLS-1$

  private static final String ARCHIVE_CMD = "ar -X64 r $workspace_dir/$lib_name.a $workspace_dir/*.o"; //$NON-NLS-1$
//  private static final String ARCHIVE_CMD = "ar r $workspace_dir/$lib_name.a $workspace_dir/*.o"; //$NON-NLS-1$
  
  private static final String LINK_CMD = "mpCC_r -g -DTRANSPORT=lapi -qrtti=all -bbigtoc -bexpfull -q64 -qsuppress=1540-0809:1500-029 -L${PGAS_LOC}/lib -L${X10_DIST_LOC}/lib -I${PGAS_LOC}/include -I${X10_DIST_LOC}/include -lx10lib17 -lx10rt17 -lupcrts_lapi -ldl -lm -lpthread"; //$NON-NLS-1$
//  private static final String LINK_CMD = "g++ -g -DTRANSPORT=sockets # -L${PGAS_LOC}/lib -L${X10_DIST_LOC}/lib -I${PGAS_LOC}/include -I${X10_DIST_LOC}/include -lx10 -lupcrts_sockets -ldl -lm -lpthread"; //$NON-NLS-1$
  
}
