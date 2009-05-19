/**
 * 
 */
package x10.effects.constraints;

import java.util.Set;
import java.util.TreeSet;

import x10.constraint.XConstraint;
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
		readSet = new TreeSet<Locs>();
		writeSet = new TreeSet<Locs>();
		atomicIncSet = new TreeSet<Locs>();
	}
	
	public Effect_c clone() {
		try {
		Effect_c result = (Effect_c) super.clone();
		result.isFun=isFun;
		result.readSet = new TreeSet<Locs>();
		result.readSet.addAll(readSet());
		
		result.writeSet = new TreeSet<Locs>();
		result.writeSet.addAll(writeSet());
		
		result.atomicIncSet = new TreeSet<Locs>();
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

	/* (non-Javadoc)
	 * @see x10.effects.constraints.Effect#commutesWith(x10.effects.constraints.Effect, x10.constraint.XConstraint)
	 */
	public boolean commutesWith(Effect e, XConstraint c) {
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
	}

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
		// Right now just deal with the simple case in which x does not occur in 
		// The right thing to do is
		/* XVar x1 = new XVar(), x2= new XVar();
		 * c.addNotEquals(x1, x2);
		 * 
		 */
		XLocal x1 = (XLocal) x.clone(), x2 = (XLocal) x.clone();
		Effect e1 = substitute( x1, x), e2 = substitute(x2, x);
		XConstraint c2 = c.copy();
		// TODO: ADd this. c2.addNotBinding(x1, x2);
		boolean result = e1.commutesWith(e2,  c2);
		return result;
		
	}

	
	public Effect substitute(XTerm t, XRoot r) {
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
		Effect_c result = new Effect_c(isFun());
		result.readSet().remove(x);
		result.writeSet().remove(x);
		result.atomicIncSet().remove(x);
		return result;
	}
	/* (non-Javadoc)
	 * @see x10.effects.constraints.Effect#exists(x10.constraint.XVar)
	 */
	public Effect exists(XLocal x, XTerm t) {
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
		Effect_c result = clone();
		result.isFun = Effects.FUN;
		return result;
	}

	/* (non-Javadoc)
	 * @see x10.effects.constraints.Effect#makeParFun()
	 */
	public Effect makeParFun() {
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
		readSet.add(t);
	}
	public void addWrite(Locs t) {
		writeSet.add(t);
	}
	public void addAtomicInc(Locs t) {
		atomicIncSet.add(t);
	}

	public Effect union(Effect e) {
		Effect_c result = clone();
		result.readSet.addAll(e.readSet());
		result.writeSet.addAll(e.writeSet());
		result.atomicIncSet.addAll(e.atomicIncSet());
		return result;
	}

    @Override
    public String toString() {
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
}
