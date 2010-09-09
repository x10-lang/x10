/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created by vj on Jan 11, 2005
 *
 * 
 */
package polyglot.ext.x10.ast;

import java.util.ArrayList;
import java.util.List;

import polyglot.ast.ArrayAccess;
import polyglot.ast.ArrayAccessAssign_c;
import polyglot.ast.ArrayAccess_c;
import polyglot.ast.Assign;
import polyglot.ast.Assign_c;
import polyglot.ast.CanonicalTypeNode_c;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.Term;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.CodeWriter;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.CFGBuilder;
import polyglot.visit.PrettyPrinter;
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
	/** Type check the expression. */
	public Node typeCheck(TypeChecker tc) throws SemanticException {
		X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
		// Used to have an X10ArrayAccess1 to the left, but it has now
		// resolved into an ArrayAccess. So this node must resolve into an ArrayAccessAssign.
		if (left instanceof ArrayAccess_c) {
			ArrayAccessAssign_c n = new ArrayAccessAssign_c( position(), (ArrayAccess) left, op, right);
			return n.del().typeCheck( tc );
		}
		 Type t = left.type();
		 Type s = right.type();
		
		if (op == ASSIGN) {
		      if (! ts.isImplicitCastValid(s, t) &&
		          ! ts.typeEquals(s, t) &&
		          ! ts.numericConversionValid(t, right.constantValue())) {

		        throw new SemanticException("Cannot assign " + s + " to " + t + ".",
		                                    position());
		      }
              return type(s);
		      
		    }

		    if (op == ADD_ASSIGN) {
		      // t += s
		      if (ts.typeEquals(t, ts.String()) && ts.canCoerceToString(s, tc.context())) {
		        throw new SemanticException(" Arrays of strings are not supported yet.");
		      }

		      if (t.isNumeric() && s.isNumeric()) {
                  return type(s);
		      }

		      throw new SemanticException("The " + op + " operator must have "
		                                  + "numeric or String operands.",
		                                  position());
		    }

		    if (op == SUB_ASSIGN || op == MUL_ASSIGN ||
		        op == DIV_ASSIGN || op == MOD_ASSIGN) {
		      if (t.isNumeric() && s.isNumeric()) {
                  return type(s);
		      }

		      throw new SemanticException("The " + op + " operator must have "
		                                  + "numeric operands.",
		                                  position());
		    }

		    if (op == BIT_AND_ASSIGN || op == BIT_OR_ASSIGN || op == BIT_XOR_ASSIGN) {
		      if (t.isBoolean() && s.isBoolean()) {
                  return type(s);
		      }

		      if (ts.isImplicitCastValid(t, ts.Long()) &&
		          ts.isImplicitCastValid(s, ts.Long())) {
                  return type(s);
		      }

		      throw new SemanticException("The " + op + " operator must have "
		                                  + "integral or boolean operands.",
		                                  position());
		    }

		    if (op == SHL_ASSIGN || op == SHR_ASSIGN || op == USHR_ASSIGN) {
		      if (ts.isImplicitCastValid(t, ts.Long()) &&
		          ts.isImplicitCastValid(s, ts.Long())) {
		        // Only promote the left of a shift.
                  return type(s);
		      }

		      throw new SemanticException("The " + op + " operator must have "
		                                  + "integral operands.",
		                                  position());
		    }

		    throw new InternalCompilerError("Unrecognized assignment operator " +
		                                    op + ".");
		
		
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
	  
	  public Term firstChild() {
	      X10ArrayAccess1 a = (X10ArrayAccess1)left();
              return a.array();
	  }
	  
	  protected void acceptCFGAssign(CFGBuilder v) {
	      X10ArrayAccess1 a = (X10ArrayAccess1)left();
	      
	      //    a[i] = e: visit a -> i -> e -> (a[i] = e)
	      v.visitCFG(a.array(), a.index(), ENTRY);
	      v.visitCFG(a.index(), right(), ENTRY);
	      v.visitCFG(right(), this, EXIT);
	  }
	  protected void acceptCFGOpAssign(CFGBuilder v) {
	      X10ArrayAccess1 a = (X10ArrayAccess1)left();
	      
	      // a[i] OP= e: visit a -> i -> a[i] -> e -> (a[i] OP= e)
	      v.visitCFG(a.array(), a.index(), ENTRY);
	      v.visitCFG(a.index(), a, EXIT);
	      v.visitThrow(a);
	      v.edge(a, right(), ENTRY);
	      v.visitCFG(right(), this, EXIT);
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
	  /**
	   * This may be called before typeChecking, so before
	   * its been figured out that this is really an ArrayAccess1Assign in disguise.
	   */
      public String toString() {
    	  if (left instanceof ArrayAccess) {
    		  ArrayAccess l = (ArrayAccess) left;
    		  Expr array = l.array();
              Expr index = l.index();
              return array + "[" + index + "]= " + right ;
    	  }
          Expr array = ((X10ArrayAccess1) left).array();
          Expr index = ((X10ArrayAccess1) left).index();
          return array + ".set(" + right + "," + index + ")";
      }
      
      /** Write the expression to an output file. */
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
		assert false;
        Expr array = ((X10ArrayAccess1) left).array();
        Expr index = ((X10ArrayAccess1) left).index();
          X10Type pt = ( X10Type) type;
          if (X10TypeMixin.isParametric(pt)) {
              Type result = (Type) pt.typeParameters().get(0);
              w.write("(");
              print(new CanonicalTypeNode_c(Position.COMPILER_GENERATED,Types.ref(result)), w, tr);
              w.write(")");
          }
          printSubExpr(array, w, tr);
          w.write ("." + opString(op) + "(");
          printSubExpr(right, w, tr);
          w.write(",");
          printSubExpr(index, w, tr);
          w.write(")");
              
    }
}
