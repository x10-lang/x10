package polyglot.ext.x10.types.constr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import polyglot.ast.Binary;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.Lit;
import polyglot.ast.Local;
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
import polyglot.ext.x10.types.constr.omega.omegaLib.GEQHandle;
import polyglot.ext.x10.types.constr.omega.omegaLib.Relation;
import polyglot.ext.x10.types.constr.omega.omegaLib.VarDecl;
import polyglot.types.LocalInstance;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;

public class CvcConstraintSystem implements ConstraintSystem {

    X10TypeSystem typeSystem;
    
    public CvcConstraintSystem(X10TypeSystem xts) {
        this.typeSystem = xts;
    }

    public SimpleConstraint binding(C_Var v1, C_Var v2) {
        return new CvcConstraint_c(typeSystem, new C_BinaryTerm_c("==", v1, v2, typeSystem.Boolean()), this);
    }

    protected void collectVariables(C_Term t, Map<C_Var,Integer> varMap, Constraint c) {
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
            
    public SimpleConstraint constraintForTerm(C_Term t) throws SemanticException {
        // Check that we can build a relation.
        SimpleConstraint oc = new CvcConstraint_c(typeSystem, t, this);
        
        System.out.println("---TESTING " + oc);

        Constraint c = new Constraint_c(typeSystem);
        c.addIn(oc);
        
        // Check that the constraint entails itself--this ensures cvc can compile it.
        if (! oc.entailedBy(c, c)) {
            throw new SemanticException("Cannot translate expression |" + t + "| into a constraint.");
        }
        
        System.out.println("---DONE TESTING " + oc);
        
        return oc;
    }
    
    public String toString() {
        return "CVC";
    }

}
