/*
 * Created on Feb 26, 2008
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package x10cpp.types;

import polyglot.types.Context;
import x10.types.X10TypeSystem_c;

public class X10CPPTypeSystem_c extends X10TypeSystem_c {
	public Context emptyContext() {
		return new X10CPPContext_c(this);
	}
}