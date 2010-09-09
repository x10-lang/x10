/*
 * Created by vj on Dec 9, 2004
 *
 * 
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Call;
import polyglot.util.Enum;

/** An immuable representation of the X10 reduction call on an array. Such
 * a call has a target (an array), the name of the method being invoked, and
 * the list of arguments to the call. 
 * Translation of the syntax: reduce a.m(t1,...,tn) or scan a.m(t1,...tn).
 * @author vj Dec 9, 2004
 * 
 */
public interface ReductionCall extends Call {
	static class Kind extends Enum { 
		public Kind( String s) { super( s); }
	}
	static final Kind SCAN = new Kind("scan");
	static final Kind REDUCE = new Kind("reduce");
	Kind kind();
	boolean isScan();
	boolean isReduce();
    
}
