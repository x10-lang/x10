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
    protected static final String DUMMY = "-U___DUMMY___";

    public static final String PLATFORM = System.getenv("X10_PLATFORM")==null?"unknown":System.getenv("X10_PLATFORM");
    public static final String X10LANG = System.getenv("X10LANG")==null?"../../../x10.runtime/src-cpp":System.getenv("X10LANG").replace(File.separatorChar, '/');

    public static final String DEFAULT_PGAS_TRANSPORT = PLATFORM.startsWith("aix_")?"lapi":"sockets";

    public static final String MANIFEST = "libx10.mft";
    public static final String[] MANIFEST_LOCATIONS = new String[] {
        X10LANG,
        X10LANG+"/lib",
    };

    protected static final String X10LIB = System.getenv("X10LIB")==null?"../../../pgas2/common/work":System.getenv("X10LIB").replace(File.separatorChar, '/');
    protected static final String X10GC = System.getenv("X10GC")==null?"../../../x10.dist":System.getenv("X10GC").replace(File.separatorChar, '/');
    protected static final String TRANSPORT = System.getenv("X10RT_TRANSPORT")==null?DEFAULT_PGAS_TRANSPORT:System.getenv("X10RT_TRANSPORT");
    protected static final boolean USE_XLC = PLATFORM.startsWith("aix_") && System.getenv("USE_GCC")==null;
    protected static final boolean USE_BFD = System.getenv("USE_BFD")!=null;

    /** These go before the files */
    public static final String[] preArgs = new String[] {
        "-g",
        "-I"+X10LIB+"/include",
        "-I"+X10LANG,
        "-I"+X10LANG+"/gen", // FIXME: development option
        "-I"+X10LANG+"/include", // dist
        "-I.",
        "-DTRANSPORT="+TRANSPORT,
    };
    /** These go after the files */
    public static final String[] postArgs = new String[] {
        "-L"+X10LIB+"/lib",
        "-L"+X10LANG,
        "-L"+X10LANG+"/lib", // dist
        "-lx10",
        "-lxlpgas_"+TRANSPORT,
        "-ldl",
        "-lm",
        "-lpthread",
    };
    /** These go before the files if gcEnabled is true */
    public static final String[] preArgsGC = new String[] {
        "-DX10_USE_BDWGC",
        "-I"+X10GC+"/include",
    };
    /** These go after the files if gcEnabled is true */
    public static final String[] postArgsGC = new String[] {
        X10GC+"/lib/libgc.a",
    };

    /** These go before the files if optimize is true */
    public static final String[] preArgsOptimize = new String[] {
        "-O2",
        USE_XLC ? "-qinline" : "-finline-functions",
        USE_XLC ? "-qhot" : DUMMY,
        "-DNDEBUG"
    };

    private final X10CPPCompilerOptions options;

    public CXXCommandBuilder(Options options) {
        assert (options != null);
        assert (options.post_compiler != null);
        this.options = (X10CPPCompilerOptions) options;
    }

    /** Is GC enabled on this platform? */
    protected boolean gcEnabled() { return false; }

    protected String defaultPostCompiler() { return "g++"; }

    /** Add the arguments that go before the output files */
    protected void addPreArgs(ArrayList<String> cxxCmd) {
        for (int i = 0; i < preArgs.length; i++) {
            cxxCmd.add(preArgs[i]);
        }
        if (!Configuration.DISABLE_GC && gcEnabled()) {
            for (int i = 0; i < preArgsGC.length; i++) {
                cxxCmd.add(preArgsGC[i]);
            }
        }
        if (x10.Configuration.OPTIMIZE) {
          for (String arg : preArgsOptimize) {
            cxxCmd.add(arg);
          }
        }
    }

    /** Add the arguments that go after the output files */
    protected void addPostArgs(ArrayList<String> cxxCmd) {
        if (!Configuration.DISABLE_GC && gcEnabled()) {
            for (int i = 0; i < postArgsGC.length; i++) {
                cxxCmd.add(postArgsGC[i]);
            }
        }
        for (int i = 0; i < postArgs.length; i++) {
            cxxCmd.add(postArgs[i]);
        }
    }

    protected void addExecutablePath(ArrayList<String> cxxCmd) {
        File exe = null;
        if (options.executable_path != null)
            exe = new File(options.executable_path);
        else if (Configuration.MAIN_CLASS != null)
            exe = new File(options.output_directory, Configuration.MAIN_CLASS);
        else
            return;
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

        Iterator iter = outputFiles.iterator();
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
        eq.enqueue(ErrorInfo.WARNING,
                "Unknown platform '"+PLATFORM+"'; using the default post-compiler (g++)");
        return new CXXCommandBuilder(options);
    }

}