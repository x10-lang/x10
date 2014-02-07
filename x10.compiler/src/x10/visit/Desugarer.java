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

package x10.visit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

import polyglot.ast.Assign;
import polyglot.ast.Binary;
import polyglot.ast.Block;
import polyglot.ast.Call;
import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.ConstructorCall;
import polyglot.ast.Eval;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FieldAssign;
import polyglot.ast.FloatLit;
import polyglot.ast.Formal;
import polyglot.ast.Id_c;
import polyglot.ast.IntLit;
import polyglot.ast.Local;
import polyglot.ast.LocalAssign;
import polyglot.ast.LocalDecl;
import polyglot.ast.Local_c;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Stmt;
import polyglot.ast.TypeNode;
import polyglot.ast.Unary;
import polyglot.ast.New;
import polyglot.ast.AmbExpr;
import polyglot.ast.If;
import polyglot.ast.Receiver;
import polyglot.ast.ProcedureCall;
import polyglot.ast.Special;
import polyglot.ast.BooleanLit;
import polyglot.frontend.Job;
import polyglot.types.Context;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.LocalInstance;
import polyglot.types.MemberInstance;
import polyglot.types.Name;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.types.UnknownType;
import polyglot.types.VarDef;
import polyglot.types.QName;
import polyglot.types.ProcedureInstance;
import polyglot.types.ProcedureDef;
import polyglot.types.ClassType;
import polyglot.types.ClassDef;
import polyglot.types.Ref;
import polyglot.util.CollectionUtil;
import x10.util.CollectionFactory;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import x10.X10CompilerOptions;
import x10.errors.Errors.IllegalConstraint;
import x10.errors.Warnings;
import x10.ast.Closure;
import x10.ast.DepParameterExpr;
import x10.ast.ParExpr;
import x10.ast.SettableAssign;
import x10.ast.StmtExpr;
import x10.ast.X10Binary_c;
import x10.ast.X10Call;
import x10.ast.X10CanonicalTypeNode;
import x10.ast.X10Cast;
import x10.ast.X10Instanceof;
import x10.ast.X10Local_c;
import x10.ast.X10Special;
import x10.ast.X10Unary_c;
import x10.ast.X10ClassDecl_c;
import x10.constraint.XFailure;
import x10.constraint.XVar;
import x10.types.X10ConstructorInstance;
import x10.types.MethodInstance;
import x10.types.TypeParamSubst;
import x10.types.ReinstantiatedMethodInstance;
import x10.types.ReinstantiatedConstructorInstance;
import x10.types.X10ConstructorInstance_c;
import x10.types.ParameterType;
import x10.types.checker.Converter;
import x10.types.checker.PlaceChecker;
import x10.types.constraints.CConstraint;
import x10.types.constraints.XConstrainedTerm;
import x10.types.matcher.Subst;
import x10.util.Synthesizer;

/**
 * Visitor to desugar the AST before code generation.
 * 
 * NOTE: all the nodes created in the Desugarer must have the appropriate type information.
 * The NodeFactory methods do not fill in the type information.  Use the helper methods available
 * in the Desugarer to create expressions, or see how the type information is filled in for other
 * types of nodes elsewhere in the Desugarer.  TODO: factor out the helper methods into the
 * {@link Synthesizer}.
 */
public class Desugarer extends ContextVisitor {
    public Desugarer(Job job, TypeSystem ts, NodeFactory nf) {
        super(job, ts, nf);
    }

    @Override
    public Node override(Node parent, Node n) {
        if (n instanceof Eval) {
            Stmt s = adjustEval((Eval) n);
            return visitEdgeNoOverride(parent, s);
        }
        return null;
    }

    @Override
    public Node leaveCall(Node old, Node n, NodeVisitor v) {
        if (n instanceof ParExpr)
            return visitParExpr((ParExpr) n);
        if (n instanceof Assign)
            return visitAssign((Assign) n);
        if (n instanceof Binary)
            return visitBinary((Binary) n);
        if (n instanceof Unary)
            return visitUnary((Unary) n);
        if (n instanceof X10Cast)
            return visitCast((X10Cast) n);
        if (n instanceof X10Instanceof)
            return visitInstanceof((X10Instanceof) n);
        if (n instanceof New)
            return desugarNew((New) n, this);
        if (n instanceof ConstructorCall)
            return desugarConstructorCall((ConstructorCall) n, this);
        if (n instanceof Call)
            return desugarCall((Call) n, this);
        // todo: also ctor calls (this&super), operators

        return n;
    }

    /**
     * Remove parenthesized expressions.
     */
    protected Expr visitParExpr(ParExpr e) {
        return e.expr();
    }

    // desugar binary operators
    private Expr visitBinary(Binary n) {
        return desugarBinary(n, this);
    }

    public static Expr desugarBinary(Binary n, ContextVisitor v) {
        Call c = X10Binary_c.desugarBinaryOp(n, v);
        if (c != null) {
            MethodInstance mi = (MethodInstance) c.methodInstance();
            if (mi.error() != null)
                throw new InternalCompilerError("Unexpected exception when desugaring "+n, n.position(), mi.error());
            return desugarCall(c, v);
        }

        return n;
    }

    private Expr getLiteral(Position pos, Type type, long val) {
        type = Types.baseType(type);
        Expr lit = null;
        if (ts.isByte(type)) {
            lit = nf.IntLit(pos, IntLit.BYTE, val);
        } else if (ts.isUByte(type)) {
            lit = nf.IntLit(pos, IntLit.UBYTE, val);
        } else if (ts.isShort(type)) {
            lit = nf.IntLit(pos, IntLit.SHORT, val);
        } else if (ts.isUShort(type)) {
            lit = nf.IntLit(pos, IntLit.USHORT, val);
        } else if (ts.isInt(type)) {
            lit = nf.IntLit(pos, IntLit.INT, val);
        } else if (ts.isLong(type)) {
            lit = nf.IntLit(pos, IntLit.LONG, val);
        } else if (ts.isUInt(type)) {
            lit = nf.IntLit(pos, IntLit.UINT, val);
        } else if (ts.isULong(type)) {
            lit = nf.IntLit(pos, IntLit.ULONG, val);
        } else if (ts.isFloat(type)) {
            lit = nf.FloatLit(pos, FloatLit.FLOAT, val);
        } else if (ts.isDouble(type)) {
            lit = nf.FloatLit(pos, FloatLit.DOUBLE, val);
        } else if (ts.isChar(type)) {
            // Don't want to cast
            return (Expr) nf.IntLit(pos, IntLit.INT, val).typeCheck(this);
        } else
            throw new InternalCompilerError(pos, "Unknown literal type: "+type);
        lit = (Expr) lit.typeCheck(this);
        if (!ts.isSubtype(lit.type(), type)) {
            lit = nf.X10Cast(pos, nf.CanonicalTypeNode(pos, type), lit,
                    Converter.ConversionType.PRIMITIVE).type(type);
        }
        return lit;
    }

    protected Expr getLiteral(Position pos, Type type, boolean val) {
        type = Types.baseType(type);
        if (ts.isBoolean(type)) {
            Type t = ts.Boolean();
            t = Types.addSelfBinding(t, val ? ts.TRUE() : ts.FALSE());
            return nf.BooleanLit(pos, val).type(t);
        } else
            throw new InternalCompilerError(pos, "Unknown literal type: "+type);
    }

    // ++x -> x+=1 or --x -> x-=1
    private Expr unaryPre(Position pos, Unary.Operator op, Expr e) {
        Type ret = e.type();
        Expr one = getLiteral(pos, ret, 1);
        Assign.Operator asgn = (op == Unary.PRE_INC) ? Assign.ADD_ASSIGN : Assign.SUB_ASSIGN;
        Expr a = assign(pos, e, asgn, one);
        a = visitAssign((Assign) a);
        return a;
    }

    // In General: x++ -> (x+=1)-1 or x-- -> (x-=1)+1
    // Important special case: x is a local (so safe to evaulate twice)
    //     x++ -> ({ val t = x; x+=1; t }) or x-- -> ({ val t = x; x-=1; t })
    private Expr unaryPost(Position pos, Unary.Operator op, Expr e) {
        Type ret = e.type();
        Expr one = getLiteral(pos, ret, 1);
        Assign.Operator asgn = (op == Unary.POST_INC) ? Assign.ADD_ASSIGN : Assign.SUB_ASSIGN;
        if (e instanceof Local) {
            Name xn = Name.makeFresh("pre");
            LocalDef xDef = ts.localDef(pos, ts.Final(), Types.ref(ret), xn);
            LocalDecl xDecl = nf.LocalDecl(pos, nf.FlagsNode(pos, Flags.FINAL), nf.CanonicalTypeNode(pos, ret), nf.Id(pos, xn), e).localDef(xDef);
            Expr incr = visitAssign(assign(pos, e, asgn, one));
            Expr res = nf.Local(pos, nf.Id(pos, xn)).localInstance(xDef.asInstance()).type(ret);
            return nf.StmtExpr(pos, Arrays.<Stmt>asList(xDecl, nf.Eval(pos, incr)), res).type(ret);
        } else {
            Binary.Operator bin = (op == Unary.POST_INC) ? Binary.SUB : Binary.ADD;
            Expr incr = assign(pos, e, asgn, one);
            incr = visitAssign((Assign) incr);
            return visitBinary((Binary) nf.Binary(pos, incr, bin, one).type(ret));
        }
    }

    // desugar unary operators
    private Expr visitUnary(Unary n) {
        Unary.Operator op = n.operator();
        if (op == Unary.PRE_DEC || op == Unary.PRE_INC) {
            return unaryPre(n.position(), op, n.expr());
        }
        if (op == Unary.POST_DEC || op == Unary.POST_INC) {
            return unaryPost(n.position(), op, n.expr());
        }

        return desugarUnary(n, this);
    }

    public static Expr desugarUnary(Unary n, ContextVisitor v) {
        Call c = X10Unary_c.desugarUnaryOp(n, v);
        if (c != null) {
            MethodInstance mi = (MethodInstance) c.methodInstance();
            if (mi.error() != null)
                throw new InternalCompilerError("Unexpected exception when desugaring "+n, n.position(), mi.error());
            return desugarCall(c, v);
        }

        return n;
    }

    // This is called from override, so we just need to transform the statement, not desugar
    // x++; -> ++x; or x--; -> --x; (to avoid creating an extra statement expr)
    private Stmt adjustEval(Eval n) {
        Position pos = n.position();
        if (n.expr() instanceof Unary) {
            Unary e = (Unary) n.expr();
            if (e.operator() == Unary.POST_DEC)
                return n.expr(e.operator(Unary.PRE_DEC));
            if (e.operator() == Unary.POST_INC)
                return n.expr(e.operator(Unary.PRE_INC));
        }
        return n;
    }

    private Assign assign(Position pos, Expr e, Assign.Operator asgn, Expr val) {
        return assign(pos, e, asgn, val, this);
    }

    private static Assign assign(Position pos, Expr e, Assign.Operator asgn, Expr val, ContextVisitor v) {
        try {
            Synthesizer synth = new Synthesizer(v.nodeFactory(), v.typeSystem());
            return synth.makeAssign(pos, e, asgn, val, v.context());
        } catch (SemanticException z) {
            throw new InternalCompilerError("Unexpected exception while creating assignment", pos, z);
        }
    }

    private Expr visitAssign(Assign n) {
        if (n instanceof SettableAssign)
            return visitSettableAssign((SettableAssign) n);
        if (n instanceof LocalAssign)
            return visitLocalAssign((LocalAssign) n);
        if (n instanceof FieldAssign)
            return visitFieldAssign((FieldAssign) n);
        return n;
    }

    public static Expr desugarAssign(Assign n, ContextVisitor v) {
        if (n instanceof SettableAssign)
            return desugarSettableAssign((SettableAssign) n, v);
        if (n instanceof LocalAssign)
            return desugarLocalAssign((LocalAssign) n, v);
        if (n instanceof FieldAssign)
            return desugarFieldAssign((FieldAssign) n, v);
        return n;
    }
    
    private Expr visitLocalAssign(LocalAssign n) {
        return desugarLocalAssign(n, this);
    }

    // x op=v -> x = x op v
    public static Expr desugarLocalAssign(LocalAssign n, ContextVisitor v) {
        Position pos = n.position();
        if (n.operator() == Assign.ASSIGN) return n;
        Binary.Operator op = n.operator().binaryOperator();
        Local left = (Local) n.left();
        Expr right = n.right();
        Type R = left.type();
        Expr val = desugarBinary((Binary) v.nodeFactory().Binary(pos, left, op, right).type(R), v);
        return assign(pos, left, Assign.ASSIGN, val, v);
    }

    protected Expr visitFieldAssign(FieldAssign n) {
        return desugarFieldAssign(n, this);
    }

    // def n(a:T, b:S){EXPR(this,a,b)} { ... }
    // def this(a:T, b:S){EXPR(a,b)} { ... }
    // if the Call/New has a ProcedureInstance with checkGuardAtRuntime, then we do this transformation:
    // e.n(e1, e2)     ->   ((r:C, a:T, b:S)=>{if (!(EXPR(r,a,b))) throw new FailedDynamicCheckException(...); return r.n(a,b); })(e, e1, e2)
    // (there are two special cases: if e is empty (so it's either "this" or nothing if the "n" is static)
    // new X(e1, e2)  ->   ((a:T, b:S)=>{if (!(EXPR(a,b))) throw new FailedDynamicCheckException(...); return new X(a,b); })(e1, e2)
    private static Expr desugarCall(Expr expr, ContextVisitor v) {
        if (expr instanceof Call)
            return desugarCall((Call)expr, v);
        if (expr instanceof Binary)
            return desugarCall(expr, null, null, (Binary)expr, v);
        if (expr instanceof Unary) {
            Unary unary = (Unary) expr;
            // TODO: how to get the methodInstance out of an unary? do we even need to worry about it or is an unary always desugared into a Call (which I handle)?
        }
        if (expr instanceof SettableAssign) {
            SettableAssign settableAssign = (SettableAssign) expr;
            // todo: what about SettableAssign ? is it always desugared into a Call or ClosureCall (because I handle both cases correctly)?
        }
        return expr;
    }
    private static Expr desugarCall(Call call, ContextVisitor v) {
        return desugarCall(call, call, null, null, v);
    }                                       
    private static Expr desugarNew(final New neu, ContextVisitor v) {
        return desugarCall(neu, null, neu, null, v);
    }
    private static Stmt desugarConstructorCall(ConstructorCall cc, ContextVisitor v) {
        final ProcedureInstance<? extends ProcedureDef> pi = cc.constructorInstance();
        if (!pi.checkConstraintsAtRuntime()) {
            return cc;
        }
        TypeSystem ts = v.typeSystem();
        NodeFactory nf = v.nodeFactory();
        Job job = v.job();
        List<Expr> args = cc.arguments();
        List<Expr> newArgs = new ArrayList<Expr>(args.size());
        Position pos = cc.position();
        Context context = v.context();
        Context blockContext = context.pushBlock();
        /*
         * For a constructor call this(e1,..,en), where this:T,e1:T1,..,en:Tn and T.this(f1:U1,..,fn:Un{),
         * we are going to be creating the following block:
         * {val x1=e1 as U1;..;val xn=en as Un;if(!g[x1/f1;..;xn/fn])throw new FDCE();this(x1,..,xn);}
         */
        List<Stmt> statements = new ArrayList<Stmt>(1);
        if (!computeDynamicCheck(pi, args, null, pos, v, blockContext, newArgs, statements))
            return cc;
        ConstructorCall newCC = cc.arguments(newArgs);
        X10TypeBuilder builder = new X10TypeBuilder(job, ts, nf);
        ContextVisitor checker = new X10TypeChecker(job, ts, nf, job.nodeMemo()).context(blockContext);
        newCC = (ConstructorCall) newCC.visit(builder).visit(checker);
        statements.add(newCC);
        return nf.Block(pos, statements);
    }
    /**
     * 
     * @param booleanGuard
     * @param constraint
     * @param selfName
     * @param baseType -- for use in generating code from any occurrence of self in constraint
     * @param nf
     * @param ts
     * @param pos
     */
    private static void addCheck(ArrayList<Expr> booleanGuard, CConstraint constraint, 
                                 final Name selfName, final Type baseType, 
                                 final NodeFactory nf, 
                                 final TypeSystem ts, final Position pos) {
        if (constraint==null) return;
        final List<Expr> guardExpr 
        = new Synthesizer(nf, ts).makeExpr(constraint, baseType, pos);  // note: this doesn't typecheck the expression, so we're missing type info.
        for (Expr e : guardExpr) {
            e = (Expr) e.visit( new NodeVisitor() {
                @Override
                public Node override(Node n) {
                    if (n instanceof Special){
                        Special special = (Special) n;
                        if (special.kind()== Special.Kind.SELF) {
                            assert selfName!=null; // self cannot appear in a method guard
                            return nf.AmbExpr(pos,nf.Id(pos,selfName));
                        }                                                             
                    }
                    return null;
                }
            });
            booleanGuard.add(e);
        }
    }
    private static <T> T reinstantiate(TypeParamSubst typeParamSubst, T t) {
        return typeParamSubst==null ? t : typeParamSubst.reinstantiate(t);
    }
    private static Expr desugarCall(final Expr n, final Call call_c, 
                                    final New new_c, final Binary binary_c, ContextVisitor v) {
        final NodeFactory nf = v.nodeFactory();
        final TypeSystem ts = v.typeSystem();
        final Job job = v.job();

        assert n!=null && (call_c==n || new_c==n || binary_c==n);
        ProcedureCall procCall = call_c!=null || new_c!=null ? (ProcedureCall) n : null;
        final ProcedureInstance<? extends ProcedureDef> procInst =
                binary_c!=null ? binary_c.methodInstance() :
                        procCall.procedureInstance();
        if (procInst==null ||  // for binary ops (like ==), the methodInstance is null
            !procInst.checkConstraintsAtRuntime())
            return n;

        final Position pos = n.position();
        List<Expr> args = binary_c!=null ? Arrays.asList(binary_c.left(), binary_c.right()) : procCall.arguments();

        final Receiver target;
        if (binary_c!=null)
            target = null;
        else
            target = (call_c==null ? new_c.qualifier() : call_c.target());
        Expr oldReceiver = null;
        if (target!=null &&
            target instanceof Expr) { // making sure that the receiver is not a TypeNode
            oldReceiver = (Expr) target;
            args = new ArrayList<Expr>(args);
            args.add(0, (Expr) oldReceiver);
        }
        ArrayList<Expr> newArgs = new ArrayList<Expr>(args.size());
        final Context context = v.context();
        final Context blockContext = context.pushBlock();

        /*
         * For a call r.m(e1,..,en), where r:T,e1:T1,..,en:Tn and U.m(f1:U1,..,fn:Un){g}:R,
         * we are going to be creating the following statement expression:
         * ({ val x$0=r as U;val x$1=e1 as U1;..;val x$n=en as Un;
         *    if(!g[r/this;x1/f1;..;xn/fn])throw new FDCE();
         *    x$0.m(f1,..,fn) })
         */
        List<Stmt> statements = new ArrayList<Stmt>();
        if (!computeDynamicCheck(procInst, args, oldReceiver, pos, v, blockContext, newArgs, statements))
            return n;
        final Expr newReceiver = oldReceiver==null ? null : newArgs.remove(0);
        final ProcedureCall newProcCall;
        if (newReceiver==null)
            newProcCall = procCall;
        else
            newProcCall = (call_c!=null ? call_c.target(newReceiver) : new_c.qualifier(newReceiver));
        Expr newExpr;
        if (binary_c!=null)
            newExpr = binary_c.left(newArgs.get(0)).right(newArgs.get(1));
        else
            newExpr = (Expr) newProcCall.arguments(newArgs);
        X10TypeBuilder builder = new X10TypeBuilder(job, ts, nf);
        ContextVisitor checker = new X10TypeChecker(job, ts, nf, job.nodeMemo()).context(blockContext);
        newExpr = (Expr) newExpr.visit(builder).visit(checker);
        return nf.StmtExpr(pos, statements, newExpr).type(newExpr.type());
    }
    
    public static boolean computeDynamicCheck(ProcedureInstance<?> procInst, List<Expr> args, Expr oldReceiver,
                                              final Position pos, ContextVisitor v, Context blockContext,
                                              List<Expr> newArgs, List<Stmt> statements) {
        // we shouldn't use the def, because sometimes the constraints come from the instance,
        // e.g.,  new Box[Int{self!=0}](v)
        // dynamically checks that v!=0  (but you can't see it in the def! only in the instance).
        // However, the instance has also the arguments (that exists in the context),
        // and for some reason formalNames of the instance doesn't return the constraint that self!=0 (and Vijay thinks it shouldn't do it anyway)
        // so I need to take the paramSubst and do it myself on the def.
        // E.g.,
        // new Box[Int{self!=0}](i)  in the instance returns a formal  arg123:Int{self==arg123, arg123==i}  but without i!=0 !
        // so I take the original formal from the def (x:T) and do the paramSubst on it to get  x:Int{self!=0}
        final TypeSystem ts = v.typeSystem();
        final NodeFactory nf = v.nodeFactory();
        final Job job = v.job();
        final Context context = v.context();
        final ProcedureDef procDef = procInst.def();
        TypeParamSubst typeParamSubst =
                procInst instanceof ReinstantiatedMethodInstance ? ((ReinstantiatedMethodInstance)procInst).typeParamSubst() :
                procInst instanceof ReinstantiatedConstructorInstance ? ((ReinstantiatedConstructorInstance)procInst).typeParamSubst() :
                    null; // this can happen when procInst is X10ConstructorInstance_c (see XTENLANG_2330). But creating an empty TypeParamSubst would also work
        final List<Type> typeParam = procInst.typeParameters(); // note that X10ConstructorInstance_c.typeParameters returns an empty list! (there is a todo there!)
        if (typeParam!=null && typeParam.size()>0) {
            if (typeParamSubst==null) typeParamSubst = new TypeParamSubst(ts,Collections.EMPTY_LIST,Collections.EMPTY_LIST);
            final ArrayList<Type> newTArgs = typeParamSubst.copyTypeArguments();
            newTArgs.addAll(typeParam);
            final ArrayList<ParameterType> newParams = typeParamSubst.copyTypeParameters();
            newParams.addAll(procDef.typeParameters());
            typeParamSubst = new TypeParamSubst(ts, newTArgs,newParams);
        }

        final List<LocalDef> oldFormals = procDef.formalNames();
        List<LocalDecl> locals = new ArrayList<LocalDecl>(args.size());
        int i=0;
        List<VarDef> Ys = new ArrayList<VarDef>(args.size());
        List<VarDef> Xs = new ArrayList<VarDef>(args.size());
        for (Expr arg : args) {
            Type pType = arg.type();
            // The argument might be null, e.g., def m(b:Z) {b.x!=null}  = 1; ... m(null);
            final LocalDef oldFormal = arg==oldReceiver ? null : oldFormals.get(oldReceiver==null ? i : i-1);
            Type type = Types.baseType(oldFormal!=null ? reinstantiate(typeParamSubst, Types.get(oldFormal.type())) : pType);
            if (type.isNull() && procInst instanceof MemberInstance<?>) {
                type = reinstantiate(typeParamSubst, ((MemberInstance<?>) procInst).container());
            }
            Type tType;
            try {
                tType = Subst.subst(type, Types.toVarArray(Ys), Types.toVarArray(Xs), new Type[0], new ParameterType[0]);
            } catch (SemanticException z) {
                throw new InternalCompilerError("Unexpected exception while inserting a dynamic check", z);
            }

            Name xn = oldFormal!=null ? Name.makeFresh(oldFormal.name()) : Name.makeFresh();
            LocalDef xDef = ts.localDef(pos, ts.Final(), Types.ref(tType), xn);
            Expr c = Converter.attemptCoercion(v.context(blockContext), arg, tType);
            c = (Expr) c.visit(v.context(blockContext));
            LocalDecl xd = nf.LocalDecl(pos, nf.FlagsNode(pos, ts.Final()),
                                        nf.CanonicalTypeNode(pos, tType), nf.Id(pos, xn), c).localDef(xDef);
            locals.add(xd);
            final Local x = (Local) nf.Local(pos, nf.Id(pos, xn)).localInstance(xDef.asInstance()).type(tType);
            newArgs.add(x);
            blockContext.addVariable(x.localInstance());
            if (oldFormal != null) {
                Ys.add(xDef);
                Xs.add(oldFormal);
            } else {
                Ys.add(xDef);
                Xs.add(procDef.thisDef());
            }
            i++;
        }
        
        // we add the guard to the body, then the return stmt.
        // if (!(GUARDEXPR(a,b))) throw new FailedDynamicCheckException(...); return ...
        final Ref<CConstraint> guardRefConstraint = procDef.guard();
        ArrayList<Expr> booleanGuard = new ArrayList<Expr>();
        if (guardRefConstraint!=null) {
            final CConstraint guard = reinstantiate(typeParamSubst, guardRefConstraint.get());
            // self cannot occur in the constraint, hence null can be passed as the type.
            addCheck(booleanGuard,guard, null, null, nf, ts, pos);
        }
        // add the constraints of the formals
        for (LocalDef localDef : procDef.formalNames()) {
            Type type = reinstantiate(typeParamSubst, Types.get(localDef.type()));
            CConstraint constraint = Types.xclause(type);
            LocalInstance li = localDef.asInstance();
            Receiver r = new X10Local_c(pos, new Id_c(pos, localDef.name())).localInstance(li);
            r = ((X10Local_c) r).type(li.type());
            XVar selfVar=null;
            try {
                selfVar = (XVar) localDef.typeSystem().xtypeTranslator().translate(constraint, r, context);
            } catch (IllegalConstraint z) {
                /// what do we do?
            }
            if (selfVar != null && constraint != null)
                constraint = constraint.instantiateSelf(selfVar);
            addCheck(booleanGuard,constraint, localDef.name(), type, nf, ts, pos);
        }
        
        final Expr newReceiver = oldReceiver==null ? null : newArgs.get(0);
        int offset = oldReceiver==null ? 0 : 1;
        // replace old formals in depExpr with the new locals
        final Map<Name,Expr> old2new = CollectionFactory.newHashMap(oldFormals.size());
        for (int k=0; k<oldFormals.size(); k++) {
            Expr newE = newArgs.get(k+offset);
            old2new.put(oldFormals.get(k).name(), newE);
        }
        // replace all AmbExpr with the new locals
        final X10TypeBuilder builder = new X10TypeBuilder(job, ts, nf);
        final ContextVisitor checker = new X10TypeChecker(job, ts, nf, job.nodeMemo()).context(blockContext);
        NodeVisitor replace = new NodeVisitor() {
            @Override
            public Node override(Node n) {
                if (n instanceof Special){
                    // if it's an outer instance, then we need to access the outer field
                    Special special = (Special) n;
                    TypeNode qualifer = special.qualifier();
                    if (qualifer==null) return newReceiver;
                    // qualifer doesn't have type info because it was created in Synthesizer.makeExpr
                    qualifer = (TypeNode) qualifer.visit(builder).visit(checker);
                    ClassType ct =  qualifer.type().toClass();
                    ClassType receiverType = Types.getClassType(newReceiver.type(), ts, context);
                    if (receiverType==null)
                        return newReceiver;
                    final ClassDef newReceiverDef = receiverType.def();
                    final ClassDef qualifierDef = ct.def();
                    if (newReceiverDef==qualifierDef)
                        return newReceiver;
                    return nf.Call(pos,newReceiver, nf.Id(pos,X10ClassDecl_c.getThisMethod(newReceiverDef.fullName(),ct.fullName())));
                }
                if (n instanceof AmbExpr) {
                    AmbExpr amb = (AmbExpr) n;
                    Name name = amb.name().id();
                    Expr newE = old2new.get(name);
                    if (newE==null) throw new OuterLocalUsed();
                    return newE;
                }
                return null;
            }
        };
        ArrayList<Expr> newCheck = new ArrayList<Expr>(booleanGuard.size());
        for (Expr e : booleanGuard) {
            try {
                newCheck.add( (Expr)e.visit(replace) );
            } catch (OuterLocalUsed e1) {
                // ignore expressions that have outer locals (constraint system bugs like XTENLANG_2638)
            	// [DC] since the above jira is fixed, making this a hard error
            	throw new InternalCompilerError("Dynamic check of constraint "+e+" was malformed");
            }
        }
        
        if (newCheck.size()==0)
            return false; // nothing to check...
        
        Warnings.dynamicCall(v.job(), Warnings.GeneratedDynamicCheck(pos));
        
        Expr newDep = newCheck.get(0);
        for (int k=1; k<newCheck.size(); k++) {
            Expr e = newCheck.get(k);
            newDep = nf.Binary(pos, newDep, Binary.Operator.COND_AND, e).type(ts.Boolean());
        }
        // if (!newDep) throw new FailedDynamicCheckException();
        newDep = nf.Unary(pos, Unary.Operator.NOT, newDep).type(ts.Boolean());
        If anIf = nf.If(pos, newDep, nf.Throw(pos,
                nf.New(pos, nf.CanonicalTypeNode(pos, ts.FailedDynamicCheckException()),
                        CollectionUtil.<Expr>list(nf.StringLit(pos, newDep.toString()))).type(ts.FailedDynamicCheckException())));
        anIf = (If) anIf.visit(builder).visit(checker).visit(v);
        statements.addAll(locals);
        statements.add(anIf);
        return true;
    }
    private static class OuterLocalUsed extends RuntimeException {}

    // T.f op=v -> T.f = T.f op v
    // e.f op=v -> ({ val x:E = e; e.f = e.f op v })
    public static Expr desugarFieldAssign(FieldAssign n, ContextVisitor v) {
        NodeFactory nf = v.nodeFactory();
        TypeSystem ts = v.typeSystem();
        Position pos = n.position();
        if (n.operator() == Assign.ASSIGN) return n;
        Binary.Operator op = n.operator().binaryOperator();
        Field left = (Field) n.left();
        Expr right = n.right();
        Type R = left.type();
        if (left.flags().isStatic()) {
            Expr val = desugarBinary((Binary) nf.Binary(pos, left, op, right).type(R), v);
            return assign(pos, left, Assign.ASSIGN, val, v);
        }
        Expr e = (Expr) left.target();
        Type E = e.type();
        if (e instanceof Local || e instanceof Special) {
            // optimize common special case; 
            // e can safely be evaluated twice so don't need a temp
            Expr lhs = nf.Field(pos, e,
                                nf.Id(pos, left.name().id())).fieldInstance(left.fieldInstance()).type(R);
            Expr val = desugarBinary((Binary) nf.Binary(pos, lhs, op, right).type(R), v);
            return assign(pos, lhs, Assign.ASSIGN, val, v);
        } else {        
            Name xn = Name.makeFresh("obj");
            LocalDef xDef = ts.localDef(pos, ts.Final(), Types.ref(E), xn);
            LocalDecl xDecl = nf.LocalDecl(pos, nf.FlagsNode(pos, Flags.FINAL), nf.CanonicalTypeNode(pos, E), nf.Id(pos, xn), e).localDef(xDef);
            Expr lhs = nf.Field(pos,
                                nf.Local(pos, nf.Id(pos, xn)).localInstance(xDef.asInstance()).type(E),
                                nf.Id(pos, left.name().id())).fieldInstance(left.fieldInstance()).type(R);
            Expr val = desugarBinary((Binary) nf.Binary(pos, lhs, op, right).type(R), v);
            Expr res = assign(pos, lhs, Assign.ASSIGN, val, v);
            return nf.StmtExpr(pos, Arrays.<Stmt>asList(xDecl), res).type(R);
        }
    }

    protected Expr visitSettableAssign(SettableAssign n) {
        return desugarSettableAssign(n, this);
    }

    // a(i)=v -> a.operator()=(i,v)
    // a(i)op=v -> {( val a$ = a; val i$ = i; val r = a$.operator()(i$) op z; a$.operator()=(i$,r) )}
    public static Expr desugarSettableAssign(SettableAssign n, ContextVisitor v) {
        NodeFactory nf = v.nodeFactory();
        TypeSystem ts = v.typeSystem();
        Position pos = n.position();
        MethodInstance mi = n.methodInstance();
        List<Expr> args = new ArrayList<Expr>(n.index());
        Expr a = n.array();
        if (n.operator() == Assign.ASSIGN) {
            args.add(n.right());
            return desugarCall(nf.Call(pos, a, nf.Id(pos, mi.name()), args).methodInstance(mi).type(mi.returnType()), v);
        }
        Binary.Operator op = n.operator().binaryOperator();
        X10Call left = (X10Call) n.left();
        MethodInstance ami = left.methodInstance();
        List<Stmt> stmts = new ArrayList<Stmt>();
        Name xn = Name.makeFresh("a");
        Type aType = a.type();
        assert (ts.isSubtype(aType, mi.container(), v.context()));
        LocalDef xDef = ts.localDef(pos, ts.Final(), Types.ref(aType), xn);
        LocalDecl xDecl = nf.LocalDecl(pos, nf.FlagsNode(pos, Flags.FINAL), 
                                       nf.CanonicalTypeNode(pos, aType), nf.Id(pos, xn), a).localDef(xDef);
        stmts.add(xDecl);
        
        List<Expr> idx1 = new ArrayList<Expr>();
        int i = 0;
        assert (ami.formalTypes().size()==n.index().size());
        for (Expr e : n.index()) {
            Type t = e.type();
            Name yn = Name.makeFresh("i"+i);
            LocalDef yDef = ts.localDef(pos, ts.Final(), Types.ref(t), yn);
            LocalDecl yDecl = nf.LocalDecl(pos, nf.FlagsNode(pos, Flags.FINAL), 
                                           nf.CanonicalTypeNode(pos, t), nf.Id(pos, yn), e).localDef(yDef);
            stmts.add(yDecl);
            idx1.add(nf.Local(pos, nf.Id(pos, yn)).localInstance(yDef.asInstance()).type(t));
            i++;
        }
        Type T = mi.formalTypes().get(mi.formalTypes().size()-1);
        Type vType = n.right().type();
        assert (ts.isSubtype(ami.returnType(), T, v.context()));
        assert (ts.isSubtype(vType, T, v.context()));
        Expr val = nf.Binary(pos,
                             desugarCall(nf.Call(pos,
                                                 nf.Local(pos, nf.Id(pos, xn)).localInstance(xDef.asInstance()).type(aType),
                                                 nf.Id(pos, ami.name()), idx1).methodInstance(ami).type(ami.returnType()),
                                         v),
                             op, 
                             n.right()).type(T);
        val = desugarBinary((Binary)val, v);
        Type rType = val.type();
        Name rn = Name.makeFresh("r");
        LocalDef rDef = ts.localDef(pos, ts.Final(), Types.ref(rType), rn);
        LocalDecl r = nf.LocalDecl(pos, nf.FlagsNode(pos, ts.Final()),
                                   nf.CanonicalTypeNode(pos, rType), nf.Id(pos, rn), val).localDef(rDef);
        stmts.add(r);
        List<Expr> args1 = new ArrayList<Expr>(idx1);
        args1.add(nf.Local(pos, nf.Id(pos, rn)).localInstance(rDef.asInstance()).type(rType));
        Expr res = desugarCall(nf.Call(pos,
                                       nf.Local(pos, nf.Id(pos, xn)).localInstance(xDef.asInstance()).type(aType),
                                       nf.Id(pos, mi.name()), args1).methodInstance(mi).type(mi.returnType()), v);
        return nf.StmtExpr(pos, stmts, res).type(rType);
    }

    /**
     * Concatenates the given list of clauses with &&, creating a conjunction.
     * Any occurrence of "self" in the list of clauses is replaced by self.
     */
    private Expr conjunction(Position pos, List<Expr> clauses, Expr self) {
        if (clauses.isEmpty()) { // FIXME: HACK: need to ensure that source expressions are preserved
            return getLiteral(pos, ts.Boolean(), true);
        }
        assert clauses.size() > 0;
        Substitution<Expr> subst = new Substitution<Expr>(Expr.class, Collections.singletonList(self)) {
            protected Expr subst(Expr n) {
                if (n instanceof X10Special && ((X10Special) n).kind() == X10Special.SELF)
                    return by.get(0);
                return n;
            }
        };
        Expr left = null;
        for (Expr clause : clauses) {
            Expr right = (Expr) clause.visit(subst);
            right = (Expr) right.visit(this);
            if (left == null)
                left = right;
            else {
                left = nf.Binary(pos, left, Binary.COND_AND, right).type(ts.Boolean());
                left = visitBinary((Binary) left);
            }
        }
        return left;
    }

    private DepParameterExpr getClause(TypeNode tn) {
        Type t = tn.type();
        if (tn instanceof X10CanonicalTypeNode) {
            CConstraint c = Types.xclause(t);
            if (c == null || c.valid())
                return null;
            XConstrainedTerm here = context().currentPlaceTerm();
            if (here != null && here.term() instanceof XVar) {
                try {
                    c = c.substitute(PlaceChecker.here(), (XVar) here.term());
                } catch (XFailure e) { }
            }
            DepParameterExpr res 
            = nf.DepParameterExpr(tn.position(), 
                          new Synthesizer(nf, ts).makeExpr(c, t, tn.position()));
            res = (DepParameterExpr) res.visit(new X10TypeBuilder(job, ts, nf)).visit(new X10TypeChecker(job, ts, nf, job.nodeMemo()).context(context().pushDepType(tn.typeRef())));
            return res;
        }
        throw new InternalCompilerError("Unknown type node type: "+tn.getClass(), tn.position());
    }

    private TypeNode stripClause(TypeNode tn) {
        Type t = tn.type();
        if (tn instanceof X10CanonicalTypeNode) {
            X10CanonicalTypeNode ctn = (X10CanonicalTypeNode) tn;
            Type baseType = Types.baseType(t);
            if (baseType != t) {
                return ctn.typeRef(Types.ref(baseType));
            }
            return ctn;
        }
        throw new InternalCompilerError("Unknown type node type: "+tn.getClass(), tn.position());
    }

    // e as T{c} -> ({ val x:T = e as T; if (!c[self/x]) throw CCE(); x })    
    private Expr visitCast(X10Cast n) {
        // We give the DYNAMIC_CALLS warning here (and not in type-checking), because we create a lot of temp cast nodes in the process that are discarded later.
        if (n.conversionType()==Converter.ConversionType.DESUGAR_LATER) {
            Warnings.dynamicCall(job(), Warnings.CastingExprToType(n.expr(),n.type(),n.position()));
            n = n.conversionType(Converter.ConversionType.CHECKED);
        }

        Position pos = n.position();
        Expr e = n.expr();
        TypeNode tn = n.castType();
        Type castDepType = tn.type();
        DepParameterExpr depClause = getClause(tn);
        tn = stripClause(tn);
        X10CompilerOptions opts = (X10CompilerOptions) job.extensionInfo().getOptions();
        if (depClause == null || opts.x10_config.NO_CHECKS)
            return n.castType(tn);

        List<Stmt> stmts = new ArrayList<Stmt>();

        // x = e as T
        Type castBaseType = tn.type();
        boolean checked = !ts.isSubtype(Types.baseType(e.type()), castBaseType, context);
        Expr cast = nf.X10Cast(pos, tn, e, checked ? Converter.ConversionType.CHECKED : Converter.ConversionType.UNCHECKED).type(castBaseType);
        Name xn = Name.makeFresh();
        LocalDef xDef = ts.localDef(pos, ts.Final(), Types.ref(castBaseType), xn);
        LocalDecl xDecl = nf.LocalDecl(pos, nf.FlagsNode(pos, Flags.FINAL), 
                                       nf.CanonicalTypeNode(pos, xDef.type()), nf.Id(pos, xn), cast).localDef(xDef);
        Expr xl = nf.Local(pos, nf.Id(pos, xn)).localInstance(xDef.asInstance()).type(castBaseType);
        stmts.add(xDecl);
        
        // if (!c[self/x]) throw CCE()
        List<Expr> condition = depClause.condition();
        Expr cond = visitUnary((Unary) nf.Unary(pos, conjunction(depClause.position(), condition, xl), Unary.NOT).type(ts.Boolean()));
        Type ccet = ts.ClassCastException();
        Expr msg = nf.StringLit(pos, castDepType.toString()).type(ts.String());
        X10ConstructorInstance ni;
        try {
            ni = ts.findConstructor(ccet, ts.ConstructorMatcher(ccet, Collections.singletonList(ts.String()), context()));
        } catch (SemanticException z) {
            throw new InternalCompilerError("Unexpected exception while desugaring "+n, pos, z);
        }
        Expr newCCE = nf.New(pos, nf.CanonicalTypeNode(pos, ccet), Collections.singletonList(msg)).constructorInstance(ni).type(ccet);
        stmts.add(nf.If(pos, cond, nf.Throw(pos, newCCE)));
        return nf.StmtExpr(pos, stmts, xl).type(castDepType);
    }

    // e instanceof T{c} -> ({ val x = e; x instanceof T && ({ x' = x as T; c[self/x'] }) })
    private Expr visitInstanceof(X10Instanceof n) {
        Position pos = n.position();
        Expr e = n.expr();
        TypeNode tn = n.compareType();
        DepParameterExpr depClause = getClause(tn);
        tn = stripClause(tn);
        if (depClause == null)
            return n;
        
        // val x = e;
        Name xn = Name.makeFresh();
        Type et = e.type();
        LocalDef xDef = ts.localDef(pos, ts.Final(), Types.ref(et), xn);
        LocalDecl xDecl = nf.LocalDecl(pos, nf.FlagsNode(pos, Flags.FINAL), 
                                       nf.CanonicalTypeNode(pos, xDef.type()), nf.Id(pos, xn), e).localDef(xDef);
        Expr xl = nf.Local(pos, nf.Id(pos, xn)).localInstance(xDef.asInstance()).type(et);

        // x instanceof T && ({ val x' = x as T; c[self/x'] })
        Expr iof = nf.Instanceof(pos, xl, tn).type(ts.Boolean());
        
        Name xn2 = Name.makeFresh();
        LocalDef xDef2 = ts.localDef(pos, ts.Final(), Types.ref(tn.type()), xn2);
        Expr cast = nf.X10Cast(pos, tn, xl, Converter.ConversionType.UNCHECKED).type(tn.type());
        LocalDecl xDecl2 = nf.LocalDecl(pos, nf.FlagsNode(pos, Flags.FINAL), 
                                       nf.CanonicalTypeNode(pos, xDef2.type()), nf.Id(pos, xn2), cast).localDef(xDef2);
        Expr xl2 = nf.Local(pos, nf.Id(pos, xn2)).localInstance(xDef2.asInstance()).type(tn.type());
        Expr cond = conjunction(depClause.position(), depClause.condition(), xl2);
        Expr inner = nf.StmtExpr(pos, Arrays.<Stmt>asList(xDecl2), cond).type(ts.Boolean());
        
        Expr rval = visitBinary((Binary) nf.Binary(pos, iof, Binary.COND_AND, inner).type(ts.Boolean()));
        
        return nf.StmtExpr(pos, Arrays.<Stmt>asList(xDecl), rval).type(ts.Boolean());
    }

    public static class Substitution<T extends Node> extends NodeVisitor {
        protected final List<T> by;
        private final Class<T> cz;
        public Substitution(Class<T> cz, List<T> by) {
            this.cz = cz;
            this.by = by;
        }
        @SuppressWarnings("unchecked") // Casting to a generic type parameter
        @Override
        public Node leave(Node old, Node n, NodeVisitor v) {
            if (cz.isInstance(n))
                return subst((T)n);
            return n;
        }
        protected T subst(T n) {
            return n;
        }
    }
}
