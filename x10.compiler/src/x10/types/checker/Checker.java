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
import polyglot.ast.Binary;
import polyglot.ast.Call;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Assign.Operator;
import polyglot.types.Name;
import polyglot.types.ProcedureDef;
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
import x10.types.X10Context;
import x10.types.X10MethodInstance;
import x10.types.X10ProcedureInstance;
import x10.types.X10TypeMixin;
import x10.visit.X10TypeChecker;
import static polyglot.ast.Assign.*;

/**
 * A set of static methods used by AST nodes to check types.
 * @author vj
 *
 */
public class Checker {
	
	public static void checkOfferType(Position pos, 
			X10ProcedureInstance<? extends ProcedureDef> pi,ContextVisitor tc) throws SemanticException {
		X10Context cxt = (X10Context) tc.context();
		Type offerType = (Type) Types.get(pi.offerType());
		Type type = cxt.collectingFinishType();
		if (offerType != null) {
			if (type == null) 
				throw new Errors.CannotCallCodeThatOffers(pi, pos);
			if (! tc.typeSystem().isSubtype(offerType, type, cxt)) 
				throw new Errors.OfferTypeMismatch(offerType, type, pos);
		} else {
        }
		
	}

	public static Node typeCheckAssign(Assign_c a, ContextVisitor tc) {
	    Assign n = a;
	    
	    try {
	        n = (Assign) a.typeCheckLeft(tc);
	    } catch (SemanticException e) {
	        Errors.issue(tc.job(), e, a);
	    }

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
	            n = n.right(e);
	        }
	        catch (SemanticException e) {
	        	// Don't try to extract the LHS expression, this is called by X10FieldAssign_c as well.
	        	Errors.issue(tc.job(), new Errors.CannotAssign(right, t, n.position()));
	        }
	    }

	    if (op == ADD_ASSIGN || op == Assign.SUB_ASSIGN || op == Assign.MUL_ASSIGN ||
	        op == DIV_ASSIGN || op == MOD_ASSIGN || op == BIT_AND_ASSIGN || op == BIT_OR_ASSIGN ||
	        op == BIT_XOR_ASSIGN || op == SHL_ASSIGN || op == SHR_ASSIGN || op == USHR_ASSIGN)
	    {
	        Binary.Operator bop = op.binaryOperator();
	        NodeFactory nf = tc.nodeFactory();
	        Binary bin = (Binary) nf.Binary(n.position(), n.left(), bop, right);
	        Call c = X10Binary_c.desugarBinaryOp(bin, tc);
	        if (c != null) {
	            X10MethodInstance mi = (X10MethodInstance) c.methodInstance();
	            if (mi.error() != null)
	                Errors.issue(tc.job(), mi.error(), n);
	            t = c.type();
	        } else {
	            Errors.issue(tc.job(), new Errors.CannotAssign(right, t, n.position()));
	        }
	    }
	    return n.type(t);
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
