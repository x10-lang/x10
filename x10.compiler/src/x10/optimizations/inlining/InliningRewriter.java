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
import polyglot.ast.Id;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.ProcedureDecl;
import polyglot.ast.Return;
import polyglot.ast.Special;
import polyglot.ast.Stmt;
import polyglot.ast.Throw;
import polyglot.frontend.Job;
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
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.ast.AssignPropertyCall;
import x10.ast.Closure;
import x10.ast.StmtSeq;
import x10.ast.X10ConstructorCall;
import x10.ast.X10ConstructorDecl;
import x10.ast.X10MethodDecl;
import x10.ast.X10Special;
import x10.types.MethodInstance;
import x10.types.X10FieldInstance;
import x10.util.AltSynthesizer;

/**
 * Rewrites a given method/closure body so that it has exactly one return
 * statement at the end if the return type is not void, and no return
 * statements if it's void. Also, replaces "this" parameter by a local
 * variable.
 * 
 * @author igor TODO: factor out into its own class
 */
public class InliningRewriter extends ContextVisitor {
    private final ProcedureDef def;
    private final LocalDef ths;
    private final LocalDef ret;
    private final Name label;
    private AltSynthesizer syn;
    private boolean[] failed = new boolean[1];

    public InliningRewriter(Closure closure, Job j, TypeSystem ts, NodeFactory nf, Context ctx) {
        this(closure.closureDef(), null, closure.body().statements(), j, ts, nf, ctx);
    }

    public InliningRewriter(ProcedureDecl decl, LocalDef ths, Job j, TypeSystem ts, NodeFactory nf, Context ctx) {
        this(decl.procedureInstance(), ths, decl.body().statements(), j, ts, nf, ctx);
    }

    public InliningRewriter(ConstructorDecl decl, LocalDef ths, Job j, TypeSystem ts, NodeFactory nf, Context ctx) {
        this(decl.constructorDef(), ths, decl.body().statements(), j, ts, nf, ctx);
    }

    private InliningRewriter(ProcedureDef def, LocalDef ths, List<Stmt> body, Job j, TypeSystem ts, NodeFactory nf, Context ctx) {
    	super(j, ts, nf);
        this.context = ctx;
        this.def = def;
        this.ths = ths;
        this.syn = new AltSynthesizer(ts, nf);
        if (def instanceof ConstructorDef) {
            this.ret = null;
            this.label = Name.makeFresh("__ret");
        } else if (body.size() == 1 && body.get(0) instanceof Return) {
            // Closure already has the right properties; make return rewriting a no-op
            this.ret = null;
            this.label = null;
        } else {
            Name rn = Name.makeFresh("ret");
            Type rt = def.returnType().get();
            this.ret = rt.isVoid() ? null : ts.localDef(def.position(), ts.NoFlags(), Types.ref(rt), rn);
            this.label = Name.makeFresh("__ret");
        }
        failed[0] = false;
    }

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
        if (n instanceof X10MethodDecl)
            return visitMethodDecl((X10MethodDecl) n);
        if (n instanceof X10ConstructorDecl) 
            return visitConstructorDecl((X10ConstructorDecl) n);
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

    private Block rewriteBody(Position pos, Block body) {
        if (failed[0]) {
            return null;
        }
        if (label == null) {
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

    // def m(`x:`T):R=S -> def m(`x:`T)={r:R; L:{ S[return v/r=v; break L;]; }; return r;}
    private X10MethodDecl visitMethodDecl(X10MethodDecl n) {
        // First check that we are within the right method
        if (n.methodDef() != def)
            return n;
        return (X10MethodDecl) n.body(rewriteBody(n.position(), n.body()));
    }
    
    // def this(`x:`T){S} -> def this(`x:`T)={L:{ S[return; / break L;] }; return;}
    private X10ConstructorDecl visitConstructorDecl(X10ConstructorDecl n) {
        // First check that we are within the right method
        if (n.constructorDef() != def)
            return n;
        return (X10ConstructorDecl) n.body(rewriteBody(n.position(), n.body()));
    }

    // (`x:`T):R=>S -> (`x:`T)=>{r:R; L:{ S[return v/r=v; break L;]; }; return r;}
    private Closure visitClosure(Closure n) {
        // First check that we are within the right closure
        if (n.closureDef() != def) return n;
        return (Closure) n.body(rewriteBody(n.position(), n.body()));
    }

    // return v; -> r=v; break L;
    private Stmt visitReturn(Return n) {
        // First check that we are within the right code body
        if (!context.currentCode().equals(def)) return n;
        if (label == null) return n;
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
        // First check that we are within the right code body
        if (!context.currentCode().equals(def)) return n;
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
        // First check that we are within the right code body
        if (!context.currentCode().equals(def)) return n;
        if (!n.isTargetImplicit()) return n;
        MethodInstance mi = n.methodInstance();
        assert ((ths == null) == (mi.flags().isStatic()));
        Position pos = n.position();
        if (mi.flags().isStatic()) {
            return n.target(nf.CanonicalTypeNode(pos, mi.container())).targetImplicit(false);
        }
        if (old.target() instanceof Special && ((Special) old.target()).kind() == X10Special.SUPER)
            n = n.nonVirtual(true); // make calls to "super.foo()" non-virtual
    
        return n.target(getThis(pos)).targetImplicit(false);
    }

    /**
     * @param n
     * @return
     */
    private Node visitX10ConstructorCall(X10ConstructorCall n) {
        // First check that we are within the right code body
        if (!context.currentCode().equals(def)) return n;
        if (null != n.target()) return n;
        return n.target(getThis(n.position()));
    }

    // this -> ths
    private Expr visitSpecial(Special n) {
        // First check that we are within the right code body
        if (!context.currentCode().equals(def)) return n;
        // Make sure ths is defined
        if (null == ths) return n; // nothing to be done (e.g. "this" in a closure)
        // Complicated cases don't get this far
        assert (n.kind() == X10Special.SUPER || n.kind() == X10Special.THIS);
        if (null != n.qualifier()) { // when the Inliner runs all outer classes have been stripped away, the qualifier should be redundant
            if (Inliner.DEBUG) Inliner.debug("Inliner ignoring special qualifier " +n.qualifier(), n);
        }
        // return a local for the inlined this
        return getThis(n.position());
    }
}