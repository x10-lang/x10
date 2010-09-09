/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.types;

public interface TopLevelResolver {
    /** Find a Named thing by a qualified name. */
    public Named find(QName name) throws SemanticException;
    
    /**
     * Check if a package exists.
     */
    public boolean packageExists(QName name);
}
