package polyglot.ext.x10.extension;

import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.frontend.ExtensionInfo;
import polyglot.ext.jl.ast.Ext_c;
import polyglot.ext.x10.types.X10TypeSystem;

public class X10Ext_c extends Ext_c implements X10Ext {
    public Node rewrite(X10TypeSystem ts, NodeFactory nf, ExtensionInfo info) {
        return node();
    }
}
