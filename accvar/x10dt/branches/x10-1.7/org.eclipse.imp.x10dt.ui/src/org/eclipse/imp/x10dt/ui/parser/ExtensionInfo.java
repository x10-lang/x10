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
import polyglot.frontend.Goal;
import polyglot.frontend.Job;
import polyglot.frontend.Parser;
import polyglot.frontend.Scheduler;
import polyglot.frontend.Source;
import polyglot.frontend.SourceGoal_c;
import polyglot.util.ErrorQueue;
import x10.parser.X10Lexer;
import x10.parser.X10Parser;

/**
 * Information about our extension of the polyglot compiler. This derives from
 * the ExtensionInfo class used by the X10 compiler, and specializes it to create
 * a parser/scanner that can read from an arbitrary Reader, and to save the parser
 * and lexer for "interesting" source files for later reference. It also creates a
 * dummy goal to record the AST of "interesting" source files.
 * @author beth
 * @author rfuhrer@watson.ibm.com
 */
public class ExtensionInfo extends polyglot.ext.x10.ExtensionInfo {
    private X10Lexer x10_lexer;
    private X10Parser x10_parser;
    private final Monitor monitor;
    private final IMessageHandler handler;
    private final Set<Source> fInterestingSources = new HashSet<Source>();
    private final Map<Source,Node> fInterestingASTs = new HashMap<Source,Node>();
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
        fInterestingASTs.clear();
        fInterestingLexers.clear();
        fInterestingParsers.clear();
        fInterestingSources.addAll(sources);
    }

    public X10Lexer getLexerFor(Source src) { return fInterestingLexers.get(src); }
    public X10Parser getParserFor(Source src) { return fInterestingParsers.get(src); }
    public Node getASTFor(Source src) { return fInterestingASTs.get(src); }

    @Override
    protected Scheduler createScheduler() {
        return new X10Scheduler(this) {
            @Override
            public List<Goal> goals(Job job) {
                List<Goal> goals = new ArrayList<Goal>();
                // This is essentially the list of goals specified by the base class,
                // up through and including type-checking.
                goals.add(Parsed(job));
                
                // Grab AST early so outline view can use it even if these goals don't finish
                if (ExtensionInfo.this.fInterestingSources.contains(job.source())) {
                    goals.add(RetrieveASTearly(job));
                }
                goals.add(TypesInitialized(job));
                goals.add(ImportTableInitialized(job));
                goals.add(CastRewritten(job));
                goals.add(PropagateAnnotations(job));
                goals.add(PreTypeCheck(job));
                goals.add(TypeChecked(job));

                // AST will be more complete here
                if (ExtensionInfo.this.fInterestingSources.contains(job.source())) {
                    goals.add(RetrieveAST(job));
                }

                goals.add(End(job));
                return goals;
            }
            /**
             * This goal simply retrieves the AST for the given job and squirrels it
             * away into the map fInterestingASTs.  This will overwrite the AST that was
             * saved by the RetrieveASTearly goal
             */
            Goal RetrieveAST(Job job) {
                return new SourceGoal_c("AST retriever", job) {
                    @Override
                    public boolean runTask() {
                        ExtensionInfo.this.fInterestingASTs.put(job().source(), job().ast());
                        return true;
                    }
                }.intern(scheduler);
            }
            /**
             * This goal simply retrieves the AST for the given job and squirrels it
             * away into the map fInterestingASTs.
             */
            Goal RetrieveASTearly(Job job) {
                return new SourceGoal_c("AST early retriever", job) {
                    @Override
                    public boolean runTask() {
                        ExtensionInfo.this.fInterestingASTs.put(job().source(), job().ast());
                        return true;
                    }
                }.intern(scheduler);
            }
            @Override
            protected Goal EndAll() { // a dummy goal that replaces PostCompiled
                return new AllBarrierGoal(this) {
                    @Override
                    public Goal prereqForJob(Job job) {
                        if (scheduler.shouldCompile(job)) {
                            return scheduler.End(job);
                        }
                        else {
                            return new SourceGoal_c("DummyEnd", job) {
                                public boolean runTask() { return true; }
                            }.intern(scheduler);
                        }
                    }
                };
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
            x10_lexer.reset(((CharBufferReader) reader).getBuffer(), source.path());
        } else {
            char[] buffer= StreamUtils.readReaderContents(reader).toCharArray();
            x10_lexer.reset(buffer, source.path());
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
