package polyglot.ext.x10.ast;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.TypeNode;
import polyglot.ext.jl.ast.Cast_c;
import polyglot.ext.jl.ast.Field_c;
import polyglot.ext.jl.ast.Lit_c;
import polyglot.ext.jl.ast.Local_c;
import polyglot.ext.x10.types.X10PrimitiveType;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.ext.x10.types.constr.C_Term;
import polyglot.ext.x10.types.constr.C_Var;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.ext.x10.types.constr.Constraint_c;
import polyglot.main.Report;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.PrettyPrinter;
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
	
		private boolean dynamicCheckNeeded = false;
		private boolean primitiveType = false;
		private boolean notNullRequired = false;
		private boolean toTypeNullable = false;
		
        public X10Cast_c(Position pos, TypeNode castType, Expr expr) {
                super(pos, castType, expr);
        }

        public Node typeCheck(TypeChecker tc) throws SemanticException {
            Type toType = castType.type();
            Type fromType = expr.type();
        	X10Type x10ToType = (X10Type) toType;
        	X10Type x10FromType = (X10Type) fromType;
            TypeSystem ts = tc.typeSystem();
            X10TypeSystem xts = (X10TypeSystem) x10ToType.typeSystem();
           if (Report.should_report("debug", 5)) {
                    Report.report(5, "[Cast_c] |" + this + "|.typeCheck(...):");
                    Report.report(5, "[Cast_c] ...type=|" +  type+"|.");
           }
           Expr result = type(toType);
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
        	   
        	   // Handle isNullable additionnal constraint 
	    	   // Such cast ((T1) nullable T2), should checks at runtime 
	    	   // the expression to cast is not null
	           if (xts.isNullable(x10FromType) && (!xts.isNullable(x10ToType))) {
	        	   this.dynamicCheckNeeded = true;
	        	   this.notNullRequired  = true;
	           } 
	           
	           // if ToType is nullable then casting the null value is legal
	           if (xts.isNullable(x10ToType)) {
	        	   this.dynamicCheckNeeded = true;
	        	   this.toTypeNullable = true;
	           }

	           // we generate deptype constraint code checking only if targetType is constrained and fromType not.
	           // Otherwise the previous isCastValid has already check type compatibility
	    	   if ((x10ToType.depClause() != null) && (x10FromType.depClause() == null)) {
			   	   if (fromType.isPrimitive()) {
		        		   if (!expr.isConstant()) {
			    	            		Report.report(1,"Warning! Primitive Cast from " + fromType + " to " + toType + " is unsafe at line " + this.position + ".");
			    	            		this.primitiveType = true;
			    	            		this.dynamicCheckNeeded = true;
		    	            }
		        		   if (toType.isClass()) {
		        			   // NOTE: Current release only allow casting to x10.lang.Object, which is unlikely to be constrained
		        			   // NOTE: If casting to some constrainted wrapper type are allowed in the future a dynamic check would be needed.      
		        			   // Class <-- Primitive (Boxing Operation)
		        			   // this.dynamicCheckNeeded = true;
		        		   }
		            	   // else constant had been promoted to deptype and checking occured previously in isCastValid
	        	   } else {
	        		   if (!x10ToType.equalsImpl(x10FromType)) {
				    		   // cast is valid if toType or fromType have constraints, checks them at runtime
		        			   Report.report(1,"Warning! Cast from " + fromType + " to " + toType + " is unsafe at line " + this.position + ".");
			            		this.dynamicCheckNeeded = true;
	        		   }
	        		   
	        		   if ((fromType.isClass()) && (toType.isPrimitive())) {
	        			   // Primitive <-- Class (UnBoxing Operation)
	        			   this.dynamicCheckNeeded = true;
	        			   this.primitiveType = true;
	        		   }
	        		   // else type are equals, we do not perform the cast
	        	   }
	    	   	}
           }
            		return type(toType);
        }
        

		public boolean isDynamicCheckNeeded() {
			return dynamicCheckNeeded;
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

        /**
         * Regroup some method that can be used either by X10Cast_c 
         * or X10Instanceof_c to avoid duplication.
         * @author vcave
         */
        public static class X10CastHelper {
            /**
             * In order to sharpen cast code generated, we try to 
             * identify the true nature of the expression to cast
             * if the expression is surronded by parenthesis, we get the nested one. 
             * @param source Expression to analyze.
             * @return Source's expression nested expression enclosed between parenthesis or source.
             */
            public static Expr getNestedExpression(Expr source) {
        		if (source instanceof ParExpr_c) {
        			Expr nestedExpr = source;
        			while(nestedExpr instanceof ParExpr_c) {
        				nestedExpr = ((ParExpr_c) nestedExpr).expr();
        			}
        			
        			return nestedExpr;
        		}
        		return source;
            }
            
			/**
             * Checks whether the expression may produce side effect (rudimentary).
             * @param expression Expression to check.
             * @return
             */
        	public static boolean isSideEffectFree(Expr exprToCast) {
        		Expr nestedExpression = X10CastHelper.getNestedExpression(exprToCast);
        		if (nestedExpression instanceof Field_c) {
        			// resolve how the field is access
        			// this.a; meth().a, etc..
        			return isSideEffectFree((Expr) ((Field_c) nestedExpression).target());
        		}
        		if ((nestedExpression instanceof Local_c) || 
        				(nestedExpression instanceof Lit_c))
        			return true;
        		
        		return false;
        	}

        	/**
        	 * Generates the constraint array to used for runtime checking.
        	 * @param toType
        	 * @return
        	 */
			public static String getRuntimeConstraintTab(X10Type toType) {
				String res = "";
				Constraint constraint;
				res += "new x10.lang.RuntimeCastChecker.RuntimeConstraint [] {";
				if ((constraint = toType.depClause()) != null) {
					Map map;
				if ((map = ((Constraint_c)constraint).constraints()) != null) {
						int size = map.entrySet().size();
						for(Iterator it = map.entrySet().iterator(); it.hasNext();) {
							Entry entry = (Entry) it.next();
			    				// generates something like that ==>   && ((TargetType) obj).propertyName() == 0)
			    	        	res +="\n";
			    	        	String valueToCheck = ((C_Term) entry.getValue()).toString();
								
			    	        	if (valueToCheck.startsWith("self.")) {
			    	        		String fieldName = valueToCheck.replaceFirst("self.","");
									res += "new x10.lang.RuntimeCastChecker.RuntimeConstraintOnSelf(\"" + 
			        	        			((C_Var) entry.getKey()).name() +"\", \"" + fieldName + "\")";
								} else {
			        	        	res += "new x10.lang.RuntimeCastChecker.RuntimeConstraint(\"" + 
			        	        			((C_Var) entry.getKey()).name() +"\", " + ((C_Term) entry.getValue()) + ")";
			    	        	}
								size--;
			    	        	if (size > 0) {
			    					res+=",";
			    				}
						}
					}
				}
				res+= "}";
				return res;				
			}
			
			/**
			 * To be used with the associated template 'cast-util-runtime-constraint-inlined'
			 * @param castType
			 * @param expr
			 * @param primitiveType
			 * @return
			 */
			public static List [] getRuntimeConstraintInlined(X10Type castType, Expr expr, boolean primitiveType) {
				List [] constraintList;
	        	Constraint constraint;
	        	if ((constraint = castType.depClause()) != null) {
	        		String castBaseType = castType.baseType().toString();
	        		Map map;
	        		if ((map = ((Constraint)constraint).constraints()) != null) {
		        		 constraintList = new List [] {new LinkedList(), new LinkedList(), new LinkedList(), new LinkedList()};
	        			for(Iterator it = map.entrySet().iterator(); it.hasNext();) {
	        				Entry entry = (Entry) it.next();
	        				if (primitiveType) {
	        					constraintList[0].add(expr);
	        				} else {
	        					constraintList[0].add("((" + castBaseType + ")"+expr + ")." + ((C_Var) entry.getKey()).name() + "()");
	        				}
	        				constraintList[1].add("==");
	            			constraintList[2].add(((C_Term) entry.getValue()).toString());
	            			if (it.hasNext()) {
	        	        		constraintList[3].add(" && ");
	            			} else {
	        	        		constraintList[3].add(" ");
	            			}
	        			}
	        			return constraintList;
	        		}
	        	}
	        	return new List [0]; 
        	}
		}
}
