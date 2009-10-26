/**
 * 
 */
package x10cpp.postcompiler;

import java.util.ArrayList;

import polyglot.main.Options;

public class MacOSX_CXXCommandBuilder extends CXXCommandBuilder {

    public MacOSX_CXXCommandBuilder(Options options) {
        super(options);
        assert (CXXCommandBuilder.PLATFORM.startsWith("macosx_"));
    }

    protected boolean gcEnabled() { return true; }

    protected void addPreArgs(ArrayList<String> cxxCmd) {
        super.addPreArgs(cxxCmd);
    }

    protected void addPostArgs(ArrayList<String> cxxCmd) {
        super.addPostArgs(cxxCmd);
    }
}
