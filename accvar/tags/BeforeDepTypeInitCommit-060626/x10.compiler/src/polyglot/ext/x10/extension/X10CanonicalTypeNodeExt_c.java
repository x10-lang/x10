package polyglot.ext.x10.extension;

import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ext.x10.types.NullableType;
import polyglot.ext.x10.types.X10ReferenceType;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.types.ArrayType;
import polyglot.types.Type;
import polyglot.frontend.ExtensionInfo;

public class X10CanonicalTypeNodeExt_c extends X10Ext_c {
	private Type boxType(X10Type t, X10TypeSystem ts) {
		if (t.isArray()) {
			ArrayType at = t.toArray();
			return at.base(boxType((X10Type) at.base(), ts));
		} else if (t.isNullable()) {
			NullableType nt = t.toNullable();
			return nt.base((X10Type) boxType(nt.base(), ts));
		} else if (t.isPrimitive()) {
			return ts.boxedType(t.toPrimitive());
		}
		return t;
	}
	private Type boxNullable(X10Type t, X10TypeSystem ts) {
		if (t.isArray()) {
			ArrayType at = t.toArray();
			return at.base(boxNullable((X10Type) at.base(), ts));
		} else if (t.isNullable()) {
			NullableType nt = t.toNullable();
			return nt.base((X10Type) boxType(nt.base(), ts));
		}
		return t;
	}
	public Node rewrite(X10TypeSystem ts, NodeFactory nf, ExtensionInfo info) {
		CanonicalTypeNode n = (CanonicalTypeNode) node();
		X10Type type = (X10Type) n.type();
		Type t = boxNullable(type, ts);
		if (t != type)
			return nf.CanonicalTypeNode(n.position(), t);
		return n;
	}
}
