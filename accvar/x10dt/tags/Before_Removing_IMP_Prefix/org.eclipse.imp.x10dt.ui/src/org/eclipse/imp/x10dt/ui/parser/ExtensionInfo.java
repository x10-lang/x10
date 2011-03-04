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

package org.eclipse.imp.x10dt.ui.parser;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lpg.runtime.IMessageHandler;
import lpg.runtime.Monitor;

import org.eclipse.imp.utils.StreamUtils;

import polyglot.ast.Node;
import polyglot.frontend.AllBarrierGoal;
import polyglot.frontend.FileSource;
import polyglot.frontend.ForgivingVisitorGoal;
import polyglot.frontend.Goal;
import polyglot.frontend.Job;
import polyglot.frontend.Parser;
import polyglot.frontend.Scheduler;
import polyglot.frontend.Source;
import polyglot.frontend.SourceGoal_c;
import polyglot.frontend.VisitorGoal;
import polyglot.util.ErrorQueue;
import x10.parser.X10Lexer;
import x10.parser.X10Parser;
import x10.visit.InstanceInvariantChecker;
import x10.visit.PositionInvariantChecker;

/**
 * Information about our extension of the polyglot compiler. This derives from
 * the ExtensionInfo class used by the X10 compiler, and specializes it to create
 * a parser/scanner that can read from an arbitrary Reader, and to save the parser
 * and lexer for "interesting" source files for later reference. It also creates a
 * dummy goal to record the AST of "interesting" source files.
 * @author beth
 * @author rfuhrer@watson.ibm.com
 */
public class ExtensionInfo extends x10.ExtensionInfo {
    private X10Lexer x10_lexer;
    private X10Parser x10_parser;
    private final Monitor monitor;
    private final IMessageHandler handler;
    private final Set<Source> fInterestingSources = new HashSet<Source>();
    private final Map<Source,Node> fInterestingASTs = new HashMap<Source,Node>();
    private final Map<Source,Job> fInterestingJobs = new HashMap<Source,Job>();
    private final Map<Source,X10Parser> fInterestingParsers = new HashMap<Source,X10Parser>();
    private final Map<Source,X10Lexer> fInterestingLexers = new HashMap<Source,X10Lexer>();

    public ExtensionInfo(Monitor monitor, IMessageHandler handler) {
        this.monitor = monitor;
        this.handler = handler;
        x10_lexer = new X10Lexer();
        x10_lexer.reset(new char[0], ""); // PORT1.7 BOB HACK to make sure lexer has a lexstream, needed by remapTerminalSymbols() (which probably shouldn't need it!)
        x10_parser = new X10Parser(x10_lexer.getILexStream()); // PORT1.7 Create this early
    }

    public void setInterestingSources(Collection<Source> sources) {
        fInterestingSources.clear();
        fInterestingJobs.clear();
        fInterestingASTs.clear();
        fInterestingLexers.clear();
        fInterestingParsers.clear();
        fInterestingSources.addAll(sources);
    }

    public X10Lexer getLexerFor(Source src) { return fInterestingLexers.get(src); }
    public X10Parser getParserFor(Source src) { return fInterestingParsers.get(src); }
    public Node getASTFor(Source src) { Job job= fInterestingJobs.get(src); return (job != null) ? job.ast() : null; /* return fInterestingASTs.get(src); */ }
    public Job getJobFor(Source src) { return fInterestingJobs.get(src); }

    @Override
    protected Scheduler createScheduler() {
        return new X10Scheduler(this) {
            @Override
            public List<Goal> goals(Job job) {
 
                if (fInterestingSources.contains(job.source())) {
                	fInterestingJobs.put(job.source(), job);
                }
                return super.validateOnlyGoals(job);
            }
 
        };
    }

    public Parser parser(Reader reader, FileSource source, ErrorQueue eq) {
        // PORT1.7 Create a new lexer/parser for every parse - unlike with 1.5,
        // this method can be called on behalf of different source files (including
        // source files residing in the runtime jar), and the front-end can be in
        // the middle of processing one source when it starts processing another.
        x10_lexer = new X10Lexer();
        x10_lexer.reset(new char[0], ""); // PORT1.7 BOB HACK to make sure lexer has a lexstream, needed by remapTerminalSymbols() (which probably shouldn't need it!)
        x10_parser = new X10Parser(x10_lexer.getILexStream());
        if (reader instanceof CharBufferReader) {
            x10_lexer.reset(((CharBufferReader) reader).getBuffer(), source.toString());
        } else {
            char[] buffer= StreamUtils.readReaderContents(reader).toCharArray();
            x10_lexer.reset(buffer, source.toString());
        }
        x10_parser.initialize(typeSystem(), nodeFactory(), source, eq); // PORT1.7 Now created early, but initialized here once the source is known
        x10_lexer.lexer(x10_parser.getIPrsStream());
        x10_parser.getIPrsStream().setMessageHandler(handler);
        if (fInterestingSources.contains(source)) {
            fInterestingLexers.put(source, x10_lexer);
            fInterestingParsers.put(source, x10_parser);
        }
        return x10_parser;
    }
}
