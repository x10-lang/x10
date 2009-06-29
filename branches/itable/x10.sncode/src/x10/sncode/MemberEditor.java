package x10.sncode;

import java.util.List;

import x10.sncode.Constraint.Term;

public abstract class MemberEditor extends Container {

	Type container;
	String name;
	Term thisVar;

	public MemberEditor() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String s) {
		name = s;
	}
	
	public void setFlags(List<String> flags) {
		for (String f : flags)
			attributes.add(new Tree.Leaf("Flag", f));
	}

	public Term getThisVar() {
		return thisVar;
	}

	public void setThisVar(Term t) {
		thisVar = t;
	}

}