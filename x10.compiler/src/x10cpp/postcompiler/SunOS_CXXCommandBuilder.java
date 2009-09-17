/**
 * 
 */
package x10cpp.postcompiler;

import java.util.ArrayList;

import polyglot.main.Options;

public class SunOS_CXXCommandBuilder extends CXXCommandBuilder {
    /** These go before the files */
    public static final String[] preArgsSunOS = new String[] {
        "-Wno-long-long",
        "-Wno-unused-parameter",
    };
    /** These go after the files */
    public static final String[] postArgsSunOS = new String[] {
        "-lresolv",
        "-lnsl",
        "-lsocket",
        "-lrt",
    };

    public SunOS_CXXCommandBuilder(Options options) {
        super(options);
        assert (CXXCommandBuilder.PLATFORM.startsWith("sunos_"));
    }

    protected boolean gcEnabled() { return false; }

    protected void addPreArgs(ArrayList<String> cxxCmd) {
        super.addPreArgs(cxxCmd);
        for (int i = 0; i < preArgsSunOS.length; i++) {
            cxxCmd.add(preArgsSunOS[i]);
        }
    }

    protected void addPostArgs(ArrayList<String> cxxCmd) {
        super.addPostArgs(cxxCmd);
        for (int i = 0; i < postArgsSunOS.length; i++) {
            cxxCmd.add(postArgsSunOS[i]);
        }
    }
}