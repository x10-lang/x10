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

public class X10AmbExpr_c extends AmbExpr_c {
    public X10AmbExpr_c(Position pos, Id name) {
        super(pos, name);
    }

    @Override
    public Node disambiguate(ContextVisitor ar) {
        try {
            return super.disambiguate(ar);
        } catch (SemanticException e) {
            Errors.issue(ar.job(), e, this);
            TypeSystem xts =  ar.typeSystem();
            X10LocalInstance li = xts.createFakeLocal(name.id(), e);
            return ar.nodeFactory().Local(position(), name).localInstance(li).type(li.type());
        }
    }
}
