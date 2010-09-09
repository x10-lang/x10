package polyglot.ext.x10.extension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import polyglot.ast.Call;
import polyglot.ast.Cast;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.types.ConstructorInstance;
import polyglot.types.MethodInstance;
import polyglot.types.Type;

public class X10CastExt_c extends X10Ext_c {
	// Insert boxing and unboxing code.
	public Node rewrite(X10TypeSystem ts, NodeFactory nf) {

		Cast c = (Cast) node();
		Type rtype = c.expr().type();
		Type ltype = c.castType().type();

		if (!ts.equals(c.type(), ltype))
			c = (Cast) c.type(ltype);

		if (ltype.isPrimitive() && rtype.isReference()) {
			// Unbox
			MethodInstance mi = ts.getter(ltype.toPrimitive());

			Cast x = nf.Cast(c.position(),
							 nf.CanonicalTypeNode(c.position(), mi.container()),
							 c.expr());
			x = (Cast) x.type(mi.container());

			Call y = nf.Call(c.position(), x, mi.name(),
							 Collections.EMPTY_LIST);
			y = (Call) y.type(mi.returnType());

			return y.methodInstance(mi);
		}
		else if (ltype.isReference() && rtype.isPrimitive()) {
			// Box
			ConstructorInstance ci = ts.wrapper(rtype.toPrimitive());

			List args = new ArrayList(1);
			args.add(c.expr());

			New x = nf.New(c.position(),
						   nf.CanonicalTypeNode(c.position(), ci.container()),
						   args);
			x = (New) x.type(ci.container());
			return x.constructorInstance(ci);
		}

		return c;
	}
}

