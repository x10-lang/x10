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
import polyglot.ast.Expr;
import polyglot.ast.Term;
import polyglot.ast.Assign.Operator;
import polyglot.ext.jl.ast.Assign_c;
import polyglot.types.TypeSystem;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;

/** An immutable representation of an X10 array access update: a[point] op= expr;
 * TODO
 * Typechecking rules:
 * (1) point must be of a type (region) that can be cast to the array index type.
 * (2) expr must be of a type that can be implicitly cast to the base type of the array.
 * (3) The operator, if any, must be permitted on the underlying type.
 * (4) No operator is allowed if the array is a value array.
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
