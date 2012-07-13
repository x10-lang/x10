package x10.constraint.xsmt;

import java.util.ArrayList;
import java.util.Arrays;


import x10.constraint.XDisEquals;
import x10.constraint.xsmt.SmtUtil.XSmtKind;

public class XSmtDisEquals extends XSmtFormula<String> implements XDisEquals {
	private static final long serialVersionUID = 8068637764379300401L;
	private static final String name = "!=";
	
	
	public XSmtDisEquals(XSmtTerm t1, XSmtTerm t2) {
		super(name, true, new ArrayList<XSmtTerm>(Arrays.asList(t1, t2)));
		assert t1 != null && t2!= null;
		// Because we cannot always know the type of CSelf we use
		// equalities to infer the type of the self variables
		SmtType type1 = t1.getType();
		SmtType type2 = t2.getType(); 
		if (type1 == SmtType.USort() && type2 != SmtType.USort())
			t1.setType(type2);

		if (type1 != SmtType.USort() && type2 == SmtType.USort())
			t2.setType(type1);
	
	}
		
	@Override
	public XSmtKind getKind() {
		return XSmtKind.NEQ; 
	}
	
	@Override
	public SmtType getType() {
		return SmtType.BoolType();
	}
	
	@Override
	public String asExprOperator() {
		return asExprName; 
	}
	
	@Override
	public String toSmt2() {
		StringBuilder sb = new StringBuilder();
		sb.append("(not (= ");
		sb.append(get(0));
		sb.append(" ");
		sb.append(get(1)); 
		sb.append("))"); 

		return sb.toString();
	}

}
