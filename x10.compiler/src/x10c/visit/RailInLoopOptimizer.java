/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */
package x10c.visit;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import polyglot.ast.Assign;
import polyglot.ast.Block;
import polyglot.ast.Do;
import polyglot.ast.Eval;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.For;
import polyglot.ast.ForInit;
import polyglot.ast.ForUpdate;
import polyglot.ast.Id;
import polyglot.ast.Local;
import polyglot.ast.LocalAssign;
import polyglot.ast.LocalDecl;
import polyglot.ast.Loop;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.NullLit;
import polyglot.ast.Receiver;
import polyglot.ast.Stmt;
import polyglot.ast.While;
import polyglot.frontend.Job;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.Name;
import polyglot.types.QName;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.InternalCompilerError;
import polyglot.util.Pair;
import polyglot.util.Position;
import polyglot.util.CollectionUtil; import x10.util.CollectionFactory;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.Closure;
import x10.ast.ClosureCall;
import x10.ast.SettableAssign;
import x10.ast.SettableAssign_c;
import x10.ast.X10Call;
import x10.ast.X10CanonicalTypeNode;
import x10.ast.X10Loop;
import x10.ast.X10Special;
import x10.types.ParameterType;
import x10.types.X10ClassType;

import x10.types.X10LocalDef;
import x10.types.X10ParsedClassType_c;
import polyglot.types.TypeSystem;
import x10.visit.X10PrettyPrinterVisitor;
import x10c.ast.BackingArray;
import x10c.ast.BackingArrayAccess;
import x10c.ast.X10CBackingArrayAccess_c;
import x10c.ast.X10CNodeFactory_c;
import x10c.types.X10CTypeSystem_c;

public class RailInLoopOptimizer extends ContextVisitor {

    // TODO enable this
    private static final boolean ANALYZE_TEMP_VALS = false;
    private static final String IMC_FIELD_NAME = "raw";

    private final X10CTypeSystem_c xts;
    private final X10CNodeFactory_c xnf;

    private final Map<Name,X10LocalDef> localdefs = CollectionFactory.newHashMap();

    // map tmp values to Array type exprs. e.g.) temp000 -> array
    private final Map<Name, Expr> nameToArray = CollectionFactory.newHashMap();

    // map tmp values to Rail/IMC type exprs. e.g.) temp001 -> array.raw
    private final Map<Name, Expr> nameToRailIMC = CollectionFactory.newHashMap();

    public RailInLoopOptimizer(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
        xts = (X10CTypeSystem_c) ts;
        xnf = (X10CNodeFactory_c) nf;
    }

    private X10LocalDef getLocalDef(Type type, Name name) {
        if (localdefs.containsKey(name)) {
            return localdefs.get(name);
        }
        else {
           X10LocalDef ldef = xts.localDef(Position.COMPILER_GENERATED, xts.NoFlags(), Types.ref(type), name);
           localdefs.put(name, ldef);
           return ldef;
        }
    }
    
    @Override
    protected Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException {
        if (n instanceof Loop) {
            Loop loop = (Loop) n;

            // list of not loop invariant var names
            final List<String> ignores = new ArrayList<String>();

            if (n instanceof For) {
                checkInitCondUpdate(ignores, (For) loop);
            }

            // targets to privatize
            final List<Pair<BackingArray, Boolean>> backingArrayAndIsFinals = new ArrayList<Pair<BackingArray, Boolean>>();
            final Map<BackingArray, Id> backingArrayToId = CollectionFactory.newHashMap();

            Stmt visited1 = (Stmt) loop.body().visit(new NodeVisitor() {
                @Override
                public Node override(Node parent, Node n) {
                    if (n instanceof Loop) {
                        return n;
                    }
                    if (n instanceof Closure) {
                        return n;
                    }
                    return null;
                }
                @Override
                public Node leave(Node parent, Node old, Node n, NodeVisitor v) {
                    if (n instanceof LocalDecl) {
                        LocalDecl ld = (LocalDecl) n;
                        Expr init = ld.init();
                        // to merge privatization statement
                        if (init instanceof BackingArray) {
                            BackingArray ba = (BackingArray) init;
                            Expr rail = ba.container();
                            if (!ignores.contains(rail.toString())) {
                                if (rail instanceof Field && ignores.contains(((Field) rail).target().toString())) {
                                    ignores.add(ld.name().toString());
                                    return ld;
                                }
                                if (rail instanceof X10CBackingArrayAccess_c && ignores.contains(((X10CBackingArrayAccess_c) rail).index().toString())) {
                                    ignores.add(ld.name().toString());
                                    return ld;
                                }
                                Type type = ld.type().type();
                                for (Pair<BackingArray, Boolean> pair : backingArrayAndIsFinals) {
                                    // already privatized at another loop
                                    if (pair.fst().container().toString().equals(rail.toString())) {
                                        ignores.add(ld.name().toString());
                                        X10CanonicalTypeNode tn = xnf.X10CanonicalTypeNode(ld.position(), type);
                                        Id id = backingArrayToId.get(pair.fst());
                                        LocalDef ldef = getLocalDef(type, id.id());
                                        ldef.setFlags(Flags.FINAL);
                                        return xnf.LocalDecl(n.position(), xnf.FlagsNode(ld.position(), Flags.FINAL), tn, ld.name(), xnf.Local(ld.position(), id).localInstance(ldef.asInstance()).type(type)).localDef(ldef).type(tn);
                                    }
                                }
                                Id id = xnf.Id(ld.position(), Name.make(ld.name().toString()));
                                BackingArray nba = xnf.BackingArray(ld.position(), id, type, rail);
                                backingArrayToId.put(nba, id);
                                backingArrayAndIsFinals.add(new Pair<BackingArray, Boolean>(nba, true));
                            }
                        }
                        else {
                            if (ANALYZE_TEMP_VALS) {
                                analyzeTempVals(ignores, ld, nameToArray, nameToRailIMC);
                            }
                            ignores.add(ld.name().toString());
                        }
                    }
                    return n;
                }
            });

            final List<Stmt> moves = new ArrayList<Stmt>();

            Stmt visited2 = (Stmt) visited1.visit(new NodeVisitor() {
                @Override
                public Node override(Node parent, Node n) {
                    if (n instanceof Loop) {
                        return n;
                    }
                    if (n instanceof Closure) {
                        return n;
                    }
                    return null;
                }
                @Override
                public Node leave(Node parent, Node old, Node n, NodeVisitor v) {
                    if (n instanceof X10Call) {
                        X10Call call = (X10Call) n;
                        Position pos = call.position();
                        Receiver target = call.target();
                        if (
                                target != null
                                && isOptimizationTarget(target.type())
                                && (call.methodInstance().name()==ClosureCall.APPLY || call.methodInstance().name()==SettableAssign.SET)
                        ) {
                            Expr elem;
                            Expr index;
                            // apply
                            if (call.arguments().size() == 1) {
                                elem = null;
                                index = call.arguments().get(0);
                            }
                            // set
                            else {
                                elem = call.arguments().get(1);
                                index = call.arguments().get(0);
                            }
                            
                            if (target instanceof Local) {
                                Local local = (Local) target;
                                if (ANALYZE_TEMP_VALS && nameToRailIMC.containsKey(local.name().id())) {
                                    target = nameToRailIMC.get(local.name().id());
                                }
                            }
                            else if (target instanceof Field) {
                                Field field = (Field) target;
                                if (!field.flags().isFinal()) {
                                    return n;
                                }
                                else if (!(field.target() instanceof X10Special && ((X10Special) field.target()).kind().equals(X10Special.THIS))) {
                                    return n;
                                }
                                else if (ignores.contains(((Field) target).target().toString())) {
                                    return n;
                                }
                            }
                            else {
                                return n;
                            }

                            if (ignores.contains(target.toString())) {
                                return n;
                            }

                            boolean contains = false;
                            Id id = null;
                            for (Pair<BackingArray, Boolean> pair : backingArrayAndIsFinals) {
                                if (pair.fst().container().toString().equals(target.toString())) {
                                    contains = true;
                                    id = backingArrayToId.get(pair.fst());
                                    break;
                                }
                            }

                            X10ClassType ct = (X10ClassType) Types.baseType(target.type());
                            List<Type> typeArguments = ct.typeArguments();
                            if (typeArguments == null)
                                typeArguments = new ArrayList<Type>(ct.x10Def().typeParameters());
                            Type type = Types.baseType(typeArguments.get(0));
                            if (!contains) {
                                id = xnf.Id(pos, Name.makeFresh(target.toString().replace(".", "$").replaceAll("[\\[\\]]", "_").replaceAll(", ","_") + "$value"));
                                BackingArray ba = xnf.BackingArray(pos, id, createArrayType(type), (Expr) target);
                                backingArrayToId.put(ba, id);
                                backingArrayAndIsFinals.add(new Pair<BackingArray, Boolean>(ba, true));
                            }
                            if (elem == null) {
                                Type arrayType = createArrayType(target.type());
                                LocalDef ldef = getLocalDef(arrayType, id.id());
                                return xnf.BackingArrayAccess(pos, xnf.Local(pos, id).localInstance(ldef.asInstance()).type(arrayType), index, type);
                            }
                            Type arrayType = createArrayType(type);
                            LocalDef ldef = getLocalDef(arrayType, id.id());
                            return xnf.BackingArrayAccessAssign(pos, xnf.Local(pos, id).localInstance(ldef.asInstance()).type(arrayType), index, Assign.ASSIGN, elem).type(type);
                        }
                    }
                    if (n instanceof SettableAssign_c) {
                        Type type = Types.baseType(((SettableAssign_c) n).type());
                        Expr array = ((SettableAssign_c) n).array();
                        if (isOptimizationTarget(array.type())) {
                            if (((SettableAssign_c) n).index().size() > 1) {
                                return n;
                            }

                            if (ignores.contains(array.toString())) {
                                return n;
                            }

                            if (array instanceof Local) {
                                Local local = (Local) array;
                                if (ANALYZE_TEMP_VALS && nameToRailIMC.containsKey(local.name().id())) {
                                    array = nameToRailIMC.get(local.name().id());
                                }
                            }
                            else if (array instanceof Field) {
                                Field field = (Field) array;
                                if (!field.flags().isFinal()) {
                                    return n;
                                }
                                else if (!(field.target() instanceof X10Special && ((X10Special) field.target()).kind().equals(X10Special.THIS))) {
                                    return n;
                                }
                                else if (ignores.contains(((Field) array).target().toString())) {
                                    return n;
                                }
                            }
                            else {
                                return n;
                            }

                            boolean contains = false;
                            Id id = null;
                            for (Pair<BackingArray, Boolean> pair : backingArrayAndIsFinals) {
                                if (pair.fst().container().toString().equals(array.toString())) {
                                    contains = true;
                                    id = backingArrayToId.get(pair.fst());
                                    break;
                                }
                            }
                            BackingArray ba;
                            if (!contains) {
                                id = xnf.Id(n.position(), Name.makeFresh(array.toString().replace(".", "$").replaceAll("[\\[\\]]", "_") + "$value"));
                                ba = xnf.BackingArray(n.position(), id, createArrayType(type), array);
                                backingArrayToId.put(ba, id);
                                backingArrayAndIsFinals.add(new Pair<BackingArray, Boolean>(ba, true));
                            }
                            else {
                                ba = xnf.BackingArray(n.position(), id, createArrayType(type), array);
                            }
                            Type arrayType = createArrayType(type);
                            LocalDef ldef = getLocalDef(arrayType, id.id());
                            return xnf.BackingArrayAccessAssign(n.position(), xnf.Local(n.position(), id).localInstance(ldef.asInstance()).type(arrayType), ((SettableAssign_c) n).index().get(0), ((SettableAssign_c) n).operator(), ((SettableAssign_c) n).right()).type(type);
                        }
                    }
                    // rail = Rail.make(10) -> rail = Rail.make(10); railvaluexxx = (int[]) rail.value;
                    if (n instanceof LocalDecl) {
                        LocalDecl ld = (LocalDecl) n;
                        Expr init = ((LocalDecl) n).init();
                        if (init instanceof BackingArray) {
                            BackingArray ba = (BackingArray) init;
                            Expr rail = ba.container();
                            if (rail != null) {
                                if (!ignores.contains(rail.toString())) {
                                    if (rail instanceof Field && ignores.contains(((Field) rail).target().toString())) {
                                        return n;
                                    }
                                    if (rail instanceof X10CBackingArrayAccess_c) {
                                        if (ignores.contains(((X10CBackingArrayAccess_c) rail).index().toString())) {
                                            return n;
                                        }
                                    }
                                    
                                    for (int i = 0; i < backingArrayAndIsFinals.size(); i++) {
                                        Pair<BackingArray, Boolean> pair = backingArrayAndIsFinals.get(i);
                                        // already exist
                                        if (backingArrayToId.get(pair.fst()).toString().equals(ld.name().toString())) {
                                            if (!ld.flags().flags().isFinal()) {
                                                backingArrayAndIsFinals.set(i, new Pair<BackingArray, Boolean>(pair.fst(), false));
                                            }
                                            return null;
                                        }
                                    }
                                    moves.add(ld);
                                    return null;
                                }
                            }
                        }
                    }
                    return n;
                }
            });

            Stmt visited3 = (Stmt) visited2.visit(new NodeVisitor() {
                @Override
                public Node leave(Node parent, Node old, Node n, NodeVisitor v) {
                    // rail = Rail.make(10) -> rail = Rail.make(10);
                    //                         railvaluexxx = (int[]) rail.value;
                    // when : rail = null   -> rail = null;
                    //                         raiilvaluexxx = null;
                    if (n instanceof Eval) {
                        if (((Eval) n).expr() instanceof LocalAssign) {
                            LocalAssign la = (LocalAssign) ((Eval) n).expr();
                            Type type = Types.baseType(la.type());
                            Local local = la.local();
                            if (isRail(type)) {
                                boolean contains = false;
                                Id id = null;
                                for (int i = 0; i < backingArrayAndIsFinals.size(); i++) {
                                    Pair<BackingArray, Boolean> pair = backingArrayAndIsFinals.get(i);
                                    if (pair.fst().container().toString().equals(local.toString())) {
                                        contains = true;
                                        id = backingArrayToId.get(pair.fst());
                                        backingArrayAndIsFinals.set(i, new Pair<BackingArray, Boolean>(pair.fst(), false));
                                        break;
                                    }
                                }
                                if (!contains) {
                                    return n;
                                }
                                if (parent instanceof Block) {
                                    for (Stmt stmt : ((Block) parent).statements()) {
                                        if (stmt instanceof LocalDecl && ((LocalDecl) stmt).init() instanceof BackingArray) {
                                            BackingArray ba = (BackingArray) ((LocalDecl) stmt).init();
                                            if (backingArrayToId.get(ba).toString().equals(id.toString())) {
                                                return n;
                                            }
                                        }
                                    }
                                }
                                List<Stmt> stmts = new ArrayList<Stmt>();
                                stmts.add((Stmt) n);
                                Type pt = Types.baseType(((X10ClassType) type).typeArguments().get(0));
                                Expr expr;
                                Type arrayType = createArrayType(pt);
                                LocalDef ldef = getLocalDef(arrayType, id.id());
                                if (la.right() instanceof NullLit) {
                                    expr = xnf.LocalAssign(n.position(), (Local) xnf.Local(n.position(), id).localInstance(ldef.asInstance()).type(arrayType), Assign.ASSIGN, la.right()).type(arrayType);
                                } else {
                                    expr = xnf.LocalAssign(n.position(), (Local) xnf.Local(n.position(), id).localInstance(ldef.asInstance()).type(arrayType), Assign.ASSIGN, xnf.BackingArray(n.position(), id, arrayType, local)).type(arrayType);
                                }
                                stmts.add(xnf.Eval(n.position(), expr));
                                return xnf.StmtSeq(n.position(), stmts);
                            }
                        }
                    }
                    return n;
                };
            });

            if (loop instanceof For) {
                loop = ((For) loop).body(visited3);
            } else if (loop instanceof While) {
                loop = ((While) loop).body(visited3);
            } else if (loop instanceof Do) {
                loop = ((Do) loop).body(visited3);
            } else if (loop instanceof X10Loop) {
                loop = ((X10Loop) loop).body(visited3);
            } else {
                throw new InternalCompilerError("something wrong!!!");
            }

            if (moves.isEmpty() && backingArrayAndIsFinals.isEmpty()) {
                return n;
            }

            List<Stmt> statements = new ArrayList<Stmt>();
            statements.addAll(moves);
            for (Pair<BackingArray, Boolean> pair : backingArrayAndIsFinals) {
                Type type = Types.baseType(pair.fst().container().type());
                if (type instanceof X10ClassType) {
                    Type pt = ((X10ClassType) type).typeArguments().get(0);
                    X10CanonicalTypeNode tn = xnf.X10CanonicalTypeNode(n.position(), pair.fst().type());
                    LocalDecl ld;
                    LocalDef localDef = getLocalDef(pair.fst().type(), backingArrayToId.get(pair.fst()).id());
                    if (pair.snd()) {
                        localDef.setFlags(xts.Final());
                        ld = xnf.LocalDecl(n.position(), xnf.FlagsNode(n.position(), xts.Final()), tn, backingArrayToId.get(pair.fst()), pair.fst())
                        .localDef(localDef)
                        .type(tn);
                    }
                    else {
                        ld = xnf.LocalDecl(n.position(), xnf.FlagsNode(n.position(), xts.NoFlags()), tn, backingArrayToId.get(pair.fst()), pair.fst())
                        .localDef(localDef)
                        .type(tn);
                    }
                    statements.add(ld);
                }
            }
            statements.add(loop);
            return xnf.Block(n.position(), statements);
        }

        return n;
    }

    private void checkInitCondUpdate(final List<String> ignores, For forn) {
        for (ForInit forInit : forn.inits()) {
            if (forInit instanceof LocalDecl) {
                ignores.add(((LocalDecl) forInit).name().toString());
            }
            if (forInit instanceof Eval) {
                if (((Eval) forInit).expr() instanceof LocalAssign) {
                    LocalAssign la = (LocalAssign) ((Eval) forInit).expr();
                    ignores.add(la.local().name().toString());
                }
            }
        }
        List<ForUpdate> iters = forn.iters();
        for (ForUpdate forUpdate : iters) {
            if (forUpdate instanceof Eval) {
                if (((Eval) forUpdate).expr() instanceof LocalAssign) {
                    LocalAssign la = (LocalAssign) ((Eval) forUpdate).expr();
                    ignores.add(la.local().name().toString());
                }
            }
        }
    }

    // TODO limitation: cannot handle like the following code: val temp; temp = array and var temp = array;
    private void analyzeTempVals(final List<String> ignores, LocalDecl ld, Map<Name, Expr> nameToArray, Map<Name, Expr> nameToRailIMC) {
        Expr init2 = ld.init();
        if (init2 != null && ld.flags().flags().isFinal()) { 
            Type lt = ld.localDef().type().get();
            if (Types.isX10RegionArray(init2.type()) && Types.isX10RegionArray(lt)) {
                if (init2 instanceof Local) {
                    Local local = (Local) init2;
                    if (nameToArray.containsKey(local.name().id())) {
                        nameToArray.put(ld.name().id(), nameToArray.get(local.name().id()));
                    } else if (!ignores.contains(local.name().toString())) {
                        nameToArray.put(ld.name().id(), local);
                    }
                } else if (init2 instanceof Field) {
                    Field f = (Field) init2;
                    if (f.flags().isFinal() && f.target() instanceof X10Special && ((X10Special) f.target()).kind().equals(X10Special.THIS)) {
                        nameToArray.put(ld.name().id(), init2);
                    }
                }
            }
            else if (isRail(init2.type()) && isRail(lt)) {
                if (init2 instanceof Local) {
                    Local local = (Local) init2;
                    if (nameToRailIMC.containsKey(local.name().id())) {
                        nameToRailIMC.put(ld.name().id(), nameToRailIMC.get(local.name().id()));
                    }
                    else if (!ignores.contains(local.name().toString())) {
                        nameToRailIMC.put(ld.name().id(), init2);
                    }
                }
                else if (init2 instanceof Field) {
                    Field f = (Field) init2;
                    // special care for the raw field of array.
                    // var/val imc:IndexedMemoryChunck[T] = array.raw;
                    if (f.target() instanceof Local && Types.isX10RegionArray(f.target().type()) && f.name().toString().equals(IMC_FIELD_NAME)) {
                        Local target = (Local) f.target();
                        if (nameToArray.containsKey(target.name().id())) {
                            Field f2 = (Field) xnf.Field(ld.position(), nameToArray.get(target.name().id()), f.name()).fieldInstance(f.fieldInstance()).type(f.type());
                            nameToRailIMC.put(ld.name().id(), f2);
                        }
                    }
                    else if (f.flags().isFinal() && f.target() instanceof X10Special && ((X10Special) f.target()).kind().equals(X10Special.THIS)) {
                        nameToRailIMC.put(ld.name().id(), init2);
                    }
                }
            }
        }
    }

    Type createArrayType(Type t) {
        return xts.createBackingArray(t.position(), Types.ref(t));
    }

    private boolean isRail(Type type) {
        Type tbase = Types.baseType(type);
        return tbase instanceof X10ParsedClassType_c && ((X10ParsedClassType_c) tbase).def().asType().typeEquals(xts.Rail(), context);
    }


    private boolean isOptimizationTarget(Type ttype) {
        ttype = Types.baseType(ttype);
        if (!isRail(ttype))
            return false;
        if (!X10PrettyPrinterVisitor.hasParams(ttype))
            return true;
        List<Type> ta = ((X10ClassType) ttype).typeArguments();
        if (ta != null && !ta.isEmpty() && !xts.isParameterType(ta.get(0))) {
            return true;
        }
        return false;
    }
}
