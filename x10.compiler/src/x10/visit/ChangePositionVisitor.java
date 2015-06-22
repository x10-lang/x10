/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

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