package polyglot.ext.x10.extension;

import polyglot.ast.Instanceof;
import polyglot.ast.Node;
import polyglot.ext.jl.ast.JL_c;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.visit.TypeChecker;

public class X10InstanceofDel_c extends JL_c {
  public Node typeCheck(TypeChecker tc) throws SemanticException {
      Instanceof n = (Instanceof) node();
      Type rtype = n.compareType().type();
      Type ltype = n.expr().type();

      if (! tc.typeSystem().isCastValid(ltype, rtype)) {
          throw new SemanticException(
                    "Left operand of \"instanceof\" must be castable to "
                    + "the right operand.");
      }

      return n.type(tc.typeSystem().Boolean());
  }
}
