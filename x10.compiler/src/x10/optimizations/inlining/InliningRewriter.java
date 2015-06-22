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

package x10.optimizations.inlining;

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.AmbAssign;
import polyglot.ast.AmbExpr;
import polyglot.ast.AmbTypeNode;
import polyglot.ast.Assign;
import polyglot.ast.Block;
import polyglot.ast.Branch;
import polyglot.ast.Call;
import polyglot.ast.ConstructorDecl;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FieldAssign;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.ProcedureDecl;
import polyglot.ast.Return;
import polyglot.ast.Special;
import polyglot.ast.Stmt;
import polyglot.ast.Throw;
import polyglot.frontend.Job;
import polyglot.types.CodeDef;
import polyglot.types.ConstructorDef;
import polyglot.types.Context;
import polyglot.types.FieldInstance;
import polyglot.types.LocalDef;
import polyglot.types.LocalInstance;
import polyglot.types.Name;
import polyglot.types.ProcedureDef;
import polyglot.types.QName;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.types.VarDef;
import polyglot.types.VarInstance;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.AssignPropertyCall;
import x10.ast.Closure;
import x10.ast.Closure_c;
import x10.ast.StmtSeq;
import x10.ast.X10ConstructorCall;
import x10.ast.X10ConstructorDecl;
import x10.ast.X10MethodDecl;
import x10.ast.X10Special;
import x10.types.ClosureDef;
import x10.types.MethodInstance;
import x10.types.X10FieldInstance;
import x10.util.AltSynthesizer;

/**
 * Rewrites a given method/closure body so that it has exactly one return
 * statement at the end if the return type is not void, and no return
 * statements if it's void. Also, replaces "this" parameter by a local
 * variable.
 * 
 * @author igor
 */
public class InliningRewriter extends ContextVisitor {
    private final CodeDef def;
    private final LocalDef ths;
    private final LocalDef ret;
    private final Name label;
    private final boolean rewriteReturn;
    private AltSynthesizer syn;
    private boolean[] failed = new boolean[1];

    public InliningRewriter(Closure closure, Job j, TypeSystem ts, NodeFactory nf, Context ctx) {
        this(closure.closureDef(), null, closure.body(), j, ts, nf, ctx);
    }

    public InliningRewriter(ProcedureDecl decl, LocalDef ths, Job j, TypeSystem ts, NodeFactory nf, Context ctx) {
        this(decl.procedureInstance(), ths, decl.body(), j, ts, nf, ctx);
    }

    private InliningRewriter(ProcedureDef def, LocalDef ths, Block body, Job j, TypeSystem ts, NodeFactory nf, Context ctx) {
        super(j, ts, nf);
        this.context = ctx;
        this.def = ctx.currentCode();
        this.ths = ths;
        this.syn = new AltSynthesizer(ts, nf);
        
        ReturnCounter rc = new ReturnCounter();
        body.visit(rc);
        this.rewriteReturn = !rc.inCannonicalForm(body);
        if (def instanceof ConstructorDef) {
            this.ret = null;
        } else {
            Type rt = def.returnType().get();
            this.ret = rt.isVoid() || !this.rewriteReturn ? null : ts.localDef(def.position(), ts.NoFlags(), Types.ref(rt), Name.makeFresh("ret"));
        }
        
        this.label = Name.makeFresh("__ret");
        failed[0] = false;
    }
    
    private static class ReturnCounter extends NodeVisitor {
        private int[] count = new int[1];
        private boolean[] hasThrow = new boolean[1];
        
        ReturnCounter() {
            super();
        }
        
        public Node leave(Node old, Node n, NodeVisitor v) {
            if (n instanceof Return) {
                count[0]++;
            } else if (n instanceof Throw) {
                hasThrow[0] = true;
            }
            return n;
        }
        
        public boolean inCannonicalForm(Block body) {
            return !hasThrow[0] && count[0] == 1 && (body.statements().get(body.statements().size()-1) instanceof Return);
        }
    }

    @Override
    public Node override(Node n) {
        if (failed[0]) 
            return n; // abort visit
        if (def == null)
            return n;
        return null;
    }

    // TODO: use override to short-circuit the traversal
    public Node leaveCall(Node old, Node n, NodeVisitor v)
            throws SemanticException {
        if (v != this) {
        }
        if (n instanceof AmbExpr || n instanceof AmbAssign || n instanceof AmbTypeNode) {
            throw new InternalCompilerError("Ambiguous node found: " + n, n.position());
        }
        if (n instanceof Closure)
            return visitClosure((Closure) n);
        if (n instanceof Return)
            return visitReturn((Return) n);
        if (n instanceof Throw)
            return visitThrow((Throw) n);
        if (n instanceof Field)
            return visitField((Field) n);
        if (n instanceof Call) { 
            return visitCall((Call) old, (Call) n);
        }
        if (n instanceof X10ConstructorCall)
            return visitX10ConstructorCall((X10ConstructorCall) n);
        if (n instanceof Special)
            return visitSpecial((Special) n);
        if (n instanceof AssignPropertyCall)
            return visitAssignPropertyCall((AssignPropertyCall)  n);
        return n;
    }

    public Block rewriteBody(Position pos, Block body) {
        if (body == null) {
            return null;
        }
        if (failed[0]) {
            return null;
        }
        if (label == null || !rewriteReturn) {
            return body;
        }
        List<Stmt> newBody = new ArrayList<Stmt>();
        if (ret != null) {
            newBody.add(nf.LocalDecl( pos,
                                       nf.FlagsNode(pos, ts.NoFlags()),
                                       nf.CanonicalTypeNode(pos, ret.type()),
                                       nf.Id(pos, ret.name()) ).localDef(ret));
        }
        // A return at the end of the method will have been converted to a
        // break. It's not needed. Turf it.
        List<Stmt> bodyStmts = body.statements();
        if (!bodyStmts.isEmpty() && (bodyStmts.get(bodyStmts.size() - 1) instanceof Branch)) {
            Branch br = (Branch) bodyStmts.get(bodyStmts.size() - 1);
            if (br.kind() == Branch.BREAK) {
                Id breakLabel = br.labelNode();
                if (breakLabel.id().equals(label)) {
                    List<Stmt> statements = new ArrayList<Stmt>();
                    for (Stmt stmt : bodyStmts) {
                        if (stmt != br) {
                            statements.add(stmt);
                        }
                    }
                    body = nf.Block(body.position(), statements);
                }
            }
        }
        newBody.add(nf.Labeled(pos, nf.Id(pos, label), body));
        if (ret != null) {
            Expr rval = nf.Local(pos, nf.Id(pos, ret.name())).localInstance(ret.asInstance()).type(ret.type().get());
            newBody.add(nf.Return(pos, rval));
        } else {
            newBody.add(nf.Return(pos));
        }
        return nf.Block(body.position(), newBody);
    }

    public static Block rewriteProcedureBody(ProcedureDecl decl, LocalDef thisDef, Job job, Context cxt) {
        cxt = cxt.pushBlock();
        if (thisDef != null) {
            cxt.addVariable(thisDef.asInstance());
        }
        for (Formal f : decl.formals()) {
            cxt.addVariable(f.localDef().asInstance());
        }
        InliningRewriter rewriter = new InliningRewriter(decl, thisDef, job, job.extensionInfo().typeSystem(), job.extensionInfo().nodeFactory(), cxt);
        Block body = (Block) decl.body().visit(rewriter);
        return rewriter.rewriteBody(decl.position(), body);
    }

    // def m(`x:`T):R=S -> {r:R; L:{ S[return v/r=v; break L;]; }; return r;}
    public static Block rewriteMethodBody(X10MethodDecl decl, LocalDef thisDef, Job job, Context cxt) {
        return rewriteProcedureBody(decl, thisDef, job, cxt);
    }

    // def this(`x:`T){S} -> {L:{ S[return; / break L;] }; return;}
    public static Block rewriteConstructorBody(X10ConstructorDecl decl, LocalDef thisDef, Job job, Context cxt) {
        return rewriteProcedureBody(decl, thisDef, job, cxt);
    }

    // (`x:`T):R=>S -> {r:R; L:{ S[return v/r=v; break L;]; }; return r;}
    public static Block rewriteClosureBody(Closure cl, Job job, Context cxt) {
        cxt = cxt.pushBlock();
        for (Formal f : cl.formals()) {
            cxt.addVariable(f.localDef().asInstance());
        }
        InliningRewriter rewriter = new InliningRewriter(cl, job, job.extensionInfo().typeSystem(), job.extensionInfo().nodeFactory(), cxt);
        Block body = (Block) cl.body().visit(rewriter);
        return rewriter.rewriteBody(cl.position(), body); // Ensure that the last statement of the body is the only return in the closure
    }

    private Closure visitClosure(Closure n) {
        // First propagate the captured environment outward
        ClosureDef cd = n.closureDef();
        // an inlined closure cannot capture anything but locals in the caller, so clean up the captured environment
        if (ths != null) {
            List<VarInstance<?>> env = new ArrayList<VarInstance<?>>(cd.capturedEnvironment().size());
            for (VarInstance<?> vi : cd.capturedEnvironment()) {
                if (vi instanceof LocalInstance) {
                    env.add(vi);
                }
            }
            if (env.size() != cd.capturedEnvironment().size()) {
                cd.setCapturedEnvironment(env); // The closure def should have been reinstantiated
            }
        }
        Closure_c.propagateCapturedEnvironment(context, cd);
        return n;
    }

    // return v; -> r=v; break L;
    private Stmt visitReturn(Return n) {
        // First check that we are within the right code body
        if (!context.currentCode().equals(def)) return n;
        if (label == null) return n;
        if (!rewriteReturn) return n;
        assert ((ret == null) == (n.expr() == null));
        Position pos = n.position();
        List<Stmt> retSeq = new ArrayList<Stmt>();
        if (ret != null) {
            Type rt = ret.type().get();
            Expr xl = nf.Local(pos, nf.Id(pos, ret.name())).localInstance(ret.asInstance()).type(rt);
            retSeq.add(nf.Eval(pos, nf.Assign(pos, xl, Assign.ASSIGN, n.expr()).type(rt)));
        }
        retSeq.add(nf.Break(pos, nf.Id(pos, label)));
        return nf.StmtSeq(pos, retSeq);
    }

    // throw e; -> if (true) throw e;
    private Stmt visitThrow(Throw n) throws SemanticException {
        // First check that we are within the right code body
        if (!context.currentCode().equals(def)) return n;
        if (label == null) return n;
        return syn.createIf(n.position(), createOpaqueTrue(n.position()), n, null);
    }

    // property(e1, e2, ... en) -> { p1=e1; p2=e2; ... pn=en; }
    private Node visitAssignPropertyCall(AssignPropertyCall n) {
        // First check that we are within the right code body
        if (!context.currentCode().equals(def)) return n;
        List<Stmt> stmts = new ArrayList<Stmt>();
        List<Expr> args = n.arguments();
        List<X10FieldInstance> props = n.properties();
        for (int i=0; i < args.size(); i++) {
            Expr arg = args.get(i);
            X10FieldInstance prop = props.get(i);
            FieldAssign assign = syn.createFieldAssign(n.position(), getThis(n.position()), prop, arg, this);
            stmts.add(syn.createEval(assign));
        }
        StmtSeq result = syn.createStmtSeq(n.position(), stmts);
        return result;
    }

    /**
     * @param pos
     * @return
     * @throws SemanticException 
     */
    private Expr createOpaqueTrue(Position pos) throws SemanticException {
        QName qname = QName.make("x10.compiler.CompilerFlags");
        Type container = typeSystem().forName(qname);
        Name name = Name.make("TRUE");
        Expr expr = syn.createStaticCall(pos, container, name);
        return expr;
    }

    private Expr getThis(Position pos) {
        LocalInstance li = ths.asInstance();
        return nf.Local(pos, nf.Id(pos, li.name())).localInstance(li).type(li.type());
    }

    // f -> ths.f
    private Field visitField(Field n) {
        if (!n.isTargetImplicit()) return n;
        FieldInstance fi = n.fieldInstance();
        assert ((ths == null) == (fi.flags().isStatic()));
        Position pos = n.position();
        if (fi.flags().isStatic()) {
            return n.target(nf.CanonicalTypeNode(pos, fi.container())).targetImplicit(false);
        }
        return n.target(getThis(pos)).targetImplicit(false);
    }

    // m(...) -> ths.m(...)
    private Call visitCall(Call old, Call n) {
        if (old.target() instanceof Special && ((Special) old.target()).kind() == X10Special.SUPER)
            n = n.nonVirtual(true); // make calls to "super.foo()" non-virtual
        if (!n.isTargetImplicit()) return n;
        MethodInstance mi = n.methodInstance();
        assert ((ths == null) == (mi.flags().isStatic()));
        Position pos = n.position();
        if (mi.flags().isStatic()) {
            return n.target(nf.CanonicalTypeNode(pos, mi.container())).targetImplicit(false);
        }
        return n.target(getThis(pos)).targetImplicit(false);
    }

    // this(...) -> ths.this(...)
    private Node visitX10ConstructorCall(X10ConstructorCall n) {
        if (null != n.target()) return n;
        return n.target(getThis(n.position()));
    }

    // this -> ths
    private Expr visitSpecial(Special n) {
        // First make sure ths is defined
        if (null == ths) return n; // nothing to be done (e.g. "this" in a closure)
        // Complicated cases don't get this far
        assert (n.kind() == X10Special.SUPER || n.kind() == X10Special.THIS);
        if (null != n.qualifier()) { // when the Inliner runs all outer classes have been stripped away, the qualifier should be redundant
            if (Inliner.DEBUG) Inliner.debug("Inliner ignoring special qualifier " +n.qualifier(), n);
        }
        context.recordCapturedVariable(ths.asInstance());
        // return a local for the inlined this
        return getThis(n.position());
    }
}
