/**
 * 
 */
package polyglot.ext.x10.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import polyglot.ext.x10.types.constr.Constraint;
import polyglot.main.Report;
import polyglot.types.ClassType;
import polyglot.types.ConstructorInstance_c;
import polyglot.types.Flags;
import polyglot.types.TypeSystem;
import polyglot.types.TypeSystem_c;
import polyglot.util.Position;

/**
 * An X10ConstructorInstance_c varies from a ConstructorInstance_c only in that it
 * maintains a returnType. If an explicit returnType is not declared in the constructor
 * then the returnType is simply a noClause variant of the container.
 * @author vj
 *
 */
public class X10ConstructorInstance_c extends ConstructorInstance_c implements
		X10ConstructorInstance {

	protected List<X10ClassType> annotations;
	
	public List<X10ClassType> annotations() {
		if (annotations == null) return Collections.EMPTY_LIST;
		return Collections.<X10ClassType>unmodifiableList(annotations);
	}
	
	public void setAnnotations(List<X10ClassType> annotations) {
		this.annotations = new ArrayList<X10ClassType>(annotations);
	}
	public X10TypeObject annotations(List<X10ClassType> annotations) {
		X10ReferenceType_c n = (X10ReferenceType_c) copy();
		n.setAnnotations(annotations);
		return n;
	}
	public X10ClassType annotationNamed(String name) {
		for (Iterator<X10ClassType> i = annotations.iterator(); i.hasNext(); ) {
			X10ClassType ct = i.next();
			if (ct.fullName().equals(name)) {
				return ct;
			}
		}
		return null;
	}
	
	X10Type returnType;
	/**
	 * Needed for deserialization.
	 */
	public X10ConstructorInstance_c() {
		super();
	}
	
	public X10ConstructorInstance_c(TypeSystem ts, Position pos,
			ClassType container, Flags flags,  
			List formalTypes, List excTypes) {
		super(ts, pos, container, flags, formalTypes, excTypes);
		this.returnType =  ((X10ParsedClassType) container).makeNoClauseVariant();
	}
	/**
	 * @param ts
	 * @param pos
	 * @param container
	 * @param flags
	 * @param formalTypes
	 * @param excTypes
	 */
	public X10ConstructorInstance_c(TypeSystem ts, Position pos,
			ClassType container, Flags flags, X10Type returnType, 
			List formalTypes, List excTypes) {
		super(ts, pos, container, flags, formalTypes, excTypes);
		this.returnType = returnType;
	}
	
	/* (non-Javadoc)
	 * @see polyglot.ext.x10.types.X10ConstructorInstance#depClause()
	 */
	public Constraint constraint() { return returnType.realClause(); }
	public void setReturnType(X10Type r) { 
		this.returnType = r; 
		
		}
	public X10Type returnType() { return this.returnType;}
	protected Constraint supClause;
	public Constraint supClause() { return supClause; }
	public void setSupClause(Constraint s) { 
		this.supClause = s; 
	}
	
	public boolean callValidImplNoClauses(List argTypes) {
		return X10MethodInstance_c.callValidImplNoClauses(this, argTypes);
	}
	@Override
	public boolean callValidImpl(final List argTypes) {
		return X10MethodInstance_c.callValidImpl(this, argTypes);
	}
	  public String signature() {
	        return ((returnType != null) ? returnType.toString() : container.toString())
	        + "(" + X10TypeSystem_c.listToString(formalTypes) + ")";
	    }
}
