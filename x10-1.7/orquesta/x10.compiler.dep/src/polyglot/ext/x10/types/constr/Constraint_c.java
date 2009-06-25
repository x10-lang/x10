/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.types.constr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import polyglot.ast.Binary;
import polyglot.ast.Expr;
import polyglot.ast.Field;
import polyglot.ast.Lit;
import polyglot.ast.Local;
import polyglot.ast.Receiver;
import polyglot.ast.Unary;
import polyglot.ext.x10.ast.Here;
import polyglot.ext.x10.ast.X10Special;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.main.Report;
import polyglot.types.Flags;
import polyglot.types.LocalDef;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeObject_c;
import polyglot.types.Types;
import polyglot.util.FilteringList;
import polyglot.util.InternalCompilerError;
import polyglot.util.Pair;
import polyglot.util.Position;


/**
 * A representation of constraints of the form X1=t1 && ... Xk == tk.
 * Note that there is no unification, only checking. So it is possible
 * to represent such a constraint directly as a mapping from Xi to ti.
 * The constraint is implemented as a Map from variables to terms.
 * 
 * @author vj
 *
 */
public class Constraint_c extends TypeObject_c implements Constraint, Cloneable {

    public X10TypeSystem typeSystem() {
        return (X10TypeSystem) super.typeSystem();
    }
    

    public Constraint unaryOp(Unary.Operator op) {
        try {
            Constraint_c n = new Constraint_c(typeSystem());
            List<SimpleConstraint> conjuncts = new ArrayList<SimpleConstraint>();
            for (SimpleConstraint sc : conjuncts()) {
                SimpleConstraint c2 = sc.unaryOp(op);
                if (c2 != null) {
                    n.addIn(c2);
                }
            }
            n.propagateEqualities();
            return n;
        }
        catch (Failure f) {
            return null;
        }
    }
    
    public Constraint binaryOp(Binary.Operator op, Constraint c) {
        try {
            Constraint_c n = new Constraint_c(typeSystem());
            List<SimpleConstraint> conjuncts = new ArrayList<SimpleConstraint>();
            for (SimpleConstraint sc : conjuncts()) {
                SimpleConstraint c2 = sc.binaryOp(op, c, this);
                if (c2 != null) {
                    n.addIn(c2);
                }
            }
            n.propagateEqualities();
            return n;
        }
        catch (Failure f) {
            return null;
        }
    }
    
    protected List<SimpleConstraint> conjuncts;
    
    public List<SimpleConstraint> conjuncts() {
        return Collections.unmodifiableList(conjuncts);
    }
	
	// For representation of T(:self == o), selfBinding is o.
	protected C_Var selfVar;

	public Set<C_EQV> eqvs() {
	    LinkedHashSet<C_EQV> eqvs = new LinkedHashSet<C_EQV>();
	    for (C_Var v : vars()) {
	        if (v instanceof C_EQV) {
	            eqvs.add((C_EQV) v);
	        }
	    }
	    return eqvs;
	}
	
	protected final X10TypeSystem typeSystem;

	public Constraint_c(X10TypeSystem xts) {
		super(xts, null);
		typeSystem= xts;
		this.conjuncts = new ArrayList<SimpleConstraint>();
		this.consistent = true;
	}
	
	private Constraint_c(X10TypeSystem xts, SimpleConstraint n) throws Failure {
	    this(xts);
	    addIn(n, true);
	}

	public Constraint_c(X10TypeSystem xts, Constraint n) throws Failure {
	    this(xts);
	    addIn(n);
	}


    /** Copy this constraint logically -- that is create a new constraint which contains
	 * the same equalities (as any) as the current one.
	 * */
	
	public Constraint copy() {
		try {
			return copyInto(new Constraint_c(typeSystem));
		}
		catch (Failure f) {
			throw new InternalCompilerError("Copying constraint made it inconsistent.", f);
		}
	}

	/** 
	 * Clone this constraint, physically. Note that this is the usual default shallow copy.
	 */
	public Constraint clone() {
	    try {
	        return (Constraint) super.clone();
	    }
	    catch (CloneNotSupportedException e) {
	        throw new InternalCompilerError("Error while cloning.", e);
	    }
	}
	
	/**
	 * Return the result of copying this into c. Assume that c will be 
	 * the depclause of the same base type as this, hence it is ok to 
	 * copy self-clauses as is. 
	 * @param c
	 * @return
	 * @throws Failure 
	 */
	private Constraint copyInto(Constraint_c c) throws Failure {
		c.addIn(this);
		return c;
	}

	public void addIn(Constraint c) throws Failure {
	    if (c == null)
	        return;
	    
	    String old = toString();
	    
	    consistent &= c.consistent();

	    List<SimpleConstraint> cc = new ArrayList<SimpleConstraint>();
	    cc.addAll(c.conjuncts());
	    
	    // Create fresh eqvs.
	    for (C_EQV eqv : c.eqvs()) {
	        C_EQV eqv2 = this.genEQV(eqv.type(), eqv.isSelfVar());
	        for (ListIterator<SimpleConstraint> i = cc.listIterator(); i.hasNext(); ) {
	            SimpleConstraint sc = i.next();
	            i.set(sc.substitute(eqv2, eqv, true, this, new HashSet<C_Term>()));
	        }
	    }
	    
	    for (SimpleConstraint sc : cc) {
	        addIn(sc, false);
	    }
	    
	    C_Var v = c.selfVar();
	    
	    if (v != null) {
	    	this.addSelfBinding(v);
	    }
	    
	    propagateEqualities();

//	    System.out.println("adding in " + c + " to " + old + " => " + this);
	    
	    return;
	}


	void propagateEqualities() throws Failure {
		List<SimpleConstraint> cc = conjuncts();
		
		if (cc.size() == 0) return;
		if (cc.size() == 1) return;
		if (cc.size() == 1 && selfVar() != null) return;

		// Do Nelson-Oppen (slowly).
		boolean changed = true;

		while (changed) {
			changed = false;

			// Check if any of the conjuncts entail new bindings.  If so add them to the other conjuncts.
			// Lather. Rinse. Repeat.
			for (SimpleConstraint sc : cc) {
				Constraint_c y = new Constraint_c(typeSystem);
				y.addIn(sc);

				Set<C_Var> vars = new HashSet<C_Var>();
				vars.addAll(sc.vars());

				Set<Pair<C_Var,C_Var>> possibleBindings = new HashSet<Pair<C_Var,C_Var>>();

				// Collect all pairs of vars where the types are compatible.
				List<C_Var> worklist = new ArrayList<C_Var>(vars);
				for (int i = 0; i < worklist.size(); i++) {
					for (int j = i+1; j < worklist.size(); j++) {
						C_Var t1 = worklist.get(i);
						C_Var t2 = worklist.get(j);
						if (! (t1.type() instanceof X10Type) || ! (t2.type() instanceof X10Type)) {
							possibleBindings.add(new Pair<C_Var, C_Var>(t1, t2));
						}
						else {
							X10Type type1 = (X10Type) t1.type();
							X10Type type2 = (X10Type) t2.type();
							if (type1.rootType().isCastValid(type2.rootType())) {
								possibleBindings.add(new Pair<C_Var, C_Var>(t1, t2));
							}
						}
					}
				}

				Set<Pair<C_Var,C_Var>> newBindings = new HashSet<Pair<C_Var,C_Var>>();
				newBindings.addAll(possibleBindings);

				for (Iterator<Pair<C_Var,C_Var>> i = newBindings.iterator(); i.hasNext(); ) {
					Pair<C_Var, C_Var> e = i.next();
					try {
						Constraint_c x = new Constraint_c(typeSystem);
						C_Var t1 = e.part1();
						C_Var t2 = e.part2();
						x.addBinding(t1, t2);
						if (! x.entailedBy(y)) {
							i.remove();
						}
					}
					catch (Failure f) {
						i.remove();
					}
				}

				// Add in any bindings from conjunct sc into the other conjuncts.
				for (Iterator<Pair<C_Var,C_Var>> i = newBindings.iterator(); i.hasNext(); ) {
					Pair<C_Var, C_Var> e = i.next();
					C_Var t1 = e.part1();
					C_Var t2 = e.part2();
//					if (selfVar() == null) {
//						if (t1 instanceof C_Special && ((C_Special) t1).kind() == C_Special.SELF) {
//							setSelfVar(t2);
//						}
//						else if (t2 instanceof C_Special && ((C_Special) t2).kind() == C_Special.SELF) {
//							setSelfVar(t1);
//						}
//					}
					for (SimpleConstraint sc2 : cc) {
	    				if (sc2 == sc) continue;
	    				sc2.addIn(sc2.constraintSystem().binding(t1, t2));
	    				changed = true;
	    			}
	    		}
	    	}
	    }
	}
	
	/**
	 * Is the constraint valid? i.e. vacuous.
         */
	public boolean valid() {
	    return conjuncts.isEmpty();
	}
	
	public void addIn(SimpleConstraint c) throws Failure {
	    addIn(c, false);
	}
	
	public void addIn(SimpleConstraint c, boolean intern) throws Failure {
	    if (c == null)
	    	return;
	    
	    if (c.valid()) {
	    	return;
	    }
	    
	    if (intern) {
	        // Create fresh eqvs.
	        for (C_Var v : c.vars()) {
	            if (v instanceof C_EQV) {
	                C_EQV eqv = (C_EQV) v;
	                C_EQV eqv2 = this.genEQV(eqv.type(), eqv.isSelfVar());
	                c = c.substitute(eqv2, eqv, true, this, new HashSet<C_Term>());
	            }
	        }
	    }
	    else {
	        for (C_Var v : c.vars()) {
	            if (v instanceof C_EQV) {
	                C_EQV eqv = (C_EQV) v;
	                System.out.println("not interning + " + eqv + " in " + c);
	            }
	        }
	    }
        
	    for (SimpleConstraint sc : conjuncts) {
	        if (sc.constraintSystem() == c.constraintSystem()) {
	            sc.addIn(c);
	            return;
	        }
	    }
	    
	    conjuncts.add(c);
	}

        /**
         * Add the constraint self==val.
         */
        public static Constraint addSelfBinding(C_Var val, Constraint c, X10TypeSystem xts) throws Failure  {
                c = (c==null) ? new Constraint_c(xts) : c;
                c.addSelfBinding(val);
                return c;
        }
        
        public C_Var selfVar() {
            return selfVar;
        }
        
        public void setSelfVar(C_Var var) {
                selfVar = var;
        }
        
        /**
         * Add the constraint self==val.
         */
	public void addSelfBinding(C_Var val) throws Failure {
	    C_Var selfVar = selfVar();
	    
            if (selfVar == null) {
                setSelfVar(val);
            }
            else {
                addBinding(selfVar, val);
            }
	}

        public Set<C_Var> vars() {
            LinkedHashSet<C_Var> vars = new LinkedHashSet<C_Var>();
            for (SimpleConstraint sc : conjuncts()) {
                vars.addAll(sc.vars());
            }
            return vars;
        }

	public Constraint addBindings(HashMap<C_Var,C_Var> result) throws Failure {
		if (result==null || result.isEmpty())
			return this;
		for (Iterator<Map.Entry<C_Var,C_Var>> it = result.entrySet().iterator(); it.hasNext();) {
			Map.Entry<C_Var,C_Var> entry =  it.next();
			C_Var t1 = entry.getKey();
			C_Var t2 =  entry.getValue();
	                addBinding(t1, t2);

		}
		return this;
	}


	/**
	 * Add t1=t2 to the constraint, unless it is inconsistent. 
	 * @param var -- t1
	 * @param val -- t2
	 */
    public Constraint addBinding(C_Var t1, C_Var t2) throws Failure {
        for (ConstraintSystem sys : typeSystem.constraintSystems()) {
            SimpleConstraint sc = sys.binding(t1, t2);
            addIn(sc, false);
        }

        if (selfVar() == null) {
        	if (t1 instanceof C_Special && ((C_Special) t1).kind() == C_Special.SELF) {
        		setSelfVar(t2);
        	}
        	else if (t2 instanceof C_Special && ((C_Special) t2).kind() == C_Special.SELF) {
        		setSelfVar(t1);
        	}
        }
        
        checkConsistency();
        
//        propagateEqualities();
        
        return this;
    }

	private void checkConsistency() throws Failure {
	    if (! consistent) {
	        throw new Failure("Constraint " + this + " is inconsistent.");
	    }
	}

	boolean consistent;
	
	/**
	 * Is the constraint consistent? i.e. X=s and X=t have not been added to it,
	 * where s and t are not equal.
	 */
	public boolean consistent() {return consistent;}
	public void setInconsistent() {consistent = false;}
	
	/**
	 * Add a boolean term.
	 * @param term
	 */
	public void addTerm(C_Term term) throws Failure {
	    for (ConstraintSystem sys : typeSystem.constraintSystems()) {
	        try {
	            SimpleConstraint sc = sys.constraintForTerm(term);
	            addIn(sc);
	        }
	        catch (SemanticException e) {
	            consistent = false;
	        }
	    }
	    
	    propagateEqualities();
	    checkConsistency();
	}


	
	/**
	 * If other is not inconsistent, and this is consistent,
	 * checks that each binding X=t in other also exists in this.
	 * @param other
	 * @return
	 */
	public boolean entails(Constraint other) {
		if (! consistent()) return true;
		if (other == null || other.valid()) return true;
		boolean result = other.entailedBy(this);
		if ( Report.should_report(Report.types,1))
			Report.report(1, this + (result? " entails " : " does not entail ") + other);
		return  result;
	}
	
	public boolean entailedBy(Constraint other) {
		if ((! other.consistent()) || valid() )
			return true;
		
		for (SimpleConstraint c : conjuncts()) {
		    if (! c.entailedBy(other, this)) {
		        System.out.println("FAILURE: " + other + " =/=> " + c);
		        System.out.println("   THUS: " + other + " =/=> " + this);
		        return false;
		    }
		    System.out.println("SUCCESS: " + other + " ===> " + c);
		}
		System.out.println("SUCCESS: " + other + " ===> " + this);
		return true;
	}
	
	protected boolean checkSelfEntails(C_Var var, C_Term val) {
		if (selfVar == null) return false;
		C_Var var1 = (C_Var) var.substitute(selfVar, C_Special.Self);
		boolean result = var1.equals(val);
		return result;
	}
	public boolean equiv(Constraint other) {
		//Report.report(1, "Constraint: " + this + " equiv " + other + "? " );
		boolean result = entails(other);
		if (result) result = (other==null)? valid() : other.entails(this);
		//Report.report(1, "Constraint: " + this + " equiv " + other + "? " + result);
		return result;
	}
	
	 public C_Var find(String varName) {
	     if (! consistent) return null;
	     for (SimpleConstraint c : conjuncts()) {
	         C_Var v = c.find(varName);
	         if (v != null) 
	             return v;
	     }
	     return null;
	 }
	
	public String toString() { 
		StringBuffer sb = new StringBuffer();
		sb.append("(:");
		String str = "exists ";
		for (C_EQV x : eqvs()) {
		    sb.append(str);
		    str = ", ";
		    sb.append(x);
		}
		if (! str.equals("exists "))
		    str = ": ";
		else
		    str = "";
		if (selfVar != null) {
		    sb.append(str);
		    sb.append("self == ");
		    sb.append(selfVar);
		    str = " in ";
		}
		for (SimpleConstraint sc : conjuncts()) {
		    sb.append(str);
		    str = ", ";
		    sb.append(sc.constraintSystem());
		    sb.append(": ");
		    sb.append(sc.toString());
		}
		sb.append(")");
		String s = sb.toString();
		return s;
	}

	protected static int eqvCount;
	public C_EQV genEQV(Type type, boolean isSelfVar) {
		return genEQV(type, isSelfVar, true);
	}
	public C_EQV genEQV(Type type, boolean isSelfVar, boolean hidden) {
		String name = "_" + eqvCount++;
		X10TypeSystem xts = (X10TypeSystem) type.typeSystem();
		LocalDef li = xts.localDef(Position.COMPILER_GENERATED, Flags.FINAL, Types.ref(type), name);
		C_EQV result = new C_EQV_c(li, isSelfVar, hidden);
		return result;
	}
	public Constraint substitute(C_Var y, C_Root x) throws Failure {
		return substitute(y, x, true, new HashSet<C_Term>());
	}
	public Constraint substitute(C_Var y, C_Root x, boolean propagate, HashSet<C_Term> visited) throws Failure {
	        Constraint c = copy();
	        c.applySubstitution(y, x, propagate, visited);
	        return c;
	}
	public Constraint substitute(C_Var[] ys, C_Root[] xs) throws Failure {
		return substitute(ys, xs, true, new HashSet<C_Term>());
	}
	public Constraint substitute(C_Var[] ys, C_Root[] xs, boolean propagate, HashSet<C_Term> visited) throws Failure {
		assert xs.length == ys.length;
		Constraint result = this;
		for (int i=0; i < xs.length; i++) {
			C_Root x = xs[i];
			C_Var y = ys[i];
			if (result==this)
			    result = clone();
			result.applySubstitution(y, x, propagate, visited);
		}
		return result;
	}
	public void applySubstitution(C_Var y, C_Root x, boolean propagate, HashSet<C_Term> visited) throws Failure {
	    for (ListIterator<SimpleConstraint> i = conjuncts.listIterator(); i.hasNext(); ) {
	        SimpleConstraint sc = i.next();
                SimpleConstraint sc2 = sc.substitute(y, x, propagate, this, visited);
                i.set(sc2);
            }
            
            // If substituting for self, we don't want to unify y with self, but we do want a binding between y and the (old) selfVar.
            if (x instanceof C_Special && ((C_Special) x).kind() == C_Special.SELF) {
                C_Var selfVar = selfVar();
                if (selfVar != null) {
                    setSelfVar(null);
                    addBinding(selfVar, y);
                }
            }
	}
	
	public boolean entailsType(C_Var y) {
		assert(y != null);
		
		X10Type yType = (X10Type) y.type();
		Constraint yTypeRC = X10TypeMixin.realClause(yType);
		return entails(yTypeRC);
	}
	public boolean hasVar(C_Root v) {
	    return vars().contains(v);
	}
	 public C_Var selfVar(Expr arg) {
		 return selfVar(arg, true);
	 }
	 public C_Var selfVar(Expr arg, boolean hidden)  {
	    	C_Var result = null;
	    	if (rigid(arg)) {
	    		try {
	    		    result = (C_Var) typeSystem.typeTranslator().trans(arg);
	    		}
	    		catch (SemanticException z) {
	    			throw new InternalCompilerError("Unexpected error " + z 
	    					+ " while trying to convert the rigid term " + arg 
	    					+ " to a C_Var.");
	    		}
	    		return result;
	    	}
	    	if (result == null) {
	    		result = genEQV(arg.type(),true, hidden);
	    	}
	    	return result;
	    }
	    public static boolean rigid(Receiver arg) {
	    	if (arg instanceof X10Type) {
	    		return true;
	    	}
	    	if (arg instanceof Expr) {
	    		return rigid((Expr) arg);
	    	}
	    	assert false;
	    	return false;
	    }
	    public static boolean rigid(Expr arg) {
	    	boolean result = false;
	    	if (arg instanceof Field) {
	    		Field f = (Field) arg;
	    		return result = rigid(f.target()) && f.flags().isFinal();
	    	}
	    	if (arg instanceof Local) {
	    		return result = ((Local) arg).flags().isFinal();
	    	}
	    	if (arg instanceof Lit || arg instanceof X10Special || arg instanceof Here) {
	    		return result = true;
	    	}
	    
	    	return result;
	    }


        public void applySubstitution(HashMap<C_Root, C_Var> bindings, boolean propagate) throws Failure {
            for (Map.Entry<C_Root, C_Var> e : bindings.entrySet()) {
                applySubstitution(e.getValue(), e.getKey(), propagate, new HashSet<C_Term>());
            }
        }

        public Constraint substitute(HashMap<C_Root, C_Var> bindings) throws Failure {
            return substitute(bindings, true);
        }

        public Constraint substitute(HashMap<C_Root, C_Var> bindings, boolean propagate) throws Failure {
            Constraint result = clone();
            result.applySubstitution(bindings, propagate);
            return result;
        }
        
        public void internRecursively(C_Var var) throws Failure {
            for (SimpleConstraint sc : conjuncts()) {
                sc.internRecursively(var, this);
            }
        }
}

