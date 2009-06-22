/*
 * Created on Feb 9, 2006
 */
package x10.uide.parser;

import lpg.lpgjavaruntime.*;

import org.eclipse.uide.parser.IASTNodeLocator;
import x10.parser.X10Parser.JPGPosition;		// SMS 14 Jun 2006

import polyglot.ast.Node;
import polyglot.types.Declaration;
import polyglot.util.Position;
import polyglot.visit.NodeVisitor;

public class PolyglotNodeLocator implements IASTNodeLocator {
    private final Node[] fNode= new Node[1];

    private int fOffset;

    private int fEndOffset;
    
    private final LexStream fLS;

    private NodeVisitor fVisitor= new NodeVisitor() {
	public NodeVisitor enter(Node n) {
	    //System.out.println("Entering node type = " + n.getClass().getName());
            // N.B.: Polyglot's notion of line # is 1 off from that of Eclipse's.
	    Position pos= n.position();
	    
	    // SMS 15 Jun 2006:
	    if (pos == null || pos.line() < 0) {
	    	System.out.println("PolyglotNodeLocator.NodeVisitor.enter(Node):  node positions < 0 for node type = " + n.getClass().getName());
	    	return this;
	    }
	    
//	    System.out.println("Node extent: " + pos.offset() + " => " + pos.endOffset() + " [" + pos.line() + ":" + pos.column() + " => [" + pos.endLine() + ":" + pos.endColumn() + "]");
	    int nodeStartOffset= pos.offset();
	    int nodeEndOffset= pos.endOffset();
	    //System.out.println("Examining " + n.getClass().getName() + " node @ [" + nodeStartOffset + "->" + nodeEndOffset + ']');

	    if (nodeStartOffset <= fOffset && nodeEndOffset >= fEndOffset) {	
//    		System.out.println(" --> " + n.getClass().getName() + " node @ [" + nodeStartOffset + "->" + nodeEndOffset + "] selected.");
    		fNode[0]= n;
	    }
	    return this;
	}
	
	// Note:  Returning null is interpreted as a signal to *not* override
	// the given node
	public Node override(Node n) {
	    // Prune traversal to avoid examining nodes outside the given text range.
            // N.B.: Polyglot's notion of line # is 1 off from that of Eclipse's.
	    Position pos= n.position();
	    
	    // SMS 15 Jun 2006
	    if (pos.line() < 0) {
	    	//System.out.println("PolyglotNodeLocator.NodeVisitor.override(Node):  node positions < 0 for node type = " + n.getClass().getName());
	    	return null;
	    }
	    
	    int nodeStartOffset= pos.offset();
	    int nodeEndOffset= pos.endOffset();
	    
//	    if (nodeStartOffset == fOffset) System.out.println("NodeStartOffset = fOffset");
//	    if (nodeEndOffset == fEndOffset) System.out.println("NodeEndOffset = fEndOffset");
	    
	    // SMS 31 Jul 2006
	    // There's a problem with trying to filter nodes using a condition like
	    // the one originally here, now commented-out below.  That is, it seems that
	    // the children of a node are not necessarily contained within the offest
	    // range of the node.  In particular, it seems that a class body has some
	    // children that are method declarations, a method declaration has a lexical
	    // extent that (more or less) corresponds to the specification, not to the
	    // specification including the body.  But the method-body node is a child of
	    // the method-declaration node in the AST, even though the offset range of
	    // the body follows the offset range of the declaration.  If the method-
	    // declaration node is returned here (as it would be with the original
	    // condition), then the nodes representing constructs within the method body
	    // will not be examined.  This can leave the class-body node as the closest
	    // enclosing node to a location within a method body.
	    //
	    // As an alternative condition that will not entail the traversal of all
	    // AST nodes, I've substituted a condition to check just whether the current
	    // node starts after the given location.  I assume that in that case the
	    // children of the node cannot contain the given location.  (That is, I
	    // assume that the children of a node will not have offsets that will
	    // precede the offset of the node.)  If that doesn't work in general, then
	    // perhaps no filtering should be done here.
	    //
	    //if (nodeStartOffset > fEndOffset || nodeEndOffset < fOffset)
	    if (nodeStartOffset > fOffset)
	    	return n;
	    return null;
	}
    };

    public PolyglotNodeLocator(LexStream ls) {
	fLS= ls;
    }

    public Object findNode(Object ast, int offset) {
	return findNode(ast, offset, offset);
    }

    public Object findNode(Object ast, int startOffset, int endOffset) {
//	System.out.println("Looking for node spanning offsets " + startOffset + " => " + endOffset);
	fOffset= startOffset;
	fEndOffset= endOffset;
	((Node) ast).visit(fVisitor);	// assigns to fNode[0], if a suitable node is found
	// SMS 14 Jun 2006:  Elaborated on println:
	if (fNode[0] == null) {
		//System.out.println("Selected node is null");
	} else {
		//System.out.println("Selected node (type): " + fNode[0].getClass().getName());
	}
	return fNode[0];
    }

    // SMS 14 Jun 2006
    // Added to address change in IASTNodeLocator
    
    public int getStartOffset(Object node) {
	Position pos;

	if (node instanceof Declaration)
	    pos= ((Declaration) node).position();
	else if (node instanceof Node)
	    pos= ((Node)node).position();
	else
	    return -1;

	return pos.offset();
//	if (pos instanceof JPGPosition) {
//	    JPGPosition jpgPos = (JPGPosition) pos;
//	    return jpgPos.getLeftIToken().getStartOffset();
//    	} else {
//    	    // should probably do something more constructive, but defer that for now
//    	    System.err.println("PolyglotNodeLocator.getStartOffset:  Node position not a JPGPosition; returning -1");
//    	    return -1;
//    	}
    }

    public int getEndOffset(Object node) {
	Position pos;

	if (node instanceof Declaration)
	    pos= ((Declaration) node).position();
	else if (node instanceof Node)
	    pos= ((Node)node).position();
	else
	    return -1;

	return pos.endOffset();
//    	if (pos instanceof JPGPosition) {
//    	    JPGPosition jpgPos = (JPGPosition) pos;
//    	    return jpgPos.getRightIToken().getEndOffset();
//    	} else {
//    	    // should probably do something more constructive, but defer that for now
//    	    System.err.println("PolyglotNodeLocator.getEndOffset:  Node position not a JPGPosition; returning -1");
//    	    return -1;
//    	}
    }

    public int getLength(Object  node) {
    	Node n = (Node) node;
    	return getEndOffset(n) - getStartOffset(n);
    }
}
