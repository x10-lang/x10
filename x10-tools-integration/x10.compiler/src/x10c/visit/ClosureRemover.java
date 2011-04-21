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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import polyglot.ast.Assign;
import polyglot.ast.Block;
import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.ClassBody;
import polyglot.ast.ClassDecl;
import polyglot.ast.ClassMember;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FieldAssign;
import polyglot.ast.FieldDecl;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.Local;
import polyglot.ast.MethodDecl;
import polyglot.ast.NamedVariable;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Receiver;
import polyglot.ast.Special;
import polyglot.ast.Special.Kind;
import polyglot.ast.TypeNode;
import polyglot.frontend.Job;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.Context;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.LocalInstance;
import polyglot.types.Name;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.types.VarDef;
import polyglot.types.VarInstance;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.UniqueID;
import polyglot.util.CollectionUtil; import x10.util.CollectionFactory;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.Closure;
import x10.ast.ClosureCall;
import x10.ast.TypeParamNode;
import x10.ast.X10Call;
import x10.ast.X10ClassDecl;
import x10.ast.X10ConstructorDecl;
import x10.ast.X10Formal;
import x10.ast.X10MethodDecl;
import x10.types.ClosureDef;
import x10.types.ParameterType;
import x10.types.ThisInstance;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.types.X10CodeDef;
import x10.types.X10ConstructorDef;
import x10.types.X10FieldDef;
import x10.types.X10FieldInstance;
import x10.types.X10LocalDef;
import x10.types.X10LocalInstance;
import x10.types.X10MethodDef;
import x10.types.constraints.TypeConstraint;

public class ClosureRemover extends ContextVisitor {
    
    public static final String STATIC_NESTED_CLASS_BASE_NAME = "$Closure";
    private static final String STATIC_METHOD_BASE_NAME = "$closure_apply";
    private static final Name OUTER_NAME = Name.make("out$$");
    
    private final TypeSystem xts;
    private final NodeFactory xnf;
    
    public ClosureRemover(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
        xts = ts;
        xnf = nf;
    }
    
    @Override
    protected Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException {

        if (n instanceof ClassDecl) {
            ClassDecl cd = (ClassDecl) n;
            final X10ClassDef def = (X10ClassDef) cd.classDef();
            final ClassType ct = def.asType();
            final List<ClassMember> nmembers = new ArrayList<ClassMember>();

            // closure -> static method
            cd = (ClassDecl) cd.visitChildren(createClosureToStaticMethodVisitor(ct, nmembers));

            // closure -> static inner class
            cd = (ClassDecl) cd.visitChildren(createClosureToStaticInnerClassVisitor(def, nmembers));
            
            ClassBody body = cd.body();
            nmembers.addAll(0, body.members());
            return ((ClassDecl) n).body(body.members(nmembers));
        }
        
        return n;
    }

    private ContextVisitor createClosureToStaticMethodVisitor(final ClassType ct, final List<ClassMember> nmembers) {
        return new ContextVisitor(job,ts,nf){
            protected Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException {
                if (n instanceof ClosureCall) {
                    
                    ClosureCall cc = (ClosureCall) n;
                    Expr target = cc.target();
                    
                    if (target instanceof Closure) {
                        final Position pos = Position.COMPILER_GENERATED;
                        Flags flags = Flags.STATIC.Final().Private();
                        
                        Closure cl = (Closure) target;
                        ClosureDef cld = cl.closureDef();
                        
                        Name name = Name.makeFresh(STATIC_METHOD_BASE_NAME);
                        List<VarInstance<? extends VarDef>> capturedEnv = cld.capturedEnvironment();
                        
                        // DEBUG
//                        System.out.println(n.position() + " " + name + " " + cl);
//                        System.out.println(capturedEnv);
                        
                        final List<NamedVariable> capturedVarsExThis = new ArrayList<NamedVariable>();
                        Map<String, X10LocalDef> nameToLocalDef = CollectionFactory.newHashMap();
                        
                        Block body = rewriteClosureBody(cl.body(), capturedEnv, capturedVarsExThis, nameToLocalDef, cl.formals());

                        List<Ref<? extends Type>> argTypes = new ArrayList<Ref<? extends Type>>(cl.formals().size());
                        for (Formal f : cl.formals()) {
                            Type t = f.type().type();
                            argTypes.add(f.type().typeRef());
                        }
    
                        List<Expr> arguments = new ArrayList<Expr>(cc.arguments());
                        List<Formal> formals = new ArrayList<Formal>(cl.formals());
                        
                        for (VarInstance<? extends VarDef> vi : capturedEnv) {
                            if (vi instanceof ThisInstance) {
                                arguments.add(xnf.Special(pos, Kind.THIS).type(vi.type()));
                                argTypes.add(Types.ref(vi.type()));
                                
                                X10LocalDef li = xts.localDef(pos, Flags.FINAL, Types.ref(vi.type()), OUTER_NAME);
                                formals.add(xnf.Formal(pos, xnf.FlagsNode(pos, Flags.FINAL), xnf.CanonicalTypeNode(pos, Types.ref(vi.type())), xnf.Id(pos, OUTER_NAME)).localDef(li));
                            }
                        }
                        
                        for (NamedVariable vn : capturedVarsExThis) {
                            Name name2 = vn.name().id();
                            X10LocalDef li;
                            if (vn instanceof Local) {
                                li = (X10LocalDef) vn.varInstance().def();
                            }
                            else {
                                li = nameToLocalDef.get(name2.toString());
                            }
                            arguments.add(vn);
                            argTypes.add(Types.ref(vn.type()));
                            formals.add(xnf.Formal(pos, xnf.FlagsNode(pos, Flags.FINAL), xnf.CanonicalTypeNode(pos, Types.ref(vn.type())), xnf.Id(pos, vn.name().id())).localDef(li));
                        }
                        
                        List<ParameterType> rts = new ArrayList<ParameterType>();
                        List<TypeNode> tns = new ArrayList<TypeNode>();
                        List<TypeParamNode> tps = new ArrayList<TypeParamNode>();
    
                        List<ParameterType> mtps = context.currentClassDef().typeParameters();
                        if (mtps != null) {
                            for (ParameterType t : mtps) {
                                ParameterType pt = (ParameterType) t;
                                rts.add(pt);
                                tns.add(xnf.X10CanonicalTypeNode(pos, pt));
                                tps.add(xnf.TypeParamNode(pos, xnf.Id(pos, pt.name())).type(pt));
                            }
                        }
                        
                        if (!(context.currentCode() instanceof X10ConstructorDef)) {
                            List<ParameterType> codeParam = getCurrentCodeParameterType(context);
                            for (ParameterType pt :codeParam) {
                                boolean contains = false;
                                for (ParameterType pt2: context.currentClassDef().typeParameters()) {
                                    if (pt.def().equals(pt2.def())) {
                                        contains = true;
                                        break;
                                    }
                                }
                                if (!contains) {
                                    rts.add(pt);
                                    tns.add(xnf.X10CanonicalTypeNode(pos, pt));
                                    tps.add(xnf.TypeParamNode(pos, xnf.Id(pos, pt.name())).type(pt));
                                }
                            }
                        }
                        
                        X10MethodDef md = (X10MethodDef) xts.methodDef(pos, Types.ref(ct), flags, cl.returnType().typeRef(), name, argTypes);
                        md.setTypeParameters(rts);
                        
                        X10MethodDecl mdcl = xnf.MethodDecl(pos, xnf.FlagsNode(pos, flags), cl.returnType(), xnf.Id(pos, name), formals,  body);
                        mdcl = mdcl.typeParameters(tps);
    
                        nmembers.add(mdcl.methodDef(md));
                        
                        X10Call x10call = (X10Call) xnf.Call(n.position(), xnf.CanonicalTypeNode(n.position(), ct), xnf.Id(n.position(), name), arguments).methodInstance(md.asInstance()).type(cl.returnType().type());
                        x10call = x10call.typeArguments(tns);
                        return x10call;
                    }                      
                }
                return n;
            }
            
            private Block rewriteClosureBody(Block closureBody,final List<VarInstance<? extends VarDef>> capturedEnv, final List<NamedVariable> capturedVarsExThis, final Map<String, X10LocalDef> nameToLocalDef, final List<Formal> formals) {
                final Position pos = Position.COMPILER_GENERATED;
                return (Block) closureBody.visit(new ContextVisitor(job, ts, nf){
                    public Node override(Node parent, Node n) {
                        if (n instanceof Closure) {
                            return n;
                        }
                        return null;
                    };
                    
                    protected Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException {
                        if (n instanceof Field) {
                            Field field = (Field) n;
                            for (VarInstance<? extends VarDef> var : capturedEnv) {
                                if (!var.flags().isFinal()) {
                                    continue;
                                }
                                // because of coming not the same VarInstance
                                if (var.def().equals(field.fieldInstance().def())) {
                                    Receiver target = field.target();
                                    if (target instanceof Local) {
                                        for (Formal formal : formals) {
                                            if (formal.name() != null && ((Local) target).name().id().equals(formal.name().id())) {
                                                return n;
                                            }
                                        }
                                    }
                                    
                                    X10LocalDef li;
                                    if (!contains(capturedVarsExThis, var.def())) {
                                        capturedVarsExThis.add((NamedVariable) old);
                                        li = xts.localDef(pos, Flags.FINAL, Types.ref(var.type()), var.name());
                                        nameToLocalDef.put(var.name().toString(), li);
                                    } else {
                                        li = nameToLocalDef.get(var.name().toString());
                                    }
                                    return xnf.Local(pos, xnf.Id(pos, var.name())).localInstance(li.asInstance()).type(var.type());
                                }
                            }
                            return field.targetImplicit(false);
                        }
                        if (n instanceof Local) {
                            Local local = (Local) n;
                            for (VarInstance<? extends VarDef> var : capturedEnv) {
                                if (var.def().equals(local.localInstance().def())) {
                                    if (!contains(capturedVarsExThis, var.def())) {
                                        capturedVarsExThis.add((NamedVariable) old);
                                        return n;
                                    }
                                }
                            }                                
                            return n;
                        }
                        
                        if (n instanceof Special) {
                            Special special = (Special) n;
                            if (special.kind() == Special.THIS) {
                                X10LocalDef li = xts.localDef(pos, Flags.FINAL, Types.ref(special.type()), OUTER_NAME);
                                return ((Local) xnf.Local(pos, xnf.Id(pos, OUTER_NAME)).type(special.type())).localInstance(li.asInstance());
                            }
                        }
                        return n;
                    };
                }.context(context));
            }
        }.context(context);
    }

    private ContextVisitor createClosureToStaticInnerClassVisitor(final X10ClassDef def,final List<ClassMember> nmembers) {
        return new ContextVisitor(job, ts, nf) {
            @Override
            public Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException {
                
                // TODO handle "this" in type constraints
                if (n instanceof Closure) {
                    Closure cl = (Closure) n;
                    ClosureDef cld = cl.closureDef();
                    final Position pos = Position.COMPILER_GENERATED;
                    Flags privateStatic = Flags.PRIVATE.Static();
                    
                    final List<VarInstance<? extends VarDef>> capturedEnv = cld.capturedEnvironment();
                    
                    Block closureBody = (Block) cl.body();
                    
                    Id staticInnerClassName = xnf.Id(pos, UniqueID.newID(STATIC_NESTED_CLASS_BASE_NAME));
                    
                    // DEBUG
//                    System.out.println(n.position() + " " + staticInnerClassName + " " + cl);
//                    System.out.println(capturedEnv);

                    // create class def for static inner
                    final X10ClassDef staticInnerClassDef = (X10ClassDef) xts.createClassDef();
                    
                    staticInnerClassDef.superType(Types.ref(xts.Object()));
                    staticInnerClassDef.kind(ClassDef.MEMBER);
                    staticInnerClassDef.name(staticInnerClassName.id());
                    staticInnerClassDef.outer(Types.<ClassDef>ref(def));
                    staticInnerClassDef.setPackage(Types.ref(context.package_()));
                    staticInnerClassDef.flags(privateStatic);
                    staticInnerClassDef.setInterfaces(cld.classDef().interfaces());
                    staticInnerClassDef.setThisDef(ts.thisDef(pos, Types.ref(staticInnerClassDef.asType())));
                    
                    // TODO set method bounds?
                    if (context.currentCode().staticContext()) {
                        staticInnerClassDef.setTypeBounds(Types.ref(new TypeConstraint()));
                    }
                    else {
                        for (ParameterType pt :def.typeParameters()) {
                            staticInnerClassDef.addTypeParameter(pt, pt.getVariance());
                        }
                        staticInnerClassDef.setTypeBounds(def.typeBounds());
                    }

                    List<ParameterType> codeParam = null;
                    if (!(context.currentCode() instanceof X10ConstructorDef)) {
                        codeParam = getCurrentCodeParameterType(context);
                        for (ParameterType pt :codeParam) {
                            boolean contains = false;
                            for (ParameterType pt2: staticInnerClassDef.typeParameters()) {
                                if (pt.def().equals(pt2.def())) {
                                    contains = true;
                                    break;
                                }
                            }
                            if (!contains) {
                                staticInnerClassDef.addTypeParameter(pt, pt.getVariance());
                            }
                        }
                    }
                    
                    // TODO handle "this" in type constraints
                    X10MethodDef closureMethodDef = (X10MethodDef) xts.methodDef(pos, Types.ref(staticInnerClassDef.asType()), Flags.PUBLIC, cld.returnType(), ClosureCall.APPLY, cld.formalTypes());
                    closureMethodDef.setThisDef(staticInnerClassDef.thisDef());
                    
                    staticInnerClassDef.setMethods(Collections.singletonList(closureMethodDef));
                    // create class decl
                    List<TypeNode> interfaces = new ArrayList<TypeNode>();
                    List<Type> cint = cld.asType().interfaces();
                    for (Type it : cint) {
                        interfaces.add(xnf.X10CanonicalTypeNode(pos, it));
                    }
                    X10ClassDecl staticInnerClassDecl = (X10ClassDecl) xnf.ClassDecl(pos, xnf.FlagsNode(pos, privateStatic), staticInnerClassName, xnf.X10CanonicalTypeNode(pos, xts.Object()), interfaces, xnf.ClassBody(pos, Collections.<ClassMember>emptyList()));
                    
                    List<TypeParamNode> tpns = new ArrayList<TypeParamNode>();
                    for (ParameterType pt : staticInnerClassDef.typeParameters()) {
                        tpns.add(xnf.TypeParamNode(pos, xnf.Id(pos, pt.name()), pt.getVariance()).type(pt));
                    }
                    
                    staticInnerClassDecl = staticInnerClassDecl.typeParameters(tpns);
                    
                    final List<NamedVariable> capturedVarsExThis = new ArrayList<NamedVariable>();
                    Map<String, X10FieldDef> nameToLocalDef = CollectionFactory.newHashMap();
                    
                    // rewrite closure method body
                    closureBody = rewriteClosureBody(closureBody, staticInnerClassDef, capturedEnv, capturedVarsExThis, nameToLocalDef, cl.formals());
                    
                    MethodDecl mdcl = xnf.MethodDecl(pos, xnf.FlagsNode(pos, Flags.PUBLIC), xnf.CanonicalTypeNode(pos, Types.baseType(cl.returnType().type())), xnf.Id(pos, ClosureCall.APPLY), cl.formals(), closureBody).methodDef(closureMethodDef);
                    mdcl = (MethodDecl) mdcl.body(closureBody);
                    mdcl = (MethodDecl) mdcl.typeCheck(this);

                    ClassBody body = xnf.ClassBody(pos, Collections.<ClassMember>singletonList(mdcl));
                    
                    staticInnerClassDecl = staticInnerClassDecl.body(body);
                    
                    // add constructor
                    List<ClassMember> cm = new ArrayList<ClassMember>(staticInnerClassDecl.body().members());
                    
                    List<Formal> formals = new ArrayList<Formal>(capturedEnv.size());
                    List<Ref<? extends Type>> argTypes = new ArrayList<Ref<? extends Type>>(capturedEnv.size());
                    List<Expr> args = new ArrayList<Expr>(capturedEnv.size());
                    Block body2 = xnf.Block(pos);
                    
                    for (VarInstance<? extends VarDef> vi : capturedEnv) {
                        if (vi instanceof ThisInstance || (vi instanceof FieldInstance && !vi.flags().isFinal())) { // the latter means the receiver fo vi should be this
                            Name name = OUTER_NAME;
                            
                            X10LocalDef li = xts.localDef(pos, Flags.FINAL, Types.ref(vi.type()), name);
                            X10Formal formal = xnf.Formal(pos, xnf.FlagsNode(pos, Flags.FINAL), xnf.X10CanonicalTypeNode(pos, Types.baseType(vi.type())), xnf.Id(pos, name)).localDef(li);
                            formals.add(formal);
                            argTypes.add(vi.def().type());
                            args.add(createExpr(pos, vi));
                            
                            X10FieldDef fi = xts.fieldDef(pos, Types.ref(staticInnerClassDef.asType()), Flags.FINAL.Private(), Types.ref(vi.type()), name);
                            staticInnerClassDef.addField(fi);
                            
                            FieldDecl fdcl = xnf.FieldDecl(pos, xnf.FlagsNode(pos, Flags.FINAL.Private()), xnf.X10CanonicalTypeNode(pos, vi.type()), xnf.Id(pos, name));
                            cm.add(fdcl.fieldDef(fi));
                            
                            FieldAssign fa = xnf.FieldAssign(pos, xnf.Special(pos, Kind.THIS).type(staticInnerClassDef.asType()), xnf.Id(pos, name), Assign.ASSIGN, xnf.Local(pos, xnf.Id(pos, name)).localInstance(li.asInstance()).type(vi.type())).fieldInstance(fi.asInstance());
                            body2 = body2.append(xnf.Eval(pos, fa));
                            break;
                        }
                    }
                    
                    for (NamedVariable vn : capturedVarsExThis) {
                        Name name = vn.name().id();
                        X10LocalDef li = xts.localDef(pos, Flags.FINAL, Types.ref(vn.type()), vn.name().id());
                        X10Formal formal = xnf.Formal(pos, xnf.FlagsNode(pos, Flags.FINAL), xnf.X10CanonicalTypeNode(pos, Types.baseType(vn.type())), xnf.Id(pos, name)).localDef(li);
                        formals.add(formal);
                        argTypes.add(li.type());
                        args.add(vn);
                        
                        assert (nameToLocalDef.containsKey(vn.name().toString()));
                        X10FieldDef fd = nameToLocalDef.get(vn.name().toString());
                        
                        staticInnerClassDef.addField(fd);
                        FieldDecl fdcl = xnf.FieldDecl(pos, xnf.FlagsNode(pos, fd.flags()), xnf.X10CanonicalTypeNode(pos, vn.type()), xnf.Id(pos, name));
                        cm.add(fdcl.fieldDef(fd));
                        
                        FieldAssign fa = xnf.FieldAssign(pos, xnf.Special(pos, Kind.THIS).type(staticInnerClassDef.asType()), xnf.Id(pos, name), Assign.ASSIGN, xnf.Local(pos, xnf.Id(pos, name)).localInstance(li.asInstance()).type(vn.type())).fieldInstance(fd.asInstance());
                        body2 = body2.append(xnf.Eval(pos, fa));
                    }
                    
                    X10ConstructorDecl consdcl = (X10ConstructorDecl) xnf.ConstructorDecl(pos, xnf.FlagsNode(pos, Flags.PRIVATE), staticInnerClassDecl.name(), formals, body2);
                    consdcl.typeParameters(staticInnerClassDecl.typeParameters());
                    CanonicalTypeNode typeNode = xnf.CanonicalTypeNode(pos, staticInnerClassDef.asType());
                    consdcl.returnType(typeNode);
                    
                    List<Type> typeArgs = new ArrayList<Type>(staticInnerClassDef.typeParameters());
                    X10ClassType staticInnerClassType = staticInnerClassDef.asType().typeArguments(typeArgs);
                    
                    X10ConstructorDef consd = (X10ConstructorDef) xts.constructorDef(pos,
                                                              Types.ref(staticInnerClassType),
                                                              Flags.PRIVATE,
                                                              argTypes);
                    
                    cm.add((ClassMember) consdcl.constructorDef(consd).typeCheck(this));
                    
                    staticInnerClassDef.addConstructor(consd);
                    
                    staticInnerClassDecl = staticInnerClassDecl.classDef(staticInnerClassDef);
                    
                    ClassBody cb = staticInnerClassDecl.body();
                    nmembers.add((ClassMember) staticInnerClassDecl.body(cb.members(cm)).typeCheck(this));
                    
                    // TODO
//                    return xnf.New(pos, xnf.CanonicalTypeNode(pos, Types.ref(staticInnerClassType)), args).constructorInstance(consd.asInstance()).type(cl.type()).typeCheck(this);
                    return xnf.New(pos, xnf.CanonicalTypeNode(pos, Types.ref(staticInnerClassType)), args).constructorInstance(consd.asInstance()).type(cl.type());
                }
                return n;
            }

            private Block rewriteClosureBody(Block closureBody,
                                             final X10ClassDef staticInnerClassDef,
                                             final List<VarInstance<? extends VarDef>> capturedEnv,
                                             final List<NamedVariable> capturedVarsExThis,
                                             final Map<String, X10FieldDef> nameToFieldDef,
                                             final List<Formal> formals
            ) {
                final Position pos = Position.COMPILER_GENERATED;
                return (Block) closureBody.visit(new ContextVisitor(job, ts, nf){
                    protected Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException {
                        if (n instanceof Field) {
                            Field field = (Field) n;
                            for (VarInstance<? extends VarDef> var : capturedEnv) {
                                if (!var.flags().isFinal()) {
                                    continue;
                                }
                                // because of coming not the same VarInstance
                                if (var.def().equals(field.fieldInstance().def())) {
                                    Receiver target = field.target();
                                    if (target instanceof Local) {
                                        // XTENLANG-2359
//                                        for (Formal formal : formals) {
//                                            if (formal.name() != null && ((Local) target).name().id().equals(formal.name().id())) {
                                                return n;
//                                            }
//                                        }
                                    }
                                    X10FieldDef fd;
                                    if (!contains(capturedVarsExThis, var.def())) {
                                        capturedVarsExThis.add((NamedVariable) old);
                                        Flags ff = Flags.FINAL.Private();
                                        if (field.flags().isTransient()) {
                                            ff = ff.Transient();
                                        }
                                        fd = xts.fieldDef(pos, Types.ref(staticInnerClassDef.asType()), ff, Types.ref(field.type()), field.name().id());
                                        nameToFieldDef.put(var.name().toString(), fd);
                                    } else {
                                        fd = nameToFieldDef.get(var.name().toString());
                                    }
                                    return xnf.Field(pos, xnf.This(pos).type(staticInnerClassDef.asType()), xnf.Id(pos, fd.name())).fieldInstance(fd.asInstance()).type(var.type());
                                }
                            }
                            return field.targetImplicit(false);
                        }
                        if (n instanceof Local) {
                            Local local = (Local) n;
                            for (VarInstance<? extends VarDef> var : capturedEnv) {
                                if (var.def().equals(local.localInstance().def())) {
                                    X10FieldDef fd;
                                    if (!contains(capturedVarsExThis, var.def())) {
                                        capturedVarsExThis.add((NamedVariable) old);
                                        Flags ff = Flags.FINAL.Private();
                                        if (local.flags().isTransient()) {
                                            ff = ff.Transient();
                                        }
                                        fd = xts.fieldDef(pos, Types.ref(staticInnerClassDef.asType()), ff, Types.ref(local.type()), local.name().id());
                                        nameToFieldDef.put(var.name().toString(), fd);
                                    } else {
                                        fd = nameToFieldDef.get(var.name().toString());
                                    }
                                    return xnf.Field(pos, xnf.This(pos).type(staticInnerClassDef.asType()), xnf.Id(pos, fd.name())).fieldInstance(fd.asInstance()).type(var.type());
                                }
                            }                                
                            return n;
                        }
                        
                        // this => out$
                        if (n instanceof Special) {
                            Special special = (Special) n;
                            if (special.kind() == Special.THIS) {
                                Type type = Types.baseType(special.type());
                                X10FieldDef fi = xts.fieldDef(pos, Types.ref(staticInnerClassDef.asType()), Flags.PRIVATE.Final(), Types.ref(type), OUTER_NAME);
                                Special thiz = (Special) xnf.Special(pos, Kind.THIS).type(staticInnerClassDef.asType());
                                return xnf.Field(pos, thiz, xnf.Id(pos, OUTER_NAME)).fieldInstance(fi.asInstance()).type(type);
                            }
                        }
                        return n;
                    };
                }.context(context));
            }
        }.context(context);
    }

    private static boolean contains(List<NamedVariable> capturedVars, VarDef def) {
        for (NamedVariable nv : capturedVars) {
            if (nv.varInstance().def().equals(def)) {
                return true;
            }
        }
        return false;
    }
    
    private List<ParameterType> getCurrentCodeParameterType(Context context) {
        X10CodeDef codeDef = (X10CodeDef) context.currentCode();
        if (!(codeDef instanceof ClosureDef)) {
            return codeDef.typeParameters();
        }
        return getCurrentCodeParameterType(context.pop());
    }
    
    private Expr createExpr(Position pos, VarInstance<? extends VarDef> vi) {
        if (vi instanceof X10LocalInstance) {
            return xnf.Local(pos, xnf.Id(pos, vi.name())).localInstance((LocalInstance) vi.def().asInstance()).type(vi.type());
        } else if (vi instanceof X10FieldInstance) {
            return xnf.Field(pos, xnf.Special(pos, Kind.THIS).type(((X10FieldInstance) vi).container()), xnf.Id(pos, vi.name())).fieldInstance((FieldInstance) vi.def().asInstance()).type(vi.type());
        } else if (vi instanceof ThisInstance) {
            return xnf.Special(pos, Kind.THIS).type(vi.type());
        }
        throw new InternalCompilerError("");//TODO
    }
}
