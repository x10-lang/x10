/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.types;

import java.util.List;

public interface TopLevelResolver {
    /** Find a Type by a qualified name. */
    public List<Type> find(QName name) throws SemanticException;
    
    /** Find a single Type by a qualified name. */
    public Type findOne(QName name) throws SemanticException;
    
    /** Find a Package by a qualified name. */
    public Package findPackage(QName name) throws SemanticException;
    
    /** Check if a package exists. */
    public boolean packageExists(QName name);
}
