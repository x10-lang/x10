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

import java.io.IOException;

import lpg.runtime.IPrsStream;
import lpg.runtime.LexStream;
import lpg.runtime.Monitor;

import org.eclipse.imp.parser.ILexer;

import x10.parser.X10Lexer;

public class LexerDelegate implements ILexer
{
	X10Lexer myLexer;

        public LexerDelegate(String absFilePath) throws IOException {
            this.myLexer = new X10Lexer(absFilePath);
        }
        public LexerDelegate(X10Lexer myLexer) {
            this.myLexer = myLexer;
        }
	public int [] getKeywordKinds() { return myLexer.getKeywordKinds(); }
	public LexStream getILexStream() { return (LexStream) myLexer.getILexStream(); }
	public void initialize(char [] content, String filename) { myLexer.reset(content, filename); }
	public void reset(char [] content, String filename) { myLexer.reset(content, filename); }
	public void lexer(Monitor monitor, IPrsStream prsStream) { myLexer.lexer(monitor, prsStream); }
}