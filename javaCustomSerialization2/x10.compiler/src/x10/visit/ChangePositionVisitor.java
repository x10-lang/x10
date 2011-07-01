package x10.visit;

import polyglot.ast.FlagsNode_c;
import polyglot.ast.Node;
import polyglot.ast.LocalDecl;
import polyglot.ast.FieldDecl;
import polyglot.util.Position;
import polyglot.util.ErrorInfo;
import polyglot.visit.NodeVisitor;
import polyglot.frontend.Job;
import polyglot.main.Report;
import x10.ast.AnnotationNode_c;
import x10.ast.X10CanonicalTypeNode_c;
import x10.ast.DepParameterExpr_c;
import x10.ast.X10Formal_c;
import x10.ast.ClosureCall_c;

public class ChangePositionVisitor extends NodeVisitor
{
    private Position newPos;
    public ChangePositionVisitor(Position newPos) {
        this.newPos = newPos;
    }

    @Override
    public Node leave(Node old, Node n, NodeVisitor v) {
        return n.position(newPos);
    }
}