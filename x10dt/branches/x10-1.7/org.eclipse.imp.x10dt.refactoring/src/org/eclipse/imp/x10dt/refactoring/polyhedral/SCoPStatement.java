package org.eclipse.imp.x10dt.refactoring.polyhedral;

import polyglot.ast.Stmt;

public interface SCoPStatement {
    IterationDomain getDomain();
    Stmt getStatement();
}
