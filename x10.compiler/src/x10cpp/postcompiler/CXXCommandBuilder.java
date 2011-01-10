/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10cpp.postcompiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import polyglot.main.Options;
import polyglot.util.ErrorInfo;
import polyglot.util.ErrorQueue;
import polyglot.util.InternalCompilerError;
import polyglot.util.QuotedStringTokenizer;
import x10.Configuration;
import x10cpp.X10CPPCompilerOptions;

public class CXXCommandBuilder {

    protected class X10RTPostCompileOptions {
        public final String cxx;
        public final Collection<? extends String> cxxFlags;
        public final Collection<? extends String> libs;
        public final Collection<? extends String> ldFlags;

        private Collection<? extends String> split(String s) {
            ArrayList<String> l = new ArrayList<String>();
            if (s==null) return l;
            QuotedStringTokenizer q = new QuotedStringTokenizer(s);
            while (q.hasMoreTokens()) l.add(q.nextToken());
            return l;
        }

        public X10RTPostCompileOptions(String filename) {
            Properties properties = new Properties();
            try {
                properties.load(new FileInputStream(filename));
            } catch(IOException e) {
                // [DC] proceeding from here will just yield a load of incomprehensible postcompile errors
                throw new InternalCompilerError(
                        "Error finding X10RT properties file: "+ e.getMessage(), e);
            }
            String s = properties.getProperty("CXX");
            cxx = s==null ? "g++" : s; //fallback if CXX not given in properties file
            String regex = " +";
            cxxFlags = split(properties.getProperty("CXXFLAGS"));
            libs     = split(properties.getProperty("LDLIBS"));
            ldFlags  = split(properties.getProperty("LDFLAGS"));
        }
    }

    protected class BDWGCPostCompileOptions {
        public final Collection<? extends String> cxxFlags;
        public final Collection<? extends String> libs;

        private Collection<? extends String> split(String s) {
            ArrayList<String> l = new ArrayList<String>();
            if (s==null) return l;
            QuotedStringTokenizer q = new QuotedStringTokenizer(s);
            while (q.hasMoreTokens()) l.add(q.nextToken());
            return l;
        }

        public BDWGCPostCompileOptions(String filename) {
            Properties properties = new Properties();
            try {
                properties.load(new FileInputStream(filename));
            } catch(IOException e) {
                // [DC] proceeding from here will just yield a load of incomprehensible postcompile errors
                throw new InternalCompilerError(
                        "Error finding BDWGC property file: "+ e.getMessage(), e);
            }
            cxxFlags = split(properties.getProperty("CXXFLAGS"));
            libs     = split(properties.getProperty("LDLIBS"));
        }
    }

    protected static final String PLATFORM = System.getenv("X10_PLATFORM")==null?"unknown":System.getenv("X10_PLATFORM");
    public static final String X10_DIST = System.getenv("X10_DIST");
    protected static final boolean USE_XLC = (PLATFORM.startsWith("aix_") && System.getenv("USE_GCC")==null) || System.getenv("USE_XLC")!=null;
    //"mpCC_r -q64 -qrtti=all -qarch=pwr5 -O3 -qtune=pwr5 -qhot -qinline"
    //"mpCC_r -q64 -qrtti=all"
    public static final String XLC_EXTRA_FLAGS = System.getenv("XLC_EXTRA_FLAGS");
    public static final boolean USE_32BIT = System.getenv("USE_32BIT")!=null;

    protected static final boolean ENABLE_PROFLIB = System.getenv("X10_ENABLE_PROFLIB") != null;

    public static final String MANIFEST = "libx10.mft";
    public static final String[] MANIFEST_LOCATIONS = new String[] { X10_DIST+"/lib" };


    private final X10CPPCompilerOptions options;

    protected X10RTPostCompileOptions x10rtOpts;

    protected BDWGCPostCompileOptions bdwgcOpts;

    public CXXCommandBuilder(Options options, ErrorQueue eq) {
        assert (options != null);
        assert (options.post_compiler != null);
        this.options = (X10CPPCompilerOptions) options;
        String rtimpl = System.getenv("X10RT_IMPL");
        if (rtimpl == null) {
            // assume pgas (default to old behavior)
            if (PLATFORM.startsWith("aix_")) {
                rtimpl = "pgas_lapi";
            } else {
                rtimpl = "sockets";
            }
        }
        // allow the user to give an explicit path, otherwise look in etc
        if (!rtimpl.endsWith(".properties")) {
            rtimpl = X10_DIST + "/etc/x10rt_"+rtimpl+".properties";
        }
        x10rtOpts = new X10RTPostCompileOptions(rtimpl);
        bdwgcOpts = new BDWGCPostCompileOptions(X10_DIST + "/etc/bdwgc.properties");
    }

    protected String defaultPostCompiler() {
	    return x10rtOpts.cxx;
    }

    /** Add the arguments that go before the output files */
    protected void addPreArgs(ArrayList<String> cxxCmd) {
        cxxCmd.add("-g");

        // prebuilt XRX
        cxxCmd.add("-I"+X10_DIST+"/include");

        // headers generated from user input
        cxxCmd.add("-I"+options.output_directory);
        cxxCmd.add("-I.");

        if (options.x10_config.OPTIMIZE) {
            cxxCmd.add(USE_XLC ? "-O3" : "-O2");
            cxxCmd.add(USE_XLC ? "-qinline" : "-finline-functions");
            cxxCmd.add("-DNO_TRACING");
            if (USE_XLC) {
                cxxCmd.add("-qhot");
                cxxCmd.add("-qtune=auto");
                cxxCmd.add("-qarch=auto");
            }
        }

        if (USE_XLC) {
            cxxCmd.add("-qsuppress=1540-0809"    // Do not warn about empty sources
                               + ":1540-1101"    // Do not warn about non-void functions with no return
                               + ":1500-029");   // Do not warn about being unable to inline when optimizing
            cxxCmd.add(USE_32BIT ? "-q32" : "-q64");
            if (XLC_EXTRA_FLAGS != null) {
                cxxCmd.add(XLC_EXTRA_FLAGS);
            }
        } else {
            cxxCmd.add("-Wno-long-long");        // Do not warn about using long long
            cxxCmd.add("-Wno-unused-parameter"); // Do not warn about unused parameters
            // DISABLED due to XTENLANG-2215; finally block rewriting has (dynamically unreachable) path in which there is no return
            // cxxCmd.add("-Wreturn-type");         // Do warn about non-void functions with no return
        }

        if (options.x10_config.NO_CHECKS) {
            cxxCmd.add("-DNO_CHECKS");
        }

        cxxCmd.addAll(x10rtOpts.cxxFlags);
    }

    /** Add the arguments that go after the output files */
    protected void addPostArgs(ArrayList<String> cxxCmd) {
        // prebuilt XRX
        cxxCmd.add("-L"+X10_DIST+"/lib");
        cxxCmd.add("-lx10");

        cxxCmd.addAll(bdwgcOpts.cxxFlags);
        cxxCmd.addAll(bdwgcOpts.libs);

        cxxCmd.addAll(x10rtOpts.ldFlags);
        cxxCmd.addAll(x10rtOpts.libs);

        cxxCmd.add("-ldl");
        cxxCmd.add("-lm");
        cxxCmd.add("-lpthread");

        if (ENABLE_PROFLIB) {
            cxxCmd.add("-lprofiler");
        }
    }

    protected void addExecutablePath(ArrayList<String> cxxCmd) {
        File exe = null;
        if (options.executable_path != null) {
            exe = new File(options.executable_path);
        } else if (options.x10_config.MAIN_CLASS != null) {
            exe = new File(options.output_directory, options.x10_config.MAIN_CLASS);
        } else {
            return;
        }
        cxxCmd.add("-o");
        cxxCmd.add(exe.getAbsolutePath().replace(File.separatorChar,'/'));
    }

    /** Construct the C++ compilation command */
    public final String[] buildCXXCommandLine(Collection<String> outputFiles) {
        String post_compiler = options.post_compiler;
        if (post_compiler.contains("javac"))
            post_compiler = defaultPostCompiler();

        QuotedStringTokenizer st = new QuotedStringTokenizer(post_compiler);
        int pc_size = st.countTokens();
        ArrayList<String> cxxCmd = new ArrayList<String>();
        String token = "";
        for (int i = 0; i < pc_size; i++) {
            token = st.nextToken();
            // A # as the first token signifies that the default postcompiler for this platform be used
            if (i==0 && token.equals("#")) {
            	cxxCmd.add(defaultPostCompiler());
            	continue;
            }

        	// consume all tokens up until the next # (or %) whereupon we will insert (or not)
        	// default CXXFLAGS parameters and generated compilation units
            if (token.equals("#") || token.equals("%")) {
                break;
            }
            cxxCmd.add(token);
        }

        boolean skipArgs = token.equals("%");
        if (!skipArgs) {
            addPreArgs(cxxCmd);
            addExecutablePath(cxxCmd);
        }

        HashSet<String> exclude = new HashSet<String>();
        try {
            String manifest = options.x10_config.MANIFEST;
            if (manifest == null) {
                for (int i = 0; i < MANIFEST_LOCATIONS.length; i++) {
                    File x10lang_m = new File(MANIFEST_LOCATIONS[i]+"/"+MANIFEST);
                    if (!x10lang_m.exists())
                        continue;
                    manifest = x10lang_m.getPath();
                }
            }
            if (manifest != null) {
                FileReader fr = new FileReader(manifest);
                BufferedReader br = new BufferedReader(fr);
                String file = "";
                while ((file = br.readLine()) != null)
                    exclude.add(file);
            }
        } catch (IOException e) { }

        Iterator<String> iter = outputFiles.iterator();
        for (; iter.hasNext(); ) {
            String file = (String) iter.next();
            file = file.replace(File.separatorChar,'/');
            if (exclude.contains(file))
                continue;
            if (file.endsWith(".cu")) continue;
            cxxCmd.add(file);
        }

        while (st.hasMoreTokens()) {
            token = st.nextToken();
            // The second '#' delimits the libraries that have to go at the end
            if (token.equals("#") || token.equals("%")) {
                break;
            }
            cxxCmd.add(token);
        }

        boolean skipLibs = token.equals("%");
        if (!skipLibs) {
            addPostArgs(cxxCmd);
        }

        while (st.hasMoreTokens()) {
            cxxCmd.add(st.nextToken());
        }

        return cxxCmd.toArray(new String[cxxCmd.size()]);
    }

    public static CXXCommandBuilder getCXXCommandBuilder(Options options, ErrorQueue eq) {
        if (PLATFORM.startsWith("win32_"))
            return new Cygwin_CXXCommandBuilder(options, eq);
        if (PLATFORM.startsWith("linux_"))
            return new Linux_CXXCommandBuilder(options, eq);
        if (PLATFORM.startsWith("aix_"))
            return new AIX_CXXCommandBuilder(options, eq);
        if (PLATFORM.startsWith("sunos_"))
            return new SunOS_CXXCommandBuilder(options, eq);
        if (PLATFORM.startsWith("macosx_"))
            return new MacOSX_CXXCommandBuilder(options, eq);
        if (PLATFORM.startsWith("freebsd_"))
            return new FreeBSD_CXXCommandBuilder(options, eq);
        eq.enqueue(ErrorInfo.WARNING,
                "Unknown platform '"+PLATFORM+"'; using the default post-compiler (g++)");
        return new CXXCommandBuilder(options, eq);
    }

}
