package polyglot.ext.x10.ast;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.TypeNode;
import polyglot.ast.Cast_c;
import polyglot.ast.Field_c;
import polyglot.ast.Lit_c;
import polyglot.ast.Local_c;
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
		protected boolean primitiveType = false;
		protected boolean notNullRequired = false;
		protected boolean toTypeNullable = false;
	
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

            this.primitiveType = false;
            this.toTypeNullable = false;
            this.notNullRequired = false;
            
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
	        	   this.notNullRequired  = true;
	           } 
	           
	           // if ToType is nullable then casting the null value is legal
	           if (xts.isNullable(x10ToType)) {
	        	   this.toTypeNullable = true;
	           }
		    	            }
            		return type(toType);
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
	    	return this.castType();
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
