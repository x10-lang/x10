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

/*
 * (C) Copyright IBM Corporation 2007
 * 
 * This file is part of the Eclipse IMP.
 */
package org.eclipse.imp.x10dt.ui.parser;

import lpg.runtime.ILexStream;
import lpg.runtime.IPrsStream;
import lpg.runtime.LexStream;
import lpg.runtime.Monitor;
import lpg.runtime.PrsStream;

import org.eclipse.imp.parser.IParser;

import x10.parser.X10Parser;

public class ParserDelegate implements IParser {
    X10Parser myParser;

    public PrsStream getParseStream() {
        try {
            return myParser.getParseStream();
        } catch (NullPointerException e) {
            System.err.println("ParserDelegate.getParseStream(..): caught NullPointerException");
            throw e;
        }
    }

    public int numTokenKinds() {
  	return myParser.numTokenKinds();
    }
    
    public int getEOFTokenKind() {
	return myParser.getEOFTokenKind();
    }

    public Object parser(Monitor monitor, int error_repair_count) {
	return myParser.parser(monitor);
    }

    public String[] orderedTerminalSymbols() {
	return myParser.orderedTerminalSymbols();
    }

    ParserDelegate(X10Parser myParser) {
        this.myParser = myParser;
    }

	public IPrsStream getIPrsStream() {
		return myParser.getIPrsStream();
	}

	public void reset(ILexStream lexStream) {
		myParser.getPrsStream().resetLexStream((LexStream) lexStream);
	}
}