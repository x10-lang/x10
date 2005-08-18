package polyglot.ext.x10.ast;

import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.TypeNode;
import polyglot.ext.jl.ast.FieldDecl_c;
import polyglot.types.Flags;
import polyglot.types.SemanticException;
import polyglot.util.Position;
import polyglot.visit.TypeChecker;

public class X10FieldDecl_c extends FieldDecl_c {

        public X10FieldDecl_c(Position pos, Flags flags, TypeNode type,
                        String name, Expr init) {
            super(pos, flags, type, name, init);
        }

        public Node typeCheck(TypeChecker tc) throws SemanticException {
            Node result = super.typeCheck(tc);

            //
            // Any occurrence of a non-final static field in X10
            // should be reported as an error.
            // 
            if (flags().isStatic() && (!flags().isFinal())) {
                throw new SemanticException("Non-final static field is illegal in X10",
                                            this.position());
            }
            return result;
        }
}
