/**
 * 
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
import java.util.Properties;

import polyglot.main.Options;
import polyglot.util.ErrorInfo;
import polyglot.util.ErrorQueue;
import polyglot.util.QuotedStringTokenizer;
import x10cpp.Configuration;
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
        
        public X10RTPostCompileOptions (ErrorQueue eq, String filename) {
            Properties properties = new Properties();
            try {
                properties.load(new FileInputStream(filename));
            } catch(IOException e) {
                eq.enqueue(ErrorInfo.IO_ERROR, "Error finding X10RT properties file: "+ e.getMessage());
            }                
            String s = properties.getProperty("CXX");
            cxx = s==null ? "g++" : s; //fallback if above error occured or CXX not given in properties file
            String regex = " +";
            cxxFlags = split(properties.getProperty("CXXFLAGS"));
            libs     = split(properties.getProperty("LDLIBS"));
            ldFlags  = split(properties.getProperty("LDFLAGS"));
        }
    }
    
    protected static final String PLATFORM = System.getenv("X10_PLATFORM")==null?"unknown":System.getenv("X10_PLATFORM");
    public static final String X10_DIST = System.getenv("X10_DIST");
    protected static final String X10GC = System.getenv("X10GC")==null?null:System.getenv("X10GC").replace(File.separatorChar, '/');
    protected static final boolean USE_XLC = PLATFORM.startsWith("aix_") && System.getenv("USE_GCC")==null;

    public static final String MANIFEST = "libx10.mft";
    public static final String[] MANIFEST_LOCATIONS = new String[] { X10_DIST+"/lib" };


    private final X10CPPCompilerOptions options;
    
    protected X10RTPostCompileOptions x10rtOpts;
    
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
                rtimpl = "pgas_sockets";
            }
        }
        // allow the user to give an explicit path, otherwise look in etc
        if (!rtimpl.endsWith(".properties")) {
            rtimpl = X10_DIST + "/etc/x10rt_"+rtimpl+".properties";
        }
        x10rtOpts = new X10RTPostCompileOptions(eq, rtimpl);
    }

    /** Is GC enabled on this platform? */
    protected boolean gcEnabled() { return false; }

    protected String defaultPostCompiler() { 
        return x10rtOpts.cxx;
    }

    /** Add the arguments that go before the output files */
    protected void addPreArgs(ArrayList<String> cxxCmd) {
        cxxCmd.add("-g");
        
        // prebuilt XRX
        cxxCmd.add("-I"+X10_DIST+"/include");
        
        // headers generated from user input
        cxxCmd.add("-I.");

        if (!Configuration.DISABLE_GC && gcEnabled()) {
            cxxCmd.add("-DX10_USE_BDWGC");
            cxxCmd.add("-I"+X10GC+"/include");
        }
        
        if (x10.Configuration.OPTIMIZE) {
            cxxCmd.add(USE_XLC ? "-O3" : "-O2");
            cxxCmd.add("-DNDEBUG");
            cxxCmd.add(USE_XLC ? "-qinline" : "-finline-functions");
            if (USE_XLC) {
                cxxCmd.add("-qhot");
            }
        }
        
        if (x10.Configuration.NO_CHECKS) {
            cxxCmd.add("-DNO_CHECKS");
        }
        
        cxxCmd.addAll(x10rtOpts.cxxFlags);

    }

    /** Add the arguments that go after the output files */
    protected void addPostArgs(ArrayList<String> cxxCmd) {
        // prebuilt XRX
        cxxCmd.add("-L"+X10_DIST+"/lib");
        cxxCmd.add("-lx10");

        if (!Configuration.DISABLE_GC && gcEnabled()) {
            cxxCmd.add("-L"+X10GC+"/lib");
            cxxCmd.add("-lgc");
        }

        cxxCmd.addAll(x10rtOpts.ldFlags);
        cxxCmd.addAll(x10rtOpts.libs);

        cxxCmd.add("-ldl");
        cxxCmd.add("-lm");
        cxxCmd.add("-lpthread");
    }

    protected void addExecutablePath(ArrayList<String> cxxCmd) {
        File exe = null;
        if (options.executable_path != null) {
            exe = new File(options.executable_path);
        } else if (Configuration.MAIN_CLASS != null) {
            exe = new File(options.output_directory, Configuration.MAIN_CLASS);
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
            // The first '#' marks the place where the filenames go
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
            String manifest = x10.Configuration.MANIFEST;
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
        eq.enqueue(ErrorInfo.WARNING,
                "Unknown platform '"+PLATFORM+"'; using the default post-compiler (g++)");
        return new CXXCommandBuilder(options, eq);
    }

}
