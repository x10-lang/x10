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
import polyglot.ast.Do;
import polyglot.ast.Eval;
import polyglot.ast.Expr;
import polyglot.ast.Expr_c;
import polyglot.ast.Field;
import polyglot.ast.For;
import polyglot.ast.ForInit;
import polyglot.ast.ForUpdate;
import polyglot.ast.Local;
import polyglot.ast.LocalAssign;
import polyglot.ast.LocalDecl;
import polyglot.ast.Loop;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.NullLit;
import polyglot.ast.Receiver;
import polyglot.ast.Stmt;
import polyglot.ast.Term;
import polyglot.ast.While;
import polyglot.frontend.Job;
import polyglot.types.Name;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.CodeWriter;
import polyglot.util.InternalCompilerError;
import polyglot.util.Pair;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import x10.ast.SettableAssign_c;
import x10.ast.X10Call;
import x10.ast.X10Loop;
import x10.ast.X10NodeFactory;
import x10.emitter.Emitter;
import x10.types.ParameterType;
import x10.types.X10ClassType;
import x10.types.X10Flags;
import x10.types.X10TypeMixin;
import x10.types.X10TypeSystem;
import x10.visit.X10PrettyPrinterVisitor;
import x10.visit.X10Translator;

public class RailInLoopOptimizer extends ContextVisitor {

    private static final boolean VALRAIL_OPTIMIZE = true;
    private final X10TypeSystem xts;
    private final X10NodeFactory xnf;

    public RailInLoopOptimizer(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
        xts = (X10TypeSystem) ts;
        xnf = (X10NodeFactory) nf;
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
                        if (xts.Int().typeEquals(X10TypeMixin.baseType(((LocalAssign) n).type()), context)) {
                            ignores.add(((LocalAssign) n).local().name().toString());
                        }
                    }
                    return n;
                }
            });

            // targets to privatize
            final List<Pair<JavaArray, Boolean>> targetAndIsFinals = new ArrayList<Pair<JavaArray, Boolean>>();

            // to merge privatization statement
            Stmt visited1 = (Stmt) loop.body().visit(new NodeVisitor() {
                @Override
                public Node override(Node parent, Node n) {
                    if (n instanceof Loop) {
                        return n;
                    }
                    return null;
                }
                @Override
                public Node leave(Node parent, Node old, Node n, NodeVisitor v) {
                    if (n instanceof LocalDecl) {
                        LocalDecl ld = (LocalDecl) n;
                        ignores.add(ld.name().toString());
                    }
                    if (n instanceof PrivatizeExpr) {
                        PrivatizeExpr privtz = (PrivatizeExpr) n;
                        Term rail = privtz.rail;
                        if (!ignores.contains(rail.toString())) {
                            if (rail instanceof Field && ignores.contains(((Field) rail).target().toString())) {
                                ignores.add(privtz.valName);
                                return n;
                            }
                            if (rail instanceof JavaArray && ignores.contains(((JavaArray) rail).index.toString())) {
                                ignores.add(privtz.valName);
                                return n;
                            }
                            for (Pair<JavaArray, Boolean> pair : targetAndIsFinals) {
                                // already privatized at another loop
                                if (pair.fst().rail.toString().equals(rail.toString())) {
                                    ignores.add(privtz.valName);
                                    return new PrivatizeExpr(n.position(), privtz.type, privtz.valName, pair.fst().name.toString(), privtz.isFinal);
                                }
                            }
                            JavaArray jna = new JavaArray(n.position(), privtz.type, (Receiver) privtz.rail, null, null, Name.make(privtz.valName));
                            targetAndIsFinals.add(new Pair<JavaArray, Boolean>(jna, true));
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
                    return null;
                }
                @Override
                public Node leave(Node parent, Node old, Node n, NodeVisitor v) {
                    if (n instanceof X10Call) {
                        X10Call call = (X10Call) n;
                        Position pos = call.position();
                        Receiver target = call.target();
                        Type type = X10TypeMixin.baseType(call.type());
                        if (!(type instanceof ParameterType) && target != null && (xts.isRail(target.type()) || xts.isValRail(target.type())) && (call.methodInstance().name().toString().equals("apply") || call.methodInstance().name().toString().equals("set"))) {
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
                            } else if (target instanceof Field) {
                                if (!((Field) target).flags().isFinal()) {
                                    return n;
                                } else if (ignores.contains(((Field) target).target().toString())) {
                                    return n;
                                }
                            }
                            else {
                                return n;
                            }

                            boolean contains = false;
                            Name name = null;
                            for (Pair<JavaArray, Boolean> pair : targetAndIsFinals) {
                                if (pair.fst().rail.toString().equals(target.toString())) {
                                    contains = true;
                                    name = pair.fst().name;
                                    break;
                                }
                            }

                            if (!contains) {
                                name = Name.makeFresh("railval");
                                targetAndIsFinals.add(new Pair<JavaArray, Boolean>(new JavaArray(pos, type, target, null, null, name), true));
                            }
                            return new JavaArray(pos, type, target, index, elem, name);
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
                                if (!((Field) array).flags().isFinal()) {
                                    return n;
                                } else if (ignores.contains(((Field) array).target().toString())) {
                                    return n;
                                }
                            }
                            else {
                                return n;
                            }

                            boolean contains = false;
                            Name name = null;
                            for (Pair<JavaArray, Boolean> pair : targetAndIsFinals) {
                                if (pair.fst().rail.toString().equals(array.toString())) {
                                    contains = true;
                                    name = pair.fst().name;
                                    break;
                                }
                            }
                            JavaArray jna;
                            if (!contains) {
                                name = Name.makeFresh("railvalue");
                                jna = new JavaArray(n.position(), type, array, ((SettableAssign_c) n).index().get(0), null, name);
                                targetAndIsFinals.add(new Pair<JavaArray, Boolean>(jna, true));
                            } else {
                                jna = new JavaArray(n.position(), type, array, ((SettableAssign_c) n).index().get(0), null, name);
                            }
                            return xnf.AmbAssign(n.position(), jna, ((SettableAssign_c) n).operator(), ((SettableAssign_c) n).right());
                        }
                    }
                    // rail = Rail.make(10) -> rail = Rail.make(10); railvaluexxx = (int[]) rail.value;
                    if (n instanceof Eval) {
                        if (((Eval) n).expr() instanceof PrivatizeExpr) {
                            Expr e = ((Eval) n).expr();
                            PrivatizeExpr privatize = (PrivatizeExpr) e;
                            Term rail = privatize.rail;
                            if (rail != null) {
                                if (!ignores.contains(rail.toString())) {
                                    if (rail instanceof Field && ignores.contains(((Field) rail).target().toString())) {
                                        return n;
                                    }
                                    if (rail instanceof JavaArray) {
                                        if (ignores.contains(((JavaArray) rail).index.toString())) {
                                            return n;
                                        }
                                    }

                                    for (int i = 0; i < targetAndIsFinals.size(); i++) {
                                      Pair<JavaArray, Boolean> pair = targetAndIsFinals.get(i);
                                      // already exist
                                      if (pair.fst().name.toString().equals(privatize.valName.toString())) {
                                          if (!privatize.isFinal) {
                                              targetAndIsFinals.set(i, new Pair<JavaArray, Boolean>(pair.fst(), false));
                                          }
                                          return null;
                                      }                                        
                                    }
                                    // ??
                                    moves.add((Stmt) xnf.Eval(e.position(), (Expr) e));
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
                                Name name = null;
                                for (int i = 0; i < targetAndIsFinals.size(); i++) {
                                    Pair<JavaArray, Boolean> pair = targetAndIsFinals.get(i);
                                    if (pair.fst().rail.toString().equals(local.toString())) {
                                        contains = true;
                                        name = pair.fst().name;
                                        targetAndIsFinals.set(i, new Pair<JavaArray, Boolean>(pair.fst(), false));
                                        break;
                                    }
                                }
                                if (!contains) {
                                    name = Name.makeFresh("railval");
                                    targetAndIsFinals.add(new Pair<JavaArray, Boolean>(new JavaArray(n.position(), type, local, null, null, name), false));
                                }
                                if (parent instanceof Block) {
                                    for (Stmt stmt : ((Block) parent).statements()) {
                                        if (stmt instanceof Eval) {
                                            Expr expr = ((Eval) stmt).expr();
                                            if (expr instanceof PrivatizeExpr) {
                                                if (((PrivatizeExpr) expr).valName.equals(name.toString())) {
                                                    return n;
                                                }
                                            }
                                        }
                                    }
                                }
                                List<Stmt> stmts = new ArrayList<Stmt>();
                                stmts.add((Stmt) n);
                                Type pt = X10TypeMixin.baseType(((X10ClassType) type).typeArguments().get(0));
                                PrivatizeExpr expr;
                                if (la.right() instanceof NullLit) {
                                    expr = new PrivatizeExpr(n.position(), pt, name.toString(), null, false, false);
                                }
                                else {
                                    expr = new PrivatizeExpr(n.position(), pt, name.toString(), local, false, false);
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
                        return null;
                    }
                    @Override
                    public Node leave(Node parent, Node old, Node n, NodeVisitor v) {
                        if (n instanceof X10Call) {
                            X10Call call = (X10Call) n;
                            Position pos = call.position();
                            Receiver target = call.target();
                            Type type = X10TypeMixin.baseType(call.type());
                            if (!(type instanceof ParameterType) && target != null && (xts.isRail(target.type()) || xts.isValRail(target.type())) && (call.methodInstance().name().toString().equals("apply") || call.methodInstance().name().toString().equals("set"))) {
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
                                if (target instanceof JavaArray) {
                                    JavaArray ja = (JavaArray) target;
                                    if (!xts.isValRail(ja.rail.type())) {
                                        return n;
                                    }
                                    if (!ja.index.isConstant() && !(ja.index instanceof Local && !ignores.contains(ja.index.toString()))) {
                                        return n;
                                    }
                                    boolean contain = false;
                                    for (Pair<JavaArray, Boolean> pair : targetAndIsFinals) {
                                        if (pair.fst().name.toString().equals(ja.name.toString())) {
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
                                Name name = null;
                                for (Pair<JavaArray, Boolean> pair : targetAndIsFinals) {
                                    if (pair.fst().rail.toString().equals(target.toString())) {
                                        contains = true;
                                        name = pair.fst().name;
                                        break;
                                    }
                                }

                                if (!contains) {
                                    name = Name.makeFresh("railval");
                                    targetAndIsFinals.add(new Pair<JavaArray, Boolean>(new JavaArray(pos, type, target, null, null, name), true));
                                }
                                return new JavaArray(pos, type, target, index, elem, name);
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

                                if (array instanceof JavaArray) {
                                    JavaArray ja = (JavaArray) array;
                                    if (!xts.isValRail(ja.rail.type())) {
                                        return n;
                                    }
                                    if (!ja.index.isConstant() && !(ja.index instanceof Local && !ignores.contains(ja.index.toString()))) {
                                        return n;
                                    }
                                    boolean contain = false;
                                    for (Pair<JavaArray, Boolean> pair : targetAndIsFinals) {
                                        if (pair.fst().name.toString().equals(ja.name.toString())) {
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
                                Name name = null;
                                for (Pair<JavaArray, Boolean> pair : targetAndIsFinals) {
                                    if (pair.fst().rail.toString().equals(array.toString())) {
                                        contains = true;
                                        name = pair.fst().name;
                                        break;
                                    }
                                }
                                JavaArray jna;
                                if (!contains) {
                                    name = Name.makeFresh("railvalue");
                                    jna = new JavaArray(n.position(), type, array, ((SettableAssign_c) n).index().get(0), null, name);
                                    targetAndIsFinals.add(new Pair<JavaArray, Boolean>(jna, true));
                                } else {
                                    jna = new JavaArray(n.position(), type, array, ((SettableAssign_c) n).index().get(0), null, name);
                                }
                                return xnf.AmbAssign(n.position(), jna, ((SettableAssign_c) n).operator(), ((SettableAssign_c) n).right());
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
            for (Pair<JavaArray, Boolean> pair : targetAndIsFinals) {
                Type type = X10TypeMixin.baseType(pair.fst().rail.type());
                if (type instanceof X10ClassType) {
                    Type pt = ((X10ClassType) type).typeArguments().get(0);
                    statements.add(xnf.Eval(n.position(), new PrivatizeExpr(n.position(), X10TypeMixin.baseType(pt), pair.fst().name.toString(), (Term) pair.fst().rail, true, pair.snd())));
                }
            }
            statements.add(loop);
            return xnf.StmtSeq(n.position(), statements);
        }

        return n;
    }

    private static class PrivatizeExpr extends Expr_c {

        private final Type type;
        private final String valName;
        private final Term rail;
        private final boolean isDecl;
        private final String privtzName;

        private boolean isFinal;

        public PrivatizeExpr(Position pos, Type type, String valName, Term rail, boolean isDecl, boolean isFinal) {
            super(pos);
            this.type = type;
            this.valName = valName;
            this.rail = rail;
            this.isDecl = isDecl;
            this.privtzName = null;
            this.isFinal = isFinal;
        }

        public PrivatizeExpr(Position pos, Type type, String valName, String privtzName, boolean isFinal) {
            super(pos);
            this.type = type;
            this.valName = valName;
            this.rail = null;
            this.isDecl = true;
            this.privtzName = privtzName;
            this.isFinal = isFinal;
        }

        @Override
        public List<Term> acceptCFG(CFGBuilder v, List<Term> succs) {
            return null;
        }

        @Override
        public void prettyPrint(CodeWriter w, PrettyPrinter pp) {
            X10Translator tr = (X10Translator) pp;
            Emitter er = new Emitter(w, tr);
            if (isFinal) {
                w.write("final ");
            }
            if (isDecl) {
                if (type.isBoolean() || type.isNumeric() || type.isChar()) {
                    er.printType(type, 0);
                } else {
                    w.write("java.lang.Object");
                }
                w.write("[] ");
            }
            w.write(valName);
            w.write(" = ");

            if (privtzName != null) {
                w.write(privtzName);
                return;
            }
            else if (rail == null) {
                w.write("null");
                return;
            }
            else {
                w.write("(");
                if (type.isBoolean() || type.isNumeric() || type.isChar()) {
                    er.printType(type, 0);
                } else {
                    w.write("java.lang.Object");
                }
                w.write("[]");
                w.write(")");

                er.prettyPrint(rail, tr);
                w.write(".value");
            }
        }

        public Term firstChild() {
            return null;
        }

        @Override
        public String toString() {
            return (isFinal ? "final " : "") + type + " " + valName + " = " + rail;
        }
    }

    private static class JavaArray extends Expr_c {

        private final Receiver rail;
        private final Expr index;
        private final Expr elem;
        private final Name name;

        public JavaArray(Position pos, Type type, Receiver rail, Expr index, Expr elem, Name name) {
            super(pos);
            super.type = type;
            this.rail = rail;
            this.index = index;
            this.elem = elem;
            this.name = name;
        }

        @Override
        public void prettyPrint(CodeWriter w, PrettyPrinter pp) {
            X10Translator tr = (X10Translator) pp;
            Emitter er = new Emitter(w, tr);
            if (elem == null && index == null) {
                w.write(name.toString());
                return;
            }
            if (elem == null && !(type.isBoolean() || type.isNumeric() || type.isChar())) {
                w.write("(");
                w.write("(");
                er.printType(type, X10PrettyPrinterVisitor.PRINT_TYPE_PARAMS);
                w.write(")");
            }
            w.write(name.toString());
            if (index != null) {
                w.write("[");
                er.prettyPrint(index, tr);
                w.write("]");
            }
            if (elem == null && !(type.isBoolean() || type.isNumeric() || type.isChar())) {
                w.write(")");
            }
            if (elem != null) {
                w.write(" = ");
                er.prettyPrint(elem, tr);
            }
        }

        @Override
        public List<Term> acceptCFG(CFGBuilder v, List<Term> succs) {
            return null;
        }

        public Term firstChild() {
            return null;
        }

        @Override
        public String toString() {
            return name.toString();
        }
    }
}
