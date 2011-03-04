package x10dt.refactoring.effects;

import java.io.PrintStream;
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
import polyglot.ast.FieldAssign;
import polyglot.ast.Formal;
import polyglot.ast.If;
import polyglot.ast.Local;
import polyglot.ast.LocalAssign;
import polyglot.ast.LocalDecl;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.Receiver;
import polyglot.ast.Stmt;
import polyglot.ast.Unary;
import polyglot.ast.Unary.Operator;
import polyglot.ast.VarDecl;
import polyglot.ast.While;
import polyglot.types.ClassType;
import polyglot.types.ConstructorInstance;
import polyglot.types.ContainerType;
import polyglot.types.Flags;
import polyglot.types.ProcedureInstance;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.visit.NodeVisitor;
import x10.ast.Async;
import x10.ast.AtStmt;
import x10.ast.DepParameterExpr;
import x10.ast.ForLoop;
import x10.ast.SettableAssign;
import x10.ast.X10Formal;
import x10.ast.X10MethodDecl;
import x10.constraint.XConstraint;
import x10.constraint.XFailure;
import x10.constraint.XTerm;
import x10.constraint.XTerms;
import x10.effects.constraints.ArrayElementLocs;
import x10.effects.constraints.Effect;
import x10.effects.constraints.Effects;
import x10.effects.constraints.FieldLocs;
import x10.effects.constraints.LocalLocs;
import x10.effects.constraints.Locs;
import x10.types.MethodInstance;
import x10.types.X10FieldInstance;
import x10.types.X10LocalInstance;
import x10.types.X10ProcedureDef;
import x10.types.X10ProcedureInstance;
import x10dt.refactoring.X10DTRefactoringPlugin;
import x10dt.refactoring.analysis.ReachingDefsVisitor.ValueMap;

public class EffectsVisitor extends NodeVisitor {
    private final Map<Node,Effect> fEffects= new HashMap<Node, Effect>();

    private final XConstraint fMethodContext;

    private final ValueMap fValueMap;

    private boolean fVerbose;

    private PrintStream fDiagStream= System.out;

    public EffectsVisitor(ValueMap valueMap, X10MethodDecl method) throws XFailure {
        fValueMap = valueMap;
        fMethodContext= computeMethodContextConstraint(method);
    }

    public void setVerbose(PrintStream diagStream) {
        fVerbose= true;
        fDiagStream= diagStream;
    }

    private final XConstraint computeMethodContextConstraint(X10MethodDecl method) throws XFailure {
        XConstraint result= null;
        DepParameterExpr methodGuard= method.guard();
        if (methodGuard != null) {
            result= conjunction(result, methodGuard.valueConstraint().get());
        }
        List<Formal> formals= method.formals();
        for(Formal formal: formals) {
            X10Formal xFormal= (X10Formal) formal;
//          X10LocalDef xDef= (X10LocalDef) xFormal.localDef();
            Type xType= xFormal.type().type();
            XConstraint xc= Types.xclause(xType);
            if (xc != null) {
                result= conjunction(result, xc);
            }
        }
        return result;
    }

    public Effect getEffectFor(Node n) {
        return fEffects.get(n);
    }

    public Effect accumulateEffectsFor(List<Node> nodes) throws XFailure {
        Effect result= null;
        for(Node node: nodes) {
            result= followedBy(result, fEffects.get(node));
        }
        return result;
    }

    // ====================================================
    // Wrappers for effects/constraint ops (for debugging)
    // ====================================================
    private Effect followedBy(Effect e1, Effect e2) throws XFailure {
        if (e1 == null) return e2;
        if (e2 == null) return e1;
        return e1.followedBy(e2, fMethodContext);
    }

    private XConstraint conjunction(XConstraint c1, XConstraint c2) throws XFailure {
        if (c1 == null)
            return c2.copy();
        return c1.leastUpperBound(c2);
    }

    // ================
    // Locations/Terms
    // ================
    private Locs createArrayLoc(Expr array, Expr index) {
        XTerm arrayTerm = createTermForExpr(array);
        XTerm indexTerm = createTermForExpr(index);
        return Effects.makeArrayElementLocs(arrayTerm, indexTerm);
    }

    private static XTerm createTermForExpr(Expr e) {
        TermCreator tc= new TermCreator(e);
        return tc.getTerm();
    }

    private XTerm createTermForReceiver(Receiver r) {
        if (r instanceof Expr) {
            return createTermForExpr((Expr) r);
        }
        // must be a CanonicalTypeNode
        CanonicalTypeNode typeNode= (CanonicalTypeNode) r;
        throw new UnsupportedOperationException("Can't produce an XTerm for type references.");
    }

    private Locs computeLocFor(Expr expr) {
        if (expr instanceof Local) {
            Local local= (Local) expr;
            LocalLocs ll= Effects.makeLocalLocs(XTerms.makeLocal(new XVarDefWrapper(local)));
            return ll;
        } else if (expr instanceof Field) {
            Field field= (Field) expr;
            FieldLocs fl= Effects.makeFieldLocs(createTermForReceiver(field.target()), new XVarDefWrapper(field));
            return fl;
        } else if (expr instanceof SettableAssign) {
            SettableAssign sa = (SettableAssign) expr;
            Expr array= sa.array();
            List<Expr> indices= sa.index();
            ArrayElementLocs ael= Effects.makeArrayElementLocs(createTermForExpr(array), createTermForExpr(indices.get(0)));
            return ael;
        }
        return null;
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

    // ============
    // Visitor
    // ============
    @Override
    public Node leave(Node parent, Node old, Node n, NodeVisitor v) {
        Effect result= null;
        try {
            if (old instanceof Async) {
                Async async = (Async) old;
                result= computeEffect(async);
            } else if (old instanceof AtStmt) {
                result= fEffects.get(((AtStmt) old).body());
            } else if (old instanceof Binary) {
                result = computeEffect((Binary) old);
            } else if (old instanceof Block) {
                result= computeEffect((Block) old);
            } else if (old instanceof Call) {
                result= computeEffect((Call) old);
            } else if (old instanceof Eval) {
                result= fEffects.get(((Eval) old).expr());
            } else if (old instanceof Field) {
                result= computeEffect((Field) old);
            } else if (old instanceof FieldAssign) {
                result= computeEffect((FieldAssign) old);
            } else if (old instanceof ForLoop) {
                result= computeEffect((ForLoop) old);
            } else if (old instanceof If) {
                result= computeEffect((If) old);
            } else if (old instanceof Local) {
                result= computeEffect((Local) old);
            } else if (old instanceof LocalAssign) {
                result= computeEffect((LocalAssign) old);
            } else if (old instanceof LocalDecl) {
                result= computeEffect((LocalDecl) old);
            } else if (old instanceof New) {
                result= computeEffect((New) old);
            } else if (old instanceof SettableAssign) {
                result = computeEffect((SettableAssign) old);
            } else if (old instanceof Unary) {
                result= computeEffect((Unary) old);
            } else if (old instanceof While) {
                result= Effects.makeBottomEffect();
            }
            if (result != null) {
                fEffects.put(old, result);
            }
        } catch (XFailure f) {
            X10DTRefactoringPlugin.getInstance().logException(f.getMessage(), f);
        }
        return super.leave(parent, old, n, v);
    }

    // ============
    // Assignments
    // ============
    private Effect computeEffect(LocalAssign la) throws XFailure {
        Effect result= null;
        Local l= la.local();
        X10LocalInstance li= (X10LocalInstance) l.localInstance();
        Expr rhs= la.right();

        if (((Flags) li.flags()).isValue()) {
            Effect rhsEff= fEffects.get(rhs);
//            Effect writeEff= Effects.makeEffect(Effects.FUN);
//            writeEff.addWrite(Effects.makeLocalLocs(XTerms.makeLocal(new XVarDefWrapper(l))));
//            result= followedBy(rhsEff, writeEff);
            result= rhsEff;
        } else {
            Effect rhsEff= fEffects.get(rhs);
            Effect writeEff= Effects.makeEffect(Effects.FUN);
            writeEff.addWrite(Effects.makeLocalLocs(XTerms.makeLocal(new XVarDefWrapper(l))));
            result= followedBy(rhsEff, writeEff);
        }
        return result;
    }

    private Effect computeEffect(FieldAssign fa) throws XFailure {
        Effect result= null;
        X10FieldInstance fi= (X10FieldInstance) fa.fieldInstance();
        Receiver target= fa.target();
        Expr rhs= fa.right();

        if (((Flags) fi.flags()).isValue()) {
            Effect rhsEff= fEffects.get(rhs);
            Effect writeEff= Effects.makeEffect(Effects.FUN);
            writeEff.addWrite(Effects.makeFieldLocs(createTermForReceiver(target), new XVarDefWrapper(fi.def())));
            result= followedBy(rhsEff, writeEff);
        } else {
            return Effects.makeBottomEffect();
        }
        return result;
    }

    private Effect computeEffect(SettableAssign sa) throws XFailure {
        Expr indexExpr = sa.index().get(0);
        Expr arrayExpr = sa.array();
        Effect result= null;
        Effect rhsEff= fEffects.get(sa.right());
        Effect indexEff= fEffects.get(indexExpr);
        Effect arrayEff= fEffects.get(arrayExpr);
        Effect writeEff= Effects.makeEffect(Effects.FUN);
        writeEff.addWrite(createArrayLoc(arrayExpr, indexExpr));
        result= followedBy(arrayEff, indexEff);
        result= followedBy(result, rhsEff);
        result= followedBy(result, writeEff);
        return result;
    }

    // ============
    // Declarations
    // ============
    private Effect computeEffect(LocalDecl localDecl) throws XFailure {
        Expr init = localDecl.init();
        Effect result= null;
        if (init != null) {
            Effect initEff= fEffects.get(init);
            Effect write= Effects.makeEffect(Effects.FUN);
            write.addWrite(computeLocFor(localDecl));
            result= followedBy(initEff, write);
        }
        return result;
    }

    private Locs computeLocFor(VarDecl vd) {
        if (vd instanceof LocalDecl) {
            LocalDecl localDecl = (LocalDecl) vd;
            return Effects.makeLocalLocs(XTerms.makeLocal(new XVarDefWrapper(localDecl.varDef())));
        }
        throw new UnsupportedOperationException("Don't know how to make a Locs for " + vd);
    }

    // ============
    // Expressions
    // ============
    private Effect computeEffect(Local local) {
        Effect result= Effects.makeEffect(Effects.FUN);
        result.addRead(Effects.makeLocalLocs(XTerms.makeLocal(new XVarDefWrapper(local))));
        return result;
    }

    private Effect computeEffect(Field field) {
        Receiver rcvr= field.target();
        Effect result= Effects.makeEffect(Effects.FUN);
        result.addRead(Effects.makeFieldLocs(createTermForReceiver(rcvr), new XVarDefWrapper(field)));
        return result;
    }

    private Effect computeEffect(New neew) throws XFailure {
        Effect result= null;
        ConstructorInstance ctorInstance = neew.constructorInstance();
        List<Expr> args = neew.arguments();
        result= computeEffect(args);
        result= followedBy(result, getMethodEffects(ctorInstance));
        return result;
    }

    private Effect computeEffect(Unary unary) {
        Effect result;
        Expr opnd= unary.expr();
        Operator op= unary.operator();
        Effect opndEff= fEffects.get(opnd);

        if (op == Unary.BIT_NOT || op == Unary.NEG || op == Unary.NOT || op == Unary.POS) {
            result= opndEff;
        } else {
            // one of the unary inc/dec ops
            Effect write= Effects.makeEffect(Effects.FUN);
            write.addAtomicInc(computeLocFor(opnd));
            try {
                if (op == Unary.POST_DEC || op == Unary.POST_INC) {
                    result= opndEff.followedBy(write, fMethodContext);
                } else /*if (op == Unary.PRE_DEC || op == Unary.PRE_INC)*/ {
                    result= write.followedBy(opndEff, fMethodContext);
                }
            } catch (XFailure f) {
                X10DTRefactoringPlugin.getInstance().logException(f.getMessage(), f);
                return null;
            }
        }
        return result;
    }

    private Effect computeEffect(Binary binary) throws XFailure {
        Effect result;
        Expr lhs= binary.left();
        Expr rhs= binary.right();
        Effect lhsEff= fEffects.get(lhs);
        Effect rhsEff= fEffects.get(rhs);

        result= followedBy(lhsEff, rhsEff);

        return result;
    }

    private Effect computeEffect(Call call) throws XFailure {
        MethodInstance methodInstance= call.methodInstance();
        ContainerType methodOwner= methodInstance.container();
        Receiver target = call.target();
        List<Expr> args = call.arguments();
        Effect result= null;

        if (methodOwner instanceof ClassType) {
            ClassType ownerClassType = (ClassType) methodOwner;
            String ownerClassName = ownerClassType.fullName().toString();

            if (ownerClassName.equals("x10.lang.Array")) {
                Expr targetExpr= (Expr) target;
                if (call.name().id().toString().equals("apply")) {
                    result= computeEffectOfArrayRead(call, targetExpr, args.get(0));
                } else if (call.name().id().toString().equals("set")) {
                    result= computeEffectOfArrayWrite(call, targetExpr, args.get(0), args.get(1));
                }
            } else {
                // First compute the effects of argument evaluation
                result= fEffects.get(target);
                result= followedBy(result, computeEffect(args));
                result= followedBy(result, getMethodEffects(methodInstance));
            }
        }
        if (fVerbose) {
            fDiagStream.println("Effect of call to method " + methodInstance.container() + "." + methodInstance.signature() + ": " + result);
        }
        return result;
    }

    private Effect computeEffect(List<Expr> args) throws XFailure {
        Effect result= null;
        for(Expr arg: args) {
            Effect argEff= fEffects.get(arg);
            result= followedBy(result, argEff);
        }
        return result;
    }

    private Effect computeEffectOfArrayWrite(Call call, Expr array, Expr index, Expr val) {
        Effect result= Effects.makeEffect(Effects.FUN);
        result.addWrite(createArrayLoc(array, index));
        return result;
    }

    private Effect computeEffectOfArrayRead(Call call, Expr array, Expr index) {
        Effect result= Effects.makeEffect(Effects.FUN);
        result.addRead(createArrayLoc(array, index));
        return result;
    }

    // ============
    // Statements
    // ============
    private Effect computeEffect(If n) throws XFailure {
        Effect condEff= fEffects.get(n.cond());
        Effect thenEff= fEffects.get(n.consequent());
        Effect elseEff= (n.alternative() != null) ? fEffects.get(n.alternative()) : null;

        return followedBy(followedBy(condEff, thenEff), elseEff);
    }

    private Effect computeEffect(Async async) {
        Effect bodyEff= fEffects.get(async.body());

        return bodyEff.makeParFun();
    }

    private Effect computeEffect(ForLoop forLoop) {
        Effect bodyEff= fEffects.get(forLoop.body());
        // Abstract any effects that involve the loop induction variable
        // TODO How to properly bound the domain of the loop induction variable?
        // It isn't quite correct to use universal quantification for that...
        Formal loopVar= forLoop.formal();

        return bodyEff.forall(XTerms.makeLocal(new XVarDefWrapper(loopVar.localDef())));
    }

    private Effect computeEffect(Block b) throws XFailure {
        Effect result= null;
        // aggregate effects of the individual statements.
        // prune out the effects on local vars whose scope is this block.
        if (fVerbose) fDiagStream.println("Computing effect of block " + b);
        List<LocalDecl> blockDecls= collectDecls(b);
        for(Stmt s: b.statements()) {
            Effect stmtEffect= fEffects.get(s);
            if (fVerbose) fDiagStream.println("   statement = " + s + "; effect = " + stmtEffect);
            Effect filteredEffect= removeLocalVarsFromEffect(blockDecls, stmtEffect);
            if (fVerbose) fDiagStream.println("             filtered effect = " + filteredEffect);
            result= followedBy(result, filteredEffect);
            if (fVerbose) fDiagStream.println("   aggregate effect = " + result);
        }
        return result;
    }

    private Effect removeLocalVarsFromEffect(List<LocalDecl> decls, Effect effect) {
        Effect result= effect;
        for(LocalDecl ld: decls) {
            XVarDefWrapper localName = new XVarDefWrapper(ld.localDef());
            if (((Flags) ld.flags().flags()).isValue()) {
                Expr init= ld.init();
                XTerm initTerm= createTermForExpr(init);
                result= result.exists(XTerms.makeLocal(localName), initTerm);
            } else {
                result= result.exists(Effects.makeLocalLocs(XTerms.makeLocal(localName)));
            }
        }
        return result;
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

    public void dump() {
        fDiagStream.println("*** Effects: ");
        for (Node n : fEffects.keySet()) {
            Effect e= fEffects.get(n);
            fDiagStream.println(n.toString() + ":");
            fDiagStream.println("   " + e);
            fDiagStream.println();
        }
    }
}
