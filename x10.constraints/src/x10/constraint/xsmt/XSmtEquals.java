package x10.constraint.xsmt;

import java.util.ArrayList;
import java.util.Arrays;


import x10.constraint.XEquals;
import x10.constraint.xsmt.SmtUtil.XSmtKind;

public class XSmtEquals extends XSmtFormula<String> implements XEquals {
	private static final long serialVersionUID = 8068637764379300401L;
	private static final String name = "==";
	
	
	public XSmtEquals(XSmtTerm t1, XSmtTerm t2) {
		super(name, true, new ArrayList<XSmtTerm>(Arrays.asList(t1, t2)));
		assert t1 != null && t2!= null;
		// Because we cannot always know the type of CSelf we use
		// equalities to infer the type of the self variables
//		SmtType type1 = t1.getType();
//		SmtType type2 = t2.getType(); 
//
//		if ((type1 == SmtType.USort() || type1 == null) && type2 != SmtType.USort())
//			t1.setType(type2);
//
//		if (type1 != SmtType.USort() && (type2 == SmtType.USort() || type2 == null))
//			t2.setType(type1);
//		
//		if (t1.getType() != t2.getType()) {
//			throw new UnsupportedOperationException("Cannot compare terms of different types: " + t1.getType() +" and " + t2.getType()); 
//		} 
	}
		
	@Override
	public XSmtKind getKind() {
		return XSmtKind.EQ; 
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
