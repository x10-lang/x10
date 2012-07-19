package x10.constraint.redesign.xsmt;

import java.io.Writer;

import x10.constraint.redesign.XType;

/**
 * Interface allowing for output in 
 * @author lshadare
 *
 */
public abstract class XPrinter {
	Writer writer; 
	
	public XPrinter(Writer w) {
		this.writer = w; 
	}

	public abstract <T extends XSmtType> void out(XSmtTerm<T> term);
	public abstract String toString(); 
	public abstract void dump(); 
}
