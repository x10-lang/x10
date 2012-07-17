package x10.constraint.xsmt;

import x10.constraint.xsmt.SmtUtil.XSmtKind;

public interface SmtTerm {
	
	/**
	 * Return the kind associated with the given XSmtTerm 
	 * @return
	 */
	public XSmtKind getKind(); 
	
	public SmtType getType(); 
	
	public String toSmt2(); 
	
	public int hashCode(); 
	
	public boolean equals(Object obj);
}
