/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on Nov 30, 2004
 */
package polyglot.ext.x10.types;

import polyglot.types.ParsedClassType;

/**
 * @author vj
 *
 *
 */
public interface X10ParsedClassType extends ParsedClassType, X10ClassType, X10NamedType {

    /**
     * The root of the superclass hierarchy -- either ts.X10Object() or ts.Object().
     * Must return ts.X10Object() if it encounters ts.x10Object() on its way
     * from this type up the superType() links. Otherwise must return ts.Object()
     * @return
     */
    X10ClassType superClassRoot();

    /** Returns true iff superClassRoot() equals ts.Object().
     * @return
     */
    boolean isJavaType();

    /**
     * Returns true iff this type is an X10 array.
     * @return
     */
    boolean isX10Array();

}
