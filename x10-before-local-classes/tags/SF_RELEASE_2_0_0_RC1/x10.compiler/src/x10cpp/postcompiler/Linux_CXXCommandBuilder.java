/**
 * 
 */
package x10cpp.postcompiler;

import java.util.ArrayList;

import polyglot.main.Options;

public class Linux_CXXCommandBuilder extends CXXCommandBuilder {
    protected static final boolean USE_X86 = CXXCommandBuilder.PLATFORM.endsWith("_x86");
    protected static final boolean USE_BFD = System.getenv("USE_BFD")!=null;
 
    public Linux_CXXCommandBuilder(Options options) {
        super(options);
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
        cxxCmd.add("-Wl,-export-dynamic");
        cxxCmd.add("-lrt");
        if (USE_BFD) {
            cxxCmd.add("-lbfd");
            cxxCmd.add("-liberty");
        }
        if (x10rt == X10RT_Impl.PGAS_LAPI) {
            cxxCmd.add("-llapi");
            cxxCmd.add("-lmpi_ibm");
            cxxCmd.add("-lpoe");
        }

    }
}
