package polyglot.ext.x10.extension;

import polyglot.ast.CanonicalTypeNode;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ext.x10.types.NullableType;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.X10Type_c;
import polyglot.frontend.ExtensionInfo;
import polyglot.types.ArrayType;
import polyglot.types.Type;

public class X10CanonicalTypeNodeExt_c extends X10Ext_c {
	private Type boxType(X10Type t, X10TypeSystem ts) {
		if (t.isArray()) {
			ArrayType at = t.toArray();
			return at.base(boxType((X10Type) at.base(), ts));
		}
        NullableType nt = t.toNullable();
        if (nt !=null) {
			return nt.base((X10Type) boxType(nt.base(), ts));
		} 
        if (t.isPrimitive()) {
			return ts.boxedType(t.toPrimitive());
		}
		return t;
	}
	private Type boxNullable(X10Type t, X10TypeSystem ts) {
		if (t.isArray()) {
			ArrayType at = t.toArray();
			return at.base(boxNullable((X10Type) at.base(), ts));
		} 
        NullableType nt = t.toNullable();
        if (nt !=null) {
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
