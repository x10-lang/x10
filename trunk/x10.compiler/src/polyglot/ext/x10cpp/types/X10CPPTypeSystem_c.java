/*
 * Created on Feb 26, 2008
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package polyglot.ext.x10cpp.types;

import polyglot.ext.x10.types.X10TypeSystem_c;
import polyglot.types.Context;

public class X10CPPTypeSystem_c extends X10TypeSystem_c {
	public Context emptyContext() {
		return new X10CPPContext_c(this);
	}
}