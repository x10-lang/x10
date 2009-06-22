/*
 * Created on Feb 9, 2006
 */
package x10.uide.editor;

import lpg.lpgjavaruntime.LexStream;

import org.eclipse.uide.parser.IASTNodeLocator;

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
	    Position pos= n.position();
	    int nodeStartOffset= fLS.getLineOffset(pos.line()) + pos.column();
	    int nodeEndOffset= fLS.getLineOffset(pos.endLine()) + pos.endColumn();
	    System.out.println("Examining " + n.getClass().getName() + " node @ [" + nodeStartOffset + "->" + nodeEndOffset + ']');

	    if (nodeStartOffset <= fOffset && nodeEndOffset >= fEndOffset) {
		System.out.println(" ==> node selected.");
		fNode[0]= n;
	    }
	    return super.enter(n);
	}
    };

    public PolyglotNodeLocator(LexStream ls) {
	fLS= ls;
    }

    public Object findNode(Object ast, int offset) {
	return findNode(ast, offset, offset);
    }

    public Object findNode(Object ast, int startOffset, int endOffset) {
	System.out.println("Looking for node spanning offsets " + startOffset + " -> " + endOffset);
	fOffset= startOffset;
	fEndOffset= endOffset;
	((Node) ast).visit(fVisitor);
	return fNode[0];
    }
}