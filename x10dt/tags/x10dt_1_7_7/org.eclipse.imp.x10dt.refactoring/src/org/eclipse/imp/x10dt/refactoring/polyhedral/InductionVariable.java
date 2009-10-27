package org.eclipse.imp.x10dt.refactoring.polyhedral;

import polyglot.ast.Expr;

public interface InductionVariable {
    public String getName();
    public Expr getMin();
    public Expr getMax();
    public Expr getStride();
}
