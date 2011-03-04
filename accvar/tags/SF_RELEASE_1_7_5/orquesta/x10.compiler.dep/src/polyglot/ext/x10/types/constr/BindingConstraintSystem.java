/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.types.constr;

import java.io.Serializable;

import polyglot.ast.Binary;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.Lit;
import polyglot.ast.Local;
import polyglot.ast.Node_c;
import polyglot.ast.Receiver;
import polyglot.ast.Term;
import polyglot.ast.TypeNode;
import polyglot.ast.Unary;
import polyglot.ast.Variable;
import polyglot.ext.x10.ast.Here;
import polyglot.ext.x10.ast.X10Special;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.frontend.Globals;
import polyglot.main.Report;
import polyglot.types.LocalInstance;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.NodeVisitor;

/**
 * Translate from a ConstExr to a constraint term that can be serialized.
 * @author vj
 *
 */
public class BindingConstraintSystem implements ConstraintSystem {

	protected final X10TypeSystem typeSystem;

	public BindingConstraintSystem(X10TypeSystem xts) {
		super();
		typeSystem = xts;
	}
	
	    public SimpleConstraint binding(C_Var v1, C_Var v2) {
	        BindingConstraint_c b = new BindingConstraint_c(typeSystem, this);
	        try {
	            b.addBinding(v1, v2);
	        }
	        catch (Failure f) {
	            throw new InternalCompilerError("Error binding " + v1 + " == " + v2 + ".", f);
	        }
	        return b;
	    }

	    public SimpleConstraint constraintForTerm(C_Term term) throws SemanticException {
	        if (term instanceof C_Var) {
	            C_Var v = (C_Var) term;
	            return binding(v, typeSystem.TRUE());
	        }
	        if (term instanceof C_UnaryTerm) {
	            C_UnaryTerm e = (C_UnaryTerm) term;
	            if (e.op().equals("!") && e.arg() instanceof C_Var) {
	                C_Var v = (C_Var) e.arg();
	                return binding(v, typeSystem.TRUE().not());
	            }
	        }
	        if (term instanceof C_BinaryTerm) {
	            C_BinaryTerm e = (C_BinaryTerm) term;
	            C_Term l = e.left();
	            C_Term r = e.right();
	            if (e.op().equals("==") && l instanceof C_Var && r instanceof C_Var) {
	                return binding((C_Var) l, (C_Var) r);
	            }
	            if (e.op().equals("&&")) {
	                SimpleConstraint cl = constraintForTerm(l);
	                SimpleConstraint cr = constraintForTerm(r);
	                try {
	                    cl.addIn(cr);
	                    return cl;
	                }
	                catch (Failure f) {
	                    throw new SemanticException("Cannot translate expression |" + term + "| into a constraint: " + f.getMessage());
	                }
	            }
	        }
	        throw new SemanticException("Cannot translate expression |" + term + "| into a constraint.");
	    }
	    
	    public String toString() {
	        return "X10";
	    }
}
