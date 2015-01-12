/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10doc;

import java.io.PrintStream;
import java.util.Set;

import polyglot.frontend.ExtensionInfo;
import polyglot.main.Main;
import polyglot.main.UsageError;
import x10.util.CollectionFactory;

public class X10DocOptions extends x10.X10CompilerOptions {

    public String doc_access_modifier = "-protected";

    public X10DocOptions(ExtensionInfo extension) {
        super(extension);
    }

    protected int parseCommand(String args[], int index, Set<String> source) throws UsageError,
            Main.TerminationException {
        int i = super.parseCommand(args, index, source);
        if (i != index) return i;

        if (args[i].equals("-public") || args[i].equals("-protected") || args[i].equals("-package")
                || args[i].equals("-private")) {
            doc_access_modifier = args[i];
            return ++i;
        }
        return index;
    }

    /**
     * Print usage information
     */
    public void usage(PrintStream out) {
        super.usage(out);

        usageForFlag(out, "-public", "Show only public classes and members. ");
        usageForFlag(out, "-protected", "Show only protected and public classes and members. This is the default.");
        usageForFlag(out, "-package", "Show only package, protected, and public classes and members.");
        usageForFlag(out, "-private", "Show all classes and members.");
    }

    private Set<String> compilationUnits = CollectionFactory.newHashSet();

    public Set<String> compilationUnits() {
        return compilationUnits;
    }
}
