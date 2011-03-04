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
import polyglot.types.Type;

public class X10NullableNodeExt_c extends X10Ext_c {
	public Node rewrite(X10TypeSystem ts, NodeFactory nf, ExtensionInfo info) {
		NullableNode n = (NullableNode) node();
		Type type = n.base().type();

		if (type.isPrimitive()) {
			Type t = ts.boxedType((X10PrimitiveType) type.toPrimitive());
			CanonicalTypeNode typenode = nf.CanonicalTypeNode(n.base().position(), t); 
			n =  n.base(typenode);
			NullableType newNullableType = ts.createNullableType(node().position(), (X10NamedType) typenode.type());
			n = (NullableNode) n.type(newNullableType);
			return n;
		} else {
			// updating nullable node expression type
			n = (NullableNode) n.type(ts.createNullableType(node().position(), (X10NamedType) type));			
		}

		return n;
	}
}
