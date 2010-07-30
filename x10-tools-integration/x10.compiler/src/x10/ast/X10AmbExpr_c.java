package x10.ast;

import polyglot.ast.AmbExpr_c;
import polyglot.ast.Id;
import polyglot.ast.Node;
import polyglot.types.SemanticException;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import x10.errors.Errors;
import x10.types.X10LocalInstance;
import x10.types.X10TypeSystem_c;
import x10.visit.X10TypeChecker;

public class X10AmbExpr_c extends AmbExpr_c {
    public X10AmbExpr_c(Position pos, Id name) {
        super(pos, name);
    }

    @Override
    public Node disambiguate(ContextVisitor ar) throws SemanticException {
        try {
            return super.disambiguate(ar);
        } catch (SemanticException e) {
            X10TypeChecker xtc = X10TypeChecker.getTypeChecker(ar);
            if (xtc.throwExceptions())
                throw e;
            Errors.issue(ar.job(), e, this);
            X10TypeSystem_c xts = (X10TypeSystem_c) ar.typeSystem();
            X10LocalInstance li = xts.createFakeLocal(name.id());
            return ar.nodeFactory().Local(position(), name).localInstance(li).type(li.type());
        }
    }
}
