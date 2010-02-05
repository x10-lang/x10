/**
 * 
 */
package x10cpp.postcompiler;

import java.util.ArrayList;

import polyglot.main.Options;
import polyglot.util.ErrorQueue;

public class Linux_CXXCommandBuilder extends CXXCommandBuilder {
    protected static final boolean USE_X86 = CXXCommandBuilder.PLATFORM.endsWith("_x86");
    protected static final boolean USE_BFD = System.getenv("USE_BFD")!=null;
 
    public Linux_CXXCommandBuilder(Options options, ErrorQueue eq) {
        super(options, eq);
        assert (CXXCommandBuilder.PLATFORM.startsWith("linux_"));
    }

    protected boolean gcEnabled() { return true; }

    protected void addPreArgs(ArrayList<String> cxxCmd) {
        super.addPreArgs(cxxCmd);
        cxxCmd.add("-Wno-long-long");
        cxxCmd.add("-Wno-unused-parameter");
        cxxCmd.add("-pthread");
        if (USE_X86) {
            cxxCmd.add("-msse2");
            cxxCmd.add("-mfpmath=sse");
        }
    }

    protected void addPostArgs(ArrayList<String> cxxCmd) {
        super.addPostArgs(cxxCmd);
        
        // Support for loading shared libraries from x10.dist/lib
        cxxCmd.add("-Wl,--rpath");
        cxxCmd.add("-Wl,"+X10_DIST+"/lib");

        cxxCmd.add("-Wl,-export-dynamic");
        cxxCmd.add("-lrt");
        if (USE_BFD) {
            cxxCmd.add("-lbfd");
            cxxCmd.add("-liberty");
        }
    }
}
