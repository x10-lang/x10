package x10dt.refactoring;

import java.io.PrintStream;

import org.eclipse.imp.runtime.PluginBase;
import org.eclipse.imp.utils.ConsoleUtil;

import x10dt.core.X10DTCorePlugin;

public class X10DTRefactoringPlugin extends PluginBase {
    public static final String kPluginID= "x10dt.refactoring"; // must match plugin ID in MANIFEST.MF

    private static final String CONSOLE_NAME= "X10DT Refactoring";

    private static X10DTRefactoringPlugin sPlugin;

    public static X10DTRefactoringPlugin getInstance() {
        return sPlugin;
    }

    private PrintStream sConsoleStream;

    public X10DTRefactoringPlugin() {
        sPlugin= this;
    }

    @Override
    public String getID() {
        return kPluginID;
    }

    @Override
    public String getLanguageID() {
        return X10DTCorePlugin.kLanguageName;
    }

    public PrintStream getConsoleStream() {
        if (sConsoleStream == null) {
            sConsoleStream= ConsoleUtil.findConsoleStream(CONSOLE_NAME);
        }
        return sConsoleStream;
    }
}
