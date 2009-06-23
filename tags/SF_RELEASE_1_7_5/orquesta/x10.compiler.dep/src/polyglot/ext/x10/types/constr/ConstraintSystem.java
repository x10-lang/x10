package polyglot.ext.x10.types.constr;

import polyglot.ast.Expr;
import polyglot.types.SemanticException;

/**
 * Translate from a ConstExr to a constraint term that can be serialized.
 * @author vj
 *
 */
public interface ConstraintSystem {
    SimpleConstraint binding(C_Var v1, C_Var v2);
    SimpleConstraint constraintForTerm(C_Term term) throws SemanticException;
}
