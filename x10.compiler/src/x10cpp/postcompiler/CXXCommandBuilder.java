/**
 * 
 */
package x10cpp.postcompiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import polyglot.main.Options;
import polyglot.util.ErrorInfo;
import polyglot.util.ErrorQueue;
import polyglot.util.QuotedStringTokenizer;
import x10cpp.Configuration;
import x10cpp.X10CPPCompilerOptions;

public class CXXCommandBuilder {
    protected static enum X10RT_Impl {
        PGAS_LAPI("xlpgas_lapi"),
        PGAS_SOCKETS("xlpgas_sockets"),
        PGAS_BGP("xlpgas_bgp"),
        MPI("x10rt_mpi"),
        STANDALONE("x10rt_standalone");
        
        private final String libName;
        
        X10RT_Impl(String libName) {
            this.libName = libName;
        }
        
        String libName() { return libName; }
    };
    
    public static final String PLATFORM = System.getenv("X10_PLATFORM")==null?"unknown":System.getenv("X10_PLATFORM");
    public static final String X10LANG = System.getenv("X10LANG")==null?"../../../x10.runtime/src-cpp":System.getenv("X10LANG").replace(File.separatorChar, '/');
    public static final String X10DIST = System.getenv("X10DIST")==null?"../../../x10.dist":System.getenv("X10DIST").replace(File.separatorChar, '/');

    public static final String MANIFEST = "libx10.mft";
    public static final String[] MANIFEST_LOCATIONS = new String[] {
        X10LANG,
        X10LANG+"/lib",
    };

    protected static final String X10LIB = System.getenv("X10LIB")==null?"../../../pgas2/common/work":System.getenv("X10LIB").replace(File.separatorChar, '/');
    protected static final String X10GC = System.getenv("X10GC")==null?X10DIST:System.getenv("X10GC").replace(File.separatorChar, '/');
    protected static final boolean USE_XLC = PLATFORM.startsWith("aix_") && System.getenv("USE_GCC")==null;

    private final X10CPPCompilerOptions options;
    
    protected final X10RT_Impl x10rt;

    public CXXCommandBuilder(Options options) {
        assert (options != null);
        assert (options.post_compiler != null);
        this.options = (X10CPPCompilerOptions) options;
        String rtimpl = System.getenv("X10RT_IMPL");
        if (rtimpl == null) {
            // assume pgas (default to old behavior)
            if (PLATFORM.startsWith("aix_")) {
                x10rt = X10RT_Impl.PGAS_LAPI;
            } else {
                x10rt = X10RT_Impl.PGAS_SOCKETS;
            }
        } else if (rtimpl.equals("PGAS_LAPI")) {
            x10rt = X10RT_Impl.PGAS_LAPI;
        } else if (rtimpl.equals("PGAS_SOCKETS")) {
            x10rt = X10RT_Impl.PGAS_SOCKETS;
        } else if (rtimpl.equals("PGAS_BGP")) {
            x10rt = X10RT_Impl.PGAS_BGP;
        } else if (rtimpl.equals("MPI")) {
            x10rt = X10RT_Impl.MPI;
        } else if (rtimpl.equals("STANDALONE")) {
            x10rt = X10RT_Impl.STANDALONE;
        } else {
            assert false : "Unknown X10RT IMPL "+ rtimpl;
            x10rt = X10RT_Impl.PGAS_SOCKETS;
        }
    }

    /** Is GC enabled on this platform? */
    protected boolean gcEnabled() { return false; }

    protected String defaultPostCompiler() { 
        if (x10rt == X10RT_Impl.MPI) {
            return "mpicxx";
        } else {
            return "g++"; 
        }
    }

    /** Add the arguments that go before the output files */
    protected void addPreArgs(ArrayList<String> cxxCmd) {
        cxxCmd.add("-g");
        cxxCmd.add("-I"+X10LIB+"/include");
        cxxCmd.add("-I"+X10LANG);
        cxxCmd.add("-I"+X10LANG+"/gen"); // FIXME: development option
        cxxCmd.add("-I"+X10LANG+"/include"); // dist
        cxxCmd.add("-I"+X10DIST+"/include");
        cxxCmd.add("-I.");

        if (!Configuration.DISABLE_GC && gcEnabled()) {
            cxxCmd.add("-DX10_USE_BDWGC");
            cxxCmd.add("-I"+X10GC+"/include");
        }
        
        if (x10.Configuration.OPTIMIZE) {
            cxxCmd.add("-O2");
            cxxCmd.add("-DNDEBUG");
            cxxCmd.add(USE_XLC ? "-qinline" : "-finline-functions");
            if (USE_XLC) {
                cxxCmd.add("-qhot");
            }
        }
    }

    /** Add the arguments that go after the output files */
    protected void addPostArgs(ArrayList<String> cxxCmd) {
        if (!Configuration.DISABLE_GC && gcEnabled()) {
            cxxCmd.add(X10GC+"/lib/libgc.a");
        }
        
        cxxCmd.add("-L"+X10LIB+"/lib");
        cxxCmd.add("-L"+X10LANG);
        cxxCmd.add("-L"+X10LANG+"/lib"); // dist
        cxxCmd.add("-L"+X10DIST+"/lib");
        cxxCmd.add("-lx10");
        cxxCmd.add("-l"+x10rt.libName());
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
            return new Cygwin_CXXCommandBuilder(options);
        if (PLATFORM.startsWith("linux_"))
            return new Linux_CXXCommandBuilder(options);
        if (PLATFORM.startsWith("aix_"))
            return new AIX_CXXCommandBuilder(options);
        if (PLATFORM.startsWith("sunos_"))
            return new SunOS_CXXCommandBuilder(options);
        if (PLATFORM.startsWith("macosx_"))
            return new MacOSX_CXXCommandBuilder(options);
        eq.enqueue(ErrorInfo.WARNING,
                "Unknown platform '"+PLATFORM+"'; using the default post-compiler (g++)");
        return new CXXCommandBuilder(options);
    }

}
