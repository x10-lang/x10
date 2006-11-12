package polyglot.ext.x10.ast;

import java.util.Iterator;
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
import polyglot.types.PrimitiveType;
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
public class X10Cast_c extends Cast_c {
	
		private boolean dynamicCheckNeeded = false;
		private  String primitiveWrapper = null;
		private boolean isX10ArrayCasted = false;
		private boolean nullableCheck = false;
		
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
        	   
               // Handle isNullable additionnal constraint 
        	   // Such cast ((T1) nullable T2), should checks at runtime 
        	   // the expression to cast is not null
               if (xts.isNullable(x10FromType) && (!xts.isNullable(x10ToType))) {
            	   this.dynamicCheckNeeded = true;
            	   this.nullableCheck  = true;
               }
               
        	   // constraint may not be meet for primitive type
        	   // i.e: (int(:self==0)) int : is a valid cast
        	   // i.e: (int(:self==0)) int(self==1) : is a valid cast at this time !!! 
        	   // (this cast is checked after by the isImplicitCastValid call)
        	   if(((X10Type)toType).depClause() != null) {
	        	   if (fromType instanceof X10PrimitiveType) {
	            	   if (expr.constantValue() != null) {
	            		   // if so we try a numeric conversion.
	            		   // This allows to promote constant to dependent type and check it against the cast type. 
	    	               // Exemple1: type(int(:self==0) 0) -> (int(:self==0) int(:self==0))
	    	               // Exemple2: type(int(:self==0) 1) -> (int(:self==0) int(:self==1))              
	    	   	            if (ts.numericConversionValid(toType,
	    		                    expr.constantValue())) {
	    		            		this.dynamicCheckNeeded = false;
	    		         	   return type(toType);        		   
	    	   	            } else {
	    	   	            	// numeric conversion is invalid in java
	    	   			   	    throw new SemanticException("Cannot cast the expression of type \"" 
	    			   					+ fromType + "\" to type \"" 
	    			   					+ toType + "\".",
	    			   				        position());	   	            	
	    	   	            }
	    	            } else {    	            	
	    	            	// expression to cast is a primitive type (with or without constraints) and its not a constant
	    	            	if (((X10Type)fromType).depClause() != null){
	    	            		// if expression has constraints, checks if implicit cast is valid.
	    	            		if (!ts.isImplicitCastValid(fromType, toType)) {
			            		throw new SemanticException("Cannot implicitly cast the expression of type \"" 
					   					+ fromType + "\" to type \"" 
					   					+ toType + "\".",
					   				        position());
	    	            		}
	        	            	// we set the primitive wrapper to generate the runtime cast check
	    	            		this.primitiveWrapper = ((PrimitiveType) fromType).wrapperTypeString(ts);
	    	            	} else {
		    	            	if (((X10Type) toType).depClause() != null) {
		    	            		this.primitiveWrapper = ((PrimitiveType) fromType).wrapperTypeString(ts);
		    	            		this.dynamicCheckNeeded = true;
		    	            	}
	    	            	}
	    	            }
	        	   } else {
	        		   if((!ts.isSubtype(fromType, toType)) && 
	        				   ((X10Type)toType).depClause() != null)  {
			    		   // cast is valid if toType or fromType have constraints, checks them at runtime
		            		Report.report(1,"Warning! Cast from " + fromType + " to " + toType + " is unsafe at line " + this.position + ".");
		            		this.dynamicCheckNeeded = true;
		            	}
		            		// else cast is statically valid
	        	   }
        	   }
           }
            		return type(toType);
        }
        
        public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
    		if (this.dynamicCheckNeeded){
        		X10CastHelper.prettyPrintCast(w, tr, (X10Type) this.castType.type(), 
        				this.expr, this, this.nullableCheck, primitiveWrapper);
        	}
        	else
        		super.prettyPrint(w,tr);
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
        			Field_c field = (Field_c) nestedExpression;
        			return !(field.target() instanceof ParExpr);
        		}
        		if ((nestedExpression instanceof Local_c) || 
        				(nestedExpression instanceof Lit_c))
        			return true;
        		
        		return false;
        	}
        	
        	/**
        	 * Pretty print method for cast.
        	 * Checks if the expression to cast has side effect and choose whether to inline or use a method
        	 * to perform the cast. 
             * @param w The Code writer
             * @param tr The prettyPrinter
             * @param castType The target cast type (toType)
             * @param exprToCast The expression to cast (fromType)
        	 * @param cast_c The enclosing cast expression.
        	 * @param primitiveWrapper The primitive type wrapper to use if needed.
        	 */
			public static void prettyPrintCast(CodeWriter w, PrettyPrinter tr, X10Type castType,
            		Expr exprToCast, X10Cast_c cast_c, boolean nullableCheck, String primitiveWrapper) {
				if (isSideEffectFree(exprToCast)) {
        			X10CastHelper.prettyPrintInlineCast(w, tr, castType, exprToCast, cast_c, true, nullableCheck, primitiveWrapper);
        		} else {
                	if (primitiveWrapper != null) {
            			X10CastHelper.prettyPrintSideEffectCast(w, tr, castType, exprToCast, cast_c, nullableCheck, true,
            					"x10.lang.RuntimeCastChecker.checkPrimitiveType");
                	} else {
            			X10CastHelper.prettyPrintSideEffectCast(w, tr, castType, exprToCast, cast_c, nullableCheck, false,
    					"x10.lang.RuntimeCastChecker.checkCast");
                	}
        		}
			}

        	/**
        	 * Pretty print method for instanceof.
        	 * Checks if the expression to cast has side effect and choose whether to inline or use a method
        	 * to perform the instanceof check. 
             * @param w The Code writer
             * @param tr The prettyPrinter
             * @param castType The target cast type (toType)
             * @param exprToCast The expression to cast (fromType)
        	 * @param instanceOf The enclosing cast expression.
        	 */
            public static void prettyPrintInstanceOf(CodeWriter w, PrettyPrinter tr, X10Type castType, 
            		Expr exprToCast, X10Instanceof_c instanceOf) {
            	if (isSideEffectFree(exprToCast)) {
        	    	X10Cast_c.X10CastHelper.prettyPrintInlineCast(w,tr,castType,exprToCast,instanceOf,false, false, null);
            	} else {
          	    	X10Cast_c.X10CastHelper.prettyPrintSideEffectCast(w,tr,castType,exprToCast,instanceOf, false, false,
  	    			"x10.lang.RuntimeCastChecker.isInstanceOf");
        		}	
          	}

            /**
             * Generates code for a constraint checking using java reflexion.
             * Generates something like that :
             *   CheckingClass.<TargetType>checkType(
        	 *	 new Constraint [] {new Constraint("p",0), new Constraint("q",1)}, 
        	 *	 obj);
             * @param w The Code writer
             * @param tr The prettyPrinter
             * @param castType The target cast type (toType)
             * @param expr The expression to cast (fromType)
			 * @param enclosingExpression Enclosing cast expression
			 * @param runtimeCastCheckerMethodName The method name to call at runtime to perform the cast.
			 */
            private static void prettyPrintSideEffectCast(CodeWriter w, PrettyPrinter tr, 
            		X10Type castType, Expr expr, Expr enclosingExpression, boolean nullableCheck, boolean primitiveType, String runtimeCastCheckerMethodName) {
            	w.begin(0);
            	
            	if (enclosingExpression instanceof X10Instanceof_c) {
                	w.write("(");
            	} else {
                	w.write("((" + castType.baseType()+ ")");            		
            	}
            	
            	w.write(runtimeCastCheckerMethodName + "(");
            	// generate Constraint
            	Constraint constraint;
            	w.write("new x10.lang.RuntimeCastChecker.RuntimeConstraint [] {");
            	if ((constraint = castType.depClause()) != null) {
            		Map map;
        		if ((map = ((Constraint_c)constraint).constraints()) != null) {
            			int size = map.entrySet().size();
            			for(Iterator it = map.entrySet().iterator(); it.hasNext();) {
            				Entry entry = (Entry) it.next();
                				// generates something like that ==>   && ((TargetType) obj).propertyName() == 0)
                	        	w.newline();
								
                	        	String valueToCheck = ((C_Term) entry.getValue()).toString();
								
                	        	if (valueToCheck.startsWith("self.")) {
                	        		String fieldName = valueToCheck.replaceFirst("self.","");
									w.write("new x10.lang.RuntimeCastChecker.RuntimeConstraintOnSelf(\"" + 
	                	        			((C_Var) entry.getKey()).name() +"\", \"" + fieldName + "\")");
									}
                	        	else {
	                	        	w.write("new x10.lang.RuntimeCastChecker.RuntimeConstraint(\"" + 
	                	        			((C_Var) entry.getKey()).name() +"\", " + ((C_Term) entry.getValue()) + ")");
                	        	}
            					size--;
                	        	if (size > 0) {
                					w.write(",");
                				}
            			}
            		}
            	}
            	w.write("}," + nullableCheck + ",");
            	enclosingExpression.printSubExpr(expr, w, tr);
            	if (primitiveType) {
            		w.write(", null");
            	} else  {
            		w.write(", " + castType.baseType()+ ".class");	
            	}
            	w.write("))");
        	}
	        
            /**
             * Generates cast of instanceof checking code. The code generates is inlined.
             * This code lead to evaluate several time the expression to cast.
             * Hence this method is only suitable to handle cast and instanceof for expression 
             * that does not produce any side effect.
             * @param w The Code writer
             * @param tr The prettyPrinter
             * @param castType The target cast type (toType)
             * @param expr The expression to cast (fromType)
			 * @param enclosingExpression Enclosing cast expression.
             * @param throwException if true then the method produce a cast operation, otherwise an instanceof one.
             * @param primitiveWrapper The primitive type wrapper to use if needed,
             */
	        private static void prettyPrintInlineCast(CodeWriter w, PrettyPrinter tr, X10Type castType, 
					Expr expr, Expr enclosingExpr, boolean throwException, boolean nullableCheck, String primitiveWrapper) {
	        	boolean notFirst = false;
	        	String castBaseType = castType.baseType().toString();
	        	w.begin(0);
	        	// begin cast
	        	w.write("(");
	        	// begin condition
    			// generating (boolean expression) --> ((...)&&(...)&&(...))
	        	w.write("(");
	        	if (primitiveWrapper == null) {
	        		// if cast is from a reference type we need to test instanceof first
		        	w.write("(");
		        	enclosingExpr.printSubExpr(expr, w, tr);
		        	w.write(" instanceof " + castBaseType + ")");
		        	notFirst = true;
	        	} else {
	        		castBaseType = primitiveWrapper;
	        	}
	        	
	        	if (nullableCheck) {
	        		if (notFirst)
	        			w.write(" && ");
	        		else
	        			notFirst = true;
	        		w.write("(");
	        		w.write("x10.lang.RuntimeCastChecker.isObjectNotNull(");
	        		enclosingExpr.printSubExpr(expr, w, tr);
        			w.write("))");
	        	}
	        	
	        	Constraint constraint;
	        	if ((constraint = castType.depClause()) != null) {
	        		Map map;
	        		if ((map = ((Constraint_c)constraint).constraints()) != null) {
	        			for(Iterator it = map.entrySet().iterator(); it.hasNext();) {
	        				Entry entry = (Entry) it.next();
	        	        	w.newline();
	        	        	if (notFirst)
	        	        		w.write(" && ");
	    	        		else
	    	        			notFirst = true;
	        	        	
        	        		w.write("(");
	        				if (primitiveWrapper != null ) {
		        				enclosingExpr.printSubExpr(expr, w, tr);	        					
	        				} else {
		        				w.write("((" + castBaseType + ")"); // TODO: check type is without dependent type constraints
		        				enclosingExpr.printSubExpr(expr, w, tr);
		            			w.write(")." + ((C_Var) entry.getKey()).name() + "()");	        					
	        				}
	            			w.write("=="); // TODO: Future work, get the operator from the constraint
	            			String valueToCheck = ((C_Term) entry.getValue()).toString();
	            			w.write("" + valueToCheck);
	            			w.write(")");
	        			}
	        		}
	        	}
	        	// end condition
    			w.write(")");
    			// generating true/false action --> ? (...) : (...)
	        	w.newline();
	        	if (throwException) {
	        		w.write(" ? (");
	        		if (primitiveWrapper == null) {
			        	w.write("(" + castBaseType + ")");
	        		}
    	        	enclosingExpr.printSubExpr(expr, w, tr);
    	        	w.write(") : (" + castBaseType + ") x10.lang.RuntimeCastChecker.throwClassCastException()");
	        	} else {
		        	w.write(" ? true : false");
	        	}
	        	// end cast
	        	w.write(")");
	        	w.end();			
        	}
        }
}
