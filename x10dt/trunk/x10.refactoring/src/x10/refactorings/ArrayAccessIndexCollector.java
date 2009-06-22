/**
 * 
 */
package x10.refactorings;

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.ArrayAccess;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ext.x10.ast.X10ArrayAccess1;
import polyglot.visit.NodeVisitor;

public class ArrayAccessIndexCollector extends NodeVisitor {
    private List<Expr> fResult;

    public ArrayAccessIndexCollector() {
    	fResult = new ArrayList<Expr>();
    }
    
	@Override
    public Node leave(Node old, Node n, NodeVisitor v) {
		if (n instanceof X10ArrayAccess1)
			fResult.add(((X10ArrayAccess1)n).index());
		else if (n instanceof ArrayAccess)
			fResult.add(((ArrayAccess)n).index());
		return n;
	}
	
	public List<Expr> getResult() {
		return fResult;
	}
    
}