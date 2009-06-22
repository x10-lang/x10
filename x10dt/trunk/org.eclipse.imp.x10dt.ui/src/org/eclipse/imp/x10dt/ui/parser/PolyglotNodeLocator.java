/*
 * Created on Feb 9, 2006
 */
package x10.uide.parser;

import lpg.lpgjavaruntime.LexStream;

import org.eclipse.uide.parser.IASTNodeLocator;
import x10.parser.X10Parser.JPGPosition;		// SMS 14 Jun 2006

import polyglot.ast.Node;
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
	    if (pos.line() < 0) {
	    	System.out.println("PolyglotNodeLocator.NodeVisitor.enter(Node):  node positions < 0 for node type = " + n.getClass().getName());
	    	return this;
	    }
	    
	    //System.out.println("Selection extent: [" + pos.line() + ":" + pos.column() + " => [" + pos.endLine() + ":" + pos.endColumn() + "]");
	    int nodeStartOffset= fLS.getLineOffset(pos.line()-1) + pos.column();
	    int nodeEndOffset= fLS.getLineOffset(pos.endLine()-1) + pos.endColumn();
	    //System.out.println("Examining " + n.getClass().getName() + " node @ [" + nodeStartOffset + "->" + nodeEndOffset + ']');

	    if (nodeStartOffset <= fOffset && nodeEndOffset >= fEndOffset) {
		//System.out.println(" --> " + n.getClass().getName() + " node @ [" + nodeStartOffset + "->" + nodeEndOffset + "] selected.");
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
	    
	    int nodeStartOffset= fLS.getLineOffset(pos.line()-1) + pos.column();
	    int nodeEndOffset= fLS.getLineOffset(pos.endLine()-1) + pos.endColumn();
	    
	    if (nodeStartOffset == fOffset) System.out.println("NodeStartOffset = fOffset");
	    if (nodeEndOffset == fEndOffset) System.out.println("NodeEndOffset = fEndOffset");
	    
	    // SMS 14 Jun 2006
	    // Overriding seems to reject too many nodes (due to problem with end offset?)
	    // so I've commented out (at least temporarily) the condition that leads to
	    // node rejection (or not)
	    if (nodeStartOffset > fEndOffset || nodeEndOffset < fOffset)
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
    	if (!(node instanceof Node)) return -1;
    	Position nodePos = ((Node)node).position();
    	
    	String fileName = nodePos.file();
    	
    	
    	JPGPosition jpgPos = null;
    	if (nodePos instanceof JPGPosition) {
    		jpgPos = (JPGPosition) nodePos;
    		return jpgPos.getLeftIToken().getStartOffset();
    	} else {
    		// should probably do something more constructive,
    		// but defer that for now
    		System.err.println("PolyglotNodeLocator.getStartOffset:  Node position not a JPGPosition; returning -1");
    		return -1;
    	}
    }
    
    
    public int getEndOffset(Object node) {
    	if (!(node instanceof Node)) return -1;
    	Position nodePos = ((Node)node).position();
    	
    	JPGPosition jpgPos = null;
    	if (nodePos instanceof JPGPosition) {
    		jpgPos = (JPGPosition) nodePos;
        	return jpgPos.getRightIToken().getEndOffset();
    	} else {
    		// should probably do something more constructive,
    		// but defer that for now
    		System.err.println("PolyglotNodeLocator.getEndOffset:  Node position not a JPGPosition; returning -1");
    		return -1;
    	}
    }
    
    
    public int getLength(Object  node) {
    	Node n = (Node) node;
    	return getEndOffset(n) - getStartOffset(n);
    }
    
}