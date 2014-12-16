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

import polyglot.ast.NodeFactory;
import polyglot.ast.StringLit;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.Position;
import x10.types.constraints.ConstraintManager;


/**
 * A constant value that represents a String constant
 */
public class StringValue extends ConstantValue {
    
    private final String val;
    
    StringValue(String s) {
        val = s;
    }
    
    public String value() {
        return val;
    }

    @Override
    public String toJavaObject() {
        return val;
    }

    @Override
    public StringLit toLit(NodeFactory nf, TypeSystem ts, Type type, Position pos) {
        type = Types.addSelfBinding(type, ConstraintManager.getConstraintSystem().makeLit(toJavaObject(), getLitType(ts)));
        return (StringLit)nf.StringLit(pos, val).type(type);
    }

    @Override
    public Type getLitType(TypeSystem ts) {
        return ts.String();
    }

    @Override
    public StringLit toUntypedLit(NodeFactory nf, Position pos) {
        return (StringLit)nf.StringLit(pos, val);
    }

    @Override
    public boolean equals(Object that) {
        if (that instanceof StringValue) {
            return val.equals(((StringValue) that).val);
        } else {
            return false;
        }
    }
    
    @Override 
    public int hashCode() {
        return val.hashCode();
    }
    
    @Override
    public String toString() {
        return val;
    }

}
