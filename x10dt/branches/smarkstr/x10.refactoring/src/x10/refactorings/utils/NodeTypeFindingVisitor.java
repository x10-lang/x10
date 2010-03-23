/**
 * 
 */
package x10.refactorings.utils;

import polyglot.ast.Node;
import polyglot.visit.NodeVisitor;

public class NodeTypeFindingVisitor extends NodeVisitor {
    private final Class fNodeType;
    private Node fResult;

    public NodeTypeFindingVisitor(Class targetType) {
	fNodeType= targetType;
    }
    @Override
    public Node leave(Node old, Node n, NodeVisitor v) {
	if (fResult == null && fNodeType.isInstance(n))
	    fResult= n;
	return n;
    }
    public Node getResult() {
	return fResult;
    }
}