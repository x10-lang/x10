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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import polyglot.ast.Assign;
import polyglot.ast.Binary;
import polyglot.ast.Binary.Operator;
import polyglot.ast.Block;
import polyglot.ast.BooleanLit;
import polyglot.ast.Call;
import polyglot.ast.Cast;
import polyglot.ast.Catch;
import polyglot.ast.CharLit;
import polyglot.ast.ClassBody;
import polyglot.ast.ClassDecl_c;
import polyglot.ast.ClassMember;
import polyglot.ast.Conditional;
import polyglot.ast.ConstructorDecl;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FieldDecl;
import polyglot.ast.FlagsNode;
import polyglot.ast.FloatLit;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.Initializer;
import polyglot.ast.IntLit;
import polyglot.ast.IntLit_c;
import polyglot.ast.Local;
import polyglot.ast.LocalDecl;
import polyglot.ast.MethodDecl;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.NullLit;
import polyglot.ast.ProcedureDecl;
import polyglot.ast.Receiver;
import polyglot.ast.Return;
import polyglot.ast.Stmt;
import polyglot.ast.StringLit;
import polyglot.ast.Try;
import polyglot.ast.TypeNode;
import polyglot.ast.Unary;
import polyglot.frontend.Job;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.ConstructorDef;
import polyglot.types.ConstructorInstance;
import polyglot.types.ContainerType;
import polyglot.types.Context;
import polyglot.types.FieldDef;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.InitializerDef;
import polyglot.types.LocalDef;
import polyglot.types.LocalInstance;
import polyglot.types.MethodDef;
import polyglot.types.QName;

import polyglot.types.Name;
import polyglot.types.ObjectType;
import polyglot.types.Package;
import polyglot.types.ParsedClassType;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.types.VarDef;
import polyglot.util.Pair;
import polyglot.util.Position;
import x10.types.X10FieldInstance;
import x10.util.CollectionFactory;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.X10CompilerOptions;
import x10.ast.Closure;
import x10.ast.ClosureCall;
import x10.ast.ParExpr;
import x10.ast.TypeParamNode;
import x10.ast.X10Call;
import x10.ast.X10Call_c;
import x10.ast.X10ClassDecl;
import x10.ast.X10ClassDecl_c;
import x10.ast.X10ConstructorDecl;
import x10.ast.X10FieldDecl;
import x10.ast.X10Field_c;
import x10.ast.X10Formal_c;
import x10.ast.X10LocalAssign_c;
import x10.ast.X10LocalDecl_c;
import x10.ast.X10MethodDecl;
import x10.ast.X10New_c;
import x10.ast.X10NodeFactory_c;
import x10.ast.X10SourceFile_c;
import x10.ast.SettableAssign;
import x10.constraint.XTerm;
import x10.emitter.Emitter;
import x10.extension.X10Ext;
import x10.types.checker.Converter;
import x10.types.constraints.CConstraint;
import x10.types.ConstrainedType;
import x10.types.ParameterType;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.types.X10ConstructorDef;
import x10.types.X10ConstructorInstance;
import x10.types.X10Def;

import x10.types.X10MethodDef;
import x10.types.MethodInstance;
import x10.types.X10ProcedureDef;
import x10.visit.Desugarer;
import x10.visit.X10PrettyPrinterVisitor;
import x10.visit.X10TypeChecker;
import x10c.ast.X10CNodeFactory_c;
import x10c.types.X10CTypeSystem_c;

public class StaticInitializer extends ContextVisitor {

    // XTENLANG-3081(part2)
    private static final boolean checkExceptionInConstantExpression = true; // should be true

    private final X10CTypeSystem_c xts;
    private final X10CNodeFactory_c xnf;
    private final WeakHashMap<X10ProcedureDef,ProcedureDecl> procDeclCache;
    private final WeakHashMap<Block,Boolean> procBodyCache;

    public static final String initializerPrefix = "get$";

    private static final String nestedShadowClass4Interface = "$Shadow";

    // mapping static field and corresponding initializer method
    private Map<Pair<Type,Name>, StaticFieldInfo> staticFinalFields = 
            CollectionFactory.newHashMap();
    // for checking single vm mode
    private X10CompilerOptions opts = (X10CompilerOptions) job.extensionInfo().getOptions();

    public StaticInitializer(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
        xts = (X10CTypeSystem_c) ts;
        xnf = (X10CNodeFactory_c) nf;
        procBodyCache = new WeakHashMap<Block,Boolean>();
        procDeclCache = new WeakHashMap<X10ProcedureDef,ProcedureDecl>();
    }

    @Override
    protected Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException {
        if (!(parent instanceof X10ClassDecl_c))
            return n;

        X10ClassDecl_c ct = (X10ClassDecl_c)parent;
        if (old != ct.body())
            return n;

        ClassBody classBody = (ClassBody) n;
        X10ClassDef classDef = ct.classDef();
        assert(classDef != null);

        Context context = ct.enterChildScope(classBody, ((ContextVisitor) v).context());

        // collect static fields to deal with
        staticFinalFields.clear();
        // classBody.dump(System.err);
        // System.out.println("StaticInitilizer for "+classDef);
        classBody = checkStaticFields(classBody, context);

        if (staticFinalFields.isEmpty())
            // nothing to do
            return classBody;

        List<ClassMember> currMembers = new ArrayList<ClassMember>();
        currMembers.addAll(classBody.members());

        if (!ct.flags().flags().isInterface()) {
            // create a new member list for initializer methods of each static field
            List<ClassMember> newMembers = createNewMembers(classDef);
            currMembers.addAll(newMembers);
        } else {
            // create a nested shadow class
            X10ClassDecl shadowDecl = createNestedShadowClass(ct);

            // create a new member list for the shadow class just created
            List<ClassMember> newMembers = createNewMembers(shadowDecl.classDef());

            // add members into the body of the shadow class
            ClassBody shadowBody = shadowDecl.body();
            shadowBody = shadowBody.members(newMembers);
            shadowDecl = shadowDecl.body(shadowBody);

            // add the shadow class in the original interface body
            currMembers.add(shadowDecl);
        }

        classBody = classBody.members(currMembers);
        // classBody.dump(System.err);
        return classBody;
    }

    private List<ClassMember> createNewMembers(X10ClassDef classDef) {
        Position CG = Position.compilerGenerated(null);
        List<ClassMember> members = new ArrayList<ClassMember>();

        for (Map.Entry<Pair<Type,Name>, StaticFieldInfo> entry : staticFinalFields.entrySet()) {
            Name fName = entry.getKey().snd();
            StaticFieldInfo fieldInfo = entry.getValue();

            if (fieldInfo.right == null && fieldInfo.fieldDef == null)
                continue;

            MethodDecl md = null; 
            if (fieldInfo.right != null) {
                // gen new field var
                FieldDecl fdExcept = makeFieldVar4Except(CG, fName, classDef);
                classDef.addField(fdExcept.fieldDef());
                // add in the top
                members.add(0, fdExcept);

                FieldDecl fdCond = makeFieldVar4Guard(CG, fName, classDef);
                classDef.addField(fdCond.fieldDef());
                // add in the top
                members.add(0, fdCond);

                if (fieldInfo.left != null) {
                    // interface case: add field declaration to the shadow class
                    FieldDef fd = fieldInfo.left.fieldDef();
                    Flags newFlags = fd.container().get().toClass().flags().clearInterface();
                    FieldDef newFd = xts.fieldDef(CG, Types.ref(classDef.asType()), newFlags, fd.type(), fd.name());
                    members.add(0, fieldInfo.left.fieldDef(newFd));
                }

                // gen new initialize method
                md = makeInitMethod(CG, fName, fieldInfo, fdExcept.fieldDef(), fdCond.fieldDef(), classDef);
            } else {
                md = makeFakeInitMethod(CG, fName, fieldInfo, classDef);
            }
            classDef.addMethod(md.methodDef());
            // add in the bottom
            members.add(md);
        }

        return members;
    }

    private X10ClassDecl createNestedShadowClass(X10ClassDecl_c interfaceClass) {
        // create ClassDef first
        X10ClassDef cDef = createShadowClassDef(interfaceClass.classDef());

        // create ClassDecl
        Position CG = Position.compilerGenerated(null);
        FlagsNode fNode = xnf.FlagsNode(CG, cDef.flags());
        Id id = xnf.Id(CG, cDef.name());
        TypeNode superTN = cDef.superType() != null ? xnf.CanonicalTypeNode(CG, cDef.superType()) : null;
        List<ClassMember> cmembers = new ArrayList<ClassMember>();
        ClassBody body = xnf.ClassBody(CG, cmembers);
        List<TypeNode> interfaceTN = Collections.<TypeNode>emptyList();
        X10ClassDecl cDecl = (X10ClassDecl) xnf.ClassDecl(CG, fNode, id, superTN, interfaceTN, 
                                                          body).classDef(cDef);
        return cDecl;
    }

    private X10ClassDef createShadowClassDef(X10ClassDef interfaceClassDef) {
        X10ClassDef cDef = (X10ClassDef) xts.createClassDef(interfaceClassDef.sourceFile());
        List<Ref<? extends Type>> interfacesRef = new ArrayList<Ref<? extends Type>>();
        interfacesRef.add(Types.ref(xts.Any()));
        cDef.setInterfaces(interfacesRef);
        cDef.name(Name.make(nestedShadowClass4Interface));
        cDef.setFlags(Flags.PUBLIC.Abstract());
        cDef.kind(ClassDef.MEMBER);
        cDef.outer(Types.ref(interfaceClassDef));
        return cDef;
    }

    private ClassBody checkStaticFields(ClassBody body, Context context) {
        final X10ClassDef cd = context.currentClassDef();
        // one pass scan of class body and collect vars for static initialization
        ClassBody c = (ClassBody)body.visit(new NodeVisitor() {
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
                if (n instanceof X10FieldDecl) {
                    X10FieldDecl fd = (X10FieldDecl)n;
                    Flags flags = fd.fieldDef().flags();
                    if (isPerProcess(fd.fieldDef())) return n;
                    if (flags.isFinal() && flags.isStatic()) {
                        // static final field
                        StaticFieldInfo fieldInfo = checkFieldDeclRHS((Expr)fd.init(), fd, cd);
                        if (fieldInfo.right != null) {
                            // drop final and set private (XTENLANG-3076)
                            // System.out.println("RHS of FieldDecl replaced: "+ct.classDef()+"."+fd.fieldDef().name());
                            FlagsNode fn = xnf.FlagsNode(fd.position(), flags.clearFinal().clearPublic().clearProtected().Private());
                            // remove rhs: suppress java-level static initialization
                            Expr init = getDefaultValue(fd.position(), fd.init().type());
                            FieldDecl newDecl = xnf.FieldDecl(fd.position(), fn, fd.type(), fd.name(),
                                                 init).fieldDef(fd.fieldDef());
                            if (cd.flags().isInterface()) {
                                // move the field declaration to a shadow class
                                fieldInfo.left = newDecl;
                                return null;
                            }
                            return newDecl;
                        }
                    }
                }
                if (n instanceof X10Field_c) {
                    X10Field_c f = (X10Field_c)n;
                    if (isPerProcess(f.fieldInstance().x10Def())) return n;
                    if (f.flags().isFinal() && f.flags().isStatic()) {
                        // found reference to static field
                        if (checkFieldRefReplacementRequired(f)) {
                            // replace with a static method call
                            Type targetType = f.target().type();
                            if (targetType instanceof ParsedClassType) {
                                X10ClassDef targetClassDef = ((ParsedClassType)targetType).def();
                                if (targetClassDef.flags().isInterface())
                                    // target nested shadow class within interface
                                    targetType = createShadowClassDef(targetClassDef).asType();
                            }
                            else if (targetType instanceof ConstrainedType)
                                targetType = ((ConstrainedType)targetType).baseType().get();

                            X10ClassType receiver = (X10ClassType)targetType;
                            return makeStaticCall(n.position(), receiver, f.name(), f.type());
                        }
                    }
                }
                return n;
            };
        });
        return c;
    }

    private StaticFieldInfo checkFieldDeclRHS(Expr rhs, X10FieldDecl fd, X10ClassDef cd) {
        // traverse nodes in RHS
        Id leftName = fd.name();

        // true means "found something not suitable for per-place initialization"
        final AtomicBoolean found = new AtomicBoolean(false);
        final boolean deep_analysis = opts.x10_config.STATICS_PER_PLACE_ANALYSIS;
        Expr newRhs = (Expr)rhs.visit(new NodeVisitor() {
            @Override
            public Node override(Node parent, Node n) {
                if (found.get())
                    // already found
                    return n;
                if (n instanceof Expr) {
                    if (isGlobalInit((Expr)n) || isConstraintToLiteral(((Expr)n).type())) {
                        // initialization can be done in all places -- do not visit subtree further
                        // System.out.println("isGlobalInit true in checkFieldDeclRHS: "+(Expr)n);
                        return n;
                    }
                    if (n instanceof X10Call_c) {
                        X10Call call = (X10Call)n;
                        MethodInstance mi = call.methodInstance();
//                        if (!mi.container().isSubtype(xts.Object(), context)) {
//                            // allow method calls on non-objects (including numerics, char and boolean)
                        if (call.target().type().isNumeric() || call.target().type().isChar() || call.target().type().isBoolean()) { 
                            // allow method calls on numerics, char or boolean
                            // XTENLANG-3081(part2)
                            // exclude "1.operator/(0)"
                            if (checkExceptionInConstantExpression) {
                                found.set(true);
                                return n;
                            }
                        } else if (deep_analysis && mi.flags().isStatic()) {
                            // found reference to static method
                            X10MethodDecl mdecl = getMethodDeclaration(mi);
                            if (mdecl == null || checkProcedureBody(mdecl.body(), 0)) {
                                // unsafe method call
                                found.set(true);
                                return n;
                            }
                        } else {
                            // non-static method call or no deep analysis
                            found.set(true);
                            return n;
                        }
                    } else if (n instanceof X10Field_c) {
                        X10Field_c f = (X10Field_c)n;
                        if (f.flags().isFinal() && f.flags().isStatic()) {
                            // found reference to static field
                            if (checkFieldRefReplacementRequired(f)) {
                                found.set(true);
                                return n;
                            }
                        }
                    } else if (n instanceof X10New_c) {
                        if (deep_analysis) {
                            X10New_c neu = (X10New_c)n;
                            X10ConstructorInstance ci = neu.constructorInstance();
                            // get declaration of constructor
                            X10ConstructorDecl cdecl = getConstructorDeclaration(ci);
                            if (cdecl == null || checkProcedureBody(cdecl.body(), 0)) {
                                // unsafe constructor
                                found.set(true);
                                return n;
                            }
                        }
                        else {
                            // deep analysis disabled
                            found.set(true);
                            return n;
                        }
                    }
                    // XTENLANG-3081(part2)
                    else {
                        // exclude "1 as Any as Object"
                        if (checkExceptionInConstantExpression) {
                            found.set(true);
                            return n;
                        }
                    }
                }
                // continue traversal
                return null;
            }
        });

        // register original rhs
        X10ClassType receiver = cd.asType();
        StaticFieldInfo fieldInfo = getFieldEntry(receiver, leftName.id());
        fieldInfo.right = (fieldInfo.methodDef != null || found.get()) ? newRhs : null;
        fieldInfo.fieldDef = fd.fieldDef();

        return fieldInfo;
    }

    private X10ConstructorDecl getConstructorDeclaration(X10ConstructorInstance ci) {
        X10ConstructorDef cd = ci.x10Def();
        X10ClassType containerBase = (X10ClassType) Types.get(cd.container());
        X10ClassDef container = containerBase.x10Def();
        if (container == null)
            return null;
        return (X10ConstructorDecl)getProcedureDeclaration(cd, container);
    }

    private X10MethodDecl getMethodDeclaration(MethodInstance mi) {
        X10MethodDef md = mi.x10Def();
        // get container and declaration for method
        X10ClassType containerBase = (X10ClassType) Types.get(md.container());
        X10ClassDef container = containerBase.x10Def();
        if (container == null)
            return null;
        return (X10MethodDecl)getProcedureDeclaration(md, container);
    }

    private ProcedureDecl getProcedureDeclaration(final X10ProcedureDef candidate, X10ClassDef container) {
        ProcedureDecl r = procDeclCache.get(candidate);
        if (r != null) return r;

        // obtain X10SourceFile ast of the target class that already runs preliminary compilation phases
        final Node ast = getAST(container);
        if (ast == null)
            return null;

        // find the target declaration of constructor or method
        final ProcedureDecl[] decl = new ProcedureDecl[1];
        ast.visit(new NodeVisitor() {
            @Override
            public Node override(Node n) {
                if (decl[0] != null)
                    // already found the decl, short-circuit search
                    return n;
                if (n instanceof X10FieldDecl)
                    // not contain ctor decls, short-circuit search
                    return n;
                if (n instanceof X10MethodDecl) {
                    if (candidate == ((X10MethodDecl) n).methodDef()) {
                        // found it!!
                        decl[0] = (X10MethodDecl) n;
                    }
                    return n;
                }
                if (n instanceof X10ConstructorDecl) {
                    if (candidate == ((X10ConstructorDecl) n).constructorDef()) {
                        // found it!!
                        decl[0] = (X10ConstructorDecl) n;
                    }
                    return n;
                }
                // continue traversal
                return null;
            }
        });
        if (decl[0] == null || decl[0].body() == null) {
            return null;
        }
        procDeclCache.put(candidate, decl[0]);
        return decl[0];
    }

    private Node getAST(X10ClassDef container) {
        // obtain the job for containing the constructor declaration
        Job job = container.job();
        if (job == null || job.ast() == null)
            return null;

        if (job == this.job())
            // current class
            return job.ast();

        // run the preliminary compilation phases on the job's AST
        Node ast = job.ast();
        assert (ast instanceof X10SourceFile_c);
        if (!((X10SourceFile_c) ast).hasBeenTypeChecked())
            ast = ast.visit(new X10TypeChecker(job, ts, nf, job.nodeMemo()).begin());
        if (ast == null)
            return null;
        if (!((X10Ext)ast.ext()).subtreeValid())
            return null;

        ast = ast.visit(new Desugarer(job, ts, nf).begin());
        return ast;
    }

    private boolean checkProcedureBody(final Block body, final int count) {
        Boolean r = procBodyCache.get(body);
        if (r != null)
            return (r == Boolean.TRUE);

        // Cut the search tree to avoid overly long compilation time.
        // True means centralized place-0 initialization is necessary,
        // which is a safe conservative assumption.
        if (count > 7) return true;

        // check static field references in the body of constructor or method
        final AtomicBoolean found = new AtomicBoolean(false);
        body.visit(new NodeVisitor() {
            @Override
            public Node override(Node n) {
                if (found.get())
                    // already found
                    return n;
                if (n instanceof Expr) {
                    if (n instanceof X10Call) {
                        X10Call call = (X10Call)n;
                        MethodInstance mi = call.methodInstance();
//                        if (!mi.container().isSubtype(xts.Object(), context)) { 
//                            // allow method calls on non-objects (including numerics, char and boolean)
                        if (call.target().type().isNumeric() || call.target().type().isChar() || call.target().type().isBoolean()) { 
                            // allow method calls on numerics, char or boolean
                            // XTENLANG-3081(part2)
                            // exclude "1.operator/(0)"
                            if (checkExceptionInConstantExpression) {
                                found.set(true);
                                return n;
                            }
                        } else if (mi.flags().isStatic()) {
                            // found reference to special initializer method
                            X10MethodDecl mdecl = getMethodDeclaration(mi);
                            if (mdecl == null || checkProcedureBody(mdecl.body(), count+1)) {
                                // target method is unsafe include static field references
                                found.set(true);
                                return n;
                            }
                        } else {
                            // we consider non-static method call as unsafe
                            found.set(true);
                            return n;
                        }
                    } else if (n instanceof X10Field_c) {
                        X10Field_c f = (X10Field_c)n;
                        if (f.flags().isFinal() && f.flags().isStatic()) {
                            if (checkFieldRefReplacementRequired(f)) {
                                // found reference to static field to be replaced
                                found.set(true);
                                return n;
                            }
                        }
                    } else if (n instanceof X10New_c) {
                        X10New_c neu = (X10New_c)n;
                        X10ConstructorInstance ci = neu.constructorInstance();
                        // get declaration of constructor
                        X10ConstructorDecl cdecl = getConstructorDeclaration(ci);
                        if (cdecl != null && !cdecl.body().equals(body) && checkProcedureBody(cdecl.body(), count+1)) {
                            // constructor include static field references to be replaced
                            found.set(true);
                            return n;
                        }
                    }
                    // XTENLANG-3081(part2)
                    else {
                        // exclude "1 as Any as Object"
                        if (checkExceptionInConstantExpression) {
                            found.set(true);
                            return n;
                        }
                    }
                }
                // continue traversal
                return null;
            }
        });
        procBodyCache.put(body, found.get() ? Boolean.TRUE : Boolean.FALSE);
        return found.get();
    }

    private Call makeStaticCall(Position pos, X10ClassType receiver, Id id, Type returnType) {
        // create MethodDef
        Name name = Name.make(initializerPrefix+id);
        StaticFieldInfo fieldInfo = getFieldEntry(receiver, id.id());
        MethodDef md = fieldInfo.methodDef;
        if (md == null) {
            md = makeMethodDef(pos, receiver, name, returnType);
            fieldInfo.methodDef = md;
        }

        // create static call for initialization
        List<TypeNode> typeArgsN = Collections.<TypeNode>emptyList();
        List<Expr> args = Collections.<Expr>emptyList();
        MethodInstance mi = xts.createMethodInstance(pos, pos, Types.ref(md));
        Call result = (Call) xnf.X10Call(pos, xnf.CanonicalTypeNode(pos, receiver),
                                        xnf.Id(pos, name), typeArgsN, args)
                                        .methodInstance(mi).type(returnType);
        return result;
    }

    private MethodDef makeMethodDef(Position pos, X10ClassType receiver, Name name, Type returnType) {
        Position CG = Position.compilerGenerated(null);
        List<Ref<? extends Type>> argTypes = Collections.<Ref<? extends Type>>emptyList();
        MethodDef md = xts.methodDef(CG, CG, Types.ref(receiver), 
                                     Flags.STATIC, Types.ref(returnType), name, argTypes, Collections.<Ref<? extends Type>>emptyList());
        return md;
    }

    private FieldDecl makeFieldVar4Guard(Position pos, Name fName, X10ClassDef classDef) {
        // make FieldDef of AtomicInteger
        ClassType type = (ClassType)xts.AtomicInteger();
        Flags flags = Flags.PRIVATE.Static().Final();

        Name name = Name.make("initStatus$"+fName);
        FieldDef fd = xts.fieldDef(pos, Types.ref(classDef.asType()), flags, Types.ref(type), name); 
        FieldInstance fi = xts.createFieldInstance(pos, Types.ref(fd));

        // create right hand side: new AtomicInteger(UNINITIALIZED)
        TypeNode tn = xnf.X10CanonicalTypeNode(pos, type);
        List<Expr> args = new ArrayList<Expr>();
        args.add(getInitDispatcherConstant(pos, "UNINITIALIZED").type(xts.Int()));

        ConstructorDef cd = xts.defaultConstructor(pos, pos, Types.ref(type)); 
        ConstructorInstance ci = xts.createConstructorInstance(pos, pos, Types.ref(cd));
        Expr init = xnf.New(pos, tn, args).constructorInstance(ci).type(type);

        // fieldDecl and its association with fieldDef
        FieldDecl result = xnf.FieldDecl(pos, xnf.FlagsNode(pos, flags), tn, xnf.Id(pos, name), init);
        result = result.fieldDef(fd);
        return result;
    }

    private FieldDecl makeFieldVar4Except(Position pos, Name fName, X10ClassDef classDef) {
        // make FieldDef of x10.lang.ExceptionInInitializer
        ClassType type = ExceptionInInitializer();
        Flags flags = Flags.PRIVATE.Static();

        Name name = Name.make("exception$"+fName);
        FieldDef fd = xts.fieldDef(pos, Types.ref(classDef.asType()), flags, Types.ref(type), name); 
        FieldInstance fi = xts.createFieldInstance(pos, Types.ref(fd));

        // create field declaration node
        TypeNode tn = xnf.X10CanonicalTypeNode(pos, type);
        FieldDecl result = xnf.FieldDecl(pos, xnf.FlagsNode(pos, flags), tn, xnf.Id(pos, name));
        result = result.fieldDef(fd);
        return result;
    }

    private Expr getDefaultValue(Position pos, Type type) {
        if (type.isBoolean())
            return xnf.BooleanLit(pos, false).type(type);
        else if (type.isChar())
            return xnf.CharLit(pos, '\0').type(type);
        else if (type.isByte() || type.isShort() || type.isInt())
            return xnf.IntLit(pos, IntLit.INT, 0).type(type);
        else if (type.isLong())
            return xnf.IntLit(pos, IntLit.LONG, 0).type(type);
        else if (type.isUByte() || type.isUShort() || type.isUInt())
            return xnf.IntLit(pos, IntLit.UINT, 0).type(type);
        else if (type.isULong())
            return xnf.IntLit(pos, IntLit.ULONG, 0).type(type);
        else if (type.isFloat())
            return xnf.FloatLit(pos, FloatLit.FLOAT, 0.0).type(type);
        else if (type.isDouble())
            return xnf.FloatLit(pos, FloatLit.DOUBLE, 0.0).type(type);
        else if (type.isString())
            return xnf.NullLit(pos).type(type);
        else
            return null;
    }

    private MethodDecl makeInitMethod(Position pos, Name fName, StaticFieldInfo fieldInfo,
        FieldDef fdExcept, FieldDef fdCond, X10ClassDef classDef) {
        // get MethodDef
        Name name = Name.make(initializerPrefix+fName);
        Type type = fieldInfo.fieldDef.type().get();
        MethodDef md = fieldInfo.methodDef;
        if (md == null) {
            md = makeMethodDef(pos, classDef.asType(), name, type);
        }

        // create a method declaration node
        List<TypeParamNode> typeParamNodes = Collections.<TypeParamNode>emptyList();
        List<Formal> formals = Collections.<Formal>emptyList();

        TypeNode returnType = xnf.X10CanonicalTypeNode(pos, type);
        Block body = makeInitMethodBody(pos, fieldInfo, fdExcept, fdCond, classDef);
        MethodDecl result = xnf.X10MethodDecl(pos, xnf.FlagsNode(pos, Flags.STATIC), returnType, xnf.Id(pos, name),
                                              typeParamNodes, formals, null, null, Collections.<TypeNode>emptyList(), body);
        // associate methodDef with methodDecl
        result = result.methodDef(md);
        return result;
    }

    private Catch genCatch(Position pos, FieldDef fdExcept, FieldDef fdCond, Name excName, X10ClassType excType, TypeNode receiver, Stmt throwExceptStmt) {
        LocalDef excDef = xts.localDef(pos, Flags.NONE, Types.ref(excType), excName);
        Formal excFormal = xnf.Formal(pos, xnf.FlagsNode(pos, excDef.flags()), xnf.CanonicalTypeNode(pos, excDef.type()), xnf.Id(pos, excDef.name())).localDef(excDef);

        List<Ref<? extends Type>> newExceptArgTypes = new ArrayList<Ref<? extends Type>>();
        newExceptArgTypes.add(Types.ref(xts.CheckedThrowable()));
        ConstructorDef cd = xts.constructorDef(pos, pos, Types.ref(fdExcept.asInstance().type().toClass()), Flags.NONE, newExceptArgTypes, Collections.<Ref<? extends Type>>emptyList());
        ConstructorInstance ci = xts.createConstructorInstance(pos, pos, Types.ref(cd));
        List<Expr> newExceptArgs = new ArrayList<Expr>();
        Expr excExpr = xnf.Local(pos, xnf.Id(pos, excDef.name())).localInstance(excDef.asInstance()).type(excDef.asInstance().type());
        newExceptArgs.add(excExpr);
        Expr newExceptExpr = xnf.New(pos, xnf.CanonicalTypeNode(pos, fdExcept.asInstance().type()), newExceptArgs).constructorInstance(ci).type(fdExcept.asInstance().type());
        Stmt storeExceptStmt = xnf.Eval(pos, xnf.FieldAssign(pos, receiver, xnf.Id(pos, fdExcept.name()), Assign.ASSIGN, newExceptExpr).fieldInstance(fdExcept.asInstance()).type(fdExcept.asInstance().type()));

        List<Stmt> catchStmts = new ArrayList<Stmt>();
        // gen exception = new x10.lang.ExceptionInInitializer(e);
        catchStmts.add(storeExceptStmt);
        // gen AtomicInteger.set(EXCEPTION_RAISED)
        catchStmts.add(xnf.Eval(pos, genStatusSetExcept(pos, receiver, fdCond)));
        // gen lockInitialized()
        catchStmts.add(xnf.Eval(pos, genLock(pos)));
        // gen notifyInitialized()
        catchStmts.add(xnf.Eval(pos, genNotify(pos)));
        // gen throw exception;
        catchStmts.add(throwExceptStmt);
        
        return xnf.Catch(pos, excFormal, xnf.Block(pos, catchStmts));
    }
    
    private Catch genCatchWithMessage(Position pos, FieldDef fdExcept, FieldDef fdCond, Name excName, X10ClassType excType, TypeNode receiver, Stmt throwExceptStmt) {
        LocalDef excDef = xts.localDef(pos, Flags.NONE, Types.ref(excType), excName);
        Formal excFormal = xnf.Formal(pos, xnf.FlagsNode(pos, excDef.flags()), xnf.CanonicalTypeNode(pos, excDef.type()), xnf.Id(pos, excDef.name())).localDef(excDef);

        List<Ref<? extends Type>> newExceptArgTypes = new ArrayList<Ref<? extends Type>>();
        newExceptArgTypes.add(Types.ref(xts.String()));
        ConstructorDef cd = xts.constructorDef(pos, pos, Types.ref(fdExcept.asInstance().type().toClass()), Flags.NONE, newExceptArgTypes, Collections.<Ref<? extends Type>>emptyList());
        ConstructorInstance ci = xts.createConstructorInstance(pos, pos, Types.ref(cd));
        List<Expr> newExceptArgs = new ArrayList<Expr>();
        MethodDef md = xts.methodDef(pos, pos, (Ref<? extends ContainerType>) excDef.type(), Flags.NONE, Types.ref(xts.String()), Name.make("getMessage"), Collections.<Ref<? extends Type>>emptyList(), Collections.<Ref<? extends Type>>emptyList());
        MethodInstance mi = xts.createMethodInstance(pos, pos, Types.ref(md));
        Expr excExpr = xnf.Local(pos, xnf.Id(pos, excDef.name())).localInstance(excDef.asInstance()).type(excDef.asInstance().type());
        Expr call = xnf.X10Call(pos, excExpr, xnf.Id(pos, mi.name()), Collections.<TypeNode>emptyList(), Collections.<Expr>emptyList()).methodInstance(mi).type(mi.returnType());
        newExceptArgs.add(call);
        Expr newExceptExpr = xnf.New(pos, xnf.CanonicalTypeNode(pos, fdExcept.asInstance().type()), newExceptArgs).constructorInstance(ci).type(fdExcept.asInstance().type());
        Stmt storeExceptStmt = xnf.Eval(pos, xnf.FieldAssign(pos, receiver, xnf.Id(pos, fdExcept.name()), Assign.ASSIGN, newExceptExpr).fieldInstance(fdExcept.asInstance()).type(fdExcept.asInstance().type()));

        List<Stmt> catchStmts = new ArrayList<Stmt>();
        // gen exception = new x10.lang.ExceptionInInitializer(e.getMessage());
        catchStmts.add(storeExceptStmt);
        // gen AtomicInteger.set(EXCEPTION_RAISED)
        catchStmts.add(xnf.Eval(pos, genStatusSetExcept(pos, receiver, fdCond)));
        // gen lockInitialized()
        catchStmts.add(xnf.Eval(pos, genLock(pos)));
        // gen notifyInitialized()
        catchStmts.add(xnf.Eval(pos, genNotify(pos)));
        // gen throw exception;
        catchStmts.add(throwExceptStmt);
        
        return xnf.Catch(pos, excFormal, xnf.Block(pos, catchStmts));
    }
    
    private Block makeInitMethodBody(Position pos, StaticFieldInfo initInfo, FieldDef fdExcept, FieldDef fdCond,
                                     X10ClassDef classDef) {

        List<Stmt> stmts;
        TypeNode receiver = xnf.X10CanonicalTypeNode(pos, classDef.asType());
        FieldInstance fi = initInfo.fieldDef.asInstance();
        Expr right = initInfo.right;
        Name name = initInfo.fieldDef.name();
        Expr left = xnf.Field(pos, receiver, xnf.Id(pos, name)).fieldInstance(fi).type(right.type());

        // gen if (AtomicInteger.get() == INITIALIZED) { return field; }
        stmts = new ArrayList<Stmt>();
        stmts.add(xnf.X10Return(pos, left, false));
        Stmt shortCutBlock = xnf.If(pos, genCheckInitialized(pos, receiver, fdCond, true), xnf.Block(pos, stmts));

        Stmt shortCutBlockExcept = null;
        Stmt throwExceptStmt = null;
        
        // gen if (AtomicInteger.get() == EXCEPTION_RAISED) { throw exception; }
        stmts = new ArrayList<Stmt>();
        if (!opts.x10_config.NO_TRACES && !opts.x10_config.OPTIMIZE) {
        	stmts.add(xnf.If(pos, genPrintStmtCheckGuard(pos), makePrintStmtExcept(pos, name, classDef)));
        }
        throwExceptStmt = xnf.Throw(pos, xnf.Field(pos, receiver, xnf.Id(pos, fdExcept.name())).fieldInstance(fdExcept.asInstance()).type(fdExcept.asInstance().type()));
        stmts.add(throwExceptStmt);
        shortCutBlockExcept = xnf.If(pos, genCheckExceptionRaised(pos, receiver, fdCond, true), xnf.Block(pos, stmts));

        // gen AtomicInteger.compareAndSet(UNINITIALIZED, INITIALIZING)
        Expr ifCond = genAtomicGuard(pos, receiver, fdCond);

        // make statement block of initialization
        stmts = new ArrayList<Stmt>();

        // if (stickyExceptionSemantics) surround with try
        Stmt fieldAssignStmt = xnf.Eval(pos, xnf.FieldAssign(pos, receiver, xnf.Id(pos, name), Assign.ASSIGN, right).fieldInstance(fi).type(right.type()));
        Name excName = Name.makeFresh("exc");
        List<Catch> catchBlocks = new ArrayList<Catch>();
        // gen catch (java.lang.Throwable exc) { exception = new x10.lang.ExceptionInInitializer(exc); AtomicInteger.set(EXCEPTION_RAISED); lockInitialized(); notifyInitialized(); throw exception; }
        catchBlocks.add(genCatch(pos, fdExcept, fdCond, excName, xts.CheckedThrowable(), receiver, throwExceptStmt));
        stmts.add(xnf.Try(pos, xnf.Block(pos, fieldAssignStmt), catchBlocks));

        if (!opts.x10_config.NO_TRACES && !opts.x10_config.OPTIMIZE) {
        	stmts.add(xnf.If(pos, genPrintStmtCheckGuard(pos), makePrintStmt(pos, name, classDef)));
        }

        stmts.add(xnf.Eval(pos, genStatusSet(pos, receiver, fdCond)));
        stmts.add(xnf.Eval(pos, genLock(pos)));
        stmts.add(xnf.Eval(pos, genNotify(pos)));
        Block initBody = xnf.Block(pos, stmts);

        // gen while(AtomicInteger.get() != INITIALIZED) { await(); }
        Expr initCheckCond = genCheckInitialized(pos, receiver, fdCond, Binary.LT);
        Block whileBody = xnf.Block(pos, xnf.Eval(pos, genAwait(pos)));

        // make statement block for waiting
        stmts =  new ArrayList<Stmt>();
        stmts.add(xnf.Eval(pos, genLock(pos)));
        stmts.add(xnf.While(pos, initCheckCond, whileBody));
        stmts.add(xnf.Eval(pos, genUnlock(pos)));
        stmts.add(shortCutBlockExcept);
        Block waitBody = xnf.Block(pos, stmts);

        // make statement block of the entire method body
        stmts =  new ArrayList<Stmt>();
        stmts.add(shortCutBlock);
        stmts.add(shortCutBlockExcept);

        // original
//        stmts.add(xnf.If(pos, initCheckCond, waitBody));
        // optimized
        stmts.add(xnf.If(pos, ifCond, initBody, xnf.If(pos, initCheckCond, waitBody)));
        stmts.add(xnf.X10Return(pos, left, false));
        Block body = xnf.Block(pos, stmts);
        return body;
    }

    private Expr genAtomicGuard(Position pos, TypeNode receiver, FieldDef fdCond) {
        FieldInstance fi = fdCond.asInstance();
        Expr ai = xnf.Field(pos, receiver, xnf.Id(pos, fdCond.name())).fieldInstance(fi).type(fi.type());
        Id cs = xnf.Id(pos, Name.make("compareAndSet"));

        List<Ref<? extends Type>> argTypes = new ArrayList<Ref<? extends Type>>();
        argTypes.add(Types.ref(xts.Int()));
        argTypes.add(Types.ref(xts.Int()));
        MethodDef md = xts.methodDef(pos, pos, Types.ref((ClassType) xts.AtomicInteger()),
                Flags.NONE, Types.ref(xts.Boolean()), cs.id(), argTypes, Collections.<Ref<? extends Type>>emptyList());
        MethodInstance mi = xts.createMethodInstance(pos, pos, Types.ref(md));

        List<Expr> args = new ArrayList<Expr>();
        args.add(getInitDispatcherConstant(pos, "UNINITIALIZED").type(xts.Int()));
        args.add(getInitDispatcherConstant(pos, "INITIALIZING").type(xts.Int()));
        List<TypeNode> typeParamNodes = new ArrayList<TypeNode>();
        typeParamNodes.add(xnf.CanonicalTypeNode(pos, xts.Int()));
        typeParamNodes.add(xnf.CanonicalTypeNode(pos, xts.Int()));
        Expr call = xnf.X10Call(pos, ai, cs, typeParamNodes, args).methodInstance(mi).type(xts.Boolean());
        return call;
    }

    private Expr genStatusSet(Position pos, TypeNode receiver, FieldDef fdCond) {
        return genStatusSet(pos, "INITIALIZED", receiver, fdCond);
    }

    private Expr genStatusSetExcept(Position pos, TypeNode receiver, FieldDef fdCond) {
        return genStatusSet(pos, "EXCEPTION_RAISED", receiver, fdCond);
    }

    private Expr genStatusSet(Position pos, String status, TypeNode receiver, FieldDef fdCond) {
        FieldInstance fi = fdCond.asInstance();
        Expr ai = xnf.Field(pos, receiver, xnf.Id(pos, fdCond.name())).fieldInstance(fi).type(fi.type());
        Id name = xnf.Id(pos, Name.make("set")); // Intentionally not SettableAssign.SET because AtomicInteger is a NativeRep class

        List<Ref<? extends Type>> argTypes = new ArrayList<Ref<? extends Type>>();
        argTypes.add(Types.ref(xts.Int()));
        MethodDef md = xts.methodDef(pos, pos, Types.ref((ClassType)xts.AtomicInteger()), 
                                     Flags.NONE, Types.ref(xts.Void()), name.id(), argTypes, Collections.<Ref<? extends Type>>emptyList());
        MethodInstance mi = xts.createMethodInstance(pos, pos, Types.ref(md));

        List<Expr> args = new ArrayList<Expr>();
        args.add(getInitDispatcherConstant(pos, status).type(xts.Int()));
        List<TypeNode> typeParamNodes = new ArrayList<TypeNode>();
        typeParamNodes.add(xnf.CanonicalTypeNode(pos, xts.Int()));
        Expr call = xnf.X10Call(pos, ai, name, typeParamNodes, args).methodInstance(mi).type(xts.Void());
        return call;
    }

    private Expr genCheckInitialized(Position pos, TypeNode receiver, FieldDef fdCond, boolean positive) {
        return genCheckInitialized(pos, receiver, fdCond, positive ? Binary.EQ : Binary.NE);
    }

    private Expr genCheckExceptionRaised(Position pos, TypeNode receiver, FieldDef fdCond, boolean positive) {
        return genCheckExceptionRaised(pos, receiver, fdCond, positive ? Binary.EQ : Binary.NE);
    }

    private Expr genCheckInitialized(Position pos, TypeNode receiver, FieldDef fdCond, Operator op) {
        return genCheck(pos, receiver, "INITIALIZED", fdCond, op);
    }

    private Expr genCheckExceptionRaised(Position pos, TypeNode receiver, FieldDef fdCond, Operator op) {
        return genCheck(pos, receiver, "EXCEPTION_RAISED", fdCond, op);
    }

    private Expr genCheck(Position pos, TypeNode receiver, String fieldName, FieldDef fdCond, Operator op) {
        FieldInstance fi = fdCond.asInstance();
        Expr ai = xnf.Field(pos, receiver, xnf.Id(pos, fdCond.name())).fieldInstance(fi).type(fi.type());
        Id name = xnf.Id(pos, Name.make("get"));

        List<Ref<? extends Type>> argTypes = Collections.<Ref<? extends Type>>emptyList();
        MethodDef md = xts.methodDef(pos, pos, Types.ref((ClassType)xts.AtomicInteger()), 
                                     Flags.NONE, Types.ref(xts.Int()), name.id(), argTypes, Collections.<Ref<? extends Type>>emptyList());
        MethodInstance mi = xts.createMethodInstance(pos, pos, Types.ref(md));

        List<Expr> args = Collections.<Expr>emptyList();
        List<TypeNode> typeParamNodes = Collections.<TypeNode>emptyList();
        Expr call = xnf.X10Call(pos, ai, name, typeParamNodes, args).methodInstance(mi).type(xts.Int());

        return xnf.Binary(pos, call, op, getInitDispatcherConstant(pos, fieldName).type(xts.Int())).type(xts.Boolean());
    }

    private Expr genLock(Position pos) {
        return callInitDispatcherMethodVoidNoarg(pos, Name.make("lockInitialized"));
    }

    private Expr genUnlock(Position pos) {
        return callInitDispatcherMethodVoidNoarg(pos, Name.make("unlockInitialized"));
    }

    private Expr genAwait(Position pos) {
        return callInitDispatcherMethodVoidNoarg(pos, Name.make("awaitInitialized"));
    }

    private Expr genNotify(Position pos) {
        return callInitDispatcherMethodVoidNoarg(pos, Name.make("notifyInitialized"));
    }

    private Expr callInitDispatcherMethodVoidNoarg(Position pos, Name methodName) {
        // create MethodDef
        Id id = xnf.Id(pos, methodName);
        List<Ref<? extends Type>> argTypes = Collections.<Ref<? extends Type>>emptyList();
        MethodDef md = xts.methodDef(pos, pos, Types.ref(InitDispatcher()), 
                                     Flags.NONE, Types.ref(xts.Void()), id.id(), argTypes, Collections.<Ref<? extends Type>>emptyList());
        MethodInstance mi = xts.createMethodInstance(pos, pos, Types.ref(md));

        // actual arguments
        List<Expr> args = Collections.<Expr>emptyList();
        List<TypeNode> typeParamNodes = Collections.<TypeNode>emptyList();
        Receiver receiver = xnf.CanonicalTypeNode(pos, InitDispatcher());
        Expr call = xnf.X10Call(pos, receiver, id, typeParamNodes, args).methodInstance(mi).type(xts.Void());
        return call;
    }

    private Expr getInitDispatcherConstant(Position pos, String name) {
        Id id = xnf.Id(pos, Name.make(name));
        FieldDef fd = xts.fieldDef(pos, Types.ref(InitDispatcher()), 
                                     Flags.STATIC, Types.ref(xts.Int()), id.id());
        FieldInstance fi = xts.createFieldInstance(pos, Types.ref(fd));
        Receiver receiver = xnf.CanonicalTypeNode(pos, InitDispatcher());
        return xnf.Field(pos, receiver, id).fieldInstance(fi);
    }

    private MethodDecl makeFakeInitMethod(Position pos, Name fName, StaticFieldInfo fieldInfo, X10ClassDef classDef) {
        // get MethodDef
        Name name = Name.make(initializerPrefix+fName);
        FieldInstance fi = fieldInfo.fieldDef.asInstance();
        MethodDef md = makeMethodDef(pos, classDef.asType(), name, fi.type());

        // create a method declaration node
        List<TypeParamNode> typeParamNodes = Collections.<TypeParamNode>emptyList();
        List<Formal> formals = Collections.<Formal>emptyList();

        // get field reference
        if (classDef.isMember() && classDef.outer().get().flags().isInterface())
            // should refer to fields in the outer interface
            classDef = (X10ClassDef)classDef.outer().get();
        TypeNode receiver = xnf.X10CanonicalTypeNode(pos, classDef.asType());
        Expr left = xnf.Field(pos, receiver, xnf.Id(pos, fieldInfo.fieldDef.name())).fieldInstance(fi).type(fi.type());

        // make statement block
        List<Stmt> stmts = new ArrayList<Stmt>();
        stmts.add(xnf.X10Return(pos, left, false));
        Block body = xnf.Block(pos, stmts);

        // create method declaration
        TypeNode returnType = xnf.X10CanonicalTypeNode(pos, fi.type());
        MethodDecl result = xnf.X10MethodDecl(pos, xnf.FlagsNode(pos, Flags.STATIC), returnType, xnf.Id(pos, name), 
                                              typeParamNodes, formals, null, null, Collections.<TypeNode>emptyList(), body);
        // associate methodDef with methodDecl
        result = result.methodDef(md);
        return result;
    }

    private ClassType X10JavaSerializable_;
    private ClassType X10JavaSerializable() {
        if (X10JavaSerializable_ == null)
            X10JavaSerializable_ = xts.load("x10.compiler.X10JavaSerializable");
        return X10JavaSerializable_;
    }

    private ClassType X10JavaDeserializer_;
    private ClassType X10JavaDeserializer() {
        if (X10JavaDeserializer_ == null)
            X10JavaDeserializer_ = xts.load("x10.compiler.X10JavaDeserializer");
        return X10JavaDeserializer_;
    }

    private ClassType InitDispatcher_;
    private ClassType InitDispatcher() {
        if (InitDispatcher_ == null)
            InitDispatcher_ = xts.load("x10.compiler.InitDispatcher");
        return InitDispatcher_;
    }

    private ClassType PlaceLocalHandle_;
    private ClassType PlaceLocalHandle() {
        if (PlaceLocalHandle_ == null)
            PlaceLocalHandle_ = xts.load("x10.compiler.PlaceLocalHandle");
        return PlaceLocalHandle_;
    }

    private ClassType ExceptionInInitializer_;
    private ClassType ExceptionInInitializer() {
        if (ExceptionInInitializer_ == null)
            ExceptionInInitializer_ = xts.load("x10.lang.ExceptionInInitializer");
        return ExceptionInInitializer_;
    }

    private Stmt makePrintStmt(Position pos, Name fieldName, X10ClassDef classDef) {
        // get fully qualified field name
        String fullName = getPackageName(classDef) + getClassName(classDef) + "." + Emitter.mangleToJava(fieldName);
        return makePrintStmt(pos, "Doing static initialization for field: " + fullName, classDef);
    }
    
    private Stmt makePrintStmtExcept(Position pos, Name fieldName, X10ClassDef classDef) {
        // get fully qualified field name
        String fullName = getPackageName(classDef) + getClassName(classDef) + "." + Emitter.mangleToJava(fieldName);
        return makePrintStmt(pos, "Rethrowing ExceptionInInitializer for field: " + fullName, classDef);
    }

    private Stmt makePrintStmt(Position pos, String message, X10ClassDef classDef) {
        Id id = xnf.Id(pos, Name.make("printStaticInitMessage"));

        // argument type
        List<Ref<? extends Type>> argTypes = new ArrayList<Ref<? extends Type>>();
        argTypes.add(Types.ref(xts.String()));

        // create MethodDef
        MethodDef md = xts.methodDef(pos, pos, Types.ref(InitDispatcher()),
                                     Flags.NONE, Types.ref(xts.Void()), id.id(), argTypes, Collections.<Ref<? extends Type>>emptyList());
        MethodInstance mi = xts.createMethodInstance(pos, pos,Types.ref(md));

        // actual arguments
        List<Expr> args = new ArrayList<Expr>();
        args.add(xnf.StringLit(pos, message).type(xts.String()));

        List<TypeNode> typeParamNodes = new ArrayList<TypeNode>();
        typeParamNodes.add(xnf.CanonicalTypeNode(pos, xts.String()));
        TypeNode receiver = xnf.CanonicalTypeNode(pos, InitDispatcher());

        return xnf.Eval(pos, xnf.X10Call(pos, receiver, id, typeParamNodes, args).methodInstance(mi).type(xts.Void()));
    }

    private Expr genPrintStmtCheckGuard(Position pos) {
       Id name = xnf.Id(pos, Name.make("TRACE_STATIC_INIT"));

        FieldDef fieldDef = xts.fieldDef(pos, Types.ref(InitDispatcher()), Flags.STATIC, Types.ref(xts.Boolean()), name.id());
        X10FieldInstance fi = xts.createFieldInstance(pos, Types.ref(fieldDef));
        Receiver receiver = xnf.CanonicalTypeNode(pos, InitDispatcher());
        Expr left = xnf.Field(pos, receiver, name).fieldInstance(fi);
        return xnf.Binary(pos, left.type(xts.Boolean()), Binary.EQ, xnf.BooleanLit(pos, true).type(xts.Boolean()));
    }

    private String getClassName(ClassDef classDef) {
        String name = classDef.name().toString();
        if (classDef.isNested()) {
            ClassDef outer = Types.get(classDef.outer());
            if (outer != null)
                // get outer's name recursively
                name = getClassName(outer) +'$' + name;
        }
        return name;
    }

    private String getPackageName(ClassDef classDef) {
        if (classDef.isNested())
            return getPackageName(Types.get(classDef.outer()));
        Package p = Types.get(classDef.package_());
        return (p != null ? p.toString() + "." : "");
    }

    private StaticFieldInfo getFieldEntry(Type target, Name name) {
        Pair<Type,Name> key = new Pair<Type,Name>(target, name);
        StaticFieldInfo fieldInfo = staticFinalFields.get(key);
        if (fieldInfo == null) {
            fieldInfo = new StaticFieldInfo();
            staticFinalFields.put(key, fieldInfo);
        }
        return fieldInfo;
    }

    private boolean isGlobalInit(Expr e) {
        // N.B. Process safe cast as constant  
        if (e instanceof Cast)
            return isConstantExpression(e);
        if (e.type().isNumeric() || e.type().isBoolean() || e.type().isChar() || e.type().isNull())
            return isConstantExpression(e);
        if (e.type().isString())
            return isStringConstant(e);
        return false;
    }

    private static boolean isSafeCast(Cast c, Context context) {
        return c.expr().type().isSubtype(c.castType().type(), context);
    }
    /**
     * from x10cpp.visit.ASTQuery
     */
    private boolean isConstantExpression(Expr e) {
        // N.B. Need to check first because NullLit_c.isConstant() returns false to workaround constant propagator's bug! 
        if (e instanceof NullLit)
            return true;
        // N.B. Process safe cast as constant  
        if (e instanceof Cast) {
            Cast c = (Cast) e;
            return isConstantExpression(c.expr()) && isSafeCast(c, context);
        }
        if (!e.isConstant())
            return false;
        if (e instanceof BooleanLit)
            return true;
        if (e instanceof IntLit)
            return true;
        if (e instanceof FloatLit)
            return true;
        if (e instanceof CharLit)
            return true;
        if (e instanceof StringLit)
            return true;
        // N.B. Process safe cast as constant  
//        if (e instanceof Cast)
//            return isConstantExpression(((Cast) e).expr());
        if (e instanceof ParExpr)
            return isConstantExpression(((ParExpr) e).expr());
        if (e instanceof Unary)
            return isConstantExpression(((Unary) e).expr());
        if (e instanceof Binary)
            return isConstantExpression(((Binary) e).left()) &&
                   isConstantExpression(((Binary) e).right());
        if (e instanceof Conditional)
            return isConstantExpression(((Conditional) e).cond()) &&
                   isConstantExpression(((Conditional) e).consequent()) &&
                   isConstantExpression(((Conditional) e).alternative());
        if (e instanceof Closure) {
            Closure c = (Closure) e;
            List<Stmt> ss = c.body().statements();
            if (ss.size() != 1)
                return false;
            if (!(ss.get(0) instanceof Return))
                return false;
            return isConstantExpression(((Return) ss.get(0)).expr());
        }
        if (e instanceof ClosureCall) {
            ClosureCall cc = (ClosureCall) e;
            List<Expr> as = ((ClosureCall) e).arguments();
            for (Expr a : as) {
                if (!isConstantExpression(a))
                    return false;
            }
            return isConstantExpression(cc.target());
        }
        if (e instanceof Call) {
            Call c = (Call) e;
            return c.isConstant();
        }
        return false;
    }

    private boolean isStringConstant(Expr e) {
        if (!e.isConstant())
            return false;
        if (e instanceof StringLit)
            return true;
        if (e instanceof X10Call) {
            // check if this is string manipulation (e.g. concatenation)
            X10Call call = (X10Call)e;
            List<Expr> args = call.arguments();
            for (Expr arg : args) {
                if (!isStringConstant(arg))
                    return false;
            }
            Type targetType = call.target().type();
            if (targetType instanceof ConstrainedType)
                targetType = ((ConstrainedType)targetType).baseType().get();
            return targetType.isString();
        }
        return false;
    }

    private boolean checkFieldRefReplacementRequired(X10Field_c f) {
    	if (f.target().type().toClass().isJavaType()) return false;

        if (f.target().type().isNumeric())
            // @NativeRep class should be excluded
            return false;

        if (f.isConstant())
            return false;

        if (isConstraintToLiteral(f.type()))
            return false;

        Pair<Type,Name> key = new Pair<Type,Name>(f.target().type(), f.name().id());
        StaticFieldInfo fieldInfo = staticFinalFields.get(key);
        // not yet registered, or registered as replacement required
        return fieldInfo == null || fieldInfo.right != null || fieldInfo.methodDef != null;
    }

    private boolean isConstraintToLiteral(Type type) {
        if (type instanceof ConstrainedType) {
            // check if self is bound to a constant
            ConstrainedType ct = (ConstrainedType)(type);
            if (ct.constraint().known()) {
                CConstraint cc = ct.constraint().get();
                XTerm selfVar = cc.selfVarBinding();
                if (selfVar != null && selfVar.isLit())
                    return true;
            }
        }
        return false;
    }

    static class StaticFieldInfo {
        Expr right;             // RHS expression, if replaced with initialization method
        MethodDef methodDef;    // getInitialized methodDef to be replaced
        FieldDef fieldDef;
        FieldDecl left;         // field declaration to be moved from interface to a shadow class
    }

    protected boolean isPerProcess(X10Def def) {
        try {
            Type t = xts.systemResolver().findOne(QName.make("x10.compiler.PerProcess"));
            return !def.annotationsMatching(t).isEmpty();
        } catch (SemanticException e) {
            return false;
        }
    }
}
