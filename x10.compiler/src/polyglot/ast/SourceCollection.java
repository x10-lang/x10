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

package polyglot.ast;

import java.util.List;


/**
 * A <code>SourceCollection</code> represents a collection of source files.
 * This node should be used only during AST rewriting, just before code
 * generation in order to generate multiple target files from a single
 * AST.
 */
public interface SourceCollection extends Node
{
    /** List of source files in the collection.
     * @return A list of {@link polyglot.ast.SourceFile SourceFile}.
     */
    List<SourceFile> sources();

    /** Set the list of source files in the collection.
     * @param sources A list of {@link polyglot.ast.SourceFile SourceFile}.
     */
    SourceCollection sources(List<SourceFile> sources);
}
