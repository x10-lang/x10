/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package polyglot.ext.x10.plugin;

import polyglot.ext.x10.ExtensionInfo;
import polyglot.frontend.Goal;
import polyglot.frontend.Job;

public interface CompilerPlugin {
	public Goal register(ExtensionInfo extInfo, Job job);
}
