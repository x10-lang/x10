/**
 * 
 */
package x10cpp.postcompiler;

import java.util.ArrayList;

import polyglot.main.Options;
import polyglot.util.ErrorQueue;

public class Cygwin_CXXCommandBuilder extends CXXCommandBuilder {

    public Cygwin_CXXCommandBuilder(Options options, ErrorQueue eq) {
        super(options, eq);
        assert (CXXCommandBuilder.PLATFORM.startsWith("win32_"));
    }

    protected boolean gcEnabled() { return true; }

    protected String defaultPostCompiler() {
        return "g++-3";
    }

    protected void addPreArgs(ArrayList<String> cxxCmd) {
        super.addPreArgs(cxxCmd);
        cxxCmd.add("-Wno-long-long");
        cxxCmd.add("-Wno-unused-parameter");
        cxxCmd.add("-msse2");
        cxxCmd.add("-mfpmath=sse");
    }

    protected void addPostArgs(ArrayList<String> cxxCmd) {
        super.addPostArgs(cxxCmd);
    }
}
