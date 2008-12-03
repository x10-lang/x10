package polyglot.ext.x10.types.constr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import polyglot.ast.Binary;
import polyglot.ast.Unary;
import polyglot.ast.Binary.Operator;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.constr.omega.omegaLib.Relation;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeObject_c;
import polyglot.types.TypeSystem;
import polyglot.util.InternalCompilerError;

public class OmegaConstraint_c extends TypeObject_c implements SimpleConstraint {
    protected C_Term t;
    protected OmegaConstraintSystem eval;
    
    protected OmegaConstraint_c(TypeSystem ts, C_Term t, OmegaConstraintSystem eval) {
        super(ts);
        this.t = t;
        this.eval = eval;
    }
    
    public ConstraintSystem constraintSystem() {
        return eval;
    }
    
    public void internRecursively(C_Var var, Constraint container) throws Failure {
        Type t = var.type();
        if (t instanceof X10Type) {
            Constraint c = X10TypeMixin.depClause((X10Type) t);
            for (SimpleConstraint sc : c.conjuncts()) {
                if (sc.constraintSystem() == eval)
                    addIn(sc);
            }
        }
    }

    @Override
    public OmegaConstraint_c copy() {
        return (OmegaConstraint_c) super.copy();
    }
    
    @Override
    public X10TypeSystem typeSystem() {
        return (X10TypeSystem) super.typeSystem();
    }
    
    public Set<C_Var> vars() {
        List<C_Var> vars = new ArrayList<C_Var>();
        t.collectVars(vars);
        return new LinkedHashSet<C_Var>(vars);
    }

    String flip(String op) {
        if (op.equals("<=")) return ">=";
        if (op.equals(">=")) return "<=";
        if (op.equals("<")) return ">";
        if (op.equals(">")) return "<";
        if (op.equals("==")) return "==";
        if (op.equals("!=")) return "!=";
        return null;
    }
    
    C_Term neg(C_Term t) {
        if (t instanceof C_Lit) {
            C_Lit l = (C_Lit) t;
            if (l.val() instanceof Integer) {
                Integer i = (Integer) l.val();
                return new C_Lit_c(-i, l.type());
            }
        }
        return new C_UnaryTerm_c("-", t, t.type());
    }

    public SimpleConstraint unaryOp(Unary.Operator op) throws Failure {
        if (op == Unary.NEG) {
            if (t instanceof C_BinaryTerm) {
                C_BinaryTerm b = (C_BinaryTerm) t;
                C_Term left = b.left();
                C_Term right = b.right();
                String bop = b.op();
                if (flip(bop) != null) {
                    if (left instanceof C_Special && ((C_Special) left).kind() == C_Special.SELF) {
                        OmegaConstraint_c n = (OmegaConstraint_c) copy();
                        n.t = new C_BinaryTerm_c(flip(bop), left, neg(right), b.type());
                        return n;
                    }
                    if (right instanceof C_Special && ((C_Special) right).kind() == C_Special.SELF) {
                        OmegaConstraint_c n = (OmegaConstraint_c) copy();
                        n.t = new C_BinaryTerm_c(flip(bop), neg(left), right, b.type());
                        return n;
                    }
                }
            }
        }
        return null;
    }

    public SimpleConstraint binaryOp(Binary.Operator op, Constraint other, Constraint me) throws Failure {
        if (t instanceof C_BinaryTerm) {
            C_BinaryTerm b = (C_BinaryTerm) t;
            C_Term left = b.left();
            C_Term right = b.right();
            String bop = b.op();
            if (flip(bop) != null) {
                if (left instanceof C_Special && ((C_Special) left).kind() == C_Special.SELF) {
                    return binaryOp(bop, left, right, op, other);
                }
                if (right instanceof C_Special && ((C_Special) right).kind() == C_Special.SELF) {
                    return binaryOp(flip(bop), right, left, op, other);
                }
            }
        }
        return null;
    }

    private SimpleConstraint binaryOp(String bop, C_Term self, C_Term bound, Operator op, Constraint c) throws Failure {
        for (SimpleConstraint sc : c.conjuncts()) {
            if (sc instanceof OmegaConstraint_c) {
                OmegaConstraint_c oc = (OmegaConstraint_c) sc;
                if (oc.t instanceof C_BinaryTerm) {
                    C_BinaryTerm b = (C_BinaryTerm) oc.t;
                    C_Term left = b.left();
                    C_Term right = b.right();
                    String bop2 = b.op();
                    if (flip(bop2) != null) {
                        if (left instanceof C_Special && ((C_Special) left).kind() == C_Special.SELF) {
                            SimpleConstraint sc2 = binaryOp(bop, self, bound, bop2, right, op);
                            return sc2;
                        }
                        if (right instanceof C_Special && ((C_Special) right).kind() == C_Special.SELF) {
                            SimpleConstraint sc2 = binaryOp(bop, self, bound, flip(bop2), left, op);
                            return sc2;
                        }
                    }   
                }
            }
        }
        return null;
    }

        // (self bop bond) op (self bop2 bound2)
        // e.g.
        // (self <= n) + (self <= m) --> (self <= n+m)
    private SimpleConstraint binaryOp(String bop, C_Term self, C_Term bound, String bop2, C_Term bound2, Operator op) {
        X10TypeSystem xts = typeSystem();
        if (bop.equals(bop2)) {
            if (op == Binary.ADD) {
                C_BinaryTerm t = new C_BinaryTerm_c(bop, self, new C_BinaryTerm_c("+", bound, bound2, bound2.type()), ts.Boolean());
                return new OmegaConstraint_c(xts, t, eval);
            }
            if (op == Binary.MUL) {
                C_BinaryTerm t = new C_BinaryTerm_c(bop, self, new C_BinaryTerm_c("*", bound, bound2, bound2.type()), ts.Boolean());
                return new OmegaConstraint_c(xts, t, eval);
            }
        }
        return null;
    }

    public boolean entailedBy(Constraint c, Constraint me) {
        List<OmegaConstraint_c> cs = new ArrayList<OmegaConstraint_c>();
        
        for (SimpleConstraint c2 : c.conjuncts()) {
            if (c2 == this)
                return true;
            if (c2 instanceof OmegaConstraint_c) {
                OmegaConstraint_c oc = (OmegaConstraint_c) c2;
                cs.add(oc);
            }
        }
        
        if (cs.isEmpty()) {
            return false;
        }
        
        Map<C_Var,Integer> varMap = new HashMap<C_Var, Integer>();

        eval.collectVariables(this.t, varMap);
        
        for (OmegaConstraint_c oc : cs) {
            eval.collectVariables(oc.t, varMap);
        }
        
        try {
            // forall x. S => R
            // ==
            // ! exists x. !(S => R)
            // is true if:
            // { x | !(S => R) } is empty
            // { x | !(!S || R) } is empty
            // { x | S && !R } is empty
            
            // !R
            C_Term t2 = new C_UnaryTerm_c("!", t, t.type());
            
            // !R && S
            for (OmegaConstraint_c oc : cs) {
                t2 = new C_BinaryTerm_c("&&", t2, oc.t, t.type());
            }
            
            Relation R = eval.omegaRelation(t2, varMap, false);
//            printRelation(new Relation(R));

            eval.omegaLib.trace = true;
            eval.omegaLib.skipSetChecks++;
            
            R.simplify();

            eval.omegaLib.skipSetChecks--;
            eval.omegaLib.trace = false;

            printRelation(R);

            boolean def_not_sat = R.isUpperBoundDefinitelyNotSatisfiable();
            boolean not_sat = R.isNotSatisfiable();
            boolean ub_sat = R.isUpperBoundSatisfiable();
            boolean lb_sat = R.isLowerBoundSatisfiable();
            boolean sat = R.isSatisfiable();
            boolean taut = R.isObviousTautology();
            
            return def_not_sat && not_sat && ! sat && ! ub_sat && ! lb_sat;
        }
        catch (SemanticException e) {
            // thrown by omegaRelation, but we should have checked that the constraint is well-formed during translation.
            throw new InternalCompilerError(e.getMessage(), e.position(), e);
        }
    }

    protected void printRelation(Relation R) {
        eval.omegaLib.skipSetChecks++;
//        eval.omegaLib.useUglyNames++;
        R.getRelBody().print();
        System.out.println();
        R.getRelBody().DNFize().print();
        System.out.println();
//        eval.omegaLib.useUglyNames--;
        eval.omegaLib.skipSetChecks--;
    }
    
    public String toString() {
        return t.toString();
    }

    public C_Var find(String varName) {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean valid() {
    	return false;
    }
    
    public SimpleConstraint substitute(C_Var y, C_Root x, boolean propagate, Constraint container, HashSet<C_Term> visited) throws Failure {
        OmegaConstraint_c sc = copy();
        sc.t = t.substitute(y, x, propagate, visited);
        return sc;
    }
    
    public void addIn(SimpleConstraint sc) throws Failure {
        addIn((OmegaConstraint_c) sc);        
    }
}