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

package x10.visit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import polyglot.ast.AmbAssign;
import polyglot.ast.AmbExpr;
import polyglot.ast.AmbTypeNode;
import polyglot.ast.Assign;
import polyglot.ast.Block;
import polyglot.ast.Call;
import polyglot.ast.Cast;
import polyglot.ast.ClassDecl;
import polyglot.ast.ClassMember;
import polyglot.ast.CodeNode;
import polyglot.ast.ConstructorCall;
import polyglot.ast.Do;
import polyglot.ast.Empty;
import polyglot.ast.Eval;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FieldAssign;
import polyglot.ast.For;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.IntLit;
import polyglot.ast.Labeled;
import polyglot.ast.Lit;
import polyglot.ast.Local;
import polyglot.ast.LocalAssign;
import polyglot.ast.LocalDecl;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Receiver;
import polyglot.ast.Return;
import polyglot.ast.Special;
import polyglot.ast.Stmt;
import polyglot.ast.SwitchBlock;
import polyglot.ast.TypeNode;
import polyglot.frontend.Job;
import polyglot.main.Report;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.Context;
import polyglot.types.FieldInstance;
import polyglot.types.Flags;
import polyglot.types.FunctionDef;
import polyglot.types.FunctionInstance;
import polyglot.types.LocalDef;
import polyglot.types.LocalInstance;
import polyglot.types.MemberInstance;
import polyglot.types.MethodDef;
import polyglot.types.MethodInstance;
import polyglot.types.Name;
import polyglot.types.QName;
import polyglot.types.Ref;
import polyglot.types.SemanticException;
import polyglot.types.StructType;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeCheckPreparer;
import x10.Configuration;
import x10.ast.Closure;
import x10.ast.ClosureCall;
import x10.ast.ParExpr;
import x10.ast.SettableAssign;
import x10.ast.StmtExpr;
import x10.ast.TypeParamNode;
import x10.ast.X10Call;
import x10.ast.X10Cast;
import x10.ast.X10Local_c;
import x10.ast.X10MethodDecl;
import x10.ast.X10NodeFactory;
import x10.types.ClosureDef;
import x10.types.ConstrainedType;
import x10.types.ParameterType;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import x10.types.X10MethodDef;
import x10.types.X10MethodInstance;
import x10.types.X10TypeMixin;
import x10.types.X10TypeSystem;
import x10.types.X10Use;
import x10.types.checker.Converter;
import x10.util.Synthesizer;
import x10cpp.visit.X10SearchVisitor;

/**
 * This visitor inlines calls to methods and closures under the following
 * conditions:
 * <ul>
 * <li>The exact class of the method target is known.
 * <li>The method being invoked is annotated @x10.compiler.Inline .
 * <li>The closure call target is a literal closure.
 * </ul>
 * 
 * Note that the code produced by the inliner is technically not valid
 * X10 code -- it may use statement expressions, the variable
 * declarations in inner blocks may shadow those in the outer blocks,
 * and it may contain calls to methods and constructors (and accesses
 * to fields) that would not normally be accessible from the starting
 * context.
 * 
 * To produce valid X10 code (by limiting the contexts for inlining),
 * set ALLOW_STMTEXPR to false.
 * 
 * @author nystrom
 * @author igor
 */
public class Inliner extends ContextVisitor {

    /**
     * Allow inlining in all contexts.  Can only be done if the backend supports
     * emitting statement expression nodes.  Otherwise, only inline in statement
     * contexts.
     * 
     * This constant also controls the use of other non-X10 features, like
     * having shadowed variable declarations, calls to inaccessible methods and
     * constructors, and accesses to inaccessible fields.
     */
    private static final boolean ALLOW_STMTEXPR = true;

    /**
     * A rather arbitrary inlining depth limit.
     */
    private static final int INLINE_DEPTH_LIMIT = 10;

    /**
     * The cached type of the @Inline annotation.
     */
    private Type InlineType;

    public Inliner(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
    }

    @Override
    public NodeVisitor begin() {
        try {
            InlineType = (Type) ts.systemResolver().find(QName.make("x10.compiler.Inline"));
        }
        catch (SemanticException e) {
            System.out.println("Unable to find x10.compiler.Inline: "+e);
            InlineType = null;
        }
        return super.begin();
    }

    private X10ClassDef getContainer(X10MethodDef md) {
        Type containerBase = X10TypeMixin.baseType(Types.get(md.container()));
        assert (containerBase instanceof X10ClassType);
        return ((X10ClassType) containerBase).x10Def();
    }

    private X10MethodDecl getDeclaration(final X10MethodDef md) {
        if (md.annotationsMatching(InlineType).isEmpty()) return null;
        if (!md.flags().isStatic() && !md.flags().isFinal()) return null;
        X10ClassDef cd = getContainer(md);
        Job job = cd.job();
        if (job == null) return null;
        Node ast = job.ast();
        ast = ast.visit(new X10TypeChecker(job, ts, nf, job.nodeMemo()).begin());
//        job.ast(ast);
//        System.out.println("==> Typechecked "+cd);
        final X10MethodDecl[] decl = new X10MethodDecl[1];
        ast.visit(new NodeVisitor() {
            public Node override(Node n) {
                if (n instanceof Expr || n instanceof Stmt || n instanceof TypeNode) {
                    return n;
                }
                if (decl[0] != null) {
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
        return decl[0];
    }

    private Type instantiate(X10MethodInstance mi, List<Type> tArgs, ParameterType t) {
        List<Type> tParms = mi.typeParameters();
        int i = 0;
        for (Type tp : tParms) {
            if (t.typeEquals(tp, context))
                return tArgs.get(i);
            ++i;
        }
        X10ClassType ct = (X10ClassType) mi.container();
        X10ClassDef cd = ct.x10Def();
        tArgs = ct.typeArguments();
        List<ParameterType> tPs = cd.typeParameters();
        i = 0;
        for (ParameterType pt : tPs) {
            if (t.typeEquals(pt, context))
                return tArgs.get(i);
            ++i;
        }
        return null;
    }

    private Type instantiate(X10MethodInstance mi, List<Type> tArgs, ConstrainedType t) {
        Type bt = Types.get(t.baseType());
        Type ibt = instantiate(mi, tArgs, bt);
        if (ibt == bt) return t;
        return X10TypeMixin.constrainedType(ibt, Types.get(t.constraint()));
    }

    private Type instantiate(X10MethodInstance mi, List<Type> tArgs, Type t) {
        if (t instanceof ConstrainedType) {
            return instantiate(mi, tArgs, (ConstrainedType) t);
        } else if (t instanceof ParameterType) {
            return instantiate(mi, tArgs, (ParameterType) t);
        }
        return t;
    }

    private X10MethodDecl instantiate(final X10MethodDecl decl, X10Call c) {
        final X10MethodInstance mi = (X10MethodInstance) c.methodInstance();
        final List<Type> tArgs = new ArrayList<Type>();
        for (TypeNode tn : c.typeArguments()) {
            tArgs.add(tn.type());
        }
        List<Type> tParms = mi.typeParameters();
        final HashMap<Name, LocalDef> vars = new HashMap<Name, LocalDef>();
        return (X10MethodDecl) decl.visit(new ContextVisitor(job, ts, nf) {
            protected Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException {
                if (n instanceof TypeNode) {
                    Type type = ((TypeNode) n).type();
                    Type iType = instantiate(mi, tArgs, type);
                    if (iType != type) {
                        return ((TypeNode) n).typeRef(Types.ref(iType));
                    }
                }
                if (n instanceof Expr) {
                    Expr e = (Expr) n;
                    Expr ie = e.type(instantiate(mi, tArgs, e.type()));
                    if (ie instanceof X10Call) {
                        X10Call c = (X10Call) ie;
                        if (c.isTargetImplicit()) {
                            c = (X10Call) c.targetImplicit(false);
                        }
                        return c.typeCheck(this);
                    } else if (ie instanceof Field) {
                        Field f = (Field) ie;
                        if (f.isTargetImplicit()) {
                            f = (Field) f.targetImplicit(false);
                        }
                        return f.typeCheck(this);
                    } else if (ie instanceof Local) {
                        LocalDef ld = vars.get(((Local) ie).name().id());
                        if (ld != null) {
                            return ((Local) ie).localInstance(ld.asInstance());
                        }
                    }
                    return ie;
                }
                if (n instanceof LocalDecl) {
                    LocalDecl d = (LocalDecl) n;
                    if (d.type() != ((LocalDecl) old).type()) {
                        LocalDef ld = d.localDef();
                        Name name = ld.name();
                        LocalDef ild = ts.localDef(ld.position(), ld.flags(), d.type().typeRef(), name);
                        vars.put(name, ild);
                        return d.localDef(ild);
                    }
                }
                if (n instanceof Formal) {
                    Formal f = (Formal) n;
                    if (f.type() != ((Formal) old).type()) {
                        LocalDef ld = f.localDef();
                        Name name = ld.name();
                        LocalDef ild = ts.localDef(ld.position(), ld.flags(), f.type().typeRef(), name);
                        vars.put(name, ild);
                        return f.localDef(ild);
                    }
                }
                return n;
            }
        }.context(context()));
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
    public Stmt inline(Stmt s_if_not_inline, X10Call c, X10MethodInstance mi, Expr result) {
        if (!staticTypeIsExact(c.target()) && !mi.flags().isFinal())
            return s_if_not_inline;

        final X10MethodDef md = mi.x10Def();

        // Don't inline recursively.  Recursive inlining will be handled when this method is inlined elsewhere.
        if (md == context().currentCode())
            return s_if_not_inline;

        X10MethodDecl decl = getDeclaration(md);
        if (decl == null)
            return s_if_not_inline;
        if (!canInlineHere(decl.body(), context().currentClassDef())) {
            if (Report.should_report("inline", 1))
                Report.report(1, "Cannot inline call " + c + " into " + context().currentCode() + "; inlined code would be illegal");
            return s_if_not_inline;
        }
        if (Report.should_report("inline", 1))
            Report.report(1, "Inlining call " + c + " into " + context().currentCode());
        return rewriteBody(decl.body(), decl.typeParameters(), decl.formals(), c.target(), c.typeArguments(), c.arguments(), result, getContainer(md));
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
                return ts.isAccessible(mi, context);
            }
        });
        return result[0];
    }

    public Stmt inlineClosure(Closure target, ClosureCall c, Expr result) {
        if (Report.should_report("inline", 1))
            Report.report(1, "Inlining closure call " + c + " into " + context().currentCode());
        return rewriteBody(target.body(), Collections.EMPTY_LIST, target.formals(), target, 
        		Collections.EMPTY_LIST, c.arguments(), result, null);
    }

    public boolean simple(Expr e) {
        // HACK: treating closures as simple is correct only if we don't do == on closures.
        return e instanceof Lit || e instanceof Local || e instanceof Closure || e.type().isNull() || (e instanceof ParExpr && simple(((ParExpr) e).expr()));
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
        assert ! t.isNull() : "null type for " + e;
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

        assert vars.size() == vformals.size();

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
        body.visit(new NodeVisitor() {
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
                }
                return n;
            }
        });
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
                return newName;
            }
            @Override
            public Node leave(Node old, Node n, NodeVisitor v) {
                if (n instanceof LocalDecl) {
                    LocalDecl d = (LocalDecl) n;
                    Name name = d.name().id();
                    Name newName = newName(name);
                    if (newName != null) {
                        d.localDef().setName(newName);
                        return d.name(d.name().id(newName));
                    }
                }
                if (n instanceof LocalAssign) {
                    LocalAssign d = (LocalAssign) n;
                    Name name = d.local().name().id();
                    Name newName = newName(name);
                    if (newName != null) {
                        LocalInstance li = d.local().localInstance();
                        li = renameLI(newName, li);
                        d = d.local(d.local().localInstance(li));
                        return d;
                    }
                }
                if (n instanceof Local) {
                    Local d = (Local) n;
                    Name name = d.name().id();
                    Name newName = newName(name);
                    if (newName != null) {
                        LocalInstance li = d.localInstance();
                        li = renameLI(newName, li);
                        d = d.localInstance(li);
                        return d.name(d.name().id(newName));
                    }
                }
                return n;
            }
            private LocalInstance renameLI(Name newName, LocalInstance li) {
                LocalDef def = li.def();
                while (li.name() != newName) {
                    if (def.name() == newName) {
                        li = li.name(newName);
                    }
                    else {
                        def.setName(newName);
                    }
                }
                return li;
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

        class HasReturn extends NodeVisitor {
            boolean result = false;

            public Node override(Node n) {
                if (n instanceof CodeNode || n instanceof ClassMember)
                    return n;

                if (n instanceof Block && ! (n instanceof SwitchBlock)) {
                    Block b = (Block) n;
                    for (int i = 0; i < b.statements().size()-1; i++) {
                        HasReturn r = new HasReturn();
                        Stmt s = b.statements().get(i);
                        b.visitChild(s, r);
                        if (r.result) {
                            result = true;
                            return n;
                        }
                    }

                    if (b.statements().size() > 0) {
                        Stmt s = b.statements().get(b.statements().size()-1);
                        if (s instanceof Return)
                            return n;
                        b.visitChild(s, this);
                    }
                }

                if (n instanceof Return) {
                    result = true;
                }

                if (result)
                    return n;

                return null;
            }
        }

        final  HasReturn hr = new HasReturn();
        body.visit(hr);

        Block b = (Block) body.visit(new NodeVisitor() {
            public Node override(Node n) {
                if (n instanceof CodeNode || n instanceof ClassMember)
                    return n;
                return null;
            }

            public Node leave(Node old, Node n, NodeVisitor v) {
                if (n instanceof Block) {
                    Block b = (Block) n;
                    List<Stmt> ss = new ArrayList<Stmt>(b.statements().size());
                    for (Stmt s : b.statements()) {
                        if (s instanceof Empty)
                            ;
                        else
                            ss.add(s);
                    }
                    if (ss.size() != b.statements().size()) {
                        return b.statements(ss);
                    }
                    return b;
                }
                
                if (n instanceof Return) {
                    Return r = (Return) n;
                    Stmt break_ = hr.result ? nf.Break(r.position(), label) : nf.Empty(r.position());
                    if (r.expr() == null) {
                        return break_;
                    }
                    else {
                        if (result == null) {
                            if (simple(r.expr()))
                                return break_;
                            LocalDecl d = makeFreshLocal(r.expr(), Flags.FINAL);
                            if (break_ instanceof Empty)
                                return nf.Block(r.position(), d);
                            return nf.Block(r.position(), d, break_);
                        }
                        else {
                            try {
                                Assign assign = assign(r.position(), result, Assign.ASSIGN, r.expr());
                                Eval eval = nf.Eval(r.position(), assign);
                                if (break_ instanceof Empty)
                                    return eval;
                                return nf.Block(r.position(), eval, break_);
                            }
                            catch (SemanticException e) {
                                throw new InternalCompilerError(e);
                            }
                        }
                    }
                }

                if (n instanceof ParExpr) {
                    ParExpr e = (ParExpr) n;
                    if (e.expr() instanceof Local) {
                        return e.expr();
                    }
                }

                return n;
            }
        });

        // return got rewritten to break
        // replace B with do B while (false)
        if (hr.result) {
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
        } else if (a instanceof SettableAssign) {
            assert (e instanceof X10Call);
            MethodInstance ami = ((X10Call)e).methodInstance();
            List<Type> aTypes = new ArrayList<Type>(ami.formalTypes());
            aTypes.add(0, ami.returnType());
            MethodInstance smi = xts.findMethod(ami.container(),
                                                xts.MethodMatcher(ami.container(), Name.make("set"), aTypes, context));
            a = ((SettableAssign) a).methodInstance(smi);
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
                    return f.targetImplicit(false);
                }
                if (n instanceof FieldAssign) {
                    FieldAssign a = (FieldAssign) n;
                    assert a.target() != null;
                    return a.targetImplicit(false);
                }
                if (n instanceof Call) {
                    Call c = (Call) n;
                    assert c.target() != null;
                    return c.targetImplicit(false);
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

    private int count;

    /** Return a clone of this visitor, incrementing the counter. */
    public Inliner inc() {
        Inliner v = (Inliner) copy();
        v.count++;
        return v;
    }

    public Node propagateConstants(Node n) {
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

    class MoreThanOne extends Exception { 
        public MoreThanOne() {
        }
    }

    // Get the expression assigned to var in s.  Return null if s has any other effects or than assignment to var or doesn't assign var.
    public Expr getVarRhs(Stmt s, Local var, boolean returnOk) {
        try {
            HashMap<LocalDef,Expr> copies = new HashMap<LocalDef,Expr>();
            Set<LocalDef> local = new HashSet<LocalDef>();
            Expr e = getExpr(s, var, returnOk, copies, local);
            return e;
        }
        catch (MoreThanOne e) {
            return null;
        }
    }
    
    private void killDef(final LocalDef def, Map<LocalDef, Expr> map) {
        for (final java.util.Iterator<Map.Entry<LocalDef, Expr>> i = map.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry<LocalDef, Expr> entry = i.next();
            Expr a = entry.getValue();
            if (a == null)
                continue;
            a.visit(new NodeVisitor() {
                @Override
                public Node override(Node n) {
                    if (n instanceof Local) {
                        Local l = (Local) n;
                        if (l.localInstance().def() == def) {
                            i.remove();
                            return n;
                        }
                    }
                    return null;
                } 
            });
        }
    }

    public Expr lookup(Map<LocalDef, Expr> copies, Expr e) {
        if (e instanceof Local) {
            Local l = (Local) e;
            Expr x = copies.get(l.localInstance().def());
            if (x != null)
                return lookup(copies, x);
        }
        return e;
    }

    // Get the expression assigned to var in s.  Return null if s does not assign var.  Throw if s has any other effects than assignment to var.
    public Expr getExpr(Stmt s, Local var, boolean returnOk, Map<LocalDef,Expr> copies, Set<LocalDef> local) throws MoreThanOne {
        if (s instanceof Block) {
            Expr result = null;
            Block b = (Block) s;
            for (Stmt si : b.statements()) {
                Expr e = getExpr(si, var, returnOk, copies, local);
                if (e != null) {
                    if (result != null)
                        throw new MoreThanOne();
                    result = e;
                }
            }
            return result;
        }

        if (s instanceof Eval) {
            Eval eval = (Eval) s;
            Expr e = eval.expr();
            if (e instanceof LocalAssign) {
                LocalAssign a = (LocalAssign) e;
                copies.put(a.local().localInstance().def(), a.right());
                killDef(a.local().localInstance().def(), copies);
                if (a.local().localInstance().def() == var.localInstance().def())
                    return lookup(copies, a.right());
                if (local.contains(a.local().localInstance().def()))
                    return null;
            }
            throw new MoreThanOne();
        }

        if (s instanceof LocalDecl) {
            LocalDecl d = (LocalDecl) s;
            local.add(d.localDef());
            if (d.init() != null) {
                if (d.init().isConstant())
                    return null;
                throw new MoreThanOne();
            }
            return null;
        }

        if (s instanceof Labeled) {
            Labeled l = (Labeled) s;
            return getExpr(l.statement(), var, returnOk, copies, local);
        }

        if (s instanceof Do) {
            Do l = (Do) s;
            if (l.cond().isConstant() && l.cond().constantValue().equals(Boolean.FALSE))
                return getExpr(l.body(), var, returnOk, copies, local);
            else
                throw new MoreThanOne();
        }

        if (s instanceof Return && returnOk) {
            Return r = (Return) s;
            if (r.expr() instanceof Local) {
                Local l = (Local) r.expr();
                if (l.localInstance().def() == var.localInstance().def())
                    return null;
            }
        }
        
        if (s instanceof Empty) {
            return null;
        }

        throw new MoreThanOne();
    }

    /**
     * Cast a given expression to a given type, unless it is already of that type.
     * TODO: factor out to Synthesizer
     * @param a the given expression
     * @param fType the given type
     * @return a cast node, or the original expression
     */
    private Expr cast(Expr a, Type fType) {
        X10TypeSystem xts = (X10TypeSystem) typeSystem();
        X10NodeFactory xnf = (X10NodeFactory) nodeFactory();
        Context context = context();
        if (!xts.typeDeepBaseEquals(fType, a.type(), context)) {
            Position pos = a.position();
            a = xnf.X10Cast(pos, xnf.CanonicalTypeNode(pos, fType), a,
                            Converter.ConversionType.UNCHECKED).type(fType);
        }
        return a;
    }

    /**
     * Create a reference to a local variable with a given def.
     * TODO: factor out to Synthesizer.
     * @param pos the position
     * @param t the given local def
     * @return the local node
     */
    private Expr local(Position pos, LocalDef t) {
        X10NodeFactory xnf = (X10NodeFactory) nodeFactory();
        LocalInstance li = t.asInstance();
        return xnf.Local(pos, xnf.Id(pos, li.name())).localInstance(li).type(li.type());
    }

    private LocalDef computeThis(MethodDef def) {
        X10TypeSystem xts = (X10TypeSystem) typeSystem();
        if (def.flags().isStatic()) return null;
        return xts.localDef(def.position(), xts.Final(), def.container(), Name.makeFresh("this"));
    }

    /**
     * Rewrites a given closure so that it has exactly one return statement at the end.
     * @author igor
     * TODO: factor out into its own class
     */
    public class InliningRewriter extends ContextVisitor {
        private final FunctionDef def;
        private final LocalDef ths;
        private final LocalDef ret;
        private final Name label;
        public InliningRewriter(Closure closure) {
            this(closure.closureDef(), null, closure.body().statements());
        }
        public InliningRewriter(X10MethodDecl decl, LocalDef ths) {
            this(decl.methodDef(), ths, decl.body().statements());
        }
        private InliningRewriter(FunctionDef def, LocalDef ths, List<Stmt> body) {
            super(Inliner.this.job(), Inliner.this.typeSystem(), Inliner.this.nodeFactory());
            this.context = Inliner.this.context();
            this.def = def;
            this.ths = ths;
            if (body.size() == 1 && body.get(0) instanceof Return) {
                // Closure already has the right properties; make return rewriting a no-op
                this.ret = null;
                this.label = null;
            } else {
                X10TypeSystem xts = (X10TypeSystem) typeSystem();
                Name rn = Name.makeFresh("ret");
                Type rt = def.returnType().get();
                this.ret = rt.isVoid() ? null : xts.localDef(def.position(), xts.NoFlags(), Types.ref(rt), rn);
                this.label = Name.makeFresh("__ret");
            }
        }
        public Node override(Node n) {
            if (def == null) return n;
            return null;
        }
        // TODO: use override to short-circuit the traversal
        public Node leaveCall(Node old, Node n, NodeVisitor v) throws SemanticException {
            if (n instanceof AmbExpr || n instanceof AmbAssign || n instanceof AmbTypeNode) {
                throw new InternalCompilerError("Ambiguous node found: "+n, n.position());
            }
            if (n instanceof X10MethodDecl)
                return visitMethodDecl((X10MethodDecl)n);
            if (n instanceof Closure)
                return visitClosure((Closure)n);
            if (n instanceof Return)
                return visitReturn((Return)n);
            if (n instanceof Field)
                return visitField((Field)n);
            if (n instanceof Call)
                return visitCall((X10Call)n);
            if (n instanceof X10Call)
                return visitCall((X10Call)n);
            if (n instanceof Special)
                return visitSpecial((Special)n);
            return n;
        }
        private Block rewriteBody(Position pos, Block body) throws SemanticException {
            if (label == null) return body;
            X10NodeFactory xnf = (X10NodeFactory) nodeFactory();
            X10TypeSystem xts = (X10TypeSystem) typeSystem();
            List<Stmt> newBody = new ArrayList<Stmt>();
            if (ret != null) {
                newBody.add(xnf.LocalDecl(pos, xnf.FlagsNode(pos, xts.NoFlags()),
                            xnf.CanonicalTypeNode(pos, ret.type()),
                            xnf.Id(pos, ret.name())).localDef(ret));
            }
            newBody.add(xnf.Labeled(pos, xnf.Id(pos, label),
                        xnf.Do(pos, body, (Expr) xnf.BooleanLit(pos, false).typeCheck(this))));
            if (ret != null) {
                Expr rval = xnf.Local(pos, xnf.Id(pos, ret.name())).localInstance(ret.asInstance()).type(ret.type().get());
                newBody.add(xnf.Return(pos, rval));
            } else {
                newBody.add(xnf.Return(pos));
            }
            return xnf.Block(body.position(), newBody);
        }
        // def m(`x:`T):R=S -> def m(`x:`T)={r:R; L:do{ S[return v/r=v; break L;]; }while(false); return r;}
        private X10MethodDecl visitMethodDecl(X10MethodDecl n) throws SemanticException {
            // First check that we are within the right method
            if (n.methodDef() != def) return n;
            return (X10MethodDecl) n.body(rewriteBody(n.position(), n.body()));
        }
        // (`x:`T):R=>S -> (`x:`T)=>{r:R; L:do{ S[return v/r=v; break L;]; }while(false); return r;}
        private Closure visitClosure(Closure n) throws SemanticException {
            // First check that we are within the right closure
            if (n.closureDef() != def) return n;
            return (Closure) n.body(rewriteBody(n.position(), n.body()));
        }
        // return v; -> r=v; break L;
        private Stmt visitReturn(Return n) throws SemanticException {
            // First check that we are within the right code body
            if (!context.currentCode().equals(def)) return n;
            if (label == null) return n;
            assert ((ret == null) == (n.expr() == null));
            X10NodeFactory xnf = (X10NodeFactory) nf;
            Position pos = n.position();
            List<Stmt> retSeq = new ArrayList<Stmt>();
            if (ret != null) {
                Type rt = ret.type().get();
                Expr xl = xnf.Local(pos, xnf.Id(pos, ret.name())).localInstance(ret.asInstance()).type(rt);
                retSeq.add(xnf.Eval(pos, xnf.Assign(pos, xl, Assign.ASSIGN, n.expr()).type(rt)));
            }
            retSeq.add(xnf.Break(pos, xnf.Id(pos, label)));
            return xnf.StmtSeq(pos, retSeq);
        }
        private Expr getThis(Position pos) {
            X10NodeFactory xnf = (X10NodeFactory) nf;
            LocalInstance tli = ths.asInstance();
            return xnf.Local(pos, xnf.Id(pos, tli.name())).localInstance(tli).type(tli.type());
        }
        // f -> ths.f
        private Field visitField(Field n) throws SemanticException {
            // First check that we are within the right code body
            if (!context.currentCode().equals(def)) return n;
            if (!n.isTargetImplicit()) return n;
            FieldInstance fi = n.fieldInstance();
            assert ((ths == null) == (fi.flags().isStatic()));
            X10NodeFactory xnf = (X10NodeFactory) nf;
            Position pos = n.position();
            if (fi.flags().isStatic()) {
                return n.target(xnf.CanonicalTypeNode(pos, fi.container())).targetImplicit(false);
            }
            return n.target(getThis(pos)).targetImplicit(false);
        }
        // m(...) -> ths.m(...)
        private X10Call visitCall(X10Call n) throws SemanticException {
            // First check that we are within the right code body
            if (!context.currentCode().equals(def)) return n;
            if (!n.isTargetImplicit()) return n;
            MethodInstance mi = n.methodInstance();
            assert ((ths == null) == (mi.flags().isStatic()));
            X10NodeFactory xnf = (X10NodeFactory) nf;
            Position pos = n.position();
            if (mi.flags().isStatic()) {
                return (X10Call) n.target(xnf.CanonicalTypeNode(pos, mi.container())).targetImplicit(false);
            }
            return (X10Call) n.target(getThis(pos)).targetImplicit(false);
        }
        // this -> ths
        private Expr visitSpecial(Special n) throws SemanticException {
            // First check that we are within the right code body
            if (!context.currentCode().equals(def)) return n;
            if (n.kind() == Special.SUPER) {
                throw new InternalCompilerError("super not supported when inlining", n.position());
            }
            assert (n.kind() == Special.THIS);
            Position pos = n.position();
            return getThis(pos);
        }
    }

    // TODO: generate a closure call instead of a statement expression
    private Expr rewriteInlinedBody(Position pos, Type retType, List<Formal> formals, Block body, LocalDecl ths, List<Expr> args) {
        boolean clashes = false;
        for (Expr a : args) {
            X10SearchVisitor<X10Local_c> xLocals = new X10SearchVisitor<X10Local_c>(X10Local_c.class);
            a.visit(xLocals);
            if (!xLocals.found()) continue;
            ArrayList<X10Local_c> locals = xLocals.getMatches();
            for (X10Local_c t : locals) {
                Name name = t.localInstance().name();
                for (Formal f : formals) {
                    if (f.name().id().equals(name)) clashes = true;
                }
            }
        }

        List<Stmt> bodyStmts = body.statements();
        assert (bodyStmts.get(bodyStmts.size()-1) instanceof Return) : "Last statement is not a return";
        // We know that the last statement has to be a return
        Return ret = (Return) bodyStmts.get(bodyStmts.size()-1);
        List<Stmt> statements = new ArrayList<Stmt>();
        for (Stmt stmt : bodyStmts) {
            if (stmt != ret) {
                statements.add(stmt);
            }
        }
        Expr e = ret.expr();
        e = cast(e, retType);

        X10NodeFactory xnf = (X10NodeFactory) nodeFactory();
        X10TypeSystem xts = (X10TypeSystem) typeSystem();
        Synthesizer synth = new Synthesizer(xnf, xts);
        StmtExpr result = (StmtExpr) xnf.StmtExpr(pos, statements, e).type(retType);

        List<Stmt> declarations = new ArrayList<Stmt>();
        if (ths != null) {
            declarations.add(ths);
        }
        LocalDef[] alt = null;
        if (clashes) {
            alt = new LocalDef[args.size()];
            int i = 0;
            for (Expr a : args) {
                Type fType = formals.get(i).type().type();
                Name tmp = Name.makeFresh();
                alt[i] = xts.localDef(pos, xts.Final(), Types.ref(fType), tmp);
                declarations.add(synth.makeLocalVar(pos, alt[i], a));
                i++;
            }
        }
        int i = 0;
        for (Expr a : args) {
            Formal f = formals.get(i);
            Expr v = clashes ? local(pos, alt[i]) : a;
            declarations.add(synth.makeLocalVar(pos, f.localDef(), v));
            i++;
        }

        return result.prepend(declarations);
    }

    private Expr inlineMethodCall(X10Call c, X10MethodDecl decl, List<Expr> args) {
        if (decl.body() == null) return c;
        LocalDef ths = computeThis(decl.methodDef());
//        System.out.println("About to inline "+decl+" at "+c.position());
//        decl.dump(System.out);
        // Ensure that the last statement of the body is the only return in the method
        decl = (X10MethodDecl) decl.visit(new InliningRewriter(decl, ths));
        LocalDecl ld = null;
        if (ths != null) {
            X10NodeFactory xnf = (X10NodeFactory) nodeFactory();
            X10TypeSystem xts = (X10TypeSystem) typeSystem();
            Position tpos = ths.position();
            ld = xnf.LocalDecl(tpos,
                    xnf.FlagsNode(tpos, ths.flags()),
                    xnf.CanonicalTypeNode(tpos, ths.type()),
                    xnf.Id(tpos, ths.name()),
                    (Expr) c.target()).localDef(ths);
        }
        return rewriteInlinedBody(c.position(), decl.returnType().type(), decl.formals(), decl.body(), ld, args);
    }

    private Expr inlineClosureCall(ClosureCall c, Closure closure, List<Expr> args) {
        // Ensure that the last statement of the body is the only return in the closure
        closure = (Closure) closure.visit(new InliningRewriter(closure));
        return rewriteInlinedBody(c.position(), closure.returnType().type(), closure.formals(), closure.body(), null, args);
    }

    private Closure getClosureLiteral(Expr target) {
        if (target instanceof Closure)
            return (Closure) target;
        if (target instanceof ParExpr)
            return getClosureLiteral(((ParExpr)target).expr());
        return null;
    }

    public Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException {
        if (!ALLOW_STMTEXPR) return n;  // FIXME: for now

        if (n instanceof X10Call) {
            X10Call c = (X10Call) n;
            if (InlineType == null) return n;
            try {
            X10MethodInstance mi = (X10MethodInstance) c.methodInstance();
            X10MethodDecl decl = getDeclaration(mi.x10Def());
            if (decl != null) {
                decl = instantiate(decl, c);
                List<Expr> args = new ArrayList<Expr>();
                int counter = 0;
                for (Expr a : c.arguments()) {
                    Type fType = mi.formalTypes().get(counter);
                    a = cast(a, fType);
                    args.add(a);
                    counter++;
                }
                Expr result = inlineMethodCall(c, decl, args);
                result = (Expr) propagateConstants(result);
                result = (Expr) result.visit(this);
                return result;
            }
            } catch (InternalCompilerError e) {
                throw new InternalCompilerError("Error while inlining", n.position(), e);
            }
        }

        if (n instanceof ClosureCall) {
            ClosureCall c = (ClosureCall) n;
            // If the target is a closure literal, inline the body
            Closure lit = getClosureLiteral(c.target());
            if (lit != null) {
                X10MethodInstance ci = c.closureInstance();
                X10TypeSystem xts = (X10TypeSystem) typeSystem();
                X10NodeFactory nf = (X10NodeFactory) nodeFactory();
                List<Expr> args = new ArrayList<Expr>();
                int counter = 0;
                for (Expr a : c.arguments()) {
                    Type fType = ci.formalTypes().get(counter);
                    a = cast(a, fType);
                    args.add(a);
                    counter++;
                }
                Expr result = inlineClosureCall(c, lit, args);
                result = (Expr) propagateConstants(result);
                result = (Expr) result.visit(this);
                return result;
            }
            return c;
        }

        // Inline simple getters and setters // FIXME: dead for now
        if (n instanceof ClosureCall) {
            ClosureCall c = (ClosureCall) n;
            if (c.arguments().size() == 1 && c.target() instanceof Expr && ((Expr) c.target()).isConstant() && ((Expr) c.target()).constantValue() instanceof Object[]) {
                Expr len = c.arguments().get(0);
                if (len.isConstant()) {
                    Object val = len.constantValue();
                    if (val instanceof Integer) {
                        int i = (Integer) val;
                        Object[] a =  (Object[]) ((Expr) c.target()).constantValue();

                        try {
                            if (0 <= i && i < a.length) {
                                Expr e = new ConstantPropagator(job, ts, nf).toExpr(a[i], c.position());
                                return e;
                            }
                        }
                        catch (SemanticException ex) {
                        }
                    }
                }
            }

            if (c.target() instanceof Closure) {
                LocalDecl d = makeFreshLocal(c, Flags.FINAL);
                d = d.init(null);
                Local var = makeLocal(d);
                Stmt s = inlineClosure((Closure) c.target(), c, var);
                s = (Stmt) propagateConstants(s);
                Expr e = getVarRhs(s, var, false);
                if (e != null) {
                    if (count < INLINE_DEPTH_LIMIT) {
                        e = (Expr) parent.visitChild(e, inc());
                    }
                    return e;
                }
            }
        }

        /*
        if (n instanceof Return) {
            Return r = (Return) n;

            if (r.expr() != null) {
                Expr call = r.expr();
                if (call instanceof X10Call || call instanceof ClosureCall) {
                    LocalDecl d = makeFreshLocal(call, Flags.FINAL);
                    d = d.init(null);
                    Local var = makeLocal(d);
                    Assign a = assign(r.position(), var, Assign.ASSIGN, call);
                    Eval eval = nf.Eval(r.position(), a);
                    r = r.expr(var);
                    Stmt s = (Stmt) leaveCall(parent, old, eval, v);
                    Expr e = getVarRhs(s, var, false);
                    if (e != null) {
                        if (count < INLINE_DEPTH_LIMIT) {
                            e = (Expr) parent.visitChild(e, inc());
                        }
                        return r.expr(e);
                    }
                    return nf.Block(r.position(), d, s, r);
                }
                else {
                    return r;
                }
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
                    if (s instanceof Eval) {
                        Eval eval = (Eval) s;
                        if (eval.expr() instanceof LocalAssign) {
                            LocalAssign localAssign = (LocalAssign) eval.expr();
                            if (localAssign.local().localInstance().def() == d.localDef()) {
                                LocalDecl d2 = d.init(localAssign.right());
                                if (d2.init() != null) {
                                    if (d2.init().isConstant()) {
                                        if (d2.flags().flags().isFinal()) {
                                            d2.localDef().setConstantValue(d2.init().constantValue());
                                        }
                                    }
                                }
                                s = d2;
                            }
                        }
                    }
                    if (! (s instanceof LocalDecl)) {
                        s = nf.NodeList(d.position(), Arrays.<Node> asList(d.init(null), s));
                    }
                }
            }

            if (s != d) {
                s = propagateConstants(s);
                if (count < INLINE_DEPTH_LIMIT) {
                    Node r = parent.visitChild(s, inc());
                    if (r instanceof LocalDecl) {
                        LocalDecl d2 = (LocalDecl) r;
                        if (d2.init() != null && d2.init().isConstant() && d2.flags().flags().isFinal()) {
                            d2.localDef().setConstantValue(d2.init().constantValue());
                        }
                    }
                    return r;
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
                    Expr result = (a instanceof SettableAssign_c) ? ((SettableAssign_c)a).left(nf, this) : a.left(nf);
                    Expr call = a.right();
                    s = attemptInlineCall(eval, call, result);
                }
            }
            else {
                s = attemptInlineCall(eval, eval.expr(), null);
            }

            if (s != eval) {
                s = propagateConstants(s);
                if (count < INLINE_DEPTH_LIMIT) {
                    return parent.visitChild(s, inc());
                }
            }

            return s;
        }

        if (n instanceof Block) {
            Block b = (Block) n;
            List<Stmt> ss = new ArrayList<Stmt>();
            for (Stmt s : b.statements()) {
                if (s instanceof Empty) {
                }
                else {
                    ss.add(s);
                }
            }
            if (ss.size() != b.statements().size())
                return b.statements(ss);
        }
        */

        return n;
    }

    private Stmt inlineSpecial(X10Call c, X10MethodDef md, Expr result) {
        X10TypeSystem ts = (X10TypeSystem) this.ts;
        X10NodeFactory nf = (X10NodeFactory) this.nf;

        if (md.name() == Name.make("apply") && c.arguments().size() == 1 && c.target() instanceof Expr) {
            Expr target = (Expr) c.target();
            if (ConstantPropagator.isConstant(target) && ConstantPropagator.constantValue(target) instanceof Object[]) {
                Object[] a =  (Object[]) ConstantPropagator.constantValue(target);
                Expr len = c.arguments().get(0);
                if (ConstantPropagator.isConstant(len)) {
                    Object v = ConstantPropagator.constantValue(len);
                    if (v instanceof Integer) {
                        int n = (Integer) v;

                        if (result != null && 0 <= n && n < a.length) {
                            try {
                                Expr e = new ConstantPropagator(job, ts, nf).toExpr(a[n], c.position());
                                Assign assign = assign(c.position(), result, Assign.ASSIGN, e);
                                Eval eval = nf.Eval(c.position(), assign);
                                return eval;
                            }
                            catch (SemanticException ex) {
                            }
                        }
                    }
                }
            }
        }
        
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
                    if (ConstantPropagator.isConstant(len)) {
                        Object v = ConstantPropagator.constantValue(len);
                        if (v instanceof Integer) {
                            int n = (Integer) v;
                            List<Expr> args = new ArrayList<Expr>(n);

                            for (int i = 0; i < n; i++) {
                                Expr ni = Converter.check(nf.IntLit(c.position(), IntLit.INT, i), this);
                                Expr ai = Converter.check(nf.ClosureCall(c.position(), init, Collections.singletonList(ni)), this);
                                args.add(ai);
                            }

                            Expr e = Converter.check(nf.Tuple(c.position(), args), this);

                            if (! ts.typeEquals(e.type(), c.type(), context)) {
                                e = nf.X10Cast(c.position(), nf.CanonicalTypeNode(c.position(), c.type()), e, Converter.ConversionType.UNKNOWN_IMPLICIT_CONVERSION);
                                e = Converter.check(e, this);
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
                return inline(s_if_not_inline, c, mi, result);
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
            if (target instanceof ParExpr) {
                ParExpr e = (ParExpr) target;
                return staticTypeIsExact(e.expr());
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
