/**
 * 
 */
package org.eclipse.imp.x10dt.refactoring.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import polyglot.ast.Node;
import polyglot.visit.NodeVisitor;

public class NodePathComputer {
    private static class PathSavingVisitor extends NodeVisitor {
        private final Node fTarget;
        private final List<Node> fPath = new ArrayList<Node>();
        private boolean fHitTarget = false;

        public PathSavingVisitor(Node target) {
            fTarget = target;
        }

        @Override
        public Node leave(Node old, Node n, NodeVisitor v) {
            if (old == fTarget)
                fHitTarget = true;
            if (fHitTarget)
                fPath.add(old);
            return old;
        }

        @Override
        public Node override(Node n) {
            if (fHitTarget)
                return n;
            return null;
        }

        public List<Node> getPath() {
            return fPath;
        }
    }

    private final NodePathComputer.PathSavingVisitor fVisitor;
    private List<Node> fPath;

    public NodePathComputer(Node root, Node target) {
        fVisitor = new PathSavingVisitor(target);
        root.visit(fVisitor);
        fPath = new ArrayList<Node>();
        fPath.addAll(fVisitor.getPath());
        Collections.reverse(fPath);
    }

    public List<Node> getPath() {
        return fPath;
    }

    /**
     * Finds the innermost Node of the given type that encloses the given Node
     * @param loop
     * @param path
     */
    public <M> M findEnclosingNode(Node node, Class<M> clazz) {
        int i= fPath.size()-1;
        for(; i >= 0; i--) {
            Node pathNode = fPath.get(i);
            if (pathNode == node) {
                break;
            }
        }
        for(i--; i >= 0; i--) {
            Node pathNode = fPath.get(i);
            if (clazz.isInstance(pathNode)) {
                return (M) pathNode;
            }
        }
        return null;
    }

    public Node getParent(Node node) {
        return findEnclosingNode(node, Node.class);
    }
}
