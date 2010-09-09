package polyglot.ext.x10.extension;

import polyglot.ast.Instanceof;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.types.Type;

public class X10InstanceofExt_c extends X10Ext_c {
  public Node rewrite(X10TypeSystem ts, NodeFactory nf) {
      Instanceof n = (Instanceof) node();
      Type rtype = n.compareType().type();

      if (rtype.isPrimitive()) {
          Type t = ts.boxedType(rtype.toPrimitive());
          return n.compareType(nf.CanonicalTypeNode(n.compareType().position(),
                                                    t));
      }

      return n;
  }
}
