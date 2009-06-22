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
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.imp.utils.StreamUtils;

import lpg.runtime.IMessageHandler;
import lpg.runtime.Monitor;

import polyglot.frontend.FileSource;
import polyglot.frontend.Goal;//PORT1.7 was polyglot.frontend.goals.Goal;
import polyglot.frontend.Job;
import polyglot.frontend.Parser;
import polyglot.frontend.Source;
import polyglot.util.ErrorQueue;
import x10.parser.X10Lexer;
import x10.parser.X10Parser;

/**
 * Information about our extension of the polyglot compiler
 * @author beth
 *
 */
public class ExtensionInfo extends polyglot.ext.x10.ExtensionInfo
{
    X10Lexer x10_lexer;
    X10Parser x10_parser;
    Monitor monitor;
    IMessageHandler handler;
    
    ExtensionInfo(Monitor monitor, IMessageHandler handler) {
        this.monitor = monitor;
        this.handler = handler;
        x10_lexer = new X10Lexer();
        x10_lexer.reset(new char[0], ""); // PORT1.7 BOB HACK to make sure lexer has a lexstream, needed by remapTerminalSymbols() (which probably shouldn't need it!)
        x10_parser = new X10Parser(x10_lexer.getILexStream()); // PORT1.7 Create this early
    }
    
    public X10Lexer getLexer() { return x10_lexer; }
    public X10Parser getParser() { return x10_parser; }

    public Job getJob(Source source) {
        Collection jobs = scheduler.jobs();
        for (Iterator i = jobs.iterator(); i.hasNext(); )
        {
            Job job = (Job) i.next();
            if (job.source() == source)
                return job;
        }
        return null;
    }
    
    public Parser parser(Reader reader, FileSource source, ErrorQueue eq) {
        //
        // X10Lexers may be created using one of two constructors.
        // The first takes a string representing a full-qualified filename;
        // the other takes a (file) Reader and a string representing the
        // name of the file in question. Invoking the first is more efficient
        // because a buffered stream is created from that string and read in
        // in a single operation. However, we depend on Polyglot to provide a
        // fully qualified file path. In Version 1.3.0, source.name() yielded
        // a fully-qualified name. In 1.3.2, source.path() yields a fully-
        // qualified name. If this assumption still holds then the first
        // constructor will work. The advantage of using the Reader constructor
        // is that it will always work, though not as efficiently.
        //
        if (reader instanceof CharBufferReader) {
            x10_lexer.reset(((CharBufferReader) reader).getBuffer(), source.path());
            x10_parser.initialize(typeSystem(), nodeFactory(), source, eq); // PORT1.7 Now created early, but initialized here once the source is known
            x10_lexer.lexer(x10_parser.getIPrsStream());
            x10_parser.getIPrsStream().setMessageHandler(handler);
            return x10_parser; // Parse the token stream to produce an AST
        }
        if (reader instanceof Reader) {
            char[] buffer= StreamUtils.readReaderContents(reader).toCharArray();
            x10_lexer.reset(buffer, source.path());
            x10_parser.initialize(typeSystem(), nodeFactory(), source, eq);
            x10_lexer.lexer(x10_parser.getIPrsStream());
            x10_parser.getIPrsStream().setMessageHandler(handler);
            return x10_parser;
        }
        return super.parser(reader, source, eq);
    }

    /**
     * Return the <code>Goal</code> to compile the source file associated with
     * <code>job</code> to completion.
     */
    public Goal getCompileGoal(Job job) {  
        return scheduler.TypeChecked(job); // CodeGenerated(job);
    }
}