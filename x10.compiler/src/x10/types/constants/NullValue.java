/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2010.
 */
package x10.types.constants;

import polyglot.ast.Expr;
import polyglot.ast.Lit;
import polyglot.ast.NodeFactory;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.Position;

/**
 * A constant value the represents the constant null.
 */
public final class NullValue extends ConstantValue {

    NullValue() { }
    
    public Object toJavaObject() { return null; }
    
    @Override
    public Lit toLit(NodeFactory nf, TypeSystem ts, Position pos) {
        return nf.NullLit(pos);
    }

    public Lit toUntypedLit(NodeFactory nf, Position pos) {
        return nf.NullLit(pos);
    }

    @Override
    public boolean equals(Object that) {
        return that instanceof NullValue;
    }
    
    @Override 
    public int hashCode() {
        return 2112;
    }
    
    @Override
    public String toString() {
        return "null";
    }
}
