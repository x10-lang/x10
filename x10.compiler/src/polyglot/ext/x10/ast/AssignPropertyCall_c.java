/**
 * 
 */
package polyglot.ext.x10.ast;

import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.Receiver;
import polyglot.ast.Term;
import polyglot.ast.Call_c;
import polyglot.ast.Stmt_c;
import polyglot.ext.x10.types.X10ConstructorInstance;
import polyglot.ext.x10.types.X10Context;
import polyglot.types.Context;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeChecker;

/**
 * @author vj
 *
 */
public class AssignPropertyCall_c extends Stmt_c implements AssignPropertyCall {

	List arguments;
	/**
	 * @param pos
	 * @param target
	 * @param name
	 * @param arguments
	 */
	public AssignPropertyCall_c(Position pos, List arguments) {
		super(pos);
		this.arguments = arguments;
		
	}
	  public Term entry() {
	        return listEntry(arguments, this);
	    }

	/* (non-Javadoc)
	 * @see polyglot.ast.Term#acceptCFG(polyglot.visit.CFGBuilder, java.util.List)
	 */
	  public List acceptCFG(CFGBuilder v, List succs) {
	        v.visitCFGList(arguments, this);
	        return succs;
	    }

	/** Return a copy of this node with this.expr equal to the given expr.
	 * @see polyglot.ext.x10.ast.Await#expr(polyglot.ast.Expr)
	 */
	public AssignPropertyCall args( List args ) {
		if (args == arguments) return this;
			AssignPropertyCall_c n = (AssignPropertyCall_c) copy();
			n.arguments = args;
			return n;
	}
	
	public List args() {
	    return arguments;
	}
	
	X10ConstructorInstance thisConstructor = null;
	public Node typeCheck(TypeChecker tc) throws SemanticException {
		TypeSystem ts = tc.typeSystem();
		Context ctx = tc.context();
		if (! (ctx.inCode()) || ! (ctx.currentCode() instanceof X10ConstructorInstance))
			throw new SemanticException("The statement property(...) must occur only in the body of a constructor ",
					position());
		thisConstructor = (X10ConstructorInstance) ctx.currentCode();
		// Now check that the types of each actual argument are subtypes of the corresponding
		// property for the class reachable through the constructor.
		
		// stub for now.
		return super.typeCheck(tc);
	}


	/** Visit the children of the statement. */
	  /** Visit the children of the block. */
	    public Node visitChildren(NodeVisitor v) {
	        List args = visitList(this.arguments, v);
		return args(args);
	    }
}


