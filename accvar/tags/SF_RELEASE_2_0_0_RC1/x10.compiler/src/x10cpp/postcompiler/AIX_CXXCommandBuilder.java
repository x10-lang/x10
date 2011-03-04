/**
 * 
 */
package x10cpp.postcompiler;

import java.util.ArrayList;

import polyglot.main.Options;

public class AIX_CXXCommandBuilder extends CXXCommandBuilder {
    //"mpCC_r -q64 -qrtti=all -qarch=pwr5 -O3 -qtune=pwr5 -qhot -qinline"
    //"mpCC_r -q64 -qrtti=all"
    public static final String XLC_EXTRA_FLAGS = System.getenv("XLC_EXTRA_FLAGS");
    
    public AIX_CXXCommandBuilder(Options options) {
        super(options);
        assert (CXXCommandBuilder.PLATFORM.startsWith("aix_"));
    }

    protected boolean gcEnabled() { return false; }

    protected String defaultPostCompiler() {
        return USE_XLC ? "mpCC_r" : "g++";
    }

    protected void addPreArgs(ArrayList<String> cxxCmd) {
        super.addPreArgs(cxxCmd);
        
        if (USE_XLC) {
            cxxCmd.add("-qsuppress=1540-0809:1540-1101:1500-029");
            cxxCmd.add("-q64"); // assume 64-bit
            cxxCmd.add("-qrtti=all");
            if (XLC_EXTRA_FLAGS != null) {
                cxxCmd.add(XLC_EXTRA_FLAGS);
            }
        } else {
            cxxCmd.add("-Wno-long-long");
            cxxCmd.add("-Wno-unused-parameter");
            cxxCmd.add("-maix64"); // Assume 64-bit
        }     
    }

    protected void addPostArgs(ArrayList<String> cxxCmd) {
        super.addPostArgs(cxxCmd);
        
        if (USE_XLC) {
            cxxCmd.add("-bbigtoc");
            cxxCmd.add("-lptools_ptr");
        } else {
            cxxCmd.add("-Wl,-bbigtoc");
            cxxCmd.add("-Wl,-lptools_ptr");
            if (x10rt == X10RT_Impl.PGAS_LAPI) {
                cxxCmd.add("-Wl,-binitfini:poe_remote_main");
                cxxCmd.add("-L/usr/lpp/ppe.poe/lib");
                cxxCmd.add("-lmpi_r");
                cxxCmd.add("-lvtd_r");
                cxxCmd.add("-llapi_r");
            }
        }
    }
}
