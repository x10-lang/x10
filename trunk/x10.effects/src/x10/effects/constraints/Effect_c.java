/**
 * 
 */
package x10.effects.constraints;

import java.util.Set;
import java.util.TreeSet;

import x10.constraint.XConstraint;
import x10.constraint.XFailure;
import x10.constraint.XTerm;
import x10.constraint.XTerms;
import x10.constraint.XVar;

/**
 * (Skeleton as of now)
 * @author vj 05/13/09
 *
 */
public class Effect_c implements Effect {

	protected Set<XTerm> readSet, writeSet, atomicIncSet;
	protected boolean isFun;

	public Effect_c(boolean b) {
		isFun = b;
		readSet = new TreeSet<XTerm>();
		writeSet = new TreeSet<XTerm>();
		atomicIncSet = new TreeSet<XTerm>();
	}
	
	public Effect_c clone() {
		try {
		Effect_c result = (Effect_c) super.clone();
		result.isFun=isFun;
		result.readSet = new TreeSet<XTerm>();
		result.readSet.addAll(readSet());
		
		result.writeSet = new TreeSet<XTerm>();
		result.writeSet.addAll(writeSet());
		
		result.atomicIncSet = new TreeSet<XTerm>();
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
	public Set<XTerm> atomicIncSet() {
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
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see x10.effects.constraints.Effect#commutesWithForall(x10.constraint.XVar)
	 */
	public boolean commutesWithForall(XVar x) {
		return commutesWithForall(x, XTerms.makeTrueConstraint());
	}

	/* (non-Javadoc)
	 * @see x10.effects.constraints.Effect#commutesWithForall(x10.constraint.XVar, x10.constraint.XConstraint)
	 */
	public boolean commutesWithForall(XVar x, XConstraint c) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see x10.effects.constraints.Effect#exists(x10.constraint.XVar)
	 */
	public Effect exists(XVar x) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see x10.effects.constraints.Effect#followedBy(x10.effects.constraints.Effect, x10.constraint.XConstraint)
	 */
	public Effect followedBy(Effect e, XConstraint c) throws XFailure {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see x10.effects.constraints.Effect#forall(x10.constraint.XVar)
	 */
	public Effect forall(XVar x) {
		// TODO Auto-generated method stub
		return null;
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
		result.isFun = Factory.FUN;
		return result;
	}

	/* (non-Javadoc)
	 * @see x10.effects.constraints.Effect#makeParFun()
	 */
	public Effect makeParFun() {
		Effect_c result = clone();
		result.isFun = Factory.PAR_FUN;
		return result;
	}

	/* (non-Javadoc)
	 * @see x10.effects.constraints.Effect#readSet()
	 */
	public Set<XTerm> readSet() {
		return readSet;
	}

	/* (non-Javadoc)
	 * @see x10.effects.constraints.Effect#writeSet()
	 */
	public Set<XTerm> writeSet() {
		return writeSet;
	}
	public void addRead(XTerm t) {
		readSet.add(t);
	}
	public void addWrite(XTerm t) {
		writeSet.add(t);
	}
	public void addAtomicInc(XTerm t) {
		atomicIncSet.add(t);
	}

}
