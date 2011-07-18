/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.ast;


/**
 * A top-level declaration.  This is any declaration that can appear in the
 * outermost scope of a source file.
 */
public interface TopLevelDecl extends Node
{
    /** The declaration's flags. */
    FlagsNode flags();

    /** The declaration's name. */
    Id name();
}
