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

package x10dt.ui.parser;

import lpg.runtime.ILexStream;
import lpg.runtime.IPrsStream;
import lpg.runtime.Monitor;

import org.eclipse.imp.parser.IParser;

import x10.parser.X10SemanticRules;

public class ParserDelegate implements IParser {
    X10SemanticRules myParser;

    ParserDelegate(X10SemanticRules myParser) {
        this.myParser= myParser;
    }

    public IPrsStream getIPrsStream() {
        try {
            return myParser.getIPrsStream();
        } catch (NullPointerException e) {
            System.err.println("ParserDelegate.getParseStream(..): caught NullPointerException");
            throw e;
        }
    }

    public int numTokenKinds() {
        return myParser.getX10Parser().numTokenKinds();
    }

    public int getEOFTokenKind() {
        return myParser.getX10Parser().getEOFTokenKind();
    }

    public Object parser(Monitor monitor, int error_repair_count) {
        return myParser.getX10Parser().parser(monitor);
    }

    public String[] orderedTerminalSymbols() {
        return myParser.getX10Parser().orderedTerminalSymbols();
    }

    public void reset(ILexStream lexStream) {
        myParser.getX10Parser().reset(lexStream);
    }
}
