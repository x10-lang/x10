/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.plugin;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import polyglot.ast.NodeFactory;
import polyglot.frontend.AbstractGoal_c;
import polyglot.frontend.FileSource;
import polyglot.frontend.Globals;
import polyglot.frontend.Parser;
import polyglot.frontend.ParserlessJLExtensionInfo;
import polyglot.frontend.Source;
import polyglot.frontend.SourceLoader;
import polyglot.types.QName;
import polyglot.types.TypeSystem;
import polyglot.util.ErrorInfo;
import polyglot.util.ErrorQueue;
import polyglot.util.InternalCompilerError;
import polyglot.util.QuotedStringTokenizer;
import x10.Configuration;
import x10.ExtensionInfo;
import x10.X10CompilerOptions;

public class LoadPlugins extends AbstractGoal_c {
	private static final long serialVersionUID = -7328993239190636933L;

	private final ExtensionInfo extInfo;
    
	public LoadPlugins(ExtensionInfo extInfo) {
		super("LoadPlugins");
		this.extInfo = extInfo;
	}

	public boolean runTask() {
		X10CompilerOptions opts = (X10CompilerOptions) extInfo.getOptions();
		String compilerPlugins = opts.x10_config.PLUGINS;

		if (compilerPlugins != null && ! compilerPlugins.equals("")) {
			loadPluginsFromConfigString(compilerPlugins);
		}

		return true;
	}

	private void loadPluginsFromConfigString(String compilerPlugins) {
		for (StringTokenizer st = new StringTokenizer(compilerPlugins, ",; \t\n"); st.hasMoreTokens(); ) {
			String pluginName = st.nextToken();
			if (pluginName.length() > 0) {
				loadPlugin(extInfo, QName.make(pluginName));
			}
		}
	}

	private static URL[] parseClassPath(String classpath) {
	    StringTokenizer st = new StringTokenizer(classpath, File.pathSeparator);
	    ArrayList<URL> cp = new ArrayList<URL>(st.countTokens());
	    for (; st.hasMoreTokens(); ) {
	        try {
	            File f = new File(st.nextToken());
	            if (f.exists()) {
	                cp.add(f.toURL());
	            }
	        } catch (MalformedURLException e) { }
	    }
	    return cp.toArray(new URL[cp.size()]);
	}

	/**
	 * Load a compiler plugin from the class file and register it with the given ExtensionInfo.
	 * @param extInfo the extension info to register the plugin with
	 * @param pluginName the name of the plugin to load
	 * TODO: add a separate plugin classpath to the compiler options
	 */
	public static void loadPlugin(ExtensionInfo extInfo, QName pluginName) {
	    loadPlugin(extInfo, pluginName, System.getProperty("java.class.path"));
	}

	/**
	 * Load a compiler plugin from the class file and register it with the given ExtensionInfo.
	 * @param extInfo the extension info to register the plugin with
	 * @param pluginName the name of the plugin to load
	 * @param classpath the classpath to use for looking up the plugin class
	 */
	public static void loadPlugin(ExtensionInfo extInfo, QName pluginName, String classpath) {
		ErrorQueue eq = extInfo.compiler().errorQueue();

		try {
//			System.out.println("classpath = " + System.getProperty("java.class.path"));
			ClassLoader cl = new URLClassLoader(parseClassPath(classpath), LoadPlugins.class.getClassLoader());
			Class<?> c = Class.forName(pluginName.toString(), true, cl);
			Object o = c.newInstance();
			if (o instanceof CompilerPlugin) {
				// OK, it's a plugin!
				CompilerPlugin plugin = (CompilerPlugin) o;
				extInfo.addPlugin(pluginName, plugin);
			}
			else {
				eq.enqueue(ErrorInfo.WARNING, "Class " + pluginName + " does not implement CompilerPlugin.  Skipping.");
			}
		}
		catch (IllegalAccessException e) {
			eq.enqueue(ErrorInfo.WARNING, "Plugin class " + pluginName + " could not be accessed.  Skipping.");
		}
		catch (InstantiationException e) {
			eq.enqueue(ErrorInfo.WARNING, "Plugin class " + pluginName + " could not be instantiated.  Skipping.");
		}
		catch (ClassNotFoundException e) {
			eq.enqueue(ErrorInfo.WARNING, "Plugin class " + pluginName + " not found.  Skipping.");
		}
	}
}
