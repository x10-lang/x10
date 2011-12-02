package x10.ast;

import java.util.List;

import polyglot.ast.Expr;
import polyglot.ast.Expr_c;
import polyglot.ast.If;
import polyglot.ast.If_c;
import polyglot.ast.Node;
import polyglot.ast.NodeList;
import polyglot.ast.Stmt;
import polyglot.ast.Term;
import polyglot.types.Context;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.CodeWriter;
import polyglot.util.CollectionUtil; import x10.util.CollectionFactory;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;
import polyglot.visit.ContextVisitor;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.PruningVisitor;
import x10.errors.Errors;
import x10.extension.X10Del;
import x10.extension.X10Ext;
import x10.types.ClosureDef;
import polyglot.types.Context;
import polyglot.types.TypeSystem;
import x10.types.checker.PlaceChecker;
import x10.types.constraints.XConstrainedTerm;
import x10.visit.X10TypeChecker;

public class FinishExpr_c extends Expr_c implements FinishExpr {

	Expr reducer;
	Stmt body;

	/**
	 * @param pos
	 */
	public FinishExpr_c(Position pos, Expr r, Stmt body) {
		super(pos);
		this.reducer = r;
		this.body = body;

	}

	public Expr reducer() {
		return reducer;
	}

	public Stmt body() {
		return body;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see polyglot.ast.Term_c#acceptCFG(polyglot.visit.CFGBuilder,
	 * java.util.List)
	 */
	@Override
	public <S> List<S> acceptCFG(CFGBuilder v, List<S> succs) {
		v.visitCFG(reducer, body, ENTRY);
		v.visitCFG(body, this, EXIT);
		return succs;
	}

	@Override
	public Node typeCheckOverride(Node parent, ContextVisitor tc) {

	    TypeSystem ts = (TypeSystem) tc.typeSystem();
	    NodeVisitor v = tc.enter(parent, this);

	    if (v instanceof PruningVisitor) {
	        return this;
	    }
	    // Check that reducer is a Reducer, throwing an exception if not.
	    Expr e = (Expr) visitChild(reducer, v);
	    Type r = Types.reducerType(e.type());
	    if (r == null) {
	        Errors.issue(tc.job(), new Errors.IsNotReducible(e, e.position()), this);
	        r = ts.unknownType(e.position());
	    }
        Node tmpNode = reconstruct(e,body).type(r);
	    Context childScope = tmpNode.enterChildScope(body, tc.context());
	    ContextVisitor childVisitor = tc.context(childScope);
	    Stmt b = (Stmt) tmpNode.visitChild(body, childVisitor);
	    Node n = reconstruct(e,b).type(r);
	    List<AnnotationNode> oldAnnotations = ((X10Ext) ext()).annotations();
	    if (oldAnnotations == null || oldAnnotations.isEmpty()) {
	        return n;
	    }
	    List<AnnotationNode> newAnnotations = node().visitList(oldAnnotations, v);
	    if (! CollectionUtil.allEqual(oldAnnotations, newAnnotations)) {
	        return ((X10Del) n.del()).annotations(newAnnotations);
	    }
	    return n;
	}

	/**
	 * When visiting the body set the collectingFinishType in the context to be
	 * the type of the reducer.
	 */
	@Override
	public Context enterChildScope(Node child, Context c) {
		Context oldC=c;
		Context xc = (Context) super.enterChildScope(child, c);
		if (child == body) {
		// Push T, not Reducible[T].
			Type type = reducer.type();
			type = Types.reducerType(type);
			if (type != null) {
				if (c==oldC)
					c=c.pushBlock();
				xc.setCollectingFinishScope(type);
			}
			addDecls(xc);
		}
		return xc;
	}

	/** Type check the statement. 
	public Node typeCheck(ContextVisitor tc) {
		// This must succeed, otherwise typeCheckOverride has already
		// thrown an exception. 
		Type reducerBase = X10TypeMixin.reducerType(reducer.type());
		assert reducerBase != null;
		return type(reducerBase);
	}
*/
	/*
	 * (non-Javadoc)
	 * 
	 * @see polyglot.ast.Term#firstChild()
	 */
	public Term firstChild() {
		return reducer;
	}

	/** Set the consequent of the statement. */
	public FinishExpr reconstruct(Expr e, Stmt b) {
		if (e == reducer && body == b)
			return this;

		FinishExpr_c n = (FinishExpr_c) copy();
		n.reducer = e;
		n.body = b;
		return n;
	}

	/** Visit the children of the statement. */
	public Node visitChildren(NodeVisitor v) {
		Expr reducer = (Expr) visitChild(this.reducer, v);
		Stmt body = (Stmt) visitChild(this.body, v);
		return reconstruct(reducer, body);
	}
	
	public String toString() {
		return "finish(" + reducer.toString() + ")" + body.toString();
	}
	
	public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
		w.write("finish (");
		printBlock(reducer, w, tr);
		w.write(")");
		printBlock(body, w, tr);
	}
}
