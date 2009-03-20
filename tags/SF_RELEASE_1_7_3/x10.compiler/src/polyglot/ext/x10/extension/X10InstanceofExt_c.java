/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.extension;

import polyglot.ast.Instanceof;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ext.x10.types.X10PrimitiveType;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.frontend.ExtensionInfo;
import polyglot.types.Type;

public class X10InstanceofExt_c extends X10Ext_c {
  public Node rewrite(X10TypeSystem ts, NodeFactory nf, ExtensionInfo info) {
      Instanceof n = (Instanceof) node();
      Type rtype = n.compareType().type();
      
      // deal with instanceof targeting primitive type such as '3 instanceof int'
      if (rtype.isPrimitive()) {
          // rewrites the compareType to the primitive boxed one.
    	  Type t = ts.boxedType((X10PrimitiveType) rtype.toPrimitive());
          Node res =  n.compareType(nf.CanonicalTypeNode(n.compareType().position(), t));
          return res;
      }

      return n;
  }
}
