/**
 * 
 */
package x10.effects.constraints;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import x10.constraint.XConstraint;
import x10.constraint.XFailure;
import x10.constraint.XLocal;
import x10.constraint.XTerm;
import x10.constraint.XTerms;
import x10.constraint.XVar;

/**
 * (Skeleton as of now)
 * @author vj 05/13/09
 *
 */
public class Effect_c implements Effect {

	protected Set<Locs> readSet, writeSet, atomicIncSet;
	protected boolean isFun;

	public Effect_c(boolean b) {
		isFun = b;
		readSet = new HashSet<Locs>();
		writeSet = new HashSet<Locs>();
		atomicIncSet = new HashSet<Locs>();
	}
	
	public Effect_c clone() {
		if (this == Effects.BOTTOM_EFFECT)
			return this;
		try {
			Effect_c result = (Effect_c) super.clone();
			result.isFun=isFun;
			result.readSet = new HashSet<Locs>();
			result.readSet.addAll(readSet());

			result.writeSet = new HashSet<Locs>();
			result.writeSet.addAll(writeSet());

			result.atomicIncSet = new HashSet<Locs>();
			result.atomicIncSet.addAll(atomicIncSet());
			return result;

		} catch (CloneNotSupportedException z) {
			// not reachable
			return null;
		}

	}
	/* (non-Javadoc)
	 * @see x10.effects.constraints.Effect#atomicIncSet()
	 */
	public Set<Locs> atomicIncSet() {
		return atomicIncSet;
	}

	/* (non-Javadoc)
	 * @see x10.effects.constraints.Effect#commutesWith(x10.effects.constraints.Effect)
	 */
	public boolean commutesWith(Effect e) {
		return commutesWith(e, XTerms.makeTrueConstraint());
	}

	private boolean disjoint(Set<Locs> a, Set<Locs> b, XConstraint c) {
		for (Locs l : a)
			for (Locs m: b) {
				if (! l.disjointFrom(m, c))
					return false;
			}
		return true;
	}
	/* (non-Javadoc)
	 * @see x10.effects.constraints.Effect#commutesWith(x10.effects.constraints.Effect, x10.constraint.XConstraint)
	 */
	public boolean commutesWith(Effect e, XConstraint c) {
		if (e == Effects.BOTTOM_EFFECT)
			return false;
		final Set<Locs> r = readSet(), w=writeSet(), a=atomicIncSet();
		final Set<Locs> er = e.readSet(), ew=e.writeSet(), ea=e.atomicIncSet();
		return 
		disjoint(r,ew,c) 
		&& disjoint(r,ea,c) 
		&& disjoint(w, er,c)
		&& disjoint(w, ew,c)
		&& disjoint(w, ea,c)
		&& disjoint(a, er,c)
		&& disjoint(a, ew,c)
		&& disjoint(a, ea,c);
	}
/*
public boolean commutesWith(Effect e, XConstraint c) {
		if (e == Effects.BOTTOM_EFFECT)
			return false;
		for (Locs l : readSet()) {
			for (Locs m: e.writeSet()) {
				if (! l.disjointFrom(m, c))
					return false;
			}
			for (Locs m: e.atomicIncSet()) {
				if (! l.disjointFrom(m, c))
					return false;
			}
		}
		for (Locs l : writeSet()) {
			for (Locs m: e.writeSet()) {
				if (! l.disjointFrom(m, c))
					return false;
			}
			for (Locs m: e.atomicIncSet()) {
				if (! l.disjointFrom(m, c))
					return false;
			}
		}
		for (Locs l : atomicIncSet()) {
			for (Locs m: e.writeSet()) {
				if (! l.disjointFrom(m, c))
					return false;
			}
			for (Locs m: e.atomicIncSet()) {
				if (! l.disjointFrom(m, c))
					return false;
			}
		}
		
		for (Locs l : e.readSet()) {
			for (Locs m: writeSet()) {
				if (! l.disjointFrom(m, c))
					return false;
			}
			for (Locs m: atomicIncSet()) {
				if (! l.disjointFrom(m, c))
					return false;
			}
		}
		return true;
	}*/
	/* (non-Javadoc)
	 * @see x10.effects.constraints.Effect#commutesWithForall(x10.constraint.XVar)
	 */
	public boolean commutesWithForall(XLocal x) {
		return commutesWithForall(x, XTerms.makeTrueConstraint());
	}

	/* (non-Javadoc)
	 * @see x10.effects.constraints.Effect#commutesWithForall(x10.constraint.XVar, x10.constraint.XConstraint)
	 */
	public boolean commutesWithForall(XLocal x, XConstraint c) {
		if (this == Effects.BOTTOM_EFFECT)
			return false;
		XLocal x1 = XTerms.makeLocal(XTerms.makeFreshName());
		XLocal x2 = XTerms.makeLocal(XTerms.makeFreshName());
	
		Effect e1 = substitute( x1, x), e2 = substitute(x2, x);
		XConstraint c2 = c.copy();
		try {
			c2.addDisBinding(x1, x2);
		} catch (XFailure z) {
			// should never happen
		}

		boolean result = e1.commutesWith(e2,  c2);
		return result;

	}

	private static class Pair<T1,T2> {
	    public T1 fst;
	    public T2 snd;
	    public Pair(T1 t1, T2 t2) {
	        fst= t1;
	        snd= t2;
	    }
	}

	public boolean commutesWithForall(List<XLocal> xs) {
        return commutesWithForall(xs, XTerms.makeTrueConstraint());
	}

	public boolean commutesWithForall(List<XLocal> xs, XConstraint c) {
        if (this == Effects.BOTTOM_EFFECT)
            return false;
        Effect e1 = this;
        Effect e2 = this;
        List<Pair<XLocal, XLocal>> freshVars= new ArrayList<Pair<XLocal,XLocal>>(xs.size());
        for(XLocal x: xs) {
            XLocal x1= XTerms.makeLocal(XTerms.makeFreshName(x.toString()));
            XLocal x2= XTerms.makeLocal(XTerms.makeFreshName(x.toString()));

            freshVars.add(new Pair<XLocal, XLocal>(x1, x2));
            e1 = e1.substitute(x1, x);
            e2 = e2.substitute(x2, x);
        }
        XConstraint c2 = c.copy();
        try {
            for(Pair<XLocal,XLocal> freshVar: freshVars) {
                XLocal x1= freshVar.fst;
                XLocal x2= freshVar.snd;
                c2.addDisBinding(x1, x2);
            }
        } catch (XFailure z) {
            // should never happen
        }

        boolean result = e1.commutesWith(e2,  c2);
        return result;

    }
	
	public Effect substitute(XTerm t, XVar r) {
		if (this==Effects.BOTTOM_EFFECT)
			return this;
		Effect_c result = new Effect_c(isFun());
		for (Locs l : readSet()) {
			result.readSet().add(l.substitute(t,r));
		}
		for (Locs l : writeSet()) {
			result.writeSet().add(l.substitute(t,r));
		}
		for (Locs l : atomicIncSet()) {
			result.atomicIncSet().add(l.substitute(t,r));
		}
		return result;
	}
	public Effect exists(LocalLocs x) {
		if (this == Effects.BOTTOM_EFFECT)
			return this;
		Effect_c result = clone();
		result.readSet().remove(x);
		result.writeSet().remove(x);
		result.atomicIncSet().remove(x);
		return result;
	}
	/* (non-Javadoc)
	 * @see x10.effects.constraints.Effect#exists(x10.constraint.XVar)
	 */
	public Effect exists(XLocal x, XTerm t) {
		if (this == Effects.BOTTOM_EFFECT)
			return this;
		Effect_c result = new Effect_c(isFun());
		for (Locs l : readSet()) {
			if (l.equals(x)) 
				continue;
			result.readSet().add(l.substitute(t, x));
		}
		for (Locs l : writeSet()) {
			if (l.equals(x)) 
				continue;
			result.writeSet().add(l.substitute(t, x));
		}
		for (Locs l : atomicIncSet()) {
			if (l.equals(x)) 
				continue;
			result.atomicIncSet().add(l.substitute(t, x));
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see x10.effects.constraints.Effect#followedBy(x10.effects.constraints.Effect, x10.constraint.XConstraint)
	 */
	public Effect followedBy(Effect e, XConstraint c)  {
		if (! isFun()) {
			if (! commutesWith(e, c))
				return null;
		}
		return union(e);
	}

	static private void generalize(Set<Locs> s, XVar x) {
		for (Locs l : s) {
			if (l instanceof ArrayElementLocs) {
				ArrayElementLocs al = (ArrayElementLocs) l;
				ArrayLocs array = al.generalize(x);
				if (array != null) {
					s.remove(l);
					s.add(array);
				}
					
			}
		}
	}
	/* (non-Javadoc)
	 * @see x10.effects.constraints.Effect#forall(x10.constraint.XVar)
	 */
	public Effect forall(XLocal x) {
		if (this == Effects.BOTTOM_EFFECT)
			return this;
		Effect_c result = clone();
		generalize(result.readSet(), x);
		generalize(result.writeSet(), x);
		generalize(result.atomicIncSet(), x);
		return result;
	}

	/* (non-Javadoc)
	 * @see x10.effects.constraints.Effect#isFun()
	 */
	public boolean isFun() {
		return isFun;
	}

	/* (non-Javadoc)
	 * @see x10.effects.constraints.Effect#makeFun()
	 */
	public Effect makeFun() {
		if (this == Effects.BOTTOM_EFFECT)
			return this;
		Effect_c result = clone();
		result.isFun = Effects.FUN;
		return result;
	}

	/* (non-Javadoc)
	 * @see x10.effects.constraints.Effect#makeParFun()
	 */
	public Effect makeParFun() {
		if (this == Effects.BOTTOM_EFFECT)
			return this;
		Effect_c result = clone();
		result.isFun = Effects.PAR_FUN;
		return result;
	}

	/* (non-Javadoc)
	 * @see x10.effects.constraints.Effect#readSet()
	 */
	 public Set<Locs> readSet() {
		return readSet;
	}

	/* (non-Javadoc)
	 * @see x10.effects.constraints.Effect#writeSet()
	 */
	public Set<Locs> writeSet() {
		return writeSet;
	}
	 public void addRead(Locs t) {
		 if (this == Effects.BOTTOM_EFFECT) return;
			 readSet.add(t);
	}
	public void addWrite(Locs t) {
		if (this == Effects.BOTTOM_EFFECT) return;
		writeSet.add(t);
	}
	public void addAtomicInc(Locs t) {
		if (this == Effects.BOTTOM_EFFECT) return;
		atomicIncSet.add(t);
	}

	public Effect union(Effect e) {
		if (this == Effects.BOTTOM_EFFECT)
			return this;
		Effect_c result = clone();
		result.readSet.addAll(e.readSet());
		result.writeSet.addAll(e.writeSet());
		result.atomicIncSet.addAll(e.atomicIncSet());
		return result;
	}

    @Override
    public String toString() {
    	if (this == Effects.BOTTOM_EFFECT)
    		return "BOTTOM_EFFECT";
        StringBuilder sb= new StringBuilder();
        sb.append("{ r: ");
        sb.append(readSet.toString());
        sb.append(", w: ");
        sb.append(writeSet.toString());
        sb.append(", a: ");
        sb.append(atomicIncSet.toString());
        sb.append(" }");
        return sb.toString();
    }
    @Override
    public int hashCode() {
    	return (isFun()? 0: 1) + readSet().hashCode() + writeSet().hashCode()
    	+ atomicIncSet().hashCode();
    }
    @Override
    public boolean equals(Object other ) {
    	if (this == other) return true;
    	if (! (other instanceof Effect_c)) return false;
    	Effect_c o = (Effect_c) other;
    	return isFun() == o.isFun()
    	&& readSet().equals(o.readSet())
    	&& writeSet().equals(o.writeSet())
    	&& atomicIncSet().equals(o.atomicIncSet());
    	
    }
}
