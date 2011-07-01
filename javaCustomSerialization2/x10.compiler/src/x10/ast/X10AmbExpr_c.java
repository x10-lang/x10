package x10.ast;

import polyglot.ast.AmbExpr_c;
import polyglot.ast.Id;
import polyglot.ast.Node;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import x10.errors.Errors;
import x10.types.X10LocalInstance;
import x10.visit.X10TypeChecker;

// TODO : Delete me
public class X10AmbExpr_c extends AmbExpr_c {
    public X10AmbExpr_c(Position pos, Id name) {
        super(pos, name);
    }
}
