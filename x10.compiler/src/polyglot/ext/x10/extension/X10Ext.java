package polyglot.ext.x10.extension;

import polyglot.ast.Ext;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.frontend.ExtensionInfo;
import polyglot.ext.x10.types.X10TypeSystem;

public interface X10Ext extends Ext {
    /**
      * Comment adjacent to the node in the source code.
      * @return the comment
      */
    public String comment();
    
    /**
      * Clone the extension object and set its comment.
      * @param comment
      * @return a new extension object
      */
    public X10Ext comment(String comment);
    
    /**
      * Clone the extension object and the node and set its comment.
      * @param comment
      * @return a new Node
      */
    public Node setComment(String comment);
    
    public Node rewrite(X10TypeSystem ts, NodeFactory nf, ExtensionInfo info);
}
