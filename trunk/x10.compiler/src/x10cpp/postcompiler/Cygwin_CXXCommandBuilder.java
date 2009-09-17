/**
 * 
 */
package x10cpp.postcompiler;

import java.util.ArrayList;

import polyglot.main.Options;

public class Cygwin_CXXCommandBuilder extends CXXCommandBuilder {
    /** These go before the files */
    public static final String[] preArgsCygwin = new String[] {
        "-Wno-long-long",
        "-Wno-unused-parameter",
        "-msse2",
        "-mfpmath=sse",
    };
    /** These go after the files */
    public static final String[] postArgsCygwin = new String[] {
    };

    public Cygwin_CXXCommandBuilder(Options options) {
        super(options);
        assert (CXXCommandBuilder.PLATFORM.startsWith("win32_"));
    }

    protected boolean gcEnabled() { return false; }

    protected void addPreArgs(ArrayList<String> cxxCmd) {
        super.addPreArgs(cxxCmd);
        for (int i = 0; i < preArgsCygwin.length; i++) {
            cxxCmd.add(preArgsCygwin[i]);
        }
    }

    protected void addPostArgs(ArrayList<String> cxxCmd) {
        super.addPostArgs(cxxCmd);
        for (int i = 0; i < postArgsCygwin.length; i++) {
            cxxCmd.add(postArgsCygwin[i]);
        }
    }
}