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

package polyglot.ast;

import java.util.List;

import polyglot.frontend.Source;
import polyglot.types.ImportTable;

/**
 * A <code>SourceFile</code> is an immutable representations of a Java
 * language source file.  It consists of a package name, a list of 
 * <code>Import</code>s, and a list of <code>GlobalDecl</code>s.
 */
public interface SourceFile extends Node
{
    /** Get the source's declared package. */
    PackageNode package_();

    /** Set the source's declared package. */
    SourceFile package_(PackageNode package_);

    /** Get the source's declared imports.
     * @return A list of {@link polyglot.ast.Import Import}.
     */
    List<Import> imports();

    /** Set the source's declared imports.
     * @param imports A list of {@link polyglot.ast.Import Import}.
     */
    SourceFile imports(List<Import> imports);

    /** Get the source's top-level declarations.
     * @return A list of {@link polyglot.ast.TopLevelDecl TopLevelDecl}.
     */
    List<TopLevelDecl> decls();

    /** Set the source's top-level declarations.
     * @param decls A list of {@link polyglot.ast.TopLevelDecl TopLevelDecl}.
     */
    SourceFile decls(List<TopLevelDecl> decls);

    /** Get the source's import table. */
    ImportTable importTable();

    /** Set the source's import table. */
    SourceFile importTable(ImportTable importTable);
 
    /** Get the source file. */
    Source source();

    /** Set the source file. */
    SourceFile source(Source source);
}
