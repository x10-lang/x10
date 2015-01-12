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
package x10c.visit;

import java.util.List;

import polyglot.ast.Assign;
import polyglot.ast.Call;
import polyglot.ast.ConstructorCall;
import polyglot.ast.Eval;
import polyglot.ast.Expr;
import polyglot.ast.FieldDecl;
import polyglot.ast.LocalDecl;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.NullLit;
import polyglot.ast.Receiver;
import polyglot.ast.Return;
import polyglot.ast.TypeNode;
import polyglot.frontend.Job;
import polyglot.types.CodeDef;
import polyglot.types.FunctionDef;
import polyglot.types.FunctionInstance;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.InternalCompilerError;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.ClosureCall;
import x10.ast.SettableAssign;
import x10.ast.X10Call;
import x10.emitter.Emitter;
import x10.types.MethodInstance;
import x10.types.ParameterType;
import x10.types.ParameterType.Variance;
import x10.types.X10ClassType;
import x10.types.X10ParsedClassType_c;
import x10.types.constraints.SubtypeConstraint;
import x10.types.constraints.TypeConstraint;
import x10.visit.X10PrettyPrinterVisitor;

// add cast node for java code generator
public class JavaCaster extends ContextVisitor {
    
    private final TypeSystem xts;
    private final NodeFactory xnf;

    public JavaCaster(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
        xts = (TypeSystem) ts;
        xnf = (NodeFactory) nf;
    }
    
    @Override
    protected Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException {
        n = typeConstraintsCast(parent, old, n);
        n = railAccessCast(parent, n);
        n = typeBoundsReturnCast(parent, old, n);
        n = covReturnCast(parent, n);
        n = stringReturnCast(parent, n);
        if (X10PrettyPrinterVisitor.useSelfDispatch) {
            n = typeParamCast(parent, n);
        }
        return n;
    }
    
    private Node typeBoundsReturnCast(Node parent, Node old, Node n) throws SemanticException {
        if (n instanceof Return) {
            Return return1 = (Return) n;
            Expr e = return1.expr();
            if (e == null) {
                return n;
            }
            if (!xts.isParameterType(e.type())) {
                return n;
            }
            CodeDef cd = context.currentCode();
            if (cd instanceof FunctionDef) {
                FunctionDef fd = (FunctionDef) cd;
                Type expectedReturnType = ((FunctionInstance<?>) fd.asInstance()).returnType();
                if (expectedReturnType.typeEquals(return1.expr().type(), context)) {
                    return n;
                }
                Type rt = Types.baseType(return1.expr().type());
                if (!expectedReturnType.typeEquals(rt, context)) {
                    return return1.expr(cast(return1.expr(), expectedReturnType));  
                }
            }
        }
        return n;
    }

    private Node stringReturnCast(Node parent, Node n) throws SemanticException {
        if (n instanceof Return) {
            Return return1 = (Return) n;
            if (return1.expr() == null) {
                return n;
            }
            CodeDef cd = context.currentCode();
            if (cd instanceof FunctionDef) {
                FunctionDef fd = (FunctionDef) cd;
                Type expectedReturnType = ((FunctionInstance<?>) fd.asInstance()).returnType();
                if (expectedReturnType.typeEquals(return1.expr().type(), context)) {
                    return n;
                }
                Type rt = Types.baseType(return1.expr().type());
                if (rt.typeEquals(xts.String(), context) && !expectedReturnType.typeEquals(xts.String(), context)) {
                    return return1.expr(cast(return1.expr(), expectedReturnType));  
                }
            }
        }
        return n;
    }
    
    private Node covReturnCast(Node parent, Node n) throws SemanticException {
        if (n instanceof Return) {
            Return return1 = (Return) n;
            if (return1.expr() == null) {
                return n;
            }
            CodeDef cd = context.currentCode();
            if (cd instanceof FunctionDef) {
                FunctionDef fd = (FunctionDef) cd;
                Type expectedReturnType = ((FunctionInstance<?>) fd.asInstance()).returnType();
                if (expectedReturnType.typeEquals(return1.expr().type(), context)) {
                    return n;
                }
                Type rt = Types.baseType(return1.expr().type());
                if (rt instanceof X10ClassType) {
                    X10ClassType ct = (X10ClassType) rt;
                    if (!ct.hasParams()) {
                        return n;
                    }
                    List<Variance> variances = ct.x10Def().variances();
                    for (Variance v : variances) {
                        if (v != ParameterType.Variance.INVARIANT) {
                            return return1.expr(cast(return1.expr(), expectedReturnType));
                        }
                    }
                }
            }
        }
        return n;
    }
    
    private Node typeParamCast(Node parent, Node n) throws SemanticException {
        if (n instanceof X10Call && !(parent instanceof Eval)) {
            X10Call call = (X10Call) n;
            Receiver target = call.target();
            MethodInstance mi = call.methodInstance();
            if (!(target instanceof TypeNode)) {
                Type bt = Types.baseType(target.type());
                X10ClassType ct = null;
                if (bt instanceof X10ClassType) {
                    ct = (X10ClassType) bt;
                } else if (xts.isParameterType(bt)) {
                    ct = (X10ClassType) Types.baseType(mi.container());
                }
                if (ct != null && (ct.typeArguments() != null && ct.typeArguments().size() > 0)) {
                    if (isDispatch(ct, mi)) {
                        return cast(call, call.type());
                    } else {
                        Type rt = mi.def().returnType().get();
                        if (!xts.isParameterType(rt) && Emitter.containsTypeParam(rt)) {
                            return cast(call, call.type());
                        }
                    }
                }
            }
        }
        if (n instanceof ClosureCall && !(parent instanceof Eval)) {
            ClosureCall call = (ClosureCall) n;
            Receiver target = call.target();
            MethodInstance mi = call.closureInstance();
            if (!(target instanceof TypeNode)) {
                Type bt = Types.baseType(target.type());
                if (bt instanceof X10ClassType) {
                    X10ClassType ct = (X10ClassType) bt;
                    if (ct.typeArguments() != null && ct.typeArguments().size() > 0) {
                        if (isDispatch(ct, mi)) {
                            return cast(call, call.type());
                        } else {
                            Type rt = mi.def().returnType().get();
                            if (!xts.isParameterType(rt) && Emitter.containsTypeParam(rt)) {
                                return cast(call, call.type());
                            }
                        }
                    }
                }
            }
        }
        return n;
    }

    private boolean isDispatch(X10ClassType bt, MethodInstance mi) {
        boolean isDispatch = false;
        if (bt.flags().isInterface() && !Emitter.isNativeRepedToJava(bt)) {
            List<Ref<? extends Type>> formalTypes = mi.def().formalTypes();
            for (Ref<? extends Type> ref : formalTypes) {
                Type type = ref.get();
                if (Emitter.containsTypeParam(type)) {
                    isDispatch = true;
                    break;
                }
            }
        }
        return isDispatch;
    }

    private Node railAccessCast(Node parent, Node n) throws SemanticException {
        if (n instanceof X10Call) {
            X10Call call = (X10Call) n;
            if (!xts.isParameterType(call.type())) {
                if (call.target() != null && isRail(call.target().type())) {
                    // e.g) val str = rail(0) = "str";
                    //   -> val str = (String)(rail(0) = "str");
                    if (!(parent instanceof Eval)) {
                        if (call.methodInstance().name() == SettableAssign.SET && !X10PrettyPrinterVisitor.isPrimitive(call.type())) {
                            return cast(call, call.type());
                        }
                    }
                }
            }
        }
        return n;
    }

    private boolean isRail(Type type) {
        Type tbase = Types.baseType(type);
        return tbase instanceof X10ParsedClassType_c && ((X10ParsedClassType_c) tbase).def().asType().typeEquals(xts.Rail(), context);
    }


    // add casts for type constraints to type parameters
    // e.g) class C[T1,T2]{T1 <: T2} { def test(t1:T1):T2 {return t1;}}
    //   -> class C[T1,T2]{T1 <: T2} { def test(t1:T1):T2 {return (T2) t1;}}
    private Node typeConstraintsCast(Node parent, Node old, Node n) throws SemanticException {
    	
    	if (!(old instanceof Expr)) {
    		return n;
    	}
    	Expr e = (Expr) old;
        
        if (!xts.isParameterType(e.type())) {
            return n;
        }

        Type superType = null;
        TypeConstraint ctc = context.currentTypeConstraint();
        List<SubtypeConstraint> terms = ctc.terms();
        for (SubtypeConstraint sc : terms) {
            if (sc.isHaszero() || sc.isIsRef()) continue;
            if (sc.subtype().typeEquals(Types.baseType(e.type()), context) && sc.subtype() instanceof ParameterType) {
                superType = sc.supertype();
            }
        }
        if (superType == null) {
            return n;
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
        Expr e = xnf.X10Cast(n.position(), xnf.CanonicalTypeNode(n.position(), Types.baseType(toType)), n);
        return (Expr) e.del().disambiguate(this).typeCheck(this).checkConstants(this);
    }
}
