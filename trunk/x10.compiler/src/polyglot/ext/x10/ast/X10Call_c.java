package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.Node;
import polyglot.ast.Receiver;
import polyglot.ext.jl.ast.Call_c;
import polyglot.types.SemanticException;
import polyglot.util.Position;
import polyglot.visit.TypeChecker;

/**
 * A method call wrapper to rewrite getLocation() calls on primitives.
 * @author Igor
 */
public class X10Call_c extends Call_c {
    public X10Call_c(Position pos, Receiver target, String name,
                     List arguments) {
        super(pos, target, name, arguments);
    }

    /**
     * Rewrite getLocation() to Here for value types, otherwise leave alone.
     */
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        if (this.target != null && this.target.type().isPrimitive() &&
                name().equals("getLocation") && arguments().isEmpty())
        {
            X10NodeFactory xnf = (X10NodeFactory) tc.nodeFactory();
            return xnf.Here(position()).typeCheck(tc);
        }

        return super.typeCheck(tc);
    }
}

