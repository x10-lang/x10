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
 *  (C) Copyright IBM Corporation 2007-2015.
 */

package polyglot.frontend;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import polyglot.ast.NodeFactory;
import polyglot.main.Options;
import polyglot.types.TypeSystem;
import polyglot.types.reflect.ClassFile;
import polyglot.util.ErrorQueue;

/**
 * <code>ExtensionInfo</code> is the main interface for defining language
 * extensions.  The frontend will load the <code>ExtensionInfo</code>
 * specified on the command-line.  It defines the type system, AST node
 * factory, parser, and other parameters of a language extension.
 */
public interface ExtensionInfo {
    /** The name of the compiler for usage messages */
    String compilerName();

    /** Report the version of the extension. */
    polyglot.main.Version version();

    /** Returns the pass scheduler. */
    Scheduler scheduler();
    
    /** 
     * Return an Options object, which will be given the command line to parse.
     */    
    Options getOptions();

    /**
     * Initialize the extension with a particular compiler.  This must
     * be called after the compiler is initialized, but before the compiler
     * starts work.
     */
    void initCompiler(polyglot.frontend.Compiler compiler);

    public Compiler compiler();

    /** The extensions that source files are expected to have.
     * Defaults to the array defaultFileExtensions. */
    String[] fileExtensions();

    /** The default extensions that source files are expected to have.
     * Defaults to an array containing defaultFileExtension */
    String[] defaultFileExtensions();

    /** The default extension that source files are expected to have. */
    String defaultFileExtension();

    /** Produce a type system for this language extension. */
    TypeSystem typeSystem();

    /** Produce a node factory for this language extension. */
    NodeFactory nodeFactory();

    /** Produce a source factory for this language extension. */
    SourceLoader sourceLoader();

    /**
     * Get the job extension for this language extension.  The job
     * extension is used to extend the <code>Job</code> class
     * without subtyping.
     */
    JobExt jobExt();
    
    /**
     * Produce a target factory for this language extension.  The target
     * factory is responsible for naming and opening output files given a
     * package name and a class or source file name.
     */
    TargetFactory targetFactory();

    /** Get a parser for this language extension. */
    Parser parser(Reader reader, FileSource source, ErrorQueue eq);

    /** Create file source for a file. The main purpose is to allow
        the character encoding to be defined. */
    FileSource createFileSource(File sourceFile, boolean userSpecified)
	throws IOException;
    
    /** Create file source for a file. The main purpose is to allow
        the character encoding to be defined. */
    FileSource createFileSource(Resource sourceFile, boolean userSpecified)
    throws IOException;

}
