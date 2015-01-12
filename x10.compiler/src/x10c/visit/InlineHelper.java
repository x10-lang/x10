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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
import x10.ast.X10CanonicalTypeNode;
import x10.ast.X10ConstructorDecl;
import x10.ast.X10MethodDecl;
import x10.emitter.Emitter;
import x10.extension.X10Ext_c;
import x10.types.ParameterType;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.types.X10MethodDef;
import x10.types.MethodInstance;
import x10.types.X10ParsedClassType;
import x10.util.CollectionFactory;
import polyglot.types.TypeSystem;

/**
 * Generate private and super call bridge methods.
 * For every private method m(a:A) in class C, generate a public static method m$P(t:C, a:A) that calls t.m(a).
 * For every call to super.f() in class C, if f() is defined in a superclass A, generate a virtual public bridge
 * method C.f$A$S().  Note that some class B (A <: B, B <: C) may also define a B.f$A$S() (because one of its
 * methods also calls super.f()).
 */
public class InlineHelper extends ContextVisitor {

    private static final String BRIDGE_TO_PRIVATE_SUFFIX = "$P";
    private static final String BRIDGE_TO_SUPER_SUFFIX = "$S";

    private final TypeSystem xts;
    private final NodeFactory xnf;
    private Map<X10MethodDef,X10MethodDef> privateBridges = CollectionFactory.<X10MethodDef,X10MethodDef>newHashMap();
    private Map<MethodInstance,X10MethodDef> superBridges = CollectionFactory.<MethodInstance,X10MethodDef>newHashMap();
    private Map<MethodInstance,X10MethodDecl> superDecls = CollectionFactory.<MethodInstance,X10MethodDecl>newHashMap();

    public InlineHelper(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
        xts = (TypeSystem) ts;
        xnf = (NodeFactory) nf;
    }

    @Override
    protected NodeVisitor enterCall(Node parent, Node n) throws SemanticException {
        return super.enterCall(parent, n);
    }
    
    @Override
    public Node override(Node parent, Node n) {
        // compute bridge methods
        // FIXME avoid name collision
        Position pos = Position.COMPILER_GENERATED;
        if (n instanceof ClassDecl) {
            ClassDecl d = (ClassDecl) n;
            final X10ClassDef cd = d.classDef();
            if (prepareForInlining(cd)) {
                final List<Call> supers = new ArrayList<Call>();
                for (ClassMember cm : d.body().members()) {
                    if (cm instanceof X10MethodDecl) {
                        final X10MethodDecl mdcl = (X10MethodDecl) cm;
                        final X10MethodDef md = mdcl.methodDef();
                        // compute bridge methods for private methods
                        if (mdcl.body() != null && prepareForInlining(md)) {
                            mdcl.body().visit(new NodeVisitor() {
                                @Override
                                public Node leave(Node parent, Node old, Node n, NodeVisitor v) {
                                    if (parent instanceof Call && n instanceof Special) {
                                        if (((Special) n).kind().equals(Special.SUPER) && Types.selfBinding(((Special) n).type()).equals(cd.thisVar())) {
                                            Call call = (Call) parent;
                                            if (!containsMethod(supers, call)) {
                                                supers.add(call);
                                                // generate bridge methods for super call
                                                MethodInstance smi = call.methodInstance(); // this will be the superclass method
                                                X10ClassType ct = smi.container().toClass();
                                                // The method is non-final, because the names may clash
                                                List<Ref<? extends Type>> formalTypes = new ArrayList<Ref<? extends Type>>(smi.formalTypes().size());
                                                for (Type ft : smi.formalTypes()) {
                                                    formalTypes.add(Types.ref(ft));
                                                }
                                                List<Ref<? extends Type>> throwTypes = new ArrayList<Ref<? extends Type>>(smi.throwTypes().size());
                                                for (Type tt : smi.throwTypes()) {
                                                    throwTypes.add(Types.ref(tt));
                                                }
                                                X10MethodDef nmd = xts.methodDef(smi.position(), smi.errorPosition(), Types.ref(cd.asType()),
                                                        Flags.PUBLIC, Types.ref(smi.returnType()),
                                                        makeSuperBridgeName(ct.def(), smi.name()), md.typeParameters(),
                                                        formalTypes, throwTypes, cd.thisDef(), Types.toLocalDefList(smi.formalNames()), Types.ref(smi.guard()),
                                                        Types.ref(smi.typeGuard()), smi.offerType(), null);
                                                superBridges.put(smi, nmd);
                                                superDecls.put(smi, mdcl);
                                            }
                                        }
                                    }
                                    return n;
                                }
                            });
                        }
                        if (!md.flags().isPrivate()) {
                            continue;
                        }
                        // generate bridge methods for private method
                        Name pmn = makePrivateBridgeName(md.name());
                        X10MethodDef nmd = findPrivateBridgeMethod(pmn, md);
                        if (nmd == null) {
                            nmd = createPrivateBridgeMethod(pmn, md, cd, pos);
                        }
                        privateBridges.put(md, nmd);
                    }
                }
            }
        }
        return null;
    }

    private X10MethodDef findPrivateBridgeMethod(Name pmn, X10MethodDef md) {
        List<Type> argTypes = new ArrayList<Type>(md.formalTypes().size());
        for (Ref<? extends Type> f : md.formalTypes()) {
            argTypes.add(f.get());
        }
        try {
            Type ct = md.container().get();
            Collection<MethodInstance> existingPrivateBridges =
                xts.findMethods(ct, xts.MethodMatcher(ct, pmn, argTypes, context));
            if (existingPrivateBridges.isEmpty()) return null;
            return existingPrivateBridges.iterator().next().x10Def();
        } catch (SemanticException e) {
            return null;
        }
    }

    private X10MethodDef createPrivateBridgeMethod(Name pmn, X10MethodDef md, X10ClassDef cd, Position pos) {
        Type ct = Types.instantiateTypeParametersExplicitly(cd.asType());
        LocalDef ldef = null;
        if (!md.flags().isStatic()) {
            ldef = xts.localDef(pos, Flags.FINAL, Types.ref(ct), cd.name());
        }
        
        List<Ref<? extends Type>> argTypes = new ArrayList<Ref<? extends Type>>(md.formalTypes().size() + 1);
        for (Ref<? extends Type> f : md.formalTypes()) {
            argTypes.add(f);
        }
        if (!md.flags().isStatic()) {
            argTypes.add(Types.ref(ct));
        }

        X10MethodDef nmd = (X10MethodDef) xts.methodDef(md.position(), md.errorPosition(), Types.ref(cd.asType()),
                md.flags().clearPrivate().clearProtected().clearNative().Public().Static(),
                Types.ref(md.returnType().get()), pmn, argTypes, md.throwTypes());
        // check
        List<ParameterType> rts = new ArrayList<ParameterType>(md.typeParameters());
        if (!md.flags().isStatic()) {
            X10ClassDef d2 = (X10ClassDef) cd;
            for (ParameterType pt : d2.typeParameters()) {
                rts.add(pt);
            }
        }
        nmd.setTypeParameters(rts);
        return nmd;
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
            final X10ClassDef cd = d.classDef();
            if (prepareForInlining(cd)) {
                List<ClassMember> members = d.body().members();
                List<ClassMember> nmembers = new ArrayList<ClassMember>(members.size());
                
                for (ClassMember cm : members) {
                    if (cm instanceof FieldDecl) {
                        FieldDecl fdcl = (FieldDecl) cm;
                        // XTENLANG-3076 make static fields private to avoid java programmers accidentally see uninitialized fields.
                        if (fdcl.flags().flags().isStatic()) {
                            // TODO uncomment this after generating accessor method for @PerPlace static fields (XTENLANG-3077)
//                            if (fdcl.flags().flags().isPublic()) {
//                                fdcl = fdcl.flags(xnf.FlagsNode(pos, fdcl.flags().flags().clearPublic().Private()));
//                            }
                        } else {
                            if (fdcl.flags().flags().isPrivate()) {
                                fdcl = fdcl.flags(xnf.FlagsNode(pos, fdcl.flags().flags().clearPrivate().Public()));
                            }
                        }
                        nmembers.add(fdcl);
                    }
                    else if (cm instanceof X10MethodDecl) {
                        X10MethodDecl mdcl = (X10MethodDecl) cm;
                        MethodDef md = mdcl.methodDef();
                        // generate bridge methods for private methods
                        nmembers.add(cm);
                        if (!mdcl.flags().flags().isPrivate()) {
                            continue;
                        }
                        X10MethodDef nmd = privateBridges.get((X10MethodDef) md);
                        List<Formal> formals = new ArrayList<Formal>(mdcl.formals());
                        Type ct = cd.asType();
                        ct = Types.instantiateTypeParametersExplicitly(ct);
                        LocalDef ldef = null;
                        if (!mdcl.flags().flags().isStatic()) {
                            ldef = xts.localDef(pos, Flags.FINAL, Types.ref(ct), cd.name());
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
                        } else {
                            body = xnf.Block(pos, xnf.Return(pos, call));
                        }
                        // XTENLANG-3011 propagate @Throws annotations
                        if (mdcl.body() != null) {
                            body = (Block) ((X10Ext_c) body.ext()).annotations(((X10Ext_c) mdcl.body().ext()).annotations());
                        }
                        X10MethodDecl nmdcl = xnf.MethodDecl(pos, xnf.FlagsNode(pos, mdcl.flags().flags().clearPrivate().clearProtected().clearNative().Public().Static()), mdcl.returnType(), xnf.Id(pos, nmd.name()), formals, body);
                        // check
                        List<TypeParamNode> ts = new ArrayList<TypeParamNode>(mdcl.typeParameters());
                        if (!mdcl.flags().flags().isStatic()) {
                            X10ClassDef d2 = (X10ClassDef) cd;
                            for (ParameterType pt : d2.typeParameters()) {
                                ts.add(xnf.TypeParamNode(pos, xnf.Id(pos, pt.name())).type(pt));
                            }
                        }
                        nmdcl = nmdcl.typeParameters(ts);
                        nmdcl = nmdcl.methodDef(nmd);
                        nmembers.add(nmdcl);
                        cd.addMethod(nmd);
                    }
                    else {
                        nmembers.add(cm);
                    }
                    
                }
                // generate bridge methods for super call
                for (MethodInstance mi : superBridges.keySet()) {
                    X10MethodDef nmd = superBridges.get(mi);
                    if (((X10ClassType) nmd.container().get()).def() != cd) continue; // check that we are in the right class
                    List<Formal> formals = new ArrayList<Formal>();
                    List<Expr> arguments = new ArrayList<Expr>(nmd.formalTypes().size());
                    int i = 0;
                    for (Ref<? extends Type> tr : nmd.formalTypes()) {
                        Name name = Name.make("a" + i);
                        LocalDef ldef = xts.localDef(pos, Flags.FINAL, tr, name);
                        Id id = xnf.Id(pos, name);
                        Formal formal = xnf.Formal(pos, xnf.FlagsNode(pos, Flags.FINAL), xnf.CanonicalTypeNode(pos, tr), id);
                        formals.add(formal.localDef(ldef));
                        arguments.add(xnf.Local(pos, id).localInstance(ldef.asInstance()).type(tr.get()));
                        ++i;
                    }
                    Type rt = nmd.returnType().get();
                    Type container = mi.container();
                    Call call = (Call) xnf.Call(pos, xnf.Super(pos).type(container), xnf.Id(pos, mi.name()), arguments).methodInstance(mi).type(rt);
                    Block body;
                    if (rt.isVoid()) {
                        body = xnf.Block(pos, xnf.Eval(pos, call));
                    } else {
                        body = xnf.Block(pos, xnf.Return(pos, call));
                    }
                    // XTENLANG-3011 propagate @Throws annotations
                    X10MethodDecl mdcl = superDecls.get(mi);
                    if (mdcl.body() != null) {
                        body = (Block) ((X10Ext_c) body.ext()).annotations(((X10Ext_c) mdcl.body().ext()).annotations());
                    }
                    X10CanonicalTypeNode returnType = xnf.X10CanonicalTypeNode(pos, rt);
                    // Copy annotations of return type as well since it has @Throws annotations for native method.
                    returnType = (X10CanonicalTypeNode) ((X10Ext_c) returnType.ext()).annotations(((X10Ext_c) mdcl.returnType().ext()).annotations());
                    X10MethodDecl mdcl1 = xnf.MethodDecl(pos, xnf.FlagsNode(pos, nmd.flags()), returnType, xnf.Id(pos, nmd.name()), formals, body);
                    mdcl1 = mdcl1.methodDef(nmd);
                    // check
                    List<TypeParamNode> typeParams = new ArrayList<TypeParamNode>(mdcl.typeParameters());
                    // need to pass class type parameters, too?
                    mdcl1 = mdcl1.typeParameters(typeParams);
                    nmembers.add(mdcl1);
                    cd.addMethod(nmd);
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
        if (n instanceof X10Call && opts.x10_config.INLINE) {
            X10Call call = (X10Call) n;
            Receiver target = call.target();
            MethodInstance mi = call.methodInstance();
            if (mi.flags().isPrivate() && !isSameDef(mi.container(), context.currentClass(),context) && !isSameTopLevel(mi.container(), context.currentClass(), context)) {
                X10MethodDef md = mi.x10Def();
                Name pmn = makePrivateBridgeName(md.name());
                X10MethodDef nmd = findPrivateBridgeMethod(pmn, md);
                if (nmd == null) {
                    X10ClassDef cd = ((X10ClassType) md.container().get()).def();
                    nmd = createPrivateBridgeMethod(pmn, md, cd, pos);
                    cd.addMethod(nmd);
                }
                Id id = xnf.Id(pos, pmn);
                List<Expr> arguments = new ArrayList<Expr>(call.arguments());
                if (!mi.flags().isStatic()) {
                    arguments.add((Expr) target); 
                }
                MethodInstance nmi = nmd.asInstance();
                Type tt = Types.baseType(target.type());
                List<Type> tas = new ArrayList<Type>();
                tas.addAll(mi.typeParameters());
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

    public static Name makeSuperBridgeName(final ClassDef cd, Name name) {
        return Name.make(Emitter.mangleAndFlattenQName(cd.asType()) + "$" + Emitter.mangleToJava(name) + BRIDGE_TO_SUPER_SUFFIX);
    }

    public static Name makePrivateBridgeName(Name name) {
        return Name.make(Emitter.mangleToJava(name) + BRIDGE_TO_PRIVATE_SUFFIX);
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
