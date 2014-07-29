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

package x10;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import polyglot.frontend.ExtensionInfo;
import polyglot.main.Main;
import polyglot.main.UsageError;
import polyglot.util.InternalCompilerError;
import x10.config.ConfigurationError;
import x10.config.OptionError;
import x10cpp.postcompiler.PrecompiledLibrary;

public class X10CompilerOptions extends polyglot.main.Options {
    
    public String executable_path = null;
    public Configuration x10_config;

    public String buildX10Lib = null;
    public boolean symbols = true;

    /**
     * Absolute path to the X10 distribution
     */
    private String distPath;
    public void setDistPath(String dp) { distPath = dp; }
    public String distPath() { return distPath; }
    
    public final List<PrecompiledLibrary> x10libs = new ArrayList<PrecompiledLibrary>();
    
	public X10CompilerOptions(ExtensionInfo extension) {
		super(extension);
		serialize_type_info = false; // turn off type info serialization for X10
		assertions = true; // turn on assertion generation for X10
		x10_config = new Configuration();
	}

	/**
	 * Add a PrecompiledLibrary object representing a remote x10library.
	 * This enabled the library to be linked against, but does not include
	 * the remote jar file in the compiler's sourcepath.
	 * Intended for use only by X10DT.
	 * 
	 * @param lib the library to add
	 */
	public void addRemotePrecompiledLibrary(PrecompiledLibrary lib) {
	    x10libs.add(lib);
	}
	
	/**
	 * Add a PrecompiledLibrary object representing a local x10library.
     * This enabled the library to be linked against and includes
     * the jar file in the compiler's sourcepath.
     * 
     * @param lib the library to add
     */
    public void addLocalPrecompiledLibrary(PrecompiledLibrary lib) {
        x10libs.add(lib);
        File jf = new File(lib.absolutePathToRoot, lib.sourceJar);
        source_path.add(jf);
    }

    protected static File createTempDir(String prefix, String suffix) throws IOException {
        File tempdir = File.createTempFile(prefix, suffix);
        // TODO following two statements should be done atomically
        tempdir.delete();
        tempdir.mkdir();
        return tempdir;
    }
    
    @Override
    public void parseCommandLine(String[] args, Set<String> source) throws UsageError {
        super.parseCommandLine(args, source);
        
        // XTENLANG-2126
        if (!keep_output_files) { // -nooutput was specified
            // ignore -d output_directory if specified and
            // set a new temporary directory to output_directory.
            // after post-compile, the output_directory will be removed.
            try {
                String prefix = "x10c-" + System.getProperty("user.name") + ".";
                String suffix = "";
                output_directory = createTempDir(prefix, suffix);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    
	@Override
	protected int parseCommand(String args[], int index, Set<String> source) 
		throws UsageError, Main.TerminationException
	{
		int i = super.parseCommand(args, index, source);
		if (i != index) return i;

		if (args[i].equals("-noassert")) {
			assertions = false;
			return ++i;
		}
        if (args[i].equals("-nosymbols")) {
            symbols = false;
            return ++i;
        }
		if (args[i].equals("-o")) {
		    ++i;
		    executable_path = args[i];
		    return ++i;
		}
		if (args[i].equals("-x10lib")) {
		    ++i;
		    String libFile = args[i];
	        try {
	            Properties properties = new Properties();
	            File f = new File(libFile);
	            properties.load(new FileInputStream(f));
	            PrecompiledLibrary libObj = new PrecompiledLibrary(f.getParentFile().getAbsolutePath(), properties);
	            addLocalPrecompiledLibrary(libObj);
	        } catch(IOException e) {
	            Main.TerminationException te = new Main.TerminationException("Unable to load x10library file "+ e.getMessage());
	            te.initCause(e);
	            throw te;
	        }
	        return ++i;
		}
		if (args[i].equals("-buildx10lib")) {
		    ++i;
			buildX10Lib = args[i];
	        return ++i;
		}
		
		try {
			x10_config.parseArgument(args[index]);
			return ++index;
		}
		catch (OptionError e) { }
		catch (ConfigurationError e) { }
		return index;
	}

	public int checkCommand(String args[], int index, Set<String> source)
		throws UsageError, Main.TerminationException, OptionError, ConfigurationError
	{
		int i = super.parseCommand(args, index, source);
		if (i != index) return i;
		
		x10_config.parseArgument(args[index]);
		return ++index;
	}

	/**
	 * Print usage information
	 */
	@Override
	public void usage(PrintStream out) {
		super.usage(out);
		usageForFlag(out, "-noassert", "turn off assertion generation");
        usageForFlag(out, "-nosymbols", "generate executable without symbols (it won't be used for type check)");
        usageForFlag(out, "-o <path>", "set generated executable path (for the post-compiler)");
        usageForFlag(out, "-buildx10lib <path>", "build an x10 library");
		usageForFlag(out, "-x10lib <lib.properties>", "use the precompiled x10 library described by <lib.properties>");

		String[][] options = x10_config.options();
		for (int i = 0; i < options.length; i++) {
			String[] optinfo = options[i];
			String optflag = "-"+optinfo[0]+"="+optinfo[1];
			String optdesc = optinfo[2]+"(default = "+optinfo[3]+")";
			usageForFlag(out, optflag, optdesc);
		}
	}
}
