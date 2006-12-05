package polyglot.ext.x10.extension;

import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.frontend.ExtensionInfo;
import polyglot.ast.Ext_c;
import polyglot.ext.x10.types.X10TypeSystem;

public class X10Ext_c extends Ext_c implements X10Ext {
    String comment;
    
    public String comment() {
        return this.comment;
    }
    
    public X10Ext comment(String comment) {
        X10Ext_c n = (X10Ext_c) copy();
        n.comment = comment;
        return n;
    }
    
    public Node setComment(String comment) {
        Node n = this.node();
        return n.ext(this.comment(comment));
    }
    
    public Node rewrite(X10TypeSystem ts, NodeFactory nf, ExtensionInfo info) {
        return node();
    }
}
