package polyglot.ext.x10.types.constr;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import polyglot.ast.Binary;
import polyglot.ast.Call;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.Formal;
import polyglot.ast.Lit;
import polyglot.ast.Local;
import polyglot.ast.Receiver;
import polyglot.ast.Term;
import polyglot.ast.TypeNode;
import polyglot.ast.Unary;
import polyglot.ast.Variable;
import polyglot.ext.x10.ast.Existential;
import polyglot.ext.x10.ast.Here;
import polyglot.ext.x10.ast.ParExpr;
import polyglot.ext.x10.ast.RectRegionMaker;
import polyglot.ext.x10.ast.RegionMaker;
import polyglot.ext.x10.ast.X10Special;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.frontend.Globals;
import polyglot.types.LocalDef;
import polyglot.types.LocalInstance;
import polyglot.types.SemanticException;
import polyglot.types.TypeSystem;

/**
 * Translate from a ConstExr to a constraint term that can be serialized.
 * @author vj
 *
 */
public class TypeTranslator {
    X10TypeSystem xts;
    
    public TypeTranslator(X10TypeSystem ts) {
        this.xts = ts;
    }

    protected C_UnaryTerm trans(Unary t, Map<LocalDef,C_EQV> eqvs) throws SemanticException {
        return new C_UnaryTerm_c(t.operator().toString(), trans(t.expr()), t.type());
    }
    protected C_Field trans(Field t, Map<LocalDef,C_EQV> eqvs) throws SemanticException {
        C_Field_c n = new C_Field_c(t, (C_Var) trans(t.target(), eqvs));
        return n;
    }
    protected C_Special trans(X10Special t, Map<LocalDef,C_EQV> eqvs)throws SemanticException {
        C_Special_c n = new C_Special_c(t);
        return n;
    }
    protected C_Local trans(LocalInstance t, Map<LocalDef,C_EQV> eqvs)throws SemanticException {
    	C_EQV e = eqvs.get(t.def());
    	if (e != null)
    		return e;
        C_Local_c n = new C_Local_c(t.def());
        return n;
    }
    protected C_Type trans(TypeNode t, Map<LocalDef,C_EQV> eqvs) throws SemanticException {
        return new C_Type_c(t);
    }
    protected C_Lit trans(Lit t, Map<LocalDef,C_EQV> eqvs) throws SemanticException {
        return new C_Lit_c(t.constantValue(), t.type());
    }

    protected C_BinaryTerm trans(Binary t, Map<LocalDef,C_EQV> eqvs) throws SemanticException {
        String op = t.operator().toString();
        Expr left = t.left();
        Expr right = t.right();
        return new C_BinaryTerm_c(op, trans(left, eqvs), trans(right, eqvs), t.type());
    }
    protected C_Var trans(Variable term, Map<LocalDef,C_EQV> eqvs) throws SemanticException {
        if (term instanceof Field) return trans((Field) term, eqvs);
        if (term instanceof X10Special) return trans((X10Special) term, eqvs);
        if (term instanceof Local) {
            LocalInstance li = ((Local) term).localInstance();
            return trans(li, eqvs);
        }

        throw new SemanticException("Cannot translate term |" + term + "| into a constraint."
                                    + "It must be a field, special or local.");
    }

    public C_Term trans(Expr e) throws SemanticException {
    	return trans(e, Collections.EMPTY_MAP);
    }
    
    public C_Term trans(Receiver e) throws SemanticException {
    	return trans(e, Collections.EMPTY_MAP);
    }
    
    public C_Term trans(Expr e, Map<LocalDef,C_EQV> eqvs) throws SemanticException {
        return trans((Receiver) e, eqvs);
    }
    
    public C_Term trans(Receiver term, Map<LocalDef,C_EQV> eqvs) throws SemanticException {
            // Report.report(1, "TypeTranslator: translating Receiver " + term);
            if (term == null) return null;
            if (term instanceof Expr) {
                Expr e = (Expr) term;
                if (e.isConstant()) {
                    return new C_Lit_c(e.constantValue(), e.type());
                }
            }
            if (term instanceof Here) return ((X10TypeSystem) term.type().typeSystem()).here(); // RMF 2/8/2007
            if (term instanceof Variable) return trans((Variable) term, eqvs);
            if (term instanceof X10Special) return trans((X10Special) term, eqvs);
            if (term instanceof Unary) {
                    Unary u = (Unary) term;
                    Expr t2 = u.expr();
                    TypeSystem ts = t2.type().typeSystem();
                    Unary.Operator op = u.operator();
                    if (op.equals(Unary.POS))
                            return trans(t2, eqvs);
                    return trans((Unary) term, eqvs);
            }
            if (term instanceof Call) {
            	Call r = (Call) term;
            	List<C_Term> ts = new ArrayList<C_Term>();
            	for (Expr e : r.arguments()) {
            		ts.add(trans(e, eqvs));
            	}
            	return new C_Call_c(trans(r.target(), eqvs), ts, r.methodInstance());
            }
            if (term instanceof Binary) return trans((Binary) term, eqvs);
            if (term instanceof ParExpr) return trans(((ParExpr) term).expr(), eqvs);
            if (term instanceof TypeNode) return trans((TypeNode) term, eqvs);
            
            throw new SemanticException("Cannot translate |" + term + "|(" + term.getClass().getName()+")" +
                            " to a term.");
    }

    
    /**
     * Translate an expression into a constraint, throwing SemanticExceptions 
     * if this is not possible.
     * This must be called after type-checking of Expr.
     * @param formals TODO
     * @param term
     * @param c
     * @return
     * @throws SemanticException
     */
    public Constraint constraint(List<Formal> formals, Expr term, Constraint c) throws SemanticException {
            if (term == null)
                return c;
            
            X10TypeSystem ts = (X10TypeSystem) term.type().typeSystem();
                    
            if (! ts.typeEquals(term.type(),ts.Boolean())) 
                    throw new SemanticException("Cannot build constraint from expression |" + term 
                                    + "| of type " 
                                    + term.type()+ " (not boolean).");

            Map<LocalDef,C_EQV> eqvs = new HashMap<LocalDef,C_EQV>();
            
            for (Iterator i = formals.iterator(); i.hasNext(); ) {
            	Formal f = (Formal) i.next();
            	C_EQV eqv = c.genEQV(f.type().type(), false);
            	eqv.localDef().setName(f.localDef().name());
            	eqvs.put(f.localDef(), eqv);
            }
            
            C_Term t = trans(term, eqvs);
            boolean found = false;
            
            for (ConstraintSystem sys : ts.constraintSystems()) {
                try {
                    SimpleConstraint sc = sys.constraintForTerm(t);
                    c.addIn(sc);
                    found = true;
                }
                catch (SemanticException e) {
                }
            }
            
            if (! found) {
                throw new SemanticException("Cannot build constraint from expression |" + term + "|.");
            }
            
            System.out.println("translation of |" + term + "| = " + c);
            
            Constraint_c c_c = (Constraint_c) c;
            c_c.propagateEqualities();
            
            return c;
    }

    public Constraint constraint(List<Formal> formals, Expr e) throws SemanticException {
            //Report.report(1, "TypeTranslator: translating to constraint " + e);
            Constraint c = new Constraint_c(xts);
            return constraint(formals, e, c);
    }
    
    public static C_Term translate(Expr r, X10TypeSystem xts) throws SemanticException {
            return xts.typeTranslator().trans(r);
    }
    public static boolean isPureTerm(Term t) {
        if (t instanceof Expr) {
            try {
                translate((Expr) t, (X10TypeSystem) Globals.TS());
                return true;
            }
            catch (SemanticException e) {
            }
        }
        return false;
    }
}
