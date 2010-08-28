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
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.InternalCompilerError;
import polyglot.util.Pair;
import polyglot.util.Position;
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
import x10.types.X10Flags;
import x10.types.X10TypeMixin;
import x10c.ast.BackingArray;
import x10c.ast.BackingArrayAccess;
import x10c.ast.X10CBackingArrayAccess_c;
import x10c.ast.X10CNodeFactory_c;
import x10c.types.X10CTypeSystem_c;

public class RailInLoopOptimizer extends ContextVisitor {

    private static final boolean VALRAIL_OPTIMIZE = true;
    private final X10CTypeSystem_c xts;
    private final X10CNodeFactory_c xnf;

    public RailInLoopOptimizer(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
        xts = (X10CTypeSystem_c) ts;
        xnf = (X10CNodeFactory_c) nf;
    }

    @Override
    protected Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException {
        if (n instanceof Loop) {
            Loop loop = (Loop) n;

            final List<String> ignores = new ArrayList<String>();

            if (n instanceof For) {
                For forn = (For) loop;
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

            // check for valrail's index 
            loop.visit(new NodeVisitor() {
                @Override
                public Node leave(Node parent, Node old, Node n, NodeVisitor v) {
                    if (n instanceof LocalAssign) {
                        if (xts.Int().typeEquals(X10TypeMixin.baseType(((LocalAssign) n).leftType()), context)) {
                            ignores.add(((LocalAssign) n).local().name().toString());
                        }
                    }
                    return n;
                }
            });

            // targets to privatize
            final List<Pair<BackingArray, Boolean>> targetAndIsFinals = new ArrayList<Pair<BackingArray, Boolean>>();
            final Map<BackingArray, Id> backingArrayToId = new HashMap<BackingArray, Id>();

            // to merge privatization statement
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
                        if (init instanceof BackingArray) {
                            BackingArray ba = (BackingArray) init;
                            Expr rail = ba.container();
                            if (!ignores.contains(rail.toString())) {
                                if (rail instanceof Field && ignores.contains(((Field) rail).target().toString())) {
                                    ignores.add(ld.name().toString());
                                    return n;
                                }
                                if (rail instanceof X10CBackingArrayAccess_c && ignores.contains(((X10CBackingArrayAccess_c) rail).index().toString())) {
                                    ignores.add(ld.name().toString());
                                    return n;
                                }
                                Type type = ld.type().type();
                                for (Pair<BackingArray, Boolean> pair : targetAndIsFinals) {
                                    // already privatized at another loop
                                    if (pair.fst().container().toString().equals(rail.toString())) {
                                        ignores.add(ld.name().toString());
                                        X10CanonicalTypeNode tn = xnf.X10CanonicalTypeNode(n.position(), type);
                                        Id id = backingArrayToId.get(pair.fst());
                                        LocalDef ldef = xts.localDef(n.position(), xts.Final(), Types.ref(type), id.id());
                                        return xnf.LocalDecl(n.position(), xnf.FlagsNode(n.position(), Flags.FINAL), tn, ld.name(), xnf.Local(n.position(), id).localInstance(ldef.asInstance()).type(type)).localDef(ldef).type(tn);
                                    }
                                }
                                Id id = xnf.Id(ld.position(), Name.make(ld.name().toString()));
                                BackingArray nba = xnf.BackingArray(n.position(), id, type, rail);
                                backingArrayToId.put(nba, id);
                                targetAndIsFinals.add(new Pair<BackingArray, Boolean>(nba, true));
                            }
                        }
                        else {
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
                        Type type = X10TypeMixin.baseType(call.type());
                        if (!(type instanceof ParameterType) && target != null && (xts.isRail(target.type()) || xts.isValRail(target.type())) && (call.methodInstance().name()==ClosureCall.APPLY || call.methodInstance().name()==SettableAssign.SET)) {
                            if (ignores.contains(target.toString())) {
                                return n;
                            }

                            Expr elem;
                            Expr index;
                            if (call.arguments().size() == 1) {
                                elem = null;
                                index = call.arguments().get(0);
                            } else {
                                elem = call.arguments().get(0);
                                index = call.arguments().get(1);
                            }

                            if (target instanceof Local) {
                                if (X10Flags.toX10Flags(((Local) target).flags()).isShared()) {
                                    return n;
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

                            boolean contains = false;
                            Id id = null;
                            for (Pair<BackingArray, Boolean> pair : targetAndIsFinals) {
                                if (pair.fst().container().toString().equals(target.toString())) {
                                    contains = true;
                                    id = backingArrayToId.get(pair.fst());
                                    break;
                                }
                            }

                            if (!contains) {
                                id = xnf.Id(pos, Name.makeFresh(target.toString().replace(".", "$").replaceAll("[\\[\\]]", "_") + "$value"));
                                BackingArray ba = xnf.BackingArray(pos, id, createArrayType(type), (Expr) target);
                                backingArrayToId.put(ba, id);
                                targetAndIsFinals.add(new Pair<BackingArray, Boolean>(ba, true));
                            }
                            if (elem == null) {
                                LocalDef ldef = xts.localDef(n.position(), xts.NoFlags(), Types.ref(target.type()), id.id());
                                return xnf.BackingArrayAccess(pos, xnf.Local(pos, id).localInstance(ldef.asInstance()).type(target.type()), index, type);
                            }
                            LocalDef ldef = xts.localDef(n.position(), xts.NoFlags(), Types.ref(type), id.id());
                            return xnf.BackingArrayAccessAssign(pos, xnf.Local(pos, id).localInstance(ldef.asInstance()), index, Assign.ASSIGN, elem).type(type);
                        }
                    }
                    if (n instanceof SettableAssign_c) {
                        Type type = X10TypeMixin.baseType(((SettableAssign_c) n).type());
                        Expr array = ((SettableAssign_c) n).array();
                        if (!(type instanceof ParameterType) && xts.isRail(array.type())) {
                            if (((SettableAssign_c) n).index().size() > 1) {
                                return n;
                            }

                            if (ignores.contains(array.toString())) {
                                return n;
                            }

                            if (array instanceof Local) {
                                if (X10Flags.toX10Flags(((Local) array).flags()).isShared()) {
                                    return n;
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
                            for (Pair<BackingArray, Boolean> pair : targetAndIsFinals) {
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
                                targetAndIsFinals.add(new Pair<BackingArray, Boolean>(ba, true));
                            }
                            else {
                                ba = xnf.BackingArray(n.position(), id, createArrayType(type), array);
                            }
                            LocalDef ldef = xts.localDef(n.position(), xts.NoFlags(), Types.ref(type), id.id());
                            return xnf.BackingArrayAccessAssign(n.position(), xnf.Local(n.position(), id).localInstance(ldef.asInstance()), ((SettableAssign_c) n).index().get(0), ((SettableAssign_c) n).operator(), ((SettableAssign_c) n).right()).type(type);
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
                                    
                                    for (int i = 0; i < targetAndIsFinals.size(); i++) {
                                        Pair<BackingArray, Boolean> pair = targetAndIsFinals.get(i);
                                        // already exist
                                        if (backingArrayToId.get(pair.fst()).toString().equals(ld.name().toString())) {
                                            if (!ld.flags().flags().isFinal()) {
                                                targetAndIsFinals.set(i, new Pair<BackingArray, Boolean>(pair.fst(), false));
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
                };
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
                            Type type = X10TypeMixin.baseType(la.type());
                            Local local = la.local();
                            if (xts.isRail(type) || xts.isValRail(type)) {
                                boolean contains = false;
                                Id id = null;
                                for (int i = 0; i < targetAndIsFinals.size(); i++) {
                                    Pair<BackingArray, Boolean> pair = targetAndIsFinals.get(i);
                                    if (pair.fst().container().toString().equals(local.toString())) {
                                        contains = true;
                                        id = backingArrayToId.get(pair.fst());
                                        targetAndIsFinals.set(i, new Pair<BackingArray, Boolean>(pair.fst(), false));
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
                                Type pt = X10TypeMixin.baseType(((X10ClassType) type).typeArguments().get(0));
                                Expr expr;
                                Type arrayType = createArrayType(pt);
                                if (la.right() instanceof NullLit) {
                                    LocalDef ldef = xts.localDef(n.position(), xts.NoFlags(), Types.ref(arrayType), id.id());
                                    expr = xnf.LocalAssign(n.position(), (Local) xnf.Local(n.position(), id).localInstance(ldef.asInstance()).type(arrayType), Assign.ASSIGN, la.right()).type(arrayType);
                                } else {
                                    LocalDef ldef = xts.localDef(n.position(), xts.NoFlags(), Types.ref(arrayType), id.id());
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

            // for valrail
            Stmt visited4;
            if (VALRAIL_OPTIMIZE) {
                visited4 = (Stmt) visited3.visit(new NodeVisitor() {
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
                            Type type = X10TypeMixin.baseType(call.type());
                            if (!(type instanceof ParameterType) && target != null && (xts.isRail(target.type()) || xts.isValRail(target.type())) && (call.methodInstance().name()==ClosureCall.APPLY || call.methodInstance().name()==SettableAssign.SET)) {
                                if (ignores.contains(target.toString())) {
                                    return n;
                                }

                                Expr elem;
                                Expr index;
                                if (call.arguments().size() == 1) {
                                    elem = null;
                                    index = call.arguments().get(0);
                                } else {
                                    elem = call.arguments().get(0);
                                    index = call.arguments().get(1);
                                }

                                // for valrail
                                if (target instanceof BackingArrayAccess) {
                                    BackingArrayAccess ja = (BackingArrayAccess) target;
                                    if (!xts.isValRail(ja.array().type())) {
                                        return n;
                                    }
                                    if (!ja.index().isConstant() && !(ja.index() instanceof Local && !ignores.contains(ja.index().toString()))) {
                                        return n;
                                    }
                                    boolean contain = false;
                                    for (Pair<BackingArray, Boolean> pair : targetAndIsFinals) {
                                        if (backingArrayToId.get(pair.fst()).toString().equals(ja.array().toString())) {
                                            contain = true;
                                            if (pair.snd() == false) {
                                                return n;
                                            }
                                        }
                                    }
                                    if (contain == false) {
                                        return n;
                                    }
                                } else {
                                    return n;
                                }

                                boolean contains = false;
                                Id id = null;
                                for (Pair<BackingArray, Boolean> pair : targetAndIsFinals) {
                                    if (pair.fst().container().toString().equals(target.toString())) {
                                        contains = true;
                                        id = backingArrayToId.get(pair.fst());
                                        break;
                                    }
                                }

                                if (!contains) {
                                    id = xnf.Id(pos, Name.makeFresh(target.toString().replace(".", "$").replaceAll("[\\[\\]]", "_") + "$value"));
                                    BackingArray ba = xnf.BackingArray(pos, id, createArrayType(type), (Expr) target);
                                    backingArrayToId.put(ba, id);
                                    targetAndIsFinals.add(new Pair<BackingArray, Boolean>(ba, true));
                                }
                                if (elem == null) {
                                    LocalDef ldef = xts.localDef(n.position(), xts.NoFlags(), Types.ref(target.type()), id.id());
                                    return xnf.BackingArrayAccess(pos, xnf.Local(pos, id).localInstance(ldef.asInstance()).type(target.type()), index, type);
                                }
                                LocalDef ldef = xts.localDef(n.position(), xts.NoFlags(), Types.ref(type), id.id());
                                return xnf.BackingArrayAccessAssign(pos, xnf.Local(pos, id).localInstance(ldef.asInstance()).type(type), index, Assign.ASSIGN, elem).type(type);
                            }
                        }
                        if (n instanceof SettableAssign_c) {
                            Type type = X10TypeMixin.baseType(((SettableAssign_c) n).type());
                            Expr array = ((SettableAssign_c) n).array();
                            if (!(type instanceof ParameterType) && xts.isRail(array.type())) {
                                if (((SettableAssign_c) n).index().size() > 1) {
                                    return n;
                                }

                                if (ignores.contains(array.toString())) {
                                    return n;
                                }

                                if (array instanceof BackingArrayAccess) {
                                    BackingArrayAccess ja = (BackingArrayAccess) array;
                                    if (!xts.isValRail(ja.array().type())) {
                                        return n;
                                    }
                                    if (!ja.index().isConstant() && !(ja.index() instanceof Local && !ignores.contains(ja.index().toString()))) {
                                        return n;
                                    }
                                    boolean contain = false;
                                    for (Pair<BackingArray, Boolean> pair : targetAndIsFinals) {
                                        if (backingArrayToId.get(pair.fst()).toString().equals(ja.array().toString())) {
                                            contain = true;
                                            if (pair.snd() == false) {
                                                return n;
                                            }
                                        }
                                    }
                                    if (contain == false) {
                                        return n;
                                    }
                                } else {
                                    return n;
                                }

                                boolean contains = false;
                                Id id = null;
                                for (Pair<BackingArray, Boolean> pair : targetAndIsFinals) {
                                    if (pair.fst().container().toString().equals(array.toString())) {
                                        contains = true;
                                        id = backingArrayToId.get(pair.fst());
                                        break;
                                    }
                                }
                                BackingArray jna;
                                if (!contains) {
                                    id = xnf.Id(n.position(), Name.makeFresh(array.toString().replace(".", "$").replaceAll("[\\[\\]]", "_") + "$value"));
                                    jna = xnf.BackingArray(n.position(), id, createArrayType(type), array);
                                    backingArrayToId.put(jna, id);
                                    targetAndIsFinals.add(new Pair<BackingArray, Boolean>(jna, true));
                                }
                                LocalDef ldef = xts.localDef(n.position(), xts.NoFlags(), Types.ref(type), id.id());
                                return xnf.BackingArrayAccessAssign(n.position(), xnf.Local(n.position(), id).localInstance(ldef.asInstance()).type(type), ((SettableAssign_c) n).index().get(0), ((SettableAssign_c) n).operator(), ((SettableAssign_c) n).right()).type(type);
                            }
                        }
                        return n;
                    };
                });
            } else {
                visited4 = visited3;
            }


            if (loop instanceof For) {
                loop = ((For) loop).body(visited4);
            } else if (loop instanceof While) {
                loop = ((While) loop).body(visited4);
            } else if (loop instanceof Do) {
                loop = ((Do) loop).body(visited4);
            } else if (loop instanceof X10Loop) {
                loop = (Loop) ((X10Loop) loop).body(visited4);
            } else {
                throw new InternalCompilerError("something wrong!!!");
            }

            if (moves.isEmpty() && targetAndIsFinals.isEmpty()) {
                return n;
            }

            List<Stmt> statements = new ArrayList<Stmt>();
            statements.addAll(moves);
            for (Pair<BackingArray, Boolean> pair : targetAndIsFinals) {
                Type type = X10TypeMixin.baseType(pair.fst().container().type());
                if (type instanceof X10ClassType) {
                    Type pt = ((X10ClassType) type).typeArguments().get(0);
                    X10CanonicalTypeNode tn = xnf.X10CanonicalTypeNode(n.position(), pair.fst().type());
                    LocalDecl ld;
                    if (pair.snd()) {
                        ld = xnf.LocalDecl(n.position(), xnf.FlagsNode(n.position(), xts.Final()), tn, backingArrayToId.get(pair.fst()), pair.fst())
                        .localDef(xts.localDef(n.position(), xts.Final(), Types.ref(pair.fst().type()), backingArrayToId.get(pair.fst()).id()))
                        .type(tn);
                    }
                    else {
                        ld = xnf.LocalDecl(n.position(), xnf.FlagsNode(n.position(), xts.NoFlags()), tn, backingArrayToId.get(pair.fst()), pair.fst())
                        .localDef(xts.localDef(n.position(), xts.NoFlags(), Types.ref(pair.fst().type()), backingArrayToId.get(pair.fst()).id()))
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

    Type createArrayType(Type t) {
        return xts.createBackingArray(t.position(), Types.ref(t));
    }
}
