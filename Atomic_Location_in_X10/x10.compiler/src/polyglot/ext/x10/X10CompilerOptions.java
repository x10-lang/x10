/*
 */
package polyglot.ext.x10;

import java.io.PrintStream;
import polyglot.frontend.ExtensionInfo;

public class X10CompilerOptions extends polyglot.main.Options {

    public X10CompilerOptions(ExtensionInfo extension) {
        super(extension);
    }

    /**
     * Print usage information
     */
    public void usage(PrintStream out) {
        out.println(extension.compilerName() + ": missing or illegal argument(s).");
        out.println("Try `" + extension.compilerName() + " -help' for more information.");
    }
}