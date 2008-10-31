package polyglot.ext.x10.types.constr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import polyglot.ast.Binary;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.IntLit;
import polyglot.ast.Lit;
import polyglot.ast.Local;
import polyglot.ast.NodeFactory;
import polyglot.ast.Receiver;
import polyglot.ast.TypeNode;
import polyglot.ast.Unary;
import polyglot.ast.Variable;
import polyglot.ext.x10.ast.Here;
import polyglot.ext.x10.ast.X10Special;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.constr.omega.omegaLib.ConstraintHandle;
import polyglot.ext.x10.types.constr.omega.omegaLib.EQHandle;
import polyglot.ext.x10.types.constr.omega.omegaLib.FAnd;
import polyglot.ext.x10.types.constr.omega.omegaLib.FOr;
import polyglot.ext.x10.types.constr.omega.omegaLib.Formula;
import polyglot.ext.x10.types.constr.omega.omegaLib.GEQHandle;
import polyglot.ext.x10.types.constr.omega.omegaLib.OmegaLib;
import polyglot.ext.x10.types.constr.omega.omegaLib.RelBody;
import polyglot.ext.x10.types.constr.omega.omegaLib.Relation;
import polyglot.ext.x10.types.constr.omega.omegaLib.VarDecl;
import polyglot.frontend.Globals;
import polyglot.types.LocalInstance;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.util.Position;

public class OmegaConstraintSystem implements ConstraintSystem {

    X10TypeSystem typeSystem;
    OmegaLib omegaLib;
    
    public OmegaConstraintSystem(X10TypeSystem xts) {
        this.typeSystem = xts;
        
        omegaLib = new OmegaLib(true);
        omegaLib.initialize();
    }

    public SimpleConstraint binding(C_Var v1, C_Var v2) {
        return new OmegaConstraint_c(typeSystem, new C_BinaryTerm_c("==", v1, v2, typeSystem.Boolean()), this);
    }

    protected void buildTerm(Relation T, ConstraintHandle h, int coeff, C_Term t, Map<C_Var,Integer> varMap) throws SemanticException {
        if (t instanceof C_BinaryTerm) {
            C_BinaryTerm e = (C_BinaryTerm) t;
            if (e.op().equals("+")) {
                buildTerm(T, h, coeff, e.left(), varMap);
                buildTerm(T, h, coeff, e.right(), varMap);
                return;
            }
            if (e.op().equals("-")) {
                buildTerm(T, h, coeff, e.left(), varMap);
                buildTerm(T, h, -coeff, e.right(), varMap);
                return;
            }
            if (e.op().equals("*")) {
                if (e.left() instanceof C_Lit) {
                    C_Lit lit = (C_Lit) e.left();
                    Object val = lit.val();
                    if (val instanceof Integer) {
                        buildTerm(T, h, ((Integer) val) * coeff, e.right(), varMap);
                        return;
                    }
                }
                if (e.right() instanceof C_Lit) {
                    C_Lit lit = (C_Lit) e.right();
                    Object val = lit.val();
                    if (val instanceof Integer) {
                        buildTerm(T, h, ((Integer) val) * coeff, e.left(), varMap);
                        return;
                    }
                }
            }
        }
        if (t instanceof C_UnaryTerm) {
            C_UnaryTerm e = (C_UnaryTerm) t;
            if (e.op().equals("-")) {
                buildTerm(T, h, -coeff, e.arg(), varMap);
                return;
            }
        }
        if (t instanceof C_Lit) {
            C_Lit e = (C_Lit) t;
            Object val = e.val();
            if (val instanceof Integer) {
                h.updateConstant((Integer) val * coeff);
                return;
            }
        }
        if (t instanceof C_Var) {
            C_Var e = (C_Var) t;
            Integer varIndex = varMap.get(e);
            if (varIndex != null) {
                VarDecl x = T.setVar(varIndex);
                h.updateCoefficient(x,coeff);
                return;
            }
        }
        
        throw new SemanticException("Cannot build constraint for term |" + t + "|; must be a conjunction of affine inequalities.");
    }
    
    protected void buildConstraint(Relation T, FAnd and, C_Term t, Map<C_Var,Integer> varMap, boolean invert) throws SemanticException {
        if (t instanceof C_UnaryTerm) {
            C_UnaryTerm e = (C_UnaryTerm) t;
            String op = e.op();
            if (op.equals("!")) {
                buildConstraint(T, and, e.arg(), varMap, !invert);
                return;
            }
        }
        if (t instanceof C_BinaryTerm) {
            C_BinaryTerm e = (C_BinaryTerm) t;
            String op = e.op();
            
            // || is not handled properly
            if (op.equals("||"))
                throw new SemanticException("Cannot build constraint for term |" + t + "|; must be a conjunction of integer inequalities.");

            if (!invert ? op.equals("&&") : op.equals("||")) {
                C_Term l = e.left();
                C_Term r = e.right();

                buildConstraint(T, and, l, varMap, invert);
                buildConstraint(T, and, r, varMap, invert);
                return;
            }
            if (!invert ? op.equals("||") : op.equals("&&")) {
                C_Term l = e.left();
                C_Term r = e.right();
                
                FOr or = and.addOr();
                buildConstraint(T, or.addAnd(), l, varMap, invert);
                buildConstraint(T, or.addAnd(), r, varMap, invert);
                return;
            }
            if (!invert ? op.equals("!=") : op.equals("==")) {
                // x != y --> x > y || x < y

                FOr or = and.addOr();
                GEQHandle h1 = or.addAnd().addGEQ(true);
                GEQHandle h2 = or.addAnd().addGEQ(true);
                
                // x > y
                buildTerm(T, h1, 1, e.left(), varMap);
                buildTerm(T, h1, -1, e.right(), varMap);
                h1.updateConstant(-1);
                
                // x < y
                buildTerm(T, h2, -1, e.left(), varMap);
                buildTerm(T, h2, 1, e.right(), varMap);
                h2.updateConstant(-1);
                return;
            }
            if (!invert ? op.equals("==") : op.equals("!=")) {
                EQHandle h = and.addEQ(true);

                // x == y --> x - y == 0
                buildTerm(T, h, 1, e.left(), varMap);
                buildTerm(T, h, -1, e.right(), varMap);
//                {
//                    GEQHandle h = and.addGEQ(false);
//
//                    // x == y --> x - y == 0
//                    buildTerm(T, h, 1, e.left(), varMap);
//                    buildTerm(T, h, -1, e.right(), varMap);
//                }
//                {
//                    GEQHandle h = and.addGEQ(false);
//
//                    // x == y --> y - x == 0
//                    buildTerm(T, h, -1, e.left(), varMap);
//                    buildTerm(T, h, 1, e.right(), varMap);
//                }
                return;
            }
            if (!invert ? op.equals(">=") : op.equals("<")) {
                GEQHandle h = and.addGEQ(true);

                // x >= y --> x - y >= 0
                buildTerm(T, h, 1, e.left(), varMap);
                buildTerm(T, h, -1, e.right(), varMap);
                return;
            }
            if (!invert ? op.equals("<=") : op.equals(">")) {
                GEQHandle h = and.addGEQ(true);

                // x <= y --> y >= x --> y - x >= 0
                buildTerm(T, h, -1, e.left(), varMap);
                buildTerm(T, h, 1, e.right(), varMap);
                return;
            }
            if (!invert ? op.equals(">") : op.equals("<=")) {
                GEQHandle h = and.addGEQ(true);

                // x > y --> x - y > 0 --> x - y - 1 >= 0
                buildTerm(T, h, 1, e.left(), varMap);
                buildTerm(T, h, -1, e.right(), varMap);
                h.updateConstant(-1);
                return;
            }
            if (!invert ? op.equals("<") : op.equals(">=")) {
                GEQHandle h = and.addGEQ(true);

                // x < y --> y - x > 0 --> y - x - 1 >= 0
                buildTerm(T, h, -1, e.left(), varMap);
                buildTerm(T, h, 1, e.right(), varMap);
                h.updateConstant(-1);
                return;
            }
        }
        
        throw new SemanticException("Cannot build constraint for term |" + t + "|; must be a conjunction of integer inequalities.");
    }

    protected void collectVariables(C_Term t, Map<C_Var,Integer> varMap) {
        List<C_Var> vars = new ArrayList<C_Var>();
        t.collectVars(vars);
        
        int varIndex = 1;
        for (C_Var v : vars) {
            Integer i = varMap.get(v);
            if (i == null) {
                varMap.put(v, varIndex++);
            }
        }
    }
            
    protected Relation omegaRelation(C_Term t, Map<C_Var, Integer> varMap, boolean invert) throws SemanticException {
//        Relation T = new Relation(omegaLib, 0);
//        RelBody body = T.getRelBody();
        Relation T = new Relation(omegaLib, varMap.size());
        
        for (C_Var v : varMap.keySet()) {
            int i = varMap.get(v);
            String name = v.toString();
//            body.addSetVar(name);
//            varMap.put(v, body.ad)
            
            VarDecl x = T.setVar(i);
            x.setInstance(0);

            omegaLib.skipSetChecks++;
            T.nameSetVar(i, name);
            omegaLib.skipSetChecks--;
        }
        
        buildConstraint(T, T.addAnd(), t, varMap, invert);
//        T.simplify();

        return T;
    }
    
    public SimpleConstraint constraintForTerm(C_Term t) throws SemanticException {
        // Check that we can build a relation.
        Map<C_Var, Integer> varMap = new HashMap<C_Var, Integer>();
        collectVariables(t, varMap);
        omegaRelation(t, varMap, false);        
        
        SimpleConstraint oc = new OmegaConstraint_c(typeSystem, t, this);
        return oc;
    }

}
