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
import polyglot.ext.x10.types.NullableType;
import polyglot.ext.x10.types.X10ArrayType;
import polyglot.ext.x10.types.X10NamedType;
import polyglot.ext.x10.types.X10PrimitiveType;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.frontend.ExtensionInfo;
import polyglot.types.ArrayType;
import polyglot.types.Type;
import polyglot.types.Types;

public class X10CanonicalTypeNodeExt_c extends X10Ext_c {
	public X10CanonicalTypeNodeExt_c() {
		super();
	}
	private X10NamedType boxType(X10NamedType t, X10TypeSystem ts) {
		if (t.isArray()) {
			X10ArrayType at = (X10ArrayType) t.toArray();
			X10NamedType n = boxType((X10NamedType) at.base(), ts);
			return (X10NamedType) at.base(Types.ref(n));
		}
        NullableType nt = t.toNullable();
        if (nt !=null) {
			return nt.base(Types.ref((X10NamedType) boxType(nt.base(), ts)));
		} 
        if (t.isPrimitive()) {
			return ts.boxedType((X10PrimitiveType) t.toPrimitive());
		}
		return t;
	}
	private Type boxNullable(X10Type t, X10TypeSystem ts) {
		if (t.isArray()) {
			ArrayType at = t.toArray();
			return at.base(Types.ref(boxNullable((X10Type) at.base(), ts)));
		} 
        NullableType nt = t.toNullable();
        if (nt !=null) {
			return nt.base(Types.ref((X10NamedType) boxType(nt.base(), ts)));
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
