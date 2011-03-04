/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created by vj on Dec 9, 2004
 *
 * 
 */
package polyglot.ext.x10.ast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import polyglot.ast.Assign;
import polyglot.ast.Assign_c;
import polyglot.ast.CanonicalTypeNode_c;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.Term;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10Type;
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
	
	public Term firstChild() {
		return left();
	}
	
	protected void acceptCFGAssign(CFGBuilder v) {
		X10ArrayAccess a = (X10ArrayAccess)left();
		v.visitCFG(a, right(), ENTRY);
		v.visitCFG(right(), this, EXIT);
	}
	protected void acceptCFGOpAssign(CFGBuilder v) {
		X10ArrayAccess a = (X10ArrayAccess)left();
		v.visitCFG(a, right(), ENTRY);
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
    public String toString() {
        return left + "." +   opString(op) +"(" + right + ")";
    }
    
    /** Write the expression to an output file. */
  public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
	  assert false;
      Expr array = ((X10ArrayAccess) left).array();
      List indices = ((X10ArrayAccess) left).index();
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
        for(Iterator i = indices.iterator(); i.hasNext();) {
            Expr e = (Expr) i.next();
            print(e, w, tr);
            
            if (i.hasNext()) {
                w.write(",");
                w.allowBreak(0, " ");
            }
        }
        w.write(")");
  }
}
