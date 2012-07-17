package x10.constraint.xsmt;

import x10.constraint.xsmt.SmtUtil.XSmtKind;

public class SmtFuncSymbol implements SmtVariable {
	private SmtType type; 
	private String name;
	
	public SmtFuncSymbol(String name, SmtType type) {
		this.name = name;
		this.type = type; 
	}
	
	public SmtFuncSymbol(String name, SmtBaseType arg, SmtBaseType ret) {
		this.name = name;
		this.type = SmtType.makeType(arg, ret); 
	}
	
	@Override
	public XSmtKind getKind() {
		return XSmtKind.VARIABLE;
	}
	
	@Override
	public SmtType getType() {
		return type; 
	}
	
	@Override
	public String getName() {
		return name; 
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SmtFuncSymbol other = (SmtFuncSymbol) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toSmt2() {
		return getName(); 
	}
	
}
