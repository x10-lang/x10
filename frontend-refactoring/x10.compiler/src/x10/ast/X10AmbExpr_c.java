package x10.ast;

import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import x10.errors.Errors;
import x10.types.SemanticException;
import x10.types.X10LocalInstance;
import x10.types.TypeSystem_c;
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
            TypeSystem_c xts = (TypeSystem_c) ar.typeSystem();
            X10LocalInstance li = xts.createFakeLocal(name.id(), e);
            return ar.nodeFactory().Local(position(), name).localInstance(li).type(li.type());
        }
    }
}
