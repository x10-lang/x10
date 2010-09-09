package x10.effects.constraints;

import x10.constraint.XConstraint;
import x10.constraint.XFailure;
import x10.constraint.XName;
import x10.constraint.XRoot;
import x10.constraint.XTerm;


public class FieldLocs_c extends RigidTerm_c implements FieldLocs {

	XName fieldName;
	public FieldLocs_c(XTerm o, XName f) {
		super(o);
		this.fieldName=f;
	}
	
	public XName field() { return fieldName;}
	
	public Locs substitute(XTerm t, XRoot s) {
		XTerm old = designator();
		XTerm result = old.clone().subst(t, s);
		return (result.equals(old)) ? this
				: Effects.makeFieldLocs(result, fieldName);
	}
	
	public XTerm obj() { return designator();}
	
	public boolean disjointFrom(Locs other, XConstraint c){
		try {
			if (other instanceof ObjLocs) {
				ObjLocs o = (ObjLocs) other;
				return (c.disEntails(obj(), o.designator()));
			}
			if  (other instanceof FieldLocs) {
				FieldLocs o = (FieldLocs) other;
				if (c.disEntails(obj(), o.obj()))
					return true;
				return ! fieldName.equals(o.field());
			}
		} catch (XFailure f) {
			//hmm this should be an error.
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
	    return obj().toString() + "." + fieldName.toString();
	}
	@Override
	public int hashCode() {
		return designator().hashCode() + field().hashCode();
	}
	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		if (! (other instanceof FieldLocs_c)) return false;
		FieldLocs_c o = (FieldLocs_c) other;
		return designator().equals(o.designator())
		&& field().equals(o.field());
	}
}
