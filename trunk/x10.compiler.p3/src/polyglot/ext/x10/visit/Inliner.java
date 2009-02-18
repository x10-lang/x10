package polyglot.ext.x10.visit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import polyglot.ast.Assign;
import polyglot.ast.Block;
import polyglot.ast.Branch;
import polyglot.ast.Call;
import polyglot.ast.Cast;
import polyglot.ast.ClassDecl;
import polyglot.ast.ClassMember;
import polyglot.ast.CodeNode;
import polyglot.ast.ConstructorCall;
import polyglot.ast.Eval;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FieldAssign;
import polyglot.ast.For;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.IntLit;
import polyglot.ast.Lit;
import polyglot.ast.Local;
import polyglot.ast.LocalDecl;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Receiver;
import polyglot.ast.Return;
import polyglot.ast.Special;
import polyglot.ast.Stmt;
import polyglot.ast.TypeNode;
import polyglot.ext.x10.ast.Closure;
import polyglot.ext.x10.ast.ClosureCall;
import polyglot.ext.x10.ast.SettableAssign_c;
import polyglot.ext.x10.ast.TypeParamNode;
import polyglot.ext.x10.ast.X10Call;
import polyglot.ext.x10.ast.X10Cast;
import polyglot.ext.x10.ast.X10Cast_c;
import polyglot.ext.x10.ast.X10MethodDecl;
import polyglot.ext.x10.ast.X10NodeFactory;
import polyglot.ext.x10.types.X10ClassDef;
import polyglot.ext.x10.types.X10ClassType;
import polyglot.ext.x10.types.X10MethodDef;
import polyglot.ext.x10.types.X10MethodInstance;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.frontend.Job;
import polyglot.main.Report;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.LocalInstance;
import polyglot.types.MemberInstance;
import polyglot.types.MethodInstance;
import polyglot.types.Name;
import polyglot.types.QName;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.types.TypeSystem_c.TypeEquals;
import polyglot.util.InternalCompilerError;
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
                if (decl0 != null) {
                    if (canInlineHere(decl0.body(), context().currentClassDef())) {
                        if (Report.should_report("inline", 1))
                            Report.report(1, "Inlining call " + c + " into " + context().currentCode());
                        return rewriteBody(decl0.body(), decl0.typeParameters(), decl0.formals(), c.target(), c.typeArguments(), c.arguments(), result, cd);
                    }
                    else {
                        if (Report.should_report("inline", 1))
                            Report.report(1, "Cannot inline call " + c + " into " + context().currentCode() + "; inlined code would be illegal");
                    }
                }
            }
        }
        return s_if_cannot_inline;
    }

    private boolean canInlineHere(Block body, final ClassDef currentClassDef) {
        final boolean[] result = new boolean[1];
        result[0] = true;
        body.visit(new NodeVisitor() {
            @Override
            public Node override(Node n) {
                if (! result[0])
                    return n;
                return null;
            }
            @Override
            public Node leave(Node old, Node n, NodeVisitor v) {
                if (n instanceof Field) {
                    Field f = (Field) n;
                    if (! accessible(f.fieldInstance()))
                        result[0] = false;
                }
                if (n instanceof FieldAssign) {
                    FieldAssign f = (FieldAssign) n;
                    if (! accessible(f.fieldInstance()))
                        result[0] = false;
                }
                if (n instanceof Call) {
                    Call f = (Call) n;
                    if (! accessible(f.methodInstance()))
                        result[0] = false;
                }
                if (n instanceof ClosureCall) {
                    ClosureCall f = (ClosureCall) n;
                    if (! accessible(f.closureInstance()))
                        result[0] = false;
                }
                if (n instanceof ConstructorCall) {
                    ConstructorCall f = (ConstructorCall) n;
                    if (! accessible(f.constructorInstance()))
                        result[0] = false;
                }
                if (n instanceof New) {
                    New f = (New) n;
                    if (! accessible(f.constructorInstance()))
                        result[0] = false;
                }
                return super.leave(old, n, v);
            }
            private boolean accessible(MemberInstance mi) {
                return ts.isAccessible(mi, currentClassDef);
            }
        });
        return result[0];
    }

    public Stmt inlineClosure(Closure target, ClosureCall c, Expr result) {
        if (Report.should_report("inline", 1))
            Report.report(1, "Inlining closure call " + c + " into " + context().currentCode());
        return rewriteBody(target.body(), target.typeParameters(), target.formals(), target, c.typeArgs(), c.arguments(), result, null);
    }

    public boolean simple(Expr e) {
        // HACK: treating closures as simple is correct only if we don't do == on closures.
        return e instanceof Lit || e instanceof Local || e instanceof Closure;
    }

    String nameOf(Expr e) {
        if (e instanceof Field)
            return ((Field) e).name().id().toString();
        if (e instanceof Local)
            return ((Local) e).name().id().toString();
        return "tmp";
    }

    public LocalDecl makeFreshLocal(Expr e, Flags flags) {
        Type t = e.type();
        Name name = Name.makeFresh(nameOf(e));
        Position pos = e.position();
        LocalDef def = ts.localDef(pos, flags, Types.ref(t), name);
        if (staticTypeIsExact(e) && flags.isFinal())
            known.add(def);
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
            List<Expr> args, Expr result, ClassDef calleeClass) {
        List<Stmt> decls = new ArrayList<Stmt>();
        List<Expr> vars = new ArrayList<Expr>();
        List<Formal> vformals = new ArrayList<Formal>();

        if (target instanceof Expr) {
            Expr e = (Expr) target;
            if (simple(e))
                vars.add(e);
            else {
                LocalDecl self = makeFreshLocal(e, Flags.FINAL);
                decls.add(self);
                vars.add(makeLocal(self));
            }
            vformals.add(null);
        }
        for (int i = 0; i < args.size(); i++) {
            Expr e = args.get(i);
            Formal f = formals.get(i);
            if (simple(e))
                vars.add(e);
            else {
                LocalDecl self = makeFreshLocal(e, f.flags().flags());
                decls.add(self);
                vars.add(makeLocal(self));
            }
            vformals.add(formals.get(i));
        }

        assert vars.size() == formals.size();

        body = renameLocals(body);

        for (int i = 0; i < vars.size(); i++) {
            if (vformals.get(i) != null)
                body = subst(body, vars.get(i), vformals.get(i));
            else
                body = substThis(body, vars.get(i), calleeClass);
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
        final Id label = nf.Id(body.position(), Name.makeFresh("label"));
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
                    loop[0] = true;
                    if (r.expr() == null) {
                        return break_;
                    }
                    else {
                        if (result == null) {
                            LocalDecl d = makeFreshLocal(r.expr(), Flags.FINAL);
                            return nf.Block(r.position(), d, break_);
                        }
                        else {
                            try {
                                Assign assign = assign(r.position(), result, Assign.ASSIGN, r.expr());
                                Eval eval = nf.Eval(r.position(), assign);
                                return nf.Block(r.position(), eval, break_);
                            }
                            catch (SemanticException e) {
                                throw new InternalCompilerError(e);
                            }
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

    private Assign assign(Position pos, Expr e, Assign.Operator asgn, Expr val) throws SemanticException {
        NodeFactory xnf = nf;
        TypeSystem xts = ts;
        Assign a = (Assign) nf.Assign(pos, e, asgn, val).type(e.type());
        if (a instanceof FieldAssign) {
            assert (e instanceof Field);
            assert ((Field) e).fieldInstance() != null;
            a = ((FieldAssign) a).fieldInstance(((Field)e).fieldInstance());
        } else if (a instanceof SettableAssign_c) {
            assert (e instanceof X10Call);
            MethodInstance ami = ((X10Call)e).methodInstance();
            List<Type> aTypes = new ArrayList<Type>(ami.formalTypes());
            aTypes.add(0, ami.returnType());
            MethodInstance smi = xts.findMethod(ami.container(),
                                                xts.MethodMatcher(ami.container(), Name.make("set"), aTypes),
                                                context.currentClassDef());
            a = ((SettableAssign_c) a).methodInstance(smi);
        }
        return a;
    }

    private Block subst(Block body, final Expr e, final Formal x) {
        return (Block) body.visit(new NodeVisitor() {
            public Node leave(Node old, Node n, NodeVisitor v) {
                if (n instanceof Local) {
                    Local l = (Local) n;
                    LocalInstance li = l.localInstance();
                    LocalDef ld = li.def();
                    LocalDef xd = x.localDef();
                    if (ld == xd)
                        return e;
                }
                return n;
            }
        });
    }

    private Block substThis(Block body, final Expr e, final ClassDef thisType) {
        return (Block) body.visit(new NodeVisitor() {
            public Node leave(Node old, Node n, NodeVisitor v) {
                if (n instanceof Field) {
                    Field f = (Field) n;
                    assert f.target() != null;
                }
                if (n instanceof FieldAssign) {
                    FieldAssign a = (FieldAssign) n;
                    assert a.target() != null;
                }
                if (n instanceof Call) {
                    Call c = (Call) n;
                    assert c.target() != null;
                }
                if (n instanceof Special) {
                    Special s = (Special) n;
                    if (s.kind() == Special.THIS) {
                        Type t = s.type();
                        Type b = X10TypeMixin.baseType(t);
                        if (b instanceof ClassType) {
                            ClassDef cd = ((ClassType) b).def();
                            if (cd == thisType)
                                return e;
                        }
                    }
                }
                return n;
            }
        });
    }

    static final int limit = 10;
    int count;

    /** Return a clone of this visitor, incrementing the counter. */
    public Inliner inc() {
        Inliner v = (Inliner) copy();
        v.count++;
        return v;
    }

    public Node propagate(Node n) {
        ConstantPropagator cp = new ConstantPropagator(job, ts, nf);
        cp = (ConstantPropagator) cp.context(context());
        return n.visit(cp);
    }

    @Override
    protected NodeVisitor enterCall(Node parent, Node n) throws SemanticException {
        if (n instanceof LocalDecl) {
            LocalDecl d = (LocalDecl) n;
            LocalDef def = d.localDef();
            if (def.flags().isFinal() && d.init() != null && staticTypeIsExact(d.init())) {
                known.add(def);
            }
        }
        return this;
    }

    public Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException {
        if (InlineType == null)
            return n;

        if (n instanceof Return) {
            Return r = (Return) n;

            if (r.expr() != null) {
                Expr call = r.expr();
                LocalDecl d = makeFreshLocal(call, Flags.FINAL);
                d = d.init(null);
                Assign a = nf.LocalAssign(r.position(), makeLocal(d), Assign.ASSIGN, call);
                Eval eval = nf.Eval(r.position(), a);
                r = r.expr(makeLocal(d));
                return nf.Block(r.position(), d, (Stmt) leaveCall(parent, old, eval, v), r);
            }
        }
        
        if (n instanceof LocalDecl) {
            LocalDecl d = (LocalDecl) n;
            Node s = d;

            // Do not inline call occurring in a for-loop header.
            if (parent instanceof For && ((For) parent).body() != d)
                return d;

            if (d.init() != null) {
                Expr result = makeLocal(d);
                Expr call = d.init();
                s = attemptInlineCall(d, call, result);
                if (s != d) {
                    s = nf.NodeList(d.position(), Arrays.<Node>asList(d.init(null), s));
                }
            }

            if (s != d) {
                s = propagate(s);
                if (count < limit) {
                    return parent.visitChild(s, inc());
                }
            }

            return s;
        }

        if (n instanceof Eval) {
            Eval eval = (Eval) n;
            Node s = eval;

            // Do not inline call occurring in a for-loop header.
            if (parent instanceof For && ((For) parent).body() != eval)
                return eval;

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
                s = propagate(s);
                if (count < limit) {
                    return parent.visitChild(s, inc());
                }
            }

            return s;
        }

        return n;
    }

    private Stmt inlineSpecial(X10Call c, X10MethodDef md, Expr result) {
        X10TypeSystem ts = (X10TypeSystem) this.ts;
        X10NodeFactory nf = (X10NodeFactory) this.nf;
        
        // Rail.makeVal( 4, (x) => e )
        // ->
        // [ ((x) => e)(0) ... ((x) => e)(3) ]
        try {
            if (md.name() == Name.make("makeVal") && ts.isRail(Types.get(md.container())) && c.arguments().size() == 2) {
                Expr len = c.arguments().get(0);
                Expr init = c.arguments().get(1);
                if (init instanceof Cast && ((Cast) init).expr() instanceof Closure) {
                    init = ((Cast) init).expr();
                }
                if (init instanceof Closure) {
                    if (len.isConstant()) {
                        Object v = len.constantValue();
                        if (v instanceof Integer) {
                            int n = (Integer) v;
                            List<Expr> args = new ArrayList<Expr>(n);

                            for (int i = 0; i < n; i++) {
                                Expr ni = X10Cast_c.check(nf.IntLit(c.position(), IntLit.INT, i), this);
                                Expr ai = X10Cast_c.check(nf.ClosureCall(c.position(), init, Collections.EMPTY_LIST, Collections.singletonList(ni)), this);
                                args.add(ai);
                            }

                            Expr e = X10Cast_c.check(nf.Tuple(c.position(), args), this);

                            if (! ts.typeEquals(e.type(), c.type())) {
                                e = nf.X10Cast(c.position(), nf.CanonicalTypeNode(c.position(), c.type()), e, false);
                                e = X10Cast_c.check(e, this);
                            }

                            if (result == null) {
                            }
                            else {
                                try {
                                    Assign assign = assign(c.position(), result, Assign.ASSIGN, e);
                                    Eval eval = nf.Eval(c.position(), assign);
                                    return eval;
                                }
                                catch (SemanticException ex) {
                                    throw new InternalCompilerError(ex);
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (SemanticException e) {
            return null;
        }

        return null;
    }

    private Stmt attemptInlineCall(Stmt s_if_not_inline, Expr call, Expr result) {
        if (call instanceof X10Call) {
            X10Call c = (X10Call) call;
            X10MethodInstance mi = (X10MethodInstance) c.methodInstance();
            X10MethodDef md = mi.x10Def();

            Stmt s = inlineSpecial(c, md, result);
            if (s != null)
                return s;

            if (!md.annotationsMatching(InlineType).isEmpty()) {
                if (staticTypeIsExact(c.target()) || md.flags().isFinal()) {
                    return inline(s_if_not_inline, c, md, result);
                }
            }
        }

        if (call instanceof ClosureCall) {
            ClosureCall c = (ClosureCall) call;
            if (c.target() instanceof Closure) {
                return inlineClosure((Closure) c.target(), c, result);
            }
        }

        return s_if_not_inline;
    }

    Set<LocalDef> known = new HashSet<LocalDef>();

    boolean staticTypeIsExact(Receiver r) {
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
            if (target instanceof Special && ((Special) target).kind() == Special.SUPER)
                return true;
            if (target instanceof X10Cast) {
                X10Cast c = (X10Cast) target;
                switch (c.conversionType()) {
                case CHECKED:
                    return staticTypeIsExact(c.expr());
                default:
                    return false;
                }
            }
            if (target instanceof New) {
                return true;
            }
            if (target instanceof Local) {
                Local l = (Local) target;
                LocalInstance li = l.localInstance();
                LocalDef d = li.def();
                if (known.contains(d))
                    return true;
            }
        }
        return false;
    }
}
