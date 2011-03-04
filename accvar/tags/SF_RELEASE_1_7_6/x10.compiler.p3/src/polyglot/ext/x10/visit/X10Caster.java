/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.visit;

import polyglot.ast.Assign;
import polyglot.ast.Call;
import polyglot.ast.ConstructorCall;
import polyglot.ast.Expr;
import polyglot.ast.FieldDecl;
import polyglot.ast.LocalDecl;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.NullLit;
import polyglot.ext.x10.ast.ClosureCall;
import polyglot.ext.x10.ast.X10Cast;
import polyglot.ext.x10.ast.X10NodeFactory;
import polyglot.ext.x10.types.X10ClassType;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.frontend.Job;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;

/**
 * Visitor that inserts explicit subtyping coercions whenever there is an
 * implicit subtyping coercion where the base types are not equal. This is
 * needed for the backend to properly insert casts when coercing between generic
 * types with covariant parameters.
 */
public class X10Caster extends ContextVisitor {
    X10TypeSystem xts;

    public X10Caster(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
        xts = (X10TypeSystem) ts;
    }

    @Override
    public Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException {
        Type toType = null;

        if (!(old instanceof Expr)) {
            return n;
        }

        Expr e = (Expr) old;

        if (n instanceof NullLit) {
            return n;
        }

        if (parent instanceof Call) {
            Call p = (Call) parent;
            for (int i = 0; i < p.arguments().size(); i++) {
                if (e == p.arguments().get(i)) {
                    toType = p.methodInstance().formalTypes().get(i);
                    break;
                }
            }
        }

        if (parent instanceof New) {
            New p = (New) parent;
            for (int i = 0; i < p.arguments().size(); i++) {
                if (e == p.arguments().get(i)) {
                    toType = p.constructorInstance().formalTypes().get(i);
                    break;
                }
            }
        }
        
        if (parent instanceof ConstructorCall) {
            ConstructorCall p = (ConstructorCall) parent;
            for (int i = 0; i < p.arguments().size(); i++) {
                if (e == p.arguments().get(i)) {
                    toType = p.constructorInstance().formalTypes().get(i);
                    break;
                }
            }

        }
        
        if (parent instanceof ClosureCall) {
            ClosureCall p = (ClosureCall) parent;
            for (int i = 0; i < p.arguments().size(); i++) {
                if (e == p.arguments().get(i)) {
                    toType = p.closureInstance().formalTypes().get(i);
                    break;
                }
            }

        }
        
        if (parent instanceof LocalDecl) {
            LocalDecl p = (LocalDecl) parent;
            if (e == p.init()) {
                toType = p.localDef().asInstance().type();
            }
        }
        
        if (parent instanceof FieldDecl) {
            FieldDecl p = (FieldDecl) parent;
            if (e == p.init()) {
                toType = p.fieldDef().asInstance().type();
            }

        }
        
        if (parent instanceof Assign) {
            Assign p = (Assign) parent;
            if (p.operator() == Assign.ASSIGN) {
                if (e == p.right()) {
                    toType = p.leftType();
                }
            }
        }
        
        if (toType != null) {
            X10NodeFactory nf = (X10NodeFactory) this.nodeFactory();
            X10TypeSystem ts = (X10TypeSystem) this.typeSystem();
            Expr e1 = (Expr) n;
            Type fromType = e1.type();
            Type fromBase = X10TypeMixin.baseType(fromType);
            if (fromBase instanceof X10ClassType) {
                X10ClassType ct = (X10ClassType) fromBase;
                if (ct.isAnonymous()) {
                    if (ct.superClass() != null)
                        fromType = ct.superClass();
                    else if (ct.interfaces().size() > 0)
                        fromType = ct.interfaces().get(0);
                }

            }
            if (ts.isSubtype(fromType, toType, context) && !ts.typeBaseEquals(fromType, toType, context) && !ts.typeEquals(toType, ts.Object(), context)) {
                Expr e2 = nf.X10Cast(e1.position(), nf.CanonicalTypeNode(e1.position(), toType), e1, X10Cast.ConversionType.UNKNOWN_IMPLICIT_CONVERSION);
                e2 = (Expr) e2.del().disambiguate(this).typeCheck(this).checkConstants(this);
                return e2;
            }
        }

        return e;
    }
}
