/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.types.constraints.xnative;

import java.util.Collections;
import java.util.List;

import polyglot.ast.IntLit;
import polyglot.ast.IntLit.Kind;
import polyglot.ast.IntLit_c;
import polyglot.ast.Typed;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.Position;

import x10.constraint.XLit;
import x10.constraint.xnative.XNativeLit;
import x10.types.constraints.CLit;
import x10.types.constraints.ConstraintManager;

/**
 * An optimized representation of literals.
 * Keeps a value and a type.
 * @author vj
 */
public class CNativeLit extends XNativeLit implements CLit, Typed {
    private static final long serialVersionUID = -2033423584924662939L;
    protected final Type type;
    public CNativeLit(Object val, Type type) {
        super(val);
        this.type = Types.addSelfBinding(Types.baseType(type), this);
    }

    /**
     * Return the type of the literal.
     */
    @Override
    public Type type() {return type;}

    @Override
    public boolean equals(Object o) {
        if (this == o)           return true;
        if (!(o instanceof CNativeLit))return false;
        CNativeLit other = (CNativeLit) o;
        if (!super.equals(other))return false;
        if (type == null)        return other.type == null;
        TypeSystem ts = type.typeSystem();
        return ts.typeEquals(Types.baseType(type), Types.baseType(other.type), ts.emptyContext());
    }

    @Override 
    public String toString() {
        if (type != null && type.isUnsignedNumeric()) {
            return new IntLit_c(Position.COMPILER_GENERATED, 
            					ConstraintManager.getConstraintSystem().getIntLitKind(type), 
            					((Number) val).longValue()).toString();
        }
        return super.toString();
    }

}
