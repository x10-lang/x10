package x10.constraint.xsmt;

import java.util.ArrayList;
import java.util.Arrays;

import x10.constraint.XNot;
import x10.constraint.xsmt.SmtUtil.XSmtKind;

public class XSmtNot extends XSmtFormula<String> implements XNot {
	private static final long serialVersionUID = 247584407495650549L;
	private static final String name = "!";
	
	public XSmtNot(XSmtTerm t) {
		super(name, true, new ArrayList<XSmtTerm>(Arrays.asList(t)));
	}
		
	@Override
	public XSmtKind getKind() {
		return XSmtKind.NOT; 
	}
	
	@Override
	public String asExprOperator() {
		return asExprName; 
	}
	
	@Override
	public SmtType getType() {
		return SmtType.BoolType();
	}	
}
