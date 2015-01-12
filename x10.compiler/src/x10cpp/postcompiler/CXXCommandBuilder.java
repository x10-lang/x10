/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10cpp.postcompiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import polyglot.frontend.Compiler;
import polyglot.main.Options;
import polyglot.util.ErrorInfo;
import polyglot.util.ErrorQueue;
import polyglot.util.InternalCompilerError;
import polyglot.util.QuotedStringTokenizer;
import polyglot.util.CollectionUtil; 
import x10.util.CollectionFactory;
import x10.Configuration;
import x10.X10CompilerOptions;
import x10cpp.X10CPPCompilerOptions;
import x10cpp.visit.X10CPPTranslator;

public class CXXCommandBuilder {
    
    private static final String UNKNOWN = "unknown";

    protected X10CPPCompilerOptions options;
    
    protected PostCompileProperties x10rt;
    
    protected SharedLibProperties sharedLibProps;
    
    // avoid threading loads of things through constructors -- set this stuff here
    protected void setData(Options options, PostCompileProperties x10rt, SharedLibProperties shared_lib_props, ErrorQueue eq) {
        this.options = (X10CPPCompilerOptions) options;
        this.x10rt = x10rt;
        this.sharedLibProps = shared_lib_props;
    }

    private String cxxCompiler = UNKNOWN;
    protected String defaultPostCompiler() {
        if (cxxCompiler.equals(UNKNOWN)) {
            String pc = x10rt.props.getProperty("X10LIB_CXX");
            if (pc != null && (pc.startsWith("mpi") || pc.startsWith("mpCC"))) {
                // ignore all other settings; mpicxx/mpCC win ties, and also
                // prevent sanity checking because they will be a wrapper on an unknown compiler
                // So, if things don't match, the user will just find out via link time errors.
            } else {
                // If we're compiling for AIX, then we try to prevent mixing of g++ and xlC compiled
                // code because they do not have binary compatible ABIs on AIX.
                if (getPlatform().contains("aix")) {
                    for (PrecompiledLibrary pcl: options.x10libs) {
                        String pc2 = pcl.props.getProperty("X10LIB_CXX");
                        if (pc2 != null) {
                            if (pc != null && !pc2.equals(pc)) {
                                throw new InternalCompilerError("Conflicting postcompilers. Both "+pc+" and "+pc2+" requested");
                            }
                            pc = pc2;
                        } 
                    }
                }
            }
            cxxCompiler = pc == null ? "g++" : pc;
        }
	    return cxxCompiler;
    }
    
    private String platform = UNKNOWN;
    public String getPlatform() {
        if (platform.equals(UNKNOWN)) {
            String p1 = x10rt.props.getProperty("X10LIB_PLATFORM");
            // Sanity check that x10rt and all the precompiled libraries were built for the same platform
            for (PrecompiledLibrary pcl: options.x10libs) {
                String p2 = pcl.props.getProperty("X10LIB_PLATFORM");
                if (p2 != null) {
                    if (p1 != null && !p2.equals(p1)) {
                        throw new InternalCompilerError("Conflicting platforms. Both "+p1+" and "+p2+" specified");
                    }
                    p1 = p2;
                } 
            }
            if (p1 == null) {
                throw new InternalCompilerError("No platform specified by given property files");
            }
            platform = p1;
        }
        
        return platform;        
    }

    
    protected final boolean usingXLC() {
        return defaultPostCompiler().contains("xlC") || 
               defaultPostCompiler().contains("mpCC") ||
	       defaultPostCompiler().contains("xlcxx");
    }
    
    protected final boolean bluegeneQ() {
        return getPlatform().contains("bgq");
    }
    protected final boolean fx10() {
        return getPlatform().contains("fx10");
    }

    /** 
     * Add all command line arguments to the C++ compiler
     * that go before the list of input files to the end of
     * the argument cxxCmd.
     * @param cxxCmd the container to which to append the arguments.
     */
    public void addPreArgs(ArrayList<String> cxxCmd) {
        if (options.x10_config.DEBUG) {
            cxxCmd.add("-g");
        }

        // x10rt and other misc header files
        cxxCmd.add("-I"+options.distPath()+"/include");
        
        // header files for all prebuilt-libraries
        for (PrecompiledLibrary pcl:options.x10libs) {
            if (options.x10_config.DEBUG) {
                cxxCmd.add("-I"+pcl.absolutePathToRoot+"/include-dbg");
            }
            cxxCmd.add("-I"+pcl.absolutePathToRoot+"/include");
        }

        // header files generated by the compilation
        cxxCmd.add("-I"+options.output_directory);
        cxxCmd.add("-I.");

        if (options.x10_config.OPTIMIZE) {
            cxxCmd.add(usingXLC() ? "-O3" : "-O2");
            cxxCmd.add(usingXLC() ? "-qinline" : "-finline-functions");
            cxxCmd.add("-DNO_TRACING");
            if (fx10()) {
                cxxCmd.add("-Kfast");
            }
            if (usingXLC()) {
                if (bluegeneQ()) {
                    cxxCmd.add("-qhot");
                    cxxCmd.add("-qtune=qp");
                    cxxCmd.add("-qsimd=auto");
                    cxxCmd.add("-qarch=qp");
                }
            }
        }

        if (usingXLC()) {
            cxxCmd.add("-qsuppress=1540-0809"    // Do not warn about empty sources
                               + ":1540-1101"    // Do not warn about non-void functions with no return
                               + ":1540-1102"    // Do not warn about uninitialized variables
                               + ":1500-029");   // Do not warn about being unable to inline when optimizing
        } else if (fx10()) {
            cxxCmd.add("-Xg");        	
            cxxCmd.add("-w");
        } else {
            cxxCmd.add("-Wno-long-long");        // Do not warn about using long long
            cxxCmd.add("-Wno-unused-parameter"); // Do not warn about unused parameters
            // DISABLED due to XTENLANG-2215; finally block rewriting has (dynamically unreachable) path in which there is no return
            // cxxCmd.add("-Wreturn-type");         // Do warn about non-void functions with no return
        }

        if (options.x10_config.NO_CHECKS) {
            cxxCmd.add("-DNO_CHECKS");
        }
        
        if (options.pg) {
            cxxCmd.add("-pg");
        }
        
        if (options.gpt) {
            cxxCmd.add("-g");
        }

        cxxCmd.addAll(x10rt.cxxFlags);
        for (PrecompiledLibrary pcl:options.x10libs) {
            cxxCmd.addAll(pcl.cxxFlags);
        }
        
        if (options.buildX10Lib != null) {
        	cxxCmd.addAll(sharedLibProps.cxxFlags);
        }
        
        cxxCmd.addAll(options.extraPreArgs);
    }

    /** 
     * Add all command line arguments to the C++ compiler
     * that go after the list of input files to the end of
     * the argument cxxCmd.
     * The majority of these flags are actually targeted at the linker,
     * and have to do with linking dependent libraries.
     * @param cxxCmd the container to which to append the arguments.
     */
    public void addPostArgs(ArrayList<String> cxxCmd) {
        if (sharedLibProps.staticLib && !usingXLC()) {
            cxxCmd.add("-Wl,--start-group");
        }
        for (PrecompiledLibrary pcl:options.x10libs) {
            
            if (options.x10_config.DEBUG && !options.x10_config.DEBUG_APP_ONLY) {
                cxxCmd.add("-L"+pcl.absolutePathToRoot+"/lib-dbg");
            }
            cxxCmd.add("-L"+pcl.absolutePathToRoot+"/lib");                
            
            cxxCmd.addAll(pcl.ldFlags);
            cxxCmd.addAll(pcl.libs);
        }
        if (sharedLibProps.staticLib && !usingXLC()) {
            cxxCmd.add("-Wl,--end-group");
        }

        // x10rt
        cxxCmd.add("-L"+options.distPath()+"/lib");
        cxxCmd.addAll(x10rt.ldFlags);
        cxxCmd.addAll(x10rt.libs);

        if (options.gpt) {
            cxxCmd.add("-Wl,--no-as-needed");
            cxxCmd.add("-lprofiler");
            cxxCmd.add("-Wl,--as-needed");
        }
        
        if (options.buildX10Lib != null) {
        	cxxCmd.addAll(sharedLibProps.ldFlags);
        }
        cxxCmd.addAll(options.extraPostArgs);
    }

    /**
     * Adds the target executable (-o <file>) to the cxxCmd
     * 
     * @param cxxCmd the container to which to append the arguments.
     */
    public void addExecutablePath(ArrayList<String> cxxCmd) {
        File exe = targetFilePath();
        if (exe != null) {
            cxxCmd.add("-o");
            cxxCmd.add(exe.getAbsolutePath().replace(File.separatorChar,'/'));
        }
    }

    public File targetFilePath() {
        File target = null;
        if (options.buildX10Lib != null) {
        	if (options.executable_path != null) {
                target = new File(options.buildX10Lib + "/lib/" + sharedLibProps.libPrefix + options.executable_path + sharedLibProps.libSuffix);
         	}
        } else {
        	if (options.executable_path != null) {
                target = new File(options.executable_path);
	        } else if (options.x10_config.MAIN_CLASS != null) {
	            target = new File(options.output_directory, options.x10_config.MAIN_CLASS);
	        }
        }
        return target;
    }

    /** Construct the C++ compilation command */
    public final String[] buildCXXCommandLine(Collection<String> outputFiles) {
        String post_compiler = options.post_compiler;
        if (post_compiler.contains("javac")) {
            post_compiler = defaultPostCompiler();
        }

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
            if (options.buildX10Lib != null && sharedLibProps.staticLib) {
                cxxCmd.add("-c");
            } else {
                addExecutablePath(cxxCmd);
            }
        }

        for (String file : outputFiles) {
            file = file.replace(File.separatorChar,'/');
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

    /**
     * @return the name of the C++ compiler (g++/xlC/mpcixx, etc)
     */
    public String getPostCompiler() {
        return defaultPostCompiler();
    }
    
    /**
     * @return all command line arguments for a compilation command that go
     *         before the files to be compiled (typically compilation options).
     */
    public List<String> getPreFileArgs() {
        ArrayList<String> ans = new ArrayList<String>();
        addPreArgs(ans);
        return ans;
    }

    /**
     * @return all command line arguments for a compilation command that go
     *         after the files to be compiled  (typically linker options). 
     */
    public List<String> getPostFileArgs() {
        ArrayList<String> ans = new ArrayList<String>();
        addPostArgs(ans);
        return ans;
    }
    
    /**
     * Return a CXXCommandBuilder that will use the given options and x10rt properties to
     * construct compilation commands.
     *
     * @param options     The compiler options to use when constructing commands
     * @param x10rt_props The property file describing the x10rt implementation 
     *                    to be used for this compilation
     * @param eq          The error queue to use to report warnings and errors during compilation.
     * @return a CXXCommandBuilder instance that will use options and x10rt_props
     */
    public static CXXCommandBuilder getCXXCommandBuilder(X10CompilerOptions options, PostCompileProperties x10rt_props, SharedLibProperties shared_lib_props, ErrorQueue eq) {
        String platform = x10rt_props.props.getProperty("X10LIB_PLATFORM", "unknown");
        CXXCommandBuilder cbb;
        if (platform.startsWith("win32_") || platform.startsWith("cygwin")) {
            cbb = new Cygwin_CXXCommandBuilder();
        } else if (platform.startsWith("linux_")) {
        	cbb = new Linux_CXXCommandBuilder();
        } else if (platform.startsWith("aix_")) {
        	cbb = new AIX_CXXCommandBuilder();
        } else if (platform.startsWith("sunos")) {
        	cbb = new SunOS_CXXCommandBuilder();
        } else if (platform.startsWith("macosx_") || platform.startsWith("darwin")) {
        	cbb = new MacOSX_CXXCommandBuilder();
        } else if (platform.startsWith("freebsd_")) {
        	cbb = new FreeBSD_CXXCommandBuilder();
        } else if (platform.startsWith("bgq")) {
            cbb = new Linux_CXXCommandBuilder();            
        } else if (platform.startsWith("fx10")) {
            cbb = new Linux_CXXCommandBuilder();            
        } else {   
            eq.enqueue(ErrorInfo.WARNING,
                       "Unknown platform '"+platform+"'; using the default post-compiler (g++)");
            cbb = new CXXCommandBuilder();
        }
        cbb.setData(options, x10rt_props, shared_lib_props, eq);
        return cbb;
    }
    
    public String getCUDAPostCompiler() {
    	return "nvcc";
    }

    public double getCUDAVersion() {
        String output = null;
        try {
            ProcessBuilder pb = new ProcessBuilder(getCUDAPostCompiler(), "-V");
            pb.redirectErrorStream(true);
            Process proc = pb.start();
            InputStreamReader stdout = new InputStreamReader(proc.getInputStream());
            try {
                char[] c = new char[72];
                int len;
                StringBuilder sb = new StringBuilder();
                while ((len = stdout.read(c)) > 0) {
                    sb.append(String.valueOf(c, 0, len));
                }
                if (sb.length() != 0) {
                    output = sb.toString();
                }
            }
            finally {
                stdout.close();
            }
            int procExitValue = proc.waitFor();
        } catch (Exception e) {
        }

        double version = -1.0; // unknown

        if (output != null) {
            String marker = "Cuda compilation tools, release ";
            int markerPos = output.indexOf(marker);
            if (markerPos >= 0) {
                int startPos = markerPos + marker.length();
                int endPos = startPos + 1;
                for ( ; endPos < output.length(); ++endPos) {
                    char c = output.charAt(endPos);
                    if (!(Character.isDigit(c) || c == '.')) break;
                }
                version = Double.parseDouble(output.substring(startPos, endPos));
            }
        }

        return version;
    }

    public List<String> getCUDAArchitectures() {
        double version = getCUDAVersion();
        ArrayList<String> ans = new ArrayList<String>();
//        ans.add("sm_10");
//        ans.add("sm_11");
//        ans.add("sm_12");
//        ans.add("sm_13");
        ans.add("sm_20");
        ans.add("sm_21");
        ans.add("sm_30");
        if (version >= 5.0) {
            ans.add("sm_35"); // requires CUDA Toolkit 5.0 or newer
        }
        if (version >= 6.0) {
            ans.add("sm_50"); // requires CUDA Toolkit 6.0 or newer
        }
        if (version >= 6.5) {
            ans.add("sm_32"); // requires CUDA Toolkit 6.5 or newer
            ans.add("sm_37"); // requires CUDA Toolkit 6.5 or newer
        }
        return ans;
    }

    public List<String> getCUDAPreFileArgs() {
        ArrayList<String> ans = new ArrayList<String>();
        ans.add("-cubin");
        //ans.add("-Xptxas");
        //ans.add("-v");
        for (PrecompiledLibrary pcl : options.x10libs) {
            if (options.x10_config.DEBUG) {
                ans.add("-I"+pcl.absolutePathToRoot+"/include-dbg");
            }
            ans.add("-I"+pcl.absolutePathToRoot+"/include");
        }
        return ans;
    }
}
