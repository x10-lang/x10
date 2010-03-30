package x10.refactorings.utils;

import java.util.Collection;
import java.util.List;

import polyglot.ast.Assign;
import polyglot.ast.NamedVariable;
import polyglot.ast.Node;
import polyglot.ast.Stmt;
import polyglot.ast.Variable;
import polyglot.types.VarInstance;
import polyglot.visit.NodeVisitor;

public abstract class PolyglotUtils {

	public static Node findNodeOfType(List<Node> path, Class clazz) {
		for (Node node : path) {
			if (clazz.isInstance(node))
				return node;
		}
		return null;
	}

	public static Node findInnermostNodeOfType(List<Node> path, Class clazz) {
		Node result = null;
		for (Node node : path) {
			if (clazz.isInstance(node))
				result = node;
		}
		return result;
	}

	public static Node findInnermostNodeOfTypes(List<Node> path, Class[] classes) {
		Node result = null;
		for (Node node : path) {
			for (Class clazz : classes) {
				if (clazz.isInstance(node))
					result = node;
			}
		}
		return result;
	}

	/**
	 * Determines whether the given statement updates one of the variables in
	 * the given collection.
	 * 
	 * @param s
	 *            the statement to test
	 * @param vars
	 *            the set of variables that might updated
	 * @return true, if the statement updates one of the variables
	 */
	public static boolean updatesVarFromCollection(Stmt s,
			final Collection<Variable> vars) {
		NodeVisitor nv = new NodeVisitor() {
			boolean updates;

			public NodeVisitor enter(Node par, Node n) {
				if (n instanceof Assign) {
					Assign a = (Assign) n;
					for (Variable v : vars) {
						VarInstance vi, ali;
						if (v instanceof NamedVariable) {
							vi = ((NamedVariable) v).varInstance();
						} else {
							vi = ExtractAsyncStaticTools.extractArrayName(v)
									.varInstance();
						}

						if (a.left() instanceof NamedVariable) {
							ali = ((NamedVariable) a.left()).varInstance();
						} else {
							ali = ExtractAsyncStaticTools.extractArrayName(
									(Variable) a.left()).varInstance();
						}
						if ((vi == null && ali == null)
								|| (vi != null && vi.equals(ali)))
							updates = true;
					}
				}
				return super.enter(n);
			}

			public String toString() {
				return "" + updates;
			}
		};
		s.visit(nv);
		return Boolean.parseBoolean(nv.toString());
	}

}
