package polyglot.ext.x10.extension;

import polyglot.ast.Ext;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ext.x10.types.X10TypeSystem;

public interface X10Ext extends Ext {
    public Node rewrite(X10TypeSystem ts, NodeFactory nf);
}
