/**
 * 
 */
package x10cpp.postcompiler;

import java.util.ArrayList;

import polyglot.main.Options;

public class SunOS_CXXCommandBuilder extends CXXCommandBuilder {

    public SunOS_CXXCommandBuilder(Options options) {
        super(options);
        assert (CXXCommandBuilder.PLATFORM.startsWith("sunos_"));
    }

    protected boolean gcEnabled() { return false; }

    protected void addPreArgs(ArrayList<String> cxxCmd) {
        super.addPreArgs(cxxCmd);
        cxxCmd.add("-Wno-long-long");
        cxxCmd.add("-Wno-unused-parameter");
    }

    protected void addPostArgs(ArrayList<String> cxxCmd) {
        super.addPostArgs(cxxCmd);
        cxxCmd.add("-lresolv");
        cxxCmd.add("-lnsl");
        cxxCmd.add("-lsocket");
        cxxCmd.add("-lrt");
    }
}
