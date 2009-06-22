package x10.refactorings;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import polyglot.ast.Binary;
import polyglot.ast.Block;
import polyglot.ast.Call;
import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.Eval;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.If;
import polyglot.ast.Local;
import polyglot.ast.LocalDecl;
import polyglot.ast.NamedVariable;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.Receiver;
import polyglot.ast.Stmt;
import polyglot.ast.Unary;
import polyglot.ast.VarDecl;
import polyglot.ast.While;
import polyglot.ast.Unary.Operator;
import polyglot.ext.x10.ast.Async;
import polyglot.ext.x10.ast.AtStmt;
import polyglot.ext.x10.ast.DepParameterExpr;
import polyglot.ext.x10.ast.ForEach;
import polyglot.ext.x10.ast.ForLoop;
import polyglot.ext.x10.ast.SettableAssign;
import polyglot.ext.x10.ast.X10Formal;
import polyglot.ext.x10.ast.X10MethodDecl;
import polyglot.ext.x10.types.X10LocalDef;
import polyglot.ext.x10.types.X10ProcedureDef;
import polyglot.ext.x10.types.X10ProcedureInstance;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.types.ClassType;
import polyglot.types.ConstructorInstance;
import polyglot.types.MethodInstance;
import polyglot.types.ProcedureInstance;
import polyglot.types.StructType;
import polyglot.types.Type;
import polyglot.types.VarDef;
import polyglot.visit.NodeVisitor;
import x10.constraint.XConstraint;
import x10.constraint.XFailure;
import x10.constraint.XName;
import x10.constraint.XNameWrapper;
import x10.constraint.XTerm;
import x10.constraint.XTerms;
import x10.effects.constraints.Effect;
import x10.effects.constraints.Effects;
import x10.effects.constraints.FieldLocs;
import x10.effects.constraints.Index_c;
import x10.effects.constraints.LocalLocs;
import x10.effects.constraints.Locs;
import x10.effects.constraints.ObjLocs;
import x10.refactorings.ReachingDefsVisitor.ValueMap;

public class EffectsVisitor extends NodeVisitor {
    private final Map<Node,Effect> fEffects= new HashMap<Node, Effect>();

    private final XConstraint fMethodContext;

    private final ValueMap fValueMap;

    public EffectsVisitor(ValueMap valueMap, X10MethodDecl method) throws XFailure {
        fValueMap = valueMap;
        fMethodContext= computeMethodContextConstraint(method);
    }

    private final XConstraint computeMethodContextConstraint(X10MethodDecl method) throws XFailure {
        XConstraint result= null;
        DepParameterExpr methodGuard= method.guard();
        if (methodGuard != null) {
            result= conjunction(result, methodGuard.xconstraint().get());
        }
        List<Formal> formals= method.formals();
        for(Formal formal: formals) {
            X10Formal xFormal= (X10Formal) formal;
            X10LocalDef xDef= (X10LocalDef) xFormal.localDef();
            X10Type xType= (X10Type) xFormal.type().type();
            XConstraint xc= X10TypeMixin.xclause(xType);
            if (xc != null) {
                result= conjunction(result, xc);
            }
        }
        return result;
    }

    public Effect getEffectFor(Node n) {
        return fEffects.get(n);
    }

    private XConstraint conjunction(XConstraint c1, XConstraint c2) throws XFailure {
        if (c1 == null)
            return c2.copy();
        return c1.addIn(c2);
    }

    private static class XVariable extends XNameWrapper<VarDef> {
        public XVariable(NamedVariable var) {
            super((VarDef) var.varInstance().def(), var.name().toString());
        }
    }

    private Locs createArrayLoc(Expr array, Expr index) {
        XTerm arrayTerm = createTermForExpr(array);
        XTerm indexTerm = createTermForExpr(index);
        return Effects.makeArrayElementLocs(Effects.makeArrayLocs(arrayTerm), new Index_c(indexTerm));
    }

    private static XTerm createTermForExpr(Expr e) {
        return null;
    }

    private static XName createExprName(Expr e) {
        if (e instanceof NamedVariable) {
            NamedVariable namedVariable = (NamedVariable) e;
            return new XVariable(namedVariable);
        }
        return null;
    }

    @Override
    public Node leave(Node parent, Node old, Node n, NodeVisitor v) {
        Effect e= null;
        try {
            if (old instanceof Async) {
                Async async = (Async) old;
                e= computeEffect(async);
            } else if (old instanceof AtStmt) {
                e= fEffects.get(((AtStmt) old).body());
            } else if (old instanceof Binary) {
                e = computeEffect((Binary) old);
            } else if (old instanceof Block) {
                e= computeEffect((Block) old);
            } else if (old instanceof Call) {
                e= computeEffect((Call) old);
            } else if (old instanceof Eval) {
                e= fEffects.get(((Eval) old).expr());
            } else if (old instanceof Field) {
                e= computeEffect((Field) old);
            } else if (old instanceof ForEach) {
                e= computeEffect((ForEach) old);
            } else if (old instanceof ForLoop) {
                e= computeEffect((ForLoop) old);
            } else if (old instanceof If) {
                e= computeEffect((If) old);
            } else if (old instanceof Local) {
                e= computeEffect((Local) old);
            } else if (old instanceof LocalDecl) {
                e= computeEffect((LocalDecl) old);
            } else if (old instanceof New) {
                e= computeEffect((New) old);
            } else if (old instanceof SettableAssign) {
                SettableAssign sa= (SettableAssign) old;
                Effect rhsEff= fEffects.get(sa.right());
                Effect writeEff= Effects.makeEffect(Effects.FUN);
                writeEff.addWrite(createArrayLoc(sa.array(), sa.index().get(0)));
                e= followedBy(rhsEff, writeEff);
            } else if (old instanceof Unary) {
                e= computeEffect((Unary) old);
            } else if (old instanceof While) {
                e= bottomEffect();
            }
            if (e != null) {
                fEffects.put(old, e);
            }
        } catch (XFailure f) {
                X10RefactoringPlugin.getInstance().logException(f.getMessage(), f);
        }
        return super.leave(parent, old, n, v);
    }

    private Effect computeEffect(If n) throws XFailure {
        Effect condEff= fEffects.get(n.cond());
        Effect thenEff= fEffects.get(n.consequent());
        Effect elseEff= (n.alternative() != null) ? fEffects.get(n.alternative()) : null;
        return followedBy(followedBy(condEff, thenEff), elseEff);
    }

    private Effect computeEffect(New neew) throws XFailure {
        Effect e= null;
        ConstructorInstance ctorInstance = neew.constructorInstance();
        List<Expr> args = neew.arguments();
        e= computeEffect(args);
        e= followedBy(e, getMethodEffects(ctorInstance));
        return e;
    }

    private Effect getMethodEffects(ProcedureInstance procInstance) {
        X10ProcedureInstance xpi= (X10ProcedureInstance) procInstance;
        X10ProcedureDef xpd= (X10ProcedureDef) xpi.def();
        List<Type> annotations= xpd.annotations();
        Effect e= Effects.makeEffect(false);

        for (Type annoType : annotations) {
            if (annoType instanceof ClassType) {
                ClassType annoClassType = (ClassType) annoType;
                if (annoClassType.name().toString().equals("fun")) {
                    return e.makeFun();
                } else if (annoClassType.name().toString().equals("parfun")) {
                    return e.makeParFun();
                }
            }
        }
        return e; // TODO return 'bottom' here - don't know what the effects are, so be safe
    }

    private Effect bottomEffect() {
        // TODO return "bottom" - an effect that is inherently and irredeemably non-parallelizable
        return Effects.makeEffect(Effects.PAR_FUN);
    }

    private Effect computeEffect(LocalDecl localDecl) throws XFailure {
        Expr init = localDecl.init();
        Effect initEff= fEffects.get(init);
        Effect e= initEff;
        if (init != null) {
            Effect write= Effects.makeEffect(Effects.FUN);
            write.addWrite(computeLocFor(localDecl));
            e= followedBy(initEff, write);
        }
        return e;
    }

    private Effect computeEffect(Async async) {
        Effect bodyEff= fEffects.get(async.body());

        return bodyEff.makeParFun();
    }

    private Effect computeEffect(Unary unary) {
        Effect e;
        Expr opnd= unary.expr();
        Operator op= unary.operator();
        Effect opndEff= fEffects.get(opnd);

        if (op == Unary.BIT_NOT || op == Unary.NEG || op == Unary.NOT || op == Unary.POS) {
            e= opndEff;
        } else {
            Effect write= Effects.makeEffect(true);
            write.addWrite(computeLocFor(opnd));
            try {
                if (op == Unary.POST_DEC || op == Unary.POST_INC) {
                    e= opndEff.followedBy(write, fMethodContext);
                } else /*if (op == Unary.PRE_DEC || op == Unary.PRE_INC)*/ {
                    e= write.followedBy(opndEff, fMethodContext);
                }
            } catch (XFailure f) {
                X10RefactoringPlugin.getInstance().logException(f.getMessage(), f);
                return null;
            }
        }
        return e;
    }

    private Locs computeLocFor(VarDecl vd) {
        if (vd instanceof LocalDecl) {
            LocalDecl localDecl = (LocalDecl) vd;
            return Effects.makeLocalLocs(XTerms.makeLocal(new XNameWrapper<LocalDecl>(localDecl, localDecl.name().toString())));
        }
        throw new UnsupportedOperationException("Don't know how to make a Locs for " + vd);
    }

    private Locs computeLocFor(Expr expr) {
        if (expr instanceof Local) {
            Local local= (Local) expr;
            LocalLocs ll= Effects.makeLocalLocs(XTerms.makeLocal(new XVariable(local)));
            return ll;
        } else if (expr instanceof Field) {
            Field field= (Field) expr;
            FieldLocs fl= Effects.makeFieldLocs((ObjLocs) computeLocFor(field.target()), new XVariable(field));
        }
        return null;
    }

    private Locs computeLocFor(Receiver r) {
        if (r instanceof Expr) {
            return computeLocFor((Expr) r);
        }
        // must be a CanonicalTypeNode
        CanonicalTypeNode typeNode= (CanonicalTypeNode) r;
        throw new UnsupportedOperationException("Can't produce a Locs for static field references.");
//        return Effects.makeStaticLocs(typeNode.qualifierRef().get() + "." + typeNode.nameString());
    }

    private ObjLocs computeObjLoc(Expr e) {
        if (e instanceof Local) {
            Local local= (Local) e;
            return Effects.makeObj(XTerms.makeLocal(new XNameWrapper<Id>(local.name())));
        }
        return null;
    }

    private Effect computeEffect(Binary binary) throws XFailure {
        Effect e;
        Expr lhs= binary.left();
        Expr rhs= binary.right();
        Effect lhsEff= fEffects.get(lhs);
        Effect rhsEff= fEffects.get(rhs);

        e= followedBy(lhsEff, rhsEff);

        return e;
    }

    private Effect computeEffect(Call call) throws XFailure {
        MethodInstance methodInstance= call.methodInstance();
        StructType methodOwner= methodInstance.container();
        Receiver target = call.target();
        List<Expr> args = call.arguments();
        Effect e= null;

        if (methodOwner instanceof ClassType) {
            ClassType ownerClassType = (ClassType) methodOwner;
            String ownerClassName = ownerClassType.fullName().toString();

            if (ownerClassName.equals("x10.lang.Array")) {
                Expr targetExpr= (Expr) target;
                if (call.name().id().toString().equals("apply")) {
                    return computeEffectOfArrayRead(call, targetExpr, args.get(0));
                } else if (call.name().id().toString().equals("set")) {
                    return computeEffectOfArrayWrite(call, targetExpr, args.get(0), args.get(1));
                }
            } else {
                // First compute the effects of argument evaluation
                e= fEffects.get(target);
                e= followedBy(e, computeEffect(args));
                e= followedBy(e, getMethodEffects(methodInstance));
            }
        }
        return e;
    }

    private Effect computeEffect(List<Expr> args) throws XFailure {
        Effect e= null;
        for(Expr arg: args) {
            Effect argEff= fEffects.get(arg);
            e= followedBy(e, argEff);
        }
        return e;
    }

    private Effect computeEffectOfArrayWrite(Call call, Expr array, Expr index, Expr val) {
        Effect e= Effects.makeEffect(Effects.FUN);
        e.addWrite(createArrayLoc(array, index));
        return e;
    }

    private Effect computeEffectOfArrayRead(Call call, Expr array, Expr index) {
        Effect e= Effects.makeEffect(Effects.FUN);
        e.addRead(createArrayLoc(array, index));
        return e;
    }

    private Effect computeEffect(ForEach n) {
        // TODO return "bottom"
        throw new UnsupportedOperationException("Can't handle ForEach loops!");
    }

    private Effect computeEffect(ForLoop forLoop) {
        Effect e= fEffects.get(forLoop.body());
        // Abstract any effects that involve the loop induction variable
        // TODO How to properly bound the domain of the loop induction variable?
        // It isn't quite correct to use universal quantification for that...
        Formal loopVar= forLoop.formal();
        e.forall(XTerms.makeLocal(new XNameWrapper<Id>(loopVar.name())));
        return e;
    }

    private Effect computeEffect(Local local) {
        Effect e= Effects.makeEffect(Effects.FUN);
        e.addRead(Effects.makeLocalLocs(XTerms.makeLocal(new XVariable(local))));
        return e;
    }

    private Effect computeEffect(Field field) {
        Receiver rcvr= field.target();
        Effect e= Effects.makeEffect(Effects.FUN);
        e.addRead(Effects.makeFieldLocs(computeObjLoc((Expr) rcvr), new XNameWrapper<Id>(field.name())));
        return e;
    }

    private Effect computeEffect(Block b) throws XFailure {
        Effect e= null;
        // aggregate effects of the individual statements.
        // prune out the effects on local vars whose scope is this block.
        List<LocalDecl> blockDecls= collectDecls(b);
        for(Stmt s: b.statements()) {
            Effect stmtEffect= fEffects.get(s);
            Effect filteredEffect= removeLocalVarsFromEffect(blockDecls, stmtEffect);
            e= followedBy(e, filteredEffect);
        }
        return e;
    }

    private Effect removeLocalVarsFromEffect(List<LocalDecl> decls, Effect effect) {
        Effect e= effect;
        for(LocalDecl ld: decls) {
            e= e.exists(XTerms.makeLocal(new XNameWrapper<Id>(ld.name())));
        }
        return e;
    }

    private List<LocalDecl> collectDecls(Block b) {
        List<LocalDecl> result= new LinkedList<LocalDecl>();
        for(Stmt s: b.statements()) {
            if (s instanceof LocalDecl) {
                result.add((LocalDecl) s);
            }
        }        
        return result;
    }

    private Effect followedBy(Effect e1, Effect e2) throws XFailure {
        if (e1 == null) return e2;
        if (e2 == null) return e1;
        return e1.followedBy(e2, fMethodContext);
    }

    public void dump() {
        System.out.println("*** Effects: ");
        for (Node n : fEffects.keySet()) {
            Effect e= fEffects.get(n);
            System.out.println(n.toString() + ":");
            System.out.println("   " + e);
            System.out.println();
        }
    }
}
