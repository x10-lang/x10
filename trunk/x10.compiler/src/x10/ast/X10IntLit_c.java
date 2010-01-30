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
package x10.ast;

import polyglot.ast.IntLit_c;
import polyglot.ast.Node;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import x10.constraint.XFailure;
import x10.constraint.XTerm;
import x10.types.X10Context;

import x10.types.X10TypeMixin;
import x10.types.X10TypeSystem;
import x10.types.XTypeTranslator;
import x10.types.constraints.CConstraint;
import x10.types.constraints.CConstraint_c;
import polyglot.ast.IntLit;
import polyglot.ast.IntLit.Kind;

/**
 * An immutable representation of an int lit, modified from JL to support a
 * self-clause in the dep type.
 * 
 * @author vj
 * 
 */
public class X10IntLit_c extends IntLit_c {

    public static class KindWithUnsigned extends IntLit.Kind {

        public KindWithUnsigned(String name) {
            super(name);
        }
    }

    public static final KindWithUnsigned UINT = new KindWithUnsigned("uint");
    public static final KindWithUnsigned ULONG = new KindWithUnsigned("ulong");

    /**
     * @param pos
     * @param kind
     * @param value
     */
    public X10IntLit_c(Position pos, Kind kind, long value) {
        super(pos, kind, value);
    }

    /** Type check the expression. */
    public Node typeCheck(ContextVisitor tc) throws SemanticException {
        if (kind == INT) {
            if ((value > (long) Integer.MAX_VALUE || value < (long) Integer.MIN_VALUE) && (value & ~0xFFFFFFFFL) != 0L) {
                throw new SemanticException("Integer literal " + value + " is out of range.", position());
            }
        }
        if (kind == UINT) {
            if (value < 0 && (value & ~0xFFFFFFFFL) != 0L) {
                throw new SemanticException("Unsigned integer literal " + value + " is out of range.", position());
            }
        }
        X10TypeSystem xts = (X10TypeSystem) tc.typeSystem();
        Type Type;
        if (kind == INT) {
            Type = xts.Int();
        }
        else if (kind == UINT) {
            Type = xts.UInt();
        }
        else if (kind == LONG) {
            Type = xts.Long();
        }
        else if (kind == ULONG) {
            Type = xts.ULong();
        }
        else {
            throw new InternalCompilerError("bad integer literal kind", position());
        }
        CConstraint c = new CConstraint_c();
        XTerm term = xts.xtypeTranslator().trans(c, this.type(Type), (X10Context) tc.context());
        try {
            c.addSelfBinding(term);
        }
        catch (XFailure e) {
        }
        Type newType = X10TypeMixin.xclause(Type, c);
        return type(newType);
    }
    
    @Override
    public String toString() {
        if (kind() == UINT) {
            return Long.toString(value & 0xffffffffL) + "U";
        }
        else if (kind() == LONG) {
            return Long.toString(value) + "L";
        }
        else if (kind() == ULONG) {
            StringBuilder sb = new StringBuilder();
            long a = value;
            if (a >= 0)
                return Long.toString(a);
            while (a != 0) {
                char ch = (char) ('0' + a % 10);
                sb.append(ch);
                a /= 10;
            }
            return sb.reverse().toString() + "UL";
        }
        else {
            return Long.toString((int) value);
        }
    }
}
