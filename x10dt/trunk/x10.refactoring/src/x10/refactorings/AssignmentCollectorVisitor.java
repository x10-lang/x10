/**
 * 
 */
package x10.refactorings;

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.Assign;
import polyglot.ast.Node;
import polyglot.visit.NodeVisitor;

public class AssignmentCollectorVisitor extends NodeVisitor {
    private List<Assign> fResult;

    public AssignmentCollectorVisitor() {
    	fResult = new ArrayList<Assign>();
    }
    @Override
    public Node leave(Node old, Node n, NodeVisitor v) {
	if (n instanceof Assign)
	    fResult.add((Assign)n);
	return n;
    }
    public List<Assign> getResult() {
	return fResult;
    }
}