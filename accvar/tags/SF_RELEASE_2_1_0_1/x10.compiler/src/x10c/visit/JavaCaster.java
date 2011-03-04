/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */
package x10c.visit;

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.Assign;
import polyglot.ast.Call;
import polyglot.ast.ConstructorCall;
import polyglot.ast.Eval;
import polyglot.ast.Expr;
import polyglot.ast.FieldDecl;
import polyglot.ast.LocalDecl;
import polyglot.ast.MethodDecl;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.NullLit;
import polyglot.ast.Receiver;
import polyglot.ast.Stmt;
import polyglot.ast.TypeNode;
import polyglot.frontend.Job;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.InternalCompilerError;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.ClosureCall;
import x10.ast.X10Call;
import x10.ast.X10NodeFactory;
import x10.ast.X10Return_c;
import x10.emitter.Emitter;
import x10.types.ParameterType;
import x10.types.X10ClassType;
import x10.types.X10ParsedClassType_c;
import x10.types.X10TypeMixin;
import x10.types.X10TypeSystem;
import x10.types.constraints.SubtypeConstraint;
import x10.visit.X10PrettyPrinterVisitor;

// add cast node for java code generator
public class JavaCaster extends ContextVisitor {
    
    private final X10TypeSystem xts;
    private final X10NodeFactory xnf;

    private Type imc;

    public JavaCaster(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
        xts = (X10TypeSystem) ts;
        xnf = (X10NodeFactory) nf;
    }
    
    @Override
    public NodeVisitor begin() {
        try {
            imc = xts.typeForName(QName.make("x10.util.IndexedMemoryChunk"));
        } catch (SemanticException e1) {
            throw new InternalCompilerError("Something is terribly wrong");
        }
        return super.begin();
    }
    
    @Override
    protected Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException {
        n = typeConstraintsCast(parent, old, n);
        n = railAccessCast(parent, n);
        if (X10PrettyPrinterVisitor.isSelfDispatch) {
            n = typeParamCast(parent, n);
        }
        return n;
    }

    private Node typeParamCast(Node parent, Node n) throws SemanticException {
        if (n instanceof X10Call && !(parent instanceof Eval)) {
            X10Call call = (X10Call) n;
            Receiver target = call.target();
            if (!(target instanceof TypeNode) && !xts.isRail(call.target().type())) {
                Type bt = X10TypeMixin.baseType(target.type());
                if (bt instanceof X10ClassType) {
                    if (((X10ClassType) bt).typeArguments().size() > 0) {
                        boolean isDispatch = false;
                        if (((X10ClassType) bt).flags().isInterface()) {
                            List<Ref<? extends Type>> formalTypes = call.methodInstance().def().formalTypes();
                            for (Ref<? extends Type> ref : formalTypes) {
                                Type type = ref.get();
                                if (Emitter.containsTypeParam(type)) {
                                    isDispatch = true;
                                    break;
                                }
                            }
                        }
                        if (isDispatch) {
                            return cast(call, call.type());
                        }
                        else if (Emitter.containsTypeParam(call.methodInstance().def().returnType().get())) {
                            return cast(call, call.type());
                        }
                    }
                }
            }
        }
        return n;
    }

    private Node railAccessCast(Node parent, Node n) throws SemanticException {
        if (n instanceof X10Call) {
            X10Call call = (X10Call) n;
            if (!(X10TypeMixin.baseType(call.type()) instanceof ParameterType)) {
                Type tbase = X10TypeMixin.baseType(call.target().type());
                if (call.target() != null && (xts.isRail(call.target().type()) || tbase instanceof X10ParsedClassType_c && ((X10ParsedClassType_c) tbase).def().asType().typeEquals(imc, context))) {
                    // e.g) val str = rail(0) = "str";
                    //   -> val str = (String)(rail(0) = "str");
                    if (!(parent instanceof Eval)) {
                        if (call.methodInstance().name().toString().equals("set") && !(call.type().isBoolean() || call.type().isNumeric() || call.type().isChar())
                        ) {
                            return cast(call, call.type());
                        }
                    }
                }
            }
        }
        return n;
    }

    // add casts for type constraints to type parameters
    // e.g) class C[T1,T2]{T1 <: T2} { def test(t1:T1):T2 {return t1;}}
    //   -> class C[T1,T2]{T1 <: T2} { def test(t1:T1):T2 {return (T2) t1;}}
    private Node typeConstraintsCast(Node parent, Node old, Node n) throws SemanticException {
        Expr e = null;
        if (n instanceof MethodDecl) {
            MethodDecl md = (MethodDecl) n;
            if (md.body() == null) {
                return n;
            }
            List<Stmt> stmts = md.body().statements();
            for (Stmt stmt : stmts) {
                if (stmt instanceof X10Return_c) {
                    X10Return_c r = (X10Return_c) stmt;
                    if (r.expr() != null && r.expr().type() != md.returnType().type()) {
                        e = r.expr();
                    }
                }
            }
        }
        
        if (e == null) {
            if (!(old instanceof Expr)) {
                return n;
            }
            e = (Expr) old;
        }
        
        if (!(X10TypeMixin.baseType(e.type()) instanceof ParameterType)) {
            return n;
        }

        Type superType = null;
        if (context.currentClass() instanceof X10ClassType) {
            List<SubtypeConstraint> terms = ((X10ClassType) context.currentClass()).x10Def().typeBounds().get().terms();
            for (SubtypeConstraint sc : terms) {
                if (sc.subtype().typeEquals(X10TypeMixin.baseType(e.type()), context) && sc.subtype() instanceof ParameterType) {
                    superType = sc.supertype();
                }
            }
        }
        if (superType == null) {
            return n;
        }

        if (n instanceof MethodDecl) {
            MethodDecl md = (MethodDecl) n;
            List<Stmt> stmts = md.body().statements();
            List<Stmt> newstmts = new ArrayList<Stmt>();
            for (Stmt stmt : stmts) {
                if (stmt instanceof X10Return_c) {
                    X10Return_c rt = (X10Return_c) stmt;
                    Expr expr = rt.expr();
                    if (expr.type() != md.returnType().type()) {
                        newstmts.add(rt.expr(cast(expr, md.returnType().type())));
                        return md.body(md.body().statements(newstmts));
                    }
                    return n;
                }
                newstmts.add(stmt);
            }
        }
        
        if (n instanceof NullLit) {
            return n;
        }

        Type toType = null;
        if (parent instanceof Call) {
            Call p = (Call) parent;
            for (int i = 0; i < p.arguments().size(); i++) {
                if (e == p.arguments().get(i)) {
                    if (p.methodInstance().formalTypes().get(i) == superType) {
                        toType = superType;
                        break;
                    }
                }
            }
        }
        else if (parent instanceof New) {
            New p = (New) parent;
            for (int i = 0; i < p.arguments().size(); i++) {
                if (e == p.arguments().get(i)) {
                    if (p.constructorInstance().formalTypes().get(i) == superType) {
                        toType = superType;
                        break;
                    }
                    break;
                }
            }
        }
        else if (parent instanceof ConstructorCall) {
            ConstructorCall p = (ConstructorCall) parent;
            for (int i = 0; i < p.arguments().size(); i++) {
                if (e == p.arguments().get(i)) {
                    if (p.constructorInstance().formalTypes().get(i) == superType) {
                        toType = superType;
                        break;
                    }
                }
            }
        }
        else if (parent instanceof ClosureCall) {
            ClosureCall p = (ClosureCall) parent;
            for (int i = 0; i < p.arguments().size(); i++) {
                if (e == p.arguments().get(i)) {
                    if (p.closureInstance().formalTypes().get(i) == superType) {
                        toType = superType;
                        break;
                    }
                }
            }

        }
        else if (parent instanceof LocalDecl) {
            LocalDecl p = (LocalDecl) parent;
            if (e == p.init()) {
                if (p.localDef().asInstance().type() == superType) {
                    toType = superType;
                }
            }
        }
        else if (parent instanceof FieldDecl) {
            FieldDecl p = (FieldDecl) parent;
            if (e == p.init()) {
                if (p.fieldDef().asInstance().type() == superType) {
                    toType = superType;
                }
            }

        }
        else if (parent instanceof Assign) {
            Assign p = (Assign) parent;
            if (p.operator() == Assign.ASSIGN) {
                if (e == p.right()) {
                    if (p.leftType() == superType) {
                        toType = superType;
                    }
                }
            }
        }

        if (toType != null) {
            return cast((Expr) n, toType);
        }
        return n;
    }

    private Expr cast(Expr n, Type toType) throws SemanticException {
        Expr e = xnf.X10Cast(n.position(), xnf.CanonicalTypeNode(n.position(), toType), n);
        return (Expr) e.del().disambiguate(this).typeCheck(this).checkConstants(this);
    }
}
