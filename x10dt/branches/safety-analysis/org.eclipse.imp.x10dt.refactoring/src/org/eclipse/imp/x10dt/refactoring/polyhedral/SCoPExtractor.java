package org.eclipse.imp.x10dt.refactoring.polyhedral;

import polyglot.ast.Expr;
import polyglot.ast.Formal;
import polyglot.ast.Stmt;
import x10.ast.ForLoop;

public class SCoPExtractor {
    public StaticControlPart extractSCoP(ForLoop fer) {
        Formal inductionVar= fer.formal();
        Expr domain= fer.domain();
        Stmt body= fer.body();
        return null;
    }
}
