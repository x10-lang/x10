package polyglot.ext.x10.ast;

import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.TypeNode;
import polyglot.ext.jl.ast.Cast_c;
import polyglot.main.Report;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.Position;
import polyglot.visit.TypeChecker;

public class X10Cast_c extends Cast_c {

        public X10Cast_c(Position pos, TypeNode castType, Expr expr) {
                super(pos, castType, expr);
        }

        public Node typeCheck(TypeChecker tc) throws SemanticException {
            Type type = castType.type();
           if (Report.should_report("debug", 5)) {
                    Report.report(5, "[Cast_c] |" + this + "|.typeCheck(...):");
                    Report.report(5, "[Cast_c] ...type=|" +  type+"|.");
           }
           Expr result = type(castType.type());
           if (Report.should_report("debug", 5)) {
           	Report.report(5, "[Cast_c] ...returning=|" +  result+"| of type=|" + result.type() + "|.");
           }
           return super.typeCheck(tc);
        }
}
