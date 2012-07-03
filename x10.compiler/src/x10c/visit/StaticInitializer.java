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
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import polyglot.ast.Assign;
import polyglot.ast.Binary;
import polyglot.ast.Block;
import polyglot.ast.BooleanLit;
import polyglot.ast.Call;
import polyglot.ast.Cast;
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

    // WIP XTENLANG-3081
    private static final boolean stickyExceptionSemantics = false;

    private final X10CTypeSystem_c xts;
    private final X10CNodeFactory_c xnf;
    private final WeakHashMap<X10ProcedureDef,ProcedureDecl> procDeclCache;
    private final WeakHashMap<Block,Boolean> procBodyCache;

    // N.B. must be sync with StaticInitializer.java and InitDispatcher.java
    public static final String initializerPrefix = "get$";
    public static final String deserializerPrefix = "getDeserialized$";

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
            // create a new member list for initializer/deserializer methods of each static field
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
        List<Stmt> initStmts = new ArrayList<Stmt>();

        for (Map.Entry<Pair<Type,Name>, StaticFieldInfo> entry : staticFinalFields.entrySet()) {
            Name fName = entry.getKey().snd();
            StaticFieldInfo fieldInfo = entry.getValue();

            if (fieldInfo.right == null && fieldInfo.fieldDef == null)
                continue;

            MethodDecl md = null; 
            if (fieldInfo.right != null) {
                FieldDecl fdPLH = null;
//                if (!opts.x10_config.MULTI_NODE) {
//                    // create PlaceLocalHandle for SingleVM MultiPlace support
//                    fdPLH = makeFieldVar4PLH(CG, fName, classDef);
//                    classDef.addField(fdPLH.fieldDef());
//                    // add in the top
//                    members.add(0, fdPLH);
//                }

                // gen new field var
                FieldDecl fdExcept = null;
                if (stickyExceptionSemantics) {
                fdExcept = makeFieldVar4Except(CG, fName, classDef);
                classDef.addField(fdExcept.fieldDef());
                // add in the top
                members.add(0, fdExcept);
                }

                FieldDecl fdCond = makeFieldVar4Guard(CG, fName, classDef);
                classDef.addField(fdCond.fieldDef());
                // add in the top
                members.add(0, fdCond);

                FieldDecl fdId = makeFieldVar4Id(CG, fName, classDef);
                classDef.addField(fdId.fieldDef());
                // add in the top
                members.add(0, fdId);

                if (fieldInfo.left != null) {
                    // interface case: add field declaration to the shadow class
                    FieldDef fd = fieldInfo.left.fieldDef();
                    Flags newFlags = fd.container().get().toClass().flags().clearInterface();
                    FieldDef newFd = xts.fieldDef(CG, Types.ref(classDef.asType()), newFlags, fd.type(), fd.name());
                    members.add(0, fieldInfo.left.fieldDef(newFd));
                }

                // gen new deserialize method and add in the bottom of the member list
//                md = makeDeserializeMethod(CG, fName, fieldInfo, fdCond.fieldDef(), classDef);
//                classDef.addMethod(md.methodDef());
//                members.add(md);

                // gen new initialize method
                if (stickyExceptionSemantics) {
                md = makeInitMethod(CG, fName, fieldInfo, fdExcept.fieldDef(), fdCond.fieldDef(), fdId.fieldDef(), fdPLH, classDef);
                } else {
                md = makeInitMethod(CG, fName, fieldInfo, null, fdCond.fieldDef(), fdId.fieldDef(), fdPLH, classDef);
                }

                // register in the table for x10-level static initialization later
//                initStmts.add(makeAddInitializer(CG, fieldInfo.fieldDef.name(), fdId.fieldDef(), classDef));

            } else {
                // gen a fake initialization method
                md = makeFakeInitMethod(CG, fName, fieldInfo, classDef);
            }
            classDef.addMethod(md.methodDef());
            // add in the bottom
            members.add(md);
        }

        if (!initStmts.isEmpty()) {
            // gen initializer block
            Block initBlockBody = xnf.Block(CG, initStmts);
            Initializer initBlock = xnf.Initializer(CG, xnf.FlagsNode(CG, Flags.STATIC), initBlockBody);
            // create InitializerDef
            InitializerDef id = xts.initializerDef(CG, Types.ref(classDef.asType()), Flags.STATIC);
            initBlock = initBlock.initializerDef(id);
            members.add(initBlock);
        }
        return members;
    }

    private X10ClassDecl createNestedShadowClass(ClassDecl_c interfaceClass) {
        // create ClassDef first
        X10ClassDef cDef = createShadowClassDef(interfaceClass.classDef());

        // create ClassDecl
        Position CG = Position.compilerGenerated(null);
        FlagsNode fNode = xnf.FlagsNode(CG, cDef.flags());
        Id id = xnf.Id(CG, cDef.name());
        TypeNode superTN = (TypeNode) xnf.CanonicalTypeNode(CG, cDef.superType());
        List<ClassMember> cmembers = new ArrayList<ClassMember>();
        ClassBody body = xnf.ClassBody(CG, cmembers);
        List<TypeNode> interfaceTN = Collections.<TypeNode>emptyList();
        X10ClassDecl cDecl = (X10ClassDecl) xnf.ClassDecl(CG, fNode, id, superTN, interfaceTN, 
                                                          body).classDef(cDef);
        return cDecl;
    }

    private X10ClassDef createShadowClassDef(X10ClassDef interfaceClassDef) {
        X10ClassDef cDef = (X10ClassDef) xts.createClassDef(interfaceClassDef.sourceFile());
        cDef.superType(Types.ref(xts.Any()));
        List<Ref<? extends Type>> interfacesRef = Collections.<Ref<? extends Type>>emptyList();
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
                            // drop final
                            // System.out.println("RHS of FieldDecl replaced: "+ct.classDef()+"."+fd.fieldDef().name());
                            FlagsNode fn = xnf.FlagsNode(fd.position(), flags.clearFinal());
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
                }
                if (n instanceof X10Call_c) {
                    X10Call call = (X10Call)n;
                    MethodInstance mi = call.methodInstance();
                    if (!mi.container().isClass() || call.target().type().isNumeric()) { 
                        // allow method calls on non-objects or numerics
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
                    } else {
                        // deep analysis disabled
                        found.set(true);
                        return n;
                    }
//                    else if (!opts.x10_config.MULTI_NODE && checkMultiplexRequiredSingleVM(ci)) {
//                        found.set(true);
//                    }
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

    private boolean checkMultiplexRequiredSingleVM(X10ConstructorInstance ci) {
        X10ConstructorDef cd = ci.x10Def();
        X10ClassType containerBase = (X10ClassType) Types.get(cd.container());
        X10ClassDef container = containerBase.x10Def();
        if (container == null)
            return false;
        String containerName = container.toString();
        if (containerName.startsWith("x10.io"))
            return false;
        if (containerName.equals("x10.lang.PlaceLocalHandle") || containerName.endsWith("x10.lang.Place"))
            return false;
        return true;
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
            public Node override(Node n) {
                if (found.get())
                    // already found
                    return n;
                if (n instanceof X10Call) {
                    X10Call call = (X10Call)n;
                    MethodInstance mi = call.methodInstance();
                    if (!mi.container().isClass() || call.target().type().isNumeric()) { 
                        // allow method calls on non-objects or numerics
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
//                    else if (!opts.x10_config.MULTI_NODE && checkMultiplexRequiredSingleVM(ci)) {
//                        found.set(true);
//                    }
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
                                     Flags.STATIC, Types.ref(returnType), name, argTypes);
        return md;
    }

    private FieldDecl makeFieldVar4PLH(Position pos, Name fName, X10ClassDef classDef) {
        // make FieldDef of PlaceLocalHandle
        ClassType type = PlaceLocalHandle();
        Flags flags = Flags.PRIVATE.Static();

        Name name = Name.make("plh$"+fName);
        FieldDef fd = xts.fieldDef(pos, Types.ref(classDef.asType()), flags, Types.ref(type), name); 
        FieldInstance fi = xts.createFieldInstance(pos, Types.ref(fd));

        // create the field declaration node
        TypeNode tn = xnf.X10CanonicalTypeNode(pos, type);
        FieldDecl result = xnf.FieldDecl(pos, xnf.FlagsNode(pos, flags), tn, xnf.Id(pos, name));
        result = result.fieldDef(fd);
        return result;
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

    private FieldDecl makeFieldVar4Id(Position pos, Name fName, X10ClassDef classDef) {
        // make FieldDef
        Type type = xts.Short();
        Name name = Name.make("fieldId$"+fName);
        Flags flags = Flags.PRIVATE.Static();

        FieldDef fd = xts.fieldDef(pos, Types.ref(classDef.asType()), flags, Types.ref(type), name); 
        FieldInstance fi = xts.createFieldInstance(pos, Types.ref(fd));

        // create the field declaration node
        TypeNode tn = xnf.X10CanonicalTypeNode(pos, type);
        FieldDecl result = xnf.FieldDecl(pos, xnf.FlagsNode(pos, flags), tn, xnf.Id(pos, name));
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
        else if (type.isUByte() || type.isUShort() || type.isUInt())
            return xnf.IntLit(pos, IntLit.UINT, 0).type(type);
        else if (type.isULong())
            return xnf.IntLit(pos, IntLit.ULONG, 0).type(type);
        else if (type.isFloat())
            return xnf.FloatLit(pos, FloatLit.FLOAT, 0.0).type(type);
        else if (type.isDouble())
            return xnf.FloatLit(pos, FloatLit.DOUBLE, 0.0).type(type);
        else if (type == xts.String())
            return xnf.NullLit(pos).type(type);
        else
            return null;
    }

    private MethodDecl makeDeserializeMethod(Position pos, Name fName, StaticFieldInfo fieldInfo, 
                                             FieldDef fdCond, X10ClassDef classDef) {
        // get MethodDef
        Name name = Name.make(deserializerPrefix+fName);
        List<Ref<? extends Type>> argTypes = new ArrayList<Ref<? extends Type>>();
        argTypes.add(Types.ref(X10JavaDeserializer()));
        MethodDef md = xts.methodDef(pos, pos, Types.ref(classDef.asType()), 
                                     Flags.STATIC, Types.ref(xts.Void()), name, argTypes);
        MethodInstance mi = xts.createMethodInstance(pos, pos, Types.ref(md));

        // X10JavaDeserializer argument definition
        Name deserializerName = Name.make("deserializer");

        // create a method declaration node
        List<TypeParamNode> typeFormals = Collections.<TypeParamNode>emptyList();
        List<Formal> formals = new ArrayList<Formal>();
        LocalDef argDef = xts.localDef(pos, Flags.NONE, Types.ref(X10JavaDeserializer()), deserializerName);
        Formal fArg = xnf.Formal(pos, xnf.FlagsNode(pos, Flags.NONE),
                xnf.CanonicalTypeNode(pos, X10JavaDeserializer()), xnf.Id(pos, deserializerName)).localDef(argDef);
        formals.add(fArg);

        TypeNode returnType = xnf.X10CanonicalTypeNode(pos, xts.Void());
        Block body = makeDeserializeMethodBody(pos, fieldInfo, fdCond, classDef, deserializerName);
        MethodDecl result = xnf.X10MethodDecl(pos, xnf.FlagsNode(pos, Flags.STATIC), returnType, xnf.Id(pos, name), 
                                              typeFormals, formals, null, null, body);
        // associate methodDef with methodDecl
        result = result.methodDef(md);
        return result;
    }

    private Block makeDeserializeMethodBody(Position pos, StaticFieldInfo initInfo, FieldDef fdCond, 
                                            X10ClassDef classDef, Name deserializerName) {
        TypeNode receiver = xnf.X10CanonicalTypeNode(pos, classDef.asType());

        FieldInstance fi = initInfo.fieldDef.asInstance();
        Name name = initInfo.fieldDef.name();

        Expr rightCustomSerialization = genDeserializeField(pos, deserializerName, fi.type(), true);
        Stmt deserializeFieldCustomSerializationBlock = xnf.Eval(pos, xnf.FieldAssign(pos, receiver, xnf.Id(pos, name), Assign.ASSIGN, rightCustomSerialization).fieldInstance(fi).type(rightCustomSerialization.type()));
        // not needed
//        deserializeFieldCustomSerializationBlock = xnf.Block(pos, deserializeFieldCustomSerializationBlock);

        // make statement block
        List<Stmt> stmts = new ArrayList<Stmt>();
        stmts.add(deserializeFieldCustomSerializationBlock);

        stmts.add(xnf.Eval(pos, genStatusSet(pos, receiver, fdCond)));
        stmts.add(xnf.Eval(pos, genLock(pos)));
        stmts.add(xnf.Eval(pos, genNotify(pos)));
        Block body = xnf.Block(pos, stmts);
        return body;
    }

    private Expr genDeserializeField(Position pos, Name baName, Type type, boolean customSerialization) {
        String str;
        Id id;
        if (customSerialization && type.toClass().isJavaType()) {
            id = xnf.Id(pos, Name.make("deserializeFieldUsingReflection"));
        } else if (customSerialization && (str = X10PrettyPrinterVisitor.needsCasting(type)) != null) {
            id = xnf.Id(pos, Name.make("deserialize" + str));
        } else {
            id = xnf.Id(pos, Name.make("deserializeField"));
        }

        // create MethodDef
        List<Ref<? extends Type>> argTypes = new ArrayList<Ref<? extends Type>>();
        argTypes.add(Types.ref(X10JavaDeserializer()));
        MethodDef md = xts.methodDef(pos, pos, Types.ref(InitDispatcher()), 
                                     Flags.NONE, Types.ref(xts.Object()), id.id(), argTypes);
        MethodInstance mi = xts.createMethodInstance(pos, pos, Types.ref(md));

        // actual arguments
        List<Expr> args = new ArrayList<Expr>();
        LocalDef ldef = xts.localDef(pos, Flags.NONE, Types.ref(X10JavaDeserializer()), baName);
        Local arg = (Local)xnf.Local(pos, xnf.Id(pos, baName)).localInstance(ldef.asInstance()).type(X10JavaDeserializer());
        args.add(arg);

        List<TypeNode> typeParamNodes = new ArrayList<TypeNode>();
        typeParamNodes.add(xnf.CanonicalTypeNode(pos, X10JavaDeserializer()));
        Receiver receiver = xnf.CanonicalTypeNode(pos, InitDispatcher());
        Expr call = xnf.X10Call(pos, receiver, id, typeParamNodes, args).methodInstance(mi).type(xts.Object());
        return call;
    }

    private MethodDecl makeInitMethod(Position pos, Name fName, StaticFieldInfo fieldInfo,
        FieldDef fdExcept, FieldDef fdCond, FieldDef fdId, FieldDecl fdPLH, X10ClassDef classDef) {
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
        Block body = makeInitMethodBody(pos, fieldInfo, fdExcept, fdCond, fdId, fdPLH, classDef);
        MethodDecl result = xnf.X10MethodDecl(pos, xnf.FlagsNode(pos, Flags.STATIC), returnType, xnf.Id(pos, name),
                                              typeParamNodes, formals, null, null, body);
        // associate methodDef with methodDecl
        result = result.methodDef(md);
        return result;
    }

    private Block makeInitMethodBody(Position pos, StaticFieldInfo initInfo, FieldDef fdExcept, FieldDef fdCond,
                                     FieldDef fdId, FieldDecl fdPLH, X10ClassDef classDef) {

        List<Stmt> stmts;
        TypeNode receiver = xnf.X10CanonicalTypeNode(pos, classDef.asType());
        FieldInstance fi = initInfo.fieldDef.asInstance();
        Expr right = initInfo.right;
        Name name = initInfo.fieldDef.name();
        Expr left = xnf.Field(pos, receiver, xnf.Id(pos, name)).fieldInstance(fi).type(right.type());

        // gen if (AtomicInteger.get() == INITIALIZED) { return field; }
        stmts = new ArrayList<Stmt>();
        stmts.add(xnf.X10Return(pos, (fdPLH == null) ? left : genApplyPLH(pos, receiver, fdPLH, right.type(), stmts), false));
        Stmt shortCutBlock = xnf.If(pos, genCheckInitialized(pos, receiver, fdCond, true), xnf.Block(pos, stmts));

        Stmt shortCutBlockExcept = null;
        if (stickyExceptionSemantics) {
        // gen if (AtomicInteger.get() == EXCEPTION_RAISED) { throw except; }
        stmts = new ArrayList<Stmt>();
        stmts.add(xnf.If(pos, genPrintStmtCheckGuard(pos), makePrintStmtExcept(pos, name, classDef)));
        stmts.add(xnf.Throw(pos, xnf.Field(pos, receiver, xnf.Id(pos, fdExcept.name())).fieldInstance(fdExcept.asInstance()).type(fdExcept.asInstance().type())));
        shortCutBlockExcept = xnf.If(pos, genCheckExceptionRaised(pos, receiver, fdCond, true), xnf.Block(pos, stmts));
        }

        // gen AtomicInteger.compareAndSet(UNINITIALIZED, INITIALIZING)
        Expr ifCond = genAtomicGuard(pos, receiver, fdCond);
        FieldInstance fdidi = fdId.asInstance();
        Expr fieldId = xnf.Field(pos, receiver, xnf.Id(pos, fdId.name())).fieldInstance(fdidi).type(fdidi.type());
        Expr bcastCall = genBroadcastField(pos, left, fieldId, fdPLH, false);
        Expr bcastCallCustomSerialization = genBroadcastField(pos, left, fieldId, fdPLH, true);

        Stmt broadcastCustomSerializationBlock;

        if (fdPLH == null) {
            // no return value
            broadcastCustomSerializationBlock = xnf.Eval(pos, bcastCallCustomSerialization);
        } else {
            // assign return value from broadcast to PlaceLocalHandle
            Expr plhCustomSerialization = xnf.FieldAssign(pos, receiver, fdPLH.name(), Assign.ASSIGN,
                                       bcastCallCustomSerialization).fieldInstance(fdPLH.fieldDef().asInstance()).type(bcastCallCustomSerialization.type());
            broadcastCustomSerializationBlock = xnf.Eval(pos, plhCustomSerialization);
        }
        // not needed
//        broadcastCustomSerializationBlock = xnf.Block(pos, broadcastCustomSerializationBlock);

        // make statement block of initialization
        stmts = new ArrayList<Stmt>();

        // TODO if (stickyExceptionSemantics) surround with try
        stmts.add(xnf.Eval(pos, xnf.FieldAssign(pos, receiver, xnf.Id(pos, name), Assign.ASSIGN, 
                                                right).fieldInstance(fi).type(right.type())));

        stmts.add(xnf.If(pos, genPrintStmtCheckGuard(pos), makePrintStmt(pos, name, classDef)));
        // If the type is a java type we can do plain java serialization

//        stmts.add(broadcastCustomSerializationBlock);

        stmts.add(xnf.Eval(pos, genStatusSet(pos, receiver, fdCond)));
        stmts.add(xnf.Eval(pos, genLock(pos)));
        stmts.add(xnf.Eval(pos, genNotify(pos)));
        Block initBody = xnf.Block(pos, stmts);

        // gen while(AtomicInteger.get() != INITIALIZED) { await(); }
        Expr initCheckCond = genCheckInitialized(pos, receiver, fdCond, false);
        Block whileBody = xnf.Block(pos, xnf.Eval(pos, genAwait(pos)));

        // make statement block for waiting
        stmts =  new ArrayList<Stmt>();
        stmts.add(xnf.Eval(pos, genLock(pos)));
        stmts.add(xnf.While(pos, initCheckCond, whileBody));
        stmts.add(xnf.Eval(pos, genUnlock(pos)));
        Block waitBody = xnf.Block(pos, stmts);

        // gen x10.lang.Runtime.hereInt() == 0
        Expr placeCheck = genPlaceCheckGuard(pos);

        // make statement block of the entire method body
        stmts =  new ArrayList<Stmt>();
        stmts.add(shortCutBlock);
        if (stickyExceptionSemantics) {
        stmts.add(shortCutBlockExcept);
        }
        // original
//        stmts.add(xnf.If(pos, placeCheck, xnf.If(pos, ifCond, initBody)));
//        stmts.add(xnf.If(pos, initCheckCond, waitBody));
        // optimized
        stmts.add(xnf.If(pos, ifCond, initBody, xnf.If(pos, initCheckCond, waitBody)));
        Expr returnVal = (fdPLH == null) ? left : genApplyPLH(pos, receiver, fdPLH, right.type(), stmts); 
        stmts.add(xnf.X10Return(pos, returnVal, false));
        Block body = xnf.Block(pos, stmts);
        return body;
    }

    private Expr genApplyPLH(Position pos, Receiver receiver, FieldDecl fdPLH, Type returnType,
                             List<Stmt> stmts) {
        Name name = Name.make("$apply$G");

        FieldInstance fi = fdPLH.fieldDef().asInstance();
        Expr target = xnf.Field(pos, receiver, fdPLH.name()).fieldInstance(fi).type(fdPLH.declType());

        // make instance call
        ObjectType targetType = (ObjectType)target.type();
        MethodDef md = xts.methodDef(pos, pos, Types.ref(targetType), Flags.NONE, Types.ref(xts.Object()), name, 
                                     Collections.<Ref<? extends Type>>emptyList());
        MethodInstance mi = xts.createMethodInstance(pos, pos, Types.ref(md));
        Expr applyCall = xnf.X10Call(pos, target, xnf.Id(pos, name), Collections.<TypeNode>emptyList(), 
                                     Collections.<Expr>emptyList()).methodInstance(mi).type(xts.Object());

        // assign to local var
        Name lname = Name.make("initVal");
        LocalDef ldef = xts.localDef(pos, Flags.NONE, Types.ref(returnType), lname);
        TypeNode tn = xnf.X10CanonicalTypeNode(pos, returnType);
        LocalDecl ldecl = xnf.LocalDecl(pos, xnf.FlagsNode(pos, Flags.NONE), tn, xnf.Id(pos, lname),
                                        applyCall).localDef(ldef);
        stmts.add(ldecl);
        // associate localDef with localDecl
        Local l = (Local)xnf.Local(pos, xnf.Id(pos, lname)).localInstance(ldef.asInstance()).type(returnType);
        return l;
    }

    private Expr genPlaceCheckGuard(Position pos) {
        ClassType type = (ClassType)xts.Runtime();
        Id name = xnf.Id(pos, Name.make("hereInt"));

        MethodDef md = xts.methodDef(pos, pos, Types.ref(type), Flags.STATIC, Types.ref(xts.Int()), name.id(),
                                     Collections.<Ref<? extends Type>>emptyList());
        MethodInstance mi = xts.createMethodInstance(pos, pos, Types.ref(md));
        Expr here = xnf.X10Call(pos, xnf.X10CanonicalTypeNode(pos, type), name, 
                                Collections.<TypeNode>emptyList(), 
                                Collections.<Expr>emptyList()).methodInstance(mi).type(xts.Int());
        Expr placeCheck = xnf.Binary(pos, here, Binary.EQ, xnf.IntLit(pos, IntLit.INT, 0).type(xts.Int())).type(xts.Boolean());
        return placeCheck;
    }

    private Expr genAtomicGuard(Position pos, TypeNode receiver, FieldDef fdCond) {
        FieldInstance fi = fdCond.asInstance();
        Expr ai = xnf.Field(pos, receiver, xnf.Id(pos, fdCond.name())).fieldInstance(fi).type(fi.type());
        Id cs = xnf.Id(pos, Name.make("compareAndSet"));

        List<Ref<? extends Type>> argTypes = new ArrayList<Ref<? extends Type>>();
        argTypes.add(Types.ref(xts.Int()));
        argTypes.add(Types.ref(xts.Int()));
        MethodDef md = xts.methodDef(pos, pos, Types.ref((ClassType) xts.AtomicInteger()),
                Flags.NONE, Types.ref(xts.Boolean()), cs.id(), argTypes);
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
        FieldInstance fi = fdCond.asInstance();
        Expr ai = xnf.Field(pos, receiver, xnf.Id(pos, fdCond.name())).fieldInstance(fi).type(fi.type());
        Id name = xnf.Id(pos, Name.make("set")); // Intentionally not SettableAssign.SET because AtomicInteger is a NativeRep class

        List<Ref<? extends Type>> argTypes = new ArrayList<Ref<? extends Type>>();
        argTypes.add(Types.ref(xts.Int()));
        MethodDef md = xts.methodDef(pos, pos, Types.ref((ClassType)xts.AtomicInteger()), 
                                     Flags.NONE, Types.ref(xts.Void()), name.id(), argTypes);
        MethodInstance mi = xts.createMethodInstance(pos, pos, Types.ref(md));

        List<Expr> args = new ArrayList<Expr>();
        args.add(getInitDispatcherConstant(pos, "INITIALIZED").type(xts.Int()));
        List<TypeNode> typeParamNodes = new ArrayList<TypeNode>();
        typeParamNodes.add(xnf.CanonicalTypeNode(pos, xts.Int()));
        Expr call = xnf.X10Call(pos, ai, name, typeParamNodes, args).methodInstance(mi).type(xts.Void());
        return call;
    }

    private Expr genBroadcastField(Position pos, Expr fieldVar, Expr fieldId, FieldDecl fdPLH, boolean customSerialization) {
        Id id;
        boolean usingReflection = false;
        if (customSerialization && fieldVar.type().toClass().isJavaType()) {
            usingReflection = true;
            id = xnf.Id(pos, Name.make("broadcastStaticFieldUsingReflection"));
        } else {
            id = xnf.Id(pos, Name.make((fdPLH == null) ?
                "broadcastStaticField" : "broadcastStaticFieldSingleVM"));
        }

        // create MethodDef
        List<Ref<? extends Type>> argTypes = new ArrayList<Ref<? extends Type>>();

        if (customSerialization && !usingReflection) {
            if (X10PrettyPrinterVisitor.isPrimitive(fieldVar.type()) || X10PrettyPrinterVisitor.isString(fieldVar.type())) {
                argTypes.add(Types.ref(fieldVar.type()));

            } else {
                  argTypes.add(Types.ref(xts.Any()));
            }
        } else {
            argTypes.add(Types.ref(xts.Any()));
        }
        argTypes.add(Types.ref(xts.Short()));
        Type returnType = (fdPLH == null) ? xts.Void() : PlaceLocalHandle();
        MethodDef md = xts.methodDef(pos, pos, Types.ref(InitDispatcher()),
                Flags.NONE, Types.ref(returnType), id.id(), argTypes);
        MethodInstance mi = xts.createMethodInstance(pos, pos, Types.ref(md));

        // actual arguments
        List<Expr> args = new ArrayList<Expr>();
        if (customSerialization) {
            args.add(fieldVar.type(fieldVar.type()));
        } else {
            args.add(fieldVar.type(xts.Object()));
        }
        args.add(fieldId.type(xts.Short()));

        List<TypeNode> typeParamNodes = new ArrayList<TypeNode>();
        typeParamNodes.add(xnf.CanonicalTypeNode(pos, xts.Object()));
        typeParamNodes.add(xnf.CanonicalTypeNode(pos, xts.Short()));
        Receiver receiver = xnf.CanonicalTypeNode(pos, InitDispatcher());
        Expr call = xnf.X10Call(pos, receiver, id, typeParamNodes, args).methodInstance(mi).type(returnType);
        return call;
    }

    private Expr genCheckInitialized(Position pos, TypeNode receiver, FieldDef fdCond, boolean positive) {
        FieldInstance fi = fdCond.asInstance();
        Expr ai = xnf.Field(pos, receiver, xnf.Id(pos, fdCond.name())).fieldInstance(fi).type(fi.type());
        Id name = xnf.Id(pos, Name.make("get"));

        List<Ref<? extends Type>> argTypes = Collections.<Ref<? extends Type>>emptyList();
        MethodDef md = xts.methodDef(pos, pos, Types.ref((ClassType)xts.AtomicInteger()), 
                                     Flags.NONE, Types.ref(xts.Int()), name.id(), argTypes);
        MethodInstance mi = xts.createMethodInstance(pos, pos, Types.ref(md));

        List<Expr> args = Collections.<Expr>emptyList();
        List<TypeNode> typeParamNodes = Collections.<TypeNode>emptyList();
        Expr call = xnf.X10Call(pos, ai, name, typeParamNodes, args).methodInstance(mi).type(xts.Int());

        return xnf.Binary(pos, call, positive ? Binary.EQ : Binary.NE, getInitDispatcherConstant(pos, "INITIALIZED").type(xts.Int())).type(xts.Boolean());
    }

    private Expr genCheckExceptionRaised(Position pos, TypeNode receiver, FieldDef fdCond, boolean positive) {
        FieldInstance fi = fdCond.asInstance();
        Expr ai = xnf.Field(pos, receiver, xnf.Id(pos, fdCond.name())).fieldInstance(fi).type(fi.type());
        Id name = xnf.Id(pos, Name.make("get"));

        List<Ref<? extends Type>> argTypes = Collections.<Ref<? extends Type>>emptyList();
        MethodDef md = xts.methodDef(pos, pos, Types.ref((ClassType)xts.AtomicInteger()), 
                                     Flags.NONE, Types.ref(xts.Int()), name.id(), argTypes);
        MethodInstance mi = xts.createMethodInstance(pos, pos, Types.ref(md));

        List<Expr> args = Collections.<Expr>emptyList();
        List<TypeNode> typeParamNodes = Collections.<TypeNode>emptyList();
        Expr call = xnf.X10Call(pos, ai, name, typeParamNodes, args).methodInstance(mi).type(xts.Int());

        return xnf.Binary(pos, call, positive ? Binary.EQ : Binary.NE, getInitDispatcherConstant(pos, "EXCEPTION_RAISED").type(xts.Int())).type(xts.Boolean());
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
                                     Flags.NONE, Types.ref(xts.Void()), id.id(), argTypes);
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
                                              typeParamNodes, formals, null, null, body);
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

    private Stmt makeAddInitializer(Position pos, Name fieldName, FieldDef fdId, X10ClassDef classDef) {
        Id id = xnf.Id(pos, Name.make("addInitializer"));

        // argument type
        List<Ref<? extends Type>> argTypes = new ArrayList<Ref<? extends Type>>();
        argTypes.add(Types.ref(xts.String()));
        argTypes.add(Types.ref(xts.String()));

        // create MethodDef
        MethodDef md = xts.methodDef(pos, pos, Types.ref(InitDispatcher()), 
                                     Flags.NONE, Types.ref(xts.Void()), id.id(), argTypes);
        MethodInstance mi = xts.createMethodInstance(pos, pos, Types.ref(md));

        // get full path class name
        String fullName = getPackageName(classDef) + getClassName(classDef);

        // actual arguments
        List<Expr> args = new ArrayList<Expr>();
        args.add(xnf.StringLit(pos, fullName).type(xts.String()));
        args.add(xnf.StringLit(pos, Emitter.mangleToJava(fieldName)).type(xts.String()));

        List<TypeNode> typeParamNodes = new ArrayList<TypeNode>();
        typeParamNodes.add(xnf.CanonicalTypeNode(pos, xts.String()));
        typeParamNodes.add(xnf.CanonicalTypeNode(pos, xts.String()));
        TypeNode receiver = xnf.CanonicalTypeNode(pos, InitDispatcher());
        Expr call = xnf.X10Call(pos, receiver, id, typeParamNodes, args).methodInstance(mi).type(xts.Int());

        receiver = xnf.X10CanonicalTypeNode(pos, classDef.asType());
        return xnf.Eval(pos, xnf.FieldAssign(pos, receiver, xnf.Id(pos, fdId.name()),
                                             Assign.ASSIGN, call).fieldInstance(fdId.asInstance()).type(xts.Short()));
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
                                     Flags.NONE, Types.ref(xts.Void()), id.id(), argTypes);
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
        if (e.type().isNumeric() || e.type().isBoolean() || e.type().isChar() || e.type().isNull())
            return isConstantExpression(e);
        if (e.type() == xts.String())
            return isStringConstant(e);
        return false;
    }

    /**
     * from x10cpp.visit.ASTQuery
     */
    private boolean isConstantExpression(Expr e) {
        if (!e.isConstant())
            return false;
        if (e instanceof BooleanLit)
            return true;
        if (e instanceof IntLit)
            return true;
        if (e instanceof FloatLit)
            return true;
        if (e instanceof Cast)
            return isConstantExpression(((Cast) e).expr());
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
            return targetType == xts.String();
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
