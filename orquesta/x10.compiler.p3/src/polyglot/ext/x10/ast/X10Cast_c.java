/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Cast_c;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.TypeNode;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.main.Report;
import polyglot.types.SemanticException;
import polyglot.util.Position;
import polyglot.visit.TypeChecker;

/**
 * Represent java cast operation.
 * (CastType) expression
 * This class is compliant with dependent type constraint.
 * If a dynamic cast is needed, then some code is generated to check 
 * instance's value, field, etc... are valid against declared type constraint.
 *
 * @author vcave
 *
 */
public class X10Cast_c extends Cast_c implements X10Cast, X10CastInfo {
		protected boolean primitiveType = false;
		protected boolean notNullRequired = false;
		protected boolean toTypeNullable = false;
	
        public X10Cast_c(Position pos, TypeNode castType, Expr expr) {
                super(pos, castType, expr);
        }

        public Node typeCheck(TypeChecker tc) throws SemanticException {
        	X10Cast_c n = (X10Cast_c) copy();

        	X10Type toType = (X10Type) n.castType.type();
            X10Type fromType = (X10Type) n.expr.type();
        	X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
            
        	n.primitiveType = false;
            n.toTypeNullable = false;
            n.notNullRequired = false;
            
           if (Report.should_report("debug", 5)) {
                    Report.report(5, "[Cast_c] |" + n + "|.typeCheck(...):");
                    Report.report(5, "[Cast_c] ...type=|" +  type+"|.");
           }
           Expr result = n.type(toType);
           if (Report.should_report("debug", 5)) {
           	Report.report(5, "[Cast_c] ...returning=|" +  result+"| of type=|" + result.type() + "|.");
           }
           
           // check java cast is valid and dependent type constraint are meet
           if (! ts.isCastValid(fromType, toType)) {
		   	    throw new SemanticException("Cannot cast the expression of type \"" 
		   					+ fromType + "\" to type \"" 
		   					+ toType + "\".",
		   				        position());
           } else { 
        	   
        	   // the cast may requires runtime checking. For example ((T) java.lang.Object)

        	   // if target type is a primitive
	           if ((n.primitiveType = toType.isPrimitive())) {
	        	   // if target type is a primitive, then we should not try to check
	        	   // whether the expr is null or not.
	        	   n.primitiveType = true;
	           }

	           // if ToType is nullable then casting the null value is legal
	           if (!ts.isValueType(toType)) {
	        	   n.toTypeNullable = true;
	        	   n.notNullRequired = false;
	        	   // to type is nullable, hence we don't want 
	        	   // to handle runtime checking with primitive 
	        	   n.primitiveType = false;
	           } else {
	        	   n.toTypeNullable = false;
	        	   // Handle isNullable additionnal constraint 
		    	   // Such cast ((T1) nullable T2), should checks at runtime 
		    	   // the expression to cast is not null
		           if (!ts.isValueType(fromType)) {
		        	   n.notNullRequired = true;
		           }
	           }
            }
           
           return n.type(toType);
        }
        
		public boolean isDepTypeCheckingNeeded() {
			return false;
		}

		public boolean notNullRequired() {
			return notNullRequired;
		}

		public boolean isPrimitiveCast() {
			return primitiveType;
		}
		
		public boolean isToTypeNullable() {
			return this.toTypeNullable;
		}

		public TypeNode getTypeNode() {
	    	return (TypeNode) this.castType().copy();
	    }

		public void setToTypeNullable(boolean b) {
			this.toTypeNullable = b;
		}

		public void setPrimitiveCast(boolean b) {
			this.primitiveType = b;
		}

		public void setNotNullRequired(boolean b) {
			this.notNullRequired = b;
		}
		
		public String toString() {
		    return expr.toString() + " as " + castType.toString();
		}
}
