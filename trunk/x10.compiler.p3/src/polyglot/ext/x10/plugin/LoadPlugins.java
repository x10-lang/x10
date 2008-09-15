/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package polyglot.ext.x10.plugin;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.StringTokenizer;

import polyglot.ext.x10.Configuration;
import polyglot.ext.x10.ExtensionInfo;
import polyglot.frontend.AbstractGoal_c;
import polyglot.frontend.FileSource;
import polyglot.frontend.Globals;
import polyglot.frontend.Parser;
import polyglot.frontend.ParserlessJLExtensionInfo;
import polyglot.frontend.Source;
import polyglot.frontend.SourceLoader;
import polyglot.types.QName;
import polyglot.util.ErrorInfo;
import polyglot.util.ErrorQueue;
import polyglot.util.InternalCompilerError;
import polyglot.util.QuotedStringTokenizer;

public class LoadPlugins extends AbstractGoal_c {

	private final ExtensionInfo extInfo;
	
	public LoadPlugins(ExtensionInfo extInfo) {
		super("LoadPlugins");
		this.extInfo = extInfo;
	}

	public boolean runTask() {
		// Handle some hard-coded plugins
		String xmlProcessor = Configuration.XML_PROCESSOR;
		boolean exportXML = Configuration.EXTERNALIZE_ASTS;

		if (xmlProcessor != null && ! xmlProcessor.equals("")) {
			exportXML = true;
		}

		if (exportXML) {
			loadPlugin(extInfo, QName.make("polyglot.ext.x10.dom.ExternalizerPlugin"));
		}

		String compilerPlugins = Configuration.PLUGINS;

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

	public static void loadPlugin(ExtensionInfo extInfo, QName pluginName) {
		ErrorQueue eq = extInfo.compiler().errorQueue();
		
		File sourceFile = null;
		
		// First try to load the plugin from source.
		// This is a bit of a hack.
		// Create a dummy JL ExtensionInfo and SourceLoader.
		polyglot.frontend.ExtensionInfo jlExtInfo = new ParserlessJLExtensionInfo() {
			public Parser parser(Reader reader, FileSource source, ErrorQueue eq) {
				throw new InternalCompilerError("Missing parser");
			}
			public String[] fileExtensions() {
				return new String[] { "java" };
			}
		};
		
		SourceLoader sourceLoader = new SourceLoader(jlExtInfo, extInfo.getOptions().source_path);
		Source s = sourceLoader.classSource(pluginName);
		
//		if (s != null) {
//			System.out.println("found source " + sourceFile);
//			sourceFile = new File(s.path());
//			long sourceModTime = sourceFile.lastModified();
//			
//			// Now find the class file so we can compare modification types.
//			long classModTime = getClassModTime(pluginName, extInfo.getOptions().classpath);
//			
//			// Compile the source file if it's newer than the class file.
//			if (sourceModTime > classModTime) {
//				eq.enqueue(ErrorInfo.WARNING, "Compiling " + pluginName + " in " + s.path());
//				compilePlugin(extInfo, sourceFile);
//			}
//		}
		
		while (true) {
			// Now load the plugin from the class file.
			try {
//				System.out.println("classpath = " + System.getProperty("java.class.path"));
				Class c = Class.forName(pluginName.toString());
				Object o = c.newInstance();
				if (o instanceof CompilerPlugin) {
					// OK, it's a plugin!
					CompilerPlugin plugin = (CompilerPlugin) o;
					extInfo.addPlugin(pluginName, plugin);
					return;
				}
				else {
					if (s == null) {
						eq.enqueue(ErrorInfo.WARNING, "Class " + pluginName + " does not implement CompilerPlugin.  Continuing.");
						return;
					}
				}
			}
			catch (IllegalAccessException e) {
				if (s == null) {
					eq.enqueue(ErrorInfo.WARNING, "Plugin class " + pluginName + " could not be accessed.  Continuing without it.");
					return;
				}
			}
			catch (InstantiationException e) {
				if (s == null) {
					eq.enqueue(ErrorInfo.WARNING, "Plugin class " + pluginName + " could not be instantiated.  Continuing without it.");
					return;
				}
			}
			catch (ClassNotFoundException e) {
				if (s == null) {
					eq.enqueue(ErrorInfo.WARNING, "Plugin class " + pluginName + " not found.  Continuing without it.");
					return;
				}
			}
			
			// Loading the class failed.  Try compiling the plugin from source.
			if (s != null) {
				eq.enqueue(ErrorInfo.WARNING, "Compiling " + pluginName + " in " + s.path());
				compilePlugin(extInfo, s.path());
				s = null;
			}
		}
		
	}

//	private static long getClassModTime(String pluginName, String classpath) {
//		StringTokenizer st = new StringTokenizer(classpath, File.pathSeparator);
//		
//		while (st.hasMoreTokens()) {
//			String s = st.nextToken();
//			
//			File dir = new File(s);
//			
//			if (! dir.exists()) {
//				continue;
//			}
//			
//			try {
//				ZipFile zip = null;
//				
//				if (dir.getName().endsWith(".jar")) {
//					zip = new JarFile(dir);
//				}
//				else if (dir.getName().endsWith(".zip")) {
//					zip = new ZipFile(dir);
//				}
//				
//				if (zip != null) {
//					String entryName = pluginName.replace('.', '/') + ".class";
//					ZipEntry entry = zip.getEntry(entryName);
//					if (entry != null) {
//						return entry.getTime();
//					}
//				}
//				else {
//					String fileName = pluginName.replace('.', File.separatorChar) + ".class";
//					File file = new File(dir, fileName);
//					if (file.exists()) {
//						return file.lastModified();
//					}
//				}
//			}
//			catch (FileNotFoundException e) {
//				// ignore the exception.
//			}
//			catch (IOException e) {
//				throw new InternalCompilerError(e);
//			}
//		}
//		
//		return 0;
//	}

	private static void compilePlugin(ExtensionInfo extInfo, String sourceFile) {
		ErrorQueue eq = extInfo.compiler().errorQueue();
		
		String javac = Configuration.PLUGIN_COMPILER;
		
		if (javac == null || javac.equals("")) {
			javac = extInfo.getOptions().post_compiler;
		}
		
		if (javac != null) {
			Runtime runtime = Runtime.getRuntime();
			
			QuotedStringTokenizer qst = new QuotedStringTokenizer(javac);
			int pc_size = qst.countTokens();
			String[] javacCmd = new String[pc_size+3];
			int j = 0;
			int source = -2;
			for (int i = 0; i < pc_size; i++) {
				String arg = qst.nextToken();
				if (arg.equals("-source"))
					source = i;
				if (source+1 == i) {
					if (arg.equals("1.0") ||
							arg.equals("1.1") ||
							arg.equals("1.2") ||
							arg.equals("1.3") ||
							arg.equals("1.4")) {
						arg = "1.5";
					}
				}
				javacCmd[j++] = arg;
			}
			javacCmd[j++] = "-classpath";
			javacCmd[j++] = extInfo.getOptions().constructPostCompilerClasspath();
			javacCmd[j++] = sourceFile;
			
			try {
				Process proc = runtime.exec(javacCmd);
				InputStreamReader err = new InputStreamReader(proc.getErrorStream());
				
				try {
					char[] c = new char[72];
					int len;
					StringBuffer sb = new StringBuffer();
					while((len = err.read(c)) > 0) {
						sb.append(String.valueOf(c, 0, len));
					}
					
					if (sb.length() != 0) {
						eq.enqueue(ErrorInfo.WARNING, sb.toString());
					}
				}
				finally {
					err.close();
				}
				
				proc.waitFor();
				
				if (proc.exitValue() > 0) {
					eq.enqueue(ErrorInfo.WARNING,
							"Non-zero return code: " + proc.exitValue());
				}
			}
			catch(Exception e) {
				eq.enqueue(ErrorInfo.WARNING, e.getMessage());
			}
		}
	}
}
