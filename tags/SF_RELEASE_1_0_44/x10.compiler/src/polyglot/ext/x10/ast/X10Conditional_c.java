/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/**
 * 
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.Conditional_c;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.visit.TypeChecker;

/**
 * @author VijaySaraswat
 *
 */
public class X10Conditional_c extends Conditional_c {

	/**
	 * @param pos
	 * @param cond
	 * @param consequent
	 * @param alternative
	 */
	public X10Conditional_c(Position pos, Expr cond, Expr consequent,
			Expr alternative) {
		super(pos, cond, consequent, alternative);
		
	}
	  public Node typeCheck(TypeChecker tc) throws SemanticException {
	        X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
	        
	        Expr e1 = consequent;
	        Expr e2 = alternative;
	        X10Type t1 = (X10Type) e1.type();
	        X10Type t2 = (X10Type) e2.type();
	      
	        if (! ts.typeEquals(cond.type(), ts.Boolean())) {
	            throw new SemanticException(
	                                        "Condition of ternary expression must be of type boolean.",
	                                        cond.position());
	        }

	        // From the JLS, section:
	        // If the second and third operands have the same type (which may be
	        // the null type), then that is the type of the conditional expression.
	        if (ts.typeEquals(t1, t2))
	        	return type(t1);
	        
	        if (ts.typeBaseEquals(t1, t2)) {
	            return type(t1.rootType());
	        }
	        
	        // Otherwise, if the second and third operands have numeric type, then
	        // there are several cases:
	        if (t1.isNumeric() && t2.isNumeric()) {
	            // - If one of the operands is of type byte and the other is of
	            // type short, then the type of the conditional expression is
	            // short.
	            if (t1.isByte() && t2.isShort() || t1.isShort() && t2.isByte()) {
	                return type(ts.Short());
	            }
	            
	            // - If one of the operands is of type T where T is byte, short, or
	            // char, and the other operand is a constant expression of type int
	            // whose value is representable in type T, then the type of the
	            // conditional expression is T.
	            
	            if (t1.isIntOrLess() &&
	                    t2.isInt() &&
	                    ts.numericConversionValid(t1, e2.constantValue())) {
	                return type(t1);
	            }

	            if (t2.isIntOrLess() &&
	                    t1.isInt() &&
	                    ts.numericConversionValid(t2, e1.constantValue())) {
	                return type(t2);
	            }
	            
	            // - Otherwise, binary numeric promotion (Sec. 5.6.2) is applied to the
	            // operand types, and the type of the conditional expression is the
	            // promoted type of the second and third operands. Note that binary
	            // numeric promotion performs value set conversion (Sec. 5.1.8).
	            return type(ts.promote(t1, t2));
	        }
	        
	        // If one of the second and third operands is of the null type and the
	        // type of the other is a reference type, then the type of the
	        // conditional expression is that reference type.
	        if (t1.isNull() && t2.isReference()) return type(t2);
	        if (t2.isNull() && t1.isReference()) return type(t1);
	        
	        // If the second and third operands are of different reference types,
	        // then it must be possible to convert one of the types to the other
	        // type (call this latter type T) by assignment conversion (Sec. 5.2); the
	        // type of the conditional expression is T. It is a compile-time error
	        // if neither type is assignment compatible with the other type.
	        
	        if (t1.isReference() && t2.isReference()) {
	            if (ts.isImplicitCastValid(t1, t2)) {
	                return type(t2);
	            }
	            if (ts.isImplicitCastValid(t2, t1)) {
	                return type(t1);
	            }
	        }

	        throw new SemanticException("Could not determine type of ternary conditional expression; cannot assign " + t1 + " to " + t2 + " or vice versa.",
	                                    position());
	   }
}
