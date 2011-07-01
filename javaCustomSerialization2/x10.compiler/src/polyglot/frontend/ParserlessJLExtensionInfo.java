/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.frontend;

import java.io.Reader;

import polyglot.ast.NodeFactory;
import polyglot.main.Version;
import polyglot.types.TypeSystem;
import polyglot.util.ErrorQueue;

/** This class implements most of the <code>ExtensionInfo</code> for the Java language.
 * It does not include a parser, however.  EPL-licensed extensions should extend this class
 * rather than JLExtensionInfo since they should not use the CUP-based grammar.
 * @author nystrom
 *
 */
public abstract class ParserlessJLExtensionInfo extends AbstractExtensionInfo {

    protected polyglot.frontend.Scheduler createScheduler() {
        return new JLScheduler(this);
    }

    public String defaultFileExtension() {
        return "jl";
    }

    public String compilerName() {
        return "jlc";
    }

    public Version version() {
        return new JLVersion();
    }

    /** Create the type system for this extension. */
    abstract protected TypeSystem createTypeSystem();

    /** Create the node factory for this extension. */
    abstract protected NodeFactory createNodeFactory();

    public JobExt jobExt() {
      return null;
    }

    /**
     * Return a parser for <code>source</code> using the given
     * <code>reader</code>.
     */
    public abstract Parser parser(Reader reader, FileSource source, ErrorQueue eq);
}
