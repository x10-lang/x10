package polyglot.ext.x10.visit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import polyglot.ast.Assign;
import polyglot.ast.Block;
import polyglot.ast.Branch;
import polyglot.ast.ClassDecl;
import polyglot.ast.ClassMember;
import polyglot.ast.CodeNode;
import polyglot.ast.Conditional;
import polyglot.ast.Eval;
import polyglot.ast.Expr;
import polyglot.ast.FloatLit;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.If;
import polyglot.ast.IntLit;
import polyglot.ast.Lit;
import polyglot.ast.Local;
import polyglot.ast.LocalDecl;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Receiver;
import polyglot.ast.Return;
import polyglot.ast.SourceFile;
import polyglot.ast.Stmt;
import polyglot.ast.TypeNode;
import polyglot.ext.x10.ast.Closure;
import polyglot.ext.x10.ast.ClosureCall;
import polyglot.ext.x10.ast.TypeParamNode;
import polyglot.ext.x10.ast.X10Call;
import polyglot.ext.x10.ast.X10MethodDecl;
import polyglot.ext.x10.types.X10ClassDef;
import polyglot.ext.x10.types.X10ClassType;
import polyglot.ext.x10.types.X10MethodDef;
import polyglot.ext.x10.types.X10MethodInstance;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.frontend.Job;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.LocalInstance;
import polyglot.types.MethodDef;
import polyglot.types.Name;
import polyglot.types.QName;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;

/**
 * This visitor inlines calls to methods and closures under the following
 * conditions:
 * <ul>
 * <li>The exact class of the method target is known.
 * <li>The call appears in either an Eval (e.g., 'm();') or an Eval(Assign) with
 * operator = (e.g., 'x = m();').
 * <li>The method being invoked is annotated @x10.compiler.Inline
 * <li>The closure call target is a literal closure.
 * </ul>
 * 
 * @author nystrom
 * 
 */
public class Inliner extends ContextVisitor {

    Type InlineType;

    public Inliner(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
    }

    @Override
    public NodeVisitor begin() {
        try {
            InlineType = (Type) ts.systemResolver().find(QName.make("x10.compiler.Inline"));
        }
        catch (SemanticException e) {
            InlineType = null;
        }
        return super.begin();
    }

    /**
     * @param s_if_cannot_inline
     *            Statement to return if the method cannot be inlined.
     * @param c
     *            Call to inline.
     * @param md
     *            Definition of method to inline.
     * @return Either the method body, with suitable substitutions, or
     *         s_if_cannot_inline.
     */
    public Stmt inline(Stmt s_if_cannot_inline, X10Call c, final X10MethodDef md, Expr result) {
        // Don't inline recursively.  Recursive inlining will be handled when this method is inlined elsewhere.
        if (md == context().currentCode())
            return s_if_cannot_inline;
        
        Type container = Types.get(md.container());
        Type base = X10TypeMixin.baseType(container);
        if (base instanceof X10ClassType) {
            X10ClassDef cd = ((X10ClassType) base).x10Def();
            Job job = cd.job();
            if (job != null) {
                Node ast = job.ast();
                final X10MethodDecl[] decl = new X10MethodDecl[1];
                ast.visit(new NodeVisitor() {
                    public Node override(Node n) {
                        if (n instanceof Expr || n instanceof Stmt || n instanceof TypeNode) {
                            return n;
                        }
                        return null;
                    }

                    public Node leave(Node old, Node n, NodeVisitor v) {
                        if (n instanceof X10MethodDecl) {
                            X10MethodDecl d = (X10MethodDecl) n;
                            if (d.methodDef() == md)
                                decl[0] = d;
                        }
                        return n;
                    }
                });

                X10MethodDecl decl0 = decl[0];
                if (decl0 != null)
                    return rewriteBody(decl0.body(), decl0.typeParameters(), decl0.formals(), c.target(), c.typeArguments(), c.arguments(), result);
            }
        }
        return s_if_cannot_inline;
    }

    public Stmt inlineClosure(Stmt s_if_cannot_inline, Closure target, ClosureCall c, Expr result) {
        return rewriteBody(target.body(), target.typeParameters(), target.formals(), target, c.typeArgs(), c.arguments(), result);
    }

    public boolean simple(Expr e) {
        // HACK: treating closures as simple is correct only if we don't do == on closures.
        return e instanceof Lit || e instanceof Local || e instanceof Closure;
    }

    public LocalDecl makeFreshLocal(Expr e) {
        Type t = e.type();
        Name name = Name.makeFresh();
        Position pos = e.position();
        LocalDef def = ts.localDef(pos, Flags.FINAL, Types.ref(t), name);
        if (e.isConstant())
            def.setConstantValue(e.constantValue());
        else
            def.setNotConstant();
        LocalDecl decl = nf.LocalDecl(pos, nf.FlagsNode(pos, def.flags()), nf.CanonicalTypeNode(pos, def.type()), nf.Id(pos, name), e);
        decl = decl.localDef(def);
        return decl;
    }

    public Local makeLocal(LocalDecl decl) {
        Local l = nf.Local(decl.position(), decl.name());
        l = l.localInstance(decl.localDef().asInstance());
        l = (Local) l.type(decl.declType());
        return l;
    }
    
    /** Rewrite body, substituting actual parameters for formals.  If the body returns, the return value is assigned into result.
     * TODO: handle type params and type arguments.  */
    public Stmt rewriteBody(Block body, List<TypeParamNode> typeParams, List<Formal> formals, Receiver target, List<TypeNode> typeArgs,
            List<Expr> args, Expr result) {
        List<Stmt> decls = new ArrayList<Stmt>();
        List<Expr> vars = new ArrayList<Expr>();
        List<Formal> vformals = new ArrayList<Formal>();

        if (target instanceof Expr) {
            Expr e = (Expr) target;
            if (simple(e))
                vars.add(e);
            else {
                LocalDecl self = makeFreshLocal(e);
                decls.add(self);
                vars.add(makeLocal(self));
            }
            vformals.add(null);
        }
        for (int i = 0; i < args.size(); i++) {
            Expr e = args.get(i);
            if (simple(e))
                vars.add(e);
            else {
                LocalDecl self = makeFreshLocal(e);
                decls.add(self);
                vars.add(makeLocal(self));
            }
            vformals.add(formals.get(i));
        }

        assert vars.size() == formals.size();

        body = renameLocals(body);

        for (int i = 0; i < vars.size(); i++) {
            body = subst(body, vars.get(i), vformals.get(i));
        }

        body = prepend(body, decls);
        Stmt s = rewireControlFlow(result, body);

        return s;
    }

    /** Rename local variables in the block to avoid shadowing errors when inlining. */    
    private Block renameLocals(Block body) {
        final Map<Name,Name> map = new HashMap<Name, Name>();
        return (Block) body.visit(new NodeVisitor() {
            public Node override(Node n) {
                if (n instanceof ClassDecl)
                    return n;
                if (n instanceof Closure)
                    return n;
                if (n instanceof ClassMember)
                    return n;
                return null;
            }
            Name newName(Name name) {
                Name newName = map.get(name);
                if (newName == null) {
                    newName = Name.makeFresh(name.toString());
                    map.put(name, newName);
                }
                return newName;
            }
            @Override
            public Node leave(Node old, Node n, NodeVisitor v) {
                if (n instanceof LocalDecl) {
                    LocalDecl d = (LocalDecl) n;
                    Name name = d.name().id();
                    Name newName = newName(name);
                    d.localDef().setName(newName);
                    return d.name(d.name().id(newName));
                }
                if (n instanceof Local) {
                    Local d = (Local) n;
                    Name name = d.name().id();
                    Name newName = newName(name);
                    LocalInstance li = d.localInstance();
                    LocalDef def = li.def();
                    while (li.name() != newName) {
                        if (def.name() == newName) {
                            li = li.name(newName);
                        }
                        else {
                            def.setName(newName);
                        }
                    }
                    d = d.localInstance(li);
                    return d.name(d.name().id(newName));
                }
                return n;
            }
        });
    }

    /** Prepend the statements to the block. */
    private Block prepend(Block body, List<Stmt> ss) {
        List<Stmt> stmts = new ArrayList<Stmt>(ss.size() + body.statements().size());
        stmts.addAll(ss);
        stmts.addAll(body.statements());
        return body.statements(stmts);
    }

    private Stmt rewireControlFlow(final Expr result, Block body) {
        final Id label = nf.Id(body.position(), Name.makeFresh());
        final boolean[] loop = new boolean[1];

        Block b = (Block) body.visit(new NodeVisitor() {
            public Node override(Node n) {
                if (n instanceof CodeNode || n instanceof ClassMember)
                    return n;
                return null;
            }

            public Node leave(Node old, Node n, NodeVisitor v) {
                if (n instanceof Return) {
                    Return r = (Return) n;
                    Branch break_ = nf.Break(r.position(), label);
                    if (r.expr() == null) {
                        loop[0] = true;
                        return break_;
                    }
                    else {
                        if (result == null) {
                            LocalDecl d = makeFreshLocal(r.expr());
                            return nf.Block(r.position(), d, break_);
                        }
                        else {
                            Assign assign = nf.Assign(r.position(), result, Assign.ASSIGN, r.expr());
                            Eval eval = nf.Eval(r.position(), assign);
                            return nf.Block(r.position(), eval, break_);
                        }
                    }
                }
                return n;
            }
        });

        // return got rewritten to break
        // replace B with do B while (false)
        if (loop[0]) {
            Expr falsch = nf.BooleanLit(b.position(), false).type(ts.Boolean());
            return nf.Labeled(b.position(), label, nf.Do(b.position(), b, falsch));
        }
        return b;
    }

    private Block subst(Block body, final Expr e, final Formal x) {
        return (Block) body.visit(new NodeVisitor() {
            public Node leave(Node old, Node n, NodeVisitor v) {
                if (n instanceof Local) {
                    Local l = (Local) n;
                    if (l.localInstance().def() == x.localDef())
                        return e;
                }
                return n;
            }
        });
    }

    boolean inlined = false;
    static final int limit = 10;
    int count;

    @Override
    protected NodeVisitor enterCall(Node n) throws SemanticException {
        if (n instanceof SourceFile)
            inlined = false;
        return this;
    }

    /** Return a clone of this visitor, incrementing the counter. */
    public Inliner inc() {
        Inliner v = (Inliner) copy();
        v.count++;
        return v;
    }

    public Node propagate(Node n) {
        return n.visit(new ConstantPropagator(job, ts, nf));
    }

    public Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException {
        if (InlineType == null)
            return n;

        if (n instanceof SourceFile) {
            if (inlined)
                n.prettyPrint(System.out);
            return n;
        }

        if (n instanceof Eval) {
            Eval eval = (Eval) n;
            Stmt s = eval;

            if (eval.expr() instanceof Assign) {
                Assign a = (Assign) eval.expr();
                if (a.operator() == Assign.ASSIGN) {
                    Expr result = a.left(nf);
                    Expr call = a.right();
                    s = attemptInlineCall(eval, call, result);
                }
            }
            else {
                s = attemptInlineCall(eval, eval.expr(), null);
            }

            if (s != eval) {
                s = (Stmt) propagate(s);
                if (count < limit) {
                    return parent.visitChild(s, inc());
                }
            }

            return s;
        }

        return n;
    }

    private Stmt attemptInlineCall(Stmt s_if_not_inline, Expr call, Expr result) {
        if (call instanceof X10Call) {
            X10Call c = (X10Call) call;
            X10MethodInstance mi = (X10MethodInstance) c.methodInstance();
            X10MethodDef md = mi.x10Def();
            if (!md.annotationsMatching(InlineType).isEmpty()) {
                if (known(c.target())) {
                    inlined = true;
                    return inline(s_if_not_inline, c, md, result);
                }
            }
        }

        if (call instanceof ClosureCall) {
            ClosureCall c = (ClosureCall) call;
            if (c.target() instanceof Closure) {
                return inlineClosure(s_if_not_inline, (Closure) c.target(), c, result);
            }
        }

        return s_if_not_inline;
    }

    boolean known(Receiver r) {
        if (r instanceof TypeNode) {
            return true;
        }
        if (r instanceof Expr) {
            Expr target = (Expr) r;
            Type t = X10TypeMixin.baseType(target.type());
            if (t instanceof X10ClassType) {
                X10ClassType ct = (X10ClassType) t;
                if (ct.flags().isFinal()) {
                    return true;
                }
            }
            if (target instanceof New)
                return true;
        }
        return false;
    }
}
