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
	public String toSmt2() {
		return getName(); 
	}
	
}
