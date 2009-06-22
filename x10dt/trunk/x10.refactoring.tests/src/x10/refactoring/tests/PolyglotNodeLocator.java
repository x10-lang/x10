package x10.refactoring.tests;

import polyglot.ast.Node;
import polyglot.util.Position;
import polyglot.visit.NodeVisitor;

public class PolyglotNodeLocator {
    private final Node fNode[] = new Node[1];
    private int fOffset;

    private int fEndOffset;

    private boolean DEBUG = false;

    private NodeVisitor fVisitor = new NodeVisitor() {
        public NodeVisitor enter(Node n) {
            if (DEBUG)
                System.out.println("Entering node type = " + n.getClass().getName() + " @ position " + n.position());
            // N.B.: Polyglot's notion of line # is 1 off from that of Eclipse's.
            Position pos = n.position();

            if (pos == null || pos.line() < 0) {
                if (DEBUG)
                    System.out.println("NodeVisitor.enter(): node positions < 0 for node type = "
                                    + n.getClass().getName());
                return this;
            }

            if (DEBUG)
                System.out.println("Node extent: " + pos.offset() + " => " + pos.endOffset() + " [" + pos.line() + ":"
                        + pos.column() + " => [" + pos.endLine() + ":" + pos.endColumn() + "]");
            int nodeStartOffset = pos.offset();
            int nodeEndOffset = pos.endOffset();
            // System.out.println("Examining " + n.getClass().getName() + " node @ [" + nodeStartOffset + "->" +
            // nodeEndOffset + ']');

            if (nodeStartOffset <= fOffset && nodeEndOffset >= fEndOffset) {
                if (DEBUG)
                    System.out.println(" --> " + n + " (" + n.getClass().getName() + ") node @ [" + nodeStartOffset
                            + "->" + nodeEndOffset + "] selected.");
                fNode[0] = n;
            }
            return this;
        }

        // Note: Returning null is interpreted as a signal to *not* override
        // the given node
        public Node override(Node n) {
            // Prune traversal to avoid examining nodes outside the given text range.
            // N.B.: Polyglot's notion of line # is 1 off from that of Eclipse's.

            if (true)
                return null; // RMF 7/3/2007 - Disabled short-circuiting b/c it seemed to
            // break node location (probably the result of "rogue offsets" on various node types).

            Position pos = n.position();

            if (pos == null || pos.line() < 0) {
                // System.out.println("PolyglotNodeLocator.NodeVisitor.override(Node):  node positions < 0 for node type = "
                // + n.getClass().getName());
                return null;
            }

            int nodeStartOffset = pos.offset();
            int nodeEndOffset = pos.endOffset();

            // if (nodeStartOffset == fOffset) System.out.println("NodeStartOffset = fOffset");
            // if (nodeEndOffset == fEndOffset) System.out.println("NodeEndOffset = fEndOffset");

            // if (nodeStartOffset > fEndOffset || nodeEndOffset < fOffset)
            if (nodeStartOffset > fOffset)
                return n;
            return null;
        }
    };

    public Node findNode(Object ast, int offset) {
        return findNode(ast, offset, offset);
    }

    public Node findNode(Object ast, int startOffset, int endOffset) {
        if (DEBUG) {
            System.out.println("Looking for node spanning offsets " + startOffset + " => " + endOffset);
        }
        if (ast == null)
            return null;
//        if (DEBUG) {
//            IPrsStream ps = fLS.getPrsStream();
//            if (endOffset == startOffset)
//                System.out.println("Token at this offset: '" + ps.getTokenAtCharacter(startOffset) + "'");
//            else {
//                System.out.println("Token span: '" + ps.getTokenAtCharacter(startOffset) + "' to '"
//                        + ps.getTokenAtCharacter(endOffset) + "'");
//            }
//        }
        fOffset = startOffset;
        fEndOffset = endOffset;
        ((Node) ast).visit(fVisitor); // assigns to fNode[0], if a suitable node is found

        if (fNode[0] != null) {
            if (DEBUG)
                System.out.println("Selected node (type): " + fNode[0] + " (" + fNode[0].getClass().getName() + ")");
        }
        return fNode[0];
    }
}
