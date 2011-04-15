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
import java.util.Collections;
import java.util.List;

import polyglot.ast.Block;
import polyglot.ast.Call;
import polyglot.ast.ClassDecl;
import polyglot.ast.ClassMember;
import polyglot.ast.ConstructorDecl;
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
import polyglot.frontend.Job;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.Context;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.MethodDef;
import polyglot.types.Name;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.ContainerType;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.X10CompilerOptions;
import x10.ast.TypeParamNode;
import x10.ast.X10Call;
import x10.ast.X10MethodDecl;
import x10.emitter.Emitter;
import x10.types.ParameterType;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.types.X10MethodDef;
import x10.types.MethodInstance;
import x10.types.X10ParsedClassType;
import polyglot.types.TypeSystem;

public class InlineHelper extends ContextVisitor {

    private static final String BRIDGE_TO_PRIVATE_SUFFIX = "$P";
    private static final String BRIDGE_TO_SUPER_SUFFIX = "$S";

    private final TypeSystem xts;
    private final NodeFactory xnf;

    private Type InlineType;
    public static final QName INLINE_ANNOTATION = QName.make("x10.compiler.Inline");

    public InlineHelper(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
        xts = (TypeSystem) ts;
        xnf = (NodeFactory) nf;
    }

    @Override
    public NodeVisitor begin() {
        try {
            InlineType = ts.systemResolver().findOne(INLINE_ANNOTATION);
        }
        catch (SemanticException e) {
            System.out.println("Unable to find "+INLINE_ANNOTATION+": "+e);
            InlineType = null;
        }
        return super.begin();
    }

    @Override
    protected NodeVisitor enterCall(Node parent, Node n) throws SemanticException {
        return super.enterCall(parent, n);
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
        if (n instanceof ConstructorDecl) {
            ConstructorDecl c = (ConstructorDecl) n;
            if (!c.flags().flags().isPrivate()) {
                return c.flags(xnf.FlagsNode(c.position(), c.flags().flags().clearProtected().Public()));
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
                                                if (!containsMethod(supers, call)) {
                                                    supers.add(call);
                                                }
                                            }
                                        }
                                        if (parent instanceof Call && n instanceof Special) {
                                            if (((Special) n).kind().equals(Special.SUPER)) {
                                                Call call = (Call) parent;
                                                if (!containsMethod(supers, call)) {
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
                        if (!mdcl.flags().flags().isPrivate()) {
                            continue;
                        }
                        List<Formal> formals = new ArrayList<Formal>(mdcl.formals());
                        Type ct = cd.asType();
                        ct = Types.instantiateTypeParametersExplicitly(ct);
                        LocalDef ldef = xts.localDef(pos, Flags.FINAL, Types.ref(ct), cd.name());
                        if (!mdcl.flags().flags().isStatic()) {
                            formals.add(xnf.Formal(pos, xnf.FlagsNode(pos, Flags.FINAL), xnf.X10CanonicalTypeNode(pos, ct), xnf.Id(pos, cd.name())).localDef(ldef));
                        }
                        
                        // copy implement to the body of the static bridge method ?
                        
                        List<Expr> args = new ArrayList<Expr>();
                        for (Formal f : mdcl.formals()) {
                            args.add(xnf.Local(pos, f.name()).localInstance(f.localDef().asInstance()).type(f.type().type()));
                        }
                        
                        List<Ref<? extends Type>> argTypes = new ArrayList<Ref<? extends Type>>(mdcl.formals().size() + 1);
                        for (Formal f : mdcl.formals()) {
                            Type t = f.type().type();
                            argTypes.add(f.type().typeRef());
                        }
                        if (!mdcl.flags().flags().isStatic()) {
                            argTypes.add(Types.ref(ct));
                        }
                        
                        Expr call;
                        if (mdcl.flags().flags().isStatic()) {
                            call = xnf.Call(pos, xnf.CanonicalTypeNode(pos, cd.asType()), 
                            		mdcl.name(), args).methodInstance(mdcl.methodDef().asInstance()).type(mdcl.returnType().type());
                        } else {
                            call = xnf.Call(pos, xnf.Local(pos, xnf.Id(pos, cd.name())).localInstance(ldef.asInstance()).type(ct), mdcl.name(), args).methodInstance(mdcl.methodDef().asInstance()).type(mdcl.returnType().type());
                        }
                        
                        Block body;
                        if (mdcl.returnType().type().isVoid()) {
                            body = xnf.Block(pos, xnf.Eval(pos, call));
                        }
                        else {
                            body = xnf.Block(pos, xnf.Return(pos, call));
                        }
                        X10MethodDecl nmdcl = xnf.MethodDecl(pos, xnf.FlagsNode(pos, mdcl.flags().flags().clearPrivate().clearProtected().clearNative().Public().Static()), mdcl.returnType(), xnf.Id(pos, makePrivateBridgeName(mdcl.name())), formals,  body);
                        X10MethodDef nmd = (X10MethodDef) xts.methodDef(pos, Types.ref(cd.asType()), nmdcl.flags().flags(), Types.ref(nmdcl.returnType().type()), nmdcl.name().id(), argTypes);

                        // check
                        List<ParameterType> rts = new ArrayList<ParameterType>();
                        List<TypeParamNode> ts = new ArrayList<TypeParamNode>(mdcl.typeParameters());
                        if (md instanceof X10MethodDef) {
                            rts.addAll(((X10MethodDef) md).typeParameters());
                        }
                        if (!mdcl.flags().flags().isStatic()) {
                            X10ClassDef d2 = (X10ClassDef) cd;
                            for (ParameterType pt : d2.typeParameters()) {
                                ts.add(xnf.TypeParamNode(pos, xnf.Id(pos, pt.name())).type(pt));
                                rts.add(pt);
                            }
                        }
                        nmdcl = nmdcl.typeParameters(ts);
                        nmd.setTypeParameters(rts);
                        nmdcl = (X10MethodDecl) nmdcl.methodDef(nmd);
                        cd.addMethod(nmd);
                        nmembers.add(nmdcl);
                    }
                    else {
                        nmembers.add(cm);
                    }
                    
                }
                // generate bridge methods for super call
                for (Call call : supers) {
                    MethodInstance mi = call.methodInstance();
                   
                    List<Formal> formals = new ArrayList<Formal>();
                  
                    List<Expr> arguments = new ArrayList<Expr>(call.arguments());
                    for (int i = 0; i < mi.formalTypes().size() ; ++i ) {
                        Type t = mi.formalTypes().get(i);
                        Name name = Name.make("a" + i);
                        LocalDef ldef = xts.localDef(pos, Flags.FINAL, Types.ref(t), name);
                        Id id = xnf.Id(pos, name);
                        Formal formal = xnf.Formal(pos, xnf.FlagsNode(pos, Flags.FINAL), xnf.X10CanonicalTypeNode(pos, t), id);
                        formals.add(formal.localDef(ldef));
                        arguments.set(i, xnf.Local(pos, id).localInstance(ldef.asInstance()).type(t));
                    }
                    call = (Call) call.arguments(arguments);
                    Block body;
                    if (call.methodInstance().returnType().isVoid()) {
                        body = xnf.Block(pos, xnf.Eval(pos, call));
                    }
                    else {
                        body = xnf.Block(pos, xnf.Return(pos, call));
                    }
                    MethodDecl mdcl1 = xnf.MethodDecl(pos, xnf.FlagsNode(pos, Flags.FINAL.Public()), 
                    		xnf.X10CanonicalTypeNode(pos, mi.returnType()), 
                    		xnf.Id(pos, makeSuperBridgeName(cd, call.name())), 
                    		formals,  body);
                    
                    List<Ref<? extends Type>> argTypes = new ArrayList<Ref<? extends Type>>(mdcl1.formals().size() + 1);
                    for (Formal f : mdcl1.formals()) {
                        Type t = f.type().type();
                        argTypes.add(f.type().typeRef());
                    }
                    
                    mdcl1 = mdcl1.methodDef(xts.methodDef(pos, Types.ref(cd.asType()), mdcl1.flags().flags(), Types.ref(mdcl1.returnType().type()), mdcl1.name().id(), argTypes));
                    nmembers.add(mdcl1);
                }
                d = d.body(d.body().members(nmembers));
                d = d.classDef(cd);
            }
            // change class (including interface) public to make it visible at any call site
            d = d.flags(xnf.FlagsNode(d.position(), d.flags().flags().clearProtected().clearPrivate().Public()));
            return d;
        }
        // caller side
        X10CompilerOptions opts = (X10CompilerOptions) job.extensionInfo().getOptions();
        if (n instanceof X10Call && opts.x10_config.INLINE_METHODS) {
            X10Call call = (X10Call) n;
            Receiver target = call.target();
            MethodInstance mi = call.methodInstance();
            if (mi.flags().isPrivate() && !isSameDef(mi.container(), context.currentClass(),context) && !isSameTopLevel(mi.container(), context.currentClass(), context)) {
                Id id = xnf.Id(pos, makePrivateBridgeName(call.name()));
                List<Type> typeArgs;
                if (mi instanceof MethodInstance) {
                    typeArgs = ((MethodInstance) mi).typeParameters();
                } else {
                    typeArgs = Collections.<Type>emptyList();
                }
                List<Expr> arguments = new ArrayList<Expr>(call.arguments());
                List<Type> formals = new ArrayList<Type>(mi.formalTypes());
                if (!mi.flags().isStatic()) {
                    arguments.add((Expr) target); 
                    Type type = Types.baseType(target.type());
                    if (type instanceof X10ClassType) {
                        formals.add(Types.baseType(((X10ClassType) type).def().asType()));
                    }
                    else {
                        formals.add(Types.baseType(target.type()));
                    }
                }
                ContainerType container = mi.container();
                X10MethodDef md = (X10MethodDef) xts.methodDef(pos, Types.ref(container), mi.flags().clearNative().clearPrivate().Static(), 
                                                               Types.ref(mi.returnType()), id.id(), getRefList(formals));
                List<ParameterType> rts = new ArrayList<ParameterType>();
                if (md instanceof X10MethodDef) {
                    rts.addAll(((X10MethodDef) mi.def()).typeParameters());
                }
                if (!mi.flags().isStatic()) {
                    if (container instanceof X10ClassType) {
                        X10ClassType t2 = (X10ClassType) container;
                        if (t2.typeArguments() != null && t2.typeArguments().size() > 0) {
                            for (Type t3 : t2.typeArguments()) {
                                if (t3 instanceof ParameterType) {
                                    ParameterType pt = (ParameterType) t3;
                                    rts.add(pt);
                                }
                            }
                        }
                    }
                }
                md.setTypeParameters(rts);

                MethodInstance nmi = (MethodInstance) md.asInstance();
                Type tt = Types.baseType(target.type());
                List<Type> tas = new ArrayList<Type>();
                if (mi instanceof MethodInstance) {
                    tas.addAll(((MethodInstance) mi).typeParameters());
                }
                if (!mi.flags().isStatic() && tt instanceof X10ParsedClassType) {
                    tas.addAll(((X10ParsedClassType) tt).x10Def().typeParameters());
                }
                nmi = (MethodInstance) nmi.typeParameters(tas);

                if (mi.flags().isStatic()) {
                    return (X10Call) xnf.Call(pos, target, id, call.arguments()).methodInstance(nmi).type(nmi.returnType());
                } else {
                    return (X10Call) xnf.Call(pos, xnf.CanonicalTypeNode(pos, target.type()), id, arguments).methodInstance(nmi).type(nmi.returnType());
                }
            }
            // c.super.m() --> c.xxx$yyy$C$m$S();
            // TODO unimplemented
            return n;
        }
        return n;
    }

    private Name makeSuperBridgeName(final ClassDef cd, Id name) {
        return Name.make(Emitter.mangleAndFlattenQName(cd.asType().fullName()) + "$" + Emitter.mangleToJava(name.id()) + BRIDGE_TO_SUPER_SUFFIX);
    }

    private Name makePrivateBridgeName(Id name) {
        return Name.make(Emitter.mangleToJava(name.id()) + BRIDGE_TO_PRIVATE_SUFFIX);
    }

    private static boolean isSameDef(Type t1, Type t2, Context context) {
        if (t1.typeEquals(t2, context)) {
            return true;
        }
        else if (t1 instanceof ClassType && t2 instanceof ClassType && ((ClassType)t1).def().equals(((ClassType)t2).def())) {
            return true;
        }
        return false;
    }
    
    private static boolean isSameTopLevel(Type t1, Type t2, Context context) {
        if (t1 instanceof X10ClassType && t2 instanceof X10ClassType) {
            t1 = Types.baseType(t1);
            t2 = Types.baseType(t2);
            return isSameDef(getTopLevel((X10ClassType) t1), getTopLevel((X10ClassType) t2), context);
        }
        return false;
    }
    
    private static Type getTopLevel(X10ClassType t) {
        X10ClassType outer = (X10ClassType) t.outer();
        if (outer == null) {
            return t;
        }
        return getTopLevel(outer);
    }

    private static List<Ref<? extends Type>> getRefList(List<Type> types) {
        List<Ref<? extends Type>> refs = new ArrayList<Ref<? extends Type>>();
        for (Type type : types) {
            refs.add(Types.ref(type));
        }
        return refs;
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

    private boolean containsMethod(final List<Call> calls, Call call) {
        for (Call c : calls) {
            if (c.methodInstance().isSameMethod(call.methodInstance(), context)) {
                return true;
            }
        }
        return false;
    }

}
