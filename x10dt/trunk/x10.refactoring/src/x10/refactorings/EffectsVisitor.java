package x10.refactorings;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import polyglot.ast.Binary;
import polyglot.ast.Block;
import polyglot.ast.BooleanLit;
import polyglot.ast.Call;
import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.CharLit;
import polyglot.ast.ClassLit;
import polyglot.ast.Eval;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.FieldAssign;
import polyglot.ast.FloatLit;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.If;
import polyglot.ast.IntLit;
import polyglot.ast.Local;
import polyglot.ast.LocalAssign;
import polyglot.ast.LocalDecl;
import polyglot.ast.NamedVariable;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.Receiver;
import polyglot.ast.Stmt;
import polyglot.ast.StringLit;
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
import polyglot.ext.x10.types.X10FieldInstance;
import polyglot.ext.x10.types.X10Flags;
import polyglot.ext.x10.types.X10LocalDef;
import polyglot.ext.x10.types.X10LocalInstance;
import polyglot.ext.x10.types.X10ProcedureDef;
import polyglot.ext.x10.types.X10ProcedureInstance;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.types.ClassType;
import polyglot.types.ConstructorInstance;
import polyglot.types.FieldInstance;
import polyglot.types.MethodInstance;
import polyglot.types.Name;
import polyglot.types.ProcedureInstance;
import polyglot.types.Qualifier;
import polyglot.types.StructType;
import polyglot.types.Type;
import polyglot.types.VarDef;
import polyglot.types.VarInstance;
import polyglot.visit.NodeVisitor;
import x10.constraint.XArray;
import x10.constraint.XConstraint;
import x10.constraint.XFailure;
import x10.constraint.XName;
import x10.constraint.XNameWrapper;
import x10.constraint.XTerm;
import x10.constraint.XTerms;
import x10.constraint.XVar;
import x10.effects.constraints.ArrayElementLocs;
import x10.effects.constraints.Effect;
import x10.effects.constraints.Effects;
import x10.effects.constraints.FieldLocs;
import x10.effects.constraints.LocalLocs;
import x10.effects.constraints.Locs;
import x10.refactorings.ReachingDefsVisitor.ValueMap;

public class EffectsVisitor extends NodeVisitor {
    private final Map<Node,Effect> fEffects= new HashMap<Node, Effect>();

    private final XConstraint fMethodContext;

    private final ValueMap fValueMap;

    public static class XVarDefWrapper implements XName {
        private final VarDef fVarDef;
        public XVarDefWrapper(VarDef vd) {
            fVarDef= vd;
        }
        public XVarDefWrapper(NamedVariable var) {
            this((VarDef) var.varInstance().def());
        }
        @Override
        public int hashCode() {
            return fVarDef.name().toString().hashCode();
        }
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof XVarDefWrapper)) return false;
            XVarDefWrapper other= (XVarDefWrapper) obj;
            if (fVarDef == other.fVarDef) return true;
            // Probably should never need the following...
            if (!fVarDef.position().equals(other.fVarDef.position()))
                return false;
            if (!fVarDef.name().toString().equals(other.fVarDef.name().toString()))
                return false;
            return true;
        }
        @Override
        public String toString() {
            return fVarDef.name().toString();
        }
    }

    private static class TermCreator {
        private static Map<Unary.Operator,XName> sUnaryOpMap= new HashMap<Unary.Operator,XName>();
        static {
            sUnaryOpMap.put(Unary.BIT_NOT, new XNameWrapper<Unary.Operator>(Unary.BIT_NOT));
            sUnaryOpMap.put(Unary.NEG, new XNameWrapper<Unary.Operator>(Unary.NEG));
            sUnaryOpMap.put(Unary.NOT, new XNameWrapper<Unary.Operator>(Unary.NOT));
            sUnaryOpMap.put(Unary.POS, new XNameWrapper<Unary.Operator>(Unary.POS));
            sUnaryOpMap.put(Unary.BIT_NOT, new XNameWrapper<Unary.Operator>(Unary.BIT_NOT));
        }

        private static Map<Binary.Operator,XName> sBinaryOpMap= new HashMap<Binary.Operator,XName>();
        static {
            sBinaryOpMap.put(Binary.ADD, new XNameWrapper<Binary.Operator>(Binary.ADD));
            sBinaryOpMap.put(Binary.BIT_AND, new XNameWrapper<Binary.Operator>(Binary.BIT_AND));
            sBinaryOpMap.put(Binary.BIT_OR, new XNameWrapper<Binary.Operator>(Binary.BIT_OR));
            sBinaryOpMap.put(Binary.BIT_XOR, new XNameWrapper<Binary.Operator>(Binary.BIT_XOR));
            sBinaryOpMap.put(Binary.COND_AND, new XNameWrapper<Binary.Operator>(Binary.COND_AND));
            sBinaryOpMap.put(Binary.COND_OR, new XNameWrapper<Binary.Operator>(Binary.COND_OR));
            sBinaryOpMap.put(Binary.DIV, new XNameWrapper<Binary.Operator>(Binary.DIV));
            sBinaryOpMap.put(Binary.EQ, new XNameWrapper<Binary.Operator>(Binary.EQ));
            sBinaryOpMap.put(Binary.GE, new XNameWrapper<Binary.Operator>(Binary.GE));
            sBinaryOpMap.put(Binary.GT, new XNameWrapper<Binary.Operator>(Binary.GT));
            sBinaryOpMap.put(Binary.LE, new XNameWrapper<Binary.Operator>(Binary.LE));
            sBinaryOpMap.put(Binary.LT, new XNameWrapper<Binary.Operator>(Binary.LT));
            sBinaryOpMap.put(Binary.MOD, new XNameWrapper<Binary.Operator>(Binary.MOD));
            sBinaryOpMap.put(Binary.MUL, new XNameWrapper<Binary.Operator>(Binary.MUL));
            sBinaryOpMap.put(Binary.NE, new XNameWrapper<Binary.Operator>(Binary.NE));
            sBinaryOpMap.put(Binary.SHL, new XNameWrapper<Binary.Operator>(Binary.SHL));
            sBinaryOpMap.put(Binary.SHR, new XNameWrapper<Binary.Operator>(Binary.SHR));
            sBinaryOpMap.put(Binary.SUB, new XNameWrapper<Binary.Operator>(Binary.SUB));
            sBinaryOpMap.put(Binary.USHR, new XNameWrapper<Binary.Operator>(Binary.USHR));
        }

        private XName getNameFor(Binary.Operator op) {
            return sBinaryOpMap.get(op);
        }

        private XName getNameFor(Unary.Operator op) {
            return sUnaryOpMap.get(op);
        }

        private class TermVisitor extends NodeVisitor {
            @Override
            public Node leave(Node parent, Node old, Node n, NodeVisitor v) {
                if (old instanceof BooleanLit) {
                    BooleanLit booleanLit = (BooleanLit) old;

                    fTermMap.put(old, XTerms.makeLit(booleanLit.value()));
                } else if (old instanceof FloatLit) {
                    FloatLit floatLit = (FloatLit) old;

                    fTermMap.put(old, XTerms.makeLit(floatLit.value()));
                } else if (old instanceof CharLit) {
                    CharLit charLit = (CharLit) old;

                    fTermMap.put(old, XTerms.makeLit(charLit.value()));
                } else if (old instanceof IntLit) {
                    IntLit intLit = (IntLit) old;

                    fTermMap.put(old, XTerms.makeLit(intLit.value()));
                } else if (old instanceof StringLit) {
                    StringLit stringLit = (StringLit) old;

                    fTermMap.put(old, XTerms.makeLit(stringLit.value()));
                } else if (old instanceof ClassLit) {
                    throw new UnsupportedOperationException("Can't handle class literals.");
                } else if (old instanceof CanonicalTypeNode) {
                    CanonicalTypeNode canonicalTypeNode = (CanonicalTypeNode) old;
                    Qualifier qualifier= canonicalTypeNode.qualifierRef().get();
                    String shortName= canonicalTypeNode.nameString();

                    fTermMap.put(old, XTerms.makeLit(qualifier.toString() + "." + shortName));
                } else if (old instanceof Field) {
                    Field field = (Field) old;
                    Receiver target= field.target();
                    Id name= field.name();

                    fTermMap.put(old, XTerms.makeField((XVar) fTermMap.get(target), new XVarDefWrapper(field)));
                } else if (old instanceof Local) {
                    Local local = (Local) old;
                    Type localType= local.type();
                    X10TypeSystem ts= (X10TypeSystem) localType.typeSystem();

                    if (local.type().isArray() || local.type().descendsFrom(ts.Array())) {
                        fTermMap.put(old, XTerms.makeArray(new XVarDefWrapper(local)));
                    } else {
                        fTermMap.put(old, XTerms.makeLocal(new XVarDefWrapper(local)));
                    }
                } else if (old instanceof Binary) {
                    Binary binary = (Binary) old;
                    Binary.Operator op= binary.operator();
                    Expr lhs= binary.left();
                    Expr rhs= binary.right();
                    XTerm lhsTerm= fTermMap.get(lhs);
                    XTerm rhsTerm= fTermMap.get(rhs);

                    fTermMap.put(old, XTerms.makeAtom(getNameFor(op), lhsTerm, rhsTerm));
                } else if (old instanceof Unary) {
                    Unary unary = (Unary) old;
                    Unary.Operator op= unary.operator();
                    Expr opnd= unary.expr();
                    XTerm opndTerm= fTermMap.get(opnd);

                    fTermMap.put(old, XTerms.makeAtom(getNameFor(op), opndTerm));
                } else if (old instanceof Call) {
                    Call call = (Call) old;
                    
                    throw new UnsupportedOperationException("Don't know how to create an XTerm for a method call.");
                } else if (old instanceof SettableAssign) {
                    SettableAssign sa= (SettableAssign) old;
                    Expr array= sa.array();
                    List<Expr> indices= sa.index();
                    XTerm arrayTerm= fTermMap.get(array);
                    XTerm indexTerm= fTermMap.get(indices.get(0));

                    fTermMap.put(old, XTerms.makeArrayElement((XArray) arrayTerm, indexTerm));
                } else if (old instanceof FieldAssign) {
                    FieldAssign fa= (FieldAssign) old;
                    FieldInstance fi= fa.fieldInstance();
                    Receiver target= fa.target();

                    fTermMap.put(old, XTerms.makeField((XVar) fTermMap.get(target), new XVarDefWrapper(fi.def())));
                } else if (old instanceof LocalAssign) {
                    LocalAssign la= (LocalAssign) old;
                    Local l= la.local();

                    fTermMap.put(old, XTerms.makeLocal(new XVarDefWrapper(l)));
                } else if (old instanceof Id) {
                    // do nothing
                    System.out.println("TermVisitor doing nothing for expr of type " + old.getClass().getCanonicalName());
                } else {
                    throw new UnsupportedOperationException("Unknown expression type");
                }
                return super.leave(parent, old, n, v);
            }
        }

        private final Expr fExpr;
        private final Map<Node,XTerm> fTermMap= new HashMap<Node,XTerm>();
        private final TermVisitor fVisitor= new TermVisitor();

        public TermCreator(Expr e) {
            fExpr= e;
            fExpr.visit(fVisitor);
        }

        public XTerm getTerm() {
            return fTermMap.get(fExpr);
        }
    }

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
                X10RefactoringPlugin.getInstance().logException(f.getMessage(), f);
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

        if (((X10Flags) li.flags()).isValue()) {
            Effect rhsEff= fEffects.get(rhs);
            Effect writeEff= Effects.makeEffect(Effects.FUN);
            writeEff.addWrite(Effects.makeLocalLocs(XTerms.makeLocal(new XVarDefWrapper(l))));
            result= followedBy(rhsEff, writeEff);
        } else {
            return Effects.makeBottomEffect();
        }
        return result;
    }

    private Effect computeEffect(FieldAssign fa) throws XFailure {
        Effect result= null;
        X10FieldInstance fi= (X10FieldInstance) fa.fieldInstance();
        Receiver target= fa.target();
        Expr rhs= fa.right();

        if (((X10Flags) fi.flags()).isValue()) {
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
                X10RefactoringPlugin.getInstance().logException(f.getMessage(), f);
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
                result= fEffects.get(target);
                result= followedBy(result, computeEffect(args));
                result= followedBy(result, getMethodEffects(methodInstance));
            }
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
        System.out.println("Computing effect of block " + b);
        List<LocalDecl> blockDecls= collectDecls(b);
        for(Stmt s: b.statements()) {
            Effect stmtEffect= fEffects.get(s);
            System.out.println("   statement = " + s + "; effect = " + stmtEffect);
            Effect filteredEffect= removeLocalVarsFromEffect(blockDecls, stmtEffect);
            System.out.println("             filtered effect = " + filteredEffect);
            result= followedBy(result, filteredEffect);
            System.out.println("   aggregate effect = " + result);
        }
        return result;
    }

    private Effect removeLocalVarsFromEffect(List<LocalDecl> decls, Effect effect) {
        Effect result= effect;
        for(LocalDecl ld: decls) {
            XVarDefWrapper localName = new XVarDefWrapper(ld.localDef());
            if (((X10Flags) ld.flags().flags()).isValue()) {
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
        System.out.println("*** Effects: ");
        for (Node n : fEffects.keySet()) {
            Effect e= fEffects.get(n);
            System.out.println(n.toString() + ":");
            System.out.println("   " + e);
            System.out.println();
        }
    }
}
