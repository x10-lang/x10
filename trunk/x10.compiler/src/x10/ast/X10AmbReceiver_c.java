package x10.ast;

import polyglot.ast.AmbReceiver_c;
import polyglot.ast.Id;
import polyglot.ast.Node;
import polyglot.ast.Prefix;
import polyglot.types.SemanticException;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import x10.errors.Errors;
import x10.types.X10LocalInstance;
import x10.types.X10TypeSystem_c;
import x10.visit.X10TypeChecker;

public class X10AmbReceiver_c extends AmbReceiver_c {

    public X10AmbReceiver_c(Position pos, Prefix prefix, Id name) {
        super(pos, prefix, name);
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
            X10LocalInstance li = xts.createFakeLocal(name.id(), e);
            return ar.nodeFactory().Local(position(), name).localInstance(li).type(li.type());
        }
    }
}
