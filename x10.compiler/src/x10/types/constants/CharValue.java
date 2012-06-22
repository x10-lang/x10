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

import polyglot.ast.CharLit;
import polyglot.ast.FloatLit;
import polyglot.ast.NodeFactory;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import x10.types.constraints.ConstraintManager;


/**
 * A class to represent a constant of type Char.
 */
public final class CharValue extends ConstantValue {
    private final char val;
    
    CharValue(char f) {
        val = f;
    }
    
    public char value() {
        return val;
    }

    @Override
    public Character toJavaObject() {
        return Character.valueOf(val);
    }

    @Override
    public CharLit toLit(NodeFactory nf, TypeSystem ts, Type type, Position pos) {
        type = Types.addSelfBinding(type, ConstraintManager.getConstraintSystem().makeLit(toJavaObject(), getLitType(ts)));
        return (CharLit)nf.CharLit(pos, val).type(type);
    }

    @Override
    public Type getLitType(TypeSystem ts) {
        return ts.Char();
    }

    @Override
    public CharLit toUntypedLit(NodeFactory nf, Position pos) {
        return (CharLit)nf.CharLit(pos, val);
    }

    @Override
    public boolean equals(Object that) {
        if (that instanceof CharValue) {
            return ((CharValue) that).val == val;
        } else {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return Character.valueOf(val).hashCode();
    }
    
    @Override
    public String toString() {
        return Character.toString(val);
    }
    
    @Override
    public long integralValue() {
        return (long)val;
    }
   
    @Override
    public double doubleValue() {
        return (double)val;
    }
    
    @Override
    public float floatValue() {
        return (float)val;
    }

}
