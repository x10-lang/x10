/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.extension;

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.New;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ext.x10.ast.Future;
import polyglot.ext.x10.types.X10PrimitiveType;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.frontend.ExtensionInfo;
import polyglot.types.ConstructorInstance;
import polyglot.types.Type;

public class X10FutureExt_c extends X10Ext_c {
    // Rewrite == and != to invoke Primitive.equals(o, p).
    public Node rewrite(X10TypeSystem ts, NodeFactory nf, ExtensionInfo info) {
        Future b = (Future) node();
        Expr body_e = b.body();
        Type body_t = body_e.type();
        
        if (body_t.isPrimitive()) {
            // BOX, i.e., cast that body to the corresponding object type
            ConstructorInstance ci = ts.wrapper((X10PrimitiveType) body_t.toPrimitive());
            List args = new ArrayList(1);
            args.add(body_e);
            New x = nf.New(b.position(),
                           nf.CanonicalTypeNode(b.position(), ci.container()),
                           args);
            x = (New) x.type(ci.container());
            // set the body of the future to be the boxed argument.
            return b.body(x.constructorInstance(ci));
        }

        return super.rewrite(ts, nf, info);
    }
}
