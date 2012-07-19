package x10.constraint.redesign.xsmt;

import java.io.Writer;
import java.util.Map;
import java.util.Set;

import x10.constraint.redesign.XTerm;
import x10.constraint.redesign.XType;

public class XSmt2Printer extends XPrinter {
	Map<XTerm<? extends XSmtType>, ? extends XSmtType> varDecl; 
	Set<? extends XSmtType> sortDecl;
	
	public XSmt2Printer(Writer w) {
		super(w);
	}

	@Override
	public <T extends XSmtType> void out(XSmtTerm<T> term) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dump() {
		// TODO Auto-generated method stub
	}

	
	@Override
	public String toString() {
		return writer.toString(); 
	}
	
	private <T extends XType> void declareVar(XTerm<T> var, T type) {
		// TODO
	}
	
	private <T extends XType> void declareSort(T type) {
		// TODO
	}

 
}
