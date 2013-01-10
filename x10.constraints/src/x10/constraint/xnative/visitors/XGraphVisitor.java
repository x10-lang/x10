package x10.constraint.xnative.visitors;

import java.util.HashMap;
import java.util.Map;
import x10.constraint.XField;
import x10.constraint.XTypeSystem;
import x10.constraint.xnative.XNativeConstraintSystem;
import x10.constraint.xnative.XNativeField;
import x10.constraint.xnative.XNativeTerm;
import x10.constraint.XTerm;
import x10.constraint.XTerm.TermVisitor;
import x10.constraint.XType;

/**
 * An XGraphVisitor may visit the graph underlying a representation of 
 * a constraint <tt>c</tt>. 
 * 
 * <p> The visitor will see the atomic formulas 
 * and the constraints <tt>s==t</tt> and <tt>s!=t</tt> stored in the graph in
 * some order. The set <tt>S</tt> of constraints seen will be sufficient to 
 * entail <tt>c</tt>. No guarantee is made that <tt>S</tt> is "minimal" in some
 * sense. 
 * 
 * <p>The visitor may return false from any visit
 * method to terminate further traversal of the graph.

 * @author vj
 *
 */
public abstract class XGraphVisitor<T extends XType> {
	
    Map<XNativeTerm<T>, XNativeTerm<T>> eqvVarRep; 
    final boolean hideEQV;
    final boolean hideFake;
	protected XNativeConstraintSystem<T> sys;
	protected XTypeSystem<T> ts;

    protected XGraphVisitor(XNativeConstraintSystem<T> sys, XTypeSystem<T> ts, boolean hideEQV, boolean hideFake) {
        this.hideEQV=hideEQV;
        this.hideFake=hideFake;
    	this.ts = ts;
    	this.sys = sys;
}

    protected void addVarRep(XNativeTerm<T> eqv, XNativeTerm<T> rep) {
        if (eqvVarRep == null) eqvVarRep = new HashMap<XNativeTerm<T>, XNativeTerm<T>>();
        if (eqvVarRep.get(eqv)==null) eqvVarRep.put(eqv, rep);
    }
    /**
     * Returns null unless eqvVarRep is set, and maps eqv to some value v.
     * In that case, returns the dereferenced version of v. That is,
     * a value v such that eqvVarRep(v)==null, or 
     * ! eqvVarRep(v).hasEQV().
     * @param eqv
     * @return
     */
    protected XNativeTerm<T> varRep(XNativeTerm <T>eqv) {
        if (eqvVarRep == null) return null;
        XNativeTerm<T> result = eqvVarRep.get(eqv);
        if (result == null) return null;
        while (sys.hasEQV(result)) {
            XNativeTerm<T> temp = eqvVarRep.get(result);
            if (temp == null) return result;
            result = temp;
        }
        return result;
    }
    /** Get the nf for eqv, given the information
     * in the hash table. The normal form for e.f1...fn is x.f1...fn 
     * if e is bound to x in the table.
     */
    @SuppressWarnings("unchecked")
	protected XNativeTerm<T> nf(XNativeTerm<T> eqv) {
        XNativeTerm<T> z = varRep(eqv);
        if (z != null) return z;

        if (eqv instanceof XField<?,?> ) {
			XNativeField<T,?> t = (XNativeField<T,?>) eqv;
            XNativeTerm<T> rt = t.receiver();
            XNativeTerm<T> tz = nf(rt);
            if (tz == null) return null;
            return t.copyReceiver(sys, tz);
        }
        return null;
    }
    
    /**
     * Visiting the graph encounters a formula t.  
     * Process this information.
     * Return false if the visit should be aborted.
     * @param t -- the formula encountered.
     * @return false -- the visit should be terminated.
     */
    protected abstract boolean visitAtomicFormula(XTerm<T> t);
    public boolean rawVisitAtomicFormula(XTerm<T> t) {
        return visitAtomicFormula(t);
    }

    /**
     * Visiting the graph encounters t1 == t2. 
     * Process this information.
     * Return false if the visit should be aborted.
     * @param t1 --  
     * @param t2 --  
     * @return false -- the visit should be terminated.
     */
    protected abstract boolean visitEquals(XTerm<T> t1, XTerm<T> t2);

    protected void addEQVBinding(final XNativeTerm<T> t1, final XNativeTerm<T> t2) {
        if (eqvVarRep!=null) {
            TermVisitor<T> tv = new TermVisitor<T>() {
                public XTerm<T> visit(XTerm<T> t) {return t.equals(t1) ? t2: null;}
            };
            for ( Map.Entry<XNativeTerm<T>,XNativeTerm<T>> t: eqvVarRep.entrySet()) {
            	XNativeTerm<T> src = t.getKey();
            	XNativeTerm<T> dest = t.getValue();
            	XNativeTerm<T> tp = src.accept(tv);
                if ((! sys.hasEQV(tp)) && (! sys.hasEQV(dest))) visitEquals(tp, dest);   
            }
        }
        addVarRep(t1, t2);
    }
    public boolean rawVisitEquals(XNativeTerm<T> t1, XNativeTerm<T> t2) {
        //assert t1 != null && t2 != null;
        if (hideEQV) {
            if (sys.hasEQV(t1)) {
            	XNativeTerm<T> t1b = nf(t1);
                if (t1b != null) t1=t1b;
            }
            if (sys.hasEQV(t2)) {
            	XNativeTerm<T> t2b = nf(t2);
                if (t2b != null) t2 = t2b;
            }
            if (sys.hasEQV(t1)) {addEQVBinding(t1, t2);return true;}
            if (sys.hasEQV(t2)) {addEQVBinding(t2, t1);return true;}
        }
        if (hideFake && t1 instanceof XNativeField && ((XNativeField<?,?>) t1).isHidden()) return true;
        if (hideFake && t2 instanceof XNativeField && ((XNativeField<?,?>) t2).isHidden()) return true;
        return visitEquals(t1, t2);
    }

    /**
     * Visiting the graph encounters t1 != t2. 
     * Process this information.
     * Return false if the visit should be aborted.
     * @param t -- the formula encountered.
     * @return false -- the visit should be terminated.
     */
    protected abstract boolean visitDisEquals(XTerm<T> t1, XTerm<T> t2);

    public boolean rawVisitDisEquals(XNativeTerm<T> t1, XNativeTerm<T> t2) {
        assert t1 != null && t2 != null;
        if (hideEQV) {
            if (sys.hasEQV(t1)) {
            	XNativeTerm<T> t1b = nf(t1);
                if (t1b != null) {
                    t1=t1b;
                }
            }
            if (sys.hasEQV(t2)) {
            	XNativeTerm<T> t2b = nf(t2);
                if (t2b != null) t2 = t2b;
            }
            if (sys.hasEQV(t1)) {
                // ugh. Of course, cannot add t1 --> t2 when we are processing t1 != t2!!!
                //	addVarRep(t1, t2);
                // Ignoring is ok. If we have a term s that is not EQV that is
                // bound to this, then s != t2 will be processed when we 
                // come through. Fortunately, != is not transitive so we do 
                // not have to worry about handling s != t, u != t where
                // t is an eqv, but s and u are not. No implicit constraint
                // can be deduced between s and u from the above.
                return true;
            }
            if (sys.hasEQV(t2)) {
                //	addVarRep(t2, t1);
                return true;
            }
        }
        if (hideFake && t1 instanceof XNativeField && ((XNativeField<?,?>) t1).isHidden()) return true;
        if (hideFake && t2 instanceof XNativeField && ((XNativeField<?,?>) t2).isHidden()) return true;
        return visitDisEquals(t1, t2);
    }

}
