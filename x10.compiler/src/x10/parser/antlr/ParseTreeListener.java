package x10.parser.antlr;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

import polyglot.ast.Node;
import x10.parserGen.X10BaseListener;
import x10.parserGen.X10Listener;

public class ParseTreeListener extends X10BaseListener implements X10Listener {
    final public ParseTreeProperty<Node> values = new ParseTreeProperty<Node>();

    public Node get(ParseTree tree) {
        return values.get(tree);
    }
}
