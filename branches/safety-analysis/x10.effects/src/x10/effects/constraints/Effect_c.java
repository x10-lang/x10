/**
 * 
 */
package x10.effects.constraints;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import x10.constraint.XConstraint;
import x10.constraint.XFailure;
import x10.constraint.XLocal;
import x10.constraint.XRoot;
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

    public Set<Pair<Effect, Effect>> interferenceWith(Effect e) {
        return interferenceWith(e, XTerms.makeTrueConstraint());
    }

    private Set<Pair<Locs,Locs>> disjoint(Set<Locs> a, Set<Locs> b, XConstraint c) {
	    Set<Pair<Locs,Locs>> result= null;
		for (Locs al : a) {
			for (Locs bl: b) {
				if (! al.disjointFrom(bl, c)) {
				    if (result == null) {
				        result= new HashSet<Pair<Locs,Locs>>();
				    }
                    result.add(new Pair<Locs,Locs>(al,bl));
				}
			}
		}
		return result;
	}

	private interface EffectPairPopulator {
	    void populateEffect(Pair<Locs,Locs> locPair, Effect e1, Effect e2);
	}

	private static EffectPairPopulator RWEffectPairPopulator = new EffectPairPopulator() {
        public void populateEffect(Pair<Locs, Locs> p, Effect e1, Effect e2) {
            e1.addRead(p.fst);
            e2.addWrite(p.snd);
        }
	};

    private static EffectPairPopulator RAEffectPairPopulator = new EffectPairPopulator() {
        public void populateEffect(Pair<Locs, Locs> p, Effect e1, Effect e2) {
            e1.addRead(p.fst);
            e2.addAtomicInc(p.snd);
        }
    };

    private static EffectPairPopulator WREffectPairPopulator = new EffectPairPopulator() {
        public void populateEffect(Pair<Locs, Locs> p, Effect e1, Effect e2) {
            e1.addWrite(p.fst);
            e2.addRead(p.snd);
        }
    };

    private static EffectPairPopulator WWEffectPairPopulator = new EffectPairPopulator() {
        public void populateEffect(Pair<Locs, Locs> p, Effect e1, Effect e2) {
            e1.addWrite(p.fst);
            e2.addWrite(p.snd);
        }
    };

    private static EffectPairPopulator WAEffectPairPopulator = new EffectPairPopulator() {
        public void populateEffect(Pair<Locs, Locs> p, Effect e1, Effect e2) {
            e1.addWrite(p.fst);
            e2.addAtomicInc(p.snd);
        }
    };

    private static EffectPairPopulator AREffectPairPopulator = new EffectPairPopulator() {
        public void populateEffect(Pair<Locs, Locs> p, Effect e1, Effect e2) {
            e1.addAtomicInc(p.fst);
            e2.addRead(p.snd);
        }
    };

    private static EffectPairPopulator AWEffectPairPopulator = new EffectPairPopulator() {
        public void populateEffect(Pair<Locs, Locs> p, Effect e1, Effect e2) {
            e1.addAtomicInc(p.fst);
            e2.addWrite(p.snd);
        }
    };

    private Set<Pair<Effect,Effect>> nonDisjointEffects(Set<Pair<Locs,Locs>> pairs, EffectPairPopulator pop) {
        Set<Pair<Effect,Effect>> result= null;

        if (pairs != null && !pairs.isEmpty()) {
            result= new HashSet<Pair<Effect,Effect>>();
            for(Pair<Locs,Locs> p: pairs) {
                Effect e1= Effects.makeEffect(true);
                Effect e2= Effects.makeEffect(true);

                pop.populateEffect(p, e1, e2);
                result.add(new Pair<Effect,Effect>(e1, e2));
            }
        }
        return result;
    }

    private Set<Pair<Effect,Effect>> combine(Set<Pair<Effect,Effect>> pairs1, Set<Pair<Effect,Effect>> pairs2) {
        if (pairs1 == null) {
            return pairs2;
        }
        if (pairs2 == null) {
            return pairs1;
        }
        pairs1.addAll(pairs2);
        return pairs1;
    }

    public boolean commutesWith(Effect e, XConstraint c) {
        if (this == Effects.BOTTOM_EFFECT || e == Effects.BOTTOM_EFFECT)
            return false;
        final Set<Locs> r = readSet(), w=writeSet(), a=atomicIncSet();
        final Set<Locs> er = e.readSet(), ew=e.writeSet(), ea=e.atomicIncSet();
        return disjoint(r, ew, c) == null &&
               disjoint(r, ea, c) == null &&
               disjoint(w, er, c) == null &&
               disjoint(w, ew, c) == null &&
               disjoint(w, ea, c) == null &&
               disjoint(a, er, c) == null &&
               disjoint(a, ew, c) == null;
//          && disjoint(a, ea,c); // RMF 9/11/09 - ok for atomically-updated locs to overlap
    }

    /* (non-Javadoc)
	 * @see x10.effects.constraints.Effect#commutesWith(x10.effects.constraints.Effect, x10.constraint.XConstraint)
	 */
	public Set<Pair<Effect,Effect>> interferenceWith(Effect e, XConstraint c) {
		if (this == Effects.BOTTOM_EFFECT || e == Effects.BOTTOM_EFFECT)
			return null;
		final Set<Locs> r = readSet(), w=writeSet(), a=atomicIncSet();
		final Set<Locs> er = e.readSet(), ew=e.writeSet(), ea=e.atomicIncSet();

		return combine(nonDisjointEffects(disjoint(r, ew, c), RWEffectPairPopulator),
		        combine(nonDisjointEffects(disjoint(r, ea, c), RAEffectPairPopulator),
		         combine(nonDisjointEffects(disjoint(w, er, c), WREffectPairPopulator),
		          combine(nonDisjointEffects(disjoint(w, ew, c), WWEffectPairPopulator),
		           combine(nonDisjointEffects(disjoint(w, ea, c), WAEffectPairPopulator),
		            combine(nonDisjointEffects(disjoint(a, er, c), AREffectPairPopulator),
		                    nonDisjointEffects(disjoint(a, ew, c), AWEffectPairPopulator)))))));
//          && disjoint(a, ea,c); // RMF 9/11/09 - ok for atomically-updated locs to overlap
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

	public Pair<XLocal,Effect> freshSubst(XLocal x) {
	    XLocal x1 = XTerms.makeLocal(XTerms.makeFreshName());
	    Effect e1 = substitute(x1, x);

	    return new Pair<XLocal,Effect>(x1, e1);
	}

	private XConstraint addDisjointness(XConstraint c, XLocal x1, XLocal x2) {
        XConstraint c2 = c.copy();
        try {
            c2.addDisBinding(x1, x2);
        } catch (XFailure z) {
            // should never happen
        }
	    return c2;
	}

	/* (non-Javadoc)
	 * @see x10.effects.constraints.Effect#commutesWithForall(x10.constraint.XVar, x10.constraint.XConstraint)
	 */
	public boolean commutesWithForall(XLocal x, XConstraint c) {
		if (this == Effects.BOTTOM_EFFECT)
			return false;

		Pair<XLocal,Effect> p1 = freshSubst(x), p2 = freshSubst(x);
		XConstraint c2 = addDisjointness(c, p1.fst, p2.fst);
		boolean result = p1.snd.commutesWith(p2.snd,  c2);

		return result;
	}

	private static final Set<Pair<Effect,Effect>> EMPTY_INTERFERENCE= new HashSet<Pair<Effect,Effect>>();

	public Set<Pair<Effect,Effect>> interferenceWithForall(XLocal x) {
	    return interferenceWithForall(x, XTerms.makeTrueConstraint());
	}

	public Set<Pair<Effect,Effect>> interferenceWithForall(XLocal x, XConstraint c) {
        if (this == Effects.BOTTOM_EFFECT)
            return EMPTY_INTERFERENCE;

        Pair<XLocal,Effect> p1 = freshSubst(x), p2 = freshSubst(x);
        XConstraint c2 = addDisjointness(c, p1.fst, p2.fst);

        return p1.snd.interferenceWith(p2.snd, c2);
	}

	public boolean commutesWithForall(List<XLocal> xs) {
        return commutesWithForall(xs, XTerms.makeTrueConstraint());
	}

	private XConstraint addDisjointness(XConstraint c, List<Pair<XLocal,XLocal>> pairs) {
        XConstraint c2 = c.copy();
        try {
            for(Pair<XLocal,XLocal> varPair: pairs) {
                XLocal x1= varPair.fst;
                XLocal x2= varPair.snd;
                c2.addDisBinding(x1, x2);
            }
        } catch (XFailure z) {
            // should never happen
        }
        return c2;
	}

	private Pair<Effect,Effect> freshSubst(List<XLocal> vars, List<Pair<XLocal,XLocal>> freshVars) {
	    Effect e1 = this;
	    Effect e2 = this;

        for(XLocal v: vars) {
            Pair<XLocal, Effect> p1 = e1.freshSubst(v);
            Pair<XLocal, Effect> p2 = e2.freshSubst(v);

            freshVars.add(new Pair<XLocal, XLocal>(p1.fst, p2.fst));
            e1 = p1.snd;
            e2 = p2.snd;
        }
        return new Pair<Effect,Effect>(e1, e2);
	}

	public boolean commutesWithForall(List<XLocal> xs, XConstraint c) {
        if (this == Effects.BOTTOM_EFFECT)
            return false;

        List<Pair<XLocal, XLocal>> freshVars= new ArrayList<Pair<XLocal,XLocal>>(xs.size());
        Pair<Effect,Effect> p = freshSubst(xs, freshVars);
        XConstraint c2= addDisjointness(c, freshVars);

        boolean result = p.fst.commutesWith(p.snd,  c2);
        return result;
    }

	public Set<Pair<Effect,Effect>> interferenceWithForall(List<XLocal> xs) {
	    return interferenceWithForall(xs, XTerms.makeTrueConstraint());
	}

	public Set<Pair<Effect,Effect>> interferenceWithForall(List<XLocal> xs, XConstraint c) {
        if (this == Effects.BOTTOM_EFFECT)
            return EMPTY_INTERFERENCE;

        List<Pair<XLocal, XLocal>> freshVars= new ArrayList<Pair<XLocal,XLocal>>(xs.size());
        Pair<Effect,Effect> p = freshSubst(xs, freshVars);
        XConstraint c2= addDisjointness(c, freshVars);

        return p.fst.interferenceWith(p.snd, c2);
	}

	public Effect substitute(XTerm t, XRoot r) {
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
			if (!commutesWith(e, c))
				return Effects.BOTTOM_EFFECT;
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
		if (e == Effects.BOTTOM_EFFECT)
		    return e;
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
