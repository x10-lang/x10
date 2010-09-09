/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/**
 * 
 */
package polyglot.ext.x10.types;

import polyglot.types.LocalInstance;

/**
 * @author vj
 *
 */
public interface X10LocalInstance extends LocalInstance, X10TypeObject, X10Use<X10LocalDef> {
    int positionInArgList();

    /**
     * Set the self var on the depclause associated with this
     * local instance if the variable is declared final.
     * Return true iff the LI changed.
     */
//    boolean setSelfClauseIfFinal();
}
