package polyglot.ext.x10.ast;

import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.TypeNode;
import polyglot.ext.jl.ast.Cast_c;
import polyglot.main.Report;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.visit.TypeChecker;

public class X10Cast_c extends Cast_c {

        public X10Cast_c(Position pos, TypeNode castType, Expr expr) {
                super(pos, castType, expr);
        }

        public Node typeCheck(TypeChecker tc) throws SemanticException {
            TypeSystem ts = tc.typeSystem();

            if (! ts.isCastValid(expr.type(), castType.type())) {
                throw new SemanticException("Cannot cast the expression of type \"" 
                                            + expr.type() + "\" to type \"" 
                                            + castType.type() + "\".",
                                            position());
            }
            Type type = castType.type();
           if (Report.should_report("debug", 5)) {
                    Report.report(5, "[Cast_c] |" + this + "|.typeCheck(...):");
                    Report.report(5, "[Cast_c] ...type=|" +  type+"|.");
           }
           return super.typeCheck(tc);
        }
}
