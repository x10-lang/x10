package x10.effects.constraints;

import x10.constraint.XName;
import x10.constraint.XConstraint;


public class FieldLocs_c extends Locs_c implements FieldLocs {

	ObjLocs obj;
	XName fieldName;
	public FieldLocs_c(ObjLocs o, XName f) {
		this.obj=o;
		this.fieldName=f;
	}
	
	public ObjLocs obj() { return obj;}
	public XName field() { return fieldName;}
	
	
	public boolean disjointFrom(Locs other, XConstraint c){
		if (! (other instanceof FieldLocs_c)) return true;
		FieldLocs_c o = (FieldLocs_c) other;
		if (! obj.disjointFrom(o.obj(), c)) {
			return ! fieldName.equals(o.field());
		}
		return true;
	}

	@Override
	public String toString() {
	    return obj.toString() + "." + fieldName.toString();
	}
}
