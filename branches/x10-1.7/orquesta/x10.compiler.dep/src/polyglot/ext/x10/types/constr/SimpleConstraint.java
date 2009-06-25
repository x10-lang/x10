package polyglot.ext.x10.types.constr;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import polyglot.ast.Binary;
import polyglot.ast.Unary;
import polyglot.ast.Binary.Operator;
import polyglot.types.TypeObject;

/** A simple constraint is an uninterpreted constraint term. */
public interface SimpleConstraint extends TypeObject {
    ConstraintSystem constraintSystem();
    
    void addIn(SimpleConstraint sc) throws Failure;
    
    boolean entailedBy(Constraint c, Constraint me);

    SimpleConstraint copy();
    
    Set<C_Var> vars();

    SimpleConstraint unaryOp(Unary.Operator op) throws Failure;
    SimpleConstraint binaryOp(Binary.Operator op, Constraint other, Constraint me) throws Failure;

    /** Substitute y for x in this conjunct.
     * 
     * @param y
     * @param x
     * @param propagate If true, substitute in the types of terms in the constraint.
     * @param container The containing constraint of this conjunct.
     * @param visited TODO
     * @return
     * @throws Failure
     */
    SimpleConstraint substitute(C_Var y, C_Root x, boolean propagate, Constraint container, HashSet<C_Term> visited) throws Failure;

    C_Var find(String varName);

    void internRecursively(C_Var var, Constraint container) throws Failure;

	boolean valid();

}
