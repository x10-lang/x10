package x10.constraint.bapat;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import stp.Expr;
import stp.Type;
import stp.VC;

public class NamedVar extends IntInformationVar {
	
	private String name;
	private String type;
	private Map<String, NamedVar> fields;
	private String uniqueName;
	private NamedVar parent;
	private Expr stpExpr;
	private IntOption intValue;
	
	public NamedVar(String name, String type, String uniqueName, NamedVar parent) {
		super();
		this.name = name;
		this.type = type;
		fields = new HashMap<String, NamedVar>();
		stpExpr = null;
		this.parent = parent;
		this.uniqueName = uniqueName;
		intValue = IntOption.noIntValue();
	}
	
	public NamedVar(String name, String uniqueName, NamedVar parent) {
		this(name, null, uniqueName, parent);
	}
	
	public String getName() {
		return name;
	}
	
	public String getType() {
		return type;
	}
	
	public String getUniqueName() {
		return uniqueName;
	}
	
	public void setType(String type) {
		//assert this.type == null || this.type.equals(type);
		this.type = type;
	}
	
	public boolean hasField(String f) {
		return getField(f) != null;
	}
	
	public NamedVar getField(String f) {
		return getField(f, new HashSet<NamedVar>());
	}
	private NamedVar getField(String f, Set<NamedVar> visited) {
		if (visited.contains(this))
			return null;
		visited.add(this);
		if (fields.containsKey(f))
			return fields.get(f);
		for (Constraint c: constraints) {
			if (c instanceof EqualityConstraint) {
				Var o = c.getOtherVar(this);
				if (o instanceof NamedVar) {
					NamedVar other = (NamedVar) o;
					NamedVar fieldOption = other.getField(f, visited);
					if (fieldOption != null)
						return fieldOption;
				}
			}
		}
		return null;
	}
	
	public Var getParent() {
		return parent;
	}
	
	public void addField(NamedVar v) {
		if (fields.containsKey(v.getName())) {
			NamedVar existing = fields.get(v.getName());
			assert existing.name.equals(v.name) && (v.type == null || v.type.equals(existing.type));
			existing.fields.putAll(v.fields);
		} else
			fields.put(v.getName(), v);
	}

	@Override
	public Expr toSTPExpr(VC vc) {
		if (stpExpr == null) {
			Type stpType = getSTPType(vc);
			stpExpr = vc.varExpr(uniqueName, stpType);
		}
		return stpExpr;
	}
	
	/**
	 * See comment at the top of BooleanConstant for
	 * the reason why we rerpesent booleans as bitvectors.
	 */
	private Type getSTPType(VC vc) {
		return vc.bvType(getNumBitsInSTPExpr());
	}
	
	private int getNumBitsInSTPExpr() {
		if (type != null && type.equals("x10.lang.Boolean"))
			return 1;
		for (Constraint c: constraints)
			if (c instanceof EqualityConstraint && c.getOtherVar(this) instanceof BooleanConstant)
				return 1;
		return BAPATSolver.NUM_BITS;
	}

	@Override
	public Expr getAssertions(VC vc) {
		int numBitsInSTPExpr = getNumBitsInSTPExpr();
		if (numBitsInSTPExpr == 1)  // boolean constant, this is unneeded and actually wrong
			return null;
		return vc.sbvGeExpr(toSTPExpr(vc), vc.bvConstExprFromInt(numBitsInSTPExpr, 0));
	}
	
	@Override
	public boolean hasIntInformation() {
		return intValue.hasIntValue();
	}

	@Override
	public int getIntInformation() {
		assert intValue.hasIntValue();
		return intValue.getValue();
	}

	@Override
	public void setIntInformation(int n) {
		if (intValue.hasIntValue())
			assert intValue.getValue() == n;
		else
			intValue = IntOption.intValue(n);
	}
	
	/* hashCode and equals */
	
	@Override
	public int hashCode() {
		assert uniqueName != null;
		return uniqueName.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NamedVar other = (NamedVar) obj;
		if (uniqueName == null) {
			if (other.uniqueName != null)
				return false;
		} else if (!uniqueName.equals(other.uniqueName))
			return false;
		return true;
	}
	
	/* toString methods for debugging */

	@Override
	public String toLongString() {
		String str = "(*" + name + " (" + uniqueName + ")";
		if (type != null)
			str += " (type: " + type + ") ";
		str += " with int value " + intValue; 
		str += " with fields: " + fields;
		str += " with constraints " + constraints;
		/*NamedVar ultimateAncestor = getUltimateAncestor();
		if (ultimateAncestor != this)
			str += " (ultimate ancestor: " + ultimateAncestor.toLongString() + ")";*/
		str += "*)";
		return str;
	}
	
	/*private NamedVar getUltimateAncestor() {
		NamedVar cur = this;
		while (true) {
			NamedVar curParent = cur.parent;
			if (curParent == null)
				break;
			else
				cur = curParent;
		}
		return cur;
	}*/
	
	@Override
	public String toString() {
		return name + " (" + uniqueName + ")";
	}

}
