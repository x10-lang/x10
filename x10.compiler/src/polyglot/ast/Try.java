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

/**
 * An immutable representation of a <code>try</code> block, one or more
 * <code>catch</code> blocks, and an optional <code>finally</code> block.
 *
 */
public interface Try extends CompoundStmt
{
    /** The block to "try". */
    Block tryBlock();

    /** Set the block to "try". */
    Try tryBlock(Block tryBlock);

    /** List of catch blocks.
     * @return A list of {@link polyglot.ast.Catch Catch}.
     */
    List<Catch> catchBlocks();

    /** Set the list of catch blocks.
     * @param catchBlocks A list of {@link polyglot.ast.Catch Catch}.
     */
    Try catchBlocks(List<Catch> catchBlocks);

    /** The block to "finally" execute. */
    Block finallyBlock();

    /** Set the block to "finally" execute. */
    Try finallyBlock(Block finallyBlock);
}
