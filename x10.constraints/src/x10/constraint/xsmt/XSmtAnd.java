package x10.constraint.xsmt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import x10.constraint.XAnd;
import x10.constraint.xsmt.SmtUtil.XSmtKind;

public class XSmtAnd extends XSmtFormula<String> implements XAnd{
	private static final long serialVersionUID = -915541248274600494L;
	private static final String name = "&&";
	
	
	public XSmtAnd(List<XSmtTerm> args) {
		super(name, true, args);
	}
	
	public XSmtAnd(XSmtTerm... terms) {
		this(new ArrayList<XSmtTerm>(Arrays.asList(terms)));
	}
		
	@Override
	public XSmtKind getKind() {
		return XSmtKind.AND; 
	}
	
	@Override
	public SmtType getType() {
		return SmtType.BoolType();
	}	
	
	@Override
	public String asExprOperator() {
		return asExprName; 
	}
}
