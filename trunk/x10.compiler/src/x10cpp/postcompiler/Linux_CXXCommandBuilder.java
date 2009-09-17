/**
 * 
 */
package x10cpp.postcompiler;

import java.util.ArrayList;

import polyglot.main.Options;

public class Linux_CXXCommandBuilder extends CXXCommandBuilder {
    public static final boolean USE_X86 = CXXCommandBuilder.PLATFORM.endsWith("_x86");
    /** These go before the files */
    public static final String[] preArgsLinux = new String[] {
        "-Wno-long-long",
        "-Wno-unused-parameter",
        "-pthread",
        USE_X86 ? "-msse2" : DUMMY,
        USE_X86 ? "-mfpmath=sse" : DUMMY,
    };
    /** These go after the files */
    public static final String[] postArgsLinux = new String[] {
        "-Wl,-export-dynamic",
        USE_BFD ? "-lbfd" : DUMMY,
        "-lrt",
        !TRANSPORT.equals("lapi") ? DUMMY : "-llapi",
        !TRANSPORT.equals("lapi") ? DUMMY : "-lmpi_ibm",
        !TRANSPORT.equals("lapi") ? DUMMY : "-lpoe",
    };

    public Linux_CXXCommandBuilder(Options options) {
        super(options);
        assert (CXXCommandBuilder.PLATFORM.startsWith("linux_"));
    }

    protected boolean gcEnabled() { return true; }

    protected void addPreArgs(ArrayList<String> cxxCmd) {
        super.addPreArgs(cxxCmd);
        for (int i = 0; i < preArgsLinux.length; i++) {
            cxxCmd.add(preArgsLinux[i]);
        }
    }

    protected void addPostArgs(ArrayList<String> cxxCmd) {
        super.addPostArgs(cxxCmd);
        for (int i = 0; i < postArgsLinux.length; i++) {
            cxxCmd.add(postArgsLinux[i]);
        }
    }
}