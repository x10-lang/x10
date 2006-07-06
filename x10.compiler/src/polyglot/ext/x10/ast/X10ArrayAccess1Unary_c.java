/*
 * Created by igor on Dec 19, 2005
 */
package polyglot.ext.x10.ast;

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.ArrayAccess;
import polyglot.ast.Call;
import polyglot.ast.Cast;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.Precedence;
import polyglot.ast.Term;
import polyglot.ast.Unary;
import polyglot.ext.jl.ast.ArrayAccess_c;
import polyglot.ext.jl.ast.Call_c;
import polyglot.ext.jl.ast.Unary_c;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.TypeChecker;

/**
 * An immutable representation of an X10 array access pre/post-
 * increment/decrement: a[index]++, etc.
 * a[index] is represented by an X10ArrayAccess1.
 *
 * @author igor Dec 19, 2005
 */
public class X10ArrayAccess1Unary_c extends Unary_c
	implements X10ArrayAccess1Unary
{

	/**
	 * @param pos
	 * @param op
	 * @param expr
	 */
	public X10ArrayAccess1Unary_c(Position pos, Operator op, X10ArrayAccess1 expr) {
		super(pos, op, expr);
	}

    /** Get the precedence of the expression. */
    public Precedence precedence() {
		return Precedence.LITERAL;
    }

	public Unary expr(Expr expr) {
		X10ArrayAccess1Unary_c n = (X10ArrayAccess1Unary_c)super.expr(expr);
		n.assertExprType();
		return n;
	}
	
	private void assertExprType() {
		if (!(expr() instanceof X10ArrayAccess1)) {
			throw new InternalCompilerError("expression of an X10ArrayAccess1Unary must be an X10 array access");
		}
	}
	
	public String opString(Operator op) {
		if (op == PRE_INC) return "preInc";
		if (op == POST_INC) return "postInc";
		if (op == PRE_DEC) return "preDec";
		if (op == POST_DEC) return "postDec";
		throw new InternalCompilerError("Unknown unary operator");
	}

	/** Type check the expression. */
	public Node typeCheck(TypeChecker tc) throws SemanticException {
		if (!expr.type().isNumeric()) {
			throw new SemanticException("Operand of " + op +
					" operator must be numeric.", expr.position());
		}
		/* TODO: we rewrite array accesses to calls way too early, so
		 * this check is not possible here.
		System.err.println("this = "+getClass()+"; Expr = "+expr.getClass());
		if (! (expr instanceof Variable)) {
			throw new SemanticException("Operand of " + op +
					" operator must be a variable.", expr.position());
		}
		 */
		/* TODO: we rewrite array accesses to calls way too early, so
		 * this check is not possible here.  Need to find some wayt to
		 * check for a value type!
		if (((Variable) expr).flags().isFinal()) {
			throw new SemanticException("Operand of " + op +
					" operator must be a non-final variable.",
					expr.position());
		}
		 */
		/** The ugly code below is borrowed from X10ArrayAccess1Assign_c */
		X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
		// Used to have an X10ArrayAccess1 as the expression, but it has now
		// resolved into an ArrayAccess. So this node must resolve into a Unary.
		// [IP] I don't think this will ever happen, but such is the nature of
		// cut-and-paste...
		if (expr instanceof ArrayAccess_c) {
			Unary_c n = new Unary_c( position(), op, (ArrayAccess) expr);
			return n.del().typeCheck( tc );
		}
		// Now it must be an X10ArrayAccess1 which has now resolved into a Call_c.
		// Use the information in the call to construct the real update call.
		Expr expr = (this.expr instanceof Cast) ? ((Cast)this.expr).expr() : this.expr;                        
		Call call = (Call) expr;
		Expr receiver = (Expr) call.target();
		List args = call.arguments();
		return new Call_c(position(), receiver, opString(op), args).del().typeCheck(tc);
	}
	
	public Term entry() {
		return expr().entry();
	}
	
	public List throwTypes(TypeSystem ts) {
		List l = new ArrayList(super.throwTypes(ts));
		l.add(ts.NullPointerException());
		return l;
	}
}
