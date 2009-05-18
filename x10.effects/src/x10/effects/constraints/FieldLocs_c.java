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
		XTerm result = old.subst(t, s);
		return (result.equals(old)) ? this : Effects.makeFieldLocs(result, fieldName);
	}
	
	public XTerm obj() { return designator();}
	
	public boolean disjointFrom(Locs other, XConstraint c){
		try {
			if (other instanceof ObjLocs) {
				ObjLocs o = (ObjLocs) other;
				return (! c.entails(obj(), o.designator()));
			}
			if  (other instanceof FieldLocs) {
				FieldLocs o = (FieldLocs) other;

				if (c.entails(obj(), o.obj()))
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
}
