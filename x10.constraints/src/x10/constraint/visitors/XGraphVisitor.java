package x10.constraint.visitors;

import java.util.HashMap;
import java.util.Map;
import x10.constraint.XField;
import x10.constraint.xnative.XNativeField;
import x10.constraint.xnative.XNativeTerm;
import x10.constraint.XTerm;
import x10.constraint.XTerm.TermVisitor;
import x10.constraint.XVar;

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
public abstract class XGraphVisitor {
    Map<XTerm, XTerm> eqvVarRep; 

    protected void addVarRep(XTerm eqv, XTerm rep) {
        if (eqvVarRep == null) eqvVarRep = new HashMap<XTerm, XTerm>();
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
    protected XTerm varRep(XTerm eqv) {
        if (eqvVarRep == null) return null;
        XTerm result = eqvVarRep.get(eqv);
        if (result == null) return null;
        while (result.hasEQV()) {
            XTerm temp = eqvVarRep.get(result);
            if (temp == null) return result;
            result = temp;
        }
        return result;
    }
    /** Get the nf for eqv, given the information
     * in the hash table. The normal form for e.f1...fn is x.f1...fn 
     * if e is bound to x in the table.
     */
    protected XTerm nf(XTerm eqv) {
        XTerm z = varRep(eqv);
        if (z != null) return z;

        if (eqv instanceof XField<?> ) {
            XField<?> t = (XField<?>) eqv;
            XTerm rt = t.receiver();
            XTerm tz =nf((XTerm)rt);
            if (tz == null) return null;
            return (XTerm)t.copyReceiver((XVar) tz);
        }
        return null;
    }
    boolean hideEQV;
    boolean hideFake;
    protected XGraphVisitor(boolean hideEQV, boolean hideFake) {
        this.hideEQV=hideEQV;
        this.hideFake=hideFake;
    }

    /**
     * Visiting the graph encounters a formula t.  
     * Process this information.
     * Return false if the visit should be aborted.
     * @param t -- the formula encountered.
     * @return false -- the visit should be terminated.
     */
    protected abstract boolean visitAtomicFormula(XTerm t);
    public boolean rawVisitAtomicFormula(XTerm t) {
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
    protected abstract boolean visitEquals(XTerm t1, XTerm t2);

    protected void addEQVBinding(final XTerm t1, final XTerm t2) {
        if (eqvVarRep!=null) {
            TermVisitor tv = new TermVisitor() {
                public XTerm visit(XTerm t) {return t.equals(t1) ? t2: null;}
            };
            for ( Map.Entry<XTerm,XTerm> t: eqvVarRep.entrySet()) {
                XTerm src = t.getKey();
                XTerm dest = t.getValue();
                XTerm tp = (XTerm)src.accept(tv);
                if ((! tp.hasEQV()) && (! dest.hasEQV())) visitEquals(tp, dest);   
            }
        }
        addVarRep(t1, t2);
    }
    public boolean rawVisitEquals(XTerm t1, XTerm t2) {
        //assert t1 != null && t2 != null;
        if (hideEQV) {
            if (t1.hasEQV()) {
                XTerm t1b = nf(t1);
                if (t1b != null) t1=t1b;
            }
            if (t2.hasEQV()) {
                XTerm t2b = nf(t2);
                if (t2b != null) t2 = t2b;
            }
            if (t1.hasEQV()) {addEQVBinding(t1, t2);return true;}
            if (t2.hasEQV()) {addEQVBinding(t2, t1);return true;}
        }
        if (hideFake && t1 instanceof XNativeField && ((XNativeField<?>) t1).isHidden()) return true;
        if (hideFake && t2 instanceof XNativeField && ((XNativeField<?>) t2).isHidden()) return true;
        return visitEquals(t1, t2);
    }

    /**
     * Visiting the graph encounters t1 != t2. 
     * Process this information.
     * Return false if the visit should be aborted.
     * @param t -- the formula encountered.
     * @return false -- the visit should be terminated.
     */
    protected abstract boolean visitDisEquals(XTerm t1, XTerm t2);

    public boolean rawVisitDisEquals(XTerm t1, XTerm t2) {
        assert t1 != null && t2 != null;
        if (hideEQV) {
            if (t1.hasEQV()) {
                XTerm t1b = nf(t1);
                if (t1b != null) {
                    t1=t1b;
                }
            }
            if (t2.hasEQV()) {
                XTerm t2b = nf(t2);
                if (t2b != null) t2 = t2b;
            }
            if (t1.hasEQV()) {
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
            if (t2.hasEQV()) {
                //	addVarRep(t2, t1);
                return true;
            }
        }
        if (hideFake && t1 instanceof XNativeField && ((XNativeField<?>) t1).isHidden()) return true;
        if (hideFake && t2 instanceof XNativeField && ((XNativeField<?>) t2).isHidden()) return true;
        return visitDisEquals(t1, t2);
    }

}
