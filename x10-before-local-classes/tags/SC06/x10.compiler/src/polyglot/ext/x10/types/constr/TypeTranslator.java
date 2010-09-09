package polyglot.ext.x10.types.constr;

import java.io.Serializable;

import polyglot.ast.Binary;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.Lit;
import polyglot.ast.Local;
import polyglot.ast.Receiver;
import polyglot.ast.Term;
import polyglot.ast.Unary;
import polyglot.ast.Variable;
import polyglot.ext.x10.ast.Here;
import polyglot.ext.x10.ast.X10Special;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.types.LocalInstance;
import polyglot.types.SemanticException;

/**
 * Translate from a ConstExr to a constraint term that can be serialized.
 * @author vj
 *
 */
public class TypeTranslator implements Serializable {

	public TypeTranslator( ) {
		super();
	}
	public C_UnaryTerm trans(Unary t) throws SemanticException {
		return new C_UnaryTerm_c(t.operator().toString(), trans(t.expr()), t.type());
	}
	public C_Field trans(Field t) throws SemanticException{
		//Report.report(1, "TypeTranslator: translating Field " + t);
		return new C_Field_c(t, (C_Var) trans(t.target()));
	}
	public C_Special trans(X10Special t)throws SemanticException {
		return new C_Special_c(t);
	}
	public C_Local trans(LocalInstance t)throws SemanticException {
		return new C_Local_c(t);
	}
	
	public C_Lit trans(Lit t) throws SemanticException {
		return new C_Lit_c(t.constantValue(), t.type());
	}
	
	public C_BinaryTerm trans(Binary t) throws SemanticException {
		//Report.report(1, "TypeTranslator: translating Binary " + t);
		String op = t.operator().toString();
		Expr left = t.left();
		Expr right = t.right();
		return new C_BinaryTerm_c(op, trans(left), trans(right), t.type());
	}
	public C_Var trans(Variable term) throws SemanticException {
		//Report.report(1, "TypeTranslator: translating Variable " + term);
		if (term instanceof Field) return trans((Field) term);
		if (term instanceof X10Special) return trans((X10Special) term);
		if (term instanceof Local) {
			LocalInstance li = ((Local) term).localInstance();
			return trans(li);
		}
		
		throw new SemanticException("Cannot translate term |" + term + "| into a constraint."
				+ "It must be a field, special or local.");
	}
	
	public  C_Term trans(Receiver term) throws SemanticException {
		//Report.report(1, "TypeTranslator: translating Receiver " + term);
		if (term == null) return null;
		if (term instanceof Lit) return trans((Lit) term);
		if (term instanceof Here) return C_Here_c.here;
		if (term instanceof Variable) return trans((Variable) term);
		if (term instanceof X10Special) return trans((X10Special) term);
		if (term instanceof Unary) return trans((Unary) term);
		if (term instanceof Binary) return trans((Binary) term);
		
		throw new SemanticException("Cannot translate term |" + term + "(" + term.getClass().getName()+")" +
				" to a term.");
	}
	public Constraint constraint(Binary term, Constraint c) throws SemanticException {
		//Report.report(1, "TypeTranslator: translating to constraint " + term);
		String op = term.operator().toString();
		Expr left = term.left();
		Expr right = term.right();
		//Report.report(1, "TypeTranslator: translating to constraint left=|" + left 
		//		+ "| op=|" + op + "| right=|" + right + "|");
		if (op.equals("==")) {
			if (left instanceof Variable) {
				//Report.report(1, "TypeTranslator: translating to constraint left=|" + left 
				//		+ "| is a variable.");
				return c.addBinding(trans((Variable) left), trans(right));
			}
			if (left instanceof X10Special) {
				X10Special s = (X10Special) left;
				if (! s.kind().toString().equals("self"))
					throw new SemanticException("Cannot constrain this.");
				return c.addBinding(trans(s), trans(right));
			}
			if (right instanceof Variable) 
				return c.addBinding(trans((Variable) right), trans(left));
		
			if (right instanceof X10Special) {
				X10Special s = (X10Special) right;
				if (! s.kind().toString().equals("self"))
					throw new SemanticException("Cannot constrain this.");
				return c.addBinding(trans(s), trans(left));
			}
		}
		if (op.equals("&&")) {
			c = constraint(left, c);
			c = constraint(right, c);
			return c;
		}
		throw new SemanticException("Cannot translate term |" + term + "| into a constraint." +
				"It must be a conjunction of equalities.");
			
	}
	/**
	 * Translate an expression into a constraint, throwing SemanticExceptions 
	 * if this is not possible.
	 * This must be called after type-checking of Expr.
	 * @param term
	 * @param c
	 * @return
	 * @throws SemanticException
	 */
	public Constraint constraint(Expr term, Constraint c) throws SemanticException {
		if (term == null) return c;
		X10TypeSystem ts = (X10TypeSystem) term.type().typeSystem();
			
		if (! ts.typeEquals(term.type(),ts.Boolean() )) 
			throw new SemanticException("Cannot build constraint from expression |" + term 
					+ "| of type " 
					+ term.type()+ " (not Boolean).");
		if (term instanceof Binary) return constraint((Binary) term, c);
		C_Term t = trans(term);
		c.addTerm(t);
		return c;
	}
	public Constraint constraint(Expr e) throws SemanticException {
		//Report.report(1, "TypeTranslator: translating to constraint " + e);
		Constraint c = new Constraint_c();
		return constraint(e, c);
	}
	public static C_Term translate(Receiver r) throws SemanticException {
		return new TypeTranslator().trans(r);
	}
	public static boolean isPureTerm(Term t) {
		boolean result=false;
		if (t instanceof Variable) {
			Variable v = (Variable) t;
			result = v.flags().isFinal();
		}
		return result;
	}
}
