/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * This file was originally derived from the Polyglot extensible compiler framework.
 *
 *  (C) Copyright 2000-2007 Polyglot project group, Cornell University
 *  (C) Copyright IBM Corporation 2007-2015.
 */

package polyglot.ast;

import java.math.BigInteger;

import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.types.Type;
import polyglot.types.Context;
import polyglot.types.Types;
import polyglot.util.*;
import polyglot.visit.ContextVisitor;
import polyglot.visit.PrettyPrinter;
import x10.errors.Errors;
import x10.errors.Errors.IllegalConstraint;
import x10.types.constants.ConstantValue;
import x10.types.constraints.CConstraint;
import x10.types.constraints.ConstraintManager;
import x10.constraint.XTerm;
import x10.constraint.XFailure;

/**
 * An immutable representation of an int lit, modified from JL to support a
 * self-clause in the dep type.
 */
public class IntLit_c extends NumLit_c implements IntLit
{
    /** The kind of literal: INT or LONG. */ 
    protected Kind kind;

    public IntLit_c(Position pos, Kind kind, long value) {
	super(pos, value);
	assert(kind != null);
        this.kind = kind;
    }

    /**
     * @return True if this is a boundary case: the literal can only appear
     * as the operand of a unary minus.
     */
    public boolean boundary() {
        return (kind == INT && (int) value == Integer.MIN_VALUE)
            || (kind == LONG && value == Long.MIN_VALUE)
            || (kind == BYTE && value == Byte.MIN_VALUE)
            || (kind == SHORT && value == Short.MIN_VALUE);
    }

    /** Get the value of the expression. */
    public long value() {
        return longValue();
    }

    /** Set the value of the expression. */
    public IntLit value(long value) {
        IntLit_c n = (IntLit_c) copy();
	n.value = value;
	return n;
    }

    /** Get the kind of the expression. */
    public IntLit.Kind kind() {
        return kind;
    }

    /** Set the kind of the expression. */
    public IntLit kind(IntLit.Kind kind) {
	IntLit_c n = (IntLit_c) copy();
	n.kind = kind;
	return n;
    }

    /** Type check the expression. */
    private void rangeCheck(ContextVisitor tc, Kind signed, Kind unsigned, long max, long min, long boundary) {
        if (kind == signed) {
            if ((value > max  || value < min)) {
                Errors.issue(tc.job(),
                        new Errors.LiteralOutOfRange(signed.toString(), value, position()));
            }
        }
        if (kind == unsigned) {
            if (value < 0 || value > boundary) {
                Errors.issue(tc.job(),
                        new Errors.LiteralOutOfRange(unsigned.toString(), value, position()));
            }
        }

    }
    public Node typeCheck(ContextVisitor tc) {
        // todo: handle LONG and ULONG
        rangeCheck(tc, Kind.INT, Kind.UINT, 1l+Integer.MAX_VALUE, Integer.MIN_VALUE, 0xffffffffl);
        rangeCheck(tc, Kind.SHORT, Kind.USHORT, 1l+Short.MAX_VALUE, Short.MIN_VALUE, 0xffffl);
        rangeCheck(tc, Kind.BYTE, Kind.UBYTE, 1l+Byte.MAX_VALUE, Byte.MIN_VALUE, 0xffl);
        TypeSystem xts = (TypeSystem) tc.typeSystem();
        Type Type;
        switch (kind) {
        case BYTE:
            Type = xts.Byte();
            break;
        case SHORT:
            Type = xts.Short();
            break;
        case INT:
            Type = xts.Int();
            break;
        case LONG:
            Type = xts.Long();
            break;
        case UBYTE:
            Type = xts.UByte();
            break;
        case USHORT:
            Type = xts.UShort();
            break;
        case UINT:
            Type = xts.UInt();
            break;
        case ULONG:
            Type = xts.ULong();
            break;
        default:
            throw new InternalCompilerError("bad integer literal kind", position());
        }
        CConstraint c = ConstraintManager.getConstraintSystem().makeCConstraint();
        try {
         XTerm term = xts.xtypeTranslator().translate(c, this.type(Type), (Context) tc.context());
         c.addSelfBinding(term);
        } catch (IllegalConstraint z) {
        	Errors.issue(tc.job(), z);
        }
        Type newType = Types.xclause(Type, c);
        return type(newType);
    }

    public String positiveToString() {
        assert boundary();
        return kind == LONG ? "9223372036854775808L" :
                kind == INT ? "2147483648" :
                ""+Math.abs(value);
    }

    public String toString() {
        if (kind() == UINT) {
            return Long.toString(value & 0xffffffffL) + "UN";
        }
        else if (kind() == LONG) {
            return Long.toString(value) + "L";
        }
        else if (kind() == ULONG) {
            StringBuilder sb = new StringBuilder();
            long a = value;
            if (a >= 0)
                return Long.toString(a);
            byte[] bytes = new byte[9];
            for (int i = 0; i < 8; i++) {
                bytes[8-i] = (byte) (a & 0xff);
                a >>>= 8;
            }
            return new BigInteger(bytes).toString() + "UL";
        }
        else {
            String s = Long.toString((int) value);
            if (kind() == BYTE) {
            	s += "Y";
            } else if (kind() == UBYTE) {
            	s += "UY";
            } else if (kind() == SHORT) {
            	s += "S";
            } else if (kind() == USHORT) {
            	s += "US";
            } else if (kind() == INT) {
                s += "N";
            }
            return s;
        }
    }

    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
        w.write(toString());
    }

    public ConstantValue constantValue() {
        return ConstantValue.makeIntegral(value, kind());
    }

    public Precedence precedence() {
        if (value < 0L && ! boundary()) {
            return Precedence.UNARY;
        }
        else {
            return Precedence.LITERAL;
        }
    }
    

}
