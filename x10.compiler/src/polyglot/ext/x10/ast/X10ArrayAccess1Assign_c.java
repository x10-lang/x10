/*
 * Created by vj on Jan 11, 2005
 *
 * 
 */
package polyglot.ext.x10.ast;

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.Assign;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.Term;
import polyglot.ext.jl.ast.Assign_c;
import polyglot.ext.jl.ast.ArrayAccessAssign_c;
import polyglot.ast.ArrayAccess;
import polyglot.ast.Assign.Operator;
import polyglot.ext.jl.ast.ArrayAccess_c;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;
import polyglot.visit.TypeChecker;

/** An immutable representation of the X10 array assignment a[ index ] = e.
 * a[index] is represented by an X10ArrayAccess1.
 * @author vj Jan 11, 2005
 * 
 */
public class X10ArrayAccess1Assign_c extends Assign_c implements
		X10ArrayAccess1Assign {

	/**
	 * @param pos
	 * @param left
	 * @param op
	 * @param right
	 */
	public X10ArrayAccess1Assign_c(Position pos, X10ArrayAccess1 left, Operator op,
			Expr right) {
		super(pos, left, op, right);
	}
	/** Type check the expression. */
	public Node typeCheck(TypeChecker tc) throws SemanticException {
		X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
		if (left instanceof ArrayAccess_c) {
			ArrayAccessAssign_c n = new ArrayAccessAssign_c( position(), (ArrayAccess) left, op, right);
			return n.typeCheck( tc );
		}
		return super.typeCheck( tc );
	}
	

	  public Assign left(Expr left) {
	      X10ArrayAccess1Assign_c n = (X10ArrayAccess1Assign_c)super.left(left);
	      n.assertLeftType();
	      return n;
	  }
	  
	  private void assertLeftType() {
	      if (!(left() instanceof X10ArrayAccess1)) {
	          throw new InternalCompilerError("left expression of an X10ArrayAccess1Assign must be an X10 array access");
	      }
	  }
	  
	  public Term entry() {
	      return left().entry();
	  }
	  
	  protected void acceptCFGAssign(CFGBuilder v) {
	      X10ArrayAccess1 a = (X10ArrayAccess1)left();
	      
	      //    a[i] = e: visit a -> i -> e -> (a[i] = e)
	      v.visitCFG(a.array(), a.index().entry());
	      v.visitCFG(a.index(), right().entry());
	      v.visitCFG(right(), this);
	  }
	  protected void acceptCFGOpAssign(CFGBuilder v) {
	      X10ArrayAccess1 a = (X10ArrayAccess1)left();
	      
	      // a[i] OP= e: visit a -> i -> a[i] -> e -> (a[i] OP= e)
	      v.visitCFG(a.array(), a.index().entry());
	      v.visitCFG(a.index(), a);
	      v.visitThrow(a);
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
