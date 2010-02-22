/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.types.checker;

import java.util.Map;

import polyglot.ast.Assign;
import polyglot.ast.Assign_c;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.Assign.Operator;
import polyglot.types.Name;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import x10.ast.X10Binary_c;
import x10.ast.X10New_c;
import x10.errors.Errors;
import x10.errors.Errors.CannotAssign;
import x10.types.ConstrainedType;
import x10.types.MacroType;
import x10.types.ParameterType;
import x10.types.X10ClassDef;
import x10.types.X10ClassType;
import static polyglot.ast.Assign.*;

/**
 * A set of static methods used by AST nodes to check types.
 * @author vj
 *
 */
public class Checker {

	public static Node typeCheckAssign(Assign_c a, ContextVisitor tc) throws SemanticException {
	    Assign_c n = (Assign_c) a.typeCheckLeft(tc);
	
	    TypeSystem ts = tc.typeSystem();
	    Type t = n.leftType();
	
	    if (t == null)
	        t = ts.unknownType(n.position());
	
	    Expr right = n.right();
	    Assign.Operator op = n.operator();
	
	    Type s = right.type();
	
	    if (op == ASSIGN) {
	        try {
	            Expr e = Converter.attemptCoercion(tc, right, t);
	            return n.right(e).type(t);
	        }
	        catch (SemanticException e) {
	        	// Dont try to extract the LHS expression, this is called by X10FieldAssign_c as well.
	        	throw new Errors.CannotAssign(right, t, n.position());
	        }
	    }
	
	    if (op == ADD_ASSIGN) {
	        // t += s
	        if (ts.typeEquals(t, ts.String(), tc.context()) && ts.canCoerceToString(s, tc.context())) {
	            Expr newRight = X10Binary_c.coerceToString(tc, right);
	            return n.right(newRight).type(ts.String());
	        }                
	
	        if (t.isNumeric() && s.isNumeric()) {
	            return n.type(ts.promote(t, s));
	        }
	
	        throw new SemanticException("The " + op + " operator must have "
	                                    + "numeric or String operands.",
	                                    n.position());
	    }
	
	    if (op == Assign.SUB_ASSIGN || op == Assign.MUL_ASSIGN ||
	            op == DIV_ASSIGN || op == MOD_ASSIGN) {
	        if (t.isNumeric() && s.isNumeric()) {
	            return n.type(ts.promote(t, s));
	        }
	
	        throw new SemanticException("The " + op + " operator must have "
	                                    + "numeric operands.",
	                                    n.position());
	    }
	
	    if (op == BIT_AND_ASSIGN || op == BIT_OR_ASSIGN || op == BIT_XOR_ASSIGN) {
	        if (t.isBoolean() && s.isBoolean()) {
	            return n.type(ts.Boolean());
	        }
	
	        if (ts.isLongOrLess(t) &&
	                ts.isLongOrLess(s)) {
	            return n.type(ts.promote(t, s));
	        }
	
	        throw new SemanticException("The " + op + " operator must have "
	                                    + "integral or boolean operands.",
	                                    n.position());
	    }
	
	    if (op == SHL_ASSIGN || op == SHR_ASSIGN || op == USHR_ASSIGN) {
	        if (ts.isLongOrLess(t) &&
	                ts.isLongOrLess(s)) {
	            // Only promote the left of a shift.
	            return n.type(ts.promote(t));
	        }
	
	        throw new SemanticException("The " + op + " operator must have "
	                                    + "integral operands.",
	                                    n.position());
	    }
	
	    throw new InternalCompilerError("Unrecognized assignment operator " +
	                                    op + ".");
	}
	
	  public static void checkVariancesOfType(Position pos, Type t, ParameterType.Variance requiredVariance, 
	    		String desc, Map<Name,ParameterType.Variance> vars, ContextVisitor tc) throws SemanticException {
	        if (t instanceof ParameterType) {
	            ParameterType pt = (ParameterType) t;
	            X10ClassDef cd = (X10ClassDef) tc.context().currentClassDef();
	            if (pt.def() != cd)
	                return;
	            ParameterType.Variance actualVariance = vars.get(pt.name());
	            if (actualVariance == null)
	                return;
	            switch (actualVariance) {
	            case INVARIANT:
	                break;
	            case COVARIANT:
	                switch (requiredVariance) {
	                case INVARIANT:
	                    throw new SemanticException("Cannot use covariant parameter " + pt + " " + desc + "; must be invariant.", pos);
	                case COVARIANT:
	                    break;
	                case CONTRAVARIANT:
	                    throw new SemanticException("Cannot use covariant parameter " + pt + " " + desc + "; must be contravariant or invariant.", pos);
	                }
	                break;
	            case CONTRAVARIANT:
	                switch (requiredVariance) {
	                case INVARIANT:
	                    throw new SemanticException("Cannot use contravariant parameter " + pt + " " + desc + "; must be invariant.", pos);
	                case COVARIANT:
	                    throw new SemanticException("Cannot use contravariant parameter " + pt + " " + desc + "; must be covariant or invariant.", pos);
	                case CONTRAVARIANT:
	                    break;
	                }
	                break;
	            }
	        }
	        if (t instanceof MacroType) {
	            MacroType mt = (MacroType) t;
	            checkVariancesOfType(pos, mt.definedType(), requiredVariance, desc, vars, tc);
	        }
	        if (t instanceof X10ClassType) {
	            X10ClassType ct = (X10ClassType) t;
	            X10ClassDef def = ct.x10Def();
	            for (int i = 0; i < ct.typeArguments().size(); i++) {
	                Type at = ct.typeArguments().get(i);
	                ParameterType pt = def.typeParameters().get(i);
	                ParameterType.Variance v = def.variances().get(i);
	                ParameterType.Variance newVariance;

	                switch (v) {
	                case INVARIANT:
	                    checkVariancesOfType(pos, at, requiredVariance, desc, vars, tc);
	                    break;
	                case COVARIANT:
	                    checkVariancesOfType(pos, at, requiredVariance, desc, vars, tc);
	                    break;
	                case CONTRAVARIANT:
	                    switch (requiredVariance) {
	                    case INVARIANT:
	                        checkVariancesOfType(pos, at, requiredVariance, desc, vars, tc);
	                        break;
	                    case COVARIANT:
	                        checkVariancesOfType(pos, at, ParameterType.Variance.CONTRAVARIANT, desc, vars, tc);
	                        break;
	                    case CONTRAVARIANT:
	                        checkVariancesOfType(pos, at, ParameterType.Variance.COVARIANT, desc, vars, tc);
	                        break;
	                    }
	                    break;
	                }
	            }
	        }
	        if (t instanceof ConstrainedType) {
	            ConstrainedType ct = (ConstrainedType) t;
	            Type at = Types.get(ct.baseType());
	            checkVariancesOfType(pos, at, requiredVariance, desc, vars, tc);
	        }
	    }


}
