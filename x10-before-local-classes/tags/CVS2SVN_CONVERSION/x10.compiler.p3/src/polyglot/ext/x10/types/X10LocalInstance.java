/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
/**
 * 
 */
package polyglot.ext.x10.types;

import polyglot.types.LocalInstance;
import polyglot.types.Type;

/**
 * @author vj
 *
 */
public interface X10LocalInstance extends LocalInstance, X10TypeObject, X10Use<X10LocalDef> {
    /** Type of the local with self==FI. */
    Type rightType();
}
