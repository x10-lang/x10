package org.eclipse.imp.x10dt.ui.editor;

import org.eclipse.imp.services.base.TreeModelBuilderBase;

import polyglot.ast.Call;
import polyglot.ast.ClassDecl;
import polyglot.ast.ClassMember;
import polyglot.ast.Expr;
import polyglot.ast.FieldDecl;
import polyglot.ast.Node;
import polyglot.ast.PackageNode;
import polyglot.ast.ProcedureDecl;
import polyglot.ast.TopLevelDecl;
import polyglot.ext.x10.ast.ArrayConstructor;
import polyglot.ext.x10.ast.ArrayConstructor_c;
import polyglot.ext.x10.ast.Async;
import polyglot.ext.x10.ast.AtEach;
import polyglot.ext.x10.ast.Atomic;
import polyglot.ext.x10.ast.Finish;
import polyglot.ext.x10.ast.ForEach;
import polyglot.ext.x10.ast.Future;
import polyglot.ext.x10.ast.Next;
import polyglot.ext.x10.ast.X10Loop;
import polyglot.util.Position;
import polyglot.visit.HaltingVisitor;
import polyglot.visit.NodeVisitor;

public class X10TreeModelBuilder extends TreeModelBuilderBase {
    @Override
    protected void visitTree(Object root) {
        Node node= (Node) root;
        if (root == null)
            return;
        node.visit(new HaltingVisitor() {
            public NodeVisitor enter(Node n) {
                if (n instanceof PackageNode) {
                    createSubItem(n);
                } else if (n instanceof TopLevelDecl) {
                    pushSubItem(n);
                } else if (n instanceof ClassDecl) {
                    pushSubItem(n);
                } else if (n instanceof ProcedureDecl) {
                    if (((ProcedureDecl) n).position() != Position.COMPILER_GENERATED) {
                        pushSubItem(n);
                    }
                } else if (n instanceof FieldDecl) {
                    createSubItem(n);
                } else if (n instanceof Async || n instanceof AtEach || n instanceof ForEach ||
                           n instanceof Future || n instanceof Finish || n instanceof Atomic ||
                           n instanceof Next) {
                    pushSubItem(n);
                } else if (n instanceof X10Loop) {
                    pushSubItem(n);
                } else if (n instanceof ArrayConstructor) {
                    ArrayConstructor cons = (ArrayConstructor) n;
                    if (cons.initializer() != null) {
                        pushSubItem(n);
                    }
                } else if (n instanceof Expr) {
                    return bypassChildren(n);
                } else if (n instanceof Call) {
                    Call call = (Call) n;
                    if (call.name().equals("force") && call.arguments().size() == 0) {
                        createSubItem(n);
                    }
                }
                return this;
            }
            @Override
            public Node leave(Node old, Node n, NodeVisitor v) {
                if (n instanceof TopLevelDecl ||
                    n instanceof ClassDecl ||
                    n instanceof ProcedureDecl ||
                    n instanceof Async || n instanceof AtEach || n instanceof ForEach ||
                    n instanceof Future || n instanceof Finish || n instanceof Atomic ||
                    n instanceof Next || n instanceof X10Loop ||
                    n instanceof ArrayConstructor)
                    popSubItem();
                return super.leave(old, n, v);
            }
        });
    }
}
