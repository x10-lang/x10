/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.ast;

import java.util.Collections;

import polyglot.ast.Assign;
import polyglot.ast.Assign_c;
import polyglot.ast.Expr;
import polyglot.ast.Local;
import polyglot.ast.LocalAssign_c;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.types.LocalInstance;
import polyglot.types.Name;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import x10.types.X10LocalInstance;

public class X10LocalAssign_c extends LocalAssign_c {

    public X10LocalAssign_c(Position pos, Local left, Operator op, Expr right) {
        super(pos, left, op, right);
    }

    @Override
    public Type leftType() {
        LocalInstance li = local().localInstance();
        if (li == null)
            return null;
        if (li instanceof X10LocalInstance) {
            return ((X10LocalInstance) li).type();
        }
        return li.type();
    }

    /** Type check the expression. */
    public Node typeCheck(ContextVisitor tc) throws SemanticException {
        return X10LocalAssign_c.typeCheckAssign(this, tc);
    }

    public static Node typeCheckAssign(Assign_c a, ContextVisitor tc) throws SemanticException {
        Assign_c n = (Assign_c) a.typeCheckLeft(tc);

        TypeSystem ts = tc.typeSystem();
        Type t = n.leftType();

        if (t == null)
            t = ts.unknownType(n.position());

        Expr right = n.right();
        Assign.Operator op = n.operator();

        Type s = right.type();

        if (op == ASSIGN) {
            try {
                Expr e = X10New_c.attemptCoercion(tc, right, t);
                return n.right(e).type(t);
            }
            catch (SemanticException e) {
                throw new SemanticException("Cannot assign |" + right + "| (of type |" + s + ") to |" 
                                            + ((LocalAssign_c) n).local() + "| (of type " + t+").",
                                            n.position());
            }
        }

        if (op == ADD_ASSIGN) {
            // t += s
            if (ts.typeEquals(t, ts.String(), tc.context()) && ts.canCoerceToString(s, tc.context())) {
                Expr newRight = X10Binary_c.coerceToString(tc, right);
                return n.right(newRight).type(ts.String());
            }                

            if (t.isNumeric() && s.isNumeric()) {
                return n.type(ts.promote(t, s));
            }

            throw new SemanticException("The " + op + " operator must have "
                                        + "numeric or String operands.",
                                        n.position());
        }

        if (op == SUB_ASSIGN || op == MUL_ASSIGN ||
                op == DIV_ASSIGN || op == MOD_ASSIGN) {
            if (t.isNumeric() && s.isNumeric()) {
                return n.type(ts.promote(t, s));
            }

            throw new SemanticException("The " + op + " operator must have "
                                        + "numeric operands.",
                                        n.position());
        }

        if (op == BIT_AND_ASSIGN || op == BIT_OR_ASSIGN || op == BIT_XOR_ASSIGN) {
            if (t.isBoolean() && s.isBoolean()) {
                return n.type(ts.Boolean());
            }

            if (ts.isLongOrLess(t) &&
                    ts.isLongOrLess(s)) {
                return n.type(ts.promote(t, s));
            }

            throw new SemanticException("The " + op + " operator must have "
                                        + "integral or boolean operands.",
                                        n.position());
        }

        if (op == SHL_ASSIGN || op == SHR_ASSIGN || op == USHR_ASSIGN) {
            if (ts.isLongOrLess(t) &&
                    ts.isLongOrLess(s)) {
                // Only promote the left of a shift.
                return n.type(ts.promote(t));
            }

            throw new SemanticException("The " + op + " operator must have "
                                        + "integral operands.",
                                        n.position());
        }

        throw new InternalCompilerError("Unrecognized assignment operator " +
                                        op + ".");
    }

}