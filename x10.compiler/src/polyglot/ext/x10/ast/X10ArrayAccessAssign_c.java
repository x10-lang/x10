/*
 * Created by vj on Dec 9, 2004
 *
 * 
 */
package polyglot.ext.x10.ast;

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.ArrayAccess;
import polyglot.ast.Assign;
import polyglot.ast.Call;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.Term;
import polyglot.ast.Assign.Operator;
import polyglot.ext.jl.ast.ArrayAccessAssign_c;
import polyglot.ext.jl.ast.ArrayAccess_c;
import polyglot.ext.jl.ast.Assign_c;
import polyglot.ext.jl.ast.Call_c;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.util.TypedList;
import polyglot.visit.CFGBuilder;
import polyglot.visit.NodeVisitor;
import polyglot.visit.TypeChecker;

/** An immutable representation of an X10 array access update: a[point] op= expr;
 * TODO
 * Typechecking rules:
 * (1) point must be of a type (region) that can be cast to the array index type.
 * (2) expr must be of a type that can be implicitly cast to the base type of the array.
 * (3) The operator, if any, must be permitted on the underlying type.
 * (4) No assignment is allowed on a value array.
 * @author vj Dec 9, 2004
 * 
 */
public class X10ArrayAccessAssign_c extends Assign_c 
implements X10ArrayAccessAssign {
	
	/**
	 * @param pos
	 * @param left
	 * @param op
	 * @param right
	 */
	public X10ArrayAccessAssign_c(Position pos, X10ArrayAccess left, Operator op,
			Expr right) {
		super(pos, left, op, right);
		
	}
	public Assign left(Expr left) {
		X10ArrayAccessAssign_c n = (X10ArrayAccessAssign_c)super.left(left);
		n.assertLeftType();
		return n;
	}
	
	private void assertLeftType() {
		if (!(left() instanceof X10ArrayAccess)) {
			throw new InternalCompilerError("left expression of an X10ArrayAccessAssign must be an array access");
		}
	}
	
	public String opString(Operator op) {
		if (op == ASSIGN ) return "set";
		if (op == ADD_ASSIGN) return "addSet";
		if (op == SUB_ASSIGN) return "subSet";
		if (op == MUL_ASSIGN) return "mulSet";
		if (op == DIV_ASSIGN) return "divSet";
		if (op == MOD_ASSIGN) return "modSet";
		if (op == BIT_AND_ASSIGN) return "bitAndSet";
		if (op == BIT_OR_ASSIGN) return "bitOrSet";
		if (op == BIT_XOR_ASSIGN) return "bitXorSet";
		if (op == SHL_ASSIGN) return "shlSet";
		if (op == SHR_ASSIGN) return "shrSet";
		if (op == USHR_ASSIGN) return "ushrSet";
		throw new InternalCompilerError("Unknown assignment operator");
	}
	
	public Node typeCheck(TypeChecker tc) throws SemanticException {
		TypeSystem ts = tc.typeSystem();
		 Type t = left.type();
		 Type s = right.type();
//		 Now left must be an X10ArrayAccess which has now resolved into a Call_c.
			// Use the information in the call to construct the real set call.
			Call call = (Call) left;
			Expr receiver = (Expr) call.target();
			List args = TypedList.copyAndCheck(call.arguments(), Expr.class, false);
			args.add( 0, right);
			
		if (op == ASSIGN) {
		      if (! ts.isImplicitCastValid(s, t) &&
		          ! ts.equals(s, t) &&
		          ! ts.numericConversionValid(t, right.constantValue())) {

		        throw new SemanticException("Cannot assign " + s + " to " + t + ".",
		                                    position());
		      }
		      return new Call_c(position(), receiver, "set", args).del().typeCheck(tc);
		    }

		    if (op == ADD_ASSIGN) {
		      // t += s
		      if (ts.equals(t, ts.String()) && ts.canCoerceToString(s, tc.context())) {
		        throw new SemanticException(" Arrays of strings are not supported yet.");
		      }

		      if (t.isNumeric() && s.isNumeric()) {
		      	 return new Call_c(position(), receiver, "addSet", args).del().typeCheck(tc);
		      }

		      throw new SemanticException("The " + op + " operator must have "
		                                  + "numeric or String operands.",
		                                  position());
		    }

		    if (op == SUB_ASSIGN || op == MUL_ASSIGN ||
		        op == DIV_ASSIGN || op == MOD_ASSIGN) {
		      if (t.isNumeric() && s.isNumeric()) {
		      	 return new Call_c(position(), receiver, opString(op), args).del().typeCheck(tc);
		      }

		      throw new SemanticException("The " + op + " operator must have "
		                                  + "numeric operands.",
		                                  position());
		    }

		    if (op == BIT_AND_ASSIGN || op == BIT_OR_ASSIGN || op == BIT_XOR_ASSIGN) {
		      if (t.isBoolean() && s.isBoolean()) {
		      	return new Call_c(position(), receiver, opString(op), args).del().typeCheck(tc);
		      }

		      if (ts.isImplicitCastValid(t, ts.Long()) &&
		          ts.isImplicitCastValid(s, ts.Long())) {
		      	return new Call_c(position(), receiver, opString(op), args).del().typeCheck(tc);
		      }

		      throw new SemanticException("The " + op + " operator must have "
		                                  + "integral or boolean operands.",
		                                  position());
		    }

		    if (op == SHL_ASSIGN || op == SHR_ASSIGN || op == USHR_ASSIGN) {
		      if (ts.isImplicitCastValid(t, ts.Long()) &&
		          ts.isImplicitCastValid(s, ts.Long())) {
		        // Only promote the left of a shift.
		      	return new Call_c(position(), receiver, opString(op), args).del().typeCheck(tc);
		      }

		      throw new SemanticException("The " + op + " operator must have "
		                                  + "integral operands.",
		                                  position());
		    }

		    throw new InternalCompilerError("Unrecognized assignment operator " +
		                                    op + ".");
		
	}
	
	public Term entry() {
		return left().entry();
	}
	
	protected void acceptCFGAssign(CFGBuilder v) {
		X10ArrayAccess a = (X10ArrayAccess)left();
		
		//    a[i1,...,ik] = e: visit a -> i1 -> i2 ->... -> ik -> e -> (a[i] = e)
		//v.visitCFG(a.array(), a.index().entry());
		//v.visitCFG(a.index(), right().entry());
		//v.visitCFG(right(), this);
		v.visitCFG(a, right().entry());
		v.visitCFG(right(), this);
	}
	protected void acceptCFGOpAssign(CFGBuilder v) {
		ArrayAccess a = (ArrayAccess)left();
		
		// a[i] OP= e: visit a -> i -> a[i] -> e -> (a[i] OP= e)
		// v.visitCFG(a.array(), a.index().entry());
		// v.visitCFG(a.index(), a);
		// v.visitThrow(a);
		v.edge(a, right().entry());
		v.visitCFG(right(), this);
	}
	
	public List throwTypes(TypeSystem ts) {
		List l = new ArrayList(super.throwTypes(ts));
		
		if (throwsArrayStoreException()) {
			l.add(ts.ArrayStoreException());
		}
		
		l.add(ts.NullPointerException());
		l.add(ts.OutOfBoundsException());
		
		return l;
	}
	
	/** Get the throwsArrayStoreException of the expression. */
	public boolean throwsArrayStoreException() {
		return op == ASSIGN && left.type().isReference();
	}
	
	
}
