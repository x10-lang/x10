/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.ast;

import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Id;
import polyglot.ast.Node;
import polyglot.ast.Receiver;
import polyglot.ast.TypeNode;
import polyglot.types.Context;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.errors.Errors;
import x10.types.MethodInstance;

/**
 * Representation of a call to an overloaded conversion method.
 * Differs from X10Call_c in that it has an extra conversion type argument.
 * @author igor
 */
public class X10ConversionCall_c extends X10Call_c {
    protected TypeNode conversionType;

    public X10ConversionCall_c(Position pos, Receiver target, Id name, TypeNode conversionType,
                               List<TypeNode> typeArguments, List<Expr> arguments) {
        super(pos, target, name, typeArguments, arguments);
        this.conversionType = conversionType;
    }

    @Override
    public Node visitChildren(NodeVisitor v) {
        X10ConversionCall_c res = (X10ConversionCall_c) super.visitChildren(v);
        TypeNode conversionType = (TypeNode) visitChild(this.conversionType, v);
        if (conversionType != this.conversionType) {
            if (res == this) {
                res = (X10ConversionCall_c) copy();
            }
            res.conversionType = conversionType;
        }
        return res;
    }

    @Override
    public Node typeCheck(ContextVisitor tc) {
        Expr res = (Expr) super.typeCheck(tc);
        if (res instanceof X10Call_c) {
            TypeSystem ts = tc.typeSystem();
            X10Call_c c = (X10Call_c) res;
            MethodInstance mi = c.methodInstance();
            Context cxt = tc.context();
            if (!ts.isSubtype(conversionType.type(), mi.container(), cxt)) {
                Errors.issue(tc.job(), new SemanticException("Conversion operator type must match the container", this.position()));
            }
        }
        return res;
    }
}

