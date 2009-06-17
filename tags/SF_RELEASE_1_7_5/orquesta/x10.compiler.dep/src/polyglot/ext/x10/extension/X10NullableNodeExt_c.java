/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.extension;

import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ext.x10.ast.NullableNode;
import polyglot.ext.x10.types.NullableType;
import polyglot.ext.x10.types.X10NamedType;
import polyglot.ext.x10.types.X10PrimitiveType;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.frontend.ExtensionInfo;
import polyglot.types.Ref;
import polyglot.types.Type;

public class X10NullableNodeExt_c extends X10Ext_c {
	public Node rewrite(X10TypeSystem ts, NodeFactory nf, ExtensionInfo info) {
	    assert false : "NullableNode should have been rewritten to CanonicalTypeNode";
		NullableNode n = (NullableNode) node();
		Type type = n.base().type();

		if (type.isPrimitive()) {
		    Type t = ts.boxedType((X10PrimitiveType) type.toPrimitive());
		    CanonicalTypeNode typenode = nf.CanonicalTypeNode(n.base().position(), t); 
		    n = n.base(typenode);
		}

		return nf.CanonicalTypeNode(n.position(), ts.createNullableType(node().position(), (Ref<? extends X10NamedType>) n.base().typeRef()));			
	}
}
