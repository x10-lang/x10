package org.eclipse.imp.x10dt.refactoring.effects;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.imp.x10dt.refactoring.X10DTRefactoringPlugin;
import org.eclipse.imp.x10dt.refactoring.analysis.ReachingDefsVisitor.ValueMap;

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
import polyglot.ast.VarDecl;
import polyglot.ast.While;
import polyglot.ast.Unary.Operator;
import polyglot.ext.x10.ast.Async;
import polyglot.ext.x10.ast.AtStmt;
import polyglot.ext.x10.ast.Atomic;
import polyglot.ext.x10.ast.DepParameterExpr;
import polyglot.ext.x10.ast.ForEach;
import polyglot.ext.x10.ast.ForLoop;
import polyglot.ext.x10.ast.SettableAssign;
import polyglot.ext.x10.ast.X10Formal;
import polyglot.ext.x10.ast.X10ProcedureDecl;
import polyglot.ext.x10.types.X10ClassType;
import polyglot.ext.x10.types.X10FieldInstance;
import polyglot.ext.x10.types.X10Flags;
import polyglot.ext.x10.types.X10LocalInstance;
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
import polyglot.types.VarInstance;
import polyglot.visit.NodeVisitor;
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

public class EffectsVisitor extends NodeVisitor {
    private final Map<Node,Effect> fEffects= new HashMap<Node, Effect>();

    private final XConstraint fMethodContext;

    private final ValueMap fValueMap; // TODO Do we really need this in the first place?

    private boolean fVerbose;

    private PrintStream fDiagStream= System.out;

    public EffectsVisitor(ValueMap valueMap, X10ProcedureDecl proc) throws XFailure {
        fValueMap = valueMap;
        fMethodContext= computeMethodContextConstraint(proc);
    }

    public void setVerbose(PrintStream diagStream) {
        fVerbose= true;
        fDiagStream= diagStream;
    }

    private final XConstraint computeMethodContextConstraint(X10ProcedureDecl proc) throws XFailure {
        XConstraint result= null;
        DepParameterExpr methodGuard= proc.guard();
        if (methodGuard != null) {
            result= conjunction(result, methodGuard.valueConstraint().get());
        }
        List<Formal> formals= proc.formals();
        for(Formal formal: formals) {
            X10Formal xFormal= (X10Formal) formal;
//          X10LocalDef xDef= (X10LocalDef) xFormal.localDef();
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

    public Effect accumulateEffectsFor(List<Node> nodes) throws XFailure {
        Effect result= null;
        for(Node node: nodes) {
            result= followedBy(result, fEffects.get(node));
        }
        return result;
    }

    private boolean isValVariable(VarInstance<?> vi) {
        return (vi.flags() instanceof X10Flags) ? ((X10Flags) vi.flags()).isValue() : false;
    }

    private boolean isValVariable(VarDecl vd) {
        return (vd.flags() instanceof X10Flags) ? ((X10Flags) vd.flags().flags()).isValue() : false;
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
        return c1.addIn(c2);
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
        } else if (expr instanceof Call) {
            Call call= (Call) expr;
            MethodInstance mi= call.methodInstance();
            if (mi.container() instanceof ClassType && ((ClassType) mi.container()).fullName().toString().equals("x10.lang.Rail") &&
                mi.name().toString().equals("apply") && mi.formalTypes().size() == 1) { // an array ref
                List<Expr> args= call.arguments();

                return createArrayLoc((Expr) call.target(), args.get(0));
            }
        }
        return null;
    }

    private Effect getMethodEffects(ProcedureInstance procInstance) {
        X10ProcedureInstance xpi= (X10ProcedureInstance) procInstance;
        X10ProcedureDef xpd= (X10ProcedureDef) xpi.def();
        List<Type> annotations= xpd.annotations();
        boolean foundAnnotation= false;
        Effect e= Effects.makeEffect(false);

        for (Type annoType : annotations) {
            if (annoType instanceof ClassType) {
                X10ClassType annoClassType = (X10ClassType) annoType;
                String annoName= annoClassType.name().toString();
                if (!annoName.equals("read") && !annoName.equals("write") && !annoName.equals("atomicInc")) {
                    continue;
                }
                List<Expr> declaredLocs= annoClassType.propertyInitializers();
                for(Expr declaredLoc: declaredLocs) {
                    Locs locs= computeLocFor(declaredLoc);

                    if (annoName.equals("read")) {
                        e.addRead(locs);
                    } else if (annoName.equals("write")) {
                        e.addWrite(locs);
                    } else if (annoName.equals("atomicInc")) {
                        e.addAtomicInc(locs);
                    }
                }
                foundAnnotation= true;
            }
        }
        return e; // foundAnnotation ? e : Effects.BOTTOM_EFFECT; // return 'bottom' here - don't know what the effects are, so be safe
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
            } else if (old instanceof Atomic) {
                result= computeEffect((Atomic) old);
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
            } else if (old instanceof ForEach) {
                result= computeEffect((ForEach) old);
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

        if (isValVariable(li)) {
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

        if (isValVariable(fi)) {
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
        Effect result;

        if (isValVariable(local.localInstance())) {
            result= null;
        } else {
            result= Effects.makeEffect(Effects.FUN);
            result.addRead(Effects.makeLocalLocs(XTerms.makeLocal(new XVarDefWrapper(local))));
        }
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
        StructType methodOwner= methodInstance.container();
        Receiver target = call.target();
        List<Expr> args = call.arguments();
        Effect result= null;

        // TODO Perform substitutions of actual parameters for formal parameters
        if (methodOwner instanceof ClassType) {
            ClassType ownerClassType = (ClassType) methodOwner;
            String ownerClassName = ownerClassType.fullName().toString();

            if (ownerClassName.equals("x10.lang.Array")) {
                if (methodInstance.flags().isStatic()) {
                    if (call.name().id().toString().equals("make")) {
                        
                    }
                } else {
                    Expr targetExpr= (Expr) target;
                    if (call.name().id().toString().equals("apply")) {
                        result= computeEffectOfArrayRead(call, targetExpr, args.get(0));
                    } else if (call.name().id().toString().equals("set")) {
                        result= computeEffectOfArrayWrite(call, targetExpr, args.get(0), args.get(1));
                    }
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

    private Effect computeEffect(Atomic atomic) {
        Effect bodyEff= fEffects.get(atomic.body());
        // TODO Should really mark that we've entered an atomic and change the
        // processing of what's inside, rather than trying to do this after the
        // body effects have already been created.
        // TODO Create a means to mark an entire effect atomic to handle atomic blocks properly
        return bodyEff;
    }

    private Effect computeEffect(Async async) {
        Effect bodyEff= fEffects.get(async.body());

        return bodyEff.makeParFun();
    }

    private Effect computeEffect(ForEach n) {
        Effect bodyEff= fEffects.get(n.body());

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
            if (isValVariable(ld)) {
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
