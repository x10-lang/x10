/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/**
 * 
 */
package polyglot.ext.x10.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import polyglot.ext.x10.ExtensionInfo.X10Scheduler;
import polyglot.ext.x10.types.constr.C_Local_c;
import polyglot.ext.x10.types.constr.Constraint;
import polyglot.ext.x10.types.constr.Constraint_c;
import polyglot.ext.x10.types.constr.Failure;
import polyglot.frontend.MissingDependencyException;
import polyglot.types.LocalInstance_c;
import polyglot.types.Flags;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;

/**
 * @author vj
 *
 */
public class X10LocalInstance_c extends LocalInstance_c implements X10LocalInstance {

	protected List<X10ClassType> annotations;
	
	public List<X10ClassType> annotations() {
		if (this != orig()) {
			return ((X10LocalInstance) orig()).annotations();
		}
		if (annotations == null)
			return Collections.EMPTY_LIST;
//		if (! annotationsSet())
//			throw new MissingDependencyException(((X10Scheduler) typeSystem().extensionInfo().scheduler()).TypeObjectAnnotationsPropagated(this), false);
		return Collections.<X10ClassType>unmodifiableList(annotations);
	}
	public boolean annotationsSet() {
		if (this != orig()) {
			return ((X10LocalInstance) orig()).annotationsSet();
		}
		return true || annotations != null; }
	public void setAnnotations(List<X10ClassType> annotations) {
		if (annotations == null) annotations = Collections.EMPTY_LIST;
		this.annotations = new ArrayList<X10ClassType>(annotations);
	}
	public X10TypeObject annotations(List<X10ClassType> annotations) {
		X10TypeObject n = (X10TypeObject) copy();
		n.setAnnotations(annotations);
		return n;
	}
	public List<X10ClassType> annotationMatching(Type t) {
		List<X10ClassType> l = new ArrayList<X10ClassType>();
		for (Iterator<X10ClassType> i = annotations().iterator(); i.hasNext(); ) {
			X10ClassType ct = i.next();
			if (ct.isSubtype(t)) {
				l.add(ct);
			}
		}
		return l;
	}
	
	/**
	 * 
	 */
	public X10LocalInstance_c() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param ts
	 * @param pos
	 * @param flags
	 * @param type
	 * @param name
	 */
	public X10LocalInstance_c(TypeSystem ts, Position pos, Flags flags,
			Type type, String name) {
		super(ts, pos, flags, type, name);
	}
	
	protected int positionInArgList = -1;
	public int positionInArgList() {
		return positionInArgList;
	}
	public void setPositionInArgList(int i) {
		positionInArgList = i;
	}
	public boolean setSelfClauseIfFinal() {
		// If the local variable is final, replace T by T(:self==t), 
		// do this even if depclause==null
		boolean changed = false;
		if ( flags().isFinal()) {
			X10Type t = (X10Type) type();
			try {
				Constraint c = Constraint_c.addSelfBinding(C_Local_c.makeSelfVar(this), t.depClause(), (X10TypeSystem) ts);
				X10Type newType = t.makeVariant(c,t.typeParameters());
				setType(newType);
			}
			catch (Failure f) {
				throw new InternalCompilerError("Could not add self binding.", f);
			}
			changed = true;
		}
		return changed;
		
	}

}
