/**
 * 
 */
package x10cpp.postcompiler;

import java.util.ArrayList;

import polyglot.main.Options;
import polyglot.util.ErrorQueue;

public class MacOSX_CXXCommandBuilder extends CXXCommandBuilder {

    public MacOSX_CXXCommandBuilder(Options options, ErrorQueue eq) {
        super(options,eq);
        assert (CXXCommandBuilder.PLATFORM.startsWith("macosx_"));
    }

    protected boolean gcEnabled() { return true; }

    protected void addPreArgs(ArrayList<String> cxxCmd) {
        super.addPreArgs(cxxCmd);
        // FIXME: for now, only support 32-bit builds
        cxxCmd.add("-m32");
    }

    protected void addPostArgs(ArrayList<String> cxxCmd) {
        super.addPostArgs(cxxCmd);
    }
}
