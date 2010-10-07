package x10.ast;

/**
 * Represents an AST node with a guard.
 * 
 * @author igor
 */
public interface Guarded {
    DepParameterExpr guard();
    Guarded guard(DepParameterExpr guard);
}
