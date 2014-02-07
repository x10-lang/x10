/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * This file was originally derived from the Polyglot extensible compiler framework.
 *
 *  (C) Copyright 2000-2007 Polyglot project group, Cornell University
 *  (C) Copyright IBM Corporation 2007-2014.
 */

package polyglot.frontend;

import java.util.*;

import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.types.TypeSystem;
import polyglot.util.CodeWriter;
import polyglot.util.CollectionUtil; import x10.util.CollectionFactory;
import polyglot.visit.TypeBuilder;

/**
 * A <code>Job</code> encapsulates work done by the compiler for a single
 * compilation unit. A <code>Job</code> contains all information for a
 * particular compilation unit carried between phases of the compiler.
 * Only one pass should be run over a job at a time.
 * 
 * TODO: The class should probably be renamed to, say, CompilationUnit.
 */
public class Job
{
    /** Field used for storing extension-specific information. */
    protected JobExt ext;

    /** The language extension used for this job. */
    protected ExtensionInfo lang;

    /** The AST constructed from the source file. */
    protected Node ast;
    
    /** Map for memoizing nodes during type-checking. */
    protected Map<Node,Node> nodeMemo;

    /** True if all passes run so far have been successful. */
    protected boolean status;

    /** Initial count of errors before running the current pass over this job. */
    protected int initialErrorCount;

    /** True if this job has reported an error. */
    protected boolean reportedErrors;

    /** True if this job was completely processed by the scheduler. */
    protected boolean completed;
    
    /** The <code>Source</code> that this <code>Job</code> represents. */
    protected Source source;

    public Job(ExtensionInfo lang, JobExt ext, Source source, Node ast) {
        this.lang = lang;
        this.ext = ext;
        this.source = source;
        this.ast = ast;

        this.status = true;
        this.initialErrorCount = 0;
        this.reportedErrors = false;
        this.completed = false;
    }
    
    public Map<Node,Node> nodeMemo() {
        if (nodeMemo == null) {
            nodeMemo = CollectionFactory.newHashMap();
        }
        return nodeMemo;
    }
    
    public void setNodeMemo(Map<Node,Node> map) {
        this.nodeMemo = map;
    }
    
    public JobExt ext() {
      return ext;
    }

    /** Get the state's AST. */
    public Node ast() {
	return ast;
    }

    /** Set the state's AST. */
    public void ast(Node ast) {
        this.ast = ast;
    }

    /** True if some pass reported an error. */
    public boolean reportedErrors() {
        return reportedErrors;
    }

    /** True if all passes have been completed. */
    void setCompleted(boolean value) {
        completed = value;
    }
    
    /** True if all passes have been completed. */
    public boolean completed() {
        return completed;
    }
    
    public void dump(CodeWriter cw) {
	if (ast != null) {
	    ast.dump(cw);
	}
    }

    /**
     * Return the <code>Source</code> associated with the 
     * <code>SourceJob</code> returned by <code>sourceJob</code>.
     */
    public Source source() {
        return this.source;
    }
    
    /**
     * Returns whether the source for this job was explicitly specified
     * by the user, or if it was drawn into the compilation process due
     * to some dependency.
     */
    public boolean userSpecified() {
        return this.source().userSpecified();
    }

    public void updateStatus(boolean status) {
        if (! status) {
            this.status = false;
        }
    }
   
    public boolean status() {
        return status;
    }

    public ExtensionInfo extensionInfo() {
	return lang;
    }

    public Compiler compiler() {
	return lang.compiler();
    }
    
    public String toString() {
        return source.toString();
    }

    public int hashCode() {
        return source.hashCode();
    }
    
    public boolean equals(Object o) {
        return o instanceof Job && ((Job) o).source.equals(source);
    }

    Goal TypesInitialized;
    
    public Goal TypesInitialized(Scheduler scheduler) {
        if (TypesInitialized == null) {
            TypesInitialized = scheduler.constructTypesInitialized(this);
        }
        return TypesInitialized;
    }
}
