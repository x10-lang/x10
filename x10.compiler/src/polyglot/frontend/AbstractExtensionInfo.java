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
import polyglot.main.Version;
import polyglot.types.TypeSystem;
import polyglot.types.reflect.ClassFile;
import polyglot.util.ErrorQueue;

/**
 * This is an abstract <code>ExtensionInfo</code>.
 */
public abstract class AbstractExtensionInfo implements ExtensionInfo {
    protected Compiler compiler;
    private Options options;
    protected TypeSystem ts = null;
    protected NodeFactory nf = null;
    protected SourceLoader source_loader = null;
    protected TargetFactory target_factory = null;
    protected Scheduler scheduler;

    public abstract String compilerName();
    public abstract String defaultFileExtension();
    public abstract Version version();
    
    public Options getOptions() {
        if (this.options == null) {
            this.options = createOptions();
        }
        return options;
    }

    protected Options createOptions() {
        return new Options(this);
    }

    public Compiler compiler() {
        return compiler;
    }

    public void initCompiler(Compiler compiler) {
        this.compiler = compiler;

        // Register the extension with the compiler.
        compiler.addExtension(this);

        // Create the type system and node factory.
        typeSystem();
        nodeFactory();
        scheduler();

        initTypeSystem();
    }

    protected abstract void initTypeSystem();

    /**
     * Get the file name extension of source files.  This is
     * either the language extension's default file name extension
     * or the string passed in with the "-sx" command-line option.
     */
    public String[] fileExtensions() {
	String[] sx = getOptions() == null ? null : getOptions().source_ext;

	if (sx == null) {
	    sx = defaultFileExtensions();
        }

        if (sx.length == 0) {
            return defaultFileExtensions();
        }

        return sx;
    }

    /** Get the default list of file extensions. */
    public String[] defaultFileExtensions() {
        String ext = defaultFileExtension();
        return new String[] { ext };
    }

    /** Get the source file loader object for this extension. */
    public SourceLoader sourceLoader() {
        if (source_loader == null) {
            source_loader = new SourceLoader(this, getOptions().source_path);
        }

        return source_loader;
    }

    /** Get the target factory object for this extension. */
    public TargetFactory targetFactory() {
        if (target_factory == null) {
            target_factory = new TargetFactory(getOptions().output_directory,
                                               getOptions().output_ext,
                                               getOptions().output_stdout,
                                               getOptions().reporter);
        }

        return target_factory;
    }
    
    protected abstract Scheduler createScheduler();
    
    public final Scheduler scheduler() {
        if (scheduler == null) {
            scheduler = createScheduler();
        }
        return scheduler;
    }

    /** Create the type system for this extension. */
    protected abstract TypeSystem createTypeSystem();

    /** Get the type system for this extension. */
    public final TypeSystem typeSystem() {
	if (ts == null) {
	    ts = createTypeSystem();
	}
	return ts;
    }

    /** Create the node factory for this extension. */
    protected abstract NodeFactory createNodeFactory();

    /** Get the AST node factory for this extension. */
    public final NodeFactory nodeFactory() {
	if (nf == null) {
	    nf = createNodeFactory();
	}
	return nf;
    }

    /**
     * Get the job extension for this language extension.  The job
     * extension is used to extend the <code>Job</code> class
     * without subtyping.
     */
    public JobExt jobExt() {
      return null;
    }

    /** Get the parser for this language extension. */
    public abstract Parser parser(Reader reader, FileSource source,
                                  ErrorQueue eq);

    public String toString() {
        return getClass().getName();
    }

    public FileSource createFileSource(File f, boolean user)
	throws IOException
    {
	return createFileSource(new FileResource(f), user);
    }
    
    public FileSource createFileSource(Resource f, boolean user)
    throws IOException
    {
	return new FileSource(f, user);
    }
}
