/**
 * 
 */
package polyglot.ext.x10.visit;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import polyglot.ast.Eval;
import polyglot.ast.Local;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Stmt;
import polyglot.ast.TypeNode;
import polyglot.ext.x10.ast.DepParameterExpr;
import polyglot.ext.x10.ast.X10NodeFactory;
import polyglot.ext.x10.ast.X10NodeOps;
import polyglot.ext.x10.ast.X10TypeNode;
import polyglot.ext.x10.ast.X10TypeNode_c;
import polyglot.ext.x10.types.X10Context;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.ext.x10.types.constr.TypeTranslator;
import polyglot.ext.x10.visit.ExprFlattener.Flattener;
import polyglot.frontend.Job;
import polyglot.frontend.MissingDependencyException;
import polyglot.frontend.Scheduler;
import polyglot.frontend.goals.Goal;
import polyglot.main.Report;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.util.ErrorInfo;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeChecker;


/**
 * A pass that elaborates deptypes into type objects. Depclauses in typenodes -- at var decls, method decls
 * and field decls -- are assimilated into the type, after a check that they form boolean expressions. THis
 * check only involves variables in the context of the deptype, and does not involve resolving method invocations.
 * 
 * Subsequent to this pass, full typechecking may be performed with the full information 
 * associated with the type now available to form method instances etc
 *
 * @author vj
 *
 */
public class TypeElaborator extends TypeChecker {
	
	public TypeElaborator(Job job, TypeSystem ts, NodeFactory nf) {
		super(job, ts, nf);
		
	}
	public Node leaveCall(Node parent, Node old, Node n, NodeVisitor v) throws SemanticException {
		
		if ((! (n instanceof X10TypeNode ))) {
			
			return n;
		}
		TypeChecker tc = (TypeChecker) new TypeChecker(job, ts, nf).context(context());
		X10TypeNode me = (X10TypeNode) n;
		X10TypeNode xt = (X10TypeNode) me.visit(tc);
		Node result = X10TypeNode_c.typeCheckDepClause( xt,tc);
		//Report.report(1, "TypeElaborator |" + n + "| ==> |" + result + "|");
		return result;
		
	}

	
}
