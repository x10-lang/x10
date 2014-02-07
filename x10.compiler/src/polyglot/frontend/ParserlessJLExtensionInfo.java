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
