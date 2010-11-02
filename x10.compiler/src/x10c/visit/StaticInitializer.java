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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import polyglot.ast.Assign;
import polyglot.ast.Binary;
import polyglot.ast.Block;
import polyglot.ast.Call;
import polyglot.ast.ClassBody;
import polyglot.ast.ClassMember;
import polyglot.ast.Expr;
import polyglot.ast.FieldDecl;
import polyglot.ast.FloatLit;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.IntLit;
import polyglot.ast.IntLit_c;
import polyglot.ast.Lit;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Receiver;
import polyglot.ast.Stmt;
import polyglot.ast.Try;
import polyglot.ast.TypeNode;
import polyglot.ast.Unary;
import polyglot.frontend.Job;
import polyglot.types.ClassType;
import polyglot.types.ConstructorDef;
import polyglot.types.ConstructorInstance;
import polyglot.types.FieldDef;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.MethodDef;
import polyglot.types.MethodInstance;
import polyglot.types.Name;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.types.VarDef;
import polyglot.util.Pair;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.TypeParamNode;
import x10.ast.X10Call;
import x10.ast.X10Call_c;
import x10.ast.X10ClassBody_c;
import x10.ast.X10ClassDecl_c;
import x10.ast.X10FieldDecl_c;
import x10.ast.X10Field_c;
import x10.ast.X10Formal_c;
import x10.ast.X10LocalAssign_c;
import x10.ast.X10LocalDecl_c;
import x10.ast.X10MethodDecl_c;
import x10.ast.X10NodeFactory_c;
import x10.ast.X10Return_c;
import x10.types.ParameterType;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import polyglot.types.Context;
import x10.types.X10Flags;
import x10.types.X10MethodInstance;
import polyglot.types.TypeSystem;
import x10.types.X10TypeSystem_c;
import x10c.ast.X10CNodeFactory_c;
import x10c.types.X10CTypeSystem_c;

public class StaticInitializer extends ContextVisitor {

    private final X10CTypeSystem_c xts;
    private final X10CNodeFactory_c xnf;

    static final private String initMethodPrefix = "getInitialized$";

    // mapping static field and corresponding initializer method
    private Map<Pair<Type,Name>, StaticFieldInfo> staticFinalFields = 
            new HashMap<Pair<Type,Name>, StaticFieldInfo>();

    public StaticInitializer(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
        xts = (X10CTypeSystem_c) ts;
        xnf = (X10CNodeFactory_c) nf;
    }

    @Override
    protected Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException {
        if (!(n instanceof X10ClassDecl_c))
            return n;

        X10ClassDecl_c ct = (X10ClassDecl_c)n;
        X10ClassDef classDef = ct.classDef();
        assert(classDef != null);

        // collect static fields to deal with
        staticFinalFields.clear();
        // ct.body().dump(System.err);
        ClassBody classBody = checkStaticFields(ct);

        // add initializer method of each static field to the class member list
        List<ClassMember> members = new ArrayList<ClassMember>();
        members.addAll(classBody.members());

        for (Map.Entry<Pair<Type,Name>, StaticFieldInfo> entry : staticFinalFields.entrySet()) {
            Name fName = entry.getKey().snd();
            StaticFieldInfo fieldInfo = entry.getValue();
            if (fieldInfo.right == null)
                continue;

            // gen new field var
            FieldDecl fdCond = makeFieldVar4Guard(fName, classDef);
            classDef.addField(fdCond.fieldDef());
            // add in the top
            members.add(0, fdCond);

            // gen new field var
            FieldDecl fdInitVar = makeFieldVar4InitVar(fName, fieldInfo, classDef);
            classDef.addField(fdInitVar.fieldDef());
            // add in the top
            members.add(0, fdInitVar);

            // gen new initialize method
            MethodDecl md = makeInitMethod(fName, fieldInfo, fdInitVar.fieldDef(), fdCond.fieldDef(), classDef);
            classDef.addMethod(md.methodDef());
            // add in the bottom
            members.add(md);
        }
        classBody = classBody.members(members);
        // classBody.dump(System.err);
        return (X10ClassDecl_c) ct.body(classBody);
    }

    private ClassBody checkStaticFields(final X10ClassDecl_c ct) {
        // one pass scan of class body and collect vars for static initialization
        ClassBody c = (ClassBody)ct.body().visit(new NodeVisitor() {
            @Override
            public Node override(Node parent, Node n) {
                if (n instanceof X10ClassDecl_c) {
                    // should not visit subtree of inner class (already done)
                    return n;
                }
                return null;
            }

            @Override
            public Node leave(Node parent, Node old, Node n, NodeVisitor v) {
                if (n instanceof X10FieldDecl_c) {
                    X10FieldDecl_c fd = (X10FieldDecl_c)n;
                    Flags flags = fd.fieldDef().flags();
                    if (flags.isFinal() && flags.isStatic()) {
                        // static final field
                        Expr right = checkFieldDeclRHS((Expr)fd.init(), fd, ct);
                        if (right != fd.init()) {
                            // rhs replaced
                            // System.out.println("RHS of FieldDecl replaced: "+ct.classDef()+"."+fd.fieldDef().name());
                            return xnf.FieldDecl(fd.position(), fd.flags(), fd.type(), fd.name(),
                                                 right).fieldDef(fd.fieldDef());
                        }
                    }
                }
                if (n instanceof X10MethodDecl_c) {
                    X10MethodDecl_c md = (X10MethodDecl_c)n;
                    if (md.methodDef().flags().isStatic() && md.body() != null && 
                        md.body().statements().size() == 1) {
                        // static method with single body
                        Stmt stmt = md.body().statements().get(0);
                        if (stmt instanceof X10Return_c) {

                            Expr right = checkMethodDeclRHS(((X10Return_c)stmt).expr());
                            if (right != ((X10Return_c)stmt).expr()) {
                                // rhs replaced
                                // System.out.println("RHS of MethodDecl replaced: "+ct.classDef()+"."+md.methodDef().name());
                                List<Stmt> stmts = new ArrayList<Stmt>();
                                stmts.add(xnf.X10Return(n.position(), right, true));
                                Block newBody = xnf.Block(n.position(), stmts);
                                return xnf.MethodDecl(n.position(), md.flags(), md.returnType(), md.name(),
                                                      md.formals(), newBody).methodDef(md.methodDef());
                            }
                        }
                    }
                }
                return n;
            };
        });
        return c;
    }

    private Expr checkFieldDeclRHS(Expr rhs, X10FieldDecl_c fd, final X10ClassDecl_c ct) {
        // traverse nodes in RHS
        Id leftName = fd.name();
        Type leftType = fd.type().type();
        final Flags flags = fd.fieldDef().flags();

        final AtomicBoolean found = new AtomicBoolean(false);
        Expr newRhs = (Expr)rhs.visit(new NodeVisitor() {
            @Override
            public Node leave(Node parent, Node old, Node n, NodeVisitor v) {
                if (n instanceof X10Call_c) {
                    X10Call call = (X10Call)n;
                    X10MethodInstance mi = (X10MethodInstance) call.methodInstance();
                    if (mi.container().isClass() && mi.flags().isStatic() && !call.target().type().isNumeric()) {
                        // found reference to static method
                        found.set(true);
                    }
                }
                if (n instanceof X10Field_c) {
                    X10Field_c f = (X10Field_c)n;
                    if (f.flags().isFinal() && f.flags().isStatic()) {
                        // found reference to static field
                        if (checkFieldRefReplacementRequired(f)) {
                            found.set(true);
                            // replace with a static method call
                            // X10ClassType receiver = ct.classDef().asType();
                            X10ClassType receiver = (X10ClassType)f.target().type();
                            return makeStaticCall(n.position(), receiver, f.name(), f.type(), flags);
                        }
                    }
                }
                return n;
            }
        });

        // register original rhs
        X10ClassType receiver = ct.classDef().asType();
        StaticFieldInfo fieldInfo = getFieldEntry(receiver, leftName.id());
        fieldInfo.right = (fieldInfo.methodDef != null || found.get()) ? newRhs : null;

        if (fieldInfo.right != null) {
            return makeStaticCall(rhs.position(), receiver, leftName, leftType, flags);
        }
        // no change
        return rhs;
    }

    Call makeStaticCall(Position pos, X10ClassType receiver, Id id, Type returnType, Flags flags) {
        // create MethodDef
        Name name = Name.make(initMethodPrefix+id);
        StaticFieldInfo fieldInfo = getFieldEntry(receiver, id.id());
        MethodDef md = fieldInfo.methodDef; 
        if (md == null) {
            Position CG = Position.compilerGenerated(null);
            List<Ref<? extends Type>> argTypes = Collections.<Ref<? extends Type>>emptyList();
            md = xts.methodDef(CG, Types.ref(receiver), 
                                         Flags.STATIC, Types.ref(returnType), name, argTypes);
            fieldInfo.methodDef = md;
        }

        // create static call for initialization
        List<TypeNode> typeArgsN = Collections.<TypeNode>emptyList();
        List<Expr> args = Collections.<Expr>emptyList();
        MethodInstance mi = xts.createMethodInstance(pos, Types.ref(md));
        Call result = (Call) xnf.X10Call(pos, xnf.CanonicalTypeNode(pos, receiver),
                                        xnf.Id(pos, name), typeArgsN, args)
                                        .methodInstance(mi).type(returnType);
        return result;
    }

    private Expr checkMethodDeclRHS(Expr rhs) {
        // traverse nodes in RHS
        Expr newRhs = (Expr)rhs.visit(new NodeVisitor() {
            @Override
            public Node leave(Node parent, Node old, Node n, NodeVisitor v) {
                if (n instanceof X10Field_c) {
                    X10Field_c f = (X10Field_c)n;
                    if (f.flags().isFinal() && f.flags().isStatic()) {
                        // replace reference to static field with a static method call
                        if (checkFieldRefReplacementRequired(f)) {
                            X10ClassType receiver = (X10ClassType)f.target().type();
                            return makeStaticCall(n.position(), receiver, f.name(), f.type(), f.flags());
                        }
                    }
                }
                return n;
            }
        });
        return newRhs;
    }

    private FieldDecl makeFieldVar4Guard(Name fName, X10ClassDef classDef) {
        // make FieldDef of AtomicBoolean
        Position CG = Position.compilerGenerated(null);
        ClassType type = (ClassType)xts.AtomicBoolean();
        Flags flags = X10Flags.PRIVATE.Static().Final();

        Name name = Name.make("initStatus$"+fName);
        FieldDef fd = xts.fieldDef(CG, Types.ref(classDef.asType()), flags, Types.ref(type), name); 
        FieldInstance fi = xts.createFieldInstance(CG, Types.ref(fd));

        // create right hand side: new AtomicBoolean(false)
        TypeNode tn = xnf.X10CanonicalTypeNode(CG, type);
        List<Expr> args = new ArrayList<Expr>();
        args.add(xnf.BooleanLit(CG, false).type(xts.Boolean()));

        ConstructorDef cd = xts.defaultConstructor(CG, Types.ref(type)); 
        ConstructorInstance ci = xts.createConstructorInstance(CG, Types.ref(cd));
        Expr init = xnf.New(CG, tn, args).constructorInstance(ci).type(type);

        // fieldDecl and its association with fieldDef
        FieldDecl result = xnf.FieldDecl(CG, xnf.FlagsNode(CG, flags), tn, xnf.Id(CG,name), init);
        result = result.fieldDef(fd);
        return result;
    }

    private FieldDecl makeFieldVar4InitVar(Name fName, StaticFieldInfo fieldInfo, X10ClassDef classDef) {
        // make FieldDef
        Position CG = Position.compilerGenerated(null);
        Type type = fieldInfo.right.type();
        Name name = Name.make("initVal$"+fName);
        Flags flags = X10Flags.PRIVATE.Static();

        FieldDef fd = xts.fieldDef(CG, Types.ref(classDef.asType()), flags, Types.ref(type), name); 
        FieldInstance fi = xts.createFieldInstance(CG, Types.ref(fd));

        // create the field declaration node
        Expr init = getDefaultValue(CG, type);
        TypeNode tn = xnf.X10CanonicalTypeNode(CG, type);
        FieldDecl result = xnf.FieldDecl(CG, xnf.FlagsNode(CG, flags), tn, xnf.Id(CG,name), init);
        // associate fieldDef with fieldDecl
        result = result.fieldDef(fd);
        return result;
    }

    private Expr getDefaultValue(Position pos, Type type) {
        if (type.isBoolean())
            return xnf.BooleanLit(pos, false).type(type);
        else if (type.isChar())
            return xnf.CharLit(pos, ' ').type(type);
        else if (type.isByte() || type.isShort() || type.isInt())
            return xnf.IntLit(pos, IntLit.INT, 0).type(type);
        else if (type.isLong())
            return xnf.IntLit(pos, IntLit.LONG, 0).type(type);
        else if (type.isFloat())
            return xnf.FloatLit(pos, FloatLit.FLOAT, 0.0).type(type);
        else if (type.isDouble())
            return xnf.FloatLit(pos, FloatLit.DOUBLE, 0.0).type(type);
        else if (type == xts.String())
            return xnf.StringLit(pos, null).type(type);
        else if (xts.isSubtype(type, xts.UByte()) || xts.isSubtype(type, xts.UShort()) ||
                 xts.isSubtype(type, xts.UInt()) || xts.isSubtype(type, xts.ULong())) {
            ConstructorDef cd = xts.defaultConstructor(pos, Types.ref((ClassType)type)); 
            ConstructorInstance ci = xts.createConstructorInstance(pos, Types.ref(cd));
            List<Expr> args = new ArrayList<Expr>();
            args.add(xnf.IntLit(pos, IntLit.INT, 0).type(type));
            return xnf.New(pos, xnf.X10CanonicalTypeNode(pos, type), args).constructorInstance(ci).type(type);
        }
        else
            return null;
    }

    private MethodDecl makeInitMethod(Name fName, StaticFieldInfo fieldInfo, FieldDef fdInitVar, 
                                      FieldDef fdCond, X10ClassDef classDef) {
        // get MethodDef
        Name name = Name.make(initMethodPrefix+fName);
        Type type = fieldInfo.right.type();
        MethodDef md = fieldInfo.methodDef;
        assert(md != null);

        // create a method declaration node
        List<TypeParamNode> typeParamNodes = Collections.<TypeParamNode>emptyList();
        List<Formal> formals = Collections.<Formal>emptyList();
        Position CG = Position.compilerGenerated(null);

        TypeNode returnType = xnf.X10CanonicalTypeNode(CG, type);
        Block body = makeMethodBody(fieldInfo, fdInitVar, fdCond, classDef);
        MethodDecl result = xnf.X10MethodDecl(CG, xnf.FlagsNode(CG, Flags.STATIC), returnType, xnf.Id(CG,name), 
                                              typeParamNodes, formals, null, null, body);
        // associate methodDef with methodDecl
        result = result.methodDef(md);
        return result;
    }

    private Block makeMethodBody(StaticFieldInfo initInfo, FieldDef fdInitVar, FieldDef fdCond, X10ClassDef classDef) {
        Position CG = Position.compilerGenerated(null);
        TypeNode receiver = xnf.X10CanonicalTypeNode(CG, classDef.asType());

        // gen AtomicBoolean.getAndSet(true)
        Expr cond = genAtomicGuard(CG, receiver, fdCond);

        FieldInstance fi = fdInitVar.asInstance();
        Expr right = initInfo.right;
        Expr left = xnf.Field(CG, receiver, xnf.Id(CG, fdInitVar.name())).fieldInstance(fi).type(right.type());
        Stmt assignStmt = xnf.Eval(CG, xnf.FieldAssign(CG, receiver, xnf.Id(CG, fdInitVar.name()), Assign.ASSIGN, 
                                                       right).fieldInstance(fi).type(right.type()));
        // gen x10.lang.Runtime.hereInt() == 0
        // TODO place check
//        Expr placeCheck = genPlaceCheckGuard(CG);

        // make statement block
        List<Stmt> stmts = new ArrayList<Stmt>();
        // TODO place check
//        stmts.add(xnf.If(CG, placeCheck, xnf.If(CG, cond, assignStmt)));
        stmts.add(xnf.If(CG, cond, assignStmt));
        stmts.add(xnf.X10Return(CG, left, false));
        Block body = xnf.Block(CG, stmts);
        return body;
    }

    private Expr genPlaceCheckGuard(Position pos) {
        ClassType type = (ClassType)xts.Runtime();
        Id name = xnf.Id(pos, Name.make("hereInt"));

        MethodDef md = xts.methodDef(pos, Types.ref(type), Flags.STATIC, Types.ref(xts.Int()), name.id(), 
                                     Collections.<Ref<? extends Type>>emptyList());
        MethodInstance mi = xts.createMethodInstance(pos, Types.ref(md));
        Expr here = xnf.X10Call(pos, xnf.X10CanonicalTypeNode(pos, type), name, 
                                Collections.<TypeNode>emptyList(), 
                                Collections.<Expr>emptyList()).methodInstance(mi).type(xts.Int());
        Expr placeCheck = xnf.Binary(pos, here, Binary.EQ, xnf.IntLit(pos, IntLit.INT, 0).type(xts.Int()));

        return placeCheck;
    }

    private Expr genAtomicGuard(Position pos, TypeNode receiver, FieldDef fdCond) {
        Expr ab = xnf.Field(pos, receiver, xnf.Id(pos, fdCond.name())).fieldInstance(fdCond.asInstance());
        Id gs = xnf.Id(pos, Name.make("getAndSet"));

        List<Ref<? extends Type>> argTypes = new ArrayList<Ref<? extends Type>>();
        argTypes.add(Types.ref(xts.Boolean()));
        MethodDef md = xts.methodDef(pos, Types.ref((ClassType)xts.AtomicBoolean()), 
                                     Flags.NONE, Types.ref(xts.Boolean()), gs.id(), argTypes);
        MethodInstance mi = xts.createMethodInstance(pos, Types.ref(md));

        List<Expr> args = new ArrayList<Expr>();
        args.add(xnf.BooleanLit(pos, true).type(xts.Boolean()));
        List<TypeNode> typeParamNodes = new ArrayList<TypeNode>();
        typeParamNodes.add(xnf.CanonicalTypeNode(pos, xts.Boolean()));
        Expr call = xnf.X10Call(pos, ab, gs, typeParamNodes, args).methodInstance(mi).type(xts.Boolean());
        Expr cond = xnf.Unary(pos, Unary.NOT, call);
        return cond;
    }

    StaticFieldInfo getFieldEntry(Type target, Name name) {
        Pair<Type,Name> key = new Pair<Type,Name>(target, name);
        StaticFieldInfo fieldInfo = staticFinalFields.get(key);
        if (fieldInfo == null) {
            fieldInfo = new StaticFieldInfo();
            staticFinalFields.put(key, fieldInfo);
        }
        return fieldInfo;
    }

    boolean checkFieldRefReplacementRequired(X10Field_c f) {
        if (f.target().type().isNumeric())
            // @NativeRep class should be excluded
            return false;

        if (f.isConstant())
            return false;

        Pair<Type,Name> key = new Pair<Type,Name>(f.target().type(), f.name().id());
        StaticFieldInfo fieldInfo = staticFinalFields.get(key);
        // not yet registered, or registered as replacement required
        return fieldInfo == null || fieldInfo.right != null || fieldInfo.methodDef != null;
    }

    static class StaticFieldInfo {
        Expr right;             // RHS expression, if replaced with initialization method
        MethodDef methodDef;    // getInitialized metdhoDef to be replaced
    }
}
