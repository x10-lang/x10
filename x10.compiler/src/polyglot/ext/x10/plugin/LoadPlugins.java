package polyglot.ext.x10.plugin;

import java.util.StringTokenizer;

import polyglot.ext.x10.Configuration;
import polyglot.frontend.AbstractPass;
import polyglot.ext.x10.ExtensionInfo;
import polyglot.ext.x10.ExtensionInfo.X10Scheduler;
import polyglot.ext.x10.plugin.RegisterPlugins.RegisterPluginsPass;
import polyglot.frontend.Job;
import polyglot.frontend.Pass;
import polyglot.frontend.goals.AbstractGoal;
import polyglot.frontend.goals.Goal;
import polyglot.util.ErrorInfo;
import polyglot.util.ErrorQueue;

public class LoadPlugins extends AbstractGoal {

	protected static final class LoadPluginsPass extends AbstractPass {
		private final ExtensionInfo extInfo;

		protected LoadPluginsPass(Goal goal, ExtensionInfo extInfo) {
			super(goal);
			this.extInfo = extInfo;
		}

		public boolean run() {
			String compilerPlugins = Configuration.PLUGINS;
			
			if (compilerPlugins == null) {
				return true;
			}
			
			for (StringTokenizer st = new StringTokenizer(compilerPlugins, ",; \t\n"); st.hasMoreTokens(); ) {
				String pluginName = st.nextToken();
				if (pluginName.length() > 0) {
					ErrorQueue eq = extInfo.compiler().errorQueue();
					try {
						Class c = Class.forName(pluginName);
						Object o = c.newInstance();
						if (o instanceof CompilerPlugin) {
							// OK, it's a plugin!
							CompilerPlugin plugin = (CompilerPlugin) o;
							extInfo.addPlugin(pluginName, plugin);
						}
						else {
							eq.enqueue(ErrorInfo.WARNING, "Class " + pluginName + " does not implement CompilerPlugin.  Continuing.");
						}
					}
					catch (IllegalAccessException e) {
						eq.enqueue(ErrorInfo.WARNING, "Plugin class " + pluginName + " could not be accessed.  Continuing without it.");
					}
					catch (InstantiationException e) {
						eq.enqueue(ErrorInfo.WARNING, "Plugin class " + pluginName + " could not be instantiated.  Continuing without it.");
					}
					catch (ClassNotFoundException e) {
						eq.enqueue(ErrorInfo.WARNING, "Plugin class " + pluginName + " not found.  Continuing without it.");
					}
				}
			}
			
			return true;
		}
	}

	public LoadPlugins() {
		super(null, "LoadPlugins");
	}

	@Override
	public Pass createPass(polyglot.frontend.ExtensionInfo extInfo) {
		return new LoadPluginsPass(this, (polyglot.ext.x10.ExtensionInfo)  extInfo);
	}

	public static Goal create(X10Scheduler scheduler) {
		return scheduler.internGoal(new LoadPlugins());
	}
}
