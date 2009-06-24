/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Special;
import polyglot.ast.Special.Kind;

public interface X10Special extends Special {
    public static final Kind SELF = new Kind("self");
    boolean isSelf();
}
