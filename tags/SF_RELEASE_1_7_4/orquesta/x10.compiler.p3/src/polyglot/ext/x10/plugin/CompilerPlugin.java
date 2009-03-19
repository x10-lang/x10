package polyglot.ext.x10.plugin;

import polyglot.ext.x10.ExtensionInfo;
import polyglot.frontend.Goal;
import polyglot.frontend.Job;

public interface CompilerPlugin {
	public Goal register(ExtensionInfo extInfo, Job job);
}
