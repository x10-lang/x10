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

import polyglot.ast.Block;
import polyglot.ast.Call;
import polyglot.ast.ClassDecl;
import polyglot.ast.ClassMember;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FieldAssign;
import polyglot.ast.FieldDecl;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Receiver;
import polyglot.ast.Special;
import polyglot.ast.TypeNode;
import polyglot.frontend.Job;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.MethodDef;
import polyglot.types.MethodInstance;
import polyglot.types.Name;
import polyglot.types.QName;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.TypeParamNode;
import x10.ast.X10Call;
import x10.ast.X10MethodDecl;
import x10.ast.X10NodeFactory;
import x10.types.ParameterType;
import x10.types.X10ClassType;
import x10.types.X10MethodDef;
import x10.types.X10TypeMixin;
import x10.types.X10TypeSystem;

public class InlineHelper extends ContextVisitor {

    private static final String BRIDGE_TO_PRIVATE_SUFFIX = "$P";
    private static final String BRIDGE_TO_SUPER_SUFFIX = "$S";

    private final X10TypeSystem xts;
    private final X10NodeFactory xnf;

    private Type InlineType;
    public static final QName INLINE_ANNOTATION = QName.make("x10.compiler.Inline");

    public InlineHelper(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
        xts = (X10TypeSystem) ts;
        xnf = (X10NodeFactory) nf;
    }

    @Override
    public NodeVisitor begin() {
        try {
            InlineType = (Type) ts.systemResolver().find(INLINE_ANNOTATION);
        }
        catch (SemanticException e) {
            System.out.println("Unable to find "+INLINE_ANNOTATION+": "+e);
            InlineType = null;
        }
        return super.begin();
    }

    @Override
    protected Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException {
        // change accessor from default and protected to public
        if (n instanceof FieldDecl) {
            FieldDecl f = (FieldDecl) n;
            if (!f.flags().flags().isPrivate()) {
                return f.flags(xnf.FlagsNode(f.position(), f.flags().flags().clearProtected().Public()));
            }
        }
        if (n instanceof MethodDecl) {
            MethodDecl m = (MethodDecl) n;
            if (!m.flags().flags().isPrivate()) {
                return m.flags(xnf.FlagsNode(m.position(), m.flags().flags().clearProtected().Public()));
            }
        }
        
        // caller side
        if (n instanceof Field) {
            // nothing to do?
            return n;
        }
        if (n instanceof FieldAssign) {
            // nothing to do?
            return n;
        }

        // generate bridge methods
        // FIXME avoid name collision
        Position pos = Position.COMPILER_GENERATED;
        if (n instanceof ClassDecl) {
            ClassDecl d = (ClassDecl) n;
            final ClassDef cd = d.classDef();
            if (prepareForInlining(cd)) {
                List<ClassMember> members = d.body().members();
                List<ClassMember> nmembers = new ArrayList<ClassMember>(members.size());
                
                final List<Call> supers = new ArrayList<Call>();
                for (ClassMember cm : members) {
                    if (cm instanceof FieldDecl) {
                        FieldDecl fdcl = (FieldDecl) cm;
                        if (fdcl.flags().flags().isPrivate()) {
                            fdcl = fdcl.flags(xnf.FlagsNode(pos, fdcl.flags().flags().clearPrivate().Public()));
                        }
                        nmembers.add(fdcl);
                    }
                    else if (cm instanceof X10MethodDecl) {
                        X10MethodDecl mdcl = (X10MethodDecl) cm;
                        MethodDef md = mdcl.methodDef();
                        if (md instanceof X10MethodDef) {
                            if (mdcl.body() != null && prepareForInlining((X10MethodDef) md)) {
                                mdcl.body().visit(new NodeVisitor() {
                                    @Override
                                    public Node leave(Node parent, Node old, Node n, NodeVisitor v) {
                                        if (n instanceof Call && ((Call)n).target() instanceof Special) {
                                            if (((Special)((Call)n).target()).kind().equals(Special.SUPER)) {
                                                Call call = (Call) n;
                                                boolean isContain = false;
                                                for (Call c : supers) {
                                                    if (c.methodInstance().signature().equals(call.methodInstance().signature())) {
                                                        isContain = true;
                                                    }
                                                }
                                                if (!isContain) {
                                                    supers.add(call);
                                                }
                                            }
                                        }
                                        if (parent instanceof Call && n instanceof Special) {
                                            if (((Special) n).kind().equals(Special.SUPER)) {
                                                Call call = (Call) parent;
                                                boolean isContain = false;
                                                for (Call c : supers) {
                                                    if (c.methodInstance().signature().equals(call.methodInstance().signature())) {
                                                        isContain = true;
                                                    }
                                                }
                                                if (!isContain) {
                                                    supers.add(call);
                                                }
                                            }
                                        }
                                        return n;
                                    }
                                });
                            }
                        }
                        
                        // generate bridge methods for private methods
                        nmembers.add(cm);
                        if (mdcl.flags().flags().isNative() || !mdcl.flags().flags().isPrivate()) {
                            continue;
                        }
                        List<Formal> formals = new ArrayList<Formal>(mdcl.formals());
                        ClassType t = cd.asType();
                        LocalDef ldef = xts.localDef(pos, Flags.FINAL, Types.ref(t), cd.name());
                        if (!mdcl.flags().flags().isStatic()) {
                            formals.add(xnf.Formal(pos, xnf.FlagsNode(pos, Flags.FINAL), xnf.X10CanonicalTypeNode(pos, t), xnf.Id(pos, cd.name())).localDef(ldef));
                        }
                        
                        // copy implement to the body of the static bridge method ?
                        
                        List<Expr> args = new ArrayList<Expr>();
                        for (Formal f : mdcl.formals()) {
                            args.add(xnf.Local(pos, f.name()).type(f.type().type()));
                        }
                        Expr call = xnf.Call(pos, xnf.Local(pos, xnf.Id(pos, cd.name())).type(cd.asType()), mdcl.name(), args).methodInstance(mdcl.methodDef().asInstance()).type(mdcl.returnType().type());
                        Block body;
                        if (mdcl.returnType().type().isVoid()) {
                            body = xnf.Block(pos, xnf.Eval(pos, call));
                        }
                        else {
                            body = xnf.Block(pos, xnf.Return(pos, call));
                        }
                        X10MethodDecl nmdcl = xnf.MethodDecl(pos, xnf.FlagsNode(pos, mdcl.flags().flags().clearPrivate().clearProtected().Public().Static()), mdcl.returnType(), xnf.Id(pos, Name.make(mdcl.name().toString() + BRIDGE_TO_PRIVATE_SUFFIX )), formals, mdcl.throwTypes(), body);
                        nmdcl = (X10MethodDecl) nmdcl.methodDef(mdcl.methodDef());
                        nmdcl = nmdcl.typeParameters(mdcl.typeParameters());
                        
                        // check
                        if (!mdcl.flags().flags().isStatic()) {
                            if (t instanceof X10ClassType) {
                                X10ClassType t2 = (X10ClassType) t;
                                if (t2.typeArguments().size() > 0) {
                                    List<TypeParamNode> ts = new ArrayList<TypeParamNode>(mdcl.typeParameters());
                                    for (Type t3 : t2.typeArguments()) {
                                        if (t3 instanceof ParameterType) {
                                            ParameterType pt = (ParameterType) t3;
                                            ts.add(xnf.TypeParamNode(pos, xnf.Id(pos, pt.name())).type(pt));
                                        }
                                    }
                                    nmdcl = nmdcl.typeParameters(ts);
                                }
                            }
                        }
                        nmembers.add(nmdcl);
                    }
                    else {
                        nmembers.add(cm);
                    }
                    
                }
                // generate bridge methods for super call
                for (Call call : supers) {
                    MethodInstance mi = call.methodInstance();
                    List<TypeNode> throwTypes = new ArrayList<TypeNode>();
                    List<Formal> formals = new ArrayList<Formal>();
                    for (Type t : mi.throwTypes()) {
                        throwTypes.add(xnf.X10CanonicalTypeNode(pos, t));
                    }
                    List<Expr> arguments = new ArrayList<Expr>(call.arguments());
                    for (int i = 0; i < mi.formalTypes().size() ; ++i ) {
                        Type t = mi.formalTypes().get(i);
                        Name name = Name.make("a" + i);
                        LocalDef ldef = xts.localDef(pos, Flags.FINAL, Types.ref(t), name);
                        Id id = xnf.Id(pos, name);
                        Formal formal = xnf.Formal(pos, xnf.FlagsNode(pos, Flags.FINAL), xnf.X10CanonicalTypeNode(pos, t), id);
                        formals.add(formal.localDef(ldef));
                        arguments.set(i, xnf.Local(pos, id).type(t));
                    }
                    call = (Call) call.arguments(arguments);
                    Block body;
                    if (call.methodInstance().returnType().isVoid()) {
                        body = xnf.Block(pos, xnf.Eval(pos, call));
                    }
                    else {
                        body = xnf.Block(pos, xnf.Return(pos, call));
                    }
                    MethodDecl mdcl1 = xnf.MethodDecl(pos, xnf.FlagsNode(pos, Flags.FINAL.Public()), xnf.X10CanonicalTypeNode(pos, mi.returnType()), xnf.Id(pos, Name.make(d.classDef().asType().fullName().toString().replace(".", "$") + "$" + call.name().toString() + BRIDGE_TO_SUPER_SUFFIX)), formals, throwTypes, body);
                    mdcl1 = mdcl1.methodDef(mi.def());
                    nmembers.add(mdcl1);
                }
                d = d.body(d.body().members(nmembers));
                // change class public to make it visible at any call site
                d = d.flags(xnf.FlagsNode(d.position(), d.flags().flags().clearProtected().clearPrivate().Public()));
                return d.classDef(cd);
            }            
        }

        // caller side
        if (n instanceof X10Call) {
            X10Call call = (X10Call) n;
            MethodInstance mi = call.methodInstance();
            Receiver target = call.target();
            if (mi.flags().isPrivate()) {
                if (!X10TypeMixin.baseType(target.type()).typeEquals(context.currentClass(), context)) {
                    // C.m(a,b) --> C.xxx.yyy.C.m$P(a,b);
                    if (mi.flags().isStatic()) { 
                        return (X10Call) xnf.Call(pos, target, xnf.Id(pos, call.name().toString() + BRIDGE_TO_PRIVATE_SUFFIX), call.arguments()).methodInstance(mi).type(mi.returnType());
                    }
                    // c.m(a,b) --> xxx.yyy.C.m$P(a,b,c); (m is private)
                    else {
                        List<Expr> arguments = new ArrayList<Expr>(call.arguments());
                        arguments.add((Expr) target); 
                        List<Type> formals = new ArrayList<Type>(mi.formalTypes());
                        Type t = target.type();
                        formals.add(t);
                        return (X10Call) xnf.Call(pos, xnf.CanonicalTypeNode(pos, target.type()), xnf.Id(pos, call.name().toString() + BRIDGE_TO_PRIVATE_SUFFIX), arguments).methodInstance(mi.formalTypes(formals)).type(mi.returnType());
                    }
                }
            }
            // c.super.m() --> c.xxx$yyy$C$m$S();
            // TODO unimplemented
            return n;
        }
        return n;
    }

    private boolean prepareForInlining(ClassDef cd) {
        for (MethodDef md : cd.methods()) {
            if (md instanceof X10MethodDef) {
                if (prepareForInlining((X10MethodDef) md)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean prepareForInlining(X10MethodDef xmd) {
        return true;
//        return !xmd.annotationsMatching(InlineType).isEmpty();
    }

}
