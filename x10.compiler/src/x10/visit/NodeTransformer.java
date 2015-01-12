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

package x10.visit;

import polyglot.ast.Call;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FieldAssign;
import polyglot.ast.Local;
import polyglot.ast.LocalDecl;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.Special;
import polyglot.ast.Stmt;
import polyglot.ast.TypeNode;
import polyglot.visit.ContextVisitor;
import x10.ast.AssignPropertyCall;
import x10.ast.Async;
import x10.ast.AtEach;
import x10.ast.AtStmt;
import x10.ast.Closure;
import x10.ast.ClosureCall;
import x10.ast.SettableAssign;
import x10.ast.TypeParamNode;
import x10.ast.X10ClassDecl;
import x10.ast.X10ConstructorCall;
import x10.ast.X10ConstructorDecl;
import x10.ast.X10FieldDecl;
import x10.ast.X10Formal;
import x10.ast.X10MethodDecl;

/**
 * Defines a transformation for each supported type of node.
 * Currently, the only supported nodes are those that carry
 * type information.
 */
public abstract class NodeTransformer {
    private ContextVisitor v; // FIXME: refactor later
    protected ContextVisitor visitor() { return v; }

    public Node transform(Node n, Node old, ContextVisitor v) {
        this.v = v;
        if (n instanceof TypeParamNode) {
            return transform((TypeParamNode) n, (TypeParamNode) old);
        } else if (n instanceof TypeNode) {
            return transform((TypeNode) n, (TypeNode) old);
        } else if (n instanceof X10Formal) {
            return transform((X10Formal) n, (X10Formal) old);
        } else if (n instanceof X10ClassDecl) {
            return transform((X10ClassDecl) n, (X10ClassDecl) old);
        } else if (n instanceof X10FieldDecl) {
            return transform((X10FieldDecl) n, (X10FieldDecl) old);
        } else if (n instanceof X10ConstructorDecl) {
            return transform((X10ConstructorDecl) n, (X10ConstructorDecl) old);
        } else if (n instanceof X10MethodDecl) {
            return transform((X10MethodDecl) n, (X10MethodDecl) old);
        } else if (n instanceof Expr) {
            return transformExpr((Expr) n, (Expr) old);
        } else if (n instanceof Stmt) {
            return transformStmt((Stmt) n, (Stmt) old);
        }
        return transformNode(n, old);
    }

    protected Node transformNode(Node n, Node old) {
        return n;
    }

    protected TypeParamNode transform(TypeParamNode pn, TypeParamNode old) {
        return (TypeParamNode) transformNode((Node) pn, (Node) old);
    }

    protected TypeNode transform(TypeNode tn, TypeNode old) {
        return (TypeNode) transformNode((Node) tn, (Node) old);
    }

    protected Expr transformExpr(Expr e, Expr old) {
        if (e instanceof Local) {
            Local o = old instanceof Local ? (Local) old : null;
            return transform((Local) e, o);
        } else if (e instanceof Field) {
            Field o = old instanceof Field ? (Field) old : null;
            return transform((Field) e, o);
        } else if (e instanceof Call) {
            Call o = old instanceof Call ? (Call) old : null;
            return transform((Call) e, o);
        } else if (e instanceof New) {
            New o = old instanceof New ? (New) old : null;
            return transform((New) e, o);
        } else if (e instanceof ClosureCall) {
            ClosureCall o = old instanceof ClosureCall ? (ClosureCall) old : null;
            return transform((ClosureCall) e, o);
        } else if (e instanceof SettableAssign) {
            SettableAssign o = old instanceof SettableAssign ? (SettableAssign) old : null;
            return transform((SettableAssign) e, o);
        } else if (e instanceof FieldAssign) {
            FieldAssign o = old instanceof FieldAssign ? (FieldAssign) old : null;
            return transform((FieldAssign) e, o);
        } else if (e instanceof Closure) {
            Closure o = old instanceof Closure ? (Closure) old : null;
            return transform((Closure) e, o);
        } else if (e instanceof Special) {
            Special o = old instanceof Special ? (Special) old : null;
            return transform((Special) e, o);
        }
        return transform(e, old);
    }

    protected Expr transform(Expr e, Expr old) {
        return (Expr) transformNode((Node) e, (Node) old);
    }

    protected Local transform(Local l, Local old) {
        return (Local) transform((Expr) l, (Expr) old);
    }
    protected Field transform(Field f, Field old) {
        return (Field) transform((Expr) f, (Expr) old);
    }

    protected Call transform(Call c, Call old) {
        return (Call) transform((Expr) c, (Expr) old);
    }

    protected New transform(New w, New old) {
        return (New) transform((Expr) w, (Expr) old);
    }

    protected ClosureCall transform(ClosureCall c, ClosureCall old) {
        return (ClosureCall) transform((Expr) c, (Expr) old);
    }

    protected SettableAssign transform(SettableAssign a, SettableAssign old) {
        return (SettableAssign) transform((Expr) a, (Expr) old);
    }

    protected FieldAssign transform(FieldAssign f, FieldAssign old) {
        return (FieldAssign) transform((Expr) f, (Expr) old);
    }

    protected Closure transform(Closure d, Closure old) {
        return (Closure) transform((Expr) d, (Expr) old);
    }

    protected Special transform(Special s, Special old) {
        return (Special) transform((Expr) s, (Expr) old);
    }

    protected Stmt transformStmt(Stmt s, Stmt old) {
        if (s instanceof X10ConstructorCall) {
            return transform((X10ConstructorCall) s, (X10ConstructorCall) old);
        } else if (s instanceof AssignPropertyCall) {
            return transform((AssignPropertyCall) s, (AssignPropertyCall) old);
        } else if (s instanceof LocalDecl) {
            return transform((LocalDecl) s, (LocalDecl) old);
        } else if (s instanceof Async) {
            return transform((Async) s, (Async) old);
        } else if (s instanceof AtStmt) {
            return transform((AtStmt) s, (AtStmt) old);
        } else if (s instanceof AtEach) {
            return transform((AtEach) s, (AtEach) old);
        }
        return transform(s, old);
    }

    protected Stmt transform(Stmt s, Stmt old) {
        return (Stmt) transformNode((Node) s, (Node) old);
    }

    protected X10ConstructorCall transform(X10ConstructorCall c, X10ConstructorCall old) {
        return (X10ConstructorCall) transform((Stmt) c, (Stmt) old);
    }

    protected AssignPropertyCall transform(AssignPropertyCall p, AssignPropertyCall old) {
        return (AssignPropertyCall) transform((Stmt) p, (Stmt) old);
    }

    protected LocalDecl transform(LocalDecl d, LocalDecl old) {
        return (LocalDecl) transform((Stmt) d, (Stmt) old);
    }

    protected X10Formal transform(X10Formal f, X10Formal old) {
        return (X10Formal) transformNode((Node) f, (Node) old);
    }

    protected Async transform(Async c, Async old) {
        return (Async) transform((Stmt) c, (Stmt) old);
    }

    protected AtStmt transform(AtStmt c, AtStmt old) {
        return (AtStmt) transform((Stmt) c, (Stmt) old);
    }

    protected AtEach transform(AtEach c, AtEach old) {
        return (AtEach) transform((Stmt) c, (Stmt) old);
    }
    
    protected X10ClassDecl transform(X10ClassDecl d, X10ClassDecl old) {
        return (X10ClassDecl) transformNode((Node) d, (Node) old);
    }

    protected X10FieldDecl transform(X10FieldDecl d, X10FieldDecl old) {
        return (X10FieldDecl) transformNode((Node) d, (Node) old);
    }

    protected X10ConstructorDecl transform(X10ConstructorDecl d, X10ConstructorDecl old) {
        return (X10ConstructorDecl) transformNode((Node) d, (Node) old);
    }

    protected X10MethodDecl transform(X10MethodDecl d, X10MethodDecl old) {
        return (X10MethodDecl) transformNode((Node) d, (Node) old);
    }
}
