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

package x10.ast;

import java.util.List;

import polyglot.ast.Assign;
import polyglot.ast.Binary;
import polyglot.ast.Block;
import polyglot.ast.Conditional;
import polyglot.ast.Expr;
import polyglot.ast.Id;
import polyglot.ast.Local;
import polyglot.ast.LocalDecl;
import polyglot.ast.Node;
import polyglot.ast.Conditional_c;
import polyglot.ast.NodeFactory;
import polyglot.ast.Stmt;
import polyglot.ast.TypeNode;
import polyglot.ast.Unary;
import polyglot.types.Context;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import x10.constraint.XConstraint;
import x10.errors.Errors;
import polyglot.types.Context;

import polyglot.types.TypeSystem;
import x10.types.checker.Converter;

/**
 * @author VijaySaraswat
 *
 */
public class X10Conditional_c extends Conditional_c implements X10Conditional {

    /**
     * @param pos
     * @param cond
     * @param consequent
     * @param alternative
     */
    public X10Conditional_c(Position pos, Expr cond, Expr consequent, Expr alternative) {
        super(pos, cond, consequent, alternative);
    }

    public Node typeCheck(ContextVisitor tc) {
        TypeSystem ts = (TypeSystem) tc.typeSystem();
        Context context = tc.context();

        if (! cond.type().isBoolean()) {
            Errors.issue(tc.job(),
                    new Errors.TernaryConditionMustBeBoolean(cond.position()),
                    this);
        }

        Expr e1 = consequent;
        Expr e2 = alternative;

        Type t1 =  e1.type();
        Type t2 =  e2.type();

        // From the JLS, section:
            // If the second and third operands have the same type (which may be
        // the null type), then that is the type of the conditional expression.
        if (ts.typeEquals(t1, t2, context))
            return type(t1);

        // def m(b:Boolean, x:Object{self!=null}, y:Object{self!=null}):Object{self!=null} {
        //    val z:Object{self!=null} = b ? x : y; // should be ok, but the following test will return their baseType which is Object.
        //Semantic Error: Cannot assign expression to target.
        //     Expression: b ? x : y
        //     Expected type: x10.lang.Object{self!=null}
        //     Found type: x10.lang.Object
        //if (ts.typeBaseEquals(t1, t2, context)) {
        //    return type(Types.baseType(t1));
        //}

        // If one of the second and third operands is of the null type and the
        // type of the other is a reference type, then the type of the
        // conditional expression is that reference type.
        if (t1.isNull() && Types.permitsNull(context, t2)) return type(t2);
        if (t2.isNull() && Types.permitsNull(context, t1)) return type(t1);

        // If the second and third operands are of different reference types,
        // then it must be possible to convert one of the types to the other
        // type (call this latter type T) by assignment conversion (Sec. 5.2); the
        // type of the conditional expression is T. It is a compile-time error
        // if neither type is assignment compatible with the other type.

        if (t1.isReference() && t2.isReference()) {
            if (ts.isImplicitCastValid(t1, t2, context)) {
                return type(t2);
            }
            if (ts.isImplicitCastValid(t2, t1, context)) {
                return type(t1);
            }
        }

        try {
            Type t = ts.leastCommonAncestor(t1, t2, context);
            Expr n1 = Converter.attemptCoercion(tc, e1, t);
            Expr n2 = Converter.attemptCoercion(tc, e2, t);
            if (n1 != null && n2 != null)
                return consequent(n1).alternative(n2).type(t);
        }
        catch (SemanticException e) {
        }

        Errors.issue(tc.job(), new Errors.TernaryConditionalTypeUndetermined(t1, t2, position()));
        return this;
    }
}