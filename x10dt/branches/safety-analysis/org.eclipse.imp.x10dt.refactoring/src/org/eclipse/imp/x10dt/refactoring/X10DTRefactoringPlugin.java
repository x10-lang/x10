/*******************************************************************************
* Copyright (c) 2009 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation
*******************************************************************************/

package org.eclipse.imp.x10dt.refactoring;

import java.io.PrintStream;

import org.eclipse.imp.runtime.PluginBase;
import org.eclipse.imp.utils.ConsoleUtil;
import org.eclipse.imp.x10dt.core.X10DTCorePlugin;

public class X10DTRefactoringPlugin extends PluginBase {
    public static final String kPluginID= "org.eclipse.imp.x10dt.refactoring"; // must match plugin ID in MANIFEST.MF

    private static final String CONSOLE_NAME= "X10DT Refactoring";

    private static X10DTRefactoringPlugin sPlugin;

    public static X10DTRefactoringPlugin getInstance() {
        return sPlugin;
    }

    private PrintStream sConsoleStream;

    public X10DTRefactoringPlugin() {
        sPlugin= this;
    }

    @Override
    public String getID() {
        return kPluginID;
    }

    @Override
    public String getLanguageID() {
        return X10DTCorePlugin.kLanguageName;
    }

    public PrintStream getConsoleStream() {
        if (sConsoleStream == null) {
            sConsoleStream= ConsoleUtil.findConsoleStream(CONSOLE_NAME);
        }
        return sConsoleStream;
    }
}
