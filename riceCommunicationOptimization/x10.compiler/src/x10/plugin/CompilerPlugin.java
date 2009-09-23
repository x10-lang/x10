/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.plugin;

import polyglot.frontend.Goal;
import polyglot.frontend.Job;
import x10.ExtensionInfo;

public interface CompilerPlugin {
	public Goal register(ExtensionInfo extInfo, Job job);
}
