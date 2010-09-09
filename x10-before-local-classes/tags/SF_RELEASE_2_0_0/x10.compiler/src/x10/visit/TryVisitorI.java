package x10.visit;

import polyglot.ast.Node;
import polyglot.types.SemanticException;
import polyglot.util.InternalCompilerError;
import polyglot.visit.NodeVisitor;

/**
 * The interface implemented by a try Visitor. A try Visitor is the same as a NodeVisitor, except
 * that it has a try* chain corresponding to the usual visit chain. This chain lets SemanticExceptions
 * through to the caller. 
 * 
 * This is useful in case an AST A is being tried out as a candidate for another AST B, under the assumption
 * that if no SemanticExceptins are thrown, then A is a valid substitute for B.
 * @author vj
 *
 */
public interface TryVisitorI {

	public Node tryOverride(Node parent, Node n) throws SemanticException;
	public Node tryVisitEdge(Node parent, Node child) throws SemanticException;
	public Node tryVisitEdgeNoOverride(Node parent, Node child) throws SemanticException;
	public Node tryLeave(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException;

}
