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
    /** These go before the files */
    public static final String[] preArgsAIX = new String[] {
        USE_XLC ? DUMMY : "-Wno-long-long",
        USE_XLC ? "-qsuppress=1540-0809:1500-029" : "-Wno-unused-parameter",
        USE_XLC ? "-q64" : "-maix64", // Assume 64-bit
        USE_XLC ? "-qrtti=all" : DUMMY,
        //USE_XLC ? DUMMY : "-pipe", // TODO: is this needed?
        USE_XLC && XLC_EXTRA_FLAGS!=null ? XLC_EXTRA_FLAGS : DUMMY,
    };
    /** These go after the files */
    public static final String[] postArgsAIX = new String[] {
        USE_XLC ? "-bbigtoc" : "-Wl,-bbigtoc",
        USE_XLC ? "-lptools_ptr" : "-Wl,-lptools_ptr",
        USE_XLC || !TRANSPORT.equals("lapi") ? DUMMY : "-Wl,-binitfini:poe_remote_main",
        USE_XLC || !TRANSPORT.equals("lapi") ? DUMMY : "-L/usr/lpp/ppe.poe/lib",
        USE_XLC || !TRANSPORT.equals("lapi") ? DUMMY : "-lmpi_r",
        USE_XLC || !TRANSPORT.equals("lapi") ? DUMMY : "-lvtd_r",
        USE_XLC || !TRANSPORT.equals("lapi") ? DUMMY : "-llapi_r",
    };

    public AIX_CXXCommandBuilder(Options options) {
        super(options);
        assert (CXXCommandBuilder.PLATFORM.startsWith("aix_"));
    }

    protected boolean gcEnabled() { return false; }

    protected String defaultPostCompiler() {
        if (USE_XLC)
            return "mpCC_r";
        return "g++";
    }

    protected void addPreArgs(ArrayList<String> cxxCmd) {
        super.addPreArgs(cxxCmd);
        for (int i = 0; i < preArgsAIX.length; i++) {
            cxxCmd.add(preArgsAIX[i]);
        }           
    }

    protected void addPostArgs(ArrayList<String> cxxCmd) {
        super.addPostArgs(cxxCmd);
        for (int i = 0; i < postArgsAIX.length; i++) {
            cxxCmd.add(postArgsAIX[i]);
        }
    }
}