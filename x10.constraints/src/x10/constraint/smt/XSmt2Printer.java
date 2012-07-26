package x10.constraint.smt;

import java.io.Writer;
import java.util.Map;
import java.util.Set;

import x10.constraint.XTerm;
import x10.constraint.XType;

public class XSmt2Printer extends XPrinter {
	Map<XTerm<? extends XType>, ? extends XType> varDecl; 
	Set<? extends XType> sortDecl;
	
	public XSmt2Printer(Writer w) {
		super(w);
	}

	@Override
	public <T extends XType> void out(XSmtTerm<T> term) {
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
