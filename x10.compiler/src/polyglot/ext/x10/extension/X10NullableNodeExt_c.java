package polyglot.ext.x10.extension;

import polyglot.ext.x10.ast.NullableNode;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.frontend.ExtensionInfo;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.types.Type;

public class X10NullableNodeExt_c extends X10Ext_c {
	public Node rewrite(X10TypeSystem ts, NodeFactory nf, ExtensionInfo info) {
		NullableNode n = (NullableNode) node();
		Type type = n.base().type();

		if (type.isPrimitive()) {
			Type t = ts.boxedType(type.toPrimitive());
			return n.base(nf.CanonicalTypeNode(n.base().position(), t));
		}

		return n;
	}
}
