/**
 * 
 */
package x10.refactorings.utils;

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
			if (n == fTarget)
				fHitTarget = true;
			if (fHitTarget)
				fPath.add(n);
			return n;
		}

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
}