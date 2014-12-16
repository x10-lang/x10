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

import polyglot.ast.Lit;
import polyglot.ast.NodeFactory;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import x10.ast.Closure_c;

public class ClosureValue extends ConstantValue {
    final Closure_c cls;
    
    ClosureValue(Closure_c cls) {
        this.cls = cls;
    }

    @Override
    public Lit toLit(NodeFactory nf, TypeSystem ts, Type type, Position pos) {
        throw new UnsupportedOperationException("Cannot get a Lit for a ClosureValue");
    }

    @Override
    public Lit toUntypedLit(NodeFactory nf, Position pos) {
        throw new UnsupportedOperationException("Cannot get a Lit for a ClosureValue");
    }

    @Override
    public Type getLitType(TypeSystem ts) {
        return cls.type();
    }

    @Override
    public Object toJavaObject() {
        throw new UnsupportedOperationException("Cannot convert a ClosureValue to a JavaObject");
    }
    
    public Closure_c getClosure() { return cls; }

}
